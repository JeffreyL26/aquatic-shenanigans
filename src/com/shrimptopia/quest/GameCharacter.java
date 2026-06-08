package com.shrimptopia.quest;

import com.shrimptopia.model.IconKind;
import java.awt.Color;

/** Sprechende Figuren in Popups/Quests. Krillkill & Akwanov haben eigene Portraits. */
public enum GameCharacter {
    ADVISOR ("Dr. Perla Pereira", "Garnelen-Biologin",   IconKind.PORTRAIT_ADVISOR,  new Color(0, 199, 183)),
    MAYOR   ("Stadtverwaltung",   "Bürgermeisteramt",   IconKind.PORTRAIT_MAYOR,    new Color(120, 160, 220)),
    PRESS   ("Die Presse",        "Boulevard-Redaktion", IconKind.PORTRAIT_MAYOR,    new Color(230, 180, 90)),
    KRILLKILL("General \"Krillkill\" Johnson", "Kriegsveteran a.D.", IconKind.PORTRAIT_GENERAL, new Color(150, 80, 70)),
    AKWANOV ("Außenminister Akwanov", "Usbekistan",     IconKind.PORTRAIT_DIPLOMAT, new Color(90, 150, 130)),
    NARRATOR("ShrimpTopia",       "",                    IconKind.SHRIMP,            new Color(255, 118, 104));

    public final String displayName, role;
    public final IconKind portrait;
    public final Color color;

    GameCharacter(String displayName, String role, IconKind portrait, Color color) {
        this.displayName = displayName; this.role = role; this.portrait = portrait; this.color = color;
    }
}
