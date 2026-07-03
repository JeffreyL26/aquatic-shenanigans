package com.shrimptopia.ui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Winziger SVG-Renderer (nur Java2D, keine externen Abhängigkeiten), genau auf den
 * Funktionsumfang der Charakter-Avatare zugeschnitten: path/circle/ellipse/rect/text,
 * lineare & radiale Verläufe, clipPaths, translate/rotate/scale, opacity & dash.
 *
 * Rasterisiert jede SVG einmal in ein zwischengespeichertes Bild. Fehlt eine Datei
 * oder schlägt das Parsen fehl, liefert {@link #get} {@code null} -> der Aufrufer
 * fällt auf das handgezeichnete Portrait (siehe {@link Icons}) zurück.
 */
public final class SvgAvatar {

    private SvgAvatar() {}

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();
    private static final Set<String> MISSING = new HashSet<>();

    /** Liefert den Avatar {@code key} (z.B. "perla") in {@code size}x{@code size} Pixeln, oder null. */
    public static synchronized BufferedImage get(String key, int size) {
        if (key == null || size <= 0) return null;
        String ck = key + "@" + size;
        BufferedImage hit = CACHE.get(ck);
        if (hit != null) return hit;
        if (MISSING.contains(key)) return null;
        try (InputStream in = SvgAvatar.class.getResourceAsStream("avatars/" + key + ".svg")) {
            if (in == null) { MISSING.add(key); return null; }
            BufferedImage img = render(in, size);
            CACHE.put(ck, img);
            return img;
        } catch (Exception e) {
            MISSING.add(key);
            return null;
        }
    }

    // ===================== Rendering =====================

    private final Map<String, GradientDef> gradients = new HashMap<>();
    private final Map<String, Element> clipPaths = new HashMap<>();

    private static BufferedImage render(InputStream in, int size) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        // Keine externen Entities/DTDs laden (Sicherheit + Robustheit).
        f.setNamespaceAware(false);
        try { f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); } catch (Exception ignore) {}
        try { f.setFeature("http://xml.org/sax/features/external-general-entities", false); } catch (Exception ignore) {}
        try { f.setFeature("http://xml.org/sax/features/external-parameter-entities", false); } catch (Exception ignore) {}
        DocumentBuilder db = f.newDocumentBuilder();
        Document doc = db.parse(in);
        Element svg = doc.getDocumentElement();

        double[] vb = { 0, 0, 600, 600 };
        String vbStr = svg.getAttribute("viewBox");
        if (vbStr != null && !vbStr.isEmpty()) {
            double[] v = nums(vbStr);
            if (v.length == 4) vb = v;
        }

        SvgAvatar r = new SvgAvatar();
        r.collectDefs(svg);

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.scale(size / vb[2], size / vb[3]);
        g.translate(-vb[0], -vb[1]);

        r.renderChildren(g, svg, Style.root());
        g.dispose();
        return img;
    }

    /** Sammelt Verlaufs- und Clip-Definitionen aus dem gesamten Dokument (per id). */
    private void collectDefs(Element root) {
        walk(root, e -> {
            String tag = e.getTagName();
            String id = e.getAttribute("id");
            if (id == null || id.isEmpty()) return;
            switch (tag) {
                case "linearGradient" -> gradients.put(id, parseGradient(e, true));
                case "radialGradient" -> gradients.put(id, parseGradient(e, false));
                case "clipPath" -> clipPaths.put(id, e);
                default -> { }
            }
        });
    }

    private interface ElemConsumer { void accept(Element e); }
    private static void walk(Element e, ElemConsumer fn) {
        fn.accept(e);
        NodeList ch = e.getChildNodes();
        for (int i = 0; i < ch.getLength(); i++)
            if (ch.item(i) instanceof Element c) walk(c, fn);
    }

    private void renderChildren(Graphics2D g, Element parent, Style parentStyle) {
        NodeList ch = parent.getChildNodes();
        for (int i = 0; i < ch.getLength(); i++)
            if (ch.item(i) instanceof Element c) renderElement(g, c, parentStyle);
    }

    private void renderElement(Graphics2D gParent, Element e, Style parentStyle) {
        String tag = e.getTagName();
        // Definitions-/Hilfsknoten zeichnen nichts.
        switch (tag) {
            case "defs", "clipPath", "linearGradient", "radialGradient", "stop", "title", "desc" -> { return; }
            default -> { }
        }

        Graphics2D g = (Graphics2D) gParent.create();
        try {
            String tr = e.getAttribute("transform");
            if (tr != null && !tr.isEmpty()) g.transform(parseTransform(tr));

            String clip = e.getAttribute("clip-path");
            if (clip != null && !clip.isEmpty()) {
                Element cp = clipPaths.get(refId(clip));
                if (cp != null) {
                    Area a = clipArea(cp);
                    if (a != null) g.clip(a);
                }
            }

            Style st = parentStyle.derive(e);

            switch (tag) {
                case "g", "svg", "a" -> renderChildren(g, e, st);
                case "path" -> paint(g, parsePath(e.getAttribute("d")), st);
                case "circle" -> {
                    double cx = d(e, "cx", 0), cy = d(e, "cy", 0), rr = d(e, "r", 0);
                    if (rr > 0) paint(g, new Ellipse2D.Double(cx - rr, cy - rr, 2 * rr, 2 * rr), st);
                }
                case "ellipse" -> {
                    double cx = d(e, "cx", 0), cy = d(e, "cy", 0), rx = d(e, "rx", 0), ry = d(e, "ry", 0);
                    if (rx > 0 && ry > 0) paint(g, new Ellipse2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry), st);
                }
                case "rect" -> paint(g, rect(e), st);
                case "line" -> {
                    Line2D ln = new Line2D.Double(d(e, "x1", 0), d(e, "y1", 0), d(e, "x2", 0), d(e, "y2", 0));
                    st.fill = null; // Linien haben nie Füllung
                    paint(g, ln, st);
                }
                case "polygon", "polyline" -> {
                    Path2D p = polyPath(e.getAttribute("points"), tag.equals("polygon"));
                    if (p != null) paint(g, p, st);
                }
                case "text" -> drawText(g, e, st);
                default -> { }
            }
        } finally {
            g.dispose();
        }
    }

    // ===================== Shapes =====================

    private static Shape rect(Element e) {
        double x = d(e, "x", 0), y = d(e, "y", 0), w = d(e, "width", 0), h = d(e, "height", 0);
        double rx = d(e, "rx", -1), ry = d(e, "ry", -1);
        if (w <= 0 || h <= 0) return new Rectangle2D.Double(x, y, Math.max(0, w), Math.max(0, h));
        if (rx < 0 && ry < 0) return new Rectangle2D.Double(x, y, w, h);
        if (rx < 0) rx = ry;
        if (ry < 0) ry = rx;
        return new RoundRectangle2D.Double(x, y, w, h, 2 * rx, 2 * ry);
    }

    private static Path2D polyPath(String pts, boolean close) {
        if (pts == null || pts.isEmpty()) return null;
        double[] v = nums(pts);
        if (v.length < 4) return null;
        Path2D p = new Path2D.Double();
        p.moveTo(v[0], v[1]);
        for (int i = 2; i + 1 < v.length; i += 2) p.lineTo(v[i], v[i + 1]);
        if (close) p.closePath();
        return p;
    }

    /** Vereinigt alle Formen einer clipPath-Definition (in der aktuellen Nutzerkoordinate). */
    private Area clipArea(Element clipPath) {
        Area area = null;
        NodeList ch = clipPath.getChildNodes();
        for (int i = 0; i < ch.getLength(); i++) {
            if (!(ch.item(i) instanceof Element c)) continue;
            Shape s = switch (c.getTagName()) {
                case "circle" -> { double cx = d(c, "cx", 0), cy = d(c, "cy", 0), rr = d(c, "r", 0);
                    yield new Ellipse2D.Double(cx - rr, cy - rr, 2 * rr, 2 * rr); }
                case "ellipse" -> { double cx = d(c, "cx", 0), cy = d(c, "cy", 0), rx = d(c, "rx", 0), ry = d(c, "ry", 0);
                    yield new Ellipse2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry); }
                case "rect" -> rect(c);
                case "path" -> parsePath(c.getAttribute("d"));
                default -> null;
            };
            if (s == null) continue;
            String tr = c.getAttribute("transform");
            if (tr != null && !tr.isEmpty()) s = parseTransform(tr).createTransformedShape(s);
            if (area == null) area = new Area(s); else area.add(new Area(s));
        }
        return area;
    }

    // ===================== Painting =====================

    private void paint(Graphics2D g, Shape shape, Style st) {
        if (shape == null) return;
        if (st.fill != null) {
            Paint p = resolvePaint(st.fill, shape, st.fillAlpha());
            if (p != null) { g.setPaint(p); g.fill(shape); }
        }
        if (st.stroke != null && st.strokeWidth > 0) {
            Paint p = resolvePaint(st.stroke, shape, st.strokeAlpha());
            if (p != null) {
                g.setPaint(p);
                g.setStroke(st.basicStroke());
                g.draw(shape);
            }
        }
    }

    /** Wandelt einen Farb-/Verlaufs-String in einen Java2D-Paint um (mit Deckkraft alpha). */
    private Paint resolvePaint(String spec, Shape shape, double alpha) {
        if (spec == null || spec.equals("none")) return null;
        if (spec.startsWith("url(")) {
            GradientDef gd = gradients.get(refId(spec));
            if (gd == null) return null;
            return gd.toPaint(shape.getBounds2D(), alpha);
        }
        Color c = parseColor(spec);
        if (c == null) return null;
        return applyAlpha(c, alpha);
    }

    private void drawText(Graphics2D g, Element e, Style st) {
        String txt = e.getTextContent();
        if (txt == null) return;
        txt = txt.trim();
        if (txt.isEmpty()) return;
        double x = d(e, "x", 0), y = d(e, "y", 0);
        int size = (int) Math.round(d(e, "font-size", 16));
        String weight = attr(e, "font-weight", st.fontWeight);
        int styleBits = (weight != null && (weight.equals("bold") || parseIntSafe(weight) >= 600)) ? Font.BOLD : Font.PLAIN;
        String family = attr(e, "font-family", "SansSerif");
        if (family != null) family = family.split(",")[0].trim().replace("'", "");
        Font font = new Font(family != null && !family.isEmpty() ? family : "SansSerif", styleBits, Math.max(1, size));
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(txt);
        String anchor = attr(e, "text-anchor", "start");
        double tx = x;
        if ("middle".equals(anchor)) tx = x - w / 2.0;
        else if ("end".equals(anchor)) tx = x - w;
        String fillSpec = st.fill == null ? "#000000" : st.fill;
        Color c = parseColor(fillSpec);
        if (c == null) c = Color.BLACK;
        g.setColor(applyAlpha(c, st.fillAlpha()));
        g.drawString(txt, (float) tx, (float) y);
    }

    // ===================== Style (vererbte Präsentationsattribute) =====================

    private static final class Style {
        String fill = "#000000";   // SVG-Default: schwarz, vererbt
        String stroke = null;       // none, vererbt
        double strokeWidth = 1;
        int cap = BasicStroke.CAP_BUTT;
        int join = BasicStroke.JOIN_MITER;
        float[] dash = null;
        double fillOpacity = 1, strokeOpacity = 1;
        double groupAlpha = 1;      // multipliziertes opacity entlang der Baumstruktur
        String fontWeight = null;

        static Style root() { return new Style(); }

        Style copy() {
            Style s = new Style();
            s.fill = fill; s.stroke = stroke; s.strokeWidth = strokeWidth; s.cap = cap; s.join = join;
            s.dash = dash; s.fillOpacity = fillOpacity; s.strokeOpacity = strokeOpacity;
            s.groupAlpha = groupAlpha; s.fontWeight = fontWeight;
            return s;
        }

        Style derive(Element e) {
            Style s = copy();
            String v;
            if (!(v = attrRaw(e, "fill")).isEmpty()) s.fill = v.equals("none") ? null : v;
            if (!(v = attrRaw(e, "stroke")).isEmpty()) s.stroke = v.equals("none") ? null : v;
            if (!(v = attrRaw(e, "stroke-width")).isEmpty()) s.strokeWidth = parseDoubleSafe(v, s.strokeWidth);
            if (!(v = attrRaw(e, "stroke-linecap")).isEmpty()) s.cap = switch (v) {
                case "round" -> BasicStroke.CAP_ROUND; case "square" -> BasicStroke.CAP_SQUARE; default -> BasicStroke.CAP_BUTT; };
            if (!(v = attrRaw(e, "stroke-linejoin")).isEmpty()) s.join = switch (v) {
                case "round" -> BasicStroke.JOIN_ROUND; case "bevel" -> BasicStroke.JOIN_BEVEL; default -> BasicStroke.JOIN_MITER; };
            if (!(v = attrRaw(e, "stroke-dasharray")).isEmpty()) s.dash = parseDash(v);
            if (!(v = attrRaw(e, "fill-opacity")).isEmpty()) s.fillOpacity = parseDoubleSafe(v, 1);
            if (!(v = attrRaw(e, "stroke-opacity")).isEmpty()) s.strokeOpacity = parseDoubleSafe(v, 1);
            if (!(v = attrRaw(e, "opacity")).isEmpty()) s.groupAlpha *= clamp01(parseDoubleSafe(v, 1));
            if (!(v = attrRaw(e, "font-weight")).isEmpty()) s.fontWeight = v;
            return s;
        }

        double fillAlpha() { return clamp01(fillOpacity * groupAlpha); }
        double strokeAlpha() { return clamp01(strokeOpacity * groupAlpha); }

        BasicStroke basicStroke() {
            float w = (float) strokeWidth;
            if (dash != null && dash.length > 0) {
                // Java2D verlangt mind. einen positiven Eintrag.
                boolean ok = false;
                for (float dd : dash) if (dd > 0) ok = true;
                if (ok) return new BasicStroke(w, cap, join, 4f, dash, 0f);
            }
            return new BasicStroke(w, cap, join, 4f);
        }
    }

    // ===================== Verläufe =====================

    private static final class GradientDef {
        boolean linear;
        double x1, y1, x2 = 1, y2;       // linear (objectBoundingBox)
        double cx = 0.5, cy = 0.5, r = 0.5;  // radial
        float[] offsets;
        Color[] colors;

        Paint toPaint(Rectangle2D b, double alpha) {
            if (offsets == null || offsets.length == 0) return null;
            Color[] cs = new Color[colors.length];
            for (int i = 0; i < colors.length; i++) cs[i] = applyAlpha(colors[i], alpha);
            float[] fr = monotonic(offsets);
            if (fr.length == 1) return cs[0];
            double bx = b.getX(), by = b.getY(), bw = b.getWidth(), bh = b.getHeight();
            if (linear) {
                Point2D p0 = new Point2D.Double(bx + x1 * bw, by + y1 * bh);
                Point2D p1 = new Point2D.Double(bx + x2 * bw, by + y2 * bh);
                if (p0.distance(p1) < 1e-6) return cs[cs.length - 1];
                return new LinearGradientPaint(p0, p1, fr, cs);
            } else {
                float radius = (float) (r * Math.max(bw, bh));
                if (radius <= 0) return cs[0];
                Point2D center = new Point2D.Double(bx + cx * bw, by + cy * bh);
                return new RadialGradientPaint(center, radius, fr, cs);
            }
        }
    }

    private GradientDef parseGradient(Element e, boolean linear) {
        GradientDef g = new GradientDef();
        g.linear = linear;
        if (linear) {
            g.x1 = frac(attr(e, "x1", "0"));
            g.y1 = frac(attr(e, "y1", "0"));
            g.x2 = frac(attr(e, "x2", "1"));
            g.y2 = frac(attr(e, "y2", "0"));
        } else {
            g.cx = frac(attr(e, "cx", "0.5"));
            g.cy = frac(attr(e, "cy", "0.5"));
            g.r = frac(attr(e, "r", "0.5"));
        }
        List<Float> offs = new ArrayList<>();
        List<Color> cols = new ArrayList<>();
        NodeList ch = e.getChildNodes();
        for (int i = 0; i < ch.getLength(); i++) {
            if (!(ch.item(i) instanceof Element s) || !s.getTagName().equals("stop")) continue;
            offs.add((float) clamp01(frac(attr(s, "offset", "0"))));
            Color c = parseColor(attr(s, "stop-color", "#000000"));
            if (c == null) c = Color.BLACK;
            double so = parseDoubleSafe(attr(s, "stop-opacity", "1"), 1);
            cols.add(applyAlpha(c, so));
        }
        g.offsets = new float[offs.size()];
        g.colors = new Color[cols.size()];
        for (int i = 0; i < offs.size(); i++) { g.offsets[i] = offs.get(i); g.colors[i] = cols.get(i); }
        return g;
    }

    /** Erzwingt streng monoton steigende Fraktionen in [0,1] (Java2D-Anforderung). */
    private static float[] monotonic(float[] in) {
        float[] out = in.clone();
        for (int i = 0; i < out.length; i++) {
            if (out[i] < 0) out[i] = 0; if (out[i] > 1) out[i] = 1;
            if (i > 0 && out[i] <= out[i - 1]) out[i] = Math.min(1f, out[i - 1] + 1e-4f);
        }
        return out;
    }

    // ===================== Transform =====================

    private static AffineTransform parseTransform(String s) {
        AffineTransform at = new AffineTransform();
        int i = 0, n = s.length();
        while (i < n) {
            while (i < n && (s.charAt(i) == ' ' || s.charAt(i) == ',')) i++;
            int nameStart = i;
            while (i < n && s.charAt(i) != '(') i++;
            if (i >= n) break;
            String name = s.substring(nameStart, i).trim();
            int close = s.indexOf(')', i);
            if (close < 0) break;
            double[] a = nums(s.substring(i + 1, close));
            i = close + 1;
            switch (name) {
                case "translate" -> at.translate(a.length > 0 ? a[0] : 0, a.length > 1 ? a[1] : 0);
                case "scale" -> at.scale(a.length > 0 ? a[0] : 1, a.length > 1 ? a[1] : (a.length > 0 ? a[0] : 1));
                case "rotate" -> {
                    if (a.length >= 3) at.rotate(Math.toRadians(a[0]), a[1], a[2]);
                    else if (a.length >= 1) at.rotate(Math.toRadians(a[0]));
                }
                case "matrix" -> { if (a.length == 6) at.concatenate(new AffineTransform(a[0], a[1], a[2], a[3], a[4], a[5])); }
                case "skewX" -> { if (a.length >= 1) at.concatenate(new AffineTransform(1, 0, Math.tan(Math.toRadians(a[0])), 1, 0, 0)); }
                case "skewY" -> { if (a.length >= 1) at.concatenate(new AffineTransform(1, Math.tan(Math.toRadians(a[0])), 0, 1, 0, 0)); }
                default -> { }
            }
        }
        return at;
    }

    // ===================== Pfad-Parser =====================

    private static Path2D parsePath(String d) {
        Path2D.Double p = new Path2D.Double();
        if (d == null || d.isEmpty()) return p;
        PathTokens t = new PathTokens(d);
        double cx = 0, cy = 0, sx = 0, sy = 0;     // aktueller Punkt, Subpfad-Start
        double lastCx = 0, lastCy = 0;             // letzter Kontrollpunkt (für S/T)
        char prev = 0;
        char cmd = 0;
        boolean started = false;
        while (t.hasMore()) {
            if (t.atCommand()) { cmd = t.nextCommand(); }
            else if (cmd == 0) break;
            char eff = cmd;
            boolean rel = Character.isLowerCase(eff);
            char up = Character.toUpperCase(eff);
            switch (up) {
                case 'M' -> {
                    double x = t.num(), y = t.num();
                    if (rel && started) { x += cx; y += cy; }
                    cx = x; cy = y; sx = x; sy = y; p.moveTo(cx, cy); started = true;
                    cmd = rel ? 'l' : 'L';   // Folgepaare = LineTo
                }
                case 'L' -> { double x = t.num(), y = t.num(); if (rel) { x += cx; y += cy; } p.lineTo(x, y); cx = x; cy = y; }
                case 'H' -> { double x = t.num(); if (rel) x += cx; p.lineTo(x, cy); cx = x; }
                case 'V' -> { double y = t.num(); if (rel) y += cy; p.lineTo(cx, y); cy = y; }
                case 'C' -> {
                    double x1 = t.num(), y1 = t.num(), x2 = t.num(), y2 = t.num(), x = t.num(), y = t.num();
                    if (rel) { x1 += cx; y1 += cy; x2 += cx; y2 += cy; x += cx; y += cy; }
                    p.curveTo(x1, y1, x2, y2, x, y); lastCx = x2; lastCy = y2; cx = x; cy = y;
                }
                case 'S' -> {
                    double x2 = t.num(), y2 = t.num(), x = t.num(), y = t.num();
                    if (rel) { x2 += cx; y2 += cy; x += cx; y += cy; }
                    double x1 = cx, y1 = cy;
                    if (prevWasCubic(prev)) { x1 = 2 * cx - lastCx; y1 = 2 * cy - lastCy; }
                    p.curveTo(x1, y1, x2, y2, x, y); lastCx = x2; lastCy = y2; cx = x; cy = y;
                }
                case 'Q' -> {
                    double x1 = t.num(), y1 = t.num(), x = t.num(), y = t.num();
                    if (rel) { x1 += cx; y1 += cy; x += cx; y += cy; }
                    p.quadTo(x1, y1, x, y); lastCx = x1; lastCy = y1; cx = x; cy = y;
                }
                case 'T' -> {
                    double x = t.num(), y = t.num();
                    if (rel) { x += cx; y += cy; }
                    double x1 = cx, y1 = cy;
                    if (prevWasQuad(prev)) { x1 = 2 * cx - lastCx; y1 = 2 * cy - lastCy; }
                    p.quadTo(x1, y1, x, y); lastCx = x1; lastCy = y1; cx = x; cy = y;
                }
                case 'Z' -> { p.closePath(); cx = sx; cy = sy; }
                default -> { return p; }   // unbekannter Befehl (z.B. Arc) -> abbrechen
            }
            prev = up;
        }
        return p;
    }

    private static boolean prevWasCubic(char c) { return c == 'C' || c == 'S'; }
    private static boolean prevWasQuad(char c) { return c == 'Q' || c == 'T'; }

    /** Tokenizer für SVG-Pfaddaten: trennt Befehle und (auch zusammenhängende) Zahlen. */
    private static final class PathTokens {
        private final String s;
        private int i;
        PathTokens(String s) { this.s = s; }

        boolean hasMore() { skipSep(); return i < s.length(); }

        boolean atCommand() {
            skipSep();
            if (i >= s.length()) return false;
            char c = s.charAt(i);
            return Character.isLetter(c);
        }

        char nextCommand() { char c = s.charAt(i); i++; return c; }

        private void skipSep() {
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c == ' ' || c == ',' || c == '\t' || c == '\n' || c == '\r') i++; else break;
            }
        }

        double num() {
            skipSep();
            int start = i, len = s.length();
            if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-')) i++;
            boolean dot = false, exp = false;
            while (i < len) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') { i++; }
                else if (c == '.' && !dot && !exp) { dot = true; i++; }
                else if ((c == 'e' || c == 'E') && !exp) { exp = true; i++; if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-')) i++; }
                else break;
            }
            if (i == start) { i++; return 0; }   // Schutz gegen Endlosschleife
            try { return Double.parseDouble(s.substring(start, i)); } catch (Exception ex) { return 0; }
        }
    }

    // ===================== kleine Helfer =====================

    private static double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }

    private static Color applyAlpha(Color c, double alpha) {
        int a = (int) Math.round(clamp01(alpha) * c.getAlpha());
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    private static String refId(String url) {
        int a = url.indexOf('#');
        int b = url.indexOf(')', a);
        if (a < 0) return url;
        return b < 0 ? url.substring(a + 1) : url.substring(a + 1, b);
    }

    /** Wert wie "45%" -> 0.45, "0.5" -> 0.5, "1" -> 1. */
    private static double frac(String v) {
        if (v == null || v.isEmpty()) return 0;
        v = v.trim();
        if (v.endsWith("%")) return parseDoubleSafe(v.substring(0, v.length() - 1), 0) / 100.0;
        return parseDoubleSafe(v, 0);
    }

    private static Color parseColor(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty() || s.equals("none")) return null;
        if (s.startsWith("#")) {
            String h = s.substring(1);
            try {
                if (h.length() == 3) {
                    int r = Integer.parseInt(h.substring(0, 1), 16) * 17;
                    int g = Integer.parseInt(h.substring(1, 2), 16) * 17;
                    int b = Integer.parseInt(h.substring(2, 3), 16) * 17;
                    return new Color(r, g, b);
                }
                if (h.length() == 6) {
                    return new Color(Integer.parseInt(h.substring(0, 2), 16),
                                     Integer.parseInt(h.substring(2, 4), 16),
                                     Integer.parseInt(h.substring(4, 6), 16));
                }
            } catch (Exception ex) { return null; }
            return null;
        }
        return switch (s.toLowerCase()) {
            case "white" -> Color.WHITE;
            case "black" -> Color.BLACK;
            case "red" -> Color.RED;
            case "none" -> null;
            default -> null;
        };
    }

    private static float[] parseDash(String v) {
        double[] n = nums(v);
        if (n.length == 0) return null;
        float[] f = new float[n.length];
        for (int i = 0; i < n.length; i++) f[i] = (float) n[i];
        return f;
    }

    /** Zerlegt eine Folge von Zahlen (durch Leerzeichen/Kommas getrennt). */
    private static double[] nums(String s) {
        if (s == null || s.isEmpty()) return new double[0];
        List<Double> out = new ArrayList<>();
        PathTokens t = new PathTokens(s);
        while (t.hasMore()) {
            // bei Pfad-fremden Buchstaben abbrechen
            char c = s.length() > 0 ? peek(t) : 0;
            if (Character.isLetter(c) && c != 'e' && c != 'E' && c != '+' && c != '-') break;
            out.add(t.num());
        }
        double[] a = new double[out.size()];
        for (int i = 0; i < a.length; i++) a[i] = out.get(i);
        return a;
    }
    private static char peek(PathTokens t) { return t.i < t.s.length() ? t.s.charAt(t.i) : 0; }

    private static double d(Element e, String name, double def) {
        String v = e.getAttribute(name);
        if (v == null || v.isEmpty()) return def;
        return parseDoubleSafe(v.endsWith("%") ? v.substring(0, v.length() - 1) : v, def);
    }
    private static String attr(Element e, String name, String def) {
        String v = e.getAttribute(name);
        return (v == null || v.isEmpty()) ? def : v;
    }
    private static String attrRaw(Element e, String name) {
        String v = e.getAttribute(name);
        return v == null ? "" : v.trim();
    }
    private static double parseDoubleSafe(String v, double def) {
        try { return Double.parseDouble(v.trim()); } catch (Exception ex) { return def; }
    }
    private static int parseIntSafe(String v) {
        try { return Integer.parseInt(v.trim()); } catch (Exception ex) { return 0; }
    }
}
