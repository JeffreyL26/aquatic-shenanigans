package com.shrimptopia.ui.minigames;

import com.shrimptopia.model.IconKind;
import com.shrimptopia.ui.Icons;
import com.shrimptopia.ui.Palette;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * "Operation Krill-Kommando" - Moorhuhn-Mechanik: feindliche Kampf-Langusten fliegen
 * über den Schirm, KLICK schießt. Aber Vorsicht: goldene Gregs sind Verbündete!
 */
public class ShooterGame extends Minigame {

    private static class Target {
        double x, y, vx, wobble, phase;
        boolean friend, dead;
        double deadT;
    }

    private final List<Target> targets = new ArrayList<>();
    private final List<double[]> hits = new ArrayList<>();   // {x, y, alter}
    private double spawnT;
    private int mx = -100, my = -100;

    @Override protected void init() {
        duration = 20;
        target = 12;
        spawnT = 0.2;
    }

    private double cloudDrift;
    private double crossPulse;

    @Override protected void onUpdate(double dt) {
        spawnT -= dt;
        cloudDrift += dt * 12;
        crossPulse = Math.max(0, crossPulse - dt * 4);
        if (spawnT <= 0) {
            spawnT = 0.55 + rng.nextDouble() * 0.5;
            Target t = new Target();
            boolean fromLeft = rng.nextBoolean();
            t.x = fromLeft ? -30 : w + 30;
            t.y = 60 + rng.nextDouble() * (h - 160);
            t.vx = (fromLeft ? 1 : -1) * (110 + rng.nextDouble() * 130);
            t.wobble = 14 + rng.nextDouble() * 26;
            t.phase = rng.nextDouble() * Math.PI * 2;
            t.friend = rng.nextDouble() < 0.25;
            targets.add(t);
        }
        for (Target t : targets) {
            if (t.dead) { t.deadT += dt; continue; }
            t.x += t.vx * dt;
            t.phase += dt * 3;
        }
        targets.removeIf(t -> (t.dead && t.deadT > 0.4) || t.x < -60 || t.x > w + 60);
        for (double[] hp : hits) hp[2] += dt;
        hits.removeIf(hp -> hp[2] > 0.35);
        updatePopups(dt);
    }

    @Override public void press(int x, int y) {
        hits.add(new double[]{x, y, 0});
        crossPulse = 1;
        // oberstes Ziel im Radius treffen
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target t = targets.get(i);
            if (t.dead) continue;
            double ty = t.y + Math.sin(t.phase) * t.wobble;
            if (Math.hypot(x - t.x, y - ty) < 30) {
                t.dead = true;
                if (t.friend) { score -= 2; popup(t.x, ty - 26, "-2  GREG?!", new Color(255, 110, 100)); }
                else { score += 1; popup(t.x, ty - 26, "+1", new Color(255, 205, 86)); }
                return;
            }
        }
    }

    @Override public void move(int x, int y) { mx = x; my = y; }
    @Override public Color accent() { return new Color(150, 165, 90); }

    @Override public void render(Graphics2D g) {
        // Nachthimmel mit Mond, Sternen und ziehenden Wolken
        g.setPaint(new GradientPaint(0, 0, new Color(12, 20, 36), 0, h, new Color(34, 46, 60)));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(255, 255, 255, 44));
        for (int i = 0; i < 46; i++)
            g.fillRect((i * 97) % Math.max(1, w), (i * 53) % Math.max(1, h - 90), 2, 2);
        g.setColor(new Color(235, 235, 210, 200));
        g.fillOval(w - 110, 26, 52, 52);
        g.setColor(new Color(12, 20, 36, 210));
        g.fillOval(w - 96, 20, 46, 46);
        g.setColor(new Color(255, 255, 255, 16));
        for (int i = 0; i < 3; i++) {
            int cxr = (int) ((cloudDrift * (0.5 + i * 0.3) + i * 300) % (w + 260)) - 130;
            g.fillRoundRect(cxr, 50 + i * 46, 150 + i * 40, 22, 22, 22);
        }
        // Hallen-Silhouette unten
        g.setColor(new Color(24, 32, 40));
        for (int i = 0; i < 6; i++) {
            int bw = 90 + (i * 37) % 60, bh = 34 + (i * 53) % 40;
            g.fillRect(i * (w / 6), h - 42 - bh, bw, bh);
        }
        g.setColor(new Color(38, 48, 58));
        g.fillRect(0, h - 42, w, 42);
        g.setColor(new Color(150, 165, 90, 120));
        g.drawLine(0, h - 42, w, h - 42);
        // Sandsäcke + MG-Nest-Deko
        g.setColor(new Color(120, 104, 70));
        for (int i = 0; i < 5; i++) g.fillRoundRect(14 + i * 24, h - 36 + (i % 2) * 9, 26, 13, 8, 8);
        g.setColor(new Color(84, 72, 48));
        for (int i = 0; i < 5; i++) g.drawRoundRect(14 + i * 24, h - 36 + (i % 2) * 9, 26, 13, 8, 8);

        for (Target t : targets) {
            double ty = t.y + Math.sin(t.phase) * t.wobble;
            if (t.dead) {
                float a = (float) Math.max(0, 1 - t.deadT / 0.4);
                g.setColor(new Color(255, 210, 90, (int) (200 * a)));
                int r = (int) (10 + t.deadT * 70);
                g.setStroke(new BasicStroke(3f));
                g.drawOval((int) t.x - r, (int) ty - r, 2 * r, 2 * r);
                g.setColor(new Color(255, 255, 255, (int) (150 * a)));
                for (int s = 0; s < 5; s++) {
                    double ang = s * 1.25 + t.deadT * 6;
                    g.fillOval((int) (t.x + Math.cos(ang) * r * 0.8) - 2, (int) (ty + Math.sin(ang) * r * 0.8) - 2, 4, 4);
                }
                continue;
            }
            // Schatten unter dem Ziel
            g.setColor(new Color(0, 0, 0, 40));
            g.fillOval((int) t.x - 14, (int) ty + 16, 28, 7);
            if (t.friend) {
                float halo = (float) (0.5 + 0.5 * Math.sin(t.phase * 2));
                g.setColor(new Color(255, 220, 120, (int) (50 + 40 * halo)));
                g.fillOval((int) t.x - 26, (int) ty - 26, 52, 52);
                Icons.resource(g, IconKind.SHRIMP, t.x, ty, 40, new Color(235, 190, 90));
                g.setColor(new Color(255, 230, 150));
                g.drawOval((int) t.x - 10, (int) ty - 30, 20, 7);   // Heiligenschein
                g.setFont(Palette.FONT_TINY);
                g.drawString("GREG", (int) t.x - 14, (int) ty + 30);
            } else {
                Icons.resource(g, IconKind.SHRIMP, t.x, ty, 42, new Color(225, 70, 60));
                // zornige Augenbrauen + Mini-Fahne
                g.setColor(new Color(40, 10, 10));
                g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, 0));
                g.drawLine((int) t.x - 8, (int) ty - 8, (int) t.x - 2, (int) ty - 5);
                g.drawLine((int) t.x + 8, (int) ty - 8, (int) t.x + 2, (int) ty - 5);
                g.setColor(new Color(60, 90, 150));
                g.fillRect((int) t.x + 12, (int) ty - 22, 12, 8);
                g.setColor(Color.WHITE);
                g.drawLine((int) t.x + 12, (int) ty - 22, (int) t.x + 12, (int) ty - 8);
            }
        }
        // Treffer-Blitze
        for (double[] hp : hits) {
            float a = (float) Math.max(0, 1 - hp[2] / 0.35);
            g.setColor(new Color(255, 255, 200, (int) (160 * a)));
            g.setStroke(new BasicStroke(2f));
            g.drawLine((int) hp[0] - 8, (int) hp[1], (int) hp[0] + 8, (int) hp[1]);
            g.drawLine((int) hp[0], (int) hp[1] - 8, (int) hp[0], (int) hp[1] + 8);
        }
        renderPopups(g);
        // Fadenkreuz mit Rückstoß-Puls
        int cr = 14 + (int) (crossPulse * 5);
        g.setColor(new Color(150, 220, 120));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(mx - cr, my - cr, 2 * cr, 2 * cr);
        g.drawLine(mx - cr - 6, my, mx - cr + 6, my);
        g.drawLine(mx + cr - 6, my, mx + cr + 6, my);
        g.drawLine(mx, my - cr - 6, mx, my - cr + 6);
        g.drawLine(mx, my + cr - 6, mx, my + cr + 6);
        g.fillOval(mx - 2, my - 2, 4, 4);
    }

    @Override public String title() { return "Operation Krill-Kommando"; }
    @Override public String subtitle() { return "General Krillkill: 'Feuer frei, Rekrut - aber NICHT auf Greg!'"; }
    @Override public String[] howTo() {
        return new String[]{
            "KLICK schießt auf vorbeifliegende rote Kampf-Langusten (+1).",
            "Goldene GREGs sind Verbündete - Treffer kostet 2 Punkte!",
            "20 Sekunden. Je mehr Abschüsse, desto fetter die Belohnung." };
    }
}
