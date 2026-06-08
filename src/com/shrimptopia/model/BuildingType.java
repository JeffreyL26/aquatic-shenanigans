package com.shrimptopia.model;

import java.awt.Color;

/**
 * Alle baubaren Gebäude inkl. Wirtschaftswerte (pro Tick / "Tag").
 * Produktionskette:  Strom -> Wasser + Futter -> Shrimp-Becken -> Verkauf -> Geld
 *
 * Konvention: *Produce = erzeugt, *Use = verbraucht.
 */
public enum BuildingType {

    HEADQUARTERS(
        "Hauptquartier", 0,
        /*workerProvide*/ 8, /*workerNeed*/ 0,
        /*powerProduce*/ 0,  /*powerUse*/ 2,
        /*waterProduce*/ 0,  /*waterUse*/ 0,
        /*feedProduce*/ 0,   /*feedUse*/ 0,
        /*shrimpProduce*/ 0, /*shrimpSell*/ 0, /*sellPrice*/ 0,
        /*repProduce*/ 0,    /*upkeep*/ 0,
        IconKind.HQ, new Color(126, 137, 152),
        "Dein Büro, dein Reich. Stellt 8 motivierte (na ja) Verwaltungs-Arbeiter. "
        + "Lebt von Kaffee und Quartalszahlen."),

    POWER_PLANT(
        "Schalentier-Kraftwerk", 600,
        0, 1,
        45, 0,
        0, 0,
        0, 0,
        0, 0, 0,
        0, 8,
        IconKind.POWER, new Color(206, 92, 72),
        "Verbrennt Shrimp-Schalen und schlechte Laune zu Strom. +45 Strom. "
        + "Die Umwelt-Aktivisten haben deine Adresse bereits notiert (-Reputation)."),

    SOLAR_ROOF(
        "Solar-Dach", 850,
        0, 0,
        28, 0,
        0, 0,
        0, 0,
        0, 0, 0,
        0, 1,
        IconKind.SOLAR, new Color(96, 116, 168),
        "Sauberer Strom vom Dach. +28 Strom, fast keine Betriebskosten und ein "
        + "kleiner Reputations-Bonus. Funktioniert auch in der Halle. Frag nicht wie."),

    WATER_PLANT(
        "Wasserwerk", 400,
        0, 1,
        0, 8,
        14, 0,
        0, 0,
        0, 0, 0,
        0, 3,
        IconKind.WATER, new Color(46, 124, 204),
        "Filtert, salzt und temperiert H2O zu erstklassigem Shrimp-Wohlfühlwasser. "
        + "+14 Wasser. Braucht Strom, sonst nur teures Plätschern."),

    ALGAE_FARM(
        "Algen-Futterfarm", 450,
        0, 1,
        0, 6,
        0, 2,
        9, 0,
        0, 0, 0,
        0, 3,
        IconKind.ALGAE, new Color(84, 166, 74),
        "Züchtet nährstoffreiche Algen als Shrimp-Buffet. +9 Futter, "
        + "verbraucht etwas Wasser & Strom. Riecht wie ein Aquarium am Montag."),

    SHRIMP_TANK(
        "Shrimp-Becken", 500,
        0, 2,
        0, 10,
        0, 4,
        0, 3,
        5, 0, 0,
        0, 4,
        IconKind.TANK, new Color(0, 156, 166),
        "Das Herz der Farm. Verwandelt Wasser + Futter + Strom in +5 Shrimps/Tag. "
        + "Ohne Futter oder Wasser sterben die kleinen BWL-Absolventen leider ab."),

    HOUSING(
        "Arbeiter-Wohnheim", 300,
        6, 0,
        0, 4,
        0, 0,
        0, 0,
        0, 0, 0,
        0, 2,
        IconKind.HOUSE, new Color(154, 124, 92),
        "Gemütliche Kojen für +6 Arbeiter. Mit Meerblick-Tapete und "
        + "WLAN, das nur an guten Tagen funktioniert."),

    SALES_OFFICE(
        "Shrimp-Börse", 700,
        0, 2,
        0, 5,
        0, 0,
        0, 0,
        0, 7, 22,
        0, 5,
        IconKind.SALES, new Color(196, 164, 62),
        "Verkauft bis zu 7 Shrimps/Tag an den Weltmarkt. Der Preis steigt mit "
        + "deiner Reputation. 'Buy low, fry high.'"),

    LAB(
        "Forschungslabor", 1100,
        0, 2,
        0, 10,
        0, 0,
        0, 0,
        0, 0, 0,
        0.05, 7,
        IconKind.LAB, new Color(150, 92, 172),
        "Optimiert Genetik, Geschmack und Glanz deiner Shrimps. Jedes Labor hebt "
        + "den Verkaufspreis dauerhaft um +12% und poliert die Reputation."),

    RESTAURANT(
        "Shrimp-Restaurant", 950,
        0, 2,
        0, 6,
        0, 0,
        0, 0,
        0, 3, 0,
        0.15, 6,
        IconKind.FOOD, new Color(202, 110, 132),
        "Serviert deine Shrimps zum Premium-Preis direkt am Tisch und "
        + "macht die Stadt verrückt nach dir. Nimmt Standard-, Bio- und Gourmet-Shrimps."),

    // ===================== v2-Gebäude (per Fortschritt freigeschaltet) =====================

    WATER_HUB(
        "Wasseraufbereitungs-Hub", 1400,
        0, 2, 0, 20,
        40, 0, 0, 0,
        0, 0, 0, 0, 8,
        IconKind.WATERHUB, new Color(38, 108, 180),
        "Industrielle Wasseraufbereitung im Großformat. +40 Wasser. Ersetzt locker "
        + "drei Wasserwerke und macht halb so viel Krach."),

    GENLAB(
        "Genlabor", 1800,
        0, 3, 0, 18,
        0, 0, 0, 0,
        0, 0, 0, 0, 12,
        IconKind.GENLAB, new Color(150, 80, 190),
        "Hier wird an der Shrimp-DNA geschraubt. Schaltet Designer-Shrimps frei - "
        + "ethisch fragwürdig, finanziell brillant."),

    EXPORT_DOCK(
        "Export-Hafen", 1500,
        0, 3, 0, 8,
        0, 0, 0, 0,
        0, 10, 0, 0, 7,
        IconKind.DOCK, new Color(40, 150, 160),
        "Verschifft Shrimps in alle Welt (na ja, per LKW - wir haben kein Meer). "
        + "Nimmt Bio-, Gourmet- und Designer-Shrimps zu Export-Preisen."),

    MILITARY_DEPOT(
        "Militär-Depot", 1600,
        0, 2, 0, 10,
        0, 0, 0, 0,
        0, 8, 0, 0, 9,
        IconKind.MILITARY, new Color(120, 130, 80),
        "General Krillkills Abnahmestelle für Protein-Bomben und Kampf-Krill. "
        + "Zahlt fürstlich. Fragen stellt hier niemand."),

    BLACK_MARKET(
        "Schwarzmarkt", 1200,
        0, 1, 0, 4,
        0, 0, 0, 0,
        0, 6, 0, -0.05, 6,
        IconKind.BLACKMARKET, new Color(70, 64, 80),
        "Verkauft auch das, wonach niemand fragen sollte (Designer- & Kampf-Shrimps) - "
        + "zu Traumpreisen, aber deine Reputation bekommt Schrammen."),

    VISITOR_CENTER(
        "Besucherzentrum", 1000,
        0, 2, 0, 6,
        0, 0, 0, 0,
        0, 0, 0, 0.25, 5,
        IconKind.VISITOR, new Color(210, 150, 70),
        "Führungen, Shrimp-Streichelbecken, Souvenir-Shop. Tourismus pur: "
        + "hebt deine Reputation kräftig."),

    ZEN_GARDEN(
        "Zen-Algengarten", 500,
        0, 0, 0, 1,
        0, 0, 0, 0,
        0, 0, 0, 0.12, 1,
        IconKind.GARDEN, new Color(90, 160, 90),
        "Ein begrünter Ruheort mit plätschernden Algenbecken. Gut fürs Image, "
        + "gut für die Mitarbeiterseele.");

    public final String displayName;
    public final int    cost;
    public final int    workerProvide;
    public final int    workerNeed;
    public final int    powerProduce;
    public final int    powerUse;
    public final double waterProduce;
    public final double waterUse;
    public final double feedProduce;
    public final double feedUse;
    public final double shrimpProduce;
    public final double shrimpSell;   // pro Tick verkaufbare/verbrauchte Shrimp-Menge
    public final double sellPrice;    // Basispreis pro Shrimp (Börse)
    public final double repProduce;   // Reputation pro Tick
    public final double upkeep;       // Geld pro Tick
    public final IconKind icon;
    public final Color  color;
    public final String description;

    BuildingType(String displayName, int cost, int workerProvide, int workerNeed,
                 int powerProduce, int powerUse, double waterProduce, double waterUse,
                 double feedProduce, double feedUse, double shrimpProduce, double shrimpSell,
                 double sellPrice, double repProduce, double upkeep, IconKind icon,
                 Color color, String description) {
        this.displayName = displayName;
        this.cost = cost;
        this.workerProvide = workerProvide;
        this.workerNeed = workerNeed;
        this.powerProduce = powerProduce;
        this.powerUse = powerUse;
        this.waterProduce = waterProduce;
        this.waterUse = waterUse;
        this.feedProduce = feedProduce;
        this.feedUse = feedUse;
        this.shrimpProduce = shrimpProduce;
        this.shrimpSell = shrimpSell;
        this.sellPrice = sellPrice;
        this.repProduce = repProduce;
        this.upkeep = upkeep;
        this.icon = icon;
        this.color = color;
        this.description = description;
    }

    /** Kurzes Label für die Anzeige auf der Kachel. */
    public String shortName() {
        return switch (this) {
            case HEADQUARTERS -> "HQ";
            case POWER_PLANT  -> "Kraftwerk";
            case SOLAR_ROOF   -> "Solar";
            case WATER_PLANT  -> "Wasser";
            case ALGAE_FARM   -> "Algen";
            case SHRIMP_TANK  -> "Becken";
            case HOUSING      -> "Wohnheim";
            case SALES_OFFICE -> "Börse";
            case LAB          -> "Labor";
            case RESTAURANT   -> "Restaurant";
            case WATER_HUB      -> "Wasser-Hub";
            case GENLAB         -> "Genlabor";
            case EXPORT_DOCK    -> "Export";
            case MILITARY_DEPOT -> "Militär";
            case BLACK_MARKET   -> "Schwarzm.";
            case VISITOR_CENTER -> "Besucher";
            case ZEN_GARDEN     -> "Garten";
        };
    }

    /** Alle baubaren Gebäude (HQ ist vorplatziert). Das Menü filtert nach Zone & Freischaltung. */
    public static BuildingType[] buildable() {
        return new BuildingType[] {
            POWER_PLANT, SOLAR_ROOF, WATER_PLANT, WATER_HUB, ALGAE_FARM, SHRIMP_TANK,
            HOUSING, SALES_OFFICE, LAB, GENLAB, RESTAURANT,
            EXPORT_DOCK, MILITARY_DEPOT, BLACK_MARKET, VISITOR_CENTER, ZEN_GARDEN
        };
    }

    // ===================== v2-Metadaten (Zone, Tier, Markt, Freischaltung) =====================

    /** Zusatzdaten pro Gebäudetyp, ohne den bestehenden Konstruktor anzufassen. */
    public static final class Meta {
        public Zone zone = Zone.PRODUKTION;
        public boolean market = false;
        public ShrimpTier[] acceptedTiers = new ShrimpTier[0];
        public double priceMult = 1.0;
        public ShrimpTier producesTier = null;   // Becken: Standard-Tier
        public String unlockFlag = null;          // null = von Anfang an verfügbar
    }

    private static final java.util.EnumMap<BuildingType, Meta> META = new java.util.EnumMap<>(BuildingType.class);

    private static Meta m(Zone zone, String unlockFlag) {
        Meta meta = new Meta(); meta.zone = zone; meta.unlockFlag = unlockFlag; return meta;
    }
    private static Meta market(Zone zone, String unlockFlag, double priceMult, ShrimpTier... accepted) {
        Meta meta = m(zone, unlockFlag); meta.market = true; meta.priceMult = priceMult; meta.acceptedTiers = accepted; return meta;
    }

    static {
        META.put(HEADQUARTERS,  m(Zone.PRODUKTION, null));
        META.put(POWER_PLANT,   m(Zone.PRODUKTION, null));
        META.put(SOLAR_ROOF,    m(Zone.PRODUKTION, null));
        META.put(WATER_PLANT,   m(Zone.PRODUKTION, null));
        META.put(ALGAE_FARM,    m(Zone.PRODUKTION, null));
        META.put(HOUSING,       m(Zone.PRODUKTION, null));
        META.put(LAB,           m(Zone.FORSCHUNG,  "zone.FORSCHUNG"));

        Meta tank = m(Zone.PRODUKTION, null); tank.producesTier = ShrimpTier.STANDARD;
        META.put(SHRIMP_TANK, tank);

        META.put(SALES_OFFICE,   market(Zone.PRODUKTION, null,            1.0, ShrimpTier.STANDARD, ShrimpTier.BIO));
        META.put(RESTAURANT,     market(Zone.EMPFANG,    "zone.EMPFANG",  1.7, ShrimpTier.STANDARD, ShrimpTier.BIO, ShrimpTier.GOURMET));
        META.put(EXPORT_DOCK,    market(Zone.LOGISTIK,   "build.export",  1.3, ShrimpTier.BIO, ShrimpTier.GOURMET, ShrimpTier.GENTECH));
        META.put(MILITARY_DEPOT, market(Zone.LOGISTIK,   "build.military",1.5, ShrimpTier.PROTEIN, ShrimpTier.WARKRILL));
        META.put(BLACK_MARKET,   market(Zone.LOGISTIK,   "build.blackmarket", 1.8, ShrimpTier.GENTECH, ShrimpTier.WARKRILL));

        META.put(WATER_HUB,      m(Zone.PRODUKTION, "build.water_hub"));
        META.put(GENLAB,         m(Zone.FORSCHUNG,  "build.genlab"));
        META.put(VISITOR_CENTER, m(Zone.EMPFANG,    "zone.EMPFANG"));
        META.put(ZEN_GARDEN,     m(Zone.EMPFANG,    "zone.EMPFANG"));
    }

    public Meta meta() { return META.getOrDefault(this, DEFAULT_META); }
    private static final Meta DEFAULT_META = new Meta();

    public Zone zone()                 { return meta().zone; }
    public boolean isMarket()          { return meta().market; }
    public ShrimpTier[] acceptedTiers(){ return meta().acceptedTiers; }
    public double priceMult()          { return meta().priceMult; }
    public ShrimpTier producesTier()   { return meta().producesTier; }
    /** null = von Anfang an verfügbar; sonst das benötigte Freischalt-Flag. */
    public String unlockFlag()         { return meta().unlockFlag; }
}
