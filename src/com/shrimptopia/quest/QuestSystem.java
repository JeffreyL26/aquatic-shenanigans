package com.shrimptopia.quest;

import com.shrimptopia.model.GameState;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Verwaltet Quests/Storylines. Neu in v3: Ketten-Stufen mit ZIEL ("armiert") erscheinen erst
 * als Popup, wenn ihr Ziel ueber Zeit erfuellt wurde - das macht das Spiel deutlich laenger.
 * Aktive Ziele sind ueber activeTasks() jederzeit im Quest-Log sichtbar.
 */
public class QuestSystem {

    private final List<Quest> all = new ArrayList<>();
    private final Map<String, Quest> byId = new HashMap<>();
    private final Set<String> done = new HashSet<>();
    private final Set<String> triggered = new HashSet<>();
    private final Set<String> flags = new HashSet<>();
    private final Deque<Quest> pending = new ArrayDeque<>();
    /** Quests, die auf die Erfuellung ihres Ziels warten (Reihenfolge = Anzeige im Log). */
    private final LinkedHashMap<String, Quest> armed = new LinkedHashMap<>();

    private int lastShownDay = -100;
    private final int minGap = 4;   // Mindestabstand zwischen AUTOMATISCHEN Quests
    private int rival = 0;
    private String lastEnding = null;

    public QuestSystem() { QuestContent.populate(this); }

    public void add(Quest q) { all.add(q); byId.put(q.id, q); }

    /** Pro Tick aufgerufen. */
    public void update(GameState gs) {
        if (!pending.isEmpty()) return;

        // 1) Armiertes Ziel erfuellt? -> Popup (ohne Gap, das Ziel wurde erarbeitet)
        for (Map.Entry<String, Quest> e : armed.entrySet()) {
            Quest q = e.getValue();
            if (q.objective == null || q.objective.test(gs, this)) {
                armed.remove(e.getKey());
                triggered.add(q.id);
                pending.add(q);
                lastShownDay = gs.getDay();
                return;
            }
        }

        // Spielende-Quests: sofort (ohne Gap), sobald die Kombi-Bedingung erfuellt ist
        for (Quest q : all) {
            if (!q.ending || triggered.contains(q.id)) continue;
            if (q.trigger.test(gs, this)) {
                triggered.add(q.id); pending.add(q); lastShownDay = gs.getDay(); lastEnding = q.title; return;
            }
        }

        // 2) Neue automatische Quest (mit Gap)
        if (gs.getDay() - lastShownDay < minGap) return;
        for (Quest q : all) {
            if (q.ending || q.chainOnly || triggered.contains(q.id) || armed.containsKey(q.id)) continue;
            if (q.trigger.test(gs, this)) {
                triggered.add(q.id);
                pending.add(q);
                lastShownDay = gs.getDay();
                return;
            }
        }
    }

    public boolean hasPending() { return !pending.isEmpty(); }
    public Quest peek() { return pending.peek(); }
    public int pendingCount() { return pending.size(); }
    public Quest get(String id) { return byId.get(id); }
    public boolean hasEnding() { for (Quest q : all) if (q.ending && triggered.contains(q.id)) return true; return false; }
    public String lastEndingTitle() { return lastEnding; }
    public java.util.List<Quest> achievedEndings() {
        java.util.List<Quest> out = new java.util.ArrayList<>();
        for (Quest q : all) if (q.ending && triggered.contains(q.id)) out.add(q);
        return out;
    }

    /** Aktive Aufgaben (armierte Ziel-Quests) fuer das Quest-Log. */
    public List<Quest> activeTasks() {
        List<Quest> out = new ArrayList<>();
        for (Quest q : armed.values()) if (q.objective != null) out.add(q);
        return out;
    }
    public int doneCount() { return done.size(); }

    public void resolve(GameState gs, int choiceIndex) {
        Quest q = pending.poll();
        if (q == null) return;
        done.add(q.id);
        lastShownDay = gs.getDay();
        if (q.choices.isEmpty()) return;
        int i = Math.max(0, Math.min(choiceIndex, q.choices.size() - 1));
        Choice c = q.choices.get(i);
        for (QuestEffect e : c.effects) e.apply(gs, this);
        if (c.resultText != null && !c.resultText.isEmpty())
            gs.log("[" + q.giverName() + "] " + c.resultText, GameState.LOG_INFO);
        if (c.nextQuestId != null) forceStart(c.nextQuestId);
    }

    /** Startet eine (Ketten-)Quest: mit Ziel -> armieren (wartet); ohne Ziel -> sofort anzeigen. */
    public void forceStart(String id) {
        Quest q = byId.get(id);
        if (q == null || triggered.contains(id) || armed.containsKey(id)) return;
        if (q.objective != null) armed.put(id, q);
        else { triggered.add(id); pending.add(q); if (q.ending) lastEnding = q.title; }
    }

    public void setFlag(String f) { flags.add(f); }
    public boolean hasFlag(String f) { return flags.contains(f); }
    public boolean isDone(String id) { return done.contains(id); }
    public boolean isTriggered(String id) { return triggered.contains(id); }
    public void addRival(double d) { rival = (int) Math.max(0, Math.min(100, rival + d)); }
    public int getRival() { return rival; }
    public boolean rivalActive() { return triggered.contains("akwanov_intro"); }
}
