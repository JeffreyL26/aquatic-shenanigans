package com.shrimptopia.ui;

import com.shrimptopia.model.IconKind;
import com.shrimptopia.save.SaveManager;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hauptmenü als modales Vollbild-Overlay: Weiterspielen, Neues Spiel, Beenden
 * und fünf Spielstand-Slots mit Speichern / Laden / Löschen.
 * Erscheint beim Start und über den "Menü"-Knopf (bzw. ESC).
 */
public class MainMenuPanel extends JComponent {

    private final GameFrame frame;
    private final Rectangle card = new Rectangle();

    private final ThemeButton.FlatButton continueBtn, newBtn, quitBtn;
    private final ThemeButton.FlatButton[] saveBtn = new ThemeButton.FlatButton[SaveManager.SLOTS];
    private final ThemeButton.FlatButton[] loadBtn = new ThemeButton.FlatButton[SaveManager.SLOTS];
    private final ThemeButton.FlatButton[] delBtn  = new ThemeButton.FlatButton[SaveManager.SLOTS];
    private final SaveManager.SlotInfo[] infos = new SaveManager.SlotInfo[SaveManager.SLOTS];

    /** Rückmeldung der letzten Aktion ("Gespeichert in Slot 2." etc.). */
    private String feedback = "";
    private boolean feedbackGood = true;

    public MainMenuPanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(null);
        setVisible(false);

        continueBtn = new ThemeButton.FlatButton("Weiterspielen");
        continueBtn.base = new Color(0, 110, 102);
        continueBtn.addActionListener(e -> close());
        add(continueBtn);

        newBtn = new ThemeButton.FlatButton("Neues Spiel");
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

        // Modal: alle Klicks fangen; Klick außerhalb der Karte schließt (wie Almanach)
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                if (!card.contains(e.getPoint())) close();
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

    public void open() {
        refreshSlots();
        setVisible(true);
        revalidate();
        doLayout();
        repaint();
    }

    public void close() { setVisible(false); frame.afterMainMenuClosed(); }

    @Override public boolean contains(int x, int y) { return isVisible(); }   // modal

    private static final int SLOT_H = 56;

    @Override public void doLayout() {
        int W = getWidth(), H = getHeight();
        if (W == 0) return;
        int cw = Math.min(680, W - 80), ch = Math.min(96 + 40 + 42 + SaveManager.SLOTS * (SLOT_H + 10) + 44, H - 40);
        card.setBounds((W - cw) / 2, (H - ch) / 2, cw, ch);

        int bw = (cw - 40 - 2 * 10) / 3;
        int by = card.y + 96;
        continueBtn.setBounds(card.x + 20, by, bw, 40);
        newBtn.setBounds(card.x + 20 + bw + 10, by, bw, 40);
        quitBtn.setBounds(card.x + 20 + 2 * (bw + 10), by, bw, 40);

        int y = by + 40 + 42;
        for (int i = 0; i < SaveManager.SLOTS; i++) {
            int bx = card.x + cw - 20 - 3 * 86 - 2 * 6;
            saveBtn[i].setBounds(bx, y + (SLOT_H - 30) / 2, 86, 30);
            loadBtn[i].setBounds(bx + 92, y + (SLOT_H - 30) / 2, 86, 30);
            delBtn[i].setBounds(bx + 184, y + (SLOT_H - 30) / 2, 86, 30);
            y += SLOT_H + 10;
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Abdunkeln + Karte
        g.setColor(new Color(8, 12, 15, 215));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Palette.PANEL);
        g.fillRoundRect(card.x, card.y, card.width, card.height, 18, 18);
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(card.x, card.y, card.width, card.height, 18, 18);

        // Titel mit Shrimp-Logo
        Icons.resource(g, IconKind.SHRIMP, card.x + 46, card.y + 46, 42, Palette.SHRIMP);
        g.setFont(Palette.FONT_H1.deriveFont(26f));
        g.setColor(Palette.TEXT);
        g.drawString("SHRIMPTOPIA", card.x + 76, card.y + 44);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        g.drawString("Indoor Shrimp Farming Tycoon - Hauptmenü (ESC)", card.x + 76, card.y + 62);

        // Laufendes Spiel kurz anreißen (hilft bei "Weiterspielen oder laden?")
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        String cur = "Aktuelles Spiel: Tag " + frame.game().getDay()
            + " · " + TopBar.fmtInt(frame.game().getMoney()) + " Geld · "
            + frame.game().buildingCount() + " Gebäude";
        g.drawString(cur, card.x + 20, card.y + 90);

        // Abschnitt Spielstände
        int y = card.y + 96 + 40 + 26;
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
}
