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

    @Override protected void onUpdate(double dt) {
        spawnT -= dt;
        if (spawnT <= 0) {
            spawnT = 0.55 + rng.nextDouble() * 0.5;
            Target t = new Target();
            boolean fromLeft = rng.nextBoolean();
            t.x = fromLeft ? -30 : w + 30;
            t.y = 60 + rng.nextDouble() * (h - 140);
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
    }

    @Override public void press(int x, int y) {
        hits.add(new double[]{x, y, 0});
        // oberstes Ziel im Radius treffen
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target t = targets.get(i);
            if (t.dead) continue;
            double ty = t.y + Math.sin(t.phase) * t.wobble;
            if (Math.hypot(x - t.x, y - ty) < 30) {
                t.dead = true;
                score += t.friend ? -2 : 1;
                return;
            }
        }
    }

    @Override public void move(int x, int y) { mx = x; my = y; }

    @Override public void render(Graphics2D g) {
        // Nachthimmel über der Halle
        g.setPaint(new GradientPaint(0, 0, new Color(16, 24, 38), 0, h, new Color(30, 42, 56)));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(255, 255, 255, 40));
        for (int i = 0; i < 40; i++)
            g.fillRect((i * 97) % w, (i * 53) % (h - 60), 2, 2);
        // Hallendach unten
        g.setColor(new Color(38, 48, 58));
        g.fillRect(0, h - 42, w, 42);
        g.setColor(new Color(0, 199, 183, 90));
        g.drawLine(0, h - 42, w, h - 42);

        for (Target t : targets) {
            double ty = t.y + Math.sin(t.phase) * t.wobble;
            if (t.dead) {
                float a = (float) Math.max(0, 1 - t.deadT / 0.4);
                g.setColor(new Color(255, 210, 90, (int) (200 * a)));
                int r = (int) (10 + t.deadT * 70);
                g.setStroke(new BasicStroke(3f));
                g.drawOval((int) t.x - r, (int) ty - r, 2 * r, 2 * r);
                continue;
            }
            if (t.friend) {
                g.setColor(new Color(255, 220, 120, 70));
                g.fillOval((int) t.x - 24, (int) ty - 24, 48, 48);
                Icons.resource(g, IconKind.SHRIMP, t.x, ty, 40, new Color(235, 190, 90));
                g.setFont(Palette.FONT_TINY);
                g.setColor(new Color(255, 230, 150));
                g.drawString("GREG", (int) t.x - 14, (int) ty + 26);
            } else {
                Icons.resource(g, IconKind.SHRIMP, t.x, ty, 42, new Color(225, 70, 60));
                // zorniger Blick + Mini-Fahne
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
            g.drawLine((int) hp[0] - 8, (int) hp[1], (int) hp[0] + 8, (int) hp[1]);
            g.drawLine((int) hp[0], (int) hp[1] - 8, (int) hp[0], (int) hp[1] + 8);
        }
        // Fadenkreuz
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(mx - 14, my - 14, 28, 28);
        g.drawLine(mx - 20, my, mx - 8, my);
        g.drawLine(mx + 8, my, mx + 20, my);
        g.drawLine(mx, my - 20, mx, my - 8);
        g.drawLine(mx, my + 8, mx, my + 20);
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
