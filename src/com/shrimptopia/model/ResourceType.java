package com.shrimptopia.model;

import com.shrimptopia.ui.Palette;
import java.awt.Color;

/** Die im HUD angezeigten Ressourcen. */
public enum ResourceType {
    MONEY     ("Vermögen",       "GELD",   Palette.MONEY,   IconKind.COIN),
    POWER     ("Strom",      "STROM",      Palette.POWER,   IconKind.BOLT),
    WATER     ("Wasser",     "WASSER",     Palette.WATER,   IconKind.DROP),
    FEED      ("Futter",     "FUTTER",     Palette.FEED,    IconKind.LEAF),
    SHRIMP    ("Shrimps",    "SHRIMPS",    Palette.SHRIMP,  IconKind.SHRIMP),
    WORKERS   ("Arbeiter",   "ARBEITER",   Palette.WORKERS, IconKind.PERSON),
    REPUTATION("Reputation", "REP",        Palette.REP,     IconKind.STAR),
    SHELLS     ("Schalen",     "SCHALEN",  Palette.SHELL, IconKind.SHELL),
    SHRIMPBOOST("SHRIMPBOOST",  "BOOST",   Palette.BOOST, IconKind.CAN),
    ROBOTS     ("Roboter",      "ROBOTER", Palette.ROBOT, IconKind.ROBOT),
    ARMY       ("Armee-Stärke", "ARMEE",   Palette.ARMY,  IconKind.SHIELD),
    WASTE      ("Abfall",       "ABFALL",  Palette.WASTE, IconKind.WASTE);

    public final String displayName;
    /** Kurzform fürs schmale Top-HUD (max. ~8 Zeichen). */
    public final String hudLabel;
    public final Color color;
    public final IconKind icon;

    ResourceType(String displayName, String hudLabel, Color color, IconKind icon) {
        this.displayName = displayName;
        this.hudLabel = hudLabel;
        this.color = color;
        this.icon = icon;
    }
}
