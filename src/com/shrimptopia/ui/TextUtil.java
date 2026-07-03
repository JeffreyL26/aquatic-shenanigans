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

    /** Bricht Text wortweise auf maxWidth um; höchstens maxLines Zeilen, letzte wird gekürzt. */
    public static java.util.List<String> wrap(FontMetrics fm, String s, int maxWidth, int maxLines) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (s == null || s.isEmpty() || maxLines <= 0) return lines;
        String[] words = s.split(" ");
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String probe = line.length() == 0 ? words[i] : line + " " + words[i];
            if (fm.stringWidth(probe) <= maxWidth || line.length() == 0) {
                line.setLength(0); line.append(probe);
            } else if (lines.size() < maxLines - 1) {
                lines.add(line.toString());
                line.setLength(0); line.append(words[i]);
            } else {   // letzte Zeile: Rest anhängen und kürzen
                for (int j = i; j < words.length; j++) line.append(' ').append(words[j]);
                break;
            }
        }
        lines.add(clip(fm, line.toString(), maxWidth));
        return lines;
    }
}
