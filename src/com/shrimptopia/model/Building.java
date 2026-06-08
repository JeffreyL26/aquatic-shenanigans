package com.shrimptopia.model;

import java.util.HashSet;
import java.util.Set;

/** Eine konkrete, auf der Karte platzierte Gebäude-Instanz. */
public class Building {
    public final BuildingType type;
    public final Zone zone;
    public final int col;
    public final int row;

    /** Index des aktuell gewählten Betriebs-Modus (in BuildingCatalog.modes(type)). */
    public int mode = 0;
    /** Gekaufte Upgrade-IDs. */
    public final Set<String> upgrades = new HashSet<>();

    /** 0..1 - wie gut das Gebäude diesen Tick lief (für die Statusanzeige). */
    public double efficiency = 1.0;
    /** Kurzer Klartext-Hinweis bei Problemen (z.B. "Kein Strom"). */
    public String statusNote = "";
    /** Effektive Werte des letzten Ticks (für den Inspektor). */
    public Stats lastStats = new Stats();
    public int placedOnDay = 0;

    public Building(BuildingType type, Zone zone, int col, int row) {
        this.type = type;
        this.zone = zone;
        this.col = col;
        this.row = row;
    }
}
