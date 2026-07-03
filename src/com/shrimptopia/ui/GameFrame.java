package com.shrimptopia.ui;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.Building;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.IconKind;
import com.shrimptopia.model.Zone;
import com.shrimptopia.quest.QuestSystem;
import com.shrimptopia.tutorial.Tutorial;
import com.shrimptopia.tutorial.TutorialStep;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/** Hauptfenster: hält Spielzustand, Quests, Tutorial, Zonen, Inspektor und die Spielschleife. */
public class GameFrame extends JFrame {

    public enum Tool { NONE, PLACE, DEMOLISH }

    private GameState game;
    private QuestSystem questSystem;
    private Tutorial tutorial;

    private Zone currentZone = Zone.PRODUKTION;
    private Building selectedBuilding;
    private Tool tool = Tool.NONE;
    private BuildingType selectedType;

    private final Timer timer;
    private final Timer animTimer;
    private boolean userPaused = false;
    private int speed = 1;

    private TopBar topBar;
    private SidePanel sidePanel;
    private MapPanel mapPanel;
    private LogPanel logPanel;
    private InspectorPanel inspector;
    private QuestLogPanel questLog;
    private ZoneTabs zoneTabs;
    private OverlayHost overlay;
    private AlmanacPanel almanac;
    private QuestTreePanel questTree;

    public GameFrame() {
        super("ShrimpTopia v2 - Indoor Shrimp Farming Tycoon");
        game = new GameState();
        questSystem = new QuestSystem();
        tutorial = new Tutorial();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(makeAppIcon());
        getContentPane().setBackground(Palette.BG_DARK);
        setLayout(new BorderLayout());

        topBar = new TopBar(this);
        sidePanel = new SidePanel(this);
        mapPanel = new MapPanel(this);
        logPanel = new LogPanel(this);
        inspector = new InspectorPanel(this);
        questLog = new QuestLogPanel(this);
        zoneTabs = new ZoneTabs(this);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Palette.BG_DARK);
        center.add(zoneTabs, BorderLayout.NORTH);
        center.add(mapPanel, BorderLayout.CENTER);

        JPanel east = new JPanel(new BorderLayout());
        east.setBackground(Palette.PANEL);
        east.add(questLog, BorderLayout.NORTH);
        east.add(inspector, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);
        add(logPanel, BorderLayout.SOUTH);

        overlay = new OverlayHost(this);
        setGlassPane(overlay);

        almanac = new AlmanacPanel(this);
        getLayeredPane().add(almanac, JLayeredPane.MODAL_LAYER);

        questTree = new QuestTreePanel(this);
        getLayeredPane().add(questTree, JLayeredPane.MODAL_LAYER);

        installKeyBindings();

        timer = new Timer(delayForSpeed(speed), this::onTick);
        animTimer = new Timer(60, e -> { mapPanel.advanceAnim(); logPanel.repaint(); });

        refreshAll();
        pack();
        setMinimumSize(new Dimension(1360, 820));
        setSize(1440, 900);
        setLocationRelativeTo(null);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (almanac != null && almanac.isVisible())
                    almanac.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
                if (questTree != null && questTree.isVisible())
                    questTree.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
            }
        });
        animTimer.start();
        updateOverlays();   // startet mit dem Tutorial
    }

    private void onTick(ActionEvent e) {
        game.tick();
        questSystem.update(game);
        refreshAll();
        updateOverlays();
    }

    private int delayForSpeed(int s) { return switch (s) { case 3 -> 220; case 2 -> 450; default -> 900; }; }

    public void refreshAll() {
        topBar.refresh();
        sidePanel.refresh();
        zoneTabs.refresh();
        mapPanel.repaint();
        logPanel.refresh();
        inspector.refresh();
        questLog.refresh();
    }

    // ===================== Overlay-Steuerung (Tutorial / Popups) =====================

    /** true, wenn der Almanach vom Tutorial (Tier-/Marketing-Schritt) geöffnet wurde - nicht vom Spieler. */
    private boolean tutorialAlmanac = false;
    private int tutorialAlmanacTab = -1;
    /** Konsequenzen der letzten Entscheidung (Ergebnis-Karte) und wartende Freischalt-Ansage. */
    private com.shrimptopia.quest.ChoiceOutcome pendingOutcome;
    private String[] pendingAnnouncement;

    private void updateOverlays() {
        // Almanach-Schritte des Tutorials (Tiers, Marketing): Menü automatisch auf-/zuklappen
        TutorialStep cur = tutorial.isActive() ? tutorial.current() : null;
        boolean wantsAlmanac = cur != null && cur.region == TutorialStep.Region.ALMANAC;
        if (wantsAlmanac && (!almanac.isVisible() || tutorialAlmanacTab != cur.almanacTab)) {
            openAlmanac(cur.almanacTab);
            tutorialAlmanac = true;
            tutorialAlmanacTab = cur.almanacTab;
        } else if (!wantsAlmanac && tutorialAlmanac) {
            tutorialAlmanac = false;
            tutorialAlmanacTab = -1;
            if (almanac.isVisible()) almanac.setVisible(false);
        }

        if (tutorial.isActive()) {
            TutorialStep s = tutorial.current();
            overlay.showTutorial(s, regionRect(s), tutorial.index(), tutorial.total());
        } else if (pendingOutcome != null) {
            overlay.showOutcome(pendingOutcome);
        } else if (nextAnnouncement() != null) {
            overlay.showAnnouncement(pendingAnnouncement[0], pendingAnnouncement[1]);
        } else if (questSystem.hasPending()) {
            overlay.showPopup(questSystem.peek());
        } else {
            overlay.hideOverlay();
        }
        applyTimerState();
    }

    private String[] nextAnnouncement() {
        if (pendingAnnouncement == null) pendingAnnouncement = game.pollAnnouncement();
        return pendingAnnouncement;
    }

    public void dismissOutcome()      { pendingOutcome = null; refreshAll(); updateOverlays(); }
    public void dismissAnnouncement() { pendingAnnouncement = null; refreshAll(); updateOverlays(); }

    private boolean overlayShowing() { return overlay.mode() != OverlayHost.Mode.NONE; }

    private void applyTimerState() {
        if (userPaused || overlayShowing() || (almanac != null && almanac.isVisible())
            || (questTree != null && questTree.isVisible())) timer.stop();
        else timer.start();
        topBar.setPausedVisual(userPaused);
    }

    private Rectangle regionRect(TutorialStep s) {
        if (s == null) return null;
        if (s.region == TutorialStep.Region.ALMANAC)
            return almanac.isVisible()
                ? SwingUtilities.convertRectangle(almanac, almanac.cardBounds(), overlay)
                : null;
        JComponent c = switch (s.region) {
            case BUILD_MENU -> sidePanel;
            case MAP -> mapPanel;
            case ZONE_TABS -> zoneTabs;
            case INSPECTOR -> inspector;
            case POPUP -> null;
            default -> topBar;   // TOPBAR, RESOURCE, CONTROLS
        };
        if (c == null) return null;
        return SwingUtilities.convertRectangle(c, new Rectangle(0, 0, c.getWidth(), c.getHeight()), overlay);
    }

    public void tutorialAdvance() { tutorial.next(); refreshAll(); updateOverlays(); }
    public void tutorialSkip()    { tutorial.skip(); refreshAll(); updateOverlays(); }
    public void resolveQuest(int i) {
        com.shrimptopia.quest.ChoiceOutcome out = questSystem.resolve(game, i);
        if (out != null && !out.isEmpty()) pendingOutcome = out;
        refreshAll();
        updateOverlays();
    }

    // ===================== Steuerung =====================

    public void setPaused(boolean p) { userPaused = p; applyTimerState(); }
    public void setSpeed(int s) { speed = s; timer.setDelay(delayForSpeed(s)); applyTimerState(); }

    public void restartGame() {
        game = new GameState();
        questSystem = new QuestSystem();
        tutorial = new Tutorial();
        currentZone = Zone.PRODUKTION;
        selectedBuilding = null;
        tool = Tool.NONE; selectedType = null;
        pendingOutcome = null;
        pendingAnnouncement = null;
        userPaused = false;
        speed = 1;
        topBar.selectSpeed(1);
        inspector.setBuilding(null);
        sidePanel.rebuildList();
        timer.setDelay(delayForSpeed(speed));
        refreshAll();
        updateOverlays();
    }

    public void selectBuildType(BuildingType t) { tool = Tool.PLACE; selectedType = t; selectedBuilding = null; inspector.setBuilding(null); refreshAll(); }
    public void toggleDemolish() { if (tool == Tool.DEMOLISH) clearTool(); else { tool = Tool.DEMOLISH; selectedType = null; refreshAll(); } }
    public void clearTool() { tool = Tool.NONE; selectedType = null; refreshAll(); }

    public void openAlmanac(int tab) {
        if (questTree.isVisible()) questTree.setVisible(false);
        almanac.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
        almanac.open(tab);
        applyTimerState();
    }
    // updateOverlays() öffnet den Almanach im Tier-Tutorial-Schritt ggf. direkt wieder
    public void afterAlmanacClosed() { refreshAll(); updateOverlays(); }

    public void openQuestTree() {
        if (almanac.isVisible()) almanac.setVisible(false);
        questTree.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
        getLayeredPane().moveToFront(questTree);
        questTree.open();
        applyTimerState();
    }
    public void afterQuestTreeClosed() { applyTimerState(); refreshAll(); }

    public void selectBuilding(Building b) {
        if (b != null && b.type == BuildingType.HEADQUARTERS) { openAlmanac(AlmanacPanel.TAB_HQ); return; }
        selectedBuilding = b;
        inspector.setBuilding(b);
        if (b != null) tutorial.onInspectorOpened();
        refreshAll();
        updateOverlays();
    }

    public void tryPlace(int col, int row) {
        if (selectedType == null) return;
        if (!game.isBuildingUnlocked(selectedType)) return;
        if (selectedType.zone() != currentZone) {
            game.log(selectedType.displayName + " gehört in eine andere Zone (" + selectedType.zone().displayName + ").", GameState.LOG_WARN);
            refreshAll(); return;
        }
        if (!game.canPlace(currentZone, col, row)) return;
        if (game.getMoney() < selectedType.cost) {
            game.log("Zu wenig Geld für " + selectedType.displayName + ".", GameState.LOG_WARN);
            refreshAll(); return;
        }
        game.place(selectedType, currentZone, col, row, true);
        game.log(selectedType.displayName + " gebaut.", GameState.LOG_INFO);
        tutorial.onBuilt(selectedType);
        refreshAll();
        updateOverlays();
    }

    public void tryDemolish(int col, int row) {
        Building b = game.demolish(currentZone, col, row, true);
        if (b != null) {
            if (selectedBuilding == b) { selectedBuilding = null; inspector.setBuilding(null); }
            game.log(b.type.displayName + " abgerissen (50% erstattet).", GameState.LOG_INFO);
            refreshAll();
        }
    }

    public void demolishSelected() {
        if (selectedBuilding == null) return;
        Building b = game.demolish(selectedBuilding.zone, selectedBuilding.col, selectedBuilding.row, true);
        if (b != null) { game.log(b.type.displayName + " abgerissen (50% erstattet).", GameState.LOG_INFO); selectedBuilding = null; inspector.setBuilding(null); refreshAll(); }
    }

    public void setZone(Zone z) {
        if (!game.isZoneUnlocked(z)) return;
        currentZone = z;
        selectedBuilding = null;
        inspector.setBuilding(null);
        sidePanel.rebuildList();
        refreshAll();
    }

    // ===================== Tastenkürzel =====================

    private void installKeyBindings() {
        JComponent root = getRootPane();
        bind(root, KeyEvent.VK_ESCAPE, "cancel", this::clearTool);
        bind(root, KeyEvent.VK_SPACE, "pause", () -> setPaused(!userPaused));
        bind(root, KeyEvent.VK_1, "s1", () -> setSpeed(1));
        bind(root, KeyEvent.VK_2, "s2", () -> setSpeed(2));
        bind(root, KeyEvent.VK_3, "s3", () -> setSpeed(3));
        bind(root, KeyEvent.VK_Q, "questtree", () -> {
            if (questTree.isVisible()) questTree.close(); else openQuestTree();
        });
    }
    private void bind(JComponent c, int key, String name, Runnable action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
        c.getActionMap().put(name, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { action.run(); } });
    }

    // ===================== Getter für Panels =====================

    // Test-Hooks (für --guitest)
    public void debugRefreshOverlay() { updateOverlays(); }
    public void debugForceQuest(String id) { questSystem.forceStart(id); refreshAll(); updateOverlays(); }
    public void debugScrollQuestTree(int y) { questTree.scrollTo(y); }

    public GameState game()            { return game; }
    public QuestSystem questSystem()    { return questSystem; }
    public Tool tool()                 { return tool; }
    public BuildingType selectedType() { return selectedType; }
    public Zone currentZone()          { return currentZone; }
    public Building selectedBuilding() { return selectedBuilding; }

    private static Image makeAppIcon() {
        int s = 64;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Palette.PANEL_LIGHT);
        g.fill(new RoundRectangle2D.Double(2, 2, s - 4, s - 4, 16, 16));
        Icons.resource(g, IconKind.SHRIMP, s / 2.0, s / 2.0, s * 0.7, Palette.SHRIMP);
        g.dispose();
        return img;
    }
}
