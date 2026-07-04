package com.shrimptopia.tutorial;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.ResourceType;
import com.shrimptopia.tutorial.TutorialStep.Advance;
import com.shrimptopia.tutorial.TutorialStep.Region;
import java.util.ArrayList;
import java.util.List;

/** Die 15 Tutorial-Schritte: Dr. Perla führt durch den Garagen-Start bis zum ersten Verkauf. */
public final class TutorialContent {
    private TutorialContent() {}

    public static List<TutorialStep> steps() {
        List<TutorialStep> s = new ArrayList<>();

        s.add(new TutorialStep(Region.POPUP,
            "Willkommen in deiner Garage, Boss! Ich bin Dr. Perla, Garnelen-Biologin. Die Uni hat mein "
            + "Labor weggespart ('Budgetgründe'), also forsche ich jetzt hier: Du baust, ich berate, "
            + "wir beweisen, dass Indoor-Shrimp reich macht. Greg beobachtet.",
            Advance.ACK));

        s.add(new TutorialStep(Region.TOPBAR,
            "Da oben siehst du alles, was zählt: Geld, Strom, Wasser, Futter, Shrimps, Arbeiter und "
            + "Reputation. Der Pulsschlag der Garage - geht hier was auf Rot, ist Greg sauer. (Greg ist "
            + "meine emotionale Support-Garnele im Wasserglas. Frag nicht.)",
            Advance.ACK));

        s.add(new TutorialStep(Region.RESOURCE,
            "Das hier ist Geld - Startkapital: 2.500, gespart vom Zeitungsaustragen. Fällt es ins Minus "
            + "und bleibt da, pfändet die Bank die Garage. Und Banker sind, anders als Garnelen, völlig "
            + "humorlos.",
            Advance.ACK).res(ResourceType.MONEY));

        s.add(new TutorialStep(Region.MAP,
            "Gebaut wird über das BAUMENÜ: Öffne es mit dem BAUEN-Knopf links in der Seitenleiste - "
            + "oder per Rechtsklick irgendwo auf die Karte. Gebäude auswählen, dann mit Klick auf ein "
            + "freies Feld platzieren. Wir starten mit Flohmarkt-Technik; die großen Hallen-Gebäude "
            + "sind noch gesperrt, bis wir aus der Garage rauswachsen. Jede Garagen-Anlage lässt sich "
            + "später an gleicher Stelle UPGRADEN.",
            Advance.ACK));

        s.add(new TutorialStep(Region.MAP,
            "Erste Regel: Ohne Strom keine Pumpen, ohne Pumpen keine Party. Öffne das BAUMENÜ "
            + "(BAUEN-Knopf links), wähle in der Kategorie STROM den ROSTIGEN DIESEL-GENERATOR - der "
            + "passende Eintrag leuchtet bereits - und setz ihn mit Klick auf ein freies Feld ab. Er "
            + "stinkt, er dröhnt, er funktioniert. Meistens. (Tipp: Rechtsklick hebt die Auswahl "
            + "wieder auf.)",
            Advance.BUILD).build(BuildingType.OLD_GENERATOR));

        s.add(new TutorialStep(Region.MAP,
            "Garnelen sind, Überraschung, auf Wasser angewiesen. Baumenü → Kategorie VERSORGUNG → "
            + "REGENTONNE mit Gartenschlauch. Ich nenne es 'improvisierte Aquakultur-Infrastruktur' und "
            + "weine dabei nur ein bisschen.",
            Advance.BUILD).build(BuildingType.RAIN_BARREL));

        s.add(new TutorialStep(Region.MAP,
            "Hungrige Shrimps sind unzufriedene Shrimps. Der ALGEN-EIMER (ebenfalls unter VERSORGUNG) "
            + "macht aus Wasser und Fensterbank-Sonne leckeres Grünzeug. Greg besteht auf Bio.",
            Advance.BUILD).build(BuildingType.ALGAE_BUCKET));

        s.add(new TutorialStep(Region.MAP,
            "Trommelwirbel: das GARAGEN-AQUARIUM (Kategorie ZUCHT) vom Flohmarkt. Es frisst Strom, "
            + "Wasser und Futter - und spuckt echte, lebende Garnelen aus. Genau dafür sind wir hier, "
            + "Boss. Bau eins!",
            Advance.BUILD).build(BuildingType.GARAGE_TANK));

        s.add(new TutorialStep(Region.MAP,
            "Garnelen zahlen keine Miete. Der KLAPPTISCH-VERKAUF (Kategorie VERKAUF) macht aus ihnen "
            + "Geld: Kühlbox, Schild, Kleingeld-Dose. Damit schließt sich die Kette: Strom → Wasser & "
            + "Futter → Aquarium → Klapptisch → Reichtum (Fernziel).",
            Advance.BUILD).build(BuildingType.YARD_SALE));

        s.add(new TutorialStep(Region.MAP,
            "Klick mal eines deiner Gebäude an. Der INSPEKTOR (rechts) zeigt genau, was rein- und "
            + "rausgeht - und ob es voll läuft oder im Blackout nuckelt. Deine Lupe für alles.",
            Advance.OPEN_INSPECTOR));

        s.add(new TutorialStep(Region.INSPECTOR,
            "Im Inspektor gibst du Gebäuden MODI, kaufst UPGRADES - und wertest Garagen-Technik zur "
            + "Hallen-Stufe auf, sobald sie freigeschaltet ist: gleiche Stelle, größeres Gebäude. "
            + "Übrigens: Gebäude nahe am HQ arbeiten einen Tick fleißiger - der Bonus schwindet mit "
            + "der Entfernung, schaden tut Abstand aber nie.",
            Advance.ACK));

        s.add(new TutorialStep(Region.ALMANAC,
            "Das hier ist der Almanach - dein Nachschlagewerk für alles: Vermögen, Ressourcen, "
            + "Tiers, Märkte und Statistiken. Ich habe ihn dir aufgeschlagen: Nicht jede Garnele ist "
            + "gleich edel - es gibt TIERS, von Standardware bis 'mit BWL-Abschluss'. Manche Märkte "
            + "kaufen nur bestimmte Tiers; feinere schaltest du per Quest & Upgrade frei.",
            Advance.ACK).tab(2));

        s.add(new TutorialStep(Region.ALMANAC,
            "Und das ist die harte Wahrheit des Handels: NACHFRAGE. Ohne Werbung kauft nur die "
            + "Nachbarschaft (~6 Shrimps/Tag). Hier im Marketing-Tab buchst du Streams - sie kosten "
            + "täglich Geld, bringen aber Kundschaft. Mira hat da später noch Ideen.",
            Advance.ACK).tab(5));

        s.add(new TutorialStep(Region.ZONE_TABS,
            "Eine Garage ist erst der Anfang. Über die ZONEN-Knöpfe links in der Seitenleiste "
            + "(unter STANDORTE) wechselst du später zwischen den Karten: Produktion, Forschung, "
            + "Logistik, Empfang. Sie schalten sich nach und nach frei.",
            Advance.ACK));

        s.add(new TutorialStep(Region.CONTROLS,
            "Letztes Werkzeug: mit PAUSE und den Tempo-Knöpfen (1x/2x/3x) bestimmst du das Spieltempo. "
            + "In Ruhe planen, dann Vollgas. Das war's, Boss - mach uns reich, und grüß Greg von mir!",
            Advance.ACK));

        return s;
    }
}
