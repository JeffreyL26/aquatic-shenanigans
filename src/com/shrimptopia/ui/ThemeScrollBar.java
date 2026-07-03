package com.shrimptopia.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Dunkle, schlanke Scrollbar im ShrimpTopia-Theme: schmale Spur in Panelfarbe,
 * abgerundeter Daumen, der beim Überfahren aufhellt. Ersetzt die klobige
 * System-Scrollbar in Seitenleiste und Inspektor.
 */
public final class ThemeScrollBar extends BasicScrollBarUI {

    private ThemeScrollBar() {}

    /** Wendet das Theme auf beide Scrollbars einer JScrollPane an (inkl. dunkler Ecke). */
    public static void apply(JScrollPane sp) {
        style(sp.getVerticalScrollBar());
        style(sp.getHorizontalScrollBar());
        JPanel corner = new JPanel();
        corner.setBackground(Palette.PANEL);
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
    }

    private static void style(JScrollBar bar) {
        if (bar == null) return;
        bar.setUI(new ThemeScrollBar());
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(9, 9));
        bar.setUnitIncrement(16);
    }

    @Override protected void paintTrack(Graphics g0, JComponent c, Rectangle r) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Palette.PANEL);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.setColor(Palette.BG_DARK);
        boolean vertical = r.height >= r.width;
        if (vertical) g.fillRoundRect(r.x + r.width / 2 - 2, r.y + 2, 4, r.height - 4, 4, 4);
        else          g.fillRoundRect(r.x + 2, r.y + r.height / 2 - 2, r.width - 4, 4, 4, 4);
        g.dispose();
    }

    @Override protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(isDragging ? Palette.ACCENT
                 : isThumbRollover() ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
        boolean vertical = r.height >= r.width;
        if (vertical) g.fillRoundRect(r.x + 1, r.y + 1, r.width - 3, r.height - 2, 7, 7);
        else          g.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 3, 7, 7);
        g.dispose();
    }

    // Keine Pfeil-Buttons - nur Spur und Daumen.
    @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
    @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
    private static JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }
}
