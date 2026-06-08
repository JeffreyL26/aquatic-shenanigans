package com.shrimptopia.tutorial;

import com.shrimptopia.model.BuildingType;
import java.util.List;

/** Steuert das geführte Einsteiger-Tutorial (Dr. Perla Pereira). */
public class Tutorial {
    private final List<TutorialStep> steps;
    private int index = 0;
    private boolean active = true;

    public Tutorial() { steps = TutorialContent.steps(); }

    public boolean isActive() { return active && index < steps.size(); }
    public TutorialStep current() { return isActive() ? steps.get(index) : null; }
    public int index() { return index; }
    public int total() { return steps.size(); }

    public void next() {
        if (index < steps.size()) index++;
        if (index >= steps.size()) active = false;
    }

    public void skip() { active = false; }

    // --- Aktions-Benachrichtigungen vom GameFrame ---
    public void onBuilt(BuildingType t) {
        TutorialStep c = current();
        if (c != null && c.advance == TutorialStep.Advance.BUILD && c.buildTarget == t) next();
    }
    public void onInspectorOpened() {
        TutorialStep c = current();
        if (c != null && c.advance == TutorialStep.Advance.OPEN_INSPECTOR) next();
    }
}
