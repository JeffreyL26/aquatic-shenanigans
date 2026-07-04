package com.shrimptopia.ui;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.Zone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tropico-Stil-Baumenü: fest neben der Seitenleiste angedockt, geöffnet über den
 * BAUEN-Knopf, die Taste B oder Rechtsklick auf die Karte (Position des Klicks ist egal).
 * Gebäude sind in Typ-Reiter gruppiert (Strom, Versorgung, Zucht ...); ein Klick wählt
 * das Gebäude zum Platzieren und schließt das Menü. Klick daneben schließt ebenfalls.
 */
public class BuildMenuPanel extends JComponent {

    /** Eine Kategorie (Reiter) mit ihren Gebäuden. */
    private record Cat(String name, BuildingType... types) {}

    private static final Cat[] CATS = {
        new Cat("Strom",     BuildingType.OLD_GENERATOR, BuildingType.POWER_PLANT, BuildingType.SOLAR_ROOF),
        new Cat("Versorgung", BuildingType.RAIN_BARREL, BuildingType.WATER_PLANT, BuildingType.WATER_HUB,
                              BuildingType.ALGAE_BUCKET, BuildingType.ALGAE_FARM),
        new Cat("Zucht",     BuildingType.GARAGE_TANK, BuildingType.SHRIMP_TANK),
        new Cat("Verkauf",   BuildingType.YARD_SALE, BuildingType.SALES_OFFICE, BuildingType.RESTAURANT,
                             BuildingType.EXPORT_DOCK, BuildingType.MILITARY_DEPOT, BuildingType.BLACK_MARKET,
                             BuildingType.BOOST_STAND),
        new Cat("Personal & Lager", BuildingType.CAMPER, BuildingType.HOUSING,
                             BuildingType.STORAGE_SHED, BuildingType.WAREHOUSE),
        new Cat("Industrie", BuildingType.LAB, BuildingType.GENLAB, BuildingType.SHELL_PRESS,
                             BuildingType.SHRIMPBOOST_FACTORY, BuildingType.ROBOT_WORKS, BuildingType.KRILL_BARRACKS),
        new Cat("Ruf & Deko", BuildingType.VISITOR_CENTER, BuildingType.ZEN_GARDEN),
    };

    private static final int MENU_W = 336, ROW_H = 54, TAB_H = 30, HEAD_H = 34;

    private final GameFrame frame;
    private final Rectangle card = new Rectangle();
    private List<Cat> visibleCats = new ArrayList<>();
    private int catIdx = 0;
    private final List<Rectangle> tabRects = new ArrayList<>();
    private final List<Rectangle> rowRects = new ArrayList<>();
    private final List<BuildingType> rowTypes = new ArrayList<>();
    private int hoverRow = -1;

    public BuildMenuPanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setVisible(false);
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!card.contains(e.getPoint())) { close(); return; }
                for (int i = 0; i < tabRects.size(); i++)
                    if (tabRects.get(i).contains(e.getPoint())) { catIdx = i; relayout(); repaint(); return; }
                for (int i = 0; i < rowRects.size(); i++)
                    if (rowRects.get(i).contains(e.getPoint())) {
                        BuildingType t = rowTypes.get(i);
                        if (frame.game().isBuildingUnlocked(t)) { frame.selectBuildType(t); close(); }
                        return;
                    }
            }
            @Override public void mouseMoved(MouseEvent e) {
                int h = -1;
                for (int i = 0; i < rowRects.size(); i++) if (rowRects.get(i).contains(e.getPoint())) h = i;
                if (h != hoverRow) { hoverRow = h; repaint(); }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override public boolean contains(int x, int y) { return isVisible(); }   // fängt den Schließ-Klick

    /** Öffnet das Menü an seiner festen Andock-Position für die angegebene Zone. */
    public void open(Zone zone) {
        visibleCats = new ArrayList<>();
        for (Cat c : CATS) {
            for (BuildingType t : c.types())
                if (t.zone() == zone) { visibleCats.add(c); break; }
        }
        if (visibleCats.isEmpty()) return;
        catIdx = Math.min(catIdx, visibleCats.size() - 1);
        // Im Tutorial direkt die Kategorie des geforderten Gebäudes aufschlagen (Eintrag leuchtet).
        BuildingType tut = frame.tutorialBuildTarget();
        if (tut != null && tut.zone() == zone) {
            for (int i = 0; i < visibleCats.size(); i++)
                for (BuildingType t : visibleCats.get(i).types()) if (t == tut) catIdx = i;
        }
        hoverRow = -1;
        setVisible(true);
        relayout();
        repaint();
    }

    public void close() { setVisible(false); frame.afterBuildMenuClosed(); }

    /**
     * Öffnet das Menü (an der festen Andock-Position) und stellt die Kategorie des angegebenen
     * Gebäudes scharf - für den Freischalt-Wegweiser ("Zeig mir!"): der Eintrag leuchtet auf.
     */
    public void openForBuilding(BuildingType target) {
        Zone zone = frame.currentZone();
        visibleCats = new ArrayList<>();
        int wantIdx = 0;
        for (Cat c : CATS) {
            boolean has = false;
            for (BuildingType t : c.types()) if (t.zone() == zone) { has = true; break; }
            if (!has) continue;
            for (BuildingType t : c.types()) if (t == target) wantIdx = visibleCats.size();
            visibleCats.add(c);
        }
        if (visibleCats.isEmpty()) return;
        catIdx = Math.min(wantIdx, visibleCats.size() - 1);
        hoverRow = -1;
        setVisible(true);
        relayout();
        repaint();
    }

    /** Puls-Phase für ein aufleuchtendes Gebäude (Freischalt-Wegweiser / Tutorial). */
    private double glowPhase = 0;
    public void tickGlow() {
        if (!isVisible()) return;
        if (frame.highlightBuild() == null && frame.tutorialBuildTarget() == null) return;
        glowPhase += 0.18;
        repaint();
    }

    /** Gebäude der aktiven Kategorie in der aktuellen Zone. */
    private List<BuildingType> currentRows() {
        List<BuildingType> out = new ArrayList<>();
        if (visibleCats.isEmpty()) return out;
        Zone zone = frame.currentZone();
        for (BuildingType t : visibleCats.get(catIdx).types())
            if (t.zone() == zone) out.add(t);
        return out;
    }

    private void relayout() {
        List<BuildingType> rows = currentRows();
        int h = HEAD_H + TAB_H + 8 + rows.size() * ROW_H + 10;
        // Feste Andock-Position neben der Seitenleiste (unabhängig vom Mausklick),
        // nur nach unten begrenzt, damit hohe Kategorien nicht aus dem Fenster laufen.
        Point a = frame.buildMenuAnchor();
        int x = Math.min(Math.max(8, a.x), Math.max(8, getWidth() - MENU_W - 8));
        int y = Math.min(Math.max(8, a.y), Math.max(8, getHeight() - h - 8));
        card.setBounds(x, y, MENU_W, h);
    }

    @Override protected void paintComponent(Graphics g0) {
        if (visibleCats.isEmpty()) return;
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        relayout();

        g.setColor(Palette.PANEL);
        g.fillRoundRect(card.x, card.y, card.width, card.height, 12, 12);
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(card.x, card.y, card.width, card.height, 12, 12);

        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.TEXT);
        g.drawString("Bauen: " + frame.currentZone().displayName, card.x + 14, card.y + 22);

        // Kategorie-Reiter (nur mit Inhalt in dieser Zone)
        tabRects.clear();
        int tx = card.x + 10, ty = card.y + HEAD_H;
        g.setFont(Palette.FONT_TINY);
        FontMetrics tfm = g.getFontMetrics();
        for (int i = 0; i < visibleCats.size(); i++) {
            String name = visibleCats.get(i).name();
            int tw = tfm.stringWidth(name) + 16;
            if (tx + tw > card.x + card.width - 10) { tx = card.x + 10; ty += TAB_H - 6; }
            Rectangle r = new Rectangle(tx, ty, tw, TAB_H - 8);
            tabRects.add(r);
            g.setColor(i == catIdx ? new Color(0, 90, 84) : Palette.PANEL_LIGHT);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
            if (i == catIdx) { g.setColor(Palette.ACCENT); g.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8); }
            g.setColor(i == catIdx ? Palette.TEXT : Palette.TEXT_DIM);
            g.drawString(name, r.x + 8, r.y + r.height / 2 + tfm.getAscent() / 2 - 1);
            tx += tw + 6;
        }
        int listY = ty + TAB_H;

        // Gebäude-Zeilen (Optik wie im Seitenmenü)
        rowRects.clear(); rowTypes.clear();
        double money = frame.game().getMoney();
        for (BuildingType t : currentRows()) {
            Rectangle r = new Rectangle(card.x + 8, listY, card.width - 16, ROW_H - 6);
            rowRects.add(r); rowTypes.add(t);
            boolean unlocked = frame.game().isBuildingUnlocked(t);
            boolean affordable = money >= t.cost;
            boolean hover = hoverRow == rowRects.size() - 1;
            g.setColor(hover && unlocked ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            // Freischalt-Wegweiser / Tutorial: das gemeinte Gebäude leuchtet pulsierend auf.
            if (t == frame.highlightBuild() || t == frame.tutorialBuildTarget()) {
                float p = (float) (0.5 + 0.5 * Math.sin(glowPhase));
                g.setColor(new Color(0, 199, 183, (int) (16 + 26 * p)));
                g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
                g.setColor(new Color(0, 199, 183, (int) (110 + 120 * p)));
                g.setStroke(new BasicStroke(3f));
                g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            }
            g.setColor(Icons.darker(t.color, unlocked ? 0.85 : 0.5));
            g.fillRoundRect(r.x + 6, r.y + 6, 36, 36, 8, 8);
            Icons.building(g, t.icon, r.x + 24, r.y + 23, 26);
            g.setFont(Palette.FONT_BOLD);
            g.setColor(unlocked ? (affordable ? Palette.TEXT : Palette.TEXT_DIM) : Palette.TEXT_DIM);
            g.drawString(TextUtil.clip(g.getFontMetrics(), t.displayName, r.width - 52 - 64), r.x + 50, r.y + 20);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
            String sub = tagline(t);
            if (!unlocked) {
                String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(t.unlockFlag(), frame.questSystem());
                sub = hint != null ? hint : "noch gesperrt";
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), sub, r.width - 52 - 56), r.x + 50, r.y + 37);
            if (unlocked) {
                g.setFont(Palette.FONT_BOLD);
                String cost = String.format("%,d", t.cost);
                int cw = g.getFontMetrics().stringWidth(cost);
                g.setColor(affordable ? Palette.MONEY : Palette.BAD);
                g.drawString(cost, r.x + r.width - cw - 10, r.y + 34);
            } else {
                Icons.padlock(g, r.x + r.width - 18, r.y + 26, 16, Palette.TEXT_DIM);
            }
            listY += ROW_H;
        }
        g.dispose();
    }

    /** Kurzbeschreibung eines Gebäudes für die Menü-Zeile. */
    static String tagline(BuildingType t) {
        return switch (t) {
            case OLD_GENERATOR -> "+12 Strom, laut";
            case RAIN_BARREL   -> "+4 Wasser";
            case ALGAE_BUCKET  -> "+2,5 Futter";
            case GARAGE_TANK   -> "+1,2 Shrimp/Tag";
            case CAMPER        -> "+3 Arbeiter";
            case YARD_SALE     -> "verkauft 3 Standard/Tag";
            case POWER_PLANT  -> "+45 Strom";
            case SOLAR_ROOF   -> "+28 Strom, sauber";
            case WATER_PLANT  -> "+14 Wasser, -8 Strom";
            case WATER_HUB    -> "+40 Wasser";
            case ALGAE_FARM   -> "+9 Futter, -6 Strom";
            case SHRIMP_TANK  -> "+5 Shrimp/Tag";
            case HOUSING      -> "+6 Arbeiter";
            case SALES_OFFICE -> "verkauft Standard/Bio";
            case LAB          -> "+12% Preis, Forschung";
            case GENLAB       -> "Designer-Shrimps";
            case RESTAURANT   -> "Premium-Verkauf, +Ruf";
            case EXPORT_DOCK  -> "Export höherer Tiers";
            case MILITARY_DEPOT -> "kauft Protein/Kampf";
            case BLACK_MARKET -> "Top-Tiers, -Ruf";
            case VISITOR_CENTER -> "viel Reputation";
            case ZEN_GARDEN   -> "+Reputation, Deko";
            case SHELL_PRESS         -> "+6 Schalen/Tag";
            case SHRIMPBOOST_FACTORY -> "Shrimp+Schalen zu Boost";
            case BOOST_STAND         -> "verkauft Boost ~90/Dose";
            case ROBOT_WORKS         -> "Roboter = je +2 Arbeiter";
            case KRILL_BARRACKS      -> "+Armee-Stärke";
            case STORAGE_SHED        -> "+Lagerkapazität";
            case WAREHOUSE           -> "viel Lagerkapazität";
            default           -> "";
        };
    }
}
