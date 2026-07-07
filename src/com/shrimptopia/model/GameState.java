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
    public static final double GOAL_MONEY = 1_500_000;
    public static final int COLS = 12;
    public static final int ROWS = 8;

    // --- Karte: ein Gitter je Zone ---
    private final EnumMap<Zone, Building[][]> grids = new EnumMap<>(Zone.class);
    private final List<Building> buildings = new ArrayList<>();

    // --- Ressourcen (Garagen-Start: kleines Budget, kleine Träume) ---
    private double money = 2_500;
    private double water = 80;
    private double feed = 60;
    // v6: Start-Reputation gesenkt - Ruf will über Entscheidungen ERARBEITET werden.
    private double reputation = 40;
    private final EnumMap<ShrimpTier, Double> shrimpStock = new EnumMap<>(ShrimpTier.class);

    // --- Freischaltungen / Politik ---
    private final Set<String> unlocked = new HashSet<>();
    private WorkerPolicy workerPolicy = WorkerPolicy.REGULAR;

    // --- Marketing: aktive Streams bauen BEKANNTHEIT auf, die (mit Reputation) Nachfrage macht ---
    private final java.util.EnumSet<MarketingStream> activeStreams = java.util.EnumSet.noneOf(MarketingStream.class);
    /** Basis-Nachfrage ohne jedes Marketing: die Nachbarschaft. */
    public static final double BASE_DEMAND = 2;
    /** Nachfrage pro Punkt Bekanntheit (vor Reputations-Faktor). */
    public static final double DEMAND_PER_AWARENESS = 0.5;
    /** Bekanntheit 0..100: wächst durch Marketing (je Kanal bis zu seiner Sättigungsgrenze),
     *  zerfällt langsam ohne Werbung. */
    private double awareness = 0;
    private double demandLast = 0, demandUsedLast = 0, marketingCostLast = 0;

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
    private double decoScore = 0;             // Ambiente: Summe aller Deko-Beiträge (-20..60)

    // v3: zusaetzliche Bestaende & Tracking
    private final java.util.EnumMap<ShrimpTier, Double> producedByTier = new java.util.EnumMap<>(ShrimpTier.class);
    private double totalSold = 0;
    private double armyStrength = 0;
    private double shells = 0, energy = 0, robots = 0;   // energy = SHRIMPBOOST
    private double totalBoostProduced = 0, totalRobotsProduced = 0;
    private double waste = 0;   // Klärschlamm aus der Darmentleerung (muss entsorgt werden)
    private final java.util.EnumMap<BuildingType, Double> incomeByType = new java.util.EnumMap<>(BuildingType.class);
    private double incomeLast = 0, shellsNet = 0, boostNet = 0, robotsNet = 0, wasteNet = 0;
    private final java.util.EnumSet<Edict> activeEdicts = java.util.EnumSet.noneOf(Edict.class);

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

    /**
     * Tropico-Stil: baut ein Gebäude am selben Platz zur nächsten Stufe aus.
     * Kostet den Neupreis abzüglich 50% Restwert der alten Stufe. Liefert die neue
     * Instanz oder null (keine Stufe / gesperrt / zu teuer).
     */
    public Building upgradeBuilding(Building b) {
        if (b == null) return null;
        BuildingType next = b.type.upgradesTo();
        if (next == null) return null;
        if (!isBuildingUnlocked(next)) {
            log("Upgrade zu '" + next.displayName + "' ist noch nicht freigeschaltet.", LOG_WARN);
            return null;
        }
        double cost = Math.max(0, next.cost - b.type.cost * 0.5);
        if (money < cost) { log("Zu wenig Geld für das Upgrade zu '" + next.displayName + "'.", LOG_WARN); return null; }
        money -= cost;
        Building nb = new Building(next, b.zone, b.col, b.row);
        nb.placedOnDay = day;
        grids.get(b.zone)[b.row][b.col] = nb;
        buildings.remove(b);
        buildings.add(nb);
        log(b.type.displayName + " zu " + next.displayName + " ausgebaut.", LOG_GOOD);
        return nb;
    }

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
        // Farmweite Upgrades gibt es genau EINMAL - egal auf wie vielen Gebäuden
        // desselben Typs sie angeboten werden (sonst würde der Effekt stacken).
        Building owner = globalUpgradeOwner(u);
        if (owner != null && owner != b) {
            log("'" + u.name + "' wirkt bereits farmweit (gekauft am " + owner.type.shortName() + ").", LOG_WARN);
            return false;
        }
        // Reine Freischalt-Upgrades sind nach der Freischaltung wirkungslos -
        // Doppelkauf (z.B. Bio-Zertifikat im Labor UND auf der Algenfarm) verhindern.
        if (u.grantsFlag != null && isUnlocked(u.grantsFlag) && !u.hasLocalEffect() && u.global == null) {
            log("'" + u.name + "': das ist bereits freigeschaltet.", LOG_WARN);
            return false;
        }
        if (money < u.cost) { log("Zu wenig Geld für Upgrade '" + u.name + "'.", LOG_WARN); return false; }
        money -= u.cost;
        b.upgrades.add(u.id);
        if (u.grantsFlag != null) unlock(u.grantsFlag, null);
        log("Upgrade gekauft: " + u.name + " (" + b.type.shortName() + ").", LOG_GOOD);
        return true;
    }

    /** Gebäude, das dieses farmweite Upgrade schon besitzt - oder null (auch bei lokalen Upgrades). */
    public Building globalUpgradeOwner(Upgrade u) {
        if (u.global == null) return null;
        for (Building x : buildings)
            if (x.upgrades.contains(u.id) && BuildingCatalog.upgrades(x.type).contains(u)) return x;
        return null;
    }

    /**
     * Warum ist dieses Upgrade für dieses Gebäude nicht (mehr) kaufbar?
     * null = kaufbar; sonst kurzer Anzeigetext für den Inspektor.
     */
    public String upgradeUnavailableReason(Building b, Upgrade u) {
        if (b.upgrades.contains(u.id)) return null;   // "gekauft" zeigt die UI selbst
        Building owner = globalUpgradeOwner(u);
        if (owner != null && owner != b) return "farmweit aktiv";
        if (u.grantsFlag != null && isUnlocked(u.grantsFlag) && !u.hasLocalEffect() && u.global == null)
            return "schon freigeschaltet";
        return null;
    }

    public boolean isEdictActive(Edict e) { return activeEdicts.contains(e); }
    public java.util.Set<Edict> getActiveEdicts() { return activeEdicts; }
    /** Schaltet ein Edikt um; Edikte derselben Gruppe schließen sich aus. */
    public void toggleEdict(Edict e) {
        if (activeEdicts.remove(e)) { log("Edikt aufgehoben: " + e.name, LOG_INFO); return; }
        if (e.requiresFlag != null && !isUnlocked(e.requiresFlag)) {
            log("Edikt '" + e.name + "' ist noch nicht freigeschaltet.", LOG_WARN);
            return;
        }
        if (e.group != null) {
            java.util.Iterator<Edict> it = activeEdicts.iterator();
            while (it.hasNext()) { Edict o = it.next(); if (e.group.equals(o.group)) { it.remove(); log("Edikt '" + o.name + "' weicht '" + e.name + "'.", LOG_INFO); } }
        }
        activeEdicts.add(e);
        log("Edikt erlassen: " + e.name, LOG_GOOD);
    }

    // ===================== Marketing =====================

    public boolean isStreamActive(MarketingStream s) { return activeStreams.contains(s); }
    public java.util.Set<MarketingStream> getActiveStreams() { return activeStreams; }

    /** Bucht einen Marketing-Stream oder kündigt ihn. */
    public boolean toggleStream(MarketingStream s) {
        if (activeStreams.remove(s)) { log("Marketing gekündigt: " + s.displayName, LOG_INFO); return true; }
        if (s.requiresFlag != null && !isUnlocked(s.requiresFlag)) {
            log("Marketing '" + s.displayName + "' ist noch nicht freigeschaltet.", LOG_WARN);
            return false;
        }
        activeStreams.add(s);
        log("Marketing gebucht: " + s.displayName + " (-" + Math.round(s.costPerDay)
            + "/Tag, Bekanntheit bis ~" + Math.round(s.capFor(reputation)) + ")", LOG_GOOD);
        return true;
    }

    /** Nachfrage heute (nach Reputations-Faktor). */
    public double getDemandLast() { return demandLast; }
    /** Davon durch Verkäufe genutzt. */
    public double getDemandUsedLast() { return demandUsedLast; }
    public double getMarketingCostLast() { return marketingCostLast; }
    /** Bekanntheit 0..100 (Marketing-Aufbau, natürlicher Zerfall). */
    public double getAwareness() { return awareness; }
    public void addAwareness(double d) { awareness = clamp(awareness + d, 0, 100); }
    /** Reputations-Faktor auf die Nachfrage (0.5 .. 1.4) - Ruf entscheidet, ob Bekanntheit KAUFT. */
    public double demandRepFactor() { return 0.5 + (reputation / 100.0) * 0.9; }

    /** Ambiente-Wert der Farm (-20..60): Deko-Bauten heben ihn, Schandflecke senken ihn. */
    public double getDecoScore() { return decoScore; }
    /** Ambiente-Faktor auf die Nachfrage: schöne Farmen ziehen Kundschaft an (0.88 .. 1.30). */
    public double decoDemandFactor() { return 1 + clamp(decoScore, -20, 50) * 0.006; }

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

    /** Wartende Freischalt-Ankündigungen ({flag, meldung}) für ein Ansage-Popup. */
    private final List<String[]> announcements = new ArrayList<>();

    /**
     * Schaltet ein Flag frei (idempotent). msg optional: wird geloggt UND als
     * Ankündigungs-Dialog vorgemerkt, damit Freischaltungen nicht im Log untergehen.
     */
    public void unlock(String flag, String msg) {
        if (flag == null || unlocked.contains(flag)) return;
        unlocked.add(flag);
        if (msg != null) {
            log(msg, LOG_GOOD);
            announcements.add(new String[]{flag, msg});
        }
    }

    /** Nächste wartende Freischalt-Ankündigung oder null. */
    public String[] pollAnnouncement() { return announcements.isEmpty() ? null : announcements.remove(0); }

    private void checkMilestoneUnlocks() {
        // v6-Balance: Garage -> Hof -> Halle sind ECHTE Stufen. Die Quests von Dr. Perla
        // sind der Hauptweg; die Vermögens-Schwellen hier sind nur ein Sicherheitsnetz,
        // falls eine Quest liegen bleibt - bewusst so hoch, dass sie selten greifen.
        if (money >= 8_000)
            unlock("era.HOF", "Genug gespart: Der Hof hinterm Haus ist gepachtet - Hof-Gebäude freigeschaltet!");
        boolean hof = isUnlocked("era.HOF");
        if (hof && money >= 30_000)
            unlock("era.HALLE", "Der Hof hat ausgedient: Hallen-Gebäude freigeschaltet!");
        boolean halle = isUnlocked("era.HALLE");
        // Verkaufsleiter: Hofladen braucht Ruf (die Leute kommen nur zu wem, den sie mögen),
        // die Börse braucht nachgewiesene Verkaufs-Historie UND Kapital.
        if (hof && reputation >= 55)
            unlock("build.hofladen", "Dein Ruf spricht sich rum: Hofladen freigeschaltet (Ausbau des Marktstands)!");
        if (halle && totalSold >= 1_200 && money >= 25_000)
            unlock("build.boerse", "Die Börse nimmt dich ernst: Shrimp-Börse freigeschaltet (Ausbau des Lieferdiensts)!");
        if (isUnlocked("zone.FORSCHUNG") && money >= 26_000)
            unlock("build.gut_station", "Freigeschaltet: Darmentleerungsanlage - Voraussetzung für makellose Gourmet-Shrimps (mit leerem Darm)!");
        if (isUnlocked("zone.FORSCHUNG") && money >= 30_000)
            unlock("build.shrimpboost", "Freigeschaltet: SHRIMPBOOST-Fabrik & -Stand! Aus Shrimps + Schalen presst du deinen EIGENEN Energydrink.");
        if (isUnlocked("zone.LOGISTIK") && money >= 70_000)
            unlock("build.robotworks", "Freigeschaltet: Garnelen-Roboter-Werk! Roboter zählen als +2 Arbeiter.");
        if (isUnlocked("tier.WARKRILL"))
            unlock("build.barracks", "Freigeschaltet: Krill-Kaserne - jetzt lässt sich eine Armee aufbauen.");
        if (halle && money >= 15_000) unlock("build.water_hub", "Freigeschaltet: Wasseraufbereitungs-Hub.");
        if (halle && money >= 32_000)
            unlock("build.plankton", "Freigeschaltet: Plankton-Presse - Futterproduktion im Industrie-Maßstab.");
        if (halle && money >= 60_000)
            unlock("build.megatank", "Freigeschaltet: Mega-Becken - Shrimp-Zucht im Schwimmbad-Format (auch als Becken-Ausbau).");
        if (halle && money >= 95_000)
            unlock("build.geo", "Freigeschaltet: Geothermie-Bohrung - Grundlast-Strom aus 800 Metern Tiefe.");
        if (isUnlocked("zone.FORSCHUNG") && money >= 36_000)
            unlock("build.genlab", "Freigeschaltet: Genlabor - hier entsteht später der Designer-Shrimp.");
        if (halle && (money >= 20_000 || day >= 300))
            unlock("zone.FORSCHUNG", "Neue Zone: Forschungsflügel (Labore). Links die Standorte wechseln!");
        if (halle && (reputation >= 60 || money >= 26_000))
            unlock("zone.EMPFANG", "Neue Zone: Empfang & Garten (Restaurant, Besucherzentrum).");
        if (money >= 55_000) {
            unlock("zone.LOGISTIK", "Neue Zone: Logistik & Export.");
            unlock("build.export", "Freigeschaltet: Export-Hafen (kauft höhere Tiers).");
        }
        if (money >= 45_000)
            unlock("mkt.billboard", "Die Plakatfläche an der A6 ist frei - Marketing 'Autobahn-Plakat' buchbar.");
        if (money >= 85_000)
            unlock("build.blackmarket", "Ein dubioser Kontakt bietet dir einen Schwarzmarkt an...");
    }

    // ===================== Simulation =====================

    public void tick() {
        if (bankrupt) return;
        day++;
        double moneyBefore = money;
        double shellsBefore = shells, boostBefore = energy, robotsBefore = robots, wasteBefore = waste;
        incomeByType.clear();

        // --- Farm-Modifikatoren aus globalen Upgrades + Arbeiter-Politik ---
        FarmModifiers fm = new FarmModifiers();
        for (Building b : buildings)
            for (String id : b.upgrades) {
                Upgrade u = findUpgrade(b.type, id);
                if (u != null && u.global != null) fm.apply(u.global);
            }
        workerPolicy.apply(fm);
        for (Edict e : activeEdicts) e.apply(fm);

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
        wProvide += robots * 2;   // v3: jeder Roboter = +2 Arbeiter (Automatisierung)

        // --- Ambiente (Deko-System): Summe aller Deko-Beiträge, geklemmt.
        //     Wirkt auf Nachfrage, Bekanntheits-Zerfall und den Ruf-Sog (unten). ---
        double decoSum = 0;
        for (Building b : buildings) decoSum += b.lastStats.decoProduce;
        decoScore = clamp(decoSum, -20, 60);
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

        // --- Algen: Wasser -> Futter (Eimer wie Farm) ---
        double algaeWaterDemand = 0;
        for (Building b : buildings)
            if (b.type.isAlgae()) algaeWaterDemand += b.lastStats.waterUse;
        algaeWaterDemand *= opsEff;
        double algaeServe = algaeWaterDemand <= 0 ? 1.0 : clamp01(water / algaeWaterDemand);
        water -= algaeWaterDemand * algaeServe;
        double feedIn = 0;
        for (Building b : buildings)
            if (b.type.isAlgae()) feedIn += b.lastStats.feedProduce;
        feedIn *= opsEff * algaeServe;
        feed += feedIn;

        // --- Becken: Wasser+Futter -> Shrimps (je Tier; Aquarium wie Becken) ---
        double tankWaterDemand = 0, tankFeedDemand = 0;
        for (Building b : buildings)
            if (b.type.isTank()) {
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

        // Übrige Wasser-/Futterverbraucher (Darmentleerung, Fontäne, Streichelbecken, Kantine ...):
        // einfacher Abzug ohne eigene Engpass-Logik - Algen & Becken haben Vorrang.
        double miscWater = 0, miscFeed = 0;
        for (Building b : buildings) {
            if (b.type.isAlgae() || b.type.isTank()) continue;
            miscWater += b.lastStats.waterUse;
            miscFeed  += b.lastStats.feedUse;
        }
        miscWater *= opsEff; miscFeed *= opsEff;
        water = Math.max(0, water - miscWater);
        feed  = Math.max(0, feed  - miscFeed);

        double shrimpIn = 0;
        for (Building b : buildings) {
            if (b.type.isTank()) {
                double made = b.lastStats.shrimpProduce * opsEff * tankServe;
                ShrimpTier tier = isTierUnlocked(b.lastStats.tier) ? b.lastStats.tier : ShrimpTier.STANDARD;
                addStock(tier, made);
                producedByTier.merge(tier, made, Double::sum);
                shrimpIn += made;
                shells += made * 0.6 * shellMult(tier);   // v3: Schalen als Nebenprodukt
            }
        }
        totalShrimpProduced += shrimpIn;

        // Engpass: Shrimps sterben (alle Tiers anteilig). Je länger ein Mangel anhält,
        // desto härter die Konsequenzen (mehr Sterben, ab ~1 Woche zusätzlich Ruf-Verlust).
        boolean tanks = anyTank();
        boolean feedShortNow  = tanks && tankFeedServe  < 0.999;
        boolean waterShortNow = tanks && tankWaterServe < 0.999;
        boolean powerShortNow = powerRatio < 0.999 && pUse > 0;
        daysShortFeed  = feedShortNow  ? daysShortFeed  + 1 : 0;
        daysShortWater = waterShortNow ? daysShortWater + 1 : 0;
        daysShortPower = powerShortNow ? daysShortPower + 1 : 0;
        if (tanks) {
            if (feedShortNow)  { scaleAllStock(starve(0.96, tankFeedServe,  daysShortFeed));  shortageWarn("Futter", daysShortFeed); }
            if (waterShortNow) { scaleAllStock(starve(0.94, tankWaterServe, daysShortWater)); shortageWarn("Wasser", daysShortWater); }
            // Dauerhafter Stromausfall killt Pumpen/Filter -> ab dem 4. Tag sterben auch hier Shrimps.
            if (powerShortNow) {
                if (daysShortPower > 3) scaleAllStock(Math.max(0.9, 1.0 - 0.02 * (daysShortPower - 3)));
                shortageWarn("Strom", daysShortPower);
            }
            // Lange Nichtbeachtung ruiniert den Ruf (Kundschaft & Presse bekommen es mit).
            int worst = Math.max(daysShortFeed, Math.max(daysShortWater, daysShortPower));
            if (worst >= 8 && day % 2 == 0) addReputation(-1);
        }

        // --- Preisfaktoren ---
        // --- v3: Verarbeitung (Schalen -> SHRIMPBOOST -> Roboter / Kaserne) ---
        double repMultLocal = 0.6 + (reputation / 100.0) * 0.8;
        for (Building b : buildings) shells += b.lastStats.shellProduce * opsEff;   // Schälerei

        // Schalentier-Kraftwerk verfeuert jetzt WIRKLICH Schalen (der Name log bisher):
        // pro Kraftwerk bis zu 2 Schalen/Tag, vergütet als Fernwärme-Gutschrift.
        double burnCap = countType(BuildingType.POWER_PLANT) * 2.0 * opsEff;
        double burned = Math.min(shells, burnCap);
        if (burned > 0) {
            shells -= burned;
            double heat = burned * 1.5;
            money += heat;
            incomeByType.merge(BuildingType.POWER_PLANT, heat, Double::sum);
        }

        double bsShrimp = 0, bsShell = 0, bsOut = 0;
        for (Building b : buildings) if (b.type == BuildingType.SHRIMPBOOST_FACTORY) {
            bsShrimp += b.lastStats.shrimpUse; bsShell += b.lastStats.shellUse; bsOut += b.lastStats.boostProduce;
        }
        bsShrimp *= opsEff; bsShell *= opsEff;
        double bsServe = Math.min(bsShrimp <= 0 ? 1 : clamp01(getShrimpTotal() / bsShrimp),
                                  bsShell  <= 0 ? 1 : clamp01(shells / bsShell));
        consumeShrimpLowest(bsShrimp * bsServe);
        shells -= bsShell * bsServe;
        double boostMade = bsOut * opsEff * bsServe;
        energy += boostMade; totalBoostProduced += boostMade;

        double rwShell = 0, rwBoost = 0, rwOut = 0;
        for (Building b : buildings) if (b.type == BuildingType.ROBOT_WORKS) {
            rwShell += b.lastStats.shellUse; rwBoost += b.lastStats.boostUse; rwOut += b.lastStats.robotProduce;
        }
        rwShell *= opsEff; rwBoost *= opsEff;
        double rwServe = Math.min(rwShell <= 0 ? 1 : clamp01(shells / rwShell),
                                  rwBoost <= 0 ? 1 : clamp01(energy / rwBoost));
        shells -= rwShell * rwServe; energy -= rwBoost * rwServe;
        double robotsMade = rwOut * opsEff * rwServe;
        robots += robotsMade; totalRobotsProduced += robotsMade;

        double ksWar = 0, ksBoost = 0, ksArmy = 0;
        for (Building b : buildings) if (b.type == BuildingType.KRILL_BARRACKS) {
            ksWar += 2; ksBoost += b.lastStats.boostUse; ksArmy += b.lastStats.armyProduce;
        }
        ksWar *= opsEff; ksBoost *= opsEff;
        double warHave = shrimpStock.getOrDefault(ShrimpTier.WARKRILL, 0.0);
        double ksServe = Math.min(ksWar <= 0 ? 1 : clamp01(warHave / ksWar),
                                  ksBoost <= 0 ? 1 : clamp01(energy / ksBoost));
        addStock(ShrimpTier.WARKRILL, -ksWar * ksServe);
        energy -= ksBoost * ksServe;
        armyStrength += ksArmy * opsEff * ksServe;
        if (ksArmy <= 0) armyStrength = Math.max(0, armyStrength - 1);   // Verfall ohne Kaserne

        // --- Premium-Veredelung: Darmentleerung erzeugt Klärschlamm, Biogas-Anlage entsorgt ihn ---
        double wasteIn = 0, wasteDisposalCap = 0;
        for (Building b : buildings) {
            wasteIn += b.lastStats.wasteProduce;
            wasteDisposalCap += b.lastStats.wasteUse;
        }
        wasteIn *= opsEff; wasteDisposalCap *= opsEff;
        waste += wasteIn;
        double wasteDisposed = Math.min(waste, wasteDisposalCap);
        waste -= wasteDisposed;
        double biogasRevenue = wasteDisposed * 4 * repMultLocal;   // Biogas ins Netz - kleine Vergütung
        money += biogasRevenue;
        if (biogasRevenue > 0) incomeByType.merge(BuildingType.BIOGAS_PLANT, biogasRevenue, Double::sum);
        wasteNet = wasteIn - wasteDisposed;

        double standCap = 0;
        for (Building b : buildings) if (b.type == BuildingType.BOOST_STAND) standCap += b.lastStats.boostUse;
        standCap *= opsEff;
        double boostSold = Math.min(standCap, energy);
        energy -= boostSold;
        double boostRevenue = boostSold * 90 * repMultLocal;
        money += boostRevenue;
        if (boostRevenue > 0) incomeByType.merge(BuildingType.BOOST_STAND, boostRevenue, Double::sum);

        int labCount = countType(BuildingType.LAB);
        double labBonus = labCount * 0.12;
        double repMult = 0.6 + (reputation / 100.0) * 0.8;
        double tariff = 1.0 - exportTariff;
        double priceGlobal = (1 + labBonus) * repMult * fm.priceMult * tariff;
        double bestPrice = 0;

        // --- Marketing: Streams bauen Bekanntheit auf (je Kanal nur bis zu seiner
        //     Sättigungsgrenze, mit abnehmendem Ertrag nahe der Grenze), Bekanntheit
        //     zerfällt langsam ohne Werbung. Nachfrage = f(Bekanntheit, Reputation). ---
        // Ambiente bremst den Zerfall: schöne Farmen bleiben im Gespräch (Besucherfotos).
        double decay = (0.1 + awareness * 0.01) * (1 - clamp(decoScore, 0, 50) * 0.008);
        awareness = Math.max(0, awareness - decay);   // Zerfall: Ruhm ist flüchtig
        double mktCost = 0;
        for (MarketingStream s : activeStreams) {
            double cap = s.capFor(reputation);
            if (awareness < cap) awareness += s.awarenessPerDay * (cap - awareness) / cap;
            mktCost += s.costPerDay;
        }
        awareness = clamp(awareness, 0, 100);
        money -= mktCost;
        marketingCostLast = mktCost;
        double demandLeft = (BASE_DEMAND + awareness * DEMAND_PER_AWARENESS) * demandRepFactor()
            * decoDemandFactor();
        demandLast = demandLeft;

        // --- Märkte: verkaufen akzeptierte Tiers (wertvollste zuerst), begrenzt durch Nachfrage.
        //     Militär-Depot & Schwarzmarkt laufen über Verträge (keine Nachfrage nötig). ---
        double soldTotal = 0;
        for (Building b : buildings) {
            if (!b.type.isMarket()) continue;
            boolean consumer = b.type != BuildingType.MILITARY_DEPOT && b.type != BuildingType.BLACK_MARKET;
            double cap = b.lastStats.sellCap * opsEff;
            if (cap <= 0) continue;
            ShrimpTier[] accepted = b.type.acceptedTiers().clone();
            Arrays.sort(accepted, (x, y) -> Double.compare(y.baseValue, x.baseValue));
            for (ShrimpTier t : accepted) {
                if (cap <= 0) break;
                if (consumer && demandLeft <= 0) break;
                if (!isTierUnlocked(t)) continue;
                double avail = shrimpStock.get(t);
                double take = Math.min(avail, cap);
                if (consumer) take = Math.min(take, demandLeft);
                if (take <= 0) continue;
                double unitPrice = t.baseValue * b.lastStats.priceMult * priceGlobal;
                money += take * unitPrice;
                incomeByType.merge(b.type, take * unitPrice, Double::sum);
                addStock(t, -take);
                cap -= take;
                if (consumer) demandLeft -= take;
                soldTotal += take;
                reputation += take * t.repPerUnit;
                bestPrice = Math.max(bestPrice, unitPrice);
            }
        }
        sellPriceEff = bestPrice;
        shrimpSoldLast = soldTotal;
        totalSold += soldTotal;
        demandUsedLast = demandLast - demandLeft;

        // --- Reputation aus Gebäuden (Modi, Restaurant, Besucher, Solar, Kraftwerk ...) ---
        // v6: schwächerer Sog zur Mitte (40) - hoher Ruf erodiert langsam, niedriger
        // erholt sich nur zäh. Große Sprünge kommen aus Quests & Entscheidungen.
        // Ambiente verschiebt die Sog-Mitte: eine schöne Farm stabilisiert den Ruf höher.
        for (Building b : buildings) reputation += b.lastStats.repPerTick;
        reputation += fm.repPerTick;
        double repCenter = 40 + clamp(decoScore, -20, 50) * 0.12;
        reputation += (repCenter - reputation) * 0.006;
        reputation = clamp(reputation, 0, 100);

        // --- Betriebskosten ---
        double upkeep = 0;
        for (Building b : buildings) upkeep += b.lastStats.upkeep;
        money -= upkeep;
        upkeepLast = upkeep;

        if (water < 0) water = 0;
        if (feed < 0) feed = 0;

        // --- Status je Gebäude ---
        boolean demandExhausted = demandLeft <= 0.01;
        for (Building b : buildings) {
            double eff;
            switch (b.type) {
                case POWER_PLANT, SOLAR_ROOF, OLD_GENERATOR, WIND_TURBINE, GEO_PLANT, SHRIMP_WHEEL -> eff = workerRatio;
                case HOUSING, CAMPER, HEADQUARTERS, ZEN_GARDEN, CANTEEN -> eff = powerRatio;
                case ALGAE_FARM, ALGAE_BUCKET, PLANKTON_PRESS -> eff = opsEff * algaeServe;
                case SHRIMP_TANK, GARAGE_TANK, KIDDIE_POOL, HATCHERY, MEGA_TANK, REEF_DOME -> eff = opsEff * tankServe;
                default -> eff = opsEff;
            }
            String note = "";
            boolean consumerMarket = b.type.isMarket()
                && b.type != BuildingType.MILITARY_DEPOT && b.type != BuildingType.BLACK_MARKET;
            if (powerRatio < 0.999 && b.lastStats.powerUse > 0) note = "Strom knapp";
            else if (workerRatio < 0.999 && b.lastStats.workerNeed > 0) note = "Personal fehlt";
            else if (b.type.isTank() && tankServe < 0.999) note = "Input knapp";
            else if (b.type.isMarket() && getShrimpTotal() < 1 && b.lastStats.sellCap > 0) note = "Kein Bestand";
            else if (consumerMarket && demandExhausted && getShrimpTotal() >= 1) note = "Nachfrage gedeckt - Marketing!";
            b.efficiency = clamp01(eff);
            b.statusNote = note;
        }

        // --- HUD-Flüsse ---
        moneyNet = money - moneyBefore;
        shellsNet = shells - shellsBefore;
        boostNet = energy - boostBefore;
        robotsNet = robots - robotsBefore;
        incomeLast = 0; for (double v : incomeByType.values()) incomeLast += v;
        waterNet = waterIn - algaeWaterDemand * algaeServe - tankWaterDemand * tankServe - miscWater;
        feedNet = feedIn - tankFeedDemand * tankServe - miscFeed;
        shrimpNet = shrimpIn - soldTotal;

        // --- Lagergrenzen: was nicht ins Lager passt, verfällt (bremst Horten & Tempo) ---
        boolean overflow = false;
        if (water > getCapWater())   { water = getCapWater(); overflow = true; }
        if (feed > getCapFeed())     { feed = getCapFeed(); overflow = true; }
        if (shells > getCapShells()) { shells = getCapShells(); overflow = true; }
        if (energy > getCapBoost())  { energy = getCapBoost(); overflow = true; }
        double totalShr = getShrimpTotal();
        if (totalShr > getCapShrimp()) { scaleAllStock(getCapShrimp() / totalShr); overflow = true; }
        if (overflow && day - lastStorageWarnDay >= 4) {
            lastStorageWarnDay = day;
            log("Lager voll - Überschuss verfällt! Bau oder upgrade ein Lager.", LOG_WARN);
        }
        // Abfall separat: überlaufender Klärschlamm wird "wild entsorgt" und kostet Reputation.
        if (waste > getCapWaste()) {
            double dumped = waste - getCapWaste();
            waste = getCapWaste();
            reputation = clamp(reputation - Math.min(3, dumped * 0.05), 0, 100);
            if (day - lastWasteWarnDay >= 3) {
                lastWasteWarnDay = day;
                log("Abfall-Lager voll - Klärschlamm läuft über und stinkt zum Himmel! Bau eine Biogas-Kläranlage (-Reputation).", LOG_BAD);
            }
        }

        checkMilestoneUnlocks();

        // --- Sieg / Pleite ---
        if (!goalReached && money >= GOAL_MONEY) {
            goalReached = true;
            log("Reichtums-Meilenstein erreicht! Es gibt aber mehrere Wege zum Sieg - siehe Almanach.", LOG_GOOD);
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
        double deco = t.deco();
        if (t == BuildingType.SOLAR_ROOF) rep += 0.015;
        if (t == BuildingType.POWER_PLANT) rep -= 0.05;
        if (t == BuildingType.OLD_GENERATOR) rep -= 0.02;

        // Modus + Upgrades zu kombinierten Faktoren einsammeln, dann in einem Zug anwenden.
        // prodMult skaliert die Hauptproduktion UND die v3-Flows (Schalen/Boost/Roboter/Abfall).
        double shrimpM = 1, powerM = 1, waterM = 1, feedM = 1, upkeepM = 1, capM = 1;
        double prodM = 1, workerM = 1, priceM = 1;
        List<Mode> ms = BuildingCatalog.modes(t);
        if (b.mode >= 0 && b.mode < ms.size()) {
            Mode m = ms.get(b.mode);
            if (m.requiresFlag != null && !isUnlocked(m.requiresFlag)) m = ms.get(0);
            shrimpM *= m.shrimpMult; powerM *= m.powerMult; waterM *= m.waterMult;
            feedM *= m.feedMult; upkeepM *= m.upkeepMult; capM *= m.capMult;
            prodM *= m.prodMult; workerM *= m.workerMult; priceM *= m.priceMult;
            rep += m.repAdd; deco += m.decoAdd;
            if (m.tierOverride != null) s.tier = m.tierOverride;
        }
        for (String id : b.upgrades) {
            Upgrade u = findUpgrade(t, id);
            if (u == null) continue;
            shrimpM *= u.shrimpMult; powerM *= u.powerMult; waterM *= u.waterMult;
            feedM *= u.feedMult; upkeepM *= u.upkeepMult; capM *= u.capMult;
            prodM *= u.prodMult; workerM *= u.workerMult; priceM *= u.priceMult;
            rep += u.repAdd; deco += u.decoAdd;
            if (u.tierOverride != null) s.tier = u.tierOverride;
        }
        s.shrimpProduce *= shrimpM; s.powerUse *= powerM; s.waterUse *= waterM;
        s.feedUse *= feedM; s.upkeep *= upkeepM; s.sellCap *= capM;
        s.powerProduce *= prodM; s.waterProduce *= prodM; s.feedProduce *= prodM;

        // HQ-Nähe-Bonus: kurze Wege zur Verwaltung. Bis zu +10% Output nahe am HQ,
        // linear abfallend mit der Distanz - Abstand ist nie ein Malus.
        double hq = hqProximityBoost(b);
        if (hq > 0) {
            s.shrimpProduce *= 1 + hq;
            s.powerProduce *= 1 + hq; s.waterProduce *= 1 + hq; s.feedProduce *= 1 + hq;
            s.sellCap *= 1 + hq;
        }
        if (t.isTank()) s.shrimpProduce *= fm.tankShrimpMult;
        s.powerUse *= fm.powerUseMult;
        s.upkeep *= fm.upkeepMult;
        if (s.waterProduce > 0) s.waterProduce *= fm.waterProduceMult;
        if (s.feedProduce > 0) s.feedProduce *= fm.feedProduceMult;
        if (t.isMarket()) s.sellCap *= fm.sellCapMult;
        s.workerProvide = (int) Math.round(s.workerProvide * workerM * fm.workerMult);
        if (!isTierUnlocked(s.tier)) s.tier = ShrimpTier.STANDARD;
        s.repPerTick = rep;
        s.decoProduce = deco;
        s.priceMult = t.priceMult() * priceM;
        Flows fl = t.flows();
        s.shrimpUse = fl.shrimpUse * prodM;
        s.shellProduce = fl.shellProduce * prodM; s.shellUse = fl.shellUse * prodM;
        s.boostProduce = fl.boostProduce * prodM; s.boostUse = fl.boostUse * prodM;
        s.robotProduce = fl.robotProduce * prodM; s.robotUse = fl.robotUse * prodM;
        s.armyProduce = fl.armyProduce * prodM;
        s.wasteProduce = fl.wasteProduce * prodM; s.wasteUse = fl.wasteUse * prodM;
        return s;
    }

    // ===================== Lagerkapazität =====================

    private int lastStorageWarnDay = -100;
    private int lastWasteWarnDay = -100;

    /** Gesamtkapazität für Index 0=Wasser, 1=Futter, 2=Shrimps, 3=Schalen, 4=Boost. */
    private double storageCap(int idx) {
        double c = 0;
        for (Building b : buildings) {
            double[] s = b.type.storage();
            if (s != null) c += s[idx] * storageMultOf(b);
        }
        return c;
    }

    /** Kombinierter Lager-Faktor eines Gebäudes aus Modus + Upgrades (Kühlhaus & Co.). */
    public double storageMultOf(Building b) {
        double m = 1;
        List<Mode> ms = BuildingCatalog.modes(b.type);
        if (b.mode >= 0 && b.mode < ms.size()) {
            Mode md = ms.get(b.mode);
            if (md.requiresFlag == null || isUnlocked(md.requiresFlag)) m *= md.storageMult;
        }
        for (String id : b.upgrades) {
            Upgrade u = findUpgrade(b.type, id);
            if (u != null) m *= u.storageMult;
        }
        return m;
    }
    public double getCapWater()  { return storageCap(0); }
    public double getCapFeed()   { return storageCap(1); }
    public double getCapShrimp() { return storageCap(2); }
    public double getCapShells() { return storageCap(3); }
    public double getCapBoost()  { return storageCap(4); }
    public double getCapWaste()  { return storageCap(5); }

    /** Maximaler HQ-Nähe-Bonus und Reichweite (Chebyshev-Distanz in Kacheln). */
    public static final double HQ_BOOST_MAX = 0.10;
    public static final int HQ_BOOST_RANGE = 4;

    /** Output-Bonus durch Nähe zum HQ: Distanz 1 → +10%, fällt linear bis Distanz 4, danach 0. */
    public double hqProximityBoost(Building b) {
        if (b == null || b.type == BuildingType.HEADQUARTERS) return 0;
        for (Building x : buildings) {
            if (x.type != BuildingType.HEADQUARTERS || x.zone != b.zone) continue;
            int dist = Math.max(Math.abs(x.col - b.col), Math.abs(x.row - b.row));
            if (dist < 1 || dist > HQ_BOOST_RANGE) return 0;
            return HQ_BOOST_MAX * (HQ_BOOST_RANGE + 1 - dist) / (double) HQ_BOOST_RANGE;
        }
        return 0;
    }

    private Upgrade findUpgrade(BuildingType t, String id) {
        for (Upgrade u : BuildingCatalog.upgrades(t)) if (u.id.equals(id)) return u;
        return null;
    }

    /** Aufeinanderfolgende Tage mit Futter-/Wasser-/Strommangel (0 = versorgt) - für HUD & Eskalation. */
    private int daysShortFeed = 0, daysShortWater = 0, daysShortPower = 0;
    public int feedShortDays()  { return daysShortFeed; }
    public int waterShortDays() { return daysShortWater; }
    public int powerShortDays() { return daysShortPower; }

    /** Verlustfaktor pro Tick: Grundverlust wächst mit der Dauer des Mangels (bis zu ~+18%). */
    private static double starve(double baseKeep, double serve, int days) {
        double extra = Math.min(0.18, 0.018 * Math.max(0, days - 1));
        return Math.max(0.55, (baseKeep - extra) + (1 - baseKeep) * serve);
    }

    private int lastWarnDay = -100;
    /** Eskalierende Mangel-Warnung: Ton verschärft sich mit der Dauer der Nichtbeachtung. */
    private void shortageWarn(String what, int days) {
        if (day - lastWarnDay < 3) return;
        lastWarnDay = day;
        String msg;
        if (days >= 12)     msg = what + "-KRISE (Tag " + days + "): Die Becken kippen - Massensterben und Ruf-Verlust!";
        else if (days >= 6) msg = what + " seit " + days + " Tagen knapp - die Shrimps STERBEN!";
        else                msg = what + " knapp - die Shrimps leiden!";
        log(msg, LOG_WARN);
    }

    private int countType(BuildingType t) { int n = 0; for (Building b : buildings) if (b.type == t) n++; return n; }
    private boolean anyTank() { for (Building b : buildings) if (b.type.isTank()) return true; return false; }

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
        /** Echtzeit-Stempel: frische Zeilen werden im Log/als Toast hervorgehoben. */
        public final long time = System.currentTimeMillis();
        public LogLine(int day, String text, int kind) { this.day = day; this.text = text; this.kind = kind; }
    }

    // ===================== Helfer =====================

    private static double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }
    private static double shellMult(ShrimpTier t) {
        return switch (t) {
            case GOURMET -> 1.1; case PROTEIN -> 1.3; case GENTECH -> 1.4; case WARKRILL -> 1.6;
            default -> 1.0;
        };
    }
    private void consumeShrimpLowest(double amount) {
        for (ShrimpTier t : ShrimpTier.values()) {
            if (amount <= 0) break;
            double have = shrimpStock.getOrDefault(t, 0.0);
            double take = Math.min(have, amount);
            if (take > 0) { shrimpStock.put(t, have - take); amount -= take; }
        }
    }
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
    public double getProducedByTier(ShrimpTier t) { return t == null ? 0 : producedByTier.getOrDefault(t, 0.0); }
    public double getTotalSold() { return totalSold; }
    public double getArmy() { return armyStrength; }
    public void addArmy(double d) { armyStrength = Math.max(0, armyStrength + d); }
    public double getShells() { return shells; }
    public double getEnergy() { return energy; }
    public double getRobots() { return robots; }
    public double getWaste()  { return waste; }
    public double getWasteNet() { return wasteNet; }
    public void addShells(double d) { shells = Math.max(0, shells + d); }
    public void addEnergy(double d) { energy = Math.max(0, energy + d); }
    public void addRobots(double d) { robots = Math.max(0, robots + d); }
    public double getTotalBoostProduced() { return totalBoostProduced; }
    public double getTotalRobotsProduced() { return totalRobotsProduced; }
    public double getIncomeLast() { return incomeLast; }
    public double getShellsNet() { return shellsNet; }
    public double getBoostNet() { return boostNet; }
    public double getRobotsNet() { return robotsNet; }
    public java.util.Map<BuildingType, Double> incomeByType() { return incomeByType; }

    /** Liste aller aktiven farmweiten Modifikatoren samt Quelle (fuer Almanach/Transparenz). */
    public java.util.List<String> activeEffects() {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (Building b : buildings)
            for (String id : b.upgrades) {
                Upgrade u = findUpgrade(b.type, id);
                if (u != null && u.global != null)
                    out.add(b.type.shortName() + " - " + u.name + ": " + effectDesc(u.global));
            }
        if (workerPolicy != WorkerPolicy.REGULAR) out.add("Arbeiter-Politik: " + workerPolicy.displayName);
        if (exportTariff > 0) out.add("Akwanov-Embargo: -" + (int) (exportTariff * 100) + "% Verkaufspreis");
        if (Math.abs(decoScore) >= 0.5) {
            long pct = Math.round((decoDemandFactor() - 1) * 100);
            out.add("Ambiente " + (decoScore >= 0 ? "+" : "") + Math.round(decoScore)
                + ": " + (pct >= 0 ? "+" : "") + pct + "% Nachfrage"
                + (decoScore > 0 ? ", Bekanntheit hält länger, Ruf stabilisiert sich höher" : " (Schandflecke schrecken ab)"));
        }
        if (!activeStreams.isEmpty()) {
            double c = 0;
            for (MarketingStream s : activeStreams) c += s.costPerDay;
            out.add("Marketing: " + activeStreams.size() + " Stream(s), Bekanntheit "
                + Math.round(awareness) + "/100, -" + Math.round(c) + " Geld/Tag");
        }
        return out;
    }
    private static String effectDesc(GlobalEffect g) {
        String pct = (g.magnitude >= 1 ? "+" : "-") + Math.round(Math.abs(g.magnitude - 1) * 100) + "%";
        return switch (g.type) {
            case TANK_SHRIMP_MULT -> pct + " Becken-Output";
            case POWERUSE_MULT -> pct + " Stromverbrauch";
            case UPKEEP_MULT -> pct + " Betriebskosten";
            case PRICE_MULT -> pct + " Verkaufspreis";
            case WATER_PRODUCE_MULT -> pct + " Wasserproduktion";
            case FEED_PRODUCE_MULT -> pct + " Futterproduktion";
            case WORKER_MULT -> pct + " Arbeiter";
            case SELLCAP_MULT -> pct + " Verkaufskapazität";
            case REP_PER_TICK -> (g.magnitude >= 0 ? "+" : "") + g.magnitude + " Reputation/Tag";
        };
    }
    public double getResource(String key) {
        return switch (key == null ? "" : key) {
            case "money" -> money; case "water" -> water; case "feed" -> feed;
            case "shells" -> shells; case "boost", "energy" -> energy; case "robots" -> robots;
            case "waste" -> waste;
            case "shrimp" -> getShrimpTotal(); default -> 0;
        };
    }

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

    // ===================== Speichern / Laden =====================
    // Der Zustand wandert als key=value-Paare in eine Map; Datei-Format & Escaping
    // übernimmt SaveManager. readSave ist das exakte Gegenstück zu writeSave.

    /** Schreibt den kompletten Spielzustand in die Map (Schlüssel ohne '='). */
    public void writeSave(java.util.Map<String, String> m) {
        m.put("money", String.valueOf(money));
        m.put("water", String.valueOf(water));
        m.put("feed", String.valueOf(feed));
        m.put("reputation", String.valueOf(reputation));
        m.put("awareness", String.valueOf(awareness));
        m.put("day", String.valueOf(day));
        m.put("goalReached", String.valueOf(goalReached));
        m.put("bankrupt", String.valueOf(bankrupt));
        m.put("negativeStreak", String.valueOf(negativeStreak));
        m.put("totalShrimpProduced", String.valueOf(totalShrimpProduced));
        m.put("exportTariff", String.valueOf(exportTariff));
        m.put("decoScore", String.valueOf(decoScore));
        m.put("totalSold", String.valueOf(totalSold));
        m.put("armyStrength", String.valueOf(armyStrength));
        m.put("shells", String.valueOf(shells));
        m.put("energy", String.valueOf(energy));
        m.put("robots", String.valueOf(robots));
        m.put("waste", String.valueOf(waste));
        m.put("totalBoostProduced", String.valueOf(totalBoostProduced));
        m.put("totalRobotsProduced", String.valueOf(totalRobotsProduced));
        m.put("workerPolicy", workerPolicy.name());
        m.put("daysShortFeed", String.valueOf(daysShortFeed));
        m.put("daysShortWater", String.valueOf(daysShortWater));
        m.put("daysShortPower", String.valueOf(daysShortPower));
        m.put("lastStorageWarnDay", String.valueOf(lastStorageWarnDay));
        m.put("lastWasteWarnDay", String.valueOf(lastWasteWarnDay));
        m.put("lastWarnDay", String.valueOf(lastWarnDay));
        m.put("eventDay", String.valueOf(eventSystem.lastEventDay()));
        for (ShrimpTier t : ShrimpTier.values()) {
            m.put("stock." + t.name(), String.valueOf(shrimpStock.getOrDefault(t, 0.0)));
            m.put("prodTier." + t.name(), String.valueOf(producedByTier.getOrDefault(t, 0.0)));
        }
        m.put("unlocked", String.join(",", unlocked));
        m.put("edicts", joinNames(activeEdicts));
        m.put("streams", joinNames(activeStreams));
        int n = 0;
        for (Building b : buildings) {
            m.put("b." + n, b.type.name() + "|" + b.zone.name() + "|" + b.col + "|" + b.row
                + "|" + b.mode + "|" + b.placedOnDay + "|" + String.join(";", b.upgrades));
            n++;
        }
        int ln = 0;
        int from = Math.max(0, log.size() - 80);
        for (int i = from; i < log.size(); i++) {
            LogLine l = log.get(i);
            m.put("log." + ln, l.kind + "|" + l.day + "|" + l.text);
            ln++;
        }
        int an = 0;
        for (String[] a : announcements) { m.put("ann." + an, a[0] + "|" + a[1]); an++; }
    }

    private static String joinNames(java.util.Collection<? extends Enum<?>> c) {
        StringBuilder sb = new StringBuilder();
        for (Enum<?> e : c) { if (sb.length() > 0) sb.append(','); sb.append(e.name()); }
        return sb.toString();
    }

    /** Stellt einen Spielstand aus den key=value-Paaren wieder her. */
    public static GameState readSave(java.util.Map<String, String> m) {
        GameState gs = new GameState(new Random());
        // Konstruktor-Nebenwirkungen (HQ, Start-Shrimps, Willkommens-Log) verwerfen
        gs.buildings.clear();
        for (Zone z : Zone.values()) gs.grids.put(z, new Building[ROWS][COLS]);
        gs.log.clear();
        gs.announcements.clear();
        for (ShrimpTier t : ShrimpTier.values()) gs.shrimpStock.put(t, 0.0);

        gs.money = d(m, "money", 2_500);
        gs.water = d(m, "water", 80);
        gs.feed = d(m, "feed", 60);
        gs.reputation = d(m, "reputation", 40);
        gs.awareness = d(m, "awareness", 0);
        gs.day = i(m, "day", 0);
        gs.goalReached = bo(m, "goalReached");
        gs.bankrupt = bo(m, "bankrupt");
        gs.negativeStreak = i(m, "negativeStreak", 0);
        gs.totalShrimpProduced = d(m, "totalShrimpProduced", 0);
        gs.exportTariff = d(m, "exportTariff", 0);
        gs.decoScore = d(m, "decoScore", 0);
        gs.totalSold = d(m, "totalSold", 0);
        gs.armyStrength = d(m, "armyStrength", 0);
        gs.shells = d(m, "shells", 0);
        gs.energy = d(m, "energy", 0);
        gs.robots = d(m, "robots", 0);
        gs.waste = d(m, "waste", 0);
        gs.totalBoostProduced = d(m, "totalBoostProduced", 0);
        gs.totalRobotsProduced = d(m, "totalRobotsProduced", 0);
        gs.daysShortFeed = i(m, "daysShortFeed", 0);
        gs.daysShortWater = i(m, "daysShortWater", 0);
        gs.daysShortPower = i(m, "daysShortPower", 0);
        gs.lastStorageWarnDay = i(m, "lastStorageWarnDay", -100);
        gs.lastWasteWarnDay = i(m, "lastWasteWarnDay", -100);
        gs.lastWarnDay = i(m, "lastWarnDay", -100);
        gs.eventSystem.restoreLastEventDay(i(m, "eventDay", -100));
        try { gs.workerPolicy = WorkerPolicy.valueOf(m.getOrDefault("workerPolicy", "REGULAR")); }
        catch (IllegalArgumentException ignored) { }
        for (ShrimpTier t : ShrimpTier.values()) {
            gs.shrimpStock.put(t, d(m, "stock." + t.name(), 0));
            double p = d(m, "prodTier." + t.name(), 0);
            if (p > 0) gs.producedByTier.put(t, p);
        }
        for (String f : split(m.get("unlocked"))) gs.unlocked.add(f);
        for (String e : split(m.get("edicts"))) {
            try { gs.activeEdicts.add(Edict.valueOf(e)); } catch (IllegalArgumentException ignored) { }
        }
        for (String s : split(m.get("streams"))) {
            try { gs.activeStreams.add(MarketingStream.valueOf(s)); } catch (IllegalArgumentException ignored) { }
        }
        for (int n = 0; m.containsKey("b." + n); n++) {
            String[] p = m.get("b." + n).split("\\|", -1);
            if (p.length < 7) continue;
            try {
                BuildingType type = BuildingType.valueOf(p[0]);
                Zone zone = Zone.valueOf(p[1]);
                int col = Integer.parseInt(p[2]), row = Integer.parseInt(p[3]);
                if (!gs.inBounds(col, row) || gs.grids.get(zone)[row][col] != null) continue;
                Building b = new Building(type, zone, col, row);
                b.mode = Integer.parseInt(p[4]);
                b.placedOnDay = Integer.parseInt(p[5]);
                if (!p[6].isEmpty()) for (String id : p[6].split(";")) b.upgrades.add(id);
                gs.grids.get(zone)[row][col] = b;
                gs.buildings.add(b);
            } catch (IllegalArgumentException ignored) { }   // unbekannter Typ/Zone: überspringen
        }
        // Sicherheitsnetz: ohne HQ funktionieren Arbeiter & HQ-Bonus nicht
        boolean hasHq = false;
        for (Building b : gs.buildings) if (b.type == BuildingType.HEADQUARTERS) hasHq = true;
        if (!hasHq) gs.place(BuildingType.HEADQUARTERS, Zone.PRODUKTION, COLS / 2, ROWS / 2, false);
        for (int n = 0; m.containsKey("log." + n); n++) {
            String[] p = m.get("log." + n).split("\\|", 3);
            if (p.length < 3) continue;
            try { gs.log.add(new LogLine(Integer.parseInt(p[1]), p[2], Integer.parseInt(p[0]))); }
            catch (NumberFormatException ignored) { }
        }
        for (int n = 0; m.containsKey("ann." + n); n++) {
            String[] p = m.get("ann." + n).split("\\|", 2);
            if (p.length == 2) gs.announcements.add(new String[]{p[0], p[1]});
        }
        // Stats vorwärmen, damit Inspektor/HUD direkt nach dem Laden plausibel sind
        FarmModifiers fm = new FarmModifiers();
        for (Building b : gs.buildings)
            for (String id : b.upgrades) {
                Upgrade u = gs.findUpgrade(b.type, id);
                if (u != null && u.global != null) fm.apply(u.global);
            }
        gs.workerPolicy.apply(fm);
        for (Edict e : gs.activeEdicts) e.apply(fm);
        for (Building b : gs.buildings) b.lastStats = gs.computeStats(b, fm);
        return gs;
    }

    private static String[] split(String v) {
        return (v == null || v.isEmpty()) ? new String[0] : v.split(",");
    }
    private static double d(java.util.Map<String, String> m, String k, double def) {
        try { return Double.parseDouble(m.getOrDefault(k, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }
    private static int i(java.util.Map<String, String> m, String k, int def) {
        try { return Integer.parseInt(m.getOrDefault(k, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }
    private static boolean bo(java.util.Map<String, String> m, String k) {
        return Boolean.parseBoolean(m.getOrDefault(k, "false"));
    }
}
