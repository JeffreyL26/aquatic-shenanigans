package com.shrimptopia.model;

import java.awt.Color;

/** Bereiche der Farm. Jede Zone ist ein eigenes Bau-Gitter mit eigenem Look. */
public enum Zone {
    PRODUKTION("Produktionshalle", new Color(44, 56, 64),  new Color(40, 51, 59)),
    FORSCHUNG ("Forschungsflügel", new Color(48, 52, 70),  new Color(42, 46, 64)),
    LOGISTIK  ("Logistik & Export", new Color(40, 58, 66),  new Color(36, 52, 60)),
    EMPFANG   ("Empfang & Garten",  new Color(46, 62, 50),  new Color(42, 56, 46));

    public final String displayName;
    public final Color floorA;
    public final Color floorB;

    Zone(String displayName, Color floorA, Color floorB) {
        this.displayName = displayName;
        this.floorA = floorA;
        this.floorB = floorB;
    }

    public String unlockFlag() { return "zone." + name(); }
    public boolean alwaysUnlocked() { return this == PRODUKTION; }
}
