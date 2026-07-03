package com.shrimptopia.ui;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.WorkerPolicy;
import com.shrimptopia.model.Zone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/** Linke Seitenleiste: Logo, zonengefiltertes Baumenü, Arbeiter-Politik und Abriss. */
public class SidePanel extends JPanel {

    private final GameFrame frame;
    private final JPanel list = new JPanel();
    private final List<BuildButton> buttons = new ArrayList<>();
    private final List<PolicyButton> policyButtons = new ArrayList<>();
    private ThemeButton.FlatToggle demolishBtn;

    public SidePanel(GameFrame frame) {
        this.frame = frame;
        setBackground(Palette.PANEL);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Palette.BG_DARK));
        setPreferredSize(new Dimension(252, 100));
        setLayout(new BorderLayout());
        add(header(), BorderLayout.NORTH);

        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(list,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Palette.PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        add(footer(), BorderLayout.SOUTH);
        rebuildList();
    }

    private JComponent header() {
        JComponent h = new JComponent() {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(Palette.PANEL_LIGHT); g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Palette.BG_DARK); g.fillRect(0, getHeight() - 1, getWidth(), 1);
                Icons.resource(g, com.shrimptopia.model.IconKind.SHRIMP, 30, 32, 40, Palette.SHRIMP);
                g.setFont(Palette.FONT_H1); g.setColor(Palette.TEXT);
                g.drawString("ShrimpTopia", 56, 30);
                g.setFont(Palette.FONT_SMALL); g.setColor(Palette.ACCENT);
                g.drawString("Indoor Shrimp Farming Tycoon", 56, 47);
            }
        };
        h.setPreferredSize(new Dimension(252, 62));
        return h;
    }

    private JComponent footer() {
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));

        JLabel pol = new JLabel("ARBEITER-POLITIK");
        pol.setFont(Palette.FONT_TINY); pol.setForeground(Palette.ACCENT);
        pol.setAlignmentX(LEFT_ALIGNMENT);
        f.add(pol);
        JPanel grid = new JPanel(new GridLayout(2, 2, 4, 4));
        grid.setOpaque(false); grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        for (WorkerPolicy p : WorkerPolicy.values()) {
            PolicyButton pb = new PolicyButton(p);
            policyButtons.add(pb);
            grid.add(pb);
        }
        f.add(grid);
        f.add(Box.createVerticalStrut(6));

        demolishBtn = new ThemeButton.FlatToggle("Abriss-Modus").base(new Color(86, 44, 44)).accent(Palette.BAD);
        demolishBtn.setAlignmentX(LEFT_ALIGNMENT);
        demolishBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        demolishBtn.addActionListener(e -> frame.toggleDemolish());
        f.add(demolishBtn);

        JLabel hint = new JLabel("Rechtsklick = Auswahl aufheben");
        hint.setFont(Palette.FONT_SMALL); hint.setForeground(Palette.TEXT_DIM);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        f.add(Box.createVerticalStrut(4));
        f.add(hint);
        return f;
    }

    /** Baut das Baumenü für die aktuelle Zone neu auf. */
    public void rebuildList() {
        list.removeAll();
        buttons.clear();
        Zone zone = frame.currentZone();
        for (BuildingType t : BuildingType.buildable()) {
            if (t.zone() != zone) continue;
            BuildButton bb = new BuildButton(t);
            buttons.add(bb);
            list.add(bb);
            list.add(Box.createVerticalStrut(6));
        }
        list.revalidate();
        list.repaint();
    }

    public void refresh() {
        double money = frame.game().getMoney();
        BuildingType sel = frame.selectedType();
        for (BuildButton b : buttons) {
            b.affordable = money >= b.type.cost;
            b.unlocked = frame.game().isBuildingUnlocked(b.type);
            b.selected = (frame.tool() == GameFrame.Tool.PLACE && sel == b.type);
            b.repaint();
        }
        for (PolicyButton p : policyButtons) p.repaint();
        demolishBtn.setSelected(frame.tool() == GameFrame.Tool.DEMOLISH);
        demolishBtn.setText(frame.tool() == GameFrame.Tool.DEMOLISH ? "Abriss: AKTIV" : "Abriss-Modus");
    }

    // ---------------- Arbeiter-Politik-Knopf ----------------
    private class PolicyButton extends JComponent {
        final WorkerPolicy policy; boolean hover;
        PolicyButton(WorkerPolicy p) {
            this.policy = p;
            setPreferredSize(new Dimension(100, 30));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(p.requiresFlag, frame.questSystem());
            setToolTipText("<html><body style='width:220px'><b>" + p.displayName + "</b><br>" + p.desc
                + (hint != null ? "<br><br><b>Freischaltung:</b> " + hint : "") + "</body></html>");
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    if (!frame.game().setWorkerPolicy(policy)) frame.refreshAll();
                    else frame.refreshAll();
                }
            });
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean cur = frame.game().getWorkerPolicy() == policy;
            boolean locked = policy.requiresFlag != null && !frame.game().isUnlocked(policy.requiresFlag);
            int w = getWidth(), h = getHeight();
            g.setColor(cur ? new Color(0, 90, 84) : hover && !locked ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT);
            g.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
            if (cur) { g.setColor(Palette.ACCENT); g.setStroke(new BasicStroke(1.6f)); g.drawRoundRect(1, 1, w - 3, h - 3, 8, 8); }
            g.setFont(Palette.FONT_SMALL); g.setColor(locked ? Palette.TEXT_DIM : Palette.TEXT);
            String label = shortPolicy(policy) + (locked ? " (?)" : "");
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, (w - fm.stringWidth(label)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }

    private static String shortPolicy(WorkerPolicy p) {
        return switch (p) {
            case REGULAR -> "Regulär"; case OVERTIME -> "Überstd."; case UNION -> "Gewerksch."; case BOOTCAMP -> "Bootcamp";
        };
    }

    // ---------------- Gebäude-Knopf ----------------
    private class BuildButton extends JComponent {
        final BuildingType type;
        boolean affordable = true, selected = false, unlocked = true, hover = false;
        BuildButton(BuildingType type) {
            this.type = type;
            setPreferredSize(new Dimension(220, 58));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
            setAlignmentX(LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(type.unlockFlag(), frame.questSystem());
            setToolTipText("<html><body style='width:220px'>" + type.description
                + (hint != null ? "<br><br><b>Freischaltung:</b> " + hint : "") + "</body></html>");
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    if (frame.game().isBuildingUnlocked(type)) frame.selectBuildType(type);
                }
            });
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            Color bg = selected ? new Color(0, 90, 84) : hover && unlocked ? Palette.PANEL_HOVER : Palette.PANEL_LIGHT;
            g.setColor(bg); g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
            if (selected) { g.setColor(Palette.ACCENT); g.setStroke(new BasicStroke(2f)); g.drawRoundRect(1, 1, w - 3, h - 3, 10, 10); }
            g.setColor(Icons.darker(type.color, unlocked ? 0.85 : 0.5));
            g.fillRoundRect(8, 8, 42, 42, 9, 9);
            Icons.building(g, type.icon, 29, 28, 30);
            g.setFont(Palette.FONT_BOLD);
            g.setColor(unlocked ? (affordable ? Palette.TEXT : Palette.TEXT_DIM) : Palette.TEXT_DIM);
            g.drawString(TextUtil.clip(g.getFontMetrics(), type.displayName, w - 58 - 10), 58, 24);
            g.setFont(Palette.FONT_SMALL); g.setColor(Palette.TEXT_DIM);
            String sub = tagline(type);
            if (!unlocked) {
                String hint = com.shrimptopia.quest.QuestTree.unlockHintFor(type.unlockFlag(), frame.questSystem());
                sub = hint != null ? hint : "noch gesperrt";
            }
            g.drawString(TextUtil.clip(g.getFontMetrics(), sub, w - 58 - 52), 58, 43);
            g.setFont(Palette.FONT_BOLD);
            String cost = unlocked ? String.format("%,d", type.cost) : "Schloss";
            int cw = g.getFontMetrics().stringWidth(cost);
            g.setColor(unlocked ? (affordable ? Palette.MONEY : Palette.BAD) : Palette.TEXT_DIM);
            g.drawString(cost, w - cw - 10, 44);
            g.dispose();
        }
    }

    private static String tagline(BuildingType t) {
        return switch (t) {
            case OLD_GENERATOR -> "+12 Strom, laut";
            case RAIN_BARREL   -> "+4 Wasser";
            case ALGAE_BUCKET  -> "+2,5 Futter";
            case GARAGE_TANK   -> "+1,2 Shrimp/Tag";
            case CAMPER        -> "+3 Arbeiter";
            case YARD_SALE     -> "verkauft 3 Standard/Tag";
            case POWER_PLANT  -> "+45 Strom";
            case SOLAR_ROOF   -> "+28 Strom, sauber";
            case WATER_PLANT  -> "+14 Wasser, -8 Strom";
            case WATER_HUB    -> "+40 Wasser";
            case ALGAE_FARM   -> "+9 Futter, -6 Strom";
            case SHRIMP_TANK  -> "+5 Shrimp/Tag";
            case HOUSING      -> "+6 Arbeiter";
            case SALES_OFFICE -> "verkauft Standard/Bio";
            case LAB          -> "+12% Preis, Forschung";
            case GENLAB       -> "Designer-Shrimps";
            case RESTAURANT   -> "Premium-Verkauf, +Ruf";
            case EXPORT_DOCK  -> "Export höherer Tiers";
            case MILITARY_DEPOT -> "kauft Protein/Kampf";
            case BLACK_MARKET -> "Top-Tiers, -Ruf";
            case VISITOR_CENTER -> "viel Reputation";
            case ZEN_GARDEN   -> "+Reputation, Deko";
            case SHELL_PRESS         -> "+6 Schalen/Tag";
            case SHRIMPBOOST_FACTORY -> "Shrimp+Schalen zu Boost";
            case BOOST_STAND         -> "verkauft Boost ~90/Dose";
            case ROBOT_WORKS         -> "Roboter = je +2 Arbeiter";
            case KRILL_BARRACKS      -> "+Armee-Stärke";
            default           -> "";
        };
    }
}
