package com.shrimptopia.ui;

import com.shrimptopia.model.Zone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Linke Seitenleiste (Tropico-Stil): der große, hervorgehobene BAUEN-Knopf als
 * Haupt-Aktion, das Abriss-Werkzeug und darunter die Zonen-Umschalter (Karten-Wechsel).
 */
public class SideBar extends JPanel {

    /** Breit genug, dass die langen Zonen-Namen ("Logistik & Export") voll hineinpassen. */
    public static final int WIDTH = 216;

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

    private static final int PAD = 12;
    private static final int BUILD_H = 58, DEMO_H = 34, ZONE_H = 38;

    @Override public void doLayout() {
        int x = PAD, w = getWidth() - PAD - 13;
        buildBtn.setBounds(x, 14, w, BUILD_H);
        demolishBtn.setBounds(x, 14 + BUILD_H + 10, w, DEMO_H);
        int y = 14 + BUILD_H + 10 + DEMO_H + 34;   // Platz für die "STANDORTE"-Überschrift
        for (SideButton b : zoneBtns) { b.setBounds(x, y, w, ZONE_H); y += ZONE_H + 6; }
    }

    /** Y-Koordinate der "STANDORTE"-Überschrift (zwischen Abriss und den Zonen-Knöpfen). */
    private int locationsLabelY() { return 14 + BUILD_H + 10 + DEMO_H + 24; }

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
        g.drawString("STANDORTE", PAD + 2, locationsLabelY());
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
            if (kind == Kind.BUILD) { paintBuild(g); g.dispose(); return; }

            int w = getWidth(), h = getHeight();
            boolean enabled = kind != Kind.ZONE || frame.game().isZoneUnlocked(zone);
            boolean selected = switch (kind) {
                case DEMOLISH -> frame.tool() == GameFrame.Tool.DEMOLISH;
                case ZONE     -> frame.currentZone() == zone;
                default       -> false;
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

            g.setFont(Palette.FONT_BOLD);
            g.setColor(enabled ? Palette.TEXT : Palette.TEXT_DIM);
            String text = enabled ? label : label + "  (gesperrt)";
            FontMetrics fm = g.getFontMetrics();
            if (kind == Kind.DEMOLISH) {
                // kleines X-Symbol vor dem Text
                int cx = 20, cy = h / 2;
                g.setColor(selected ? Palette.BAD : Palette.TEXT_DIM);
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, 0));
                g.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
                g.drawLine(cx + 5, cy - 5, cx - 5, cy + 5);
                g.setColor(Palette.TEXT);
                g.drawString(TextUtil.clip(fm, text, w - 42), 34, (h + fm.getAscent() - fm.getDescent()) / 2);
            } else {
                g.drawString(TextUtil.clip(fm, text, w - 24), 13, (h + fm.getAscent() - fm.getDescent()) / 2);
            }
            g.dispose();
        }

        /**
         * Der Haupt-Knopf: flacher, präziser "Primary Button" - satte Teal-Fläche,
         * 1px-Kante, Hotkey-Chip. Kein Verlauf, kein Glanz, kein Schatten-Blob.
         */
        private void paintBuild(Graphics2D g) {
            int w = getWidth(), h = getHeight();
            boolean open = frame.buildMenuVisible() || frame.tool() == GameFrame.Tool.PLACE;

            // Fläche: geschlossen = Akzent (Primary), offen = dunkel mit Akzent-Kante (aktiver Zustand)
            Color fill = open ? new Color(0, 62, 57)
                       : hover ? new Color(0, 176, 162)
                       : new Color(0, 160, 148);
            g.setColor(fill);
            g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
            g.setColor(open ? Palette.ACCENT : Icons.darker(fill, 0.72));
            g.setStroke(new BasicStroke(open ? 1.8f : 1.2f));
            g.drawRoundRect(0, 0, w - 2, h - 2, 10, 10);

            // Tutorial-Wegweiser: pulsierender Leuchtrahmen, solange ein Bauschritt aussteht.
            if (frame.tutorialBuildTarget() != null && !frame.buildMenuVisible()) {
                float p = (float) (0.5 + 0.5 * Math.sin(glow));
                g.setColor(new Color(255, 255, 255, (int) (50 + 130 * p)));
                g.setStroke(new BasicStroke(2.6f));
                g.drawRoundRect(1, 1, w - 4, h - 4, 10, 10);
            }

            Color ink = open ? Palette.ACCENT : new Color(8, 28, 26);
            String text = open ? "SCHLIESSEN" : "BAUEN";
            g.setFont(Palette.FONT_H2.deriveFont(15f));
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(text);
            // Hammer + Text als Block mittig; rechts dezenter Hotkey-Chip "B"
            int contentW = 24 + tw;
            int startX = Math.max(14, (w - contentW) / 2 - 6);
            int midY = h / 2;
            drawHammer(g, startX + 9, midY, 19, ink);
            g.setColor(ink);
            g.drawString(text, startX + 24, midY + fm.getAscent() / 2 - 2);
            // Hotkey-Chip
            g.setFont(Palette.FONT_TINY);
            FontMetrics kf = g.getFontMetrics();
            int ks = 17, kx = w - ks - 8, ky = midY - ks / 2;
            g.setColor(open ? new Color(0, 199, 183, 40) : new Color(0, 0, 0, 55));
            g.fillRoundRect(kx, ky, ks, ks, 5, 5);
            g.setColor(open ? Palette.ACCENT : new Color(8, 28, 26, 190));
            g.drawRoundRect(kx, ky, ks, ks, 5, 5);
            g.drawString("B", kx + (ks - kf.stringWidth("B")) / 2 + 1, ky + ks / 2 + kf.getAscent() / 2 - 1);
            g.dispose();
        }

        /** Kleiner Hammer (Stiel + Kopf), zentriert um (cx, cy). */
        private void drawHammer(Graphics2D g, double cx, double cy, double s, Color color) {
            Graphics2D h = (Graphics2D) g.create();
            h.setColor(color);
            h.translate(cx, cy);
            h.rotate(Math.toRadians(-40));
            h.fillRoundRect((int) (-s * 0.10), (int) (-s * 0.12), (int) (s * 0.22), (int) (s * 0.72), 3, 3);
            h.fillRoundRect((int) (-s * 0.44), (int) (-s * 0.46), (int) (s * 0.9), (int) (s * 0.34), 4, 4);
            h.dispose();
        }
    }
}
