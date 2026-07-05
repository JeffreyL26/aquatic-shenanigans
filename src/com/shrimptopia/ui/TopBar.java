package com.shrimptopia.ui;

import com.shrimptopia.model.GameState;
import com.shrimptopia.model.ResourceType;
import javax.swing.*;
import java.awt.*;

/** Obere Leiste: Ressourcen-HUD links, Tag/Ziel und Steuerung rechts. */
public class TopBar extends JPanel {

    private final GameFrame frame;
    private final ResourceStrip strip;
    private final JLabel statusLabel;
    private ThemeButton.FlatToggle pauseBtn;
    private final java.util.Map<Integer, ThemeButton.FlatToggle> speedToggles = new java.util.HashMap<>();

    public TopBar(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Palette.PANEL);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Palette.BG_DARK));
        setPreferredSize(new Dimension(100, 80));

        strip = new ResourceStrip();
        add(strip, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 10));
        controls.setOpaque(false);

        statusLabel = new JLabel();
        statusLabel.setFont(Palette.FONT_BOLD);
        statusLabel.setForeground(Palette.TEXT);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setPreferredSize(new Dimension(150, 40));
        controls.add(statusLabel);

        pauseBtn = new ThemeButton.FlatToggle("Pause").accent(Palette.WARN);
        pauseBtn.addActionListener(e -> frame.setPaused(pauseBtn.isSelected()));
        controls.add(pauseBtn);

        ButtonGroup speed = new ButtonGroup();
        controls.add(speedButton(speed, "1x", 1, true));
        controls.add(speedButton(speed, "2x", 2, false));
        controls.add(speedButton(speed, "3x", 3, false));

        ThemeButton.FlatButton quests = new ThemeButton.FlatButton("Quest-Baum");
        quests.setToolTipText("Alle Handlungsstränge, Missionen & Freischaltungen (Taste Q)");
        quests.addActionListener(e -> frame.openQuestTree());
        controls.add(quests);

        ThemeButton.FlatButton alm = new ThemeButton.FlatButton("Almanach");
        alm.addActionListener(e -> frame.openAlmanac(0));
        controls.add(alm);

        ThemeButton.FlatButton neu = new ThemeButton.FlatButton("Neu");
        neu.addActionListener(e -> frame.restartGame());
        controls.add(neu);

        add(controls, BorderLayout.EAST);
    }

    public void setPausedVisual(boolean paused) {
        pauseBtn.setSelected(paused);
        pauseBtn.setText(paused ? "Weiter" : "Pause");
    }

    private ThemeButton.FlatToggle speedButton(ButtonGroup g, String text, int speed, boolean sel) {
        ThemeButton.FlatToggle b = new ThemeButton.FlatToggle(text);
        b.setSelected(sel);
        b.addActionListener(e -> frame.setSpeed(speed));
        g.add(b);
        speedToggles.put(speed, b);
        return b;
    }

    /** Setzt die Tempo-Anzeige (z.B. nach Neustart). */
    public void selectSpeed(int s) {
        ThemeButton.FlatToggle t = speedToggles.get(s);
        if (t != null) t.setSelected(true);
    }

    /** Vom GameFrame nach jedem Tick aufgerufen. */
    public void refresh() {
        GameState gs = frame.game();
        int pct = (int) Math.min(100, gs.getMoney() / GameState.GOAL_MONEY * 100);
        boolean won = frame.questSystem().hasEnding();
        String et = frame.questSystem().lastEndingTitle();
        String state = gs.isBankrupt() ? "  PLEITE" : (won ? "  GEWONNEN" : "");
        String line2 = won ? (et == null ? "Gewonnen!" : et.replace("ENDE: ", "")) : "Reichtum " + pct + "%";
        statusLabel.setText("<html>Tag " + gs.getDay() + state
            + "<br><font color='#8aa0b0'>" + line2 + "</font></html>");
        strip.repaint();
    }

    /** Vom Anim-Timer getickt: lässt kritische Ressourcen-Slots weiterblinken (auch bei Pause). */
    public void tickCritical() {
        GameState gs = frame.game();
        if (gs.powerShortDays() > 0 || gs.waterShortDays() > 0 || gs.feedShortDays() > 0) strip.repaint();
    }

    /** Zeichnet die Ressourcen-Anzeige. */
    private class ResourceStrip extends JComponent {
        private java.util.List<ResourceType> lastOrder = new java.util.ArrayList<>();
        ResourceStrip() {
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("");   // aktiviert das Tooltip-System für getToolTipText(MouseEvent)
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mousePressed(java.awt.event.MouseEvent e) {
                    if (lastOrder.isEmpty()) return;
                    int pad = 12; double slotW = (getWidth() - pad * 2.0) / lastOrder.size();
                    int idx = (int) ((e.getX() - pad) / slotW);
                    if (idx < 0 || idx >= lastOrder.size()) return;
                    ResourceType r = lastOrder.get(idx);
                    frame.openAlmanac(r == ResourceType.MONEY ? 0 : r == ResourceType.SHRIMP ? 2 : 1);
                }
            });
        }

        /** Bei schmalen Slots (Spätspiel, viele Ressourcen) fehlt der Platz für Label/Sub-Zeile - als Tooltip nachreichen. */
        @Override public String getToolTipText(java.awt.event.MouseEvent e) {
            if (lastOrder.isEmpty()) return null;
            int pad = 12; double slotW = (getWidth() - pad * 2.0) / lastOrder.size();
            if (slotW >= 100) return null;
            int idx = (int) ((e.getX() - pad) / slotW);
            if (idx < 0 || idx >= lastOrder.size()) return null;
            return lastOrder.get(idx).displayName;
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            GameState gs = frame.game();

            java.util.List<ResourceType> order = new java.util.ArrayList<>(java.util.List.of(
                ResourceType.MONEY, ResourceType.POWER, ResourceType.WATER,
                ResourceType.FEED, ResourceType.SHRIMP, ResourceType.WORKERS, ResourceType.REPUTATION));
            if (gs.getShells() > 0) order.add(ResourceType.SHELLS);
            if (gs.isUnlocked("build.shrimpboost")) order.add(ResourceType.SHRIMPBOOST);
            if (gs.isUnlocked("build.robotworks")) order.add(ResourceType.ROBOTS);
            if (gs.getArmy() > 0 || gs.isUnlocked("build.barracks")) order.add(ResourceType.ARMY);
            if (gs.getWaste() > 0 || gs.isUnlocked("build.gut_station")) order.add(ResourceType.WASTE);
            lastOrder = order;
            int n = order.size();
            int pad = 12;
            double slotW = (getWidth() - pad * 2.0) / n;
            for (int i = 0; i < n; i++) {
                double x = pad + i * slotW;
                drawSlot(g, gs, order.get(i), x, slotW);
                if (i > 0) {
                    g.setColor(Palette.BG_DARK);
                    g.fillRect((int) x, 16, 1, getHeight() - 32);
                }
            }
            g.dispose();
        }

        private void drawSlot(Graphics2D g, GameState gs, ResourceType r, double x, double w) {
            // Kritische Versorgung (Strom/Wasser/Futter): rot hinterlegen und hektisch blinken.
            // Je länger der Mangel ignoriert wird, desto schneller/greller das Blinken (Konsequenzen
            // eskalieren im Modell zusätzlich: mehr Sterben, ab ~1 Woche Ruf-Verlust).
            int critDays = switch (r) {
                case POWER -> gs.powerShortDays();
                case WATER -> gs.waterShortDays();
                case FEED  -> gs.feedShortDays();
                default -> 0;
            };
            if (critDays > 0) {
                double speed = critDays >= 6 ? 90.0 : 150.0;
                double ph = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / speed);
                int boost = Math.min(70, critDays * 7);
                g.setColor(new Color(255, 60, 60, (int) (30 + 70 * ph + boost)));
                g.fillRoundRect((int) x + 3, 8, (int) w - 6, getHeight() - 16, 10, 10);
                g.setColor(new Color(255, 90, 90, (int) (120 + 135 * ph)));
                g.setStroke(new BasicStroke(critDays >= 6 ? 2.5f : 1.8f));
                g.drawRoundRect((int) x + 3, 8, (int) w - 6, getHeight() - 16, 10, 10);
            }
            // Bei vielen gleichzeitig freigeschalteten Ressourcen (Spätspiel) wird jeder Slot schmal -
            // Icon verkleinern und näher an den Text rücken, statt Text unlesbar kurz zu clippen.
            boolean tight = w < 100;
            double iconSize = tight ? 20 : 30;
            double iconCx = x + (tight ? 16 : 24), iconCy = getHeight() / 2.0;
            Icons.resource(g, r.icon, iconCx, iconCy, iconSize, r.color);

            String value, sub;
            Color subColor = Palette.TEXT_DIM;
            switch (r) {
                case MONEY -> {
                    value = fmtInt(gs.getMoney());
                    double net = gs.getMoneyNet();
                    sub = signed(net) + "/Tag";
                    subColor = net >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case POWER -> {
                    value = gs.getPowerProduced() + " MW";
                    boolean ok = gs.getPowerProduced() >= gs.getPowerUsed();
                    sub = "Verbr. " + gs.getPowerUsed();
                    subColor = ok ? Palette.TEXT_DIM : Palette.BAD;
                }
                case WATER -> {
                    value = fmtInt(gs.getWater()) + "/" + fmtInt(gs.getCapWater());
                    boolean full = gs.getWater() >= gs.getCapWater() - 0.5;
                    sub = full ? "Lager voll!" : signed(gs.getWaterNet()) + "/Tag";
                    subColor = full ? Palette.WARN : gs.getWaterNet() >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case FEED -> {
                    value = fmtInt(gs.getFeed()) + "/" + fmtInt(gs.getCapFeed());
                    boolean full = gs.getFeed() >= gs.getCapFeed() - 0.5;
                    sub = full ? "Lager voll!" : signed(gs.getFeedNet()) + "/Tag";
                    subColor = full ? Palette.WARN : gs.getFeedNet() >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case SHRIMP -> {
                    value = fmtInt(gs.getShrimp()) + "/" + fmtInt(gs.getCapShrimp());
                    boolean full = gs.getShrimp() >= gs.getCapShrimp() - 0.5;
                    sub = full ? "Lager voll!" : signed(gs.getShrimpNet()) + "/Tag";
                    subColor = full ? Palette.WARN : gs.getShrimpNet() >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case WORKERS -> {
                    value = gs.getWorkersAvail() + " frei";
                    boolean ok = gs.getWorkersUsed() <= gs.getWorkersAvail();
                    sub = "Bedarf " + gs.getWorkersUsed();
                    subColor = ok ? Palette.TEXT_DIM : Palette.BAD;
                }
                case REPUTATION -> {
                    value = fmtInt(gs.getReputation()) + "/100";
                    double repMult = 0.6 + gs.getReputation() / 100.0 * 0.8;
                    sub = String.format("Preis x%.2f", repMult);
                }
                case SHELLS      -> { value = fmtInt(gs.getShells()) + "/" + fmtInt(gs.getCapShells()); sub = "Lager"; }
                case SHRIMPBOOST -> { value = fmtInt(gs.getEnergy()) + "/" + fmtInt(gs.getCapBoost()); sub = "Dosen"; }
                case ROBOTS      -> { value = fmtInt(gs.getRobots()); sub = "je +2 Arbeiter"; }
                case ARMY        -> { value = fmtInt(gs.getArmy()); sub = "Stärke"; }
                case WASTE       -> {
                    value = fmtInt(gs.getWaste()) + "/" + fmtInt(gs.getCapWaste());
                    boolean full = gs.getWaste() >= gs.getCapWaste() - 0.5;
                    sub = full ? "läuft über!" : "entsorgen";
                    subColor = full ? Palette.BAD : Palette.TEXT_DIM;
                }
                default -> { value = ""; sub = ""; }
            }

            double tx = x + (tight ? 30 : 44);
            // Texte auf die Slot-Breite begrenzen, sonst laufen sie in den Nachbar-Slot
            int maxW = Math.max(10, (int) (x + w - tx - 4));
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.TEXT_DIM);
            if (!tight) g.drawString(TextUtil.clip(g.getFontMetrics(), r.hudLabel, maxW), (float) tx, (float) (iconCy - 14));
            g.setFont(Palette.FONT_H2);
            g.setColor(Palette.TEXT);
            g.drawString(TextUtil.clip(g.getFontMetrics(), value, maxW), (float) tx, (float) (iconCy + 3));
            if (!tight) {
                g.setFont(Palette.FONT_SMALL);
                g.setColor(subColor);
                g.drawString(TextUtil.clip(g.getFontMetrics(), sub, maxW), (float) tx, (float) (iconCy + 18));
            }
        }
    }

    static String fmtInt(double v) {
        return String.format("%,d", Math.round(v));
    }

    static String signed(double v) {
        return (v >= 0 ? "+" : "") + String.format("%.1f", v);
    }
}
