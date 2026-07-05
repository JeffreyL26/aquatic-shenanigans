package com.shrimptopia.ui.minigames;

import com.shrimptopia.model.GameState;
import com.shrimptopia.model.ShrimpTier;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registry aller Minigames: id -> Spiel-Fabrik + Maximal-Belohnung.
 * Die tatsächliche Belohnung skaliert linear mit dem Rating (0..1) und dem
 * Quest-Einsatz (stake) - Können zahlt sich aus, hohe Einsätze erst recht.
 */
public final class MinigameCatalog {

    /** Maximal-Belohnung bei Rating 1.0 und stake 1.0. */
    public record Entry(String id, Supplier<Minigame> factory,
                        double money, double rep, double shrimp) { }

    private static final Entry[] ENTRIES = {
        new Entry("shooter", ShooterGame::new, 1600, 6, 0),
        new Entry("scrub",   ScrubGame::new,    350, 8, 0),
        new Entry("fuse",    FuseGame::new,     550, 4, 8),
        new Entry("rhythm",  RhythmGame::new,   800, 10, 0),
        new Entry("haggle",  HaggleGame::new,  2400, 2, 0),
        new Entry("sort",    SortGame::new,     900, 6, 0),
    };

    private MinigameCatalog() {}

    public static Entry get(String id) {
        for (Entry e : ENTRIES) if (e.id().equals(id)) return e;
        return null;
    }

    /** Wendet die Belohnung auf den Spielstand an und liefert die Ergebnis-Zeilen. */
    public static void applyReward(GameState gs, Entry e, double rating, double stake, List<String> out) {
        long money = Math.round(e.money() * rating * stake);
        long rep = Math.round(e.rep() * rating * stake);
        long shrimp = Math.round(e.shrimp() * rating * stake);
        if (money != 0) { gs.addMoney(money); out.add("Geld +" + String.format("%,d", money)); }
        if (rep != 0)   { gs.addReputation(rep); out.add("Reputation +" + rep); }
        if (shrimp != 0){ gs.addShrimp(ShrimpTier.STANDARD, shrimp); out.add("Gerettete Shrimps +" + shrimp); }
        if (out.isEmpty()) out.add("Keine Belohnung - nächstes Mal klappt's!");
    }
}
