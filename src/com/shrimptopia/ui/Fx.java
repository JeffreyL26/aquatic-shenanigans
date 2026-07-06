package com.shrimptopia.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RoundRectangle2D;

/**
 * Gemeinsame Render-Helfer für den visuellen Politur-Durchgang: weiche Schlagschatten,
 * Verlaufs-Karten und ein Glanzstreifen, damit jedes Panel dieselbe Tiefe bekommt statt
 * flacher Volltonflächen. Bewusst günstig gehalten (kein echtes Gaussian-Blur) - ein paar
 * versetzte, halbtransparente Ringe reichen für den Eindruck von Tiefe auf Kachel-UI.
 */
public final class Fx {
    private Fx() {}

    /** Weicher Schlagschatten unter einer Rundrect-Fläche. */
    public static void shadow(Graphics2D g, double x, double y, double w, double h, double arc, int depth) {
        for (int i = depth; i >= 1; i--) {
            int alpha = Math.max(0, Math.min(60, (int) (60.0 * i / depth)));
            g.setColor(new Color(0, 0, 0, alpha / depth + 6));
            g.fill(new RoundRectangle2D.Double(x - i * 0.3, y - i * 0.2 + i * 1.1, w + i * 0.6, h + i * 0.6, arc + i, arc + i));
        }
    }

    /** Vertikaler Farbverlauf über eine Rundrect-Fläche. */
    public static void vGradient(Graphics2D g, double x, double y, double w, double h, double arc, Color top, Color bottom) {
        Paint old = g.getPaint();
        g.setPaint(new GradientPaint((float) x, (float) y, top, (float) x, (float) (y + h), bottom));
        g.fill(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        g.setPaint(old);
    }

    /** Komplette Karte: Schatten + Verlauf-Füllung + Rand in einem Aufruf. */
    public static void card(Graphics2D g, double x, double y, double w, double h, double arc,
                             Color top, Color bottom, Color border, float borderWidth) {
        shadow(g, x, y, w, h, arc, 5);
        vGradient(g, x, y, w, h, arc, top, bottom);
        if (border != null) {
            g.setColor(border);
            g.setStroke(new BasicStroke(borderWidth));
            g.draw(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        }
    }

    /** Dünner Glanzstreifen am oberen Rand einer Karte - simuliert sanften Hochglanz. */
    public static void topSheen(Graphics2D g, double x, double y, double w, double arc) {
        Paint old = g.getPaint();
        double sheenH = Math.max(6, arc * 1.4);
        g.setPaint(new GradientPaint((float) x, (float) y, new Color(255, 255, 255, 24),
            (float) x, (float) (y + sheenH), new Color(255, 255, 255, 0)));
        g.fill(new RoundRectangle2D.Double(x + 1, y + 1, w - 2, sheenH, arc, arc));
        g.setPaint(old);
    }

    public static Color mix(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
            (int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t),
            (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
    }

    public static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }
}
