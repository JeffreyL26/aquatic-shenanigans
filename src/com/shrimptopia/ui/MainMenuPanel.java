package com.shrimptopia.ui;

import com.shrimptopia.model.IconKind;
import com.shrimptopia.save.SaveManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Hauptmenü in zwei Erscheinungsformen:
 * - Titelbildschirm (Spielstart): opake Unterwasser-Szene mit großem Schriftzug,
 *   animierten Blasen/Shrimps - das Spielfeld ist NICHT sichtbar. Kein Schließen
 *   per ESC/Außenklick; verlassen nur über Neues Spiel / Laden / Beenden.
 * - In-Game-Menü (ESC bzw. "Menü"-Knopf): abgedunkeltes Overlay über dem Spiel
 *   mit Weiterspielen und allen Spielstand-Aktionen.
 */
public class MainMenuPanel extends JComponent {

    private final GameFrame frame;
    private final Rectangle card = new Rectangle();
    private boolean titleMode = false;

    private final ThemeButton.FlatButton continueBtn, newBtn, quitBtn;
    private final ThemeButton.FlatButton[] saveBtn = new ThemeButton.FlatButton[SaveManager.SLOTS];
    private final ThemeButton.FlatButton[] loadBtn = new ThemeButton.FlatButton[SaveManager.SLOTS];
    private final ThemeButton.FlatButton[] delBtn  = new ThemeButton.FlatButton[SaveManager.SLOTS];
    private final SaveManager.SlotInfo[] infos = new SaveManager.SlotInfo[SaveManager.SLOTS];

    /** Rückmeldung der letzten Aktion ("Gespeichert in Slot 2." etc.). */
    private String feedback = "";
    private boolean feedbackGood = true;

    // --- Ambiente des Titelbildschirms (Blasen, treibende Shrimps, Lichtspiel) ---
    private float animT = 0f;
    private static final int BUBBLES = 46;
    private final float[] bx = new float[BUBBLES], by = new float[BUBBLES],
                          br = new float[BUBBLES], bs = new float[BUBBLES], bp = new float[BUBBLES];
    private static final float[] SHRIMP_X0 = {0.18f, 0.68f, 0.42f};
    private static final float[] SHRIMP_Y  = {0.20f, 0.52f, 0.78f};
    private static final float[] SHRIMP_SZ = {150f, 90f, 120f};
    private static final float[] SHRIMP_V  = {0.010f, -0.014f, 0.007f};

    public MainMenuPanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(null);
        setVisible(false);

        Random rnd = new Random(7);
        for (int i = 0; i < BUBBLES; i++) {
            bx[i] = rnd.nextFloat();
            by[i] = rnd.nextFloat();
            br[i] = 2f + rnd.nextFloat() * 6f;
            bs[i] = 0.0008f + rnd.nextFloat() * 0.0025f;
            bp[i] = rnd.nextFloat() * (float) (Math.PI * 2);
        }

        continueBtn = new ThemeButton.FlatButton("Weiterspielen");
        continueBtn.base = new Color(0, 110, 102);
        continueBtn.addActionListener(e -> close());
        add(continueBtn);

        newBtn = new ThemeButton.FlatButton("Neues Spiel");
        newBtn.base = new Color(0, 110, 102);
        newBtn.addActionListener(e -> {
            frame.restartGame();
            feedback = "Neues Spiel gestartet - viel Erfolg in der Garage!";
            feedbackGood = true;
            close();
        });
        add(newBtn);

        quitBtn = new ThemeButton.FlatButton("Beenden");
        quitBtn.base = new Color(86, 44, 44);
        quitBtn.addActionListener(e -> System.exit(0));
        add(quitBtn);

        for (int i = 0; i < SaveManager.SLOTS; i++) {
            final int slot = i + 1;
            saveBtn[i] = new ThemeButton.FlatButton("Speichern");
            saveBtn[i].addActionListener(e -> doSave(slot));
            add(saveBtn[i]);
            loadBtn[i] = new ThemeButton.FlatButton("Laden");
            loadBtn[i].base = new Color(0, 90, 84);
            loadBtn[i].addActionListener(e -> doLoad(slot));
            add(loadBtn[i]);
            delBtn[i] = new ThemeButton.FlatButton("Löschen");
            delBtn[i].base = new Color(86, 44, 44);
            delBtn[i].addActionListener(e -> doDelete(slot));
            add(delBtn[i]);
        }

        // Modal: alle Klicks fangen; im In-Game-Menü schließt ein Klick außerhalb der
        // Karte (wie Almanach). Auf dem Titelbildschirm gibt es kein "dahinter".
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                if (!titleMode && !card.contains(e.getPoint())) close();
            }
        });
    }

    private void doSave(int slot) {
        boolean ok = SaveManager.save(slot, frame.game(), frame.questSystem(), frame.tutorial());
        feedback = ok ? "Gespeichert in Slot " + slot + " (Tag " + frame.game().getDay() + ")."
                      : "Speichern fehlgeschlagen - Verzeichnis nicht beschreibbar?";
        feedbackGood = ok;
        refreshSlots();
        repaint();
    }

    private void doLoad(int slot) {
        SaveManager.Loaded l = SaveManager.load(slot);
        if (l == null) {
            feedback = "Slot " + slot + " ist leer oder nicht lesbar.";
            feedbackGood = false;
            repaint();
            return;
        }
        frame.applyLoadedGame(l);
        feedback = "Slot " + slot + " geladen (Tag " + l.game().getDay() + ").";
        feedbackGood = true;
        close();
    }

    private void doDelete(int slot) {
        if (infos[slot - 1] == null) return;
        boolean ok = SaveManager.delete(slot);
        feedback = ok ? "Slot " + slot + " gelöscht." : "Löschen fehlgeschlagen.";
        feedbackGood = ok;
        refreshSlots();
        repaint();
    }

    private void refreshSlots() {
        for (int i = 0; i < SaveManager.SLOTS; i++) infos[i] = SaveManager.info(i + 1);
        for (int i = 0; i < SaveManager.SLOTS; i++) {
            boolean filled = infos[i] != null;
            loadBtn[i].setEnabled(filled);
            delBtn[i].setEnabled(filled);
        }
    }

    /** In-Game-Menü (über ESC bzw. "Menü"-Knopf). */
    public void open() { open(false); }

    /** Titelbildschirm beim Spielstart. */
    public void openTitle() { open(true); }

    private void open(boolean title) {
        titleMode = title;
        continueBtn.setVisible(!title);
        for (int i = 0; i < SaveManager.SLOTS; i++) saveBtn[i].setVisible(!title);
        feedback = "";
        refreshSlots();
        setVisible(true);
        revalidate();
        doLayout();
        repaint();
    }

    public void close() { setVisible(false); frame.afterMainMenuClosed(); }

    public boolean isTitle() { return titleMode; }

    /** Vom Animations-Timer des Frames: Blasen steigen, Shrimps treiben, Licht schwankt. */
    public void tickAnim() {
        if (!isVisible() || !titleMode) return;
        animT += 0.06f;
        for (int i = 0; i < BUBBLES; i++) {
            by[i] -= bs[i];
            if (by[i] < -0.05f) { by[i] = 1.05f; bx[i] = (bx[i] + 0.137f) % 1f; }
        }
        repaint();
    }

    @Override public boolean contains(int x, int y) { return isVisible(); }   // modal

    private static final int SLOT_H = 56;

    /** Oberkante der Spielstand-Liste - Layout und Malen nutzen dieselbe Rechnung. */
    private int slotsStartY() {
        return titleMode ? card.y + 20 + 46 + 18 + 22
                         : card.y + 96 + 40 + 42;
    }

    @Override public void doLayout() {
        int W = getWidth(), H = getHeight();
        if (W == 0) return;

        if (titleMode) {
            int cw = Math.min(640, W - 80);
            int ch = 20 + 46 + 18 + 22 + SaveManager.SLOTS * (SLOT_H + 10) - 10 + 44;
            // Karte im unteren Drittel, darüber bleibt Platz für den Schriftzug.
            int cy = Math.max(150, Math.min((int) (H * 0.34), H - ch - 24));
            card.setBounds((W - cw) / 2, cy, cw, ch);

            int bw = (cw - 40 - 10) / 2;
            continueBtn.setBounds(0, 0, 0, 0);
            newBtn.setBounds(card.x + 20, card.y + 20, bw, 46);
            quitBtn.setBounds(card.x + 20 + bw + 10, card.y + 20, bw, 46);
        } else {
            int cw = Math.min(680, W - 80), ch = Math.min(96 + 40 + 42 + SaveManager.SLOTS * (SLOT_H + 10) + 44, H - 40);
            card.setBounds((W - cw) / 2, (H - ch) / 2, cw, ch);

            int bw = (cw - 40 - 2 * 10) / 3;
            int by = card.y + 96;
            continueBtn.setBounds(card.x + 20, by, bw, 40);
            newBtn.setBounds(card.x + 20 + bw + 10, by, bw, 40);
            quitBtn.setBounds(card.x + 20 + 2 * (bw + 10), by, bw, 40);
        }

        int y = slotsStartY();
        for (int i = 0; i < SaveManager.SLOTS; i++) {
            int bx2 = titleMode ? card.x + card.width - 20 - 2 * 86 - 6
                                : card.x + card.width - 20 - 3 * 86 - 2 * 6;
            int step = 0;
            if (!titleMode) { saveBtn[i].setBounds(bx2, y + (SLOT_H - 30) / 2, 86, 30); step = 92; }
            loadBtn[i].setBounds(bx2 + step, y + (SLOT_H - 30) / 2, 86, 30);
            delBtn[i].setBounds(bx2 + step + 92, y + (SLOT_H - 30) / 2, 86, 30);
            y += SLOT_H + 10;
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int W = getWidth(), H = getHeight();

        if (titleMode) paintTitleScene(g, W, H);
        else {
            // In-Game: Spiel abdunkeln
            g.setColor(new Color(8, 12, 15, 215));
            g.fillRect(0, 0, W, H);
        }

        // Menükarte
        g.setColor(titleMode ? new Color(Palette.PANEL.getRed(), Palette.PANEL.getGreen(), Palette.PANEL.getBlue(), 235)
                             : Palette.PANEL);
        g.fillRoundRect(card.x, card.y, card.width, card.height, 18, 18);
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(card.x, card.y, card.width, card.height, 18, 18);

        if (!titleMode) {
            // Kopfzeile mit Shrimp-Logo (auf dem Titelbildschirm steht der Schriftzug oben)
            Icons.resource(g, IconKind.SHRIMP, card.x + 46, card.y + 46, 42, Palette.SHRIMP);
            g.setFont(Palette.FONT_H1.deriveFont(26f));
            g.setColor(Palette.TEXT);
            g.drawString("SHRIMPTOPIA", card.x + 76, card.y + 44);
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            g.drawString("Indoor Shrimp Farming Tycoon - Hauptmenü (ESC)", card.x + 76, card.y + 62);

            // Laufendes Spiel kurz anreißen (hilft bei "Weiterspielen oder laden?")
            String cur = "Aktuelles Spiel: Tag " + frame.game().getDay()
                + " · " + TopBar.fmtInt(frame.game().getMoney()) + " Geld · "
                + frame.game().buildingCount() + " Gebäude";
            g.drawString(cur, card.x + 20, card.y + 90);
        }

        // Abschnitt Spielstände
        int y = slotsStartY();
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.ACCENT);
        g.drawString("SPIELSTÄNDE", card.x + 20, y - 8);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < SaveManager.SLOTS; i++) {
            SaveManager.SlotInfo inf = infos[i];
            g.setColor(Palette.PANEL_LIGHT);
            g.fillRoundRect(card.x + 20, y, card.width - 40, SLOT_H, 10, 10);
            g.setFont(Palette.FONT_BOLD);
            g.setColor(Palette.TEXT);
            g.drawString("Slot " + (i + 1), card.x + 36, y + 23);
            g.setFont(Palette.FONT_SMALL);
            if (inf != null) {
                g.setColor(Palette.TEXT_DIM);
                g.drawString("Tag " + inf.day() + " · " + TopBar.fmtInt(inf.money()) + " Geld · "
                    + df.format(new Date(inf.savedAt())), card.x + 36, y + 41);
            } else {
                g.setColor(new Color(150, 166, 176, 140));
                g.drawString("- leer -", card.x + 36, y + 41);
            }
            y += SLOT_H + 10;
        }

        // Feedback-Zeile
        if (!feedback.isEmpty()) {
            g.setFont(Palette.FONT_SMALL);
            g.setColor(feedbackGood ? Palette.GOOD : Palette.BAD);
            g.drawString(feedback, card.x + 20, card.y + card.height - 16);
        }
        g.dispose();
    }

    // ===================== Titelbildschirm-Szene =====================

    /** Opake Unterwasser-Szene: Verlauf, Lichtkegel, treibende Shrimps, Blasen, Boden - plus Schriftzug. */
    private void paintTitleScene(Graphics2D g, int W, int H) {
        // Wasser-Verlauf: oben hell(er), unten Tiefsee
        g.setPaint(new GradientPaint(0, 0, new Color(11, 52, 66), 0, H, new Color(4, 13, 19)));
        g.fillRect(0, 0, W, H);

        // Lichtkegel von der Wasseroberfläche, sanft schwankend
        for (int i = 0; i < 4; i++) {
            double sway = Math.sin(animT * 0.35 + i * 1.7) * 40;
            int x = (int) (W * (0.14 + i * 0.24) + sway);
            Polygon p = new Polygon();
            p.addPoint(x - 26, -10); p.addPoint(x + 26, -10);
            p.addPoint(x + 160, H); p.addPoint(x - 160, H);
            g.setColor(new Color(120, 220, 210, 10));
            g.fillPolygon(p);
        }

        // Treibende Shrimp-Silhouetten (halbtransparent, verschiedene Größen/Tempi)
        for (int i = 0; i < SHRIMP_X0.length; i++) {
            float xf = (((SHRIMP_X0[i] + SHRIMP_V[i] * animT) % 1.2f) + 1.2f) % 1.2f - 0.1f;
            double yy = (SHRIMP_Y[i] + Math.sin(animT * 0.5 + i * 2.1) * 0.012) * H;
            Icons.resource(g, IconKind.SHRIMP, xf * W, yy, SHRIMP_SZ[i],
                new Color(255, 118, 104, 26));
        }

        // Meeresboden-Silhouette mit ein paar schwankenden Algen
        g.setColor(new Color(3, 9, 13));
        g.fillRect(0, H - 42, W, 42);
        g.fillOval(-90, H - 92, 360, 130);
        g.fillOval(W - 330, H - 104, 440, 150);
        g.fillOval(W / 2 - 160, H - 78, 320, 90);
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float[] ax = {0.07f, 0.11f, 0.48f, 0.84f, 0.90f};
        float[] ah = {70f, 100f, 60f, 90f, 65f};
        for (int k = 0; k < ax.length; k++) {
            double sway = Math.sin(animT * 0.6 + k * 1.3) * 9;
            double x = ax[k] * W, h = ah[k];
            g.setColor(new Color(10, 62, 48, 170));
            g.draw(new QuadCurve2D.Double(x, H - 12, x - sway, H - 12 - h * 0.55, x + sway, H - 12 - h));
        }

        // Aufsteigende Blasen
        for (int i = 0; i < BUBBLES; i++) {
            double px = bx[i] * W + Math.sin(animT * 0.8 + bp[i]) * 10;
            double py = by[i] * H;
            int r = Math.round(br[i]);
            g.setColor(new Color(190, 235, 230, 14));
            g.fillOval((int) px - r, (int) py - r, 2 * r, 2 * r);
            g.setColor(new Color(190, 235, 230, 34));
            g.setStroke(new BasicStroke(1.2f));
            g.drawOval((int) px - r, (int) py - r, 2 * r, 2 * r);
        }

        // Schriftzug über der Menükarte: Logo + Titel + Unterzeile
        String title = "SHRIMPTOPIA";
        Font tf = Palette.FONT_H1.deriveFont(Font.BOLD, 58f);
        FontMetrics fm = g.getFontMetrics(tf);
        g.setFont(tf);
        int tw = fm.stringWidth(title);
        int iconSz = 60, gap = 20;
        int total = iconSz + gap + tw;
        int tx = (W - total) / 2 + iconSz + gap;
        int tBase = Math.max(86, card.y - 64);
        Icons.resource(g, IconKind.SHRIMP, (W - total) / 2.0 + iconSz / 2.0,
            tBase - fm.getAscent() * 0.34, iconSz, Palette.SHRIMP);
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(title, tx + 3, tBase + 4);
        g.setColor(Palette.TEXT);
        g.drawString(title, tx, tBase);
        g.setColor(Palette.ACCENT);
        g.fillRoundRect(tx, tBase + 10, tw, 4, 2, 2);
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.TEXT_DIM);
        String sub = "Indoor Shrimp Farming Tycoon";
        g.drawString(sub, (W - g.getFontMetrics().stringWidth(sub)) / 2, tBase + 34);

        // Fußzeile
        g.setFont(Palette.FONT_SMALL);
        g.setColor(new Color(150, 166, 176, 160));
        String foot = "v7 · ESC öffnet dieses Menü auch im Spiel";
        g.drawString(foot, (W - g.getFontMetrics().stringWidth(foot)) / 2, H - 14);
    }
}
