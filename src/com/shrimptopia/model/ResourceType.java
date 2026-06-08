package com.shrimptopia.model;

import com.shrimptopia.ui.Palette;
import java.awt.Color;

/** Die im HUD angezeigten Ressourcen. */
public enum ResourceType {
    MONEY     ("Vermögen",       Palette.MONEY,   IconKind.COIN),
    POWER     ("Strom",      Palette.POWER,   IconKind.BOLT),
    WATER     ("Wasser",     Palette.WATER,   IconKind.DROP),
    FEED      ("Futter",     Palette.FEED,    IconKind.LEAF),
    SHRIMP    ("Shrimps",    Palette.SHRIMP,  IconKind.SHRIMP),
    WORKERS   ("Arbeiter",   Palette.WORKERS, IconKind.PERSON),
    REPUTATION("Reputation", Palette.REP,     IconKind.STAR),
    SHELLS     ("Schalen",     Palette.SHELL, IconKind.SHELL),
    SHRIMPBOOST("SHRIMPBOOST",  Palette.BOOST, IconKind.CAN),
    ROBOTS     ("Roboter",      Palette.ROBOT, IconKind.ROBOT),
    ARMY       ("Armee-Stärke", Palette.ARMY,  IconKind.SHIELD);

    public final String displayName;
    public final Color color;
    public final IconKind icon;

    ResourceType(String displayName, Color color, IconKind icon) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
    }
}
