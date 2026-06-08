package com.shrimptopia.model;

import com.shrimptopia.events.EventSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Der komplette Spielzustand: Ressourcen, mehrere Zonen-Gitter und die Wirtschaftssimulation.
 *
 * v2-Erweiterungen: Shrimp-Tiers (Qualitätsstufen), Märkte mit Tier-Akzeptanz,
 * Gebäude-Modi & -Upgrades, farmweite Modifikatoren, Arbeiter-Politik und Freischaltungen.
 *
 * Tick-Pipeline: Modifikatoren -> effektive Stats je Gebäude -> Strom/Arbeiter -> Wasser ->
 * Algen(Futter) -> Becken(Shrimps je Tier) -> Märkte(Verkauf) -> Reputation/Kosten -> Freischaltungen.
 */
public class GameState {

    public static final int LOG_INFO = 0, LOG_GOOD = 1, LOG_BAD = 2, LOG_WARN = 3;
    public static final double GOAL_MONEY = 50_000;
    public static final int COLS = 12;
    public static final int ROWS = 8;

    // --- Karte: ein Gitter je Zone ---
    private final EnumMap<Zone, Building[][]> grids = new EnumMap<>(Zone.class);
    private final List<Building> buildings = new ArrayList<>();

    // --- Ressourcen ---
    private double money = 12_000;
    private double water = 80;
    private double feed = 60;
    private double reputation = 60;
    private final EnumMap<ShrimpTier, Double> shrimpStock = new EnumMap<>(ShrimpTier.class);

    // --- Freischaltungen / Politik ---
    private final Set<String> unlocked = new HashSet<>();
    private WorkerPolicy workerPolicy = WorkerPolicy.REGULAR;

    // --- letzter Tick (HUD/Inspektor) ---
    private double moneyNet, waterNet, feedNet, shrimpNet;
    private int powerProduced, powerUsed, workersAvail, workersUsed;
    private double infraEff = 1.0, sellPriceEff = 0, shrimpSoldLast = 0, upkeepLast = 0;

    // --- Fortschritt ---
    private int day = 0;
    private boolean goalReached = false, bankrupt = false;
    private int negativeStreak = 0;
    private double totalShrimpProduced = 0;   // kumuliert (für Charakter-Trigger)
    private double exportTariff = 0;          // Akwanov-Embargo: 0..0.6 Preisabschlag auf Märkte

    private final Random rng;
    private final EventSystem eventSystem;
    private final List<LogLine> log = new ArrayList<>();

    public GameState() { this(new Random()); }

    public GameState(Random rng) {
        this.rng = rng;
        this.eventSystem = new EventSystem(rng);
        for (Zone z : Zone.values()) grids.put(z, new Building[ROWS][COLS]);
        for (ShrimpTier t : ShrimpTier.values()) shrimpStock.put(t, 0.0);
        shrimpStock.put(ShrimpTier.STANDARD, 12.0);
        place(BuildingType.HEADQUARTERS, Zone.PRODUKTION, COLS / 2, ROWS / 2, false);
        log("Willkommen in ShrimpTopia! Folge dem Tutorial - oder bau einfach los.", LOG_INFO);
    }

    // ===================== Bauen / Abreißen =====================

    public boolean inBounds(int col, int row) { return col >= 0 && col < COLS && row >= 0 && row < ROWS; }

    public boolean canPlace(Zone zone, int col, int row) {
        return inBounds(col, row) && grids.get(zone)[row][col] == null;
    }

    public boolean place(BuildingType type, Zone zone, int col, int row, boolean charge) {
        if (!canPlace(zone, col, row)) return false;
        if (charge && money < type.cost) return false;
        if (charge) money -= type.cost;
        Building b = new Building(type, zone, col, row);
        b.placedOnDay = day;
        // Standard-Modus, der zum aktuellen Freischaltungs-Stand passt:
        b.mode = 0;
        grids.get(zone)[row][col] = b;
        buildings.add(b);
        return true;
    }

    public Building demolish(Zone zone, int col, int row, boolean refund) {
        if (!inBounds(col, row)) return null;
        Building b = grids.get(zone)[row][col];
        if (b == null || b.type == BuildingType.HEADQUARTERS) return null;
        if (refund) money += b.type.cost * 0.5;
        grids.get(zone)[row][col] = null;
        buildings.remove(b);
        return b;
    }

    public Building at(Zone zone, int col, int row) { return inBounds(col, row) ? grids.get(zone)[row][col] : null; }
    public Building[][] grid(Zone zone) { return grids.get(zone); }

    // Kompatibilitäts-Shims (Produktionshalle)
    public boolean canPlace(int col, int row) { return canPlace(Zone.PRODUKTION, col, row); }
    public boolean place(BuildingType type, int col, int row, boolean charge) { return place(type, Zone.PRODUKTION, col, row, charge); }
    public Building demolish(int col, int row, boolean refund) { return demolish(Zone.PRODUKTION, col, row, refund); }
    public Building at(int col, int row) { return at(Zone.PRODUKTION, col, row); }
    public Building[][] grid() { return grids.get(Zone.PRODUKTION); }

    // ===================== Modi / Upgrades / Politik =====================

    public void setMode(Building b, int modeIndex) {
        List<Mode> ms = BuildingCatalog.modes(b.type);
        if (modeIndex < 0 || modeIndex >= ms.size()) return;
        Mode m = ms.get(modeIndex);
        if (m.requiresFlag != null && !isUnlocked(m.requiresFlag)) {
            log("Modus '" + m.name + "' ist noch nicht freigeschaltet.", LOG_WARN);
            return;
        }
        b.mode = modeIndex;
    }

    public boolean buyUpgrade(Building b, Upgrade u) {
        if (b.upgrades.contains(u.id)) return false;
        if (u.requiresFlag != null && !isUnlocked(u.requiresFlag)) {
            log("Upgrade '" + u.name + "' ist noch gesperrt.", LOG_WARN);
            return false;
        }
        if (money < u.cost) { log("Zu wenig Geld für Upgrade '" + u.name + "'.", LOG_WARN); return false; }
        money -= u.cost;
        b.upgrades.add(u.id);
        if (u.grantsFlag != null) unlock(u.grantsFlag, null);
        log("Upgrade gekauft: " + u.name + " (" + b.type.shortName() + ").", LOG_GOOD);
        return true;
    }

    public WorkerPolicy getWorkerPolicy() { return workerPolicy; }
    public boolean setWorkerPolicy(WorkerPolicy p) {
        if (p.requiresFlag != null && !isUnlocked(p.requiresFlag)) return false;
        workerPolicy = p;
        log("Arbeiter-Politik: " + p.displayName + ".", LOG_INFO);
        return true;
    }

    // ===================== Freischaltungen =====================

    public boolean isUnlocked(String flag) { return flag == null || unlocked.contains(flag); }
    public boolean isTierUnlocked(ShrimpTier t) { return t.alwaysUnlocked() || unlocked.contains(t.unlockFlag()); }
    public boolean isZoneUnlocked(Zone z) { return z.alwaysUnlocked() || unlocked.contains(z.unlockFlag()); }
    public boolean isBuildingUnlocked(BuildingType t) { return t.unlockFlag() == null || unlocked.contains(t.unlockFlag()); }

    /** Schaltet ein Flag frei (idempotent). msg optional für den Log. */
    public void unlock(String flag, String msg) {
        if (flag == null || unlocked.contains(flag)) return;
        unlocked.add(flag);
        if (msg != null) log(msg, LOG_GOOD);
    }

    private void checkMilestoneUnlocks() {
        if (money >= 5_000)  unlock("build.water_hub", "Freigeschaltet: Wasseraufbereitungs-Hub.");
        if (buildingCount() >= 5 || day >= 8)
            unlock("zone.FORSCHUNG", "Neue Zone: Forschungsflügel (Labore). Oben die Reiter wechseln!");
        if (reputation >= 64 || money >= 9_000)
            unlock("zone.EMPFANG", "Neue Zone: Empfang & Garten (Restaurant, Besucherzentrum).");
        if (money >= 15_000) {
            unlock("zone.LOGISTIK", "Neue Zone: Logistik & Export.");
            unlock("build.export", "Freigeschaltet: Export-Hafen (kauft höhere Tiers).");
        }
        if (money >= 22_000)
            unlock("build.blackmarket", "Ein dubioser Kontakt bietet dir einen Schwarzmarkt an...");
    }

    // ===================== Simulation =====================

    public void tick() {
        if (bankrupt) return;
        day++;
        double moneyBefore = money;

        // --- Farm-Modifikatoren aus globalen Upgrades + Arbeiter-Politik ---
        FarmModifiers fm = new FarmModifiers();
        for (Building b : buildings)
            for (String id : b.upgrades) {
                Upgrade u = findUpgrade(b.type, id);
                if (u != null && u.global != null) fm.apply(u.global);
            }
        workerPolicy.apply(fm);

        // --- Effektive Stats je Gebäude + Aggregate ---
        double wProvide = 0, wNeed = 0, pProd = 0, pUse = 0;
        for (Building b : buildings) {
            Stats s = computeStats(b, fm);
            b.lastStats = s;
            wProvide += s.workerProvide;
            wNeed += s.workerNeed;
            pProd += s.powerProduce;
            pUse += s.powerUse;
        }
        workersAvail = (int) Math.round(wProvide);
        workersUsed = (int) Math.round(wNeed);
        powerProduced = (int) Math.round(pProd);
        powerUsed = (int) Math.round(pUse);
        double workerRatio = wNeed <= 0 ? 1.0 : clamp01(wProvide / wNeed);
        double powerRatio = pUse <= 0 ? 1.0 : clamp01(pProd / pUse);
        double opsEff = Math.min(workerRatio, powerRatio);
        infraEff = opsEff;

        // --- Wasserproduktion ---
        double waterIn = 0;
        for (Building b : buildings) waterIn += b.lastStats.waterProduce;
        waterIn *= opsEff;
        water += waterIn;

        // --- Algen: Wasser -> Futter ---
        double algaeWaterDemand = 0;
        for (Building b : buildings)
            if (b.type == BuildingType.ALGAE_FARM) algaeWaterDemand += b.lastStats.waterUse;
        algaeWaterDemand *= opsEff;
        double algaeServe = algaeWaterDemand <= 0 ? 1.0 : clamp01(water / algaeWaterDemand);
        water -= algaeWaterDemand * algaeServe;
        double feedIn = 0;
        for (Building b : buildings)
            if (b.type == BuildingType.ALGAE_FARM) feedIn += b.lastStats.feedProduce;
        feedIn *= opsEff * algaeServe;
        feed += feedIn;

        // --- Becken: Wasser+Futter -> Shrimps (je Tier) ---
        double tankWaterDemand = 0, tankFeedDemand = 0;
        for (Building b : buildings)
            if (b.type == BuildingType.SHRIMP_TANK) {
                tankWaterDemand += b.lastStats.waterUse;
                tankFeedDemand += b.lastStats.feedUse;
            }
        tankWaterDemand *= opsEff;
        tankFeedDemand *= opsEff;
        double tankWaterServe = tankWaterDemand <= 0 ? 1.0 : clamp01(water / tankWaterDemand);
        double tankFeedServe = tankFeedDemand <= 0 ? 1.0 : clamp01(feed / tankFeedDemand);
        double tankServe = Math.min(tankWaterServe, tankFeedServe);
        water -= tankWaterDemand * tankServe;
        feed -= tankFeedDemand * tankServe;

        double shrimpIn = 0;
        for (Building b : buildings) {
            if (b.type == BuildingType.SHRIMP_TANK) {
                double made = b.lastStats.shrimpProduce * opsEff * tankServe;
                ShrimpTier tier = isTierUnlocked(b.lastStats.tier) ? b.lastStats.tier : ShrimpTier.STANDARD;
                addStock(tier, made);
                shrimpIn += made;
            }
        }
        totalShrimpProduced += shrimpIn;

        // Engpass: Shrimps sterben (alle Tiers anteilig)
        boolean tanks = anyTank();
        if (tanks) {
            if (tankFeedServe < 1.0)  { scaleAllStock(0.96 + 0.04 * tankFeedServe);  warnOnce("Futter knapp - Shrimps hungern!"); }
            if (tankWaterServe < 1.0) { scaleAllStock(0.94 + 0.06 * tankWaterServe); warnOnce("Wasser knapp - Shrimps leiden!"); }
            if (powerRatio < 1.0)     { warnOnce("Stromausfall - Becken laufen nur teilweise!"); }
        }

        // --- Preisfaktoren ---
        int labCount = countType(BuildingType.LAB);
        double labBonus = labCount * 0.12;
        double repMult = 0.6 + (reputation / 100.0) * 0.8;
        double tariff = 1.0 - exportTariff;
        double priceGlobal = (1 + labBonus) * repMult * fm.priceMult * tariff;
        double bestPrice = 0;

        // --- Märkte: verkaufen akzeptierte Tiers (wertvollste zuerst) ---
        double soldTotal = 0;
        for (Building b : buildings) {
            if (!b.type.isMarket()) continue;
            double cap = b.lastStats.sellCap * opsEff;
            if (cap <= 0) continue;
            ShrimpTier[] accepted = b.type.acceptedTiers().clone();
            Arrays.sort(accepted, (x, y) -> Double.compare(y.baseValue, x.baseValue));
            for (ShrimpTier t : accepted) {
                if (cap <= 0) break;
                if (!isTierUnlocked(t)) continue;
                double avail = shrimpStock.get(t);
                double take = Math.min(avail, cap);
                if (take <= 0) continue;
                double unitPrice = t.baseValue * b.type.priceMult() * priceGlobal;
                money += take * unitPrice;
                addStock(t, -take);
                cap -= take;
                soldTotal += take;
                reputation += take * t.repPerUnit;
                bestPrice = Math.max(bestPrice, unitPrice);
            }
        }
        sellPriceEff = bestPrice;
        shrimpSoldLast = soldTotal;

        // --- Reputation aus Gebäuden (Modi, Restaurant, Besucher, Solar, Kraftwerk ...) ---
        for (Building b : buildings) reputation += b.lastStats.repPerTick;
        reputation += fm.repPerTick;
        reputation += (50 - reputation) * 0.01;
        reputation = clamp(reputation, 0, 100);

        // --- Betriebskosten ---
        double upkeep = 0;
        for (Building b : buildings) upkeep += b.lastStats.upkeep;
        money -= upkeep;
        upkeepLast = upkeep;

        if (water < 0) water = 0;
        if (feed < 0) feed = 0;

        // --- Status je Gebäude ---
        for (Building b : buildings) {
            double eff;
            switch (b.type) {
                case POWER_PLANT, SOLAR_ROOF -> eff = workerRatio;
                case HOUSING, HEADQUARTERS, ZEN_GARDEN -> eff = powerRatio;
                case ALGAE_FARM -> eff = opsEff * algaeServe;
                case SHRIMP_TANK -> eff = opsEff * tankServe;
                default -> eff = opsEff;
            }
            String note = "";
            if (powerRatio < 0.999 && b.lastStats.powerUse > 0) note = "Strom knapp";
            else if (workerRatio < 0.999 && b.lastStats.workerNeed > 0) note = "Personal fehlt";
            else if (b.type == BuildingType.SHRIMP_TANK && tankServe < 0.999) note = "Input knapp";
            else if (b.type.isMarket() && getShrimpTotal() < 1 && b.lastStats.sellCap > 0) note = "Kein Bestand";
            b.efficiency = clamp01(eff);
            b.statusNote = note;
        }

        // --- HUD-Flüsse ---
        moneyNet = money - moneyBefore;
        waterNet = waterIn - algaeWaterDemand * algaeServe - tankWaterDemand * tankServe;
        feedNet = feedIn - tankFeedDemand * tankServe;
        shrimpNet = shrimpIn - soldTotal;

        checkMilestoneUnlocks();

        // --- Sieg / Pleite ---
        if (!goalReached && money >= GOAL_MONEY) {
            goalReached = true;
            log("ZIEL ERREICHT! " + (int) GOAL_MONEY + " Geld - du bist Shrimp-Tycoon!", LOG_GOOD);
        }
        if (money < 0) {
            negativeStreak++;
            if (negativeStreak == 1) log("Du bist im Minus! Senke Kosten oder verkaufe mehr Shrimps.", LOG_BAD);
            if (negativeStreak >= 20) { bankrupt = true; log("PLEITE! Die Bank hat die Halle gepfändet.", LOG_BAD); }
        } else negativeStreak = 0;

        eventSystem.maybeTrigger(this);
    }

    /** Berechnet die effektiven Tick-Werte eines Gebäudes (Basis + Modus + Upgrades + Farm-Modifikatoren). */
    public Stats computeStats(Building b, FarmModifiers fm) {
        BuildingType t = b.type;
        Stats s = new Stats();
        s.powerProduce = t.powerProduce; s.powerUse = t.powerUse;
        s.waterProduce = t.waterProduce; s.waterUse = t.waterUse;
        s.feedProduce = t.feedProduce; s.feedUse = t.feedUse;
        s.shrimpProduce = t.shrimpProduce;
        s.sellCap = t.shrimpSell;
        s.upkeep = t.upkeep;
        s.workerProvide = t.workerProvide; s.workerNeed = t.workerNeed;
        s.tier = t.producesTier() != null ? t.producesTier() : ShrimpTier.STANDARD;
        double rep = t.repProduce;
        if (t == BuildingType.SOLAR_ROOF) rep += 0.03;
        if (t == BuildingType.POWER_PLANT) rep -= 0.05;

        List<Mode> ms = BuildingCatalog.modes(t);
        if (b.mode >= 0 && b.mode < ms.size()) {
            Mode m = ms.get(b.mode);
            if (m.requiresFlag != null && !isUnlocked(m.requiresFlag)) m = ms.get(0);
            s.shrimpProduce *= m.shrimpMult; s.powerUse *= m.powerMult; s.waterUse *= m.waterMult;
            s.feedUse *= m.feedMult; s.upkeep *= m.upkeepMult; s.sellCap *= m.capMult;
            s.powerProduce *= m.prodMult; s.waterProduce *= m.prodMult; s.feedProduce *= m.prodMult;
            rep += m.repAdd;
            if (m.tierOverride != null) s.tier = m.tierOverride;
        }
        for (String id : b.upgrades) {
            Upgrade u = findUpgrade(t, id);
            if (u == null) continue;
            s.shrimpProduce *= u.shrimpMult; s.powerUse *= u.powerMult; s.waterUse *= u.waterMult;
            s.feedUse *= u.feedMult; s.upkeep *= u.upkeepMult; s.sellCap *= u.capMult;
            s.powerProduce *= u.prodMult; s.waterProduce *= u.prodMult; s.feedProduce *= u.prodMult;
            rep += u.repAdd;
            if (u.tierOverride != null) s.tier = u.tierOverride;
        }
        if (t == BuildingType.SHRIMP_TANK) s.shrimpProduce *= fm.tankShrimpMult;
        s.powerUse *= fm.powerUseMult;
        s.upkeep *= fm.upkeepMult;
        if (s.waterProduce > 0) s.waterProduce *= fm.waterProduceMult;
        if (s.feedProduce > 0) s.feedProduce *= fm.feedProduceMult;
        if (t.isMarket()) s.sellCap *= fm.sellCapMult;
        s.workerProvide = (int) Math.round(s.workerProvide * fm.workerMult);
        if (!isTierUnlocked(s.tier)) s.tier = ShrimpTier.STANDARD;
        s.repPerTick = rep;
        return s;
    }

    private Upgrade findUpgrade(BuildingType t, String id) {
        for (Upgrade u : BuildingCatalog.upgrades(t)) if (u.id.equals(id)) return u;
        return null;
    }

    private int lastWarnDay = -100;
    private void warnOnce(String msg) {
        if (day - lastWarnDay >= 4) { log(msg, LOG_WARN); lastWarnDay = day; }
    }

    private int countType(BuildingType t) { int n = 0; for (Building b : buildings) if (b.type == t) n++; return n; }
    private boolean anyTank() { for (Building b : buildings) if (b.type == BuildingType.SHRIMP_TANK) return true; return false; }

    private void addStock(ShrimpTier t, double d) { shrimpStock.merge(t, d, Double::sum); if (shrimpStock.get(t) < 0) shrimpStock.put(t, 0.0); }
    private void scaleAllStock(double f) { for (ShrimpTier t : ShrimpTier.values()) shrimpStock.put(t, Math.max(0, shrimpStock.get(t) * f)); }

    // ===================== Effekte für Ereignisse / Quests =====================

    public void addMoney(double d)      { money += d; }
    public void addWater(double d)      { water = Math.max(0, water + d); }
    public void addFeed(double d)       { feed = Math.max(0, feed + d); }
    public void addShrimp(double d)     { addStock(ShrimpTier.STANDARD, d); }
    public void addShrimp(ShrimpTier t, double d) { addStock(t, d); }
    public void addReputation(double d) { reputation = clamp(reputation + d, 0, 100); }
    public void multShrimp(double f)    { scaleAllStock(f); }
    public void multWater(double f)     { water = Math.max(0, water * f); }
    public void setExportTariff(double t){ exportTariff = clamp(t, 0, 0.8); }
    public double getExportTariff()     { return exportTariff; }

    // ===================== Log =====================

    public void log(String text, int kind) {
        log.add(new LogLine(day, text, kind));
        while (log.size() > 250) log.remove(0);
    }
    public List<LogLine> getLog() { return log; }

    public static class LogLine {
        public final int day; public final String text; public final int kind;
        public LogLine(int day, String text, int kind) { this.day = day; this.text = text; this.kind = kind; }
    }

    // ===================== Helfer =====================

    private static double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }
    private static double clamp(double v, double lo, double hi) { return v < lo ? lo : (v > hi ? hi : v); }

    // ===================== Getter =====================

    public List<Building> buildings() { return buildings; }
    public int buildingCount() { return buildings.size(); }
    public int countBuildings(BuildingType t) { return countType(t); }
    public Random rng() { return rng; }

    public double getMoney()      { return money; }
    public double getWater()      { return water; }
    public double getFeed()       { return feed; }
    public double getReputation() { return reputation; }
    public int    getDay()        { return day; }

    public double getShrimpStock(ShrimpTier t) { return shrimpStock.getOrDefault(t, 0.0); }
    public double getShrimpTotal() { double s = 0; for (double v : shrimpStock.values()) s += v; return s; }
    public double getShrimp()      { return getShrimpTotal(); }
    public EnumMap<ShrimpTier, Double> getShrimpStocks() { return shrimpStock; }
    public double getTotalShrimpProduced() { return totalShrimpProduced; }

    public double getMoneyNet()  { return moneyNet; }
    public double getWaterNet()  { return waterNet; }
    public double getFeedNet()   { return feedNet; }
    public double getShrimpNet() { return shrimpNet; }
    public int getPowerProduced(){ return powerProduced; }
    public int getPowerUsed()    { return powerUsed; }
    public int getWorkersAvail() { return workersAvail; }
    public int getWorkersUsed()  { return workersUsed; }
    public double getInfraEff()  { return infraEff; }
    public double getSellPriceEff() { return sellPriceEff; }
    public double getShrimpSoldLast() { return shrimpSoldLast; }
    public double getUpkeepLast() { return upkeepLast; }

    public boolean isGoalReached() { return goalReached; }
    public boolean isBankrupt()    { return bankrupt; }
    public Set<String> unlockedFlags() { return unlocked; }
}
