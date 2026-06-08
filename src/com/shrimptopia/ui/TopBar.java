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
        String state = gs.isBankrupt() ? "  PLEITE" : (gs.isGoalReached() ? "  TYCOON!" : "");
        statusLabel.setText("<html>Tag " + gs.getDay() + state
            + "<br><font color='#8aa0b0'>Ziel " + pct + "%</font></html>");
        strip.repaint();
    }

    /** Zeichnet die Ressourcen-Anzeige. */
    private class ResourceStrip extends JComponent {
        ResourceStrip() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            GameState gs = frame.game();

            ResourceType[] order = {
                ResourceType.MONEY, ResourceType.POWER, ResourceType.WATER,
                ResourceType.FEED, ResourceType.SHRIMP, ResourceType.WORKERS, ResourceType.REPUTATION
            };
            int n = order.length;
            int pad = 12;
            double slotW = (getWidth() - pad * 2.0) / n;
            for (int i = 0; i < n; i++) {
                double x = pad + i * slotW;
                drawSlot(g, gs, order[i], x, slotW);
                if (i > 0) {
                    g.setColor(Palette.BG_DARK);
                    g.fillRect((int) x, 16, 1, getHeight() - 32);
                }
            }
            g.dispose();
        }

        private void drawSlot(Graphics2D g, GameState gs, ResourceType r, double x, double w) {
            double iconCx = x + 24, iconCy = getHeight() / 2.0;
            Icons.resource(g, r.icon, iconCx, iconCy, 30, r.color);

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
                    value = fmtInt(gs.getWater());
                    sub = signed(gs.getWaterNet()) + "/Tag";
                    subColor = gs.getWaterNet() >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case FEED -> {
                    value = fmtInt(gs.getFeed());
                    sub = signed(gs.getFeedNet()) + "/Tag";
                    subColor = gs.getFeedNet() >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case SHRIMP -> {
                    value = fmtInt(gs.getShrimp());
                    sub = signed(gs.getShrimpNet()) + "/Tag";
                    subColor = gs.getShrimpNet() >= 0 ? Palette.GOOD : Palette.BAD;
                }
                case WORKERS -> {
                    value = gs.getWorkersAvail() + " frei";
                    boolean ok = gs.getWorkersUsed() <= gs.getWorkersAvail();
                    sub = "Bedarf " + gs.getWorkersUsed();
                    subColor = ok ? Palette.TEXT_DIM : Palette.BAD;
                }
                default -> { // REPUTATION
                    value = fmtInt(gs.getReputation()) + "/100";
                    double repMult = 0.6 + gs.getReputation() / 100.0 * 0.8;
                    sub = String.format("Preis x%.2f", repMult);
                }
            }

            double tx = x + 44;
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.TEXT_DIM);
            g.drawString(r.displayName.toUpperCase(), (float) tx, (float) (iconCy - 14));
            g.setFont(Palette.FONT_H2);
            g.setColor(Palette.TEXT);
            g.drawString(value, (float) tx, (float) (iconCy + 3));
            g.setFont(Palette.FONT_SMALL);
            g.setColor(subColor);
            g.drawString(sub, (float) tx, (float) (iconCy + 18));
        }
    }

    static String fmtInt(double v) {
        return String.format("%,d", Math.round(v));
    }

    static String signed(double v) {
        return (v >= 0 ? "+" : "") + String.format("%.1f", v);
    }
}
