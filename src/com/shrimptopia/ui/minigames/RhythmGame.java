package com.shrimptopia.ui.minigames;

import com.shrimptopia.model.IconKind;
import com.shrimptopia.ui.Icons;
import com.shrimptopia.ui.Palette;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * "Boygroup-Bootcamp" - Rhythmus-Mechanik: Noten fallen auf 4 Spuren, Tasten A/S/D/F
 * (oder Klick auf die Spur) im richtigen Moment an der Ziellinie drücken.
 */
public class RhythmGame extends Minigame {

    private static class Note { int lane; double tHit; boolean hit, missed; }

    private static final String[] KEYS = { "A", "S", "D", "F" };
    private static final Color[] LANE_COLORS = {
        new Color(0, 199, 183), new Color(255, 159, 67), new Color(184, 162, 255), new Color(255, 120, 190) };
    private static final double TRAVEL = 2.0;   // Sekunden vom oberen Rand zur Ziellinie
    private static final double WINDOW = 0.16;

    private final List<Note> notes = new ArrayList<>();
    private double t;
    private int combo, bestCombo;
    private String verdict = "";
    private double verdictT;
    private final double[] laneFlash = new double[4];   // Aufleuchten der Spur beim Tastendruck

    @Override protected void init() {
        duration = 22;
        t = 0;
        double beat = 2.2;
        int count = 0;
        while (beat < duration - 1.5) {
            Note n = new Note();
            n.lane = rng.nextInt(4);
            n.tHit = beat;
            notes.add(n); count++;
            if (rng.nextDouble() < 0.22) {   // Doppel-Note auf anderer Spur
                Note d = new Note();
                d.lane = (n.lane + 1 + rng.nextInt(3)) % 4;
                d.tHit = beat;
                notes.add(d); count++;
            }
            beat += 0.42 + rng.nextInt(3) * 0.21;
        }
        target = count * 1.35;   // Mix aus GUT (1) und PERFEKT (2)
    }

    @Override protected void onUpdate(double dt) {
        t += dt;
        verdictT = Math.max(0, verdictT - dt);
        for (int i = 0; i < 4; i++) laneFlash[i] = Math.max(0, laneFlash[i] - dt * 4);
        for (Note n : notes)
            if (!n.hit && !n.missed && n.tHit < t - WINDOW) { n.missed = true; combo = 0; }
    }

    @Override public Color accent() { return new Color(255, 120, 190); }

    @Override public void lane(int idx) {
        if (idx < 0 || idx > 3) return;
        laneFlash[idx] = 1;
        Note best = null;
        double bestDiff = WINDOW;
        for (Note n : notes) {
            if (n.hit || n.missed || n.lane != idx) continue;
            double diff = Math.abs(n.tHit - t);
            if (diff <= bestDiff) { bestDiff = diff; best = n; }
        }
        if (best == null) { combo = 0; setVerdict("DANEBEN"); return; }
        best.hit = true;
        combo++;
        bestCombo = Math.max(bestCombo, combo);
        if (bestDiff < 0.07) { score += 2; setVerdict("PERFEKT!"); }
        else { score += 1; setVerdict("GUT"); }
    }

    @Override public void press(int x, int y) {
        int laneW = w / 4;
        lane(Math.min(3, Math.max(0, x / laneW)));
    }

    private void setVerdict(String v) { verdict = v; verdictT = 0.5; }

    @Override public void render(Graphics2D g) {
        int laneW = w / 4;
        int hitY = h - 70;
        // Bühne mit wandernden Scheinwerfern und Publikums-Silhouette
        g.setPaint(new GradientPaint(0, 0, new Color(24, 16, 34), 0, h, new Color(46, 22, 52)));
        g.fillRect(0, 0, w, h);
        for (int i = 0; i < 2; i++) {
            double sweep = Math.sin(t * (0.7 + i * 0.4) + i * 2) * w * 0.3 + w * (0.3 + i * 0.4);
            java.awt.geom.Path2D beam = new java.awt.geom.Path2D.Double();
            beam.moveTo(sweep - 14, 0);
            beam.lineTo(sweep + 14, 0);
            beam.lineTo(sweep + 90, h);
            beam.lineTo(sweep - 90, h);
            beam.closePath();
            g.setColor(new Color(255, 240, 200, 14));
            g.fill(beam);
        }
        for (int l = 0; l < 4; l++) {
            g.setColor(new Color(255, 255, 255, l % 2 == 0 ? 8 : 14));
            g.fillRect(l * laneW, 0, laneW, h);
            // Lane-Flash beim Tastendruck
            if (laneFlash[l] > 0) {
                Color lc = LANE_COLORS[l];
                g.setPaint(new GradientPaint(0, hitY, new Color(lc.getRed(), lc.getGreen(), lc.getBlue(), (int) (90 * laneFlash[l])),
                    0, hitY - 180, new Color(lc.getRed(), lc.getGreen(), lc.getBlue(), 0)));
                g.fillRect(l * laneW, hitY - 180, laneW, 180);
            }
            g.setColor(new Color(LANE_COLORS[l].getRed(), LANE_COLORS[l].getGreen(), LANE_COLORS[l].getBlue(), 60));
            g.drawLine(l * laneW, 0, l * laneW, h);
        }
        // Publikum unten (wippende Silhouetten)
        g.setColor(new Color(10, 8, 16, 200));
        for (int i = 0; i < w / 34 + 1; i++) {
            int bob = (int) (Math.sin(t * 3 + i) * 3);
            g.fillOval(i * 34, h - 26 + bob, 24, 30);
        }
        // Ziellinie + Tasten
        g.setColor(new Color(255, 255, 255, 170));
        g.setStroke(new BasicStroke(3f));
        g.drawLine(0, hitY, w, hitY);
        for (int l = 0; l < 4; l++) {
            int cx = l * laneW + laneW / 2;
            g.setColor(new Color(LANE_COLORS[l].getRed(), LANE_COLORS[l].getGreen(), LANE_COLORS[l].getBlue(), 60));
            g.fillOval(cx - 22, hitY - 22, 44, 44);
            g.setColor(LANE_COLORS[l]);
            g.drawOval(cx - 22, hitY - 22, 44, 44);
            g.setFont(Palette.FONT_H2);
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Palette.TEXT);
            g.drawString(KEYS[l], cx - fm.stringWidth(KEYS[l]) / 2, hitY + 6);
        }
        // fallende Noten (kleine Shrimps)
        for (Note n : notes) {
            if (n.hit) continue;
            double rel = (n.tHit - t) / TRAVEL;
            if (rel > 1 || rel < -0.08) continue;
            int y = (int) (hitY - rel * (hitY - 20));
            int cx = n.lane * laneW + laneW / 2;
            Color c = n.missed ? new Color(120, 120, 120) : LANE_COLORS[n.lane];
            Icons.resource(g, IconKind.SHRIMP, cx, y, 30, c);
        }
        // Combo + Urteil
        g.setFont(Palette.FONT_H1);
        if (combo >= 4) {
            g.setColor(new Color(255, 205, 86));
            g.drawString("COMBO x" + combo, 16, 34);
        }
        if (verdictT > 0) {
            g.setColor(verdict.startsWith("PERF") ? new Color(96, 220, 120)
                     : verdict.equals("GUT") ? Palette.ACCENT : new Color(255, 110, 100));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(verdict, (w - fm.stringWidth(verdict)) / 2, hitY - 50);
        }
    }

    @Override public String title() { return "Boygroup-Bootcamp"; }
    @Override public String subtitle() { return "Salsa-Sepp: 'UND Fühler! UND drehen! KAI, WO- ah, da bist du.'"; }
    @Override public String[] howTo() {
        return new String[]{
            "Noten fallen auf 4 Spuren Richtung Ziellinie.",
            "Tasten A / S / D / F (oder Klick auf die Spur) im richtigen Moment!",
            "PERFEKT = 2 Punkte, GUT = 1 Punkt. Combos sehen großartig aus." };
    }
}
