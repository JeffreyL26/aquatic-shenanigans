package com.shrimptopia.ui.minigames;

import java.awt.Graphics2D;
import java.util.Random;

/**
 * Basis aller Quest-Minigames: ein kurzes, in sich geschlossenes Geschicklichkeitsspiel
 * (10-25 Sekunden), das im MinigamePanel läuft. Das Ergebnis ist ein Rating 0..1
 * (score/target), das die Belohnung skaliert - Können zahlt sich aus.
 */
public abstract class Minigame {

    protected int w, h;              // Arena-Größe in Pixeln
    protected Random rng;
    protected double stake = 1;      // Einsatz-Multiplikator aus der Quest (skaliert die Belohnung)
    protected double timeLeft, duration;
    protected double score, target;  // rating = clamp01(score / target)
    protected boolean done;

    public final void start(int w, int h, Random rng, double stake) {
        this.w = w; this.h = h; this.rng = rng; this.stake = stake;
        this.score = 0; this.done = false;
        init();
        this.timeLeft = duration;
    }

    /** Setzt duration/target und baut den Anfangszustand auf. */
    protected abstract void init();

    public final void update(double dt) {
        if (done) return;
        timeLeft -= dt;
        if (timeLeft <= 0) { timeLeft = 0; done = true; return; }
        onUpdate(dt);
    }

    protected abstract void onUpdate(double dt);
    public abstract void render(Graphics2D g);

    // --- Eingaben (Arena-Koordinaten); Spiele überschreiben, was sie brauchen ---
    public void press(int x, int y) { }
    public void move(int x, int y) { }
    public void drag(int x, int y) { move(x, y); }
    /** Lane-Taste A/S/D/F -> 0..3; Aktionstaste (LEER) -> -1. */
    public void lane(int idx) { }

    public boolean isDone() { return done; }
    protected void finish() { done = true; }

    public double rating() { return Math.max(0, Math.min(1, target <= 0 ? 0 : score / target)); }
    public double timeLeft() { return timeLeft; }
    public double duration() { return duration; }
    public String scoreLabel() { return Math.round(score) + " / " + Math.round(target); }

    public abstract String title();
    public abstract String subtitle();
    /** Kurze Anleitung für den Intro-Bildschirm (eine Mechanik-Zeile pro Eintrag). */
    public abstract String[] howTo();
}
