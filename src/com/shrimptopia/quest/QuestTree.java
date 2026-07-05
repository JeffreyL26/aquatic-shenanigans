package com.shrimptopia.quest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Statische Struktur hinter dem Quest-Baum-Menü: gruppiert den Quest-Katalog in farbcodierte
 * Handlungsstränge (Lanes), berechnet Querverweise zwischen Strängen (Überschneidungen) und
 * indiziert, welche Quest-Option welche Gebäude/Tiers/Zonen/Modi freischaltet.
 */
public final class QuestTree {

    private QuestTree() {}

    /** Ein Handlungsstrang: geordnete Quest-Kette mit eigener Farbe. */
    public static final class Line {
        public final String id, name;
        public final Color color;
        public final List<String> quests;
        Line(String id, String name, Color color, String... quests) {
            this.id = id; this.name = name; this.color = color; this.quests = List.of(quests);
        }
    }

    /** Wo ein Unlock-Key herkommt: Quest + Optionsindex. */
    public static final class UnlockSource {
        public final String questId; public final int choiceIndex;
        UnlockSource(String questId, int choiceIndex) { this.questId = questId; this.choiceIndex = choiceIndex; }
    }

    private static final List<Line> LINES = List.of(
        new Line("behoerden",  "Behörden-Dschungel",       new Color(120, 160, 220),
            "beh_formular", "beh_pruefung", "beh_plakette"),
        new Line("tierschutz", "Tierschutz-Saga",          new Color(255, 130, 200),
            "tier_wuerde", "tier_demo", "tier_gewerkschaft"),
        new Line("influencer", "Influencer-Aufstieg",      new Color(184, 162, 255),
            "inf_viral", "inf_sponsor", "inf_cancel"),
        new Line("krabbo",     "Krabbo-Fehde",             new Color(255, 159, 67),
            "konk_krabbo", "konk_angebot"),
        new Line("kyle",       "Kyle aus dem Keller",      new Color(255, 84, 32),
            "kyle_intro", "kyle_1", "kyle_2", "kyle_3", "kyle_4", "kyle_5"),
        new Line("marketing",  "Marketing-Offensive",      new Color(150, 220, 120),
            "mark_intro", "mark_social", "mark_agency"),
        new Line("boygroup",   "New Krills on the Block",  new Color(255, 120, 190),
            "boy_intro", "boy_casting", "boy_training", "boy_debut", "boy_scandal", "boy_finale"),
        new Line("krillkill",  "Operation Protein-Sturm",  new Color(150, 165, 90),
            "krillkill_intro", "krillkill_1", "krillkill_2", "krillkill_3",
            "krillkill_4", "krillkill_5", "krillkill_6", "krillkill_7"),
        new Line("akwanov",    "Akwanov-Rivalität",        new Color(90, 170, 150),
            "akwanov_intro", "akwanov_1", "akwanov_2", "akwanov_3",
            "akwanov_4", "akwanov_5", "akwanov_6", "akwanov_7", "akwanov_8"),
        new Line("boost",      "Der Becken-3-Vorfall",     new Color(40, 224, 220),
            "boost_1", "boost_2", "boost_3", "boost_4", "boost_5", "boost_6",
            "sts6_dmitri", "sts6_taschkent", "union_akwanov"),
        new Line("konflikte",  "Konflikte & Kriege",       new Color(235, 90, 90),
            "conf_border", "conf_flotilla", "conf_raid", "conf_siege", "conf_war"),
        new Line("einzel",     "Einzelquests",             new Color(0, 199, 183),
            "era_halle", "kat_blackout", "kat_algen", "gourmet_darm", "presse_kritiker", "kat_moewen",
            "beh_steuer", "idee_spa", "geld_investor", "dmitri_zeugnis", "dmitri_kantine"),
        new Line("enden",      "Spielenden",               new Color(255, 205, 86),
            "end_imperator", "end_vertical", "end_protein", "end_union", "end_saint", "end_meme"));

    private static final Map<String, Line> LINE_OF = new HashMap<>();
    static { for (Line l : LINES) for (String q : l.quests) LINE_OF.put(q, l); }

    public static List<Line> lines() { return LINES; }
    public static Line lineOf(String questId) { return LINE_OF.get(questId); }

    /** Anzeigename für Unlock-Keys, die per Quest freigeschaltet werden. */
    private static final Map<String, String> UNLOCK_LABEL = new LinkedHashMap<>();
    static {
        UNLOCK_LABEL.put("era.HALLE",          "Hallen-Upgrade: große Gebäude (Kraftwerk, Becken, Börse ...)");
        UNLOCK_LABEL.put("mkt.radio",          "Marketing: Lokalradio-Spot");
        UNLOCK_LABEL.put("mkt.social",         "Marketing: Social-Media-Kanal");
        UNLOCK_LABEL.put("mkt.tube",           "Marketing: ShrimpTube-Kanal");
        UNLOCK_LABEL.put("mkt.billboard",      "Marketing: Autobahn-Plakat");
        UNLOCK_LABEL.put("mkt.tv",             "Marketing: TV-Spot 'Shrimp your day!'");
        UNLOCK_LABEL.put("build.stage",        "Gebäude: Show-Bühne (Empfang & Garten)");
        UNLOCK_LABEL.put("mkt.boyband",        "Marketing: Subliminal-Pop (New Krills on the Block)");
        UNLOCK_LABEL.put("tier.BIO",           "Bio-Shrimp (Tier + Becken-Modus)");
        UNLOCK_LABEL.put("build.gut_station",  "Gebäude: Darmentleerungsanlage (Voraussetzung für Gourmet)");
        UNLOCK_LABEL.put("build.waste_plant",  "Gebäude: Biogas-Kläranlage (entsorgt Klärschlamm)");
        UNLOCK_LABEL.put("tier.GOURMET",       "Gourmet-Shrimp (Tier + Becken-Modus)");
        UNLOCK_LABEL.put("tier.PROTEIN",       "Protein-Bombe (Tier + Becken-Modus)");
        UNLOCK_LABEL.put("tier.WARKRILL",      "Kampf-Krill (Tier, Modus + Kaserne)");
        UNLOCK_LABEL.put("zone.LOGISTIK",      "Zone: Logistik & Export");
        UNLOCK_LABEL.put("build.military",     "Gebäude: Militär-Depot");
        UNLOCK_LABEL.put("build.blackmarket",  "Gebäude: Schwarzmarkt");
        UNLOCK_LABEL.put("krillkill.bootcamp", "Upgrade: Krillkill-Bootcamp (Wohncontainer)");
        UNLOCK_LABEL.put("sts6",               "Shrimp Team Six - Einsätze im HQ-Kommando");
        UNLOCK_LABEL.put("shrimp_union",       "Gewerkschafts-Edikte (Betriebsrat / Überwachung) im HQ-Kommando");
    }
    /** Keys, die indirekt an einem Quest-Unlock hängen (z.B. Kaserne via Kampf-Krill). */
    private static final Map<String, String> UNLOCK_ALIAS = Map.of(
        "build.barracks", "tier.WARKRILL",
        "worker.bootcamp", "krillkill.bootcamp");

    /** Freischalt-Hinweise für Meilenstein-Flags (nicht per Quest, sondern über Fortschritt). */
    private static final Map<String, String> MILESTONE_HINT = Map.of(
        "era.HALLE",          "Ab dem Hallen-Ausbau (raus aus der Garage)",
        "build.shrimpboost",  "Ab der SHRIMPBOOST-Fabrik (Forschung, ~18.000 Geld)",
        "build.robotworks",   "Ab dem Garnelen-Roboter-Werk (Logistik, ~40.000 Geld)",
        "build.plankton",     "Meilenstein: Hallen-Betrieb & ~20.000 Geld",
        "build.genlab",       "Meilenstein: Forschungsflügel & ~22.000 Geld",
        "build.megatank",     "Meilenstein: Hallen-Betrieb & ~35.000 Geld",
        "build.geo",          "Meilenstein: Hallen-Betrieb & ~55.000 Geld");

    public static String unlockLabel(String key) { return UNLOCK_LABEL.get(key); }
    public static Set<String> knownUnlockKeys() { return UNLOCK_LABEL.keySet(); }

    /** key -> alle Quest-Optionen, die ihn freischalten (UNLOCK- und GRANT_FLAG-Effekte). */
    public static Map<String, List<UnlockSource>> unlockSources(QuestSystem qs) {
        Map<String, List<UnlockSource>> out = new LinkedHashMap<>();
        for (Quest q : qs.allQuests())
            for (int i = 0; i < q.choices.size(); i++)
                for (QuestEffect e : q.choices.get(i).effects)
                    if (e.type == QuestEffect.Type.UNLOCK || e.type == QuestEffect.Type.GRANT_FLAG)
                        out.computeIfAbsent(e.key, k -> new ArrayList<>()).add(new UnlockSource(q.id, i));
        return out;
    }

    /** flag -> Quest, deren Option das Flag setzt/freischaltet (für Querverweise). */
    public static String flagGrantedBy(String flag, QuestSystem qs) {
        for (Quest q : qs.allQuests())
            for (Choice c : q.choices)
                for (QuestEffect e : c.effects)
                    if ((e.type == QuestEffect.Type.GRANT_FLAG || e.type == QuestEffect.Type.UNLOCK)
                        && flag.equals(e.key)) return q.id;
        return null;
    }

    /**
     * Quests aus ANDEREN Strängen, auf die diese Quest per Auslöser verweist
     * (questDone, Flags anderer Stränge, Rivalitäts-Level -> Akwanov-Strang).
     */
    public static Set<String> crossRefs(Quest q, QuestSystem qs) {
        Set<String> refs = new LinkedHashSet<>();
        collectRefs(q.trigger, refs, qs);
        collectRefs(q.objective, refs, qs);
        Line mine = lineOf(q.id);
        refs.removeIf(id -> {
            Line l = lineOf(id);
            return l == null || l == mine || id.equals(q.id);
        });
        return refs;
    }

    private static void collectRefs(Condition c, Set<String> out, QuestSystem qs) {
        if (c == null) return;
        switch (c.type) {
            case QUEST_DONE -> out.add(c.key);
            case FLAG, UNLOCK -> { String g = flagGrantedBy(c.key, qs); if (g != null) out.add(g); }
            case RIVAL -> out.add("akwanov_intro");   // Rivalität entsteht im Akwanov-Strang
            case ALL, ANY -> { for (Condition s : c.subs) collectRefs(s, out, qs); }
            default -> { }
        }
    }

    /**
     * Menschlich lesbarer Freischalt-Hinweis für einen requiresFlag/unlockFlag:
     * "Quest 'Titel' (Strang) - Option '...'" oder null, wenn keine Quest den Key vergibt.
     */
    public static String unlockHintFor(String flag, QuestSystem qs) {
        if (flag == null) return null;
        String key = UNLOCK_ALIAS.getOrDefault(flag, flag);
        List<UnlockSource> src = unlockSources(qs).get(key);
        // Kein Quest-Geber? Dann evtl. ein Meilenstein-Flag (Fortschritt statt Quest).
        if (src == null || src.isEmpty()) return MILESTONE_HINT.get(key);
        // Nach Quest gruppieren: schalten ALLE Optionen einer Quest frei, reicht der Quest-Name.
        Map<String, List<UnlockSource>> byQuest = new LinkedHashMap<>();
        for (UnlockSource s : src) byQuest.computeIfAbsent(s.questId, k -> new ArrayList<>()).add(s);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<UnlockSource>> e : byQuest.entrySet()) {
            Quest q = qs.get(e.getKey());
            if (q == null) continue;
            if (sb.length() > 0) sb.append("  ODER  ");
            Line l = lineOf(q.id);
            sb.append("Quest \"").append(q.title).append("\"");
            if (l != null) sb.append(" (").append(l.name).append(")");
            if (e.getValue().size() < q.choices.size()) {
                sb.append(" - Option \"").append(q.choices.get(e.getValue().get(0).choiceIndex).text).append("\"");
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    /** Alles, was eine einzelne Option freischaltet (Labels), für Badges im Baum. */
    public static List<String> unlocksOfChoice(Choice c) {
        List<String> out = new ArrayList<>();
        for (QuestEffect e : c.effects)
            if ((e.type == QuestEffect.Type.UNLOCK || e.type == QuestEffect.Type.GRANT_FLAG)
                && UNLOCK_LABEL.containsKey(e.key))
                out.add(UNLOCK_LABEL.get(e.key));
        return out;
    }
}
