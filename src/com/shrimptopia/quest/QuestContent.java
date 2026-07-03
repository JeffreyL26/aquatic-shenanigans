package com.shrimptopia.quest;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.ShrimpTier;

import static com.shrimptopia.quest.QuestEffect.*;

/**
 * Der gesamte Quest-/Story-Katalog: 4 Ketten + 7 Einzelquests, General Krillkills
 * "Operation Protein-Sturm" (7 Stufen) und Außenminister Akwanovs Rivalität (8 Stufen).
 * Kettenstufen sind chainOnly und werden per Auswahl (then) gestartet.
 */
public final class QuestContent {

    private QuestContent() {}

    private static QuestSystem QS;

    public static void populate(QuestSystem qs) {
        QS = qs;
        garage();
        marketing();
        behoerden();
        tierschutz();
        influencer();
        krabbo();
        kyle();
        boostSpill();
        usbekistan();
        einzelquests();
        krillkill();
        akwanov();
        conflicts();
        endings();
        attachObjectives();
    }

    // ===================== AUFSTIEG - Raus aus der Garage =====================
    private static void garage() {
        auto("era_halle", GameCharacter.ADVISOR, "Dr. Perla (mit Zollstock)",
            Condition.all(Condition.money(3200), Condition.shrimpProduced(80)), 1,
            "Die Garage platzt aus allen Nähten",
            "Boss, Aquarium Nummer drei steht auf der Waschmaschine und der Generator teilt sich die "
            + "Steckdose mit dem Kühlschrank. Ich habe nachgemessen: Wir sind offiziell zu groß für die "
            + "Garage. Nebenan steht eine Industriehalle leer - der Vermieter stellt keine Fragen, "
            + "solange wir keine stellen.",
            c("Die Halle mieten! (-1500)", "Hallen-Gebäude freigeschaltet! Garagen-Technik lässt sich "
                + "jetzt im Inspektor an Ort und Stelle UPGRADEN.", money(-1500), unlock("era.HALLE"), rep(4)),
            c("Erstmal nur den Hof dazu pachten. (-600)", "Etwas beengt, aber es läuft: Hallen-Gebäude "
                + "freigeschaltet - Upgrade im Inspektor.", money(-600), unlock("era.HALLE")));
    }

    // ===================== MARKETING-OFFENSIVE (Mira) =====================
    private static void marketing() {
        auto("mark_intro", GameCharacter.MAYOR, "Assistentin Mira (mit Excel-Tabelle)",
            Condition.any(Condition.sold(50), Condition.day(90)), 1,
            "Chef, die Kühlbox ist VOLL",
            "Die Nachbarschaft ist satt, Greta vom Kiosk auch. Mehr als eine Handvoll Shrimps am Tag "
            + "kauft hier niemand - die Leute müssen erstmal WISSEN, dass es uns gibt. Ich hätte da "
            + "Ideen, eine Excel-Tabelle und sehr viel Motivation. Das Marketing-Menü ist im Almanach.",
            c("Lokalradio buchen. (-300)", "Der Spot läuft zwischen Blasmusik und Staumeldung. "
                + "Marketing-Stream 'Lokalradio' buchbar!", money(-300), unlock("mkt.radio")).then("mark_social"),
            c("Flugblätter reichen erstmal.", "Greta verteilt fleißig weiter. Mehr Reichweite gibt's "
                + "später.", rep(2)).then("mark_social"),
            c("Radio UND Flyer-Offensive! (-500)", "Volle Breitseite: 'Lokalradio' buchbar, die "
                + "Zettel hängen überall.", money(-500), unlock("mkt.radio"), rep(3)).then("mark_social"));

        chain("mark_social", GameCharacter.MAYOR, "Assistentin Mira (Handy gezückt)", 0,
            "Die Leute FOTOGRAFIEREN unseren Stand",
            "Chef, wir sind lokal berühmt. Lass mich einen offiziellen Social-Media-Auftritt starten: "
            + "Becken-Fotos, Garnelen-Fakten, Behind-the-Scenes mit Greg. Ich brauche nur ein kleines "
            + "Budget und dein Gesicht. Zur Not nur das Budget. (Den echten Video-Kanal heben wir uns auf.)",
            c("Auftritt starten! (-400)", "Die Seite wächst. 90% der Kommentare fragen 'warum?'. Egal: "
                + "Stream 'Social Media' buchbar!", money(-400), unlock("mkt.social")).then("mark_agency"),
            c("Ohne mein Gesicht.", "Greg ist jetzt das Gesicht. Funktioniert besser als deins. Stream "
                + "'Social Media' buchbar.", unlock("mkt.social"), rep(1)).then("mark_agency"),
            c("Das Internet ist eine Modeerscheinung.", "Mira starrt dich lange an. (Kein neuer "
                + "Marketing-Stream.)", rep(-2)).then("mark_agency"));

        chain("mark_agency", GameCharacter.MAYOR, "Krusten & Krusten (Werbeagentur)", 1,
            "Zwei Herren, ein Jingle",
            "Zwei identische Herren in identischen Anzügen: 'Wir haben Ihren Betrieb beobachtet. "
            + "Rührend. Wir machen aus Ihnen eine MARKE. Primetime-TV, Jingle, die volle Dröhnung. Sie "
            + "werden den Jingle hassen. Alle werden ihn hassen. Und dann kaufen alle.'",
            c("TV-Kampagne unterschreiben. (-3000)", "'Shrimp your day!' läuft zur Primetime. Der "
                + "Jingle verfolgt dich im Schlaf. Stream 'TV-Spot' buchbar!", money(-3000), unlock("mkt.tv")),
            c("Nur das Plakat-Paket. (-1200)", "20 Meter Garnele an der A6. Stream 'Autobahn-Plakat' "
                + "buchbar.", money(-1200), unlock("mkt.billboard")),
            c("Wir bleiben bodenständig.", "Die Herren nicken synchron und verlassen rückwärts den "
                + "Raum.", rep(3)));
    }

    // ===================== KONFLIKTE (Armee-Stärke vs. Bedrohung) =====================
    private static void conflicts() {
        auto("conf_border", GameCharacter.AKWANOV, null, Condition.any(Condition.rival(12), Condition.day(450)), 2,
            "Garnelen-Grenzzwischenfall",
            "Eine usbekische 'Forschungsdrohne' verirrt sich über deine Halle. Akwanov: 'Reiner Zufall, mein "
            + "Freund. Wir kartieren nur... Ihre Lüftungsschächte.' General Krillkill - der pensionierte "
            + "Kriegsveteran, der sich zum Verteidiger deiner Halle ernannt hat - steht schon stramm.",
            c("Verteidigen (Armee einsetzen)", "Die Stellung wird gehalten - oder auch nicht.", QuestEffect.battle(30, 1500)),
            c("Diplomatie (-800)", "Du erkaufst Ruhe. Krillkill ist enttäuscht.", money(-800), rival(-10)));

        auto("conf_flotilla", GameCharacter.AKWANOV, null, Condition.rival(30), 2,
            "Die Embargo-Flottille",
            "Akwanov schickt eine Flotte von LKW (kein Meer, schon vergessen?), um deine Lieferketten zu "
            + "blockieren. 'Bedauerlich. Höhere Gewalt. Ich habe Tee dabei.'",
            c("Gegenangriff mit Kampf-Krill", "Die Garde rückt aus.", QuestEffect.battle(56, 2600)),
            c("Roboter-Blitz", "Stahl gegen Diplomatie.", QuestEffect.battle(64, 2400)),
            c("Diplomatie (-1500)", "Frieden, aber teuer.", money(-1500), rival(-12)));

        auto("conf_raid", GameCharacter.AKWANOV, null, Condition.rival(45), 2,
            "Spionage-Razzia",
            "Dmitri ist zurück - diesmal mit Verstärkung und einem Bolzenschneider. Akwanov: 'Wir nennen es "
            + "Wissensaustausch. Sehr einseitig.'",
            c("Verteidigen", "Die Halle wehrt sich.", QuestEffect.battle(120, 4000)),
            c("Gegenangriff mit Kampf-Krill", "Krillkill brüllt 'ANGRIFF!'.", QuestEffect.battle(96, 3800)),
            c("Diplomatie (-3000)", "Schweigegeld.", money(-3000), rival(-15)));

        auto("conf_siege", GameCharacter.AKWANOV, null, Condition.all(Condition.rival(60), Condition.money(200000)), 2,
            "Die Belagerung",
            "Akwanov riegelt die Zufahrt komplett ab. 'Ein doppelt eingeschlossenes Land weiß, wie man jemanden "
            + "einschließt.' Jetzt zählt nur die Armee.",
            c("Bis zum letzten Krill verteidigen", "Alles oder nichts.", QuestEffect.battle(200, 7000)),
            c("Roboter-Blitz", "Die Maschinen-Brigade rollt.", QuestEffect.battle(170, 6000)),
            c("Kapitulieren (-6000, -Ruf)", "Du beugst dich. Vorerst.", money(-6000), rep(-8), rival(-20)));

        auto("conf_war", GameCharacter.KRILLKILL, null,
            Condition.all(Condition.rival(66), Condition.money(320000), Condition.army(200)), 1,
            "DER FINALE KRILL-KRIEG",
            "General Krillkill: 'DAS IST ES, REKRUT! Alles, wofür wir gezüchtet haben! Akwanov vor den Toren, "
            + "die Suppe im Rücken - HEUTE schreiben wir GESCHICHTE!' Akwanov: 'Oder eine Rechnung. Ich nehme auch das.'",
            c("Großoffensive (volle Armee)", "Der entscheidende Schlag.", QuestEffect.battle(400, 30000), rival(-40)),
            c("Verhandeln statt verheizen (-15000)", "Ein ehrenhafter, sündhaft teurer Frieden.", money(-15000), rep(10), rival(-50)));
    }

    /** Bindet Ketten-Stufen an messbare Produktions-Ziele (macht das Spiel deutlich laenger). */
    // ===================== SPIELENDEN (Kombi aus Quests + Reichtum + Produktion) =====================
    private static Quest endingQ(String id, GameCharacter g, Condition trig, String title, String body, Choice... cs) {
        Quest q = new Quest(id, g, title, body, cs).when(trig).asEnding();
        QS.add(q);
        return q;
    }
    private static void endings() {
        endingQ("end_imperator", GameCharacter.MAYOR, Condition.money(1_500_000),
            "ENDE: Shrimp-Imperator",
            "1.500.000 auf dem Konto. Du sitzt auf einem Thron aus Garnelen-Schalen, die halbe Stadt arbeitet "
            + "für dich, und irgendwo weint ein Buchhalter vor Glück. Der reichste Krustentier-Magnat der Geschichte.",
            c("Lang lebe der Imperator!", "REICHTUMS-ENDE erreicht!"));

        endingQ("end_vertical", GameCharacter.ADVISOR, Condition.all(Condition.money(150000),
                Condition.resource("robots", 40), Condition.resource("boost", 200), Condition.resource("shells", 1000)),
            "ENDE: Vertikal integriert",
            "Schalen werden zu Energydrink, Energydrink zu Robotern, Roboter zu noch mehr Robotern. Greg, "
            + "Dr. Perlas Support-Garnele, nickt anerkennend aus dem Wasserglas. Du hast keine Farm mehr - "
            + "du hast ein selbstlaufendes Industrie-Imperium.",
            c("Die Maschine läuft.", "INDUSTRIE-ENDE erreicht!"));

        endingQ("end_protein", GameCharacter.KRILLKILL, Condition.all(Condition.questDone("krillkill_7"),
                Condition.questDone("conf_war"), Condition.army(250)),
            "ENDE: Operation Protein-Sturm vollendet",
            "General Krillkill salutiert mit Tränen im Auge: 'WIR HABEN ES GESCHAFFT, REKRUT! Die Suppe ist "
            + "besiegt, die Garde steht, kein Akwanov nimmt uns die Halle. Heute sind wir GESCHICHTE.' (Er heult doch.)",
            c("Esprit de Corps!", "MILITÄR-ENDE erreicht!"));

        endingQ("end_union", GameCharacter.AKWANOV, Condition.all(Condition.flag("akwanov_ally"),
                Condition.money(120000), Condition.rep(70)),
            "ENDE: Vereinigte Hallen-Garnelen-Union",
            "Akwanov hebt das Teeglas: 'Mein Freund, gemeinsam haben wir den Ozean endgültig arbeitslos gemacht. "
            + "Usbekistan und Sie - die Garnelen-Supermacht ohne einen Tropfen Meer. Auf die HALLEN!'",
            c("Auf die Hallen!", "ALLIANZ-ENDE erreicht!"));

        endingQ("end_saint", GameCharacter.PRESS, Condition.all(Condition.rep(95),
                Condition.questDone("tier_gewerkschaft"), Condition.tierProduced(ShrimpTier.BIO, 3000)),
            "ENDE: Garnelen-Heiliger",
            "Deine Shrimps haben Gewerkschaft, Wellness und Mitbestimmung. Lena weint vor Rührung, die Presse "
            + "feiert dich als ethischsten Tycoon aller Zeiten. Eine Garnele hält eine erstaunlich eloquente Dankesrede.",
            c("Für die Würde der Garnele!", "ETHIK-ENDE erreicht!"));

        endingQ("end_meme", GameCharacter.MAYOR, Condition.all(Condition.questDone("inf_cancel"),
                Condition.rep(85), Condition.sold(5000)),
            "ENDE: Meme-Legende",
            "Du hast den Shitstorm überlebt, #JusticeForBrian in #LegendForBrian verwandelt und nebenbei 5000 "
            + "Shrimps verkauft. Mira: 'Chef, wir sind nicht nur trending - wir SIND der Trend.'",
            c("Ruhm ist Geld.", "KULTUR-ENDE erreicht!")).by("Assistentin Mira");
    }

    private static void obj(String id, Condition c, String text) {
        Quest q = QS.get(id);
        if (q != null) { q.objective = c; q.objectiveText = text; q.chainOnly = true; q.trigger = Condition.never(); }
    }

    private static void attachObjectives() {
        // v3.2: Ziele ESKALIEREN im Kettenverlauf - frühe Stufen bleiben Einzelziele,
        // spätere kombinieren mehrere Anforderungen (Condition.all) mit steigenden Mengen.
        // Faustregel: nur Ressourcen kombinieren, die in jedem Spielverlauf erreichbar sind.
        // Marketing-Kette
        obj("mark_social", Condition.sold(600), "Verkaufe insgesamt 600 Shrimps");
        obj("mark_agency", Condition.all(Condition.money(30_000), Condition.sold(2_000)),
            "30.000 Vermögen & 2.000 Shrimps verkauft");
        // Behörden
        obj("beh_pruefung", Condition.buildCount(BuildingType.SHRIMP_TANK, 4), "Betreibe 4 Shrimp-Becken");
        obj("beh_plakette", Condition.all(Condition.rep(60), Condition.sold(1_200)),
            "Reputation 60 & 1.200 Shrimps verkauft");
        // Tierschutz
        obj("tier_demo", Condition.tierProduced(ShrimpTier.BIO, 1600), "Produziere 1.600 Bio-Shrimps");
        obj("tier_gewerkschaft", Condition.all(Condition.sold(4_000), Condition.shrimpProduced(6_000)),
            "4.000 verkauft & 6.000 produziert");
        // Influencer
        obj("inf_sponsor", Condition.money(60_000), "Erreiche 60.000 Vermögen");
        obj("inf_cancel", Condition.all(Condition.rep(70), Condition.sold(3_000)),
            "Reputation 70 & 3.000 Shrimps verkauft");
        // Krabbo
        obj("konk_angebot", Condition.all(Condition.money(120_000), Condition.shrimpProduced(8_000)),
            "120.000 Vermögen & 8.000 Shrimps produziert");
        // Kyle (eskaliert Stufe für Stufe)
        obj("kyle_1", Condition.all(Condition.sold(400), Condition.rep(55)),
            "400 verkauft & Reputation 55 (gegen die Ein-Sterne)");
        obj("kyle_2", Condition.all(Condition.sold(1_800), Condition.money(20_000)),
            "1.800 verkauft & 20.000 Vermögen (trotz Dumping)");
        obj("kyle_3", Condition.all(Condition.sold(3_500), Condition.rep(68)),
            "3.500 verkauft & Reputation 68 (Content-Klau aussitzen)");
        obj("kyle_4", Condition.all(Condition.shrimpProduced(9_000), Condition.resource("shells", 400)),
            "9.000 Shrimps produziert & 400 Schalen auf Lager");
        obj("kyle_5", Condition.all(Condition.money(150_000), Condition.sold(7_000)),
            "150.000 Vermögen & 7.000 verkauft (Kyle überflügeln)");
        // Becken-3-Vorfall (Spätspiel: kombiniert Boost-Produktion mit allem anderen)
        obj("boost_2", Condition.all(Condition.resource("boost", 60), Condition.rep(60)),
            "60 SHRIMPBOOST auf Lager & Reputation 60 (die Räte beobachten dich)");
        obj("boost_3", Condition.all(Condition.sold(8_000), Condition.resource("boost", 120)),
            "8.000 verkauft & 120 SHRIMPBOOST (die Räte rüsten sich)");
        obj("boost_4", Condition.all(Condition.shrimpProduced(16_000), Condition.money(180_000)),
            "16.000 produziert & 180.000 Vermögen (Krillkill sichtet Rekruten)");
        obj("boost_5", Condition.all(Condition.rep(70), Condition.sold(10_000)),
            "Reputation 70 & 10.000 verkauft (das Ministerium wird aufmerksam)");
        obj("boost_6", Condition.all(Condition.money(260_000), Condition.resource("boost", 200)),
            "260.000 Vermögen & 200 SHRIMPBOOST (Konferenz vorbereiten)");
        // General Krillkill
        obj("krillkill_1", Condition.shrimpProduced(2400), "Produziere insgesamt 2.400 Shrimps");
        obj("krillkill_2", Condition.shrimpProduced(6400), "Produziere insgesamt 6.400 Shrimps");
        obj("krillkill_3", Condition.all(Condition.money(80_000), Condition.sold(2_500)),
            "80.000 Vermögen fürs Bootcamp & 2.500 verkauft");
        obj("krillkill_4", Condition.all(Condition.sold(4_000), Condition.shrimpProduced(10_000)),
            "4.000 verkauft & 10.000 produziert (der General plant einen Feldtest)");
        obj("krillkill_5", Condition.all(Condition.buildCount(BuildingType.LAB, 1), Condition.money(100_000)),
            "Forschungslabor & 100.000 Kriegskasse");
        obj("krillkill_6", Condition.all(Condition.tierProduced(ShrimpTier.PROTEIN, 1600), Condition.sold(6_000)),
            "1.600 Protein-Bomben & 6.000 verkauft");
        obj("krillkill_7", Condition.all(Condition.tierProduced(ShrimpTier.WARKRILL, 960), Condition.army(80)),
            "960 Kampf-Krill & Armee-Stärke 80");
        // Aussenminister Akwanov
        obj("akwanov_1", Condition.money(140_000), "Erreiche 140.000 Vermögen");
        obj("akwanov_2", Condition.sold(7_200), "Verkaufe 7.200 Shrimps trotz Tarif");
        obj("akwanov_3", Condition.all(Condition.buildCount(BuildingType.ALGAE_FARM, 3), Condition.resource("feed", 600)),
            "3 Algenfarmen & 600 Futter auf Lager");
        obj("akwanov_4", Condition.all(Condition.money(180_000), Condition.sold(9_000)),
            "180.000 Vermögen & 9.000 verkauft");
        obj("akwanov_5", Condition.all(Condition.rep(65), Condition.shrimpProduced(14_000)),
            "Reputation 65 & 14.000 produziert");
        obj("akwanov_6", Condition.all(Condition.money(240_000), Condition.resource("shells", 800)),
            "240.000 Vermögen & 800 Schalen (Embargo aushalten)");
        obj("akwanov_7", Condition.all(Condition.buildCount(BuildingType.SOLAR_ROOF, 2), Condition.resource("boost", 60)),
            "2 Solar-Dächer & 60 SHRIMPBOOST auf Lager");
        obj("akwanov_8", Condition.all(Condition.money(320_000), Condition.shrimpProduced(24_000)),
            "320.000 Vermögen & 24.000 produziert (Finale)");
    }

    // ---- Helfer ----
    private static void auto(String id, GameCharacter g, String name, Condition trig, int kind,
                             String title, String body, Choice... cs) {
        Quest q = new Quest(id, g, title, body, cs).by(name).when(trig).kind(kind);
        QS.add(q);
    }
    private static void chain(String id, GameCharacter g, String name, int kind,
                              String title, String body, Choice... cs) {
        Quest q = new Quest(id, g, title, body, cs).by(name).chain().kind(kind);
        QS.add(q);
    }
    private static Choice c(String text, String result, QuestEffect... e) { return new Choice(text, result, e); }

    // ===================== KETTE A - Behörden-Dschungel =====================
    private static void behoerden() {
        auto("beh_formular", GameCharacter.MAYOR, "Frau Dr. Quallmann (Gewerbeaufsicht)",
            Condition.any(Condition.buildCount(BuildingType.SHRIMP_TANK, 3), Condition.day(100)), 0,
            "Willkommen im Schalentier-Register",
            "Mir ist aufgefallen, dass Sie Garnelen halten, ohne das Formular B-12/Shrimp in dreifacher "
            + "Ausfertigung eingereicht zu haben. Eines bitte handschriftlich. Vorschrift seit 1987, "
            + "niemand weiß mehr, warum.",
            c("Formular brav ausfüllen.", "Papierkram erledigt - die Behörde ist zufrieden.", rep(5)).then("beh_pruefung"),
            c("Ich kenne da jemanden... (-800)", "Ein brauner Umschlag wechselt den Besitzer.", money(-800), grantFlag("schmiergeld")),
            c("Welches Formular?", "Du ignorierst es. Das wird Konsequenzen haben...", rep(-3)).then("beh_pruefung"));

        chain("beh_pruefung", GameCharacter.MAYOR, "Frau Dr. Quallmann (mit Klemmbrett)", 0,
            "Routinemäßige unangemeldete Prüfung",
            "Bitte zeigen Sie mir die Schwimmlizenzen Ihrer Garnelen. Was, die haben keine? "
            + "Schwimmen die etwa schwarz?",
            c("Lizenzen nachkaufen. (-600)", "Alles legal. Die Garnelen schwimmen jetzt mit Papieren.", money(-600), rep(4)).then("beh_plakette"),
            c("Charmant durchquatschen.", "Sie lächelt tatsächlich. Glück gehabt.", rep(3)).then("beh_plakette"),
            c("Garnelen verstecken!", "Zwei Tage 'geschlossen wegen Inventur'. Sehr verdächtig.", rep(-8)).then("beh_plakette"));

        chain("beh_plakette", GameCharacter.MAYOR, "Bürgermeisterin Frau Schalk", 1,
            "Auszeichnung oder Anzeige?",
            "Wir können Sie mit der Goldenen Schalentier-Plakette ehren ODER eine Akte anlegen, die "
            + "dicker ist als Sie. Sie dürfen sich was wünschen - oder eben nicht.",
            c("Spende fürs Rathaus-Aquarium. (-1500)", "Die Plakette hängt jetzt stolz im HQ.", money(-1500), rep(10)),
            c("Ich verzichte höflich.", "Man trennt sich neutral.", rep(0)),
            c("Ich gehe an die Presse!", "Held des kleinen Mannes - oder Querulant. Die Stadt ist gespalten.", rep(5)));
    }

    // ===================== KETTE B - Tierschutz-Saga =====================
    private static void tierschutz() {
        auto("tier_wuerde", GameCharacter.PRESS, "Aktivistin Lena (Krill Lives Matter)",
            Condition.buildCount(BuildingType.SHRIMP_TANK, 5), 0,
            "Haben Ihre Shrimps eigentlich Hobbys?",
            "Ihre Garnelen schwimmen den ganzen Tag nur im Kreis - Beschäftigungsmangel! Garnelen "
            + "brauchen Anregung, Selbstverwirklichung, vielleicht ein kleines Podcast-Mikrofon.",
            c("Becken-Spielzeug installieren. (-400)", "Glücklichere Shrimps, glücklichere Lena.", money(-400), rep(8)).then("tier_demo"),
            c("Das sind Garnelen, Lena.", "Lena ist beleidigt. Das gibt Ärger.", rep(-6)).then("tier_demo"));

        chain("tier_demo", GameCharacter.PRESS, "Presse / Lena", 2,
            "Veganer-Demo vor dem Werkstor",
            "'SHRIMPS SIND FREUNDE, KEIN COCKTAIL'. Ein Demonstrant hat sich ans Tor geklebt. "
            + "Mit Bio-Klebstoff, immerhin.",
            c("Kostenlose Streichelzoo-Führung. (-200)", "Die Demo löst sich in Begeisterung auf.", money(-200), rep(10)).then("tier_gewerkschaft"),
            c("Polizei rufen.", "Ein Tag Betriebsstörung, aber Ruhe.", rep(-3)).then("tier_gewerkschaft"),
            c("Mit Lena verhandeln (Bio-Becken).", "Ein Becken wird zum Bio-Wellness-Becken. Bio-Tier freigeschaltet!",
                rep(12), unlock("tier.BIO")).then("tier_gewerkschaft"));

        chain("tier_gewerkschaft", GameCharacter.PRESS, "Shrimp-Betriebsrat 'Klausi'", 0,
            "Die Garnelen haben sich organisiert",
            "Ihre Garnelen haben eine Gewerkschaft gegründet. Forderungen: 4-Tage-Woche, mehr "
            + "Algen-Snacks, Mitspracherecht bei der Beleuchtungsfarbe. Ihr Sprecher heißt Klausi.",
            c("Forderungen akzeptieren.", "Zufriedene Premium-Shrimps! Bio-Qualität freigeschaltet. Klausi wird mächtig.",
                rep(15), unlock("tier.BIO"), grantFlag("klausi_stark")),
            c("Klausi 'befördern'. (-300)", "Klausi sitzt jetzt im besten Becken. Korruption wirkt.", money(-300)),
            c("Aussperrung!", "Zwei Tage Produktionsstopp. Du bist jetzt der Bösewicht.", rep(-12)));
    }

    // ===================== KETTE C - Influencer-Aufstieg =====================
    private static void influencer() {
        auto("inf_viral", GameCharacter.MAYOR, "Assistentin Mira",
            Condition.any(Condition.money(40_000), Condition.rep(80)), 1,
            "CHEF! Wir sind TRENDING!",
            "'Guy gets rich farming shrimp indoors' - 4 Millionen Views! Die Kommentare sind zu 60% "
            + "beeindruckt und zu 40% besorgt um unsere geistige Gesundheit. Was machen wir?",
            c("Voll drauf einsteigen - eigener Kanal! (-500)", "ShrimpTube läuft - als Marketing-Stream buchbar. Die Reichweite explodiert.", money(-500), rep(6), unlock("mkt.tube")).then("inf_sponsor"),
            c("Bescheiden weiterarbeiten.", "Der Hype verpufft, aber wir bleiben sympathisch.", rep(3)),
            c("Merch verkaufen! (T-Shirts)", "Shrimp-Shirts gehen weg wie warme Semmeln. Etwas Sellout-Vibe.", money(1200), rep(-4)).then("inf_sponsor"));

        chain("inf_sponsor", GameCharacter.MAYOR, "AquaBro Supplements", 0,
            "Bro, wollen wir was zusammen machen?",
            "Wir sind AquaBro, der Energy-Drink mit echtem Garnelen-Extrakt (don't ask). Sag in jedem "
            + "Video 'AquaBro - shrimp your day!' und kleb dieses leicht radioaktive Logo aufs Becken.",
            c("Deal! Geld ist Geld.", "Die Kasse klingelt, die Glaubwürdigkeit weniger. Das AquaBro-Logo klebt jetzt "
                + "auffällig nah an den Becken.", money(2400), rep(-8), grantFlag("boost_futter")).then("inf_cancel"),
            c("Nur, wenn ihr nicht verfüttert.", "Kleinerer Deal, aber mit Haltung.", money(960), rep(6)).then("inf_cancel"),
            c("Ihr seid mir zu dubios.", "Du lässt die Finger davon.", rep(2)));

        chain("inf_cancel", GameCharacter.MAYOR, "Mira (panisch)", 2,
            "Chef. CHEF. Wir werden gecancelt.",
            "Ein altes Video: Du hast eine Garnele 'Brian' genannt und dann verkauft. #JusticeForBrian "
            + "trendet. Brian hat jetzt mehr Follower als wir. Brian ist tot, Chef. Brian war Suppe.",
            c("Aufrichtige Entschuldigung + Gedenkbecken. (-700)", "Nach Tagen kommt das große Comeback. Alle lieben Reue.", money(-700), rep(13)),
            c("Brian-Suppe als Limited Edition!", "Absolute Legende. Das Internet dreht durch (im Guten).", money(2000), rep(8)),
            c("Internet aus, Tee trinken.", "Du wirkst ignorant, aber stoisch.", rep(-10)));
    }

    // ===================== KETTE D - Krabbo-Fehde =====================
    private static void krabbo() {
        auto("konk_krabbo", GameCharacter.MAYOR, "Assistentin Mira (liest Lokalzeitung)",
            Condition.money(80_000), 2,
            "Krabbo Inc. baut nebenan",
            "Chad Krabbowski eröffnet eine Mega-Shrimp-Fabrik gegenüber. Slogan: 'Billiger. Größer. "
            + "Vermutlich illegal.' Er unterbietet schon deine Preise.",
            c("Qualitätsoffensive: in Tiers investieren. (-1000)", "Gourmet-Zucht freigeschaltet - Qualität schlägt Masse.", money(-1000), unlock("tier.GOURMET")).then("konk_angebot"),
            c("Preiskrieg starten.", "Hart, aber Krabbo verliert Marktanteile.", rep(5)).then("konk_angebot"),
            c("Industriespionage. (-500)", "Mira als Maulwurf. Riskant, aber lehrreich.", money(-500)).then("konk_angebot"));

        chain("konk_angebot", GameCharacter.MAYOR, "Chad Krabbowski (Hai-Krawatte)", 0,
            "Lass uns reden, Kleiner",
            "Du hast Talent, aber du spielst in der Kreisliga. Verkauf mir deine Halle - oder wir machen "
            + "Business zusammen - oder du bleibst mein niedlicher kleiner Rivale. Deine Wahl.",
            c("Niemals - Fusion zu gleichen Teilen.", "Cleverer Deal. Chad besitzt jetzt einen Zipfel von dir.", money(5000), rep(5)),
            c("In deine Träume, Chad!", "David gegen Goliath. Die Bevölkerung jubelt.", rep(10)),
            c("Ich kaufe DICH. (-25000)", "Chad weint. Seine Hallen gehören jetzt dir.", money(-25_000), rep(10)));
    }

    // ===================== KETTE E - Kyle, der neidische Redditor =====================
    private static void kyle() {
        auto("kyle_intro", GameCharacter.KYLE, null,
            Condition.any(Condition.sold(80), Condition.day(120)), 2,
            "NEUER RIVALE: Der Kommentar aus der Tiefe",
            "Dein Erfolg trendet auf r/IndoorShrimp - und ganz oben thront u/KrustenKyle_87: 'lol. "
            + "mittelmäßig. shrimps kann JEDER. ich mach das jetzt auch, nur richtig. aus meinem keller. "
            + "RemindMe! 6 months.' Mira googelt ihn: gebaut wie ein Sitzsack, Haut wie ein frisch "
            + "geölter Pfannkuchen, 340.000 Karma. Er hat bereits 40 Aquarien bestellt.",
            c("Freundlich willkommen heißen.", "Er nennt deine Nettigkeit 'passiv-aggressives "
                + "Gatekeeping' und blockiert dich. Behalte ihn im Auge.", rep(3)).then("kyle_1"),
            c("'ok kyle' antworten.", "'ok kyle' wird zum Meme des Monats. Kyle schwitzt vor Wut. "
                + "Also noch mehr als sonst.", rep(5)).then("kyle_1"),
            c("Kommentar melden.", "Der Kommentar bekommt drei Awards und einen Fanclub. Streisand "
                + "lässt grüßen.", rep(-3)).then("kyle_1"));

        chain("kyle_1", GameCharacter.KYLE, null, 2,
            "Der Ein-Stern-Schwarm",
            "Kyles Subreddit r/EchteShrimpZuechter (41 Mitglieder, davon 11 er selbst) startet eine "
            + "'Community-Aktion': überall Ein-Stern-Bewertungen. 'Betreiber arrogant, Garnelen wirken "
            + "ungelesen', schreibt er - dreimal, von drei Accounts, alle mit demselben Tippfehler "
            + "('Garnelle').",
            c("Mit Qualität dagegenhalten.", "Deine Kunden kontern mit echten Bewertungen. Die "
                + "'Garnelle' wird zum Insider-Witz.", rep(4)).then("kyle_2"),
            c("Anwalt einschalten. (-600)", "Die Fake-Bewertungen verschwinden. Kyle antwortet mit "
                + "einem 23-minütigen Reaktions-Video.", money(-600), rep(2)).then("kyle_2"),
            c("Jede Bewertung höflich beantworten.", "Zeitaufwändig - aber die Höflichkeit macht ihn "
                + "WAHNSINNIG.", rep(3)).then("kyle_2"));

        chain("kyle_2", GameCharacter.KYLE, null, 2,
            "KYLES KELLER-KRABBEN - jetzt 30% billiger",
            "Kyle unterbietet deine Preise um 30 Prozent. Woher die Marge? Sein Setup: 40 Aquarien, "
            + "ein Mining-PC als Heizung und Baustellenlampen, die er 'gefunden' hat. Anwohner "
            + "beschreiben den Geruch als 'juristisch relevant'. Er nennt es DISRUPTION.",
            c("Qualitätsoffensive statt Preiskampf.", "Deine Stammkunden bleiben treu. Kyles Kunden "
                + "bekommen Ausschlag.", rep(5)).then("kyle_3"),
            c("Kurz mitdumpen. (-1200)", "Teuer, aber Kyles Lager läuft über. Sein Keller riecht "
                + "jetzt NOCH relevanter.", money(-1200)).then("kyle_3"),
            c("Gesundheitsamt anrufen.", "Ergebnis der Probe: 'grenzwertig, aber legal'. Kyle rahmt "
                + "das Gutachten ein und hängt es über die Fritteuse.", rep(-2)).then("kyle_3"));

        chain("kyle_3", GameCharacter.KYLE, null, 2,
            "ShrimpTube OHNE Lügen",
            "Kyle lädt deine Videos rückwärts abgespielt hoch ('Transformation, googelt es') und lobt "
            + "sich darunter mit fünf Accounts, die zufällig alle 'Garnelle' schreiben. Sein Kanal "
            + "wächst trotzdem - die Leute wollen IHN sehen: Im Hintergrund isst er kalte Ravioli "
            + "direkt aus der Dose.",
            c("Content-Strike einreichen.", "Der Kanal ist weg. Kyle streamt jetzt 'REACTION auf "
                + "meine UNTERDRÜCKUNG' vor 14 Zuschauern.", rep(2)).then("kyle_4"),
            c("Ihn zum Duell-Video herausfordern.", "Das 'Shrimp-Off' wird ein Hit. Er verliert "
                + "deutlich, behauptet aber das Gegenteil. In Großbuchstaben.", rep(6)).then("kyle_4"),
            c("Ignorieren und weiter wachsen.", "Sehr reif. Kyle deutet es als Angst und lässt sich "
                + "ein T-Shirt davon drucken.", rep(1)).then("kyle_4"));

        chain("kyle_4", GameCharacter.KYLE, "Assistentin Mira (mit Taschenrechner)", 2,
            "Woher nimmt er die ARBEITSKRAFT?",
            "Chef, Kyles Zahlen gehen nicht auf: 900 verschickte Shrimps pro Woche. Allein. Aus einem "
            + "Keller. Selbst wenn er nie schläft und sich intravenös von Energydrinks ernährt - "
            + "unmöglich. Und nachts sieht man hinter seinem Kellerfenster Schatten. Kleine, gebeugte, "
            + "SEHR fleißige Schatten. Mit Dauerwelle.",
            c("Mira undercover einschleusen. (-500)", "Sie kommt mit Fotos zurück: Klapptische, "
                + "Akkord-Schälerei, ein Dienstplan in Sütterlin.", money(-500), grantFlag("kyle_beweis")).then("kyle_5"),
            c("Anonymer Tipp an Frau Dr. Quallmann.", "Die Gewerbeaufsicht liebt anonyme Tipps. Und "
                + "Razzien. BESONDERS Razzien.", rep(2)).then("kyle_5"),
            c("Teleobjektiv kaufen, Beweise sammeln. (-300)", "Gestochen scharf: Oma Hilde am "
                + "Schäl-Fließband, 23:40 Uhr.", money(-300), grantFlag("kyle_beweis")).then("kyle_5"));

        chain("kyle_5", GameCharacter.KYLE, "Frau Dr. Quallmann (Razzia-Einsatzleitung)", 1,
            "Operation Kellerlicht",
            "Die Razzia bringt es ans Licht: In Kyles Keller schälten SIEBZEHN RENTNER im Akkord "
            + "Garnelen - angeworben per Aushang 'Gesellige Bastelrunde, Hustenbonbons inklusive', "
            + "bezahlt in Reddit-Awards. Oma Hilde (91): 'Er sagte, wir sind ein Start-up.' Das "
            + "Urteil: drei Jahre wegen illegaler Rentnerarbeit. Kyles Imperium wird zwangsversteigert. "
            + "Im Gefängnis moderiert er jetzt das Schwarze Brett - streng, aber fair, sagt er. Nur "
            + "streng, sagen alle anderen.",
            c("Die Rentner-Genossenschaft unterstützen! (-2000)", "Die 'Flinke Finger eG' schält jetzt "
                + "offiziell für DICH - Vorständin: Oma Hilde. Die Stadt liebt es.", money(-2000), rep(14), grantFlag("kyle_coop")),
            c("Kyles 40 Aquarien ersteigern. (-2500)", "Die Keller-Krabben ziehen bei dir ein und "
                + "werden liebevoll umgeschult.", money(-2500), QuestEffect.shrimp(ShrimpTier.STANDARD, 400)),
            c("Ihm eine Postkarte in den Knast schreiben.", "Er antwortet mit 14 Seiten. Das erste "
                + "Wort ist 'Erstens:'. Du hängst sie ins Besucherzentrum.", rep(6)));
    }

    // ===================== KETTE F - Der Becken-3-Vorfall (versteckt) =====================
    // Erscheint NUR, wenn der Spieler per Dialogwahl (a) Klausis Gewerkschaft anerkannt hat
    // ("klausi_stark") und (b) SHRIMPBOOST in Beckennähe erlaubt hat ("boost_futter" via
    // Krillkills Fütterungs-Experiment ODER AquaBro-Deal) - und tatsächlich Boost produziert.
    private static void boostSpill() {
        auto("boost_1", GameCharacter.MAYOR, "Technischer Leiter Olaf (klatschnass)",
            Condition.all(Condition.flag("boost_futter"), Condition.flag("klausi_stark"),
                Condition.resource("boost", 25)), 2,
            "DER VORFALL IN BECKEN 3",
            "Boss... kleiner Zwischenfall. Eine komplette Palette SHRIMPBOOST ist in Becken 3 gekippt. "
            + "In DAS Becken. Das mit Klausis Gewerkschafts-Shrimps. Dr. Perla misst 'Werte, für die es "
            + "noch keine Skala gibt'. Die Shrimps schwimmen jetzt in Formation. Im Kreis. GEGEN den "
            + "Uhrzeigersinn. Klausi nennt es 'Streikposten'.",
            c("Becken abriegeln und beobachten.", "Dr. Perla bezieht mit Klemmbrett und Schlafsack "
                + "Stellung vor Becken 3.", grantFlag("boost_beobachtet")).then("boost_2"),
            c("Vertuschen. Hier ist NICHTS passiert. (-800)", "Olaf bekommt Schweigegeld und ein "
                + "neues Poloshirt. Becken 3 brodelt weiter.", money(-800), grantFlag("boost_vertuscht")).then("boost_2"),
            c("General Krillkill informieren.", "Krillkill starrt lange ins Becken. Dann flüstert er: "
                + "'Potenzial.'", rep(-2), grantFlag("boost_krillkill")).then("boost_2"));

        chain("boost_2", GameCharacter.PRESS, "Klausi 2.0 (per Morse-Klopfen)", 0,
            "Die Schwimmenden Räte",
            "Die Mutation ist stabil: Becken 3 hat ein ZENTRALKOMITEE gegründet. Klausi 2.0 "
            + "kommuniziert jetzt per Morse-Klopfen gegen die Scheibe und fordert: Tarifvertrag, "
            + "Algen-Prämie und ein Veto bei der Beckenbeleuchtung. Dr. Perla: 'Wissenschaftlich "
            + "faszinierend.' Mira: 'Juristisch ein Albtraum.'",
            c("Verhandeln - den Betriebsrat anerkennen.", "Historisch! Die Gewerkschafts-Edikte "
                + "(Betriebsrat/Überwachung) sind im HQ-Kommando verfügbar.", rep(8), grantFlag("shrimp_union")).then("boost_3"),
            c("Überwachung installieren. (-1500)", "Kameras, Spitzel-Shrimps, Aktenordner. Die "
                + "Gewerkschafts-Edikte sind im HQ-Kommando verfügbar - Klausi 2.0 weiß davon.",
                money(-1500), rep(-4), grantFlag("shrimp_union")).then("boost_3"),
            c("Ignorieren. Es sind GARNELEN.", "Klausi 2.0 notiert deine Antwort. Wortwörtlich. In "
                + "ein winziges Protokollbuch.", rep(-2)).then("boost_3"));

        chain("boost_3", GameCharacter.PRESS, "Eilmeldung aus Halle 1", 2,
            "DER BECKEN-COUP",
            "Es ist passiert: Becken 3 hat über Nacht die Futterklappe besetzt und Becken 4 "
            + "'befreit'. Die Räte kontrollieren zwei Becken und haben eine Fahne aus Algen gehisst. "
            + "Klausi 2.0 verliest Forderungen. General Krillkill steht daneben - widerwillig "
            + "beeindruckt: 'REKRUT. DIESE FORMATION. Diese Shrimps haben TALENT.'",
            c("Friedlich verhandeln: Algen-Prämie. (-2000)", "Die Fahne bleibt, die Arbeit geht "
                + "weiter. Ein ziviler Kompromiss.", money(-2000), rep(8), grantFlag("shrimp_union"),
                grantFlag("coup_frieden")).then("boost_4"),
            c("Krillkill lässt räumen.", "Der Coup endet in Minute vier. Ein paar Räte 'emigrieren' "
                + "in den Filter.", multShrimp(0.92), rep(-5), grantFlag("coup_gewalt")).then("boost_4"),
            c("Klausi 2.0 kaufen. (-3000)", "Der Revolutionsführer hat jetzt ein Einzel-Becken mit "
                + "Whirlpool-Funktion. Die Räte nennen ihn 'Verräter', aber leiser.", money(-3000),
                grantFlag("coup_korruption")).then("boost_4"));

        chain("boost_4", GameCharacter.KRILLKILL, null, 1,
            "SHRIMP TEAM SIX",
            "Krillkill salutiert: 'Rekrut. Ich habe die sechs talentiertesten Mutanten aus Becken 3 "
            + "rekrutiert. Taktisches Denken. Verstärkte Panzerung. Opponierbare Scheren. Ich "
            + "präsentiere: SHRIMP TEAM SIX. Sie brauchen nur einen Auftrag. Und winzige "
            + "Nachtsichtgeräte. Die habe ich bereits bestellt. Von deinem Geld.'",
            c("Einsatzbefehl erteilen!", "STS-6 ist einsatzbereit! Wähle Einsätze im HQ-Kommando "
                + "(Almanach): Spionage, Werkschutz oder verdeckte PR.", money(-500), unlock("sts6")).then("boost_5"),
            c("Nur zur Verteidigung, General.", "Krillkill seufzt, akzeptiert aber. STS-6 im "
                + "HQ-Kommando verfügbar.", unlock("sts6"), rep(2)).then("boost_5"),
            c("Das ist eine SCHRECKLICHE Idee.", "Krillkill macht es trotzdem. Die Nachtsichtgeräte "
                + "waren nicht stornierbar. STS-6 im HQ-Kommando verfügbar.", unlock("sts6")).then("boost_5"));

        chain("boost_5", GameCharacter.MAYOR, "Ministerialrat Dr. Feuchtwanger (Referat Krustentier-Sicherheit)", 2,
            "Die Regierung klopft an",
            "Ein Herr im grauen Anzug: 'Uns liegen Berichte vor über... intelligente Garnelen mit "
            + "Gewerkschaftsbuch. Und eine private Spionageeinheit aus Meeresfrüchten. Das Ministerium "
            + "hat dafür KEIN Formular. Wir mussten eines ERFINDEN: Formular X-0/Shrimp. Füllen Sie es "
            + "aus, bevor wir Sie zum Sicherheitsrisiko erklären müssen.'",
            c("Voll kooperieren: Register & Aufsicht.", "Deine Shrimps sind jetzt die einzige amtlich "
                + "registrierte Krustentier-Gewerkschaft Europas.", rep(6), grantFlag("gov_koop")).then("boost_6"),
            c("Lobbyisten engagieren. (-4000)", "Das Formular verschwindet in einem Ausschuss. Auf "
                + "unbestimmte Zeit.", money(-4000), grantFlag("gov_lobby")).then("boost_6"),
            c("'Welche Garnelen?'", "Dr. Feuchtwanger blinzelt nicht. Er notiert: 'unkooperativ'. Das "
                + "Ministerium beobachtet dich jetzt. Auch mit Teleobjektiv.", rep(-6), grantFlag("gov_leugnen")).then("boost_6"));

        chain("boost_6", GameCharacter.PRESS, "Die Friedenskonferenz (Live-Übertragung)", 1,
            "Die Friedenskonferenz von Becken 3",
            "Historischer Tag: Am runden Tisch (einem Planschbecken) sitzen Klausi 2.0 für das "
            + "Zentralkomitee, General Krillkill für die Armee, Dr. Feuchtwanger für das Ministerium - "
            + "und du. Tagesordnung: ein dauerhafter Becken-Frieden. Krillkill will die Mutanten, "
            + "Klausi will Mitbestimmung, das Ministerium will vor allem, dass NIEMAND davon erfährt.",
            c("Der Becken-Frieden: Mitbestimmung für alle. (-5000)", "Der 'Vertrag von Becken 3' geht "
                + "in die Geschichte der Krustentier-Diplomatie ein. Es gibt eine Gedenk-Alge.",
                money(-5000), rep(15), grantFlag("becken_frieden")),
            c("Geheimprotokoll: STS-6 wird Staatsauftragnehmer. (+8000)", "Das Ministerium zahlt "
                + "fürstlich für 'Dienstleistungen'. Fragen stellt hier niemand mehr.", money(8000), rep(-4)),
            c("Krillkill wird Beckenminister.", "Militärverwaltung. Die Algen-Fahne wird eingezogen, "
                + "Klausi 2.0 geht ins Exil (Becken 7). Krillkill weint vor Stolz.", rep(-6), grantFlag("becken_militaer")));
    }

    // ===================== USBEKISTAN-VERFLECHTUNGEN =====================
    // Drei Brücken-Quests verzahnen Becken-3-Errungenschaften (STS-6, Gewerkschaft) mit der
    // Akwanov-Story - über Condition.unlock() gated: Wer den Becken-3-Vorfall nie freigeschaltet
    // hat, sieht sie schlicht nie; die Akwanov-Kette selbst bleibt unangetastet.
    // Dazu zwei reine Dmitri-Quests, die nur am Akwanov-Fortschritt hängen.
    private static void usbekistan() {
        auto("sts6_dmitri", GameCharacter.KRILLKILL, null,
            Condition.all(Condition.unlock("sts6"), Condition.questDone("akwanov_4"), Condition.rival(15)), 1,
            "Gegenspionage: Zielperson Dmitri",
            "REKRUT! Shrimp Team Six meldet Vollzug: Zielperson 'Praktikant Dmitri' wurde beim "
            + "Fotografieren von Becken 3 gestellt. Er behauptet, es sei 'für sein "
            + "Praktikums-Scrapbook'. STS-6 hat ihm daraufhin einen Wackelpudding als "
            + "'Prototyp-Mutant' untergeschoben. Er hat ihn bereits per Diplomatenpost nach "
            + "Taschkent verschickt. Wie verfahren wir weiter?",
            c("Desinformation fortsetzen!", "Usbekistans Militärlabor forscht jetzt an Wackelpudding. "
                + "Akwanov gratuliert dir persönlich zu diesem Zug.", rival(-8), rep(2)),
            c("Dmitri konfrontieren.", "Dmitri gesteht sofort ALLES - auch Dinge, die niemand gefragt "
                + "hat. Er weint ein bisschen. STS-6 tröstet ihn.", rival(5)),
            c("Dmitri zum STS-6-Ehrenmitglied ernennen.", "Er bekommt eine winzige Urkunde und weint "
                + "schon wieder. Loyalität: unklar. Begeisterung: grenzenlos.", rival(-5), grantFlag("dmitri_sts6")));

        auto("sts6_taschkent", GameCharacter.KRILLKILL, null,
            Condition.all(Condition.unlock("sts6"), Condition.rival(45)), 1,
            "Operation Trockenhafen",
            "Shrimp Team Six ist in Taschkent eingesickert - im Aquarium eines Diplomatenkoffers. "
            + "Der Bericht: Akwanovs 'größte Garnelen-Halle der Welt' besteht zu 80 Prozent aus "
            + "Pappkulisse. Echte Becken: vier. Betreut von Dmitris Cousin Ruslan, der ebenfalls "
            + "'Praktikant' ist. Seit elf Jahren.",
            c("An die Presse leaken!", "'POTEMKINSCHE GARNELEN' titelt der Boulevard. Akwanov nennt "
                + "es 'kreative Rauminszenierung'.", rep(8), rival(15)),
            c("Akwanov diskret erpressen. (+6000)", "Er zahlt zähneknirschend - und schickt dir im "
                + "Gegenzug eine Rechnung über 'Beratungsleistungen'.", money(6000), rival(10)),
            c("Das Geheimnis für später aufheben.", "Wissen ist Macht. Akwanov spürt, dass du etwas "
                + "weißt, und wird nervös höflich.", rival(-5), grantFlag("akwanov_geheimnis")));

        auto("union_akwanov", GameCharacter.PRESS, "Klausi 2.0 (Solidaritäts-Morsen)",
            Condition.all(Condition.unlock("shrimp_union"), Condition.questDone("akwanov_2")), 2,
            "Die Krustentier-Internationale",
            "Klausi 2.0 hat vom Embargo erfahren und plant den nächsten Schritt: Solidaritäts-Morsen "
            + "an die usbekischen Hallen-Shrimps. 'ARBEITER-GARNELEN ALLER LÄNDER...' Akwanov ruft an, "
            + "zum ersten Mal ohne Teeglas: 'Ihre Gewerkschaft AGITIERT meine Bestände! Meine Shrimps "
            + "fordern PAUSEN. Was ist eine Pause?!'",
            c("Solidarität! Die Botschaft geht raus.", "In Taschkent treiben die Shrimps in "
                + "Streik-Formation. Akwanov altert sichtbar.", rival(15), rep(6)),
            c("Klausi 2.0 bremsen (Algen-Prämie, -1500).", "Die Internationale wird vertagt. Bis auf "
                + "Weiteres. Klausi notiert es im Protokollbuch.", money(-1500), rival(-5)),
            c("Als Verhandlungsmasse nutzen. (+3000)", "Du verkaufst Akwanov "
                + "'Gewerkschafts-Abwehr-Beratung'. Klausi 2.0 nennt dich 'Klassenfeind', schwimmt "
                + "aber weiter für dich.", money(3000), rival(5), rep(-3)));

        auto("dmitri_zeugnis", GameCharacter.AKWANOV, "Praktikant Dmitri (mit 412-Seiten-Bericht)",
            Condition.all(Condition.questDone("akwanov_4"), Condition.money(200_000)), 0,
            "Dmitris Zwischenzeugnis",
            "Dmitri steht im Büro: 'Chef-Boss, Universität Taschkent braucht Zwischenzeugnis für "
            + "Praktikum.' Sein beigelegter Praktikumsbericht umfasst 412 Seiten, davon 380 "
            + "maßstabsgetreue Zeichnungen deiner Ventilanlagen. Kapitel 7 heißt 'Schwachstellen'. "
            + "Er hat alles selbst gebunden. Mit Wasserzeichen.",
            c("Zeugnis: 'stets bemüht und SEHR gründlich'.", "Dmitri rahmt das Zeugnis. Akwanov rahmt "
                + "Kapitel 7.", rival(-4), rep(2)),
            c("Bericht 'zur Korrektur' einbehalten.", "Dmitri nickt verständnisvoll. Am nächsten Tag "
                + "liegt trotzdem eine Kopie in Taschkent - er hatte Durchschlagpapier.", rival(8)),
            c("Ihm ECHTE Aufgaben geben: Pumpenwartung.", "Die Pumpen liefen nie besser. Dmitri weint "
                + "vor Stolz in den Wasserkreislauf (minimal salziger).", water(40), rep(2), rival(-2)));

        auto("dmitri_kantine", GameCharacter.AKWANOV, null,
            Condition.all(Condition.questDone("akwanov_5"), Condition.rep(60)), 0,
            "Der Praktikanten-Poker",
            "Akwanov ruft an: 'Mein Freund, es ist... peinlich. Dmitri weigert sich, nach Taschkent "
            + "zurückzukehren. Er sagt, Ihre Kantine hat DONNERSTAGS SCHNITZEL. Ich biete einen "
            + "Tausch: Sie erhalten dafür zwei NEUE Praktikanten. Sehr loyal. Sehr... verwandt mit mir.'",
            c("Dmitri bleibt. (Schnitzel-Garantie)", "Dmitri jubelt. Er spioniert jetzt offiziell für "
                + "BEIDE Seiten und ist damit der transparenteste Agent der Geschichte.", rival(5), rep(3)),
            c("Tausch akzeptieren.", "Die Neffen sind höflich, fotografieren nichts und arbeiten "
                + "hart. SEHR verdächtig.", rival(-8)),
            c("Dmitri selbst entscheiden lassen.", "Er wählt 'beide Länder, im Wechsel, je nach "
                + "Speiseplan'. Ein Pendel-Praktikant.", rep(5)));
    }

    // ===================== EINZELQUESTS =====================
    private static void einzelquests() {
        auto("kat_blackout", GameCharacter.MAYOR, "Technischer Leiter Olaf", Condition.day(90), 2,
            "Es wird dunkel im Becken",
            "Blackout, Chef. Die Shrimps schwimmen im Dunkeln und werden nervös. Eine hat gerade eine "
            + "andere angeschaut. So fangen Massenpaniken an.",
            c("Notaggregat anwerfen. (-600)", "Laut, stinkt, aber Strom ist da.", money(-600), rep(-3)),
            c("Shrimps mit Kerzen beruhigen. (-100)", "Romantisch und leicht brandgefährlich.", money(-100), multShrimp(0.97)),
            c("Aushalten.", "Stress im Becken. Olaf ist enttäuscht.", multShrimp(0.9)));

        auto("kat_algen", GameCharacter.PRESS, "Der Algen-Praktikant",
            Condition.all(Condition.buildCount(BuildingType.ALGAE_FARM, 2), Condition.day(150)), 0,
            "Ähm... die Algen machen Probleme",
            "Es gibt jetzt VIEL mehr Algen. Sie wachsen aus den Steckdosen. Eine hat einen Namen "
            + "bekommen. Gute Nachricht: Futter im Überfluss. Schlechte: Die Algen wollen Miete.",
            c("Überschuss-Futter verkaufen.", "Schnelles Geld, volles Lager.", money(400), feed(20)),
            c("Algen-Smoothies an Touristen.", "Hype! (Jemand wird kurz grün im Gesicht.)", money(700), rep(-2)));

        auto("presse_kritiker", GameCharacter.PRESS, "Presse (anonym)",
            Condition.all(Condition.buildCount(BuildingType.RESTAURANT, 1), Condition.rep(50)), 0,
            "Ein Kritiker kommt incognito",
            "Morgen testet der gefürchtete Gourmet-Kritiker Sebastian Schlemmerle anonym dein "
            + "Restaurant. Er hat schon eine Auster persönlich beleidigt.",
            c("Nur Spitzen-Shrimps servieren! (-300)", "'Empfohlen von Schlemmerle' - ein Ritterschlag.", money(-300), rep(20)),
            c("Mit Charme und Show ablenken.", "Die Show kommt an.", rep(5)),
            c("Normale Karte geben.", "Solide, unspektakulär.", rep(3)));

        auto("kat_moewen", GameCharacter.MAYOR, "Technischer Leiter Olaf (mit Besen)", Condition.day(200), 2,
            "Sie sind durchs Dach!",
            "MÖWEN, CHEF! Ein Schwarm hält unsere Becken für ein All-you-can-eat-Buffet. Eine wohnt "
            + "jetzt auf dem HQ. Sie heißt angeblich Kevin.",
            c("Profi-Falkner engagieren. (-500)", "Möwen weg, humane Lösung.", money(-500), rep(2)),
            c("Becken mit Netzen sichern. (-250)", "Etwas Verlust, aber künftig sicher.", money(-250), multShrimp(0.95)),
            c("Kevin adoptieren.", "Kevin ist jetzt Maskottchen. Frisst aber kräftig mit.", multShrimp(0.85), rep(8)));

        auto("beh_steuer", GameCharacter.MAYOR, "Finanzamt (Herr Pfennig)", Condition.money(100_000), 2,
            "Wir hätten da ein paar Fragen",
            "Sie haben '14.000 Garnelen als Geschäftspartner' geltend gemacht. Auch 'romantische "
            + "Beckenbeleuchtung - Bewirtungskosten' wirft Fragen auf.",
            c("Saubere Bücher vorlegen.", "Kleine Nachzahlung, reines Gewissen.", money(-1200), rep(5)),
            c("Steuerberater einfliegen lassen. (-1500)", "Herr Pfennig wird fast freundlich.", money(-1500), rep(1)),
            c("Es drauf ankommen lassen.", "Bußgeld und schlechte Presse.", money(-2000), rep(-6)));

        auto("idee_spa", GameCharacter.MAYOR, "Assistentin Mira (mit Pitch-Deck)",
            Condition.all(Condition.money(120_000), Condition.unlock("zone.FORSCHUNG")), 0,
            "Chef, hören Sie mir KURZ zu",
            "Stell dir vor: ein Shrimp-Spa. Menschen lassen sich von Garnelen die Füße maniküren. "
            + "Premium-Wellness! Was kann schon schiefgehen?",
            c("Lass es uns wagen! (-2000)", "Der Shrimp-Spa wird ein Hit (meistens).", money(-2000), rep(6)),
            c("Zu riskant, Mira.", "Gute Mitarbeiterführung.", rep(1)),
            c("Mach es - mit Haftungsausschluss! (-1200)", "Sicher und solide.", money(-1200), rep(2)));

        auto("geld_investor", GameCharacter.MAYOR, "Investorin Frau Goldberg (Sonnenbrille, drinnen)",
            Condition.all(Condition.money(140_000), Condition.rep(70)), 1,
            "Ich rieche hier Einhorn-Potenzial",
            "Dein 'Shrimp-as-a-Service'-Modell ist disruptiv. Ich biete Kapital im Tausch gegen Anteile "
            + "und ein paar 'Synergien'. Wir bauen ein Garnelen-Imperium. Oder ein hübsches IPO.",
            c("Deal annehmen (Anteile).", "Frisches Geld, aber Goldberg redet jetzt mit.", money(10_000), grantFlag("investor")),
            c("Nur ein Kredit, keine Anteile.", "Du behältst die Kontrolle.", money(5000)),
            c("Ich brauche niemanden.", "Selfmade-Image. Respekt.", rep(5)));
    }

    // ===================== GENERAL KRILLKILL =====================
    private static void krillkill() {
        auto("krillkill_intro", GameCharacter.KRILLKILL, null,
            Condition.all(Condition.day(250), Condition.buildCount(BuildingType.SHRIMP_TANK, 2), Condition.shrimpProduced(1200)), 2,
            "GENERAL KRILLKILL ÜBERNIMMT DAS KOMMANDO",
            "STILLGESTANDEN! General Krillkill, 1. Krustentier-Division. Ich beobachte deinen Laden aus "
            + "dem Lüftungsschacht. Du züchtest VORSPEISEN! Während DA DRAUSSEN die Suppe näher kommt! "
            + "(Frag nicht, was die Suppe ist, Rekrut - dafür bist du noch nicht bereit.) Lass MICH aus "
            + "diesen Garnelen Soldaten machen. Protein. Kampfkraft. Sag JA, Rekrut!",
            c("Jawohl, General!", "Die Presse fragt, wer der Mann mit dem Helm ist.", rep(-3)).then("krillkill_1"),
            c("Wer hat Sie reingelassen?", "Er campiert jetzt offiziell im Lüftungsschacht.", rep(0)).then("krillkill_1"));

        chain("krillkill_1", GameCharacter.KRILLKILL, null, 0,
            "GRUNDAUSBILDUNG",
            "Erste Lektion: Ein Soldat, der ZU GUT lebt, wird FETT UND FEIG. Ich brauche eine "
            + "Trainingshalle. Bau mir eine Stelle, wo ich diese Weichlinge härten kann!",
            c("Neues Drill-Becken bauen!", "Ein Becken mit Krillkill-Wimpel. Die Härtung beginnt.", grantFlag("krillkill")).then("krillkill_2"),
            c("Bestehendes Becken umwidmen.", "Zwei Tage Härtungsphase, null Output. HUI!", multShrimp(0.95)).then("krillkill_2"));

        chain("krillkill_2", GameCharacter.KRILLKILL, null, 0,
            "ERNÄHRUNGS-DOKTRIN",
            "Algen?! Du fütterst meine Truppe mit SALAT?! Soldaten brauchen PROTEIN. Doppelte Rationen, "
            + "kein Pardon. Krieg ist teuer, Rekrut. Frieden ist teurer - der hat keine Shrimps.",
            c("Doppelte Rationen!", "Größere, schwerere Shrimps. Der General nickt zufrieden.", feed(-15), rep(0)).then("krillkill_3"),
            c("Sparflamme, General.", "Er ist enttäuscht. 'Die Suppe lacht über uns.'", rep(0)).then("krillkill_3"),
            c("Experiment: SHRIMPBOOST ins Futter mischen.", "Die Shrimps zucken synchron. Krillkill notiert: "
                + "'VIELVERSPRECHEND.' Dr. Perla notiert: 'BEDENKLICH.'", grantFlag("boost_futter")).then("krillkill_3"));

        chain("krillkill_3", GameCharacter.KRILLKILL, null, 0,
            "DAS BOOTCAMP",
            "Meine Ausbilder sind ÜBERFORDERT. Wir brauchen ein BOOTCAMP. Schlamm. Hindernisse. Eine "
            + "Kletterwand für Wesen ohne Arme. Da werden aus Zivilisten KÄMPFER!",
            c("Bootcamp errichten! (-900)", "Bootcamp steht. Arbeiter-Politik 'Bootcamp' freigeschaltet!", money(-900), grantFlag("krillkill.bootcamp")).then("krillkill_4"),
            c("Erst sparen, General.", "Er kommentiert ab jetzt jeden Tag bissiger.", rep(0)).then("krillkill_4"));

        chain("krillkill_4", GameCharacter.KRILLKILL, null, 0,
            "FELDTEST gegen Languste Brenda",
            "Es wird Zeit für einen FELDTEST! Der Gegner: eine aggressive Languste namens Brenda. Sie "
            + "hat schon zwei meiner besten Männer auf dem Gewissen. Heute zahlen wir es ihr heim!",
            c("Frontalangriff!", "Sieg über Brenda! Kriegsbeute kassiert. Militär-Depot & Logistik frei!",
                money(1500), rep(5), unlock("zone.LOGISTIK"), unlock("build.military")).then("krillkill_5"),
            c("Vorsichtig herantasten.", "Langsam, aber sicher. Militär-Depot & Logistik freigeschaltet.",
                unlock("zone.LOGISTIK"), unlock("build.military")).then("krillkill_5"),
            c("Brenda... verhandeln?", "MIT DEM FEIND REDEN?! Der General ist fassungslos.", rep(0)).then("krillkill_5"));

        chain("krillkill_5", GameCharacter.KRILLKILL, null, 0,
            "DIE PROTEIN-BOMBE",
            "Mir fehlt der proteinreichste Shrimp, den die Schöpfung je verkraftet hat. Ein Tier, das "
            + "eine Gabel verbiegt. Wir brauchen das LABOR. Ich HASSE Wissenschaft. Aber ich hasse die Suppe mehr.",
            c("Genetik hochfahren!", "Protein-Bombe freigeschaltet! Tierschützer stellen Fragen.", unlock("tier.PROTEIN"), rep(-4)).then("krillkill_6"),
            c("Nur natürliche Zucht.", "Langsamer, aber sauberer. Protein-Bombe freigeschaltet.", unlock("tier.PROTEIN")).then("krillkill_6"));

        chain("krillkill_6", GameCharacter.KRILLKILL, null, 1,
            "ERSTE GARDE",
            "DA STEHT SIE. KAMPF-KRILL. Augen wie Stahl, Panzer wie Beton. Ich könnte heulen. Tu ich "
            + "aber nicht. *(Er heult ein bisschen.)* Jetzt züchte mir eine GARDE!",
            c("Massenproduktion!", "Kampf-Krill in voller Stärke freigeschaltet! Maximaler Output.", unlock("tier.WARKRILL")).then("krillkill_7"),
            c("Elite-Zucht, kleine Stückzahl.", "Kampf-Krill freigeschaltet - jeder ein Premium-Soldat.", unlock("tier.WARKRILL")).then("krillkill_7"));

        chain("krillkill_7", GameCharacter.KRILLKILL, null, 1,
            "DIE WAHRHEIT ÜBER DIE SUPPE",
            "Setz dich, Rekrut. Früher war ich der jüngste Küchenchef der Marine. Bei einem Bankett "
            + "sah ich in den Topf - und der Kleinste, ein krummes Kerlchen, sah ZURÜCK. Und salutierte. "
            + "Mit einer Schere. Ich warf den Topf um und schwor: nie wieder Vorspeise, nur noch SOLDATEN. "
            + "'Die Suppe', Rekrut... die Suppe bin ich.",
            c("Wir benennen den ersten Kampf-Krill nach ihm.", "Esprit de Corps! Der General bleibt als sanfterer Mentor.", rep(5)),
            c("General, Sie brauchen Urlaub.", "Ansichtskarte aus Bremerhaven: 'Habe Brenda getroffen. Wir verstehen uns jetzt.'", rep(2)),
            c("Danke, General.", "Ein schlichter, ehrlicher Abschluss. Das Denkmal des krummen Kerlchens entsteht.", rep(1)));
    }

    // ===================== AUSSENMINISTER AKWANOV =====================
    private static void akwanov() {
        auto("akwanov_intro", GameCharacter.AKWANOV, null, Condition.money(120_000), 2,
            "NEUER RIVALE: Außenminister Akwanov",
            "Aaah, der berühmte Shrimp-Tycoon! Ivan Akwanov, Außenminister Usbekistans. Mein Land hat "
            + "KEIN Meer - wir sind doppelt gesegnet. Und doch züchten wir die Garnele der Zukunft. In "
            + "HALLEN. Wie Sie. Nur größer. Ich biete Ihnen meine Freundschaft an - günstiger als das, "
            + "was sonst kommt.",
            c("Charmiert zurücklächeln.", "Er notiert sich das mit einem öligen Lächeln.", rival(0)).then("akwanov_1"),
            c("Usbekistan hat doch nicht mal Wasser.", "Er liebt eine Herausforderung. Die Rivalität beginnt.", rival(10)).then("akwanov_1"));

        chain("akwanov_1", GameCharacter.AKWANOV, null, 0,
            "Die Charme-Offensive",
            "Ein Geschenk: ein Beratervertrag. Völlig kostenlos - nun ja, beratend kostenlos. Im "
            + "Gegenzug teilen wir nur ein paar harmlose Betriebsdaten. Unterschreiben Sie auf Seite 47, "
            + "in der Mitte des Absatzes, den niemand liest.",
            c("Vertrag unterschreiben.", "Beratungshonorar kassiert. (Er weiß jetzt mehr, als gut ist.)", money(1500), rival(-5), grantFlag("dataLeak")).then("akwanov_2"),
            c("Höflich ablehnen.", "'Ich mag Menschen, die ihre eigenen Akten lesen.'", rival(0)).then("akwanov_2"),
            c("Vertrag öffentlich zerreißen!", "Die Presse liebt den Trotz.", rep(8), rival(15)).then("akwanov_2"));

        chain("akwanov_2", GameCharacter.AKWANOV, null, 2,
            "Der Trockenhafen-Tarif",
            "Usbekistan führt einen 'Solidaritätsbeitrag für Binnenländer' ein. Eine kleine, "
            + "symbolische Abgabe auf Ihre Garnelen. Etwa so klein wie unser Küstenstreifen.",
            c("Schlucken und weiterproduzieren.", "Embargo aktiv: -15% auf alle Marktpreise.", tariff(0.15)).then("akwanov_3"),
            c("Gegen-Lobby anheuern. (-2000)", "Abgabe halbiert.", money(-2000), tariff(0.075), rival(10)).then("akwanov_3"),
            c("Inlandsmarkt ausbauen.", "Tipp: Restaurants sind weniger anfällig fürs Embargo.", tariff(0)).then("akwanov_3"));

        chain("akwanov_3", GameCharacter.AKWANOV, null, 0,
            "Die Plankton-Diplomatie",
            "Der weltgrößte Plankton-Großhändler - ein Freund meiner Tante im Handelsministerium - "
            + "erwägt, exklusiv an Usbekistan zu liefern. Ihre Algenfarmen könnten... durstig werden.",
            c("Eigenen Liefervertrag sichern. (-2500)", "Versorgung gesichert.", money(-2500), rep(5), rival(5)).then("akwanov_4"),
            c("Abwarten.", "Das Futterlager schrumpft über Nacht.", feed(-30)).then("akwanov_4"),
            c("Akwanovs Tante 'kennenlernen'. (-1000)", "Ein Bonus-Kontakt für später.", money(-1000), rival(-5), grantFlag("tante")).then("akwanov_4"));

        chain("akwanov_4", GameCharacter.AKWANOV, null, 2,
            "Praktikant aus Taschkent",
            "Usbekistan entsendet einen Austausch-Praktikanten: Dmitri. Sehr lernbegierig. Er stellt "
            + "erstaunlich viele Fragen darüber, wo genau Ihr Hauptwasserventil sitzt.",
            c("Dmitri herzlich aufnehmen.", "Sabotage! Das Wasserlager leert sich teilweise.", water(-40), rep(-3)).then("akwanov_5"),
            c("Spionageabwehr aktivieren. (-1800)", "Sabotage stark reduziert. Dauerhafter Schutz.", money(-1800), grantFlag("sabotageResist"), rival(10)).then("akwanov_5"),
            c("Dmitri umdrehen. (-1200)", "'Verräter. Wie unternehmerisch von Ihnen.'", money(-1200), grantFlag("doppelagent"), rival(15)).then("akwanov_5"));

        chain("akwanov_5", GameCharacter.AKWANOV, null, 2,
            "Akten, die niemand lesen sollte",
            "Mir wurde ein Ordner zugespielt. Ihr Ordner. Besonders die Margen sind interessant. Ich "
            + "erwäge, der Presse einen selektiven Einblick zu gewähren. 'Garnelen-Baron beutet Plankton aus.'",
            c("Gegen-Leak veröffentlichen!", "Akwanovs eigene Akten leaken. 'Wer hat... DMITRI!'", rep(6), rival(20)).then("akwanov_6"),
            c("PR-Agentur engagieren. (-3000)", "Schaden begrenzt.", money(-3000), rep(-3), rival(5)).then("akwanov_6"),
            c("Den Schlag einstecken.", "Die Schmierkampagne sitzt.", rep(-12)).then("akwanov_6"));

        chain("akwanov_6", GameCharacter.AKWANOV, null, 2,
            "Das große Embargo",
            "Usbekistan verhängt ein VOLLEMBARGO auf ausländische Hallen-Garnelen. Kein Export ohne "
            + "meinen Stempel. Und meine Stempel sind gerade alle in der Reinigung. Auf unbestimmte Zeit.",
            c("Schattenmarkt 'Trockenhafen Taschkent' öffnen.", "Ironie pur: Du verkaufst jetzt AN Usbekistan. Schwarzmarkt frei.", tariff(0), unlock("build.blackmarket"), rival(-10)).then("akwanov_7"),
            c("Welthandels-Klage einreichen. (-4000)", "Nach zähem Verfahren fällt das Embargo.", money(-4000), tariff(0), rival(25)).then("akwanov_7"),
            c("Eigenen Inlandsmarkt hochziehen.", "Regionale Wertschöpfung - aber das Embargo bleibt vorerst.", rep(4), tariff(0.4)).then("akwanov_7"));

        chain("akwanov_7", GameCharacter.AKWANOV, null, 2,
            "Der Stromstecker-Vorfall",
            "Ihr Stromnetz hängt an einer Börse, an der Usbekistan gerade aggressiv investiert. Es "
            + "wäre ein JAMMER, wenn heute Nacht die romantische Beckenbeleuchtung ausginge. Garnelen "
            + "sind im Dunkeln so schreckhaft.",
            c("Eigenes Solar-Inselnetz ausbauen.", "Strompreis-unabhängig und grün.", rep(5), rival(10)).then("akwanov_8"),
            c("Notreserve anzapfen. (-2500)", "Spike abgefedert.", money(-2500)).then("akwanov_8"),
            c("Es drauf ankommen lassen.", "Teurer Betrieb für ein paar Tage.", money(-1500)).then("akwanov_8"));

        chain("akwanov_8", GameCharacter.AKWANOV, null, 1,
            "Meer der Entscheidungen (Finale)",
            "Mein würdiger, halsstarriger Rivale. Wir haben uns bekriegt, belauert, beleakt - es war "
            + "WUNDERBAR. Vielleicht ist es Zeit für etwas Größeres. Oder Sie zwingen mich, meine "
            + "letzte, hässlichste Akte zu öffnen. Ich habe Tee mitgebracht. Und einen Vertrag.",
            c("Die Überraschungs-Allianz.", "Die 'Vereinigte Hallen-Garnelen-Union'! Embargo dauerhaft weg, +Verkaufspreis.", tariff(0), rep(15), grantFlag("akwanov_ally")),
            c("Totale Eskalation!", "Dann KRIEG, mein Freund. Wirtschaftskrieg - mit Dauer-Embargo.", tariff(0.2)),
            c("Der feindliche Aufkauf. (-40000)", "'Sie wollen mein Ministerium KAUFEN? Ich nehme an.'", money(-40_000), rep(10)));
    }
}
