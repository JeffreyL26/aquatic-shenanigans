package com.shrimptopia.ui.minigames;

import com.shrimptopia.model.IconKind;
import com.shrimptopia.ui.Icons;
import com.shrimptopia.ui.Palette;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * "Darm-Kontrolle" - Fließband-Mechanik: Shrimps laufen über drei Bänder Richtung
 * Premium-Kiste. KLICK sortiert Exemplare mit VOLLEM DARM (dunkler Streifen) aus.
 * Wer saubere Ware anklickt, verärgert Reinhild.
 */
public class SortGame extends Minigame {

    private static class Item {
        double x; int belt;
        boolean dirty, dead;
        double deadT;
    }

    private final List<Item> items = new ArrayList<>();
    private double spawnT;
    private int missed;
    private double reinhildGlare;   // Reinhild guckt streng, wenn man saubere anklickt

    @Override protected void init() {
        duration = 20;
        target = 10;
        spawnT = 0.3;
    }

    private int beltY(int belt) { return h / 4 + belt * (h / 4); }

    @Override protected void onUpdate(double dt) {
        spawnT -= dt;
        if (spawnT <= 0) {
            spawnT = 0.55 + rng.nextDouble() * 0.35;
            Item it = new Item();
            it.belt = rng.nextInt(3);
            it.x = -30;
            it.dirty = rng.nextDouble() < 0.4;
            items.add(it);
        }
        double speed = 120 + (duration - timeLeft) * 4;
        for (Item it : items) {
            if (it.dead) { it.deadT += dt; continue; }
            it.x += speed * dt;
        }
        for (Item it : items)
            if (!it.dead && it.x > w + 20 && it.dirty) { it.dead = true; missed++; }
        items.removeIf(it -> (it.dead && it.deadT > 0.35) || it.x > w + 30);
        reinhildGlare = Math.max(0, reinhildGlare - dt);
    }

    @Override public void press(int x, int y) {
        for (int i = items.size() - 1; i >= 0; i--) {
            Item it = items.get(i);
            if (it.dead) continue;
            if (Math.hypot(x - it.x, y - beltY(it.belt)) < 26) {
                it.dead = true;
                if (it.dirty) score++;
                else { score = Math.max(0, score - 1); reinhildGlare = 0.8; }
                return;
            }
        }
    }

    @Override public void render(Graphics2D g) {
        g.setColor(new Color(36, 42, 48));
        g.fillRect(0, 0, w, h);
        // Bänder
        for (int b = 0; b < 3; b++) {
            int y = beltY(b);
            g.setColor(new Color(58, 64, 72));
            g.fillRoundRect(0, y - 20, w, 40, 12, 12);
            g.setColor(new Color(40, 46, 52));
            double off = (duration - timeLeft) * 60 % 26;
            for (int x = (int) -off; x < w; x += 26) g.drawLine(x, y - 20, x + 12, y + 20);
        }
        // Premium-Kiste rechts
        g.setColor(new Color(200, 170, 90));
        g.fillRoundRect(w - 26, 20, 26, h - 40, 8, 8);
        g.setColor(new Color(120, 96, 40));
        g.drawRoundRect(w - 26, 20, 26, h - 40, 8, 8);
        g.setFont(Palette.FONT_TINY);
        Graphics2D gv = (Graphics2D) g.create();
        gv.rotate(-Math.PI / 2, w - 8, h / 2.0);
        gv.setColor(new Color(70, 52, 20));
        gv.drawString("GOURMET-EXPORT", w - 60, h / 2 + 4);
        gv.dispose();

        for (Item it : items) {
            int y = beltY(it.belt);
            if (it.dead) {
                float a = (float) Math.max(0, 1 - it.deadT / 0.35);
                g.setColor(new Color(255, 255, 255, (int) (140 * a)));
                g.drawOval((int) it.x - 16, y - 16, 32, 32);
                continue;
            }
            Icons.resource(g, IconKind.SHRIMP, it.x, y, 34, new Color(255, 170, 140));
            if (it.dirty) {   // voller Darm: deutlicher dunkler Streifen
                g.setColor(new Color(70, 50, 30));
                g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, 0));
                g.drawLine((int) it.x - 9, y - 3, (int) it.x + 8, y - 6);
            }
        }
        // Reinhild kommentiert Fehlgriffe
        if (reinhildGlare > 0) {
            g.setFont(Palette.FONT_BOLD);
            g.setColor(new Color(255, 120, 110, (int) (255 * Math.min(1, reinhildGlare))));
            g.drawString("Reinhild: 'Der war SAUBER.'", 16, 26);
        }
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.TEXT_DIM);
        g.drawString("Durchgerutscht (voller Darm): " + missed, 16, h - 10);
    }

    @Override public String title() { return "Darm-Kontrolle"; }
    @Override public String subtitle() { return "Reinhild Darmstädter: 'Nur MAKELLOSE Ware verlässt dieses Band.'"; }
    @Override public String[] howTo() {
        return new String[]{
            "Shrimps laufen Richtung Gourmet-Kiste.",
            "KLICK sortiert Exemplare mit DUNKLEM DARM-STREIFEN aus (+1).",
            "Saubere Shrimps anklicken kostet einen Punkt - Reinhild sieht ALLES." };
    }
}
