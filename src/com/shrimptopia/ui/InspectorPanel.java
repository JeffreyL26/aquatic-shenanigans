package com.shrimptopia.ui;

import com.shrimptopia.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Rechter Inspektor: zeigt das gewählte Gebäude mit Live-Werten, Modi und Upgrades. */
public class InspectorPanel extends JPanel {

    private final GameFrame frame;
    private Building building;
    private final JPanel content = new JPanel();

    public InspectorPanel(GameFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(296, 100));
        setBackground(Palette.PANEL);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Palette.BG_DARK));
        setLayout(new BorderLayout());
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JScrollPane sc = new JScrollPane(content,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        sc.getViewport().setBackground(Palette.PANEL);
        ThemeScrollBar.apply(sc);
        add(sc, BorderLayout.CENTER);
        rebuild();
    }

    public void setBuilding(Building b) { this.building = b; rebuild(); }
    public Building getBuilding() { return building; }
    public void refresh() { repaintAll(content); }

    private void repaintAll(Component c) {
        c.repaint();
        if (c instanceof Container cc) for (Component k : cc.getComponents()) repaintAll(k);
    }

    private void rebuild() {
        content.removeAll();
        if (building == null) {
            content.add(hint("Klick ein Gebäude an, um Details, Modi und Upgrades zu sehen."));
        } else {
            content.add(new Header());
            BuildingType next = building.type.upgradesTo();
            if (next != null) {
                content.add(section("UPGRADE (GLEICHE STELLE)"));
                boolean nextUnlocked = frame.game().isBuildingUnlocked(next);
                double upCost = Math.max(0, next.cost - building.type.cost * 0.5);
                ThemeButton.FlatButton up = new ThemeButton.FlatButton(
                    nextUnlocked ? "Upgrade: " + next.displayName + " (-" + Math.round(upCost) + ")"
                                 : next.displayName + " (gesperrt)");
                if (nextUnlocked) up.base = new Color(0, 90, 84);
                up.addActionListener(e -> {
                    Building nb = frame.game().upgradeBuilding(building);
                    if (nb != null) frame.selectBuilding(nb);
                    frame.refreshAll();
                });
                content.add(wrapFull(up));
                if (!nextUnlocked) {
                    String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(next.unlockFlag(), frame.questSystem());
                    content.add(hint("Freischaltung: " + (hint != null ? hint : "durch Spielfortschritt.")));
                }
            }
            List<Mode> modes = BuildingCatalog.modes(building.type);
            if (!modes.isEmpty()) {
                content.add(section("BETRIEBS-MODUS"));
                for (int i = 0; i < modes.size(); i++) content.add(new ModeRow(i, modes.get(i)));
            }
            List<Upgrade> ups = BuildingCatalog.upgrades(building.type);
            if (!ups.isEmpty()) {
                content.add(section("UPGRADES"));
                for (Upgrade u : ups) content.add(new UpgradeRow(u));
            }
            if (building.type != BuildingType.HEADQUARTERS) {
                content.add(Box.createVerticalStrut(8));
                ThemeButton.FlatButton demo = new ThemeButton.FlatButton("Abreißen (50% zurück)");
                demo.base = new Color(86, 44, 44);
                demo.setAlignmentX(LEFT_ALIGNMENT);
                demo.addActionListener(e -> frame.demolishSelected());
                content.add(wrapFull(demo));
            }
        }
        if (frame.questSystem().rivalActive()) content.add(new RivalRow());
        content.add(Box.createVerticalGlue());
        content.revalidate();
        content.repaint();
    }

    private JComponent hint(String text) {
        JLabel l = new JLabel("<html><body style='width:224px'>" + text + "</body></html>");
        l.setForeground(Palette.TEXT_DIM);
        l.setFont(Palette.FONT_BODY);
        l.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JComponent section(String title) {
        JLabel l = new JLabel(title);
        l.setForeground(Palette.ACCENT);
        l.setFont(Palette.FONT_TINY);
        l.setBorder(BorderFactory.createEmptyBorder(10, 14, 4, 14));
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return l;
    }

    private JComponent wrapFull(JComponent inner) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(inner, BorderLayout.CENTER);
        return p;
    }

    // ---------------- Header mit Live-Werten ----------------
    private class Header extends JComponent {
        Header() { setAlignmentX(LEFT_ALIGNMENT); setMaximumSize(new Dimension(Integer.MAX_VALUE, 236)); setPreferredSize(new Dimension(290, 236)); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Building b = building; if (b == null) { g.dispose(); return; }
            BuildingType t = b.type; Stats s = b.lastStats;
            g.setColor(Palette.PANEL_LIGHT);
            g.fillRect(0, 0, getWidth(), getHeight());
            // Icon
            g.setColor(Icons.darker(t.color, 0.85));
            g.fillRoundRect(12, 12, 50, 50, 10, 10);
            Icons.building(g, t.icon, 37, 36, 38);
            // Name + Status
            int headW = getWidth() - 72 - 12;
            g.setFont(Palette.FONT_H2); g.setColor(Palette.TEXT);
            g.drawString(TextUtil.clip(g.getFontMetrics(), t.displayName, headW), 72, 30);
            Color dot = b.efficiency > 0.85 ? Palette.GOOD : b.efficiency > 0.35 ? Palette.WARN : Palette.BAD;
            g.setColor(dot);
            g.drawString(TextUtil.clip(g.getFontMetrics(), (int) Math.round(b.efficiency * 100) + "% "
                + (b.statusNote.isEmpty() ? "läuft" : b.statusNote), headW), 72, 50);

            int y = 84;
            g.setFont(Palette.FONT_SMALL);
            y = row(g, y, Palette.POWER, "Strom", flow(s.powerProduce, s.powerUse));
            y = row(g, y, Palette.WATER, "Wasser", flow(s.waterProduce, s.waterUse));
            y = row(g, y, Palette.FEED, "Futter", flow(s.feedProduce, s.feedUse));
            if (s.shrimpProduce > 0)
                y = row(g, y, Palette.SHRIMP, "Shrimps", String.format("+%.1f %s", s.shrimpProduce, s.tier.shortName));
            if (t.isMarket())
                y = row(g, y, Palette.MONEY, "Verkauf", String.format("bis %.0f/Tag", s.sellCap));
            if (s.workerProvide > 0 || s.workerNeed > 0)
                y = row(g, y, Palette.WORKERS, "Arbeiter", flow(s.workerProvide, s.workerNeed));
            if (s.wasteProduce > 0)
                y = row(g, y, Palette.WASTE, "Abfall", String.format("+%.0f/Tag", s.wasteProduce));
            if (s.wasteUse > 0)
                y = row(g, y, Palette.WASTE, "Entsorgt", String.format("-%.0f/Tag", s.wasteUse));
            y = row(g, y, Palette.TEXT_DIM, "Kosten", String.format("-%.1f/Tag", s.upkeep));
            double hq = frame.game().hqProximityBoost(b);
            if (hq > 0)
                y = row(g, y, Palette.ACCENT, "HQ-Bonus", "+" + Math.round(hq * 100) + "% Output (Nähe)");
            g.dispose();
        }
        private int row(Graphics2D g, int y, Color c, String label, String val) {
            g.setColor(c); g.fillOval(14, y - 9, 9, 9);
            g.setColor(Palette.TEXT_DIM); g.drawString(label, 30, y);
            g.setColor(Palette.TEXT); g.drawString(val, 150, y);
            return y + 18;
        }
        private String flow(double prod, double use) {
            if (prod > 0 && use > 0) return String.format("+%.0f / -%.0f", prod, use);
            if (prod > 0) return String.format("+%.0f", prod);
            if (use > 0) return String.format("-%.0f", use);
            return "-";
        }
    }

    // ---------------- Modus-Zeile ----------------
    private class ModeRow extends JComponent {
        final int index; final Mode m; boolean hover;
        ModeRow(int index, Mode m) {
            this.index = index; this.m = m;
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setPreferredSize(new Dimension(290, 50));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(m.requiresFlag, frame.questSystem());
            setToolTipText("<html><body style='width:230px'>" + m.desc
                + (hint != null ? "<br><br><b>Freischaltung:</b> " + hint : "") + "</body></html>");
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    if (locked()) return;
                    frame.game().setMode(building, index);
                    frame.refreshAll();
                }
            });
        }
        boolean locked() { return m.requiresFlag != null && !frame.game().isUnlocked(m.requiresFlag); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean sel = building != null && building.mode == index;
            boolean lock = locked();
            int w = getWidth();
            g.setColor(sel ? new Color(0, 90, 84) : hover && !lock ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
            g.fillRoundRect(12, 4, w - 24, 42, 9, 9);
            if (sel) { g.setColor(Palette.ACCENT); g.setStroke(new BasicStroke(2f)); g.drawRoundRect(13, 5, w - 26, 40, 9, 9); }
            g.setFont(Palette.FONT_BOLD); g.setColor(lock ? Palette.TEXT_DIM : Palette.TEXT);
            g.drawString(TextUtil.clip(g.getFontMetrics(), m.name + (lock ? "  (gesperrt)" : ""), w - 48), 24, 22);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
            String sub = m.desc;
            if (lock) {
                String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(m.requiresFlag, frame.questSystem());
                if (hint != null) sub = hint;
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), sub, w - 48), 24, 38);
            g.dispose();
        }
    }

    // ---------------- Upgrade-Zeile ----------------
    private class UpgradeRow extends JComponent {
        final Upgrade u; boolean hover;
        UpgradeRow(Upgrade u) {
            this.u = u;
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setPreferredSize(new Dimension(290, 50));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(u.requiresFlag, frame.questSystem());
            setToolTipText("<html><body style='width:230px'>" + u.desc
                + (hint != null ? "<br><br><b>Freischaltung:</b> " + hint : "") + "</body></html>");
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    if (owned() || locked()) return;
                    if (frame.game().buyUpgrade(building, u)) { rebuild(); frame.refreshAll(); }
                }
            });
        }
        boolean owned() { return building != null && building.upgrades.contains(u.id); }
        boolean locked() { return u.requiresFlag != null && !frame.game().isUnlocked(u.requiresFlag); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean own = owned(), lock = locked();
            boolean afford = frame.game().getMoney() >= u.cost;
            int w = getWidth();
            g.setColor(own ? new Color(30, 70, 50) : hover && !lock ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
            g.fillRoundRect(12, 4, w - 24, 42, 9, 9);
            g.setFont(Palette.FONT_BOLD); g.setColor(lock ? Palette.TEXT_DIM : Palette.TEXT);
            g.drawString(TextUtil.clip(g.getFontMetrics(), u.name, w - 100), 24, 22);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
            String sub = u.desc;
            if (lock) {
                String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(u.requiresFlag, frame.questSystem());
                if (hint != null) sub = hint;
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), sub, w - 90), 24, 38);
            g.setFont(Palette.FONT_BOLD);
            String tag = own ? "gekauft" : lock ? "gesperrt" : String.valueOf(u.cost);
            g.setColor(own ? Palette.GOOD : lock ? Palette.TEXT_DIM : afford ? Palette.MONEY : Palette.BAD);
            int tw = g.getFontMetrics().stringWidth(tag);
            g.drawString(tag, w - tw - 22, 22);
            g.dispose();
        }
    }

    // ---------------- Akwanov-Rivalität ----------------
    private class RivalRow extends JComponent {
        RivalRow() { setAlignmentX(LEFT_ALIGNMENT); setMaximumSize(new Dimension(Integer.MAX_VALUE, 86)); setPreferredSize(new Dimension(290, 86)); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            g.setColor(new Color(60, 40, 40));
            g.fillRoundRect(12, 8, w - 24, 70, 10, 10);
            Icons.portrait(g, com.shrimptopia.model.IconKind.PORTRAIT_DIPLOMAT, 40, 42, 50, new Color(90, 150, 130));
            g.setFont(Palette.FONT_BOLD); g.setColor(Palette.TEXT);
            g.drawString("Rivalität: Akwanov", 74, 30);
            int rv = frame.questSystem().getRival();
            g.setColor(new Color(40, 30, 30)); g.fillRoundRect(74, 40, w - 100, 12, 6, 6);
            g.setColor(rv > 65 ? Palette.BAD : rv > 30 ? Palette.WARN : Palette.GOOD);
            g.fillRoundRect(74, 40, (int) ((w - 100) * rv / 100.0), 12, 6, 6);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
            String phase = rv > 65 ? "Kalter Krill-Krieg" : rv > 30 ? "Handelskrieg" : "Höflich";
            int embW = 0;
            if (frame.game().getExportTariff() > 0) {
                String emb = "Embargo -" + (int) (frame.game().getExportTariff() * 100) + "%";
                embW = g.getFontMetrics().stringWidth(emb) + 8;
                g.setColor(Palette.BAD);
                g.drawString(emb, w - 22 - g.getFontMetrics().stringWidth(emb), 68);
                g.setColor(Palette.TEXT_DIM);
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), phase + "  (" + rv + "/100)", w - 22 - 74 - embW), 74, 68);
            g.dispose();
        }
    }
}
