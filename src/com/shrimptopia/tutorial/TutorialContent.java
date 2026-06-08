package com.shrimptopia.tutorial;

import com.shrimptopia.model.BuildingType;
import com.shrimptopia.model.ResourceType;
import com.shrimptopia.tutorial.TutorialStep.Advance;
import com.shrimptopia.tutorial.TutorialStep.Region;
import java.util.ArrayList;
import java.util.List;

/** Die 15 Tutorial-Schritte (Dr. Perla Pereira führt durch die Produktionskette). */
public final class TutorialContent {
    private TutorialContent() {}

    public static List<TutorialStep> steps() {
        List<TutorialStep> s = new ArrayList<>();

        s.add(new TutorialStep(Region.POPUP,
            "Willkommen in deiner ganz eigenen Garnelen-Halle, Boss! Ich bin Dr. Perla, deine Biologin. "
            + "Das Internet sagt, mit Indoor-Shrimp wird man reich - lass uns das wissenschaftlich überprüfen.",
            Advance.ACK));

        s.add(new TutorialStep(Region.TOPBAR,
            "Da oben siehst du alles, was zählt: Geld, Strom, Wasser, Futter, Shrimps, Arbeiter und "
            + "Reputation. Der Pulsschlag der Halle - geht hier was auf Rot, ist Greg sauer.",
            Advance.ACK));

        s.add(new TutorialStep(Region.RESOURCE,
            "Das hier ist Geld. Fällt es ins Minus und bleibt da, pfändet die Bank die Halle - und "
            + "Banker sind, anders als Garnelen, völlig humorlos. Ziel: 50.000, dann bist du Tycoon.",
            Advance.ACK).res(ResourceType.MONEY));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Hier links ist dein Baumenü. Alles, was du je hochziehst, kommt von hier. Ich sag dir "
            + "gleich, womit wir anfangen.",
            Advance.ACK));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Erste Regel: Ohne Strom keine Pumpen, ohne Pumpen keine Party. Bau ein SCHALENTIER-KRAFTWERK "
            + "(im Menü) auf ein freies Feld. Ja, es stinkt für die Reputation - Solar kommt später.",
            Advance.BUILD).build(BuildingType.POWER_PLANT));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Garnelen sind, Überraschung, auf Wasser angewiesen. Bau ein WASSERWERK. Es zwackt etwas "
            + "Strom ab - deshalb zuerst das Kraftwerk. Siehst du, ich hatte einen Plan.",
            Advance.BUILD).build(BuildingType.WATER_PLANT));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Hungrige Shrimps sind unzufriedene Shrimps. Die ALGEN-FUTTERFARM macht aus Strom und Wasser "
            + "leckeres Grünzeug. Greg besteht auf Bio.",
            Advance.BUILD).build(BuildingType.ALGAE_FARM));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Die Becken bedienen sich nicht von selbst. Ein ARBEITER-WOHNHEIM bringt Personal. Zu wenig "
            + "Leute heißt gedrosselter Betrieb.",
            Advance.BUILD).build(BuildingType.HOUSING));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Trommelwirbel: das SHRIMP-BECKEN. Es frisst Strom, Wasser und Futter - und spuckt echte, "
            + "lebende Garnelen aus. Genau dafür sind wir hier, Boss. Bau eins!",
            Advance.BUILD).build(BuildingType.SHRIMP_TANK));

        s.add(new TutorialStep(Region.BUILD_MENU,
            "Garnelen zahlen keine Miete. Die SHRIMP-BÖRSE verkauft sie und macht daraus Geld. Damit "
            + "schließt sich die Kette: Strom -> Wasser & Futter -> Becken -> Börse -> Reichtum.",
            Advance.BUILD).build(BuildingType.SALES_OFFICE));

        s.add(new TutorialStep(Region.MAP,
            "Klick mal eines deiner Gebäude an. Der INSPEKTOR (rechts) zeigt genau, was rein- und "
            + "rausgeht - und ob es voll läuft oder im Brownout nuckelt. Deine Lupe für alles.",
            Advance.OPEN_INSPECTOR));

        s.add(new TutorialStep(Region.INSPECTOR,
            "Im Inspektor gibst du Gebäuden MODI und kaufst UPGRADES - z.B. das Becken auf Bio-Zucht "
            + "umstellen oder die Börse aufrüsten. Mehr Leistung, mehr Kosten. Probier es später aus.",
            Advance.ACK));

        s.add(new TutorialStep(Region.RESOURCE,
            "Nicht jede Garnele ist gleich edel. Es gibt TIERS - von solider Standardware bis 'mit "
            + "BWL-Abschluss'. Manche Märkte kaufen nur bestimmte Tiers. Qualität zahlt sich aus.",
            Advance.ACK).res(ResourceType.SHRIMP));

        s.add(new TutorialStep(Region.ZONE_TABS,
            "Eine Halle ist erst der Anfang. Über die ZONEN-REITER (oben) wechselst du zwischen "
            + "Standorten: Produktion, Forschung, Logistik, Empfang. Sie schalten sich nach und nach frei.",
            Advance.ACK));

        s.add(new TutorialStep(Region.CONTROLS,
            "Letztes Werkzeug: mit PAUSE und den Tempo-Knöpfen (1x/2x/3x) bestimmst du das Spieltempo. "
            + "In Ruhe planen, dann Vollgas. Das war's, Boss - mach uns reich, und grüß Greg von mir!",
            Advance.ACK));

        return s;
    }
}
