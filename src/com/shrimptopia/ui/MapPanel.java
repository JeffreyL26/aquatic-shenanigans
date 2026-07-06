package com.shrimptopia.ui;

import com.shrimptopia.model.Building;
import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.ShrimpTier;
import com.shrimptopia.model.Zone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Die Spielkarte der aktuellen Zone: Gitter, Gebäude, Animation und Maus-Interaktion. */
public class MapPanel extends JPanel implements Scrollable {

    private static final int TILE = 64;
    private static final int MARGIN = 20;
    private final GameFrame frame;
    private int originX, originY;
    private int hoverCol = -1, hoverRow = -1;
    private double anim = 0;

    public MapPanel(GameFrame frame) {
        this.frame = frame;
        setBackground(Palette.BG_DARK);
        setPreferredSize(new Dimension(GameState.COLS * TILE + 40, GameState.ROWS * TILE + 40));
        setToolTipText("");
        ToolTipManager.sharedInstance().setInitialDelay(250);
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e)  { updateHover(e); }
            @Override public void mouseDragged(MouseEvent e){ updateHover(e); }
            @Override public void mouseExited(MouseEvent e) { hoverCol = hoverRow = -1; repaint(); }
            @Override public void mousePressed(MouseEvent e){ handleClick(e); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void advanceAnim() { anim += 0.06; repaint(); }

    private void computeOrigin() {
        // Mittig, aber nie über den linken/oberen Rand hinaus (sonst wird das Gitter abgeschnitten,
        // ohne dass man an die verdeckten Kacheln herankommt). Der Rest ist per Scrollpane erreichbar.
        originX = Math.max(MARGIN, (getWidth() - GameState.COLS * TILE) / 2);
        originY = Math.max(MARGIN, (getHeight() - GameState.ROWS * TILE) / 2);
    }
    private int colAt(int mx) { return (mx - originX) / TILE; }
    private int rowAt(int my) { return (my - originY) / TILE; }

    private void updateHover(MouseEvent e) {
        computeOrigin();
        int c = colAt(e.getX()), r = rowAt(e.getY());
        if (e.getX() < originX || e.getY() < originY) { c = -1; r = -1; }
        if (c != hoverCol || r != hoverRow) { hoverCol = c; hoverRow = r; repaint(); }
    }

    private void handleClick(MouseEvent e) {
        computeOrigin();
        if (SwingUtilities.isRightMouseButton(e)) {
            // Mit aktiver Auswahl/Abriss: abwählen. Ohne Auswahl: das (fest angedockte)
            // Baumenü öffnen - wo der Rechtsklick landet, spielt keine Rolle.
            if (frame.tool() != GameFrame.Tool.NONE) { frame.clearTool(); return; }
            frame.openBuildMenu();
            return;
        }
        int c = colAt(e.getX()), r = rowAt(e.getY());
        if (e.getX() < originX || e.getY() < originY || !frame.game().inBounds(c, r)) return;
        Zone zone = frame.currentZone();
        GameFrame.Tool tool = frame.tool();
        if (tool == GameFrame.Tool.PLACE && frame.selectedType() != null) {
            frame.tryPlace(c, r);
        } else if (tool == GameFrame.Tool.DEMOLISH) {
            frame.tryDemolish(c, r);
        } else {
            Building b = frame.game().at(zone, c, r);
            frame.selectBuilding(b);
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        computeOrigin();
        GameState gs = frame.game();
        Zone zone = frame.currentZone();
        // Garage vs. Halle ist keine eigene Zone, sondern der Ausbau-Zustand der Produktion.
        boolean garage = zone == Zone.PRODUKTION && !gs.isUnlocked("era.HALLE");
        Color accent = garage ? GARAGE_ACCENT : zone.accent;

        // Zonen-getönter Hintergrund + Vignette (auch neben dem Gitter sichtbar)
        drawBackdrop(g, accent);

        // Boden (zone-/ära-spezifisch) mit Kachel-Relief und dezenten Gebrauchsspuren
        Color[] floor = floorColors(zone, garage);
        Color gridCol = mix(Palette.GRID, accent, 0.14);
        for (int r = 0; r < GameState.ROWS; r++)
            for (int c = 0; c < GameState.COLS; c++) {
                int x = originX + c * TILE, y = originY + r * TILE;
                g.setColor(((c + r) % 2 == 0) ? floor[0] : floor[1]);
                g.fillRect(x, y, TILE, TILE);
                // Relief: helle Ober-, dunkle Unterkante - die Kacheln wirken wie verlegt
                g.setColor(new Color(255, 255, 255, 8));
                g.drawLine(x + 1, y + 1, x + TILE - 2, y + 1);
                g.setColor(new Color(0, 0, 0, 30));
                g.drawLine(x + 1, y + TILE - 1, x + TILE - 1, y + TILE - 1);
                // deterministische Gebrauchsspuren (kein Zufall -> flackerfrei)
                int h = (c * 31 + r * 17) % 11;
                if (h == 0) {
                    g.setColor(new Color(0, 0, 0, garage ? 22 : 12));
                    g.fillRoundRect(x + 9 + (c % 3) * 8, y + 12 + (r % 4) * 6, 26, 16, 8, 8);
                }
                g.setColor(gridCol);
                g.drawRect(x, y, TILE, TILE);
            }
        drawFloorDecor(g, zone, garage, accent);
        drawWalls(g, zone, garage, accent);
        drawAmbient(g, zone, garage);

        // Gebäude der aktuellen Zone
        Building[][] grid = gs.grid(zone);
        for (int r = 0; r < GameState.ROWS; r++)
            for (int c = 0; c < GameState.COLS; c++) {
                Building b = grid[r][c];
                if (b != null) drawBuilding(g, b);
            }

        // Auswahl-Hervorhebung
        Building sel = frame.selectedBuilding();
        if (sel != null && sel.zone == zone) {
            int x = originX + sel.col * TILE, y = originY + sel.row * TILE;
            float p = (float) (0.5 + 0.5 * Math.sin(anim * 2));
            g.setColor(new Color(0, 199, 183, (int) (120 + 80 * p)));
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(x + 2, y + 2, TILE - 4, TILE - 4, 12, 12);
        }

        // Hover / Bau-Vorschau
        if (gs.inBounds(hoverCol, hoverRow)) {
            int x = originX + hoverCol * TILE, y = originY + hoverRow * TILE;
            GameFrame.Tool tool = frame.tool();
            if (tool == GameFrame.Tool.PLACE && frame.selectedType() != null) {
                BuildingType t = frame.selectedType();
                boolean ok = gs.canPlace(zone, hoverCol, hoverRow) && gs.getMoney() >= t.cost
                        && gs.isBuildingUnlocked(t) && t.zone() == zone;
                Color edge = ok ? Palette.GOOD : Palette.BAD;
                g.setColor(new Color(edge.getRed(), edge.getGreen(), edge.getBlue(), 40));
                g.fillRect(x, y, TILE, TILE);
                if (gs.canPlace(zone, hoverCol, hoverRow)) {
                    Composite old = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
                    drawGlyph(g, t, x, y, 1.0);
                    g.setComposite(old);
                }
                g.setColor(edge); g.setStroke(new BasicStroke(2.5f));
                g.drawRect(x + 2, y + 2, TILE - 4, TILE - 4);
            } else if (tool == GameFrame.Tool.DEMOLISH && gs.at(zone, hoverCol, hoverRow) != null) {
                g.setColor(new Color(255, 92, 92, 60)); g.fillRect(x, y, TILE, TILE);
                g.setColor(Palette.BAD); g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, 0));
                g.drawLine(x + 14, y + 14, x + TILE - 14, y + TILE - 14);
                g.drawLine(x + TILE - 14, y + 14, x + 14, y + TILE - 14);
            } else {
                g.setColor(new Color(0, 199, 183, 60)); g.setStroke(new BasicStroke(2f));
                g.drawRect(x + 1, y + 1, TILE - 2, TILE - 2);
            }
        }

        drawZoneNameplate(g, zone, garage, accent);
        drawToast(g, gs);
        g.dispose();
    }

    /** Wichtige Log-Meldungen kurz als Banner oben auf der Karte einblenden (Log-Ticker allein ist zu dezent). */
    private static final long TOAST_MS = 6000;
    private void drawToast(Graphics2D g, GameState gs) {
        long now = System.currentTimeMillis();
        java.util.List<GameState.LogLine> log = gs.getLog();
        GameState.LogLine toast = null;
        for (int i = log.size() - 1; i >= 0 && i >= log.size() - 8; i--) {
            GameState.LogLine ll = log.get(i);
            if (ll.kind != GameState.LOG_INFO && now - ll.time < TOAST_MS) { toast = ll; break; }
        }
        if (toast == null) return;
        long age = now - toast.time;
        float alpha = age > TOAST_MS - 1200 ? (TOAST_MS - age) / 1200f : 1f;   // sanft ausblenden
        Color edge = switch (toast.kind) {
            case GameState.LOG_GOOD -> Palette.GOOD;
            case GameState.LOG_BAD  -> Palette.BAD;
            default                 -> Palette.WARN;
        };
        g.setFont(Palette.FONT_BOLD);
        FontMetrics fm = g.getFontMetrics();
        String text = TextUtil.clip(fm, toast.text, getWidth() - 140);
        int tw = fm.stringWidth(text);
        int bw = tw + 46, bh = 30;
        int bx = (getWidth() - bw) / 2, by = originY + 8;
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, Math.min(1, alpha))));
        g.setColor(new Color(16, 22, 27, 225));
        g.fillRoundRect(bx, by, bw, bh, 14, 14);
        g.setColor(edge);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(bx, by, bw, bh, 14, 14);
        g.fillOval(bx + 12, by + bh / 2 - 5, 10, 10);
        g.setColor(Palette.TEXT);
        g.drawString(text, bx + 30, by + bh / 2 + fm.getAscent() / 2 - 2);
        g.setComposite(old);
    }

    /** Warmer Amber-Ton für den Garagen-Zustand (die Halle nutzt die Zonen-Signaturfarbe). */
    private static final Color GARAGE_ACCENT = new Color(198, 150, 96);

    private static Color mix(Color a, Color b, double t) {
        return new Color(
            (int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t),
            (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
    }
    private static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }

    /** Boden-Farbpaar je Zone/Ära. Die Garage: roher, gegossener Beton (kaum Karo-Muster). */
    private static Color[] floorColors(Zone zone, boolean garage) {
        if (garage) return new Color[]{ new Color(70, 66, 60), new Color(67, 63, 57) };
        return new Color[]{ zone.floorA, zone.floorB };
    }

    /** Zonen-getönter Radial-Hintergrund + Vignette über die ganze Fläche (auch neben dem Gitter). */
    private void drawBackdrop(Graphics2D g, Color accent) {
        int w = getWidth(), h = getHeight();
        double cx = originX + GameState.COLS * TILE / 2.0, cy = originY + GameState.ROWS * TILE / 2.0;
        float rad = Math.max(240f, Math.max(w, h) * 0.72f);
        Color center = mix(Palette.BG_DARK, accent, 0.12);
        Color edge   = Icons.darker(Palette.BG_DARK, 0.55);
        g.setPaint(new RadialGradientPaint(new java.awt.geom.Point2D.Double(cx, cy), rad,
            new float[]{0f, 1f}, new Color[]{center, edge}));
        g.fillRect(0, 0, w, h);
    }

    /**
     * Gebäudehülle statt bloßem Rahmen: massive Wände rund ums Gitter, ein Rolltor unten
     * (Produktion/Garage), Decken-Schatten am oberen Rand und Niet-Ecken - die Karte wirkt
     * wie ein Raum, nicht wie eine schwebende Tabelle.
     */
    private void drawWalls(Graphics2D g, Zone zone, boolean garage, Color accent) {
        int gx = originX, gy = originY, gw = GameState.COLS * TILE, gh = GameState.ROWS * TILE;
        int t = 10;   // Wandstärke

        // Decken-Schatten: oben fällt Schatten in den Raum (Tiefe)
        g.setPaint(new GradientPaint(0, gy, new Color(0, 0, 0, 70), 0, gy + 44, new Color(0, 0, 0, 0)));
        g.fillRect(gx, gy, gw, 44);
        g.setPaint(new GradientPaint(gx, 0, new Color(0, 0, 0, 40), gx + 26, 0, new Color(0, 0, 0, 0)));
        g.fillRect(gx, gy, 26, gh);

        // Wände (Ring aus vier Rechtecken)
        Color wall = garage ? new Color(66, 55, 45) : mix(new Color(34, 44, 52), accent, 0.20);
        Color wallEdge = Icons.darker(wall, 0.62);
        g.setColor(wall);
        g.fillRect(gx - t, gy - t, gw + 2 * t, t);            // oben
        g.fillRect(gx - t, gy + gh, gw + 2 * t, t);           // unten
        g.fillRect(gx - t, gy - t, t, gh + 2 * t);            // links
        g.fillRect(gx + gw, gy - t, t, gh + 2 * t);           // rechts
        g.setColor(wallEdge);
        g.setStroke(new BasicStroke(1.4f));
        g.drawRect(gx - t, gy - t, gw + 2 * t - 1, gh + 2 * t - 1);
        // Akzent-Lichtleiste an der Wand-Innenkante
        g.setColor(alpha(accent, 150));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(gx - 1, gy - 1, gw + 1, gh + 1);

        // Rolltor unten Mitte (Produktion & Garage): hier fährt der Lieferwagen rein
        if (zone == Zone.PRODUKTION) {
            int dw = garage ? 84 : 120;
            int dx = gx + (gw - dw) / 2;
            g.setColor(Icons.darker(wall, 0.8));
            g.fillRect(dx, gy + gh, dw, t);
            g.setColor(alpha(garage ? new Color(230, 190, 120) : accent, 190));
            g.setStroke(new BasicStroke(2.2f));
            g.drawLine(dx, gy + gh + 2, dx + dw, gy + gh + 2);
            g.setColor(new Color(255, 255, 255, 60));
            for (int lx = dx + 8; lx < dx + dw - 6; lx += 12)
                g.drawLine(lx, gy + gh + 4, lx, gy + gh + t - 2);
        }

        // Niet-Ecken
        g.setColor(alpha(accent, 130));
        int rr = 3;
        g.fillOval(gx - t / 2 - rr, gy - t / 2 - rr, 2 * rr, 2 * rr);
        g.fillOval(gx + gw + t / 2 - rr, gy - t / 2 - rr, 2 * rr, 2 * rr);
        g.fillOval(gx - t / 2 - rr, gy + gh + t / 2 - rr, 2 * rr, 2 * rr);
        g.fillOval(gx + gw + t / 2 - rr, gy + gh + t / 2 - rr, 2 * rr, 2 * rr);
    }

    /** Deko auf dem Boden (unter den Gebäuden): gibt jeder Karte einen eigenen Charakter. */
    private void drawFloorDecor(Graphics2D g, Zone zone, boolean garage, Color accent) {
        int gx = originX, gy = originY, gw = GameState.COLS * TILE, gh = GameState.ROWS * TILE;
        Shape oldClip = g.getClip();
        g.clip(new java.awt.geom.Rectangle2D.Double(gx, gy, gw, gh));
        if (garage)                     decorGarage(g, gx, gy, gw, gh);
        else switch (zone) {
            case PRODUKTION -> decorHall(g, gx, gy, gw, gh, accent);
            case FORSCHUNG  -> decorLab(g, gx, gy, gw, gh, accent);
            case LOGISTIK   -> decorLogistics(g, gx, gy, gw, gh, accent);
            case EMPFANG    -> decorGarden(g, gx, gy, gw, gh, accent);
        }
        g.setClip(oldClip);
    }

    /**
     * Garage v2: roher Betonboden mit Dehnungsfugen und Rissen, Werkbank + Pegboard an der
     * Nordwand, Fensterlicht von links oben, Kistenstapel, Kabelstrang und Ölflecken -
     * eine echte Hobby-Werkstatt statt Schachbrett.
     */
    private void decorGarage(Graphics2D g, int gx, int gy, int gw, int gh) {
        // Dehnungsfugen im Beton (alle 3 Kacheln, deutlich kräftiger als das Kachelraster)
        g.setColor(new Color(40, 37, 33, 190));
        g.setStroke(new BasicStroke(3f));
        for (int c = 3; c < GameState.COLS; c += 3) g.drawLine(gx + c * 64, gy, gx + c * 64, gy + gh);
        for (int r = 3; r < GameState.ROWS; r += 3) g.drawLine(gx, gy + r * 64, gx + gw, gy + r * 64);
        // Risse (deterministisch)
        g.setColor(new Color(38, 35, 31, 150));
        g.setStroke(new BasicStroke(1.4f));
        g.draw(new java.awt.geom.Path2D.Double() {{
            moveTo(gx + gw * 0.18, gy + gh * 0.62); lineTo(gx + gw * 0.24, gy + gh * 0.7);
            lineTo(gx + gw * 0.22, gy + gh * 0.78); lineTo(gx + gw * 0.3, gy + gh * 0.86);
        }});
        g.draw(new java.awt.geom.Path2D.Double() {{
            moveTo(gx + gw * 0.7, gy + gh * 0.2); lineTo(gx + gw * 0.76, gy + gh * 0.3);
            lineTo(gx + gw * 0.73, gy + gh * 0.38);
        }});

        // Werkbank-Zeile an der Nordwand: Platte, Pegboard-Punkte, Werkzeug-Silhouetten
        int benchH = 26;
        g.setColor(new Color(96, 74, 50));
        g.fillRect(gx, gy, gw, benchH);
        g.setColor(new Color(70, 54, 36));
        g.drawLine(gx, gy + benchH, gx + gw, gy + benchH);
        g.setColor(new Color(120, 96, 66));
        for (int x = gx + 20; x < gx + gw - 10; x += 42) g.drawLine(x, gy + 3, x, gy + benchH - 3);
        // Pegboard-Punkte + Werkzeuge
        g.setColor(new Color(50, 40, 28));
        for (int x = gx + 10; x < gx + gw - 8; x += 14) g.fillOval(x, gy + 5, 3, 3);
        g.setColor(new Color(200, 200, 205, 170));
        g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(gx + 46, gy + 9, gx + 46, gy + 21);   // Schraubenzieher
        g.drawLine(gx + 43, gy + 9, gx + 49, gy + 9);
        g.drawOval(gx + gw / 2 - 5, gy + 8, 10, 10);      // Rolle Klebeband
        g.drawLine(gx + gw - 70, gy + 8, gx + gw - 60, gy + 20);   // Schraubschlüssel
        g.drawOval(gx + gw - 74, gy + 5, 7, 7);

        // Fensterlicht: schräger Lichtkegel von links oben über den Boden
        java.awt.geom.Path2D beam = new java.awt.geom.Path2D.Double();
        beam.moveTo(gx + gw * 0.06, gy + benchH);
        beam.lineTo(gx + gw * 0.3, gy + benchH);
        beam.lineTo(gx + gw * 0.52, gy + gh);
        beam.lineTo(gx + gw * 0.16, gy + gh);
        beam.closePath();
        g.setPaint(new GradientPaint(gx, gy, new Color(255, 226, 160, 42), gx + gw * 0.45f, gy + gh, new Color(255, 226, 160, 4)));
        g.fill(beam);

        // Ölflecken (weniger, aber satter)
        for (int i = 0; i < 3; i++) {
            double ox = gx + gw * (0.32 + i * 0.24);
            double oy = gy + gh * (0.42 + (i % 2) * 0.3);
            double rw = 46 + i * 14, rh = rw * 0.55;
            g.setColor(new Color(14, 13, 15, 70));
            g.fill(new Ellipse2D.Double(ox - rw / 2, oy - rh / 2, rw, rh));
            g.setColor(new Color(30, 30, 40, 60));
            g.fill(new Ellipse2D.Double(ox - rw * 0.28, oy - rh * 0.24, rw * 0.5, rh * 0.5));
        }

        // Kistenstapel unten rechts + Ersatzreifen
        int bx = gx + gw - 86, by = gy + gh - 62;
        g.setColor(new Color(150, 116, 74));
        g.fillRect(bx, by + 18, 40, 30);
        g.fillRect(bx + 8, by - 8, 34, 26);
        g.setColor(new Color(96, 72, 44));
        g.drawRect(bx, by + 18, 40, 30);
        g.drawRect(bx + 8, by - 8, 34, 26);
        g.drawLine(bx, by + 33, bx + 40, by + 33);
        g.setColor(new Color(26, 26, 28));
        g.fillOval(bx - 34, by + 16, 34, 34);
        g.setColor(new Color(70, 70, 74));
        g.drawOval(bx - 26, by + 24, 18, 18);

        // Kabelstrang von der Westwand zur Mitte (Mehrfachsteckdose)
        g.setColor(new Color(30, 30, 34, 200));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new java.awt.geom.CubicCurve2D.Double(gx, gy + gh * 0.55, gx + gw * 0.12, gy + gh * 0.62,
            gx + gw * 0.2, gy + gh * 0.48, gx + gw * 0.3, gy + gh * 0.58));
        g.setColor(new Color(230, 230, 235));
        g.fillRoundRect((int) (gx + gw * 0.3), (int) (gy + gh * 0.575), 26, 10, 3, 3);
        g.setColor(new Color(40, 40, 44));
        g.drawRoundRect((int) (gx + gw * 0.3), (int) (gy + gh * 0.575), 26, 10, 3, 3);

        // dunkle Ecken-Vignette (enge Garage)
        g.setPaint(new RadialGradientPaint(new java.awt.geom.Point2D.Double(gx + gw / 2.0, gy + gh / 2.0),
            Math.max(gw, gh) * 0.62f, new float[]{0.55f, 1f}, new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 96)}));
        g.fillRect(gx, gy, gw, gh);
    }

    /** Halle: Sicherheits-/Fahrbahn-Markierungen, Warnband, Stützpfeiler - industriell & geordnet. */
    private void decorHall(Graphics2D g, int gx, int gy, int gw, int gh, Color accent) {
        // Warnband (schräge Streifen) am oberen Innenrand
        int bandH = 12;
        Shape old = g.getClip();
        g.clip(new java.awt.geom.Rectangle2D.Double(gx, gy, gw, bandH));
        g.setColor(new Color(240, 200, 60, 70));
        for (int x = gx - gh; x < gx + gw; x += 26) {
            java.awt.geom.Path2D.Double stripe = new java.awt.geom.Path2D.Double();
            stripe.moveTo(x, gy + bandH); stripe.lineTo(x + 13, gy);
            stripe.lineTo(x + 26, gy); stripe.lineTo(x + 13, gy + bandH); stripe.closePath();
            g.fill(stripe);
        }
        g.setClip(old);
        // gestrichelte Mittel-Fahrbahnlinie
        g.setColor(alpha(accent, 60));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{18f, 14f}, 0f));
        int midY = gy + gh / 2;
        g.draw(new java.awt.geom.Line2D.Double(gx + 20, midY, gx + gw - 20, midY));
        // Stützpfeiler an einigen Gitterknoten
        g.setStroke(new BasicStroke(2f));
        for (int cx = 2; cx < GameState.COLS; cx += 4)
            for (int cy = 2; cy < GameState.ROWS; cy += 4) {
                int px = gx + cx * TILE, py = gy + cy * TILE;
                g.setColor(new Color(255, 255, 255, 18));
                g.fillRect(px - 6, py - 6, 12, 12);
                g.setColor(alpha(accent, 70));
                g.drawRect(px - 6, py - 6, 12, 12);
            }
    }

    /** Forschung: Knotengitter mit Glühpunkten und Leiterbahn - technisch & kühl. */
    private void decorLab(Graphics2D g, int gx, int gy, int gw, int gh, Color accent) {
        for (int c = 1; c < GameState.COLS; c += 2)
            for (int r = 1; r < GameState.ROWS; r += 2) {
                int px = gx + c * TILE, py = gy + r * TILE;
                g.setColor(alpha(accent, 55));
                g.fill(new Ellipse2D.Double(px - 2.5, py - 2.5, 5, 5));
                g.setColor(alpha(accent, 18));
                g.fill(new Ellipse2D.Double(px - 6, py - 6, 12, 12));
            }
        // Leiterbahn
        g.setColor(alpha(accent, 45));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        java.awt.geom.Path2D trace = new java.awt.geom.Path2D.Double();
        trace.moveTo(gx + TILE, gy + TILE);
        trace.lineTo(gx + 5 * TILE, gy + TILE);
        trace.lineTo(gx + 5 * TILE, gy + 3 * TILE);
        trace.lineTo(gx + 9 * TILE, gy + 3 * TILE);
        trace.lineTo(gx + 9 * TILE, gy + 5 * TILE);
        g.draw(trace);
    }

    /** Logistik: Verladespuren mit Pfeil-Chevrons Richtung Ausgang - Umschlagplatz. */
    private void decorLogistics(Graphics2D g, int gx, int gy, int gw, int gh, Color accent) {
        // zwei Fahrspuren mit Chevrons Richtung rechts (Export)
        int[] lanes = { gy + gh / 4, gy + 3 * gh / 4 };
        for (int ly : lanes) {
            g.setColor(alpha(accent, 55));
            g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int x = gx + 40; x < gx + gw - 60; x += 70) {
                g.draw(new java.awt.geom.Line2D.Double(x, ly - 14, x + 22, ly));
                g.draw(new java.awt.geom.Line2D.Double(x, ly + 14, x + 22, ly));
            }
        }
        // gestrichelte Spurtrenner
        g.setColor(new Color(230, 230, 230, 28));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{16f, 12f}, 0f));
        g.draw(new java.awt.geom.Line2D.Double(gx + 20, gy + gh / 2, gx + gw - 20, gy + gh / 2));
        // schraffierte Dock-Kante rechts
        Shape old = g.getClip();
        g.clip(new java.awt.geom.Rectangle2D.Double(gx + gw - 14, gy, 14, gh));
        g.setColor(alpha(accent, 50));
        g.setStroke(new BasicStroke(3f));
        for (int y = gy - gw; y < gy + gh; y += 16)
            g.draw(new java.awt.geom.Line2D.Double(gx + gw - 16, y, gx + gw, y + 16));
        g.setClip(old);
    }

    /** Empfang & Garten: Steinpfad, Grasbüschel, Teich - grün & einladend. */
    private void decorGarden(Graphics2D g, int gx, int gy, int gw, int gh, Color accent) {
        // warmes Sonnenlicht oben rechts
        g.setPaint(new RadialGradientPaint(new java.awt.geom.Point2D.Double(gx + gw * 0.82, gy + gh * 0.16),
            gw * 0.5f, new float[]{0f, 1f}, new Color[]{new Color(255, 240, 170, 40), new Color(255, 240, 170, 0)}));
        g.fillRect(gx, gy, gw, gh);
        // geschwungener Steinpfad
        g.setColor(new Color(210, 205, 185, 55));
        g.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new java.awt.geom.QuadCurve2D.Double(gx + 20, gy + gh - 30, gx + gw * 0.5, gy + gh * 0.4, gx + gw - 30, gy + 40));
        g.setColor(new Color(150, 145, 125, 40));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new java.awt.geom.QuadCurve2D.Double(gx + 20, gy + gh - 30, gx + gw * 0.5, gy + gh * 0.4, gx + gw - 30, gy + 40));
        // Teich unten links
        g.setColor(new Color(64, 150, 190, 60));
        g.fill(new Ellipse2D.Double(gx + 30, gy + gh - 90, 120, 66));
        g.setColor(new Color(150, 210, 230, 70));
        g.setStroke(new BasicStroke(1.6f));
        g.draw(new Ellipse2D.Double(gx + 52, gy + gh - 74, 60, 30));
        // Grasbüschel
        g.setColor(alpha(accent, 90));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 14; i++) {
            double tx = gx + Math.abs(Math.sin(i * 7.3)) * (gw - 40) + 20;
            double ty = gy + Math.abs(Math.cos(i * 4.9)) * (gh - 40) + 20;
            g.draw(new java.awt.geom.Line2D.Double(tx, ty, tx - 4, ty - 9));
            g.draw(new java.awt.geom.Line2D.Double(tx, ty, tx, ty - 11));
            g.draw(new java.awt.geom.Line2D.Double(tx, ty, tx + 4, ty - 9));
        }
    }

    /** Lebendigkeit: zonenspezifische Schwebeteilchen (Staub, Dampf, Glühfunken, Blätter). */
    private void drawAmbient(Graphics2D g, Zone zone, boolean garage) {
        int gx = originX, gy = originY, gw = GameState.COLS * TILE, gh = GameState.ROWS * TILE;
        if (garage) {
            // träge Staubkörnchen im Funzellicht
            g.setColor(new Color(255, 226, 170, 26));
            for (int i = 0; i < 18; i++) {
                double px = gx + (Math.abs(Math.sin(i * 12.9)) * gw);
                double py = gy + gh - ((anim * (5 + i % 3) + i * 41) % gh);
                double rr = 1.5 + (i % 2);
                g.fill(new Ellipse2D.Double(px, py, rr, rr));
            }
            return;
        }
        switch (zone) {
            case PRODUKTION -> {   // Dampfschwaden
                g.setColor(new Color(255, 255, 255, 16));
                for (int i = 0; i < 22; i++) {
                    double px = gx + (Math.abs(Math.sin(i * 12.9)) * gw);
                    double py = gy + gh - ((anim * (14 + (i % 5) * 6) + i * 37) % gh);
                    double rr = 2 + (i % 3);
                    g.fill(new Ellipse2D.Double(px, py, rr, rr));
                }
            }
            case FORSCHUNG -> {   // schwebende Glühfunken
                for (int i = 0; i < 20; i++) {
                    double px = gx + (Math.abs(Math.sin(i * 9.1)) * gw) + Math.sin(anim + i) * 8;
                    double py = gy + gh - ((anim * (8 + i % 4) + i * 53) % gh);
                    g.setColor(alpha(zone.accent, 60));
                    g.fill(new Ellipse2D.Double(px, py, 3, 3));
                    g.setColor(alpha(zone.accent, 20));
                    g.fill(new Ellipse2D.Double(px - 2, py - 2, 7, 7));
                }
            }
            case LOGISTIK -> {   // feiner Staub, seitlich driftend
                g.setColor(new Color(255, 235, 200, 16));
                for (int i = 0; i < 16; i++) {
                    double px = gx + ((anim * (10 + i % 4) + i * 61) % gw);
                    double py = gy + (Math.abs(Math.cos(i * 5.5)) * gh);
                    g.fill(new Ellipse2D.Double(px, py, 2, 2));
                }
            }
            case EMPFANG -> {   // fallende Blätter/Pollen
                for (int i = 0; i < 16; i++) {
                    double px = gx + (Math.abs(Math.sin(i * 6.3)) * gw) + Math.sin(anim * 0.8 + i) * 16;
                    double py = gy + ((anim * (7 + i % 3) + i * 47) % gh);
                    g.setColor(alpha(i % 2 == 0 ? zone.accent : new Color(230, 200, 90), 55));
                    g.fill(new Ellipse2D.Double(px, py, 3.5, 2.4));
                }
            }
        }
    }

    /** Karten-Schild oben links: Zonenname bzw. GARAGE/HOF, damit sofort klar ist, wo man ist. */
    private void drawZoneNameplate(Graphics2D g, Zone zone, boolean garage, Color accent) {
        String name = garage
            ? (frame.game().isUnlocked("era.HOF") ? "GARAGE & HOF" : "GARAGE")
            : zone.displayName.toUpperCase();
        g.setFont(Palette.FONT_BOLD);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(name);
        int chipW = tw + 20 + 16, chipH = 26;
        int x = originX - 3;
        int y = originY - chipH - 9;
        if (y < 4) y = originY + 6;
        g.setColor(new Color(14, 19, 24, 232));
        g.fillRoundRect(x, y, chipW, chipH, 9, 9);
        g.setColor(accent);
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(x, y, chipW, chipH, 9, 9);
        g.setColor(accent);
        g.fillOval(x + 10, y + chipH / 2 - 4, 8, 8);
        g.setColor(Palette.TEXT);
        g.drawString(name, x + 26, y + chipH / 2 + fm.getAscent() / 2 - 1);
    }

    private void drawBuilding(Graphics2D g, Building b) {
        int x = originX + b.col * TILE, y = originY + b.row * TILE;
        // weicher Bodenschatten unter dem Gebäude (Tiefe)
        g.setColor(new Color(0, 0, 0, 55));
        g.fill(new Ellipse2D.Double(x + 7, y + TILE - 13, TILE - 14, 9));
        drawGlyph(g, b.type, x, y, b.efficiency);
        if (b.type.isTank()) drawSwimmers(g, b, x, y);
        Color dot = b.efficiency > 0.85 ? Palette.GOOD : b.efficiency > 0.35 ? Palette.WARN : Palette.BAD;
        if (b.type.workerNeed == 0 && b.type.powerUse == 0 && b.type.powerProduce == 0) dot = Palette.GOOD;
        g.setColor(dot);
        g.fillOval(x + TILE - 16, y + 8, 8, 8);
    }

    /** Kleine schwimmende Shrimps im Becken (Farbe = produziertes Tier). */
    private void drawSwimmers(Graphics2D g, Building b, int x, int y) {
        ShrimpTier tier = b.lastStats != null ? b.lastStats.tier : ShrimpTier.STANDARD;
        Color c = tier.color;
        int n = 2 + (int) Math.round(b.efficiency * 3);
        for (int i = 0; i < n; i++) {
            double ph = anim * 1.6 + i * 2.1;
            double sx = x + 16 + (Math.sin(ph) * 0.5 + 0.5) * (TILE - 32);
            double sy = y + 24 + (Math.cos(ph * 0.8 + i) * 0.5 + 0.5) * (TILE - 38);
            g.setColor(c);
            g.fill(new Ellipse2D.Double(sx, sy, 5, 3.2));
        }
    }

    private void drawGlyph(Graphics2D g, BuildingType t, int x, int y, double eff) {
        int inset = 5;
        RoundRectangle2D pad = new RoundRectangle2D.Double(x + inset, y + inset, TILE - 2 * inset, TILE - 2 * inset, 12, 12);
        g.setColor(Icons.darker(t.color, 0.5));
        g.fill(pad);
        g.setColor(t.color);
        g.fill(new RoundRectangle2D.Double(x + inset, y + inset, TILE - 2 * inset, TILE - 2 * inset - 4, 12, 12));
        if (eff < 0.85) { g.setColor(new Color(16, 22, 27, (int) (140 * (1 - eff)))); g.fill(pad); }
        double cx = x + TILE / 2.0, cy = y + TILE / 2.0 - 4;
        Icons.building(g, t.icon, cx, cy, TILE * 0.52);
        g.setFont(Palette.FONT_TINY);
        FontMetrics fm = g.getFontMetrics();
        String name = TextUtil.clip(fm, t.shortName(), TILE - 4);
        g.setColor(new Color(255, 255, 255, 220));
        g.drawString(name, (int) (cx - fm.stringWidth(name) / 2.0), y + TILE - 9);
    }

    @Override public String getToolTipText(MouseEvent e) {
        computeOrigin();
        int c = colAt(e.getX()), r = rowAt(e.getY());
        if (e.getX() < originX || e.getY() < originY) return null;
        GameState gs = frame.game();
        if (!gs.inBounds(c, r)) return null;
        Building b = gs.at(frame.currentZone(), c, r);
        if (b != null) {
            int eff = (int) Math.round(b.efficiency * 100);
            String status = b.statusNote.isEmpty()
                ? "<font color='#60dc78'>läuft (" + eff + "%)</font>"
                : "<font color='#ffbe46'>" + b.statusNote + " (" + eff + "%)</font>";
            String tierTxt = b.type == BuildingType.SHRIMP_TANK
                ? "<br>Produziert: " + b.lastStats.tier.displayName : "";
            return "<html><body style='width:230px'><b>" + b.type.displayName + "</b><br>" + status
                + tierTxt + "<br><i>Klick für Inspektor</i></body></html>";
        }
        if (frame.tool() == GameFrame.Tool.PLACE && frame.selectedType() != null) {
            BuildingType t = frame.selectedType();
            return "<html><body style='width:230px'><b>" + t.displayName + "</b> &nbsp; <font color='#ffcd56'>"
                + t.cost + " Geld</font><br><span style='color:#aab'>" + t.description + "</span></body></html>";
        }
        return null;
    }

    // --- Scrollable: füllt den Viewport, wenn Platz ist (dann zentriertes Gitter), sonst
    //     behält das Gitter seine volle Größe und wird per Scrollbalken erreichbar. ---
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle vis, int orientation, int direction) { return TILE; }
    @Override public int getScrollableBlockIncrement(Rectangle vis, int orientation, int direction) { return TILE * 3; }
    @Override public boolean getScrollableTracksViewportWidth() {
        return getParent() instanceof JViewport vp && vp.getWidth() >= getPreferredSize().width;
    }
    @Override public boolean getScrollableTracksViewportHeight() {
        return getParent() instanceof JViewport vp && vp.getHeight() >= getPreferredSize().height;
    }
}
