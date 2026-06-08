package com.shrimptopia.ui;

import com.shrimptopia.model.IconKind;
import java.awt.*;
import java.awt.geom.*;

/**
 * Selbstgezeichnete Vektor-Symbole (keine Emoji-Fonts nötig -> überall identisch).
 * Koordinaten sind relativ zum Zentrum (cx,cy); "size" ist die volle Kantenlänge.
 */
public final class Icons {

    private Icons() {}

    private static final Color WHITE = new Color(245, 250, 252);
    private static final Color INK   = new Color(22, 28, 33);

    // ---------- HUD-Ressourcensymbole (einfarbig) ----------

    public static void resource(Graphics2D g, IconKind k, double cx, double cy, double size, Color color) {
        switch (k) {
            case COIN   -> coin(g, cx, cy, size, color);
            case BOLT   -> bolt(g, cx, cy, size, color);
            case DROP   -> droplet(g, cx, cy, size, color, true);
            case LEAF   -> leaf(g, cx, cy, size, color);
            case SHRIMP -> shrimp(g, cx, cy, size, color);
            case PERSON -> person(g, cx, cy, size, color);
            case STAR   -> star(g, cx, cy, size, color);
            default     -> { }
        }
    }

    // ---------- Gebäudesymbole (auf farbiger Kachel, hell gezeichnet) ----------

    public static void building(Graphics2D g, IconKind k, double cx, double cy, double size) {
        switch (k) {
            case HQ    -> hq(g, cx, cy, size);
            case TANK  -> tank(g, cx, cy, size);
            case WATER -> waterworks(g, cx, cy, size);
            case ALGAE -> algae(g, cx, cy, size);
            case POWER -> bolt(g, cx, cy, size, Palette.POWER);
            case SOLAR -> solar(g, cx, cy, size);
            case HOUSE -> house(g, cx, cy, size);
            case SALES -> sales(g, cx, cy, size);
            case LAB   -> lab(g, cx, cy, size);
            case FOOD  -> food(g, cx, cy, size);
            case WATERHUB    -> waterhub(g, cx, cy, size);
            case GENLAB      -> genlab(g, cx, cy, size);
            case DOCK        -> dock(g, cx, cy, size);
            case MILITARY    -> military(g, cx, cy, size);
            case BLACKMARKET -> blackmarket(g, cx, cy, size);
            case VISITOR     -> visitor(g, cx, cy, size);
            case GARDEN      -> garden(g, cx, cy, size);
            default    -> { }
        }
    }

    /** Charakter-Portrait für Popups/Inspektor. */
    public static void portrait(Graphics2D g, IconKind k, double cx, double cy, double size, Color bg) {
        g.setColor(bg);
        g.fill(new Ellipse2D.Double(cx - size / 2, cy - size / 2, size, size));
        switch (k) {
            case PORTRAIT_GENERAL  -> faceGeneral(g, cx, cy, size);
            case PORTRAIT_DIPLOMAT -> faceDiplomat(g, cx, cy, size);
            case PORTRAIT_ADVISOR  -> faceAdvisor(g, cx, cy, size);
            case PORTRAIT_MAYOR    -> faceMayor(g, cx, cy, size);
            default                -> shrimp(g, cx, cy, size * 0.7, Palette.SHRIMP);
        }
    }

    // ===================== Primitive Symbole =====================

    private static void coin(Graphics2D g, double cx, double cy, double s, Color c) {
        double r = s * 0.46;
        g.setColor(c);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        g.setColor(darker(c, 0.75));
        g.setStroke(new BasicStroke((float) (s * 0.07)));
        g.draw(new Ellipse2D.Double(cx - r * 0.74, cy - r * 0.74, 2 * r * 0.74, 2 * r * 0.74));
        Font f = new Font("SansSerif", Font.BOLD, (int) Math.max(8, s * 0.62));
        g.setFont(f);
        g.setColor(darker(c, 0.55));
        drawCentered(g, "$", cx, cy);
    }

    private static void bolt(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D p = poly(cx, cy, s,
            0.16, -0.50,  -0.30, 0.08,  0.00, 0.08,
            -0.16, 0.50,   0.32, -0.10,  0.02, -0.10);
        g.setColor(c);
        g.fill(p);
        g.setColor(darker(c, 0.7));
        g.setStroke(new BasicStroke((float) (s * 0.04)));
        g.draw(p);
    }

    private static void droplet(Graphics2D g, double cx, double cy, double s, Color c, boolean highlight) {
        double r = s * 0.34;
        double cyBall = cy + s * 0.12;
        Path2D tip = poly(cx, cy, s, -0.30, 0.04,  0.0, -0.50,  0.30, 0.04);
        Area drop = new Area(tip);
        drop.add(new Area(new Ellipse2D.Double(cx - r, cyBall - r, 2 * r, 2 * r)));
        g.setColor(c);
        g.fill(drop);
        if (highlight) {
            g.setColor(brighter(c, 1.4));
            g.fill(new Ellipse2D.Double(cx - r * 0.5, cyBall - r * 0.55, r * 0.5, r * 0.7));
        }
    }

    private static void leaf(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D p = new Path2D.Double();
        p.moveTo(cx, cy - s * 0.48);
        p.quadTo(cx + s * 0.42, cy, cx, cy + s * 0.48);
        p.quadTo(cx - s * 0.42, cy, cx, cy - s * 0.48);
        p.closePath();
        g.setColor(c);
        g.fill(p);
        g.setColor(darker(c, 0.7));
        g.setStroke(new BasicStroke((float) (s * 0.05)));
        g.draw(new Line2D.Double(cx, cy - s * 0.40, cx, cy + s * 0.40));
    }

    private static void shrimp(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        g.setStroke(new BasicStroke((float) (s * 0.20), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Körper als Kringel (offene "C"-Kurve)
        g.draw(new Arc2D.Double(cx - s * 0.34, cy - s * 0.34, s * 0.68, s * 0.68, 35, 285, Arc2D.OPEN));
        // Schwanzfächer
        Path2D tail = poly(cx, cy, s, 0.30, -0.20, 0.50, -0.34, 0.46, -0.06);
        g.fill(tail);
        // kleine Beinchen
        g.setStroke(new BasicStroke((float) (s * 0.05), BasicStroke.CAP_ROUND, 0));
        for (int i = 0; i < 3; i++) {
            double a = Math.toRadians(150 + i * 28);
            double x = cx + Math.cos(a) * s * 0.20, y = cy + Math.sin(a) * s * 0.20;
            g.draw(new Line2D.Double(x, y, x + Math.cos(a) * s * 0.13, y + Math.sin(a) * s * 0.13));
        }
        // Auge
        g.setColor(INK);
        double ex = cx + Math.cos(Math.toRadians(40)) * s * 0.30;
        double ey = cy + Math.sin(Math.toRadians(40)) * s * 0.30;
        g.fill(new Ellipse2D.Double(ex - s * 0.05, ey - s * 0.05, s * 0.10, s * 0.10));
    }

    private static void person(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        double hr = s * 0.18;
        g.fill(new Ellipse2D.Double(cx - hr, cy - s * 0.40, 2 * hr, 2 * hr));
        Path2D body = poly(cx, cy, s, -0.28, 0.46, -0.20, 0.02, 0.20, 0.02, 0.28, 0.46);
        g.fill(body);
    }

    private static void star(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D p = new Path2D.Double();
        double ro = s * 0.5, ri = s * 0.21;
        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? ro : ri;
            double a = -Math.PI / 2 + i * Math.PI / 5;
            double x = cx + Math.cos(a) * r, y = cy + Math.sin(a) * r;
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        g.setColor(c);
        g.fill(p);
    }

    // ===================== Gebäude-Details =====================

    private static void hq(Graphics2D g, double cx, double cy, double s) {
        g.setColor(WHITE);
        g.fill(new RoundRectangle2D.Double(cx - s * 0.34, cy - s * 0.18, s * 0.68, s * 0.56, s * 0.1, s * 0.1));
        g.setColor(new Color(120, 132, 148));
        for (int i = 0; i < 3; i++)
            g.fill(new Rectangle2D.Double(cx - s * 0.24 + i * s * 0.20, cy - s * 0.06, s * 0.12, s * 0.12));
        // Fahne
        g.setColor(WHITE);
        g.setStroke(new BasicStroke((float) (s * 0.05)));
        g.draw(new Line2D.Double(cx, cy - s * 0.18, cx, cy - s * 0.5));
        g.setColor(Palette.ACCENT);
        g.fill(poly(cx, cy, s, 0.0, -0.50, 0.26, -0.42, 0.0, -0.34));
    }

    private static void tank(Graphics2D g, double cx, double cy, double s) {
        RoundRectangle2D body = new RoundRectangle2D.Double(
            cx - s * 0.42, cy - s * 0.34, s * 0.84, s * 0.74, s * 0.12, s * 0.12);
        // Wasser
        Shape clip = g.getClip();
        g.setClip(body);
        g.setColor(new Color(64, 180, 210, 200));
        g.fill(new Rectangle2D.Double(cx - s * 0.42, cy - s * 0.08, s * 0.84, s * 0.5));
        // Welle
        g.setColor(new Color(150, 225, 240));
        Path2D wave = new Path2D.Double();
        wave.moveTo(cx - s * 0.42, cy - s * 0.06);
        wave.quadTo(cx - s * 0.21, cy - s * 0.16, cx, cy - s * 0.06);
        wave.quadTo(cx + s * 0.21, cy + s * 0.04, cx + s * 0.42, cy - s * 0.06);
        g.setStroke(new BasicStroke((float) (s * 0.05)));
        g.draw(wave);
        g.setClip(clip);
        // kleiner Shrimp im Becken
        shrimp(g, cx + s * 0.02, cy + s * 0.14, s * 0.5, Palette.SHRIMP);
        // Glasrahmen
        g.setColor(WHITE);
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(body);
    }

    private static void waterworks(Graphics2D g, double cx, double cy, double s) {
        droplet(g, cx, cy - s * 0.06, s * 0.9, Palette.WATER, true);
        g.setColor(WHITE);
        g.fill(new Rectangle2D.Double(cx - s * 0.34, cy + s * 0.34, s * 0.20, s * 0.12));
        g.fill(new Rectangle2D.Double(cx + s * 0.14, cy + s * 0.34, s * 0.20, s * 0.12));
    }

    private static void algae(Graphics2D g, double cx, double cy, double s) {
        Color[] greens = { new Color(110, 200, 90), new Color(80, 170, 70), new Color(140, 215, 110) };
        double[][] b = { {-0.18, 0.10, 0.26}, {0.16, 0.04, 0.30}, {0.0, -0.20, 0.24}, {0.22, 0.26, 0.18}, {-0.24, 0.30, 0.16} };
        for (int i = 0; i < b.length; i++) {
            g.setColor(greens[i % greens.length]);
            double r = b[i][2] * s;
            g.fill(new Ellipse2D.Double(cx + b[i][0] * s - r, cy + b[i][1] * s - r, 2 * r, 2 * r));
        }
        leaf(g, cx + s * 0.04, cy - s * 0.06, s * 0.5, new Color(60, 150, 60));
    }

    private static void solar(Graphics2D g, double cx, double cy, double s) {
        Path2D panel = poly(cx, cy, s, -0.46, 0.30, -0.30, -0.34, 0.46, -0.30, 0.30, 0.34);
        g.setColor(new Color(40, 70, 120));
        g.fill(panel);
        g.setColor(new Color(120, 170, 230));
        g.setStroke(new BasicStroke((float) (s * 0.035)));
        g.draw(panel);
        for (int i = 1; i < 3; i++) {
            double t = i / 3.0;
            g.draw(new Line2D.Double(cx - 0.46 * s + t * 0.92 * s, cy + 0.30 * s - t * 0.0,
                                     cx - 0.30 * s + t * 0.92 * s, cy - 0.34 * s));
        }
        g.draw(new Line2D.Double(cx - 0.38 * s, cy, cx + 0.38 * s, cy));
    }

    private static void house(Graphics2D g, double cx, double cy, double s) {
        g.setColor(WHITE);
        g.fill(poly(cx, cy, s, -0.40, -0.04, 0.0, -0.42, 0.40, -0.04));   // Dach
        g.fill(new Rectangle2D.Double(cx - s * 0.30, cy - s * 0.04, s * 0.60, s * 0.44)); // Wand
        g.setColor(new Color(150, 124, 92));
        g.fill(new Rectangle2D.Double(cx - s * 0.08, cy + s * 0.10, s * 0.16, s * 0.30)); // Tür
    }

    private static void sales(Graphics2D g, double cx, double cy, double s) {
        g.setColor(WHITE);
        double[] h = { 0.22, 0.40, 0.60 };
        for (int i = 0; i < 3; i++) {
            double bx = cx - s * 0.34 + i * s * 0.24;
            double bh = h[i] * s;
            g.fill(new RoundRectangle2D.Double(bx, cy + s * 0.34 - bh, s * 0.16, bh, s * 0.04, s * 0.04));
        }
        g.setColor(Palette.GOOD);  // Aufwärtspfeil
        g.setStroke(new BasicStroke((float) (s * 0.07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx - s * 0.30, cy + s * 0.02, cx + s * 0.30, cy - s * 0.34));
        g.fill(poly(cx, cy, s, 0.30, -0.34, 0.14, -0.30, 0.26, -0.16));
    }

    private static void lab(Graphics2D g, double cx, double cy, double s) {
        Path2D flask = new Path2D.Double();
        flask.moveTo(cx - s * 0.10, cy - s * 0.40);
        flask.lineTo(cx - s * 0.10, cy - s * 0.06);
        flask.lineTo(cx - s * 0.34, cy + s * 0.38);
        flask.lineTo(cx + s * 0.34, cy + s * 0.38);
        flask.lineTo(cx + s * 0.10, cy - s * 0.06);
        flask.lineTo(cx + s * 0.10, cy - s * 0.40);
        g.setColor(new Color(170, 110, 200, 230));
        g.fill(flask);
        g.setColor(WHITE);
        g.setStroke(new BasicStroke((float) (s * 0.055), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(flask);
        g.draw(new Line2D.Double(cx - s * 0.16, cy - s * 0.40, cx + s * 0.16, cy - s * 0.40));
        g.setColor(new Color(220, 180, 240));   // Bläschen
        g.fill(new Ellipse2D.Double(cx - s * 0.06, cy + s * 0.16, s * 0.10, s * 0.10));
        g.fill(new Ellipse2D.Double(cx + s * 0.08, cy + s * 0.22, s * 0.07, s * 0.07));
    }

    private static void food(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(70, 90, 100));
        g.fill(new Ellipse2D.Double(cx - s * 0.34, cy - s * 0.34, s * 0.68, s * 0.68)); // Teller
        g.setColor(WHITE);
        g.setStroke(new BasicStroke((float) (s * 0.06), BasicStroke.CAP_ROUND, 0));
        // Gabel
        double fx = cx - s * 0.16;
        for (int i = -1; i <= 1; i++)
            g.draw(new Line2D.Double(fx + i * s * 0.06, cy - s * 0.26, fx + i * s * 0.06, cy - s * 0.06));
        g.draw(new Line2D.Double(fx, cy - s * 0.06, fx, cy + s * 0.28));
        // Messer
        double kx = cx + s * 0.16;
        g.draw(new Line2D.Double(kx, cy - s * 0.26, kx, cy + s * 0.28));
        g.setStroke(new BasicStroke((float) (s * 0.12), BasicStroke.CAP_ROUND, 0));
        g.draw(new Line2D.Double(kx, cy - s * 0.26, kx, cy - s * 0.02));
    }

    // ===================== v2-Gebäude =====================

    private static void waterhub(Graphics2D g, double cx, double cy, double s) {
        droplet(g, cx - s * 0.16, cy - s * 0.08, s * 0.6, Palette.WATER, true);
        droplet(g, cx + s * 0.18, cy + s * 0.02, s * 0.5, brighter(Palette.WATER, 1.1), false);
        g.setColor(WHITE);
        for (int i = 0; i < 3; i++)
            g.fill(new Rectangle2D.Double(cx - s * 0.36 + i * s * 0.24, cy + s * 0.34, s * 0.16, s * 0.10));
    }

    private static void genlab(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(210, 170, 240));
        g.setStroke(new BasicStroke((float) (s * 0.07), BasicStroke.CAP_ROUND, 0));
        Path2D a = new Path2D.Double(), b = new Path2D.Double();
        for (int i = 0; i <= 16; i++) {
            double t = i / 16.0, y = cy - s * 0.42 + t * s * 0.84;
            double dx = Math.sin(t * Math.PI * 2) * s * 0.22;
            if (i == 0) { a.moveTo(cx + dx, y); b.moveTo(cx - dx, y); }
            else { a.lineTo(cx + dx, y); b.lineTo(cx - dx, y); }
        }
        g.draw(a); g.draw(b);
        g.setStroke(new BasicStroke((float) (s * 0.04)));
        for (int i = 2; i <= 14; i += 3) {
            double t = i / 16.0, y = cy - s * 0.42 + t * s * 0.84, dx = Math.sin(t * Math.PI * 2) * s * 0.22;
            g.draw(new Line2D.Double(cx - dx, y, cx + dx, y));
        }
    }

    private static void dock(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(70, 180, 190));
        g.fill(new Rectangle2D.Double(cx - s * 0.38, cy + s * 0.04, s * 0.4, s * 0.34));
        g.setColor(new Color(220, 170, 70));
        g.fill(new Rectangle2D.Double(cx - s * 0.30, cy - s * 0.26, s * 0.4, s * 0.3));
        g.setColor(WHITE);
        g.setStroke(new BasicStroke((float) (s * 0.05)));
        g.draw(new Rectangle2D.Double(cx - s * 0.38, cy + s * 0.04, s * 0.4, s * 0.34));
        g.draw(new Rectangle2D.Double(cx - s * 0.30, cy - s * 0.26, s * 0.4, s * 0.3));
        // Export-Pfeil
        g.setColor(Palette.GOOD);
        g.setStroke(new BasicStroke((float) (s * 0.08), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx + s * 0.22, cy + s * 0.30, cx + s * 0.40, cy - s * 0.10));
        g.fill(poly(cx, cy, s, 0.40, -0.10, 0.24, -0.04, 0.38, 0.10));
    }

    private static void military(Graphics2D g, double cx, double cy, double s) {
        g.setColor(WHITE);
        g.setStroke(new BasicStroke((float) (s * 0.09), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            double y = cy - s * 0.22 + i * s * 0.22;
            g.draw(new Line2D.Double(cx - s * 0.26, y, cx, y + s * 0.14));
            g.draw(new Line2D.Double(cx, y + s * 0.14, cx + s * 0.26, y));
        }
    }

    private static void blackmarket(Graphics2D g, double cx, double cy, double s) {
        coin(g, cx + s * 0.14, cy + s * 0.14, s * 0.5, Palette.MONEY);
        // Domino-Maske
        g.setColor(new Color(28, 26, 34));
        g.fill(new RoundRectangle2D.Double(cx - s * 0.40, cy - s * 0.30, s * 0.62, s * 0.26, s * 0.2, s * 0.2));
        g.setColor(new Color(200, 200, 210));
        g.fill(new Ellipse2D.Double(cx - s * 0.30, cy - s * 0.24, s * 0.14, s * 0.13));
        g.fill(new Ellipse2D.Double(cx - s * 0.10, cy - s * 0.24, s * 0.14, s * 0.13));
    }

    private static void visitor(Graphics2D g, double cx, double cy, double s) {
        g.setColor(WHITE);
        g.fill(new RoundRectangle2D.Double(cx - s * 0.38, cy - s * 0.16, s * 0.76, s * 0.40, s * 0.08, s * 0.08));
        g.setColor(new Color(210, 150, 70));
        g.fill(new Ellipse2D.Double(cx - s * 0.05, cy - s * 0.05, s * 0.1, s * 0.1));
        star(g, cx, cy - s * 0.34, s * 0.34, Palette.MONEY);
    }

    private static void garden(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(70, 150, 200, 200));
        g.fill(new Ellipse2D.Double(cx - s * 0.34, cy + s * 0.06, s * 0.68, s * 0.30));
        leaf(g, cx - s * 0.18, cy - s * 0.10, s * 0.5, new Color(90, 175, 80));
        leaf(g, cx + s * 0.04, cy - s * 0.18, s * 0.55, new Color(110, 195, 95));
        leaf(g, cx + s * 0.22, cy - s * 0.06, s * 0.45, new Color(70, 150, 70));
    }

    // ===================== Charakter-Portraits =====================

    private static final Color SKIN = new Color(236, 200, 168);

    private static void headBase(Graphics2D g, double cx, double cy, double s) {
        g.setColor(SKIN);
        g.fill(new Ellipse2D.Double(cx - s * 0.26, cy - s * 0.30, s * 0.52, s * 0.58));
    }
    private static void eyes(Graphics2D g, double cx, double cy, double s, double spread) {
        g.setColor(INK);
        g.fill(new Ellipse2D.Double(cx - spread * s - s * 0.04, cy - s * 0.06, s * 0.07, s * 0.08));
        g.fill(new Ellipse2D.Double(cx + spread * s - s * 0.03, cy - s * 0.06, s * 0.07, s * 0.08));
    }

    private static void faceAdvisor(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(120, 80, 50));   // Haar
        g.fill(new Ellipse2D.Double(cx - s * 0.30, cy - s * 0.36, s * 0.60, s * 0.34));
        headBase(g, cx, cy, s);
        // Brille
        g.setColor(new Color(40, 44, 52));
        g.setStroke(new BasicStroke((float) (s * 0.035)));
        g.draw(new Ellipse2D.Double(cx - s * 0.20, cy - s * 0.10, s * 0.15, s * 0.13));
        g.draw(new Ellipse2D.Double(cx + s * 0.05, cy - s * 0.10, s * 0.15, s * 0.13));
        g.draw(new Line2D.Double(cx - s * 0.05, cy - s * 0.04, cx + s * 0.05, cy - s * 0.04));
        eyes(g, cx, cy, s, 0.12);
        g.setColor(new Color(190, 90, 90));
        g.setStroke(new BasicStroke((float) (s * 0.03), BasicStroke.CAP_ROUND, 0));
        g.draw(new Arc2D.Double(cx - s * 0.08, cy + s * 0.04, s * 0.16, s * 0.10, 200, 140, Arc2D.OPEN));
    }

    private static void faceMayor(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(60, 60, 70));
        g.fill(new Ellipse2D.Double(cx - s * 0.28, cy - s * 0.34, s * 0.56, s * 0.26));
        headBase(g, cx, cy, s);
        eyes(g, cx, cy, s, 0.11);
        // Anzug-Kragen + Krawatte
        g.setColor(new Color(40, 50, 70));
        g.fill(poly(cx, cy, s, -0.30, 0.50, -0.10, 0.28, 0.10, 0.28, 0.30, 0.50));
        g.setColor(new Color(180, 70, 80));
        g.fill(poly(cx, cy, s, 0.0, 0.28, -0.05, 0.50, 0.05, 0.50));
    }

    private static void faceGeneral(Graphics2D g, double cx, double cy, double s) {
        headBase(g, cx, cy, s);
        // Helm
        g.setColor(new Color(90, 110, 80));
        g.fill(new Arc2D.Double(cx - s * 0.30, cy - s * 0.42, s * 0.60, s * 0.50, 0, 180, Arc2D.PIE));
        g.fill(new Rectangle2D.Double(cx - s * 0.30, cy - s * 0.18, s * 0.60, s * 0.05));
        // weit aufgerissene Augen
        g.setColor(WHITE);
        g.fill(new Ellipse2D.Double(cx - s * 0.18, cy - s * 0.08, s * 0.13, s * 0.13));
        g.fill(new Ellipse2D.Double(cx + s * 0.05, cy - s * 0.08, s * 0.13, s * 0.13));
        g.setColor(INK);
        g.fill(new Ellipse2D.Double(cx - s * 0.13, cy - s * 0.04, s * 0.06, s * 0.06));
        g.fill(new Ellipse2D.Double(cx + s * 0.09, cy - s * 0.04, s * 0.06, s * 0.06));
        // Zigarre
        g.setColor(new Color(110, 70, 40));
        g.fill(new Rectangle2D.Double(cx + s * 0.02, cy + s * 0.14, s * 0.22, s * 0.05));
        g.setColor(new Color(255, 120, 60));
        g.fill(new Ellipse2D.Double(cx + s * 0.22, cy + s * 0.135, s * 0.05, s * 0.05));
    }

    private static void faceDiplomat(Graphics2D g, double cx, double cy, double s) {
        g.setColor(new Color(30, 30, 36));   // gegeltes Haar
        g.fill(new Ellipse2D.Double(cx - s * 0.28, cy - s * 0.36, s * 0.56, s * 0.24));
        g.fill(poly(cx, cy, s, -0.28, -0.20, 0.28, -0.30, 0.28, -0.12, -0.28, -0.06));
        headBase(g, cx, cy, s);
        eyes(g, cx, cy, s, 0.11);
        // dünner Schnurrbart
        g.setColor(new Color(30, 30, 36));
        g.setStroke(new BasicStroke((float) (s * 0.03), BasicStroke.CAP_ROUND, 0));
        g.draw(new Line2D.Double(cx - s * 0.10, cy + s * 0.10, cx + s * 0.10, cy + s * 0.10));
        // schmieriges Lächeln
        g.setColor(new Color(150, 70, 70));
        g.draw(new Arc2D.Double(cx - s * 0.10, cy + s * 0.10, s * 0.20, s * 0.10, 200, 140, Arc2D.OPEN));
        // Anzug
        g.setColor(new Color(50, 70, 60));
        g.fill(poly(cx, cy, s, -0.30, 0.50, -0.10, 0.28, 0.10, 0.28, 0.30, 0.50));
    }

    // ===================== Helfer =====================

    /** Baut einen geschlossenen Pfad aus (dx,dy)-Paaren relativ zu (cx,cy), skaliert mit s. */
    private static Path2D poly(double cx, double cy, double s, double... pts) {
        Path2D p = new Path2D.Double();
        for (int i = 0; i < pts.length; i += 2) {
            double x = cx + pts[i] * s, y = cy + pts[i + 1] * s;
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    private static void drawCentered(Graphics2D g, String txt, double cx, double cy) {
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(txt);
        int h = fm.getAscent();
        g.drawString(txt, (float) (cx - w / 2.0), (float) (cy + h / 2.0 - fm.getDescent() / 2.0));
    }

    static Color darker(Color c, double f) {
        return new Color((int) (c.getRed() * f), (int) (c.getGreen() * f), (int) (c.getBlue() * f));
    }

    static Color brighter(Color c, double f) {
        return new Color(Math.min(255, (int) (c.getRed() * f)),
                         Math.min(255, (int) (c.getGreen() * f)),
                         Math.min(255, (int) (c.getBlue() * f)));
    }
}
