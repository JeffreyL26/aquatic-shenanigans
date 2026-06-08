package com.shrimptopia.quest;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.GameState;
import java.util.Arrays;
import java.util.List;

/** Auslöse-Bedingung einer Quest. */
public class Condition {
    public enum Type { ALWAYS, NEVER, DAY, MONEY, REP, SHRIMP_PRODUCED, BUILD_COUNT, UNLOCK, FLAG, QUEST_DONE, RIVAL, ALL, ANY }

    public final Type type;
    public double value;
    public BuildingType building;
    public String key;
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
            case BUILD_COUNT -> gs.countBuildings(building) >= value;
            case UNLOCK -> gs.isUnlocked(key);
            case FLAG -> qs.hasFlag(key);
            case QUEST_DONE -> qs.isDone(key);
            case RIVAL -> qs.getRival() >= value;
            case ALL -> { for (Condition c : subs) if (!c.test(gs, qs)) yield false; yield true; }
            case ANY -> { for (Condition c : subs) if (c.test(gs, qs)) yield true; yield false; }
        };
    }
}
