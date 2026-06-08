package com.shrimptopia.tutorial;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.ResourceType;

/** Ein einzelner geführter Tutorial-Schritt. */
public class TutorialStep {
    /** Welcher UI-Bereich hervorgehoben (Spotlight) wird. */
    public enum Region { POPUP, TOPBAR, RESOURCE, BUILD_MENU, MAP, ZONE_TABS, INSPECTOR, CONTROLS }
    /** Wie der Schritt abgeschlossen wird. */
    public enum Advance { ACK, BUILD, OPEN_INSPECTOR }

    public final Region region;
    public final String text;
    public final Advance advance;
    public ResourceType resource;     // für RESOURCE-Spotlight
    public BuildingType buildTarget;  // für BUILD-Schritt

    public TutorialStep(Region region, String text, Advance advance) {
        this.region = region; this.text = text; this.advance = advance;
    }

    public TutorialStep res(ResourceType r)   { this.resource = r; return this; }
    public TutorialStep build(BuildingType t) { this.buildTarget = t; return this; }

    public boolean isAck() { return advance == Advance.ACK; }
}
