package com.shrimptopia.ui;

import com.shrimptopia.model.GameState;
import com.shrimptopia.quest.Quest;
import com.shrimptopia.quest.QuestSystem;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Dauerhaft sichtbares Quest-Log: aktive Aufgaben (Ziel-Quests) mit Fortschrittsbalken. */
public class QuestLogPanel extends JPanel {

    private final GameFrame frame;

    public QuestLogPanel(GameFrame frame) {
        this.frame = frame;
        setBackground(Palette.PANEL);
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Palette.BG_DARK));
        setPreferredSize(new Dimension(296, 226));
    }

    public void refresh() { repaint(); }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        GameState gs = frame.game();
        QuestSystem qs = frame.questSystem();
        int w = getWidth();

        // Kopf
        g.setColor(Palette.PANEL_LIGHT);
        g.fillRect(0, 0, w, 24);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.ACCENT);
        List<Quest> tasks = qs.activeTasks();
        g.drawString("AKTIVE AUFGABEN (" + tasks.size() + ")   -   erledigt: " + qs.doneCount(), 12, 16);

        int y = 34;
        if (tasks.isEmpty()) {
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            for (String line : wrap("Keine offenen Ziele. Erreiche Meilensteine (Geld, Produktion, Tag), um neue Quests auszuloesen.", g.getFontMetrics(), w - 24))
                { g.drawString(line, 12, y); y += 15; }
            g.dispose();
            return;
        }

        int blockH = 50;
        int maxShown = Math.max(1, (getHeight() - 30) / blockH);
        int shown = Math.min(tasks.size(), maxShown);
        for (int i = 0; i < shown; i++) {
            Quest q = tasks.get(i);
            double p = q.objective != null ? q.objective.progress(gs, qs) : 0;
            String desc = q.objective != null ? q.objective.describe(gs, qs) : "";
            String label = q.objectiveText != null ? q.objectiveText : q.title;

            g.setColor(Palette.PANEL_LIGHT);
            g.fillRoundRect(8, y, w - 16, blockH - 6, 8, 8);
            // Geber + Aufgabe (Geber links, Fortschrittstext rechts - nicht überlappen lassen)
            g.setFont(Palette.FONT_TINY);
            int dw0 = g.getFontMetrics().stringWidth(desc);
            g.setColor(Palette.ACCENT2);
            g.drawString(TextUtil.clip(g.getFontMetrics(), q.giverName(), w - 32 - dw0 - 8), 16, y + 14);
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT);
            g.drawString(TextUtil.clip(g.getFontMetrics(), label, w - 24), 16, y + 28);
            // Fortschrittsbalken
            int bx = 16, bw = w - 32, by = y + 34, bh = 8;
            g.setColor(Palette.BG_DARK);
            g.fillRoundRect(bx, by, bw, bh, 4, 4);
            g.setColor(p >= 1 ? Palette.GOOD : Palette.ACCENT);
            g.fillRoundRect(bx, by, (int) (bw * p), bh, 4, 4);
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.TEXT_DIM);
            int dw = g.getFontMetrics().stringWidth(desc);
            g.drawString(desc, w - dw - 16, y + 14);
            y += blockH;
        }
        if (tasks.size() > shown) {
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.TEXT_DIM);
            g.drawString("+ " + (tasks.size() - shown) + " weitere", 12, getHeight() - 6);
        }
        g.dispose();
    }

    private static java.util.List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        java.util.List<String> out = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = cur.length() == 0 ? word : cur + " " + word;
            if (fm.stringWidth(test) > maxWidth && cur.length() > 0) { out.add(cur.toString()); cur = new StringBuilder(word); }
            else cur = new StringBuilder(test);
        }
        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }
}
