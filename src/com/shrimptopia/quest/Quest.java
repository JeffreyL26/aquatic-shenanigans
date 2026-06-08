package com.shrimptopia.quest;

import java.util.Arrays;
import java.util.List;

/** Eine Quest / ein Story-Popup im Tropico-Stil. */
public class Quest {
    public final String id;
    public final GameCharacter giver;
    public final String title;
    public final String body;
    public final List<Choice> choices;

    public String giverName;                 // optionaler Anzeigename (sonst giver.displayName)
    public Condition trigger = Condition.always();
    public boolean chainOnly = false;        // nur per Folgequest startbar, nie automatisch
    public int kind = 0;                     // 0 info, 1 gut, 2 schlecht (Log-Farbe)
    public Condition objective = null;       // muss ueber Zeit erfuellt werden, bevor das Popup erscheint
    public String objectiveText = null;      // Anzeige im Quest-Log (Aufgabe)

    public Quest(String id, GameCharacter giver, String title, String body, Choice... choices) {
        this.id = id; this.giver = giver; this.title = title; this.body = body;
        this.choices = Arrays.asList(choices);
    }

    public Quest when(Condition c) { this.trigger = c; return this; }
    public Quest chain()           { this.chainOnly = true; this.trigger = Condition.never(); return this; }
    public Quest by(String name)   { this.giverName = name; return this; }
    public Quest kind(int k)       { this.kind = k; return this; }
    /** Ketten-Stufe mit Ziel: erscheint erst, wenn das Ziel ueber Zeit erfuellt ist. */
    public Quest goal(Condition obj, String text) {
        this.objective = obj; this.objectiveText = text;
        this.chainOnly = true; this.trigger = Condition.never();
        return this;
    }

    public String giverName() { return giverName != null ? giverName : giver.displayName; }
}
