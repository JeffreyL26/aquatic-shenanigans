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
public class MapPanel extends JPanel {

    private static final int TILE = 64;
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
        originX = (getWidth() - GameState.COLS * TILE) / 2;
        originY = (getHeight() - GameState.ROWS * TILE) / 2;
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
        if (SwingUtilities.isRightMouseButton(e)) { frame.clearTool(); return; }
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

        // Boden (zone-spezifisch)
        for (int r = 0; r < GameState.ROWS; r++)
            for (int c = 0; c < GameState.COLS; c++) {
                int x = originX + c * TILE, y = originY + r * TILE;
                g.setColor(((c + r) % 2 == 0) ? zone.floorA : zone.floorB);
                g.fillRect(x, y, TILE, TILE);
                g.setColor(Palette.GRID);
                g.drawRect(x, y, TILE, TILE);
            }
        drawAmbient(g, zone);

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

    /** Lebendigkeit: aufsteigende Blasen pro Zone. */
    private void drawAmbient(Graphics2D g, Zone zone) {
        int gx = originX, gy = originY, gw = GameState.COLS * TILE, gh = GameState.ROWS * TILE;
        g.setColor(new Color(255, 255, 255, 18));
        for (int i = 0; i < 26; i++) {
            double seed = i * 12.9898;
            double bx = gx + ((Math.abs(Math.sin(seed)) * gw));
            double speed = 12 + (i % 5) * 6;
            double by = gy + gh - ((anim * speed + i * 37) % gh);
            double rr = 2 + (i % 3);
            g.fill(new Ellipse2D.Double(bx, by, rr, rr));
        }
    }

    private void drawBuilding(Graphics2D g, Building b) {
        int x = originX + b.col * TILE, y = originY + b.row * TILE;
        drawGlyph(g, b.type, x, y, b.efficiency);
        if (b.type == BuildingType.SHRIMP_TANK) drawSwimmers(g, b, x, y);
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
}
