package com.shrimptopia.model;

/**
 * Farmweite Dekrete (Tropico-Stil), die das HQ erlässt. Edikte derselben Gruppe schließen
 * sich gegenseitig aus (#8); gruppenlose Edikte sind frei kombinierbar.
 */
public enum Edict {
    OPEN_HOUSE   ("Tag der offenen Halle", "Besucher strömen rein: +Reputation, dafür höhere Kosten.", "Werbung"),
    AD_BLITZ     ("Aggressive Werbekampagne", "Marktschreierei: +10% Verkaufspreis, aber -Reputation.", "Werbung"),
    BOOST_MANDATE("SHRIMPBOOST-Pflicht", "Belegschaft auf Energydrink: +15% Becken-Output, -Reputation.", "Arbeit"),
    FOUR_DAY     ("Vier-Tage-Woche", "Glückliche Belegschaft (+Reputation), aber -10% Arbeitskraft.", "Arbeit"),
    MARTIAL_LAW  ("Kriegsrecht", "Eiserne Disziplin: +20% Arbeitskraft, aber kräftig -Reputation.", "Arbeit"),
    TAX_HAVEN    ("Steueroase Garnelien", "Kreative Buchführung: -20% Betriebskosten, leicht -Reputation.", "Staat"),
    SUBSIDIES    ("Subventionen beantragt", "Vater Staat zahlt mit: +8% Verkaufspreis.", "Staat"),
    FREE_TRADE   ("Freihandels-Abkommen", "Mehr Absatz: +20% Verkaufskapazität.", "Handel"),
    PREMIUM      ("Premium-Strategie", "Luxus-Image: +15% Preis, aber -10% Menge.", "Handel"),
    ECO_CERT     ("Öko-Zertifizierung", "Grünes Gewissen: ++Reputation, aber -10% Becken-Output.", "Öko"),
    MAX_EFFICIENCY("Maximale Effizienz", "Output über alles: +12% Becken-Output, -Reputation.", "Öko"),
    GREG_BOARD   ("Greg in den Vorstand", "Dr. Perlas Support-Garnele im Wasserglas führt mit: stetig +Reputation.", null),
    NIGHT_ROBOTS ("Nachtschicht für Roboter", "Roboter laufen rund um die Uhr: +5% Arbeitskraft.", null),

    // Shrimp-Gewerkschaft (freigeschaltet über den Becken-3-Vorfall)
    UNION_COUNCIL("Becken-Betriebsrat", "Klausi 2.0 bekommt Mitsprache: +Reputation, aber -5% Becken-Output.", "Gewerkschaft", "shrimp_union"),
    UNION_WATCH  ("Becken-Überwachung", "Kameras & Spitzel-Shrimps halten die Räte im Zaum: +10% Output, -Reputation.", "Gewerkschaft", "shrimp_union"),
    // Shrimp Team Six - deine Spionagetruppe (ein Einsatz zurzeit)
    STS_SPY      ("STS-6: Industriespionage", "Team Six belauscht die Konkurrenz: +8% Verkaufspreis, leicht -Reputation.", "STS6", "sts6"),
    STS_GUARD    ("STS-6: Werkschutz", "Team Six sichert die Becken (niemand klaut, nichts sabotiert): +10% Becken-Output, +10% Kosten.", "STS6", "sts6"),
    STS_PR       ("STS-6: Verdeckte PR", "Team Six 'korrigiert' die öffentliche Meinung: +Reputation/Tag, +Kosten.", "STS6", "sts6");

    public final String name, desc, group;
    /** null = immer verfügbar; sonst nötiges Freischalt-Flag (Quest). */
    public final String requiresFlag;
    Edict(String name, String desc, String group) { this(name, desc, group, null); }
    Edict(String name, String desc, String group, String requiresFlag) {
        this.name = name; this.desc = desc; this.group = group; this.requiresFlag = requiresFlag;
    }

    public void apply(FarmModifiers fm) {
        switch (this) {
            case OPEN_HOUSE    -> { fm.repPerTick += 0.12; fm.upkeepMult *= 1.10; }
            case AD_BLITZ      -> { fm.priceMult *= 1.10; fm.repPerTick -= 0.05; }
            case BOOST_MANDATE -> { fm.tankShrimpMult *= 1.15; fm.repPerTick -= 0.05; }
            case FOUR_DAY      -> { fm.repPerTick += 0.10; fm.workerMult *= 0.90; }
            case MARTIAL_LAW   -> { fm.workerMult *= 1.20; fm.repPerTick -= 0.15; }
            case TAX_HAVEN     -> { fm.upkeepMult *= 0.80; fm.repPerTick -= 0.04; }
            case SUBSIDIES     -> { fm.priceMult *= 1.08; }
            case FREE_TRADE    -> { fm.sellCapMult *= 1.20; }
            case PREMIUM       -> { fm.priceMult *= 1.15; fm.sellCapMult *= 0.90; }
            case ECO_CERT      -> { fm.repPerTick += 0.15; fm.tankShrimpMult *= 0.90; }
            case MAX_EFFICIENCY-> { fm.tankShrimpMult *= 1.12; fm.repPerTick -= 0.08; }
            case GREG_BOARD    -> { fm.repPerTick += 0.06; }
            case NIGHT_ROBOTS  -> { fm.workerMult *= 1.05; }
            case UNION_COUNCIL -> { fm.repPerTick += 0.10; fm.tankShrimpMult *= 0.95; }
            case UNION_WATCH   -> { fm.tankShrimpMult *= 1.10; fm.repPerTick -= 0.08; }
            case STS_SPY       -> { fm.priceMult *= 1.08; fm.repPerTick -= 0.04; }
            case STS_GUARD     -> { fm.tankShrimpMult *= 1.10; fm.upkeepMult *= 1.10; }
            case STS_PR        -> { fm.repPerTick += 0.12; fm.upkeepMult *= 1.08; }
        }
    }
}
