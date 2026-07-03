package com.shrimptopia.quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Fasst die Konsequenzen einer Quest-Entscheidung zusammen (für die Ergebnis-Karte im Overlay):
 * Ergebnistext plus konkrete Zeilen wie "Geld -800", "FREIGESCHALTET: ...", "Neue Aufgabe: ...".
 */
public class ChoiceOutcome {

    public static final int INFO = 0, GOOD = 1, BAD = 2, UNLOCK = 3, TASK = 4;

    public static final class Line {
        public final String text;
        public final int kind;
        Line(String text, int kind) { this.text = text; this.kind = kind; }
    }

    public final Quest quest;
    public final Choice choice;
    public final List<Line> lines = new ArrayList<>();

    public ChoiceOutcome(Quest quest, Choice choice) { this.quest = quest; this.choice = choice; }

    public void add(String text, int kind) { lines.add(new Line(text, kind)); }

    /** Nichts Zeigbares? (keine Effekte, kein Ergebnistext) */
    public boolean isEmpty() {
        return lines.isEmpty() && (choice == null || choice.resultText == null || choice.resultText.isEmpty());
    }
}
