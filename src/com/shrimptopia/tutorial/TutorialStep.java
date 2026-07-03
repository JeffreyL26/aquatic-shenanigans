package com.shrimptopia.tutorial;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.ResourceType;

/** Ein einzelner geführter Tutorial-Schritt. */
public class TutorialStep {
    /** Welcher UI-Bereich hervorgehoben (Spotlight) wird. ALMANAC öffnet das Shrimp-Tier-Menü. */
    public enum Region { POPUP, TOPBAR, RESOURCE, BUILD_MENU, MAP, ZONE_TABS, INSPECTOR, CONTROLS, ALMANAC }
    /** Wie der Schritt abgeschlossen wird. */
    public enum Advance { ACK, BUILD, OPEN_INSPECTOR }

    public final Region region;
    public final String text;
    public final Advance advance;
    public ResourceType resource;     // für RESOURCE-Spotlight
    public BuildingType buildTarget;  // für BUILD-Schritt
    public int almanacTab = 2;        // für ALMANAC-Schritte: welcher Almanach-Tab geöffnet wird

    public TutorialStep(Region region, String text, Advance advance) {
        this.region = region; this.text = text; this.advance = advance;
    }

    public TutorialStep res(ResourceType r)   { this.resource = r; return this; }
    public TutorialStep build(BuildingType t) { this.buildTarget = t; return this; }
    public TutorialStep tab(int t)            { this.almanacTab = t; return this; }

    public boolean isAck() { return advance == Advance.ACK; }
}
