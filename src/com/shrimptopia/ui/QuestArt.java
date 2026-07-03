package com.shrimptopia.ui;

import com.shrimptopia.model.IconKind;
import java.awt.*;
import java.awt.geom.*;

/**
 * Hexagon-Knoten und handgezeichnete Mini-Illustrationen für den Quest-Baum.
 * Jede Quest-ID bekommt ein zur Mission passendes Vektor-Motiv (gleicher Stil wie Icons.java:
 * helle Formen auf getönter Kachel, keine Emoji-Fonts). Im "grau"-Zustand (noch nicht
 * freigeschaltet / nicht gewählt) wird alles entsättigt gezeichnet.
 */
public final class QuestArt {

    private QuestArt() {}

    private static final Color INK  = new Color(240, 246, 249);
    private static final Color INK2 = new Color(180, 196, 205);
    private static final Color DIM  = new Color(110, 122, 130);
    private static final Color DIM2 = new Color(84, 94, 101);

    /** Pointy-Top-Hexagon um (cx,cy) mit "Radius" r (Ecke oben). */
    public static Path2D.Double hexagon(double cx, double cy, double r) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            double a = Math.toRadians(60 * i - 90);
            double x = cx + r * Math.cos(a), y = cy + r * Math.sin(a);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    /** Zeichnet die Missions-Illustration zentriert in einem Hex (s = Motivgröße). */
    public static void illustration(Graphics2D g, String questId, double cx, double cy, double s, boolean gray) {
        Color ink = gray ? DIM : INK, sub = gray ? DIM2 : INK2;
        switch (questId == null ? "" : questId) {
            // ---- Aufstieg & Marketing ----
            case "era_halle"     -> garageUp(g, cx, cy, s, ink, sub, gray ? DIM : new Color(96, 220, 120));
            case "mark_intro"    -> megaphone(g, cx, cy, s, ink, sub);
            case "mark_social"   -> phoneHeart(g, cx, cy, s, ink, gray ? DIM : new Color(255, 110, 150));
            case "mark_agency"   -> tvSet(g, cx, cy, s, ink, sub);
            // ---- Behörden ----
            case "beh_formular"  -> { sheet(g, cx, cy, s, ink, sub); stamp(g, cx + s*0.26, cy + s*0.24, s*0.3, gray ? DIM : new Color(220, 90, 90)); }
            case "beh_pruefung"  -> clipboard(g, cx, cy, s, ink, sub);
            case "beh_plakette"  -> medal(g, cx, cy, s, gray ? DIM : new Color(255, 205, 86), ink);
            case "beh_steuer"    -> { Icons.resource(g, IconKind.COIN, cx - s*0.16, cy, s*0.62, gray ? DIM : new Color(255, 205, 86)); paragraph(g, cx + s*0.26, cy, s*0.55, ink); }
            // ---- Tierschutz ----
            case "tier_wuerde"   -> { heart(g, cx, cy - s*0.1, s*0.6, gray ? DIM : new Color(255, 110, 150)); Icons.resource(g, IconKind.SHRIMP, cx, cy + s*0.26, s*0.42, sub); }
            case "tier_demo"     -> protestSign(g, cx, cy, s, ink, sub);
            case "tier_gewerkschaft" -> fist(g, cx, cy, s, ink, sub);
            // ---- Influencer ----
            case "inf_viral"     -> playButton(g, cx, cy, s, ink, gray ? DIM : new Color(255, 92, 92));
            case "inf_sponsor"   -> Icons.resource(g, IconKind.CAN, cx, cy, s * 0.85, gray ? DIM : new Color(40, 224, 220));
            case "inf_cancel"    -> { hash(g, cx - s*0.18, cy - s*0.05, s*0.5, ink); flame(g, cx + s*0.22, cy + s*0.1, s*0.55, gray ? DIM : new Color(255, 140, 60), gray ? DIM2 : new Color(255, 205, 86)); }
            // ---- Kyle ----
            case "kyle_intro"    -> { downvote(g, cx - s*0.1, cy, s*0.75, gray ? DIM : new Color(122, 156, 255)); bubble(g, cx + s*0.26, cy - s*0.22, s*0.5, ink, sub); }
            case "kyle_1"        -> oneStar(g, cx, cy, s, gray ? DIM : new Color(255, 205, 86), ink);
            case "kyle_2"        -> priceTag(g, cx, cy, s, ink, gray ? DIM : new Color(255, 110, 90));
            case "kyle_3"        -> { playButton(g, cx - s*0.12, cy - s*0.1, s*0.8, sub, gray ? DIM2 : new Color(120, 90, 70)); playButton(g, cx + s*0.12, cy + s*0.12, s*0.8, ink, gray ? DIM : new Color(255, 92, 92)); }
            case "kyle_4"        -> basement(g, cx, cy, s, ink, sub, gray ? DIM : new Color(255, 205, 86));
            case "kyle_5"        -> prison(g, cx, cy, s, ink, sub, gray ? DIM : new Color(255, 118, 104));
            // ---- Krabbo ----
            case "konk_krabbo"   -> { factory(g, cx - s*0.08, cy + s*0.06, s*0.8, ink, sub); claw(g, cx + s*0.3, cy - s*0.24, s*0.42, gray ? DIM : new Color(255, 120, 90)); }
            case "konk_angebot"  -> briefcase(g, cx, cy, s, ink, sub);
            // ---- Einzelquests ----
            case "kat_blackout"  -> bulbOff(g, cx, cy, s, ink, sub);
            case "kat_algen"     -> { Icons.resource(g, IconKind.LEAF, cx - s*0.16, cy - s*0.1, s*0.6, gray ? DIM : new Color(124, 200, 92)); Icons.resource(g, IconKind.LEAF, cx + s*0.2, cy + s*0.16, s*0.45, sub); }
            case "presse_kritiker" -> { Icons.resource(g, IconKind.STAR, cx - s*0.14, cy - s*0.08, s*0.55, gray ? DIM : new Color(255, 205, 86)); fork(g, cx + s*0.26, cy + s*0.05, s*0.6, ink); }
            case "kat_moewen"    -> seagull(g, cx, cy, s, ink, sub);
            case "idee_spa"      -> spa(g, cx, cy, s, ink, gray ? DIM : new Color(64, 170, 255));
            case "geld_investor" -> { Icons.resource(g, IconKind.COIN, cx, cy + s*0.12, s*0.6, gray ? DIM : new Color(255, 205, 86)); arrowUp(g, cx + s*0.26, cy - s*0.2, s*0.45, gray ? DIM : new Color(96, 220, 120)); }
            // ---- Krillkill ----
            case "krillkill_intro" -> helmet(g, cx, cy, s, ink, sub);
            case "krillkill_1"   -> whistle(g, cx, cy, s, ink, sub);
            case "krillkill_2"   -> drumstick(g, cx, cy, s, ink, sub);
            case "krillkill_3"   -> dumbbell(g, cx, cy, s, ink, sub);
            case "krillkill_4"   -> { sword(g, cx - s*0.14, cy, s*0.75, ink, sub); claw(g, cx + s*0.26, cy - s*0.05, s*0.5, gray ? DIM : new Color(255, 120, 90)); }
            case "krillkill_5"   -> flask(g, cx, cy, s, ink, gray ? DIM : new Color(150, 220, 120));
            case "krillkill_6"   -> { Icons.resource(g, IconKind.SHIELD, cx, cy, s*0.85, gray ? DIM : new Color(150, 165, 90)); Icons.resource(g, IconKind.SHRIMP, cx, cy - s*0.02, s*0.4, ink); }
            case "krillkill_7"   -> soupPot(g, cx, cy, s, ink, sub);
            // ---- Akwanov ----
            case "akwanov_intro" -> teaGlass(g, cx, cy, s, ink, gray ? DIM : new Color(230, 150, 60));
            case "akwanov_1"     -> contract(g, cx, cy, s, ink, sub);
            case "akwanov_2"     -> percent(g, cx, cy, s, ink);
            case "akwanov_3"     -> { Icons.resource(g, IconKind.DROP, cx - s*0.16, cy, s*0.6, gray ? DIM : new Color(64, 170, 255)); Icons.resource(g, IconKind.LEAF, cx + s*0.2, cy + s*0.08, s*0.45, sub); }
            case "akwanov_4"     -> spyGlasses(g, cx, cy, s, ink, sub);
            case "akwanov_5"     -> folderEye(g, cx, cy, s, ink, sub);
            case "akwanov_6"     -> noEntry(g, cx, cy, s, gray ? DIM : new Color(235, 90, 90), ink);
            case "akwanov_7"     -> plug(g, cx, cy, s, ink, sub);
            case "akwanov_8"     -> { flagPair(g, cx, cy, s, ink, sub, gray ? DIM : new Color(90, 170, 150)); }
            // ---- Becken-3-Vorfall ----
            case "boost_1"       -> spiltCan(g, cx, cy, s, ink, gray ? DIM : new Color(40, 224, 220));
            case "boost_2"       -> { fist(g, cx - s*0.08, cy - s*0.04, s*0.8, ink, sub); Icons.resource(g, IconKind.SHRIMP, cx + s*0.28, cy + s*0.26, s*0.36, gray ? DIM : new Color(255, 118, 104)); }
            case "boost_3"       -> coupFlag(g, cx, cy, s, ink, gray ? DIM : new Color(124, 200, 92));
            case "boost_4"       -> nightVision(g, cx, cy, s, ink, gray ? DIM : new Color(96, 220, 120));
            case "boost_5"       -> ministry(g, cx, cy, s, ink, sub);
            case "boost_6"       -> dove(g, cx, cy, s, ink, gray ? DIM : new Color(124, 200, 92));
            // ---- Usbekistan-Verflechtungen ----
            case "sts6_dmitri"   -> pudding(g, cx, cy, s, ink, gray ? DIM : new Color(255, 120, 90));
            case "sts6_taschkent" -> kulisse(g, cx, cy, s, ink, sub);
            case "union_akwanov" -> { globe(g, cx - s*0.06, cy, s*0.85, ink, sub); g.setColor(gray ? DIM : new Color(40, 224, 220)); g.fillRoundRect((int)(cx + s*0.2), (int)(cy - s*0.3), (int)(s*0.16), (int)(s*0.16), 4, 4); }
            case "dmitri_zeugnis" -> report(g, cx, cy, s, ink, sub, gray ? DIM : new Color(220, 90, 90));
            case "dmitri_kantine" -> schnitzel(g, cx, cy, s, ink, gray ? DIM : new Color(214, 160, 90));
            // ---- Konflikte ----
            case "conf_border"   -> drone(g, cx, cy, s, ink, sub);
            case "conf_flotilla" -> truck(g, cx, cy, s, ink, sub);
            case "conf_raid"     -> cutter(g, cx, cy, s, ink, sub);
            case "conf_siege"    -> wall(g, cx, cy, s, ink, sub);
            case "conf_war"      -> { sword(g, cx - s*0.12, cy, s*0.8, ink, sub); swordMirror(g, cx + s*0.12, cy, s*0.8, ink, sub); }
            // ---- Spielenden ----
            case "end_imperator" -> crown(g, cx, cy, s, gray ? DIM : new Color(255, 205, 86), ink);
            case "end_vertical"  -> Icons.resource(g, IconKind.ROBOT, cx, cy, s*0.85, gray ? DIM : new Color(150, 168, 196));
            case "end_protein"   -> { Icons.resource(g, IconKind.SHIELD, cx, cy, s*0.85, gray ? DIM : new Color(150, 165, 90)); star5(g, cx, cy - s*0.02, s*0.3, gray ? DIM2 : new Color(255, 205, 86)); }
            case "end_union"     -> flagPair(g, cx, cy, s, ink, sub, gray ? DIM : new Color(90, 170, 150));
            case "end_saint"     -> { halo(g, cx, cy - s*0.3, s*0.5, gray ? DIM : new Color(255, 205, 86)); Icons.resource(g, IconKind.SHRIMP, cx, cy + s*0.08, s*0.6, gray ? DIM : new Color(255, 118, 104)); }
            case "end_meme"      -> trophy(g, cx, cy, s, gray ? DIM : new Color(255, 205, 86), ink);
            default              -> Icons.resource(g, IconKind.SHRIMP, cx, cy, s * 0.7, gray ? DIM : new Color(255, 118, 104));
        }
    }

    // ===================== Motive =====================

    private static void garageUp(Graphics2D g, double cx, double cy, double s, Color ink, Color sub, Color accent) {
        g.setColor(ink);
        Path2D.Double roof = new Path2D.Double();
        roof.moveTo(cx - s*0.42, cy - s*0.02); roof.lineTo(cx, cy - s*0.34); roof.lineTo(cx + s*0.42, cy - s*0.02);
        roof.closePath();
        g.fill(roof);
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.34, cy - s*0.02, s*0.68, s*0.36));
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.04)));
        for (int i = 0; i < 3; i++)
            g.draw(new Line2D.Double(cx - s*0.22, cy + s*0.06 + i * s*0.09, cx + s*0.22, cy + s*0.06 + i * s*0.09));
        arrowUp(g, cx + s*0.34, cy - s*0.28, s*0.4, accent);
    }

    private static void megaphone(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        Path2D.Double horn = new Path2D.Double();
        horn.moveTo(cx - s*0.34, cy - s*0.1); horn.lineTo(cx + s*0.18, cy - s*0.34);
        horn.lineTo(cx + s*0.18, cy + s*0.22); horn.lineTo(cx - s*0.34, cy + s*0.02);
        horn.closePath();
        g.fill(horn);
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.44, cy - s*0.12, s*0.12, s*0.26, 4, 4));
        g.fill(new RoundRectangle2D.Double(cx - s*0.3, cy + s*0.04, s*0.1, s*0.3, 4, 4));
        g.setStroke(new BasicStroke((float) (s * 0.06), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++)
            g.draw(new Line2D.Double(cx + s*0.26, cy - s*0.26 + i * s*0.14, cx + s*0.4, cy - s*0.3 + i * s*0.16));
    }

    private static void phoneHeart(Graphics2D g, double cx, double cy, double s, Color ink, Color accent) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.18, cy - s*0.4, s*0.36, s*0.8, s*0.1, s*0.1));
        g.setColor(new Color(24, 30, 36));
        g.fill(new RoundRectangle2D.Double(cx - s*0.14, cy - s*0.32, s*0.28, s*0.58, 4, 4));
        heart(g, cx, cy - s*0.03, s*0.34, accent);
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.03, cy + s*0.3, s*0.06, s*0.06));
    }

    private static void tvSet(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.4, cy - s*0.26, s*0.8, s*0.52, 6, 6));
        g.setColor(new Color(24, 30, 36));
        g.fill(new RoundRectangle2D.Double(cx - s*0.34, cy - s*0.2, s*0.68, s*0.4, 4, 4));
        Icons.resource(g, com.shrimptopia.model.IconKind.SHRIMP, cx, cy, s * 0.34, sub);
        g.setColor(sub);
        g.setStroke(new BasicStroke((float) (s * 0.05), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx - s*0.14, cy + s*0.38, cx - s*0.05, cy + s*0.26));
        g.draw(new Line2D.Double(cx + s*0.14, cy + s*0.38, cx + s*0.05, cy + s*0.26));
        g.draw(new Line2D.Double(cx - s*0.1, cy - s*0.42, cx - s*0.02, cy - s*0.28));
        g.draw(new Line2D.Double(cx + s*0.16, cy - s*0.44, cx + s*0.04, cy - s*0.28));
    }

    private static void pudding(Graphics2D g, double cx, double cy, double s, Color ink, Color jelly) {
        // Teller
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.42, cy + s*0.18, s*0.84, s*0.18));
        // wackelnder Pudding (leicht schief = wackelt)
        g.setColor(jelly);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx - s*0.3, cy + s*0.24);
        p.curveTo(cx - s*0.36, cy - s*0.1, cx - s*0.14, cy - s*0.34, cx + s*0.06, cy - s*0.3);
        p.curveTo(cx + s*0.3, cy - s*0.26, cx + s*0.38, cy, cx + s*0.3, cy + s*0.24);
        p.closePath();
        g.fill(p);
        g.setColor(new Color(255, 255, 255, 110));
        g.fill(new Ellipse2D.Double(cx - s*0.2, cy - s*0.22, s*0.14, s*0.1));
        // Wackel-Linien
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.04), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(cx - s*0.44, cy - s*0.3, cx - s*0.4, cy - s*0.22, cx - s*0.44, cy - s*0.14));
        g.draw(new QuadCurve2D.Double(cx + s*0.46, cy - s*0.26, cx + s*0.42, cy - s*0.18, cx + s*0.46, cy - s*0.1));
    }

    private static void kulisse(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        // Fassade (von schräg hinten gesehen: vorn hui ...)
        g.setColor(ink);
        g.fill(new Rectangle2D.Double(cx - s*0.4, cy - s*0.32, s*0.5, s*0.62));
        g.setColor(new Color(24, 30, 36));
        for (int r = 0; r < 2; r++)
            for (int c = 0; c < 2; c++)
                g.fill(new Rectangle2D.Double(cx - s*0.34 + c * s*0.22, cy - s*0.24 + r * s*0.24, s*0.14, s*0.14));
        // ... hinten Stützbalken (Pappkulisse)
        g.setColor(sub);
        g.setStroke(new BasicStroke((float) (s * 0.07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx + s*0.1, cy - s*0.2, cx + s*0.4, cy + s*0.3));
        g.draw(new Line2D.Double(cx + s*0.1, cy + s*0.06, cx + s*0.34, cy + s*0.3));
        g.draw(new Line2D.Double(cx + s*0.16, cy + s*0.3, cx + s*0.44, cy + s*0.3));
    }

    private static void globe(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new Ellipse2D.Double(cx - s*0.34, cy - s*0.34, s*0.68, s*0.68));
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.045)));
        g.draw(new Ellipse2D.Double(cx - s*0.34, cy - s*0.34, s*0.68, s*0.68));
        g.draw(new Ellipse2D.Double(cx - s*0.14, cy - s*0.34, s*0.28, s*0.68));
        g.draw(new Line2D.Double(cx - s*0.34, cy, cx + s*0.34, cy));
        g.draw(new Arc2D.Double(cx - s*0.34, cy - s*0.5, s*0.68, s*0.68, 200, 140, Arc2D.OPEN));
        g.draw(new Arc2D.Double(cx - s*0.34, cy - s*0.18, s*0.68, s*0.68, 20, 140, Arc2D.OPEN));
    }

    private static void report(Graphics2D g, double cx, double cy, double s, Color ink, Color sub, Color stampC) {
        // dicker gebundener Bericht
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.3, cy - s*0.36, s*0.6, s*0.72, 4, 4));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.34, cy - s*0.4, s*0.6, s*0.72, 4, 4));
        // Spiral-Bindung
        g.setColor(sub);
        for (int i = 0; i < 5; i++)
            g.fill(new Ellipse2D.Double(cx - s*0.38, cy - s*0.34 + i * s*0.14, s*0.07, s*0.07));
        // Titelzeilen + "412 S."
        g.setColor(new Color(24, 30, 36));
        g.fill(new Rectangle2D.Double(cx - s*0.24, cy - s*0.3, s*0.4, s*0.05));
        g.fill(new Rectangle2D.Double(cx - s*0.24, cy - s*0.18, s*0.3, s*0.04));
        stamp(g, cx + s*0.06, cy + s*0.16, s*0.3, stampC);
    }

    private static void schnitzel(Graphics2D g, double cx, double cy, double s, Color ink, Color meat) {
        // Teller
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.44, cy - s*0.26, s*0.88, s*0.6));
        g.setColor(new Color(24, 30, 36));
        g.setStroke(new BasicStroke((float) (s * 0.03)));
        g.draw(new Ellipse2D.Double(cx - s*0.34, cy - s*0.18, s*0.68, s*0.44));
        // Schnitzel (unregelmäßige Panade)
        g.setColor(meat);
        Path2D.Double sn = new Path2D.Double();
        sn.moveTo(cx - s*0.26, cy - s*0.02);
        sn.curveTo(cx - s*0.3, cy - s*0.16, cx - s*0.02, cy - s*0.2, cx + s*0.1, cy - s*0.12);
        sn.curveTo(cx + s*0.3, cy - s*0.06, cx + s*0.26, cy + s*0.12, cx + s*0.08, cy + s*0.16);
        sn.curveTo(cx - s*0.1, cy + s*0.2, cx - s*0.24, cy + s*0.12, cx - s*0.26, cy - s*0.02);
        sn.closePath();
        g.fill(sn);
        g.setColor(new Color(255, 255, 255, 70));
        g.fill(new Ellipse2D.Double(cx - s*0.14, cy - s*0.08, s*0.1, s*0.06));
        // Zitronenscheibe (ergraut mit, wenn die Mission gesperrt ist)
        g.setColor(meat == DIM ? DIM2 : new Color(255, 224, 130));
        g.fill(new Ellipse2D.Double(cx + s*0.14, cy - s*0.16, s*0.16, s*0.16));
    }

    private static void spiltCan(Graphics2D g, double cx, double cy, double s, Color ink, Color fluid) {
        // Pfütze
        g.setColor(fluid);
        g.fill(new Ellipse2D.Double(cx - s*0.4, cy + s*0.18, s*0.8, s*0.22));
        g.fill(new Ellipse2D.Double(cx + s*0.12, cy + s*0.3, s*0.24, s*0.12));
        // gekippte Dose
        AffineTransform old = g.getTransform();
        g.translate(cx - s*0.08, cy - s*0.06);
        g.rotate(Math.toRadians(112));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(-s*0.13, -s*0.24, s*0.26, s*0.48, s*0.08, s*0.08));
        g.setColor(fluid);
        g.fill(new RoundRectangle2D.Double(-s*0.13, -s*0.08, s*0.26, s*0.16, 3, 3));
        g.setTransform(old);
        // heraus schwappende Tropfen
        g.setColor(fluid);
        g.fill(new Ellipse2D.Double(cx + s*0.06, cy + s*0.02, s*0.08, s*0.08));
        g.fill(new Ellipse2D.Double(cx + s*0.18, cy + s*0.1, s*0.06, s*0.06));
    }

    private static void coupFlag(Graphics2D g, double cx, double cy, double s, Color ink, Color algae) {
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(new Line2D.Double(cx - s*0.16, cy - s*0.42, cx - s*0.16, cy + s*0.42));
        g.setColor(algae);
        Path2D.Double f = new Path2D.Double();
        f.moveTo(cx - s*0.16, cy - s*0.4);
        f.curveTo(cx + s*0.1, cy - s*0.5, cx + s*0.16, cy - s*0.3, cx + s*0.42, cy - s*0.38);
        f.lineTo(cx + s*0.42, cy - s*0.12);
        f.curveTo(cx + s*0.16, cy - s*0.04, cx + s*0.1, cy - s*0.24, cx - s*0.16, cy - s*0.14);
        f.closePath();
        g.fill(f);
        // kleine Faust auf der Fahne
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx + s*0.02, cy - s*0.34, s*0.14, s*0.14, 4, 4));
    }

    private static void nightVision(Graphics2D g, double cx, double cy, double s, Color ink, Color glow) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.42, cy - s*0.16, s*0.84, s*0.3, s*0.12, s*0.12));
        g.setColor(new Color(24, 30, 36));
        g.fill(new Ellipse2D.Double(cx - s*0.34, cy - s*0.12, s*0.26, s*0.24));
        g.fill(new Ellipse2D.Double(cx + s*0.08, cy - s*0.12, s*0.26, s*0.24));
        g.setColor(glow);
        g.fill(new Ellipse2D.Double(cx - s*0.28, cy - s*0.06, s*0.14, s*0.12));
        g.fill(new Ellipse2D.Double(cx + s*0.14, cy - s*0.06, s*0.14, s*0.12));
        g.setStroke(new BasicStroke((float) (s * 0.05)));
        g.draw(new Line2D.Double(cx - s*0.42, cy - s*0.02, cx - s*0.5, cy - s*0.1));
        g.draw(new Line2D.Double(cx + s*0.42, cy - s*0.02, cx + s*0.5, cy - s*0.1));
        // Antenne
        g.setColor(ink);
        g.draw(new Line2D.Double(cx, cy - s*0.16, cx, cy - s*0.36));
        g.fill(new Ellipse2D.Double(cx - s*0.035, cy - s*0.42, s*0.07, s*0.07));
    }

    private static void ministry(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        Path2D.Double roof = new Path2D.Double();
        roof.moveTo(cx - s*0.44, cy - s*0.14); roof.lineTo(cx, cy - s*0.4); roof.lineTo(cx + s*0.44, cy - s*0.14);
        roof.closePath();
        g.fill(roof);
        g.setColor(sub);
        for (int i = 0; i < 4; i++)
            g.fill(new Rectangle2D.Double(cx - s*0.34 + i * s*0.2, cy - s*0.08, s*0.08, s*0.36));
        g.setColor(ink);
        g.fill(new Rectangle2D.Double(cx - s*0.42, cy + s*0.3, s*0.84, s*0.08));
        paragraph(g, cx, cy - s*0.2, s*0.26, sub);
    }

    private static void dove(Graphics2D g, double cx, double cy, double s, Color ink, Color branch) {
        g.setColor(ink);
        Path2D.Double body = new Path2D.Double();
        body.moveTo(cx - s*0.34, cy + s*0.04);
        body.curveTo(cx - s*0.1, cy - s*0.16, cx + s*0.18, cy - s*0.14, cx + s*0.34, cy + s*0.02);
        body.curveTo(cx + s*0.2, cy + s*0.18, cx - s*0.12, cy + s*0.2, cx - s*0.34, cy + s*0.04);
        g.fill(body);
        // Flügel
        Path2D.Double wing = new Path2D.Double();
        wing.moveTo(cx - s*0.02, cy - s*0.06);
        wing.curveTo(cx + s*0.06, cy - s*0.34, cx + s*0.24, cy - s*0.38, cx + s*0.3, cy - s*0.3);
        wing.curveTo(cx + s*0.18, cy - s*0.18, cx + s*0.1, cy - s*0.08, cx - s*0.02, cy - s*0.06);
        g.fill(wing);
        // Kopf + Schnabel
        g.fill(new Ellipse2D.Double(cx - s*0.42, cy - s*0.1, s*0.16, s*0.14));
        Path2D.Double beak = new Path2D.Double();
        beak.moveTo(cx - s*0.42, cy - s*0.03); beak.lineTo(cx - s*0.52, cy); beak.lineTo(cx - s*0.42, cy + s*0.03);
        beak.closePath();
        g.fill(beak);
        // Algen-Zweig
        g.setColor(branch);
        g.setStroke(new BasicStroke((float) (s * 0.045), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(cx - s*0.5, cy + s*0.1, cx - s*0.36, cy + s*0.16, cx - s*0.22, cy + s*0.1));
        for (int i = 0; i < 3; i++)
            g.fill(new Ellipse2D.Double(cx - s*0.46 + i * s*0.1, cy + s*0.08, s*0.06, s*0.045));
    }

    private static void downvote(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx - s*0.14, cy - s*0.42); p.lineTo(cx + s*0.14, cy - s*0.42);
        p.lineTo(cx + s*0.14, cy + s*0.02); p.lineTo(cx + s*0.3, cy + s*0.02);
        p.lineTo(cx, cy + s*0.42); p.lineTo(cx - s*0.3, cy + s*0.02);
        p.lineTo(cx - s*0.14, cy + s*0.02); p.closePath();
        g.fill(p);
    }

    private static void bubble(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.4, cy - s*0.3, s*0.8, s*0.5, s*0.24, s*0.24));
        Path2D.Double tip = new Path2D.Double();
        tip.moveTo(cx - s*0.14, cy + s*0.16); tip.lineTo(cx - s*0.26, cy + s*0.42); tip.lineTo(cx + s*0.02, cy + s*0.18);
        tip.closePath();
        g.fill(tip);
        g.setColor(sub);
        for (int i = 0; i < 3; i++) g.fill(new Ellipse2D.Double(cx - s*0.22 + i * s*0.18, cy - s*0.1, s*0.09, s*0.09));
    }

    private static void oneStar(Graphics2D g, double cx, double cy, double s, Color gold, Color ink) {
        star5(g, cx - s*0.14, cy, s*0.34, gold);
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.055)));
        for (int i = 0; i < 2; i++)
            g.draw(QuestArt.hexStar(cx + s*0.2 + i * s*0.24, cy, s*0.13));
        g.setFont(new Font("SansSerif", Font.BOLD, (int) (s * 0.42)));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("1", (float) (cx - s*0.14 - fm.stringWidth("1") / 2.0), (float) (cy + s*0.44));
    }
    private static Path2D.Double hexStar(double cx, double cy, double r) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double a = Math.toRadians(-90 + i * 36);
            double rr = (i % 2 == 0) ? r : r * 0.45;
            double x = cx + rr * Math.cos(a), y = cy + rr * Math.sin(a);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    private static void priceTag(Graphics2D g, double cx, double cy, double s, Color ink, Color accent) {
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.rotate(Math.toRadians(-24));
        g.setColor(ink);
        Path2D.Double tag = new Path2D.Double();
        tag.moveTo(-s*0.38, -s*0.16); tag.lineTo(s*0.1, -s*0.28);
        tag.lineTo(s*0.34, 0); tag.lineTo(s*0.1, s*0.28); tag.lineTo(-s*0.38, s*0.16);
        tag.closePath();
        g.fill(tag);
        g.setColor(new Color(24, 30, 36));
        g.fill(new Ellipse2D.Double(-s*0.3, -s*0.06, s*0.12, s*0.12));
        g.setTransform(old);
        arrowDown(g, cx + s*0.28, cy - s*0.26, s*0.4, accent);
    }
    private static void arrowDown(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        g.setStroke(new BasicStroke((float) (s * 0.14), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx, cy - s*0.3, cx, cy + s*0.34));
        g.draw(new Line2D.Double(cx - s*0.2, cy + s*0.12, cx, cy + s*0.34));
        g.draw(new Line2D.Double(cx + s*0.2, cy + s*0.12, cx, cy + s*0.34));
    }

    private static void basement(Graphics2D g, double cx, double cy, double s, Color ink, Color sub, Color light) {
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.44, cy - s*0.3, s*0.88, s*0.6));
        g.setColor(light);
        g.fill(new RoundRectangle2D.Double(cx - s*0.32, cy - s*0.18, s*0.64, s*0.36, 5, 5));
        // gebeugte Schatten-Silhouetten (die fleißigen Rentner)
        g.setColor(new Color(24, 30, 36));
        for (int i = 0; i < 3; i++) {
            double x = cx - s*0.2 + i * s*0.2;
            g.fill(new Ellipse2D.Double(x - s*0.055, cy - s*0.08, s*0.11, s*0.1));           // Kopf (gebeugt)
            g.fill(new Arc2D.Double(x - s*0.09, cy - s*0.02, s*0.18, s*0.22, 0, 180, Arc2D.CHORD)); // Rücken
        }
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.05)));
        g.draw(new RoundRectangle2D.Double(cx - s*0.32, cy - s*0.18, s*0.64, s*0.36, 5, 5));
        g.draw(new Line2D.Double(cx, cy - s*0.18, cx, cy + s*0.18));
    }

    private static void prison(Graphics2D g, double cx, double cy, double s, Color ink, Color sub, Color shrimp) {
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.42, cy - s*0.36, s*0.84, s*0.72, 8, 8));
        Icons.resource(g, com.shrimptopia.model.IconKind.PERSON, cx, cy + s*0.02, s*0.5, new Color(24, 30, 36));
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 4; i++) {
            double x = cx - s*0.3 + i * s*0.2;
            g.draw(new Line2D.Double(x, cy - s*0.36, x, cy + s*0.36));
        }
        g.draw(new Line2D.Double(cx - s*0.42, cy - s*0.36, cx + s*0.42, cy - s*0.36));
        Icons.resource(g, com.shrimptopia.model.IconKind.SHRIMP, cx + s*0.3, cy + s*0.3, s*0.26, shrimp);
    }

    private static void sheet(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        double w = s * 0.62, h = s * 0.8;
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - w/2, cy - h/2, w, h, 4, 4));
        g.setColor(sub);
        for (int i = 0; i < 4; i++)
            g.fill(new Rectangle2D.Double(cx - w/2 + 4, cy - h/2 + 6 + i * h * 0.18, w - 8 - (i == 3 ? w*0.3 : 0), 2.4));
    }

    private static void stamp(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        g.setStroke(new BasicStroke((float) (s * 0.14)));
        g.draw(new Ellipse2D.Double(cx - s/2, cy - s/2, s, s));
        g.draw(new Line2D.Double(cx - s*0.24, cy, cx + s*0.24, cy));
    }

    private static void clipboard(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        double w = s * 0.6, h = s * 0.8;
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - w/2, cy - h/2, w, h, 5, 5));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - w*0.2, cy - h/2 - s*0.05, w*0.4, s*0.12, 3, 3));
        g.setStroke(new BasicStroke((float) (s * 0.06), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            double y = cy - h*0.18 + i * h * 0.24;
            g.draw(new Line2D.Double(cx - w*0.3, y, cx - w*0.18, y + s*0.06));
            g.draw(new Line2D.Double(cx - w*0.18, y + s*0.06, cx - w*0.02, y - s*0.08));
            g.draw(new Line2D.Double(cx + w*0.08, y, cx + w*0.32, y));
        }
    }

    private static void medal(Graphics2D g, double cx, double cy, double s, Color gold, Color ink) {
        g.setColor(ink);
        Path2D.Double band = new Path2D.Double();
        band.moveTo(cx - s*0.18, cy - s*0.42); band.lineTo(cx + s*0.18, cy - s*0.42);
        band.lineTo(cx + s*0.06, cy - s*0.05); band.lineTo(cx - s*0.06, cy - s*0.05); band.closePath();
        g.fill(band);
        g.setColor(gold);
        g.fill(new Ellipse2D.Double(cx - s*0.26, cy - s*0.1, s*0.52, s*0.52));
        star5(g, cx, cy + s*0.16, s*0.2, ink);
    }

    private static void paragraph(Graphics2D g, double cx, double cy, double s, Color ink) {
        g.setFont(new Font("Serif", Font.BOLD, (int) s));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(ink);
        g.drawString("§", (float) (cx - fm.stringWidth("§") / 2.0), (float) (cy + fm.getAscent() * 0.36));
    }

    private static void heart(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx, cy + s*0.36);
        p.curveTo(cx - s*0.62, cy - s*0.1, cx - s*0.3, cy - s*0.52, cx, cy - s*0.16);
        p.curveTo(cx + s*0.3, cy - s*0.52, cx + s*0.62, cy - s*0.1, cx, cy + s*0.36);
        g.setColor(c);
        g.fill(p);
    }

    private static void protestSign(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.04, cy - s*0.1, s*0.08, s*0.52));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.36, cy - s*0.44, s*0.72, s*0.4, 5, 5));
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.26, cy - s*0.34, s*0.52, s*0.05));
        g.fill(new Rectangle2D.Double(cx - s*0.26, cy - s*0.22, s*0.36, s*0.05));
    }

    private static void fist(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.1, cy + s*0.12, s*0.2, s*0.34, 5, 5));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.26, cy - s*0.34, s*0.52, s*0.5, 10, 10));
        g.setColor(sub);
        for (int i = 0; i < 3; i++)
            g.fill(new RoundRectangle2D.Double(cx - s*0.2 + i * s*0.155, cy - s*0.34, s*0.1, s*0.2, 4, 4));
    }

    private static void playButton(Graphics2D g, double cx, double cy, double s, Color ink, Color accent) {
        g.setColor(accent);
        g.fill(new RoundRectangle2D.Double(cx - s*0.42, cy - s*0.3, s*0.84, s*0.6, s*0.2, s*0.2));
        Path2D.Double tri = new Path2D.Double();
        tri.moveTo(cx - s*0.1, cy - s*0.16); tri.lineTo(cx + s*0.18, cy); tri.lineTo(cx - s*0.1, cy + s*0.16); tri.closePath();
        g.setColor(ink);
        g.fill(tri);
    }

    private static void hash(Graphics2D g, double cx, double cy, double s, Color ink) {
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.12), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx - s*0.18, cy - s*0.3, cx - s*0.3, cy + s*0.3));
        g.draw(new Line2D.Double(cx + s*0.3, cy - s*0.3, cx + s*0.18, cy + s*0.3));
        g.draw(new Line2D.Double(cx - s*0.36, cy - s*0.12, cx + s*0.4, cy - s*0.12));
        g.draw(new Line2D.Double(cx - s*0.4, cy + s*0.12, cx + s*0.36, cy + s*0.12));
    }

    private static void flame(Graphics2D g, double cx, double cy, double s, Color outer, Color innerC) {
        Path2D.Double f = new Path2D.Double();
        f.moveTo(cx, cy - s*0.42);
        f.curveTo(cx + s*0.3, cy - s*0.1, cx + s*0.28, cy + s*0.16, cx, cy + s*0.36);
        f.curveTo(cx - s*0.28, cy + s*0.16, cx - s*0.3, cy - s*0.1, cx, cy - s*0.42);
        g.setColor(outer);
        g.fill(f);
        g.setColor(innerC);
        g.fill(new Ellipse2D.Double(cx - s*0.12, cy + s*0.02, s*0.24, s*0.3));
    }

    private static void factory(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.4, cy - s*0.05, s*0.8, s*0.34));
        g.setColor(ink);
        Path2D.Double roof = new Path2D.Double();
        roof.moveTo(cx - s*0.4, cy - s*0.05);
        roof.lineTo(cx - s*0.4, cy - s*0.28); roof.lineTo(cx - s*0.16, cy - s*0.05);
        roof.lineTo(cx - s*0.16, cy - s*0.28); roof.lineTo(cx + s*0.08, cy - s*0.05);
        roof.closePath();
        g.fill(roof);
        g.fill(new Rectangle2D.Double(cx + s*0.18, cy - s*0.4, s*0.1, s*0.36));
    }

    private static void claw(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx - s*0.3, cy + s*0.4);
        p.curveTo(cx - s*0.5, cy - s*0.1, cx - s*0.2, cy - s*0.5, cx + s*0.14, cy - s*0.34);
        p.curveTo(cx - s*0.05, cy - s*0.18, cx - s*0.02, cy - s*0.05, cx + s*0.1, cy + s*0.02);
        p.curveTo(cx + s*0.34, cy - s*0.12, cx + s*0.42, cy + s*0.1, cx + s*0.22, cy + s*0.26);
        p.curveTo(cx, cy + s*0.42, cx - s*0.14, cy + s*0.46, cx - s*0.3, cy + s*0.4);
        g.fill(p);
    }

    private static void briefcase(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.14, cy - s*0.4, s*0.28, s*0.14, 4, 4));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.4, cy - s*0.28, s*0.8, s*0.56, 8, 8));
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.4, cy - s*0.04, s*0.8, s*0.07));
    }

    private static void bulbOff(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new Ellipse2D.Double(cx - s*0.24, cy - s*0.38, s*0.48, s*0.48));
        g.fill(new RoundRectangle2D.Double(cx - s*0.1, cy + s*0.08, s*0.2, s*0.2, 4, 4));
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.09), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx - s*0.34, cy - s*0.44, cx + s*0.34, cy + s*0.3));
    }

    private static void fork(Graphics2D g, double cx, double cy, double s, Color ink) {
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.08), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx, cy - s*0.18, cx, cy + s*0.4));
        for (int i = -1; i <= 1; i++)
            g.draw(new Line2D.Double(cx + i * s*0.1, cy - s*0.4, cx + i * s*0.1, cy - s*0.14));
    }

    private static void seagull(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setStroke(new BasicStroke((float) (s * 0.1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(ink);
        g.draw(new QuadCurve2D.Double(cx - s*0.42, cy - s*0.05, cx - s*0.2, cy - s*0.32, cx, cy - s*0.05));
        g.draw(new QuadCurve2D.Double(cx, cy - s*0.05, cx + s*0.2, cy - s*0.32, cx + s*0.42, cy - s*0.05));
        g.setColor(sub);
        g.draw(new QuadCurve2D.Double(cx - s*0.3, cy + s*0.26, cx - s*0.16, cy + s*0.08, cx - s*0.02, cy + s*0.26));
        g.draw(new QuadCurve2D.Double(cx - s*0.02, cy + s*0.26, cx + s*0.12, cy + s*0.08, cx + s*0.26, cy + s*0.26));
    }

    private static void spa(Graphics2D g, double cx, double cy, double s, Color ink, Color water) {
        g.setColor(water);
        g.fill(new Arc2D.Double(cx - s*0.38, cy - s*0.05, s*0.76, s*0.5, 180, 180, Arc2D.CHORD));
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = -1; i <= 1; i++) {
            double x = cx + i * s * 0.18;
            g.draw(new QuadCurve2D.Double(x - s*0.05, cy - s*0.1, x + s*0.07, cy - s*0.24, x - s*0.03, cy - s*0.38));
        }
    }

    private static void arrowUp(Graphics2D g, double cx, double cy, double s, Color c) {
        g.setColor(c);
        g.setStroke(new BasicStroke((float) (s * 0.14), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx, cy + s*0.34, cx, cy - s*0.3));
        g.draw(new Line2D.Double(cx - s*0.2, cy - s*0.08, cx, cy - s*0.3));
        g.draw(new Line2D.Double(cx + s*0.2, cy - s*0.08, cx, cy - s*0.3));
    }

    private static void helmet(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new Arc2D.Double(cx - s*0.4, cy - s*0.34, s*0.8, s*0.72, 0, 180, Arc2D.CHORD));
        g.fill(new RoundRectangle2D.Double(cx - s*0.46, cy - s*0.02, s*0.92, s*0.12, 6, 6));
        g.setColor(sub);
        g.fill(new Ellipse2D.Double(cx - s*0.08, cy - s*0.3, s*0.16, s*0.1));
        star5(g, cx, cy - s*0.14, s*0.12, sub);
    }

    private static void whistle(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.3, cy - s*0.1, s*0.44, s*0.44));
        g.fill(new RoundRectangle2D.Double(cx - s*0.12, cy - s*0.18, s*0.48, s*0.16, 5, 5));
        g.setColor(sub);
        g.setStroke(new BasicStroke((float) (s * 0.06), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++)
            g.draw(new Line2D.Double(cx + s*0.3 + i*s*0.06, cy - s*0.34 - i*s*0.03, cx + s*0.42 + i*s*0.06, cy - s*0.42 - i*s*0.03));
    }

    private static void drumstick(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.42, cy - s*0.3, s*0.56, s*0.5));
        g.setColor(sub);
        g.setStroke(new BasicStroke((float) (s * 0.11), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx + s*0.02, cy + s*0.1, cx + s*0.3, cy + s*0.32));
        g.fill(new Ellipse2D.Double(cx + s*0.24, cy + s*0.24, s*0.16, s*0.16));
    }

    private static void dumbbell(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.22, cy - s*0.05, s*0.44, s*0.1, 4, 4));
        g.setColor(ink);
        for (int side = -1; side <= 1; side += 2) {
            g.fill(new RoundRectangle2D.Double(cx + side * s*0.3 - s*0.06, cy - s*0.24, s*0.12, s*0.48, 4, 4));
            g.fill(new RoundRectangle2D.Double(cx + side * s*0.42 - s*0.05, cy - s*0.16, s*0.1, s*0.32, 4, 4));
        }
    }

    private static void sword(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.rotate(Math.toRadians(-40));
        g.setColor(ink);
        Path2D.Double blade = new Path2D.Double();
        blade.moveTo(0, -s*0.48); blade.lineTo(s*0.05, -s*0.38); blade.lineTo(s*0.05, s*0.1);
        blade.lineTo(-s*0.05, s*0.1); blade.lineTo(-s*0.05, -s*0.38); blade.closePath();
        g.fill(blade);
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(-s*0.16, s*0.1, s*0.32, s*0.07, 3, 3));
        g.fill(new RoundRectangle2D.Double(-s*0.035, s*0.17, s*0.07, s*0.2, 3, 3));
        g.setTransform(old);
    }

    private static void swordMirror(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.scale(-1, 1);
        sword(g, 0, 0, s, ink, sub);
        g.setTransform(old);
    }

    private static void flask(Graphics2D g, double cx, double cy, double s, Color ink, Color fluid) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx - s*0.08, cy - s*0.42); p.lineTo(cx + s*0.08, cy - s*0.42);
        p.lineTo(cx + s*0.08, cy - s*0.1); p.lineTo(cx + s*0.3, cy + s*0.34);
        p.lineTo(cx - s*0.3, cy + s*0.34); p.lineTo(cx - s*0.08, cy - s*0.1);
        p.closePath();
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(p);
        g.setColor(fluid);
        Path2D.Double f = new Path2D.Double();
        f.moveTo(cx - s*0.17, cy + s*0.08); f.lineTo(cx + s*0.17, cy + s*0.08);
        f.lineTo(cx + s*0.27, cy + s*0.3); f.lineTo(cx - s*0.27, cy + s*0.3); f.closePath();
        g.fill(f);
    }

    private static void soupPot(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.36, cy - s*0.08, s*0.72, s*0.44, 8, 8));
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.44, cy - s*0.12, s*0.88, s*0.1, 4, 4));
        g.setStroke(new BasicStroke((float) (s * 0.06), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = -1; i <= 1; i++) {
            double x = cx + i * s * 0.16;
            g.draw(new QuadCurve2D.Double(x - s*0.04, cy - s*0.18, x + s*0.06, cy - s*0.3, x - s*0.02, cy - s*0.42));
        }
    }

    private static void teaGlass(Graphics2D g, double cx, double cy, double s, Color ink, Color tea) {
        g.setColor(tea);
        g.fill(new Rectangle2D.Double(cx - s*0.2, cy - s*0.14, s*0.4, s*0.4));
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.055)));
        g.draw(new Rectangle2D.Double(cx - s*0.22, cy - s*0.24, s*0.44, s*0.52));
        g.draw(new Arc2D.Double(cx + s*0.2, cy - s*0.1, s*0.24, s*0.26, -90, 180, Arc2D.OPEN));
        for (int i = -1; i <= 1; i += 2)
            g.draw(new QuadCurve2D.Double(cx + i*s*0.08 - s*0.03, cy - s*0.3, cx + i*s*0.08 + s*0.05, cy - s*0.4, cx + i*s*0.08 - s*0.02, cy - s*0.5));
    }

    private static void contract(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        sheet(g, cx - s*0.05, cy, s * 0.95, ink, sub);
        g.setColor(sub);
        AffineTransform old = g.getTransform();
        g.translate(cx + s*0.22, cy + s*0.2);
        g.rotate(Math.toRadians(-45));
        g.fill(new RoundRectangle2D.Double(-s*0.04, -s*0.3, s*0.08, s*0.5, 3, 3));
        Path2D.Double tip = new Path2D.Double();
        tip.moveTo(-s*0.04, s*0.2); tip.lineTo(s*0.04, s*0.2); tip.lineTo(0, s*0.32); tip.closePath();
        g.fill(tip);
        g.setTransform(old);
    }

    private static void percent(Graphics2D g, double cx, double cy, double s, Color ink) {
        g.setColor(ink);
        g.setStroke(new BasicStroke((float) (s * 0.1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx - s*0.28, cy + s*0.32, cx + s*0.28, cy - s*0.32));
        g.draw(new Ellipse2D.Double(cx - s*0.38, cy - s*0.4, s*0.24, s*0.24));
        g.draw(new Ellipse2D.Double(cx + s*0.14, cy + s*0.16, s*0.24, s*0.24));
    }

    private static void spyGlasses(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.42, cy - s*0.2, s*0.36, s*0.3));
        g.fill(new Ellipse2D.Double(cx + s*0.06, cy - s*0.2, s*0.36, s*0.3));
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(new Line2D.Double(cx - s*0.06, cy - s*0.08, cx + s*0.06, cy - s*0.08));
        g.setColor(sub);
        g.fill(new Arc2D.Double(cx - s*0.24, cy + s*0.12, s*0.48, s*0.24, 180, 180, Arc2D.CHORD));
    }

    private static void folderEye(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.4, cy - s*0.3, s*0.34, s*0.12, 4, 4));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.4, cy - s*0.22, s*0.8, s*0.5, 6, 6));
        g.setColor(sub);
        g.fill(new Ellipse2D.Double(cx - s*0.16, cy - s*0.08, s*0.32, s*0.2));
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.05, cy - s*0.05, s*0.1, s*0.13));
    }

    private static void noEntry(Graphics2D g, double cx, double cy, double s, Color red, Color ink) {
        g.setColor(red);
        g.fill(new Ellipse2D.Double(cx - s*0.4, cy - s*0.4, s*0.8, s*0.8));
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.28, cy - s*0.07, s*0.56, s*0.14, 5, 5));
    }

    private static void plug(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.2, cy - s*0.18, s*0.4, s*0.34, 8, 8));
        g.setColor(sub);
        g.fill(new Rectangle2D.Double(cx - s*0.14, cy - s*0.4, s*0.08, s*0.24));
        g.fill(new Rectangle2D.Double(cx + s*0.06, cy - s*0.4, s*0.08, s*0.24));
        g.setStroke(new BasicStroke((float) (s * 0.08), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(cx, cy + s*0.16, cx - s*0.05, cy + s*0.34, cx + s*0.2, cy + s*0.4));
    }

    private static void flagPair(Graphics2D g, double cx, double cy, double s, Color ink, Color sub, Color accent) {
        g.setColor(sub);
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(new Line2D.Double(cx - s*0.26, cy - s*0.4, cx - s*0.26, cy + s*0.4));
        g.draw(new Line2D.Double(cx + s*0.26, cy - s*0.4, cx + s*0.26, cy + s*0.4));
        g.setColor(ink);
        g.fill(new Rectangle2D.Double(cx - s*0.26, cy - s*0.4, s*0.34, s*0.2));
        g.setColor(accent);
        g.fill(new Rectangle2D.Double(cx - s*0.08, cy - s*0.4, s*0.34, s*0.2));
    }

    private static void drone(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.14, cy - s*0.1, s*0.28, s*0.2, 6, 6));
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(new Line2D.Double(cx - s*0.14, cy - s*0.06, cx - s*0.36, cy - s*0.22));
        g.draw(new Line2D.Double(cx + s*0.14, cy - s*0.06, cx + s*0.36, cy - s*0.22));
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx - s*0.48, cy - s*0.28, s*0.26, s*0.06, 3, 3));
        g.fill(new RoundRectangle2D.Double(cx + s*0.22, cy - s*0.28, s*0.26, s*0.06, 3, 3));
        g.fill(new Ellipse2D.Double(cx - s*0.06, cy + s*0.1, s*0.12, s*0.12));
    }

    private static void truck(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new RoundRectangle2D.Double(cx - s*0.44, cy - s*0.22, s*0.56, s*0.34, 4, 4));
        g.setColor(sub);
        g.fill(new RoundRectangle2D.Double(cx + s*0.14, cy - s*0.1, s*0.3, s*0.22, 4, 4));
        g.setColor(ink);
        g.fill(new Ellipse2D.Double(cx - s*0.3, cy + s*0.1, s*0.18, s*0.18));
        g.fill(new Ellipse2D.Double(cx + s*0.16, cy + s*0.1, s*0.18, s*0.18));
    }

    private static void cutter(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setStroke(new BasicStroke((float) (s * 0.1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(sub);
        g.draw(new Line2D.Double(cx - s*0.06, cy - s*0.02, cx - s*0.36, cy + s*0.38));
        g.draw(new Line2D.Double(cx + s*0.06, cy - s*0.02, cx + s*0.36, cy + s*0.38));
        g.setColor(ink);
        g.draw(new QuadCurve2D.Double(cx - s*0.16, cy - s*0.36, cx, cy - s*0.1, cx + s*0.12, cy - s*0.38));
        g.fill(new Ellipse2D.Double(cx - s*0.07, cy - s*0.1, s*0.14, s*0.14));
    }

    private static void wall(Graphics2D g, double cx, double cy, double s, Color ink, Color sub) {
        g.setColor(ink);
        g.fill(new Rectangle2D.Double(cx - s*0.42, cy - s*0.18, s*0.84, s*0.52));
        for (int i = 0; i < 4; i++)
            g.fill(new Rectangle2D.Double(cx - s*0.42 + i * s*0.24, cy - s*0.32, s*0.14, s*0.14));
        g.setColor(sub);
        g.setStroke(new BasicStroke((float) (s * 0.035)));
        for (int r = 0; r < 3; r++)
            g.draw(new Line2D.Double(cx - s*0.42, cy - s*0.02 + r * s*0.16, cx + s*0.42, cy - s*0.02 + r * s*0.16));
    }

    private static void crown(Graphics2D g, double cx, double cy, double s, Color gold, Color ink) {
        g.setColor(gold);
        Path2D.Double p = new Path2D.Double();
        p.moveTo(cx - s*0.4, cy + s*0.24); p.lineTo(cx - s*0.44, cy - s*0.22);
        p.lineTo(cx - s*0.2, cy - s*0.02); p.lineTo(cx, cy - s*0.34);
        p.lineTo(cx + s*0.2, cy - s*0.02); p.lineTo(cx + s*0.44, cy - s*0.22);
        p.lineTo(cx + s*0.4, cy + s*0.24); p.closePath();
        g.fill(p);
        g.setColor(ink);
        g.fill(new Rectangle2D.Double(cx - s*0.4, cy + s*0.26, s*0.8, s*0.1));
    }

    private static void trophy(Graphics2D g, double cx, double cy, double s, Color gold, Color ink) {
        g.setColor(gold);
        g.fill(new Arc2D.Double(cx - s*0.26, cy - s*0.38, s*0.52, s*0.55, 180, 180, Arc2D.CHORD));
        g.setStroke(new BasicStroke((float) (s * 0.06)));
        g.draw(new Arc2D.Double(cx - s*0.42, cy - s*0.34, s*0.2, s*0.26, 90, 180, Arc2D.OPEN));
        g.draw(new Arc2D.Double(cx + s*0.22, cy - s*0.34, s*0.2, s*0.26, -90, 180, Arc2D.OPEN));
        g.fill(new Rectangle2D.Double(cx - s*0.05, cy - s*0.1, s*0.1, s*0.24));
        g.fill(new RoundRectangle2D.Double(cx - s*0.2, cy + s*0.14, s*0.4, s*0.1, 3, 3));
        star5(g, cx, cy - s*0.16, s*0.14, ink);
    }

    private static void halo(Graphics2D g, double cx, double cy, double s, Color gold) {
        g.setColor(gold);
        g.setStroke(new BasicStroke((float) (s * 0.14)));
        g.draw(new Ellipse2D.Double(cx - s*0.4, cy - s*0.16, s*0.8, s*0.32));
    }

    private static void star5(Graphics2D g, double cx, double cy, double r, Color c) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double a = Math.toRadians(-90 + i * 36);
            double rr = (i % 2 == 0) ? r : r * 0.45;
            double x = cx + rr * Math.cos(a), y = cy + rr * Math.sin(a);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        g.setColor(c);
        g.fill(p);
    }
}
