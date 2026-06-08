package com.shrimptopia.model;

/** Bestimmt, welches Vektor-Symbol für ein Gebäude/eine Ressource gezeichnet wird. */
public enum IconKind {
    HQ, TANK, WATER, ALGAE, POWER, SOLAR, HOUSE, SALES, LAB, FOOD,
    // v2-Gebäude
    WATERHUB, GENLAB, DOCK, MILITARY, BLACKMARKET, VISITOR, GARDEN,
    // v3-Gebaeude/Ressourcen
    SHELL, CAN, ROBOT, SHIELD,
    // reine Ressourcen-/UI-Symbole
    COIN, BOLT, DROP, LEAF, SHRIMP, PERSON, STAR,
    // Charakter-Portraits
    PORTRAIT_GENERAL, PORTRAIT_DIPLOMAT, PORTRAIT_ADVISOR, PORTRAIT_MAYOR
}
