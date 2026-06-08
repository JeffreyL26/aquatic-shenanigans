package com.shrimptopia.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Registry der Betriebs-Modi und Upgrades pro Gebäudetyp.
 * Becken-Modi legen das produzierte Shrimp-Tier fest (teils freischaltungsabhängig).
 */
public final class BuildingCatalog {

    private BuildingCatalog() {}

    private static final EnumMap<BuildingType, List<Mode>> MODES = new EnumMap<>(BuildingType.class);
    private static final EnumMap<BuildingType, List<Upgrade>> UPGRADES = new EnumMap<>(BuildingType.class);

    private static void modes(BuildingType t, Mode... ms) { MODES.put(t, List.of(ms)); }
    private static void upgrades(BuildingType t, Upgrade... us) { UPGRADES.put(t, List.of(us)); }

    static {
        // ---------------- Shrimp-Becken: Modus = Tier-Wahl + Trade-off ----------------
        modes(BuildingType.SHRIMP_TANK,
            new Mode("std",  "Standard-Zucht",   "Solide Massenware. Tier: Standard.").tier(ShrimpTier.STANDARD),
            new Mode("mass", "Masse statt Klasse", "+35% Output, +20% Strom, leicht schlechtere Reputation.")
                .shrimp(1.35).power(1.2).rep(-0.01).tier(ShrimpTier.STANDARD),
            new Mode("bio",  "Bio-Zucht",        "Tier: Bio. Mehr Futter, etwas weniger Stück - aber wertvoller.")
                .tier(ShrimpTier.BIO).feed(1.3).shrimp(0.85).needs("tier.BIO"),
            new Mode("gourmet", "Gourmet-Zucht",  "Tier: Gourmet. Hoher Futterbedarf, kleine Mengen, Spitzenpreise.")
                .tier(ShrimpTier.GOURMET).feed(1.6).shrimp(0.7).needs("tier.GOURMET"),
            new Mode("protein", "Protein-Drill",  "Tier: Protein-Bombe. General Krillkills Liebling. Futter- und stromhungrig.")
                .tier(ShrimpTier.PROTEIN).feed(1.8).power(1.3).shrimp(0.75).rep(-0.02).needs("tier.PROTEIN"),
            new Mode("gentech", "Designer-Becken", "Tier: Designer-Shrimp. Sehr wertvoll, sehr umstritten.")
                .tier(ShrimpTier.GENTECH).power(1.5).shrimp(0.65).rep(-0.04).needs("tier.GENTECH"),
            new Mode("warkrill", "Kampf-Krill-Aufzucht", "Tier: Kampf-Krill. Das Endgame. Brutal teuer im Betrieb.")
                .tier(ShrimpTier.WARKRILL).feed(2.0).power(1.6).shrimp(0.5).rep(-0.05).needs("tier.WARKRILL"));
        upgrades(BuildingType.SHRIMP_TANK,
            new Upgrade("autofeed", "Automatik-Fütterung", 400, "-20% Futterverbrauch dieses Beckens.").feed(0.8),
            new Upgrade("oxygen",  "Sauerstoff-Anreicherung", 500, "+20% Shrimp-Output dieses Beckens.").shrimp(1.2));

        // ---------------- Kraftwerk ----------------
        modes(BuildingType.POWER_PLANT,
            new Mode("norm", "Normalbetrieb", "Ausgewogen."),
            new Mode("over", "Overdrive", "+40% Strom, +50% Kosten, mehr Gestank (-Reputation).").prod(1.4).upkeep(1.5).rep(-0.04),
            new Mode("eco",  "Eco-Drossel", "-30% Strom, -40% Kosten, etwas saubererer Ruf.").prod(0.7).upkeep(0.6).rep(0.02));
        upgrades(BuildingType.POWER_PLANT,
            new Upgrade("filter", "Abgasfilter", 600, "Saubererer Betrieb: +Reputation/Tag.").rep(0.06),
            new Upgrade("turbine", "Turbinen-Tuning", 700, "+20% Stromproduktion.").prod(1.2));

        // ---------------- Solar ----------------
        modes(BuildingType.SOLAR_ROOF,
            new Mode("fix",  "Feststehend", "Wartungsarm."),
            new Mode("track", "Sonnen-Nachführung", "+25% Strom, +40% Kosten.").prod(1.25).upkeep(1.4));
        upgrades(BuildingType.SOLAR_ROOF,
            new Upgrade("battery", "Pufferbatterie", 500, "+15% Strom.").prod(1.15));

        // ---------------- Wasserwerk / Hub ----------------
        modes(BuildingType.WATER_PLANT,
            new Mode("norm", "Normaldruck", "Ausgewogen."),
            new Mode("high", "Hochdruck", "+30% Wasser, +40% Strom.").prod(1.3).power(1.4).upkeep(1.2));
        upgrades(BuildingType.WATER_PLANT,
            new Upgrade("recycle", "Recycling-Kreislauf", 800, "[FARMWEIT] +15% Wasserproduktion aller Werke.")
                .global(GlobalEffect.Type.WATER_PRODUCE_MULT, 1.15));
        modes(BuildingType.WATER_HUB,
            new Mode("norm", "Normalbetrieb", "Ausgewogen."),
            new Mode("turbo", "Turbo-Filter", "+35% Wasser, +50% Strom.").prod(1.35).power(1.5).upkeep(1.3));
        upgrades(BuildingType.WATER_HUB,
            new Upgrade("smart", "Smart-Steuerung", 900, "[FARMWEIT] -10% Stromverbrauch der ganzen Farm.")
                .global(GlobalEffect.Type.POWERUSE_MULT, 0.9));

        // ---------------- Algen-Futterfarm ----------------
        modes(BuildingType.ALGAE_FARM,
            new Mode("norm", "Standard-Algen", "Ausgewogen."),
            new Mode("turbo", "Turbo-Algen", "+40% Futter, +30% Strom & Wasser.").prod(1.4).power(1.3).water(1.3),
            new Mode("eco", "Spar-Algen", "-25% Futter, -40% Kosten.").prod(0.75).upkeep(0.6));
        upgrades(BuildingType.ALGAE_FARM,
            new Upgrade("nutrient", "Nährstoff-Boost", 600, "+25% Futterproduktion dieser Farm.").prod(1.25),
            new Upgrade("cleanfeed", "Bio-Futter-Zertifikat", 900, "[KETTE] Schaltet Bio-Shrimps frei.").grants("tier.BIO"));

        // ---------------- Wohnheim / Arbeiter ----------------
        modes(BuildingType.HOUSING,
            new Mode("std", "Standard-Kojen", "Ausgewogen."),
            new Mode("comfort", "Komfort-Suiten", "+Reputation, aber +50% Kosten.").rep(0.05).upkeep(1.5),
            new Mode("capsule", "Kapsel-Hotel", "Eng & billig: -40% Kosten, -Reputation.").upkeep(0.6).rep(-0.03));
        upgrades(BuildingType.HOUSING,
            new Upgrade("app", "Mitarbeiter-App", 600, "[FARMWEIT] +10% verfügbare Arbeiter.")
                .global(GlobalEffect.Type.WORKER_MULT, 1.1),
            new Upgrade("training", "Trainingsprogramm", 900, "[FARMWEIT] +15% Becken-Output (gut geschultes Personal).")
                .global(GlobalEffect.Type.TANK_SHRIMP_MULT, 1.15),
            new Upgrade("bootcamp", "Krillkill-Bootcamp", 1100, "[FREISCHALT] Erlaubt die Arbeiter-Politik 'Bootcamp'.")
                .grants("worker.bootcamp").needs("krillkill.bootcamp"));

        // ---------------- Börse & Märkte ----------------
        modes(BuildingType.SALES_OFFICE,
            new Mode("norm", "Normalbetrieb", "Ausgewogen."),
            new Mode("aggro", "Aggressives Marketing", "+40% Verkaufsmenge, +30% Kosten, -Reputation.").cap(1.4).upkeep(1.3).rep(-0.02),
            new Mode("boutique", "Premium-Boutique", "-30% Menge, dafür +Reputation.").cap(0.7).rep(0.03));
        upgrades(BuildingType.SALES_OFFICE,
            new Upgrade("onlineshop", "Online-Shop", 700, "[FARMWEIT] +15% Verkaufskapazität aller Märkte.")
                .global(GlobalEffect.Type.SELLCAP_MULT, 1.15));

        // ---------------- Labor / Genlabor ----------------
        modes(BuildingType.LAB,
            new Mode("basic", "Grundlagenforschung", "Solide, etwas Reputation.").rep(0.02),
            new Mode("market", "Marktforschung", "Treibt Preise (siehe Upgrade), kostet mehr.").upkeep(1.3));
        upgrades(BuildingType.LAB,
            new Upgrade("biocert", "Bio-Zertifizierung", 800, "[KETTE] Schaltet Bio-Shrimps frei.").grants("tier.BIO"),
            new Upgrade("gourmetcert", "Gourmet-Protokoll", 1200, "[KETTE] Schaltet Gourmet-Shrimps frei.").grants("tier.GOURMET").needs("tier.BIO"),
            new Upgrade("patents", "Patentportfolio", 1400, "[FARMWEIT] +10% Verkaufspreis.")
                .global(GlobalEffect.Type.PRICE_MULT, 1.1));
        modes(BuildingType.GENLAB,
            new Mode("safe", "Vorschriftsmäßig", "Ethik-Kommission zufrieden."),
            new Mode("yolo", "Move fast, break shrimp", "Schnellere Resultate, schlechtere Reputation.").rep(-0.05).upkeep(1.2));
        upgrades(BuildingType.GENLAB,
            new Upgrade("sequencer", "Gen-Sequenzierung", 1500, "[KETTE] Schaltet Designer-Shrimps frei.").grants("tier.GENTECH"));

        // ---------------- Restaurant & Empfang ----------------
        modes(BuildingType.RESTAURANT,
            new Mode("norm", "Standard-Karte", "Ausgewogen."),
            new Mode("star", "Sterne-Küche", "-20% Menge, ++Reputation, +40% Kosten.").cap(0.8).rep(0.05).upkeep(1.4));
        upgrades(BuildingType.RESTAURANT,
            new Upgrade("delivery", "Lieferdienst", 700, "+30% Verkaufsmenge.").cap(1.3));
        upgrades(BuildingType.VISITOR_CENTER,
            new Upgrade("campaign", "Marketing-Kampagne", 700, "[FARMWEIT] +0.1 Reputation/Tag.")
                .global(GlobalEffect.Type.REP_PER_TICK, 0.1));

        // ---------------- Export / Militär / Schwarzmarkt ----------------
        modes(BuildingType.EXPORT_DOCK,
            new Mode("norm", "Standard-Logistik", "Ausgewogen."),
            new Mode("bulk", "Massenverschiffung", "+40% Menge, +30% Kosten.").cap(1.4).upkeep(1.3));
        modes(BuildingType.MILITARY_DEPOT,
            new Mode("norm", "Vertragsgemäß", "General Krillkill nickt zufrieden."));
        modes(BuildingType.BLACK_MARKET,
            new Mode("low", "Diskret", "Wenig Aufsehen."),
            new Mode("greedy", "Alles muss raus", "+50% Menge, deutlich -Reputation.").cap(1.5).rep(-0.05));
    }

    public static List<Mode> modes(BuildingType t)       { return MODES.getOrDefault(t, List.of()); }
    public static List<Upgrade> upgrades(BuildingType t) { return UPGRADES.getOrDefault(t, List.of()); }
    public static boolean hasModes(BuildingType t)       { return !modes(t).isEmpty(); }
    public static boolean hasUpgrades(BuildingType t)    { return !upgrades(t).isEmpty(); }
}
