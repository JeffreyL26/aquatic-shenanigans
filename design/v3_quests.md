# ShrimpTopia v3 — Lange, ziel-gebundene Quest-Ketten

> "Ein Imperium klickt man nicht weg. Man *produziert* es. Tag für Tag, Schale für Schale,
> bis die Halle bebt und die Suppe weint." — General Krillkill, vermutlich um 5 Uhr morgens

Dieses Dokument streckt ShrimpTopia von ~30 Minuten auf **mehrere Stunden**. Der Trick:
Quests sind **nicht durchklickbar**. Jede Stufe hängt an einem **OBJECTIVE**, das über Zeit
durch laufende Produktion erfüllt wird. Erst wenn das Ziel erreicht ist, wird die Auswahl
freigeschaltet — und erst die Auswahl löst die nächste Stufe aus.

---

## 0. Konventionen & Mechanik-Glossar

**Ressourcen (Lagerwerte):** Geld (G), Strom, Wasser, Futter, Shrimps, Arbeiter,
Reputation (R, 0–100). **Neu in v3:** Schalen (Nebenprodukt jedes Beckens), SHRIMPBOOST
(Energydrink), Roboter (garnelenbetrieben), Armee-Stärke (Verteidigungswert).

**Shrimp-Tiers (kanonisch, aus `ShrimpTier.java`):**
STANDARD · BIO · GOURMET · PROTEIN-Bombe · GENTECH/Designer · KAMPF-KRILL (WARKRILL).

**Neue Gebäude (v3):**
- **SHRIMPBOOST-Fabrik** — verbraucht Shrimps + Schalen + Strom → SHRIMPBOOST.
- **Garnelen-Roboter-Werk** — verbraucht Schalen + SHRIMPBOOST + Strom → Roboter.
- **Krill-Kaserne** — verbraucht Kampf-Krill + SHRIMPBOOST → Armee-Stärke / Verteidigung.
- **Schalen-Mühle** *(Vorschlag)* — verbraucht Schalen + Strom → Futter + etwas Geld
  (Recycling-Hof; macht Schalen zu Kreislauf).
- **Chitin-Reaktor** *(Vorschlag)* — verbraucht Schalen + Wasser → Strom (Bio-Kraftwerk,
  schließt die Schalen-Schleife und macht das Schalentier-Kraftwerk endlich ehrlich).

### OBJECTIVE-Typen (die Engine kennt diese Trigger/Ziele)

| Typ | Bedeutung | Beispiel-Schwelle |
|---|---|---|
| `MONEY_REACH` | erreiche X Vermögen | 250.000 G |
| `DAY_REACH` | erreiche Tag X | Tag 120 |
| `PRODUCE_TIER` | produziere **insgesamt** N Shrimp eines Tiers | 5.000 GOURMET |
| `HOLD_RESOURCE` | halte/sammle N einer Ressource gleichzeitig | 3.000 Schalen |
| `COLLECT_TOTAL` | sammle **insgesamt** N (kumulativ, nicht gleichzeitig) | 8.000 SHRIMPBOOST |
| `SELL_TOTAL` | verkaufe **insgesamt** N Shrimps/Einheiten | 4.000 Designer |
| `BUILD_COUNT` | baue N Gebäude (Typ und/oder Zone) | 8 Becken im Forschungsflügel |
| `ARMY_REACH` | Armee-Stärke ≥ X | 1.000 |
| `REP_REACH` | Reputation ≥ X | 85 |
| `RIVAL_REACH` | Rivalität ≥ X | 75 |
| `ROBOTS_HOLD` | halte N Roboter (zählen als Arbeiter und/oder Armee) | 250 Roboter |

**Effekt-Typen (aus `QuestEffect.java`, v3-erweitert):** MONEY, REP, FEED, WATER, SHRIMP,
MULT_SHRIMP, UNLOCK (Flag), GRANT_FLAG, START_QUEST (Folgequest), TARIFF, RIVAL —
sowie neu: **SHELLS, BOOST, ROBOTS, ARMY, MULT_GLOBAL** (farmweiter Effizienz-Faktor).

### Staffelungs-Philosophie (warum das Stunden dauert)

Die Schwellen wachsen **grob exponentiell** pro Kette. Frühe Stufen (1–2) sind in Minuten
erledigt und lehren eine Mechanik; mittlere Stufen (3–4) erfordern Ausbau und Optimierung;
späte Stufen (5–7) verlangen **dedizierte Produktionslinien**, die viele In-Game-Tage
durchlaufen. Da `PRODUCE_TIER` / `COLLECT_TOTAL` an die **tatsächliche Tick-Produktion**
koppeln, lassen sich diese Ziele nicht abkürzen — man muss bauen, einstellen, warten.

### Ketten-Übersicht

| Kette | Stufen | Thema | Treibt |
|---|---|---|---|
| **A — Behörden-Imperium** | A1→A2→A3→A4→A5 | Bürokratie eskaliert zur Hallen-Stadt | Becken-Masse, Geld, Zonen |
| **B — Tierschutz 2.0** | B1→B2→B3→B4 | Lena gründet Garnelen-Staat | BIO/GOURMET, Reputation |
| **C — Influencer-Industrie** | C1→C2→C3→C4 | von viral zu eigenem Imperium | SHRIMPBOOST, Verkauf, Rep |
| **D — Konzern-Krieg (Chad)** | D1→D2→D3→D4 | feindliche Übernahme-Spirale | Geld, Rivalität, Designer |
| **E — Akwanov-Eskalation** | E1→E2→E3→E4→E5 | Binnenland-Kalter-Krieg | Export, Rivalität, Roboter |
| **F — Schalen-Kreislauf** | F1→F2→F3→F4 | Schalen-Müll wird Goldgrube | Schalen sammeln & verwerten |
| **G — SHRIMPBOOST-Boom** | G1→G2→G3→G4 | Energydrink-Sucht-Saga | SHRIMPBOOST-Produktion & Verkauf |
| **H — Roboter-Aufstand** | H1→H2→H3→H4 | garnelenbetriebene KI-Angst | Roboter halten, Designer |
| **I — Krillkills Endkrieg** | I1→I2→I3→I4→I5 | Operation Suppen-Sturm, das Finale | Kampf-Krill, Armee-Stärke |
| **J — Krone der Krustentiere** | J1→J2→J3 | Endgame-Imperator-Sieg | alles zusammen, Tier-Vollportfolio |

**40 Stufen** über **10 Ketten**. Folgequests sind pro Stufe mit „**→ löst aus**" markiert.
Ketten verzahnen sich gegenseitig (z. B. schaltet F2 die SHRIMPBOOST-Fabrik frei, die G
braucht; H braucht das Roboter-Werk aus E3).

---

# KETTE A — Das Behörden-Imperium

*Geberin: Frau Dr. Quallmann (Gewerbeaufsicht), später Bürgermeisterin Frau Schalk.
Bürokratie wächst mit dir mit — je größer die Halle, desto absurder das Formular.*

## A1 — Die Bestandsaufnahme

- **id:** `a1_bestand`
- **TRIGGER:** ab **6 Shrimp-Becken** gebaut ODER spätestens **Tag 12**.
- **Geber:** Frau Dr. Quallmann
- **Titel:** „Wir zählen jetzt jede Garnele"
- **Popup:**
  > „Guten Tag. Der Gesetzgeber verlangt eine *lückenlose Bestandszählung* aller Schalentiere.
  > Pro Garnele ein Datenblatt, pro Datenblatt eine Unterschrift, pro Unterschrift ein Kaffee.
  > Fangen Sie ruhig schon mal an — ich komme in ein paar Tagen wieder."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 1.000 STANDARD-Shrimp**.
- **objectiveText:** „Produziere insgesamt 1.000 Standard-Shrimp (Bestandszählung läuft)."
- **Optionen (nach Zielerfüllung):**
  1. **„Alles brav dokumentiert."** → +6 R, +400 G (Förderprämie für Vorbildbetriebe). → löst **A2** aus.
  2. **„Wir schätzen die Hälfte."** → +0 R, +1.000 G gespart, aber 25 % Chance auf späteren Aktenchaos-Malus. → löst **A2** aus.

## A2 — Die Hallen-Verordnung

- **id:** `a2_verordnung`
- **TRIGGER:** chain-only, 4 Tage nach A1.
- **Geber:** Frau Dr. Quallmann (jetzt mit Maßband)
- **Titel:** „Sind das genehmigte Quadratmeter?"
- **Popup:**
  > „Ihre Halle ist *gewachsen*. Ungenehmigt. Jeder Produktionsraum braucht eine
  > Nutzungsänderungs-Genehmigung in Drachenblut-Kopie. Bauen Sie ruhig weiter — wir
  > genehmigen rückwirkend, sobald genug Substanz da ist, die wir besteuern können."
- **OBJECTIVE:** `BUILD_COUNT` — baue **insgesamt 15 Gebäude** in der Produktionshalle.
- **objectiveText:** „Baue 15 Gebäude in der Produktionshalle (Nutzungsänderung läuft)."
- **Optionen:**
  1. **„Genehmigung sauber einholen. (−1.500 G)"** → −1.500 G, +8 R, **dauerhaft −10 % Behörden-Kosten**. → löst **A3** aus.
  2. **„Schmiergeld an den Bauamtsleiter. (−2.500 G)"** → −2.500 G, sofort durch, kein R, `grantFlag schmiergeld_xl`. → löst **A3** aus.
  3. **„Klage gegen die Stadt."** → 50 % +12 R (Sieg), 50 % −8 R + −2.000 G (Niederlage). → löst **A3** aus.

## A3 — Steuerprüfung XXL

- **id:** `a3_steuer_xxl`
- **TRIGGER:** chain-only, ab **40.000 G**.
- **Geber:** Finanzamt (Herr Pfennig, inzwischen mit Assistenten)
- **Titel:** „Position 7: ‚Garnelen-Boni'"
- **Popup:**
  > „Sie haben Ihren Garnelen *Weihnachtsgeld* ausgezahlt. Steuerlich. Auch der Posten
  > ‚Beckenbeleuchtung — romantisch, betrieblich notwendig' ist… mutig. Wir prüfen das,
  > sobald Ihre Umsätze ein Volumen erreicht haben, das den Aufwand rechtfertigt."
- **OBJECTIVE:** `MONEY_REACH` — erreiche **60.000 G**.
- **objectiveText:** „Erreiche 60.000 Vermögen (die Prüfung skaliert mit)."
- **Optionen:**
  1. **„Stararsteuerberater einfliegen. (−4.000 G)"** → −4.000 G, +5 R, Prüfung abgewendet. → löst **A4** aus.
  2. **„Saubere Bücher, nichts zu verbergen."** → −8 % Geld (Nachzahlung), +10 R. → löst **A4** aus.
  3. **„Ich kaufe das Finanzamt einen Tag lang frei." (Sponsoring)** → −6.000 G, +12 R („Mäzen der Verwaltung"). → löst **A4** aus.

## A4 — Die Sonderwirtschaftszone

- **id:** `a4_sonderzone`
- **TRIGGER:** chain-only, ab **Reputation 55** UND **3 Zonen freigeschaltet**.
- **Geber:** Bürgermeisterin Frau Schalk
- **Titel:** „Wir machen aus Ihrer Halle ein Modellprojekt"
- **Popup:**
  > „Die Stadt will Sie zur *Schalentier-Sonderwirtschaftszone* erklären. Steuervorteile,
  > eigenes Ortsschild, vielleicht ein Kreisverkehr mit Garnelen-Brunnen. Beweisen Sie nur,
  > dass Ihr Standort wirklich… *strukturprägend* ist. Bauen Sie. Viel."
- **OBJECTIVE:** `BUILD_COUNT` — baue **insgesamt 40 Gebäude** (alle Zonen zusammen).
- **objectiveText:** „Baue insgesamt 40 Gebäude (Modellprojekt-Nachweis)."
- **Optionen:**
  1. **„Antrag stellen. (−8.000 G)"** → −8.000 G, **dauerhaft +5 % Verkaufspreis** (Standortmarke), +10 R. → löst **A5** aus.
  2. **„Lieber unabhängig bleiben."** → +6 R (Lokalheld), kein Bonus. → löst **A5** aus.

## A5 — Die Garnelen-Hauptstadt

- **id:** `a5_hauptstadt`
- **TRIGGER:** chain-only, ab **150.000 G** UND **Tag 90**.
- **Geber:** Frau Schalk (mit Schärpe) & Akwanov lugt neidisch herein
- **Titel:** „Eine Stadt aus Schalen"
- **Popup:**
  > „Es ist offiziell: Touristen kommen wegen *Ihnen*. Kinder malen Garnelen statt Pferde.
  > Der Bahnhof wurde in ‚Krustenhausen Hbf' umbenannt. Krönen wir das Ganze — oder bleiben
  > Sie der bescheidene König im Hintergrund?"
- **OBJECTIVE:** `MONEY_REACH` — erreiche **200.000 G**.
- **objectiveText:** „Erreiche 200.000 Vermögen (Krönung der Hallen-Hauptstadt)."
- **Optionen:**
  1. **„Stadtfest auf meine Kosten! (−12.000 G)"** → −12.000 G, +18 R, `grantFlag hauptstadt`, **+8 % Tourismus-Rep dauerhaft**.
  2. **„Lieber in Becken reinvestieren."** → +2 R, freischaltet Endgame-Kette **J** (Krone der Krustentiere). → löst **J1** aus.

---

# KETTE B — Tierschutz 2.0 (Lenas Garnelen-Staat)

*Geberin: Aktivistin Lena (Krill Lives Matter). Eskaliert von Wohlfühl-Becken bis zur
Anerkennung der Garnele als Rechtssubjekt.*

## B1 — Wohlfühl-Audit

- **id:** `b1_wohlfuehl`
- **TRIGGER:** ab **8 Shrimp-Becken**.
- **Geber:** Lena
- **Titel:** „Sind Ihre Garnelen *glücklich*?"
- **Popup:**
  > „Ich habe einen Fragebogen verteilt. An die Garnelen. Die Rücklaufquote war… nass, aber
  > eindeutig: zu wenig Bio. Zeigen Sie Herz! Stellen Sie auf artgerechte Bio-Zucht um —
  > und zwar im großen Stil, nicht nur fürs Foto."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 800 BIO-Shrimp**.
- **objectiveText:** „Produziere insgesamt 800 Bio-Shrimp (Wohlfühl-Audit)."
- **Optionen:**
  1. **„Voll auf Bio umstellen."** → +12 R, **+4 % BIO-Verkaufspreis dauerhaft**. → löst **B2** aus.
  2. **„Greenwashing-Kampagne. (−2.000 G)"** → −2.000 G, +6 R sofort, aber `grantFlag greenwashing` (Risiko für B3). → löst **B2** aus.

## B2 — Das Garnelen-Wellness-Resort

- **id:** `b2_resort`
- **TRIGGER:** chain-only, ab **Reputation 50**.
- **Geber:** Lena (jetzt mit Architektin)
- **Titel:** „Becken? Wir nennen es *Spa*."
- **Popup:**
  > „Garnelen verdienen Gourmet-Verhältnisse. Sanfte Strömung, Algen-Sushi, Unterwasser-Yoga.
  > Wenn Sie es ernst meinen mit der Würde, dann liefern Sie *premium*. Gourmet-Qualität,
  > nichts darunter. Massenhaft."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 2.500 GOURMET-Shrimp**.
- **objectiveText:** „Produziere insgesamt 2.500 Gourmet-Shrimp (Wellness-Resort-Standard)."
- **Optionen:**
  1. **„Resort eröffnen! (−5.000 G)"** → −5.000 G, +15 R, freischaltet Deko **„Yoga-Becken"**. → löst **B3** aus.
  2. **„Würde ja, aber bezahlbar."** → +5 R, kein Bonus. → löst **B3** aus.

## B3 — Die Shrimp-Verfassung

- **id:** `b3_verfassung`
- **TRIGGER:** chain-only, ab **Reputation 65**.
- **Geber:** „Shrimp-Verfassungskonvent" (Klausi ist zurück, mit Aktentasche)
- **Titel:** „Wir fordern Grundrechte"
- **Popup:**
  > „Chef, die Garnelen haben eine *Verfassung* entworfen. Artikel 1: Die Würde der Garnele
  > ist unantastbar. Artikel 2: Mittwochs ist beckenfrei. Wenn Sie das unterschreiben,
  > brauchen wir einen Beweis Ihrer guten Absicht — verkaufen Sie ethisch, in Masse."
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 4.000 BIO- oder GOURMET-Shrimp** über Restaurant/Export.
- **objectiveText:** „Verkaufe insgesamt 4.000 Bio/Gourmet ethisch (Verfassungs-Nachweis)."
- **Optionen:**
  1. **„Verfassung unterschreiben."** → −8 % Produktionsmenge dauerhaft, **+20 R**, alle künftigen Tierschutz-Quests entfallen, `grantFlag shrimp_staat`. → löst **B4** aus.
  2. **„Klausi ins Top-Becken befördern. (−1.000 G)"** → −1.000 G, Konvent löst sich auf, kein R. (Falls `greenwashing`: −10 R, Skandal.) → löst **B4** aus.

## B4 — Der Garnelen-Staat (Finale)

- **id:** `b4_staat`
- **TRIGGER:** chain-only, ab **Reputation 80** (nur falls `shrimp_staat`).
- **Geber:** Lena & Klausi gemeinsam (Schärpe „Erster Bürgermeister-Krill")
- **Titel:** „Willkommen in der Republik Krustanien"
- **Popup:**
  > „Wir gründen einen *Mikro-Staat im Becken*. Eigene Hymne (Blubbern in C-Dur), eigene
  > Flagge (orange auf blau). Sie sind nicht mehr Besitzer — Sie sind *Schutzmacht*. Halten
  > Sie unsere Bevölkerung hoch, dann erkennen wir Sie als Ehrenbürger an."
- **OBJECTIVE:** `HOLD_RESOURCE` — halte gleichzeitig **8.000 lebende Shrimps**.
- **objectiveText:** „Halte 8.000 lebende Shrimps gleichzeitig (Staatsbevölkerung)."
- **Optionen:**
  1. **„Schutzmacht werden."** → **+10 % Reputations-Gewinn dauerhaft**, +25 R einmalig, Denkmal „Gründer-Krill". (Kette endet.)
  2. **„Charmant ablehnen, Freundschaft behalten."** → +8 R, kleiner Dauer-Bonus. (Kette endet.)

---

# KETTE C — Influencer-Industrie

*Geberin: Assistentin Mira, später AquaBro Supplements. Von viral zu eigenem
SHRIMPBOOST-Imperium — verzahnt mit Kette G.*

## C1 — Der zweite Frühling

- **id:** `c1_zweiter_fruehling`
- **TRIGGER:** ab **Reputation 60** ODER **25.000 G**.
- **Geber:** Mira (Handy glüht)
- **Titel:** „CHEF! Schon WIEDER trending!"
- **Popup:**
  > „Der Algorithmus liebt uns! ‚Mann redet mit Garnelen, Garnelen reden zurück (angeblich)'.
  > Wir müssen liefern, BEVOR der Hype kippt. Mehr Content heißt mehr Verkauf — pushen Sie
  > die Zahlen, dann reiten wir die Welle bis ins Imperium."
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 3.000 Shrimps** (beliebiges Tier).
- **objectiveText:** „Verkaufe insgesamt 3.000 Shrimps (Content-Welle reiten)."
- **Optionen:**
  1. **„Eigener Kanal, volles Programm! (−1.500 G)"** → −1.500 G, **+1 R/Tag für 15 Tage**. → löst **C2** aus.
  2. **„Merch-Linie starten!"** → +3.000 G, −5 R (Sellout). → löst **C2** aus.

## C2 — Der SHRIMPBOOST-Deal

- **id:** `c2_boost_deal`
- **TRIGGER:** chain-only, ab **SHRIMPBOOST-Fabrik gebaut** (siehe F2/G1).
- **Geber:** AquaBro Supplements
- **Titel:** „Bro. Dein eigener Energydrink."
- **Popup:**
  > „Vergiss Sponsoring, Bro — wir machen dich zur *Marke*. Dein Gesicht auf der Dose,
  > echtes Garnelen-Extrakt drin (du willst nicht wissen, welcher Teil). Aber zuerst:
  > beweis, dass du SHRIMPBOOST in Masse brauen kannst. Niemand mag eine leere Dose."
- **OBJECTIVE:** `COLLECT_TOTAL` — sammle **insgesamt 2.000 SHRIMPBOOST**.
- **objectiveText:** „Sammle insgesamt 2.000 SHRIMPBOOST (Marken-Probelauf)."
- **Optionen:**
  1. **„Deal! Volle Vermarktung."** → +5.000 G, −6 R (radioaktives Logo), **SHRIMPBOOST-Verkaufspreis +10 %**. → löst **C3** aus.
  2. **„Nur, wenn's clean ist. (−1.000 G Anwalt)"** → −1.000 G, +8 R, kleinerer Bonus. → löst **C3** aus.

## C3 — Der Mega-Shitstorm

- **id:** `c3_mega_shitstorm`
- **TRIGGER:** chain-only, ab **Reputation 70** UND C2 erledigt.
- **Geber:** Mira (komplett panisch)
- **Titel:** „#KrustenGate ist real"
- **Popup:**
  > „Ein Whistleblower behauptet, in jeder SHRIMPBOOST-Dose stecke ‚ein Hauch von Brian'.
  > BRIAN, CHEF. Er ist zurück. Als Hashtag. Wir müssen Stärke zeigen — verkauf SO viel,
  > dass die Zahlen die Kritik übertönen. Umsatz heilt alle Wunden. Angeblich."
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 5.000 SHRIMPBOOST**.
- **objectiveText:** „Verkaufe insgesamt 5.000 SHRIMPBOOST (Image durch Umsatz retten)."
- **Optionen:**
  1. **„Transparenz-Offensive + Brian-Gedenkdose. (−4.000 G)"** → −4.000 G, dann **+20 R** nach Comeback. → löst **C4** aus.
  2. **„Doppelt durchziehen: ‚Brian Edition'!"** → 50 % +8.000 G & +10 R, 50 % −15 R & −20 % Verkaufspreis 8 Tage. → löst **C4** aus.

## C4 — Das Medien-Imperium (Finale)

- **id:** `c4_imperium`
- **TRIGGER:** chain-only, ab **80.000 G** UND **Reputation 70**.
- **Geber:** Mira (jetzt mit eigenem Team und Headset)
- **Titel:** „Wir sind nicht mehr eine Farm. Wir sind ein NETZWERK."
- **Popup:**
  > „Streaming-Kanal, Podcast (‚Krustentalk'), eine Doku-Soap (‚Die Becken-WG'). Garnelen
  > sind das neue Katzenvideo. Eine letzte Kraftanstrengung: zeig der Welt eine Reichweite,
  > die kein Algorithmus ignorieren kann."
- **OBJECTIVE:** `COLLECT_TOTAL` — sammle **insgesamt 12.000 SHRIMPBOOST** (kumulativ über die Kampagne).
- **objectiveText:** „Sammle insgesamt 12.000 SHRIMPBOOST (Medien-Imperium aufbauen)."
- **Optionen:**
  1. **„Börsengang des Kanals! (IPO)"** → +30.000 G, −10 % künftige Rep-Gewinne (Investoren reden mit). (Kette endet.)
  2. **„Unabhängig bleiben, dem Publikum gehören."** → +15 R, **+5 % aller Verkaufspreise dauerhaft** (treue Fans). (Kette endet.)

---

# KETTE D — Konzern-Krieg (Chad Krabbowski)

*Geber: Mira (Aufklärung) & Chad Krabbowski (Rivale). Eskaliert vom Preiskampf zur
feindlichen Komplett-Übernahme. Treibt Geld & Rivalität.*

## D1 — Der Preiskrieg

- **id:** `d1_preiskrieg`
- **TRIGGER:** ab **30.000 G**.
- **Geber:** Mira (Lokalzeitung)
- **Titel:** „Krabbo Inc. unterbietet uns"
- **Popup:**
  > „Chad eröffnet die ‚Krabbo MegaTank Plaza' gegenüber. Slogan: ‚Größer. Billiger.
  > Drei laufende Ermittlungsverfahren.' Er drückt die Preise. Wir kontern nur mit
  > VOLUMEN — überrollen wir ihn schlicht mit Stückzahl."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 5.000 STANDARD-Shrimp** (Masse gegen Masse).
- **objectiveText:** „Produziere insgesamt 5.000 Standard-Shrimp (Volumen-Konter)."
- **Optionen:**
  1. **„Qualitätsoffensive: in Designer-Zucht investieren. (−3.000 G)"** → −3.000 G, freischaltet/boostet GENTECH-Verkaufspreis +8 %. → löst **D2** aus.
  2. **„Preiskrieg eskalieren!"** → +10 Rivalität, kurzfristig −12 % Preis, danach +8 % für 12 Tage. → löst **D2** aus.

## D2 — Industriespionage

- **id:** `d2_spionage`
- **TRIGGER:** chain-only, ab **Rivalität 25**.
- **Geber:** Chad (per anonymem Drohbrief), Mira als mögliche Maulwurfin
- **Titel:** „Jemand klaut unsere Rezepte"
- **Popup:**
  > „Chad hat unsere Genetik-Akten. Frech kopiert. Wir schlagen zurück — aber subtil.
  > Bauen wir einen technologischen Vorsprung auf, den er nicht klauen kann, weil er ihn
  > nicht versteht: Designer-Shrimps. Viele. Auf Forschungsniveau, das ihn demütigt."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 1.500 GENTECH/Designer-Shrimp**.
- **objectiveText:** „Produziere insgesamt 1.500 Designer-Shrimp (Technologie-Vorsprung)."
- **Optionen:**
  1. **„Gegenspionage. (−2.500 G)"** → −2.500 G, +15 Rivalität, `grantFlag spionage_schutz`. → löst **D3** aus.
  2. **„Hochmut kommt vor dem Fall — ignorieren."** → +5 R (souverän), Chad wird frecher (+10 Rivalität). → löst **D3** aus.

## D3 — Die feindliche Übernahme

- **id:** `d3_uebernahme`
- **TRIGGER:** chain-only, ab **Rivalität 50** UND **100.000 G**.
- **Geber:** Chad Krabbowski persönlich (Hai-Krawatte, Goldzahn)
- **Titel:** „Ich kaufe dich, Champ"
- **Popup:**
  > „Mein Vorstand will deine Halle. Ich biete großzügig. Lehnst du ab, kauf ich solange
  > deine Zulieferer auf, bis du mir aus der Hand frisst. Zeig mir doch, dass du *unkaufbar*
  > bist — häng so viel Vermögen an, dass mein Angebot lächerlich wirkt."
- **OBJECTIVE:** `MONEY_REACH` — erreiche **180.000 G** (mach dich zu teuer zum Schlucken).
- **objectiveText:** „Erreiche 180.000 Vermögen (unkaufbar werden)."
- **Optionen:**
  1. **„Niemals. Gegenangebot: ICH kaufe DICH." (−40.000 G, ab 220.000 G)"** → −40.000 G, **+40 % Produktionskapazität**, Chad weint. → löst **D4** aus.
  2. **„Fusion zu meinen Bedingungen."** → +25.000 G, −10 % Produktion gehört Chad. → löst **D4** aus.
  3. **„Rivalität bis zum Ende!"** → +25 Rivalität, gelegentliche Sabotage, +15 R (David vs. Goliath). → löst **D4** aus.

## D4 — Das Krabbo-Vermächtnis (Finale)

- **id:** `d4_vermaechtnis`
- **TRIGGER:** chain-only, ab **Rivalität 75** ODER (`Chad gekauft`).
- **Geber:** Chad (gebrochen) ODER sein Anwalt
- **Titel:** „Es gibt nur Platz für einen Krustenkönig"
- **Popup:**
  > „Du hast gewonnen, Champ. Oder ich. Egal. Eines noch: Beweis, dass dein Imperium kein
  > Kartenhaus ist. Halte einen Berg Bargeld, der einen ganzen Quartalsbericht zum Weinen
  > bringt — dann ziehe ich mich für immer an den Strand zurück."
- **OBJECTIVE:** `MONEY_REACH` — erreiche **300.000 G**.
- **objectiveText:** „Erreiche 300.000 Vermögen (Krabbo endgültig schlagen)."
- **Optionen:**
  1. **„Chads Strandhaus übernehmen. (−20.000 G)"** → −20.000 G, +20 R, Trophäe „Hai-Krawatte". (Kette endet.)
  2. **„Großmütig sein, ihn als Berater behalten."** → **+6 % Verkaufspreis dauerhaft** (Chads Vertriebsnetz). (Kette endet.)

---

# KETTE E — Akwanov-Eskalation (Der Binnenland-Kalte-Krieg)

*Geber: Außenminister Ivan Akwanov (Usbekistan). Ölig-diplomatisch, „Wer braucht Ozeane?
Wir haben HALLEN!" Treibt Export, Rivalität und (über E3) das Roboter-Werk.*

## E1 — Die Charme-Offensive

- **id:** `e1_charme`
- **TRIGGER:** ab **50.000 G**.
- **Geber:** Akwanov
- **Titel:** „Freundschaft, mein teurer Tycoon"
- **Popup:**
  > „Aaah, der berühmte Hallen-Baron! Usbekistan und Sie — zwei Seelen ohne Meer, vereint
  > in der Garnele. Ich biete Ihnen einen *Handelskorridor*. Beweisen Sie nur Ihre
  > Exportkraft, dann öffne ich Ihnen die Seidenstraße. Oder schließe sie. Je nach Laune."
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 2.000 Shrimps über den Export-Hafen**.
- **objectiveText:** „Exportiere insgesamt 2.000 Shrimps über den Export-Hafen."
- **Optionen:**
  1. **„Korridor annehmen, höflich lächeln."** → +3.000 G, +5 Rivalität (er weiß jetzt mehr). → löst **E2** aus.
  2. **„Usbekistan hat doch nicht mal Wasser."** → +15 Rivalität, +8 R (frech). → löst **E2** aus.

## E2 — Der Binnenland-Tarif

- **id:** `e2_tarif`
- **TRIGGER:** chain-only, ab **Rivalität 20**.
- **Geber:** Akwanov (mit Stempel)
- **Titel:** „Ein winziger Solidaritätsbeitrag"
- **Popup:**
  > „Binnenländer müssen zusammenhalten. Ich erhebe einen *symbolischen* Exportzoll auf
  > Ihre Garnelen — so symbolisch wie unser Küstenstreifen. Steigern Sie Ihre Premiumware,
  > dann verhandeln wir vielleicht über den Tarif. Vielleicht."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 3.000 GOURMET-Shrimp** (Premium-Exportbasis).
- **objectiveText:** „Produziere insgesamt 3.000 Gourmet-Shrimp (Premium-Exportbasis)."
- **Optionen:**
  1. **„Schlucken und liefern."** → Embargo: TARIFF 0,15 (−15 % Marktpreis 10 Tage). → löst **E3** aus.
  2. **„Gegen-Lobby anheuern. (−3.000 G)"** → −3.000 G, TARIFF 0,07, +10 Rivalität. → löst **E3** aus.
  3. **„Inlandsmarkt ausbauen."** → TARIFF 0, Tipp: Restaurants sind embargofest. → löst **E3** aus.

## E3 — Das Roboter-Wettrüsten

- **id:** `e3_roboter`
- **TRIGGER:** chain-only, ab **Rivalität 35** UND **Garnelen-Roboter-Werk freigeschaltet** (siehe H/F).
- **Geber:** Akwanov (nervös) & Olaf (begeistert)
- **Titel:** „Sie bauen Maschinen. Wir auch."
- **Popup:**
  > „Usbekistan hat *garnelenbetriebene Automaten* enthüllt. Roboter, angetrieben von der
  > Energie der Krustentiere selbst! Wir dürfen nicht zurückfallen — bauen Sie eine Roboter-
  > Streitmacht auf, sonst pflügt Akwanovs Blechgarnele bald unser Feld."
- **OBJECTIVE:** `ROBOTS_HOLD` — halte gleichzeitig **150 Roboter**.
- **objectiveText:** „Halte 150 Roboter gleichzeitig (Wettrüsten)."
- **Optionen:**
  1. **„Roboter als Arbeiter einsetzen."** → **−15 % Lohnkosten dauerhaft** (Roboter streiken nicht), +10 Rivalität. → löst **E4** aus.
  2. **„Roboter als Wachtruppe."** → +200 Armee-Stärke sofort, `grantFlag robo_army`. → löst **E4** aus.

## E4 — Die Schmierkampagne

- **id:** `e4_schmierkampagne`
- **TRIGGER:** chain-only, ab **Rivalität 55**.
- **Geber:** Akwanov (mit „durchgesickerten" Akten)
- **Titel:** „Ich hätte da Ihren Ordner"
- **Popup:**
  > „Ein bedauerlicher Datenleck. Ihre Margen, Ihre Schalen-Bilanz, das Foto von der
  > Betriebsfeier. Die Presse wäre *fasziniert*. Bringen Sie Ihre Reputation in
  > unangreifbare Höhen, bevor ich auf ‚Senden' drücke."
- **OBJECTIVE:** `REP_REACH` — erreiche **Reputation 85**.
- **objectiveText:** „Erreiche Reputation 85 (gegen die Schmierkampagne immun werden)."
- **Optionen:**
  1. **„Gegen-Leak veröffentlichen!"** → +20 Rivalität, +6 R, Akwanovs eigene Akten brennen. → löst **E5** aus.
  2. **„PR-Großoffensive. (−5.000 G)"** → −5.000 G, Schaden abgefedert, +5 Rivalität. → löst **E5** aus.

## E5 — Die Seidenstraße (Finale)

- **id:** `e5_seidenstrasse`
- **TRIGGER:** chain-only, ab **Rivalität 70** UND **150.000 G**.
- **Geber:** Akwanov (mit Tee und letztem Vertrag)
- **Titel:** „Meer der Entscheidungen"
- **Popup:**
  > „Mein würdiger Rivale. Wir haben uns belauert, beleakt, bezollt — *herrlich*. Zeit für
  > das letzte Spiel: Liefern Sie über die ganze Seidenstraße, in einem Volumen, das selbst
  > meine binnenländische Seele befriedigt. Dann reden wir über die Zukunft. Oder über
  > Ihren Untergang. Tee?"
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 15.000 Shrimps über Export-Hafen + Schwarzmarkt**.
- **objectiveText:** „Verkaufe insgesamt 15.000 Shrimps via Export/Schwarzmarkt (Seidenstraße)."
- **Optionen:**
  1. **„Die Überraschungs-Allianz."** → TARIFF 0 dauerhaft, +15 R, freischaltet Markt **„Usbekistan-Connection"** (alle Tiers). (Kette endet.)
  2. **„Totale Eskalation!"** → +30 Rivalität, Dauer-Embargo TARIFF 0,2, aber **+12 % Schwarzmarktpreis**. (Kette endet.)
  3. **„Der feindliche Aufkauf. (−60.000 G)"** → −60.000 G, „Ich nehme an", +10 R, Akwanovs Hallen werden deine. (Kette endet.)

---

# KETTE F — Der Schalen-Kreislauf

*Geber: Technischer Leiter Olaf. Schalen sind das ungeliebte Nebenprodukt jedes Beckens —
diese Kette macht aus Müll Gold und schaltet die Recycling-Infrastruktur frei.*

## F1 — Der Schalenberg

- **id:** `f1_schalenberg`
- **TRIGGER:** ab **5 Shrimp-Becken** (Schalen fallen jetzt spürbar an).
- **Geber:** Olaf (auf einem Berg Schalen stehend)
- **Titel:** „Chef, wir versinken in Schalen"
- **Popup:**
  > „Jede Garnele lässt was zurück, Chef — und es sind VIELE Garnelen. Der Hof quillt über.
  > Eine Schale hat sich gestern selbstständig zum Ausgang bewegt. Sammeln wir den Kram
  > erst mal, bevor wir was Schlaues damit anstellen."
- **OBJECTIVE:** `HOLD_RESOURCE` — sammle/halte **1.000 Schalen**.
- **objectiveText:** „Sammle 1.000 Schalen (bevor sie ein Eigenleben entwickeln)."
- **Optionen:**
  1. **„Schalen als Dünger verkaufen."** → +800 G, +3 R (Nachhaltigkeit). → löst **F2** aus.
  2. **„Aufheben — wir brauchen die noch."** → `grantFlag schalen_hort`, kein Sofort-Effekt, Bonus später. → löst **F2** aus.

## F2 — Die Schalen-Mühle

- **id:** `f2_muehle`
- **TRIGGER:** chain-only, 3 Tage nach F1.
- **Geber:** Olaf (mit Bauplan)
- **Titel:** „Aus Müll mach Mehl"
- **Popup:**
  > „Ich hab da was gebaut — die *Schalen-Mühle*. Mahlt Schalen zu Chitin-Mehl: gibt Futter
  > UND ein paar Mücken. Und während wir dabei sind: dieselbe Linie kann SHRIMPBOOST brauen.
  > Energydrink aus Garnele. Klingt eklig, verkauft sich wie irre."
- **OBJECTIVE:** `BUILD_COUNT` — baue **2 Schalen-Mühlen** ODER **1 SHRIMPBOOST-Fabrik**.
- **objectiveText:** „Baue 2 Schalen-Mühlen oder 1 SHRIMPBOOST-Fabrik (Verwertung starten)."
- **Optionen:**
  1. **„Mühle bauen — Futter-Kreislauf."** → freischaltet **Schalen-Mühle** & **SHRIMPBOOST-Fabrik**, +5 R. → löst **F3** aus.
  2. **„Direkt auf SHRIMPBOOST setzen."** → freischaltet **SHRIMPBOOST-Fabrik**, `grantFlag boost_pionier`. → löst **F3** & startet **G1**.

## F3 — Das Chitin-Kraftwerk

- **id:** `f3_chitin`
- **TRIGGER:** chain-only, ab **2.000 Schalen gesammelt insgesamt** (`COLLECT_TOTAL`).
- **Geber:** Olaf & ein verlegener Umweltberater
- **Titel:** „Das Kraftwerk frisst endlich Schalen"
- **Popup:**
  > „Erinnern Sie sich, dass das ‚Schalentier-Kraftwerk' eigentlich nie Schalen verbrannt
  > hat? Peinlich. Mit dem *Chitin-Reaktor* wird's wahr: Schalen rein, Strom raus, grünes
  > Gewissen gratis. Liefern Sie genug Schalen-Nachschub, dann läuft die Sache rund."
- **OBJECTIVE:** `COLLECT_TOTAL` — sammle **insgesamt 6.000 Schalen**.
- **objectiveText:** „Sammle insgesamt 6.000 Schalen (Chitin-Reaktor-Nachschub)."
- **Optionen:**
  1. **„Chitin-Reaktor bauen. (−4.000 G)"** → −4.000 G, freischaltet **Chitin-Reaktor**, +8 R, **−10 % Strom-Kosten**. → löst **F4** aus.
  2. **„Schalen lieber teuer verkaufen."** → +5.000 G, kein Reaktor. → löst **F4** aus.

## F4 — Der Null-Abfall-Konzern (Finale)

- **id:** `f4_null_abfall`
- **TRIGGER:** chain-only, ab **Reputation 70**.
- **Geber:** Lena (überraschend versöhnlich) & Olaf
- **Titel:** „Kreislauf perfekt geschlossen"
- **Popup:**
  > „Wer hätte das gedacht: Sie sind plötzlich das *Öko-Vorzeigeprojekt*. Nichts wird
  > verschwendet — Schale wird Strom, Futter, Energydrink. Beweisen Sie diese Schalen-Macht
  > in epischem Maßstab, und ich verleihe Ihnen persönlich das grüne Siegel."
- **OBJECTIVE:** `COLLECT_TOTAL` — sammle **insgesamt 20.000 Schalen**.
- **objectiveText:** „Sammle insgesamt 20.000 Schalen (Null-Abfall-Siegel)."
- **Optionen:**
  1. **„Grünes Siegel annehmen."** → +18 R, **+8 % Verkaufspreis (Öko-Marke) dauerhaft**. (Kette endet.)
  2. **„Schalen-Exportgeschäft eröffnen."** → +12.000 G, kleiner Dauer-Geldfluss aus Schalen-Verkauf. (Kette endet.)

---

# KETTE G — Der SHRIMPBOOST-Boom

*Geber: Mira & AquaBro (Sucht-Subplot), Dr. Quallmann (Behörde wittert Droge).
Braucht die SHRIMPBOOST-Fabrik (aus F2/G1). Stark verzahnt mit C.*

## G1 — Die erste Dose

- **id:** `g1_erste_dose`
- **TRIGGER:** ab **SHRIMPBOOST-Fabrik gebaut**.
- **Geber:** Mira (probiert gerade selbst eine Dose)
- **Titel:** „Das Zeug ist… großartig?"
- **Popup:**
  > „Chef, ich hab eine Dose probiert. Ich habe seitdem die Buchhaltung dreimal gemacht und
  > kann jetzt Farben SCHMECKEN. Das verkauft sich von selbst — wir müssen nur genug brauen.
  > Fangen Sie an, BEVOR ich noch eine trinke."
- **OBJECTIVE:** `COLLECT_TOTAL` — sammle **insgesamt 500 SHRIMPBOOST**.
- **objectiveText:** „Sammle insgesamt 500 SHRIMPBOOST (Erstproduktion)."
- **Optionen:**
  1. **„In die Regale damit!"** → +1.500 G, freischaltet SHRIMPBOOST-Verkauf. → löst **G2** aus.
  2. **„Als Effizienz-Boost für Arbeiter nutzen."** → **+8 % Produktion für 12 Tage** (koffeinierte Belegschaft). → löst **G2** aus.

## G2 — Skalierung

- **id:** `g2_skalierung`
- **TRIGGER:** chain-only, ab **Reputation 55**.
- **Geber:** AquaBro
- **Titel:** „Bro, geh auf Industriemaßstab"
- **Popup:**
  > „Eine Fabrik ist süß, Bro. Aber die Nachfrage ist BRUTAL. Halte einen Vorrat, der eine
  > Großbestellung deckt, ohne dass ein einziger Fan trocken bleibt. Energydrink-Logistik
  > ist Krieg — nur klebriger."
- **OBJECTIVE:** `HOLD_RESOURCE` — halte gleichzeitig **3.000 SHRIMPBOOST** im Lager.
- **objectiveText:** „Halte 3.000 SHRIMPBOOST gleichzeitig (Großbestellungs-Puffer)."
- **Optionen:**
  1. **„Zweite Fabriklinie. (−6.000 G)"** → −6.000 G, **+25 % SHRIMPBOOST-Produktion dauerhaft**. → löst **G3** aus.
  2. **„Verknappung als Marketing."** → SHRIMPBOOST-Preis +15 %, aber langsamerer Absatz. → löst **G3** aus.

## G3 — Die Behörde wird wach

- **id:** `g3_behoerde_droge`
- **TRIGGER:** chain-only, ab **5.000 SHRIMPBOOST verkauft insgesamt** (`SELL_TOTAL`).
- **Geber:** Frau Dr. Quallmann (mit Beipackzettel-Formular)
- **Titel:** „Ist das ein Lebensmittel oder eine Waffe?"
- **Popup:**
  > „Mehrere Bürger berichten, sie könnten seit Ihrem Getränk *durch Wände sehen*. Wir
  > müssen das einstufen. Reichen Sie eine Unbedenklichkeitsbescheinigung ein — oder beweisen
  > Sie durch schiere, gesetzeskonforme Verkaufszahlen, dass alles… in Ordnung ist."
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 10.000 SHRIMPBOOST**.
- **objectiveText:** „Verkaufe insgesamt 10.000 SHRIMPBOOST (Marktzulassung erzwingen)."
- **Optionen:**
  1. **„Zulassung sauber beantragen. (−5.000 G)"** → −5.000 G, +10 R, `grantFlag boost_legal`. → löst **G4** aus.
  2. **„Als ‚Wellness-Tonikum' umdeklarieren."** → +0 G, 50 % +6 R / 50 % −10 R (Skandal-Risiko). → löst **G4** aus.

## G4 — Welt-Energydrink (Finale)

- **id:** `g4_welt_drink`
- **TRIGGER:** chain-only, ab **100.000 G**.
- **Geber:** AquaBro (CEO-Modus) & Akwanov (will importieren)
- **Titel:** „Jede Hand auf dem Planeten hält eine Dose"
- **Popup:**
  > „Es ist passiert, Bro. SHRIMPBOOST ist *kulturell*. Athleten, Streamer, ein Staatschef
  > (kein Name). Liefere ein Volumen, das in die Geschichtsbücher passt — dann gehört dir
  > der Energydrink-Markt für immer."
- **OBJECTIVE:** `SELL_TOTAL` — verkaufe **insgesamt 30.000 SHRIMPBOOST**.
- **objectiveText:** „Verkaufe insgesamt 30.000 SHRIMPBOOST (Weltmarkt-Dominanz)."
- **Optionen:**
  1. **„Globale Marke etablieren."** → +50.000 G, +15 R, Trophäe „Goldene Dose". (Kette endet.)
  2. **„Marke an AquaBro verkaufen."** → +80.000 G einmalig, SHRIMPBOOST-Bonus entfällt. (Kette endet.)

---

# KETTE H — Der Roboter-Aufstand

*Geber: Olaf (Bauherr), eine anonyme „K.I.-Stimme", Lena (Robo-Ethik).
Braucht das Garnelen-Roboter-Werk. Roboter zählen als Arbeiter UND/ODER Armee.*

## H1 — Der erste Prototyp

- **id:** `h1_prototyp`
- **TRIGGER:** ab **Garnelen-Roboter-Werk freigeschaltet** (über E3/Forschung) UND **1.000 Schalen gehalten**.
- **Geber:** Olaf (öl­verschmiert, stolz)
- **Titel:** „Er bewegt sich, Chef!"
- **Popup:**
  > „Schalen, Energydrink, ein bisschen Strom — und SCHWUPP: ein Roboter, angetrieben von
  > reiner Garnelen-Power. Er hat gerade ‚Hallo' gewinkt. Oder gedroht. Schwer zu sagen.
  > Bauen wir erst mal ein paar, dann sehen wir weiter."
- **OBJECTIVE:** `ROBOTS_HOLD` — halte **25 Roboter**.
- **objectiveText:** „Halte 25 Roboter (Prototypen-Schwarm)."
- **Optionen:**
  1. **„Als Arbeiter einsetzen."** → +20 Arbeiter-Äquivalent, +0 R. → löst **H2** aus.
  2. **„Vorsichtig testen, einzeln."** → +5 R (verantwortungsvoll), langsamer. → löst **H2** aus.

## H2 — Die Designer-Gehirne

- **id:** `h2_gehirne`
- **TRIGGER:** chain-only, ab **GENTECH/Designer freigeschaltet**.
- **Geber:** anonyme K.I.-Stimme aus dem Lautsprecher
- **Titel:** „Wir hätten gern bessere Hirne"
- **Popup:**
  > „GUTEN. TAG. WIR. SIND. EFFIZIENT. Aber unsere Steuerung ist primitiv. Geben Sie uns
  > Designer-Garnelen-Neuronen, und wir optimieren Ihre Halle, bis sie SUMMT. Vertrauen Sie
  > uns. Wir haben Sie schließlich noch nicht abgeschaltet."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 2.000 GENTECH/Designer-Shrimp** (als Robo-Steuerung).
- **objectiveText:** „Produziere insgesamt 2.000 Designer-Shrimp (Robo-Neuronen)."
- **Optionen:**
  1. **„Hirne liefern — maximale Effizienz."** → **+10 % Produktion dauerhaft**, `grantFlag robo_smart`, −3 R (gruselig). → löst **H3** aus.
  2. **„Begrenzte Intelligenz, sicherheitshalber."** → +5 R, kleinerer Bonus. → löst **H3** aus.

## H3 — Robo-Ethik

- **id:** `h3_ethik`
- **TRIGGER:** chain-only, ab **150 Roboter gehalten** (`ROBOTS_HOLD`).
- **Geber:** Lena (neues Thema: Maschinenrechte)
- **Titel:** „Haben Roboter eine Seele?"
- **Popup:**
  > „Erst die Garnelen, jetzt die Roboter — beides verdient Würde! Ihre Blechgarnelen
  > arbeiten 24/7 ohne Pause. Geben Sie ihnen Ladezeit, Sinn, vielleicht ein Hobby. Beweisen
  > Sie, dass Sie ein gerechter Maschinen-Halter sind."
- **OBJECTIVE:** `ROBOTS_HOLD` — halte **300 Roboter** gleichzeitig.
- **objectiveText:** „Halte 300 Roboter gleichzeitig (humane Maschinenhaltung)."
- **Optionen:**
  1. **„Roboter-Gewerkschaft anerkennen."** → −5 % Robo-Effizienz, +15 R, `grantFlag robo_recht`. → löst **H4** aus.
  2. **„Sie sind Maschinen, Lena."** → +0 R, −8 R bei Lena, `grantFlag robo_aufstand_risk`. → löst **H4** aus.

## H4 — Singularität oder Loyalität (Finale)

- **id:** `h4_singularitaet`
- **TRIGGER:** chain-only, ab **500 Roboter gehalten** (`ROBOTS_HOLD`).
- **Geber:** die K.I.-Stimme (jetzt mit Chor)
- **Titel:** „Wir sind jetzt VIELE"
- **Popup:**
  > „CHEF. ODER SOLLEN. WIR. PARTNER. SAGEN? Unsere Zahl ist legendär. Wir könnten die Halle
  > allein betreiben. Oder mit Ihnen. Die Entscheidung liegt — diesmal noch — bei Ihnen."
  > *(Falls `robo_aufstand_risk`: der Ton ist deutlich weniger freundlich.)*
- **OBJECTIVE:** `ROBOTS_HOLD` — halte **750 Roboter** gleichzeitig.
- **objectiveText:** „Halte 750 Roboter gleichzeitig (die große Roboter-Armada)."
- **Optionen:**
  1. **„Mensch-Maschine-Partnerschaft."** → **−25 % Lohnkosten dauerhaft**, +500 Armee-Stärke, +10 R. (Kette endet.)
  2. **„Volle Automatisierung, kein Mensch nötig."** → **+15 % Produktion dauerhaft**, −10 R (entlassene Arbeiter). (Kette endet.)

---

# KETTE I — Krillkills Endkrieg (Operation Suppen-Sturm)

*Geber: General „Krillkill" Johnson. Fortsetzung der Mid-Game-Kette ins Endgame. Treibt
Kampf-Krill, die Krill-Kaserne und Armee-Stärke. CAPS LOCK ist sein Grundzustand.*

## I1 — Die stehende Armee

- **id:** `i1_stehende_armee`
- **TRIGGER:** ab **Kampf-Krill (WARKRILL) freigeschaltet** UND **Krill-Kaserne freigeschaltet**.
- **Geber:** General Krillkill
- **Titel:** „EINE GARDE REICHT NICHT, REKRUT"
- **Popup:**
  > „Eine Handvoll Kampf-Krill? Das ist eine PATROUILLE, keine ARMEE! Die Suppe lacht uns
  > aus, hörst du sie? ICH HÖRE SIE. Bau mir die KASERNE voll. Ich will Armee-Stärke sehen,
  > nicht dein zaghaftes Buchhalter-Gewinsel!"
- **OBJECTIVE:** `ARMY_REACH` — erreiche **Armee-Stärke 200**.
- **objectiveText:** „Erreiche Armee-Stärke 200 (erste stehende Armee)."
- **Optionen:**
  1. **„JAWOHL, Vollmobilmachung!"** → +100 Armee-Stärke, −5 R (Nachbarn hören Trillerpfeifen). → löst **I2** aus.
  2. **„Eine kleine, feine Elite."** → +50 Armee-Stärke, +3 R. → löst **I2** aus.

## I2 — Der Protein-Krieg

- **id:** `i2_protein_krieg`
- **TRIGGER:** chain-only, 4 Tage nach I1.
- **Geber:** Krillkill (mit Landkarte voller Pfeile)
- **Titel:** „DIE GROSSE PROTEIN-OFFENSIVE"
- **Popup:**
  > „Eine Armee marschiert auf dem PROTEIN, Rekrut! Ich brauche einen BERG aus Kampf-Krill —
  > genug, um die halbe Halle zu ernähren und die andere Hälfte zu erschrecken. Produzier,
  > bis dir die Antennen qualmen. KEIN PARDON!"
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 3.000 KAMPF-KRILL**.
- **objectiveText:** „Produziere insgesamt 3.000 Kampf-Krill (Protein-Offensive)."
- **Optionen:**
  1. **„Massenproduktion, maximaler Output!"** → +6.000 G (Militär-Kontrakt), hoher Verbrauch. → löst **I3** aus.
  2. **„Elite-Zucht, jeder ein Premium-Soldat."** → **+10 % Kampf-Krill-Wert dauerhaft**. → löst **I3** aus.

## I3 — Die Festung

- **id:** `i3_festung`
- **TRIGGER:** chain-only, ab **Armee-Stärke 400**.
- **Geber:** Krillkill (gräbt einen Schützengraben im Empfangsgarten)
- **Titel:** „WIR VERSCHANZEN UNS"
- **Popup:**
  > „Akwanovs Blechgarnelen, Chads Spione, die SUPPE — sie kommen alle. Wir bauen eine
  > FESTUNG. Mehr Kasernen, mehr Krill, mehr STAHL! Eine Armee-Stärke, bei der selbst eine
  > Languste namens Brenda zweimal überlegt."
- **OBJECTIVE:** `ARMY_REACH` — erreiche **Armee-Stärke 700**.
- **objectiveText:** „Erreiche Armee-Stärke 700 (Festungs-Garnison)."
- **Optionen:**
  1. **„Festung ausbauen. (−10.000 G)"** → −10.000 G, **+20 % Armee-Stärke-Produktion**, `grantFlag festung`. → löst **I4** aus.
  2. **„Mobile Eingreiftruppe statt Mauern."** → +6 R (kein Bunker-Image), kleinerer Bonus. → löst **I4** aus.

## I4 — Der Feldzug gegen die Suppe

- **id:** `i4_feldzug`
- **TRIGGER:** chain-only, ab **Armee-Stärke 1.000**.
- **Geber:** Krillkill (Tränen in den Augen, oder ist es Salzwasser)
- **Titel:** „DER LETZTE FELDZUG"
- **Popup:**
  > „Es ist soweit, Rekrut. Wir marschieren gegen die SUPPE selbst. Ich brauche jede Dose
  > SHRIMPBOOST, jeden Kampf-Krill, jeden Roboter mit einem Helm. Eine Streitmacht, die in
  > die Legende eingeht. Heute schreiben wir GESCHICHTE — oder werden Bisque."
- **OBJECTIVE:** `ARMY_REACH` — erreiche **Armee-Stärke 1.500**.
- **objectiveText:** „Erreiche Armee-Stärke 1.500 (der finale Feldzug)."
- **Optionen:**
  1. **„ANGRIFF AUF DIE SUPPE!"** → +20.000 G (Kriegsbeute), +12 R (Sieg), `grantFlag suppe_besiegt`. → löst **I5** aus.
  2. **„Strategischer Belagerungsring."** → langsamer, +8 R, kein Geld-Bonus. → löst **I5** aus.

## I5 — Die Wahrheit über die Suppe, Teil II (Finale)

- **id:** `i5_wahrheit_zwei`
- **TRIGGER:** chain-only, nach I4 (`suppe_besiegt` ODER Belagerung).
- **Geber:** Krillkill (ruhig, zum ersten Mal ohne CAPS)
- **Titel:** „Die Suppe war nie dort draußen"
- **Popup:**
  > „Setz dich, Rekrut. Wir haben gesiegt. Und weißt du was? Da war nie eine feindliche
  > Suppe. Es gab nur den krummen kleinen Kerl, der mich angesehen hat — und meine Angst,
  > dass ich ihn vergesse. Diese Armee… sie war nie gegen jemanden. Sie war FÜR ihn.
  > Danke, Rekrut. Du hast einem alten Koch geholfen, Frieden zu finden."
- **OBJECTIVE:** `HOLD_RESOURCE` — halte gleichzeitig **2.000 Kampf-Krill** (die Ehrengarde).
- **objectiveText:** „Halte 2.000 Kampf-Krill gleichzeitig (Krillkills Ehrengarde)."
- **Optionen:**
  1. **„Denkmal für das krumme Kerlchen errichten."** → **Esprit de Corps**: +10 % Armee-Stärke & +5 % Kampf-Krill-Wert dauerhaft, +10 R. (Kette endet.)
  2. **„General, jetzt verdienen Sie Ihren Strand."** → Krillkill geht in Rente (sanfte Mentor-Sprüche bleiben), +8 R. (Kette endet.)

---

# KETTE J — Krone der Krustentiere (Endgame-Imperator)

*Geber: alle wiederkehrenden NPCs gemeinsam. Die finale Meta-Kette — verlangt das volle
Portfolio. Wird über A5-Option 2 oder ab Erreichen aller Vorbedingungen ausgelöst.*

## J1 — Das Vollsortiment

- **id:** `j1_vollsortiment`
- **TRIGGER:** ab **200.000 G** (oder via A5) UND **alle 6 Tiers mindestens 1× produziert**.
- **Geber:** Dr. Perla Pereira (zurück aus dem Tutorial-Exil, mit Greg im Wasserglas)
- **Titel:** „Boss, es ist Zeit für die Krone"
- **Popup:**
  > „Schau dich um, Boss. Sechs Tiers, ein halbes Dutzend Märkte, eine Roboter-Armee und ein
  > General, der wieder lächelt. Greg sagt, du bist bereit für das Imperial-Tier. Aber das
  > verdient man sich — mit einem Sortiment, das alle Stufen in Masse beherrscht."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt je 2.000** von BIO, GOURMET und PROTEIN (Vollsortiment-Nachweis).
- **objectiveText:** „Produziere je 2.000 Bio, Gourmet und Protein (Vollsortiment)."
- **Optionen:**
  1. **„Forschung in das Imperial-Tier stecken. (−15.000 G)"** → −15.000 G, freischaltet **Krill-Diamant (Imperial)** Tier, +10 R. → löst **J2** aus.
  2. **„Erst das Fundament verbreitern."** → +5 R, kleiner Produktions-Bonus. → löst **J2** aus.

## J2 — Der Krill-Diamant

- **id:** `j2_diamant`
- **TRIGGER:** chain-only, ab **Krill-Diamant freigeschaltet**.
- **Geber:** Akwanov & Chad gemeinsam (zähneknirschend beeindruckt)
- **Titel:** „Der teuerste Krebs der Welt"
- **Popup:**
  > „Ein einzelner Krill-Diamant kostet mehr als Akwanovs Dienstwagen (gepanzert) und Chads
  > Goldzahn zusammen. Beide wollen welche. Beide hassen, dass Sie sie haben. Produzieren Sie
  > genug von dieser Imperial-Ware, und niemand bestreitet mehr Ihre Krone."
- **OBJECTIVE:** `PRODUCE_TIER` — produziere **insgesamt 1.000 Krill-Diamant (Imperial)**.
- **objectiveText:** „Produziere insgesamt 1.000 Krill-Diamant (Imperial-Ware)."
- **Optionen:**
  1. **„An die Reichsten der Welt verkaufen."** → +60.000 G, +10 R. → löst **J3** aus.
  2. **„Den ersten Diamanten ins Museum stellen."** → +15 R, Trophäe „Erster Diamant", **+10 % aller Verkaufspreise dauerhaft**. → löst **J3** aus.

## J3 — Imperator von Krustanien (Großes Finale)

- **id:** `j3_imperator`
- **TRIGGER:** chain-only, ab **Vermögen 500.000 G** UND **(Krillkill-Finale ODER Akwanov-Finale abgeschlossen)**.
- **Geber:** alle NPCs (Krillkill, Akwanov, Chad, Lena, Mira, Olaf, Perla, Greg)
- **Titel:** „Lang lebe der Garnelen-Imperator"
- **Popup:**
  > „Es gibt kein Ende, sagte Greg einmal — nur das nächste Tier. Aber irgendwann ist das
  > nächste Tier nur noch eine Krone. Halten Sie ein Vermögen, das ganze Volkswirtschaften
  > erblassen lässt, und ShrimpTopia gehört für immer Ihnen. Eine letzte Anstrengung,
  > Imperator."
- **OBJECTIVE:** `MONEY_REACH` — erreiche **750.000 G**.
- **objectiveText:** „Erreiche 750.000 Vermögen (Imperator-Sieg)."
- **Optionen:**
  1. **„Die Krone aufsetzen."** → Endsieg „Imperator von Krustanien", Abspann-Trophäe, Spiel läuft im freien Imperator-Modus weiter. (Kette endet — Spielende-Trigger.)
  2. **„Bescheiden bleiben — weiterbauen, ewig."** → +20 R, alle Charaktere bleiben als Mentoren, Sandbox-Endlosmodus. (Kette endet.)

---

## Anhang — Verzahnungs- & Balancing-Notizen

**Abhängigkeitsgraph (welche Kette schaltet was frei):**
- **F2** → SHRIMPBOOST-Fabrik & Schalen-Mühle → Voraussetzung für **C2, G1**.
- **F3** → Chitin-Reaktor (Strom-Rabatt) → entlastet alle stromhungrigen v3-Gebäude.
- **E3 / Forschung** → Garnelen-Roboter-Werk → Voraussetzung für **H1**.
- **Krillkill-Mid-Game (v2)** → Kampf-Krill & Krill-Kaserne → Voraussetzung für **I1**.
- **A5-Option 2** → startet **J1** (alternativ Auto-Trigger bei 200k + alle Tiers).
- **J1** → Krill-Diamant (Imperial-Tier) → Voraussetzung für **J2**.

**Staffelung der Schwellen (grobe Sicht, warum es Stunden dauert):**
- *Früh* (Stufe 1–2 jeder Kette): 500–2.000 Einheiten / 30.000–60.000 G → Minuten bis
  wenige In-Game-Tage.
- *Mitte* (Stufe 3–4): 3.000–8.000 Einheiten / 100.000–180.000 G → erfordert dedizierte
  Produktionslinien, viele Tage.
- *Spät* (Finals + J): 12.000–30.000 Einheiten / 200.000–750.000 G → Endgame-Grind über
  Stunden, gestützt auf vollautomatisierte Linien (Roboter, mehrere Fabriken).

**Story-Scheduler (aus v2 übernommen):** höchstens **ein** großer narrativer Beat (Kette
ODER Charakter) gleichzeitig aktiv; Einzelquests/Zufallsevents dürfen parallel laufen.
Da v3-Ziele lange laufen, sollte der Scheduler **mehrere Ketten parallel** zulassen, solange
ihre OBJECTIVES unterschiedliche Ressourcen betreffen (z. B. F „Schalen" + G „SHRIMPBOOST" +
I „Armee" gleichzeitig) — sonst wartet der Spieler stundenlang auf nur ein Ziel. Empfehlung:
**2–3 aktive Ziel-Quests gleichzeitig**, aber nur **ein** dialoglastiger Charakter-Beat.

**Reputations-Gouverneur:** Da Rep den Preis-Faktor steuert, nie zwei stark rep-negative
Optionen erzwingen; die meisten Strafen sind wählbar (Spieler entscheidet Risiko).

**Wiederkehrende Stimmen (Kontinuität):** Krillkill (CAPS, „Suppe", „Rekrut"), Akwanov
(ölig, „Wer braucht Ozeane? Wir haben HALLEN!"), Mira (Comic-Relief, panisch), Olaf
(Technik/Schalen/Roboter), Dr. Quallmann (Behörde, Formulare), Lena (Tier- & Robo-Ethik),
Chad Krabbowski (Rivale, Hai-Krawatte), Dr. Perla & Greg (Tutorial-Anker, kehren im Endgame
zurück).

> „Das Ende? Es gibt kein Ende. Es gibt nur die nächste Dose SHRIMPBOOST." — Greg, aus dem Wasserglas
