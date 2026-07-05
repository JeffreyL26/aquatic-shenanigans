package com.shrimptopia.ui.minigames;

import com.shrimptopia.ui.Palette;
import java.awt.*;

/**
 * "Feilsch-o-Mat" - Timing-Mechanik: Der Verhandlungszeiger pendelt über die Leiste.
 * KLICK (oder LEER) stoppt ihn - je näher an der Mitte, desto besser der Deal.
 * Fünf Runden, der Zeiger wird schneller, die grüne Zone schmaler.
 */
public class HaggleGame extends Minigame {

    private static final int ROUNDS = 5;
    private int round;
    private double phase;
    private double pauseT;
    private String verdict = "";
    private final int[] results = new int[ROUNDS];

    @Override protected void init() {
        duration = 30;               // weiche Obergrenze; endet nach 5 Runden
        target = ROUNDS * 3;         // 3 Punkte pro Runde maximal
        round = 0;
        phase = rng.nextDouble() * Math.PI;
    }

    private double speed() { return 2.2 + round * 0.7; }
    private double greenZone() { return 0.20 - round * 0.02; }

    @Override protected void onUpdate(double dt) {
        if (pauseT > 0) {
            pauseT -= dt;
            if (pauseT <= 0) {
                round++;
                if (round >= ROUNDS) finish();
                else phase = rng.nextDouble() * Math.PI;
            }
            return;
        }
        phase += dt * speed();
    }

    private void lock() {
        if (pauseT > 0 || done) return;
        double pos = Math.sin(phase);   // -1 .. 1
        double d = Math.abs(pos);
        int pts;
        if (d < 0.07) { pts = 3; verdict = "HANDSCHLAG! Bester Preis!"; }
        else if (d < greenZone()) { pts = 2; verdict = "Guter Deal."; }
        else if (d < 0.45) { pts = 1; verdict = "Na ja. Man nimmt, was man kriegt."; }
        else { pts = 0; verdict = "Der Gegenüber lacht. Laut."; }
        results[round] = pts;
        score += pts;
        pauseT = 0.9;
    }

    @Override public void press(int x, int y) { lock(); }
    @Override public void lane(int idx) { if (idx == -1) lock(); }

    @Override public void render(Graphics2D g) {
        g.setColor(new Color(30, 36, 42));
        g.fillRect(0, 0, w, h);
        // Verhandlungstisch
        g.setColor(new Color(88, 62, 40));
        g.fillRect(0, h - 90, w, 90);
        g.setColor(new Color(60, 42, 28));
        g.drawLine(0, h - 90, w, h - 90);

        int barW = (int) (w * 0.72), barH = 34;
        int bx = (w - barW) / 2, by = h / 2 - 60;

        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.TEXT);
        String rd = "Runde " + Math.min(round + 1, ROUNDS) + " / " + ROUNDS;
        g.drawString(rd, bx, by - 44);
        // Runden-Ergebnisse als Punkte
        for (int i = 0; i < ROUNDS; i++) {
            g.setColor(i < round ? (results[i] >= 2 ? new Color(96, 220, 120) : results[i] == 1 ? Palette.WARN : new Color(255, 92, 92))
                                 : new Color(255, 255, 255, 50));
            g.fillOval(bx + 90 + i * 20, by - 56, 13, 13);
        }

        // Zonen: rot | gelb | grün | perfekt | grün | gelb | rot
        double gz = greenZone();
        g.setColor(new Color(150, 60, 55));
        g.fillRoundRect(bx, by, barW, barH, 10, 10);
        int yw = (int) (barW * 0.45 / 2), gw = (int) (barW * gz / 2), pw = (int) (barW * 0.07 / 2);
        g.setColor(new Color(190, 160, 70));
        g.fillRect(bx + barW / 2 - yw, by, 2 * yw, barH);
        g.setColor(new Color(70, 160, 90));
        g.fillRect(bx + barW / 2 - gw, by, 2 * gw, barH);
        g.setColor(new Color(120, 240, 150));
        g.fillRect(bx + barW / 2 - pw, by, 2 * pw, barH);
        g.setColor(new Color(255, 255, 255, 120));
        g.drawRoundRect(bx, by, barW, barH, 10, 10);

        // Zeiger
        double pos = Math.sin(phase);
        int px = bx + barW / 2 + (int) (pos * (barW / 2 - 6));
        g.setColor(Palette.TEXT);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(px, by - 10, px, by + barH + 10);
        Polygon tip = new Polygon(new int[]{ px - 7, px + 7, px }, new int[]{ by - 18, by - 18, by - 8 }, 3);
        g.fillPolygon(tip);

        if (!verdict.isEmpty() && pauseT > 0) {
            g.setFont(Palette.FONT_H2);
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Palette.ACCENT2);
            g.drawString(verdict, (w - fm.stringWidth(verdict)) / 2, by + barH + 44);
        }
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        String hint = "KLICK oder LEERTASTE stoppt den Zeiger - Ziel: die Mitte.";
        g.drawString(hint, (w - g.getFontMetrics().stringWidth(hint)) / 2, h - 40);
    }

    @Override public String title() { return "Feilsch-o-Mat"; }
    @Override public String subtitle() { return "Fünf Verhandlungsrunden. Der Gegenüber hat Übung. Du hast einen Zeiger."; }
    @Override public String[] howTo() {
        return new String[]{
            "Der Zeiger pendelt über die Verhandlungs-Leiste.",
            "KLICK / LEERTASTE stoppt ihn: Mitte = HANDSCHLAG (3), grün = gut (2).",
            "5 Runden - der Zeiger wird schneller, die grüne Zone schmaler." };
    }
}
