# ShrimpTopia v3 — Das Hauptquartier als Kommandozentrale

> "Ein HQ ist kein Buero. Ein HQ ist der Ort, an dem du beschliesst, dass 14.000 Garnelen
> deine Steuererklaerung sind." — General Krillkill, beim Versuch, den Konferenzraum zu beschlagnahmen

Dieses Dokument macht das **Hauptquartier (HQ)** zum Herzstueck der Verwaltung: ein einzelnes,
nicht abreissbares Gebaeude (Zone: Empfang & Garten), das beim Anklicken **nicht** den normalen
Gebaeude-Inspektor oeffnet, sondern ein eigenes **Kommandozentrale-Panel** mit drei Tabs:
**Imperium-Status**, **Edikte** und **HQ-Ausbau**.

Alles ist transkribier-fertig: IDs, Bedingungen in Worten, konkrete Zahlen, Optionen mit Effekten.
Werte beziehen sich auf die bekannte v2/v3-Skala (Ziel `GOAL_MONEY = 50.000`, Reputation 0-100,
Verkaufspreis-Faktor 0,6x-1,4x ueber Reputation). Neue v3-Ressourcen (Schalen, SHRIMPBOOST,
Roboter, Armee-Staerke) sind beruecksichtigt.

**Engine-Hooks (neue Felder, die das HQ liest/schreibt):**
- `hqLevel` (0-3, Ausbaustufe), `hqUpgrades` (Set von Upgrade-IDs)
- `activeEdicts` (Set von Edikt-IDs), `edictSlots` (max. gleichzeitig aktive Edikte, default 2)
- pro Edikt ein `mutexGroup` (gegenseitiger Ausschluss), `requires` (Voraussetzung), `cost`/`upkeep`
- Multiplikatoren, die der Tick-Loop farmweit anwendet: `effGlobal`, `repPerTick`, `sellPriceMul`,
  `upkeepMul`, `armyPerTick`, `boostDemand`, `shellGain`, `roboEff` usw.

---

## TEIL 1 — HQ-Uebersicht (Empire-Status beim Anklicken)

Klick aufs HQ -> **Kommandozentrale** oeffnet sich (Standard-Tab: Imperium-Status). Optik in
Java2D-Vektor: dunkle Holz-/Stahl-Schreibtischflaeche, eine grosse Landkarte an der Wand mit
blinkenden Zonen-Markern, ein Globus (den Akwanov staendig auf Usbekistan dreht), ein rotes
Telefon (Krillkill-Hotline), ein Aktenstapel (Behoerde). Eine kleine Greg-Figur sitzt im
Wasserglas auf dem Schreibtisch und kommentiert trocken.

### 1.1 Kopfzeile — "Lage des Imperiums"

Eine Status-Zeile mit Gesamt-Bewertung, abgeleitet aus mehreren Achsen:

- **Imperium-Rang** (Titel, skaliert mit Vermoegen): Hinterhof-Zuechter (<5k) -> Becken-Baron
  (5-20k) -> Garnelen-Magnat (20-50k) -> **Shrimp-Tycoon** (50k, Sieg) -> Imperator von Garnelien
  (>100k).
- **Fortschrittsbalken zum Ziel:** `money / GOAL_MONEY` als Prozent ("Du bist zu 64% ein Tycoon").
- **Tag & Phase:** aktueller Tag + Phasen-Label (P1 Grundbetrieb … P4 Endgame, aus `progression.md`).
- **Stimmungs-Smiley** der Belegschaft (aus Reputation + Arbeiter-Effizienz): von "meutert gleich"
  bis "wuerde fuer dich ins Becken springen".

### 1.2 Ressourcen-Bilanz (die Lebensadern)

Tabelle mit **Bestand**, **Produktion/Tick**, **Verbrauch/Tick**, **Netto** (gruen/rot) je Ressource:

| Ressource | Bestand | +/Tick | -/Tick | Netto | Bemerkung |
|---|---|---|---|---|---|
| Vermoegen (Geld) | live | Einnahmen | Upkeep | Netto-Cashflow | rot blinkend bei Netto < 0 |
| Strom | live | Erzeugung | Verbrauch | Reserve | Warnung "Brownout droht" bei Netto < 0 |
| Wasser | live | … | … | … | |
| Futter | live | … | … | … | |
| Shrimps (gesamt + je Tier 1-6) | live | Zucht | Verkauf/Verbrauch | | Tier-Aufschluesselung als Mini-Balken |
| Arbeiter | belegt/frei | Wohnheime | Bedarf | frei | inkl. **Roboter-Anteil** (s.u.) |
| Reputation | 0-100 | repPerTick | | Trend-Pfeil | steuert Verkaufsfaktor 0,6x-1,4x |
| **Schalen** (neu) | live | aus Becken | Fabrik/Werk/Kraftwerk | | Nebenprodukt, "Abfall wird Gold" |
| **SHRIMPBOOST** (neu) | live | Fabrik | Verkauf/Boost-Konsum | | Energydrink, hoher Verkaufswert |
| **Roboter** (neu) | live | Roboter-Werk | — | | zaehlen als Arbeiter UND/ODER Armee |
| **Armee-Staerke** (neu) | live | Krill-Kaserne | Event-Verluste | | Verteidigung gegen Negativ-Events |

> Greg (im Wasserglas): "Netto-Cashflow rot? Mut. Echter Tycoon-Mut. Oder Inkompetenz. Schwer zu sagen."

### 1.3 Live-Boni-Liste ("Was zieht gerade an meinen Zahlen?")

Eine scrollbare Liste **aller aktiven Modifikatoren** mit Quelle und Vorzeichen — endlich
nachvollziehbar, warum die Zahlen so sind. Beispiele:

- `+18% Verkaufspreis` (3x Forschungslabor)
- `-0,15 Rep/Tick` (Kraftwerk Vollast-Modus, 2x)
- `+20% Effizienz` (Edikt: SHRIMPBOOST-Pflicht)
- `-25% Upkeep` (HQ-Upgrade: Verwaltungsetage)
- `+Tier-Veredelung aktiv` (Genlabor)

Klick auf einen Eintrag springt zum verursachenden Gebaeude/Edikt. Das ist gleichzeitig der
"bessere Inspektor"-Wunsch auf Imperiums-Ebene.

### 1.4 Charakter- & Diplomatie-Ampel

Drei kleine Portraits mit Status-Leiste:
- **General Krillkill:** Armee-Staerke + aktuelle "Operation"-Stufe ("Operation Protein-Sturm,
  Stufe 4/7"). Knopf "Lagebericht".
- **Aussenminister Akwanov:** **Rivalitaets-Score** (0-100) + aktives Embargo/Tarif. Je hoeher,
  desto mehr Saebelrasseln. Knopf "Diplomatie".
- **Behoerde (Dr. Quallmann):** Buerokratie-Level + offene Formulare. Knopf "Akten".

### 1.5 Quest-/Ziel-Tracker (der wichtige Teil fuer die Spielzeit-Verlaengerung)

Da v3-Quests **nicht durchklickbar** sind, sondern an **Ziele ueber Zeit** gebunden, zeigt das HQ
einen permanenten **Ziel-Tracker** mit Fortschrittsbalken pro aktivem Ziel:

- "Produziere insgesamt **5.000** Tier-3-Shrimps" — `[#####-----] 2.640 / 5.000`
- "Halte **2.000 Schalen** im Lager" — `[#######---] 1.420 / 2.000`
- "Verkaufe insgesamt **50.000** Shrimps" — Balken
- "Armee-Staerke **>= 250**" — Balken
- "Erreiche **Tag 60**" — Balken

Trigger-Typen (Engine, fuer alle Quests/Edikt-Voraussetzungen nutzbar): *erreiche X Vermoegen;
Tag X; produziere insgesamt N Shrimps Tier k; sammle/halte N einer Ressource; verkaufe insgesamt N;
baue N Gebaeude (Typ/Zone); Armee-Staerke >= X; Reputation >= X; Rivalitaet >= X.* Alle Schwellen
unten sind bewusst **gross & gestaffelt** (Stunden statt 30 Minuten).

---

## TEIL 2 — EDIKTE (Dekrete im Tropico-Stil)

**Was ist ein Edikt?** Ein farmweites Dekret, das du im HQ-Tab "Edikte" **ein- und ausschaltest**.
Anders als Gebaeude-Modi (lokal) wirken Edikte **global**. Sie sind Tropico-Politiken: klare
Trade-offs, oft gegenseitig ausschliessend, manche mit Voraussetzung oder Kosten.

**Regeln (Engine):**
- **Edikt-Slots:** Anfangs **2** gleichzeitig aktiv. HQ-Upgrade "Verwaltungsetage" -> 3,
  "Lagezentrum" -> 4. (Verhindert, dass man einfach alles anschaltet — man muss waehlen.)
- **Aktivierung:** Einmalkosten (`cost`, falls vorhanden) + ggf. laufender `upkeep`/Tick.
  Sofort wirksam, sofort abschaltbar (ausser markierte "gebunden"-Edikte mit Mindestlaufzeit).
- **Mutex-Gruppen:** Edikte derselben `mutexGroup` schliessen sich aus (UI graut die anderen aus).
- **Wechsel-Cooldown:** Aktiviert/deaktiviert pro Edikt max. 1x alle 3 Tage (Anti-Cheese,
  analog Arbeiter-Politik-Cooldown).

Insgesamt **14 Edikte** in 5 Mutex-Gruppen + freie Edikte.

### Mutex-Uebersicht (was sich gegenseitig ausschliesst)

| Gruppe | Edikte | Thema |
|---|---|---|
| **WERBUNG** | E01 Tag der offenen Halle ⟂ E02 Festung Garnelien | Offenheit vs. Abschottung |
| **ARBEIT** | E03 SHRIMPBOOST-Pflicht ⟂ E04 Work-Life-Balance-Becken | Leistung vs. Wohlfuehlen |
| **STAAT** | E05 Kriegsrecht ⟂ E06 Steueroase Garnelien ⟂ E07 Gewaltenteilung | Macht-Ausrichtung |
| **HANDEL** | E08 Garnelen-Standard (Goldstandard) ⟂ E09 Dumping-Offensive | Preis hoch vs. Menge |
| **OEKO** | E10 100%-Oeko-Siegel ⟂ E11 Maximale-Ausbeute-Doktrin | Image vs. Output |
| *(frei, kombinierbar)* | E12 Greg in den Vorstand, E13 Roboter-Wehrpflicht, E14 Schalen-Pfand | Einzelgaenger |

---

### E01 — "Tag der offenen Halle"  *(Gruppe WERBUNG)*

- **id:** `edict_open_hall`
- **Effekt:** **+0,25 Reputation/Tick** [FARMWEIT]; **+8% Verkaufspreis** (Besucher kaufen direkt);
  ABER **-10% Strom/Wasser-Effizienz** (Touristen latschen durch die Anlage, lassen Tueren offen)
  und **+10% Chance** auf Stoer-Events (jemand faellt ins Becken).
- **Kosten:** Einmalig **400 G** (Beschilderung, Absperrbaender). Kein Upkeep.
- **Voraussetzung:** Zone C (Handelspromenade) freigeschaltet ODER 1 Besucherzentrum.
- **Mutex:** schliesst **E02 Festung Garnelien** aus.
- **Gag:** "Schulklassen, Influencer und genau ein Mann, der jedes Jahr kommt und fragt, ob die
  Garnelen 'auch Gefuehle haben'. Ja, Herbert. Hauptsaechlich Existenzangst."

### E02 — "Festung Garnelien"  *(Gruppe WERBUNG)*

- **id:** `edict_fortress`
- **Effekt:** **-50% Schaden durch Negativ-Events** [FARMWEIT] (Diebstahl, Moewen, Spionage);
  **+5% Armee-Staerke-Erzeugung**; ABER **-0,15 Rep/Tick** (geschlossen, paranoid, "was verbergt
  ihr da?") und **kein Besucher-Einkommen**.
- **Kosten:** Einmalig **800 G** (Zaun, Kameras, Stacheldraht in Garnelen-Optik). Upkeep **+3/Tick**.
- **Voraussetzung:** 1 Militaer-Depot ODER Krillkill aktiv.
- **Mutex:** schliesst **E01 Tag der offenen Halle** aus.
- **Gag:** Krillkill: "ENDLICH. Jeder Besucher ist ein potenzieller Suppen-Spion, Soldat. JEDER."

### E03 — "SHRIMPBOOST-Pflicht fuer alle Mitarbeiter"  *(Gruppe ARBEIT)*

- **id:** `edict_boost_mandate`
- **Effekt:** **+20% Arbeiter-Effizienz** [FARMWEIT] (alle laufen auf Energydrink); ABER
  **verbraucht SHRIMPBOOST** (z.B. 2 SHRIMPBOOST/Tick aus dem Lager — laeuft das Lager leer,
  faellt der Bonus auf 0 und es regnet **-0,1 Rep/Tick** "Mitarbeiter zittern sichtbar").
- **Kosten:** Kein Einmalpreis. Laufender SHRIMPBOOST-Verbrauch (s.o.).
- **Voraussetzung:** 1 SHRIMPBOOST-Fabrik gebaut.
- **Mutex:** schliesst **E04 Work-Life-Balance-Becken** aus.
- **Gag:** "Die Belegschaft hat seit drei Tagen nicht geblinzelt, aber die Produktivitaet ist
  GIGANTISCH. Die Gewerkschaft (Klausi) prueft rechtliche Schritte."

### E04 — "Work-Life-Balance-Becken"  *(Gruppe ARBEIT)*

- **id:** `edict_work_life`
- **Effekt:** **+0,2 Rep/Tick** [FARMWEIT]; **immun gegen Burnout-/Streik-Events**;
  **+1 Tier-Qualitaet** in 20% der Becken (entspannte Shrimps wachsen edler); ABER
  **-12% Arbeiter-Effizienz** (4-Tage-Woche, Achtsamkeits-Pausen).
- **Kosten:** Einmalig **600 G** (Yogamatten, Saftbar). Kein Upkeep.
- **Voraussetzung:** keine.
- **Mutex:** schliesst **E03 SHRIMPBOOST-Pflicht** aus.
- **Gag:** "Jeder Mitarbeiter hat jetzt ein 'persoenliches Lieblingsbecken'. Die Produktivitaet
  sinkt, die Augenringe verschwinden. Klausi nickt zufrieden."

### E05 — "Kriegsrecht"  *(Gruppe STAAT)*

- **id:** `edict_martial_law`
- **Effekt:** **+25% Output aller Produktionsgebaeude** [FARMWEIT] (Krillkill bruellt rund um die
  Uhr); **+10% Armee-Staerke-Erzeugung**; **immun gegen Demos/Tierschutz-Events**; ABER
  **-0,4 Rep/Tick** [FARMWEIT-Rep] (es ist, nun ja, Kriegsrecht) und **+15% Stromverbrauch**.
- **Kosten:** Einmalig **1.500 G**. Upkeep **+5/Tick** (Krillkills "Beratungshonorar").
- **Voraussetzung:** General Krillkill aktiv (Stufe >= 3) UND 1 Krill-Kaserne.
- **Mutex:** schliesst **E06 Steueroase** und **E07 Gewaltenteilung** aus.
- **Gag:** Krillkill: "DIE SHRIMPS MARSCHIEREN IM GLEICHSCHRITT, SIR! …das tun sie nicht, es sind
  Garnelen, aber DER GEIST STIMMT!"

### E06 — "Steueroase Garnelien"  *(Gruppe STAAT)*

- **id:** `edict_tax_haven`
- **Effekt:** **-30% Upkeep aller Gebaeude** [FARMWEIT] (kreative Buchfuehrung); **+5% behaltenes
  Verkaufseinkommen**; ABER **+40% Chance** auf Einzelquest **E5 Steuerpruefung** (`beh_steuer`)
  und **-0,1 Rep/Tick** ("Briefkasten-Garnele auf den Cayman-Inseln").
- **Kosten:** Einmalig **1.000 G** (Anwaelte). Kein Upkeep (das ist ja der Witz).
- **Voraussetzung:** Vermoegen >= 15.000 (man muss erst was zu schuetzen haben).
- **Mutex:** schliesst **E05 Kriegsrecht** und **E07 Gewaltenteilung** aus.
- **Gag:** "Offiziell hat deine Halle ihren Sitz in einem Briefkasten in Garnelien, einem Land,
  das nur auf deinen Steuerunterlagen existiert. Herr Pfennig vom Finanzamt schwitzt schon."

### E07 — "Gewaltenteilung (Demokratie der Becken)"  *(Gruppe STAAT)*

- **id:** `edict_democracy`
- **Effekt:** **+0,3 Rep/Tick** [FARMWEIT]; **-50% Chance auf Behoerden-/Demo-Eskalation**;
  schaltet seltene Bonus-Quests frei ("Wahlkampf"); ABER **-15% Output** (Buerokratie,
  Abstimmungen, "die Algenfarm legt ein Veto ein").
- **Kosten:** Einmalig **500 G** (Wahlurnen in Becken-Groesse). Kein Upkeep.
- **Voraussetzung:** keine.
- **Mutex:** schliesst **E05 Kriegsrecht** und **E06 Steueroase** aus.
- **Gag:** "Die Garnelen waehlen jetzt einen Becken-Rat. Vorsitzender: Klausi (schon wieder).
  Erste Amtshandlung: laengere Mittagspause. Krillkill ist FASSUNGSLOS."

### E08 — "Garnelen-Standard (der Goldstandard)"  *(Gruppe HANDEL)*

- **id:** `edict_gold_standard`
- **Effekt:** **+20% Verkaufspreis** [FARMWEIT]; **+0,1 Rep/Tick** (Prestige); ABER
  **-25% Verkaufsmenge/Tick** (nur die besten Stuecke gehen raus, der Rest "wartet auf Niveau")
  und **nur Tier >= 3 profitiert vom Bonus**.
- **Kosten:** Einmalig **1.200 G** (Marken-Kampagne). Kein Upkeep.
- **Voraussetzung:** Tier 3 freigeschaltet (Genlabor ODER Labor-Upgrade "Feinschmecker-Genetik").
- **Mutex:** schliesst **E09 Dumping-Offensive** aus.
- **Gag:** "Eine ShrimpTopia-Garnele ist jetzt eine Wertanlage. Banken akzeptieren sie als
  Sicherheit. Chad Krabbowski ist gruen vor Neid (oder das ist seine Krawatte)."

### E09 — "Dumping-Offensive"  *(Gruppe HANDEL)*

- **id:** `edict_dumping`
- **Effekt:** **+40% Verkaufsmenge/Tick** [FARMWEIT] (alles muss raus); **drueckt Chad Krabbowskis
  Marktanteil** (Rivalitaet sinkt langsam); ABER **-20% Verkaufspreis** und **-0,2 Rep/Tick**
  ("Billig-Shrimp-Schleuder").
- **Kosten:** Einmalig **300 G**. Kein Upkeep.
- **Voraussetzung:** keine.
- **Mutex:** schliesst **E08 Garnelen-Standard** aus.
- **Gag:** "Drei Garnelen zum Preis von einer! Akwanov spricht von 'unfairen Wettbewerbsmethoden',
  was reich ist, kommend von Akwanov."

### E10 — "100%-Oeko-Siegel"  *(Gruppe OEKO)*

- **id:** `edict_eco_seal`
- **Effekt:** **+0,3 Rep/Tick** [FARMWEIT]; **+12% Verkaufspreis** (Oeko-Aufschlag); schaltet
  Bonus-Markt "Bio-Feinkost" frei; ABER **-15% Strom-Effizienz** (kein Kohle-Kraftwerk-Bonus,
  nur "saubere" Energie) und **Kraftwerke im Vollast-Modus sind verboten** (Engine sperrt den Modus).
- **Kosten:** Einmalig **900 G** (Zertifizierung von einer Behoerde, die es vielleicht gibt).
  Upkeep **+2/Tick** (Audits).
- **Voraussetzung:** Reputation >= 50.
- **Mutex:** schliesst **E11 Maximale-Ausbeute-Doktrin** aus.
- **Gag:** "Deine Halle ist jetzt klimaneutral. Die Garnelen kompensieren ihren CO2-Fussabdruck
  durch das Pflanzen winziger Algen-Baeume. Lena von 'Krill Lives Matter' weint vor Glueck."

### E11 — "Maximale-Ausbeute-Doktrin"  *(Gruppe OEKO)*

- **id:** `edict_max_yield`
- **Effekt:** **+30% Shrimp-Produktion** [FARMWEIT]; **+50% Schalen-Ausbeute** (mehr Durchsatz =
  mehr Abfall); ABER **-1 Tier-Qualitaet** in allen Becken (Akkord), **-0,3 Rep/Tick** und
  **+20% Wasser-/Futterverbrauch**.
- **Kosten:** Einmalig **400 G**. Kein Upkeep.
- **Voraussetzung:** keine.
- **Mutex:** schliesst **E10 100%-Oeko-Siegel** aus.
- **Gag:** "Wir pressen jetzt 1,4 Garnelen aus dem Platz fuer eine. Frag nicht wie. Frag NIE wie.
  Die Tierschuetzer haben bereits einen Mietwagen reserviert."

### E12 — "Greg in den Vorstand befoerdern"  *(frei kombinierbar)*

- **id:** `edict_greg_board`
- **Effekt:** **+0,15 Rep/Tick** (das Volk liebt eine Underdog-Garnele); **+5% Effizienz**
  [FARMWEIT] ("Greg motiviert durch stille Praesenz"); **alle 10 Tage** ein kleiner zufaelliger
  Bonus ("Greg hatte eine Idee": +200-800 G ODER +Forschungs-Tick). Kein Nachteil — Greg ist
  ueber jeden Zweifel erhaben.
- **Kosten:** Einmalig **0 G** (Greg arbeitet fuer Plankton). Kein Upkeep.
- **Voraussetzung:** Tutorial abgeschlossen (Greg muss eingefuehrt sein).
- **Mutex:** keine.
- **Gag:** Greg (aus dem Wasserglas): "Ich habe keine Beine, keine Stimme und keinen
  betriebswirtschaftlichen Hintergrund. Perfekte Voraussetzungen fuer einen Vorstandsposten."

### E13 — "Roboter-Wehrpflicht"  *(frei kombinierbar)*

- **id:** `edict_robot_draft`
- **Effekt:** **Alle Roboter zaehlen jetzt als Armee-Staerke** (statt als Arbeiter): **+2
  Armee-Staerke pro Roboter/Tick**; **+10% Verteidigung**; ABER **die Roboter fehlen als
  Arbeitskraft** -> effektiv **-X freie Arbeiter** (= Anzahl Roboter). Bei Arbeitermangel
  stehen Gebaeude still.
- **Kosten:** Einmalig **600 G**. Kein Upkeep.
- **Voraussetzung:** 1 Garnelen-Roboter-Werk UND >= 10 Roboter im Bestand.
- **Mutex:** keine (aber konkurriert faktisch mit dem Arbeitskraft-Bedarf).
- **Gag:** Krillkill: "GARNELENBETRIEBENE KAMPFROBOTER, SIR! Die Zukunft der Kriegsfuehrung hat
  zehn Beine und einen leicht fischigen Geruch!"

### E14 — "Schalen-Pfand (Kreislaufwirtschaft)"  *(frei kombinierbar)*

- **id:** `edict_shell_deposit`
- **Effekt:** **+25% Schalen-Ausbeute** aus allen Becken [FARMWEIT]; **-15% Input-Bedarf** der
  SHRIMPBOOST-Fabrik und des Roboter-Werks (effizienteres Recycling); **+0,05 Rep/Tick** (Oeko);
  ABER **+1 Arbeiter-Bedarf pro Becken** (Sammeln & Sortieren der Schalen).
- **Kosten:** Einmalig **500 G** (Pfand-Automaten in Garnelen-Groesse). Kein Upkeep.
- **Voraussetzung:** 1 SHRIMPBOOST-Fabrik ODER 1 Roboter-Werk (es muss einen Schalen-Abnehmer geben).
- **Mutex:** keine.
- **Gag:** "25 Cent Pfand pro Schale. Die Garnelen haben angefangen, ihre eigenen Schalen zu
  horten, um sie dir zurueckzuverkaufen. Klausi nennt das 'Klasseninstinkt'."

### Edikt-Schnelltabelle (Uebersicht)

| id | Name | Kern-Effekt | Trade-off | Voraussetzung | Mutex |
|---|---|---|---|---|---|
| `edict_open_hall` | Tag der offenen Halle | +0,25 Rep/T, +8% Preis | -10% Strom/Wasser-Eff, +Events | Zone C / Besucherzentrum | E02 |
| `edict_fortress` | Festung Garnelien | -50% Event-Schaden, +5% Armee | -0,15 Rep/T, kein Besucher-$ | Militaer-Depot / Krillkill | E01 |
| `edict_boost_mandate` | SHRIMPBOOST-Pflicht | +20% Arbeiter-Eff | verbraucht BOOST, sonst -Rep | SHRIMPBOOST-Fabrik | E04 |
| `edict_work_life` | Work-Life-Balance-Becken | +0,2 Rep/T, Streik-immun, +Tier | -12% Arbeiter-Eff | — | E03 |
| `edict_martial_law` | Kriegsrecht | +25% Output, +10% Armee | -0,4 Rep/T, +15% Strom | Krillkill≥3 + Kaserne | E06,E07 |
| `edict_tax_haven` | Steueroase Garnelien | -30% Upkeep, +5% Einkommen | +Steuerpruefung, -0,1 Rep/T | Geld≥15.000 | E05,E07 |
| `edict_democracy` | Gewaltenteilung | +0,3 Rep/T, -Eskalation | -15% Output | — | E05,E06 |
| `edict_gold_standard` | Garnelen-Standard | +20% Preis (Tier≥3) | -25% Menge | Tier 3 frei | E09 |
| `edict_dumping` | Dumping-Offensive | +40% Menge, -Rivalitaet | -20% Preis, -0,2 Rep/T | — | E08 |
| `edict_eco_seal` | 100%-Oeko-Siegel | +0,3 Rep/T, +12% Preis | -15% Strom-Eff, Vollast gesperrt | Rep≥50 | E11 |
| `edict_max_yield` | Maximale-Ausbeute | +30% Shrimps, +50% Schalen | -1 Tier, -0,3 Rep/T, +Verbrauch | — | E10 |
| `edict_greg_board` | Greg in den Vorstand | +0,15 Rep/T, +5% Eff, Bonus | keiner (Greg!) | Tutorial fertig | — |
| `edict_robot_draft` | Roboter-Wehrpflicht | Roboter -> +2 Armee/Stk | Roboter fehlen als Arbeiter | Roboter-Werk + ≥10 Robo | — |
| `edict_shell_deposit` | Schalen-Pfand | +25% Schalen, -15% Fabrik-Input | +1 Arbeiter/Becken | BOOST-Fabrik / Roboter-Werk | — |

---

## TEIL 3 — HQ-Ausbau (3 Upgrades)

Das HQ selbst ist ausbaubar (`hqLevel` 0 -> 3). Jede Stufe ist **teuer & gestaffelt** (Endgame-
Senke fuer Geld, verlaengert die Spielzeit) und schaltet eine neue Kommandozentrale-Funktion frei.
Reihenfolge ist fix: Verwaltungsetage -> Lagezentrum -> PR-Abteilung.

### HQ-1 — "Verwaltungsetage"

- **id:** `hq_admin_floor`
- **Kosten:** **6.000 G** + benoetigt **Tag >= 15**.
- **Effekt:**
  - **Edikt-Slots: 2 -> 3** (ein Dekret mehr gleichzeitig).
  - **-15% Upkeep aller Gebaeude** [FARMWEIT] (zentrale Buchhaltung).
  - Schaltet im Imperium-Status die **Live-Boni-Liste** (Teil 1.3) voll auf (vorher nur Top-3).
- **Optik:** zweite Etage waechst auf dem HQ, Fenster mit tippenden Schatten-Silhouetten, ein
  Faxgeraet, das nie aufhoert.
- **Gag:** Mira: "Wir haben jetzt eine Personalabteilung! Sie besteht aus mir und einem Ordner,
  aber wir haben sie."

### HQ-2 — "Lagezentrum (War Room)"

- **id:** `hq_war_room`
- **Kosten:** **18.000 G** + benoetigt **Verwaltungsetage gebaut** + **Zone D freigeschaltet**.
- **Effekt:**
  - **Edikt-Slots: 3 -> 4**.
  - **-50% Schaden durch ALLE Negativ-Events** [FARMWEIT] (Fruehwarnsystem auf der grossen Karte).
  - **+10% Armee-Staerke-Erzeugung** und schaltet die **Diplomatie-/Kriegs-Aktionen** gegen
    Akwanov frei (Rivalitaet aktiv gegensteuern statt nur zusehen).
  - Zeigt **eingehende Events 1 Tick vorher** an ("Moewen im Anflug, Sir!").
- **Optik:** grosse leuchtende Lagekarte (Radar-Sweep wie in Zone D), rotes Telefon klingelt,
  Krillkill steht permanent mit Zeigestock daneben.
- **Gag:** Krillkill: "DAS, SIR, IST EIN KRIEGSRAUM. Hier planen wir den Sieg gegen die SUPPE.
  …Nein, ich erklaere nicht, welche Suppe. OPERATIONSSICHERHEIT."

### HQ-3 — "PR-Abteilung"

- **id:** `hq_pr_department`
- **Kosten:** **40.000 G** + benoetigt **Lagezentrum gebaut** + **Reputation >= 70**.
- **Effekt:**
  - **+0,3 Rep/Tick** [FARMWEIT] dauerhaft (Pressemitteilungen am Fliessband).
  - **Reputation faellt nie unter 30** (Schadensbegrenzungs-Team faengt Shitstorms ab).
  - **+8% Verkaufspreis** [FARMWEIT] (Markenwert).
  - Verwandelt **negative Quests in PR-Chancen**: bei jeder eigentlich rep-negativen
    Quest-/Event-Entscheidung **halbiert** sich der Rep-Verlust.
- **Optik:** Glasfassade mit blinkendem "SHRIMPTOPIA"-Logo, ein Podium mit Mikrofonen,
  Akwanov versucht staendig, sich ins Pressefoto zu draengen.
- **Gag:** Mira: "Wir haben den Shitstorm um Brian in eine Netflix-Doku verwandelt. Brian ist
  jetzt ein Symbol. Brian war Suppe, aber jetzt ist Brian ein SYMBOL."

### HQ-Ausbau-Tabelle

| id | Stufe | Kosten | Voraussetzung | Wichtigster Effekt |
|---|---|---|---|---|
| `hq_admin_floor` | 1 Verwaltungsetage | 6.000 G | Tag ≥ 15 | +1 Edikt-Slot, -15% Upkeep |
| `hq_war_room` | 2 Lagezentrum | 18.000 G | HQ-1 + Zone D | +1 Edikt-Slot, -50% Event-Schaden, Anti-Akwanov |
| `hq_pr_department` | 3 PR-Abteilung | 40.000 G | HQ-2 + Rep ≥ 70 | +0,3 Rep/T, Rep-Boden 30, +8% Preis |

---

## TEIL 4 — Beispiel-Ziel-Quests rund ums HQ (Spielzeit-Verlaengerung)

Damit das HQ den "viele Stunden"-Bogen traegt, hier vier **zielgebundene** HQ-Quests (nicht
durchklickbar — erfuellen sich ueber Zeit durch Produktion). Schwellen bewusst gross.

- **HQ-Q1 "Buerokratie-Endgegner"** (`hq_q_admin`): *Baue die Verwaltungsetage.*
  Ziel: `Vermoegen >= 6.000` halten + Tag >= 15. Belohnung: HQ-1 baubar + Mira-Logzeile.
- **HQ-Q2 "Operation Vorratskammer"** (`hq_q_stock`): *Halte gleichzeitig 3.000 Schalen,
  1.000 SHRIMPBOOST und 50 Roboter.* Belohnung: -10% Input aller v3-Fabriken dauerhaft.
- **HQ-Q3 "Stehende Armee"** (`hq_q_army`): *Erreiche Armee-Staerke >= 500* (ueber Krill-Kaserne,
  ueber viele Ticks). Belohnung: Akwanov traut sich kein hartes Embargo mehr (Rivalitaets-Cap).
- **HQ-Q4 "Garnelien fuer immer"** (`hq_q_empire`): *Verkaufe insgesamt 100.000 Shrimps
  UND erreiche Tag 90 UND Vermoegen >= 100.000.* Belohnung: Titel "Imperator von Garnelien" +
  Denkmal im Empfang & Garten (Greg-Statue aus massivem Plankton).

> Greg: "Hundert Tage? Hunderttausend Garnelen? Chef, du brauchst kein Imperium. Du brauchst
> ein Hobby. …Aber gut, weiter geht's."
