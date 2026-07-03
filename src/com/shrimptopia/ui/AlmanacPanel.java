package com.shrimptopia.ui;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.Edict;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.MarketingStream;
import com.shrimptopia.model.ResourceType;
import com.shrimptopia.model.ShrimpTier;
import com.shrimptopia.model.WorkerPolicy;
import com.shrimptopia.quest.Quest;
import com.shrimptopia.quest.QuestTree;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

/** Tropico-artiger Almanach: tiefe Kennzahlen in Tabs (Vermögen, Ressourcen, Tiers, Effekte, Statistik). */
public class AlmanacPanel extends JComponent {

    private static final String[] TABS = { "Vermögen", "Ressourcen", "Shrimp-Tiers", "Effekte", "Statistik", "Marketing", "HQ-Kommando" };
    /** Tab-Indizes für Aufrufer (GameFrame, Tutorial). */
    public static final int TAB_TIERS = 2, TAB_MARKETING = 5, TAB_HQ = 6;
    private final GameFrame frame;
    private int tab = 0;
    private final Rectangle card = new Rectangle();
    private final java.util.List<Rectangle> edictRects = new java.util.ArrayList<>();
    private final java.util.List<Edict> edictAt = new java.util.ArrayList<>();
    private final java.util.List<Rectangle> policyRects = new java.util.ArrayList<>();
    private final java.util.List<WorkerPolicy> policyAt = new java.util.ArrayList<>();
    private final java.util.List<Rectangle> mktRects = new java.util.ArrayList<>();
    private final java.util.List<MarketingStream> mktAt = new java.util.ArrayList<>();
    private final ThemeButton.FlatToggle[] tabBtn = new ThemeButton.FlatToggle[TABS.length];
    private final ThemeButton.FlatButton closeBtn;

    public AlmanacPanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(null);
        setVisible(false);
        for (int i = 0; i < TABS.length; i++) {
            final int t = i;
            ThemeButton.FlatToggle b = new ThemeButton.FlatToggle(TABS[i]);
            b.addActionListener(e -> { tab = t; refreshTabs(); repaint(); });
            add(b);
            tabBtn[i] = b;
        }
        closeBtn = new ThemeButton.FlatButton("Schließen");
        closeBtn.base = new Color(86, 44, 44);
        closeBtn.addActionListener(e -> close());
        add(closeBtn);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!card.contains(e.getPoint())) { close(); return; }
                if (tab == TAB_HQ) {   // HQ-Kommando: Arbeiter-Politik wählen / Edikte umschalten
                    for (int i = 0; i < policyRects.size(); i++)
                        if (policyRects.get(i).contains(e.getPoint())) {
                            frame.game().setWorkerPolicy(policyAt.get(i)); frame.refreshAll(); repaint(); return;
                        }
                    for (int i = 0; i < edictRects.size(); i++)
                        if (edictRects.get(i).contains(e.getPoint())) {
                            frame.game().toggleEdict(edictAt.get(i)); frame.refreshAll(); repaint(); return;
                        }
                }
                if (tab == TAB_MARKETING) {   // Marketing: Streams buchen/kündigen
                    for (int i = 0; i < mktRects.size(); i++)
                        if (mktRects.get(i).contains(e.getPoint())) {
                            frame.game().toggleStream(mktAt.get(i)); frame.refreshAll(); repaint(); return;
                        }
                }
            }
        });
    }

    public void open(int t) { tab = Math.max(0, Math.min(t, TABS.length - 1)); refreshTabs(); setVisible(true); revalidate(); doLayout(); repaint(); }
    public void close() { setVisible(false); frame.afterAlmanacClosed(); }
    /** Karten-Rechteck (für das Tutorial-Spotlight aufs Tier-Menü). */
    public Rectangle cardBounds() { return new Rectangle(card); }
    private void refreshTabs() { for (int i = 0; i < tabBtn.length; i++) tabBtn[i].setSelected(i == tab); }
    @Override public boolean contains(int x, int y) { return isVisible(); }   // modal: faengt alle Klicks

    @Override public void doLayout() {
        int W = getWidth(), H = getHeight();
        if (W == 0) return;
        int cw = Math.min(1000, W - 80), ch = Math.min(720, H - 60);
        card.setBounds((W - cw) / 2, (H - ch) / 2, cw, ch);
        int bx = card.x + 20, by = card.y + 50;
        // Reiter dürfen die Karte nicht überragen: bei Platzmangel gleichmäßig stauchen
        int avail = cw - 40 - (tabBtn.length - 1) * 6;
        int natural = 0;
        for (ThemeButton.FlatToggle b : tabBtn) natural += Math.max(120, b.getPreferredSize().width);
        double scale = natural > avail ? (double) avail / natural : 1.0;
        for (ThemeButton.FlatToggle b : tabBtn) {
            int w = (int) (Math.max(120, b.getPreferredSize().width) * scale);
            b.setBounds(bx, by, w, 32);
            bx += w + 6;
        }
        Dimension cd = closeBtn.getPreferredSize();
        closeBtn.setBounds(card.x + cw - cd.width - 16, card.y + 12, cd.width, 30);
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        aa(g);
        g.setColor(new Color(8, 12, 16, 175));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Palette.PANEL);
        g.fill(new RoundRectangle2D.Double(card.x, card.y, card.width, card.height, 16, 16));
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(card.x, card.y, card.width, card.height, 16, 16));
        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.TEXT);
        g.drawString("Almanach", card.x + 20, card.y + 34);

        int cx = card.x + 28, cy = card.y + 104, cw = card.width - 56, bottom = card.y + card.height - 20;
        g.setColor(Palette.BG_DARK);
        g.fillRect(card.x + 12, card.y + 90, card.width - 24, 1);
        GameState gs = frame.game();
        switch (tab) {
            case 0 -> drawWealth(g, gs, cx, cy, cw, bottom);
            case 1 -> drawResources(g, gs, cx, cy, cw);
            case 2 -> drawTiers(g, gs, cx, cy, cw, bottom);
            case 3 -> drawEffects(g, gs, cx, cy, cw, bottom);
            case 4 -> drawStats(g, gs, cx, cy, cw);
            case TAB_MARKETING -> drawMarketing(g, gs, cx, cy, cw, bottom);
            default -> drawKommando(g, gs, cx, cy, cw, bottom);
        }
        g.dispose();
    }

    private static void aa(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    private static String n(double v) { return String.format("%,d", Math.round(v)); }
    private static String signed(double v) { return (v >= 0 ? "+" : "") + String.format("%.1f", v); }

    private int row(Graphics2D g, int x, int y, int w, String label, String value, Color valColor) {
        g.setFont(Palette.FONT_BODY);
        g.setColor(Palette.TEXT_DIM);
        g.drawString(label, x, y);
        g.setColor(valColor);
        int vw = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + w - vw, y);
        return y + 24;
    }
    private void head(Graphics2D g, int x, int y, String t) {
        g.setFont(Palette.FONT_TINY); g.setColor(Palette.ACCENT); g.drawString(t.toUpperCase(), x, y);
    }

    // ---------------- Vermögen ----------------
    private void drawWealth(Graphics2D g, GameState gs, int x, int y, int w, int bottom) {
        g.setFont(Palette.FONT_H1); g.setColor(Palette.MONEY);
        g.drawString(n(gs.getMoney()) + " Geld", x, y);
        g.setFont(Palette.FONT_SMALL); g.setColor(gs.getMoneyNet() >= 0 ? Palette.GOOD : Palette.BAD);
        g.drawString(signed(gs.getMoneyNet()) + " / Tag (netto)", x, y + 20);
        int yy = y + 56;
        head(g, x, yy, "Einnahmen pro Tag (nach Markt)"); yy += 22;
        Map<BuildingType, Double> inc = gs.incomeByType();
        if (inc.isEmpty()) yy = row(g, x, yy, w, "Noch keine Verkaufseinnahmen", "0", Palette.TEXT_DIM);
        for (Map.Entry<BuildingType, Double> e : inc.entrySet())
            yy = row(g, x, yy, w, e.getKey().displayName, "+" + n(e.getValue()), Palette.GOOD);
        yy += 12;
        head(g, x, yy, "Ausgaben pro Tag"); yy += 22;
        yy = row(g, x, yy, w, "Betriebskosten (Upkeep)", "-" + n(gs.getUpkeepLast()), Palette.BAD);
        if (gs.getMarketingCostLast() > 0)
            yy = row(g, x, yy, w, "Marketing-Streams", "-" + n(gs.getMarketingCostLast()), Palette.BAD);
        yy += 12;
        g.setColor(Palette.BG_DARK); g.fillRect(x, yy - 8, w, 1);
        yy = row(g, x, yy + 8, w, "Netto pro Tag", signed(gs.getMoneyNet()), gs.getMoneyNet() >= 0 ? Palette.GOOD : Palette.BAD);
        int pct = (int) Math.min(100, gs.getMoney() / GameState.GOAL_MONEY * 100);
        row(g, x, bottom - 6, w, "Fortschritt zum Tycoon-Ziel (" + n(GameState.GOAL_MONEY) + ")", pct + "%", Palette.ACCENT2);
    }

    // ---------------- Ressourcen ----------------
    private void drawResources(Graphics2D g, GameState gs, int x, int y, int w) {
        head(g, x, y, "Bestand und Veränderung pro Tag"); y += 26;
        y = resRow(g, ResourceType.MONEY, "Vermögen", n(gs.getMoney()), signed(gs.getMoneyNet()), x, y, w, true);
        y = resRow(g, ResourceType.POWER, "Strom", gs.getPowerProduced() + " MW", "Verbrauch " + gs.getPowerUsed(), x, y, w, true);
        y = resRow(g, ResourceType.WATER, "Wasser", n(gs.getWater()) + " / " + n(gs.getCapWater()), signed(gs.getWaterNet()), x, y, w, true);
        y = resRow(g, ResourceType.FEED, "Futter", n(gs.getFeed()) + " / " + n(gs.getCapFeed()), signed(gs.getFeedNet()), x, y, w, true);
        y = resRow(g, ResourceType.SHRIMP, "Shrimps (gesamt)", n(gs.getShrimp()) + " / " + n(gs.getCapShrimp()), signed(gs.getShrimpNet()), x, y, w, true);
        y = resRow(g, ResourceType.WORKERS, "Arbeiter", gs.getWorkersAvail() + " frei", "Bedarf " + gs.getWorkersUsed(), x, y, w, true);
        y = resRow(g, ResourceType.REPUTATION, "Reputation", n(gs.getReputation()) + "/100", "", x, y, w, true);
        // Spätspiel-Ressourcen: erst sichtbar, wenn freigeschaltet - vorher nur "???"
        y = resRow(g, ResourceType.SHELLS, "Schalen", n(gs.getShells()) + " / " + n(gs.getCapShells()), signed(gs.getShellsNet()), x, y, w,
            gs.getShells() > 0);
        y = resRow(g, ResourceType.SHRIMPBOOST, "SHRIMPBOOST", n(gs.getEnergy()) + " / " + n(gs.getCapBoost()), signed(gs.getBoostNet()), x, y, w,
            gs.isUnlocked("build.shrimpboost"));
        y = resRow(g, ResourceType.ROBOTS, "Roboter", n(gs.getRobots()), signed(gs.getRobotsNet()), x, y, w,
            gs.isUnlocked("build.robotworks"));
        y = resRow(g, ResourceType.ARMY, "Armee-Stärke", n(gs.getArmy()), "", x, y, w,
            gs.getArmy() > 0 || gs.isUnlocked("build.barracks"));
    }
    private int resRow(Graphics2D g, ResourceType r, String label, String value, String sub, int x, int y, int w, boolean unlocked) {
        Icons.resource(g, r.icon, x + 12, y - 5, 22, unlocked ? r.color : Palette.PANEL_HOVER);
        g.setFont(Palette.FONT_BODY);
        g.setColor(unlocked ? Palette.TEXT : Palette.TEXT_DIM);
        g.drawString(unlocked ? label : "???", x + 32, y);
        g.drawString(unlocked ? value : "???", x + 300, y);
        g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
        g.drawString(unlocked ? sub : "noch nicht entdeckt", x + 460, y);
        return y + 28;
    }

    // ---------------- Tiers ----------------
    private void drawTiers(Graphics2D g, GameState gs, int x, int y, int w, int bottom) {
        head(g, x, y, "Shrimp-Qualitätsstufen - Wert, Märkte, Freischaltung"); y += 26;
        for (ShrimpTier t : ShrimpTier.values()) {
            boolean unlocked = gs.isTierUnlocked(t);
            g.setColor(t.color);
            g.fill(new RoundRectangle2D.Double(x, y - 13, 16, 16, 4, 4));
            g.setFont(Palette.FONT_BOLD);
            g.setColor(unlocked ? Palette.TEXT : Palette.TEXT_DIM);
            g.drawString(t.displayName + (unlocked ? "" : "  (gesperrt)"), x + 26, y);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.MONEY);
            g.drawString(n(t.baseValue) + " Geld/Stk.", x + 300, y);
            g.setColor(t.repPerUnit >= 0 ? Palette.GOOD : Palette.BAD);
            g.drawString("Rep " + signed(t.repPerUnit * 100) + "%", x + 420, y);
            g.setColor(Palette.TEXT_DIM);
            g.drawString("Lager " + n(gs.getShrimpStock(t)) + " | gesamt " + n(gs.getProducedByTier(t)), x + 530, y);
            // Maerkte
            StringBuilder mk = new StringBuilder();
            for (BuildingType bt : BuildingType.buildable())
                if (bt.isMarket()) for (ShrimpTier at : bt.acceptedTiers())
                    if (at == t) { if (mk.length() > 0) mk.append(", "); mk.append(bt.shortName()); }
            g.setColor(Palette.TEXT_DIM);
            g.drawString(TextUtil.clip(g.getFontMetrics(), "Verkauf bei: " + (mk.length() > 0 ? mk : "-"), w - 26), x + 26, y + 16);
            y += 42;
        }
    }

    // ---------------- Effekte ----------------
    private void drawEffects(Graphics2D g, GameState gs, int x, int y, int w, int bottom) {
        head(g, x, y, "Aktive farmweite Effekte (Upgrades, Politik, Embargo)"); y += 26;
        java.util.List<String> fx = gs.activeEffects();
        if (fx.isEmpty()) {
            g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT_DIM);
            g.drawString("Noch keine farmweiten Effekte aktiv. Kaufe [FARMWEIT]-Upgrades im Inspektor.", x, y);
            return;
        }
        g.setFont(Palette.FONT_BODY);
        for (String s : fx) {
            g.setColor(Palette.ACCENT); g.fillOval(x, y - 9, 8, 8);
            g.setColor(Palette.TEXT); g.drawString(TextUtil.clip(g.getFontMetrics(), s, w - 16), x + 16, y);
            y += 24;
            if (y > bottom) break;
        }
    }

    // ---------------- Statistik ----------------
    private void drawStats(Graphics2D g, GameState gs, int x, int y, int w) {
        head(g, x, y, "Statistik & Rekorde"); y += 26;
        y = row(g, x, y, w, "Tag", n(gs.getDay()), Palette.TEXT);
        y = row(g, x, y, w, "Gebäude gesamt", n(gs.buildingCount()), Palette.TEXT);
        y = row(g, x, y, w, "Shrimps insgesamt produziert", n(gs.getTotalShrimpProduced()), Palette.SHRIMP);
        y = row(g, x, y, w, "Shrimps insgesamt verkauft", n(gs.getTotalSold()), Palette.MONEY);
        y = row(g, x, y, w, "SHRIMPBOOST insgesamt produziert", n(gs.getTotalBoostProduced()), Palette.BOOST);
        y = row(g, x, y, w, "Roboter insgesamt gebaut", n(gs.getTotalRobotsProduced()), Palette.ROBOT);
        y = row(g, x, y, w, "Armee-Stärke", n(gs.getArmy()), Palette.ARMY);
        y = row(g, x, y, w, "Reputation", n(gs.getReputation()) + "/100", Palette.REP);
        y += 12;
        head(g, x, y, "Erreichte Spielenden (von 6)"); y += 22;
        java.util.List<Quest> ends = frame.questSystem().achievedEndings();
        if (ends.isEmpty()) {
            g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT_DIM);
            g.drawString("Noch keines. Wege zum Sieg: Reichtum, Industrie, Militär, Allianz, Ethik, Kultur.", x, y);
        } else for (Quest q : ends) {
            g.setColor(Palette.MONEY); g.fillOval(x, y - 9, 8, 8);
            g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT);
            g.drawString(q.title.replace("ENDE: ", ""), x + 16, y);
            y += 22;
        }
    }

    // ---------------- Marketing (Nachfrage & Streams) ----------------
    private void drawMarketing(Graphics2D g, GameState gs, int x, int y, int w, int bottom) {
        mktRects.clear(); mktAt.clear();
        double used = gs.getDemandUsedLast(), dem = gs.getDemandLast();
        g.setFont(Palette.FONT_H1); g.setColor(Palette.ACCENT2);
        g.drawString(Math.round(dem) + " Nachfrage / Tag", x, y);
        g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
        g.drawString(TextUtil.clip(g.getFontMetrics(), "Basis " + Math.round(GameState.BASE_DEMAND) + " (Nachbarschaft) + Streams, x"
            + String.format("%.2f", gs.demandRepFactor()) + " Reputations-Faktor   -   gestern verkauft: "
            + Math.round(used) + "   -   Marketing-Kosten: " + Math.round(gs.getMarketingCostLast()) + "/Tag", w), x, y + 18);
        g.drawString(TextUtil.clip(g.getFontMetrics(), "Verbraucher-Märkte (Klapptisch, Börse, Restaurant, Export) verkaufen nur, was nachgefragt wird."
            + " Militär & Schwarzmarkt laufen über Verträge.", w), x, y + 34);

        int yy = y + 54;
        head(g, x, yy, "Streams - Klick bucht oder kündigt (Kosten laufen pro Tag)"); yy += 18;
        int rowH = 44;
        for (MarketingStream s : MarketingStream.values()) {
            boolean on = gs.isStreamActive(s);
            boolean locked = s.requiresFlag != null && !gs.isUnlocked(s.requiresFlag);
            Rectangle rr = new Rectangle(x, yy, w, rowH - 4);
            mktRects.add(rr); mktAt.add(s);
            g.setColor(on ? new Color(0, 90, 84) : Palette.PANEL_LIGHT);
            g.fillRoundRect(rr.x, rr.y, rr.width, rr.height, 8, 8);
            if (on) { g.setColor(Palette.ACCENT); g.setStroke(new BasicStroke(1.5f)); g.drawRoundRect(rr.x, rr.y, rr.width, rr.height, 8, 8); }
            g.setFont(Palette.FONT_BOLD); g.setColor(locked ? Palette.TEXT_DIM : Palette.TEXT);
            g.drawString(s.displayName + (locked ? "  (gesperrt)" : ""), rr.x + 12, rr.y + 16);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
            String sub = s.desc;
            if (locked) {
                String hint = QuestTree.unlockHintFor(s.requiresFlag, frame.questSystem());
                if (hint != null) sub = "Freischaltung: " + hint;
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), sub, w - 250), rr.x + 12, rr.y + 32);
            g.setFont(Palette.FONT_BOLD);
            String eco = "+" + Math.round(s.demand) + " Nachfrage   -" + Math.round(s.costPerDay) + " Kosten/Tag";
            g.setColor(locked ? Palette.TEXT_DIM : Palette.MONEY);
            g.drawString(eco, rr.x + w - g.getFontMetrics().stringWidth(eco) - 60, rr.y + 16);
            g.setColor(on ? Palette.GOOD : Palette.TEXT_DIM);
            String tag = on ? "AN" : "AUS";
            g.drawString(tag, rr.x + w - g.getFontMetrics().stringWidth(tag) - 14, rr.y + 25);
            yy += rowH;
        }
        g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
        g.drawString("Tipp: Neue Streams schaltest du über Quests frei (Mira, Krusten & Krusten, ShrimpTube).", x, Math.min(yy + 18, bottom));
    }

    // ---------------- HQ-Kommando & Edikte ----------------
    private static final Font FONT_LAW_TITLE = new Font("Serif", Font.BOLD, 13);
    private static final Font FONT_LAW_SIGN  = new Font("Serif", Font.BOLD, 24);

    private void drawKommando(Graphics2D g, GameState gs, int x, int y, int w, int bottom) {
        edictRects.clear(); edictAt.clear();
        policyRects.clear(); policyAt.clear();
        g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), "Imperium: " + rank(gs.getMoney()) + "   -   Tag " + n(gs.getDay())
            + "   -   Gebäude " + n(gs.buildingCount()) + "   -   Armee-Stärke " + n(gs.getArmy()), w), x, y);
        int yy = y + 26;

        // Arbeiter-Politik (betriebsweite Linie) - eine Reihe wählbarer Kacheln
        head(g, x, yy, "Arbeiter-Politik - Klick wählt die betriebsweite Linie"); yy += 12;
        WorkerPolicy[] pols = WorkerPolicy.values();
        int pgap = 8;
        int pw = (w - (pols.length - 1) * pgap) / pols.length;
        int ph = 58;
        for (int i = 0; i < pols.length; i++) {
            Rectangle rr = new Rectangle(x + i * (pw + pgap), yy, pw, ph);
            policyRects.add(rr); policyAt.add(pols[i]);
            drawPolicyTile(g, gs, pols[i], rr);
        }
        yy += ph + 16;

        head(g, x, yy, "Edikte - Klick erlässt/hebt auf; Edikte derselben Gruppe schließen sich aus"); yy += 12;
        Edict[] all = Edict.values();
        int gap = 10;
        int cols = Math.max(2, Math.min(3, w / 300));
        int rows = (all.length + cols - 1) / cols;
        int tileW = (w - (cols - 1) * gap) / cols;
        int tileH = Math.max(66, (bottom - yy - (rows - 1) * gap) / rows);
        for (int i = 0; i < all.length; i++) {
            Rectangle rr = new Rectangle(x + (i % cols) * (tileW + gap), yy + (i / cols) * (tileH + gap), tileW, tileH);
            edictRects.add(rr); edictAt.add(all[i]);
            drawEdictTile(g, gs, all[i], rr);
        }
    }

    /** Eine Arbeiter-Politik-Kachel: aktuelle Linie hervorgehoben, gesperrte gedimmt. */
    private void drawPolicyTile(Graphics2D g, GameState gs, WorkerPolicy p, Rectangle r) {
        boolean cur = gs.getWorkerPolicy() == p;
        boolean locked = p.requiresFlag != null && !gs.isUnlocked(p.requiresFlag);
        g.setColor(cur ? new Color(0, 90, 84) : locked ? new Color(28, 35, 41) : Palette.PANEL_LIGHT);
        g.fill(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 10, 10));
        g.setColor(cur ? Palette.ACCENT : locked ? new Color(64, 76, 84) : new Color(110, 124, 132));
        g.setStroke(new BasicStroke(cur ? 2f : 1.3f));
        g.draw(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 10, 10));
        g.setFont(Palette.FONT_BOLD);
        g.setColor(locked ? Palette.TEXT_DIM : Palette.TEXT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), p.displayName, r.width - 20), r.x + 10, r.y + 19);
        String sub = p.desc;
        if (locked) {
            String hint = QuestTree.unlockHintFor(p.requiresFlag, frame.questSystem());
            sub = hint != null ? "Freischaltung: " + hint : "noch gesperrt";
        }
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        int ly = r.y + 34;
        for (String line : TextUtil.wrap(g.getFontMetrics(), sub, r.width - 20, 2)) {
            g.drawString(line, r.x + 10, ly);
            ly += 14;
        }
        if (cur) {
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.GOOD);
            String tag = "AKTIV";
            int sw = g.getFontMetrics().stringWidth(tag);
            g.drawString(tag, r.x + r.width - sw - 10, r.y + 19);
        }
    }

    /** Eine Edikt-Kachel im Urkunden-Look: Doppelrahmen, §-Zeichen, Serifentitel, Gruppen-Plakette, Stempel. */
    private void drawEdictTile(Graphics2D g, GameState gs, Edict e, Rectangle r) {
        boolean on = gs.isEdictActive(e);
        boolean locked = e.requiresFlag != null && !gs.isUnlocked(e.requiresFlag);
        // Papier + Doppelrahmen wie eine Urkunde
        g.setColor(on ? new Color(18, 72, 66) : locked ? new Color(28, 35, 41) : new Color(44, 52, 58));
        g.fill(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 10, 10));
        Color frameCol = on ? Palette.ACCENT : locked ? new Color(64, 76, 84) : new Color(110, 124, 132);
        g.setColor(frameCol);
        g.setStroke(new BasicStroke(on ? 2f : 1.3f));
        g.draw(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 10, 10));
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(frameCol.getRed(), frameCol.getGreen(), frameCol.getBlue(), 110));
        g.drawRect(r.x + 4, r.y + 4, r.width - 8, r.height - 8);   // innere Zierlinie

        // Paragraphen-Zeichen als Siegel links
        g.setFont(FONT_LAW_SIGN);
        g.setColor(locked ? Palette.TEXT_DIM : on ? Palette.ACCENT : Palette.ACCENT2);
        g.drawString("§", r.x + 12, r.y + 27);
        // Titel in Serifenschrift
        int tx = r.x + 34;
        g.setFont(FONT_LAW_TITLE);
        g.setColor(locked ? Palette.TEXT_DIM : Palette.TEXT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), e.name, r.x + r.width - 12 - tx), tx, r.y + 21);
        // Trennlinie unter dem Titel
        g.setColor(new Color(frameCol.getRed(), frameCol.getGreen(), frameCol.getBlue(), 130));
        g.drawLine(tx, r.y + 28, r.x + r.width - 12, r.y + 28);
        // Beschreibung (bzw. Freischalt-Hinweis) über bis zu zwei Zeilen
        String sub = e.desc;
        if (locked) {
            String hint = QuestTree.unlockHintFor(e.requiresFlag, frame.questSystem());
            sub = hint != null ? "Freischaltung: " + hint : "noch gesperrt";
        }
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        int ly = r.y + 42;
        for (String line : TextUtil.wrap(g.getFontMetrics(), sub, r.width - 24, 2)) {
            g.drawString(line, r.x + 12, ly);
            ly += 14;
        }
        // Gruppen-Plakette unten links
        String grp = (e.group != null ? e.group : "frei").toUpperCase();
        g.setFont(Palette.FONT_TINY);
        FontMetrics fm = g.getFontMetrics();
        int gw = fm.stringWidth(grp) + 12, gh = 15, gy = r.y + r.height - gh - 7;
        g.setColor(new Color(frameCol.getRed(), frameCol.getGreen(), frameCol.getBlue(), 60));
        g.fillRoundRect(r.x + 12, gy, gw, gh, 7, 7);
        g.setColor(locked ? Palette.TEXT_DIM : Palette.TEXT);
        g.drawString(grp, r.x + 18, gy + 11);
        // Stempel unten rechts (leicht gedreht wie ein Amtsstempel)
        String tag = on ? "ERLASSEN" : locked ? "GESPERRT" : "AUS";
        Color stampCol = on ? Palette.GOOD : locked ? Palette.BAD : Palette.TEXT_DIM;
        int sw = fm.stringWidth(tag) + 14, sh = 17;
        int sx = r.x + r.width - sw - 12, sy = r.y + r.height - sh - 6;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.rotate(Math.toRadians(-4), sx + sw / 2.0, sy + sh / 2.0);
        g2.setColor(stampCol);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(sx, sy, sw, sh, 5, 5);
        g2.setFont(Palette.FONT_TINY);
        g2.drawString(tag, sx + 7, sy + 12);
        g2.dispose();
    }
    private static String rank(double money) {
        if (money >= 1_500_000) return "Shrimp-Imperator";
        if (money >= 500_000) return "Garnelen-Magnat";
        if (money >= 150_000) return "Shrimp-Tycoon";
        if (money >= 50_000) return "Etablierter Züchter";
        if (money >= 10_000) return "Aufsteiger";
        return "Hinterhof-Start-up";
    }
}
