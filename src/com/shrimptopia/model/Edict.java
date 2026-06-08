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
    GREG_BOARD   ("Greg in den Vorstand", "Die Glas-Garnele führt mit: stetig +Reputation.", null),
    NIGHT_ROBOTS ("Nachtschicht für Roboter", "Roboter laufen rund um die Uhr: +5% Arbeitskraft.", null);

    public final String name, desc, group;
    Edict(String name, String desc, String group) { this.name = name; this.desc = desc; this.group = group; }

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
        }
    }
}
