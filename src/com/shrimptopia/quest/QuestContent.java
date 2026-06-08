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
        behoerden();
        tierschutz();
        influencer();
        krabbo();
        einzelquests();
        krillkill();
        akwanov();
        conflicts();
        endings();
        attachObjectives();
    }

    // ===================== KONFLIKTE (Armee-Stärke vs. Bedrohung) =====================
    private static void conflicts() {
        auto("conf_border", GameCharacter.AKWANOV, null, Condition.any(Condition.rival(12), Condition.day(45)), 2,
            "Garnelen-Grenzzwischenfall",
            "Eine usbekische 'Forschungsdrohne' verirrt sich über deine Halle. Akwanov: 'Reiner Zufall, mein "
            + "Freund. Wir kartieren nur... Ihre Lüftungsschächte.' General Krillkill steht schon stramm.",
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

        auto("conf_siege", GameCharacter.AKWANOV, null, Condition.all(Condition.rival(60), Condition.money(50000)), 2,
            "Die Belagerung",
            "Akwanov riegelt die Zufahrt komplett ab. 'Ein doppelt eingeschlossenes Land weiß, wie man jemanden "
            + "einschließt.' Jetzt zählt nur die Armee.",
            c("Bis zum letzten Krill verteidigen", "Alles oder nichts.", QuestEffect.battle(200, 7000)),
            c("Roboter-Blitz", "Die Maschinen-Brigade rollt.", QuestEffect.battle(170, 6000)),
            c("Kapitulieren (-6000, -Ruf)", "Du beugst dich. Vorerst.", money(-6000), rep(-8), rival(-20)));

        auto("conf_war", GameCharacter.KRILLKILL, null,
            Condition.all(Condition.rival(66), Condition.money(80000), Condition.army(200)), 1,
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
        endingQ("end_imperator", GameCharacter.MAYOR, Condition.money(500000),
            "ENDE: Shrimp-Imperator",
            "500.000 auf dem Konto. Du sitzt auf einem Thron aus Garnelen-Schalen, die halbe Stadt arbeitet "
            + "für dich, und irgendwo weint ein Buchhalter vor Glück. Der reichste Krustentier-Magnat der Geschichte.",
            c("Lang lebe der Imperator!", "REICHTUMS-ENDE erreicht!"));

        endingQ("end_vertical", GameCharacter.ADVISOR, Condition.all(Condition.money(150000),
                Condition.resource("robots", 40), Condition.resource("boost", 200), Condition.resource("shells", 1000)),
            "ENDE: Vertikal integriert",
            "Schalen werden zu Energydrink, Energydrink zu Robotern, Roboter zu noch mehr Robotern. Greg nickt "
            + "anerkennend aus dem Wasserglas. Du hast keine Farm mehr - du hast ein selbstlaufendes Industrie-Imperium.",
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
        // Story-Ketten
        obj("beh_pruefung", Condition.buildCount(BuildingType.SHRIMP_TANK, 4), "Betreibe 4 Shrimp-Becken");
        obj("beh_plakette", Condition.rep(60), "Reputation auf 60 bringen");
        obj("tier_demo", Condition.tierProduced(ShrimpTier.BIO, 200), "Produziere 200 Bio-Shrimps");
        obj("tier_gewerkschaft", Condition.sold(500), "Verkaufe insgesamt 500 Shrimps");
        obj("inf_sponsor", Condition.money(15000), "Erreiche 15.000 Vermögen");
        obj("inf_cancel", Condition.rep(70), "Reputation auf 70 bringen");
        obj("konk_angebot", Condition.money(30000), "Erreiche 30.000 Vermögen");
        // General Krillkill
        obj("krillkill_1", Condition.shrimpProduced(300), "Produziere insgesamt 300 Shrimps");
        obj("krillkill_2", Condition.shrimpProduced(800), "Produziere insgesamt 800 Shrimps");
        obj("krillkill_3", Condition.money(20000), "Spare 20.000 Vermögen fürs Bootcamp");
        obj("krillkill_4", Condition.sold(400), "Verkaufe 400 Shrimps (Feldzug gegen Brenda)");
        obj("krillkill_5", Condition.buildCount(BuildingType.LAB, 1), "Betreibe ein Forschungslabor");
        obj("krillkill_6", Condition.tierProduced(ShrimpTier.PROTEIN, 200), "Produziere 200 Protein-Bomben");
        obj("krillkill_7", Condition.tierProduced(ShrimpTier.WARKRILL, 120), "Züchte 120 Kampf-Krill");
        // Aussenminister Akwanov
        obj("akwanov_1", Condition.money(35000), "Erreiche 35.000 Vermögen");
        obj("akwanov_2", Condition.sold(900), "Verkaufe 900 Shrimps trotz Tarif");
        obj("akwanov_3", Condition.buildCount(BuildingType.ALGAE_FARM, 3), "3 Algenfarmen für die Versorgung");
        obj("akwanov_4", Condition.money(45000), "Erreiche 45.000 Vermögen");
        obj("akwanov_5", Condition.rep(60), "Halte die Reputation bei 60");
        obj("akwanov_6", Condition.money(60000), "Erreiche 60.000 Vermögen (Embargo aushalten)");
        obj("akwanov_7", Condition.buildCount(BuildingType.SOLAR_ROOF, 2), "Strom-Unabhängigkeit (2 Solar-Dächer)");
        obj("akwanov_8", Condition.money(80000), "Erreiche 80.000 Vermögen (Finale)");
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
            Condition.any(Condition.buildCount(BuildingType.SHRIMP_TANK, 3), Condition.day(10)), 0,
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
            c("Forderungen akzeptieren.", "Zufriedene Premium-Shrimps! Bio-Qualität freigeschaltet.", rep(15), unlock("tier.BIO")),
            c("Klausi 'befördern'. (-300)", "Klausi sitzt jetzt im besten Becken. Korruption wirkt.", money(-300)),
            c("Aussperrung!", "Zwei Tage Produktionsstopp. Du bist jetzt der Bösewicht.", rep(-12)));
    }

    // ===================== KETTE C - Influencer-Aufstieg =====================
    private static void influencer() {
        auto("inf_viral", GameCharacter.MAYOR, "Assistentin Mira",
            Condition.any(Condition.money(10_000), Condition.rep(60)), 1,
            "CHEF! Wir sind TRENDING!",
            "'Guy gets rich farming shrimp indoors' - 4 Millionen Views! Die Kommentare sind zu 60% "
            + "beeindruckt und zu 40% besorgt um unsere geistige Gesundheit. Was machen wir?",
            c("Voll drauf einsteigen - eigener Kanal! (-500)", "ShrimpTube läuft. Die Reichweite explodiert.", money(-500), rep(6)).then("inf_sponsor"),
            c("Bescheiden weiterarbeiten.", "Der Hype verpufft, aber wir bleiben sympathisch.", rep(3)),
            c("Merch verkaufen! (T-Shirts)", "Shrimp-Shirts gehen weg wie warme Semmeln. Etwas Sellout-Vibe.", money(1200), rep(-4)).then("inf_sponsor"));

        chain("inf_sponsor", GameCharacter.MAYOR, "AquaBro Supplements", 0,
            "Bro, wollen wir was zusammen machen?",
            "Wir sind AquaBro, der Energy-Drink mit echtem Garnelen-Extrakt (don't ask). Sag in jedem "
            + "Video 'AquaBro - shrimp your day!' und kleb dieses leicht radioaktive Logo aufs Becken.",
            c("Deal! Geld ist Geld.", "Die Kasse klingelt, die Glaubwürdigkeit weniger.", money(2400), rep(-8)).then("inf_cancel"),
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
        auto("konk_krabbo", GameCharacter.MAYOR, "Mira (liest Lokalzeitung)",
            Condition.money(20_000), 2,
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

    // ===================== EINZELQUESTS =====================
    private static void einzelquests() {
        auto("kat_blackout", GameCharacter.MAYOR, "Technischer Leiter Olaf", Condition.day(9), 2,
            "Es wird dunkel im Becken",
            "Brownout, Chef. Die Shrimps schwimmen im Dunkeln und werden nervös. Eine hat gerade eine "
            + "andere angeschaut. So fangen Massenpaniken an.",
            c("Notaggregat anwerfen. (-600)", "Laut, stinkt, aber Strom ist da.", money(-600), rep(-3)),
            c("Shrimps mit Kerzen beruhigen. (-100)", "Romantisch und leicht brandgefährlich.", money(-100), multShrimp(0.97)),
            c("Aushalten.", "Stress im Becken. Olaf ist enttäuscht.", multShrimp(0.9)));

        auto("kat_algen", GameCharacter.PRESS, "Der Algen-Praktikant",
            Condition.all(Condition.buildCount(BuildingType.ALGAE_FARM, 2), Condition.day(15)), 0,
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

        auto("kat_moewen", GameCharacter.MAYOR, "Olaf (mit Besen)", Condition.day(20), 2,
            "Sie sind durchs Dach!",
            "MÖWEN, CHEF! Ein Schwarm hält unsere Becken für ein All-you-can-eat-Buffet. Eine wohnt "
            + "jetzt auf dem HQ. Sie heißt angeblich Kevin.",
            c("Profi-Falkner engagieren. (-500)", "Möwen weg, humane Lösung.", money(-500), rep(2)),
            c("Becken mit Netzen sichern. (-250)", "Etwas Verlust, aber künftig sicher.", money(-250), multShrimp(0.95)),
            c("Kevin adoptieren.", "Kevin ist jetzt Maskottchen. Frisst aber kräftig mit.", multShrimp(0.85), rep(8)));

        auto("beh_steuer", GameCharacter.MAYOR, "Finanzamt (Herr Pfennig)", Condition.money(25_000), 2,
            "Wir hätten da ein paar Fragen",
            "Sie haben '14.000 Garnelen als Geschäftspartner' geltend gemacht. Auch 'romantische "
            + "Beckenbeleuchtung - Bewirtungskosten' wirft Fragen auf.",
            c("Saubere Bücher vorlegen.", "Kleine Nachzahlung, reines Gewissen.", money(-1200), rep(5)),
            c("Steuerberater einfliegen lassen. (-1500)", "Herr Pfennig wird fast freundlich.", money(-1500), rep(1)),
            c("Es drauf ankommen lassen.", "Bußgeld und schlechte Presse.", money(-2000), rep(-6)));

        auto("idee_spa", GameCharacter.MAYOR, "Mira (mit Pitch-Deck)",
            Condition.all(Condition.money(30_000), Condition.unlock("zone.FORSCHUNG")), 0,
            "Chef, hören Sie mir KURZ zu",
            "Stell dir vor: ein Shrimp-Spa. Menschen lassen sich von Garnelen die Füße maniküren. "
            + "Premium-Wellness! Was kann schon schiefgehen?",
            c("Lass es uns wagen! (-2000)", "Der Shrimp-Spa wird ein Hit (meistens).", money(-2000), rep(6)),
            c("Zu riskant, Mira.", "Gute Mitarbeiterführung.", rep(1)),
            c("Mach es - mit Haftungsausschluss! (-1200)", "Sicher und solide.", money(-1200), rep(2)));

        auto("geld_investor", GameCharacter.MAYOR, "Frau Goldberg (Sonnenbrille, drinnen)",
            Condition.all(Condition.money(35_000), Condition.rep(70)), 1,
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
            Condition.all(Condition.day(25), Condition.buildCount(BuildingType.SHRIMP_TANK, 2), Condition.shrimpProduced(150)), 2,
            "GENERAL KRILLKILL ÜBERNIMMT DAS KOMMANDO",
            "STILLGESTANDEN! General Krillkill, 1. Krustentier-Division. Ich beobachte deinen Laden aus "
            + "dem Lüftungsschacht. Du züchtest VORSPEISEN! Während DA DRAUSSEN die Suppe näher kommt! "
            + "Lass MICH aus diesen Garnelen Soldaten machen. Protein. Kampfkraft. Sag JA, Rekrut!",
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
            c("Sparflamme, General.", "Er ist enttäuscht. 'Die Suppe lacht über uns.'", rep(0)).then("krillkill_3"));

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
        auto("akwanov_intro", GameCharacter.AKWANOV, null, Condition.money(30_000), 2,
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
