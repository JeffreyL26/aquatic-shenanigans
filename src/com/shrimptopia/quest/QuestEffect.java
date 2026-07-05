package com.shrimptopia.quest;

import com.shrimptopia.model.GameState;
import com.shrimptopia.model.ShrimpTier;

/** Konsequenz einer Quest-Auswahl. */
public class QuestEffect {
    public enum Type { MONEY, REP, FEED, WATER, SHRIMP, MULT_SHRIMP, UNLOCK, GRANT_FLAG, START_QUEST, TARIFF, RIVAL, BATTLE, MINIGAME }

    public final Type type;
    public double amount;
    public double amount2;   // BATTLE: Belohnung/Einsatz
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
    public static QuestEffect battle(double threat, double reward) { QuestEffect e = new QuestEffect(Type.BATTLE); e.amount = threat; e.amount2 = reward; return e; }
    /** Startet nach der Ergebnis-Karte ein Minigame; stake skaliert die Belohnung. */
    public static QuestEffect minigame(String id, double stake) { QuestEffect e = new QuestEffect(Type.MINIGAME); e.key = id; e.amount = stake; return e; }

    public void apply(GameState gs, QuestSystem qs) {
        switch (type) {
            case MONEY -> gs.addMoney(amount);
            case REP -> gs.addReputation(amount);
            case FEED -> gs.addFeed(amount);
            case WATER -> gs.addWater(amount);
            case SHRIMP -> gs.addShrimp(tier == null ? ShrimpTier.STANDARD : tier, amount);
            case MULT_SHRIMP -> gs.multShrimp(amount);
            case UNLOCK -> gs.unlock(key, null);
            // Flag in BEIDEN Welten setzen: Quest-Bedingungen lesen qs, Upgrade-/Modus-Sperren
            // (z.B. Krillkill-Bootcamp) lesen gs.isUnlocked - sonst bleibt das Upgrade ewig zu.
            case GRANT_FLAG -> { qs.setFlag(key); gs.unlock(key, null); }
            case START_QUEST -> qs.forceStart(key);
            case TARIFF -> gs.setExportTariff(amount);
            case RIVAL -> qs.addRival(amount);
            case MINIGAME -> qs.requestMinigame(key, amount <= 0 ? 1 : amount);
            case BATTLE -> {
                double army = gs.getArmy();
                double ratio = amount <= 0 ? 2 : army / amount;
                if (ratio >= 1.2) {
                    gs.addMoney(amount2); gs.addReputation(5); gs.addArmy(-amount * 0.1);
                    gs.log("SIEG! Armee-Stärke " + (int) army + " schlägt Bedrohung " + (int) amount + ".", GameState.LOG_GOOD);
                } else if (ratio >= 0.85) {
                    gs.addMoney(amount2 * 0.4); gs.addReputation(1); gs.addArmy(-amount * 0.2);
                    gs.log("Teilerfolg gegen Bedrohung " + (int) amount + " (Armee " + (int) army + ").", GameState.LOG_INFO);
                } else {
                    gs.addMoney(-amount2 * 0.6); gs.addReputation(-6); gs.addArmy(-amount * 0.3); gs.multShrimp(0.9);
                    gs.log("NIEDERLAGE! Armee " + (int) army + " war zu schwach (Bedrohung " + (int) amount + ").", GameState.LOG_BAD);
                }
            }
        }
    }
}
