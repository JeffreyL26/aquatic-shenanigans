package com.shrimptopia.ui;

import com.shrimptopia.quest.ChoiceOutcome;
import com.shrimptopia.quest.GameCharacter;
import com.shrimptopia.quest.Quest;
import com.shrimptopia.tutorial.TutorialStep;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Glass-Pane-Overlay: zeigt entweder einen Tutorial-Schritt (mit Spotlight) ODER ein
 * Quest-Popup im Tropico-Stil (Portrait, Text, Auswahl-Buttons). Blockt Eingaben darunter.
 */
public class OverlayHost extends JComponent {

    public enum Mode { NONE, TUTORIAL, POPUP, OUTCOME, ANNOUNCE }

    private final GameFrame frame;
    private Mode mode = Mode.NONE;

    // Tutorial
    private TutorialStep step;
    private Rectangle spotlight;
    private int stepIdx, stepTotal;
    // Popup / Ergebnis
    private Quest quest;
    private ChoiceOutcome outcome;
    // Freischalt-Ankündigung
    private String annTitle, annBody, annHint, annFlag;
    private List<String> annHintLines = new ArrayList<>();

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
        if (mode == Mode.POPUP || mode == Mode.OUTCOME || mode == Mode.ANNOUNCE) return true;
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
        this.mode = Mode.POPUP; this.quest = q; this.step = null; this.spotlight = null; this.outcome = null;
        rebuildPopupButtons();
        setVisible(true);
        relayout(); repaint();
    }

    /** Ergebnis-Karte nach einer Entscheidung: Ergebnistext + konkrete Konsequenzen. */
    public void showOutcome(ChoiceOutcome o) {
        this.mode = Mode.OUTCOME; this.outcome = o; this.quest = o.quest;
        this.step = null; this.spotlight = null;
        clearButtons();
        ThemeButton.FlatButton ok = new ThemeButton.FlatButton("Weiter");
        ok.addActionListener(e -> frame.dismissOutcome());
        add(ok); buttons.add(ok);
        setVisible(true);
        relayout(); repaint();
    }

    /** Freischalt-Ansage (Dr. Perla): was ist neu und wo findet man es. */
    public void showAnnouncement(String flag, String msg) {
        this.mode = Mode.ANNOUNCE; this.quest = null; this.outcome = null;
        this.step = null; this.spotlight = null;
        this.annTitle = "FREIGESCHALTET!";
        this.annBody = msg;
        this.annFlag = flag;
        this.annHint = hintFor(flag);
        clearButtons();
        // "Zeig mir!" führt direkt zur Neuerung (Zone/Almanach) und lässt sie aufleuchten.
        if (hintFor(flag) != null) {
            ThemeButton.FlatButton go = new ThemeButton.FlatButton("→ Zeig mir!");
            go.addActionListener(e -> frame.navigateFromAnnouncement());
            add(go); buttons.add(go);
        }
        ThemeButton.FlatButton ok = new ThemeButton.FlatButton("Verstanden!");
        ok.addActionListener(e -> frame.dismissAnnouncement());
        add(ok); buttons.add(ok);
        setVisible(true);
        relayout(); repaint();
    }

    /** Wo findet der Spieler die Neuerung? */
    private static String hintFor(String flag) {
        if (flag == null) return null;
        if (flag.startsWith("zone.")) return "Neue Karte! Links in der Seitenleiste unter STANDORTE wechseln.";
        if (flag.equals("era.HALLE")) return "Neue Gebäude im Baumenü links - Garagen-Technik lässt sich im Inspektor UPGRADEN.";
        if (flag.startsWith("mkt."))  return "Buchbar im Almanach unter MARKETING.";
        if (flag.startsWith("build.")) return "Neues Gebäude im Baumenü (in der passenden Zone).";
        if (flag.startsWith("tier.")) return "Neuer Zucht-Modus: Becken anklicken → Inspektor.";
        return null;
    }

    public void hideOverlay() {
        mode = Mode.NONE; quest = null; step = null; outcome = null;
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
            int cw = Math.min(640, W - 80);
            bodyLines = wrap(step.text, fm, cw - 130);
            // Kartenhöhe an den Text anpassen, sonst läuft er in die Buttons
            int ch = Math.max(168, 86 + bodyLines.size() * 20 + 52);
            int cx = (W - cw) / 2, cy = H - ch - 36;
            card.setBounds(cx, cy, cw, ch);
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
        } else if (mode == Mode.OUTCOME && outcome != null) {
            int cw = Math.min(620, W - 120);
            String res = outcome.choice != null && outcome.choice.resultText != null && !outcome.choice.resultText.isEmpty()
                ? outcome.choice.resultText : "So sei es.";
            bodyLines = wrap(res, fm, cw - 48);
            int headerH = 108, chosenH = 20;
            int textH = bodyLines.size() * 20 + 8;
            int fxH = outcome.lines.isEmpty() ? 0 : outcome.lines.size() * 20 + 12;
            int ch = headerH + chosenH + textH + fxH + 48 + 14;
            int cx = (W - cw) / 2, cy = Math.max(40, (H - ch) / 2);
            card.setBounds(cx, cy, cw, ch);
            buttons.get(0).setBounds(cx + 20, cy + ch - 52, cw - 40, 40);
        } else if (mode == Mode.ANNOUNCE) {
            int cw = Math.min(600, W - 120);
            bodyLines = wrap(annBody == null ? "" : annBody, fm, cw - 48);
            annHintLines = annHint == null ? new ArrayList<>() : wrap(annHint, fm, cw - 48);
            int headerH = 108;
            int textH = bodyLines.size() * 20 + 8 + annHintLines.size() * 18 + (annHintLines.isEmpty() ? 0 : 8);
            int ch = headerH + textH + 48 + 14;
            int cx = (W - cw) / 2, cy = Math.max(40, (H - ch) / 2);
            card.setBounds(cx, cy, cw, ch);
            int by = cy + ch - 52;
            if (buttons.size() <= 1) {
                buttons.get(0).setBounds(cx + 20, by, cw - 40, 40);
            } else {
                int gap = 10, total = cw - 40 - gap, half = total / 2;
                buttons.get(0).setBounds(cx + 20, by, half, 40);
                buttons.get(1).setBounds(cx + 20 + half + gap, by, total - half, 40);
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
        boolean questCard = (mode == Mode.POPUP || mode == Mode.OUTCOME) && quest != null;
        GameCharacter giver = questCard ? quest.giver : GameCharacter.ADVISOR;
        String speaker = questCard ? quest.giverName() : "Dr. Perla Pereira";
        String role = questCard ? giver.role : "Garnelen-Biologin";
        double px = card.x + 54, py = card.y + 54;
        String avatarKey = questCard ? avatarKeyFor(quest) : GameCharacter.ADVISOR.avatarKey;
        BufferedImage avatar = SvgAvatar.get(avatarKey, 152);
        if (avatar != null) {
            Ellipse2D ring = new Ellipse2D.Double(px - 38, py - 38, 76, 76);
            g.setColor(Palette.PANEL_LIGHT);
            g.fill(ring);
            Shape oldClip = g.getClip();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.clip(ring);
            g.drawImage(avatar, (int) (px - 38), (int) (py - 38), 76, 76, null);
            g.setClip(oldClip);
            g.setColor(giver.color);
            g.setStroke(new BasicStroke(2f));
            g.draw(ring);
        } else {
            g.setColor(Palette.PANEL_LIGHT);
            g.fillOval((int) px - 40, (int) py - 40, 80, 80);
            Icons.portrait(g, giver.portrait, px, py, 76, giver.color);
        }

        int tx = card.x + 104;
        int headW = card.x + card.width - tx - 20;
        g.setFont(Palette.FONT_H2);
        g.setColor(Palette.TEXT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), speaker, headW), tx, card.y + 30);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.ACCENT);
        g.drawString(TextUtil.clip(g.getFontMetrics(), role, headW), tx, card.y + 48);

        // Titel (Popup/Ergebnis/Ansage) bzw. Schrittzahl (Tutorial)
        if (mode == Mode.POPUP || mode == Mode.OUTCOME) {
            g.setFont(Palette.FONT_H2);
            g.setColor(Palette.ACCENT2);
            String title = quest.title + (mode == Mode.OUTCOME ? "  -  ERGEBNIS" : "");
            g.drawString(TextUtil.clip(g.getFontMetrics(), title, card.width - 104 - 20), tx, card.y + 74);
        } else if (mode == Mode.ANNOUNCE) {
            g.setFont(Palette.FONT_H2);
            g.setColor(Palette.MONEY);
            g.drawString(annTitle == null ? "" : annTitle, tx, card.y + 74);
        } else {
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            g.drawString("Tutorial " + (stepIdx + 1) + "/" + stepTotal, tx, card.y + 66);
        }

        // Fließtext
        int yy = card.y + (mode == Mode.TUTORIAL ? 86 : 112);
        int bx = card.x + (mode == Mode.TUTORIAL ? 104 : 24);
        if (mode == Mode.OUTCOME && outcome != null) {
            // Echo der gewählten Option
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            g.drawString(TextUtil.clip(g.getFontMetrics(), "Deine Wahl: \"" + outcome.choice.text + "\"", card.width - 48), bx, yy);
            yy += 20;
        }
        g.setFont(Palette.FONT_BODY);
        g.setColor(Palette.TEXT);
        for (String line : bodyLines) { g.drawString(line, bx, yy); yy += 20; }

        if (mode == Mode.OUTCOME && outcome != null && !outcome.lines.isEmpty()) {
            yy += 8;
            g.setFont(Palette.FONT_BOLD);
            for (ChoiceOutcome.Line l : outcome.lines) {
                Color c = switch (l.kind) {
                    case ChoiceOutcome.GOOD -> Palette.GOOD;
                    case ChoiceOutcome.BAD -> Palette.BAD;
                    case ChoiceOutcome.UNLOCK -> Palette.MONEY;
                    case ChoiceOutcome.TASK -> Palette.ACCENT;
                    default -> Palette.TEXT_DIM;
                };
                g.setColor(c);
                g.fillRoundRect(bx, yy - 9, 9, 9, 3, 3);
                g.setColor(l.kind == ChoiceOutcome.INFO ? Palette.TEXT_DIM : Palette.TEXT);
                g.drawString(TextUtil.clip(g.getFontMetrics(), l.text, card.width - 48 - 18), bx + 16, yy);
                yy += 20;
            }
        }
        if (mode == Mode.ANNOUNCE && !annHintLines.isEmpty()) {
            yy += 6;
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.ACCENT);
            for (String line : annHintLines) { g.drawString(line, bx, yy); yy += 18; }
        }

        g.dispose();
    }

    /**
     * Ordnet den Sprecher einer Quest einem SVG-Avatar zu: zuerst per Stichwort im
     * (oft überschriebenen) Sprecher-Namen, sonst über die Hauptfigur (giver.avatarKey).
     * Figuren ohne eigenen Avatar liefern null -> handgezeichnetes Portrait.
     */
    private static String avatarKeyFor(Quest q) {
        if (q == null) return null;
        String name = q.giverName();
        if (name != null) {
            String n = name.toLowerCase();
            if (n.contains("klausi"))      return "klausi";
            if (n.contains("feuchtwanger")) return "feuchtwanger";
            if (n.contains("krusten & krusten") || n.contains("werbeagentur")) return "krusten";
            if (n.contains("algen"))       return "praktikant";
            if (n.contains("quallmann"))   return "quallmann";
            if (n.contains("schalk"))      return "schalk";
            if (n.contains("lena"))        return "lena";
            if (n.contains("mira"))        return "mira";
            if (n.contains("aquabro"))     return "aquabro";
            if (n.contains("krabbo") || n.contains("chad")) return "chad";
            if (n.contains("olaf"))        return "olaf";
            if (n.contains("pfennig"))     return "pfennig";
            if (n.contains("goldberg"))    return "goldberg";
            if (n.contains("schlemmerle")) return "schlemmerle";
            if (n.contains("akwanov"))     return "ivan";
            if (n.contains("krillkill"))   return "krillkill";
            if (n.contains("perla"))       return "perla";
            if (n.contains("kyle"))        return "kyle";
            if (n.contains("dmitri"))      return "dmitri";
        }
        return q.giver != null ? q.giver.avatarKey : null;
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
