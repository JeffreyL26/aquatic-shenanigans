# ShrimpTopia v3 — Gesamt-Progression & Roter Faden (Phasen-Timeline)

> "Ein Imperium ist kein Sprint, Rekrut. Es ist eine BISQUE: Du kochst sie über
> Stunden ein, und wenn du zu früh den Deckel hebst, ist alles nur Brühe." — General Krillkill
>
> "Geduld, mein teurer Tycoon. Auch Usbekistan wurde nicht an einem Tag zweimal
> eingeschlossen." — Außenminister Akwanov

Dieses Dokument ist der **rote Faden** über alle sechs v3-Design-Dokumente
(`v3_resources.md`, `v3_quests.md`, `v3_army.md`, `v3_hq.md`, `v3_events.md`,
`v3_almanac.md`). Es legt fest, **WAS WANN** passiert: welche Schwellen, Freischaltungen,
Quests, Edikte und Konflikte in welcher **Phase** liegen, sodass das Spiel von ~30 Minuten
auf **mehrere Stunden** wächst — geschichtet in **Früh-, Mittel-, Spät- und Endgame-Phase**.

Es ist außerdem ein **Kritiker-Dokument**: Abschnitt 1 listet die gefundenen Widersprüche
zwischen den Quell-Dokumenten und legt die **kanonische Auflösung** fest, damit die Engine
EINE konsistente Wahrheit umsetzt.

---

## 1. KRITIK: Widersprüche zwischen den Dokumenten (kanonische Auflösung)

Beim Querlesen aller v3-Dokumente sind **10 Inkonsistenzen** aufgefallen. Damit die Engine
nicht vier verschiedene Vokabulare implementiert, gilt ab hier die "KANON"-Spalte.

### 1.1 Uneinheitliche Ressourcen-IDs

| Dokument | SHRIMPBOOST | Roboter | Armee | Schalen |
|---|---|---|---|---|
| `v3_resources.md` | `SHRIMPBOOST` | `ROBOTS` | `ARMY` | `SHELLS` |
| `v3_almanac.md` | `BOOST` | `ROBOTS` | `ARMY` | `SHELLS` |
| `v3_events.md` | `addBoost/getBoost` | `addRobots` | `addArmy` | `addShells` |
| `v3_army.md` | `shrimpboost` | `robots` | `army` | `schalen` |

**KANON:** `ResourceType.SHELLS`, `ResourceType.SHRIMPBOOST`, `ResourceType.ROBOTS`,
`ResourceType.ARMY`. Getter/Setter heißen einheitlich `getShrimpboost()`/`addShrimpboost()`
(NICHT `getBoost`). `v3_events.md`-Schreibweisen (`addBoost`) sind nur Kürzel — Engine bindet
sie auf den Kanon.

### 1.2 Uneinheitliche Objective-/Condition-Typen (gravierend)

Vier Dokumente, vier Namensschemata für dieselben Ziele. **KANON** (an `v3_quests.md`
angelehnt, weil dort am vollständigsten):

| Konzept | KANON-Typ | Verworfene Aliase |
|---|---|---|
| Erreiche X Geld | `MONEY_REACH` | `MONEY` |
| Erreiche Tag X | `DAY_REACH` | `DAY` |
| Produziere insg. N eines Tiers | `PRODUCE_TIER` | `TIER_PRODUCED` |
| Halte N gleichzeitig | `HOLD_RESOURCE` | `RESOURCE_HELD`, `ROBOTS_HOLD` |
| Sammle insg. N (kumulativ) | `COLLECT_TOTAL` | `RESOURCE_PRODUCED`, `RESOURCE_TOTAL` |
| Verkaufe insg. N | `SELL_TOTAL` | `RESOURCE_SOLD`, `TOTAL_SOLD` |
| Baue N Gebäude (Typ/Zone) | `BUILD_COUNT` | — |
| Armee-Stärke ≥ X | `ARMY_REACH` | `ARMY` |
| Reputation ≥ X | `REP_REACH` | `REP` |
| Rivalität ≥ X | `RIVAL_REACH` | `RIVAL` |
| Konflikt X gewonnen | `CONFLICT_WON` | — |

`ROBOTS_HOLD` wird `HOLD_RESOURCE(ROBOTS)`; `PRODUCE_TIER(WARKRILL)` ersetzt das alte
`SHRIMP_PRODUCED` mit Tier-Filter.

### 1.3 GOAL_MONEY: alter 50k-Sieg vs. neues Hunderttausender-Endgame

`v3_hq.md`/`v3_almanac.md` führen noch `GOAL_MONEY = 50.000` (Tycoon) + 100.000 (Imperator).
`v3_quests.md` Kette J endet bei **750.000 G**, Kette D bei 300.000 G.

**KANON:** `GOAL_MONEY = 50.000` bleibt als **erster Meilenstein-Titel "Shrimp-Tycoon"**,
ist aber **NICHT mehr das Spielende** — es ist der Übergang Mittel→Spätphase. Das echte
Endgame-Gate ist `j3_imperator` (750.000 G). Rang-Leiter wird gestreckt (siehe 2.1).

### 1.4 Was frisst die Krill-Kaserne? (PROTEIN vs. WARKRILL)

`v3_almanac.md` (Tier 4) behauptet, **PROTEIN** sei "Pflicht-Input für die Krill-Kaserne".
`v3_resources.md` (4.3) und `v3_army.md` sagen klar: **KAMPF-KRILL (WARKRILL) + SHRIMPBOOST**.

**KANON:** Die Kaserne verbraucht **WARKRILL + SHRIMPBOOST** (so in 2 von 3 Docs + Army-Formel).
PROTEIN ist **Input-Vorstufe** (Mast → günstige Biomasse, Militär-Depot-Absatz, optionaler
Beschleuniger für WARKRILL-Aufzucht), NICHT der direkte Kaserne-Input. Almanac-Tier-4-Text
in diesem Punkt korrigieren.

### 1.5 Zwei inkompatible Armee-Modelle

`v3_resources.md`: Kaserne **+4 Armee/Tag**, Roboter **+3 Armee/Stück** (flach).
`v3_army.md`: elaborierte `armyTarget`-Formel (Kaserne +25 Basis, +0,30/WARKRILL,
+0,8/Roboter-Wach, ×Multiplikatoren, Zerfall).

**KANON:** Das **`armyTarget`-Modell aus `v3_army.md` gewinnt** — es ist das einzige mit
Zerfall/Puffer und trägt das Konflikt-System. Die simplen Zahlen aus `v3_resources.md` (4.3/2.1)
werden als grobe Schätzwerte verworfen. Roboter-Armee-Beitrag = **+0,8 (Wach) / +0,4 (Arbeit)**.

### 1.6 Roboter-Armee-Wert dreifach widersprüchlich

`v3_resources.md`: +3/Robo · `v3_army.md`: +0,8/Robo · `v3_hq.md` E13: +2 Armee/Robo.

**KANON:** **+0,8/Robo (Wach)** als Basis (Army-Formel). HQ-Edikt E13 "Roboter-Wehrpflicht"
gibt einen **Sonderbonus** (alle Roboter → Wach + zusätzlich ×1,5 auf ihren Armee-Beitrag,
also effektiv ~+1,2/Robo) — so bleibt E13 attraktiv, ohne das Grundmodell zu sprengen.

### 1.7 Roboter-Arbeiter-Wert: +2 (resources) vs. "ersetzt 1 Arbeiter" (army)

`v3_resources.md`: Roboter = **+2 Arbeiter**; `v3_army.md`: Roboter ersetzt **1** Arbeiter-Bedarf.

**KANON:** Roboter = **+2 Arbeiter-Äquivalent** (resources-Wert, attraktiver fürs Skalieren).
Garage-Modus "Manufaktur/quality" kann auf +3 heben (siehe `v3_resources.md` 4.2).

### 1.8 Schalen-Ausbeute: 0,6 vs. 0,5 pro Shrimp

`v3_resources.md`: 0,6/Shrimp · `v3_almanac.md`: ~0,5/Shrimp.

**KANON:** **0,6 pro produziertem Shrimp** × Tier-Multiplikator (resources ist die
Detail-Quelle). Almanac-Text auf 0,6 angleichen.

### 1.9 SHRIMPBOOST-Fabrik braucht Shrimp ALS Input — Notiz, kein Widerspruch

Die Fabrik verbraucht **3 Shrimp/Tick**. Das konkurriert mit dem Verkauf. **Beabsichtigt**
(Balancing-Hebel, siehe `v3_resources.md` Abschnitt 3). Hier nur dokumentiert, damit die
Engine den Shrimp-Verbrauch nicht "vergisst".

### 1.10 Phasen-Namen wurden referenziert, aber nie definiert

`v3_hq.md` (1.1) und `v3_almanac.md` (B5) verweisen auf "P0–P4 aus `progression.md`" — diese
Datei existierte bis jetzt nicht. **Dieses Dokument** definiert die kanonischen Phasen
(Abschnitt 2). Damit ist der dangling Verweis aufgelöst.

> **Abdeckungs-Check (alle Spielerwünsche):** längere ziel-gebundene Quests ✔ (Abschnitt 3–7,
> 40 Stufen + HQ-Quests), neue Ressourcen/Ketten ✔ (Schalen→Boost→Roboter, Kaserne→Armee),
> Armee+Konflikte+Taktiken ✔ (10 Konflikte, `armyTarget`, Taktik-Auswahl), HQ-Edikte ✔
> (14 Edikte, 3 HQ-Stufen), Tier-/Almanach-Menüs ✔ (6 Tiers, 6 Almanach-Reiter). Alle in die
> Timeline unten eingehängt.

---

## 2. Die fünf Phasen (kanonische Definition)

Ein **Tick = 1 Tag**. Die Timeline ist in Tagen verankert (nicht Echtzeit), weil die Engine
in Tagen rechnet. Grobe Echtzeit-Schätzung in Klammern bei Standard-Spielgeschwindigkeit.

| Phase | Tage | Echtzeit ~ | Thema | Sieg-/Rang-Marke |
|---|---|---|---|---|
| **P0 — Tutorial** | 1–8 | 0–10 min | Greg & Perla führen ein; erste Becken, erste Schalen | Hinterhof-Züchter |
| **P1 — Grundbetrieb** | 8–30 | 10–40 min | Versorgungsketten stabil, erste Zonen, Schalen sichtbar | Becken-Baron (5k) |
| **P2 — Industrialisierung** | 30–60 | 40–90 min | SHRIMPBOOST-Kette, Edikte, erste Konflikte | Garnelen-Magnat (20k) |
| **P3 — Hochskalierung** | 60–110 | 1,5–3 h | Roboter, Armee, Mid-Game-Finals der Ketten | **Shrimp-Tycoon (50k)** |
| **P4 — Endgame** | 110–200+ | 3–6 h+ | Kette-Finals, Krill-Krieg, Krone von Krustanien | Imperator (750k) |

### 2.1 Gestreckte Rang-Leiter (ersetzt die alte 2-Stufen-Leiter)

`v3_hq.md` 1.1 hatte 5 Ränge bis 100k. KANON-Leiter (Endgame-tauglich):

| Rang | ab Vermögen | Phase |
|---|---|---|
| Hinterhof-Züchter | 0 | P0 |
| Becken-Baron | 5.000 | P1 |
| Garnelen-Magnat | 20.000 | P2 |
| **Shrimp-Tycoon** | **50.000** | P3 (alter Sieg, jetzt Zwischenmarke) |
| Krustentier-Industrieller | 120.000 | P3/P4 |
| Hallen-Hauptstadt-König | 250.000 | P4 |
| Imperator von Krustanien | 500.000–750.000 | P4-Finale |

**Warum das streckt:** Die alten 50k waren in ~30 min erreichbar. Die neue Leiter verlangt das
**15-fache** an der Spitze — und Geld allein reicht nicht (Endgame-Gates brauchen Armee, Tiers,
Roboter, siehe Kette J).

---

## 3. PHASE-TIMELINE: Was schaltet WANN frei (Master-Tabelle)

Diese Tabelle ist der eigentliche rote Faden: jede Zeile = ein Freischalt-Ereignis, sortiert
nach typischem Tag. "Gate" = Bedingung, die es auslöst. Quest-IDs verweisen auf `v3_quests.md`.

| Tag (ca.) | Phase | Ereignis / Freischaltung | Gate (Objective) | Quelle |
|---|---|---|---|---|
| 1 | P0 | Spielstart: 1 Becken, HQ, 8 Arbeiter, Schalen fallen passiv an | — | resources 1.1 |
| 3 | P0 | Tutorial: erstes Forschungslabor → Tier `BIO`, Forschungspunkte (opt.) | tutorial | almanac, resources 1.5 |
| 5 | P0 | **Schälerei** baubar (Einstieg Schalen-Kette) | von Anfang an | resources 4.4 |
| 5 | P0/P1 | **Kette F** startet: `f1_schalenberg` | 5 Becken | quests F1 |
| 8 | P1 | `edict_greg_board` verfügbar (Greg in Vorstand, nachdem Greg eingeführt) | Tutorial fertig | hq E12 |
| 10 | P1 | Almanach-Reiter B1/B2/B3 (Vermögen/Ressourcen/Tiers) nutzbar | — | almanac B |
| 12 | P1 | **Kette A** startet: `a1_bestand` (Behörde) | 6 Becken / Tag 12 | quests A1 |
| 12 | P1 | `q.shells_intro` / F1-Ziel: 200–1.000 Schalen halten | HOLD(SHELLS) | resources 6, quests F1 |
| 15 | P1 | **HQ-1 "Verwaltungsetage"** baubar (+1 Edikt-Slot, −15% Upkeep) | 6.000 G + Tag 15 | hq HQ-1 |
| 16 | P1/P2 | Tier `GOURMET` (Quest "Becken-Gourmet" + Labor-Upgrade) | tier.GOURMET | almanac Tier 3 |
| 18 | P2 | **Kette B** startet: `b1_wohlfuehl` (Lena) | 8 Becken | quests B1 |
| 18 | P2 | `f2_muehle` → **Schalen-Mühle + SHRIMPBOOST-Fabrik** freigeschaltet | BUILD/chain F1+3d | quests F2 |
| 20 | P2 | **SHRIMPBOOST-Fabrik** baubar; **Energydrink-Stand** + Boost-Politik | zone.FORSCHUNG + build.shrimpboost | resources 4.1/4.6 |
| 20 | P2 | **Kette G** startet: `g1_erste_dose` | SHRIMPBOOST-Fabrik gebaut | quests G1 |
| 22 | P2 | `edict_boost_mandate` (SHRIMPBOOST-Pflicht) verfügbar | 1 Boost-Fabrik | hq E03 |
| 22 | P2 | v3-Events e02/e08 (Boost-Events) feuern ab Boost ≥ 25/40 | getBoost() | events |
| 25 | P2 | Tier `PROTEIN` (Mast-Becken) → Militär-Depot-Absatz | tier.PROTEIN | almanac Tier 4 |
| 25 | P2 | **Kette C** startet: `c1_zweiter_fruehling` (Influencer) | Rep 60 / 25.000 G | quests C1 |
| 30 | P2/P3 | **Kette D** startet: `d1_preiskrieg` (Chad) | 30.000 G | quests D1 |
| 30 | P2 | **Konflikt-System scharf**: `conf_grenz` möglich | Rivalität ≥31 + Kaserne + Tag 30 | army Konflikt 1 |
| 32 | P3 | Tier `GENTECH` (Genlabor) → Export + Schwarzmarkt | tier.GENTECH | almanac Tier 5 |
| 35 | P3 | **Garnelen-Roboter-Werk + Garage** freigeschaltet | `iron_shrimp`/E3/Forschung | resources 4.2/4.5, quests E3 |
| 35 | P3 | **Kette H** startet: `h1_prototyp` (Roboter) | Roboter-Werk + 1.000 Schalen | quests H1 |
| 35 | P3 | **Kette E** startet (ab E1 früher): `e1_charme` ab 50.000 G | 50.000 G | quests E1 |
| 38 | P3 | Tier `WARKRILL` (Kampf-Aufzucht, nach Designer-Stufe) | tier.WARKRILL | almanac Tier 6 |
| 40 | P3 | **Krill-Kaserne** freigeschaltet (Armee-Stärke beginnt) | zone.LOGISTIK + tier.WARKRILL + barracks | resources 4.3 |
| 40 | P3 | **Kette I** startet: `i1_stehende_armee` | WARKRILL + Kaserne | quests I1 |
| 40 | P3 | Almanach-Reiter **B6 (Armee & Konflikte)** wird sichtbar | Kaserne gebaut | almanac B6 |
| 45 | P3 | **Chitin-Reaktor** (Schalen→Strom) via `f3_chitin` | COLLECT 6.000 Schalen | quests F3 |
| 45 | P3 | `edict_martial_law` / `edict_robot_draft` / `edict_shell_deposit` verfügbar | Kaserne/Roboter/Boost | hq E05/E13/E14 |
| 50 | P3 | Konflikte 2–5 (Embargo, Razzia, Sabotage-Flotte, Blockade) eskalieren | Rivalität 35→50 | army K2–K5 |
| 60 | P3/P4 | **HQ-2 "Lagezentrum"** (+1 Slot, −50% Event-Schaden, Anti-Akwanov) | 18.000 G + HQ-1 + Zone D | hq HQ-2 |
| 70 | P4 | Mid-Game-Finals: B4/C4/D4 erreichbar | Rep 80 / 80k / Rivalität 75 | quests |
| 80 | P4 | Konflikte 6–9 (Stromnetz, Söldner, Belagerung, Verrat) | Rivalität 55→65 | army K6–K9 |
| 90 | P4 | **HQ-3 "PR-Abteilung"** (Rep-Boden 30, +8% Preis) | 40.000 G + HQ-2 + Rep 70 | hq HQ-3 |
| 90 | P4 | `a5_hauptstadt` (Hallen-Hauptstadt) | 150.000 G + Tag 90 | quests A5 |
| 100 | P4 | **Kette E-Finale** `e5_seidenstrasse`, **I-Finale** `i4`/`i5` | Rivalität 70 + 150k / Armee 1.000+ | quests |
| 110 | P4 | **KONFLIKT 10 "Der finale Krill-Krieg"** | Rivalität 66 + 80k + Armee 200 + Krillkill 7 | army K10 |
| 120 | P4 | **Kette J** startet: `j1_vollsortiment` (oder via A5-Option 2) | 200.000 G + alle 6 Tiers | quests J1 |
| 130 | P4 | Tier **`IMPERIAL` (Krill-Diamant)** freigeschaltet | `j1` Option 1 | quests J2 |
| 150+ | P4 | `j3_imperator` Endgame-Gate: 750.000 G + ein Charakter-Finale | MONEY_REACH 750k | quests J3 |

---

## 4. PHASE-DETAIL: Was der Spieler in jeder Phase TUT (roter Faden)

### 4.1 P0 — Tutorial (Tag 1–8): "Die ersten Schalen"
- **Loop:** Strom→Wasser+Futter→Becken→Verkauf an Börse/Restaurant. Greg & Perla erklären.
- **Neu sichtbar:** Schalen sammeln sich passiv (0,6/Shrimp) — der "Köder" (resources 1.1).
- **Erste Quest-Trigger:** keine harten Ziele, nur Onboarding. Kein Druck.
- **Falle vermeiden:** Schalen haben noch keinen Abnehmer → der Spieler fragt "wofür?".
- **Greg:** "Du sammelst Müll. Vertrau mir, das wird das Klügste, was du je getan hast."

### 4.2 P1 — Grundbetrieb (Tag 8–30): "Bürokratie & Bio"
- **Loop:** mehr Becken, erste Zonen (Forschung), Tier BIO/GOURMET, Reputation aufbauen.
- **Quests:** A1 (Behörde, 1.000 STANDARD produzieren), B1 (800 BIO), F1 (1.000 Schalen).
- **Erstes HQ-Upgrade:** Verwaltungsetage (Tag 15) → 3 Edikt-Slots.
- **Edikte:** Greg-Vorstand (E12, gratis), evtl. Work-Life (E04) oder Dumping (E09) früh.
- **Schwellen sind klein** (Hunderter) → in Minuten/wenigen Tagen erfüllbar, lehren je 1 Mechanik.

### 4.3 P2 — Industrialisierung (Tag 30–60): "Die zweite Wirtschaftsstufe"
- **Loop-Erweiterung:** Schalen → **SHRIMPBOOST-Fabrik** → Energydrink (verkaufen ODER Boost-Politik).
- **Quests:** F2 (Mühle/Boost-Fabrik bauen), G1/G2 (500→3.000 Boost), C1/C2, D1, B2/B3.
- **Konflikt-System scharf:** Konflikt 1 (Grenzzwischenfall, threat 30) als Tutorial des Taktik-Popups.
- **Strom-Wand #1:** Boost-Fabrik (6 Strom) + zweite Becken-Welle → Kraftwerk-Imperium muss wachsen.
- **Edikte werden zentral:** Boost-Pflicht (E03) vs. Work-Life (E04); Handels-Edikte E08/E09.
- **Ziele wachsen auf Tausender** → erste echte "viele Tage"-Quests.

### 4.4 P3 — Hochskalierung (Tag 60–110): "Roboter & Armee"
- **Loop-Erweiterung:** Schalen+Boost → **Roboter-Werk** (0,25 Robo/Tick, BEWUSST langsam) →
  Roboter als Arbeiter ODER Armee (Garage-Politik).
- **Militär-Achse startet:** WARKRILL-Becken → **Krill-Kaserne** → `armyTarget` baut sich auf.
- **Quests:** H1–H3 (25→300 Roboter), I1–I3 (Armee 200→700), E3 (150 Roboter Wettrüsten),
  F3 (6.000 Schalen, Chitin-Reaktor), Mid-Game-Finals B4/C4/D4.
- **Konflikte 2–5** (threat 55→120): Embargo, Razzia, Sabotage-Flotte, Blockade.
- **Strom-Wand #2:** Roboter-Werk (12 Strom) + Garage (4+5/Robo) → Chitin-Reaktor entlastet (F3).
- **Shrimp-Tycoon (50k)** wird hier erreicht — früher das Spielende, jetzt Halbzeit.

### 4.5 P4 — Endgame (Tag 110–200+): "Krieg, Krone, Imperium"
- **Loop:** vollautomatisierte Linien (mehrere Boost-Fabriken, Roboter-Belegschaft, mehrere Kasernen).
- **Quests:** Ketten-Finals E5/I4/I5, H4 (750 Roboter), G4 (30.000 Boost verkauft),
  F4 (20.000 Schalen), A5 (200k), D4 (300k), dann **Kette J** (Krone der Krustentiere).
- **Konflikte 6–10:** Stromnetz, Söldner-Chad, Belagerung, Verrat → **KONFLIKT 10 Krill-Krieg**
  (threat 400+, dreigeteiltes Ende A/B/C).
- **Tier IMPERIAL (Krill-Diamant)** als finale Veredelung (J2).
- **Endgame-Gate:** `j3_imperator` = 750.000 G + (Krillkill- ODER Akwanov-Finale).
- **Nach dem Sieg:** freier Imperator-/Sandbox-Endlosmodus (J3 Optionen).

---

## 5. SCHWELLEN-STAFFELUNG (warum es Stunden statt 30 Minuten dauert)

Kern-Prinzip aller Quell-Dokumente: Schwellen wachsen **grob um Faktor 5–10 pro Stufe**, und
weil die Ketten pro Tick nur **kleine Mengen** liefern (Roboter 0,25/Tick, Boost 2/Tick je Fabrik,
Armee zäh über `armyTarget`-Annäherung), lassen sich Ziele NICHT abkürzen.

### 5.1 Geld-Achse
`2.000 → 10.000 → 50.000 (Tycoon) → 120.000 → 200.000 → 300.000 → 500.000 → 750.000 (Imperator)`

### 5.2 Ressourcen-Achsen (kanonische Endgame-Schwellen)
| Ressource | Früh (P1/P2) | Mitte (P3) | Spät/Endgame (P4) |
|---|---|---|---|
| Schalen (HOLD/COLLECT) | 200 / 1.000 | 2.000 / 6.000 | 20.000 |
| SHRIMPBOOST (COLLECT/SELL) | 100 / 500 | 2.000 / 3.000 / 5.000 | 12.000 / 30.000 |
| Roboter (HOLD) | 15 / 25 | 150 / 300 | 750 |
| Armee-Stärke (ARMY_REACH) | 50 | 200 / 400 / 700 | 1.000 / 1.500 |
| Tier-Produktion (PRODUCE_TIER) | 800–1.000 | 2.000–3.000 | 5.000+ / 1.000 Imperial |

### 5.3 Tag-Achse (Mindest-Spieldauer als Gate)
`Tag 12 → 15 → 30 → 60 → 90 → 120` — verhindert, dass reine Geld-Spikes das Endgame
überspringen (z.B. A5 verlangt Tag 90 UND 150k, J1 verlangt alle 6 Tiers).

> **Engine-Hinweis:** Ein einzelner Geld-Spike (z.B. großer Schwarzmarkt-Verkauf) darf NIE ein
> Mehrstunden-Gate auslösen. Endgame-Gates sind deshalb **UND-verknüpft** (Geld UND Tag UND
> Ressource/Armee), siehe A5, conf_krillkrieg, j3.

---

## 6. PARALLELITÄT & STORY-SCHEDULER (damit niemand stundenlang wartet)

Aus `v3_quests.md` (Anhang) übernommen und als Kanon fixiert:

- **2–3 aktive Ziel-Quests gleichzeitig** erlaubt, ABER nur **EIN** dialoglastiger
  Charakter-Beat zur selben Zeit (Anti-Textwand).
- Parallele Ketten dürfen laufen, **solange ihre Objectives unterschiedliche Ressourcen
  betreffen** — z.B. F (Schalen) + G (SHRIMPBOOST) + I (Armee) gleichzeitig. Drei Ketten, die
  alle "halte X Schalen" verlangen, würden den Spieler blockieren → Scheduler verhindert das.
- **Reputations-Gouverneur:** nie zwei stark rep-negative Optionen erzwingen; Strafen bleiben
  wählbar (Spieler entscheidet Risiko).
- **Konflikt-Gate-Regel:** Konflikte triggern nur, wenn BEIDE Achsen bespielt sind (Krillkill
  liefert Armee, Akwanov liefert Bedrohung) — `v3_army.md` 3.1. Sonst friedliche Armee ohne
  Gegner oder Bedrohung ohne Verteidigung (= unfair). Engine zwingt sanft zu beidem.

### 6.1 Empfohlene Ketten-Verzahnung (Abhängigkeitsgraph, kanonisch)
```
F2 ──► SHRIMPBOOST-Fabrik + Schalen-Mühle ──► Voraussetzung für C2, G1
F3 ──► Chitin-Reaktor (Strom-Rabatt) ──► entlastet ALLE stromhungrigen v3-Gebäude
E3 / Forschung ──► Garnelen-Roboter-Werk ──► Voraussetzung für H1
Krillkill-Mid-Game (v2) ──► WARKRILL + Krill-Kaserne ──► Voraussetzung für I1
A5-Option 2 ──► startet J1 (alternativ Auto-Trigger bei 200k + alle Tiers)
J1 ──► Krill-Diamant (IMPERIAL) ──► Voraussetzung für J2 ──► J3 (Endgame)
```

---

## 7. ENDGAME-KONVERGENZ: die drei Sieg-Pfade (roter Faden des Finales)

Das Spiel hat **mehrere, sich überschneidende Sieg-Bedingungen** — kein einzelner Klick beendet es.
Alle laufen über P4 und verlangen unterschiedliche Schwerpunkte:

| Sieg-Pfad | Auslöser | Schwerpunkt | Quelle |
|---|---|---|---|
| **Militär-Sieg** (Krill-Krieg Ende B) | conf_krillkrieg gewonnen, Rivalität 66+, Armee siegt | Armee, WARKRILL, Kaserne | army K10 |
| **Allianz** (Krill-Krieg Ende A) | Diplomatie-Verlauf, niedrige Rivalität | Geld, Reputation, Diplomatie | army K10 |
| **Aufkauf** (Krill-Krieg Ende C) | 120k+ Geld, Doppelagent/Chad-Allianz | Geld, Spionage | army K10 |
| **Wirtschafts-Imperator** | `j3_imperator` 750.000 G + ein Charakter-Finale | Vollsortiment, alle Tiers, Geld | quests J3 |
| **Vertikal integriert** (Sieg-Variante) | 1.000 Schalen + 200 Boost + 40 Roboter halten | volle v3-Kette | resources q.vertical_empire |
| **Garnelen-Staat** (Tierschutz-Ende) | 8.000 lebende Shrimps, Rep 80, shrimp_staat | Reputation, Masse | quests B4 |

**Kanon-Regel:** `j3_imperator` ist das **Haupt-Endgame-Gate**, das ein Charakter-Finale
(Krillkill ODER Akwanov) voraussetzt — so führen die Konflikt-Enden in den Imperator-Sieg.
Die anderen Pfade sind **alternative Triumphe** (Achievements + Buffs), nicht das harte Spielende.
Nach dem Sieg: **Sandbox-Endlosmodus** (J3 Option 2).

---

## 8. TON-KONSISTENZ-CHECK (Kritiker-Befund)

Durchgehend stark und konsistent:
- **Krillkill** (CAPS, "Soldaten/Rekrut", "die Suppe", militärische Metaphern, die immer in
  Suppe enden) — einheitlich in quests/army/events/hq. ✔
- **Akwanov** (ölig, "Wer braucht Ozeane? Wir haben HALLEN!", doppelt eingeschlossenes Binnenland,
  Tee, "Trockenhafen") — konsistent. ✔
- **Greg** (Wasserglas, trockene Kommentare als roter Faden durch HQ/Almanach/Quests). ✔
- **Mira, Olaf, Lena, Quallmann, Chad, Perla** — Stimmen konsistent getroffen. ✔
- **Brian-Running-Gag** ("Brian war Suppe / jetzt Symbol / ein Hauch von Brian in der Dose") —
  zieht sich elegant durch resources/quests/hq. ✔

**Kleine Tonbruch-Warnungen (kein Blocker):**
1. `v3_hq.md` verwendet stellenweise KEINE echten Umlaute (`Buero`, `staerke`, `Aussenminister`)
   — sollte auf echte ä/ö/ü/ß vereinheitlicht werden (Stil-Vorgabe). **Empfehlung:** HQ-Doc
   in einem Pass auf Umlaute ziehen.
2. Sonst keine Emojis, kurze knackige Popups — Stilvorgabe erfüllt. ✔

---

## 9. ENGINE-TO-DOS (konsolidiert über alle v3-Docs, priorisiert nach Phase)

**P0/P1-Voraussetzung (zuerst bauen):**
1. `ResourceType`: `SHELLS, SHRIMPBOOST, ROBOTS, ARMY` (+ optional `RESEARCH`) — KANON-IDs (1.1).
2. Becken-Schalen-Nebenprodukt im Tick (0,6 × Shrimp × Tier-Mult) — (1.8).
3. `GameState`: Bestände + kumulative `Produced/Sold`-Zähler je Ressource; `getArmyStrength()`.
4. `Condition.Type` um KANON-Objective-Typen (1.2) erweitern + in `test()` verdrahten.

**P2 (Industrie):**
5. `BuildingType`: SHRIMPBOOST-Fabrik, Schälerei, Schalen-Mühle, Energydrink-Stand + I/O-Felder
   (`shellsProduce/Use, boostProduce/Use`). Modi/Upgrades aus resources Abschnitt 4.
6. HQ-Edikt-System (`activeEdicts`, `edictSlots`, `mutexGroup`, Cooldown) + E01–E14.
7. Konflikt-Event-Kategorie `CONFLICT` + Taktik-Popup (Konflikt 1 als Erstes).

**P3 (Skalierung):**
8. Roboter-Werk + Garage + `robotPolicy`; Roboter = +2 Arbeiter (1.7) / +0,8 Armee-Wach (1.6).
9. Krill-Kaserne + `armyTarget`-Modell aus `v3_army.md` (KANON, 1.5): Zuflüsse, Annäherung, Zerfall,
   `krillkillMult`-Stufen.
10. Chitin-Reaktor (Schalen→Strom); Konflikte 2–5.
11. HQ-1/HQ-2 Ausbau; Almanach-Reiter B1–B6; Tier-Menü ("Garnelen-Akte").

**P4 (Endgame):**
12. Konflikte 6–10 + dreigeteiltes Ende (A/B/C) + Achievements.
13. Tier `IMPERIAL` (Krill-Diamant); Kette J; `j3_imperator` als Endgame-Gate + Sandbox-Modus.
14. HQ-3 (PR-Abteilung, Rep-Boden 30); Rang-Leiter (2.1) verdrahten.

---

> "Vier Phasen, zehn Ketten, zehn Konflikte, vierzehn Edikte — und am Ende, Rekrut, hält
> jemand eine Krone aus Krustentieren in der Hand und fragt sich, wo die letzten sechs Stunden
> geblieben sind. DAS, mein Freund, ist Tycoon." — General Krillkill, Schlussappell
>
> "Es gibt kein Ende. Es gibt nur die nächste Phase. Und danach die nächste Dose." — Greg, aus dem Wasserglas
