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

    /** Wie lange frische Meldungen hervorgehoben werden (ms). */
    private static final long FRESH_MS = 6000;

    private record Line(String text, Color color, int kind, long time, boolean first) {}

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        long now = System.currentTimeMillis();

        int pad = 16, top = 30, lineH = 16;
        int avail = Math.max(1, (getHeight() - top) / lineH);
        int textWidth = getWidth() - pad * 2;

        // Alle Meldungen in umbrochene Zeilen auflösen (Farbe nach Wichtigkeit)
        g.setFont(Palette.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        List<Line> lines = new ArrayList<>();
        boolean freshImportant = false;
        for (GameState.LogLine ll : frame.game().getLog()) {
            Color c = switch (ll.kind) {
                case GameState.LOG_GOOD -> Palette.GOOD;
                case GameState.LOG_BAD  -> Palette.BAD;
                case GameState.LOG_WARN -> Palette.WARN;
                default                 -> Palette.TEXT_DIM;   // Info dezent -> Wichtiges sticht heraus
            };
            if (ll.kind != GameState.LOG_INFO && now - ll.time < FRESH_MS) freshImportant = true;
            String full = "Tag " + ll.day + ":  " + ll.text;
            boolean first = true;
            for (String sub : wrap(full, fm, textWidth)) {
                lines.add(new Line(sub, c, ll.kind, ll.time, first));
                first = false;
            }
        }

        int start = Math.max(0, lines.size() - avail);
        int y = top;
        for (int i = start; i < lines.size(); i++) {
            Line l = lines.get(i);
            long age = now - l.time();
            // Frische Meldungen: kurz aufleuchtender Hintergrund (blendet aus)
            if (age < FRESH_MS) {
                int a = (int) (38 * (1 - age / (double) FRESH_MS));
                g.setColor(new Color(0, 199, 183, Math.max(0, a)));
                g.fillRoundRect(pad - 8, y - 1, getWidth() - pad * 2 + 8, lineH, 6, 6);
            }
            // Farbstreifen am Zeilenanfang (pro Meldung)
            if (l.first()) {
                g.setColor(l.kind() == GameState.LOG_INFO ? Palette.PANEL_HOVER : l.color());
                g.fillRoundRect(5, y, 4, lineH - 3, 2, 2);
            }
            g.setColor(age < FRESH_MS && l.kind() == GameState.LOG_INFO ? Palette.TEXT : l.color());
            g.drawString(l.text(), pad, y + fm.getAscent() - 1);
            y += lineH;
        }

        // Kopf zuletzt (liegt über den Zeilen)
        g.setColor(Palette.PANEL_LIGHT);
        g.fillRect(0, 0, getWidth(), 22);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.ACCENT);
        g.drawString("EREIGNIS-LOG", 12, 15);
        if (freshImportant) {   // pulsierender Punkt bei frischen wichtigen Meldungen
            float p = (float) (0.5 + 0.5 * Math.sin(now / 180.0));
            g.setColor(new Color(255, 159, 67, (int) (120 + 130 * p)));
            g.fillOval(96, 6, 10, 10);
            g.setColor(Palette.TEXT_DIM);
            g.setFont(Palette.FONT_TINY);
            g.drawString("NEU", 112, 15);
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
