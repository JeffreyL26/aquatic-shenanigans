# ShrimpTopia v2 — Freischalt-Progression & Roter Faden

> "Ein Imperium baut man nicht an einem Tag. Man baut es an ungefaehr vierzig.
> Und an Tag eins versteht man noch nicht mal das Wasserwerk." — HQ-Handbuch, Kapitel 0

Dieses Dokument ist der **rote Faden** ueber alle v2-Design-Dateien. Es beantwortet die
eine Frage, die alle anderen Dokumente offen lassen: **WAS wird WANN freigeschaltet —
und in welcher Reihenfolge erlebt der Spieler Tutorial, Quests, Tiers, Maerkte, Zonen,
Modi/Upgrades und die beiden Charaktere?**

Gelesene Quellen: `tutorial.md`, `quests.md`, `zones_buildings.md`, `modes_upgrades.md`,
`tiers_markets.md`, `char_krillkill.md`, `char_akwanov.md`, `events_extra.md`.

---

## TEIL 1 — Kritik: Ton, Abdeckung, Luecken & Widersprueche

### 1.1 Ton-Konsistenz — sehr gut, durchgehend tragfaehig

- Alle acht Dateien sind **deutsch, witzig, meme-bewusst** und treffen den Tropico/
  Surviving-Mars-Ton. Vermenschlichte Shrimps (BWL-Abschluss, Gewerkschaft "Klausi",
  Influencer-Drops), absurde Behoerden (Formular B-12/Shrimp, Schwimmlizenzen) und
  trockene Pointen ("Brian war Suppe") sitzen.
- Charakterstimmen sind sauber getrennt: Krillkill bruellt in CAPS und Drill-Metaphern,
  Akwanov ist oelig-diplomatisch mit Meer-Running-Gag, Perla (Tutorial) trocken-warm.
- **Keine Emojis** — Konvention eingehalten.
- Kleiner Hinweis: Greg (Perlas Garnele im Wasserglas) und Mira (Assistentin) sind tolle
  wiederkehrende Anker. Empfehlung: Greg darf ruhig auch ausserhalb des Tutorials in
  1-2 Logzeilen auftauchen (Kontinuitaet), und Perla sollte nach dem Tutorial nicht
  spurlos verschwinden — siehe Luecke L4.

### 1.2 Abdeckung der Spieler-Wuensche — vollstaendig

| Wunsch | Abgedeckt in | Status |
|---|---|---|
| Tutorial (an die Hand nehmend) | `tutorial.md` (15 Schritte, Perla) | OK |
| Quests / Handlungsstraenge (Tropico-Popups) | `quests.md` (16 Quests, 4 Ketten) + beide Charakter-Straenge | OK |
| Progressive Freischaltung | verstreut in allen Dateien | **erst hier zusammengefuehrt** |
| Besserer Gebaeude-Inspektor | `tutorial.md` Schritt 11, `modes_upgrades.md`, `char_akwanov.md` (Rivalen-Panel) | OK |
| Mehrere Zonen statt nur Grid | `zones_buildings.md` (4 Zonen) | OK |
| Modi + Upgrades fuer Gebaeude & Arbeiter (mit Oekosystem-Wechselwirkung) | `modes_upgrades.md` | OK, sehr stark |
| Shrimp-Tiers + Maerkte die nur bestimmte Tiers kaufen | `tiers_markets.md` (6 Tiers, 6 Maerkte, Akzeptanzmatrix) | OK |
| Zwei Charaktere (Krillkill, Akwanov) | `char_krillkill.md`, `char_akwanov.md` | OK |

Alles Gewuenschte ist vorhanden. Der eigentliche fehlende Baustein war die **zeitliche
Verknuepfung** — genau das liefert Teil 2.

### 1.3 Luecken & Widersprueche (MUSS-Korrekturen fuer die Umsetzung)

> Diese Punkte sind echte Inkonsistenzen ZWISCHEN den Dateien. Die Progression in Teil 2
> legt jeweils die **kanonische** Variante fest, damit die Engine nicht vier verschiedene
> Tier-Begriffe gleichzeitig kennen muss.

**W1 — Vier widerspruechliche Tier-Systeme.** Das ist die groesste Baustelle.
- `tiers_markets.md`: **6 Tiers** (Feld/Wald/Wiese, Bueroklammer, Wagyu, Neon-Hypebeast,
  Glowrilla 9000/GVO, Krill-Diamant).
- `quests.md`: **4 Tiers** mit anderen Namen (T1 Pfuetzen, T2 Standard, T3 Gourmet, T4 Wagyu).
- `char_krillkill.md`: ein zusaetzliches Top-Tier **"Kampf-Krill"**.
- `modes_upgrades.md` / `zones_buildings.md`: reden nur von "Tier 1-3".
- **Kanon-Entscheidung (siehe 1.4):** Das **6-Tier-System aus `tiers_markets.md` gilt**.
  `quests.md` muss umgelabelt werden (T1->Tier1, T2->Tier2, "Gourmet"->Tier3 Wagyu,
  "Wagyu legendaer"->Tier3). Krillkills **"Kampf-Krill" = Tier 5 "Glowrilla 9000 (GVO)"**
  (beide werden ueber Krillkill-Quest + Genpfusch-Labormodus freigeschaltet, beide gehen
  an den Militaer-Markt — es ist nachweislich derselbe Slot, doppelt benannt).

**W2 — Militaer-Markt dreifach definiert.**
- `tiers_markets.md`: kauft Tier **1 + 5**, Festpreis 1,3x.
- `char_krillkill.md`: kauft "hohe Tiers / Kampf-Krill".
- `zones_buildings.md` (Militaer-Depot): kauft **nur Tier 1**, Festpreis ~18.
- **Kanon:** Ein einziger Markt "Militaer-Kontrakt", physisch das **Militaer-Depot**
  (Zone D). Kauft **Tier 1 (Truppenverpflegung) und Tier 5 (Sonderprojekte)** zu Festpreis,
  rep-unabhaengig. Krillkills "Kampf-Krill" IST Tier 5. Damit sind alle drei Quellen versoehnt.

**W3 — Export-Hafen-Freischaltung doppelt.**
- `zones_buildings.md`: "Botschafts-Buero gebaut ODER Rep > 60".
- `tiers_markets.md`: Quest "Containerschiff klarmachen".
- **Kanon:** Quest "Containerschiff klarmachen" (Akwanov-Strang, ~Stufe 1-2) ist der
  **Story-Ausloeser**; das **Botschafts-Buero** ist das **physische Gebaeude**, das danach
  baubar wird; **Rep > 60** ist der **Soft-Fallback**, falls der Spieler den Akwanov-Strang
  ignoriert. Alle drei sind Stufen desselben Gates.

**W4 — Genlabor vs. Forschungslabor vs. Tier-3-Gate.**
- `zones_buildings.md`: Genlabor erzeugt Tier 3, Freischaltung "Labor + Quest CRISPR-Shrimp".
- `tiers_markets.md`: Tier 3 (Wagyu) via Quest "Becken-Gourmet" + Labor-Upgrade "Feinschmecker-Genetik".
- **Kanon:** Tier 2 (Bueroklammer) kommt passiv mit dem **ersten Forschungslabor**. Tier 3
  (Wagyu) braucht das **Genlabor** (Zone B) ODER das Labor-Upgrade "Feinschmecker-Genetik";
  die Quest heisst einheitlich **"Becken-Gourmet"** (= das frueher "CRISPR-Shrimp" genannte
  Beat). Genlabor und Upgrade sind zwei Wege zum selben Ziel.

**W5 — Zonen-Freischaltung nirgends terminiert.** `zones_buildings.md` sagt "progressiv
freigeschaltet (Quests/Geld)", nennt aber keine Schwellen. **Behoben in Teil 2.4.**

**W6 — Charakter-Reihenfolge ungeklaert.** Krillkill triggert auf Wirtschaftsgroesse >= 80
(Mittelphase, ~Tag 25), Akwanov auf money >= 30.000 (Spaetphase). Beide Dokumente kennen
einander nicht. **Behoben:** Krillkill kommt klar ZUERST (Mid-Game), Akwanov DANACH
(Late-Game). Reihenfolge wird in Teil 2 als Feature gesetzt (erst der verrueckte Verbuendete,
dann der echte Gegenspieler).

**W7 — Tutorial verspricht Inhalte, die spaeter gegated sind.** Das Tutorial (Schritte
13-14) erklaert Tiers und Zonen, obwohl beides zu Spielbeginn noch gesperrt ist.
**Empfehlung:** Diese zwei Schritte als reine "Vorschau, kommt spaeter"-Erklaerung
markieren (Perla: "Brauchst du noch nicht, aber gut zu wissen, Boss") — nicht als sofort
nutzbare Funktion. Sonst klickt der Spieler ins Leere.

**W8 — `quests.md` C1/D1 nutzen Geld-Schwellen, die mit Charakter-Triggern kollidieren
koennen.** C1 ab 10.000 G, D1 ab 20.000 G, Akwanov ab 30.000 G. Da `quests.md` selbst
"max. 1 aktive Hauptquest" fordert, muss ein **globaler Story-Scheduler** Charakter-Beats
und Quest-Ketten serialisieren (siehe 2.6).

### 1.4 Kanonische Setzungen (verbindlich fuer Teil 2)

1. **Tiers:** 6 Stufen aus `tiers_markets.md` sind kanonisch. "Kampf-Krill" = Tier 5.
2. **Maerkte:** 6 Maerkte aus `tiers_markets.md`. Militaer-Depot (Zone D) = "Militaer-Kontrakt".
3. **Charakter-Reihenfolge:** Krillkill (Mid) vor Akwanov (Late).
4. **Story-Scheduler:** Hoechstens EIN narrativer Beat gleichzeitig aktiv (Ketten + Charaktere
   teilen sich einen Slot; Zufallsevents und Einzelquests duerfen parallel laufen).

---

## TEIL 2 — Die Freischalt-Progression (Timeline)

Vier Achsen messen den Fortschritt — die Progression koppelt Freischaltungen an die
jeweils **passendste**:

- **Geld** (`money`, Ziel `GOAL_MONEY = 50.000`) — fuer Wirtschafts-Meilensteine.
- **Tag** (`gs.getDay()`) — fuer Tutorial-Sicherheitsnetze und Cooldowns.
- **Wirtschaftsgroesse** (`shrimp + 4*Becken + 0.01*kumulierter Verkauf`) — fuer Krillkill.
- **Quest-Flags** — fuer narrativ gebundene Inhalte.

### 2.0 Uebersicht: Die fuenf Phasen

| Phase | Grober Zeitraum | Geld-Korridor | Leitmotiv | Neuer Charakter |
|---|---|---|---|---|
| **P0 Tutorial** | Tag 1 (-3) | Start-Polster | "Wie funktioniert die Kette?" | Dr. Perla |
| **P1 Grundbetrieb** | Tag 1-12 | 0 - 5.000 | "Kette stabilisieren, erstes Labor" | (Behoerde, Mira) |
| **P2 Mittelphase** | Tag 12-28 | 5.000 - 20.000 | "Qualitaet entdecken, Krillkill kommt" | **General Krillkill** |
| **P3 Spaetphase** | Tag 28-45 | 20.000 - 50.000 | "Hype, Export, der Rivale erwacht" | **Aussenminister Akwanov** |
| **P4 Endgame** | ab 50.000 / ab Tag ~45 | 50.000+ | "Imperator werden, beide Finals" | beide eskalieren |

---

### 2.1 PHASE 0 — Tutorial (Tag 1, Perla an der Hand)

- **Inhalt:** Die 15 Tutorial-Schritte aus `tutorial.md`, Reihenfolge unveraendert
  (HUD -> Baumenue -> Strom -> Wasser -> Futter -> Personal -> Becken -> Boerse ->
  Inspektor -> Modi/Upgrades -> Tiers -> Zonen -> Pause/Speed).
- **Freigeschaltet ist hier NUR:** die 9 v1-Basisgebaeude (Kraftwerk, Solar, Wasserwerk,
  Algen-Farm, Becken, Wohnheim, Boerse, Labor, Restaurant), Zone A+B sichtbar, Tier 1, Markt
  "Lokale Boerse".
- **Korrektur zu W7:** Tutorial-Schritt 13 (Tiers) und 14 (Zonen) sind **Vorschau-Erklaerungen**.
  Perla sagt explizit, dass beides "spaeter freigeschaltet" wird. Der Tier-Indikator im HUD
  zeigt anfangs nur Tier 1.
- **Sicherheitsnetz:** Geld im Tutorial leicht subventionieren (W aus `tutorial.md`),
  damit niemand vor Schritt 9 verarmt.
- **Uebergang:** Tutorial endet -> Perla bleibt als **gelegentliche Hinweis-Stimme** erhalten
  (behebt Luecke L4): sie meldet sich bei der ersten Freischaltung jeder neuen Mechanik
  ("Boss, das erste Tier-2-Becken laeuft — schau mal in den Inspektor").

---

### 2.2 PHASE 1 — Grundbetrieb (Tag 1-12, money 0 - 5.000)

**Ziel der Phase:** Produktionskette stabil bekommen, erstes Forschungslabor, erste
Buerokratie-Quest, Einstieg in Modi.

**Freischaltungen (in Reihenfolge):**

| Wann (Trigger) | Was wird frei | Quelle |
|---|---|---|
| Sofort nach Tutorial | Alle 9 Basisgebaeude baubar; **Betriebs-Modi** aller Gebaeude (Normalmodus default) | `modes_upgrades.md` |
| 1. Gebaeude eines Typs gebaut | Dessen **Upgrades** im Inspektor kaufbar | `modes_upgrades.md` |
| **1. Forschungslabor gebaut** | **Tier 2 "Bueroklammer-Shrimp"** (passiv) + Becken-Upgrade "Genetik-Boost-Kit" wird sichtbar | `tiers_markets.md`, `modes_upgrades.md` |
| Tier 2 existiert | Restaurant lohnt sich jetzt (kauft Tier 2-4) | `tiers_markets.md` |
| **3 Shrimp-Becken ODER Tag 10** | **Quest-Kette A "Behoerden-Dschungel" (A1)** startet | `quests.md` |
| ab 2 Wasserwerken / Quest | Gebaeude **Wasseraufbereitungs-Hub** (Zone A) | `zones_buildings.md` |
| Labor + 5.000 Geld | Gebaeude **Bio-Reaktor** (Zone A) | `zones_buildings.md` |
| Brownout an 1 Tag + ab Tag 8 | Einzelquest **E1 Stromausfall** moeglich | `quests.md` |

**Roter Faden:** Der Spieler lernt, dass **das Labor der erste echte Hebel** ist (schaltet
Tier 2 frei = Restaurant wird sinnvoll). Die Behoerden-Kette gibt das erste Tropico-Gefuehl
und (ueber A3) einen dauerhaften Buerokratie-Rabatt. Modi werden hier zum ersten Mal aktiv
gebraucht (z.B. Kraftwerk-Drosselbetrieb gegen Rep-Malus, Becken-Premium-Modus als
Vorgeschmack auf Tiers).

---

### 2.3 PHASE 2 — Mittelphase & Krillkills Auftritt (Tag 12-28, money 5.000 - 20.000)

**Ziel der Phase:** Qualitaet (Tier 3) entdecken, Zone C oeffnen, und den ersten Charakter
erleben. **Hier kommt General Krillkill** — bewusst VOR Akwanov.

**Freischaltungen:**

| Wann (Trigger) | Was wird frei | Quelle |
|---|---|---|
| 3 normale Becken / 1. Labor | Gebaeude **Premium-Zuchtbecken** (Tier 2, Zone B) | `zones_buildings.md` |
| Rep > 40 / 1. Restaurant | Gebaeude **Besucherzentrum** (Zone C) -> Rep-Maschine | `zones_buildings.md` |
| **Wirtschaftsgroesse >= 80 UND Tag >= 25 UND >= 2 Becken** | **GENERAL KRILLKILL Vorstellungs-Popup** | `char_krillkill.md` |
| Krillkill Stufe 1 | Becken-Modus **"Drill-Becken"** | `char_krillkill.md` |
| Krillkill Stufe 3 | Gebaeude **Bootcamp** (Zone D-Vorbote) + Arbeiter-Upgrade "Drill-Instructor" | `char_krillkill.md` |
| Krillkill Stufe 4 | **Markt "Militaer-Kontrakt" (= Militaer-Depot, Zone D)** wird sichtbar | `char_krillkill.md` |
| Quest "Becken-Gourmet" (Mid) + Genlabor/Labor-Upgrade | **Tier 3 "Wagyu-Wassergarnele"** + Gebaeude **Genlabor** (Zone B) | `tiers_markets.md`, `zones_buildings.md` |
| Quest "Containerschiff klarmachen" / Rep > 60 | **Markt Export-Hafen** + Gebaeude **Botschafts-Buero/Export-Hafen** (Zone C) | W3-Kanon |
| ab 2 Algen-Farmen, ab Tag 15 | Einzelquest **E2 Algenbluete** | `quests.md` |
| 5 Becken | Quest-Kette B "Tierschutz" (B1) | `quests.md` |
| 10.000 G / Rep 60 | Quest-Kette C "Influencer" (C1) | `quests.md` |

**Warum Krillkill zuerst (W6-Begruendung):** Krillkill ist ein **liebenswerter Verbuendeter
mit Nebenwirkungen** — perfekt fuer die Mittelphase, in der der Spieler noch experimentiert.
Seine 7-stufige "Operation Protein-Sturm" fuehrt organisch in **Zone D (Untergeschoss)**,
das **Drill-Becken** (erster Tier-modifizierender Modus, der echte Konsequenzen hat) und
den **Militaer-Markt** (erster Spezialmarkt mit Tier-Restriktion) ein. Krillkill ist damit
der **lebende Tutorial fuer das Tier-x-Markt-Konzept**: Er zwingt den Spieler sanft, Tier 5
zu produzieren UND den passenden Abnehmer (Militaer) zu bauen — exakt die Kernlektion aus
`tiers_markets.md`.

**Krillkill-Tier-Verknuepfung (Kanon aus W1/W2):** Krillkills "Kampf-Krill" = **Tier 5
Glowrilla 9000 (GVO)**. Seine Stufe 5 ("Genetik hochfahren") = der Labor-Modus "Genpfusch"
aus `tiers_markets.md`. Tier 5 wird also gemeinsam von Krillkill-Stufe 5/6 UND dem
Labor-Genpfusch-Modus freigeschaltet. Sein Geheimnis-Reveal (Stufe 7) gibt den Dauer-Buff
"Esprit de Corps" und schliesst seinen Strang ab — idealerweise bevor Akwanov richtig
loslegt, damit nie zwei grosse Story-Beats konkurrieren.

**Roter Faden:** Mittelphase = "Qualitaet schlaegt Quantitaet wird zur Option". Tier 3
(Export-Hafen) ist der **erste grosse Geldhebel** ueber Reputation; Krillkill ist der
schraege Pfad zu Tier 5 + Militaer. Der Spieler erlebt zum ersten Mal echte **Build-Archetyp-
Entscheidungen** aus `modes_upgrades.md` (Eco-Tycoon vs. Massenproduktion vs.
Premium-Manufaktur).

---

### 2.4 PHASE 3 — Spaetphase & Akwanov, der echte Rivale (Tag 28-45, money 20.000 - 50.000)

**Ziel der Phase:** Hype-Tiers, Export-Imperium, und der diplomatische Gegenspieler.
**Hier erwacht Aussenminister Akwanov** — als ernsthafter Antagonist, sobald du ein
Schwergewicht bist.

**Freischaltungen:**

| Wann (Trigger) | Was wird frei | Quelle |
|---|---|---|
| 20.000 G | Quest-Kette D "Krabbo-Fehde" (D1) | `quests.md` |
| 1. Restaurant + Rep 50 | Einzelquest E3 Gourmet-Kritiker | `quests.md` |
| ab Tag 20 | Einzelquest E4 Moewen-Invasion | `quests.md` |
| 25.000 G | Einzelquest E5 Steuerpruefung moeglich | `quests.md` |
| **25.000 G + Viral-Event ausgeloest** | **Tier 4 "Neon-Hypebeast-Shrimp"** | `tiers_markets.md` |
| **money >= 30.000 (oder shrimpSoldLast >= 50 x5 Ticks)** | **AUSSENMINISTER AKWANOV Vorstellungs-Popup** + Rivalen-Panel im Inspektor | `char_akwanov.md` |
| Akwanov Stufe 2 | Mechanik **Embargo / exportTariff** wird real (Soft-Tutorial: Restaurants embargofrei) | `char_akwanov.md` |
| Akwanov Stufe 4 | Mechanik **Spionageabwehr / sabotageResist** (HQ/Labor-Upgrade) | `char_akwanov.md` |
| Akwanov Stufe 6 | **Schattenmarkt "Trockenhafen Taschkent"** (`usbekChannel`) waehlbar | `char_akwanov.md` |
| Rep < 40 ODER Schmuggler-Kontakt | **Markt Schwarzmarkt** (Tier 4-6, volatil) | `tiers_markets.md` |
| 30.000 G + Forschung | Einzelquest E6 absurde Idee (Shrimp-Spa) | `quests.md` |
| 35.000 G + Rep 70 | Einzelquest E7 Investor | `quests.md` |
| Genpfusch-Labormodus + Krillkill-Quest | **Tier 5 "Glowrilla 9000 (GVO)"** (= Kampf-Krill) | W1-Kanon |

**Warum Akwanov zuletzt (Begruendung):** Akwanov ist **nur interessant, wenn es etwas zu
bekaempfen gibt**. Sein ganzer Bogen (Embargo, Sabotage, Schmierkampagne) braucht eine
laufende Exportwirtschaft als Angriffsflaeche — genau die hat der Spieler ab ~30.000 G.
Er ist der **mechanisch anspruchsvollere** Charakter (Rivalitaets-Score, neue Maerkte,
Gegenmassnahmen), passt also in die Phase, in der der Spieler das System beherrscht.
Sein Strang nutzt **Markt-Mechaniken** (Export-Tarif, Schattenmarkt) — komplementaer zu
Krillkills **Produktions-Mechaniken** (Drill-Becken, Tier-Veredelung).

**Roter Faden Charakter-Dualismus:** Die beiden sind als **Gegenpol** designt und das
nutzt die Progression aus:
- Arbeiter-Upgrades **"Krillkill-Bootcamp"** (Effizienz, Rep-Kosten) vs. **"Akwanov-Akademie"**
  (Rep, Diplomatie) — `modes_upgrades.md` sagt explizit, dass beide sich danach "nicht mehr
  reden". Das ist ein **wiederkehrender narrativer Konflikt**, der genau in P3 spielbar wird.
- Zufallsevent #14 "Akwanov und Krillkill streiten beim Buffet" (`events_extra.md`) wird
  erst **ab P3 sinnvoll**, wenn beide Charaktere eingefuehrt sind -> Bedingung: beide aktiv.
- Event #7 (Krillkill Verteidigungsuebung) braucht "mind. 2 Zonen" -> ab P2/P3.

#### Zonen-Freischaltung (behebt W5)

`zones_buildings.md` nennt keine Schwellen. Kanonisch gesetzt:

| Zone | Freigeschaltet ab | Begruendung |
|---|---|---|
| **Zone A** (Produktionshalle) | Start | Versorgung ist Grundlage |
| **Zone B** (Becken-Galerie) | Start | Erstes Becken muss irgendwo hin |
| **Zone C** (Handelspromenade) | Phase 2: Rep > 40 ODER 1. Restaurant (= Besucherzentrum-Gate) | Verkauf/Oeffentlichkeit wird erst mit Reputation relevant |
| **Zone D** (Untergeschoss/"Die Tiefe") | Phase 2: **Krillkill-Quest Stufe 3** (Bootcamp) ODER 1. Forschungslabor + Tag 25 | Klandestine Zone = belohnt das Eintauchen in Krillkills Strang |

---

### 2.5 PHASE 4 — Endgame: Imperator statt Tycoon (ab 50.000 G)

**Ziel:** Die beiden Charakter-Finals, die Top-Tiers, der Joker-Markt.

| Wann (Trigger) | Was wird frei | Quelle |
|---|---|---|
| **50.000 G** | **Sieg "Shrimp-Tycoon"** erreicht — Spiel laeuft im "Imperator-Modus" weiter | Projektziel |
| rivalry >= 66 UND money >= 50.000 | **Akwanov Stufe 8 Finale** (Ende A Allianz / B Eskalation / C Aufkauf) | `char_akwanov.md` |
| Krillkill Stufe 7 abgeschlossen | Dauer-Buff "Esprit de Corps" + Denkmal | `char_krillkill.md` |
| Akwanov-Quest "Seidenstrasse" (Endgame) | **Markt Usbekistan-Connection** (alle Tiers, Wuerfelpreis) | `tiers_markets.md` |
| Alle 5 Tiers je 1x verkauft + Quest "Krone der Krustentiere" | **Tier 6 "Krill-Diamant (Imperial)"** | `tiers_markets.md` |
| Akwanov Ende B + money >= 100.000 | Achievement "Krill or be krilled" (Dominanz-Sieg) | `char_akwanov.md` |

**Roter Faden:** 50.000 G ist nicht das Ende, sondern die **Schwelle zum Imperium**. Tier 6
und der Usbekistan-Wuerfelmarkt sind bewusst hinter "alle anderen Tiers verkauft" + Akwanov-
Finale gelegt — sie sind die Belohnung dafuer, dass man **beide Charakter-Boegen UND das
volle Tier-Portfolio** gemeistert hat. Der Progressions-Loop aus `tiers_markets.md` (Tier 1
grau-braun -> Tier 6 Imperial) deckt sich exakt mit dieser Phaseneinteilung.

---

### 2.6 Der Story-Scheduler (behebt W8, Designvorgabe)

Ein zentraler Treiber serialisiert alle narrativen Beats (analog `EventSystem`, aber
deterministisch):

- **Ein Haupt-Slot:** Hoechstens **eine** Quest-Kette ODER ein Charakter-Beat gleichzeitig
  aktiv. Prioritaet bei Konflikt: laufender Charakter-Beat > laufende Kette > neue Kette.
- **Charakter-Exklusivitaet:** Solange ein Krillkill- oder Akwanov-Beat offen ist, startet
  keine neue A/B/C/D-Kette. Umgekehrt schiebt der Scheduler einen faelligen Charakter-Beat
  bis zum Kettenende auf (max. ein paar Tage Verzug — kein Problem, beide Charaktere sind
  ohnehin tagebasiert getaktet).
- **Cooldown:** mind. 3-4 Tage Pause zwischen Beats (Tropico-Gefuehl, aus `quests.md`),
  Charaktere haben eigene 8-12-Tage-Cooldowns (`char_akwanov.md`).
- **Parallel erlaubt:** Zufallsevents (`events_extra.md`) und Einzelquests E1-E7 — sie sind
  kurz und kollidieren nicht mit dem Haupt-Slot.
- **Reputations-Gouverneur:** Da fast alle Beats in Reputation zahlen und Rep den Verkaufspreis
  (0,6x-1,4x) steuert, sollte der Scheduler nicht zwei stark rep-negative Beats direkt
  hintereinander feuern (sonst kippt der Preis-Faktor zu hart — Hinweis aus `quests.md` 3.57).

---

## TEIL 3 — Lernkurve auf einen Blick (Master-Timeline)

```
 Tag  1 ──────── 12 ──────────── 28 ──────────────── 45 ───────── ∞
      │ P0/P1     │ P2             │ P3                 │ P4
      │ Tutorial  │ Mittelphase    │ Spaetphase         │ Endgame
      │ +Grundkette+Labor          │                    │
 ─────┼───────────┼────────────────┼────────────────────┼───────────
 GELD │ 0    5.000│          20.000│              50.000│  100.000
 ─────┼───────────┼────────────────┼────────────────────┼───────────
 TIERS│ T1  ──── T2(Labor) ── T3(Wagyu) ── T4(Neon) ── T5(GVO) ── T6(Diamant)
 ─────┼───────────┼────────────────┼────────────────────┼───────────
MAERKTE Boerse ── Restaurant ─ Export+Militaer ─ Schwarzmarkt ─ Usbekistan
 ─────┼───────────┼────────────────┼────────────────────┼───────────
 ZONEN│ A + B     │ +C, +D         │                    │
 ─────┼───────────┼────────────────┼────────────────────┼───────────
 CHARS│           │ ★ KRILLKILL    │ ★ AKWANOV          │ beide Finals
 ─────┼───────────┼────────────────┼────────────────────┼───────────
QUESTS│ A(Behoerde)│ B(Tier),C(Inf)│ D(Krabbo),E1-E7    │ Krone/Seidenstr.
```

**Designziel der Kurve:** Jede Phase fuehrt **genau eine neue Kernmechanik** prominent ein
(P0 Kette, P1 Labor/Modi, P2 Tiers+Zonen+Krillkill, P3 Maerkte+Akwanov, P4 Top-Tiers+Finals),
waehrend die vorherigen weiterlaufen. So bleibt der Spieler nie ueberfordert, hat aber immer
ein neues Spielzeug. Die beiden Charaktere sitzen bewusst an den zwei spannendsten Wende-
punkten (erstes echtes Mid-Game-Spielzeug = Krillkill; Beherrschung des Systems = Akwanov).

---

## TEIL 4 — Konkrete Korrektur-To-dos fuer die anderen Dateien

1. **`quests.md`** — Tier-Namen auf das 6er-System umstellen (W1). T1->Tier1, T2->Tier2,
   "T3 Gourmet"->Tier3 Wagyu; "Wagyu legendaer T4" streichen (Verwechslungsgefahr).
2. **`char_krillkill.md`** — "Kampf-Krill" als **Alias fuer Tier 5 (Glowrilla GVO)**
   kennzeichnen; Militaer-Kontrakt mit `tiers_markets.md` (Tier 1+5) angleichen (W1/W2).
3. **`zones_buildings.md`** — Militaer-Depot von "nur Tier 1" auf **Tier 1+5** korrigieren;
   Zonen-Freischalt-Schwellen aus 2.4 ergaenzen (W2/W5).
4. **`tiers_markets.md` / `zones_buildings.md`** — Export-Hafen-Gate vereinheitlichen
   (Quest "Containerschiff" + Botschafts-Buero + Rep-Fallback, W3).
5. **`tutorial.md`** — Schritte 13/14 als Vorschau markieren ("kommt spaeter freigeschaltet",
   W7); Perla als gelegentliche Hinweis-Stimme nach dem Tutorial vorsehen (L4).
6. **Engine** — Story-Scheduler implementieren (2.6); `getShrimpEconomySize()` + kumulierter
   Verkauf (Krillkill); `rivalry/exportTariff/usbekChannel/sabotageResist/dataLeak` (Akwanov).

> "Das Ende? Es gibt kein Ende. Es gibt nur das naechste Tier." — Greg, aus dem Wasserglas
