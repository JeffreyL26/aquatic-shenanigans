# ShrimpTopia v2 - Zonen & Gebaeude

> "Eine Halle ist keine Halle. Eine Halle ist ein Imperium mit schlechter Lueftung."
> - Vorwort des HQ-Handbuchs, Kapitel 1, das niemand liest.

Dieses Dokument macht die Farm **raeumlich abwechslungsreich**: vier thematische Zonen statt
einem eintoenigen Grid, dazu sieben neue Gebaeude, die an Shrimp-Tiers, Maerkte und die beiden
Charaktere (General Krillkill, Aussenminister Akwanov) andocken. Alle Optik-Ideen sind in
**Java2D-Vektor** umsetzbar - keine externen Assets, alles aus Rechtecken, Ovalen, Gradients
und ein bisschen `sin()`.

---

## 0. Das grosse Bild: Vom Grid zur Anlage

**Heute:** ein 12x8-Schachbrett (`GameState.COLS=12`, `ROWS=8`), zwei Bodenkacheln, alles grau.

**v2:** dieselbe Karte, aber in **vier Zonen aufgeteilt** - jede Zone ist ein farblich
und stimmungsmaessig eigener Bereich. Zonen werden **progressiv freigeschaltet** (Quests/Geld),
sodass die Farm waehrend des Spiels sichtbar waechst, statt von Anfang an leer und gross zu wirken.

Vorschlag fuer das Zonen-Layout auf dem 12x8-Raster (Spalten c, Zeilen r):

```
   c0          c3 c4          c7 c8          c11
 +----------------+----------------+----------------+
 |  ZONE A        |   ZONE B       |   ZONE C       |  r0
 |  Produktions-  |   Becken-      |   Handels-     |  r1
 |  halle         |   Galerie      |   promenade    |  r2
 |  (Versorgung)  |   (Aufzucht)   |   (Verkauf)    |  r3
 +----------------+----------------+----------------+
 |                ZONE D                            |  r4
 |        Untergeschoss / "Die Tiefe"               |  r5
 |     (Forschung, Militaer, Genlabor, geheim)      |  r6
 |                                                  |  r7
 +--------------------------------------------------+
```

Technisch: eine `Zone`-Enum + ein `int[ROWS][COLS]`-Zonen-Lookup (oder Rechteck-Grenzen).
Jede Zone liefert: eigene Bodenfarben, eigenen Hintergrund-Gradient, ein paar Ambient-Layer
und (optional) eine Liste erlaubter Gebaeudetypen. Gebaeude lassen sich nur in passenden Zonen
bauen -> raeumliche Logik **und** ein sanfter Bau-Leitfaden in einem.

---

## 1. ZONE A - Die Produktionshalle ("Maschinenraum")

**Thema:** Versorgung & Infrastruktur. Hier brummt, zischt und tropft es. Der unromantische,
ehrliche Teil der Farm, wo Strom, Wasser und Algen-Futter entstehen.

**Optik / Hintergrund:**
- Boden: dunkles Industriegrau-Blau (`FLOOR_A/B` leicht abgedunkelt, +Oel-Flecken als
  halbtransparente Ovale).
- Hintergrund-Layer: Rohrleitungen (dicke gerundete Linien quer ueber die Wand), Ventilraeder
  (Kreise mit Speichen), ein paar gelbe Warn-Streifen-Dreiecke an den Raendern.
- Lichtstimmung: kalt, leicht gruenstichig - Leuchtstoffroehren-Feeling.

**Ambiente & Animation (Java2D):**
- **Dampf-Puffs:** alle paar Ticks steigt aus Kraftwerk/Wasserwerk ein wachsendes,
  ausblassendes Oval auf (`alpha` faellt, `y` sinkt, `radius` waechst).
- **Pulsierende Pipes:** Rohrleitungen bekommen einen wandernden Helligkeits-Hotspot
  (`sin(t)`-Phase pro Rohr), wirkt wie fliessende Fluessigkeit.
- **Drehende Ventilraeder:** Rotation = `time * speed`, schneller wenn das Gebaeude darunter
  hohe Effizienz hat - kaputte Anlage = Rad steht still (toller Status-Hint ohne Text).
- **Funken** beim Kraftwerk (kurze gelbe Striche, wenn `efficiency > 0.85`).

**Gebaeude hier:** Schalentier-Kraftwerk, Solar-Dach (an der Hallenwand/Dach gerendert),
Wasserwerk, Algen-Futterfarm - plus die neuen **Wasseraufbereitungs-Hub** und **Bio-Reaktor**.

---

## 2. ZONE B - Die Becken-Galerie ("Schaubecken")

**Thema:** Aufzucht & Herzstueck. Die schoene Zone, die man Besuchern zeigt. Hier leben die
Shrimps, hier entstehen die Tiers, hier ist die romantische Beleuchtung (siehe: Babyboom-Event).

**Optik / Hintergrund:**
- Boden: feuchtes Tuerkis (`ACCENT` als Basis, dunkler), nasser Glanz.
- Becken werden als **leuchtende Glas-Aquarien** gerendert: Gradient von hell (oben) nach
  tief-blau (unten), feiner weisser Rand = Glaskante.

**Ambiente & Animation (Java2D):**
- **Schwimmende Shrimps:** pro Becken N kleine orange Shrimp-Glyphen (gibt es schon in
  `Icons`), die auf Sinus-Bahnen durchs Becken treiben; N skaliert mit `shrimpProduce`/Effizienz.
  Tier-3-Becken haben goldene Shrimps mit kleinem Glitzer-Punkt.
- **Wasser-Kaustik:** wandernde helle Schlieren auf dem Beckenboden - ein paar ueberlagerte
  `sin`-Wellen als hellblaue, halbtransparente Polygone/Linien. Billig, sieht teuer aus.
- **Aufsteigende Luftblasen:** kleine weisse Kreischen, die nach oben wandern und am
  Rand "platzen" (kurz groesser, dann weg).
- **Romantische Beleuchtung:** baubarer Modus (rosa Glow ums Becken) -> Anspielung aufs
  Babyboom-Event, +Shrimp-Wachstum, -ein bisschen Wuerde.
- **Tag/Nacht:** ein globaler `dayPhase`-Wert (z.B. an `gs.getDay()` gekoppelt) zieht einen
  warmen->kalten Overlay-Gradient ueber die ganze Karte; nachts leuchten die Becken staerker.

**Gebaeude hier:** Shrimp-Becken (in mehreren Tier-Ausbaustufen) plus das neue
**Premium-Zuchtbecken** und das **Genlabor** (Tier-Veredelung).

---

## 3. ZONE C - Die Handelspromenade ("Schaufenster")

**Thema:** Verkauf, Reputation, Oeffentlichkeit. Glaenzend, laut, kommerziell. Hier kommen
Besucher rein, hier verkauft Aussenminister Akwanov deine Shrimps an die Welt.

**Optik / Hintergrund:**
- Boden: heller, polierter Stein (waermere Grautoene, dezente Reflexion).
- Hintergrund: grosse Schaufenster-Glasfront mit "Aussenwelt" (ein simpler Stadt-Silhouetten-
  Streifen aus Rechtecken am Horizont + Wolken-Ovale, die langsam driften).
- Leuchtreklame: ein blinkendes "SHRIMPS" Schild (Buchstaben, deren Alpha im Takt pulsiert).

**Ambiente & Animation (Java2D):**
- **Besucher-Punkte:** kleine Personen-Glyphen (gibt es schon: `PERSON`), die entlang der
  Promenade von Tile zu Tile schlendern (lineare Interpolation zwischen Wegpunkten). Mehr
  Besucher bei hoher Reputation -> die Zone wird sichtbar voller, je besser du wirtschaftest.
- **Boersen-Ticker:** ein horizontal scrollender Text-Streifen am oberen Rand der Zone
  ("SHRMP +2,4% | AKWANOV-DEAL UNTERZEICHNET | ..."), reine `drawString`-Animation.
- **Muenz-Pop:** bei jedem Verkauf steigt ein kleines gelbes `COIN`-Glyph ueber der Boerse
  auf und verblasst (klassisches Tycoon-Feedback).
- **Wetter durchs Fenster:** gelegentlich Regen (fallende blaue Striche hinter dem Glas) oder
  Sonne (warmer Lichtkegel) - rein kosmetisch, koppelbar an Events.

**Gebaeude hier:** Shrimp-Boerse, Shrimp-Restaurant plus die neuen **Export-Hafen**,
**Besucherzentrum** und (Akwanovs Liebling) das **Botschafts-Buero**.

---

## 4. ZONE D - Das Untergeschoss ("Die Tiefe")

**Thema:** Forschung, Militaer und Dinge, ueber die man nicht spricht. Spaeter freigeschaltet.
General Krillkills Revier. Dunkel, klandestin, leicht paranoid.

**Optik / Hintergrund:**
- Boden: fast schwarz mit gruenem Scan-Linien-Raster (CRT/Bunker-Aesthetik).
- Hintergrund: Beton, Notbeleuchtung (rote Punkte), Sandsaecke und Aktenschraenke als Deko-Rechtecke.
- Eine Stahltuer mit "ZUTRITT NUR MIT FREIGABE" als Tile, solange die Zone gesperrt ist.

**Ambiente & Animation (Java2D):**
- **Radar-Sweep:** ein rotierender gruener Keil (Pac-Man-Sektor) im Militaer-Depot, dazu
  aufblinkende "Kontakt"-Punkte.
- **Blinkende Notbeleuchtung:** rote Punkte, die langsam pulsen (`abs(sin)`).
- **Scan-Linie:** eine horizontale, halbtransparente helle Linie, die ueber die ganze Zone
  nach unten wandert und oben neu startet - billiger Hightech-Look.
- **Glas-Reagenz-Blubbern** im Genlabor (gruene Blasen in schmalen Zylindern).

**Gebaeude hier:** Forschungslabor plus **Militaer-Depot** (Krillkill) und das **Genlabor**
(falls man es lieber hier unten statt in Zone B verortet - Designer-Entscheidung).

---

## 5. Sieben neue Gebaeude

Werte sind grob und an die bestehende Skala angelehnt (Kosten 300-1100, Produktion einstellig
bis mittel, Ziel 50.000). Spalten in der Reihenfolge der `BuildingType`-Felder gedacht.

> Tier-Logik (v2-Annahme): Shrimps haben **Tier 1-3** (Standard / Premium / Luxus). Maerkte
> kaufen nur bestimmte Tiers. Veredelung hebt Tier; nur bestimmte Maerkte zahlen den Tier-Aufschlag.

### 5.1 Wasseraufbereitungs-Hub *(Zone A)*
- **Funktion:** Recycling. Gibt einen Teil des in Becken/Algen verbrauchten Wassers zurueck
  (z.B. +25% Wasser-Effizienz im Umkreis bzw. global) und liefert selbst etwas Wasser.
- **Werte:** Kosten ~750. powerUse 2, waterProduce +10, dazu ein **Recycling-Bonus**
  (reduziert effektiven `waterUse` aller Becken um 20%). upkeep 4. Kleiner Reputations-Bonus
  (oeko!).
- **Freischaltung:** ab 2 gebauten Wasserwerken **oder** Quest "Trockenlegung" abgeschlossen.
- **Gag:** "Filtert das Wasser so gruendlich, dass die Shrimps sich beschweren, es schmecke
  'nach gar nichts'. Premium-Problem."

### 5.2 Bio-Reaktor *(Zone A)*
- **Funktion:** Verwandelt Shrimp-Schalen-Abfall + Algen in **Premium-Futter** und etwas Biogas
  (=Strom). Spaetgame-Versorgung, ersetzt mehrere Algenfarmen.
- **Werte:** Kosten ~900. feedProduce +16, powerProduce +6, waterUse 4, workerNeed 1, upkeep 5.
  Premium-Futter -> kleiner Tier-Bonus fuer Becken im Umkreis.
- **Freischaltung:** Forschungslabor gebaut **+** 5.000 Geld erreicht.
- **Gag:** "Aus Resten wird Reichtum. Riecht wie das Innere eines Turnschuhs, aber die Shrimps
  lieben es. Vegan zertifiziert (die Algen haben nicht widersprochen)."

### 5.3 Premium-Zuchtbecken *(Zone B)*
- **Funktion:** Wie ein Shrimp-Becken, produziert aber **Tier-2-Shrimps** (Premium) - mehr Wert,
  aber hungriger. Pflicht-Lieferant fuers Restaurant und den Export.
- **Werte:** Kosten ~850. waterUse 14, feedUse 6, powerUse 3, shrimpProduce +5 **(Tier 2)**,
  workerNeed 1, upkeep 6. Profitiert ueberproportional vom Genlabor.
- **Freischaltung:** ab 3 normalen Becken **oder** erstes Labor gebaut.
- **Gag:** "Hier wachsen Shrimps mit Anspruch. Eigene Playlist, gefiltertes Wasser,
  Achtsamkeits-Coach. Der Wuerfel pro Stueck kostet mehr als dein erstes Auto."

### 5.4 Genlabor *(Zone B oder D)*
- **Funktion:** **Tier-Veredelung.** Hebt einen Teil deiner Tier-1/2-Shrimps pro Tick auf das
  naechste Tier. Macht aus "Massenware" "Luxus" (Tier 3). Schaltet Tier-3-Maerkte ueberhaupt
  erst sinnvoll frei.
- **Werte:** Kosten ~1100. powerUse 6, waterUse 4, workerNeed 2, upkeep 8. Wandelt z.B.
  bis zu 4 Shrimps/Tick eine Tier-Stufe hoch. repProduce klein (kontrovers...).
- **Freischaltung:** Forschungslabor + Quest "CRISPR-Shrimp" (Krillkill ist begeistert,
  Akwanov besorgt).
- **Gag:** "Wir verbessern nichts, was nicht verbessert werden muss. Ausser dem Geschmack.
  Und der Groesse. Und dem leichten Leuchten im Dunkeln. Voellig unbedenklich."

### 5.5 Export-Hafen *(Zone C)*
- **Funktion:** Grosshandel-Verkauf, **kauft nur Tier 2-3**, aber in grosser Menge und zu hohem
  Preis. Akwanovs Aussenhandels-Kanal. Verkaufspreis stark reputationsabhaengig.
- **Werte:** Kosten ~1000. shrimpSell +12 (nur Tier>=2), sellPrice ~30 (x Reputations-Multi),
  powerUse 2, workerNeed 2, upkeep 6.
- **Freischaltung:** Botschafts-Buero gebaut **oder** Reputation > 60.
- **Gag:** "Container voller Shrimps gehen in 40 Laender. In 39 davon nennt man sie 'Delikatesse',
  in einem 'Beweismittel'. Akwanov regelt das."

### 5.6 Besucherzentrum *(Zone C)*
- **Funktion:** **Reputations-Maschine.** Verkauft keine Shrimps direkt, generiert aber stetig
  Reputation + etwas Eintrittsgeld und lockt die Besucher-Animation in Zone C.
- **Werte:** Kosten ~600. repProduce +0.15, addMoney klein (Tickets), powerUse 2, workerNeed 1,
  upkeep 3.
- **Freischaltung:** ab Reputation > 40 **oder** erstes Restaurant gebaut.
- **Gag:** "Mit Streichelbecken (Aufsicht empfohlen), Gift-Shop und der weltgroessten
  Shrimp-aus-Lego-Nachbildung. Schulklassen lieben es. Die Shrimps weniger."

### 5.7 Militaer-Depot *(Zone D)*
- **Funktion:** **General Krillkills** Beitrag. Schuetzt vor Negativ-Events (Moewen-Einbruch,
  Diebstahl) und verkauft "Notrationen" (Tier-1-Shrimps) an die Armee zu festem, krisensicherem
  Preis - unabhaengig von Reputation. Defensive Einnahmequelle.
- **Werte:** Kosten ~950. shrimpSell +8 (nur Tier 1, **fester** Preis ~18, kein Rep-Multi),
  powerUse 2, workerNeed 2, upkeep 7. Passiv: reduziert Schaden negativer Events um ~50%.
- **Freischaltung:** Zone D freigeschaltet + Quest "General an Bord".
- **Gag:** "General Krillkill: 'Jede Garnele ist ein Soldat. Jedes Becken eine Festung.'
  Verkauft Shrimp-Dosen mit 30 Jahren Haltbarkeit. Geschmack: 'funktional'."

### Mini-Tabelle (Uebersicht)

| Gebaeude | Zone | Kosten | Kernwert | Schaltet frei bei | Tier/Markt |
|---|---|---|---|---|---|
| Wasseraufbereitungs-Hub | A | ~750 | +10 Wasser, -20% Becken-Verbrauch | 2x Wasserwerk / Quest | - |
| Bio-Reaktor | A | ~900 | +16 Futter, +6 Strom | Labor + 5k Geld | Premium-Futter-Bonus |
| Premium-Zuchtbecken | B | ~850 | +5 Shrimps (Tier 2) | 3x Becken / 1. Labor | erzeugt Tier 2 |
| Genlabor | B/D | ~1100 | hebt 4 Shrimps/Tick ein Tier | Labor + Quest | erzeugt Tier 3 |
| Export-Hafen | C | ~1000 | +12 Verkauf, Preis ~30 x Rep | Botschaft / Rep>60 | kauft Tier 2-3 |
| Besucherzentrum | C | ~600 | +Reputation + Tickets | Rep>40 / Restaurant | - |
| Militaer-Depot | D | ~950 | +8 Verkauf fest, Event-Schutz | Zone D + Quest | kauft Tier 1 |

---

## 6. Konkrete, leicht umsetzbare Ideen fuer ein lebendigeres Grid (Java2D-Vektor)

Alles ohne Assets, nur Math + `Graphics2D`. Geordnet von "fast geschenkt" bis "etwas Arbeit".

**Quick Wins (1 Methode, < 1h):**
1. **Zonen-Boden:** statt zwei globaler Bodenfarben pro Zone eigene `FLOOR_A/B` - sofort
   raeumliche Gliederung. Nur ein Zonen-Lookup im Boden-Loop noetig.
2. **Zonen-Trennlinien:** dickere, leicht leuchtende Linien (`ACCENT`, alpha) zwischen den
   Zonen + ein dezentes Zonen-Label (`drawString`) oben links in jeder Zone.
3. **Vignette:** radialer Dunkel-Gradient ueber die ganze Karte -> Tiefe, lenkt den Blick zur Mitte.
4. **Boden-"Pfuetzen"/Flecken:** ein paar fixe, halbtransparente Ovale pro Zone (seeded `Random`,
   damit sie konstant bleiben) - bricht die Sterilitaet.

**Animation per globalem `tick`/`time` (eine `Timer`-getriebene `repaint`-Schleife reicht):**
5. **Schwimmende Shrimps in Becken** (Zone B) - `sin`-Bahnen, Anzahl = f(Effizienz). Groesster
   Wow-Effekt pro Codezeile.
6. **Luftblasen** in Becken - Liste kleiner Partikel, `y -= speed`, reset oben.
7. **Wasser-Kaustik** - 2-3 ueberlagerte verschobene `sin`-Linien als helle, halbtransparente
   Polylines auf dem Beckenboden.
8. **Dampf/Funken** an Versorgungsgebaeuden (Zone A) - aufsteigende, ausblassende Ovale.
9. **Drehende Ventilraeder / Radar-Sweep** - `AffineTransform.rotate(time*speed)`, Speed an
   Effizienz koppeln (steht still = kaputt). Doppelt als Status-Anzeige.
10. **Status-Puls statt statischem Punkt:** der vorhandene gruen/gelb/rot-Statuspunkt
    (`MapPanel.drawBuilding`) pulst leicht (`alpha = 0.6 + 0.4*sin`). Aufmerksamkeit ohne Laerm.

**Welt-Atmosphaere (globale Overlays):**
11. **Tag/Nacht-Zyklus:** ein `dayPhase` aus `gs.getDay()` -> warmer (Tag) / kuehl-blauer (Nacht)
    Overlay-Gradient ueber die Karte; nachts leuchten Becken & Reklame staerker.
12. **Wetter durchs Schaufenster** (Zone C): fallende Regen-Striche oder Sonnen-Lichtkegel,
    optional an Events gekoppelt ("Wasserrohrbruch" -> es regnet symbolisch).
13. **Besucher-Punkte** (Zone C): kleine Personen, die zwischen Wegpunkten interpolieren;
    Anzahl = f(Reputation). Die Promenade wird sichtbar voller, je erfolgreicher du bist.
14. **Boersen-Ticker & Muenz-Pops** (Zone C): scrollender Textstreifen + aufsteigende Coins
    bei Verkauf - klassisches Tycoon-Feedback, billig in Java2D.

**Polish-Details:**
15. **Bau-/Abriss-Effekt:** beim Platzieren kurz ein expandierender Ring (`drawOval`, wachsender
    Radius, fallendes alpha); beim Abriss eine kleine "Staubwolke" aus 4-5 grauen Ovalen.
16. **Hover-Glow** statt nur Rahmen: weicher `RadialGradientPaint`-Schimmer unter der Maus.
17. **Charakter-Cameos:** taucht ein Event/Quest mit Krillkill oder Akwanov auf, laeuft kurz
    ein groesserer Personen-Glyph (Krillkill = eckig/olivgruen, Akwanov = rund/anzugblau) durch
    die jeweilige Zone (D bzw. C) - verbindet Charaktere raeumlich mit der Karte.
18. **Effizienz-Sterben:** bei `efficiency < 0.35` flackern die schwimmenden Shrimps eines Beckens
    grau und treiben langsam nach oben (das "BWL-Absolventen sterben ab" aus der Becken-Beschreibung,
    jetzt sichtbar) - emotional wirksamer Warnhinweis.

**Reihenfolge-Empfehlung fuer maximalen Effekt:** zuerst 1+2+5 (Zonen sichtbar machen +
schwimmende Shrimps), dann 11 (Tag/Nacht) und 13/14 (lebendige Promenade). Damit fuehlt sich die
Karte sofort wie eine echte, atmende Anlage an - bei minimalem Code.
