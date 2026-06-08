package com.shrimptopia.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Zentrales Farb- und Schrift-Theme für ShrimpTopia.
 * Look: "industrielle Aquakultur" - dunkles Schiefer-Blau mit Teal-Akzenten.
 */
public final class Palette {

    private Palette() {}

    // Hintergründe
    public static final Color BG_DARK     = new Color(16, 22, 27);
    public static final Color PANEL       = new Color(26, 34, 41);
    public static final Color PANEL_LIGHT = new Color(36, 47, 56);
    public static final Color PANEL_HOVER = new Color(48, 62, 72);

    // Karten-Boden (zwei abwechselnde Kacheln)
    public static final Color FLOOR_A = new Color(44, 56, 64);
    public static final Color FLOOR_B = new Color(40, 51, 59);
    public static final Color GRID    = new Color(30, 39, 46);

    // Akzente
    public static final Color ACCENT  = new Color(0, 199, 183);   // Teal
    public static final Color ACCENT2 = new Color(255, 159, 67);  // Orange

    // Text
    public static final Color TEXT     = new Color(233, 241, 245);
    public static final Color TEXT_DIM = new Color(150, 166, 176);

    // Ressourcen-Farben
    public static final Color MONEY   = new Color(255, 205, 86);
    public static final Color POWER   = new Color(255, 214, 64);
    public static final Color WATER   = new Color(64, 170, 255);
    public static final Color FEED    = new Color(124, 200, 92);
    public static final Color SHRIMP  = new Color(255, 118, 104);
    public static final Color WORKERS = new Color(184, 162, 255);
    public static final Color REP     = new Color(255, 130, 200);

    // Statusfarben
    public static final Color GOOD = new Color(96, 220, 120);
    public static final Color BAD  = new Color(255, 92, 92);
    public static final Color WARN = new Color(255, 190, 70);

    // v3-Ressourcen
    public static final Color SHELL = new Color(214, 192, 150);
    public static final Color BOOST = new Color(40, 224, 220);
    public static final Color ROBOT = new Color(150, 168, 196);
    public static final Color ARMY  = new Color(140, 152, 92);

    // Schriften
    public static final Font FONT_H1    = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_H2    = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY  = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD  = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_TINY  = new Font("SansSerif", Font.BOLD, 10);
    public static final Font FONT_MONO  = new Font("Monospaced", Font.BOLD, 14);
}
