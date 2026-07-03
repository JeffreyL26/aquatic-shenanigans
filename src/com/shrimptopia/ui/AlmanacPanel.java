package com.shrimptopia.ui;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.Edict;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.MarketingStream;
import com.shrimptopia.model.ResourceType;
import com.shrimptopia.model.ShrimpTier;
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
                if (tab == TAB_HQ) {   // HQ-Kommando: Edikte umschalten
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
        y = resRow(g, ResourceType.MONEY, "Vermögen", n(gs.getMoney()), signed(gs.getMoneyNet()), x, y, w);
        y = resRow(g, ResourceType.POWER, "Strom", gs.getPowerProduced() + " MW", "Verbrauch " + gs.getPowerUsed(), x, y, w);
        y = resRow(g, ResourceType.WATER, "Wasser", n(gs.getWater()), signed(gs.getWaterNet()), x, y, w);
        y = resRow(g, ResourceType.FEED, "Futter", n(gs.getFeed()), signed(gs.getFeedNet()), x, y, w);
        y = resRow(g, ResourceType.SHRIMP, "Shrimps (gesamt)", n(gs.getShrimp()), signed(gs.getShrimpNet()), x, y, w);
        y = resRow(g, ResourceType.WORKERS, "Arbeiter", gs.getWorkersAvail() + " frei", "Bedarf " + gs.getWorkersUsed(), x, y, w);
        y = resRow(g, ResourceType.REPUTATION, "Reputation", n(gs.getReputation()) + "/100", "", x, y, w);
        y = resRow(g, ResourceType.SHELLS, "Schalen", n(gs.getShells()), signed(gs.getShellsNet()), x, y, w);
        y = resRow(g, ResourceType.SHRIMPBOOST, "SHRIMPBOOST", n(gs.getEnergy()), signed(gs.getBoostNet()), x, y, w);
        y = resRow(g, ResourceType.ROBOTS, "Roboter", n(gs.getRobots()), signed(gs.getRobotsNet()), x, y, w);
        y = resRow(g, ResourceType.ARMY, "Armee-Stärke", n(gs.getArmy()), "", x, y, w);
    }
    private int resRow(Graphics2D g, ResourceType r, String label, String value, String sub, int x, int y, int w) {
        Icons.resource(g, r.icon, x + 12, y - 5, 22, r.color);
        g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT);
        g.drawString(label, x + 32, y);
        g.setColor(Palette.TEXT); g.drawString(value, x + 300, y);
        g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
        g.drawString(sub, x + 460, y);
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
            String eco = "+" + Math.round(s.demand) + " Nachfrage   -" + Math.round(s.costPerDay) + "/Tag";
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
    private void drawKommando(Graphics2D g, GameState gs, int x, int y, int w, int bottom) {
        edictRects.clear(); edictAt.clear();
        g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), "Imperium: " + rank(gs.getMoney()) + "   -   Tag " + n(gs.getDay())
            + "   -   Gebäude " + n(gs.buildingCount()) + "   -   Armee-Stärke " + n(gs.getArmy()), w), x, y);
        int yy = y + 26;
        head(g, x, yy, "Edikte - Klick erlässt/hebt auf; Edikte derselben Gruppe schließen sich aus"); yy += 18;
        int rowH = Math.max(28, Math.min(38, (bottom - yy) / Edict.values().length));
        for (Edict e : Edict.values()) {
            boolean on = gs.isEdictActive(e);
            boolean locked = e.requiresFlag != null && !gs.isUnlocked(e.requiresFlag);
            Rectangle rr = new Rectangle(x, yy, w, rowH - 3);
            edictRects.add(rr); edictAt.add(e);
            g.setColor(on ? new Color(0, 90, 84) : Palette.PANEL_LIGHT);
            g.fillRoundRect(rr.x, rr.y, rr.width, rr.height, 8, 8);
            if (on) { g.setColor(Palette.ACCENT); g.setStroke(new BasicStroke(1.5f)); g.drawRoundRect(rr.x, rr.y, rr.width, rr.height, 8, 8); }
            g.setFont(Palette.FONT_BOLD); g.setColor(locked ? Palette.TEXT_DIM : Palette.TEXT);
            g.drawString(e.name + (e.group != null ? "   [" + e.group + "]" : "   [frei]"), rr.x + 12, rr.y + 13);
            g.setFont(Palette.FONT_TINY); g.setColor(Palette.TEXT_DIM);
            String sub = e.desc;
            if (locked) {
                String hint = QuestTree.unlockHintFor(e.requiresFlag, frame.questSystem());
                sub = hint != null ? "Freischaltung: " + hint : "noch gesperrt";
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), sub, w - 90), rr.x + 12, rr.y + rowH - 9);
            g.setFont(Palette.FONT_BOLD);
            g.setColor(on ? Palette.GOOD : Palette.TEXT_DIM);
            String tag = on ? "AN" : locked ? "GESPERRT" : "AUS";
            g.drawString(tag, rr.x + w - g.getFontMetrics().stringWidth(tag) - 14, rr.y + rowH / 2 + 4);
            yy += rowH;
        }
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
