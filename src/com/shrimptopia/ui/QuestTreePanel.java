package com.shrimptopia.ui;

import com.shrimptopia.model.GameState;
import com.shrimptopia.quest.Choice;
import com.shrimptopia.quest.Quest;
import com.shrimptopia.quest.QuestSystem;
import com.shrimptopia.quest.QuestTree;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quest-Baum-Menü: alle Handlungsstränge als farbcodierte Lanes aus Hexagon-Missionen.
 * Erledigte Missionen zeigen ihre Illustration in Farbe, noch nicht freigeschaltete sind
 * ausgegraut; nicht gewählte Dialogoptionen erscheinen grau im Detail-Panel rechts.
 * Querverbindungen (Überschneidungen zwischen Strängen) werden als Farbverlaufs-Kurven
 * gezeichnet. Unten listet der Baum alle quest-gebundenen Freischaltungen mit Quelle.
 */
public class QuestTreePanel extends JComponent {

    private static final int LANE_H = 118, HEX_R = 25, DETAIL_W = 336;

    private final GameFrame frame;
    private final ThemeButton.FlatButton closeBtn;
    private final Rectangle card = new Rectangle();
    private final Rectangle canvas = new Rectangle();

    private int scrollY = 0;
    private int contentH = 0;
    private String hoverId = null, selectedId = null;

    /** Position eines Hex-Knotens in Content-Koordinaten. */
    private static final class Node { final String id; final int cx, cy; Node(String id, int cx, int cy) { this.id = id; this.cx = cx; this.cy = cy; } }
    private final List<Node> nodes = new ArrayList<>();
    private final Map<String, Node> nodeById = new HashMap<>();

    public QuestTreePanel(GameFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(null);
        setVisible(false);
        closeBtn = new ThemeButton.FlatButton("Schließen");
        closeBtn.base = new Color(86, 44, 44);
        closeBtn.addActionListener(e -> close());
        add(closeBtn);

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!card.contains(e.getPoint())) { close(); return; }
                String hit = nodeAt(e.getPoint());
                if (hit != null) { selectedId = hit; repaint(); }
            }
            @Override public void mouseMoved(MouseEvent e) {
                String hit = nodeAt(e.getPoint());
                if (!java.util.Objects.equals(hit, hoverId)) {
                    hoverId = hit;
                    setCursor(Cursor.getPredefinedCursor(hit != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            }
            @Override public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                scrollY = clampScroll(scrollY + e.getWheelRotation() * 48);
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    public void open() {
        scrollY = 0;
        if (selectedId == null) {
            // Erste aktive/anstehende Quest vorauswählen, damit rechts direkt etwas steht
            QuestSystem qs = frame.questSystem();
            for (QuestTree.Line l : QuestTree.lines())
                for (String id : l.quests)
                    if (qs.isTriggered(id) && !qs.isDone(id) || qs.isArmed(id)) { selectedId = id; break; }
        }
        setVisible(true);
        revalidate(); doLayout(); repaint();
    }

    public void close() { setVisible(false); frame.afterQuestTreeClosed(); }

    /** Für Offscreen-Tests: zu einer Content-Position scrollen. */
    public void scrollTo(int y) { scrollY = clampScroll(y); repaint(); }

    @Override public boolean contains(int x, int y) { return isVisible(); }   // modal

    @Override public void doLayout() {
        int W = getWidth(), H = getHeight();
        if (W == 0) return;
        int cw = Math.min(1220, W - 60), ch = Math.min(840, H - 40);
        card.setBounds((W - cw) / 2, (H - ch) / 2, cw, ch);
        canvas.setBounds(card.x + 16, card.y + 58, cw - DETAIL_W - 44, ch - 74);
        Dimension cd = closeBtn.getPreferredSize();
        closeBtn.setBounds(card.x + cw - cd.width - 16, card.y + 12, cd.width, 30);
        layoutNodes();
        scrollY = clampScroll(scrollY);
    }

    private int clampScroll(int v) { return Math.max(0, Math.min(v, Math.max(0, contentH - canvas.height))); }

    /** Berechnet alle Hex-Positionen (Content-Koordinaten) und die Gesamthöhe. */
    private void layoutNodes() {
        nodes.clear(); nodeById.clear();
        int y = 0;
        for (QuestTree.Line line : QuestTree.lines()) {
            int n = line.quests.size();
            int dx = n > 1 ? Math.min(112, (canvas.width - 40 - 2 * HEX_R) / (n - 1)) : 0;
            int x0 = 20 + HEX_R;
            for (int i = 0; i < n; i++) {
                Node node = new Node(line.quests.get(i), x0 + i * dx, y + 52);
                nodes.add(node); nodeById.put(node.id, node);
            }
            y += LANE_H;
        }
        contentH = y + unlockSectionHeight() + 16;
    }

    private int unlockSectionHeight() { return 30 + QuestTree.knownUnlockKeys().size() * 38; }

    private String nodeAt(Point p) {
        if (!canvas.contains(p)) return null;
        int px = p.x - canvas.x, py = p.y - canvas.y + scrollY;
        for (Node n : nodes) {
            double dxx = px - n.cx, dyy = py - n.cy;
            if (dxx * dxx + dyy * dyy <= (HEX_R + 4) * (HEX_R + 4)) return n.id;
        }
        return null;
    }

    // ===================== Zeichnen =====================

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(8, 12, 16, 175));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Palette.PANEL);
        g.fill(new RoundRectangle2D.Double(card.x, card.y, card.width, card.height, 16, 16));
        g.setColor(Palette.ACCENT);
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(card.x, card.y, card.width, card.height, 16, 16));
        g.setFont(Palette.FONT_H1);
        g.setColor(Palette.TEXT);
        g.drawString("Quest-Baum", card.x + 20, card.y + 34);
        g.setFont(Palette.FONT_SMALL);
        g.setColor(Palette.TEXT_DIM);
        g.drawString("Klick auf ein Hexagon für Details - Mausrad zum Scrollen", card.x + 150, card.y + 34);
        g.setColor(Palette.BG_DARK);
        g.fillRect(card.x + 12, card.y + 46, card.width - 24, 1);

        paintCanvas(g);
        paintDetail(g);
        g.dispose();
    }

    private void paintCanvas(Graphics2D g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.clip(new Rectangle(canvas.x, canvas.y, canvas.width, canvas.height));
        g.translate(canvas.x, canvas.y - scrollY);
        QuestSystem qs = frame.questSystem();
        GameState gs = frame.game();

        // 1) Querverbindungen (Überschneidungen) zuerst, unter den Hexagons
        for (QuestTree.Line line : QuestTree.lines())
            for (String id : line.quests) {
                Quest q = qs.get(id);
                if (q == null) continue;
                for (String ref : QuestTree.crossRefs(q, qs)) {
                    Node a = nodeById.get(ref), b = nodeById.get(id);
                    if (a == null || b == null) continue;
                    QuestTree.Line la = QuestTree.lineOf(ref);
                    boolean live = qs.isTriggered(id);
                    Color c1 = withAlpha(la.color, live ? 170 : 80), c2 = withAlpha(line.color, live ? 170 : 80);
                    g.setPaint(new GradientPaint(a.cx, a.cy, c1, b.cx, b.cy, c2));
                    g.setStroke(live ? new BasicStroke(2f)
                        : new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[]{5f, 5f}, 0f));
                    double mx = (a.cx + b.cx) / 2.0;
                    g.draw(new CubicCurve2D.Double(a.cx, a.cy + HEX_R, mx, a.cy + HEX_R + 26, mx, b.cy - HEX_R - 26, b.cx, b.cy - HEX_R));
                }
            }

        // 2) Lanes
        int y = 0;
        for (QuestTree.Line line : QuestTree.lines()) {
            paintLane(g, line, y, qs, gs);
            y += LANE_H;
        }

        // 3) Freischaltungs-Übersicht
        paintUnlocks(g, y, qs, gs);

        g.dispose();

        // Scroll-Hinweis
        if (contentH > canvas.height) {
            Graphics2D gh = (Graphics2D) g0.create();
            gh.setColor(Palette.PANEL_LIGHT);
            int barH = Math.max(30, canvas.height * canvas.height / contentH);
            int barY = canvas.y + (canvas.height - barH) * scrollY / Math.max(1, contentH - canvas.height);
            gh.fillRoundRect(canvas.x + canvas.width - 5, barY, 4, barH, 3, 3);
            gh.dispose();
        }
    }

    private boolean lineDiscovered(QuestTree.Line line, QuestSystem qs) {
        if (line.id.equals("enden")) return true;
        if (line.id.equals("konflikte") && qs.rivalActive()) return true;
        for (String id : line.quests) if (qs.isTriggered(id) || qs.isArmed(id)) return true;
        return false;
    }

    private void paintLane(Graphics2D g, QuestTree.Line line, int y, QuestSystem qs, GameState gs) {
        boolean discovered = lineDiscovered(line, qs);
        // Kopf: Farbchip + Name + Fortschritt
        g.setColor(discovered ? line.color : Palette.TEXT_DIM);
        g.fillRoundRect(20, y + 8, 10, 10, 3, 3);
        g.setFont(Palette.FONT_TINY);
        int done = 0; for (String id : line.quests) if (qs.isDone(id)) done++;
        String head = discovered
            ? line.name.toUpperCase() + "   " + done + "/" + line.quests.size()
            : "???  (NOCH NICHT ENTDECKT)";
        g.drawString(head, 38, y + 17);
        g.setColor(withAlpha(discovered ? line.color : Palette.TEXT_DIM, 40));
        g.fillRect(20, y + 22, canvas.width - 40, 1);

        // Verbinder zwischen aufeinanderfolgenden Missionen
        for (int i = 0; i + 1 < line.quests.size(); i++) {
            Node a = nodeById.get(line.quests.get(i)), b = nodeById.get(line.quests.get(i + 1));
            boolean walked = qs.isTriggered(b.id) || qs.isArmed(b.id);
            g.setColor(walked ? line.color : Palette.PANEL_HOVER);
            g.setStroke(walked ? new BasicStroke(2.4f)
                : new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[]{4f, 4f}, 0f));
            g.draw(new Line2D.Double(a.cx + HEX_R - 4, a.cy, b.cx - HEX_R + 4, b.cy));
        }

        int dx = line.quests.size() > 1
            ? nodeById.get(line.quests.get(1)).cx - nodeById.get(line.quests.get(0)).cx : 112;
        int titleW = Math.max(56, Math.min(96, dx - 6));
        for (String id : line.quests) paintNode(g, line, nodeById.get(id), qs, gs, discovered, titleW);
    }

    private void paintNode(Graphics2D g, QuestTree.Line line, Node n, QuestSystem qs, GameState gs, boolean discovered, int titleW) {
        Quest q = qs.get(n.id);
        if (q == null) return;
        boolean doneQ = qs.isDone(n.id);
        boolean armed = qs.isArmed(n.id);
        boolean pending = qs.isTriggered(n.id) && !doneQ;
        boolean reached = doneQ || armed || pending;
        boolean gray = !reached;
        boolean hover = n.id.equals(hoverId), sel = n.id.equals(selectedId);

        Path2D.Double hex = QuestArt.hexagon(n.cx, n.cy, HEX_R);
        g.setColor(gray ? Palette.PANEL_LIGHT : Icons.darker(line.color, 0.32));
        g.fill(hex);

        if (discovered || line.id.equals("enden"))
            QuestArt.illustration(g, n.id, n.cx, n.cy, HEX_R * 1.5, gray);
        else {
            g.setFont(Palette.FONT_H2); g.setColor(Palette.TEXT_DIM);
            FontMetrics fm = g.getFontMetrics();
            g.drawString("?", n.cx - fm.stringWidth("?") / 2, n.cy + fm.getAscent() / 2 - 2);
        }

        // Rahmen: Stranfarbe wenn erreicht, sonst grau; Auswahl/Hover heller
        g.setColor(sel ? Palette.TEXT : hover ? withAlpha(Palette.TEXT, 190) : gray ? Palette.PANEL_HOVER : line.color);
        g.setStroke(new BasicStroke(sel || pending ? 2.6f : 1.8f));
        g.draw(hex);

        if (pending) {   // Entscheidung offen: pulsierender Doppelrahmen
            g.setColor(withAlpha(Palette.ACCENT2, 200));
            g.setStroke(new BasicStroke(1.6f));
            g.draw(QuestArt.hexagon(n.cx, n.cy, HEX_R + 4));
        }
        if (armed && q.objective != null) {   // Ziel-Fortschritt als Bogen
            double p = q.objective.progress(gs, qs);
            g.setColor(withAlpha(line.color, 230));
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(n.cx - HEX_R - 5, n.cy - HEX_R - 5, 2 * (HEX_R + 5), 2 * (HEX_R + 5), 90, -(int) Math.round(360 * p));
        }
        if (doneQ) {   // Haken-Badge
            g.setColor(Palette.GOOD);
            g.fillOval(n.cx + HEX_R - 11, n.cy - HEX_R - 2, 13, 13);
            g.setColor(Palette.BG_DARK);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Double(n.cx + HEX_R - 8, n.cy - HEX_R + 4.5, n.cx + HEX_R - 5.5, n.cy - HEX_R + 7));
            g.draw(new Line2D.Double(n.cx + HEX_R - 5.5, n.cy - HEX_R + 7, n.cx + HEX_R - 1, n.cy - HEX_R + 1));
        }

        // Überschneidungs-Punkte: Farben der verknüpften Stränge
        int bx = n.cx - HEX_R + 2;
        for (String ref : QuestTree.crossRefs(q, qs)) {
            QuestTree.Line rl = QuestTree.lineOf(ref);
            if (rl == null) continue;
            g.setColor(withAlpha(rl.color, reached ? 255 : 130));
            g.fillOval(bx, n.cy - HEX_R - 4, 7, 7);
            bx += 9;
        }

        // Titel + Options-Punkte
        g.setFont(Palette.FONT_TINY);
        String title = (discovered || line.id.equals("enden")) ? q.title.replace("ENDE: ", "") : "???";
        title = TextUtil.clip(g.getFontMetrics(), title, titleW);
        g.setColor(gray ? Palette.TEXT_DIM : Palette.TEXT);
        g.drawString(title, n.cx - g.getFontMetrics().stringWidth(title) / 2, n.cy + HEX_R + 14);

        int nCh = q.choices.size();
        if (nCh > 0 && discovered) {
            int chosen = qs.chosenChoice(n.id);
            int total = nCh * 12 - 4;
            int x = n.cx - total / 2;
            for (int i = 0; i < nCh; i++) {
                if (doneQ && i == chosen) {
                    g.setColor(line.color);
                    g.fillRoundRect(x, n.cy + HEX_R + 20, 8, 8, 3, 3);
                } else {
                    g.setColor(doneQ ? Palette.PANEL_HOVER : Palette.TEXT_DIM);
                    g.drawRoundRect(x, n.cy + HEX_R + 20, 8, 8, 3, 3);
                }
                x += 12;
            }
        }
    }

    // ---------------- Freischaltungen ----------------

    private void paintUnlocks(Graphics2D g, int y, QuestSystem qs, GameState gs) {
        g.setFont(Palette.FONT_TINY);
        g.setColor(Palette.ACCENT);
        g.drawString("FREISCHALTUNGEN DURCH QUESTS (Gebäude, Tiers, Zonen, Upgrades & Modi)", 20, y + 14);
        g.setColor(withAlpha(Palette.ACCENT, 40));
        g.fillRect(20, y + 20, canvas.width - 40, 1);
        int yy = y + 30;
        Map<String, List<QuestTree.UnlockSource>> src = QuestTree.unlockSources(qs);
        for (String key : QuestTree.knownUnlockKeys()) {
            boolean open = gs.isUnlocked(key) || qs.hasFlag(key);
            g.setColor(open ? withAlpha(Palette.GOOD, 26) : Palette.PANEL_LIGHT);
            g.fillRoundRect(20, yy, canvas.width - 40, 32, 8, 8);
            // Status-Symbol
            if (open) {
                g.setColor(Palette.GOOD);
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Double(30, yy + 16, 35, yy + 21));
                g.draw(new Line2D.Double(35, yy + 21, 44, yy + 10));
            } else {
                g.setColor(Palette.TEXT_DIM);
                g.fillRoundRect(30, yy + 13, 12, 9, 2, 2);
                g.setStroke(new BasicStroke(1.8f));
                g.drawArc(32, yy + 7, 8, 10, 0, 180);
            }
            g.setFont(Palette.FONT_BOLD);
            g.setColor(open ? Palette.TEXT : Palette.TEXT_DIM);
            g.drawString(QuestTree.unlockLabel(key), 54, yy + 14);
            g.setFont(Palette.FONT_SMALL);
            g.setColor(Palette.TEXT_DIM);
            String hint = sourceLabel(key, src, qs);
            g.drawString(TextUtil.clip(g.getFontMetrics(), open ? "freigeschaltet" + (hint != null ? "  (" + hint + ")" : "")
                : (hint != null ? hint : "durch Spielfortschritt"), canvas.width - 110), 54, yy + 28);
            yy += 38;
        }
    }

    private String sourceLabel(String key, Map<String, List<QuestTree.UnlockSource>> src, QuestSystem qs) {
        List<QuestTree.UnlockSource> list = src.get(key);
        if (list == null || list.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        String lastQuest = null;
        for (QuestTree.UnlockSource s : list) {
            if (s.questId.equals(lastQuest)) continue;
            lastQuest = s.questId;
            Quest q = qs.get(s.questId);
            QuestTree.Line l = QuestTree.lineOf(s.questId);
            if (sb.length() > 0) sb.append("  ODER  ");
            sb.append(l != null ? l.name : "?").append(" -> Mission \"").append(q != null ? q.title : s.questId).append("\"");
        }
        return sb.toString();
    }

    // ---------------- Detail-Panel rechts ----------------

    private void paintDetail(Graphics2D g) {
        int x = card.x + card.width - DETAIL_W - 16, y = card.y + 58;
        int w = DETAIL_W, h = card.height - 74;
        g.setColor(Palette.BG_DARK);
        g.fillRoundRect(x, y, w, h, 12, 12);

        QuestSystem qs = frame.questSystem();
        GameState gs = frame.game();
        String id = selectedId != null ? selectedId : hoverId;
        Quest q = id != null ? qs.get(id) : null;
        if (q == null) {
            g.setFont(Palette.FONT_BODY); g.setColor(Palette.TEXT_DIM);
            int yy = y + 40;
            for (String line : wrap("Wähle links eine Mission aus, um Details, alle Dialogoptionen (gewählt / nicht gewählt) und Freischaltungen zu sehen.", g.getFontMetrics(), w - 40))
                { g.drawString(line, x + 20, yy); yy += 20; }
            return;
        }

        QuestTree.Line line = QuestTree.lineOf(q.id);
        Color lc = line != null ? line.color : Palette.ACCENT;
        boolean doneQ = qs.isDone(q.id), armed = qs.isArmed(q.id);
        boolean pending = qs.isTriggered(q.id) && !doneQ;
        boolean reached = doneQ || armed || pending;
        boolean discovered = line == null || lineDiscovered(line, qs) || line.id.equals("enden");

        Graphics2D gd = (Graphics2D) g.create();
        gd.clip(new Rectangle(x, y, w, h));
        int yy = y + 26;

        // Strang + Titel
        gd.setColor(lc);
        gd.fillRoundRect(x + 16, yy - 12, 10, 10, 3, 3);
        gd.setFont(Palette.FONT_TINY);
        gd.drawString(line != null ? line.name.toUpperCase() : "", x + 32, yy - 3);
        yy += 12;
        gd.setFont(Palette.FONT_H2);
        gd.setColor(discovered ? Palette.TEXT : Palette.TEXT_DIM);
        for (String l : wrap(discovered ? q.title : "??? - noch nicht entdeckt", gd.getFontMetrics(), w - 32))
            { gd.drawString(l, x + 16, yy); yy += 18; }
        if (discovered) {
            gd.setFont(Palette.FONT_SMALL);
            gd.setColor(Palette.ACCENT);
            gd.drawString(q.giverName(), x + 16, yy); yy += 16;
        }

        // Status
        gd.setFont(Palette.FONT_BOLD);
        if (doneQ)        { gd.setColor(Palette.GOOD);     gd.drawString("Erledigt", x + 16, yy + 4); }
        else if (pending) { gd.setColor(Palette.ACCENT2);  gd.drawString("Entscheidung steht an!", x + 16, yy + 4); }
        else if (armed)   { gd.setColor(lc);               gd.drawString("Aktives Ziel", x + 16, yy + 4); }
        else              { gd.setColor(Palette.TEXT_DIM); gd.drawString("Noch nicht freigeschaltet", x + 16, yy + 4); }
        yy += 12;

        if (armed && q.objective != null) {
            double p = q.objective.progress(gs, qs);
            gd.setFont(Palette.FONT_SMALL); gd.setColor(Palette.TEXT);
            gd.drawString((q.objectiveText != null ? q.objectiveText : "Ziel") + "  (" + q.objective.describe(gs, qs) + ")", x + 16, yy + 12);
            gd.setColor(Palette.PANEL_LIGHT);
            gd.fillRoundRect(x + 16, yy + 18, w - 32, 8, 4, 4);
            gd.setColor(p >= 1 ? Palette.GOOD : lc);
            gd.fillRoundRect(x + 16, yy + 18, (int) ((w - 32) * Math.min(1, p)), 8, 4, 4);
            yy += 34;
        } else if (doneQ && q.objectiveText != null) {
            gd.setFont(Palette.FONT_SMALL); gd.setColor(Palette.TEXT_DIM);
            gd.drawString("Ziel war: " + q.objectiveText, x + 16, yy + 12); yy += 20;
        }

        // Beschreibung (nur wenn erreicht - sonst Spoiler)
        gd.setFont(Palette.FONT_SMALL);
        gd.setColor(reached ? Palette.TEXT : Palette.TEXT_DIM);
        List<String> body = wrap(reached ? q.body : "Diese Mission wurde noch nicht freigeschaltet. Spiele den Handlungsstrang weiter, um sie zu erreichen.", gd.getFontMetrics(), w - 32);
        int maxBody = 7;
        for (int i = 0; i < Math.min(body.size(), maxBody); i++) { gd.drawString(i == maxBody - 1 && body.size() > maxBody ? body.get(i) + " ..." : body.get(i), x + 16, yy + 12); yy += 15; }
        yy += 8;

        // Überschneidungen
        java.util.Set<String> refs = QuestTree.crossRefs(q, qs);
        if (!refs.isEmpty()) {
            gd.setFont(Palette.FONT_TINY); gd.setColor(Palette.TEXT_DIM);
            gd.drawString("VERKNÜPFT MIT:", x + 16, yy + 8);
            int rx = x + 105;
            for (String ref : refs) {
                QuestTree.Line rl = QuestTree.lineOf(ref);
                if (rl == null) continue;
                gd.setColor(rl.color);
                gd.fillRoundRect(rx, yy, 8, 8, 3, 3);
                gd.setFont(Palette.FONT_TINY);
                gd.drawString(rl.name, rx + 11, yy + 8);
                rx += 15 + gd.getFontMetrics().stringWidth(rl.name);
                if (rx > x + w - 60) break;
            }
            yy += 20;
        }

        // Optionen
        gd.setFont(Palette.FONT_TINY); gd.setColor(Palette.ACCENT);
        gd.drawString("OPTIONEN / HANDLUNGSSTRÄNGE", x + 16, yy + 8);
        yy += 14;
        int chosen = qs.chosenChoice(q.id);
        for (int i = 0; i < q.choices.size(); i++) {
            Choice c = q.choices.get(i);
            boolean isChosen = doneQ && i == chosen;
            boolean grayed = !isChosen;                         // nicht gewählt ODER noch nicht freigeschaltet
            List<String> tl = wrap(c.text, getFontMetrics(Palette.FONT_SMALL), w - 60);
            List<String> extra = new ArrayList<>();
            for (String u : QuestTree.unlocksOfChoice(c)) extra.add("Schaltet frei: " + u);
            if (c.nextQuestId != null) {
                Quest nx = qs.get(c.nextQuestId);
                extra.add("-> Folge-Mission: " + (nx != null && (qs.isTriggered(nx.id) || qs.isArmed(nx.id) || discovered) ? nx.title : "???"));
            }
            int bh = 10 + tl.size() * 15 + extra.size() * 14;
            gd.setColor(isChosen ? withAlpha(lc, 34) : Palette.PANEL);
            gd.fillRoundRect(x + 16, yy, w - 32, bh, 8, 8);
            gd.setColor(isChosen ? lc : Palette.PANEL_HOVER);
            gd.setStroke(new BasicStroke(isChosen ? 1.8f : 1f));
            gd.drawRoundRect(x + 16, yy, w - 32, bh, 8, 8);
            int ty = yy + 15;
            gd.setFont(Palette.FONT_SMALL);
            gd.setColor(grayed ? Palette.TEXT_DIM : Palette.TEXT);
            for (String l : tl) { gd.drawString(l, x + 26, ty); ty += 15; }
            gd.setFont(Palette.FONT_TINY);
            for (String e : extra) {
                gd.setColor(grayed ? Palette.TEXT_DIM : e.startsWith("Schaltet") ? Palette.MONEY : Palette.ACCENT);
                gd.drawString(TextUtil.clip(gd.getFontMetrics(), e, w - 60), x + 26, ty); ty += 14;
            }
            if (isChosen) {
                gd.setFont(Palette.FONT_TINY); gd.setColor(lc);
                String tag = "GEWÄHLT";
                gd.drawString(tag, x + w - 26 - gd.getFontMetrics().stringWidth(tag), yy + 13);
            } else if (doneQ) {
                gd.setFont(Palette.FONT_TINY); gd.setColor(Palette.TEXT_DIM);
                String tag = "nicht gewählt";
                gd.drawString(tag, x + w - 26 - gd.getFontMetrics().stringWidth(tag), yy + 13);
            }
            yy += bh + 6;
        }
        gd.dispose();
    }

    private static Color withAlpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }

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
