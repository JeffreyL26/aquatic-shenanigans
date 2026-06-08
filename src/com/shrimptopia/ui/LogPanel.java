package com.shrimptopia.ui;

import com.shrimptopia.model.GameState;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Unten: scrollender Ereignis-Ticker (neueste Meldung unten). */
public class LogPanel extends JPanel {

    private final GameFrame frame;

    public LogPanel(GameFrame frame) {
        this.frame = frame;
        setBackground(Palette.PANEL);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Palette.BG_DARK));
        setPreferredSize(new Dimension(100, 138));
    }

    public void refresh() { repaint(); }

    private record Line(String text, Color color) {}

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Kopf
        g.setColor(Palette.PANEL_LIGHT);
        g.fillRect(0, 0, getWidth(), 22);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.ACCENT);
        g.drawString("EREIGNIS-LOG", 12, 15);

        int pad = 12, top = 30, lineH = 16;
        int avail = Math.max(1, (getHeight() - top) / lineH);
        int textWidth = getWidth() - pad * 2;

        // Alle Meldungen in umbrochene Zeilen auflösen
        g.setFont(Palette.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        List<Line> lines = new ArrayList<>();
        for (GameState.LogLine ll : frame.game().getLog()) {
            Color c = switch (ll.kind) {
                case GameState.LOG_GOOD -> Palette.GOOD;
                case GameState.LOG_BAD  -> Palette.BAD;
                case GameState.LOG_WARN -> Palette.WARN;
                default                 -> Palette.TEXT;
            };
            String full = "Tag " + ll.day + ":  " + ll.text;
            for (String sub : wrap(full, fm, textWidth)) lines.add(new Line(sub, c));
        }

        int start = Math.max(0, lines.size() - avail);
        int y = top + fm.getAscent();
        for (int i = start; i < lines.size(); i++) {
            g.setColor(lines.get(i).color());
            g.drawString(lines.get(i).text(), pad, y);
            y += lineH;
        }
        g.dispose();
    }

    private static List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = cur.length() == 0 ? word : cur + " " + word;
            if (fm.stringWidth(test) > maxWidth && cur.length() > 0) {
                out.add(cur.toString());
                cur = new StringBuilder(word);
            } else {
                cur = new StringBuilder(test);
            }
        }
        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }
}
