package com.shrimptopia.quest;

import com.shrimptopia.model.GameState;
import com.shrimptopia.model.ShrimpTier;

/** Konsequenz einer Quest-Auswahl. */
public class QuestEffect {
    public enum Type { MONEY, REP, FEED, WATER, SHRIMP, MULT_SHRIMP, UNLOCK, GRANT_FLAG, START_QUEST, TARIFF, RIVAL }

    public final Type type;
    public double amount;
    public ShrimpTier tier;
    public String key;

    private QuestEffect(Type type) { this.type = type; }

    public static QuestEffect money(double a) { QuestEffect e = new QuestEffect(Type.MONEY); e.amount = a; return e; }
    public static QuestEffect rep(double a)   { QuestEffect e = new QuestEffect(Type.REP); e.amount = a; return e; }
    public static QuestEffect feed(double a)  { QuestEffect e = new QuestEffect(Type.FEED); e.amount = a; return e; }
    public static QuestEffect water(double a) { QuestEffect e = new QuestEffect(Type.WATER); e.amount = a; return e; }
    public static QuestEffect shrimp(ShrimpTier t, double a) { QuestEffect e = new QuestEffect(Type.SHRIMP); e.tier = t; e.amount = a; return e; }
    public static QuestEffect multShrimp(double a) { QuestEffect e = new QuestEffect(Type.MULT_SHRIMP); e.amount = a; return e; }
    public static QuestEffect unlock(String flag)  { QuestEffect e = new QuestEffect(Type.UNLOCK); e.key = flag; return e; }
    public static QuestEffect grantFlag(String f)  { QuestEffect e = new QuestEffect(Type.GRANT_FLAG); e.key = f; return e; }
    public static QuestEffect startQuest(String id){ QuestEffect e = new QuestEffect(Type.START_QUEST); e.key = id; return e; }
    public static QuestEffect tariff(double t)      { QuestEffect e = new QuestEffect(Type.TARIFF); e.amount = t; return e; }
    public static QuestEffect rival(double r)       { QuestEffect e = new QuestEffect(Type.RIVAL); e.amount = r; return e; }

    public void apply(GameState gs, QuestSystem qs) {
        switch (type) {
            case MONEY -> gs.addMoney(amount);
            case REP -> gs.addReputation(amount);
            case FEED -> gs.addFeed(amount);
            case WATER -> gs.addWater(amount);
            case SHRIMP -> gs.addShrimp(tier == null ? ShrimpTier.STANDARD : tier, amount);
            case MULT_SHRIMP -> gs.multShrimp(amount);
            case UNLOCK -> gs.unlock(key, null);
            case GRANT_FLAG -> qs.setFlag(key);
            case START_QUEST -> qs.forceStart(key);
            case TARIFF -> gs.setExportTariff(amount);
            case RIVAL -> qs.addRival(amount);
        }
    }
}
