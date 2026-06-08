# Charakter-Bogen: Aussenminister Ivan Akwanov

> "Wer braucht schon Ozeane? Wir haben HALLEN!"
> -- Aussenminister Ivan Akwanov, Usbekistan, vor laufenden Kameras, dreimal hintereinander

## Wer ist das?

Ivan Akwanov ist Aussenminister von Usbekistan -- dem stolzesten **doppelt
eingeschlossenen Binnenland** der Welt (kein Meer, und selbst die Nachbarn
haben kein Meer; man muesste zwei Grenzen ueberqueren, nur um nass zu werden).
Akwanov hat eine Vision: Usbekistan soll die Garnelen-Supermacht des 21.
Jahrhunderts werden -- **ohne einen einzigen Tropfen Ozean**. Seine Waffe ist
nicht die Flotte, sondern die **Lobbyarbeit**: Handelsabkommen, Embargos,
Subventionen, verschwundene Aktenordner und ein Laecheln, das man am liebsten
schriftlich festhalten lassen wuerde.

Solange du ein kleiner Hinterhof-Zuechter bist, ignoriert er dich hoeflich.
Sobald deine Halle anfaengt, ernsthaft Geld zu drucken, betrachtet er dich als
seinen **persoenlichen Erzrivalen** -- charmant, diplomatisch und absolut
bereit, dir das Wasser im wahrsten Sinne abzugraben (was bei seiner geografischen
Lage eine besondere Ironie ist).

**Stimme:** diplomatisch-oelig, schlitzohrig, immer drei Hoeflichkeiten zu viel.
Droht nie direkt -- er "bedauert lediglich, dass die Umstaende so unguenstig
gefallen sind". Running-Gags: das fehlende Meer, der "Trockenhafen von Taschkent",
seine ominoese Tante im Handelsministerium, und sein heiliger Krieg gegen das
Wort "Kuestennation".

---

## 1. Auftritts-Bedingung

Akwanov hat eine Schwelle, ab der dein Imperium fuer ihn "interessant"
(= bedrohlich) wird. Gekoppelt an die wachsende Wirtschaftsgroesse:

| Trigger | Wert | Begruendung |
|---|---|---|
| **Primaer** | `money >= 30.000` | 60% des Siegziels (`GOAL_MONEY = 50.000`). Du bist offiziell ein Schwergewicht. |
| **Alternativ (Frueh-Trigger)** | `shrimpSoldLast >= 50` ueber 5 aufeinanderfolgende Ticks | Hohes Exportvolumen weckt ihn auch, wenn du in Anlagen statt Bargeld investierst. |
| **Cooldown-Regel** | wie `EventSystem.MIN_GAP`, aber eigener Zaehler `lastRivalDay` | Storyline-Beats kollidieren nicht mit Zufallsereignissen. |

Sobald einer der Trigger feuert, startet der Bogen mit dem Vorstellungs-Popup
und der **Rivalitaets-Score** wird auf 0 initialisiert (siehe Mechanik unten).
Danach laeuft die Storyline beat-getrieben: ein neuer Beat ist frueheste 8-12
Tage nach dem letzten moeglich (eigener Cooldown), zusaetzlich an
Wirtschafts-Schwellen gekoppelt.

---

## 2. Vorstellungs-Popup

> **NEUER RIVALE: Aussenminister Akwanov meldet sich**
>
> Ein Diplomat in einem zu teuren Anzug betritt deine Halle, ruempft kurz die
> Nase ueber den Algengeruch und laechelt dann breit.
>
> *"Aaah, der beruehmte Shrimp-Tycoon! Gestatten -- Ivan Akwanov, Aussenminister
> Usbekistans. Wir beobachten Ihre... Halle... mit grossem Interesse. Wissen Sie,
> mein Land hat KEIN Meer. Nicht eins. Unsere Nachbarn haben auch keins -- wir
> sind, wie man sagt, doppelt gesegnet. Und doch wird Usbekistan die Garnele der
> Zukunft zuechten. In HALLEN. Wie Sie. Nur... groesser. Und mit besseren
> Akten."*
>
> *"Ich biete Ihnen heute meine Freundschaft an. Nehmen Sie sie -- es ist
> guenstiger als das, was danach kommt. Auf eine lange, fruchtbare und voellig
> einseitige Zusammenarbeit."*
>
> **[ Charmiert zuruecklaecheln ]** -- Rivalitaet +0 (neutral). Er notiert sich das.
> **[ "Usbekistan hat doch nicht mal Wasser." ]** -- Rivalitaet +10. Er liebt eine Herausforderung.

*Mechanik-Hook:* Setzt `rivalActive = true`, oeffnet das Rivalen-Panel im
Inspektor (Foto, Rivalitaets-Score-Balken, aktueller Storyline-Schritt).

---

## 3. Mechanische Hooks (Ueberblick)

Diese Felder/Systeme treiben den ganzen Bogen. Konkret und an die bestehende
Engine angedockt (`GameState`, `EventSystem`):

### 3.1 Rivalitaets-Score (`rivalry`, 0-100)
- Startet bei 0. Steigt durch konfrontative Optionen, sinkt durch Zugestaendnisse.
- **0-30 "Hoeflich":** Akwanov stichelt nur. Milde Effekte.
- **31-65 "Handelskrieg":** Embargo aktiv, Sabotage-Events moeglich.
- **66-100 "Kalter Krill-Krieg":** harte Sabotage, aber auch die grossen
  End-Optionen (Allianz / totale Eskalation) werden freigeschaltet.
- Treibt die Haerte der jeweiligen Beat-Effekte (z.B. Embargo-Staerke skaliert
  mit `rivalry`).

### 3.2 Akwanov-Embargo (Markt-Modifikator)
- Neues Feld `exportTariff` (0.0-0.6). Greift im Verkauf in `tick()` direkt am
  Preis an:
  ```
  price = sellPriceBase * (1 + labBonus) * repMult * (1 - exportTariff);
  ```
- Ein Embargo setzt z.B. `exportTariff = 0.25` -> dein Boersen-Export bringt 25%
  weniger. Restaurants (Inlandsmarkt) sind ausgenommen -- Akwanov kontrolliert
  den Export, nicht deinen Vorgarten.
- Hebt sich nach N Tagen oder per Storyline-Entscheidung wieder auf.

### 3.3 Usbekistan-Schattenmarkt (eigener Markt)
- Spaeter im Bogen freischaltbar: ein **alternativer Absatzkanal** ("Trockenhafen
  von Taschkent"). Kauft Shrimps zu Festpreis, **immun gegen `exportTariff`**,
  aber zu Akwanovs Konditionen (niedrigerer Basispreis, dafuer planbar). Mechanisch
  ein zweites SALES_OFFICE-Verhalten mit Flag `usbekChannel = true`.

### 3.4 Sabotage-Events (Storyline-getriggert, nicht zufaellig)
- Eigene Event-Kategorie `RIVAL`. Nutzen dieselben Effekt-Consumer wie
  `EventSystem` (`multWater`, `multShrimp`, `addMoney`, `addReputation`), feuern
  aber **gezielt** im jeweiligen Beat, nicht ueber den Zufalls-Roll.
- Gegen-Mechanik: eine **Sicherheits-/Spionageabwehr-Stufe** (z.B. als
  Gebaeude-Upgrade am HQ oder Forschungslabor), die Sabotage-Effekte daempft
  (`sabotageResist`, 0.0-0.8 -> reduziert den Schaden).

### 3.5 Reputation als zweite Front
- Viele Optionen zahlen in `reputation` (0-100) statt nur in Geld. Akwanov greift
  oft genau hier an, weil Reputation deinen Verkaufspreis bestimmt (`repMult`).

---

## 4. Die Rivalitaets-Storyline (8 Stufen)

Jeder Beat: **Titel**, **Popup (Akwanovs Stimme)**, **Spieler-Ziel/Reaktion**,
**Optionen mit Konsequenzen**. Alle Zahlen sind Vorschlaege, skalierbar ueber
`rivalry`.

---

### Stufe 1 -- "Die Charme-Offensive"
**Trigger:** direkt nach dem Vorstellungs-Popup, ca. Tag +8.

> *"Mein Freund! Ich habe Ihnen ein kleines Geschenk mitgebracht: ein
> Beratervertrag. Voellig kostenlos -- nun ja, beratend kostenlos. Meine
> Experten optimieren Ihre Halle, und im Gegenzug... teilen wir nur ein paar
> harmlose Betriebsdaten. Reine Formsache. Unterschreiben Sie hier, hier, und
> auf Seite 47 in der Mitte des Absatzes, den niemand liest."*

**Spieler-Ziel:** Erste Weiche -- Kooperation oder Distanz?

| Option | Konsequenz |
|---|---|
| **Vertrag unterschreiben** | +1.500 Geld sofort ("Beratungshonorar"). ABER: Rivalitaet -5 *und* ein verstecktes Flag `dataLeak = true` -> in Stufe 4 weiss er genau, wo deine Becken stehen (Sabotage trifft haerter). |
| **Hoeflich ablehnen** | Nichts passiert. Rivalitaet +0. Akwanov: *"Schade. Ich mag Menschen, die ihre eigenen Akten lesen. Das macht den Sieg... persoenlicher."* |
| **Vertrag oeffentlich zerreissen** | +8 Reputation (Presse liebt den Trotz). Rivalitaet +15. |

---

### Stufe 2 -- "Der Trockenhafen-Tarif"
**Trigger:** `money >= 35.000` oder Tag +10 nach Stufe 1.

> *"Reine Nachricht der Hoeflichkeit: Usbekistan fuehrt ab sofort einen
> 'Solidaritaetsbeitrag fuer Binnenlaender' ein. Alle Importe von Halleng-Garnelen
> -- also Ihre -- werden mit einer kleinen, symbolischen Abgabe belegt. Ganz klein.
> Mikroskopisch. Etwa so klein wie unser Kuestenstreifen."*

**Spieler-Ziel:** Erstes Embargo. `exportTariff = 0.15` fuer 12 Tage.

| Option | Konsequenz |
|---|---|
| **Schlucken und weiterproduzieren** | Embargo laeuft seine 12 Tage. Rivalitaet +0. Du verlierst ~15% Exporterloes -- finanzier es ueber mehr Becken. |
| **Gegen-Lobby anheuern (-2.000 Geld)** | Halbiert die Abgabe sofort (`exportTariff = 0.075`) und verkuerzt sie auf 6 Tage. Rivalitaet +10. |
| **Inlandsmarkt ausbauen (Restaurant-Fokus)** | Kein direkter Effekt, aber Hinweis-Popup: Restaurants sind embargofrei. (Soft-Tutorial fuer die Embargo-Mechanik.) |

---

### Stufe 3 -- "Die Plankton-Diplomatie"
**Trigger:** Tag +10 nach Stufe 2.

> *"Ein Geruecht, mehr nicht: Der weltgroesste Plankton-Grosshaendler -- ein
> guter Freund meiner Tante im Handelsministerium -- erwaegt, exklusiv an
> Usbekistan zu liefern. Ihre Algenfarmen koennten... durstig werden. Aber wer
> bin ich, mich in freie Maerkte einzumischen? Ich bin nur Aussenminister eines
> Landes ohne Meer, der zufaellig jeden Lieferanten beim Vornamen kennt."*

**Spieler-Ziel:** Angriff auf die Lieferkette (Futter/Wasser).

| Option | Konsequenz |
|---|---|
| **Eigenen Liefervertrag sichern (-2.500 Geld)** | Immun gegen diesen Beat. +5 Reputation (Versorgungssicherheit). Rivalitaet +5. |
| **Abwarten** | Sabotage-Event feuert: `multFeed(0.6)` (Futterlager schrumpft). Rivalitaet +0. |
| **Akwanovs Tante "kennenlernen" (Bestechung -1.000)** | Beat abgewendet UND in Stufe 5 (Spionage) ein Bonus-Tipp. Rivalitaet -5. Akwanov ahnt nichts -- noch. |

---

### Stufe 4 -- "Praktikant aus Taschkent"
**Trigger:** `rivalry >= 31` (Handelskrieg aktiv) oder Tag +10 nach Stufe 3.

> *"Gute Neuigkeiten! Usbekistan entsendet einen Austausch-Praktikanten in Ihre
> Halle. Bildungspartnerschaft, voelkerverbindend, sehr ruehrend. Er heisst Dmitri,
> er ist sehr lernbegierig, und er stellt erstaunlich viele Fragen darueber, wo
> genau Ihr Hauptwasserventil sitzt."*

**Spieler-Ziel:** Erster echter Sabotage-Versuch. Knuepft an `dataLeak` aus Stufe 1.

> *Sabotage-Effekt (vor `sabotageResist`):* `multWater(0.5)` + Reputation -3.
> Wenn `dataLeak == true`: zusaetzlich `multShrimp(0.85)` (er wusste, welches
> Becken das wertvollste ist). Anspielung auf das bestehende Event "Praktikant
> verwechselt die Schalter" -- nur diesmal ist es kein Versehen.

| Option | Konsequenz |
|---|---|
| **Dmitri herzlich aufnehmen** | Volle Sabotage trifft (ggf. gedaempft durch `sabotageResist`). Rivalitaet +0. Spaeter Hinweis: bau eine Spionageabwehr. |
| **Spionageabwehr aktivieren (-1.800 Geld, einmalig)** | Setzt `sabotageResist = 0.6` dauerhaft. Sabotage diesmal stark reduziert. Rivalitaet +10. |
| **Dmitri als Doppelagenten umdrehen (-1.200 Geld)** | Sabotage faellt aus. Du erhaeltst in Stufe 5 verlaessliche Spionage-Infos. Rivalitaet +15. Akwanov: *"Verraeter. Wie... unternehmerisch von Ihnen."* |

---

### Stufe 5 -- "Akten, die niemand lesen sollte"
**Trigger:** Tag +10 nach Stufe 4, `rivalry >= 40`.

> *"Mir ist da ein Ordner zugespielt worden. Ihr Ordner. Interessante Lektuere --
> besonders die Margen. Ich erwaege, der Presse einen kleinen, sehr selektiven
> Einblick zu gewaehren. 'Garnelen-Baron beutet Plankton aus', so in der Art. Rein
> hypothetisch. Ich habe sogar schon die Schlagzeile gesetzt."*

**Spieler-Ziel:** Spionage / Informationskrieg. Angriff auf die Reputation.

> *Effekt bei Nichtstun:* Reputation -12 (Schmierkampagne). Bei aktivem
> Doppelagent (Stufe 4) oder Tanten-Connection (Stufe 3): du kennst die Story
> vorher und kannst kontern.

| Option | Konsequenz |
|---|---|
| **Gegen-Leak veroeffentlichen** *(nur mit Doppelagent/Tante)* | Akwanovs eigene Akten leaken. Reputation +6 statt -12. Rivalitaet +20. Riesen-Lacher: *"Wer hat... oh. Dmitri. DMITRI!"* |
| **PR-Agentur engagieren (-3.000 Geld)** | Reputationsverlust auf -3 begrenzt. Rivalitaet +5. |
| **Den Schlag einstecken** | Reputation -12. Rivalitaet +0. Spar dir das Geld, blut bei der Reputation. |

---

### Stufe 6 -- "Das grosse Embargo"
**Trigger:** `money >= 45.000` (du bist kurz vorm Sieg) oder `rivalry >= 55`.

> *"Es schmerzt mich -- wirklich, hier, direkt unter der Krawattennadel. Aber
> Usbekistan verhaengt ab heute ein VOLLEMBARGO auf Hallen-Garnelen aus
> auslaendischer Produktion. Kein Export laeuft mehr ohne meinen Stempel. Und
> meine Stempel sind, sagen wir, gerade alle in der Reinigung. Auf unbestimmte
> Zeit."*

**Spieler-Ziel:** Haertester Markt-Druck. `exportTariff = 0.40` (skaliert mit
`rivalry` bis 0.6), unbefristet -- bis du diesen Beat aufloest.

| Option | Konsequenz |
|---|---|
| **Schattenmarkt "Trockenhafen Taschkent" oeffnen** | Schaltet `usbekChannel = true` frei: zweiter Absatzkanal, **immun gegen das Embargo**, aber zu Akwanovs Festpreis (Basispreis ~0,75x, dafuer planbar). Ironie pur: du verkaufst jetzt AN Usbekistan. Rivalitaet -10. Akwanov: *"Wenn man sie nicht schlagen kann... besteuert man sie wenigstens an der eigenen Grenze."* |
| **Welthandels-Klage einreichen (-4.000 Geld, dauert 8 Tage)** | Embargo wird nach 8 Tagen komplett gekippt (`exportTariff = 0`). Rivalitaet +25. In der Zwischenzeit blutest du. |
| **Eigenen Inlandsmarkt hochziehen (Restaurant-Pivot)** | Kein Geld-Sofortverlust, aber du musst aktiv auf Restaurants umstellen (embargofrei). Reputation +4 fuer "regionale Wertschoepfung". |

---

### Stufe 7 -- "Der Stromstecker-Vorfall"
**Trigger:** Tag +10 nach Stufe 6, `rivalry >= 60`. Eskalations-Vorbote.

> *"Ich hoere, Ihr Stromnetz haengt an derselben Boerse, an der Usbekistan gerade
> aggressiv... investiert. Rein finanziell, versteht sich. Es waere ein JAMMER,
> wenn ausgerechnet heute Nacht die Preise explodieren und Ihre romantische
> Beckenbeleuchtung ausgeht. Garnelen sind im Dunkeln so... schreckhaft."*

**Spieler-Ziel:** Letzte Sabotage vor der grossen Wahl. Angriff auf Strom/Betriebskosten.

> *Effekt bei Nichtstun (vor `sabotageResist`):* `upkeep`-Spike fuer 5 Tage
> (Betriebskosten +60%) + ein Tick mit `powerRatio`-Drosselung (alle Becken
> laufen 1 Tag nur auf 70%).

| Option | Konsequenz |
|---|---|
| **Eigenes Solar-Dach / Inselnetz ausbauen** | Macht dich strompreis-unabhaengig. Beat abgewehrt, +5 Reputation (gruener Strom). Rivalitaet +10. |
| **Notreserve anzapfen (-2.500 Geld)** | Spike abgefedert, kein Produktionsverlust. Rivalitaet +0. |
| **Es drauf ankommen lassen** | Voller Effekt (ggf. durch `sabotageResist` gedaempft). Rivalitaet +5 ("Sie sind zaeher als gedacht"). |

---

### Stufe 8 -- "Meer der Entscheidungen" (Finale: Allianz ODER Eskalation)
**Trigger:** `rivalry >= 66` ("Kalter Krill-Krieg") UND `money >= GOAL_MONEY`
(du bist Tycoon). Der grosse Schluss-Showdown.

> *"Mein Rivale. Mein wuerdiger, halsstarriger Rivale. Wir haben uns bekriegt,
> belauert, beleakt -- es war WUNDERBAR. Aber sehen Sie: Sie haben gewonnen, was
> man mit Geld gewinnt. Ich habe gewonnen, was man mit Akten gewinnt. Vielleicht
> ist es Zeit fuer... etwas Groesseres. Oder Sie zwingen mich, meine letzte,
> haesslichste Akte zu oeffnen. Ihre Wahl, mein Freund. Ich habe Tee mitgebracht.
> Und einen Vertrag. Und, nun ja, auch eine Bombe -- aber nur bildlich."*

**Spieler-Ziel:** Den Bogen abschliessen. Drei sich gegenseitig ausschliessende Enden.

#### Ende A -- **Die Ueberraschungs-Allianz** *(empfohlen bei niedrigem-mittlerem rivalry-Verlauf / vielen Zugestaendnissen)*
> *"Die 'Vereinigte Hallen-Garnelen-Union'! Sie liefern das Know-how, Usbekistan
> die Subventionen meiner Tante. Gemeinsam machen wir den Ozean endgueltig
> arbeitslos."*

- Embargo dauerhaft aufgehoben (`exportTariff = 0`).
- **Neuer dauerhafter Bonus:** "Akwanov-Subvention" -> +0,15 auf `repMult`
  ODER pauschal +12% Verkaufspreis (Union-Markt). Schattenmarkt bleibt offen.
- Reputation +15. Akwanov wird vom Rivalen zum (windigen) Geschaeftspartner ->
  ab jetzt nur noch *gute* Akwanov-Events.
- Achievement: **"Doppelt eingeschlossen, gemeinsam reich."**

#### Ende B -- **Totale Eskalation (Handelskrieg bis zum letzten Krill)** *(bei hohem rivalry)*
> *"Dann KRIEG, mein Freund. Wirtschaftskrieg. Ich habe Zeit, ich habe Akten, und
> ich habe -- ich kann es nicht oft genug sagen -- KEIN MEER, das mich ablenkt."*

- Permanenter "Akwanov-Druck": dauerhaftes `exportTariff = 0.20` UND erhoehte
  Frequenz von RIVAL-Sabotage-Events (alle ~12 Tage statt storyline-gebunden).
- ABER: Sieg moeglich durch **Dominanz**. Erreichst du `money >= 100.000` trotz
  Dauer-Embargo, kapituliert Akwanov:
  > *"Sie... haben gewonnen. Mit HALLEN. Ich gehe zurueck nach Taschkent und
  > zuechte Karpfen. Karpfen, verdammt. In einer Badewanne."*
- Achievement: **"Krill or be krilled."**

#### Ende C -- **Der feindliche Aufkauf (du schluckst Usbekistan)** *(nur wenn money sehr hoch UND Doppelagent aus Stufe 4 aktiv)*
> *"Sie wollen mein Ministerium KAUFEN? Das ist... das ist das Frechste, was mir
> je angeboten wurde. Ich bin geruehrt. Ich bin beleidigt. Ich nehme an."*

- Einmalzahlung (z.B. -40.000 Geld). Du uebernimmst den usbekischen Markt
  KOMPLETT: Schattenmarkt wird zum Premium-Kanal (Basispreis 1,2x, embargofrei,
  unbegrenzte Abnahme).
- Akwanov wird dein **Angestellter** -- erscheint fortan in Events als dein
  schlecht gelaunter, hochbezahlter Lobbyist ("Aussenminister a.D., jetzt
  Senior Vice President fuer Akten").
- Achievement: **"Aussenminister, gekauft und bezahlt."**

---

## 5. Rivalitaets-Score: Zusammenfassende Wirkungstabelle

| rivalry | Phase | Effekt auf den Bogen |
|---|---|---|
| 0-30 | Hoeflich | Nur milde Tarife, kleine Sticheleien. Enden tendieren zu **Allianz (A)**. |
| 31-65 | Handelskrieg | Embargos + storyline-getriggerte Sabotage. Alle Enden moeglich. |
| 66-100 | Kalter Krill-Krieg | Harte Sabotage, hohe Tarife. Schaltet **Eskalation (B)** & **Aufkauf (C)** frei. |

**Wie sich rivalry bewegt:** Konfrontation (zerreissen, klagen, Gegen-Leak) ->
hoch. Zugestaendnisse (unterschreiben, Schattenmarkt nutzen, bestechen) ->
runter. Der Spieler steuert sein eigenes Ende ueber den ganzen Bogen mit.

---

## 6. Implementierungs-Checkliste (fuer die Engine)

- [ ] `GameState`: Felder `rivalActive`, `rivalry`, `exportTariff`,
      `usbekChannel`, `sabotageResist`, `dataLeak`, `lastRivalDay`.
- [ ] `tick()` Verkauf: Preis um `* (1 - exportTariff)` erweitern (nur
      SALES_OFFICE-Export, Restaurant ausgenommen).
- [ ] Schattenmarkt-Verkaufszweig (`usbekChannel`): Festpreis, ignoriert
      `exportTariff`.
- [ ] Neue Event-Kategorie `RIVAL` in `GameEvent.Kind`; storyline-getriggert
      ueber einen eigenen `RivalStoryline`-Treiber (analog `EventSystem`, aber
      sequentiell statt zufaellig).
- [ ] Popup-Dialoge mit 1-3 Auswahl-Buttons (neuer modaler Dialog im UI;
      bestehende Events sind nur Log-Eintraege -- der Rivalen-Bogen braucht
      Entscheidungs-Popups).
- [ ] Rivalen-Panel im Gebaeude-/Status-Inspektor: Foto, Score-Balken,
      aktueller Schritt, aktive Tarife.
- [ ] Achievements A/B/C.
