# ShrimpTopia v3 — Neue Ressourcen & Produktionsketten

> "Eine Garnele, die nur Garnele bleibt, hat das Memo nicht gelesen. Wir machen aus ihr
> einen Energydrink, einen Roboter und notfalls einen Soldaten." — HQ-Handbuch, Kapitel 7,
> Untertitel: "Vertikale Integration für Krustentiere"

Dieses Dokument erweitert die bestehende Kette (`Strom -> Wasser + Futter -> Becken -> Markt -> Geld`)
um eine **zweite Wirtschaftsstufe**: aus dem Nebenprodukt der Becken (Schalen) wird ein
Energydrink (SHRIMPBOOST), daraus Roboter, daraus Arbeiter und Armee. Ziel: das Spiel von
~30 Minuten auf **mehrere Stunden** zu strecken, indem hinter den Shrimps eine ganze
Industriekette mit gestaffelten, großen Schwellen liegt.

**Bezugsgrößen aus dem bestehenden Code (damit die Zahlen passen):**
- Becken (`SHRIMP_TANK`): kostet **500**, macht **+5 Shrimp/Tag**, braucht 10 Wasser, 4 Futter, 2 Strom, 2 Arbeiter.
- STANDARD-Shrimp Basiswert **18**, GOURMET **55**, PROTEIN **85**, KAMPF-KRILL **210** Geld/Stück.
- Kraftwerk **+45 Strom** (Kosten 600), Algenfarm **+9 Futter** (450), Wasserwerk **+14 Wasser** (400).
- HQ stellt 8 Arbeiter; Wohnheim **+6 Arbeiter** (300).
- Konvention der Felder: `*Produce` = erzeugt/Tick, `*Use` = verbraucht/Tick, `upkeep` = Geld/Tick.
- Unlock-Flags folgen dem bestehenden Schema: `tier.X`, `zone.X`, `build.X`, `worker.X`.

---

## 1. Neue Ressourcen (Lagerbestände)

Alle vier neuen Ressourcen sind **Lagerbestände** (Stock), keine reinen Durchsatz-Werte:
sie sammeln sich an, können gehortet, verbraucht oder verkauft werden. Das ist die
mechanische Voraussetzung für die v3-Quest-Ziele "sammle/halte N einer Ressource".

### 1.1 Schalen (`SHELLS`) — "Der Müll, der Geld druckt"

| Feld | Wert |
|---|---|
| **id / enum** | `ResourceType.SHELLS` |
| **Anzeigename** | "Schalen" |
| **Icon-Idee** | `IconKind.SHELL` (gebogenes Halbmond-Oval, sandfarben) |
| **Farbe** | warmes Beige/Sandbraun `Color(214, 192, 150)` |

**Beschreibung / Gag:** Jede Garnele häutet sich, jault kurz, und hinterlässt eine Schale.
Früher Abfall ("Brian war Suppe, seine Schale ist jetzt Industrie"), jetzt der Rohstoff,
der die ganze zweite Wirtschaftsstufe trägt. Riecht nach Strand und Kapitalismus.

**Wie sie entsteht (Nebenprodukt, passiv):**
- **Jedes Becken** wirft pro Tick **0,6 Schalen je produziertem Shrimp** ab
  → ein Standard-Becken (+5 Shrimp/Tag) liefert nebenbei **+3 Schalen/Tag**, ganz ohne Zusatzkosten.
- Höhere Tiers häuten sich seltener, aber wertvoller: Multiplikator auf die Schalen-Ausbeute
  pro Becken-Tier: STANDARD/BIO **1,0**, GOURMET **1,1**, PROTEIN **1,3**, GENTECH **1,4**, KAMPF-KRILL **1,6**.
- **Schälerei** (siehe Gebäude 4.4) verdoppelt/triagiert das gezielt.

**Wofür gebraucht:**
- **Input** für SHRIMPBOOST-Fabrik, Roboter-Werk und (optional) als Brennstoff fürs Kraftwerk.
- **Verkauf:** an die Schälerei/Recycling zu **niedrigem Wert ~2 Geld/Stück** (Müll bleibt Müll).
- **Quests:** "Halte N Schalen", "Presse N Schalen" — frühe, leicht erreichbare Sammelziele.

**Freischaltung:** **Von Anfang an** als Nebenprodukt sichtbar (Becken erzeugt sie sofort).
Aktiv nutzbar wird sie mit der **ersten Schälerei oder SHRIMPBOOST-Fabrik**. So sieht der
Spieler den Bestand früh wachsen und fragt sich "wofür?" — der klassische Tycoon-Köder.

---

### 1.2 SHRIMPBOOST (`SHRIMPBOOST`) — "Energydrink mit Beinen"

| Feld | Wert |
|---|---|
| **id / enum** | `ResourceType.SHRIMPBOOST` |
| **Anzeigename** | "SHRIMPBOOST" |
| **Icon-Idee** | `IconKind.CAN` (Getränkedose mit Blitz, neon-cyan) |
| **Farbe** | grell Neon-Cyan/Türkis `Color(40, 224, 220)` |

**Beschreibung / Gag:** Ein in Schalen-Pulver und reine Garnelen-Essenz aufgelöster
Energydrink, der "100 % natürlich, 0 % FDA-genehmigt" ist. General Krillkill trinkt
ihn literweise und nennt ihn "Mut in Dosen". Olaf hat nach drei Dosen einen Roboterarm
aus Schrott in 20 Minuten gebaut, daher der zweite Verwendungszweck.

**Wie er entsteht (verarbeitend):**
- **SHRIMPBOOST-Fabrik** (Gebäude 4.1): verbraucht **3 Shrimp + 4 Schalen + 6 Strom**
  → **+2 SHRIMPBOOST/Tag**. Garnelen werden also wortwörtlich zu Energie verarbeitet (dunkel, aber lukrativ).

**Wofür gebraucht (Doppelnutzen):**
- **Verkauf (hoher Wert):** am **Energydrink-Stand / Export** zu **~90 Geld/Dose** — eine
  Dose ist mehr wert als 5 Standard-Shrimps. Der erste große Geldsprung der Spätphase.
- **Effizienz-Boost:** als Input für Roboter-Werk und Krill-Kaserne; außerdem optionaler
  **Farm-Boost-Verbrauch** (siehe Boost-Politik 5.2): "1 Dose/Tick füttert die Belegschaft"
  → **+15 % Becken-Output farmweit**, solange Vorrat reicht.
- **Quests:** "Produziere insgesamt N SHRIMPBOOST", "Halte N auf Lager", "Verkaufe N".

**Freischaltung:** Bau der **SHRIMPBOOST-Fabrik**, die ihrerseits **Forschungsflügel
(`zone.FORSCHUNG`) + Flag `build.shrimpboost`** braucht (Quest-Belohnung, siehe 6).

---

### 1.3 Roboter (`ROBOTS`) — "Garnelenbetriebene Belegschaft"

| Feld | Wert |
|---|---|
| **id / enum** | `ResourceType.ROBOTS` |
| **Anzeigename** | "Roboter" |
| **Icon-Idee** | `IconKind.ROBOT` (eckiger Kopf mit Antenne + Garnelen-Visor) |
| **Farbe** | metallisch Stahlblau `Color(150, 168, 196)` |

**Beschreibung / Gag:** Garnelenbetriebene Roboter — angetrieben von einer Dose SHRIMPBOOST
im Tank und einer echten Garnele am Steuerknüppel im Cockpit ("Sie denkt, sie fährt Bagger.
Lasst sie."). Arbeiten 24/7, beschweren sich nie bei Gewerkschaft "Klausi", und können
im Notfall eine Halle verteidigen.

**Wie sie entstehen (verarbeitend, langsam):**
- **Garnelen-Roboter-Werk** (Gebäude 4.2): verbraucht **5 Schalen + 1 SHRIMPBOOST + 12 Strom**
  → **+1 Roboter alle 4 Tage** (0,25 Roboter/Tick). Bewusst langsam: Roboter sind teuer und mächtig.

**Wofür gebraucht (zählen als Arbeiter UND/ODER Armee):**
- **Arbeitskraft:** jeder Roboter zählt als **+2 Arbeiter** (kein Wohnheim, keine Lohnkosten —
  nur Strom-Upkeep über die Roboter-Garage). Skaliert die Produktion ohne Bevölkerungslimit.
- **Armee:** über die **Roboter-Politik** (5.3) lassen sich Roboter in den Verteidigungs-Modus
  schalten: dann zählt jeder als **+3 Armee-Stärke** statt als Arbeiter (Entweder/Oder, pro Politik umschaltbar).
- **Quests:** "Baue N Roboter", "Halte N Roboter", später Armee-Quests.

**Freischaltung:** Bau des **Roboter-Werks**, gegated über **Flag `build.robotworks`**
(braucht zuvor laufende SHRIMPBOOST-Produktion + eine Quest, siehe 6).

---

### 1.4 Armee-Stärke (`ARMY`) — "Krillkills Lieblingszahl"

| Feld | Wert |
|---|---|
| **id / enum** | `ResourceType.ARMY` (Stat/Score, kein verkäuflicher Bestand) |
| **Anzeigename** | "Armee-Stärke" |
| **Icon-Idee** | `IconKind.SHIELD` (Schild mit gekreuzten Garnelen) |
| **Farbe** | Militär-Oliv `Color(120, 130, 80)` |

**Beschreibung / Gag:** Eine abstrakte Verteidigungs- und Drohkulissen-Zahl. General
Krillkill brüllt sie morgens beim Appell ("SOLDATEN! Heute sind wir 240 stark!"). Schützt
gegen Akwanovs Sabotage-Events und ist Pflicht-Ziel mehrerer Endgame-Quests.

**Wie sie entsteht (kein Lager, sondern Summe aus Quellen):**
- **Krill-Kaserne** (Gebäude 4.3): verbraucht **2 KAMPF-KRILL + 1 SHRIMPBOOST + 8 Strom**
  → **+4 Armee-Stärke/Tag** (akkumuliert dauerhaft, solange Inputs fließen).
- **Roboter im Verteidigungs-Modus** (5.3): **+3 Armee** je Roboter (sofort, kein Verbrauch).
- Verfällt langsam (-1/Tag), wenn keine Kaserne aktiv ist ("ohne Drill rosten Soldaten ein").

**Wofür gebraucht:**
- **Schutz:** Armee-Stärke >= X reduziert/blockt Akwanov-Sabotage- und Embargo-Events
  (z. B. ab 100 Armee: -50 % Schadenswahrscheinlichkeit).
- **Quests:** "Armee-Stärke >= X" ist ein eigener Zieltyp (Endgame, große Schwellen).
- **Reputation-Trade-off:** hohe Armee macht Tierschützerin Lena und Dr. Quallmann nervös
  (kleiner passiver Rep-Malus ab 150 Armee — "Warum hat eine Garnelenfarm eine Armee?").

**Freischaltung:** Bau der **Krill-Kaserne**, gegated über **`zone.LOGISTIK` + Flag
`build.barracks`** (Krillkill-Strang, Mittel-/Spätphase).

---

### 1.5 Optional: Forschungspunkte (`RESEARCH`) — "Streck-Mechanik für die Spielzeit"

| Feld | Wert |
|---|---|
| **id / enum** | `ResourceType.RESEARCH` (optionaler Vorschlag) |
| **Anzeigename** | "Forschung" |
| **Icon-Idee** | `IconKind.FLASK` (Erlenmeyerkolben mit Garnele drin) |
| **Farbe** | violett `Color(170, 120, 210)` |

**Beschreibung / Gag:** Akkumulierte "Aha!"-Momente von Dr. Perla Pereira und ihrer
Wasserglas-Garnele Greg. Greg starrt, Perla notiert, ein Punkt entsteht.

**Wie:** Forschungslabor/Genlabor erzeugen passiv **+1 bzw. +2 Forschung/Tag**, optional
beschleunigt durch SHRIMPBOOST-Verbrauch (+50 %).

**Wofür:** Kauf-Währung für die **teuren farmweiten Upgrades** und das Freischalten der
GENTECH/KAMPF-KRILL-Tiers — entkoppelt High-End-Freischaltungen vom reinen Geld und
**streckt die Progression** (Geld allein reicht nicht mehr). Quests: "Halte/sammle N Forschung".

**Freischaltung:** mit dem ersten Forschungslabor (passt in die bestehende Phase P1).

> Empfehlung: Schalen + SHRIMPBOOST + Roboter + Armee sind das **Pflicht-Paket**.
> Forschungspunkte sind ein **starker Spielzeit-Strecker**, aber optional, falls die
> Engine schlank bleiben soll.

---

## 2. Die erweiterte Produktionskette (Übersicht)

```
                                  ┌─────────── Verkauf: Energydrink-Stand (~90 G/Dose)
                                  │
 Strom ┐                          │              ┌── Boost-Politik: +15% Becken-Output farmweit
       │                          ▼              │
 Wasser┼─► Becken ─► Shrimps ─► SHRIMPBOOST-Fabrik ─► SHRIMPBOOST ──┤
       │     │         │   ▲                                        │
 Futter┘     │         │   │                                        ▼
             │         │   └────────────────┐         Garnelen-Roboter-Werk ─► ROBOTER
             ▼         │                     │              ▲                     │
          SCHALEN ◄────┘ (Nebenprodukt)      │              │                     ├─► +2 Arbeiter (Garage)
             │                               │              │                     └─► +3 Armee (Verteidigung)
             ├─► Schälerei (x2 Schalen, Verkauf ~2 G)       │
             ├─► Kraftwerk-Brennstoff (Schalen -> +Strom)    │
             └─► Input Roboter-Werk ──────────────────────────┘

 KAMPF-KRILL (Becken) ─┐
                       ▼
 SHRIMPBOOST ─────► Krill-Kaserne ─► ARMEE-STÄRKE ─► Schutz vor Akwanov-Sabotage + Endgame-Quests
```

### 2.1 Ketten-Tabelle (Inputs → Outputs, pro Tick/Tag)

| Stufe | Gebäude | Inputs / Tick | Output / Tick | Sekundär-Output |
|---|---|---|---|---|
| 0 | Kraftwerk/Solar | — / Schalen (opt.) | +45 / +28 Strom | — |
| 0 | Wasserwerk/Hub | Strom | +14 / +40 Wasser | — |
| 0 | Algenfarm | Wasser, Strom | +9 Futter | — |
| 1 | **Becken** | 10 Wasser, 4 Futter, 2 Strom | **+5 Shrimp** | **+3 Schalen** (Nebenprodukt) |
| 2 | **Schälerei** | 6 Schalen (roh), 3 Strom | +12 Schalen (aufbereitet) | Verkauf 2 G/Stück |
| 2 | **SHRIMPBOOST-Fabrik** | 3 Shrimp, 4 Schalen, 6 Strom | **+2 SHRIMPBOOST** | — |
| 3 | **Energydrink-Stand** | 5 SHRIMPBOOST (Lager) | Verkauf | **~90 G/Dose** |
| 3 | **Garnelen-Roboter-Werk** | 5 Schalen, 1 SHRIMPBOOST, 12 Strom | **+0,25 Roboter** | — |
| 4 | **Roboter-Garage** | 5 Strom je Roboter | hält Roboter aktiv | +2 Arbeiter **oder** +3 Armee je Roboter |
| 4 | **Krill-Kaserne** | 2 KAMPF-KRILL, 1 SHRIMPBOOST, 8 Strom | **+4 Armee/Tag** | Rep-Malus ab Schwelle |

---

## 3. Wirtschaftslogik & Warum es die Spielzeit streckt

1. **Verschachtelte Abhängigkeiten.** Roboter brauchen SHRIMPBOOST **und** Schalen **und**
   viel Strom. SHRIMPBOOST verbraucht Shrimps, die man eigentlich verkaufen wollte. Jede
   neue Stufe konkurriert um Ressourcen der vorherigen → der Spieler muss **ausbalancieren
   statt nur skalieren**, was natürlich Zeit kostet.
2. **Strom-Wand.** Stufe-2/3-Gebäude sind extrem stromhungrig (6–12/Tick). Vor jeder Expansion
   muss erst das Kraftwerk-Imperium wachsen → ein zweiter, paralleler Bau-Loop.
3. **Langsame Roboter.** 0,25 Roboter/Tick bedeutet: eine ernsthafte Roboter-Belegschaft
   (z. B. 40 Stück = +80 Arbeiter) dauert **viele Spieltage** und kostet laufend Inputs.
4. **Gestaffelte, große Quest-Schwellen** (siehe 6): "produziere 5.000 SHRIMPBOOST",
   "baue 50 Roboter", "Armee-Stärke 1.000" — bewusst weit über dem alten 30-Minuten-Horizont.
5. **Forschungspunkte (optional)** entkoppeln Top-Tier-Freischaltung von Geld → man kann sich
   den Sieg nicht "erkaufen", sondern muss die Kette **betreiben**.

---

## 4. Neue Gebäude (Details)

> Alle Werte im Format des bestehenden `BuildingType`-Enums. Neue I/O-Felder
> (`shellsProduce/Use`, `boostProduce/Use`, `robotProduce/Use`, `armyProduce`) müssen
> in `BuildingType`/`Stats`/`ResourceType` ergänzt werden — die Engine erweitert das.

### 4.1 SHRIMPBOOST-Fabrik (`SHRIMPBOOST_FACTORY`)

| Attribut | Wert |
|---|---|
| **Name** | "SHRIMPBOOST-Fabrik" |
| **Kurzname** | "Boost-Fabrik" |
| **Zone** | Forschungsflügel (`Zone.FORSCHUNG`) |
| **Kosten** | **1.600** Geld |
| **Arbeiter (need)** | 3 |
| **Strom (use)** | 6 |
| **Inputs** | 3 Shrimp + 4 Schalen / Tick |
| **Output** | **+2 SHRIMPBOOST / Tick** |
| **Upkeep** | 9 Geld/Tick |
| **Rep** | -0,01/Tick (Garnelen zu Saft pressen ist… umstritten) |
| **Freischaltung** | `zone.FORSCHUNG` + Flag `build.shrimpboost` (Quest "Dosenpfand", siehe 6) |
| **Icon / Farbe** | `IconKind.CAN`, Neon-Cyan `Color(40, 200, 200)` |

**Gag:** "Wir nehmen eine Garnele, die zu nichts kommen wollte, und geben ihr einen
zweiten Karriereweg: Energydrink. Posthum, aber lukrativ." — Mira, Marketing.

**Modi:**
- `norm` "Standard-Rezeptur": ausgewogen.
- `caffeine` "Doppelt Koffein": +50 % SHRIMPBOOST, +40 % Strom & Input, -0,02 Rep.
  (`boostProduce 1.5`, `power 1.4`, Inputs x1.4)
- `light` "SHRIMPBOOST Zero": -30 % Output, dafür +0,03 Rep ("zuckerfrei, gewissensfrei").

**Upgrades:**
- "Karbonisierung" (700): +25 % SHRIMPBOOST-Output dieser Fabrik.
- "Pfand-System" (900) [FARMWEIT]: zurückgegebene Dosen → -15 % Schalen-Verbrauch aller Boost-Fabriken.

---

### 4.2 Garnelen-Roboter-Werk (`ROBOT_WORKS`)

| Attribut | Wert |
|---|---|
| **Name** | "Garnelen-Roboter-Werk" |
| **Kurzname** | "Roboter-Werk" |
| **Zone** | Logistik & Export (`Zone.LOGISTIK`) |
| **Kosten** | **2.400** Geld |
| **Arbeiter (need)** | 4 |
| **Strom (use)** | 12 |
| **Inputs** | 5 Schalen + 1 SHRIMPBOOST / Tick |
| **Output** | **+0,25 Roboter / Tick** (1 alle 4 Tage) |
| **Upkeep** | 14 Geld/Tick |
| **Rep** | 0 |
| **Freischaltung** | `zone.LOGISTIK` + Flag `build.robotworks` (Quest "Iron Shrimp", siehe 6) |
| **Icon / Farbe** | `IconKind.ROBOT`, Stahlblau `Color(120, 140, 170)` |

**Gag:** "Jeder Roboter bekommt eine echte Garnele ins Cockpit. Nicht aus technischen
Gründen — sie besteht einfach darauf." — Olaf, Technik.

**Modi:**
- `norm` "Serienfertigung": ausgewogen.
- `rush` "Akkordband": +60 % Roboter-Output, +50 % Strom & Input, +1 Arbeiter
  (`robotProduce 1.6`, `power 1.5`, Inputs x1.5).
- `quality` "Manufaktur": -30 % Output, aber die Roboter zählen als **+3 Arbeiter** statt +2
  (Premium-Modelle).

**Upgrades:**
- "Fließband-Optimierung" (1.200): +25 % Roboter-Output.
- "Garnelen-KI v2" (1.500) [FARMWEIT]: jeder Roboter zählt als +1 zusätzlicher Arbeiter/Armee.

---

### 4.3 Krill-Kaserne (`KRILL_BARRACKS`)

| Attribut | Wert |
|---|---|
| **Name** | "Krill-Kaserne" |
| **Kurzname** | "Kaserne" |
| **Zone** | Logistik & Export (`Zone.LOGISTIK`) |
| **Kosten** | **2.000** Geld |
| **Arbeiter (need)** | 3 |
| **Strom (use)** | 8 |
| **Inputs** | 2 KAMPF-KRILL + 1 SHRIMPBOOST / Tick |
| **Output** | **+4 Armee-Stärke / Tick** |
| **Upkeep** | 12 Geld/Tick |
| **Rep** | -0,02/Tick (eine Farm mit Kaserne wirkt verdächtig) |
| **Freischaltung** | `zone.LOGISTIK` + `tier.WARKRILL` + Flag `build.barracks` (Krillkill-Strang) |
| **Icon / Farbe** | `IconKind.SHIELD`, Militär-Oliv `Color(120, 130, 80)` |

**Gag:** General "Krillkill" Johnson: "DIESE SOLDATEN HABEN ZEHN BEINE UND NULL ANGST.
WER BRAUCHT EINE MARINE, WENN MAN EINE HALLE HAT?" (Akwanov nickt heimlich zustimmend.)

**Modi:**
- `drill` "Standard-Drill": ausgewogen.
- `bootcamp` "Bootcamp": +50 % Armee, +40 % Input & Strom, -0,03 Rep.
- `parade` "Parade-Modus": -20 % Armee, dafür +0,04 Rep ("Show statt Schlacht", Tourismus).

**Upgrades:**
- "Exoskelett-Rüstung" (1.300): +30 % Armee-Output dieser Kaserne.
- "Esprit de Corps" (1.600) [FARMWEIT, Krillkill-Finale]: +10 % aller Armee-Quellen,
  Armee verfällt nicht mehr ohne Kaserne.

---

### 4.4 Schälerei / Schalen-Presse (`SHELL_PRESS`)

| Attribut | Wert |
|---|---|
| **Name** | "Schälerei & Schalen-Presse" |
| **Kurzname** | "Schälerei" |
| **Zone** | Produktionshalle (`Zone.PRODUKTION`) |
| **Kosten** | **700** Geld |
| **Arbeiter (need)** | 2 |
| **Strom (use)** | 3 |
| **Inputs** | 6 rohe Schalen / Tick |
| **Output** | **+12 aufbereitete Schalen / Tick** (Netto +6) **oder** Verkauf zu 2 G/Stück |
| **Upkeep** | 4 Geld/Tick |
| **Rep** | 0 |
| **Freischaltung** | **von Anfang an** baubar (Einstiegsgebäude in die Schalen-Kette) |
| **Icon / Farbe** | `IconKind.SHELL`, Sandbeige `Color(200, 180, 140)` |

**Gag:** "Brian war Suppe. Brians Schale ist jetzt Wirtschaftswachstum. Wir nennen das
Kreislaufwirtschaft." — Schild über der Presse.

**Modi:**
- `process` "Aufbereiten": macht aus 6 rohen 12 Industrie-Schalen (für Boost/Roboter).
- `sell` "Direktverkauf": verkauft alle Schalen zu 2 G/Stück (frühes Notgeld).
- `grind` "Pulvermühle": -Schalen, +Futter (Schalen-Mehl als billiges Beifutter, +3 Futter/Tick).

**Upgrades:**
- "Hydraulik-Presse" (500): +30 % Schalen-Durchsatz.
- "Calcium-Rückgewinnung" (800) [FARMWEIT]: Becken werfen +20 % mehr Schalen ab.

---

### 4.5 Roboter-Garage (`ROBOT_GARAGE`)

| Attribut | Wert |
|---|---|
| **Name** | "Roboter-Garage & Ladestation" |
| **Kurzname** | "Garage" |
| **Zone** | Logistik & Export (`Zone.LOGISTIK`) |
| **Kosten** | **900** Geld |
| **Arbeiter (need)** | 0 (Roboter warten sich gegenseitig) |
| **Strom (use)** | 4 (Basis) + 5 je aktivem Roboter |
| **Inputs** | Strom (s. o.) |
| **Output** | hält bis zu **8 Roboter aktiv** (sonst stehen sie ungeladen rum = kein Effekt) |
| **Upkeep** | 6 Geld/Tick |
| **Rep** | 0 |
| **Freischaltung** | Flag `build.robotworks` (kommt mit dem Roboter-Werk) |
| **Icon / Farbe** | `IconKind.ROBOT`, dunkles Stahlgrau `Color(96, 104, 120)` |

**Gag:** "Ohne Garage sind Roboter nur teure Statuen mit Garnelen drin." — Notiz an der Tür.
Jede Garage ist ein **Kapazitäts-Multiplikator**: Roboter zählen erst als Arbeiter/Armee,
wenn genug Garagen-Slots da sind. Schaltet die **Roboter-Politik** (5.3) frei.

**Modi:**
- `worker` "Schichtbetrieb": alle Roboter zählen als **Arbeiter** (+2 je Stück).
- `defense` "Wachdienst": alle Roboter zählen als **Armee** (+3 je Stück).
- `mixed` "Geteilt": 50/50-Split (halbe Arbeiter, halbe Armee, abgerundet).

**Upgrades:**
- "Schnellladung" (700): +4 Roboter-Slots je Garage.
- "Solar-Carport" (900): -50 % Strom-Bedarf der Garage (Roboter laden tagsüber selbst).

---

### 4.6 Energydrink-Stand (`BOOST_STAND`) — Markt

| Attribut | Wert |
|---|---|
| **Name** | "SHRIMPBOOST-Stand" |
| **Kurzname** | "Boost-Stand" |
| **Zone** | Empfang & Garten (`Zone.EMPFANG`) |
| **Kosten** | **1.300** Geld |
| **Arbeiter (need)** | 2 |
| **Strom (use)** | 5 (Kühlung) |
| **Inputs** | bis zu **5 SHRIMPBOOST / Tick** aus dem Lager |
| **Output** | **Verkauf ~90 Geld/Dose** (preismult 1,0; mit Reputation skalierend) |
| **Upkeep** | 7 Geld/Tick |
| **Rep** | +0,05/Tick (die Influencer lieben den Stand) |
| **Freischaltung** | `zone.EMPFANG` + Flag `build.shrimpboost` (sobald man Boost herstellt) |
| **Icon / Farbe** | `IconKind.CAN`, Neon-Pink/Cyan `Color(255, 90, 180)` |

**Gag:** "Influencer-Drop des Jahres. Limitiert, weil wir die Garnelen nicht so schnell
verflüssigen können." Chad Krabbowski steht heimlich in der Schlange.

**Modi:**
- `norm` "Tagesgeschäft": ausgewogen.
- `hype` "Limited Drop": +40 % Menge, -20 % Preis, ++Rep (Volumen-Hype).
- `premium` "Boutique-Dose": -30 % Menge, +40 % Preis (Luxus-Energydrink).

**Upgrades:**
- "Vending-Automaten" (800) [FARMWEIT]: +20 % SHRIMPBOOST-Verkaufskapazität.
- "Markenbotschafter Greg" (1.000): +15 % SHRIMPBOOST-Verkaufspreis (Greg starrt aus dem Glas, es verkauft sich).

---

## 5. Politiken & Boosts (neue Optionen mit Effekten)

### 5.1 Schalen-Brennstoff-Politik (Kraftwerk)
- **Aus:** Kraftwerk läuft normal (+45 Strom).
- **An:** Kraftwerk verbrennt zusätzlich **4 Schalen/Tick** → **+15 % Strom** (+~7),
  aber **-0,02 Rep** (Geruch). Schließt den Kreis: Becken-Abfall → Strom für mehr Becken.

### 5.2 Boost-Politik (farmweit)
- **Aus:** kein SHRIMPBOOST-Verbrauch.
- **An:** verbraucht **1 SHRIMPBOOST/Tick aus Lager** → **+15 % Becken-Output farmweit**,
  solange Vorrat reicht. Läuft Lager leer, schaltet automatisch ab (kein Crash).
- Trade-off: jede verfütterte Dose ist eine nicht verkaufte 90-Geld-Dose.

### 5.3 Roboter-Politik (über Garage-Modus, 4.5)
- **Schichtbetrieb / Wachdienst / Geteilt** (s. o.): entscheidet, ob Roboter als Arbeiter
  oder Armee zählen. Jederzeit umschaltbar, aber mit 1 Tag "Umrüstzeit" (kein Effekt am Umschalttag).

### 5.4 Armee-Doktrin (Krill-Kaserne, Krillkill-Strang)
- **Defensiv:** -30 % Armee-Aufbau, aber Rep-Malus halbiert.
- **Offensiv:** +30 % Armee-Aufbau, aber Akwanov-Rivalität steigt schneller ("Wettrüsten").

---

## 6. Quest-Objectives für die neue Kette (zeitbasiert, nicht durchklickbar)

> Alle Ziele binden an **Bestände/Produktion über Zeit** — exakt die in der Aufgabe genannten
> Trigger-Typen. Schwellen sind **groß und gestaffelt**, um das Spiel auf mehrere Stunden zu
> strecken. Bestehende `Condition.Type`s (DAY/MONEY/REP/SHRIMP_PRODUCED/BUILD_COUNT/RIVAL)
> werden um neue Zieltypen ergänzt: `RESOURCE_HELD`, `RESOURCE_PRODUCED`, `RESOURCE_SOLD`, `ARMY`.

| Quest-ID | Phase | Ziel (in Worten) | Bedingung (formal) | Belohnung / Freischaltung |
|---|---|---|---|---|
| `q.shells_intro` | P1 | Sammle erstmals **200 Schalen** | `RESOURCE_HELD(SHELLS) >= 200` | Schälerei-Rabatt; Flag `build.shrimpboost` Vorstufe |
| `q.dosenpfand` | P2 | Halte **150 Schalen** UND baue 1 Forschungsflügel-Gebäude | `ALL(RESOURCE_HELD(SHELLS)>=150, zone.FORSCHUNG)` | Schaltet **SHRIMPBOOST-Fabrik** frei (`build.shrimpboost`) |
| `q.first_boost` | P2 | Produziere insgesamt **100 SHRIMPBOOST** | `RESOURCE_PRODUCED(SHRIMPBOOST) >= 100` | Schaltet **Energydrink-Stand** frei; Boost-Politik |
| `q.cash_cans` | P3 | Verkaufe insgesamt **500 SHRIMPBOOST** | `RESOURCE_SOLD(SHRIMPBOOST) >= 500` | +Geld-Bonus; Upgrade "Karbonisierung" verbilligt |
| `q.iron_shrimp` | P3 | Halte **300 Schalen** + **20 SHRIMPBOOST** gleichzeitig | `ALL(RESOURCE_HELD(SHELLS)>=300, RESOURCE_HELD(SHRIMPBOOST)>=20)` | Schaltet **Roboter-Werk + Garage** frei (`build.robotworks`) |
| `q.robot_crew` | P3 | Baue/halte **15 Roboter** | `RESOURCE_HELD(ROBOTS) >= 15` | "Garnelen-KI v2" verbilligt; Reputations-Bonus |
| `q.barracks` | P3/P4 | Produziere **2.000 KAMPF-KRILL** insgesamt | `SHRIMP_PRODUCED(WARKRILL) >= 2000` | Schaltet **Krill-Kaserne** frei (`build.barracks`) — Krillkill-Trigger |
| `q.standing_army` | P4 | Erreiche **Armee-Stärke 250** | `ARMY >= 250` | Krillkill Stufe-Up; Sabotage-Schutz aktiv |
| `q.boost_empire` | P4 | Produziere insgesamt **5.000 SHRIMPBOOST** | `RESOURCE_PRODUCED(SHRIMPBOOST) >= 5000` | farmweites Boost-Upgrade; großer Geld-Meilenstein |
| `q.robot_army` | P4 | Baue insgesamt **50 Roboter** | `RESOURCE_PRODUCED(ROBOTS) >= 50` | Achievement "Maschinen-Imperium"; +Armee-Bonus |
| `q.fortress` | P4 (Endgame) | Armee-Stärke **1.000** UND Vermögen **150.000** | `ALL(ARMY>=1000, MONEY>=150000)` | Krillkill-Finale "Operation Protein-Sturm" abgeschlossen |
| `q.vertical_empire` | P4 (Endgame) | Halte gleichzeitig **1.000 Schalen + 200 SHRIMPBOOST + 40 Roboter** | `ALL(RESOURCE_HELD(SHELLS)>=1000, RESOURCE_HELD(SHRIMPBOOST)>=200, RESOURCE_HELD(ROBOTS)>=40)` | Sieg-Variante "Vertikal integriert"; Dauer-Buff |

**Staffelungs-Prinzip:** frühe Quests (Hunderter), mittlere (Tausender), Endgame (Zehntausender
Geld / vierstellige Armee). Da jede Stufe der Kette pro Tick nur kleine Mengen liefert
(Roboter 0,25/Tick, SHRIMPBOOST 2/Tick je Fabrik), sind das **Stunden** Spielzeit — nicht 30 Minuten.

---

## 7. Engine-To-dos (für die Umsetzung)

1. **`ResourceType`** erweitern um `SHELLS, SHRIMPBOOST, ROBOTS, ARMY` (+ optional `RESEARCH`),
   inkl. neuer `IconKind`-Werte (`SHELL, CAN, ROBOT, SHIELD, FLASK`) und Palette-Farben.
2. **`BuildingType`** um I/O-Felder `shellsProduce/Use, boostProduce/Use, robotProduce/Use,
   armyProduce` erweitern; die 6 neuen Gebäude eintragen (4.1–4.6) inkl. `Meta`/Zone/Unlock.
3. **`Stats`** um dieselben Durchsatz-/Bestandsfelder ergänzen; Becken-Schalen-Nebenprodukt
   (0,6 × Shrimp × Tier-Multiplikator) im Tick berechnen.
4. **`GameState`** um Bestände + kumulierte Produktions-/Verkaufszähler je Ressource
   (`getResourceHeld/Produced/Sold(type)`) + `getArmyStrength()` erweitern; "kein Input → kein Output"-Regel.
5. **`Condition.Type`** um `RESOURCE_HELD, RESOURCE_PRODUCED, RESOURCE_SOLD, ARMY` ergänzen
   (mit `ResourceType key`-Feld) und in `test()` verdrahten.
6. **`BuildingCatalog`** Modi/Upgrades aus Abschnitt 4 eintragen; Politiken aus Abschnitt 5
   als `WorkerPolicy`-Analoga / Farm-weite Toggles.

---

> "Erst war es eine Garnele. Dann ein Energydrink. Dann ein Roboter. Dann eine Armee.
> Irgendwo hat jemand vergessen, Nein zu sagen — und genau das ist Tycoon." — Greg, aus dem Wasserglas
