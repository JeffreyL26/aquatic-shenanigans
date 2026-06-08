# Shrimp-Tiers & Verkaufs-Maerkte (v2 Design)

> "Eine Garnele ist eine Garnele? Falsch. Eine Garnele ist ein Produkt, ein Lebensgefuehl
> und auf den richtigen Maerkten eine Anlageklasse." — Aussenminister Akwanov, vermutlich

Dieses Dokument definiert die **Qualitaetsstufen (Tiers)** deiner Shrimps sowie die
**Maerkte**, an die du sie verkaufst. Kernidee fuer die Spieltiefe:

- **Nicht jeder Markt kauft jedes Tier.** Wer Premium-Shrimps zuechtet, muss auch die
  passenden Abnehmer freischalten — sonst sitzt man auf einem Becken voller Luxus, den
  die Dorf-Boerse einfach nicht bezahlen will ("ham wir nicht in der Preisliste").
- **Hoehere Tiers = mehr Geld, aber heiklerer Absatz.** Knappe Tageskapazitaeten und
  Reputationsrisiken zwingen dich, dein Portfolio breit aufzustellen.
- **Manche Tiers sind kontrovers** und kosten beim Verkauf Reputation — Geld jetzt,
  schlechte Presse spaeter.

Werte sind **relativ** zur bestehenden Oekonomie (Boerse-Basispreis ~22/Stk,
Reputations-Multiplikator 0,6x–1,4x). Tier 1 = Referenzwert **1,0x**.

---

## 1. Die Shrimp-Tiers (6 Stufen)

| # | Tier | Farbe | Basiswert (rel.) | Rep-Effekt/Verkauf | Freischaltung |
|---|------|-------|-----------------:|:------------------:|---------------|
| 1 | Feld-, Wald- & Wiesen-Shrimp | Schlamm-Grau-Braun `#8A7B5C` | 1,0x | 0 | Startgarnele (sofort) |
| 2 | Bueroklammer-Shrimp | Buero-Beige `#C9B27E` | 1,8x | 0 | 1x Forschungslabor |
| 3 | Wagyu-Wassergarnele | Marmor-Rosa `#E8A0A8` | 3,5x | +klein | Quest "Becken-Gourmet" + 1 Labor-Upgrade |
| 4 | Neon-Hypebeast-Shrimp | UV-Pink/Cyan `#FF3CAC` -> `#00E5FF` | 6,0x | +mittel (instabil) | Meilenstein 25k Geld + viral-Event |
| 5 | Glowrilla 9000 (GVO) | Radioaktiv-Gruen `#39FF14` | 9,0x | -mittel (kontrovers) | Labor-Modus "Genpfusch" + Quest Krillkill |
| 6 | Krill-Diamant (Imperial) | Tiefsee-Violett + Glitzer `#7A3CFF` | 16,0x | +/- (polarisiert) | Endgame: alle Tiers + Quest "Krone der Krustentiere" |

### Tier 1 — Feld-, Wald- & Wiesen-Shrimp
- **Farbe:** stumpfes Schlamm-Grau-Braun (`#8A7B5C`) — die Garnele, die keiner liebt, aber alle essen.
- **Beschreibung/Gag:** Die Brot-und-Butter-Garnele. Schmeckt nach "Garnele halt".
  Kommt mit einem Hauptschulabschluss in Plankton-Konsum und null Ambitionen.
  Verkauft sich ueberall, weil sie niemandem wehtut.
- **Basiswert:** 1,0x (Referenz). Massenware.
- **Rep-Effekt:** 0. Niemand regt sich ueber eine normale Garnele auf.
- **Freischaltung:** Von Anfang an. Was dein Standard-Becken auswirft.

### Tier 2 — Bueroklammer-Shrimp
- **Farbe:** Buero-Beige (`#C9B27E`) mit einem Hauch Aktenordner.
- **Beschreibung/Gag:** Diese Garnele hat einen **BWL-Abschluss** (siehe Becken-Lore),
  optimiert ihren eigenen Naehrwert per Excel und antwortet auf E-Mails binnen 24h.
  Etwas fester im Biss, "ein verlaesslicher Performer".
- **Basiswert:** 1,8x.
- **Rep-Effekt:** 0. Solide, unaufgeregt, leicht langweilig.
- **Freischaltung:** Sobald **1 Forschungslabor** steht (Genetik-Grundlagen).

### Tier 3 — Wagyu-Wassergarnele
- **Farbe:** Marmor-Rosa (`#E8A0A8`) mit feiner Fett-Maserung (ja, Garnelen-Marmorierung,
  frag das Marketing).
- **Beschreibung/Gag:** Taeglich von Hand massiert, hoert klassische Musik und bekommt
  romantische Beckenbeleuchtung. Schmilzt angeblich auf der Zunge. Kostet so viel wie
  ein Kleinwagen pro Kilo, und genau das ist der Punkt.
- **Basiswert:** 3,5x.
- **Rep-Effekt:** **+klein** beim Verkauf an Gourmet-Abnehmer (Luxus poliert das Image).
- **Freischaltung:** Quest **"Becken-Gourmet"** abschliessen + **Labor-Upgrade "Feinschmecker-Genetik"**.

### Tier 4 — Neon-Hypebeast-Shrimp
- **Farbe:** UV-Pink-zu-Cyan-Verlauf (`#FF3CAC` -> `#00E5FF`), leuchtet im Dunkeln.
- **Beschreibung/Gag:** Die Garnele als **Lifestyle-Produkt**. Limitierte "Drops",
  nummerierte Schalen, Wartelisten. Influencer essen sie nicht, sie **posten** sie.
  Eine Krustentier-Sneaker-Kultur. Hype heute, Ramsch morgen.
- **Basiswert:** 6,0x (mit Hype-Schwankung — siehe Schwarzmarkt/Influencer-Logik).
- **Rep-Effekt:** **+mittel, aber instabil.** Geht der Hype, kann's auch nach hinten losgehen.
- **Freischaltung:** Meilenstein **25.000 Geld** erreicht **und** mindestens ein
  Viral-Event ("Dein Meme geht viral") ausgeloest.

### Tier 5 — Glowrilla 9000 (GVO)
- **Farbe:** radioaktiv-leuchtendes Gruen (`#39FF14`), sichtbar auch durch zwei Wande.
- **Beschreibung/Gag:** Genetisch "leicht ueberoptimiert". Doppelt so gross, halb so
  natuerlich, glueht sanft vor sich hin. Schmeckt fantastisch — die Verbraucherschuetzer
  weniger. "Voellig unbedenklich!" steht in Schriftgroesse 4 auf der Packung.
- **Basiswert:** 9,0x.
- **Rep-Effekt:** **-mittel (kontrovers).** Geld jetzt, Shitstorm gratis dazu.
- **Freischaltung:** Labor-**Modus "Genpfusch"** aktivieren + Quest von **General Krillkill**
  ("Wir brauchen groessere Garnelen. Fragen Sie nicht warum.").

### Tier 6 — Krill-Diamant (Imperial)
- **Farbe:** Tiefsee-Violett mit echtem Glitzer-Schimmer (`#7A3CFF`), funkelt wie ein Edelstein.
- **Beschreibung/Gag:** Die legendaere Garnele. Wird nicht gegessen, sondern **besessen**.
  Sammler legen sie in Vitrinen, Oligarchen verschenken sie zu Hochzeiten, ein Exemplar
  hing kurzzeitig im Museum. Polarisiert: die einen feiern die Krone der Krustentiere,
  die anderen finden 16-faches Geld fuer eine Garnele dekadent.
- **Basiswert:** 16,0x.
- **Rep-Effekt:** **polarisiert (+/-)** — haengt vom Markt ab (Export-Hafen: +, Schwarzmarkt: -).
- **Freischaltung:** Endgame. Alle anderen Tiers mindestens einmal verkauft + Quest
  **"Krone der Krustentiere"**.

---

## 2. Die Maerkte / Verkaufsorte (6 Stueck)

**Wichtig:** Spalte "Akzeptiert" zeigt, welche Tiers ein Markt ueberhaupt annimmt.
Hohe Tiers brauchen die richtigen Abnehmer — die Dorf-Boerse winkt bei Tier 4+ einfach ab.

| Markt | Akzeptiert Tiers | Preis-Multi | Kapazitaet/Tag (rel.) | Rep-Effekt | Freischaltung |
|-------|:----------------:|:-----------:|:---------------------:|:----------:|---------------|
| Lokale Shrimp-Boerse | 1–2 | 0,9x | hoch (10) | 0 | Start (= bestehende Boerse) |
| Shrimp-Restaurant | 2–4 | 1,4x | mittel (4) | + | Restaurant gebaut |
| Export-Hafen | 3–6 | 1,8x | mittel-hoch (7) | + (Prestige) | Quest "Containerschiff klarmachen" |
| Militaer-Kontrakt (Krillkill) | 1, 5 | 1,3x fix | sehr hoch (15) | -- | General-Krillkill-Questreihe |
| Schwarzmarkt (Hafen-Hinterhof) | 4–6 | 2,2x (volatil) | niedrig (3) | -- | Reputation < 40 ODER Schmuggler-Kontakt |
| Usbekistan-Connection | alle 1–6 | 0,7x–3,0x (Wuerfel) | variabel (1–12) | zufaellig | Akwanov-Quest "Die Seidenstrasse der Shrimps" |

### Markt A — Lokale Shrimp-Boerse
- **Akzeptiert:** Tier 1–2. Mehr versteht die Preistabelle nicht ("Wagyu-WAS?").
- **Preis-Logik:** `Basiswert x 0,9 x Reputations-Multiplikator (0,6–1,4)`. Der ehrliche
  Grosshandel: zuverlaessig, aber drueckt den Preis.
- **Kapazitaet/Tag:** hoch (~10 rel.). Schluckt fast alles, was du an Massenware lieferst.
- **Rep-Effekt:** 0. Brot-und-Butter-Geschaeft.
- **Gag:** Haengt am Schwarzen Brett: "Heute: Garnele. Morgen: auch Garnele."
- **Freischaltung:** Bestehende `Shrimp-Boerse`. Dein Einstieg.

### Markt B — Shrimp-Restaurant
- **Akzeptiert:** Tier 2–4. Die Kueche will Qualitaet, aber keine glowenden GVO-Monster auf dem Teller.
- **Preis-Logik:** `Basiswert x 1,4 x Reputations-Multiplikator`. Direktverkauf am Tisch
  zum Premium-Preis (knuepft an den bestehenden +40/Stk-Restaurant-Mechanismus an).
- **Kapazitaet/Tag:** mittel (~4 rel.) — nur so viele Tische, so viele Mae(u)ler.
- **Rep-Effekt:** **+** (macht die Stadt verrueckt nach dir, wie gehabt).
- **Gag:** Tagesgericht: "Garnele, dekonstruiert" — ist eine ganze Garnele, nur teurer.
- **Freischaltung:** `Shrimp-Restaurant` gebaut.

### Markt C — Export-Hafen
- **Akzeptiert:** Tier 3–6. Der internationale Luxusmarkt; Massenware lohnt den Container nicht.
- **Preis-Logik:** `Basiswert x 1,8 x Reputations-Multiplikator`. Bester **legaler**
  Preis fuer Spitzenware. Belohnt hohe Reputation ueberproportional.
- **Kapazitaet/Tag:** mittel-hoch (~7 rel.) — ein Schiff fasst viel, faehrt aber nicht taeglich.
- **Rep-Effekt:** **+ (Prestige).** "Exportweltmeister bei Garnelen" macht sich gut in der Presse.
- **Gag:** Zollformular Z-37b: "Inhalt? — Glueck in Schalenform."
- **Freischaltung:** Quest **"Containerschiff klarmachen"** (Akwanov-Handlungsstrang, Diplomatie & Logistik).

### Markt D — Militaer-Kontrakt (General Krillkill)
- **Akzeptiert:** Tier 1 **und** Tier 5. Genau diese zwei: billige Truppenverpflegung
  in Masse **plus** die grossen leuchtenden Glowrillas fuer "Sonderprojekte".
- **Preis-Logik:** **Festpreis 1,3x**, unabhaengig von Reputation. Der General feilscht nicht,
  der General **bestellt**. Planbares Einkommen, aber gedeckelt.
- **Kapazitaet/Tag:** sehr hoch (~15 rel.) — die Armee hat immer Hunger und ein Budget.
- **Rep-Effekt:** **--.** Mit dem Militaer im Bett zu liegen kommt bei der Oeffentlichkeit
  schlecht an. Verlaesslich, aber imagefeindlich.
- **Gag:** Krillkill: "Diese Garnelen marschieren NICHT. Ich will wissen, warum sie nicht marschieren."
- **Freischaltung:** General-Krillkill-Questreihe (schaltet zugleich Tier 5 mit frei).

### Markt E — Schwarzmarkt (Hafen-Hinterhof)
- **Akzeptiert:** Tier 4–6. Hier landen Hype-Drops, GVO-Ware und gestohlene Diamant-Krill.
  Fragen werden keine gestellt.
- **Preis-Logik:** `Basiswert x 2,2 x Volatilitaets-Wuerfel (0,7–1,6)`. Hoechste Margen,
  aber riskant — der Preis schwankt taeglich, manchmal kommt gar kein Kaeufer.
- **Kapazitaet/Tag:** niedrig (~3 rel.) — diskret bleibt diskret.
- **Rep-Effekt:** **--.** Wenn's rauskommt, war's das mit dem sauberen Image.
- **Gag:** Der Typ im Trenchcoat: "Krill-Diamant? Nie gehoert. Wie viele?"
- **Freischaltung:** Oeffnet sich bei **Reputation < 40** (Verzweiflung) ODER ueber den
  **Schmuggler-Kontakt** (Quest). Die elegante Loesung fuer Leute mit Geldsorgen und ohne Skrupel.

### Markt F — Usbekistan-Connection
- **Akzeptiert:** **alle** Tiers 1–6. Der grosse Joker-Markt (Hommage ans Original-Meme:
  binnenlaendisches Garnelen-Imperium).
- **Preis-Logik:** **Wuerfelmarkt.** Pro Liefertag wuerfelt der Markt einen Multiplikator
  zwischen **0,7x und 3,0x** und eine zufaellige Tier-Vorliebe ("Heute kauft Taschkent NUR Neon-Shrimps").
  Hoch-Risiko, Hoch-Belohnung — kann ein Glueckstreffer oder ein Reinfall sein.
- **Kapazitaet/Tag:** variabel (1–12 rel., wuerfelabhaengig).
- **Rep-Effekt:** **zufaellig** (-3 bis +3) — mal Held der Seidenstrasse, mal dubioser Exporteur.
- **Gag:** Akwanov am Telefon: "Mein Cousin in Buchara nimmt alles. Alles! ...meistens."
- **Freischaltung:** Akwanov-Quest **"Die Seidenstrasse der Shrimps"** (Diplomatie-Endgame).

---

## 3. Tier-x-Markt-Akzeptanzmatrix (auf einen Blick)

Legende: **Ja** = wird gekauft, **–** = abgelehnt.

| Tier \ Markt | Boerse | Restaurant | Export-Hafen | Militaer | Schwarzmarkt | Usbekistan |
|--------------|:------:|:----------:|:------------:|:--------:|:------------:|:----------:|
| 1 Feld/Wald/Wiese | Ja | – | – | Ja | – | Ja |
| 2 Bueroklammer | Ja | Ja | – | – | – | Ja |
| 3 Wagyu | – | Ja | Ja | – | – | Ja |
| 4 Neon-Hypebeast | – | Ja | Ja | – | Ja | Ja |
| 5 Glowrilla (GVO) | – | – | Ja | Ja | Ja | Ja |
| 6 Krill-Diamant | – | – | Ja | – | Ja | Ja |

**Kernlektion fuer den Spieler:** Wer auf hohe Tiers umsteigt, MUSS die passenden
Maerkte freischalten. Reine Boerse + Tier-6-Becken = volles Lager, leeres Konto.
Optimales Spiel heisst Portfolio: Massenware ueber Boerse/Militaer abdruecken,
Premium ueber Export-Hafen veredeln, Heisses diskret ueber Schwarzmarkt — und ab
und zu beim Usbekistan-Wuerfel sein Glueck versuchen.

---

## 4. Progressions-Loop (wie es sich anfuehlen soll)

1. **Frueh:** Nur Tier 1, nur Boerse. Geld ist knapp, alles ist grau-braun.
2. **Erstes Labor:** Tier 2 schaltet frei -> Restaurant lohnt sich ploetzlich.
3. **Mid-Game:** Quests bringen Tier 3 (Wagyu) + Export-Hafen — der erste echte Geldhebel,
   Reputation wird zur Waehrung.
4. **Hype-Phase:** Tier 4 (Neon) nach 25k + Viral-Event; volatil, aber lukrativ.
5. **Verlockung der dunklen Seite:** Tier 5 (GVO) + Militaer/Schwarzmarkt — viel Geld,
   Reputation broeckelt. Moralische Entscheidung.
6. **Endgame:** Tier 6 (Krill-Diamant) + Usbekistan-Joker. Du bist Shrimp-Tycoon,
   jetzt wirst du Shrimp-**Imperator**.

> "Am Ende geht es nicht um Garnelen. Es geht NIE um Garnelen." — Akwanov, melancholisch
