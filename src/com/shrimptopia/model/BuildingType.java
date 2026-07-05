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
        "Die Garage, in der alles begann - jetzt mit Schreibtisch. Stellt 8 motivierte (na ja) "
        + "Verwaltungs-Arbeiter. Lebt von Kaffee und Quartalszahlen."),

    // ===================== Garagen-Stufe (Start-Tier, ausbaubar) =====================

    GARAGE_TANK(
        "Garagen-Aquarium", 120,
        0, 0,
        0, 2,
        0, 1,
        0, 0.8,
        1.2, 0, 0,
        0, 0.8,
        IconKind.TANK, new Color(60, 130, 138),
        "Ein 200-Liter-Aquarium vom Flohmarkt, geflickt mit Aquarien-Silikon. +1,2 Shrimps/Tag. "
        + "Später an gleicher Stelle zum großen Shrimp-Becken upgradebar."),

    OLD_GENERATOR(
        "Rostiger Diesel-Generator", 150,
        0, 0,
        12, 0,
        0, 0,
        0, 0,
        0, 0, 0,
        0, 2.5,
        IconKind.POWER, new Color(150, 96, 70),
        "Springt beim dritten Tritt an und riecht nach Berufsschule. +12 Strom. "
        + "Die Nachbarn üben schon Beschwerdebriefe (-Reputation)."),

    RAIN_BARREL(
        "Regentonne & Gartenschlauch", 80,
        0, 0,
        0, 1,
        4, 0,
        0, 0,
        0, 0, 0,
        0, 0.5,
        IconKind.WATER, new Color(70, 120, 160),
        "Regenwasser, ein Kohlefilter und Vertrauen. +4 Wasser. Dr. Perla nennt es "
        + "'improvisierte Aquakultur-Infrastruktur' und weint dabei ein bisschen."),

    ALGAE_BUCKET(
        "Algen-Eimer", 100,
        0, 0,
        0, 1,
        0, 0.6,
        2.5, 0,
        0, 0, 0,
        0, 0.8,
        IconKind.ALGAE, new Color(96, 140, 76),
        "Baueimer + Fensterbank + Geduld = +2,5 Futter. Eine der Algen heißt inzwischen "
        + "Ingrid und gilt als Familienmitglied."),

    CAMPER(
        "Wohnwagen im Hof", 180,
        3, 0,
        0, 1,
        0, 0,
        0, 0,
        0, 0, 0,
        0, 1,
        IconKind.HOUSE, new Color(140, 118, 96),
        "Ein geerbter Wohnwagen mit Gasherd und Charakter. +3 Arbeiter, "
        + "die 'übergangsweise' hier wohnen. Seit zwei Jahren."),

    YARD_SALE(
        "Klapptisch-Verkauf", 100,
        0, 1,
        0, 1,
        0, 0,
        0, 0,
        0, 3, 22,
        0, 1,
        IconKind.SALES, new Color(170, 146, 70),
        "Klapptisch, Kühlbox, handgemaltes Schild: 'SHRIMPS (ECHT)'. Verkauft bis zu "
        + "3 Standard-Shrimps/Tag an Spaziergänger - mehr Nachfrage bringt nur Marketing."),

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
        + "gut für die Mitarbeiterseele."),

    // ===================== v3-Gebäude (zweite Wirtschaftsstufe) =====================

    SHELL_PRESS(
        "Schälerei & Schalen-Presse", 700,
        0, 2, 0, 3,
        0, 0, 0, 0,
        0, 0, 0, 0, 4,
        IconKind.SHELL, new Color(200, 180, 140),
        "Irgendeine Garnele - nennen wir sie Brian - war Suppe. Ihre Schale ist jetzt "
        + "Wirtschaftswachstum: Presst Becken-Abfall zu Industrie-Schalen für die zweite "
        + "Wirtschaftsstufe (+6 Schalen/Tag)."),

    SHRIMPBOOST_FACTORY(
        "SHRIMPBOOST-Fabrik", 1600,
        0, 3, 0, 6,
        0, 0, 0, 0,
        0, 0, 0, -0.01, 9,
        IconKind.CAN, new Color(40, 200, 200),
        "Löst Shrimps und Schalen zu Energydrink auf: 100% natürlich, 0% FDA-genehmigt. "
        + "Verbraucht 3 Shrimp + 4 Schalen, macht 2 SHRIMPBOOST/Tag. 'Mut in Dosen.'"),

    BOOST_STAND(
        "SHRIMPBOOST-Stand", 1300,
        0, 2, 0, 5,
        0, 0, 0, 0,
        0, 0, 0, 0.05, 7,
        IconKind.CAN, new Color(255, 90, 180),
        "Verkauft SHRIMPBOOST-Dosen zum Premium-Preis (~90 Geld/Dose). Influencer lieben "
        + "den Stand; sogar die Konkurrenz steht heimlich in der Schlange."),

    ROBOT_WORKS(
        "Garnelen-Roboter-Werk", 2400,
        0, 4, 0, 12,
        0, 0, 0, 0,
        0, 0, 0, 0, 14,
        IconKind.ROBOT, new Color(120, 140, 170),
        "Baut garnelenbetriebene Roboter - eine echte Garnele sitzt im Cockpit und denkt, "
        + "sie fährt Bagger. Braucht Schalen + SHRIMPBOOST. Jeder Roboter zählt als +2 Arbeiter."),

    KRILL_BARRACKS(
        "Krill-Kaserne", 2000,
        0, 3, 0, 8,
        0, 0, 0, 0,
        0, 0, 0, -0.02, 12,
        IconKind.SHIELD, new Color(120, 130, 80),
        "Drillt Kampf-Krill zu einer stehenden Armee. 'WER BRAUCHT EINE MARINE, WENN MAN "
        + "EINE HALLE HAT?' Verbraucht Kampf-Krill + SHRIMPBOOST, erzeugt Armee-Stärke."),

    // ===================== Premium-Veredelung: Darmentleerung & Abfall-Kreislauf =====================

    GUT_STATION(
        "Darmentleerungsanlage", 1300,
        0, 3, 0, 8,
        0, 12, 0, 0,
        0, 0, 0, 0, 8,
        IconKind.GUT, new Color(150, 176, 150),
        "Premium-Shrimps kommen nur mit tadellos leerem Darm auf den Teller: In klaren Hälterungs- "
        + "becken spülen die Garnelen 24 Stunden lang durch (Darm-Purge), bevor sie als Gourmet-Ware "
        + "gelten. Voraussetzung für die Gourmet-Zucht - erzeugt dabei aber Klärschlamm (+5 Abfall/Tag)."),

    BIOGAS_PLANT(
        "Biogas-Kläranlage", 1100,
        0, 2, 0, 3,
        0, 0, 0, 0,
        0, 0, 0, 0, 5,
        IconKind.BIOGAS, new Color(120, 150, 96),
        "Frisst den Klärschlamm der Darmentleerung und fault ihn zu Biogas - das du fürs Netz "
        + "vergütet bekommst. Entsorgt bis zu 6 Abfall/Tag. Ohne Entsorgung stinkt der Schlamm zum "
        + "Himmel und ruiniert deinen Ruf."),

    // ===================== Lager (Ressourcen-Kapazität) =====================

    STORAGE_SHED(
        "Lager-Schuppen", 140,
        0, 0, 0, 1,
        0, 0, 0, 0,
        0, 0, 0, 0, 0.5,
        IconKind.CRATE, new Color(140, 118, 86),
        "Regale, Fässer, ein Vorhängeschloss. Erhöht die Lagerkapazität für Wasser, Futter, "
        + "Shrimps & Co. - was nicht ins Lager passt, verfällt. Upgradebar zum Hochregal-Lager."),

    WAREHOUSE(
        "Hochregal-Lager", 800,
        0, 2, 0, 4,
        0, 0, 0, 0,
        0, 0, 0, 0, 2.5,
        IconKind.CRATE, new Color(158, 132, 94),
        "Palettenweise Platz: großzügige Lagerkapazität für alle Ressourcen. Ein Gabelstapler "
        + "namens Günther inklusive. Günther hat einen eigenen Parkplatz.");

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
            case GARAGE_TANK   -> "Aquarium";
            case OLD_GENERATOR -> "Generator";
            case RAIN_BARREL   -> "Regentonne";
            case ALGAE_BUCKET  -> "Algen-Eimer";
            case CAMPER        -> "Wohnwagen";
            case YARD_SALE     -> "Klapptisch";
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
            case SHELL_PRESS         -> "Schälerei";
            case SHRIMPBOOST_FACTORY -> "Boost-Fabrik";
            case BOOST_STAND         -> "Boost-Stand";
            case ROBOT_WORKS         -> "Roboter-Werk";
            case KRILL_BARRACKS      -> "Kaserne";
            case GUT_STATION         -> "Darm-Purge";
            case BIOGAS_PLANT        -> "Biogas";
            case STORAGE_SHED        -> "Lager";
            case WAREHOUSE           -> "Hochregal";
        };
    }

    /** Alle baubaren Gebäude (HQ ist vorplatziert). Das Menü filtert nach Zone & Freischaltung. */
    public static BuildingType[] buildable() {
        return new BuildingType[] {
            OLD_GENERATOR, RAIN_BARREL, ALGAE_BUCKET, GARAGE_TANK, CAMPER, YARD_SALE, STORAGE_SHED,
            POWER_PLANT, SOLAR_ROOF, WATER_PLANT, WATER_HUB, ALGAE_FARM, SHRIMP_TANK,
            HOUSING, WAREHOUSE, SALES_OFFICE, LAB, GENLAB, RESTAURANT,
            EXPORT_DOCK, MILITARY_DEPOT, BLACK_MARKET, VISITOR_CENTER, ZEN_GARDEN,
            SHELL_PRESS, SHRIMPBOOST_FACTORY, BOOST_STAND, ROBOT_WORKS, KRILL_BARRACKS,
            GUT_STATION, BIOGAS_PLANT
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
        public BuildingType upgradesTo = null;    // Tropico-Stil: Ausbau zur nächsten Stufe am selben Platz
    }

    private static final java.util.EnumMap<BuildingType, Meta> META = new java.util.EnumMap<>(BuildingType.class);

    private static Meta m(Zone zone, String unlockFlag) {
        Meta meta = new Meta(); meta.zone = zone; meta.unlockFlag = unlockFlag; return meta;
    }
    private static Meta market(Zone zone, String unlockFlag, double priceMult, ShrimpTier... accepted) {
        Meta meta = m(zone, unlockFlag); meta.market = true; meta.priceMult = priceMult; meta.acceptedTiers = accepted; return meta;
    }

    private static Meta up(Meta meta, BuildingType next) { meta.upgradesTo = next; return meta; }

    static {
        META.put(HEADQUARTERS,  m(Zone.PRODUKTION, null));

        // Garagen-Stufe: sofort baubar, ausbaubar zur Hallen-Stufe (era.HALLE)
        Meta gTank = up(m(Zone.PRODUKTION, null), SHRIMP_TANK); gTank.producesTier = ShrimpTier.STANDARD;
        META.put(GARAGE_TANK, gTank);
        META.put(OLD_GENERATOR, up(m(Zone.PRODUKTION, null), POWER_PLANT));
        META.put(RAIN_BARREL,   up(m(Zone.PRODUKTION, null), WATER_PLANT));
        META.put(ALGAE_BUCKET,  up(m(Zone.PRODUKTION, null), ALGAE_FARM));
        META.put(CAMPER,        up(m(Zone.PRODUKTION, null), HOUSING));
        META.put(YARD_SALE,     up(market(Zone.PRODUKTION, null, 0.85, ShrimpTier.STANDARD), SALES_OFFICE));

        // Hallen-Stufe: erst nach dem Garagen-Ausbau (Quest "era.HALLE")
        META.put(POWER_PLANT,   m(Zone.PRODUKTION, "era.HALLE"));
        META.put(SOLAR_ROOF,    m(Zone.PRODUKTION, "era.HALLE"));
        META.put(WATER_PLANT,   m(Zone.PRODUKTION, "era.HALLE"));
        META.put(ALGAE_FARM,    m(Zone.PRODUKTION, "era.HALLE"));
        // Wohnheim erst, wenn der Betrieb wirklich Personal braucht (Meilenstein-Flag) -
        // direkt zur Hallen-Miete ein ganzes Wohnheim wäre unlogisch schnell skaliert.
        META.put(HOUSING,       m(Zone.PRODUKTION, "build.housing"));
        META.put(LAB,           m(Zone.FORSCHUNG,  "zone.FORSCHUNG"));

        Meta tank = m(Zone.PRODUKTION, "era.HALLE"); tank.producesTier = ShrimpTier.STANDARD;
        META.put(SHRIMP_TANK, tank);

        META.put(SALES_OFFICE,   market(Zone.PRODUKTION, "era.HALLE",     1.0, ShrimpTier.STANDARD, ShrimpTier.BIO));
        META.put(RESTAURANT,     market(Zone.EMPFANG,    "zone.EMPFANG",  1.7, ShrimpTier.STANDARD, ShrimpTier.BIO, ShrimpTier.GOURMET));
        META.put(EXPORT_DOCK,    market(Zone.LOGISTIK,   "build.export",  1.3, ShrimpTier.BIO, ShrimpTier.GOURMET, ShrimpTier.GENTECH));
        META.put(MILITARY_DEPOT, market(Zone.LOGISTIK,   "build.military",1.5, ShrimpTier.PROTEIN, ShrimpTier.WARKRILL));
        META.put(BLACK_MARKET,   market(Zone.LOGISTIK,   "build.blackmarket", 1.8, ShrimpTier.GENTECH, ShrimpTier.WARKRILL));

        META.put(WATER_HUB,      m(Zone.PRODUKTION, "build.water_hub"));
        META.put(GENLAB,         m(Zone.FORSCHUNG,  "build.genlab"));
        META.put(VISITOR_CENTER, m(Zone.EMPFANG,    "zone.EMPFANG"));
        META.put(ZEN_GARDEN,     m(Zone.EMPFANG,    "zone.EMPFANG"));

        META.put(SHELL_PRESS,         m(Zone.PRODUKTION, "era.HALLE"));
        // Premium-Veredelung: Darmentleerung schaltet die Gourmet-Zucht frei, Biogas entsorgt den Schlamm
        META.put(GUT_STATION,         m(Zone.FORSCHUNG,  "build.gut_station"));
        META.put(BIOGAS_PLANT,        m(Zone.FORSCHUNG,  "build.waste_plant"));
        META.put(SHRIMPBOOST_FACTORY, m(Zone.FORSCHUNG,  "build.shrimpboost"));
        META.put(BOOST_STAND,         m(Zone.EMPFANG,    "build.shrimpboost"));
        META.put(ROBOT_WORKS,         m(Zone.LOGISTIK,   "build.robotworks"));
        META.put(KRILL_BARRACKS,      m(Zone.LOGISTIK,   "build.barracks"));

        // Lager: Schuppen ab Start, upgradebar zum Hochregal-Lager (Hallen-Stufe)
        META.put(STORAGE_SHED, up(m(Zone.PRODUKTION, null), WAREHOUSE));
        META.put(WAREHOUSE,    m(Zone.PRODUKTION, "era.HALLE"));
    }

    // ===================== Lagerkapazität =====================

    /** Kapazität je Gebäude: {Wasser, Futter, Shrimps, Schalen, Boost, Abfall}. null = kein Lager. */
    private static final java.util.EnumMap<BuildingType, double[]> STORAGE = new java.util.EnumMap<>(BuildingType.class);
    static {
        STORAGE.put(HEADQUARTERS, new double[]{ 350, 250, 120, 150,  80,  60 });
        STORAGE.put(STORAGE_SHED, new double[]{ 250, 180,  60, 120,  50,  40 });
        STORAGE.put(WAREHOUSE,    new double[]{ 900, 650, 250, 450, 250, 160 });
    }
    public double[] storage() { return STORAGE.get(this); }

    public Meta meta() { return META.getOrDefault(this, DEFAULT_META); }
    private static final Meta DEFAULT_META = new Meta();

    public Zone zone()                 { return meta().zone; }
    public boolean isMarket()          { return meta().market; }
    public ShrimpTier[] acceptedTiers(){ return meta().acceptedTiers; }
    public double priceMult()          { return meta().priceMult; }
    public ShrimpTier producesTier()   { return meta().producesTier; }
    /** null = von Anfang an verfügbar; sonst das benötigte Freischalt-Flag. */
    public String unlockFlag()         { return meta().unlockFlag; }
    /** Nächste Ausbaustufe (Tropico-Stil) oder null. */
    public BuildingType upgradesTo()   { return meta().upgradesTo; }
    /** Produziert dieses Gebäude Shrimps (Becken jeder Stufe)? */
    public boolean isTank()            { return this == SHRIMP_TANK || this == GARAGE_TANK; }
    /** Erzeugt dieses Gebäude Futter aus Wasser (Algen jeder Stufe)? */
    public boolean isAlgae()           { return this == ALGAE_FARM || this == ALGAE_BUCKET; }

    // ===================== v3-Flows (Schalen/SHRIMPBOOST/Roboter/Armee) =====================
    private static final java.util.EnumMap<BuildingType, Flows> FLOWS = new java.util.EnumMap<>(BuildingType.class);
    static {
        FLOWS.put(SHELL_PRESS,         new Flows().shellsOut(6));
        FLOWS.put(SHRIMPBOOST_FACTORY, new Flows().shrimp(3).shellsIn(4).boostOut(2));
        FLOWS.put(BOOST_STAND,         new Flows().boostIn(5));
        FLOWS.put(ROBOT_WORKS,         new Flows().shellsIn(5).boostIn(1).robotsOut(0.25));
        FLOWS.put(KRILL_BARRACKS,      new Flows().boostIn(1).army(4));
        FLOWS.put(GUT_STATION,         new Flows().wasteOut(5));
        FLOWS.put(BIOGAS_PLANT,        new Flows().wasteIn(6));
    }
    private static final Flows NO_FLOWS = new Flows();
    public Flows flows() { return FLOWS.getOrDefault(this, NO_FLOWS); }
}
