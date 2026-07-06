package com.shrimptopia.ui;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.IconKind;
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
 *
 * Aufbau wie in Tropico: oben eine Reihe von Kategorie-Reitern mit ICONS, darunter ein
 * großzügiges Karten-Raster (Symbol oben, Name darunter) und ganz unten eine Detail-Zeile,
 * die das gerade überfahrene Gebäude erklärt. Klick auf eine Karte wählt es zum Platzieren.
 */
public class BuildMenuPanel extends JComponent {

    /** Eine Kategorie (Reiter) mit Icon, Signaturfarbe und ihren Gebäuden. */
    private record Cat(String name, IconKind icon, Color color, BuildingType... types) {}

    private static final Cat[] CATS = {
        new Cat("Strom",      IconKind.BOLT,   Palette.POWER,
                BuildingType.OLD_GENERATOR, BuildingType.SHRIMP_WHEEL, BuildingType.POWER_PLANT,
                BuildingType.SOLAR_ROOF, BuildingType.WIND_TURBINE, BuildingType.GEO_PLANT),
        new Cat("Versorgung", IconKind.DROP,   Palette.WATER,
                BuildingType.RAIN_BARREL, BuildingType.WATER_PLANT, BuildingType.WATER_HUB,
                BuildingType.ALGAE_BUCKET, BuildingType.ALGAE_FARM, BuildingType.PLANKTON_PRESS),
        new Cat("Zucht",      IconKind.SHRIMP, Palette.SHRIMP,
                BuildingType.KIDDIE_POOL, BuildingType.GARAGE_TANK, BuildingType.SHRIMP_TANK,
                BuildingType.HATCHERY, BuildingType.MEGA_TANK, BuildingType.REEF_DOME),
        new Cat("Verkauf",    IconKind.COIN,   Palette.MONEY,
                BuildingType.YARD_SALE, BuildingType.MARKET_STALL, BuildingType.FARM_SHOP,
                BuildingType.DELIVERY_SERVICE, BuildingType.SALES_OFFICE, BuildingType.RESTAURANT,
                BuildingType.EXPORT_DOCK, BuildingType.MILITARY_DEPOT, BuildingType.BLACK_MARKET,
                BuildingType.BOOST_STAND),
        new Cat("Personal & Lager", IconKind.PERSON, Palette.WORKERS,
                BuildingType.CAMPER, BuildingType.HOUSING, BuildingType.CANTEEN,
                BuildingType.STORAGE_SHED, BuildingType.WAREHOUSE, BuildingType.COLD_STORE),
        new Cat("Industrie",  IconKind.ROBOT,  Palette.ROBOT,
                BuildingType.LAB, BuildingType.GENLAB, BuildingType.SHELL_PRESS,
                BuildingType.SHRIMPBOOST_FACTORY, BuildingType.ROBOT_WORKS, BuildingType.KRILL_BARRACKS,
                BuildingType.GUT_STATION, BuildingType.BIOGAS_PLANT),
        new Cat("Ruf & Deko", IconKind.STAR,   Palette.REP,
                BuildingType.VISITOR_CENTER, BuildingType.ZEN_GARDEN, BuildingType.MASCOT_STATUE,
                BuildingType.FOUNTAIN, BuildingType.PETTING_POOL, BuildingType.BOYBAND_STAGE),
    };

    // Tropico-Raster: Symbol oben, Name darunter - mehr Übersicht auf einen Blick.
    private static final int PAD = 12, GAP = 10;
    private static final int GRID_COLS = 3, CARD_W = 150, CARD_H = 112;
    private static final int MENU_W = PAD * 2 + GRID_COLS * CARD_W + (GRID_COLS - 1) * GAP;
    private static final int HEAD_H = 30, TABBAR_H = 52, CATNAME_H = 22, FOOTER_H = 46;
    private static final int TAB_SIZE = 46, TAB_GAP = 7;

    private final GameFrame frame;
    private final Rectangle card = new Rectangle();
    private List<Cat> visibleCats = new ArrayList<>();
    private int catIdx = 0;
    private int hoverTab = -1, hoverRow = -1;
    private final List<Rectangle> tabRects = new ArrayList<>();
    private final List<Rectangle> rowRects = new ArrayList<>();
    private final List<BuildingType> rowTypes = new ArrayList<>();

    public BuildMenuPanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setVisible(false);
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!card.contains(e.getPoint())) { close(); return; }
                for (int i = 0; i < tabRects.size(); i++)
                    if (tabRects.get(i).contains(e.getPoint())) { catIdx = i; hoverRow = -1; relayout(); repaint(); return; }
                for (int i = 0; i < rowRects.size(); i++)
                    if (rowRects.get(i).contains(e.getPoint())) {
                        BuildingType t = rowTypes.get(i);
                        if (frame.game().isBuildingUnlocked(t)) { frame.selectBuildType(t); close(); }
                        return;
                    }
            }
            @Override public void mouseMoved(MouseEvent e) {
                int hr = -1, ht = -1;
                for (int i = 0; i < rowRects.size(); i++) if (rowRects.get(i).contains(e.getPoint())) hr = i;
                for (int i = 0; i < tabRects.size(); i++) if (tabRects.get(i).contains(e.getPoint())) ht = i;
                if (hr != hoverRow || ht != hoverTab) { hoverRow = hr; hoverTab = ht; repaint(); }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override public boolean contains(int x, int y) { return isVisible(); }   // fängt den Schließ-Klick

    /** Öffnet das Menü an seiner festen Andock-Position für die angegebene Zone. */
    public void open(Zone zone) {
        buildVisible(zone);
        if (visibleCats.isEmpty()) return;
        catIdx = Math.min(catIdx, visibleCats.size() - 1);
        // Im Tutorial direkt die Kategorie des geforderten Gebäudes aufschlagen (Eintrag leuchtet).
        BuildingType tut = frame.tutorialBuildTarget();
        if (tut != null && tut.zone() == zone) selectCatOf(tut);
        hoverRow = hoverTab = -1;
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
        buildVisible(frame.currentZone());
        if (visibleCats.isEmpty()) return;
        selectCatOf(target);
        hoverRow = hoverTab = -1;
        setVisible(true);
        relayout();
        repaint();
    }

    /** Sammelt die Kategorien mit Inhalt in dieser Zone. */
    private void buildVisible(Zone zone) {
        visibleCats = new ArrayList<>();
        for (Cat c : CATS)
            for (BuildingType t : c.types())
                if (t.zone() == zone) { visibleCats.add(c); break; }
    }

    /** Wählt die Kategorie, die das gegebene Gebäude enthält (falls sichtbar). */
    private void selectCatOf(BuildingType target) {
        for (int i = 0; i < visibleCats.size(); i++)
            for (BuildingType t : visibleCats.get(i).types()) if (t == target) { catIdx = i; return; }
        catIdx = Math.min(catIdx, visibleCats.size() - 1);
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

    private int gridRows() {
        int n = currentRows().size();
        return Math.max(1, (n + GRID_COLS - 1) / GRID_COLS);
    }

    private void relayout() {
        int gridH = gridRows() * CARD_H + (gridRows() - 1) * GAP;
        int h = HEAD_H + TABBAR_H + CATNAME_H + gridH + FOOTER_H + PAD;
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

        // Panel mit Schatten
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(card.x + 3, card.y + 5, card.width, card.height, 14, 14);
        g.setColor(Palette.PANEL);
        g.fillRoundRect(card.x, card.y, card.width, card.height, 14, 14);
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(card.x, card.y, card.width, card.height, 14, 14);

        // Kopfzeile
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.TEXT);
        g.drawString("Bauen", card.x + 14, card.y + 21);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        String zn = frame.currentZone().displayName;
        FontMetrics zfm = g.getFontMetrics();
        g.drawString(zn, card.x + card.width - zfm.stringWidth(zn) - 14, card.y + 21);
        g.setColor(new Color(255, 255, 255, 22));
        g.fillRect(card.x + 12, card.y + HEAD_H - 2, card.width - 24, 1);

        drawTabBar(g);

        // Kategorie-Name (Titel über dem Raster)
        Cat cat = visibleCats.get(catIdx);
        int catY = card.y + HEAD_H + TABBAR_H;
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.ACCENT2);
        g.drawString(cat.name(), card.x + 14, catY + 15);
        List<BuildingType> rows = currentRows();
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.TEXT_DIM);
        String cnt = rows.size() + " Gebäude";
        g.drawString(cnt, card.x + card.width - g.getFontMetrics().stringWidth(cnt) - 14, catY + 14);

        // Karten-Raster
        rowRects.clear(); rowTypes.clear();
        double money = frame.game().getMoney();
        int gridX0 = card.x + PAD, gridY0 = catY + CATNAME_H;
        for (int i = 0; i < rows.size(); i++) {
            int col = i % GRID_COLS, gr = i / GRID_COLS;
            int rx = gridX0 + col * (CARD_W + GAP);
            int ry = gridY0 + gr * (CARD_H + GAP);
            Rectangle r = new Rectangle(rx, ry, CARD_W, CARD_H);
            rowRects.add(r); rowTypes.add(rows.get(i));
            drawCard(g, r, rows.get(i), money, hoverRow == i);
        }

        drawFooter(g);
        g.dispose();
    }

    /** Die Icon-Reiterleiste am oberen Rand (eine Kachel je Kategorie). */
    private void drawTabBar(Graphics2D g) {
        tabRects.clear();
        int y = card.y + HEAD_H + (TABBAR_H - TAB_SIZE) / 2;
        int x = card.x + PAD;
        for (int i = 0; i < visibleCats.size(); i++) {
            Cat c = visibleCats.get(i);
            Rectangle r = new Rectangle(x, y, TAB_SIZE, TAB_SIZE);
            tabRects.add(r);
            boolean sel = i == catIdx;
            boolean hov = i == hoverTab;
            g.setColor(sel ? new Color(0, 90, 84) : hov ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            g.setColor(sel ? Palette.ACCENT : new Color(70, 84, 92));
            g.setStroke(new BasicStroke(sel ? 2f : 1.2f));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            Icons.resource(g, c.icon(), r.getCenterX(), r.getCenterY() - 3, 24, sel ? c.color() : Icons.darker(c.color(), 0.7));
            // aktiver Reiter: kleiner "Zeiger" nach unten
            if (sel) {
                g.setColor(Palette.ACCENT);
                int cx = (int) r.getCenterX();
                int[] xs = { cx - 6, cx + 6, cx };
                int[] ys = { r.y + r.height + 1, r.y + r.height + 1, r.y + r.height + 7 };
                g.fillPolygon(xs, ys, 3);
            }
            x += TAB_SIZE + TAB_GAP;
        }
    }

    /** Eine Gebäude-Karte: farbige Symbolkachel oben, Name darunter, Preis/Schloss unten. */
    private void drawCard(Graphics2D g, Rectangle r, BuildingType t, double money, boolean hover) {
        boolean unlocked = frame.game().isBuildingUnlocked(t);
        boolean affordable = money >= t.cost;
        boolean glow = t == frame.highlightBuild() || t == frame.tutorialBuildTarget();

        g.setColor(hover && unlocked ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g.setColor(hover && unlocked ? Palette.ACCENT : new Color(58, 70, 78));
        g.setStroke(new BasicStroke(hover && unlocked ? 1.8f : 1.1f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        if (glow) {
            float p = (float) (0.5 + 0.5 * Math.sin(glowPhase));
            g.setColor(new Color(0, 199, 183, (int) (110 + 120 * p)));
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        }

        // Symbolkachel (mittig oben)
        int isz = 56;
        int ix = r.x + (r.width - isz) / 2, iy = r.y + 10;
        g.setColor(Icons.darker(t.color, unlocked ? 0.9 : 0.5));
        g.fillRoundRect(ix, iy, isz, isz, 12, 12);
        g.setColor(unlocked ? Icons.brighter(t.color, 1.05) : Icons.darker(t.color, 0.6));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(ix, iy, isz, isz, 12, 12);
        Icons.building(g, t.icon, ix + isz / 2.0, iy + isz / 2.0, isz * 0.62);
        if (!unlocked) {
            g.setColor(new Color(20, 26, 31, 150));
            g.fillRoundRect(ix, iy, isz, isz, 12, 12);
            Icons.padlock(g, ix + isz / 2.0, iy + isz / 2.0, 22, Palette.TEXT);
        }

        // Name (zentriert, bis zu 2 Zeilen)
        g.setFont(Palette.FONT_SMALL);
        g.setColor(unlocked ? Palette.TEXT : Palette.TEXT_DIM);
        FontMetrics fm = g.getFontMetrics();
        List<String> lines = TextUtil.wrap(fm, t.displayName, r.width - 12, 2);
        int ny = r.y + isz + 24;
        for (String line : lines) {
            g.drawString(line, r.x + (r.width - fm.stringWidth(line)) / 2, ny);
            ny += 13;
        }

        // Preis (oder "gesperrt")
        g.setFont(Palette.FONT_BOLD);
        if (unlocked) {
            String cost = String.format("%,d", t.cost);
            g.setColor(affordable ? Palette.MONEY : Palette.BAD);
            g.drawString(cost, r.x + (r.width - g.getFontMetrics().stringWidth(cost)) / 2, r.y + r.height - 10);
        } else {
            g.setColor(Palette.TEXT_DIM);
            g.setFont(Palette.FONT_TINY);
            String lk = "gesperrt";
            g.drawString(lk, r.x + (r.width - g.getFontMetrics().stringWidth(lk)) / 2, r.y + r.height - 10);
        }
    }

    /** Fußzeile: Details zum überfahrenen Gebäude (Name, Kurzinfo bzw. Freischalt-Hinweis). */
    private void drawFooter(Graphics2D g) {
        int fy = card.y + card.height - FOOTER_H;
        g.setColor(new Color(255, 255, 255, 20));
        g.fillRect(card.x + 12, fy, card.width - 24, 1);

        BuildingType t = hoverRow >= 0 && hoverRow < rowTypes.size() ? rowTypes.get(hoverRow) : null;
        if (t == null) {
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            g.drawString(TextUtil.clip(g.getFontMetrics(),
                "Gebäude wählen, dann auf ein freies Feld setzen.", card.width - 28), card.x + 14, fy + 27);
            return;
        }
        boolean unlocked = frame.game().isBuildingUnlocked(t);
        g.setFont(Palette.FONT_BOLD);
        g.setColor(Palette.TEXT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), t.displayName, card.width - 28), card.x + 14, fy + 20);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        String sub;
        if (unlocked) {
            sub = tagline(t);
            if (sub.isEmpty()) sub = t.description;
        } else {
            String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(t.unlockFlag(), frame.questSystem());
            sub = hint != null ? "Freischaltung: " + hint : "Noch gesperrt.";
            g.setColor(Palette.ACCENT);
        }
        g.drawString(TextUtil.clip(g.getFontMetrics(), sub, card.width - 28), card.x + 14, fy + 37);
    }

    /** Kurzbeschreibung eines Gebäudes für die Detail-Zeile. */
    static String tagline(BuildingType t) {
        return switch (t) {
            case OLD_GENERATOR -> "+12 Strom, laut";
            case RAIN_BARREL   -> "+4 Wasser";
            case ALGAE_BUCKET  -> "+2,5 Futter";
            case GARAGE_TANK   -> "+1,2 Shrimp/Tag";
            case CAMPER        -> "+3 Arbeiter";
            case YARD_SALE     -> "verkauft 2 Standard/Tag";
            case MARKET_STALL  -> "verkauft 5/Tag (Std/Bio)";
            case FARM_SHOP     -> "verkauft 9/Tag, voller Preis";
            case DELIVERY_SERVICE -> "verkauft 15/Tag, auch Gourmet";
            case POWER_PLANT  -> "+45 Strom";
            case SOLAR_ROOF   -> "+28 Strom, sauber";
            case WATER_PLANT  -> "+14 Wasser, -8 Strom";
            case WATER_HUB    -> "+40 Wasser";
            case ALGAE_FARM   -> "+9 Futter, -6 Strom";
            case SHRIMP_TANK  -> "+5 Shrimp/Tag";
            case HOUSING      -> "+6 Arbeiter";
            case SALES_OFFICE -> "verkauft 24/Tag, +15% Preis";
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
            case GUT_STATION         -> "Gourmet-Voraussetzung, +Abfall";
            case BIOGAS_PLANT        -> "entsorgt Abfall zu Biogas";
            case STORAGE_SHED        -> "+Lagerkapazität";
            case WAREHOUSE           -> "viel Lagerkapazität";
            case SHRIMP_WHEEL        -> "+7 Strom, sportlich";
            case WIND_TURBINE        -> "+22 Strom, sauber";
            case GEO_PLANT           -> "+70 Strom, Grundlast";
            case PLANKTON_PRESS      -> "+14 Futter, industriell";
            case KIDDIE_POOL         -> "+2,2 Shrimp/Tag, billig";
            case HATCHERY            -> "+3,5 Shrimp/Tag, sparsam";
            case MEGA_TANK           -> "+11 Shrimp/Tag, hungrig";
            case REEF_DOME           -> "+3,2 GOURMET/Tag";
            case CANTEEN             -> "+4 Arbeiter, +Ruf";
            case COLD_STORE          -> "Lager für Shrimps & Futter";
            case MASCOT_STATUE       -> "+Reputation (Greg!)";
            case FOUNTAIN            -> "+Reputation, Deko";
            case PETTING_POOL        -> "+Reputation, Familien";
            case BOYBAND_STAGE       -> "Boygroup-Heimspielstätte, +Ruf";
            default           -> "";
        };
    }
}
