package com.shrimptopia.quest;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.ShrimpTier;
import java.util.Arrays;
import java.util.List;

/** Auslöse-/Ziel-Bedingung einer Quest (mit Fortschritts-Anzeige fürs Quest-Log). */
public class Condition {
    public enum Type { ALWAYS, NEVER, DAY, MONEY, REP, SHRIMP_PRODUCED, TIER_PRODUCED, SOLD,
        RESOURCE, ARMY, BUILD_COUNT, UNLOCK, FLAG, QUEST_DONE, RIVAL, ALL, ANY }

    public final Type type;
    public double value;
    public BuildingType building;
    public String key;
    public ShrimpTier tier;
    public List<Condition> subs;

    private Condition(Type type) { this.type = type; }

    public static Condition all(Condition... cs) { Condition c = new Condition(Type.ALL); c.subs = Arrays.asList(cs); return c; }
    public static Condition any(Condition... cs) { Condition c = new Condition(Type.ANY); c.subs = Arrays.asList(cs); return c; }

    public static Condition always() { return new Condition(Type.ALWAYS); }
    public static Condition never()  { return new Condition(Type.NEVER); }
    public static Condition day(int d)            { Condition c = new Condition(Type.DAY); c.value = d; return c; }
    public static Condition money(double m)       { Condition c = new Condition(Type.MONEY); c.value = m; return c; }
    public static Condition rep(double r)         { Condition c = new Condition(Type.REP); c.value = r; return c; }
    public static Condition shrimpProduced(double s){ Condition c = new Condition(Type.SHRIMP_PRODUCED); c.value = s; return c; }
    public static Condition tierProduced(ShrimpTier t, double n) { Condition c = new Condition(Type.TIER_PRODUCED); c.tier = t; c.value = n; return c; }
    public static Condition sold(double n)        { Condition c = new Condition(Type.SOLD); c.value = n; return c; }
    public static Condition resource(String key, double n) { Condition c = new Condition(Type.RESOURCE); c.key = key; c.value = n; return c; }
    public static Condition army(double n)        { Condition c = new Condition(Type.ARMY); c.value = n; return c; }
    public static Condition buildCount(BuildingType t, int n) { Condition c = new Condition(Type.BUILD_COUNT); c.building = t; c.value = n; return c; }
    public static Condition unlock(String flag)   { Condition c = new Condition(Type.UNLOCK); c.key = flag; return c; }
    public static Condition flag(String f)        { Condition c = new Condition(Type.FLAG); c.key = f; return c; }
    public static Condition questDone(String id)  { Condition c = new Condition(Type.QUEST_DONE); c.key = id; return c; }
    public static Condition rival(double r)        { Condition c = new Condition(Type.RIVAL); c.value = r; return c; }

    public boolean test(GameState gs, QuestSystem qs) {
        return switch (type) {
            case ALWAYS -> true;
            case NEVER -> false;
            case DAY -> gs.getDay() >= value;
            case MONEY -> gs.getMoney() >= value;
            case REP -> gs.getReputation() >= value;
            case SHRIMP_PRODUCED -> gs.getTotalShrimpProduced() >= value;
            case TIER_PRODUCED -> gs.getProducedByTier(tier) >= value;
            case SOLD -> gs.getTotalSold() >= value;
            case RESOURCE -> gs.getResource(key) >= value;
            case ARMY -> gs.getArmy() >= value;
            case BUILD_COUNT -> gs.countBuildings(building) >= value;
            case UNLOCK -> gs.isUnlocked(key);
            case FLAG -> qs.hasFlag(key);
            case QUEST_DONE -> qs.isDone(key);
            case RIVAL -> qs.getRival() >= value;
            case ALL -> { for (Condition c : subs) if (!c.test(gs, qs)) yield false; yield true; }
            case ANY -> { for (Condition c : subs) if (c.test(gs, qs)) yield true; yield false; }
        };
    }

    /** Aktueller Zahlenwert dieser Bedingung (fuer Fortschritt). */
    private double current(GameState gs, QuestSystem qs) {
        return switch (type) {
            case DAY -> gs.getDay();
            case MONEY -> gs.getMoney();
            case REP -> gs.getReputation();
            case SHRIMP_PRODUCED -> gs.getTotalShrimpProduced();
            case TIER_PRODUCED -> gs.getProducedByTier(tier);
            case SOLD -> gs.getTotalSold();
            case RESOURCE -> gs.getResource(key);
            case ARMY -> gs.getArmy();
            case BUILD_COUNT -> gs.countBuildings(building);
            case RIVAL -> qs.getRival();
            default -> test(gs, qs) ? 1 : 0;
        };
    }

    /** Fortschritt 0..1 fuers Quest-Log. */
    public double progress(GameState gs, QuestSystem qs) {
        if (type == Type.ALL) { double m = 1; for (Condition c : subs) m = Math.min(m, c.progress(gs, qs)); return m; }
        if (type == Type.ANY) { double m = 0; for (Condition c : subs) m = Math.max(m, c.progress(gs, qs)); return m; }
        if (value <= 0) return test(gs, qs) ? 1 : 0;
        return Math.max(0, Math.min(1, current(gs, qs) / value));
    }

    /** Kurzbeschreibung "aktuell / Ziel" fuers Quest-Log. */
    public String describe(GameState gs, QuestSystem qs) {
        if (type == Type.ALL || type == Type.ANY) {
            int done = 0; for (Condition c : subs) if (c.test(gs, qs)) done++;
            return done + "/" + subs.size() + " Teilziele";
        }
        if (value > 0) return fmt(current(gs, qs)) + " / " + fmt(value);
        return test(gs, qs) ? "erledigt" : "offen";
    }

    private static String fmt(double v) {
        if (v >= 1000) return String.format("%,d", Math.round(v));
        return String.valueOf(Math.round(v));
    }
}
