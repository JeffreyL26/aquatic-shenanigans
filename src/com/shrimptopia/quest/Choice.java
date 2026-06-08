package com.shrimptopia.quest;

import java.util.Arrays;
import java.util.List;

/** Eine wählbare Antwort-Option in einem Quest-Popup. */
public class Choice {
    public final String text;       // Button-Beschriftung
    public final String resultText; // Log-Meldung nach der Wahl (kann null sein)
    public final List<QuestEffect> effects;
    public String nextQuestId;       // optionale Folgequest

    public Choice(String text, String resultText, QuestEffect... effects) {
        this.text = text;
        this.resultText = resultText;
        this.effects = Arrays.asList(effects);
    }

    public Choice then(String questId) { this.nextQuestId = questId; return this; }
}
