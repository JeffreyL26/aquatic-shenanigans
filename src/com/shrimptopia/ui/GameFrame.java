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
    private MapPanel mapPanel;
    private LogPanel logPanel;
    private InspectorPanel inspector;
    private QuestLogPanel questLog;
    private SideBar sideBar;
    private JPanel centerPanel;
    private OverlayHost overlay;
    private AlmanacPanel almanac;
    private QuestTreePanel questTree;
    private BuildMenuPanel buildMenu;
    private MinigamePanel minigame;

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
        mapPanel = new MapPanel(this);
        logPanel = new LogPanel(this);
        inspector = new InspectorPanel(this);
        questLog = new QuestLogPanel(this);
        sideBar = new SideBar(this);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Palette.BG_DARK);
        // Tropico-Stil: links die feste Seitenleiste (Bauen, Abriss, Zonen-Wechsel)
        center.add(sideBar, BorderLayout.WEST);
        // Karte in einer Scrollpane: passt der Bereich (kleiner/skalierter Bildschirm) nicht,
        // wird das Gitter gescrollt statt abgeschnitten.
        JScrollPane mapScroll = new JScrollPane(mapPanel);
        mapScroll.setBorder(BorderFactory.createEmptyBorder());
        mapScroll.getViewport().setBackground(Palette.BG_DARK);
        mapScroll.setBackground(Palette.BG_DARK);
        ThemeScrollBar.apply(mapScroll);
        mapScroll.getVerticalScrollBar().setUnitIncrement(32);
        mapScroll.getHorizontalScrollBar().setUnitIncrement(32);
        center.add(mapScroll, BorderLayout.CENTER);
        centerPanel = center;

        JPanel east = new JPanel(new BorderLayout());
        east.setBackground(Palette.PANEL);
        east.add(questLog, BorderLayout.NORTH);
        east.add(inspector, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);
        add(logPanel, BorderLayout.SOUTH);

        overlay = new OverlayHost(this);
        setGlassPane(overlay);

        almanac = new AlmanacPanel(this);
        getLayeredPane().add(almanac, JLayeredPane.MODAL_LAYER);

        questTree = new QuestTreePanel(this);
        getLayeredPane().add(questTree, JLayeredPane.MODAL_LAYER);

        buildMenu = new BuildMenuPanel(this);
        getLayeredPane().add(buildMenu, JLayeredPane.POPUP_LAYER);

        minigame = new MinigamePanel(this);
        getLayeredPane().add(minigame, JLayeredPane.DRAG_LAYER);   // über allem außer Glass-Pane

        installKeyBindings();

        timer = new Timer(delayForSpeed(speed), this::onTick);
        animTimer = new Timer(60, e -> { mapPanel.advanceAnim(); logPanel.repaint(); buildMenu.tickGlow(); sideBar.tickGlow(); topBar.tickCritical(); });

        refreshAll();
        pack();
        setMinimumSize(new Dimension(1040, 640));
        // Wunschgröße 1440x900, aber nie größer als der nutzbare Bildschirm (abzüglich Taskleiste),
        // damit das Fenster - und damit die Karte - nicht unten/rechts abgeschnitten wird.
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1440, screen.width), Math.min(900, screen.height));
        setLocationRelativeTo(null);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (almanac != null && almanac.isVisible())
                    almanac.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
                if (questTree != null && questTree.isVisible())
                    questTree.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
                if (minigame != null && minigame.isVisible())
                    minigame.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
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
        sideBar.refresh();
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
        // Läuft ein Minigame, pausiert alles andere - Popups warten, bis es vorbei ist.
        if (minigame.isVisible()) {
            overlay.hideOverlay();
            applyTimerState();
            return;
        }
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

        // Kein Overlay mehr offen? Dann wartendes Quest-Minigame starten.
        if (!overlayShowing() && !minigame.isVisible()) {
            String[] mg = questSystem.pollMinigame();
            if (mg != null) {
                minigame.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
                getLayeredPane().moveToFront(minigame);
                if (minigame.open(mg[0], Double.parseDouble(mg[1]))) applyTimerState();
            }
        }
    }

    /** Nach Ende eines Minigames: Spiel fortsetzen, wartende Popups anzeigen. */
    public void onMinigameClosed() { refreshAll(); updateOverlays(); }
    public boolean minigameActive() { return minigame.isVisible(); }

    private String[] nextAnnouncement() {
        if (pendingAnnouncement == null) pendingAnnouncement = game.pollAnnouncement();
        return pendingAnnouncement;
    }

    public void dismissOutcome()      { pendingOutcome = null; refreshAll(); updateOverlays(); }
    public void dismissAnnouncement() { pendingAnnouncement = null; refreshAll(); updateOverlays(); }

    private boolean overlayShowing() { return overlay.mode() != OverlayHost.Mode.NONE; }

    private void applyTimerState() {
        if (userPaused || overlayShowing() || (almanac != null && almanac.isVisible())
            || (questTree != null && questTree.isVisible())
            || (minigame != null && minigame.isVisible())) timer.stop();
        else timer.start();
        topBar.setPausedVisual(userPaused);
    }

    private Rectangle regionRect(TutorialStep s) {
        if (s == null) return null;
        if (s.region == TutorialStep.Region.ALMANAC)
            return almanac.isVisible()
                ? SwingUtilities.convertRectangle(almanac, almanac.cardBounds(), overlay)
                : null;
        // Bau-Schritte: Seitenleiste + Karte freistellen - Baumenü öffnen (BAUEN-Knopf/Rechtsklick)
        // und dann auf der Karte platzieren.
        if (s.advance == TutorialStep.Advance.BUILD) {
            return SwingUtilities.convertRectangle(centerPanel,
                new Rectangle(0, 0, centerPanel.getWidth(), centerPanel.getHeight()), overlay);
        }
        JComponent c = switch (s.region) {
            case BUILD_MENU -> sideBar;
            case MAP -> mapPanel;
            case ZONE_TABS -> sideBar;
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
        highlightBuild = null;
        minigame.forceClose();
        userPaused = false;
        speed = 1;
        topBar.selectSpeed(1);
        inspector.setBuilding(null);
        timer.setDelay(delayForSpeed(speed));
        refreshAll();
        updateOverlays();
    }

    /** Während eines BUILD-Tutorialschritts: das geforderte Gebäude, sonst null. */
    public BuildingType tutorialBuildTarget() {
        TutorialStep s = tutorial.isActive() ? tutorial.current() : null;
        return (s != null && s.advance == TutorialStep.Advance.BUILD) ? s.buildTarget : null;
    }

    // --- Freischalt-Wegweiser ("Zeig mir!"): kurzzeitig aufleuchtender Bau-Button ---
    private BuildingType highlightBuild;
    private long highlightExpireMs;

    /** Das gerade per Freischaltung hervorgehobene Gebäude (leuchtet ein paar Sekunden), sonst null. */
    public BuildingType highlightBuild() {
        if (highlightBuild != null && System.currentTimeMillis() > highlightExpireMs) highlightBuild = null;
        return highlightBuild;
    }

    /**
     * Springt eine Freischaltung an: passende Zone/Almanach öffnen und - bei Gebäuden -
     * den zugehörigen Button im Baumenü aufleuchten lassen ("wie per Link zum Objekt").
     */
    public void navigateToUnlock(String flag) {
        if (flag == null) return;
        if (flag.startsWith("mkt.")) { openAlmanac(AlmanacPanel.TAB_MARKETING); return; }
        if (flag.startsWith("tier.")) { setZone(Zone.PRODUKTION); return; }
        if (flag.startsWith("zone.")) {
            try { setZone(Zone.valueOf(flag.substring(5))); } catch (IllegalArgumentException ignored) {}
            return;
        }
        // build.* und era.HALLE: erstes baubares Gebäude mit diesem Flag suchen, in die passende
        // Zone wechseln und das Baumenü mit aufleuchtendem Eintrag öffnen.
        BuildingType t = null;
        for (BuildingType bt : BuildingType.buildable())
            if (flag.equals(bt.unlockFlag())) { t = bt; break; }
        if (t != null) {
            setZone(t.zone());
            highlightBuild = t;
            highlightExpireMs = System.currentTimeMillis() + 8000;
            buildMenu.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
            getLayeredPane().moveToFront(buildMenu);
            buildMenu.openForBuilding(t);
            refreshAll();
        }
    }

    /** Vom "Zeig mir!"-Knopf der Freischalt-Ansage: hinspringen und die Ansage schließen. */
    public void navigateFromAnnouncement() {
        if (pendingAnnouncement != null) navigateToUnlock(pendingAnnouncement[0]);
        dismissAnnouncement();
    }

    public void selectBuildType(BuildingType t) {
        BuildingType forced = tutorialBuildTarget();
        if (forced != null && t != forced) {
            game.log("Tutorial: Bau zuerst " + forced.displayName + " - der leuchtende Button im Baumenü.", GameState.LOG_WARN);
            refreshAll(); return;
        }
        tool = Tool.PLACE; selectedType = t; selectedBuilding = null; inspector.setBuilding(null); refreshAll();
    }
    public void toggleDemolish() { if (tool == Tool.DEMOLISH) clearTool(); else { tool = Tool.DEMOLISH; selectedType = null; refreshAll(); } }
    public void clearTool() { tool = Tool.NONE; selectedType = null; refreshAll(); }

    /**
     * Tropico-Stil: das Baumenü dockt immer an derselben Stelle neben der Seitenleiste an -
     * egal ob es über den BAUEN-Knopf, die Taste B oder Rechtsklick auf die Karte geöffnet wird.
     */
    public void openBuildMenu() {
        buildMenu.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
        getLayeredPane().moveToFront(buildMenu);
        buildMenu.open(currentZone);
        refreshAll();
    }
    public void toggleBuildMenu() {
        if (buildMenu.isVisible()) buildMenu.close(); else openBuildMenu();
    }
    public boolean buildMenuVisible() { return buildMenu.isVisible(); }

    /** Feste Andock-Position des Baumenüs (in Layered-Pane-Koordinaten): oben, neben der Seitenleiste. */
    public Point buildMenuAnchor() {
        if (sideBar != null && sideBar.getWidth() > 0)
            return SwingUtilities.convertPoint(sideBar, new Point(sideBar.getWidth() + 10, 10), getLayeredPane());
        return new Point(SideBar.WIDTH + 10, 96);
    }

    public void afterBuildMenuClosed() { refreshAll(); }

    public void openAlmanac(int tab) {
        if (questTree.isVisible()) questTree.setVisible(false);
        if (buildMenu.isVisible()) buildMenu.setVisible(false);
        almanac.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
        almanac.open(tab);
        applyTimerState();
    }
    public void closeAlmanac() { if (almanac.isVisible()) almanac.close(); }
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
        BuildingType forced = tutorialBuildTarget();
        if (forced != null && selectedType != forced) { clearTool(); return; }
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
        refreshAll();
    }

    // ===================== Tastenkürzel =====================

    private void installKeyBindings() {
        JComponent root = getRootPane();
        bind(root, KeyEvent.VK_ESCAPE, "cancel", () -> {
            if (buildMenu.isVisible()) { buildMenu.close(); return; }
            clearTool();
        });
        bind(root, KeyEvent.VK_SPACE, "pause", () -> setPaused(!userPaused));
        bind(root, KeyEvent.VK_1, "s1", () -> setSpeed(1));
        bind(root, KeyEvent.VK_2, "s2", () -> setSpeed(2));
        bind(root, KeyEvent.VK_3, "s3", () -> setSpeed(3));
        bind(root, KeyEvent.VK_Q, "questtree", () -> {
            if (questTree.isVisible()) questTree.close(); else openQuestTree();
        });
        bind(root, KeyEvent.VK_B, "buildmenu", this::toggleBuildMenu);
    }
    private void bind(JComponent c, int key, String name, Runnable action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), name);
        c.getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                // Während eines Minigames gehören die Tasten dem Minigame.
                if (minigame != null && minigame.isVisible()) { minigame.handleKey(key); return; }
                action.run();
            }
        });
    }

    // ===================== Getter für Panels =====================

    // Test-Hooks (für --guitest)
    public void debugRefreshOverlay() { updateOverlays(); }
    public void debugOpenBuildMenu(BuildingType t) {
        setZone(t.zone());
        buildMenu.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
        getLayeredPane().moveToFront(buildMenu);
        buildMenu.openForBuilding(t);
    }
    public void debugOpenMinigame(String id, double stake, double playSeconds) {
        minigame.setBounds(0, 0, getLayeredPane().getWidth(), getLayeredPane().getHeight());
        getLayeredPane().moveToFront(minigame);
        minigame.debugForceIntro(id, stake);
        if (playSeconds > 0) minigame.debugStartAndTick(playSeconds);
    }
    public void debugCloseMinigame() { minigame.forceClose(); }
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
