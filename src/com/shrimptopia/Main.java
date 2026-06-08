package com.shrimptopia;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.Zone;
import com.shrimptopia.ui.GameFrame;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/** Einstiegspunkt. Startet die GUI - oder mit "--selftest" einen Konsolen-Logiktest. */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--selftest")) {
            selfTest();
            return;
        }
        if (args.length > 0 && args[0].equals("--guitest")) {
            guiTest();
            return;
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }

    /** Baut eine funktionierende Wirtschaft auf und simuliert sie - rein in der Konsole. */
    private static void selfTest() {
        System.out.println("== ShrimpTopia Selbsttest ==");
        GameState gs = new GameState(new Random(42));

        gs.place(BuildingType.POWER_PLANT,  0, 0, true);
        gs.place(BuildingType.WATER_PLANT,  1, 0, true);
        gs.place(BuildingType.ALGAE_FARM,   2, 0, true);
        gs.place(BuildingType.SHRIMP_TANK,  3, 0, true);
        gs.place(BuildingType.SHRIMP_TANK,  4, 0, true);
        gs.place(BuildingType.HOUSING,      0, 1, true);
        gs.place(BuildingType.SALES_OFFICE, 1, 1, true);

        System.out.printf("Startkapital nach Bau: %.0f Geld, %d Gebäude%n",
            gs.getMoney(), gs.buildingCount());
        System.out.println("Tag |    Geld | Wasser | Futter | Shrimp |  Rep | Strom(P/V) | Arb(V/B)");

        boolean ok = true;
        for (int i = 1; i <= 120; i++) {
            gs.tick();
            if (i % 15 == 0 || i == 1) {
                System.out.printf("%3d | %7.0f | %6.0f | %6.0f | %6.0f | %4.0f | %4d/%-4d | %3d/%-3d%n",
                    gs.getDay(), gs.getMoney(), gs.getWater(), gs.getFeed(), gs.getShrimp(),
                    gs.getReputation(), gs.getPowerProduced(), gs.getPowerUsed(),
                    gs.getWorkersAvail(), gs.getWorkersUsed());
            }
            if (!Double.isFinite(gs.getMoney()) || !Double.isFinite(gs.getShrimp())
                || !Double.isFinite(gs.getWater()) || !Double.isFinite(gs.getFeed())) {
                ok = false;
                System.out.println("FEHLER: nicht-endlicher Wert in Tick " + i);
                break;
            }
        }

        System.out.println("----");
        System.out.printf("Ende: %.0f Geld, Reputation %.0f, Ziel erreicht: %s, pleite: %s%n",
            gs.getMoney(), gs.getReputation(), gs.isGoalReached(), gs.isBankrupt());
        // Mit dieser Konfiguration sollte die Wirtschaft profitabel wachsen.
        if (gs.getMoney() <= 12000 - 3450) {
            System.out.println("WARN: Wirtschaft wächst nicht wie erwartet.");
        }
        System.out.println(ok ? "SELFTEST OK" : "SELFTEST FAIL");
    }

    /** Rendert die Oberfläche offscreen in mehrere PNGs (Tutorial, Haupt-UI, Quest-Popup). */
    private static void guiTest() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        try {
            SwingUtilities.invokeAndWait(() -> {
                GameFrame f = new GameFrame();
                f.pack();
                f.setSize(1440, 900);
                f.validate();

                GameState gs = f.game();
                // Freischaltungen, damit Zonen/Tiers in der UI sichtbar sind
                for (String flag : new String[]{"zone.FORSCHUNG", "zone.EMPFANG", "zone.LOGISTIK",
                        "build.water_hub", "build.export", "build.military", "build.genlab",
                        "build.shrimpboost", "build.robotworks", "build.barracks", "tier.WARKRILL",
                        "tier.BIO", "tier.GOURMET", "tier.PROTEIN", "krillkill.bootcamp"})
                    gs.unlock(flag, null);

                // Produktionshalle bestücken
                BuildingType[] prod = {
                    BuildingType.POWER_PLANT, BuildingType.SOLAR_ROOF, BuildingType.WATER_PLANT,
                    BuildingType.WATER_HUB, BuildingType.ALGAE_FARM, BuildingType.SHRIMP_TANK,
                    BuildingType.SHRIMP_TANK, BuildingType.HOUSING, BuildingType.SALES_OFFICE,
                    BuildingType.SHELL_PRESS, BuildingType.SHRIMPBOOST_FACTORY, BuildingType.ROBOT_WORKS };
                int[][] pos = { {0,0},{1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{0,1},{1,1},{2,1},{3,1},{4,1} };
                for (int i = 0; i < prod.length; i++) gs.place(prod[i], Zone.PRODUKTION, pos[i][0], pos[i][1], false);
                for (int i = 0; i < 30; i++) gs.tick();

                // 1) Tutorial-Overlay (Schritt 1 ist aktiv)
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_tutorial.png");

                // 2) Haupt-UI mit ausgewähltem Becken (Inspektor)
                f.tutorialSkip();
                f.selectBuilding(gs.at(Zone.PRODUKTION, 5, 0));
                f.debugForceQuest("krillkill_1"); f.debugForceQuest("akwanov_1");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_main.png");

                // 3) Quest-Popup (General Krillkill)
                f.debugForceQuest("krillkill_intro");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_popup.png");

                // 4) Spielende-Popup
                f.resolveQuest(0);
                f.debugForceQuest("end_imperator");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_ending.png");

                // 5) Almanach (Vermögen-Tab)
                f.resolveQuest(0);
                f.openAlmanac(0);
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_almanac.png");

                // 5) HQ-Kommando (Edikte)
                gs.toggleEdict(com.shrimptopia.model.Edict.OPEN_HOUSE);
                gs.toggleEdict(com.shrimptopia.model.Edict.FREE_TRADE);
                f.openAlmanac(5);
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_hq.png");
            });
        } catch (Exception e) {
            System.out.println("GUITEST FAIL: " + e);
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void snap(java.awt.Container c, String name) {
        int w = Math.max(900, c.getWidth()), h = Math.max(640, c.getHeight());
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        c.printAll(g);
        g.dispose();
        File f = new File(System.getProperty("java.io.tmpdir"), name);
        try {
            ImageIO.write(img, "png", f);
            System.out.println("GUITEST OK - " + f.getAbsolutePath() + " (" + w + "x" + h + ")");
        } catch (Exception ex) {
            System.out.println("GUITEST write fail: " + ex);
        }
    }
}
