package com.shrimptopia.ui;

import com.shrimptopia.model.Zone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Linke Seitenleiste (Tropico-Stil): fester BAUEN-Knopf für das angedockte Baumenü,
 * das Abriss-Werkzeug und darunter die Zonen-Umschalter (Karten-Wechsel).
 */
public class SideBar extends JPanel {

    public static final int WIDTH = 176;

    private enum Kind { BUILD, DEMOLISH, ZONE }

    private final GameFrame frame;
    private final SideButton buildBtn;
    private final SideButton demolishBtn;
    private final List<SideButton> zoneBtns = new ArrayList<>();
    private double glow = 0;

    public SideBar(GameFrame frame) {
        this.frame = frame;
        setLayout(null);
        setBackground(Palette.PANEL);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Palette.BG_DARK));
        setPreferredSize(new Dimension(WIDTH, 200));

        buildBtn = new SideButton(Kind.BUILD, "BAUEN", null);
        buildBtn.setToolTipText("Baumenü öffnen/schließen (Taste B - oder Rechtsklick auf die Karte)");
        add(buildBtn);

        demolishBtn = new SideButton(Kind.DEMOLISH, "Abriss", null);
        demolishBtn.setToolTipText("Abriss-Werkzeug: Gebäude anklicken (50% Erstattung). Rechtsklick beendet.");
        add(demolishBtn);

        for (Zone z : Zone.values()) {
            SideButton b = new SideButton(Kind.ZONE, z.displayName, z);
            zoneBtns.add(b);
            add(b);
        }
    }

    @Override public void doLayout() {
        int x = 10, w = getWidth() - 21;
        buildBtn.setBounds(x, 12, w, 44);
        demolishBtn.setBounds(x, 62, w, 30);
        int y = 126;
        for (SideButton b : zoneBtns) { b.setBounds(x, y, w, 32); y += 38; }
    }

    /** Vom Anim-Timer: lässt den BAUEN-Knopf während eines Tutorial-Bauschritts pulsieren. */
    public void tickGlow() {
        if (frame.tutorialBuildTarget() != null) { glow += 0.18; buildBtn.repaint(); }
    }

    public void refresh() {
        repaint();
        for (Component c : getComponents()) c.repaint();
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.TEXT_DIM);
        g.drawString("STANDORTE", 12, 118);
        g.dispose();
    }

    /** Ein Eintrag der Seitenleiste; zeichnet sich je nach Werkzeug-/Zonen-Zustand selbst. */
    private class SideButton extends JComponent {
        final Kind kind;
        final String label;
        final Zone zone;
        boolean hover = false;

        SideButton(Kind kind, String label, Zone zone) {
            this.kind = kind; this.label = label; this.zone = zone;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    switch (kind) {
                        case BUILD    -> frame.toggleBuildMenu();
                        case DEMOLISH -> frame.toggleDemolish();
                        case ZONE     -> { if (frame.game().isZoneUnlocked(zone)) frame.setZone(zone); }
                    }
                }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            boolean enabled = kind != Kind.ZONE || frame.game().isZoneUnlocked(zone);
            boolean selected = switch (kind) {
                case BUILD    -> frame.buildMenuVisible() || frame.tool() == GameFrame.Tool.PLACE;
                case DEMOLISH -> frame.tool() == GameFrame.Tool.DEMOLISH;
                case ZONE     -> frame.currentZone() == zone;
            };
            Color accent = kind == Kind.DEMOLISH ? Palette.BAD : Palette.ACCENT;

            g.setColor(selected ? new Color(0, 90, 84) : (hover && enabled ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT));
            if (selected && kind == Kind.DEMOLISH) g.setColor(new Color(96, 34, 34));
            g.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
            if (selected) {
                g.setColor(accent);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
            }
            // Tutorial-Wegweiser: der BAUEN-Knopf pulst, solange ein Bauschritt aussteht.
            if (kind == Kind.BUILD && frame.tutorialBuildTarget() != null && !frame.buildMenuVisible()) {
                float p = (float) (0.5 + 0.5 * Math.sin(glow));
                g.setColor(new Color(0, 199, 183, (int) (110 + 120 * p)));
                g.setStroke(new BasicStroke(3f));
                g.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
            }

            g.setFont(kind == Kind.BUILD ? Palette.FONT_H2 : Palette.FONT_BOLD);
            g.setColor(enabled ? Palette.TEXT : Palette.TEXT_DIM);
            String text = enabled ? label : label + "  (gesperrt)";
            FontMetrics fm = g.getFontMetrics();
            if (kind == Kind.BUILD) {
                // Hammer-Symbol vor dem Text
                int tw = fm.stringWidth(text);
                int tx = Math.max(32, (w - tw + 20) / 2);
                drawHammer(g, tx - 15, h / 2.0, 14, Palette.ACCENT);
                g.drawString(text, tx, (h + fm.getAscent() - fm.getDescent()) / 2);
            } else {
                g.drawString(TextUtil.clip(fm, text, w - 22), 12, (h + fm.getAscent() - fm.getDescent()) / 2);
            }
            g.dispose();
        }

        /** Kleiner Hammer (Stiel + Kopf), zentriert um (cx, cy). */
        private void drawHammer(Graphics2D g, double cx, double cy, double s, Color color) {
            Graphics2D h = (Graphics2D) g.create();
            h.setColor(color);
            h.translate(cx, cy);
            h.rotate(Math.toRadians(-40));
            h.fillRoundRect((int) (-s * 0.10), (int) (-s * 0.15), (int) (s * 0.22), (int) (s * 0.75), 3, 3);
            h.fillRoundRect((int) (-s * 0.42), (int) (-s * 0.48), (int) (s * 0.86), (int) (s * 0.34), 4, 4);
            h.dispose();
        }
    }
}
