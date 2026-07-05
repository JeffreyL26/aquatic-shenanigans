package com.shrimptopia.ui.minigames;

import com.shrimptopia.ui.Palette;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * "Großputz!" - Schrubb-Mechanik: Algenflecken mit gedrückter Maustaste WEGWISCHEN,
 * bevor die Zeit abläuft. Jeder Fleck braucht mehrere Wischer.
 */
public class ScrubGame extends Minigame {

    private static class Blob {
        double x, y, r, hp;
        int seed;
    }

    private final List<Blob> blobs = new ArrayList<>();
    private int mx = -100, my = -100;
    private double sweepGlow;

    @Override protected void init() {
        duration = 18;
        int n = 12;
        target = n;
        for (int i = 0; i < n; i++) {
            Blob b = new Blob();
            b.r = 26 + rng.nextDouble() * 22;
            b.x = b.r + 20 + rng.nextDouble() * (w - 2 * b.r - 40);
            b.y = b.r + 20 + rng.nextDouble() * (h - 2 * b.r - 40);
            b.hp = 1.0;
            b.seed = rng.nextInt(1000);
            blobs.add(b);
        }
    }

    @Override protected void onUpdate(double dt) {
        sweepGlow = Math.max(0, sweepGlow - dt * 3);
        if (blobs.isEmpty()) finish();
    }

    @Override public void drag(int x, int y) {
        mx = x; my = y;
        sweepGlow = 1;
        for (int i = blobs.size() - 1; i >= 0; i--) {
            Blob b = blobs.get(i);
            if (Math.hypot(x - b.x, y - b.y) < b.r + 10) {
                b.hp -= 0.09;
                if (b.hp <= 0) { blobs.remove(i); score++; }
            }
        }
    }

    @Override public void press(int x, int y) { drag(x, y); }
    @Override public void move(int x, int y) { mx = x; my = y; }

    @Override public void render(Graphics2D g) {
        // gekachelte Beckenwand
        g.setColor(new Color(52, 84, 96));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(40, 68, 80));
        for (int x = 0; x < w; x += 48) g.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 48) g.drawLine(0, y, w, y);

        for (Blob b : blobs) {
            float a = (float) Math.max(0.25, b.hp);
            g.setColor(new Color(70, 130, 60, (int) (200 * a)));
            // unregelmäßiger Fleck aus mehreren Kreisen (deterministisch aus seed)
            for (int i = 0; i < 5; i++) {
                double ang = b.seed * 0.7 + i * 1.26;
                double ox = Math.cos(ang) * b.r * 0.35, oy = Math.sin(ang) * b.r * 0.3;
                double rr = b.r * (0.55 + ((b.seed + i) % 4) * 0.1);
                g.fillOval((int) (b.x + ox - rr), (int) (b.y + oy - rr), (int) (2 * rr), (int) (2 * rr));
            }
            g.setColor(new Color(110, 180, 90, (int) (150 * a)));
            g.fillOval((int) (b.x - b.r * 0.4), (int) (b.y - b.r * 0.45), (int) b.r, (int) (b.r * 0.7));
        }

        // Schwamm am Cursor
        g.setColor(sweepGlow > 0 ? new Color(255, 220, 120) : new Color(230, 200, 110));
        g.fillRoundRect(mx - 18, my - 12, 36, 24, 8, 8);
        g.setColor(new Color(180, 150, 70));
        g.drawRoundRect(mx - 18, my - 12, 36, 24, 8, 8);
        g.setColor(new Color(255, 255, 255, 90));
        g.fillRoundRect(mx - 14, my - 8, 28, 7, 4, 4);
        if (sweepGlow > 0) {
            g.setColor(new Color(180, 230, 255, (int) (120 * sweepGlow)));
            g.drawOval(mx - 26, my - 20, 52, 40);
        }
        g.setFont(Palette.FONT_TINY);
        g.setColor(new Color(255, 255, 255, 130));
        g.drawString("Wischen: Maustaste gedrückt halten & rubbeln", 12, h - 10);
    }

    @Override public String title() { return "Großputz!"; }
    @Override public String subtitle() { return "Die Becken müssen blitzen, bevor jemand Wichtiges hinsieht."; }
    @Override public String[] howTo() {
        return new String[]{
            "Maustaste GEDRÜCKT HALTEN und über Algenflecken RUBBELN.",
            "Jeder Fleck braucht ein paar Wischer, bis er verschwindet.",
            "18 Sekunden - schaffst du alle 12 Flecken?" };
    }
}
