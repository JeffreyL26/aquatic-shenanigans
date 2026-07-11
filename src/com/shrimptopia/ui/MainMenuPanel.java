package com.shrimptopia.ui;

import com.shrimptopia.model.IconKind;
import com.shrimptopia.save.SaveManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Hauptmenü in zwei Erscheinungsformen:
 * - Titelbildschirm (Spielstart): opake Unterwasser-Szene mit Intro-Animation,
 *   wellendem Schriftzug, Splash-Tagline, vorbeischwimmenden Shrimps, Blasen
 *   und Lichtspiel - das Spielfeld ist NICHT sichtbar. Kein Schließen per
 *   ESC/Außenklick; verlassen nur über Neues Spiel / Laden / Beenden.
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

    // --- Animationszustand des Titelbildschirms ---
    /** Laufende Szenenzeit (~1 Einheit pro Sekunde) und Intro-Fortschritt 0..1. */
    private float animT = 0f;
    private float introT = 1f;

    private static final int BUBBLES = 46;
    private final float[] bx = new float[BUBBLES], by = new float[BUBBLES],
                          br = new float[BUBBLES], bs = new float[BUBBLES], bp = new float[BUBBLES];

    /** Hintergrund-Silhouetten (groß, sehr transparent, treiben langsam). */
    private static final float[] SIL_X0 = {0.18f, 0.68f, 0.42f};
    private static final float[] SIL_Y  = {0.20f, 0.42f, 0.80f};
    private static final float[] SIL_SZ = {150f, 90f, 120f};
    private static final float[] SIL_V  = {0.010f, -0.014f, 0.007f};

    /** Kleiner Schwarm, der periodisch durchs Bild zieht. */
    private static final int SWARM = 10;
    private final float[] sdx = new float[SWARM], sdy = new float[SWARM],
                          ssz = new float[SWARM], sph = new float[SWARM];

    /** Splash-Taglines im Stil klassischer Titelbildschirme - wechseln alle paar Sekunden. */
    private static final String[] TAGLINES = {
        "Frisch aus der Garage!",
        "General Krillkill approved!",
        "Jetzt mit Boygroup!",
        "Kyle hasst diesen Trick!",
        "Über 9000 Shrimps!",
        "Mit echtem Leitungswasser!",
        "Von Nachbarn empfohlen!",
        "100% handgefüttert!",
    };

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
        for (int m = 0; m < SWARM; m++) {
            sdx[m] = -140f + rnd.nextFloat() * 280f;
            sdy[m] = -52f + rnd.nextFloat() * 104f;
            ssz[m] = 16f + rnd.nextFloat() * 10f;
            sph[m] = rnd.nextFloat() * (float) (Math.PI * 2);
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
        introT = title ? 0f : 1f;   // Intro nur auf dem Titelbildschirm
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

    /** Vom Animations-Timer des Frames: Intro, Blasen, Shrimps, Lichtspiel. */
    public void tickAnim() {
        if (!isVisible() || !titleMode) return;
        animT += 0.06f;
        if (introT < 1f) {
            introT = Math.min(1f, introT + 0.055f);
            doLayout();   // Karte gleitet während des Intros an ihren Platz
        }
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
            // Während des Intros gleitet sie von unten an ihren Platz.
            int cy = Math.max(150, Math.min((int) (H * 0.34), H - ch - 24));
            cy += Math.round((1f - ease(introT)) * 70f);
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
        if (titleMode) {
            // "Atmender" Glow hinter dem Neues-Spiel-Knopf lenkt den Blick auf den Einstieg.
            Rectangle nb = newBtn.getBounds();
            float pulse = 0.5f + 0.5f * (float) Math.sin(animT * 2.4);
            g.setColor(new Color(0, 199, 183, (int) (10 + pulse * 12)));
            for (int k = 4; k >= 1; k--)
                g.fillRoundRect(nb.x - 3 * k, nb.y - 3 * k, nb.width + 6 * k, nb.height + 6 * k, 16, 16);
            g.setColor(new Color(0, 199, 183, 150 + (int) (70 * pulse)));
        } else {
            g.setColor(Palette.ACCENT);
        }
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

        // Vordergrund-Blasen ÜBER der Karte geben der Szene Tiefe (sehr transparent).
        if (titleMode) {
            for (int i = 0; i < 8; i++) {
                double px = ((bx[i] + 0.5f) % 1f) * W + Math.sin(animT * 0.7 + bp[i]) * 14;
                double py = by[i] * H;
                int r = Math.round(br[i] * 2.4f);
                g.setColor(new Color(190, 235, 230, 10));
                g.fillOval((int) px - r, (int) py - r, 2 * r, 2 * r);
                g.setColor(new Color(190, 235, 230, 22));
                g.setStroke(new BasicStroke(1.4f));
                g.drawOval((int) px - r, (int) py - r, 2 * r, 2 * r);
            }
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

    /**
     * Opake Unterwasser-Szene: Verlauf, Wasseroberfläche, Lichtkegel, Plankton,
     * Silhouetten, Schwarm, Held-Shrimp, Boden mit Blasenquellen, Blasen -
     * darüber der animierte Schriftzug mit Splash-Tagline.
     */
    private void paintTitleScene(Graphics2D g, int W, int H) {
        // Wasser-Verlauf: oben hell(er), unten Tiefsee
        g.setPaint(new GradientPaint(0, 0, new Color(11, 52, 66), 0, H, new Color(4, 13, 19)));
        g.fillRect(0, 0, W, H);

        // Wasseroberfläche: zwei sanft wandernde Wellenlinien knapp unter dem oberen Rand
        g.setStroke(new BasicStroke(1.6f));
        for (int line = 0; line < 2; line++) {
            Path2D wave = new Path2D.Double();
            for (int x = 0; x <= W; x += 8) {
                double wy = 11 + line * 7
                    + 4 * Math.sin(x * 0.018 + animT * 1.3 + line * 1.2)
                    + 2.5 * Math.sin(x * 0.043 - animT * 0.8);
                if (x == 0) wave.moveTo(x, wy); else wave.lineTo(x, wy);
            }
            g.setColor(new Color(190, 235, 230, line == 0 ? 45 : 22));
            g.draw(wave);
        }

        // Lichtkegel von der Wasseroberfläche, sanft schwankend und pulsierend
        for (int i = 0; i < 4; i++) {
            double sway = Math.sin(animT * 0.35 + i * 1.7) * 40;
            int alpha = 8 + (int) (5 * (0.5 + 0.5 * Math.sin(animT * 0.5 + i * 2.3)));
            int x = (int) (W * (0.14 + i * 0.24) + sway);
            Polygon p = new Polygon();
            p.addPoint(x - 26, -10); p.addPoint(x + 26, -10);
            p.addPoint(x + 160, H); p.addPoint(x - 160, H);
            g.setColor(new Color(120, 220, 210, alpha));
            g.fillPolygon(p);
        }

        // Plankton: winzige treibende Punkte in mehreren "Tiefen"
        for (int i = 0; i < 36; i++) {
            float hxf = frac((float) Math.sin(i * 12.9898) * 43758.547f);
            float hyf = frac((float) Math.sin(i * 78.233) * 12543.1f);
            float px = frac(hxf + animT * 0.005f * (0.4f + hyf));
            float py = frac(hyf + animT * 0.0012f);
            g.setColor(new Color(190, 235, 230, 12 + (i % 3) * 6));
            g.fillOval((int) (px * W), (int) (py * H), 2, 2);
        }

        // Treibende Shrimp-Silhouetten im Hintergrund (halbtransparent, träge)
        for (int i = 0; i < SIL_X0.length; i++) {
            float xf = (((SIL_X0[i] + SIL_V[i] * animT) % 1.2f) + 1.2f) % 1.2f - 0.1f;
            double yy = (SIL_Y[i] + Math.sin(animT * 0.5 + i * 2.1) * 0.012) * H;
            Icons.resource(g, IconKind.SHRIMP, xf * W, yy, SIL_SZ[i],
                new Color(255, 118, 104, 26));
        }

        // Kleiner Schwarm zieht periodisch von rechts nach links durch
        float swarmCycle = (animT + 11f) % 23f;
        if (swarmCycle < 9f) {
            float q = swarmCycle / 9f;
            double baseX = (1.12 - 1.24 * q) * W, baseY = 0.70 * H;
            for (int m = 0; m < SWARM; m++) {
                double mx = baseX + sdx[m];
                double my = baseY + sdy[m] + Math.sin(animT * 2.2 + sph[m]) * 7;
                Graphics2D gm = (Graphics2D) g.create();
                gm.translate(mx, my);
                gm.scale(-1, 1);   // Kopf zeigt in Schwimmrichtung (links)
                gm.rotate(Math.toRadians(Math.sin(animT * 9 + sph[m]) * 10));
                Icons.resource(gm, IconKind.SHRIMP, 0, 0, ssz[m], new Color(255, 118, 104, 85));
                gm.dispose();
            }
        }

        // Held-Shrimp: quert alle ~14s die Szene, wedelt mit dem Schwanz, zieht Blasen
        float heroCycle = animT % 14f;
        int heroRun = (int) (animT / 14f);
        if (heroCycle < 6.5f) {
            float q = heroCycle / 6.5f;
            int dir = (heroRun % 2 == 0) ? 1 : -1;
            double hx = (dir == 1 ? -0.12 + 1.24 * q : 1.12 - 1.24 * q) * W;
            double hy = H * 0.50 + Math.sin(q * Math.PI * 4.4) * 22;
            Graphics2D gh = (Graphics2D) g.create();
            gh.translate(hx, hy);
            if (dir == -1) gh.scale(-1, 1);   // Auge/Kopf voran
            gh.rotate(Math.toRadians(Math.sin(animT * 12) * 8));
            Icons.resource(gh, IconKind.SHRIMP, 0, 0, 140, new Color(255, 118, 104, 150));
            gh.dispose();
            for (int k = 1; k <= 6; k++) {
                double tx2 = hx - dir * (46 + k * 26);
                double ty2 = hy - 8 - k * 5 + Math.sin(animT * 3.5 + k) * 5;
                int rr = Math.max(2, 7 - k);
                g.setColor(new Color(190, 235, 230, Math.max(0, 72 - k * 11)));
                g.drawOval((int) tx2 - rr, (int) ty2 - rr, 2 * rr, 2 * rr);
            }
        }

        // Meeresboden-Silhouette mit schwankenden Algen und Blasenquellen
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
        g.setStroke(new BasicStroke(1.2f));
        float[] vents = {0.22f, 0.56f, 0.87f};
        for (float v : vents) {
            for (int k = 0; k < 7; k++) {
                float p = frac(animT * 0.22f + k * 0.143f + v);
                double vy = H - 18 - p * H * 0.38;
                double vx = v * W + Math.sin(p * 9 + k * 1.7) * 7;
                int rr = 2 + Math.round(3 * p);
                g.setColor(new Color(190, 235, 230, (int) (70 * (1 - p))));
                g.drawOval((int) vx - rr, (int) vy - rr, 2 * rr, 2 * rr);
            }
        }

        // Aufsteigende Blasen (Umgebung)
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

        // Leichte Vignette lenkt den Blick zur Mitte
        g.setPaint(new RadialGradientPaint(new Point(W / 2, (int) (H * 0.45)),
            Math.max(W, H) * 0.75f, new float[]{0.55f, 1f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 110)}));
        g.fillRect(0, 0, W, H);

        paintTitleText(g, W, H);
    }

    /** Schriftzug: Buchstaben fallen ein und wellen, Glanz-Sweep, Unterzeile, Splash-Tagline. */
    private void paintTitleText(Graphics2D g, int W, int H) {
        String title = "SHRIMPTOPIA";
        Font tf = Palette.FONT_H1.deriveFont(Font.BOLD, 58f);
        FontMetrics fm = g.getFontMetrics(tf);
        g.setFont(tf);
        int tw = fm.stringWidth(title);
        int iconSz = 60, gap = 20;
        int total = iconSz + gap + tw;
        int tx = (W - total) / 2 + iconSz + gap;
        int tBase = Math.max(86, card.y - 64);

        // Logo fällt mit dem ersten Buchstaben ein
        float e0 = ease(clamp01(introT * 2.0f));
        Icons.resource(g, IconKind.SHRIMP, (W - total) / 2.0 + iconSz / 2.0,
            tBase - fm.getAscent() * 0.34 - (1f - e0) * 46,
            iconSz, new Color(255, 118, 104, (int) (255 * e0)));

        // Glanz-Sweep: alle ~6,5s wandert ein Lichtstreifen über die Buchstaben
        float sweep = animT % 6.5f;
        double sweepX = sweep < 1.3f ? tx - 90 + (tw + 180) * (sweep / 1.3f) : Double.NaN;

        int xcur = tx;
        for (int i = 0; i < title.length(); i++) {
            char ch = title.charAt(i);
            int cwd = fm.charWidth(ch);
            float li = ease(clamp01(introT * 2.0f - i * 0.08f));
            double dy = Math.sin(animT * 1.7 + i * 0.52) * 5 - (1f - li) * 46;
            String s = String.valueOf(ch);
            g.setColor(new Color(0, 0, 0, (int) (150 * li)));
            g.drawString(s, xcur + 3, (int) (tBase + dy) + 4);
            g.setColor(new Color(233, 241, 245, (int) (255 * li)));
            g.drawString(s, xcur, (int) (tBase + dy));
            if (!Double.isNaN(sweepX)) {
                int ga = (int) (200 * Math.max(0, 1 - Math.abs(xcur + cwd / 2.0 - sweepX) / 75.0) * li);
                if (ga > 0) {
                    g.setColor(new Color(255, 255, 255, ga));
                    g.drawString(s, xcur, (int) (tBase + dy));
                }
            }
            xcur += cwd;
        }

        // Teal-Unterstrich wächst aus der Mitte
        float eu = ease(clamp01(introT * 1.4f - 0.35f));
        int uw = (int) (tw * eu);
        g.setColor(Palette.ACCENT);
        g.fillRoundRect(tx + (tw - uw) / 2, tBase + 10, uw, 4, 2, 2);

        // Unterzeile
        g.setFont(Palette.FONT_H2);
        float es = clamp01(introT * 1.6f - 0.5f);
        g.setColor(new Color(150, 166, 176, (int) (255 * es)));
        String sub = "Indoor Shrimp Farming Tycoon";
        g.drawString(sub, (W - g.getFontMetrics().stringWidth(sub)) / 2, tBase + 34);

        // Splash-Tagline: schräg, orange, pulsierend, wechselt alle 6 Sekunden
        float et = clamp01(introT * 2f - 1f);
        if (et > 0f) {
            String tag = TAGLINES[(int) (animT / 6f) % TAGLINES.length];
            float pop = clamp01((animT % 6f) / 0.4f);
            double scale = (0.6 + 0.4 * pop) * (1 + 0.06 * Math.sin(animT * 3.1)) * et;
            Graphics2D gt = (Graphics2D) g.create();
            gt.translate(tx + tw - 14, tBase + 26);
            gt.rotate(Math.toRadians(-9));
            gt.scale(scale, scale);
            gt.setFont(Palette.FONT_H2.deriveFont(16f));
            FontMetrics tm = gt.getFontMetrics();
            int tgw = tm.stringWidth(tag);
            gt.setColor(new Color(0, 0, 0, (int) (160 * et)));
            gt.drawString(tag, -tgw / 2 + 2, 2 + 5);
            gt.setColor(new Color(255, 159, 67, (int) (255 * et)));
            gt.drawString(tag, -tgw / 2, 5);
            gt.dispose();
        }

        // Fußzeile
        g.setFont(Palette.FONT_SMALL);
        g.setColor(new Color(150, 166, 176, 160));
        String foot = "v7 · ESC öffnet dieses Menü auch im Spiel";
        g.drawString(foot, (W - g.getFontMetrics().stringWidth(foot)) / 2, H - 14);
    }

    // ---- kleine Animations-Helfer ----

    private static float clamp01(float v) { return v < 0f ? 0f : Math.min(v, 1f); }

    /** easeOutCubic: schneller Start, weiches Ankommen. */
    private static float ease(float u) { u = clamp01(u); float m = 1f - u; return 1f - m * m * m; }

    private static float frac(float v) { return v - (float) Math.floor(v); }
}
