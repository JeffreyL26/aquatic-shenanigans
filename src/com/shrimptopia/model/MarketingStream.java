package com.shrimptopia.model;

/**
 * Marketing-Streams: dauerhaft laufende Werbekanäle mit Tageskosten, die NACHFRAGE erzeugen.
 * Verbraucher-Märkte (Klapptisch, Börse, Restaurant, Export) verkaufen pro Tag höchstens so
 * viele Shrimps, wie Nachfrage da ist - ohne Marketing kauft eben nur die Nachbarschaft.
 * (Militär-Depot und Schwarzmarkt laufen über Verträge und brauchen keine Werbung.)
 */
public enum MarketingStream {

    FLYER    ("Flugblätter & Vereinsheim", "Selbstgedruckte Zettel, Greta vom Kiosk verteilt mit.",
              8, 6, null),
    RADIO    ("Lokalradio-Spot", "'Frische Shrimps - jetzt auch ohne Meer!' Läuft zwischen Blasmusik und Stau-Meldung.",
              30, 14, "mkt.radio"),
    SOCIAL   ("Social-Media-Auftritt", "Mira postet Becken-Fotos & Storys. Die Kommentare sind zu 90% 'warum?'. Es funktioniert.",
              60, 26, "mkt.social"),
    TUBE     ("ShrimpTube-Kanal", "Dein eigener Video-Kanal aus dem Viral-Hit. Reichweite: beängstigend.",
              120, 55, "mkt.tube"),
    BILLBOARD("Autobahn-Plakat", "20 Meter Garnele an der A6. Niemand vergisst dieses Plakat. Niemand.",
              200, 90, "mkt.billboard"),
    TV_SPOT  ("TV-Spot 'Shrimp your day!'", "Primetime-Werbung von der Agentur Krusten & Krusten. Ohrwurm-Garantie.",
              480, 210, "mkt.tv");

    public final String displayName, desc;
    /** Geld pro Tag, solange der Stream läuft. */
    public final double costPerDay;
    /** Zusätzliche Basis-Nachfrage (Shrimps/Tag, vor Reputations-Faktor). */
    public final double demand;
    /** null = von Anfang an buchbar; sonst benötigtes Freischalt-Flag. */
    public final String requiresFlag;

    MarketingStream(String displayName, String desc, double costPerDay, double demand, String requiresFlag) {
        this.displayName = displayName; this.desc = desc;
        this.costPerDay = costPerDay; this.demand = demand; this.requiresFlag = requiresFlag;
    }
}
