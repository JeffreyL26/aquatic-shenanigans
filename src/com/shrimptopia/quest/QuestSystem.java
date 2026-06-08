package com.shrimptopia.quest;

import com.shrimptopia.model.GameState;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Verwaltet Quests/Storylines: prüft Auslöse-Bedingungen, reiht Popups ein und
 * wendet Auswahl-Konsequenzen an. Wird vom GameFrame nach jedem Tick aktualisiert.
 */
public class QuestSystem {

    private final List<Quest> all = new ArrayList<>();
    private final Map<String, Quest> byId = new HashMap<>();
    private final Set<String> done = new HashSet<>();
    private final Set<String> triggered = new HashSet<>();
    private final Set<String> flags = new HashSet<>();
    private final Deque<Quest> pending = new ArrayDeque<>();

    private int lastShownDay = -100;
    private final int minGap = 3;   // Tage Mindestabstand zwischen automatischen Quests
    private int rival = 0;          // Akwanov-Rivalität 0..100

    public QuestSystem() { QuestContent.populate(this); }

    public void add(Quest q) { all.add(q); byId.put(q.id, q); }

    /** Pro Tick: höchstens eine neue (nicht-Ketten-)Quest einreihen. */
    public void update(GameState gs) {
        if (!pending.isEmpty()) return;
        if (gs.getDay() - lastShownDay < minGap) return;
        for (Quest q : all) {
            if (q.chainOnly || triggered.contains(q.id)) continue;
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

    public void forceStart(String id) {
        Quest q = byId.get(id);
        if (q != null && !triggered.contains(id)) { triggered.add(id); pending.add(q); }
    }

    public void setFlag(String f) { flags.add(f); }
    public boolean hasFlag(String f) { return flags.contains(f); }
    public boolean isDone(String id) { return done.contains(id); }
    public boolean isTriggered(String id) { return triggered.contains(id); }
    public void addRival(double d) { rival = (int) Math.max(0, Math.min(100, rival + d)); }
    public int getRival() { return rival; }
    public boolean rivalActive() { return triggered.contains("akwanov_intro"); }
}
