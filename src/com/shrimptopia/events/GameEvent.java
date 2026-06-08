package com.shrimptopia.events;

import com.shrimptopia.model.GameState;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Ein humorvolles Zufallsereignis mit Effekt auf den Spielzustand. */
public class GameEvent {

    public enum Kind { GOOD, BAD, NEUTRAL }

    public final String title;
    public final String text;
    public final Kind kind;
    private final Consumer<GameState> effect;
    private final Predicate<GameState> condition; // wann das Ereignis überhaupt auftreten darf

    public GameEvent(String title, String text, Kind kind,
                     Predicate<GameState> condition, Consumer<GameState> effect) {
        this.title = title;
        this.text = text;
        this.kind = kind;
        this.condition = condition;
        this.effect = effect;
    }

    public boolean canTrigger(GameState gs) {
        return condition == null || condition.test(gs);
    }

    public void apply(GameState gs) {
        if (effect != null) effect.accept(gs);
    }
}
