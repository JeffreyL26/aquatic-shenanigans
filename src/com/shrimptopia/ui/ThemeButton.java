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
        boolean pressed = m.isPressed(), hover = m.isRollover();

        // Dezenter Schatten unterm Button, außer im gedrückten Zustand (wirkt dann "eingedrückt").
        if (!pressed) {
            g.setColor(new Color(0, 0, 0, 55));
            g.fillRoundRect(0, 3, w - 1, h - 2, 10, 10);
        }
        Color top, bottom;
        if (selected)      { top = Icons.brighter(accent, 1.12); bottom = Icons.darker(accent, 0.88); }
        else if (pressed)  { top = Icons.darker(base, 0.82); bottom = Icons.darker(base, 0.7); }
        else if (hover)    { top = Icons.brighter(Palette.PANEL_HOVER, 1.08); bottom = Palette.PANEL_HOVER; }
        else               { top = Icons.brighter(base, 1.06); bottom = Icons.darker(base, 0.92); }
        int yOff = pressed ? 2 : 1;
        Fx.vGradient(g, 0, yOff, w - 1, h - 2, 10, top, bottom);
        if (selected || hover) {
            g.setColor(selected ? Icons.brighter(accent, 1.2) : Fx.alpha(Palette.ACCENT, 90));
            g.setStroke(new BasicStroke(selected ? 1.5f : 1.1f));
            g.drawRoundRect(0, yOff, w - 2, h - 2 - (yOff - 1), 10, 10);
        }

        g.setColor(selected ? Palette.BG_DARK : Palette.TEXT);
        FontMetrics fm = g.getFontMetrics(b.getFont());
        g.setFont(b.getFont());
        String text = TextUtil.clip(fm, b.getText(), w - 12);
        int tw = fm.stringWidth(text);
        g.drawString(text, Math.max(6, (w - tw) / 2), yOff - 1 + (h + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }
}
