package com.shrimptopia.ui;

import com.shrimptopia.ui.minigames.Minigame;
import com.shrimptopia.ui.minigames.MinigameCatalog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Vollbild-Overlay, das Quest-Minigames ausführt: Intro (Anleitung) -> Spiel
 * (eigene 60-FPS-Schleife) -> Ergebnis (Sterne + Belohnung). Die Belohnung skaliert
 * mit dem Rating - Geschick zahlt sich direkt in Geld/Reputation aus.
 */
public class MinigamePanel extends JComponent {

    private enum Phase { INTRO, PLAY, DONE }

    private final GameFrame frame;
    private final Timer loop;
    private long lastNs;

    private MinigameCatalog.Entry entry;
    private Minigame game;
    private double stake = 1;
    private Phase phase = Phase.INTRO;
    private final List<String> resultLines = new ArrayList<>();
    private double rating;

    // Arena-Layout (in Panel-Koordinaten)
    private final Rectangle arena = new Rectangle();
    private static final int HEAD_H = 56;

    public MinigamePanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setVisible(false);
        loop = new Timer(16, this::onLoop);

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                switch (phase) {
                    case INTRO -> beginPlay();
                    case PLAY  -> { if (game != null) game.press(e.getX() - arena.x, e.getY() - arena.y); }
                    case DONE  -> close();
                }
            }
            @Override public void mouseMoved(MouseEvent e)   { if (phase == Phase.PLAY && game != null) game.move(e.getX() - arena.x, e.getY() - arena.y); }
            @Override public void mouseDragged(MouseEvent e) { if (phase == Phase.PLAY && game != null) game.drag(e.getX() - arena.x, e.getY() - arena.y); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);

        bind(KeyEvent.VK_SPACE, "mg_space", this::keySpace);
        bind(KeyEvent.VK_ESCAPE, "mg_esc", this::keyEscape);
        bind(KeyEvent.VK_ENTER, "mg_enter", () -> { if (phase == Phase.DONE) close(); });
        int[] laneKeys = { KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F };
        for (int i = 0; i < laneKeys.length; i++) {
            final int lane = i;
            bind(laneKeys[i], "mg_lane" + i, () -> { if (phase == Phase.PLAY && game != null) game.lane(lane); });
        }
    }

    private void keySpace() {
        if (phase == Phase.INTRO) beginPlay();
        else if (phase == Phase.PLAY && game != null) game.lane(-1);
        else if (phase == Phase.DONE) close();
    }
    private void keyEscape() {
        if (phase == Phase.PLAY) endPlay();   // abbrechen: aktueller Stand zählt
        else close();
    }

    /** Vom GameFrame: global gebundene Tasten (ESC/LEER ...) landen hier, solange das Spiel läuft. */
    public void handleKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE -> keySpace();
            case KeyEvent.VK_ESCAPE -> keyEscape();
            default -> { }   // Tempo-/Menü-Tasten sind während des Minigames bewusst tot
        }
    }

    private void bind(int key, String name, Runnable action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { if (isVisible()) action.run(); }
        });
    }

    @Override public boolean contains(int x, int y) { return isVisible(); }   // modal

    /** Öffnet das Minigame mit Intro-Bildschirm. Unbekannte IDs werden ignoriert. */
    public boolean open(String id, double stake) {
        entry = MinigameCatalog.get(id);
        if (entry == null) return false;
        this.stake = stake <= 0 ? 1 : stake;
        game = entry.factory().get();
        phase = Phase.INTRO;
        resultLines.clear();
        setVisible(true);
        repaint();
        return true;
    }

    private void layoutArena() {
        int aw = Math.min(920, getWidth() - 120);
        int ah = Math.min(540, getHeight() - 200);
        arena.setBounds((getWidth() - aw) / 2, (getHeight() - ah) / 2 + HEAD_H / 2, aw, ah);
    }

    private void beginPlay() {
        layoutArena();
        game.start(arena.width, arena.height, new Random(), stake);
        phase = Phase.PLAY;
        lastNs = System.nanoTime();
        loop.start();
    }

    private void onLoop(ActionEvent e) {
        long now = System.nanoTime();
        double dt = Math.min(0.05, (now - lastNs) / 1e9);
        lastNs = now;
        game.update(dt);
        if (game.isDone()) endPlay();
        repaint();
    }

    private void endPlay() {
        loop.stop();
        phase = Phase.DONE;
        rating = game.rating();
        resultLines.clear();
        MinigameCatalog.applyReward(frame.game(), entry, rating, stake, resultLines);
        frame.game().log("Minigame '" + game.title() + "': " + game.scoreLabel()
            + " - " + String.join(", ", resultLines), com.shrimptopia.model.GameState.LOG_GOOD);
        repaint();
    }

    private void close() {
        loop.stop();
        setVisible(false);
        game = null;
        frame.onMinigameClosed();
    }

    /** Schließt hart (z.B. bei Neustart), ohne Belohnung. */
    public void forceClose() {
        loop.stop();
        setVisible(false);
        game = null;
    }

    @Override protected void paintComponent(Graphics g0) {
        if (game == null && phase != Phase.DONE) return;
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        layoutArena();

        Color accent = game != null ? game.accent() : Palette.ACCENT;

        // Abdunkeln mit Vignette + Bühne
        g.setColor(new Color(4, 8, 12, 215));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setPaint(new RadialGradientPaint(
            new java.awt.geom.Point2D.Double(getWidth() / 2.0, getHeight() / 2.0), Math.max(400, getWidth() * 0.6f),
            new float[]{0f, 1f}, new Color[]{ new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 16), new Color(0, 0, 0, 0) }));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Karte mit Schatten + Akzentrahmen
        int hx = arena.x, hy = arena.y - HEAD_H;
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(hx - 8, hy - 6, arena.width + 24, arena.height + HEAD_H + 24, 16, 16);
        g.setColor(Palette.PANEL);
        g.fillRoundRect(hx - 12, hy - 12, arena.width + 24, arena.height + HEAD_H + 24, 16, 16);
        g.setColor(accent);
        g.setStroke(new BasicStroke(2.2f));
        g.drawRoundRect(hx - 12, hy - 12, arena.width + 24, arena.height + HEAD_H + 24, 16, 16);

        // Kopfzeile: Akzentbalken links + Titel
        g.setColor(accent);
        g.fillRoundRect(hx - 2, hy + 6, 5, 34, 3, 3);
        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.TEXT);
        g.drawString(game != null ? game.title() : "", hx + 12, hy + 26);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        g.drawString(game != null ? game.subtitle() : "", hx + 12, hy + 43);

        if (phase == Phase.PLAY && game != null) {
            // Punkte + Zeitbalken rechts oben
            g.setFont(Palette.FONT_H2);
            g.setColor(Palette.MONEY);
            String sc = game.scoreLabel();
            int scw = g.getFontMetrics().stringWidth(sc);
            g.drawString(sc, hx + arena.width - scw - 4, hy + 24);
            double tf = game.duration() <= 0 ? 0 : game.timeLeft() / game.duration();
            int tbw = 160, tbx = hx + arena.width - tbw - 4, tby = hy + 32;
            g.setColor(Palette.PANEL_LIGHT);
            g.fillRoundRect(tbx, tby, tbw, 10, 5, 5);
            g.setColor(tf < 0.25 ? Palette.BAD : accent);
            g.fillRoundRect(tbx, tby, (int) (tbw * tf), 10, 5, 5);
            g.setColor(new Color(255, 255, 255, 60));
            g.drawRoundRect(tbx, tby, tbw, 10, 5, 5);
        }
        if (stake > 1.2) {
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.ACCENT2);
            String st = "EINSATZ x" + String.format("%.1f", stake);
            int stw = g.getFontMetrics().stringWidth(st);
            g.setColor(new Color(255, 159, 67, 30));
            g.fillRoundRect(hx + 12, hy + 47, stw + 12, 14, 7, 7);
            g.setColor(Palette.ACCENT2);
            g.drawString(st, hx + 18, hy + 58);
        }

        // Arena (das Spiel selbst erst ab Start - im Intro ist es noch nicht initialisiert)
        Shape oldClip = g.getClip();
        g.clip(arena);
        if (phase == Phase.INTRO) {
            g.setColor(new Color(14, 20, 26));
            g.fillRect(arena.x, arena.y, arena.width, arena.height);
        } else if (game != null) {
            g.translate(arena.x, arena.y);
            game.render(g);
            g.translate(-arena.x, -arena.y);
        }
        g.setClip(oldClip);
        g.setColor(new Color(255, 255, 255, 40));
        g.drawRect(arena.x, arena.y, arena.width, arena.height);

        if (phase == Phase.INTRO) drawIntro(g);
        if (phase == Phase.DONE) drawDone(g);
        g.dispose();
    }

    private void drawIntro(Graphics2D g) {
        Color accent = game.accent();
        g.setColor(new Color(10, 14, 18, 220));
        g.fillRect(arena.x, arena.y, arena.width, arena.height);
        int cx = arena.x + arena.width / 2;
        int y = arena.y + arena.height / 2 - 96;

        g.setFont(Palette.FONT_H1.deriveFont(24f));
        g.setColor(accent);
        String t = game.title().toUpperCase();
        int tw = g.getFontMetrics().stringWidth(t);
        g.drawString(t, cx - tw / 2, y);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx - tw / 2, y + 8, cx + tw / 2, y + 8);
        y += 40;

        g.setFont(Palette.FONT_BODY);
        for (String line : game.howTo()) {
            int lw = g.getFontMetrics().stringWidth(line);
            g.setColor(accent);
            g.fillRoundRect(cx - lw / 2 - 16, y - 9, 8, 8, 3, 3);
            g.setColor(Palette.TEXT);
            g.drawString(line, cx - lw / 2, y);
            y += 24;
        }
        y += 10;
        // Belohnungs-Vorschau (max. Werte inkl. Einsatz)
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        StringBuilder rw = new StringBuilder("Belohnung bis zu:  ");
        if (entry.money() > 0) rw.append(String.format("%,d", Math.round(entry.money() * stake))).append(" Geld   ");
        if (entry.rep() > 0) rw.append("+").append(Math.round(entry.rep() * stake)).append(" Ruf   ");
        if (entry.shrimp() > 0) rw.append("+").append(Math.round(entry.shrimp() * stake)).append(" Shrimps");
        g.drawString(rw.toString().trim(), cx - g.getFontMetrics().stringWidth(rw.toString().trim()) / 2, y);
        y += 34;

        // Start-"Button"
        g.setFont(Palette.FONT_H2);
        String s = "KLICK ODER LEERTASTE  -  START";
        int sw = g.getFontMetrics().stringWidth(s);
        g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
        g.fillRoundRect(cx - sw / 2 - 18, y - 20, sw + 36, 32, 10, 10);
        g.setColor(accent);
        g.drawRoundRect(cx - sw / 2 - 18, y - 20, sw + 36, 32, 10, 10);
        g.drawString(s, cx - sw / 2, y + 2);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.TEXT_DIM);
        String esc = "ESC bricht ab";
        g.drawString(esc, cx - g.getFontMetrics().stringWidth(esc) / 2, y + 30);
    }

    private void drawDone(Graphics2D g) {
        Color accent = game.accent();
        g.setColor(new Color(10, 14, 18, 225));
        g.fillRect(arena.x, arena.y, arena.width, arena.height);
        int cx = arena.x + arena.width / 2;

        // Ergebnis-Karte
        int cw = Math.min(420, arena.width - 60);
        int chh = 210 + resultLines.size() * 24;
        int cyTop = arena.y + (arena.height - chh) / 2;
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(cx - cw / 2 + 4, cyTop + 5, cw, chh, 16, 16);
        g.setColor(Palette.PANEL_LIGHT);
        g.fillRoundRect(cx - cw / 2, cyTop, cw, chh, 16, 16);
        g.setColor(accent);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(cx - cw / 2, cyTop, cw, chh, 16, 16);

        int y = cyTop + 56;
        int stars = rating >= 0.85 ? 3 : rating >= 0.55 ? 2 : rating >= 0.25 ? 1 : 0;
        for (int i = 0; i < 3; i++) {
            boolean lit = i < stars;
            if (lit) {   // Glow hinter dem Stern
                g.setColor(new Color(255, 205, 86, 45));
                g.fillOval(cx - 60 + i * 56 - 26, y - 26, 52, 52);
            }
            Icons.resource(g, com.shrimptopia.model.IconKind.STAR, cx - 60 + i * 56, y, lit ? 44 : 36,
                lit ? Palette.MONEY : new Color(64, 74, 82));
        }
        y += 46;
        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.TEXT);
        String res = game.scoreLabel() + "   (" + Math.round(rating * 100) + "%)";
        g.drawString(res, cx - g.getFontMetrics().stringWidth(res) / 2, y);
        y += 18;
        g.setColor(new Color(255, 255, 255, 30));
        g.drawLine(cx - cw / 2 + 24, y, cx + cw / 2 - 24, y);
        y += 26;
        g.setFont(Palette.FONT_H2);
        for (String line : resultLines) {
            g.setColor(Palette.GOOD);
            Icons.resource(g, com.shrimptopia.model.IconKind.COIN, cx - g.getFontMetrics().stringWidth(line) / 2 - 18, y - 5, 14,
                line.startsWith("Geld") ? Palette.MONEY : line.startsWith("Rep") ? Palette.REP : Palette.SHRIMP);
            g.drawString(line, cx - g.getFontMetrics().stringWidth(line) / 2, y);
            y += 24;
        }
        y += 14;
        g.setFont(Palette.FONT_H2);
        String s = "WEITER  (Klick)";
        int sw = g.getFontMetrics().stringWidth(s);
        g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
        g.fillRoundRect(cx - sw / 2 - 16, y - 18, sw + 32, 28, 9, 9);
        g.setColor(accent);
        g.drawRoundRect(cx - sw / 2 - 16, y - 18, sw + 32, 28, 9, 9);
        g.drawString(s, cx - sw / 2, y + 1);
    }

    // Debug-Hooks für den GUI-Test
    public void debugForceIntro(String id, double stake) { open(id, stake); }
    public void debugStartAndTick(double seconds) {
        if (phase != Phase.INTRO) return;
        beginPlay();
        loop.stop();
        for (double t = 0; t < seconds; t += 0.016) game.update(0.016);
        repaint();
    }
}
