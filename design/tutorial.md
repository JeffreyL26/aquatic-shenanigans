# Tutorial — "Willkommen in der Halle, Boss"

Das an die Hand nehmende Einsteiger-Tutorial fuer ShrimpTopia v2. Es richtet sich an
Spielerinnen und Spieler, die **nichts** ueber das Spiel wissen. Kein abrupter Start: Wir
erklaeren erst das HUD, dann das Baumenue, dann Schritt fuer Schritt die komplette
Produktionskette — bis am Ende die ersten Garnelen Geld abwerfen.

---

## Die Advisor-Figur: **Dr. Perla Pereira**

> **Rolle:** Pragmatische Aquakultur-Biologin und deine rechte Hand. Hat ihren Doktor
> ueber "Sozialverhalten der Pazifischen Weissbeingarnele unter Schichtarbeit" gemacht
> und ist trotzdem freiwillig hier. Trockener Humor, null Geduld fuer Brownouts, aber
> ein grosses Herz fuer Garnelen mit Ambitionen.
>
> **Stimme/Ton:** Freundlich, leicht ironisch, immer praktisch. Sagt Dinge wie
> "Vertrau mir, ich hab einen Doktortitel — wenn auch in Garnelen." Spricht den Spieler
> direkt mit **"Boss"** oder **"Chef"** an. Erscheint als kleines Portrait am
> Tutorial-Popup (links unten), nie aufdringlich, immer ueberspringbar.
>
> **Optionaler Begleit-Gag:** Auf ihrem Schreibtisch sitzt **Greg**, eine besonders
> selbstbewusste Garnele im Wasserglas, die laut Perla "den Laden eigentlich schmeisst".

Globale Regel: Jedes Tutorial-Popup hat unten rechts einen **"Ueberspringen"**-Link
(Tutorial komplett aus) und — wo sinnvoll — einen **"Weiter"**-Button. Solange ein
Schritt eine Aktion verlangt, ist nur der relevante UI-Bereich hervorgehoben
(Spotlight), der Rest ist leicht abgedunkelt.

---

## Tutorial-Schritte (15 Schritte)

### Schritt 1 — Begruessung
- **UI-Bereich:** `QUEST_POPUP`
- **Text (Perla):** "Willkommen in deiner ganz eigenen Garnelen-Halle, Boss! Ich bin
  Dr. Perla, deine Biologin. Das Internet sagt, mit Indoor-Shrimp wird man reich — lass
  uns das mal wissenschaftlich ueberpruefen."
- **Fortschritt:** Spieler klickt **Weiter**.

### Schritt 2 — Das Ressourcen-HUD oben
- **UI-Bereich:** `TOPBAR_RESSOURCEN`
- **Text (Perla):** "Da oben siehst du alles, was zaehlt: Geld, Strom, Wasser, Futter,
  Shrimps, Arbeiter und Reputation. Das ist quasi der Pulsschlag der Halle — geht hier
  was auf Rot, ist Greg sauer."
- **Fortschritt:** Spieler klickt **Weiter**.

### Schritt 3 — Geld, dein wichtigstes Tier
- **UI-Bereich:** `EINZELNE_RESSOURCE(Geld)`
- **Text (Perla):** "Das hier ist Geld. Du startest mit einem ueberschaubaren Polster.
  Faellt es ins Minus und bleibt da, pfaendet die Bank die Halle — und Banker sind, anders
  als Garnelen, voellig humorlos. Ziel sind 50.000, dann bist du offiziell Shrimp-Tycoon."
- **Fortschritt:** Spieler klickt **Weiter**.

### Schritt 4 — Das Baumenue
- **UI-Bereich:** `BAUMENUE`
- **Text (Perla):** "Hier links ist dein Baumenue. Alles, was du je in dieser Halle
  hochziehst, kommt von hier. Klick dich gern mal durch — aber bau noch nichts, ich sag
  dir gleich, womit wir anfangen."
- **Fortschritt:** Spieler oeffnet/ueberfaehrt einen Eintrag im Baumenue (oder klickt **Weiter**).

### Schritt 5 — Strom: das erste Kraftwerk
- **UI-Bereich:** `BAUE_GEBAEUDE(Schalentier-Kraftwerk)`
- **Text (Perla):** "Erste Regel der Aquakultur: Ohne Strom keine Pumpen, ohne Pumpen
  keine Party. Such dir das **Schalentier-Kraftwerk** aus dem Menue und klatsch es auf ein
  freies Feld. Ja, es stinkt ein bisschen fuer die Reputation — Solar bauen wir spaeter."
- **Fortschritt:** Spieler baut ein **Schalentier-Kraftwerk**.

### Schritt 6 — Wasser: das Wasserwerk
- **UI-Bereich:** `BAUE_GEBAEUDE(Wasserwerk)`
- **Text (Perla):** "Garnelen sind, Ueberraschung, ziemlich auf Wasser angewiesen. Bau
  ein **Wasserwerk**. Es zwackt etwas Strom ab — deshalb zuerst das Kraftwerk. Siehst du,
  ich hatte einen Plan."
- **Fortschritt:** Spieler baut ein **Wasserwerk**.

### Schritt 7 — Futter: die Algen-Futterfarm
- **UI-Bereich:** `BAUE_GEBAEUDE(Algen-Futterfarm)`
- **Text (Perla):** "Hungrige Shrimps sind unzufriedene Shrimps. Die **Algen-Futterfarm**
  macht aus Strom und etwas Wasser leckeres Gruenzeug. Greg besteht auf Bio."
- **Fortschritt:** Spieler baut eine **Algen-Futterfarm**.

### Schritt 8 — Personal: das Arbeiter-Wohnheim
- **UI-Bereich:** `BAUE_GEBAEUDE(Arbeiter-Wohnheim)`
- **Text (Perla):** "Die Becken bedienen sich nicht von selbst. Ein **Arbeiter-Wohnheim**
  bringt Personal in die Halle. Zu wenig Leute heisst gedrosselter Betrieb — und niemand
  will einer Garnele erklaeren, warum heute keiner Schicht hatte."
- **Fortschritt:** Spieler baut ein **Arbeiter-Wohnheim**.

### Schritt 9 — Das Herzstueck: das Shrimp-Becken
- **UI-Bereich:** `BAUE_GEBAEUDE(Shrimp-Becken)`
- **Text (Perla):** "Trommelwirbel: das **Shrimp-Becken**. Es frisst Strom, Wasser und
  Futter — und spuckt dafuer echte, lebende Garnelen aus. Genau dafuer sind wir hier, Boss.
  Bau eins!"
- **Fortschritt:** Spieler baut ein **Shrimp-Becken**.

### Schritt 10 — Geld machen: die Shrimp-Boerse
- **UI-Bereich:** `BAUE_GEBAEUDE(Shrimp-Boerse)`
- **Text (Perla):** "Garnelen sind nett, aber Miete bezahlen sie nicht. Die
  **Shrimp-Boerse** verkauft deine Shrimps und macht daraus glorreiches Geld. Damit
  schliesst sich die Kette: Strom -> Wasser & Futter -> Becken -> Boerse -> Reichtum."
- **Fortschritt:** Spieler baut eine **Shrimp-Boerse**.

### Schritt 11 — Der Inspektor
- **UI-Bereich:** `INSPEKTOR`
- **Text (Perla):** "Klick mal eines deiner Gebaeude an. Der **Inspektor** zeigt dir
  genau, was rein- und rausgeht — und ob es gerade voll laeuft oder im Brownout vor sich
  hin nuckelt. Das ist deine Lupe fuer alles, was klemmt."
- **Fortschritt:** Spieler oeffnet den **Inspektor** (Gebaeude angeklickt).

### Schritt 12 — Modi & Upgrades
- **UI-Bereich:** `MODI_UPGRADES`
- **Text (Perla):** "Im Inspektor kannst du Gebaeuden **Modi** geben und sie **upgraden** —
  z.B. das Becken auf Effizienz trimmen oder die Arbeiter in Nachtschicht schicken. Mehr
  Leistung, mehr Kosten. Probier ruhig einen Modus aus."
- **Fortschritt:** Spieler oeffnet das Modi/Upgrade-Panel oder waehlt einen Modus.

### Schritt 13 — Shrimp-Tiers & waehlerische Maerkte
- **UI-Bereich:** `EINZELNE_RESSOURCE(Shrimps)`
- **Text (Perla):** "Nicht jede Garnele ist gleich edel. Es gibt **Tiers** —
  von 'solide Standardware' bis 'mit BWL-Abschluss'. Manche Maerkte kaufen nur bestimmte
  Tiers. Hoehere Reputation und Upgrades heben die Qualitaet, und Premium zahlt sich aus."
- **Fortschritt:** Spieler klickt **Weiter** (Tooltip/Tier-Anzeige hervorgehoben).

### Schritt 14 — Zonen
- **UI-Bereich:** `ZONEN_TABS`
- **Text (Perla):** "Eine Halle ist erst der Anfang. Ueber die **Zonen-Tabs** wechselst du
  zwischen mehreren Standorten. So trennst du sauber: hier die stinkenden Kraftwerke, dort
  das schicke Premium-Becken fuer die Influencer-Garnelen."
- **Fortschritt:** Spieler wechselt einmal die **Zone** (oder klickt **Weiter**).

### Schritt 15 — Pause, Tempo & los geht's
- **UI-Bereich:** `PAUSE_SPEED`
- **Text (Perla):** "Letztes Werkzeug: Mit **Pause** und den Tempo-Knoepfen (1x/2x/3x)
  bestimmst du das Spieltempo. In Ruhe planen, dann Vollgas. Das war's, Boss — die Halle
  gehoert dir. Mach uns reich, und gruess Greg von mir."
- **Fortschritt:** Spieler startet/erhoeht das Tempo (oder klickt **Fertig**) -> Tutorial endet.

---

## Reihenfolge (Lehrlogik der Produktionskette)

1. HUD verstehen (Schritte 2-3)
2. Baumenue kennenlernen (Schritt 4)
3. **Strom** (Kraftwerk, Schritt 5)
4. **Wasser** (Wasserwerk, Schritt 6)
5. **Futter** (Algen-Farm, Schritt 7)
6. **Personal** (Wohnheim, Schritt 8)
7. **Produktion** (Shrimp-Becken, Schritt 9)
8. **Verkauf** (Boerse, Schritt 10)
9. Inspektor & Modi/Upgrades (Schritte 11-12)
10. Tiers & Maerkte (Schritt 13)
11. Zonen (Schritt 14)
12. Pause/Speed & Abschluss (Schritt 15)

So lernt der Spieler die Kette **Strom -> Wasser & Futter -> Becken -> Boerse** in genau der
Reihenfolge, in der sie auch funktionieren muss — und steht am Ende vor einer kleinen, aber
voll laufenden Garnelen-Fabrik.

## Designhinweise zur Umsetzung
- Jeder Bau-Schritt prueft konkret den gebauten Gebaeudetyp als Abschlussbedingung; erst
  dann oeffnet das naechste Popup. Falsch platziert? Perla gibt einen freundlichen Hinweis.
- Geld im Tutorial ggf. leicht subventionieren, damit kein Spieler an Schritt 9 verarmt.
- Alle Popups: Portrait von Dr. Perla links, "Weiter"/"Fertig" rechts, "Ueberspringen"
  klein darunter. Spotlight nur auf dem aktiven `UI-Bereich`.
- Bedingungen wie "Spieler baut X" / "Spieler oeffnet Inspektor" / "Spieler klickt Weiter"
  sind direkt an bestehende Events (Gebaeude platziert, Inspektor geoeffnet) koppelbar.
