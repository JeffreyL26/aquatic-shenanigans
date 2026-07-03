package com.shrimptopia.quest;

import com.shrimptopia.model.IconKind;
import java.awt.Color;

/**
 * Sprechende Figuren in Popups/Quests. {@code avatarKey} verweist auf eine gebündelte
 * SVG-Datei (com/shrimptopia/ui/avatars/&lt;key&gt;.svg); null -> handgezeichnetes Portrait.
 * Spezifische Nebenfiguren (Mira, Olaf, Schalk ...) werden anhand des Sprecher-Namens
 * zugeordnet (siehe OverlayHost), die Hauptfiguren über diesen Key.
 */
public enum GameCharacter {
    ADVISOR ("Dr. Perla Pereira", "Garnelen-Biologin",   IconKind.PORTRAIT_ADVISOR,  new Color(0, 199, 183), "perla"),
    MAYOR   ("Stadtverwaltung",   "Bürgermeisteramt",   IconKind.PORTRAIT_MAYOR,    new Color(120, 160, 220), "rathaus"),
    PRESS   ("Die Presse",        "Boulevard-Redaktion", IconKind.PORTRAIT_MAYOR,    new Color(230, 180, 90), "presse"),
    KRILLKILL("General \"Krillkill\" Johnson", "Kriegsveteran a.D.", IconKind.PORTRAIT_GENERAL, new Color(150, 80, 70), "krillkill"),
    AKWANOV ("Außenminister Akwanov", "Usbekistan",     IconKind.PORTRAIT_DIPLOMAT, new Color(90, 150, 130), "ivan"),
    KYLE    ("u/KrustenKyle_87",      "Reddit-Rivale & Keller-Mogul", IconKind.PORTRAIT_MAYOR, new Color(255, 105, 50), "kyle"),
    NARRATOR("ShrimpTopia",       "",                    IconKind.SHRIMP,            new Color(255, 118, 104), "shrimptopia");

    public final String displayName, role;
    public final IconKind portrait;
    public final Color color;
    public final String avatarKey;

    GameCharacter(String displayName, String role, IconKind portrait, Color color, String avatarKey) {
        this.displayName = displayName; this.role = role; this.portrait = portrait; this.color = color;
        this.avatarKey = avatarKey;
    }
}
