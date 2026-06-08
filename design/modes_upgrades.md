# Betriebs-Modi & Upgrades — Design v2

> "Eine Garnele ist nur so gut wie ihr Mittelmanagement." — General Krillkill, vermutlich

Dieses Dokument definiert **Betriebs-Modi** (umschaltbar, jederzeit, kostenlos) und **Upgrades**
(einmalig gekauft, dauerhaft) fuer alle 9 Gebaeude sowie globale **Arbeiter-Politiken** und
**Arbeiter-Upgrades**.

## Konventionen & Grundwerte (aus v1)

Alle relativen Prozentangaben beziehen sich auf die v1-Basiswerte pro Tick ("Tag"):

| Gebaeude | Basis-Output | Wichtigste Inputs | Upkeep | Rep/Tick |
|---|---|---|---|---|
| Schalentier-Kraftwerk | +45 Strom | — | 8 | 0 (eigentlich rep-negativ im Flavour) |
| Solar-Dach | +28 Strom | — | 1 | +0 (kleiner Bonus geplant) |
| Wasserwerk | +14 Wasser | 8 Strom | 3 | 0 |
| Algen-Futterfarm | +9 Futter | 6 Wasser, 2 Strom | 3 | 0 |
| Shrimp-Becken | +5 Shrimps | 10 Wasser, 4 Futter, 2 Arbeiter | 4 | 0 |
| Arbeiter-Wohnheim | +6 Arbeiter | 4 Wasser | 2 | 0 |
| Shrimp-Boerse | -7 Shrimps -> Geld (22/Stk) | 5 Wasser, 2 Arb. | 5 | 0 |
| Forschungslabor | +12% Verkaufspreis (farmweit) | 10 Wasser, 2 Arb. | 7 | +0,05 |
| Shrimp-Restaurant | -3 Shrimps -> Geld (+40/Stk) | 6 Wasser, 2 Arb. | 6 | +0,15 |

**Modi-Regeln (allgemein):**
- Jedes Gebaeude hat genau **einen aktiven Modus**. Default ist immer der **erste** ("Normalbetrieb").
- Modi sind **kostenlos & sofort** umschaltbar (Trade-offs statt Kosten).
- Upgrades kosten **relativ zum Baupreis** (z.B. "0,6x Baupreis") und sind permanent.
- **[FARMWEIT]** = wirkt auf ALLE Gebaeude/die ganze Farm.
- **[KETTE]** = beeinflusst gezielt ANDERE Gebaeude-Typen oder Arbeiter.

Wechselwirkungen mit **Reputation** sind wichtig: Reputation (0-100) skaliert den Verkaufspreis
0,6x-1,4x. Wer also Rep verbrennt, verkauft billiger — Modi/Upgrades, die Rep kosten, zahlen sich
nur bei hohem Durchsatz aus.

---

## 1. Schalentier-Kraftwerk

*Verbrennt Schalen und schlechte Laune zu Strom. Die Aktivisten haben deine Adresse schon notiert.*

### Modi
1. **Normalbetrieb** — +45 Strom, Upkeep 8. Riecht nach verbranntem Meer, aber laeuft.
2. **Vollast ("Krillkill-Knopf")** — *+40% Strom (+63), +30% Upkeep, -0,15 Rep/Tick [FARMWEIT-Rep].*
   Der General liebt diesen Knopf. Die Veganer-Demo vor dem Werkstor auch (als Motiv).
3. **Drosselbetrieb** — *-35% Strom (+29), -50% Upkeep, +0,05 Rep/Tick.*
   "Wir tun ja was fuer die Umwelt." Tust du nicht, aber es zaehlt der Gedanke.

### Upgrades
- **Schalen-Rueckgewinnung** (0,5x Baupreis) — Recycelt Shrimp-Schalen aus Boerse & Restaurant:
  *-25% Upkeep* und *+0,1 Rep/Tick*. **[KETTE]** Skaliert mit Anzahl Boersen/Restaurants
  (mehr Verkaufsstellen = mehr Schalen = mehr Sparen).
- **Filteranlage "Saubere Suende"** (0,8x Baupreis) — *Neutralisiert den Rep-Malus aller Kraftwerke*
  **[FARMWEIT]** und gibt jedem Kraftwerk *+0,1 Rep/Tick*. Macht den Vollast-Modus politisch tragbar.

---

## 2. Solar-Dach

*Sauberer Strom vom Dach. Funktioniert auch in der Halle. Frag nicht wie.*

### Modi
1. **Sonnenanbeter (Normal)** — +28 Strom, Upkeep 1, +0,03 Rep/Tick. Brav und gruen.
2. **Spiegel-Overdrive** — *+25% Strom (+35), -0,02 Rep/Tick (Nachbarn beschweren sich ueber Blendung),*
   Upkeep 2. Die Halle ist jetzt auch von der ISS sichtbar.
3. **Eco-Schaufenster** — *-20% Strom (+22), +0,12 Rep/Tick [FARMWEIT-Rep], Upkeep 1.*
   Reiner PR-Modus: weniger Watt, mehr "Wir retten den Planeten"-Plakate.

### Upgrades
- **Akku-Bank** (0,7x Baupreis) — Speichert Stromspitzen: *gleicht Strom-Schwankungen aus* und gibt
  *+10% effektiven Strom* bei Nacht-Events. Verhindert Black-out-Events ("Praktikant verwechselt die Schalter").
- **Gruener-Strom-Zertifikat** (0,4x Baupreis) — **[FARMWEIT]** *+0,08 Rep/Tick pro Solar-Dach.*
  Zertifiziert von einer Behoerde, die es vielleicht gibt. Stapelbar = "100% Oeko-Farm"-PR.

---

## 3. Wasserwerk

*Filtert, salzt und temperiert H2O zu Shrimp-Wohlfuehlwasser.*

### Modi
1. **Standard-Spuelung** — +14 Wasser, 8 Strom, Upkeep 3.
2. **Tiefseepumpe** — *+45% Wasser (+20), +50% Stromverbrauch (12 Strom), Upkeep 4.*
   Foerdert Wasser aus Tiefen, in denen selbst Shrimps nervoes werden.
3. **Spar-Kreislauf** — *-30% Wasser (+10), -40% Strom (5 Strom), +0,05 Rep/Tick.*
   Recycelt Beckenwasser. Schmeckt die Shrimps nicht, aber sie beschweren sich nicht.

### Upgrades
- **Umkehrosmose-Modul** (0,6x Baupreis) — *+20% Wasser* und entfernt Stromkosten-Strafe der Tiefseepumpe.
- **Wasser-Verbund-Leitung** (0,9x Baupreis) — **[KETTE]** Versorgt Algen-Farmen & Becken direkt:
  *-15% Wasserverbrauch aller Algen-Futterfarmen und Shrimp-Becken* **[FARMWEIT]**. Die wahre Infrastruktur-Magie.

---

## 4. Algen-Futterfarm

*Zuechtet Algen als Shrimp-Buffet. Riecht wie ein Aquarium am Montag.*

### Modi
1. **Bio-Buffet (Normal)** — +9 Futter, 6 Wasser, 2 Strom, Upkeep 3.
2. **Hochdruck-Photosynthese** — *+50% Futter (+14), +60% Strom (3,2), +30% Wasser (7,8).*
   Algen auf Energydrink. Wachsen so schnell, dass man ihnen beim Wachsen zuhoeren kann.
3. **Gourmet-Algen** — *-25% Futter (+6,8), aber **[KETTE]** speist Becken mit Premium-Futter:
   +1 Shrimp-Tier-Qualitaet im belieferten Becken*, +0,05 Rep/Tick. Fuer den anspruchsvollen Shrimp.

### Upgrades
- **Naehrstoff-Rezeptur "Algae+"** (0,5x Baupreis) — *+15% Futter* und **[KETTE]** *+5% Shrimp-Produktion*
  in allen Becken (besser gefuetterte Shrimps wachsen schneller) **[FARMWEIT]**.
- **Geschlossenes Becken** (0,7x Baupreis) — *-30% Wasserverbrauch*, *+0,08 Rep/Tick* (kein Gestank mehr,
  die Montags-Aquarium-Beschwerden verstummen).

---

## 5. Shrimp-Becken

*Das Herz der Farm. Wandelt Wasser + Futter + Strom in kleine BWL-Absolventen.*

### Modi
1. **Wohlfuehl-Becken (Normal)** — +5 Shrimps, 10 Wasser, 4 Futter, 2 Arbeiter, Upkeep 4.
2. **Mast-Modus ("Akkord")** — *+35% Shrimps (+6,75), +40% Futter (5,6), +30% Wasser (13),
   -1 Shrimp-Tier-Qualitaet [KETTE: Maerkte].* Quantitaet schlaegt Qualitaet. Massenware.
3. **Premium-Zucht ("Romantische Beleuchtung")** — *-20% Shrimps (+4), +1 Shrimp-Tier-Qualitaet,
   +0,1 Rep/Tick.* Beckenromantik wirkt: Shrimp-Babyboom in Edel-Qualitaet, die nur Restaurants & Edel-Maerkte kaufen.
4. **Brut-Modus** — *Produziert 0 verkaufbare Shrimps, aber +2 Arbeiter-Aequivalent in Pflege-Output
   [KETTE]* — eigentlich: erhoeht Output benachbarter Becken um +10% (Zucht-Synergie). Optionaler 4. Modus.

### Upgrades
- **Automatik-Fuetterer** (0,6x Baupreis) — *-1 Arbeiterbedarf* (1 statt 2). **[KETTE]** Entlastet den
  Arbeitsmarkt farmweit — wertvoll bei Arbeitermangel.
- **Genetik-Boost-Kit** (1,0x Baupreis) — *+1 Shrimp-Tier-Qualitaet permanent* und *+10% Shrimps.*
  Braucht ein Forschungslabor auf der Farm (sonst gesperrt) **[KETTE]**.

---

## 6. Arbeiter-Wohnheim

*Gemuetliche Kojen mit Meerblick-Tapete und WLAN, das nur an guten Tagen funktioniert.*

### Modi
1. **Standard-Kojen (Normal)** — +6 Arbeiter, 4 Wasser, Upkeep 2.
2. **Kapsel-Hotel ("Effizienz")** — *+40% Arbeiter (+8), +0 Wasser-Bonus, -0,08 Rep/Tick
   [FARMWEIT-Rep].* Eng, aber viele. Die Gewerkschaft hat Fragen.
3. **Wellness-Resort** — *-20% Arbeiter (+5), +6 Wasser, +0,15 Rep/Tick [FARMWEIT-Rep],
   +5% Arbeiter-Effizienz [FARMWEIT].* Zufriedene Arbeiter, glueckliche Shrimps, gute Presse.

### Upgrades
- **Werks-Kantine** (0,8x Baupreis) — **[FARMWEIT]** *+8% Arbeiter-Effizienz* (mehr Output pro Arbeiter
  in allen Gebaeuden). Das Shrimp-Buffet ist natuerlich gratis.
- **Mitarbeiter-App "ShrimpChat"** (0,5x Baupreis) — *Reduziert Personal-Negativ-Events* (Streiks,
  Krankmeldungen) **[FARMWEIT]** und gibt *+0,05 Rep/Tick*. Push-Nachrichten inklusive.

---

## 7. Shrimp-Boerse

*Verkauft Shrimps an den Weltmarkt. 'Buy low, fry high.'*

### Modi
1. **Sichere Bank (Normal)** — verkauft 7 Shrimps/Tick zu 22/Stk (rep-skaliert), Upkeep 5.
   Kauft nur Standard-Tier.
2. **Day-Trading ("Zock")** — *Verkaufsmenge +30% (9), Preis schwankt +/-25% pro Tick
   (Zufall), Upkeep +50%.* Hochrisiko. Der Shrimp-DAX kennt nur zwei Richtungen: beide.
3. **Termingeschaeft ("Stabil")** — *-15% Menge (6), aber fixer Preis (keine Schwankung) und
   immun gegen Markt-Crash-Events.* Fuer Buchhalter mit Herzschwaeche.

### Upgrades
- **Hochfrequenz-Terminal** (0,9x Baupreis) — *+40% Verkaufsmenge* (verkauft mehr Shrimps/Tick).
  Blinkt aggressiv. Niemand versteht es, aber es verdient Geld.
- **Marken-Kampagne "Shrimp-Influencer"** (0,7x Baupreis) — **[FARMWEIT]** *+0,12 Rep/Tick* solange
  ein Verkauf laeuft. Dein Meme geht viral — und damit dein Verkaufspreis nach oben.

---

## 8. Forschungslabor

*Optimiert Genetik, Geschmack und Glanz. Jedes Labor hebt den Verkaufspreis dauerhaft.*

### Modi
1. **Grundlagenforschung (Normal)** — passiver +12% Verkaufspreis-Bonus [FARMWEIT], +0,05 Rep/Tick, Upkeep 7.
2. **Marktforschung ("Tier-Programm")** — *Verkaufspreis-Bonus -> +6%, dafuer +1 Shrimp-Tier-Qualitaet
   in allen Becken [FARMWEIT].* Forscht an besseren Shrimps statt besseren Preisen.
3. **PR-Labor ("Weisskittel-Theater")** — *+0,20 Rep/Tick [FARMWEIT-Rep], -50% Preis-Bonus (+6%),
   Upkeep -2.* Forscht hauptsaechlich an Pressefotos mit Reagenzglaesern.

### Upgrades
- **Supercomputer "DeepKrill"** (1,2x Baupreis) — *Preis-Bonus pro Labor +12% -> +18%* **[FARMWEIT]**
  und schaltet **Genetik-Boost-Kit** (Becken-Upgrade) frei. Rechnet die Shrimps schoen.
- **Open-Science-Initiative** (0,6x Baupreis) — **[FARMWEIT]** *-10% Upkeep aller Labore und
  Algen-Farmen [KETTE]* plus *+0,1 Rep/Tick*. Geteiltes Wissen, geteilte Stromrechnung.

---

## 9. Shrimp-Restaurant

*Serviert Shrimps zum Premium-Preis (+40/Stk) und macht die Stadt verrueckt nach dir.*

### Modi
1. **Bistro (Normal)** — verbraucht 3 Shrimps/Tick, +40/Stk, +0,15 Rep/Tick, Upkeep 6.
   Akzeptiert Standard-Tier.
2. **Fine Dining ("Michelin-Jagd")** — *-1 Shrimp/Tick (2), aber +100/Stk, +0,3 Rep/Tick
   [FARMWEIT-Rep]. Verlangt Premium-Tier-Shrimps [KETTE]* — ohne Premium-Becken laeuft es leer.
3. **All-you-can-Shrimp ("Volksfest")** — *+2 Shrimps/Tick (5), -50% Preis/Stk (+20), -0,05 Rep/Tick
   (Massenabfertigung).* Schlange bis zum Parkplatz, Bewertungen durchwachsen.

### Upgrades
- **Stern-Koch "Gordon Krill"** (1,1x Baupreis) — *+50% Preis/Stk* und **[FARMWEIT]** *+0,1 Rep/Tick.*
  Bruellt die Shrimps an, bis sie perfekt sind.
- **Lieferdienst "ShrimpEats"** (0,7x Baupreis) — *+2 Shrimps/Tick Durchsatz* ohne Sitzplatz-Limit.
  **[KETTE]** Profitiert von Boersen-Influencer-Kampagne (geteilte Reichweite).

---

## Arbeiter — Globale Politiken (Modi)

Genau **eine** Politik ist farmweit aktiv. Default: **Tarifvertrag**. Kostenlos umschaltbar,
aber mit Cooldown (haeufiges Wechseln demoralisiert die Belegschaft — geplanter Anti-Cheese-Mechanismus).

1. **Tarifvertrag (Normal)** — Basis-Effizienz, Basis-Lohnkosten, Rep neutral. Alles brav nach Vorschrift.
2. **Ueberstunden-Offensive** — *+25% Arbeiter-Effizienz [FARMWEIT], +30% Lohn-Upkeep,
   -0,1 Rep/Tick [FARMWEIT-Rep].* Risiko: Burnout-Event ("Halbe Belegschaft krank") steigt.
   Kurzfristiger Boost, langfristig teuer.
3. **Drei-Schicht-Betrieb** — *Gebaeude laufen rund um die Uhr: +15% Output ALLER Produktionsgebaeude
   [FARMWEIT], +20% Stromverbrauch farmweit [KETTE: Kraftwerke].* Die Halle schlaeft nie. Die Shrimps auch nicht.
4. **Gewerkschaftsfreundlich ("Krill mit Herz")** — *-10% Effizienz, -15% Lohn-Upkeep
   (weniger Fluktuation), +0,2 Rep/Tick [FARMWEIT-Rep], immun gegen Streik-Events.*
   Aussenminister Akwanov nickt zufrieden. Die Presse liebt dich.

---

## Arbeiter — Upgrades (global, einmalig)

- **Onboarding-Programm "Welcome to Shrimptopia"** (mittlere Kosten) — **[FARMWEIT]** *+10% Arbeiter-
  Effizienz*, *-25% Einarbeitungszeit neuer Arbeiter*. Neue Wohnheime liefern sofort vollen Output.
- **Krillkill-Bootcamp** (hohe Kosten) — **[FARMWEIT]** *+20% Effizienz, +5% Output aller Becken & Kraftwerke
  [KETTE], aber -0,15 Rep/Tick [FARMWEIT-Rep]* (Drill-Sergeant-Methoden). General Krillkill persoenlich
  bruellt deine Arbeiter zu Hoechstleistungen. Wer ueberlebt, ist effizient.
- **Akwanov-Akademie ("Soft Skills fuer harte Shrimps")** (hohe Kosten) — **[FARMWEIT]** *+12% Effizienz,
  +0,15 Rep/Tick [FARMWEIT-Rep], halbiert Personal-Negativ-Events.* Gegenpol zum Bootcamp:
  diplomatisch, teuer, beliebt. Schliesst sich mit aktivem Bootcamp-Drill thematisch nicht aus, aber die
  beiden Charaktere reden danach nicht mehr miteinander (Flavour/Quest-Hook).

---

## Synergien & Build-Archetypen (Designer-Notiz)

- **Eco-Tycoon:** Solar + Eco-Schaufenster + Gewerkschaftsfreundlich + PR-Labor -> Reputation explodiert
  -> 1,4x Verkaufspreis trott kleinerer Mengen. Verkauft wenig, aber teuer.
- **Massenproduktion:** Kraftwerk-Vollast + Becken-Mast + Drei-Schicht + Hochfrequenz-Boerse ->
  riesiger Durchsatz, mieser Tier, niedrige Rep -> lebt von schierer Menge. Anti-Synergie mit Restaurants.
- **Premium-Manufaktur:** Gourmet-Algen + Premium-Becken + Genetik-Boost + Fine-Dining-Restaurant ->
  hohe Tiers fuer Edel-Maerkte, kleine Mengen, fette Margen.
- **Charakter-Spannung:** Krillkill-Bootcamp (Effizienz, Rep-Kosten) vs. Akwanov-Akademie (Rep, Diplomatie)
  als wiederkehrender narrativer Konflikt — koppelbar an die Quest-Straenge.
