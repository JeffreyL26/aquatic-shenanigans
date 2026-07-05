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

        // Abdunkeln + Bühne
        g.setColor(new Color(6, 10, 14, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Kopfzeile
        int hx = arena.x, hy = arena.y - HEAD_H;
        g.setColor(Palette.PANEL);
        g.fillRoundRect(hx - 12, hy - 12, arena.width + 24, arena.height + HEAD_H + 24, 16, 16);
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(hx - 12, hy - 12, arena.width + 24, arena.height + HEAD_H + 24, 16, 16);

        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.TEXT);
        g.drawString(game != null ? game.title() : "", hx + 4, hy + 26);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        g.drawString(game != null ? game.subtitle() : "", hx + 4, hy + 43);

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
            g.setColor(tf < 0.25 ? Palette.BAD : Palette.ACCENT);
            g.fillRoundRect(tbx, tby, (int) (tbw * tf), 10, 5, 5);
        }
        if (stake > 1.2) {
            g.setFont(Palette.FONT_TINY);
            g.setColor(Palette.ACCENT2);
            g.drawString("EINSATZ x" + String.format("%.1f", stake), hx + 4, hy + 55);
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
        g.setColor(new Color(10, 14, 18, 215));
        g.fillRect(arena.x, arena.y, arena.width, arena.height);
        int cx = arena.x + arena.width / 2;
        int y = arena.y + arena.height / 2 - 60;
        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.ACCENT);
        String t = "MINIGAME: " + game.title();
        g.drawString(t, cx - g.getFontMetrics().stringWidth(t) / 2, y);
        y += 30;
        g.setFont(Palette.FONT_BODY);
        g.setColor(Palette.TEXT);
        for (String line : game.howTo()) {
            g.drawString(line, cx - g.getFontMetrics().stringWidth(line) / 2, y);
            y += 22;
        }
        y += 16;
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.MONEY);
        String s = ">> KLICK oder LEERTASTE zum Start <<";
        g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y);
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.TEXT_DIM);
        String esc = "(ESC bricht ab)";
        g.drawString(esc, cx - g.getFontMetrics().stringWidth(esc) / 2, y + 20);
    }

    private void drawDone(Graphics2D g) {
        g.setColor(new Color(10, 14, 18, 215));
        g.fillRect(arena.x, arena.y, arena.width, arena.height);
        int cx = arena.x + arena.width / 2;
        int y = arena.y + arena.height / 2 - 80;

        int stars = rating >= 0.85 ? 3 : rating >= 0.55 ? 2 : rating >= 0.25 ? 1 : 0;
        for (int i = 0; i < 3; i++) {
            Icons.resource(g, com.shrimptopia.model.IconKind.STAR, cx - 50 + i * 50, y, 40,
                i < stars ? Palette.MONEY : new Color(70, 80, 88));
        }
        y += 44;
        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.TEXT);
        String res = game.scoreLabel() + "  (" + Math.round(rating * 100) + "%)";
        g.drawString(res, cx - g.getFontMetrics().stringWidth(res) / 2, y);
        y += 34;
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.GOOD);
        for (String line : resultLines) {
            g.drawString(line, cx - g.getFontMetrics().stringWidth(line) / 2, y);
            y += 24;
        }
        y += 14;
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.MONEY);
        String s = ">> KLICK zum Weiterspielen <<";
        g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y);
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
