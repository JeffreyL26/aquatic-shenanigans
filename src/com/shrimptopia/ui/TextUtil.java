package com.shrimptopia.ui;

import java.awt.FontMetrics;

/** Kleine Text-Helfer fürs Zeichnen (Kürzen mit Ellipse). */
public final class TextUtil {
    private TextUtil() {}

    public static String clip(FontMetrics fm, String s, int maxWidth) {
        if (s == null) return "";
        if (fm.stringWidth(s) <= maxWidth) return s;
        String dots = "...";
        int dw = fm.stringWidth(dots);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (fm.stringWidth(b.toString() + s.charAt(i)) + dw > maxWidth) break;
            b.append(s.charAt(i));
        }
        return b + dots;
    }
}
