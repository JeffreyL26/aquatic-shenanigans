package com.shrimptopia.model;

import java.awt.Color;

/**
 * Qualitätsstufen der Shrimps. Höhere Tiers sind wertvoller, aber teils kontrovers
 * (negative Reputation) und nur an bestimmten Märkten verkaufbar.
 * Reihenfolge = Qualität (STANDARD am niedrigsten).
 */
public enum ShrimpTier {
    STANDARD ("Wald-und-Wiesen-Shrimp", 18,  0.00, new Color(255, 150, 130), "STD"),
    BIO      ("Bio-Shrimp",             32,  0.04, new Color(150, 210, 120), "BIO"),
    GOURMET  ("Gourmet-Shrimp",         55,  0.06, new Color(255, 198, 110), "GOU"),
    PROTEIN  ("Protein-Bombe",          85, -0.03, new Color(232, 120,  88), "PRO"),
    GENTECH  ("Designer-Shrimp",       130, -0.07, new Color(190, 120, 230), "GEN"),
    WARKRILL ("Kampf-Krill",           210, -0.12, new Color(220,  70,  70), "WAR");

    public final String displayName;
    public final double baseValue;    // Geld pro Stück (Basis, vor Markt/Reputation)
    public final double repPerUnit;    // Reputationsänderung pro verkauftem Stück
    public final Color color;
    public final String shortName;

    ShrimpTier(String displayName, double baseValue, double repPerUnit, Color color, String shortName) {
        this.displayName = displayName;
        this.baseValue = baseValue;
        this.repPerUnit = repPerUnit;
        this.color = color;
        this.shortName = shortName;
    }

    /** Freischalt-Flag (STANDARD ist immer frei). */
    public String unlockFlag() { return "tier." + name(); }
    public boolean alwaysUnlocked() { return this == STANDARD; }
}
