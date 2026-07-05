package com.shrimptopia;

import com.shrimptopia.model.Building;
import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.GameState;
import com.shrimptopia.model.MarketingStream;
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

    /** Simuliert Garagen-Start, Hallen-Ausbau und Marketing - rein in der Konsole. */
    private static void selfTest() {
        System.out.println("== ShrimpTopia Selbsttest (Garagen-Start) ==");
        GameState gs = new GameState(new Random(42));

        // Phase 1: Garagen-Wirtschaft (Start-Tier)
        gs.place(BuildingType.OLD_GENERATOR, 0, 0, true);
        gs.place(BuildingType.RAIN_BARREL,   1, 0, true);
        gs.place(BuildingType.RAIN_BARREL,   2, 0, true);
        gs.place(BuildingType.ALGAE_BUCKET,  3, 0, true);
        gs.place(BuildingType.ALGAE_BUCKET,  4, 0, true);
        gs.place(BuildingType.GARAGE_TANK,   5, 0, true);
        gs.place(BuildingType.GARAGE_TANK,   6, 0, true);
        gs.place(BuildingType.YARD_SALE,     0, 1, true);
        double start = gs.getMoney();
        System.out.printf("Startkapital nach Garagen-Bau: %.0f Geld, %d Gebäude%n", start, gs.buildingCount());
        System.out.println("Tag |   Geld | Wasser | Futter | Shrimp | Nachfrage | verkauft");

        boolean ok = true;
        for (int i = 1; i <= 100 && ok; i++) {
            gs.tick();
            if (i % 20 == 0 || i == 1)
                System.out.printf("%3d | %6.0f | %6.0f | %6.0f | %6.1f | %9.1f | %8.1f%n",
                    gs.getDay(), gs.getMoney(), gs.getWater(), gs.getFeed(), gs.getShrimp(),
                    gs.getDemandLast(), gs.getShrimpSoldLast());
            ok = finite(gs);
        }
        if (gs.getMoney() <= start) { ok = false; System.out.println("WARN: Garagen-Wirtschaft wächst nicht."); }

        // Phase 2: Halle freischalten, Garagen-Technik ausbauen, Marketing buchen
        gs.unlock("era.HALLE", null);
        gs.addMoney(4000);
        gs.toggleStream(MarketingStream.FLYER);
        int upgraded = 0;
        for (Building b : new java.util.ArrayList<>(gs.buildings()))
            if (b.type.upgradesTo() != null && gs.upgradeBuilding(b) != null) upgraded++;
        double p2 = gs.getMoney();
        System.out.println("Phase 2: Halle frei, " + upgraded + " Gebäude ausgebaut, Marketing 'Flugblätter' aktiv.");
        for (int i = 1; i <= 100 && ok; i++) { gs.tick(); ok = finite(gs); }

        System.out.println("----");
        System.out.printf("Ende: %.0f Geld (Phase-2-Start %.0f), Nachfrage %.1f, verkauft %.1f/Tag, pleite: %s%n",
            gs.getMoney(), p2, gs.getDemandLast(), gs.getShrimpSoldLast(), gs.isBankrupt());
        if (gs.getMoney() <= p2) { ok = false; System.out.println("WARN: Hallen-Wirtschaft wächst nicht."); }
        System.out.println(ok ? "SELFTEST OK" : "SELFTEST FAIL");
    }

    private static boolean finite(GameState gs) {
        boolean f = Double.isFinite(gs.getMoney()) && Double.isFinite(gs.getShrimp())
            && Double.isFinite(gs.getWater()) && Double.isFinite(gs.getFeed());
        if (!f) System.out.println("FEHLER: nicht-endlicher Wert an Tag " + gs.getDay());
        return f;
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
                for (String flag : new String[]{"era.HALLE", "zone.FORSCHUNG", "zone.EMPFANG", "zone.LOGISTIK",
                        "build.water_hub", "build.export", "build.military", "build.genlab",
                        "build.shrimpboost", "build.robotworks", "build.barracks", "tier.WARKRILL",
                        "build.gut_station", "build.waste_plant",
                        "tier.BIO", "tier.GOURMET", "tier.PROTEIN", "krillkill.bootcamp",
                        "mkt.radio", "mkt.social", "mkt.tube", "mkt.billboard", "mkt.tv",
                        "sts6", "shrimp_union"})
                    gs.unlock(flag, null);
                gs.toggleStream(com.shrimptopia.model.MarketingStream.FLYER);
                gs.toggleStream(com.shrimptopia.model.MarketingStream.RADIO);
                gs.toggleStream(com.shrimptopia.model.MarketingStream.SOCIAL);

                // Produktionshalle bestücken
                BuildingType[] prod = {
                    BuildingType.POWER_PLANT, BuildingType.SOLAR_ROOF, BuildingType.WATER_PLANT,
                    BuildingType.WATER_HUB, BuildingType.ALGAE_FARM, BuildingType.SHRIMP_TANK,
                    BuildingType.SHRIMP_TANK, BuildingType.HOUSING, BuildingType.SALES_OFFICE,
                    BuildingType.SHELL_PRESS, BuildingType.SHRIMPBOOST_FACTORY, BuildingType.ROBOT_WORKS,
                    BuildingType.GUT_STATION, BuildingType.BIOGAS_PLANT };
                int[][] pos = { {0,0},{1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{0,1},{1,1},{2,1},{3,1},{4,1},{5,1},{6,1} };
                for (int i = 0; i < prod.length; i++) gs.place(prod[i], Zone.PRODUKTION, pos[i][0], pos[i][1], false);
                for (int i = 0; i < 30; i++) gs.tick();
                while (gs.pollAnnouncement() != null) { }   // Meilenstein-Ansagen abräumen (stören die Snaps)

                // 1) Tutorial-Overlay (Schritt 1 ist aktiv)
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_tutorial.png");

                // 1a) Bauschritt (Generator) mit dem fest angedockten Baumenü: Menü und
                //     Tutorial-Dialog dürfen sich nicht überdecken.
                for (int i = 0; i < 4; i++) f.tutorialAdvance();
                f.openBuildMenu();
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_buildmenu.png");
                f.toggleBuildMenu();

                // 1b) Almanach-Schritte des Tutorials: Tier-Menü, dann Marketing-Menü
                for (int i = 0; i < 7; i++) f.tutorialAdvance();
                snap(f.getRootPane(), "shrimptopia_v3_tutorial_tiers.png");
                f.tutorialAdvance();
                snap(f.getRootPane(), "shrimptopia_v3_tutorial_marketing.png");

                // 2) Haupt-UI mit ausgewähltem Becken (Inspektor)
                f.tutorialSkip();
                f.selectBuilding(gs.at(Zone.PRODUKTION, 5, 0));
                f.debugForceQuest("krillkill_1"); f.debugForceQuest("akwanov_1");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_main.png");

                // 2b) Inspektor mit Ausbau-Button (Garagen-Aquarium)
                gs.place(BuildingType.GARAGE_TANK, Zone.PRODUKTION, 5, 1, false);
                f.selectBuilding(gs.at(Zone.PRODUKTION, 5, 1));
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_ausbau.png");

                // 3) Quest-Popup (General Krillkill)
                f.debugForceQuest("krillkill_intro");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_popup.png");

                // 4) Ergebnis-Karte (Konsequenzen der Wahl)
                f.resolveQuest(0);
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_outcome.png");
                f.dismissOutcome();

                // 4b) Spielende-Popup
                f.debugForceQuest("end_imperator");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_ending.png");

                // 5) Quest-Baum: einige Stränge anspielen, damit alle Zustände sichtbar sind
                f.resolveQuest(0); f.dismissOutcome();
                f.debugForceQuest("beh_formular");  f.resolveQuest(0); f.dismissOutcome();
                f.debugForceQuest("tier_wuerde");   f.resolveQuest(0); f.dismissOutcome();
                f.debugForceQuest("inf_viral");     f.resolveQuest(0); f.dismissOutcome();
                f.debugForceQuest("akwanov_intro"); f.resolveQuest(1); f.dismissOutcome();   // Rivalität beginnt

                // 5b) Freischalt-Ansage (Meilenstein-Unlock als Dialog statt Logzeile)
                gs.unlock("build.blackmarket", "Ein dubioser Kontakt bietet dir einen Schwarzmarkt an...");
                f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_announce.png");
                f.dismissAnnouncement();

                // 5c) Kyle-Popup (neuer Reddit-Rivale mit eigenem Avatar)
                f.debugForceQuest("kyle_intro");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_kyle.png");
                f.resolveQuest(1); f.dismissOutcome();

                // 5d) Becken-3-Vorfall (versteckte Kette, hier erzwungen)
                f.debugForceQuest("boost_1");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_boost.png");
                f.resolveQuest(0); f.dismissOutcome();

                // 5e) Dmitri-Quest (Praktikant mit eigenem Avatar)
                f.debugForceQuest("dmitri_zeugnis");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_dmitri.png");
                f.resolveQuest(2); f.dismissOutcome();

                // 5f) Reinhild Darmstädter (Darmentleerung -> Gourmet-Freischaltung, eigener Avatar)
                f.debugForceQuest("gourmet_darm");
                f.validate(); f.debugRefreshOverlay();
                snap(f.getRootPane(), "shrimptopia_v3_darm.png");
                f.resolveQuest(0); f.dismissOutcome();

                f.openQuestTree();
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_questtree.png");
                f.debugScrollQuestTree(600);
                snap(f.getRootPane(), "shrimptopia_v3_questtree2.png");

                // 6) Almanach (Vermögen-Tab)
                f.openAlmanac(0);
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_almanac.png");

                // 7) Marketing-Tab (Streams & Nachfrage)
                f.openAlmanac(5);
                f.validate();
                snap(f.getRootPane(), "shrimptopia_v3_marketing.png");

                // 8) HQ-Kommando (Edikte)
                gs.toggleEdict(com.shrimptopia.model.Edict.OPEN_HOUSE);
                gs.toggleEdict(com.shrimptopia.model.Edict.FREE_TRADE);
                gs.toggleEdict(com.shrimptopia.model.Edict.STS_GUARD);
                gs.toggleEdict(com.shrimptopia.model.Edict.UNION_COUNCIL);
                f.openAlmanac(6);
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
