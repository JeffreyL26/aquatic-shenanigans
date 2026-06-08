package com.shrimptopia.ui;

import com.shrimptopia.quest.GameCharacter;
import com.shrimptopia.quest.Quest;
import com.shrimptopia.tutorial.TutorialStep;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Glass-Pane-Overlay: zeigt entweder einen Tutorial-Schritt (mit Spotlight) ODER ein
 * Quest-Popup im Tropico-Stil (Portrait, Text, Auswahl-Buttons). Blockt Eingaben darunter.
 */
public class OverlayHost extends JComponent {

    public enum Mode { NONE, TUTORIAL, POPUP }

    private final GameFrame frame;
    private Mode mode = Mode.NONE;

    // Tutorial
    private TutorialStep step;
    private Rectangle spotlight;
    private int stepIdx, stepTotal;
    // Popup
    private Quest quest;

    private Rectangle card = new Rectangle();
    private List<String> bodyLines = new ArrayList<>();
    private final List<AbstractButton> buttons = new ArrayList<>();

    public OverlayHost(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(null);
        // verschluckt Klicks, damit das Spiel darunter nicht reagiert
        addMouseListener(new java.awt.event.MouseAdapter() {});
    }

    public Mode mode() { return mode; }

    /**
     * Steuert, welche Klicks das Overlay abfängt. Popups sind voll modal; im Tutorial
     * fängt nur die Karte (mit Buttons) Klicks ab - der Rest geht durch, damit man die
     * hervorgehobene UI bedienen kann (z.B. Gebäude bauen).
     */
    @Override public boolean contains(int x, int y) {
        if (mode == Mode.POPUP) return true;
        if (mode == Mode.TUTORIAL) return card.contains(x, y);
        return false;
    }

    public void showTutorial(TutorialStep s, Rectangle spot, int idx, int total) {
        this.mode = Mode.TUTORIAL; this.step = s; this.spotlight = spot;
        this.stepIdx = idx; this.stepTotal = total; this.quest = null;
        rebuildTutorialButtons();
        setVisible(true);
        relayout(); repaint();
    }

    public void showPopup(Quest q) {
        this.mode = Mode.POPUP; this.quest = q; this.step = null; this.spotlight = null;
        rebuildPopupButtons();
        setVisible(true);
        relayout(); repaint();
    }

    public void hideOverlay() {
        mode = Mode.NONE; quest = null; step = null;
        removeAll(); buttons.clear();
        setVisible(false); repaint();
    }

    private void clearButtons() { for (AbstractButton b : buttons) remove(b); buttons.clear(); }

    private void rebuildTutorialButtons() {
        clearButtons();
        boolean last = stepIdx >= stepTotal - 1;
        if (step != null && step.advance == TutorialStep.Advance.ACK) {
            ThemeButton.FlatButton next = new ThemeButton.FlatButton(last ? "Fertig" : "Weiter");
            next.addActionListener(e -> frame.tutorialAdvance());
            add(next); buttons.add(next);
        }
        ThemeButton.FlatButton skip = new ThemeButton.FlatButton("Tutorial überspringen");
        skip.addActionListener(e -> frame.tutorialSkip());
        add(skip); buttons.add(skip);
    }

    private void rebuildPopupButtons() {
        clearButtons();
        if (quest == null) return;
        for (int i = 0; i < quest.choices.size(); i++) {
            final int idx = i;
            ThemeButton.FlatButton b = new ThemeButton.FlatButton(quest.choices.get(i).text);
            b.addActionListener(e -> frame.resolveQuest(idx));
            add(b); buttons.add(b);
        }
        // Fallback: ein Popup ohne Optionen wäre sonst nicht schließbar.
        if (buttons.isEmpty()) {
            ThemeButton.FlatButton ok = new ThemeButton.FlatButton("Weiter");
            ok.addActionListener(e -> frame.resolveQuest(0));
            add(ok); buttons.add(ok);
        }
    }

    @Override public void doLayout() { relayout(); }

    private void relayout() {
        int W = getWidth(), H = getHeight();
        if (W == 0 || H == 0) return;
        Font bodyFont = Palette.FONT_BODY;
        FontMetrics fm = getFontMetrics(bodyFont);

        if (mode == Mode.TUTORIAL && step != null) {
            int cw = Math.min(640, W - 80), ch = 168;
            int cx = (W - cw) / 2, cy = H - ch - 36;
            card.setBounds(cx, cy, cw, ch);
            bodyLines = wrap(step.text, fm, cw - 130);
            // Buttons unten rechts / links
            int by = cy + ch - 44;
            int x = cx + cw - 12;
            for (int i = 0; i < buttons.size(); i++) {
                AbstractButton b = buttons.get(i);
                Dimension d = b.getPreferredSize();
                if (i == buttons.size() - 1 && buttons.size() > 1) {
                    // "überspringen" links unten
                    b.setBounds(cx + 16, by, d.width, 32);
                } else {
                    x -= d.width;
                    b.setBounds(x, by, d.width, 32);
                    x -= 8;
                }
            }
        } else if (mode == Mode.POPUP && quest != null) {
            int cw = Math.min(660, W - 120);
            // Fliesstext volle Breite UNTER Portrait/Titel (kein Overlap mehr)
            bodyLines = wrap(quest.body, fm, cw - 48);
            int headerH = 108;
            int textH = bodyLines.size() * 20 + 12;
            int btnH = Math.max(1, buttons.size()) * 46 + 8;
            int ch = headerH + textH + btnH + 16;
            int cx = (W - cw) / 2, cy = Math.max(40, (H - ch) / 2);
            card.setBounds(cx, cy, cw, ch);
            int by = cy + headerH + textH;
            for (AbstractButton b : buttons) {
                b.setBounds(cx + 20, by, cw - 40, 40);
                by += 46;
            }
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int W = getWidth(), H = getHeight();

        // Abdunkeln (mit Spotlight-Loch im Tutorial)
        Area dim = new Area(new Rectangle(0, 0, W, H));
        if (mode == Mode.TUTORIAL && spotlight != null)
            dim.subtract(new Area(new RoundRectangle2D.Double(
                spotlight.x - 4, spotlight.y - 4, spotlight.width + 8, spotlight.height + 8, 14, 14)));
        g.setColor(new Color(8, 12, 16, 150));
        g.fill(dim);
        if (mode == Mode.TUTORIAL && spotlight != null) {
            g.setColor(Palette.ACCENT);
            g.setStroke(new BasicStroke(2.5f));
            g.draw(new RoundRectangle2D.Double(spotlight.x - 4, spotlight.y - 4,
                spotlight.width + 8, spotlight.height + 8, 14, 14));
        }

        if (mode == Mode.NONE) { g.dispose(); return; }

        // Karte (Spielende-Popups golden hervorgehoben)
        boolean isEnding = mode == Mode.POPUP && quest != null && quest.ending;
        g.setColor(Palette.PANEL);
        g.fill(new RoundRectangle2D.Double(card.x, card.y, card.width, card.height, 16, 16));
        g.setColor(isEnding ? Palette.MONEY : Palette.ACCENT);
        g.setStroke(new BasicStroke(isEnding ? 3f : 2f));
        g.draw(new RoundRectangle2D.Double(card.x, card.y, card.width, card.height, 16, 16));
        if (isEnding) {
            g.setFont(Palette.FONT_TINY); g.setColor(Palette.MONEY);
            g.drawString(">>> SPIELENDE ERREICHT <<<", card.x + 104, card.y + 14);
        }

        // Portrait + Sprecher
        GameCharacter giver = (mode == Mode.POPUP) ? quest.giver : GameCharacter.ADVISOR;
        String speaker = (mode == Mode.POPUP) ? quest.giverName() : "Dr. Perla Pereira";
        String role = (mode == Mode.POPUP) ? giver.role : "Garnelen-Biologin";
        double px = card.x + 54, py = card.y + 54;
        g.setColor(Palette.PANEL_LIGHT);
        g.fillOval((int) px - 40, (int) py - 40, 80, 80);
        Icons.portrait(g, giver.portrait, px, py, 76, giver.color);

        int tx = card.x + 104;
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.TEXT);
        g.drawString(speaker, tx, card.y + 30);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.ACCENT);
        g.drawString(role, tx, card.y + 48);

        // Titel (Popup) bzw. Schrittzahl (Tutorial)
        if (mode == Mode.POPUP) {
            g.setFont(Palette.FONT_H2);
            g.setColor(Palette.ACCENT2);
            g.drawString(TextUtil.clip(g.getFontMetrics(), quest.title, card.width - 104 - 20), tx, card.y + 74);
        } else {
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            g.drawString("Tutorial " + (stepIdx + 1) + "/" + stepTotal, tx, card.y + 66);
        }

        // Fließtext
        g.setFont(Palette.FONT_BODY);
        g.setColor(Palette.TEXT);
        int yy = card.y + (mode == Mode.POPUP ? 112 : 86);
        int bx = card.x + (mode == Mode.POPUP ? 24 : 104);
        for (String line : bodyLines) { g.drawString(line, bx, yy); yy += 20; }

        g.dispose();
    }

    private static List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> out = new ArrayList<>();
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
