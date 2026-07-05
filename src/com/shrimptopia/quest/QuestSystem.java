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
    /** Welche Option (Index) bei einer erledigten Quest gewählt wurde - für den Quest-Baum. */
    private final Map<String, Integer> chosen = new HashMap<>();
    private final Set<String> triggered = new HashSet<>();
    private final Set<String> flags = new HashSet<>();
    private final Deque<Quest> pending = new ArrayDeque<>();
    /** Quests, die auf die Erfuellung ihres Ziels warten (Reihenfolge = Anzeige im Log). */
    private final LinkedHashMap<String, Quest> armed = new LinkedHashMap<>();

    private int lastShownDay = -100;
    private final int minGap = 40;        // Mindestabstand (Tage/Ticks) zwischen AUTOMATISCHEN Quests
    private final int minGapArmed = 24;   // Mindestabstand, bevor ein erarbeitetes Ketten-Ziel als Popup erscheint
    private int rival = 0;
    private String lastEnding = null;
    /** Wurde der Hinweis "im Quest-Log oben rechts" schon einmal gezeigt? */
    private boolean hintedQuestLog = false;

    public QuestSystem() { QuestContent.populate(this); }

    public void add(Quest q) { all.add(q); byId.put(q.id, q); }

    /** Pro Tick aufgerufen. */
    public void update(GameState gs) {
        if (!pending.isEmpty()) return;

        // 1) Spielende-Quests zuerst: sofort, sobald die Kombi-Bedingung erfuellt ist
        //    (ein Finale soll nie durch einen Gap blockiert werden).
        for (Quest q : all) {
            if (!q.ending || triggered.contains(q.id)) continue;
            if (q.trigger.test(gs, this)) {
                triggered.add(q.id); pending.add(q); lastShownDay = gs.getDay(); lastEnding = q.title; return;
            }
        }

        // 2) Armiertes Ziel erfuellt? -> Popup, aber erst nach einem Mindestabstand. Sonst
        //    erscheint direkt nach dem Wegklicken eines Popups das naechste Schlag auf Schlag.
        if (gs.getDay() - lastShownDay >= minGapArmed) {
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
        }

        // 3) Neue automatische Quest (groesserer Gap -> Zeit, die Produktion aufzubauen)
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

    /**
     * Wendet die gewählte Option an und liefert eine Zusammenfassung der Konsequenzen
     * (Deltas, Freischaltungen, Kampfausgang, neue Aufgabe) für die Ergebnis-Karte.
     */
    public ChoiceOutcome resolve(GameState gs, int choiceIndex) {
        Quest q = pending.poll();
        if (q == null) return null;
        done.add(q.id);
        lastShownDay = gs.getDay();
        if (q.choices.isEmpty()) return null;
        int i = Math.max(0, Math.min(choiceIndex, q.choices.size() - 1));
        chosen.put(q.id, i);
        Choice c = q.choices.get(i);
        ChoiceOutcome out = new ChoiceOutcome(q, c);

        // Zustand VOR den Effekten festhalten
        double m0 = gs.getMoney(), rep0 = gs.getReputation(), feed0 = gs.getFeed(),
               water0 = gs.getWater(), army0 = gs.getArmy(), shrimp0 = gs.getShrimpTotal(),
               tariff0 = gs.getExportTariff();
        int rival0 = rival;
        Set<String> flags0 = new HashSet<>(gs.unlockedFlags());
        int log0 = gs.getLog().size();

        for (QuestEffect e : c.effects) e.apply(gs, this);

        // Deltas -> Zeilen
        delta(out, gs.getMoney() - m0, "Geld");
        delta(out, gs.getReputation() - rep0, "Reputation");
        delta(out, gs.getFeed() - feed0, "Futter");
        delta(out, gs.getWater() - water0, "Wasser");
        delta(out, gs.getArmy() - army0, "Armee-Stärke");
        double ds = gs.getShrimpTotal() - shrimp0;
        if (Math.abs(ds) >= 1) delta(out, ds, "Shrimps");
        if (rival != rival0)
            out.add("Rivalität mit Akwanov " + signed(rival - rival0) + "  (jetzt " + rival + "/100)",
                rival > rival0 ? ChoiceOutcome.BAD : ChoiceOutcome.GOOD);
        double tariff = gs.getExportTariff();
        if (Math.abs(tariff - tariff0) > 0.001)
            out.add(tariff <= 0.001 ? "Embargo aufgehoben - wieder voller Verkaufspreis!"
                    : "Embargo: -" + Math.round(tariff * 100) + "% auf alle Marktpreise",
                tariff > tariff0 ? ChoiceOutcome.BAD : ChoiceOutcome.GOOD);
        for (String f : gs.unlockedFlags())
            if (!flags0.contains(f)) {
                String label = QuestTree.unlockLabel(f);
                if (label != null) out.add("FREIGESCHALTET: " + label, ChoiceOutcome.UNLOCK);
            }
        // Meldungen, die Effekte direkt geloggt haben (z.B. Kampf: SIEG/NIEDERLAGE)
        List<GameState.LogLine> lg = gs.getLog();
        for (int k = log0; k < lg.size(); k++)
            if (lg.get(k).kind != GameState.LOG_INFO)
                out.add(lg.get(k).text, lg.get(k).kind == GameState.LOG_GOOD ? ChoiceOutcome.GOOD : ChoiceOutcome.BAD);

        if (c.resultText != null && !c.resultText.isEmpty())
            gs.log("[" + q.giverName() + "] " + c.resultText, GameState.LOG_INFO);
        if (c.nextQuestId != null) {
            forceStart(c.nextQuestId);
            Quest nx = byId.get(c.nextQuestId);
            if (nx != null && armed.containsKey(nx.id) && nx.objectiveText != null) {
                // Den Hinweis aufs Quest-Log nur beim ersten Mal mitschreiben, sonst nervt er.
                String hint = hintedQuestLog ? "" : "  (im Quest-Log oben rechts)";
                hintedQuestLog = true;
                out.add("Neue Aufgabe: " + nx.objectiveText + hint, ChoiceOutcome.TASK);
            }
            else if (nx != null && triggered.contains(nx.id) && !done.contains(nx.id))
                out.add("Es geht sofort weiter ...", ChoiceOutcome.TASK);
        }
        return out;
    }

    private static void delta(ChoiceOutcome out, double d, String name) {
        if (Math.abs(d) < 0.5) return;
        out.add(name + " " + signed(d), d > 0 ? ChoiceOutcome.GOOD : ChoiceOutcome.BAD);
    }
    private static String signed(double v) {
        long r = Math.round(v);
        return (r >= 0 ? "+" : "") + String.format("%,d", r);
    }

    /** Startet eine (Ketten-)Quest: mit Ziel -> armieren (wartet); ohne Ziel -> sofort anzeigen. */
    public void forceStart(String id) {
        Quest q = byId.get(id);
        if (q == null || triggered.contains(id) || armed.containsKey(id)) return;
        if (q.objective != null) armed.put(id, q);
        else { triggered.add(id); pending.add(q); if (q.ending) lastEnding = q.title; }
    }

    // --- Minigames: eine Quest-Option kann ein Minigame anfordern; das GameFrame
    //     startet es, sobald die Ergebnis-Karte weggeklickt ist. ---
    private String pendingMinigame;
    private double pendingMinigameStake = 1;
    public void requestMinigame(String id, double stake) { pendingMinigame = id; pendingMinigameStake = stake; }
    /** Liefert {id, stake} genau einmal, sonst null. */
    public String[] pollMinigame() {
        if (pendingMinigame == null) return null;
        String[] r = { pendingMinigame, String.valueOf(pendingMinigameStake) };
        pendingMinigame = null;
        return r;
    }

    public void setFlag(String f) { flags.add(f); }
    public boolean hasFlag(String f) { return flags.contains(f); }
    public boolean isDone(String id) { return done.contains(id); }
    public boolean isTriggered(String id) { return triggered.contains(id); }
    public boolean isArmed(String id) { return armed.containsKey(id); }
    /** Gewählte Option einer erledigten Quest (-1 = noch keine Wahl getroffen). */
    public int chosenChoice(String id) { return chosen.getOrDefault(id, -1); }
    /** Kompletter Quest-Katalog (für den Quest-Baum). */
    public List<Quest> allQuests() { return java.util.Collections.unmodifiableList(all); }
    public void addRival(double d) { rival = (int) Math.max(0, Math.min(100, rival + d)); }
    public int getRival() { return rival; }
    public boolean rivalActive() { return triggered.contains("akwanov_intro"); }
}
