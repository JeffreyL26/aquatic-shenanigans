package com.shrimptopia.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Registry der Betriebs-Modi und Upgrades pro Gebäudetyp.
 * Becken-Modi legen das produzierte Shrimp-Tier fest (teils freischaltungsabhängig).
 * Seit v7 hat JEDES baubare Gebäude mindestens einen Trade-off-Modus und ein Upgrade;
 * prodMult skaliert bei Flow-Gebäuden (Schälerei, Boost-Fabrik ...) auch die v3-Flows.
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
            new Upgrade("oxygen",  "Sauerstoff-Anreicherung", 500, "+20% Shrimp-Output dieses Beckens.").shrimp(1.2),
            new Upgrade("flow",    "Strömungs-Optimierer", 650, "+15% Output, +10% Strom: die Garnelen schwimmen im Kreis und nennen es Fitness.")
                .shrimp(1.15).power(1.1));
        // Mega-Becken erbt Tier-Modi & Upgrades des Shrimp-Beckens (gleiche Zuchtlogik, mehr Volumen)
        MODES.put(BuildingType.MEGA_TANK, MODES.get(BuildingType.SHRIMP_TANK));
        UPGRADES.put(BuildingType.MEGA_TANK, UPGRADES.get(BuildingType.SHRIMP_TANK));

        // ---------------- Garagen-Stufe ----------------
        modes(BuildingType.GARAGE_TANK,
            new Mode("std",  "Ruhige Hand", "Behutsame Pflege, stabile kleine Ernte."),
            new Mode("dense", "Dichte Hälterung", "+25% Output, +30% Futter, die Garnelen beschweren sich schriftlich.")
                .shrimp(1.25).feed(1.3).rep(-0.01));
        upgrades(BuildingType.GARAGE_TANK,
            new Upgrade("led",  "LED-Leiste", 120, "+10% Output: Licht an, Laune gut.").shrimp(1.1),
            new Upgrade("pump", "Flohmarkt-Luftpumpe", 150, "+15% Output, +50% Strom dieses Aquariums.").shrimp(1.15).power(1.5));

        modes(BuildingType.OLD_GENERATOR,
            new Mode("norm", "Normalbetrieb", "Läuft. Meistens."),
            new Mode("idle", "Standgas", "-25% Strom, -40% Kosten: er darf auch mal durchatmen.").prod(0.75).upkeep(0.6),
            new Mode("full", "Vollgas", "+30% Strom, +50% Kosten, die Nachbarn hören ihn bis ins Wohnzimmer.")
                .prod(1.3).upkeep(1.5).rep(-0.02));
        upgrades(BuildingType.OLD_GENERATOR,
            new Upgrade("muffler", "Neuer Auspuff", 180, "Weniger Gestank & Lärm: +Reputation/Tag.").rep(0.03).deco(1),
            new Upgrade("tuning",  "Berufsschul-Feintuning", 220, "+15% Strom - Dmitri hat dran gedreht.").prod(1.15));

        modes(BuildingType.RAIN_BARREL,
            new Mode("norm", "Ein Fass", "Regen rein, Wasser raus."),
            new Mode("double", "Doppelte Tonne", "+30% Wasser, +40% Kosten (zweites Fass, zweiter Schlauch).")
                .prod(1.3).upkeep(1.4));
        upgrades(BuildingType.RAIN_BARREL,
            new Upgrade("finefilter", "Feinfilter-Strumpf", 100, "+20% Wasser: filtert Laub, Mücken und eine verlorene Wette.").prod(1.2));

        modes(BuildingType.ALGAE_BUCKET,
            new Mode("sill",  "Fensterbank", "Sonne ist gratis."),
            new Mode("lamp",  "Wachstumslampe", "+35% Futter, +50% Strom - Ingrid blüht auf.").prod(1.35).power(1.5));
        upgrades(BuildingType.ALGAE_BUCKET,
            new Upgrade("fertilizer", "Ingrids Spezialdünger", 120, "+25% Futter. Rezept streng geheim.").prod(1.25));

        modes(BuildingType.CAMPER,
            new Mode("wg",   "WG-Modus", "Drei Leute, ein Gasherd, null Privatsphäre."),
            new Mode("bunk", "Ausbau-Betten", "+1 Arbeiter, +40% Kosten: das 'Gästezimmer' ist jetzt offiziell.")
                .workers(1.34).upkeep(1.4));
        upgrades(BuildingType.CAMPER,
            new Upgrade("awning", "Markise & Campingstühle", 150, "+Reputation, +1 Ambiente: fast schon Urlaub.").rep(0.02).deco(1));

        modes(BuildingType.YARD_SALE,
            new Mode("fair",  "Ehrlicher Klapptisch", "Handgemalte Preise, ehrliche Ware."),
            new Mode("nepp",  "Touristen-Nepp", "+20% Preis, -20% Menge, minimal schlechterer Ruf.")
                .price(1.2).cap(0.8).rep(-0.01));
        upgrades(BuildingType.YARD_SALE,
            new Upgrade("coolbox", "Kühlbox XXL", 140, "+50% Verkaufsmenge: mehr Platz zwischen den Eiswürfeln.").cap(1.5));

        // ---------------- Verkaufsleiter (Hof/Halle) ----------------
        modes(BuildingType.MARKET_STALL,
            new Mode("norm",   "Standard-Stand", "Markise auf, Lächeln an."),
            new Mode("crier",  "Marktschreier", "+30% Menge, der Käsehändler grüßt nicht mehr.").cap(1.3).rep(-0.01),
            new Mode("deli",   "Feinkost-Auslage", "+15% Preis, -20% Menge: Qualität statt Quantität.").price(1.15).cap(0.8));
        upgrades(BuildingType.MARKET_STALL,
            new Upgrade("icebath", "Eiswannen-Vitrine", 250, "+25% Verkaufsmenge.").cap(1.25),
            new Upgrade("biobanner", "Bio-Banner", 300, "+10% Preis - 'ECHT BIO (ECHT)'. Braucht Bio-Freischaltung.")
                .price(1.1).needs("tier.BIO"));

        modes(BuildingType.FARM_SHOP,
            new Mode("norm",  "Bedienung mit Herz", "Klingel, Schwatz, Stammkundschaft."),
            new Mode("self",  "Selbstbedienung", "-30% Kosten, -10% Menge, die Klingel verstaubt.").upkeep(0.7).cap(0.9),
            new Mode("event", "Erlebnis-Einkauf", "+20% Preis, +30% Kosten, +2 Ambiente: Hofführung inklusive.")
                .price(1.2).upkeep(1.3).deco(2));
        upgrades(BuildingType.FARM_SHOP,
            new Upgrade("loyalty", "Kundenkarte", 400, "+20% Verkaufsmenge: der zehnte Shrimp ist gratis.").cap(1.2),
            new Upgrade("regional", "Regional-Regal", 350, "+Reputation: Honig vom Nachbarn, Eier von nebenan.").rep(0.03));

        modes(BuildingType.DELIVERY_SERVICE,
            new Mode("norm",   "Standard-Touren", "Zwei Transporter, ein Plan."),
            new Mode("express", "Express-Flotte", "+30% Menge, +40% Kosten: Mira fährt selbst.").cap(1.3).upkeep(1.4),
            new Mode("bundle",  "Sammel-Touren", "-30% Kosten, -15% Menge, dafür entspannte Fahrer.").upkeep(0.7).cap(0.85));
        upgrades(BuildingType.DELIVERY_SERVICE,
            new Upgrade("router", "Routenplaner-Software", 600, "+20% Verkaufsmenge.").cap(1.2),
            new Upgrade("etrucks", "E-Transporter", 700, "-20% Kosten, +Reputation: leise und sauber.").upkeep(0.8).rep(0.03));

        // ---------------- Kraftwerk ----------------
        modes(BuildingType.POWER_PLANT,
            new Mode("norm", "Normalbetrieb", "Ausgewogen."),
            new Mode("over", "Overdrive", "+40% Strom, +50% Kosten, mehr Gestank (-Reputation).").prod(1.4).upkeep(1.5).rep(-0.04),
            new Mode("eco",  "Eco-Drossel", "-30% Strom, -40% Kosten, etwas saubererer Ruf.").prod(0.7).upkeep(0.6).rep(0.02));
        upgrades(BuildingType.POWER_PLANT,
            new Upgrade("filter", "Abgasfilter", 600, "Saubererer Betrieb: +Reputation/Tag, +2 Ambiente.").rep(0.06).deco(2),
            new Upgrade("turbine", "Turbinen-Tuning", 700, "+20% Stromproduktion.").prod(1.2));

        // ---------------- Solar / Wind / Laufrad / Geothermie ----------------
        modes(BuildingType.SOLAR_ROOF,
            new Mode("fix",  "Feststehend", "Wartungsarm."),
            new Mode("track", "Sonnen-Nachführung", "+25% Strom, +40% Kosten.").prod(1.25).upkeep(1.4));
        upgrades(BuildingType.SOLAR_ROOF,
            new Upgrade("battery", "Pufferbatterie", 500, "+15% Strom.").prod(1.15),
            new Upgrade("polish",  "Panel-Politur", 300, "+10% Strom: Dmitri und ein Fensterwischer.").prod(1.1));

        modes(BuildingType.WIND_TURBINE,
            new Mode("norm",  "Normalbetrieb", "Dreht, wenn's weht."),
            new Mode("storm", "Sturm-Auslegung", "+25% Strom, +50% Kosten - die Möwen steigen aus.").prod(1.25).upkeep(1.5));
        upgrades(BuildingType.WIND_TURBINE,
            new Upgrade("rotor", "Rotor-Wartung", 350, "+15% Strom, das Wedeln wird würdevoller.").prod(1.15));

        modes(BuildingType.SHRIMP_WHEEL,
            new Mode("norm",  "Freiwilligen-Schicht", "Nur wer will, läuft."),
            new Mode("double", "Doppelschicht-Läufer", "+30% Strom, +50% Futterlohn, leichte Image-Fragen.")
                .prod(1.3).upkeep(1.5).rep(-0.01));
        upgrades(BuildingType.SHRIMP_WHEEL,
            new Upgrade("energybar", "Energieriegel-Sponsoring", 150, "+20% Strom: die Läufer sind jetzt 'Athleten'.").prod(1.2));

        modes(BuildingType.GEO_PLANT,
            new Mode("norm", "Grundlast", "Wärme satt, rund um die Uhr."),
            new Mode("deep", "Tiefen-Boost", "+25% Strom, +40% Kosten. Bitte nichts anbohren.").prod(1.25).upkeep(1.4));
        upgrades(BuildingType.GEO_PLANT,
            new Upgrade("exchanger", "Wärmetauscher v2", 1200, "+15% Strom.").prod(1.15));

        // ---------------- Wasserwerk / Hub ----------------
        modes(BuildingType.WATER_PLANT,
            new Mode("norm", "Normaldruck", "Ausgewogen."),
            new Mode("high", "Hochdruck", "+30% Wasser, +40% Strom.").prod(1.3).power(1.4).upkeep(1.2));
        upgrades(BuildingType.WATER_PLANT,
            new Upgrade("recycle", "Recycling-Kreislauf", 800, "[FARMWEIT] +15% Wasserproduktion aller Werke.")
                .global(GlobalEffect.Type.WATER_PRODUCE_MULT, 1.15),
            new Upgrade("membrane", "Membranfilter", 450, "+20% Wasser dieses Werks.").prod(1.2));
        modes(BuildingType.WATER_HUB,
            new Mode("norm", "Normalbetrieb", "Ausgewogen."),
            new Mode("turbo", "Turbo-Filter", "+35% Wasser, +50% Strom.").prod(1.35).power(1.5).upkeep(1.3));
        upgrades(BuildingType.WATER_HUB,
            new Upgrade("smart", "Smart-Steuerung", 900, "[FARMWEIT] -10% Stromverbrauch der ganzen Farm.")
                .global(GlobalEffect.Type.POWERUSE_MULT, 0.9),
            new Upgrade("nighttariff", "Nachtstrom-Tarif", 700, "-20% Stromverbrauch dieses Hubs.").power(0.8));

        // ---------------- Algen / Plankton ----------------
        modes(BuildingType.ALGAE_FARM,
            new Mode("norm", "Standard-Algen", "Ausgewogen."),
            new Mode("turbo", "Turbo-Algen", "+40% Futter, +30% Strom & Wasser.").prod(1.4).power(1.3).water(1.3),
            new Mode("eco", "Spar-Algen", "-25% Futter, -40% Kosten.").prod(0.75).upkeep(0.6));
        upgrades(BuildingType.ALGAE_FARM,
            new Upgrade("nutrient", "Nährstoff-Boost", 600, "+25% Futterproduktion dieser Farm.").prod(1.25),
            new Upgrade("cleanfeed", "Bio-Futter-Zertifikat", 900, "[KETTE] Schaltet Bio-Shrimps frei.").grants("tier.BIO"));

        modes(BuildingType.PLANKTON_PRESS,
            new Mode("norm", "Standard-Pressung", "Plankton rein, Pellets raus."),
            new Mode("high", "Hochdruck-Pressung", "+35% Futter, +40% Strom, +30% Kosten.")
                .prod(1.35).power(1.4).upkeep(1.3));
        upgrades(BuildingType.PLANKTON_PRESS,
            new Upgrade("umami", "Umami-Rezeptur", 600, "+20% Futter: die Garnelen fragen nach Nachschlag.").prod(1.2));

        // ---------------- Zucht-Spezialisten ----------------
        modes(BuildingType.KIDDIE_POOL,
            new Mode("norm",   "Ein Ring, ein Traum", "Aufblasbar, aber ambitioniert."),
            new Mode("stacked", "Doppelt gestapelt", "+30% Output, +30% Futter, TÜV guckt weg.")
                .shrimp(1.3).feed(1.3).rep(-0.01));
        upgrades(BuildingType.KIDDIE_POOL,
            new Upgrade("heater", "Pool-Heizung", 180, "+20% Output, +50% Strom: Wellness fürs Planschbecken.")
                .shrimp(1.2).power(1.5));

        modes(BuildingType.HATCHERY,
            new Mode("gentle", "Behutsame Aufzucht", "Warm, ruhig, viel Gesang."),
            new Mode("turbo",  "Turbo-Brut", "+30% Output, +40% Futter, die Larven werden Workaholics.")
                .shrimp(1.3).feed(1.4).rep(-0.01));
        upgrades(BuildingType.HATCHERY,
            new Upgrade("lullaby", "Dr. Perlas Abendlied (Aufnahme)", 400, "+15% Output, +Reputation. Es hilft. Wir wissen nicht, warum.")
                .shrimp(1.15).rep(0.01));

        modes(BuildingType.REEF_DOME,
            new Mode("ripe", "Gourmet-Reifung", "Spitzenqualität in Ruhe reifen lassen."),
            new Mode("show", "Schau-Riff", "-20% Output, +Reputation, +3 Ambiente: Besucher kleben am Glas.")
                .shrimp(0.8).rep(0.04).deco(3));
        upgrades(BuildingType.REEF_DOME,
            new Upgrade("coral", "Lebende Korallen-Zucht", 900, "+10% Output, +2 Ambiente.").shrimp(1.1).deco(2));

        // ---------------- Wohnheim / Personal ----------------
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

        modes(BuildingType.CANTEEN,
            new Mode("norm",    "Hausmannskost", "Warm, viel, verlässlich."),
            new Mode("gourmet", "Gourmet-Kantine", "+1 Arbeiter, +50% Kosten: Leute kündigen woanders, um HIER zu essen.")
                .workers(1.25).upkeep(1.5).rep(0.01));
        upgrades(BuildingType.CANTEEN,
            new Upgrade("casserole", "Algen-Auflauf-Patent", 350, "+1 Arbeiter: Ingrids Auflauf zieht Personal an.").workers(1.25));

        // ---------------- Börse & Märkte ----------------
        modes(BuildingType.SALES_OFFICE,
            new Mode("norm", "Normalbetrieb", "Ausgewogen."),
            new Mode("aggro", "Aggressives Marketing", "+40% Verkaufsmenge, +30% Kosten, -Reputation.").cap(1.4).upkeep(1.3).rep(-0.02),
            new Mode("boutique", "Premium-Boutique", "-30% Menge, dafür +Reputation.").cap(0.7).rep(0.03));
        upgrades(BuildingType.SALES_OFFICE,
            new Upgrade("onlineshop", "Online-Shop", 700, "[FARMWEIT] +15% Verkaufskapazität aller Märkte.")
                .global(GlobalEffect.Type.SELLCAP_MULT, 1.15),
            new Upgrade("terminal", "Börsen-Terminal", 900, "+10% Verkaufspreis dieser Börse.").price(1.1));

        // ---------------- Labor / Genlabor ----------------
        modes(BuildingType.LAB,
            new Mode("basic", "Grundlagenforschung", "Solide, etwas Reputation.").rep(0.02),
            new Mode("market", "Marktforschung", "Treibt Preise (siehe Upgrade), kostet mehr.").upkeep(1.3));
        upgrades(BuildingType.LAB,
            new Upgrade("biocert", "Bio-Zertifizierung", 800, "[KETTE] Schaltet Bio-Shrimps frei.").grants("tier.BIO"),
            new Upgrade("gourmetcert", "Gourmet-Protokoll", 1200, "[KETTE] Schaltet die Darmentleerungsanlage frei (Voraussetzung für Gourmet-Shrimps).").grants("build.gut_station").needs("tier.BIO"),
            new Upgrade("patents", "Patentportfolio", 1400, "[FARMWEIT] +10% Verkaufspreis.")
                .global(GlobalEffect.Type.PRICE_MULT, 1.1));
        modes(BuildingType.GENLAB,
            new Mode("safe", "Vorschriftsmäßig", "Ethik-Kommission zufrieden."),
            new Mode("yolo", "Move fast, break shrimp", "Schnellere Resultate, schlechtere Reputation.").rep(-0.05).upkeep(1.2));
        upgrades(BuildingType.GENLAB,
            new Upgrade("sequencer", "Gen-Sequenzierung", 1500, "[KETTE] Schaltet Designer-Shrimps frei.").grants("tier.GENTECH"),
            new Upgrade("ethics", "Ethik-Gutachten (gekauft)", 900, "+Reputation: ein Stempel, der beruhigt.").rep(0.05));

        // ---------------- Restaurant & Empfang ----------------
        modes(BuildingType.RESTAURANT,
            new Mode("norm", "Standard-Karte", "Ausgewogen."),
            new Mode("star", "Sterne-Küche", "-20% Menge, ++Reputation, +40% Kosten.").cap(0.8).rep(0.05).upkeep(1.4),
            new Mode("buffet", "Mittags-Buffet", "+30% Menge, -15% Preis: satt macht's trotzdem.").cap(1.3).price(0.85));
        upgrades(BuildingType.RESTAURANT,
            new Upgrade("delivery", "Lieferdienst", 700, "+30% Verkaufsmenge.").cap(1.3),
            new Upgrade("wine", "Weinkarte & Sommelier", 500, "+15% Preis: zum Shrimp empfiehlt er einen Riesling.").price(1.15));

        modes(BuildingType.VISITOR_CENTER,
            new Mode("norm", "Standard-Führung", "Helm auf, Staunen an."),
            new Mode("vip",  "VIP-Touren", "++Reputation, +40% Kosten, +2 Ambiente: Häppchen inklusive.")
                .rep(0.04).upkeep(1.4).deco(2));
        upgrades(BuildingType.VISITOR_CENTER,
            new Upgrade("campaign", "Marketing-Kampagne", 700, "[FARMWEIT] +0.1 Reputation/Tag.")
                .global(GlobalEffect.Type.REP_PER_TICK, 0.1),
            new Upgrade("souvenir", "Souvenir-Shop", 600, "-40% Kosten des Zentrums: Plüsch-Greg verkauft sich von selbst.")
                .upkeep(0.6).deco(1));

        // ---------------- Export / Militär / Schwarzmarkt ----------------
        modes(BuildingType.EXPORT_DOCK,
            new Mode("norm", "Standard-Logistik", "Ausgewogen."),
            new Mode("bulk", "Massenverschiffung", "+40% Menge, +30% Kosten.").cap(1.4).upkeep(1.3));
        upgrades(BuildingType.EXPORT_DOCK,
            new Upgrade("customs", "Zoll-Kontakte", 800, "+10% Exportpreis: die Formulare stempeln sich fast von allein.").price(1.1));
        modes(BuildingType.MILITARY_DEPOT,
            new Mode("norm", "Vertragsgemäß", "General Krillkill nickt zufrieden."),
            new Mode("over", "Übererfüllung", "+30% Menge, -Reputation: Fragen stellt hier wirklich niemand.")
                .cap(1.3).rep(-0.02));
        upgrades(BuildingType.MILITARY_DEPOT,
            new Upgrade("clearance", "Geheimhaltungsstufe KRILL", 900, "+15% Preis: geheime Ware ist teure Ware.").price(1.15));
        modes(BuildingType.BLACK_MARKET,
            new Mode("low", "Diskret", "Wenig Aufsehen."),
            new Mode("greedy", "Alles muss raus", "+50% Menge, deutlich -Reputation.").cap(1.5).rep(-0.05));
        upgrades(BuildingType.BLACK_MARKET,
            new Upgrade("hush", "Schweigegeld-Fonds", 1000, "Dämpft den Ruf-Schaden: +Reputation/Tag.").rep(0.04));

        // ---------------- v3: Verarbeitung (Flows skalieren mit prodMult) ----------------
        modes(BuildingType.SHELL_PRESS,
            new Mode("norm",  "Normalbetrieb", "Brian wäre stolz."),
            new Mode("shift", "Doppelschicht", "+40% Schalen, +50% Strom & Kosten.").prod(1.4).power(1.5).upkeep(1.4));
        upgrades(BuildingType.SHELL_PRESS,
            new Upgrade("diamond", "Diamant-Mahlwerk", 500, "+25% Schalen-Ausstoß.").prod(1.25));

        modes(BuildingType.SHRIMPBOOST_FACTORY,
            new Mode("norm", "Rezeptur X", "Das Original. 0% FDA."),
            new Mode("caffeine", "Extra-Koffein", "+30% Ausstoß (und Verbrauch), +40% Kosten, nervöses Personal.")
                .prod(1.3).upkeep(1.4).rep(-0.01),
            new Mode("zero", "SHRIMPBOOST Zero", "-15% Ausstoß, +Reputation: schmeckt fast wie das Original.")
                .prod(0.85).rep(0.02));
        upgrades(BuildingType.SHRIMPBOOST_FACTORY,
            new Upgrade("bottling", "Abfüllstraße", 800, "+20% Durchsatz (Verbrauch steigt mit).").prod(1.2));

        modes(BuildingType.BOOST_STAND,
            new Mode("norm",  "Normalverkauf", "Eine Dose, ein Lächeln."),
            new Mode("happy", "Happy Hour", "+30% Absatz, +30% Kosten, beste Stimmung.").prod(1.3).upkeep(1.3).rep(0.01));
        upgrades(BuildingType.BOOST_STAND,
            new Upgrade("neon", "Neon-Schild", 400, "+2 Ambiente, +Reputation: nachts sieht man den Stand vom Mond.")
                .deco(2).rep(0.02));

        modes(BuildingType.ROBOT_WORKS,
            new Mode("norm",  "Tagschicht", "Ein Roboter nach dem anderen."),
            new Mode("night", "Nachtschicht", "+35% Ausstoß (und Verbrauch), +50% Strom & Kosten.")
                .prod(1.35).power(1.5).upkeep(1.5));
        upgrades(BuildingType.ROBOT_WORKS,
            new Upgrade("servos", "Präzisions-Servos", 900, "+25% Roboter-Ausstoß.").prod(1.25));

        modes(BuildingType.KRILL_BARRACKS,
            new Mode("drill", "Grunddrill", "Links, links, links-links-links."),
            new Mode("elite", "Elite-Programm", "+30% Armee-Aufbau (und Boost-Verbrauch), +40% Kosten.")
                .prod(1.3).upkeep(1.4));
        upgrades(BuildingType.KRILL_BARRACKS,
            new Upgrade("armor", "Panzerungs-Schmiede", 900, "+20% Armee-Aufbau: Chitin trifft Titan.").prod(1.2));

        // ---------------- Darmentleerung & Abfall ----------------
        modes(BuildingType.GUT_STATION,
            new Mode("norm",   "Standard-Purge", "24 Stunden Durchspülen."),
            new Mode("gentle", "Schonende Spülung", "-30% Klärschlamm, +30% Kosten: Wellness statt Waschgang.")
                .prod(0.7).upkeep(1.3));
        upgrades(BuildingType.GUT_STATION,
            new Upgrade("cascade", "Feinfilter-Kaskade", 600, "-20% Klärschlamm.").prod(0.8));

        modes(BuildingType.BIOGAS_PLANT,
            new Mode("norm", "Normalbetrieb", "Fault vor sich hin."),
            new Mode("full", "Volllast-Fermenter", "+30% Entsorgungskapazität, +30% Strom & Kosten.")
                .prod(1.3).power(1.3).upkeep(1.3));
        upgrades(BuildingType.BIOGAS_PLANT,
            new Upgrade("gasturbine", "Gas-Verstromung", 700, "+20% Entsorgungskapazität.").prod(1.2));

        // ---------------- Lager ----------------
        modes(BuildingType.STORAGE_SHED,
            new Mode("norm",  "Offenes Lager", "Regale, Fässer, Vorhängeschloss."),
            new Mode("dense", "Dicht gestapelt", "+20% Kapazität, +30% Kosten (man findet nichts mehr).")
                .storage(1.2).upkeep(1.3));
        upgrades(BuildingType.STORAGE_SHED,
            new Upgrade("shelving", "Regal-Optimierung", 150, "+25% Lagerkapazität dieses Schuppens.").storage(1.25));

        modes(BuildingType.WAREHOUSE,
            new Mode("norm",    "Normalbetrieb", "Palette rein, Palette raus."),
            new Mode("guenther", "Günther-Modus", "+15% Kapazität, +20% Kosten: Günther fährt Überstunden.")
                .storage(1.15).upkeep(1.2).rep(0.01));
        upgrades(BuildingType.WAREHOUSE,
            new Upgrade("autorack", "Automatisches Hochregal", 900, "+30% Kapazität, +20% Strom.").storage(1.3).power(1.2));

        modes(BuildingType.COLD_STORE,
            new Mode("norm", "Minus 18 Grad", "Standard-Frost."),
            new Mode("deep", "Tiefkühl-Boost", "+30% Kapazität, +40% Strom: Minus 32 Grad, Glühwein Pflicht.")
                .storage(1.3).power(1.4));
        upgrades(BuildingType.COLD_STORE,
            new Upgrade("insulation", "Iso-Dämmung", 500, "+25% Kapazität, -10% Strom.").storage(1.25).power(0.9));

        // ---------------- Ruf & Deko (Ambiente-Träger) ----------------
        modes(BuildingType.ZEN_GARDEN,
            new Mode("still",     "Stille", "Plätschern, sonst nichts."),
            new Mode("meditate",  "Geführte Meditation", "+Reputation, +2 Ambiente, +50% Kosten (der Coach nimmt 80/h).")
                .rep(0.03).deco(2).upkeep(1.5));
        upgrades(BuildingType.ZEN_GARDEN,
            new Upgrade("koi", "Koi-Garnelen-Teich", 400, "+3 Ambiente, +Reputation: Zierkrabben mit Namensschildern.")
                .deco(3).rep(0.02));

        modes(BuildingType.MASCOT_STATUE,
            new Mode("classic", "Klassisch", "Greg in Gold, Blick in die Ferne."),
            new Mode("lit",     "Nachts beleuchtet", "+3 Ambiente, +50% Kosten: Greg strahlt. Wörtlich.")
                .deco(3).upkeep(1.5).rep(0.02));
        upgrades(BuildingType.MASCOT_STATUE,
            new Upgrade("gilding", "Echte Vergoldung", 800, "+4 Ambiente, +Reputation: jetzt salutieren auch Touristen.")
                .deco(4).rep(0.04));

        modes(BuildingType.FOUNTAIN,
            new Mode("calm",  "Sanftes Plätschern", "Dezent und dauerhaft."),
            new Mode("show",  "Wasser-Choreografie", "+3 Ambiente, +60% Kosten: stündlich das große Finale.")
                .deco(3).upkeep(1.6).rep(0.02));
        upgrades(BuildingType.FOUNTAIN,
            new Upgrade("led", "LED-Unterwasserlicht", 250, "+2 Ambiente: Kitsch-Level Endboss, Phase 2.").deco(2));

        modes(BuildingType.PETTING_POOL,
            new Mode("norm",   "Freies Streicheln", "Geduldige Garnelen, gerührte Eltern."),
            new Mode("school", "Schulklassen-Programm", "++Reputation, +40% Kosten: 30 Kinder, ein Becken, ein Plan.")
                .rep(0.04).upkeep(1.4));
        upgrades(BuildingType.PETTING_POOL,
            new Upgrade("veterans", "Geduld-Veteranen", 300, "+Reputation, +1 Ambiente: die ruhigsten Garnelen der Branche.")
                .rep(0.03).deco(1));

        modes(BuildingType.BOYBAND_STAGE,
            new Mode("weekend", "Wochenend-Shows", "Freitag bis Sonntag: NEW KRILLS live."),
            new Mode("tour",    "Welttournee-Proben", "++Reputation, +2 Ambiente, +50% Kosten: die Nebelmaschine läuft heiß.")
                .rep(0.05).deco(2).upkeep(1.5),
            new Mode("meet",    "Meet & Greet", "+3 Ambiente, +30% Kosten: Selfies mit Siggi Scampi.")
                .deco(3).upkeep(1.3));
        upgrades(BuildingType.BOYBAND_STAGE,
            new Upgrade("pyro", "Pyrotechnik-Paket", 800, "+3 Ambiente, +Reputation: Funken, Konfetti, Gänsehaut.")
                .deco(3).rep(0.03));
    }

    public static List<Mode> modes(BuildingType t)       { return MODES.getOrDefault(t, List.of()); }
    public static List<Upgrade> upgrades(BuildingType t) { return UPGRADES.getOrDefault(t, List.of()); }
    public static boolean hasModes(BuildingType t)       { return !modes(t).isEmpty(); }
    public static boolean hasUpgrades(BuildingType t)    { return !upgrades(t).isEmpty(); }
}
