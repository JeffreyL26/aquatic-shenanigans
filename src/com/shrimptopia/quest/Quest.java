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

    public Quest(String id, GameCharacter giver, String title, String body, Choice... choices) {
        this.id = id; this.giver = giver; this.title = title; this.body = body;
        this.choices = Arrays.asList(choices);
    }

    public Quest when(Condition c) { this.trigger = c; return this; }
    public Quest chain()           { this.chainOnly = true; this.trigger = Condition.never(); return this; }
    public Quest by(String name)   { this.giverName = name; return this; }
    public Quest kind(int k)       { this.kind = k; return this; }

    public String giverName() { return giverName != null ? giverName : giver.displayName; }
}
