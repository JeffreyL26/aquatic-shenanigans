package com.shrimptopia.ui.minigames;

import com.shrimptopia.ui.Palette;
import java.awt.*;

/**
 * "Blackout!" - Whack-a-Mole-Mechanik am Sicherungskasten: Sicherungen fangen an zu
 * funken (gelb -> rot). KLICK repariert; wer zu langsam ist, kassiert einen Kurzschluss.
 */
public class FuseGame extends Minigame {

    private static final int COLS = 4, ROWS = 3;
    private final double[] lit = new double[COLS * ROWS];    // >0 = funkt seit t Sekunden
    private final double[] boom = new double[COLS * ROWS];   // Explosions-Restzeit
    private double spawnT;
    private double flash;
    private double fuseLife = 1.6;

    @Override protected void init() {
        duration = 20;
        target = 12;
        spawnT = 0.5;
    }

    @Override protected void onUpdate(double dt) {
        spawnT -= dt;
        fuseLife = Math.max(1.0, 1.6 - (duration - timeLeft) * 0.025);   // wird schneller
        if (spawnT <= 0) {
            spawnT = 0.75 + rng.nextDouble() * 0.5;
            int tries = 8;
            while (tries-- > 0) {
                int i = rng.nextInt(lit.length);
                if (lit[i] <= 0 && boom[i] <= 0) { lit[i] = 0.0001; break; }
            }
        }
        for (int i = 0; i < lit.length; i++) {
            if (lit[i] > 0) {
                lit[i] += dt;
                if (lit[i] > fuseLife) {   // zu spät: Kurzschluss
                    lit[i] = 0;
                    boom[i] = 0.5;
                    score = Math.max(0, score - 1);
                    flash = 0.3;
                    Rectangle r = cell(i);
                    popup(r.getCenterX(), r.y - 4, "-1  KNALL!", new Color(255, 110, 100));
                }
            }
            if (boom[i] > 0) boom[i] -= dt;
        }
        flash = Math.max(0, flash - dt);
        updatePopups(dt);
    }

    private Rectangle cell(int i) {
        int cw = (w - 80) / COLS, ch = (h - 90) / ROWS;
        return new Rectangle(40 + (i % COLS) * cw + 8, 50 + (i / COLS) * ch + 8, cw - 16, ch - 16);
    }

    @Override public void press(int x, int y) {
        for (int i = 0; i < lit.length; i++)
            if (lit[i] > 0 && cell(i).contains(x, y)) {
                lit[i] = 0;
                score++;
                Rectangle r = cell(i);
                popup(r.getCenterX(), r.y - 4, "+1", new Color(150, 230, 130));
                return;
            }
    }

    @Override public Color accent() { return new Color(255, 205, 86); }

    private final java.util.List<double[]> sparks = new java.util.ArrayList<>();   // {x,y,vx,vy,age}

    @Override public void render(Graphics2D g) {
        g.setColor(new Color(26, 28, 32));
        g.fillRect(0, 0, w, h);
        // Backstein-Andeutung hinter dem Kasten
        g.setColor(new Color(44, 38, 36));
        for (int y = 0; y < h; y += 26)
            for (int x = (y / 26 % 2) * 26; x < w; x += 52)
                g.drawRect(x, y, 52, 26);
        g.setColor(new Color(52, 56, 62));
        g.fillRoundRect(24, 34, w - 48, h - 58, 14, 14);
        g.setColor(new Color(90, 96, 104));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(24, 34, w - 48, h - 58, 14, 14);
        // Kabelbaum: Leitungen von jeder Sicherung zur Sammelschiene unten
        g.setColor(new Color(70, 76, 84));
        g.setStroke(new BasicStroke(2.4f));
        for (int i = 0; i < lit.length; i++) {
            Rectangle r = cell(i);
            g.drawLine((int) r.getCenterX(), r.y + r.height, (int) r.getCenterX(), h - 34);
        }
        g.setColor(new Color(110, 116, 124));
        g.fillRoundRect(34, h - 38, w - 68, 10, 5, 5);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.TEXT_DIM);
        g.drawString("HAUPTVERTEILER  -  NICHT IM DUNKELN ÖFFNEN", 40, 28);
        // Funkenregen aktualisiert/gezeichnet (einfacher Partikelregen aus aktiven Sicherungen)
        for (int i = 0; i < lit.length; i++)
            if (lit[i] > 0 && sparks.size() < 60 && rng.nextDouble() < 0.4) {
                Rectangle r = cell(i);
                sparks.add(new double[]{ r.getCenterX(), r.getCenterY(), rng.nextInt(60) - 30, 40 + rng.nextInt(60), 0 });
            }
        for (double[] s : sparks) { s[0] += s[2] * 0.016; s[1] += s[3] * 0.016; s[4] += 0.016; }
        sparks.removeIf(s -> s[4] > 0.6);
        for (double[] s : sparks) {
            int a = (int) (220 * Math.max(0, 1 - s[4] / 0.6));
            g.setColor(new Color(255, 230, 140, a));
            g.fillRect((int) s[0], (int) s[1], 2, 3);
        }

        for (int i = 0; i < lit.length; i++) {
            Rectangle r = cell(i);
            boolean on = lit[i] > 0;
            double p = on ? Math.min(1, lit[i] / fuseLife) : 0;
            Color body = on ? new Color((int) (120 + 135 * p), (int) (120 - 60 * p), 40) : new Color(44, 48, 54);
            g.setColor(body);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            g.setColor(on ? new Color(255, (int) (220 - 160 * p), 80) : new Color(70, 76, 84));
            g.setStroke(new BasicStroke(on ? 2.5f : 1.2f));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            // Sicherungs-Hebel
            g.setColor(on ? new Color(30, 26, 20) : new Color(90, 98, 108));
            g.fillRoundRect(r.x + r.width / 2 - 6, r.y + 12, 12, r.height - 24, 6, 6);
            if (on) {   // Funken
                g.setColor(new Color(255, 240, 150, 200));
                int cx = r.x + r.width / 2, cy = r.y + r.height / 2;
                for (int s = 0; s < 4; s++) {
                    double a = lit[i] * 12 + s * 1.6;
                    g.drawLine(cx, cy, cx + (int) (Math.cos(a) * 14), cy + (int) (Math.sin(a) * 14));
                }
            }
            if (boom[i] > 0) {
                g.setColor(new Color(255, 90, 60, (int) (255 * boom[i] / 0.5)));
                g.setStroke(new BasicStroke(3f));
                g.drawLine(r.x + 6, r.y + 6, r.x + r.width - 6, r.y + r.height - 6);
                g.drawLine(r.x + r.width - 6, r.y + 6, r.x + 6, r.y + r.height - 6);
            }
        }
        renderPopups(g);
        if (flash > 0) {   // Kurzschluss: kurzes Dunkel-Flackern
            g.setColor(new Color(0, 0, 0, (int) (170 * flash / 0.3)));
            g.fillRect(0, 0, w, h);
        }
    }

    @Override public String title() { return "Blackout!"; }
    @Override public String subtitle() { return "Olaf: 'Die Sicherungen! IMMER die Sicherungen!'"; }
    @Override public String[] howTo() {
        return new String[]{
            "Sicherungen fangen an zu FUNKEN (gelb wird rot).",
            "KLICK auf funkende Sicherungen repariert sie (+1).",
            "Zu langsam = Kurzschluss (-1). Es wird immer hektischer!" };
    }
}
