package com.shrimptopia.model;

/**
 * Marketing-Streams: dauerhaft laufende Werbekanäle mit Tageskosten, die BEKANNTHEIT aufbauen.
 * Bekanntheit (0..100) bestimmt zusammen mit der Reputation die tägliche NACHFRAGE - und jeder
 * Kanal trägt nur bis zu seiner Sättigungsgrenze: Flugblätter machen dich im Viertel bekannt,
 * aber niemals landesweit. Ohne laufende Werbung bröckelt die Bekanntheit langsam wieder ab.
 * (Militär-Depot und Schwarzmarkt laufen über Verträge und brauchen keine Werbung.)
 */
public enum MarketingStream {

    FLYER    ("Flugblätter & Vereinsheim", "Selbstgedruckte Zettel, Greta vom Kiosk verteilt mit. Mehr als das Viertel erreicht das nie.",
              8, 1.0, 12, null),
    RADIO    ("Lokalradio-Spot", "'Frische Shrimps - jetzt auch ohne Meer!' Läuft zwischen Blasmusik und Stau-Meldung.",
              35, 1.6, 30, "mkt.radio"),
    SOCIAL   ("Social-Media-Auftritt", "Mira postet Becken-Fotos & Storys. Die Kommentare sind zu 90% 'warum?'. Es funktioniert.",
              70, 2.0, 48, "mkt.social"),
    TUBE     ("ShrimpTube-Kanal", "Dein eigener Video-Kanal aus dem Viral-Hit. Reichweite: beängstigend.",
              240, 2.6, 78, "mkt.tube"),
    BILLBOARD("Autobahn-Plakat", "20 Meter Garnele an der A6. Niemand vergisst dieses Plakat. Niemand.",
              150, 2.2, 62, "mkt.billboard"),
    TV_SPOT  ("TV-Spot 'Shrimp your day!'", "Primetime-Werbung von der Agentur Krusten & Krusten. Ohrwurm-Garantie.",
              480, 3.5, 95, "mkt.tv"),
    BOYBAND  ("Subliminal-Pop (New Krills)", "Siggi Scampis Boygroup flüstert 'kauf Shrimps' zwischen die Refrains. "
              + "Wirkt unterschwellig: die Sättigungsgrenze steigt mit eurer Reputation.",
              60, 1.4, 40, "mkt.boyband");

    public final String displayName, desc;
    /** Geld pro Tag, solange der Stream läuft. */
    public final double costPerDay;
    /** Bekanntheits-Zuwachs pro Tag (voller Wert bei Bekanntheit 0, nimmt Richtung Grenze ab). */
    public final double awarenessPerDay;
    /** Sättigungsgrenze: über diese Bekanntheit trägt der Kanal nicht hinaus. */
    public final double awarenessCap;
    /** null = von Anfang an buchbar; sonst benötigtes Freischalt-Flag. */
    public final String requiresFlag;

    MarketingStream(String displayName, String desc, double costPerDay,
                    double awarenessPerDay, double awarenessCap, String requiresFlag) {
        this.displayName = displayName; this.desc = desc;
        this.costPerDay = costPerDay;
        this.awarenessPerDay = awarenessPerDay; this.awarenessCap = awarenessCap;
        this.requiresFlag = requiresFlag;
    }

    /** Effektive Sättigungsgrenze (Subliminal-Pop skaliert mit der Reputation). */
    public double capFor(double reputation) {
        return this == BOYBAND ? awarenessCap + reputation * 0.25 : awarenessCap;
    }
}
