package com.shrimptopia.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Vollständig selbst gezeichnete Buttons im ShrimpTopia-Theme.
 * Umgeht das native Look-and-Feel (das Farben ignoriert und Texte beschneidet).
 */
public final class ThemeButton {

    private ThemeButton() {}

    /** Normaler Klick-Button. */
    public static class FlatButton extends JButton {
        Color base = Palette.PANEL_LIGHT, accent = Palette.ACCENT;
        public FlatButton(String text) { super(text); setup(this); }
        @Override public Dimension getPreferredSize() { return pref(this); }
        @Override protected void paintComponent(Graphics g) { render((Graphics2D) g, this, base, accent, false); }
    }

    /** Umschaltbarer Button (z.B. Pause, Geschwindigkeit, Abriss). */
    public static class FlatToggle extends JToggleButton {
        Color base = Palette.PANEL_LIGHT, accent = Palette.ACCENT;
        public FlatToggle(String text) { super(text); setup(this); }
        public FlatToggle accent(Color c) { this.accent = c; return this; }
        public FlatToggle base(Color c)   { this.base = c; return this; }
        @Override public Dimension getPreferredSize() { return pref(this); }
        @Override protected void paintComponent(Graphics g) { render((Graphics2D) g, this, base, accent, isSelected()); }
    }

    private static void setup(AbstractButton b) {
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setRolloverEnabled(true);
        b.setFont(Palette.FONT_BOLD);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static Dimension pref(AbstractButton b) {
        FontMetrics fm = b.getFontMetrics(b.getFont());
        return new Dimension(fm.stringWidth(b.getText()) + 28, 34);
    }

    private static void render(Graphics2D g0, AbstractButton b, Color base, Color accent, boolean selected) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ButtonModel m = b.getModel();
        int w = b.getWidth(), h = b.getHeight();

        Color bg = selected ? accent
                 : m.isPressed() ? Icons.darker(base, 0.8)
                 : m.isRollover() ? Palette.PANEL_HOVER : base;
        g.setColor(bg);
        g.fillRoundRect(0, 1, w - 1, h - 2, 10, 10);
        if (selected) {
            g.setColor(Icons.brighter(accent, 1.15));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(0, 1, w - 2, h - 3, 10, 10);
        }

        g.setColor(selected ? Palette.BG_DARK : Palette.TEXT);
        FontMetrics fm = g.getFontMetrics(b.getFont());
        g.setFont(b.getFont());
        String text = TextUtil.clip(fm, b.getText(), w - 12);
        int tw = fm.stringWidth(text);
        g.drawString(text, Math.max(6, (w - tw) / 2), (h + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }
}
