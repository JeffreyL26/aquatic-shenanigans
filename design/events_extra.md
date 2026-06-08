# Zufallsereignisse v2 - Erweiterungspaket

15 neue Zufallsereignisse im Stil der bestehenden (Tropico-frech, Surviving-Mars-trocken).
Vermenschlichte Shrimps, Meme-Bewusstsein, kurze Popup-Texte, keine Emojis.

## Konventionen (passend zum bestehenden `EventSystem`)

- **Art**: `GOOD` / `BAD` / `NEUTRAL`
- **Effekt-Vokabular** (vorhanden in `GameState`): `addMoney(x)`, `addReputation(x)`,
  `addFeed(x)`, `multWater(f)`, `multShrimp(f)`.
- **Neue Effekt-Ideen fuer v2** (noch zu implementieren): `multFeed(f)`, `addPower(x)`,
  `upgradeRandomTier()` / `downgradeRandomTier()`, `addWorker(x)`, Zonen-/Charakter-Hooks.
  Diese sind unten kursiv markiert, falls sie ueber das aktuelle Vokabular hinausgehen.
- **Bedingung** = Praedikat auf `GameState` (z.B. `gs.getShrimp() > 20`). Leer = immer moeglich.

---

## GOOD - Erfreuliche Ereignisse

### 1. Shrimp mit BWL-Abschluss
**Art:** GOOD
**Text:** Einer deiner Shrimps hat heimlich ein Fernstudium gemacht und optimiert jetzt die Lagerhaltung. Niemand wagt zu widersprechen.
**Effekt-Idee:** `addMoney(+1400)`, `addReputation(+3)`
**Bedingung:** keine

### 2. Tier-S Durchbruch im Labor
**Art:** GOOD
**Text:** Das Forschungslabor hebt versehentlich ein ganzes Becken auf Premium-Qualitaet. Sushi-Koeche weinen vor Glueck.
**Effekt-Idee:** *`upgradeRandomTier()`* (ein Becken steigt eine Tier-Stufe), `addReputation(+8)`
**Bedingung:** Forschungslabor gebaut

### 3. Akwanov handelt ein Abkommen aus
**Art:** GOOD
**Text:** Aussenminister Akwanov unterzeichnet das "Pazifische Plankton-Pakt". Futter stroemt zollfrei ins Lager.
**Effekt-Idee:** `addFeed(+60)`, `addReputation(+4)`
**Bedingung:** keine

### 4. Romantik-Playlist im Becken
**Art:** GOOD
**Text:** Jemand hat Saxophon-Jazz ueber die Beckenlautsprecher laufen lassen. Die Shrimps sind in Stimmung.
**Effekt-Idee:** `multShrimp(1.18)`
**Bedingung:** `gs.getShrimp() > 8`

### 5. Solar-Hitzewelle
**Art:** GOOD
**Text:** Wolkenloser Himmel, das Solar-Dach glueht. Du verkaufst Ueberschuss-Strom an die Nachbarhalle.
**Effekt-Idee:** `addMoney(+1100)` *(bzw. `addPower(+x)`, falls Strom gepuffert wird)*
**Bedingung:** Solar-Dach gebaut

### 6. Arbeiter des Monats
**Art:** GOOD
**Text:** Ein Arbeiter rettet drei Becken vor dem Ueberlaufen und will dafuer nur einen Aufkleber. Vorbildlich.
**Effekt-Idee:** `addReputation(+6)`, `multShrimp(1.05)`
**Bedingung:** `gs.getWorkers() > 1`

---

## BAD - Aergerliche Ereignisse

### 7. General Krillkill ruft "Verteidigungsuebung" aus
**Art:** BAD
**Text:** General Krillkill riegelt eine Zone fuer ein Manoever ab. Produktion steht, die Stromrechnung laeuft weiter.
**Effekt-Idee:** `addMoney(-800)`, `multShrimp(0.95)`
**Bedingung:** mindestens 2 Zonen freigeschaltet

### 8. Tier-Downgrade-Skandal
**Art:** BAD
**Text:** Ein Foodblogger entlarvt deine "Diamant-Shrimps" als solide Mittelklasse. Das Internet hat keine Gnade.
**Effekt-Idee:** *`downgradeRandomTier()`*, `addReputation(-10)`
**Bedingung:** `gs.getReputation() > 40`

### 9. Plankton-Streik
**Art:** BAD
**Text:** Die Algen-Futterfarm meldet sich krank. "Burnout durch Dauerphotosynthese", steht im Attest.
**Effekt-Idee:** *`multFeed(0.6)`* (bzw. `addFeed(-30)`)
**Bedingung:** Algen-Futterfarm gebaut

### 10. Heizungsausfall in der Nachtschicht
**Art:** BAD
**Text:** Das Becken kuehlt auf Nordsee-Niveau ab. Deine tropischen Shrimps sind not amused und ziehen sich zusammen.
**Effekt-Idee:** `multShrimp(0.8)`, `multWater(0.9)`
**Bedingung:** `gs.getShrimp() > 12`

### 11. Influencer-Kooperation geht nach hinten los
**Art:** BAD
**Text:** Der bezahlte Shrimp-Influencer nennt dich live "ueberbewertet" und kassiert trotzdem. Klassiker.
**Effekt-Idee:** `addMoney(-1200)`, `addReputation(-6)`
**Bedingung:** `gs.getMoney() > 3000`

### 12. Wohnheim-Wasserschaden
**Art:** BAD
**Text:** Im Arbeiter-Wohnheim ist die Dusche explodiert. Die Crew streikt, bis es trocken ist.
**Effekt-Idee:** `multWater(0.7)`, `addReputation(-3)`
**Bedingung:** Arbeiter-Wohnheim gebaut

---

## NEUTRAL / GEMISCHT - Es kommt drauf an

### 13. Boersengang der Shrimp-AG
**Art:** NEUTRAL
**Text:** Du gehst an die Shrimp-Boerse. Bei gutem Ruf jubeln die Anleger, sonst werfen sie mit Cocktailsauce.
**Effekt-Idee:** wenn `getReputation() > 60`: `addMoney(+4000)`, `addReputation(+5)`; sonst: `addMoney(-700)`, `addReputation(-6)`
**Bedingung:** Shrimp-Boerse gebaut

### 14. Akwanov und Krillkill streiten beim Buffet
**Art:** NEUTRAL
**Text:** Die beiden Herren geraten ueber die richtige Garnelen-Aussenpolitik aneinander. Die Presse liebt es, dein Catering nicht.
**Effekt-Idee:** wenn `getMoney() > 2500`: `addMoney(-500)`, `addReputation(+8)`; sonst: `addReputation(-4)`
**Bedingung:** keine

### 15. Mystery-Box vom Grosshaendler
**Art:** NEUTRAL
**Text:** Eine unbeschriftete Palette steht im Hof. Drin: entweder Premium-Futter oder sehr viel Verpackungsmuell.
**Effekt-Idee:** 50/50: `addFeed(+50)` ODER `addMoney(-400)` (Entsorgungskosten)
**Bedingung:** keine

---

## Zusammenfassung

| #  | Titel                                      | Art     | Kerneffekt                       |
|----|--------------------------------------------|---------|----------------------------------|
| 1  | Shrimp mit BWL-Abschluss                   | GOOD    | +Geld, +Ruf                      |
| 2  | Tier-S Durchbruch im Labor                 | GOOD    | Tier-Upgrade, +Ruf               |
| 3  | Akwanov handelt ein Abkommen aus           | GOOD    | +Futter, +Ruf                    |
| 4  | Romantik-Playlist im Becken                | GOOD    | Shrimps x1.18                    |
| 5  | Solar-Hitzewelle                           | GOOD    | +Geld / +Strom                   |
| 6  | Arbeiter des Monats                        | GOOD    | +Ruf, Shrimps x1.05              |
| 7  | Krillkill "Verteidigungsuebung"            | BAD     | -Geld, Shrimps x0.95             |
| 8  | Tier-Downgrade-Skandal                     | BAD     | Tier-Downgrade, -Ruf             |
| 9  | Plankton-Streik                            | BAD     | Futter x0.6                      |
| 10 | Heizungsausfall in der Nachtschicht        | BAD     | Shrimps x0.8, Wasser x0.9        |
| 11 | Influencer-Kooperation geht schief         | BAD     | -Geld, -Ruf                      |
| 12 | Wohnheim-Wasserschaden                     | BAD     | Wasser x0.7, -Ruf                |
| 13 | Boersengang der Shrimp-AG                  | NEUTRAL | abh. von Ruf: +Geld oder -Geld   |
| 14 | Akwanov und Krillkill streiten             | NEUTRAL | abh. von Geld: PR vs. Ruf-Minus  |
| 15 | Mystery-Box vom Grosshaendler              | NEUTRAL | 50/50 +Futter oder -Geld         |
