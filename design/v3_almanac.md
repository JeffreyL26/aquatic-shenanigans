# ShrimpTopia v3 — Tier-Menü & Almanach (Tropico-Stil)

> "Ein Tycoon liest keine Tabellen. Ein IMPERATOR liest Tabellen — und weiß dann,
> wen er anbrüllen muss." — Aushang im HQ-Pausenraum, unter einem Kaffeefleck.

Dieses Dokument liefert **transkribier-fertige** Texte für zwei UI-Flächen der v3-Erweiterung:

- **(a) das TIER-MENÜ** ("Garnelen-Akte") — der spielinterne Guide über alle sechs
  Shrimp-Tiers: was sie sind, wie man sie produziert, wo man sie verkauft, ihr Wert
  und ihr Reputations-/Kontroverse-Effekt.
- **(b) der ALMANACH** ("Das Große Garnelen-Kompendium") — die Tropico-Statistik-Übersicht
  mit sechs Reitern: Vermögen, Ressourcen, Tiers, Effekte, Statistik, Armee & Konflikte.

**Datenstand (Engine-Kanon, aus `ShrimpTier.java` / `BuildingType.java`):**
Tier-Basiswerte und Reputations-Effekte sind die **echten Enum-Werte** der laufenden Engine.
Die v3-Ressourcen (Schalen, SHRIMPBOOST, Roboter, Armee-Stärke) und die drei neuen Fabriken
sind **neu** und werden von der Engine noch umgesetzt — die Zahlen unten sind die
Design-Vorgaben dafür.

---

# TEIL A — DAS TIER-MENÜ ("Garnelen-Akte")

> UI-Aufruf: Reiter **"Garnelen-Akte"** im Almanach oder Klick auf das Tier-Symbol im HUD.
> Jede Karte zeigt: **Name · Farbe · Wert/Stück · Rep-Effekt · Produktion · Absatz · Akte (Gag)**.

Jeder Tier-Eintrag listet:
- **Was/Kann** — 1-2 Sätze Charakter.
- **Produktion** — Becken-Modus + Freischalt-Flag.
- **Absatz** — welche Märkte das Tier kaufen (Kanon aus `BuildingType.META`).
- **Wert** — Basiswert/Stück (Engine-Enum) **vor** Markt-Multiplikator & Reputation.
- **Kontroverse** — Reputations-Effekt pro verkauftem Stück (`repPerUnit`).

| # | Tier (ID) | Anzeige-Name | Wert/Stk | Rep/Stk | Freischalt-Flag |
|---|-----------|--------------|---------:|--------:|-----------------|
| 1 | `STANDARD` | Wald-und-Wiesen-Shrimp | 18 | +0,00 | (immer frei) |
| 2 | `BIO` | Bio-Shrimp | 32 | +0,04 | `tier.BIO` |
| 3 | `GOURMET` | Gourmet-Shrimp | 55 | +0,06 | `tier.GOURMET` |
| 4 | `PROTEIN` | Protein-Bombe | 85 | -0,03 | `tier.PROTEIN` |
| 5 | `GENTECH` | Designer-Shrimp | 130 | -0,07 | `tier.GENTECH` |
| 6 | `WARKRILL` | Kampf-Krill | 210 | -0,12 | `tier.WARKRILL` |

> Verkaufspreis-Formel (Erinnerung): `Endpreis = baseValue × Markt-priceMult × Rep-Multi(0,6–1,4)`.
> Hohe Tiers tragen sich also nur, wenn der **richtige Markt** freigeschaltet ist UND die
> Reputation stimmt. Massenware füllt das Lager, der richtige Abnehmer füllt das Konto.

---

## Tier 1 — Wald-und-Wiesen-Shrimp `STANDARD`
- **Farbe:** stumpfes Lachs-Grau `RGB(255,150,130)`.
- **Was/Kann:** Die Brot-und-Butter-Garnele. Hauptschulabschluss in Plankton-Konsum,
  null Ambitionen, beleidigt niemanden. Schmeckt nach "Garnele halt".
- **Produktion:** Standard-Becken im **Normalmodus**. Sofort verfügbar, kein Flag nötig.
  Jedes Becken wirft pro Tag Standard-Shrimps aus.
- **Absatz:** **Lokale Shrimp-Börse** (priceMult 1,0x) und **Shrimp-Restaurant** (1,7x).
  Die Massen-Garnele für den ehrlichen Großhandel.
- **Wert:** **18**/Stück. Referenz-Garnele, die Skala fängt hier an.
- **Kontroverse:** **±0**. Niemand regt sich über eine normale Garnele auf. Image-neutral.

## Tier 2 — Bio-Shrimp `BIO`
- **Farbe:** öko-Grün `RGB(150,210,120)`.
- **Was/Kann:** Frei-schwimmend aufgewachsen, Algen aus kontrolliertem Anbau, ein Zertifikat
  mehr als Geschmack. Die Garnele mit Jutebeutel und Mehrweg-Schale.
- **Produktion:** Becken-Modus **"Bio-Aufzucht"**, freigeschaltet über `tier.BIO`
  (= erstes **Forschungslabor** gebaut). Verbraucht etwas mehr Futter, dafür Rep-positiv.
- **Absatz:** **Börse** (1,0x), **Restaurant** (1,7x) und **Export-Hafen** (1,3x).
  Der breiteste Absatz von allen — gutes Allround-Tier.
- **Wert:** **32**/Stück.
- **Kontroverse:** **+0,04**/Stück. Leichter Reputations-Bonus — sauberes Gewissen verkauft sich.

## Tier 3 — Gourmet-Shrimp `GOURMET`
- **Farbe:** warmes Bernstein-Gold `RGB(255,198,110)`.
- **Was/Kann:** Täglich handmassiert, hört klassische Musik, schmilzt angeblich auf der Zunge.
  Kostet pro Kilo wie ein Kleinwagen, und genau das ist der Punkt.
- **Produktion:** Becken-Modus **"Gourmet-Manufaktur"**, Flag `tier.GOURMET` (Quest
  "Becken-Gourmet" + Labor-Upgrade ODER Genlabor). Niedriger Durchsatz, hoher Wert.
- **Absatz:** **Restaurant** (1,7x) und **Export-Hafen** (1,3x). Premium-Abnehmer only —
  die Börse kennt den Begriff "Gourmet" nicht.
- **Wert:** **55**/Stück.
- **Kontroverse:** **+0,06**/Stück. Stärkster Rep-Bonus im Spiel. Luxus poliert das Image.

## Tier 4 — Protein-Bombe `PROTEIN`
- **Farbe:** muskulöses Ziegel-Orange `RGB(232,120,88)`.
- **Was/Kann:** Die Fitness-Garnele. Doppelt so viel Eiweiß, halb so viel Charme. Trinkt
  ihren eigenen SHRIMPBOOST und nennt dich "Bro". Grundnahrungsmittel der Krill-Kaserne.
- **Produktion:** Becken-Modus **"Mast-Becken"**, Flag `tier.PROTEIN`. Frisst viel Futter,
  liefert dafür proteinreiche Masse — der Schlüssel-Input für **Armee-Stärke** (siehe Teil B6).
- **Absatz:** **Militär-Depot** (1,5x, Festpreis-Logik). Der General nimmt Protein-Bomben
  in Masse als Truppenverpflegung — fragt aber nicht nach Geschmack.
- **Wert:** **85**/Stück.
- **Kontroverse:** **-0,03**/Stück. Leicht negativ — "Pumpgarnele" ist kein gutes Pressebild.

## Tier 5 — Designer-Shrimp `GENTECH`
- **Farbe:** radioaktiv schimmerndes Violett `RGB(190,120,230)`.
- **Was/Kann:** Genetisch "leicht überoptimiert". Größer, glänzender, glüht sanft im Dunkeln.
  Schmeckt fantastisch — die Verbraucherschützer weniger. "Völlig unbedenklich!" in Schriftgröße 4.
- **Produktion:** **Genlabor** + Becken-Modus **"Genpfusch"**, Flag `tier.GENTECH`.
  Braucht das Genlabor (Zone Forschung) und viel Strom.
- **Absatz:** **Export-Hafen** (1,3x, legal & prestigeträchtig) und **Schwarzmarkt**
  (1,8x, diskret & lukrativ). Geld jetzt, Shitstorm gratis dazu.
- **Wert:** **130**/Stück.
- **Kontroverse:** **-0,07**/Stück. Deutlich negativ — GVO-Garnelen machen Schlagzeilen.

## Tier 6 — Kampf-Krill `WARKRILL`
- **Farbe:** alarm-rotes Kampf-Rot `RGB(220,70,70)`.
- **Was/Kann:** General Krillkills Meisterwerk. Eine Garnele, die zurückbeißt. Marschiert
  in Formation, salutiert vor dem Becken und kennt drei Nahkampftechniken. "JEDE GARNELE
  IST EIN SOLDAT, SOLDAT!"
- **Produktion:** Becken-Modus **"Kampf-Aufzucht"** (schaltet erst nach Designer-Stufe frei),
  Flag `tier.WARKRILL`. Höchster Wert, höchste Kontroverse, langsamste Aufzucht. Pflicht-Input
  für die **Krill-Kaserne** (Armee-Stärke).
- **Absatz:** **Militär-Depot** (1,5x) und **Schwarzmarkt** (1,8x). Der General bestellt,
  der Schwarzmarkt zahlt — beide stellen keine Fragen.
- **Wert:** **210**/Stück. Endgame-Garnele.
- **Kontroverse:** **-0,12**/Stück. Größter Reputations-Schaden im Spiel. Eine Privatarmee
  aus Krustentieren beruhigt die Öffentlichkeit nicht.

### Tier-x-Markt-Akzeptanzmatrix (Kanon aus der Engine)

Legende: **J** = wird gekauft, **–** = abgelehnt.

| Tier \ Markt | Börse (1,0x) | Restaurant (1,7x) | Export (1,3x) | Militär (1,5x) | Schwarzmarkt (1,8x) |
|--------------|:------------:|:-----------------:|:-------------:|:--------------:|:-------------------:|
| 1 Standard   | J | J | – | – | – |
| 2 Bio        | J | J | J | – | – |
| 3 Gourmet    | – | J | J | – | – |
| 4 Protein    | – | – | – | J | – |
| 5 Designer   | – | – | J | – | J |
| 6 Kampf-Krill| – | – | – | J | J |

**Kernlektion:** Wer hochzüchtet, MUSS den passenden Markt freischalten. Reine Börse +
Tier-6-Becken = volles Lager, leeres Konto.

---

# TEIL B — DER ALMANACH ("Das Große Garnelen-Kompendium")

> UI: ein Buch-Overlay mit sechs Reitern (analog Tropico-Almanach). Oben rechts ein
> kleines Greg-im-Wasserglas-Icon, das je nach Reiter einen trockenen Kommentar murmelt.
> Alle Kennzahlen sind **Live-Werte aus `GameState` + `Stats`**; "Heute" = aktueller Tick,
> "Gesamt" = kumuliert seit Spielstart.

Reiter-Übersicht:

| # | Reiter (ID) | Witzige Überschrift | Kerninhalt |
|---|-------------|---------------------|-----------|
| B1 | `WEALTH` | "Wo das Geld schwimmt" | Umsatz nach Markt, Kosten nach Kategorie, Netto |
| B2 | `RESOURCES` | "Ebbe & Flut" | Produktion/Verbrauch je Ressource (inkl. v3) |
| B3 | `TIERS` | "Die Garnelen-Akte" | Verweis aufs Tier-Menü + Produktions-Mix |
| B4 | `EFFECTS` | "Wer hier mitredet" | aktive farmweite Modifikatoren + Quelle |
| B5 | `STATS` | "Fürs Protokoll" | Spielzeit, Gesamt produziert/verkauft, Rekorde |
| B6 | `ARMY` | "Krieg & Krustentier" | Armee-Stärke, Bedrohung, Konflikte |

---

## B1 — VERMÖGEN: "Wo das Geld schwimmt"

> "Geld ist wie Wasser im Becken: Du merkst es erst, wenn keins mehr da ist." — Mira,
> beim Blick auf die rote Zahl.

Die Bilanz-Seite. Zeigt, **woher das Geld kommt** (Umsatz je Markt), **wohin es fließt**
(Kosten je Kategorie) und unten fett das **Netto pro Tag** + Kontostand.

**Kennzahlen:**
- **Umsatz nach Markt** (Geld/Tag, je Verkaufsstelle):
  - Lokale Shrimp-Börse · Shrimp-Restaurant · Export-Hafen · Militär-Depot · Schwarzmarkt
  - **SHRIMPBOOST-Verkauf** (neuer Hochwert-Posten, falls Energydrink extern verkauft wird)
  - je Zeile: Stück/Tag, Ø-Preis/Stück, Tagesumsatz, Anteil am Gesamtumsatz in %.
- **Kosten nach Kategorie** (Geld/Tag):
  - **Betrieb** (Summe aller `upkeep`)
  - **Löhne** (Arbeiterzahl × Lohnsatz, abhängig von Arbeiter-Politik)
  - **Inputs verschwendet** (überzählige Shrimps/Schalen ohne Abnehmer = Lagerverlust)
  - **Export-Tarif / Embargo** (Akwanov-Strafabgabe, falls aktiv)
  - **Strafen & Bußgelder** (Dr. Quallmann / Behörden-Events)
- **Netto/Tag** = Umsatz − Kosten. Grün = Gewinn, Rot = du verbrennst Geld.
- **Kontostand** (`money`) + 7-Tage-Trendpfeil + Tage bis Bankrott (bei negativem Netto).
- **Ziel-Anzeige:** Fortschritt zu `GOAL_MONEY = 50.000` (Tycoon) und `100.000` (Imperator).

**Gag-Fußzeile:** "Hinweis der Buchhaltung: 'Glück in Schalenform' ist KEIN steuerlich
absetzbarer Posten. Wir haben es versucht."

---

## B2 — RESSOURCEN: "Ebbe & Flut"

> "Wir produzieren genug Strom für eine Kleinstadt und genug Schalen für eine Müllhalde.
> Beides nennen wir 'Synergie'." — Olaf (Technik), stolz.

Die Stoffstrom-Seite. Für **jede Ressource** eine Zeile mit Produktion, Verbrauch, Saldo
und Lagerbestand. Saldo grün = Überschuss, rot = Engpass (blinkt bei drohendem Nullstand).

**Kennzahlen pro Ressource (Produktion / Verbrauch / Saldo / Bestand):**

Bestehende Ressourcen (`ResourceType`):
- **Strom** (`POWER`) — Erzeugung (Kraftwerk/Solar) vs. Verbrauch aller Gebäude.
- **Wasser** (`WATER`) — Wasserwerk/Hub vs. Becken & Algenfarmen.
- **Futter** (`FEED`) — Algenfarm/Bio-Reaktor vs. Becken-Mast.
- **Shrimps** (`SHRIMP`) — Becken-Output vs. Verkauf + Fabrik-Inputs.
- **Arbeiter** (`WORKERS`) — bereitgestellt (HQ/Wohnheim/**Roboter**) vs. benötigt.
- **Reputation** (`REPUTATION`) — Gewinn (Solar/Besucherzentrum/Bio) vs. Verlust (Kraftwerk/GVO/Militär).

Neue v3-Lagerbestände:
- **Schalen** (`SHELLS`) — Nebenprodukt **jedes Becken-Ticks** (~0,5 Schalen je produziertem
  Shrimp). Input für SHRIMPBOOST-Fabrik, Roboter-Werk und (optional) als Kraftwerk-Brennstoff.
  Ohne Abnehmer stapeln sie sich = Lager-Malus + leichter Rep-Verlust ("riecht").
- **SHRIMPBOOST** (`BOOST`) — Energydrink. Erzeugt in der SHRIMPBOOST-Fabrik. Verbrauch:
  Effizienz-Boost farmweit ODER Verkauf (Hochwert) ODER Input fürs Roboter-Werk / die Kaserne.
- **Roboter** (`ROBOTS`) — garnelenbetriebene Einheiten aus dem Roboter-Werk. Zählen wahlweise
  als **Arbeiter** (kein Lohn, keine Pause) und/oder als **Armee-Stärke**.
- **Armee-Stärke** (`ARMY`) — siehe Reiter B6. Wird in der Krill-Kaserne erzeugt.

**Engpass-Ampel:** Jede Ressource mit Saldo < 0 und Bestand < 1 Tagesverbrauch wird rot
markiert ("KRITISCH"). Klick springt zum verantwortlichen Gebäudetyp.

**Gag-Fußzeile:** "Schalen-Überschuss erreicht. Die Möwen kennen unsere Adresse jetzt auswendig."

---

## B3 — TIERS: "Die Garnelen-Akte"

> "Sechs Tiers. Sechs Persönlichkeiten. Ein Therapeut wäre billiger gewesen." — Dr. Perla Pereira.

Spiegelt Teil A in den Almanach. Zeigt zusätzlich den **aktuellen Produktions-Mix** und
die **kumulierte Produktion je Tier** — die Grundlage für viele Quest-Ziele.

**Kennzahlen:**
- **Produktions-Mix heute** (Balken je Tier: Stück/Tag, Anteil in %).
- **Gesamt produziert je Tier** (kumuliert — Trigger für Ziel "produziere N Shrimps eines Tiers").
- **Freigeschaltet?** je Tier (Flag gesetzt = grün, sonst grau mit Freischalt-Bedingung in Worten).
- **Ø-Verkaufspreis je Tier** (real erzielt, inkl. Markt × Reputation — zeigt, welches Tier
  sich gerade wirklich lohnt).
- **Best-Markt-Tipp:** je Tier der aktuell lukrativste freigeschaltete Abnehmer.

**Gag-Fußzeile:** "Kampf-Krill verlangt, hier 'Elite-Einheit' genannt zu werden. Antrag abgelehnt."

---

## B4 — EFFEKTE: "Wer hier mitredet"

> "In dieser Farm hat jeder eine Meinung — sogar die Boni." — Mira, sortiert Akten.

Listet **alle aktiven farmweiten Modifikatoren** (`FarmModifiers` / `GlobalEffect`) mit
**Wert, Wirkung und Quelle**. Damit sieht der Spieler genau, warum sein Preis-Multiplikator
gerade 1,3x ist und wer ihm Reputation klaut.

**Kennzahlen (je Effekt: Name · Wert · Wirkung · Quelle · Dauer):**
- **Verkaufspreis-Multiplikator** — z.B. `×1,12 je Labor` (Quelle: Forschungslabor; permanent).
- **SHRIMPBOOST-Effizienz** — z.B. `+15% auf alle Becken` (Quelle: SHRIMPBOOST aktiv; solange Vorrat).
- **Reputations-Modifikatoren** — Solar/Besucherzentrum (+) vs. Kraftwerk/GVO/Militär (−), je Quelle.
- **Arbeiter-Politik-Effekt** — aktuelle Politik (z.B. "Drill" = +Effizienz/−Rep) mit Werten.
- **Charakter-Buffs** — "Esprit de Corps" (Krillkill-Finale), "Akwanov-Akademie" (Diplomatie),
  je mit Quelle und ob permanent/temporär.
- **Embargo / Export-Tarif** — Akwanov-Strafe (z.B. `−20% Export-Umsatz`), Dauer/Restdauer.
- **Event-Modifikatoren** — temporäre Boni/Mali aus Zufallsevents (Viral-Hype +, Möwen −),
  mit Countdown.

**Anzeige-Logik:** positive Effekte grün, negative rot, neutrale grau. Sortiert nach
Betragsgröße. Hover zeigt den exakten Rechenweg ("Basis 18 × 1,7 Restaurant × 1,3 Rep = 39,8").

**Gag-Fußzeile:** "Aktiver Effekt: 'Greg beobachtet dich.' Wirkung: keine. Quelle: Greg."

---

## B5 — STATISTIK: "Fürs Protokoll"

> "Eines Tages schreibt jemand ein Buch über dich. Diese Seite ist das Inhaltsverzeichnis."
> — Akwanov, der schon weiß, dass er das Buch schreiben wird.

Die Rekord- und Lebenslauf-Seite. Reine Historie, keine Steuerung.

**Kennzahlen:**
- **Spielzeit:** aktueller **Tag** (`gs.getDay()`), reale Spieldauer, aktive Phase (P0–P4).
- **Gesamt produziert:** Shrimps gesamt (`getTotalShrimpProduced()`), aufgeschlüsselt je Tier;
  Schalen gesamt, SHRIMPBOOST gesamt, Roboter gesamt.
- **Gesamt verkauft:** Stück gesamt + Lebenszeit-Umsatz; Aufschlüsselung je Markt
  (Trigger für Ziel "verkaufe insgesamt N").
- **Gebäude:** je Typ gebaut/aktiv, je Zone (Trigger für Ziel "baue N Gebäude Typ/Zone").
- **Rekorde:** bester Tages-Netto, höchster Kontostand, höchste Reputation, größte Becken-Zahl,
  längste Pleite-Strähne, teuerster Einzelverkauf (vermutlich ein Kampf-Krill).
- **Meilensteine:** abgehakte Ziele (50.000 Tycoon, 100.000 Imperator, alle 6 Tiers verkauft …).

**Gag-Fußzeile:** "Rekord: 0 Tage ohne einen Garnelen-Wortwitz. Wir arbeiten daran. Nicht."

---

## B6 — ARMEE & KONFLIKTE: "Krieg & Krustentier"

> "ICH HABE EINE ARMEE AUS GARNELEN AUFGEBAUT, UND SIE FRAGEN, OB DAS NÖTIG WAR?!" — General "Krillkill" Johnson.
> "Wer braucht schon Ozeane? Wir haben HALLEN. Und jetzt auch eine ARMEE in den Hallen." — Außenminister Akwanov, nervös.

Der v3-Konflikt-Reiter. Bündelt **Armee-Stärke**, eingehende **Bedrohungen** und den Stand
der beiden Charakter-Konflikte (Krillkill / Akwanov).

**Kennzahlen:**
- **Armee-Stärke** (`ARMY`) — aktueller Wert + Produktion/Tag aus der **Krill-Kaserne**
  (Input: Kampf-Krill + SHRIMPBOOST). Trigger für Ziel "Armee-Stärke ≥ X".
- **Verteidigungsbedarf:** aktuelle Bedrohungsstufe (steigt mit Vermögen & Rivalität) vs.
  vorhandene Stärke. Defizit rot = anfällig für Sabotage/Überfall-Events.
- **Roboter (militärisch):** wie viele Roboter als Armee statt als Arbeiter zugeteilt sind
  (umschaltbar) — zählen mit in die Armee-Stärke.
- **Rivalität** (`rival`, 0–100) — Akwanov-Spannung. Trigger für Ziel "Rivalität ≥ X" und
  für sein Finale (≥ 66 + 50.000 Geld).
- **Aktive Konflikte/Embargos:** laufende Sabotage, Schmierkampagnen, Embargos mit Restdauer
  und empfohlener Gegenmaßnahme.
- **Krillkill-Strang:** aktuelle Stufe (1–7) + nächstes Ziel; Akwanov-Strang: Stufe (1–8) + nächstes Ziel.
- **Konflikt-Log:** letzte fünf militärisch/diplomatisch relevante Ereignisse.

**Gag-Fußzeile:** "Verteidigungsbericht: 3 Möwen abgewehrt, 1 Steuerprüfer verschreckt,
0 Ozeane verteidigt (haben wir nicht)."

---

# TEIL C — QUEST-ZIELE (Trigger-Referenz für die Engine)

> Damit Quests **nicht durchklickbar** sind, sondern **über Zeit durch Produktion** erfüllt
> werden, binden alle v3-Quests an messbare Ziele. Diese Tabelle ist die transkribier-fertige
> Liste der Ziel-Typen — bestehende Typen aus `Condition.Type`, neue v3-Typen kursiv markiert.

| Ziel-Typ (ID) | Bedingung in Worten | Schwellen-Staffelung (Bsp. für Stunden-Länge) |
|---------------|---------------------|-----------------------------------------------|
| `MONEY` | Erreiche X Vermögen | 2.000 → 10.000 → 50.000 → 100.000 → 250.000 |
| `DAY` | Erreiche Tag X | 10 → 25 → 45 → 80 → 120 |
| `SHRIMP_PRODUCED` | Produziere insgesamt N Shrimps (gesamt) | 500 → 5.000 → 25.000 → 100.000 |
| *`TIER_PRODUCED`* | Produziere insgesamt N Shrimps eines **Tiers** | je Tier: 200 → 2.000 → 10.000 |
| *`RESOURCE_HELD`* | Halte/sammle N einer Ressource (Schalen/BOOST/Roboter/Futter/Wasser) | 100 → 1.000 → 10.000 |
| *`TOTAL_SOLD`* | Verkaufe insgesamt N (gesamt oder je Markt) | 300 → 3.000 → 30.000 |
| `BUILD_COUNT` | Baue N Gebäude (Typ oder Zone) | 1 → 3 → 8 → 15 |
| *`ARMY`* | Armee-Stärke ≥ X | 50 → 250 → 1.000 → 5.000 |
| `REP` | Reputation ≥ X | 40 → 60 → 80 → 95 |
| `RIVAL` | Rivalität ≥ X | 25 → 50 → 66 → 90 |

**Staffel-Prinzip (Ziel: viele Stunden statt ~30 Min):** Schwellen wachsen grob **um Faktor
5–10 pro Stufe**. Frühe Ziele sind in Minuten machbar, späte erfordern **etablierte
Produktionsketten über viele Tage** (z.B. "halte 10.000 Schalen" zwingt zum Bau mehrerer
Becken UND Verarbeitungs-Fabriken, statt nur Geld zu sammeln). So entsteht echte
Spätspiel-Tiefe ohne neue Klick-Arbeit.

**Vorschlag: zwei zusätzliche v3-Gebäude** (passend zur SHRIMPBOOST/Roboter/Kaserne-Kette):
- **Schalen-Recycler** (Zone Produktion): wandelt überschüssige **Schalen → Futter + etwas
  Strom**. Entschärft den Schalen-Stau und schließt den Stoffkreislauf (Bio-Image-Bonus).
- **Roboter-Kommandozentrale** (Zone Logistik): teilt **Roboter** dynamisch zwischen Arbeit
  und Armee zu und gibt einen kleinen farmweiten Effizienz-Buff je zugewiesenem Roboter —
  das Steuerpult für die ganze v3-Automatisierungs-Kette.

> "Am Ende geht es nicht um Garnelen. Es geht NIE um Garnelen." — Akwanov, melancholisch.
> "Doch. Es geht IMMER um Garnelen." — Krillkill, lauter.
