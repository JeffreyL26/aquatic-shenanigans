# ShrimpTopia v3 — Das Konflikt-System: Krillkill vs. Akwanov

> "EINE ARMEE IST WIE EINE BISQUE, REKRUT: MAN KOCHT SIE ÜBER WOCHEN EIN, UND IN
> EINER EINZIGEN SEKUNDE KANN SIE ÜBERLAUFEN."
> — General Krillkill, beim Versuch, eine militärische Metapher zu finden, die KEINE Suppe ist

> *"Krieg? Ich bevorzuge das Wort 'sehr robuste Außenhandelsbilanz'. Aber gut — wenn Ihr
> General brüllen will, gebe ich ihm etwas zum Brüllen."*
> — Außenminister Ivan Akwanov, Usbekistan, beim Aufstellen seiner ersten Sabotage-Flotte

Dieses Dokument verschmilzt die beiden bestehenden Charakterbögen (`char_krillkill.md`,
`char_akwanov.md`) zu **einem echten Konflikt-System mit Armee-Stärke, Bedrohungen und
Taktiken**. Es baut auf der v2-Engine (`rivalry` 0-100, `exportTariff`, Flag-`sabotageResist`,
`Condition`-Typen DAY/MONEY/REP/RIVAL/SHRIMP_PRODUCED/BUILD_COUNT/FLAG, `QuestEffect`) und der
v3-Mechanik (Schalen, SHRIMPBOOST, Roboter, Armee-Stärke, neue Gebäude) auf.

Ton: deutsch, humorvoll, Tropico/Surviving-Mars. Keine Emojis. Stimmen unverändert weitergeführt.

**Designziel (Spieldauer):** Das Konflikt-System ist der zentrale **Endgame-Streckmechanismus**.
Alle Schwellen sind GROSS und gestaffelt, sodass der finale Krill-Krieg erst nach **mehreren
Stunden** und einem real aufgebauten Militär-Apparat erreichbar ist. Konflikte ziehen sich über
Tage (Bedrohungs-Aufbau + Kampf-Tick), nicht durchklickbar.

---

## TEIL 1 — ARMEE-STÄRKE (`army`, neue Ressource / abgeleiteter Wert)

Die Armee-Stärke ist eine **eigene HUD-Ressource** (`army`, Anzeige im Krillkill-/Rivalen-Panel,
nicht im Standard-HUD — sie erscheint erst, wenn die Krill-Kaserne steht). Sie ist KEIN reiner
Lagerbestand wie Schalen, sondern ein **gepuffertes Maximum mit Zerfall**: Produktion schiebt sie
hoch, Inaktivität und Konflikte ziehen sie runter.

### 1.1 Woraus speist sich `army`? (Zuflüsse pro Tick)

Pro Sim-Tick (1 Tick = 1 Tag) wird ein Ziel-Wert `armyTarget` berechnet und `army` nähert sich
ihm langsam an (siehe 1.3). `armyTarget` ist die Summe folgender Quellen:

| Quelle | Beitrag zu `armyTarget` | Bedingung / Hinweis |
|---|---|---|
| **Krill-Kaserne (Gebäude)** | je Kaserne **+25** Basis, skaliert mit Auslastung | Verbraucht pro Tick **Kampf-Krill-Bestand + SHRIMPBOOST**. Ohne Input: Beitrag halbiert. |
| **Kampf-Krill-Bestand (Tier WARKRILL)** | **+0,30 pro lebendem Kampf-Krill** | Direkt aus `shrimpByTier[WARKRILL]`. Das ist der Haupttreiber — eine Armee ist nur so groß wie ihre Truppe. |
| **Roboter (`robots`)** | **+0,8 pro Roboter** im "Wach"-Modus, **+0,4** im "Arbeits"-Modus | Roboter zählen wahlweise als Arbeiter ODER Armee (Politik-Schalter, siehe 1.5). |
| **Bootcamp (v2-Gebäude)** | **+1,5 pro Drill-Becken** im Wirkbereich | Wandelt rohen Kampf-Krill in "ausgebildete" Stärke um (Multiplikator-Vorstufe). |
| **Bootcamp-Politik "Dauerdrill"** | **× 1,15** auf den gesamten `armyTarget` | Arbeiter-Politik (siehe `WorkerPolicy`). Kostet +Upkeep + leichten Rep-Malus. |
| **Krillkill-Upgrade-Stufen** | additive/multiplikative Buffs, siehe 1.4 | Krillkills Quest-Kette liefert die Multiplikatoren. |
| **SHRIMPBOOST-Reserve (Energydrink)** | **× (1 + min(0,25; boost/200))** | Ein voller Energydrink-Vorrat puscht die ganze Armee um bis zu +25% ("Soldaten auf Koffein"). |

**Formel (transkribier-fertig):**

```
armyTarget =
    ( 25 * kaserneAktiv                       // Krill-Kaserne, je Stück (0.5x ohne Input)
    + 0.30 * kampfKrillBestand                // Tier WARKRILL lebend
    + robotArmyBeitrag                          // 0.8/Roboter (Wach) bzw. 0.4 (Arbeit)
    + 1.5 * drillBeckenImBootcampBereich )
  * krillkillMult                               // 1.0 .. ~1.6 (Upgrade-Stufen, Abschnitt 1.4)
  * (dauerdrillAktiv ? 1.15 : 1.0)              // Bootcamp-Politik
  * (1 + min(0.25, shrimpboost / 200.0))        // Energydrink-Boost
```

`kaserneAktiv` = Anzahl Krill-Kasernen, halbiert wenn der Tick-Input (Kampf-Krill + SHRIMPBOOST)
nicht reicht. `robotArmyBeitrag` = `0.8 * robotsWach + 0.4 * robotsArbeit`.

### 1.2 Grobe Zahlen-Anker (Balancing)

| Spielphase | Typische `army` | Wie erreicht |
|---|---|---|
| Vor Krill-Kaserne | 0 | Armee existiert noch nicht |
| Erste Kaserne, ~30 Kampf-Krill | **~35-45** | Reicht für die ersten 2-3 Konflikte (Grenzzwischenfall, Embargo-Patrouille) |
| Reife Militärbasis (2 Kasernen, 150 Kampf-Krill, 20 Roboter, Bootcamp) | **~120-160** | Mittelschwere Konflikte (Sabotage-Flotte, Spionage-Razzia) |
| Voll ausgebaut (3 Kasernen, 400+ Kampf-Krill, 60 Roboter-Wach, alle Krillkill-Upgrades, Dauerdrill, voller Boost) | **~350-450** | Bereit für den finalen Krill-Krieg (Bedrohung ~400) |

Die **Bedrohungsstärken** der Konflikte in Teil 2 sind so getaktet, dass man **immer ein Stück**
über den aktuell mühelosen Stand investieren muss — der finale Krieg verlangt fast das Maximum.

### 1.3 Wie zerfällt die Armee? (Abflüsse / Zerfall)

`army` ist verderblich — eine stehende Truppe will gefüttert, gedrillt und beschäftigt werden.

1. **Annäherung an `armyTarget`** (pro Tick):
   ```
   if (army < armyTarget)  army += min( 8, (armyTarget - army) * 0.20 );   // Aufbau langsam
   if (army > armyTarget)  army -= min(12, (army - armyTarget) * 0.30 );   // Abbau schneller
   ```
   Aufbau ist bewusst zäh (Armee braucht Wochen), Abbau schneller (Disziplin verfällt fix).
2. **Grund-Zerfall ("Friedensmüdigkeit"):** jeden Tick **−2% von `army`**, solange seit dem letzten
   Konflikt > 6 Tage vergangen sind. Krillkills Buff "Esprit de Corps" halbiert das auf −1%.
3. **Versorgungs-Kollaps:** Fällt der SHRIMPBOOST-Vorrat auf 0 ODER der Kampf-Krill-Bestand auf 0,
   sinkt `army` für diesen Tick um **−10% zusätzlich** ("die Truppe meutert ohne Energydrink").
4. **Konflikt-Verluste:** Jeder ausgefochtene Konflikt zieht je nach Ausgang einen festen
   `army`-Betrag ab (siehe Taktik-Tabellen) — Krieg kostet Substanz, auch bei Sieg.

### 1.4 Krillkill-Upgrades → `krillkillMult` (aus seiner v2-Kette + 3 neue v3-Stufen)

Die v2-Kette "Operation Protein-Sturm" endet bei Stufe 7. v3 hängt **drei Militär-Upgrade-Stufen**
an, die `krillkillMult` heben. Jede ist an ein **Produktions-Ziel** gebunden (nicht durchklickbar):

| Upgrade (Flag) | Effekt auf `krillkillMult` | Freischalt-Ziel (über Zeit) |
|---|---|---|
| `army.bootcamp` (aus v2 Stufe 3) | × 1,10 | Bootcamp gebaut + 50 Kampf-Krill insgesamt produziert |
| `army.protein_doktrin` | × 1,20 (statt 1,10) | Halte 5 Tage in Folge SHRIMPBOOST ≥ 40 |
| `army.kampfkrill_garde` | × 1,40 | Produziere insgesamt **300 Kampf-Krill** (SHRIMP_PRODUCED, Tier WARKRILL) |
| `army.eisenpanzer` (v3-neu) | × 1,55 + `sabotageResist`-Bonus +0,1 | Halte 200 Roboter-Stunden (kumuliert `robots`≥20 über 10 Tage) |
| `army.krill_doktrin_final` (v3-neu) | × 1,60 + Konflikt-Verluste −25% | Krillkill Stufe 7 abgeschlossen UND Krill-Kaserne ×2 gebaut |

`krillkillMult` ist immer der **höchste** freigeschaltete Wert (nicht kumulativ multipliziert).

### 1.5 Roboter als Doppel-Ressource (Arbeiter UND/ODER Armee)

Roboter (`robots`, aus dem Garnelen-Roboter-Werk) haben einen **Politik-Schalter** (analog
`WorkerPolicy`), der bestimmt, wohin ihr Beitrag fließt:

| Roboter-Politik | Wirkung |
|---|---|
| **"Schicht schieben" (Arbeit)** | Roboter zählen als Arbeiter (ersetzen je 1 menschlichen Arbeiter-Bedarf). Armee-Beitrag nur +0,4/Stk. |
| **"An die Front" (Wach)** | Roboter zählen NICHT als Arbeiter, aber +0,8/Stk auf `armyTarget`. |
| **"Geteilt 50/50"** | Halbe Arbeiterleistung, halber Armee-Beitrag. Bequem, aber ineffizient. |

Im Krieg darf man also kurzfristig Roboter von der Produktion an die Front umschalten — ein echter
taktischer Hebel (Produktion bricht ein, Armee steigt).

---

## TEIL 2 — DIE KONFLIKTE (10 eskalierende Ereignisse)

Konflikte sind eine **eigene Event-Kategorie `CONFLICT`** (verwandt mit `RIVAL`, aber mit
Taktik-Auswahl-Popup und Armee-Vergleich). Jeder Konflikt hat:

- **Trigger** (in Worten, an `rivalry` / `money` / `day` / Flags / `army` gebunden),
- **Bedrohungsstärke `threat`** (Zahl, gegen die `army` antritt),
- **Popup-Text** in beiden Stimmen (Akwanov droht, Krillkill brüllt zurück),
- **2-4 Taktik-Optionen**, deren Ausgang von `effArmy` vs. `threat` abhängt.

### 2.0 Ausgangs-Logik (für ALLE Konflikte gleich, transkribier-fertig)

Jede Taktik liefert einen **Effektiv-Stärke-Wert** `effArmy` (Armee × Taktik-Faktor, ggf. + Boni)
und vergleicht ihn mit `threat`:

```
ratio = effArmy / threat;

ratio >= 1.20  -> ERFOLG        (klarer Sieg, Boni)
0.85 <= ratio < 1.20 -> TEILERFOLG (knapper Sieg, gemischte Folgen)
ratio < 0.85   -> NIEDERLAGE    (Verlust, harte Folgen)
```

`sabotageResist` (Flag/Stufe aus Akwanov-Bogen + Krillkill `army.eisenpanzer`) **reduziert die
Niederlage-Folgen** um seinen Prozentwert. Jeder Konflikt verändert zusätzlich `rivalry` (Eskalation)
und zieht einen Konflikt-`army`-Verlust ab (durch `army.krill_doktrin_final` −25%).

**Taktik-Faktoren (gelten quer über alle Konflikte):**

| Taktik | `effArmy`-Faktor | Nebenwirkung |
|---|---|---|
| **Verteidigen** | × 1,00 (solide) | Geringster `army`-Verlust. Defensiv. |
| **Gegenangriff mit Kampf-Krill** | × 1,25, ABER verbraucht zusätzlich Kampf-Krill-Bestand | Höchste Schlagkraft, höchster Substanz-Verlust. |
| **Roboter-Blitz** | × 1,15, nur wenn `robots ≥ X` | Schnell, schont Kampf-Krill, verbraucht Energydrink + etwas `robots`. |
| **Diplomatie (Akwanov-Tee)** | ignoriert `army`, kostet Geld/Rep | "Sieg" durch Zahlung; `rivalry` sinkt; keine `army`-Verluste, aber teuer. |

---

### Konflikt 1 — "Garnelen-Grenzzwischenfall" (Tutorial des Systems)
- **ID:** `conf_grenz`
- **Trigger:** `rivalry ≥ 31` UND Krill-Kaserne ≥ 1 gebaut UND Tag ≥ 30.
- **Bedrohungsstärke:** `threat = 30`.

> *Akwanov:* "Ein bedauerliches Missverständnis, mein Freund. Eine usbekische Patrouille hat sich
> in Ihre Halle... verirrt. Sie suchten das Meer. Wie immer. Sie sind aus Versehen durch Ihr
> teuerstes Becken marschiert. Rein versehentlich, versteht sich."
>
> *Krillkill:* "DAS WAR KEIN VERSEHEN, REKRUT, DAS WAR EINE AUFKLÄRUNGSMISSION! STELLUNG BEZIEHEN!
> Endlich darf die Truppe zeigen, wofür ich sie gedrillt habe!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen** | — | Patrouille abgewiesen. +4 Rep, `rivalry` +5. `army` −3. | Becken leicht beschädigt, `multShrimp(0.95)`. `army` −5. | Becken geplündert: −600 Geld, Rep −3. `army` −8. |
| **Diplomatie ("Verirrte heimschicken")** | −500 Geld | Vorfall beigelegt, `rivalry` −5. Keine `army`-Verluste. | — | — |

*Lerneffekt:* führt das Taktik-Popup + den `effArmy`/`threat`-Vergleich ein. Bewusst leicht.

---

### Konflikt 2 — "Embargo-Durchsetzung: Die Zoll-Flottille"
- **ID:** `conf_embargo_patrol`
- **Trigger:** `exportTariff > 0` (Akwanov-Embargo aktiv, Stufe 2/6 seines Bogens) UND `rivalry ≥ 35`.
- **Bedrohungsstärke:** `threat = 55`.

> *Akwanov:* "Mein Zoll besteht jetzt auf 'physischer Kontrolle' jeder Lieferung. Meine Beamten
> sind sehr gründlich. Und sehr bewaffnet. Rein zur Sicherheit der Garnelen, natürlich."
>
> *Krillkill:* "DIE WOLLEN UNSERE EXPORTE AUF HOHER... HALLE AUFBRINGEN?! NICHT MIT MEINEN
> SOLDATEN! ESKORTIERT JEDEN CONTAINER, ALS WÄRE GOLD DRIN — ES IST PROTEIN, DAS IST BESSER!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen (Konvoi eskortieren)** | — | Embargo-Schikane gebrochen: `exportTariff` −0,10. `army` −5. | Halbe Ladung kommt durch, −400 Geld. `army` −7. | Ladung beschlagnahmt: −1.200 Geld, `exportTariff` +0,05. `army` −10. |
| **Roboter-Blitz** | `robots ≥ 10` | Flottille überrannt: `exportTariff` −0,15, Rep −2. `robots` −3, Boost −20. | wie Verteidigen-Teilerfolg, aber schneller. | `robots` −5, kein Embargo-Effekt. |
| **Diplomatie** | −2.000 Geld | "Bearbeitungsgebühr" zahlt sich frei: `exportTariff` −0,10, `rivalry` −8. | — | — |

---

### Konflikt 3 — "Spionage-Razzia: Operation Aktenkoffer"
- **ID:** `conf_razzia`
- **Trigger:** Akwanov-Bogen Stufe 4+ (Flag `dmitri` / Praktikant-Beat passiert) UND `rivalry ≥ 40`.
- **Bedrohungsstärke:** `threat = 70`. **Modifikator:** wenn `dataLeak == true` (Akwanov Stufe 1
  unterschrieben): `threat = 90` (er weiß, wo alles steht).

> *Akwanov:* "Eine Razzia? Ich würde es 'spontane unangekündigte Freundschaftsinspektion' nennen.
> Meine Leute holen nur ein paar Akten ab. Und Ihre Forschungsdaten. Und, wenn schon dabei, Ihre
> Zuchtprotokolle."
>
> *Krillkill:* "GEGENSPIONAGE, REKRUT! Ich habe Kampf-Krill in den Lüftungsschächten postiert —
> die kneifen ALLES, was ein Klemmbrett trägt. WO IST MEIN EISENPANZER-BATAILLON?!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen (Schächte sichern)** | — | Razzia vereitelt. +5 Rep, Forschung sicher. `army` −6. | Ein paar Akten weg: Rep −4. `army` −8. | Forschungsklau: nächstes Tier-Upgrade +50% teurer, Rep −8. `army` −12. |
| **Gegenangriff mit Kampf-Krill** | Kampf-Krill ≥ 40 | Spione "überzeugt": +6 Rep, `rivalry` +10. Kampf-Krill −20, `army` −10. | Rep −2, Kampf-Krill −20. | Rep −6, Kampf-Krill −20, `army` −14. |
| **Diplomatie (Tante bestechen)** | −1.500 Geld | Razzia abgesagt, `rivalry` −6, Bonus-Tipp im Finale. | — | — |

*`sabotageResist` reduziert hier die Niederlage-Rep-Verluste deutlich (bis −80%).*

---

### Konflikt 4 — "Akwanovs Erste Sabotage-Flotte"
- **ID:** `conf_flotte_1`
- **Trigger:** `rivalry ≥ 45` UND `money ≥ 40.000`.
- **Bedrohungsstärke:** `threat = 95`.

> *Akwanov:* "Usbekistan hat eine Flotte. Auf RÄDERN, zugegeben — wir haben kein Meer. Aber sie
> rollt, sie hupt, und sie ist UNTERWEGS zu Ihrer Halle. Betrachten Sie es als... freundschaftlichen
> Besuch mit Rammbock."
>
> *Krillkill:* "EINE FLOTTE OHNE WASSER?! DAS IST DER LÄCHERLICHSTE UND GEFÄHRLICHSTE GEGNER, DEN
> WIR JE HATTEN! GENAU MEIN GESCHMACK! ALLE MANN... ALLE ZEHN BEINE AN DECK!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen** | — | Flotte zurückgeschlagen. +6 Rep, `rivalry` +8. `army` −8. | 1 Gebäude beschädigt (1 Tag 0-Produktion). `army` −12. | 2 Becken zerstört (Bestand −25%), −2.000 Geld. `army` −18. |
| **Gegenangriff mit Kampf-Krill** | Kampf-Krill ≥ 60 | Flotte vernichtet, Kriegsbeute +2.500 Geld, Rep +4. Kampf-Krill −40, `army` −12. | +1.000 Geld, Kampf-Krill −40. `army` −15. | Kampf-Krill −40, 1 Becken zerstört. `army` −20. |
| **Roboter-Blitz** | `robots ≥ 20` | Flotte zerlegt, Schalen-Bonus +30 (Schrott). `robots` −6, Boost −30. | `robots` −8, leichter Schaden. | `robots` −10, 1 Gebäude beschädigt. |
| **Diplomatie** | −4.000 Geld | "Mautgebühr": Flotte dreht ab, `rivalry` −10. | — | — |

---

### Konflikt 5 — "Der Plankton-Blockade-Krieg"
- **ID:** `conf_blockade`
- **Trigger:** `rivalry ≥ 50` UND Tag ≥ 50.
- **Bedrohungsstärke:** `threat = 120`. **Angriff auf die Versorgung** (Futter/Wasser).

> *Akwanov:* "Ich habe alle Plankton-Routen aufgekauft. ALLE. Ihre Algenfarmen werden Hunger haben,
> Ihre Becken Durst. Hungrige Soldaten kämpfen schlecht — das habe ich in einem Ihrer eigenen
> Berichte gelesen. Danke dafür, übrigens."
>
> *Krillkill:* "ER GREIFT DEN NACHSCHUB AN! DAS IST KEIN GENTLEMAN, DAS IST EIN STRATEGE! WIR
> BRECHEN DIE BLOCKADE — NOTFALLS FRESSEN MEINE SOLDATEN DIE BLOCKADE!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen (Reserven mobilisieren)** | Futter ≥ 50 | Blockade ausgesessen. `rivalry` +6. `army` −8. Futter −30. | `multFeed(0.7)` für 3 Tage. `army` −12. | `multFeed(0.5)` + `multWater(0.7)` für 5 Tage. `army` −16. |
| **Gegenangriff (Konvoi erkämpfen)** | Kampf-Krill ≥ 80 | Routen befreit: +60 Futter, Rep +4. Kampf-Krill −50, `army` −14. | +30 Futter, Kampf-Krill −50. | Blockade hält, Kampf-Krill −50, `army` −20. |
| **Diplomatie (Liefervertrag erkaufen)** | −5.000 Geld | Blockade umgangen, `rivalry` −8, dauerhaft +Futter-Bonus klein. | — | — |

---

### Konflikt 6 — "Der Stromnetz-Überfall" (Eskalations-Vorbote)
- **ID:** `conf_stromnetz`
- **Trigger:** `rivalry ≥ 55` UND Akwanov-Bogen Stufe 7 (Stromstecker-Beat) erreicht.
- **Bedrohungsstärke:** `threat = 150`.

> *Akwanov:* "Ihr Stromnetz... wie soll ich sagen... gehört jetzt teilweise mir. Eine feindliche
> Übernahme der Trafostation. Heute Nacht wird es dunkel, und Ihre Garnelen sind im Dunkeln so
> schreckhaft. Schlaft gut. Falls Sie Strom dafür haben."
>
> *Krillkill:* "ER KAPPT DEN SAFT! OHNE STROM KEINE PUMPEN, OHNE PUMPEN KEINE PARTY — UND OHNE
> PARTY KEINE ARMEE! ROBOTER AN DIE TRAFOSTATION, SOFORT! WIR HOLEN UNS DEN SAFT ZURÜCK!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Roboter-Blitz (Trafostation stürmen)** | `robots ≥ 25` | Netz zurückerobert, +5 Rep. `robots` −8, Boost −40. `army` −10. | Strom 2 Tage auf 70%. `robots` −10. | Strom 4 Tage auf 60%, Upkeep +60% für 5 Tage. `robots` −12. |
| **Verteidigen (Inselnetz hochfahren)** | Solar-Dach ≥ 1 | Strompreis-unabhängig, +5 Rep. `army` −10. | leichte Drosselung 2 Tage. `army` −13. | volle Drosselung. `army` −16. |
| **Diplomatie (Notreserve + Bestechung)** | −3.500 Geld | Spike abgefedert, `rivalry` −5. | — | — |

---

### Konflikt 7 — "Die Söldner-Affäre: Chad Krabbowski steigt ein"
- **ID:** `conf_chad`
- **Trigger:** `rivalry ≥ 58` UND Krabbo-Fehde-Kette (D) abgeschlossen ODER `money ≥ 60.000`.
- **Bedrohungsstärke:** `threat = 180`.

> *Akwanov:* "Ich habe Verstärkung angeheuert. Ihren alten Freund Chad Krabbowski. Er hasst Sie
> fast so sehr wie ich, und er arbeitet billiger. Eine Söldner-Allianz der Beleidigten, wenn Sie so
> wollen."
>
> *Krillkill:* "KRABBOWSKI?! DIESER GLATTE KRABBEN-SCHNÖSEL! Der hat noch NIE einen ehrlichen
> Kampf-Krill von innen gesehen! Wir zeigen ihm, was eine echte Division ist — KEINE GNADE FÜR
> KRABBEN!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Gegenangriff mit Kampf-Krill** | Kampf-Krill ≥ 100 | Chad gedemütigt: +3.000 Geld, Rep +6, `rivalry` +10. Kampf-Krill −60, `army` −16. | Chad flieht: +1.000 Geld, Kampf-Krill −60. | Chad plündert: −3.000 Geld, 2 Becken zerstört. Kampf-Krill −60, `army` −22. |
| **Roboter-Blitz** | `robots ≥ 35` | Söldner zerstreut, Schalen +50 (Beute). `robots` −10, Boost −50. | leichter Schaden, `robots` −12. | `robots` −15, −1.500 Geld. |
| **Diplomatie (Chad abwerben, −8.000 Geld)** | −8.000 Geld | Chad wechselt die Seite (Flag `chad_allied`): hilft im Finale. `rivalry` −5. | — | — |

---

### Konflikt 8 — "Belagerung: Die Trockenhafen-Armada"
- **ID:** `conf_belagerung`
- **Trigger:** `rivalry ≥ 62` UND `money ≥ 70.000`.
- **Bedrohungsstärke:** `threat = 240`.

> *Akwanov:* "Der gesamte 'Trockenhafen von Taschkent' marschiert auf. Tausend Container-LKW, jeder
> mit einem wütenden Beamten. Wir umzingeln Ihre Halle. Niemand rein, niemand raus, kein Export.
> Eine klassische Belagerung — nur staubiger, weil, nun ja, kein Meer."
>
> *Krillkill:* "EINE BELAGERUNG! ENDLICH EIN RICHTIGER KRIEG! REKRUT, DAS IST DER MOMENT, FÜR DEN
> ICH JEDEN EINZELNEN KAMPF-KRILL GEZÜCHTET HABE! HALTET DIE STELLUNG ODER STIRBT ALS HELDEN — ABER
> BITTE NICHT ALS SUPPE!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen (Festung halten)** | — | Belagerung gebrochen: +8 Rep, `rivalry` +10, `exportTariff` −0,15. `army` −20. | 5 Tage Export-Stopp (`exportTariff` +0,20 temporär). `army` −28. | Halle teilweise gestürmt: −5.000 Geld, 3 Becken zerstört, Rep −10. `army` −35. |
| **Gegenangriff mit Kampf-Krill** | Kampf-Krill ≥ 150 | Armada zerschlagen: +5.000 Geld Beute, Rep +6. Kampf-Krill −90, `army` −22. | +2.000 Geld, Kampf-Krill −90. | Kampf-Krill −90, 2 Becken zerstört, `army` −30. |
| **Roboter-Blitz (Durchbruch)** | `robots ≥ 50` | Belagerungsring gesprengt, +4 Rep. `robots` −18, Boost −80. | `robots` −20, leichter Schaden. | `robots` −25, Belagerung hält 3 Tage. |
| **Diplomatie (Welthandels-Klage, −10.000)** | −10.000 Geld | Belagerung in 8 Tagen aufgelöst, `rivalry` +15 (er ist sauer). | — | — |

---

### Konflikt 9 — "Der Doppelagenten-Verrat" (Wendepunkt)
- **ID:** `conf_verrat`
- **Trigger:** `rivalry ≥ 65` UND (Flag `doppelagent` aktiv ODER Flag `dataLeak` aktiv). Knüpft an
  Akwanov-Stufe 4-Entscheidung an. Bewusst KEIN reiner Stärke-Kampf — ein Informations-Konflikt.
- **Bedrohungsstärke:** `threat = 280`.

> *Akwanov:* "Mir ist etwas zu Ohren gekommen. Ein Verräter in MEINEN Reihen — oder in IHREN? Einer
> von uns hat einen Maulwurf, und ich fürchte, gleich erfahren wir beide, bei wem. Spannend, nicht?
> Ich liebe einen Cliffhanger."
>
> *Krillkill:* "MAULWÜRFE?! IN MEINER DIVISION?! Ich durchsuche JEDEN Soldaten, JEDES Becken,
> JEDEN Lüftungsschacht! Niemand verrät die 1. Krustentier-Division und kommt damit ALGENFREI
> davon!"

| Taktik | Bedingung | Erfolg | Teilerfolg | Niederlage |
|---|---|---|---|---|
| **Verteidigen (Spionageabwehr)** | `sabotageResist`-Flag | Maulwurf bei Akwanov enttarnt: Rep +8, `rivalry` +12, Akwanov verliert 1 Beat-Vorteil. `army` −12. | unentschieden, Rep −2. `army` −16. | Dein Maulwurf enttarnt: −4.000 Geld, Forschungs-Datenklau, Rep −8. `army` −20. |
| **Gegenangriff (Gegen-Leak)** | Flag `doppelagent` | Akwanovs Akten leaken: Rep +10, `rivalry` +20. Riesen-Lacher ("DMITRI!"). `army` −10. | Rep +2, `rivalry` +5. | Leak verpufft, Rep −4. `army` −15. |
| **Diplomatie (gemeinsamen Maulwurf opfern)** | — | Beide ziehen den Maulwurf ab: `rivalry` −15 (Deeskalation!), führt eher zu Ende A. | — | — |

*Dieser Konflikt **steuert das Finale**: hohe Aggression hier → Ende B (Eskalation); Diplomatie → Ende A (Allianz); `chad_allied` + Sieg → Ende C (Aufkauf).*

---

### Konflikt 10 — "DER FINALE KRILL-KRIEG" (Showdown, dreigeteiltes Ende)
- **ID:** `conf_krillkrieg`
- **Trigger:** `rivalry ≥ 66` ("Kalter Krill-Krieg") UND `money ≥ 80.000` UND `army ≥ 200` UND
  Krillkill Stufe 7 abgeschlossen. **Alle Voraussetzungen müssen erfüllt sein** — das ist der
  Endgame-Gate nach mehreren Stunden Aufbau.
- **Bedrohungsstärke:** `threat = 400` (skaliert mit `rivalry` bis 480). Höchste im Spiel.

> *Akwanov:* "Es ist soweit, mein würdigster Rivale. Mein letztes, hässlichstes Aktenstück ist
> geöffnet. Die Gesamtstreitkräfte Usbekistans — alle drei LKW und der ganze Stolz eines Landes
> ohne Meer — stehen vor Ihrem Tor. Heute entscheidet sich, wer die Garnele des 21. Jahrhunderts
> züchtet. Ich habe Tee mitgebracht. Und eine Armada. Und, nun ja, auch eine bildliche Bombe."
>
> *Krillkill:* "DAS IST ER, REKRUT! DER KRIEG, VOR DEM ICH MEIN GANZES LEBEN GEWARNT HABE! NICHT
> 'DIE SUPPE' — etwas VIEL SCHLIMMERES: EIN MANN MIT KRAWATTENNADEL UND OHNE OZEAN! ALLE SOLDATEN
> AN DIE FRONT! HEUTE SCHREIBEN WIR GESCHICHTE — ODER WIR WERDEN BISQUE!"

| Taktik | Bedingung | Ausgang abhängig von `effArmy` vs. `threat=400` |
|---|---|---|
| **Totaler Gegenangriff (alles)** | Kampf-Krill ≥ 250 | **ERFOLG → ENDE B (Totale Eskalation, Sieg):** Akwanov kapituliert. Achievement "Krill or be krilled". Permanenter Buff "Kriegsheld" (+20% Militär-Markt-Preis). Kampf-Krill −150, `army` −60. **TEILERFOLG:** Pyrrhussieg, halbe Halle zerstört, aber Akwanov zieht ab. **NIEDERLAGE:** Halle verwüstet, −20.000 Geld, zurück auf Konflikt 8. |
| **Eiserne Verteidigung + Roboter-Wall** | `robots ≥ 60`, `sabotageResist` | **ERFOLG:** Belagerung scheitert endgültig, Diplomatie-Option danach erzwingbar (Übergang zu Ende A/C). **NIEDERLAGE:** wie oben. |
| **Diplomatie: Die Hallen-Garnelen-Union** | `rivalry`-Verlauf eher niedrig / viele Zugeständnisse | **ENDE A (Allianz):** Embargo dauerhaft weg, "Akwanov-Subvention" (+12% Verkaufspreis), Rep +15. Achievement "Doppelt eingeschlossen, gemeinsam reich." Krillkill grummelt, akzeptiert. |
| **Feindlicher Aufkauf** | `money ≥ 120.000` UND Flag `doppelagent`/`chad_allied` | **ENDE C (Aufkauf):** −40.000 Geld, du übernimmst Usbekistan komplett. Akwanov wird dein Lobbyist, Krillkill dein "Verteidigungsminister a.D.". Achievement "Außenminister, gekauft und bezahlt." |

---

## TEIL 3 — KONVERGENTE STORYLINE: Krillkill liefert, Akwanov bedroht

Die beiden v2-Bögen laufen parallel, aber das v3-Konflikt-System **verzahnt sie zu einer Achse**:

```
          KRILLKILL-ACHSE (liefert ARMEE)              AKWANOV-ACHSE (liefert BEDROHUNG)
          ----------------------------------           ----------------------------------
Mid   ┌─ Operation Protein-Sturm 1-7 (v2) ─┐           ┌─ Charme-Offensive / Tarif (v2) ─┐
      │  Drill-Becken, Bootcamp, Kampf-Krill│           │  exportTariff, dataLeak         │
      └──────────────┬─────────────────────┘           └────────────┬───────────────────┘
                     │ schaltet frei: Krill-Kaserne                  │ erhöht: rivalry
                     ▼                                               ▼
Late          army.protein_doktrin ───►  KONFLIKTE 1-5  ◄─── Sabotage/Embargo/Razzia
              army.kampfkrill_garde       (Bedrohung 30-120)
              army.eisenpanzer
                     │                                               │
                     ▼                                               ▼
End      army.krill_doktrin_final ──►  KONFLIKTE 6-9  ◄── Stromüberfall/Söldner/Belagerung/Verrat
         (krillkillMult bis 1.60)        (Bedrohung 150-280)
                     │                                               │
                     └──────────────► KONFLIKT 10 ◄──────────────────┘
                                   DER FINALE KRILL-KRIEG
                                      (Bedrohung 400+)
                                   ┌──────┼──────┐
                                Ende A   Ende B   Ende C
                              (Allianz)(Eskal.)(Aufkauf)
```

### 3.1 Verknüpfungs-Regeln (transkribier-fertig)

- **Krillkill-Quest-Stufen schalten Armee-Bausteine frei**, Akwanov-Stufen heben `rivalry` und
  starten Konflikte. Krillkill ohne Akwanov = friedliche Armee ohne Gegner (Konflikte triggern nie).
  Akwanov ohne Krillkill = Bedrohung ohne Verteidigung (jeder Konflikt endet in Niederlage → harte
  Strafen). Das Spiel **zwingt** den Spieler sanft, **beide** Bögen zu spielen.
- **`rivalry` ist die gemeinsame Eskalationsleiter.** Konflikt-Trigger hängen an `rivalry`-Schwellen
  (31 → 45 → 55 → 66). Aggressive Taktiken (Gegenangriff) heben `rivalry`, Diplomatie senkt es.
- **Der `rivalry`-Verlauf bestimmt das Ende:**
  - überwiegend Diplomatie/Zugeständnisse → **Ende A (Allianz)**.
  - überwiegend Gegenangriff/hartes Spiel, `rivalry` 66+ → **Ende B (Eskalation/Sieg)**.
  - hohe `money` + Doppelagent/Chad-Allianz → **Ende C (Aufkauf)**.

### 3.2 Die drei Enden (Storyline-Konvergenz)

| Ende | Bedingung | Krillkills Reaktion | Akwanovs Reaktion |
|---|---|---|---|
| **A — Allianz** | niedriger `rivalry`-Verlauf, Konflikt-10-Diplomatie | "EIN WAFFENSTILLSTAND?! ...Na gut. Aber meine Soldaten BLEIBEN scharf. Frieden ist nur Krieg mit besserem Catering." | "Die Vereinigte Hallen-Garnelen-Union! Gemeinsam machen wir den Ozean arbeitslos." |
| **B — Eskalation/Sieg** | `rivalry` 66+, Gegenangriff, `army` siegt | "WIR HABEN GEWONNEN, REKRUT! Für jeden Kampf-Krill, für das krumme Kerlchen, FÜR DIE EHRE!" *(heult ein bisschen)* | "Sie... haben gewonnen. Mit HALLEN. Ich züchte jetzt Karpfen. In einer Badewanne." |
| **C — Aufkauf** | `money ≥ 120k`, Doppelagent/Chad | "Sie haben den FEIND GEKAUFT?! Das ist... eigentlich die heimtückischste Taktik überhaupt. Respekt, Rekrut." | "Sie wollen mein Ministerium KAUFEN? Ich bin gerührt. Ich bin beleidigt. Ich nehme an." |

---

## TEIL 4 — NEUE GEBÄUDE & QUEST-OBJECTIVES (Engine-Anbindung)

### 4.1 Neue Gebäude (aus der v3-Mechanik, plus 2 Vorschläge)

| Gebäude | Input → Output | Konflikt-Rolle |
|---|---|---|
| **SHRIMPBOOST-Fabrik** | Shrimps + Schalen + Strom → SHRIMPBOOST | Energydrink puscht `army` (+25%) und ist Input der Kaserne/des Roboter-Werks. |
| **Garnelen-Roboter-Werk** | Schalen + SHRIMPBOOST + Strom → `robots` | Roboter = Arbeiter ODER Armee (Politik-Schalter). |
| **Krill-Kaserne** | Kampf-Krill + SHRIMPBOOST → `army` / Verteidigung | Kern-Armee-Produzent. Pflicht-Gate für Konflikt 1. |
| **Verteidigungs-Bunker** *(Vorschlag)* | Strom + Beton (Geld-Upkeep) → `sabotageResist` +Festungsbonus | Gibt einen **flachen `army`-Bonus bei der Taktik "Verteidigen"** (+15% `effArmy` defensiv) und dämpft Niederlage-Folgen. |
| **Drohnen-Schwarm-Hangar** *(Vorschlag)* | `robots` + SHRIMPBOOST + Strom → "Drohnen" | Schaltet die Taktik **"Roboter-Blitz"** mit niedrigeren `robots`-Schwellen frei und gibt ihr +10% `effArmy`. |

### 4.2 Quest-Objectives (zielgebunden, NICHT durchklickbar)

Alle Konflikt-Trigger und Armee-Upgrades nutzen **bestehende `Condition`-Typen** plus ein paar
neue, über Zeit erfüllbare Ziele (engine-seitig zu ergänzen — analog `SHRIMP_PRODUCED`):

| Ziel-Typ | Beispiel-Schwelle (Endgame, GROSS) | Engine |
|---|---|---|
| `SHRIMP_PRODUCED` (Tier WARKRILL) | 300 / 800 / 2.000 Kampf-Krill kumuliert | vorhanden, Tier-Filter ergänzen |
| `RESOURCE_HELD` (neu: `army`) | `army ≥ 100 / 200 / 400` | neu (analog MONEY/REP) |
| `RESOURCE_HELD` (neu: SHRIMPBOOST/Schalen/Roboter) | halte ≥ 40 / 200 / 60 | neu |
| `RESOURCE_TOTAL` (neu: produziere insgesamt N SHRIMPBOOST) | 500 / 2.000 | neu (kumulativer Zähler) |
| `BUILD_COUNT` (Krill-Kaserne / Roboter-Werk) | 1 / 2 / 3 | vorhanden |
| `MONEY` / `DAY` / `RIVAL` / `REP` | siehe Konflikt-Trigger | vorhanden |
| `CONFLICT_WON` (neu) | "gewinne Konflikt X" als Folge-Gate | neu (Flag pro Konflikt-Sieg) |

**Staffelung für Spieldauer (mehrere Stunden):** Die Schwellen für die finalen Armee-Upgrades
(300+ Kampf-Krill kumuliert, `army ≥ 200`, `money ≥ 80.000` für Konflikt 10) sind bewusst so hoch,
dass der Aufbau der Militär-Achse den Großteil der zusätzlichen Spielzeit ausmacht — die ~30-Minuten-
Version ist damit auf ein mehrstündiges Endgame gestreckt, ohne den Spieler durchklicken zu lassen.

### 4.3 Engine-To-dos (knapp)

- [ ] `GameState`: Felder `army`, `armyTarget`, `robots`, `robotPolicy`, `shrimpboost`, `schalen`,
      kumulative Zähler (`totalWarkrill`, `totalBoost`); Tick-Logik aus Teil 1 (Aufbau/Zerfall).
- [ ] `army`-Beitrag der Krill-Kaserne / Roboter / Bootcamp pro Tick berechnen; `krillkillMult`
      aus den Upgrade-Flags ableiten.
- [ ] Neue Event-Kategorie `CONFLICT` (analog `RIVAL`), mit Taktik-Auswahl-Popup (2-4 Buttons),
      `effArmy`/`threat`-Vergleich (Teil 2.0), Erfolg/Teilerfolg/Niederlage-Effekt-Sätze.
- [ ] `Condition`/`QuestEffect` um Armee-/Ressourcen-Ziele erweitern (4.2).
- [ ] Krill-Kaserne / SHRIMPBOOST-Fabrik / Roboter-Werk / Bunker / Hangar als `BuildingType` + Meta.
- [ ] Rivalen-Panel um **Armee-Stärke-Balken** und **`effArmy` vs. `threat`-Vorschau** im aktiven
      Konflikt erweitern.
- [ ] Drei Enden (A/B/C) als Konflikt-10-Verzweigung + Achievements.

> "Am Ende, Rekrut, zählt nicht, wie viele Garnelen du gezüchtet hast. Es zählt, wie viele davon
> SALUTIERT haben." — General Krillkill, Schlussansprache
