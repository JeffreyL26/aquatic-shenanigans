# Zufallsereignisse v3 - Schalen, SHRIMPBOOST, Roboter, Armee & HQ

14 brandneue Zufallsereignisse rund um die v3-Mechanik: das Nebenprodukt **Schalen**,
den Energydrink **SHRIMPBOOST**, die garnelenbetriebenen **Roboter**, die **Armee-Stärke**
(Krill-Kaserne) und das wachsende **HQ**. Ton wie gehabt: Tropico-frech, Surviving-Mars-trocken,
vermenschlichte Garnelen, meme-bewusst, keine Emojis. Popup-Texte kurz und knackig.

Bestehende Charaktere geben weiter den Ton an: **General "Krillkill" Johnson** (brüllend,
militärisch, paranoid, nennt Shrimps "Soldaten"), **Außenminister Akwanov** (ölig-diplomatisch,
"Wer braucht schon Ozeane? Wir haben HALLEN!"), **Dr. Perla Pereira**, **Mira**, **Olaf** (Technik),
**Dr. Quallmann** (Behörde), **Lena** (Tierschutz), **Chad Krabbowski** (Rivale).

---

## Konventionen (passend zum bestehenden `EventSystem` / `GameEvent`)

- **Art**: `GOOD` / `BAD` / `NEUTRAL` (entspricht `GameEvent.Kind`).
- **Vorhandenes Effekt-Vokabular** (`GameState`): `addMoney(x)`, `addReputation(x)`,
  `addFeed(x)`, `addWater(x)`, `multWater(f)`, `multShrimp(f)`, `addShrimp(tier, x)`,
  `getMoney()`, `getReputation()`, `getShrimp()`, `getDay()`, `getWorkersAvail()`.
- **Neues v3-Vokabular** (Engine setzt es um; hier kursiv markiert): *`addShells(x)`* /
  *`multShells(f)`* / *`getShells()`* (Schalen), *`addBoost(x)`* / *`getBoost()`* (SHRIMPBOOST),
  *`addRobots(x)`* / *`getRobots()`* (Roboter), *`addArmy(x)`* / *`multArmy(f)`* / *`getArmy()`*
  (Armee-Stärke), *`getRivalry()`* (Rivalität), *`addPower(x)`* (gepufferter Strom).
- **Bedingung** = Prädikat auf `GameState`, in Worten beschrieben. Leer = immer möglich.
- **Optionen**: Einige Events bieten eine Spieler-Wahl (Tropico-Dilemma). Wo keine Optionen
  stehen, feuert das Event direkt mit seinem Effekt.
- **Cooldown/Trigger** wie gehabt über `EventSystem` (Mindestabstand + Wahrscheinlichkeit);
  zusätzliche Schwellen über die `condition` unten.

---

## GOOD - Erfreuliche Ereignisse

### v3-e01 - Schalen-Recycling-Wunder
**Art:** GOOD
**Text:** Olaf hat aus dem Schalenberg ein Granulat gepresst, das brennt wie Kohle. "Müll? Das ist Brennstoff, Chef!"
**Effekt-Idee:** *`addShells(-40)`* und dafür *`addPower(+60)`*; `addReputation(+2)`. Falls kein Strompuffer existiert: `addMoney(+900)`.
**Bedingung:** *`getShells() >= 40`* (genug Schalen im Lager), Kraftwerk gebaut.

### v3-e02 - SHRIMPBOOST sponsert E-Sport-Turnier
**Art:** GOOD
**Text:** Ein Pro-Gamer trinkt live deinen Energydrink und gewinnt das Finale. "Powered by Garnele" steht jetzt auf jedem Trikot.
**Effekt-Idee:** *`addBoost(-25)`* (Sponsoring-Lager), `addMoney(+3200)`, `addReputation(+9)`.
**Bedingung:** *`getBoost() >= 25`* (SHRIMPBOOST-Fabrik liefert bereits).

### v3-e03 - Roboter macht Überstunden umsonst
**Art:** GOOD
**Text:** Ein Garnelen-Roboter weigert sich, Feierabend zu machen: "Mein Akku ist Koffein, nicht Strom." Die Nachtschicht jubelt.
**Effekt-Idee:** `multShrimp(1.12)` für diesen Zyklus, `addReputation(+3)`.
**Bedingung:** *`getRobots() >= 2`* (mindestens zwei Roboter aktiv).

### v3-e04 - Krillkill bekommt einen Orden
**Art:** GOOD
**Text:** General Krillkill wird für "herausragende Garnelen-Landesverteidigung" dekoriert und brüllt vor Stolz drei Stunden lang.
**Effekt-Idee:** *`multArmy(1.20)`*, `addReputation(+5)`.
**Bedingung:** *`getArmy() >= 50`* (Krill-Kaserne liefert nennenswerte Armee-Stärke).

### v3-e05 - Akwanov verkauft Energydrink ins Binnenland
**Art:** GOOD
**Text:** "Wer braucht schon Ozeane? Wir haben HALLEN - und jetzt auch Energydrinks!" Akwanov öffnet drei neue Wüstenstaaten als Markt.
**Effekt-Idee:** *`addBoost(-30)`*, `addMoney(+4500)`, `addReputation(+6)`.
**Bedingung:** *`getBoost() >= 30`* und Export-Hafen gebaut.

### v3-e06 - HQ-Renovierung beeindruckt Investoren
**Art:** GOOD
**Text:** Das frisch ausgebaute Hauptquartier hat jetzt eine Garnelen-Lobby aus Aquarienglas. Ein Investor unterschreibt im Stehen.
**Effekt-Idee:** `addMoney(+5000)`, `addReputation(+7)`.
**Bedingung:** HQ mindestens einmal ausgebaut/aufgewertet; `getMoney() > 6000`.

---

## BAD - Ärgerliche Ereignisse

### v3-e07 - Schalen-Lawine im Lager
**Art:** BAD
**Text:** Der ungesicherte Schalenberg kippt um und begräbt drei Förderbänder. Dr. Quallmann schickt schon mal das Bußgeld-Formular.
**Effekt-Idee:** *`multShells(0.5)`* (halber Schalenvorrat verschüttet), `addMoney(-1100)`, `addReputation(-3)`.
**Bedingung:** *`getShells() >= 80`* (Lager überfüllt - Strafe fürs Horten).

### v3-e08 - SHRIMPBOOST-Rückruf
**Art:** BAD
**Text:** Eine Charge Energydrink war "etwas zu energetisch" - drei Tester schweben noch an der Decke. Behörde ordnet Rückruf an.
**Effekt-Idee:** *`addBoost(-40)`* (Charge vernichtet), `addMoney(-1800)`, `addReputation(-8)`.
**Bedingung:** *`getBoost() >= 40`*, Dr. Quallmann/Behörde aktiv.

### v3-e09 - Roboter-Aufstand (kurz)
**Art:** BAD
**Text:** Die Garnelen-Roboter bilden eine Gewerkschaft und fordern "Recht auf Salzwasser-Pause". Olaf verhandelt mit WD-40.
**Effekt-Idee:** *`getRobots()` Roboter pausieren* einen Zyklus -> `multShrimp(0.85)`; `addMoney(-700)` Schmierstoff- und Beruhigungskosten.
**Bedingung:** *`getRobots() >= 4`* (große Roboter-Belegschaft).

### v3-e10 - Chad Krabbowski klaut die Rezeptur
**Art:** BAD
**Text:** Rivale Chad bringt "CHAD-BOOST" auf den Markt - verdächtig ähnlich. Er grinst auf jeder Litfaßsäule der Stadt.
**Effekt-Idee:** `addMoney(-2400)`, `addReputation(-5)`, *Rivalität steigt (`getRivalry()` +)*.
**Bedingung:** SHRIMPBOOST-Fabrik gebaut; `getReputation() > 40`.

### v3-e11 - Kaserne überzieht das Budget
**Art:** BAD
**Text:** General Krillkill bestellt 400 Mini-Helme "für die Soldaten". Niemand hat den Mut, die Rechnung zu hinterfragen.
**Effekt-Idee:** `addMoney(-2000)`; *`multArmy(0.9)`* (Helme passen nicht, Moral sinkt leicht).
**Bedingung:** Krill-Kaserne gebaut; *`getArmy() >= 30`*.

### v3-e12 - Kampf-Krill bricht aus dem HQ aus
**Art:** BAD
**Text:** Ein Trupp Kampf-Krill stürmt das Hauptquartier und besetzt den Kaffeeautomaten. Verhandlungen laufen, der Kaffee wird kalt.
**Effekt-Idee:** *`multArmy(0.8)`* (Deserteure), `addReputation(-4)`, `addMoney(-600)` Aufräumkosten.
**Bedingung:** *`getArmy() >= 40`* und Kampf-Krill-Becken in Betrieb.

---

## NEUTRAL / GEMISCHT - Es kommt drauf an (mit Optionen)

### v3-e13 - Akwanov bietet Waffen-für-Schalen-Deal
**Art:** NEUTRAL
**Text:** Akwanov flüstert ölig: "Ein befreundetes Binnenland zahlt fürstlich für Schalen - fragen Sie nicht, wofür."
**Bedingung:** *`getShells() >= 60`*.
**Optionen:**
- **A) Deal annehmen:** *`addShells(-60)`*, `addMoney(+3000)`, `addReputation(-4)` (zwielichtig).
- **B) Ablehnen:** `addReputation(+3)`, kein Geld. Akwanov ist beleidigt (*Rivalität/Diplomatie-Hook optional*).

### v3-e14 - Krillkill fordert Mobilmachung
**Art:** NEUTRAL
**Text:** "Chad Krabbowski hat eine GARNELE über unsere Mauer geworfen! Das ist KRIEG, Soldaten!" Krillkill will alle Becken auf Kampf-Krill umstellen.
**Bedingung:** Krill-Kaserne gebaut; *`getRivalry()` mittel oder hoch*.
**Optionen:**
- **A) Mobilmachen:** *`addArmy(+40)`*, aber `multShrimp(0.8)` (Produktion gedrosselt) und *`addBoost(-15)`* (Truppen tanken).
- **B) Krillkill beruhigen:** `addMoney(-500)` (Eis und Beförderungen), `addReputation(+2)`, Armee unverändert.

### v3-e15 - Mira findet anonyme HQ-Spende
**Art:** NEUTRAL
**Text:** Mira: "Chef, im HQ-Briefkasten lag ein Koffer. Entweder ein Großmäzen liebt uns - oder jemand will uns reinlegen."
**Bedingung:** HQ gebaut; *`getRobots() >= 1`* (jemand muss den Koffer ja tragen).
**Optionen:**
- **A) Koffer öffnen:** 60/40 -> meistens `addMoney(+3500)`; sonst `addMoney(-1500)` (Erpressung) und `addReputation(-3)`.
- **B) Der Behörde melden:** `addReputation(+5)` (Vorbild), *`getRivalry()` sinkt leicht*, kein Geld.

---

## Zusammenfassung

| id      | Titel                                  | Art     | Kerneffekt                              | Hauptbedingung           |
|---------|----------------------------------------|---------|-----------------------------------------|--------------------------|
| v3-e01  | Schalen-Recycling-Wunder               | GOOD    | -Schalen -> +Strom/+Geld, +Ruf          | Schalen >= 40, Kraftwerk |
| v3-e02  | SHRIMPBOOST sponsert E-Sport           | GOOD    | -Boost, +Geld, +Ruf                     | Boost >= 25              |
| v3-e03  | Roboter macht Überstunden umsonst      | GOOD    | Shrimps x1.12, +Ruf                     | Roboter >= 2             |
| v3-e04  | Krillkill bekommt einen Orden          | GOOD    | Armee x1.20, +Ruf                       | Armee >= 50              |
| v3-e05  | Akwanov verkauft Boost ins Binnenland  | GOOD    | -Boost, ++Geld, +Ruf                    | Boost >= 30, Export-Hafen|
| v3-e06  | HQ-Renovierung beeindruckt Investoren  | GOOD    | ++Geld, +Ruf                            | HQ ausgebaut, Geld > 6k  |
| v3-e07  | Schalen-Lawine im Lager                | BAD     | Schalen x0.5, -Geld, -Ruf               | Schalen >= 80            |
| v3-e08  | SHRIMPBOOST-Rückruf                     | BAD     | -Boost, -Geld, -Ruf                     | Boost >= 40              |
| v3-e09  | Roboter-Aufstand (kurz)                | BAD     | Shrimps x0.85, -Geld                    | Roboter >= 4             |
| v3-e10  | Chad klaut die Rezeptur                | BAD     | -Geld, -Ruf, +Rivalität                 | Boost-Fabrik, Ruf > 40   |
| v3-e11  | Kaserne überzieht das Budget           | BAD     | -Geld, Armee x0.9                       | Kaserne, Armee >= 30     |
| v3-e12  | Kampf-Krill bricht aus dem HQ aus      | BAD     | Armee x0.8, -Ruf, -Geld                 | Armee >= 40              |
| v3-e13  | Waffen-für-Schalen-Deal (Akwanov)      | NEUTRAL | Wahl: -Schalen/+Geld/-Ruf vs. +Ruf      | Schalen >= 60            |
| v3-e14  | Krillkill fordert Mobilmachung         | NEUTRAL | Wahl: +Armee/-Prod. vs. -Geld/+Ruf      | Kaserne, Rivalität mittel|
| v3-e15  | Mira findet anonyme HQ-Spende          | NEUTRAL | Wahl: 60/40 Geld vs. +Ruf melden        | HQ, Roboter >= 1         |

---

## Hinweise zur Implementierung (für die Engine)

- **Neue Ressourcen-Hooks**, die diese Events voraussetzen und die noch in `GameState`
  ergänzt werden müssen: Schalen (`addShells`, `multShells`, `getShells`), SHRIMPBOOST
  (`addBoost`, `getBoost`), Roboter (`addRobots`, `getRobots`), Armee-Stärke
  (`addArmy`, `multArmy`, `getArmy`), Rivalität (`getRivalry`) und optional ein
  Strompuffer (`addPower`).
- **Optionen/Dilemmata**: Die drei NEUTRAL-Events brauchen eine kleine Erweiterung des
  Popups um Auswahl-Buttons (A/B). Falls v3 das noch nicht kann, können sie übergangsweise
  wie die bestehenden NEUTRAL-Events automatisch die wahrscheinlichere Option auflösen
  (siehe "Gourmet-Kritiker incognito" als Vorbild).
- **Schwellen sind bewusst hoch und gestaffelt** (Boost >= 25/30/40, Armee >= 30/40/50,
  Schalen >= 40/60/80), passend zur längeren Spielzeit von v3. So feuern die v3-Events
  erst, wenn die jeweilige Produktionskette wirklich läuft, und nicht schon in der Frühphase.
- **Keine Duplikate**: Alle 14 Events sind neu gegenüber dem hartkodierten Katalog in
  `EventSystem.java` und gegenüber `design/events_extra.md`.
