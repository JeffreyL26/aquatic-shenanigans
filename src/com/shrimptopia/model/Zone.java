package com.shrimptopia.model;

import java.awt.Color;

/** Bereiche der Farm. Jede Zone ist ein eigenes Bau-Gitter mit eigenem Look. */
public enum Zone {
    PRODUKTION("Produktionshalle", new Color(46, 58, 67),  new Color(41, 52, 60),  new Color(0, 199, 183)),
    FORSCHUNG ("Forschungsflügel", new Color(48, 50, 74),  new Color(42, 44, 66),  new Color(170, 120, 230)),
    LOGISTIK  ("Logistik & Export", new Color(44, 54, 60),  new Color(39, 48, 54),  new Color(255, 159, 67)),
    EMPFANG   ("Empfang & Garten",  new Color(46, 64, 50),  new Color(41, 58, 45),  new Color(110, 200, 120));

    public final String displayName;
    public final Color floorA;
    public final Color floorB;
    /** Signaturfarbe der Zone (Rahmen, Nameplate, Deko) - macht die Karten unterscheidbar. */
    public final Color accent;

    Zone(String displayName, Color floorA, Color floorB, Color accent) {
        this.displayName = displayName;
        this.floorA = floorA;
        this.floorB = floorB;
        this.accent = accent;
    }

    public String unlockFlag() { return "zone." + name(); }
    public boolean alwaysUnlocked() { return this == PRODUKTION; }
}
