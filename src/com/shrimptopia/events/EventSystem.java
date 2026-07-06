package com.shrimptopia.events;

import com.shrimptopia.model.Building;
import com.shrimptopia.model.GameState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hält den Katalog der Zufallsereignisse und entscheidet, wann eines feuert.
 * Ereignisse können nur alle paar Tage und mit einer gewissen Wahrscheinlichkeit auftreten.
 * Die Garagen-Phase bleibt ereignisfrei; einzelne Ereignisse setzen zusätzlich passende
 * Voraussetzungen voraus (z.B. ein Becken, damit keines "auslaufen" kann, das es nicht gibt).
 */
public class EventSystem {

    private final List<GameEvent> catalog = new ArrayList<>();
    private final Random rng;
    private int lastEventDay = -100;
    private static final int MIN_GAP = 5;          // Tage Mindestabstand
    private static final double CHANCE = 0.17;     // Wahrscheinlichkeit pro Tick (nach Cooldown)

    public EventSystem(Random rng) {
        this.rng = rng;
        buildCatalog();
    }

    private void buildCatalog() {
        add("Shrimp-Influencer zu Besuch",
            "Ein Shrimp-Influencer filmt deine Becken für 2 Mio. Follower. #ShrimpLife trendet.",
            GameEvent.Kind.GOOD, null,
            gs -> gs.addReputation(12));

        add("Veganer-Demo vor dem Werkstor",
            "\"Krabben sind auch nur Menschen!\" steht auf den Schildern. Die Presse ist da.",
            GameEvent.Kind.BAD, null,
            gs -> gs.addReputation(-9));

        add("Praktikant verwechselt die Schalter",
            "Jemand hielt den Notausschalter für den Lichtschalter. Kurzer Blackout, halbes Becken ausgelaufen.",
            GameEvent.Kind.BAD, gs -> hasTank(gs) && gs.getPowerUsed() > 0,
            gs -> { gs.multWater(0.75); gs.addReputation(-2); });

        add("Algenblüte!",
            "Die Algenfarm läuft heiß: unerwartet fette Ernte. Das Futterlager quillt über.",
            GameEvent.Kind.GOOD, EventSystem::hasAlgae,
            gs -> gs.addFeed(40));

        add("Gourmet-Kritiker incognito",
            "Ein gefürchteter Kritiker probiert deine Shrimps. Sein Urteil hängt an deinem Ruf...",
            GameEvent.Kind.NEUTRAL, null,
            gs -> {
                if (gs.getReputation() > 55) { gs.addMoney(1800); gs.addReputation(6); }
                else { gs.addMoney(-600); gs.addReputation(-4); }
            });

        add("Shrimp-Gewerkschaft gegründet",
            "Deine Shrimps... äh, Arbeiter fordern bessere Konditionen. Einmalige Sonderzahlung fällig.",
            GameEvent.Kind.BAD, gs -> gs.getMoney() > 1500,
            gs -> gs.addMoney(-900));

        add("Finanzamt klopft an",
            "Steuerprüfung. Sie haben den Posten 'Garnelen-Spa' nicht ganz verstanden.",
            GameEvent.Kind.BAD, gs -> gs.getMoney() > 2000,
            gs -> gs.addMoney(-1300));

        add("TV-Doku über Indoor-Shrimp-Farming",
            "Das Fernsehen widmet dir eine ganze Doku. Über Nacht kennt dich die halbe Republik.",
            GameEvent.Kind.GOOD, null,
            gs -> { gs.addReputation(15); gs.addMoney(2200); });

        add("Wasserrohrbruch in Halle 3",
            "Ein Rohr gibt auf. Die Halfte des Wasservorrats versickert im Hallenboden.",
            GameEvent.Kind.BAD, gs -> gs.getWater() > 40,
            gs -> gs.multWater(0.5));

        add("Futter-Sonderangebot beim Großhändler",
            "Plankton im Mega-Sparpack. Du schlägst zu, das Lager freut sich.",
            GameEvent.Kind.GOOD, null,
            gs -> gs.addFeed(35));

        add("Shrimp-Babyboom",
            "Romantische Beleuchtung im Becken zeigt Wirkung. Die Population explodiert (im guten Sinne).",
            GameEvent.Kind.GOOD, gs -> hasTank(gs) && gs.getShrimp() > 5,
            gs -> gs.multShrimp(1.25));

        add("Krankheit im Becken",
            "Eine mysteriöse Krankheit geht um. Quarantäne! Ein Teil des Bestands ist verloren.",
            GameEvent.Kind.BAD, gs -> gs.getShrimp() > 10,
            gs -> gs.multShrimp(0.7));

        add("Investor steigt ein",
            "Ein Risikokapitalgeber liebt 'Shrimp-as-a-Service'. Frisches Geld, aber er redet jetzt mit.",
            GameEvent.Kind.NEUTRAL, null,
            gs -> { gs.addMoney(5000); gs.addReputation(-5); });

        add("Dein Meme geht viral",
            "'Indoor Shrimp Farming' ist plötzlich überall. Du bist der Meme-König der Aquakultur.",
            GameEvent.Kind.GOOD, null,
            gs -> gs.addReputation(20));

        add("Stromnetz-Bonus",
            "Du speist Überschuss-Strom ins Netz zurück. Der Versorger überweist dir eine hübsche Prämie.",
            GameEvent.Kind.GOOD, gs -> gs.getPowerProduced() > gs.getPowerUsed(),
            gs -> gs.addMoney(1500));

        add("Möwen-Einbruch",
            "Eine Möwenbande hat ein Oberlicht entdeckt. Buffet eröffnet. Bestand dezimiert.",
            GameEvent.Kind.BAD, gs -> gs.getShrimp() > 15,
            gs -> { gs.multShrimp(0.85); gs.addReputation(-2); });
    }

    private void add(String title, String text, GameEvent.Kind kind,
                     java.util.function.Predicate<GameState> cond,
                     java.util.function.Consumer<GameState> effect) {
        catalog.add(new GameEvent(title, text, kind, cond, effect));
    }

    private static boolean hasTank(GameState gs) {
        for (Building b : gs.buildings()) if (b.type.isTank()) return true;
        return false;
    }

    private static boolean hasAlgae(GameState gs) {
        for (Building b : gs.buildings()) if (b.type.isAlgae()) return true;
        return false;
    }

    /** Pro Tick aufgerufen: feuert evtl. ein passendes Zufallsereignis. */
    public void maybeTrigger(GameState gs) {
        // Garagen-Phase bleibt ereignisfrei: Kein Werkstor, keine Halle 3, keine Presse -
        // Zufallsereignisse starten erst mit dem Hof-Ausbau (dem Ausstieg aus der Garage).
        if (!gs.isUnlocked("era.HOF")) return;
        if (gs.getDay() - lastEventDay < MIN_GAP) return;
        if (rng.nextDouble() > CHANCE) return;

        // Bis zu ein paar Versuche, ein erlaubtes Ereignis zu finden.
        for (int attempt = 0; attempt < 6; attempt++) {
            GameEvent e = catalog.get(rng.nextInt(catalog.size()));
            if (e.canTrigger(gs)) {
                e.apply(gs);
                lastEventDay = gs.getDay();
                int logKind = switch (e.kind) {
                    case GOOD -> GameState.LOG_GOOD;
                    case BAD  -> GameState.LOG_BAD;
                    default   -> GameState.LOG_INFO;
                };
                gs.log("EREIGNIS - " + e.title + ": " + e.text, logKind);
                return;
            }
        }
    }
}
