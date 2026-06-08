package com.shrimptopia.ui;

import com.shrimptopia.model.Zone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/** Reiterleiste oberhalb der Karte zum Wechseln der Zonen. */
public class ZoneTabs extends JPanel {
    private final GameFrame frame;
    private final List<ZoneTab> tabs = new ArrayList<>();

    public ZoneTabs(GameFrame frame) {
        this.frame = frame;
        setBackground(Palette.BG_DARK);
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Palette.PANEL_LIGHT));
        for (Zone z : Zone.values()) {
            ZoneTab t = new ZoneTab(z);
            tabs.add(t);
            add(t);
        }
    }

    public void refresh() { for (ZoneTab t : tabs) t.repaint(); }

    private class ZoneTab extends JComponent {
        final Zone zone;
        boolean hover = false;

        ZoneTab(Zone zone) {
            this.zone = zone;
            setPreferredSize(new Dimension(150, 30));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    if (frame.game().isZoneUnlocked(zone)) frame.setZone(zone);
                }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean unlocked = frame.game().isZoneUnlocked(zone);
            boolean current = frame.currentZone() == zone;
            int w = getWidth(), h = getHeight();
            g.setColor(current ? new Color(0, 90, 84) : (hover && unlocked ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT));
            g.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
            if (current) {
                g.setColor(Palette.ACCENT);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
            }
            g.setFont(Palette.FONT_BOLD);
            g.setColor(unlocked ? Palette.TEXT : Palette.TEXT_DIM);
            String label = unlocked ? zone.displayName : zone.displayName + "  (gesperrt)";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, 12, (h + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }
}
