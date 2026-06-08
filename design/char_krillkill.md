# Charakter-Bogen: General Ronald "Krillkill" Johnson

> "EIN SHRIMP IST NICHT EUER FREUND, REKRUT. EIN SHRIMP IST EINE UNGENUTZTE WAFFE
> MIT ZEHN BEINEN UND EINER MISSION."

ShrimpTopia v2 — Charakter & Handlungsstrang.
Sprache aller In-Game-Texte: Deutsch. Ton: bruellend, militaerisch, paranoid, liebenswert verrueckt.

---

## 1. Steckbrief

| Feld | Wert |
|---|---|
| Voller Name | General Ronald "Krillkill" Johnson (a.D., angeblich) |
| Rolle | Selbsternannter Chef-Ausbilder deiner Garnelen-Streitkraefte |
| Fraktion | "Die 1. Krustentier-Division" (existiert nur in seinem Kopf, jetzt auch bei dir) |
| Erscheint ab | Shrimp-Wirtschaftsgroesse >= 80 (siehe Abschnitt 2) |
| Stimme | CAPS LOCK ist sein Grundzustand. Befehle, Drill-Metaphern, Paranoia. Nennt Shrimps "Soldaten" / "die Truppe" / "meine kleinen Protein-Pakete". |
| Tic | Salutiert vor Becken. Misstraut Algen ("zu links"). Zaehlt Shrimps wie eine Kompanie. |
| Geheimnis | Ja. Tragikomisch. Reveal in Stufe 7. (Abschnitt 5) |

**Charakter-Kern:** Krillkill ist davon ueberzeugt, dass die Welt von einer kommenden
"Suppe" bedroht wird (welche Suppe, sagt er nie). Nur die staerksten, proteinreichsten,
kampferprobtesten Shrimps koennen die Menschheit retten. Er meint es absolut ernst.
Er meint ALLES absolut ernst. Das ist gleichzeitig sein Charme und dein Problem.

---

## 2. Auftritts-Bedingung

Krillkill reagiert nicht auf dein Geld — Geld ist ihm "Buchhalter-Feigheit".
Er reagiert auf deine **militaerische Relevanz**, gemessen an einer abgeleiteten Kennzahl:

```
Shrimp-Wirtschaftsgroesse = (lebende Shrimps)
                          + 4 * (Anzahl Shrimp-Becken)
                          + 0.01 * (insgesamt verkaufte Shrimps)
```

- **Schwelle: Wirtschaftsgroesse >= 80.**
  Begruendung am Balancing: Start sind 12 Shrimps und 0 Becken. Das wird etwa erreicht,
  wenn der Spieler ~3 Becken stabil betreibt und einen ordentlichen Bestand haelt — also
  klar in der Mittelphase, nachdem die Grundkette steht, aber lange vor dem 50k-Ziel.
- Zusatzbedingung (verhindert Frueh-Trigger durch Glueck): **mindestens Tag 25** und
  **mindestens 2 Shrimp-Becken gebaut**.
- Feuert genau **einmal** (Flag `krillkillIntroShown`). Danach laeuft seine Quest-Kette
  unabhaengig vom weiteren Wirtschaftsverlauf.

---

## 3. Vorstellungs-Popup (der grosse Auftritt)

**Titel:** `GENERAL KRILLKILL UEBERNIMMT DAS KOMMANDO`

**Bild/Icon-Hinweis:** Vektor-Portrait, Stahlhelm mit aufgemalten Garnelen-Antennen, Zigarre,
ein einzelner Shrimp salutiert auf seiner Schulter.

**Text (seine Stimme):**

> *Die Hallentore fliegen auf. Ein Mann in einer Uniform, die es bei keiner Armee gibt,
> marschiert herein und baut sich vor deinem groessten Becken auf.*
>
> "STILLGESTANDEN! General Krillkill, 1. Krustentier-Division, frueher mal woanders.
>
> Ich beobachte deinen Laden seit Wochen aus dem Lueftungsschacht. Und weisst du, was
> ich sehe? WEICHLINGE. Du zuechtest Vorspeisen. VORSPEISEN! Waehrend DA DRAUSSEN die
> Suppe naeher kommt!
>
> Diese Tiere haben Potenzial. Panzer. Mut. Zehn Beine und KEINE Ausreden. Aber sie sind
> verzogen — Romantik-Beleuchtung, Wohlfuehlwasser, Buffet rund um die Uhr. PFUI.
>
> Ich biete dir ein einmaliges Angebot: Lass MICH aus diesen Garnelen Soldaten machen.
> Protein. Kampfkraft. Eine Armee, die sich verkauft wie warme Semmeln und zuschlaegt
> wie eine kalte Faust. Sag JA — und die Geschichte wird uns Helden nennen.
>
> Sag nein, und ich campiere trotzdem im Lueftungsschacht. Deine Wahl, Rekrut."

**Auswahl-Optionen:**

| Option | Sofort-Effekt | Folge |
|---|---|---|
| **"Jawohl, General!"** | Reputation -3 ("die Presse fragt, wer der Mann mit dem Helm ist"), Krillkill-Quest-Kette startet bei Stufe 1. | Voller Zugang zum Strang. |
| **"Wer hat Sie reingelassen?"** | Krillkill bleibt (Log: "Er hat sich im Lueftungsschacht haeuslich eingerichtet."). Kette startet trotzdem, aber Stufe 1 startet erst beim naechsten manuellen Klick auf sein Becken-Menue. | Verzoegerter, aber identischer Strang. |

*(Es gibt bewusst kein dauerhaftes "Nein" — Krillkill ist ein Naturereignis, kein Angebot.)*

---

## 4. Quest-Kette: "Operation Protein-Sturm" (7 Stufen)

Format pro Stufe: **Titel** / **Popup-Text** (seine Stimme) / **Ziel** / **Optionen mit Konsequenzen**.
Stufen schreiten fort, sobald das Ziel erfuellt ist; das naechste Popup feuert beim folgenden Tageswechsel.

---

### Stufe 1 — "GRUNDAUSBILDUNG"

**Popup:**
> "Erste Lektion, Rekrut: Ein Soldat, der hungert, ist ein Soldat, der kapituliert.
> Aber ein Soldat, der ZU GUT lebt, wird FETT UND FEIG. Ich brauche eine Trainingshalle.
> Bau mir eine Stelle, wo ich diese Weichlinge haerten kann — Liegestuetze haben Garnelen
> zwar keine, aber sie werden welche LERNEN."

**Ziel:** Baue **1 zusaetzliches Shrimp-Becken** ODER widme ein bestehendes Becken per
Modus-Schalter zum **"Drill-Becken"** um.

**Optionen:**
| Option | Konsequenz |
|---|---|
| **"Neues Drill-Becken bauen"** | Normale Baukosten. Becken bekommt sichtbaren Krillkill-Wimpel. Stufe abgeschlossen. |
| **"Bestehendes Becken umwidmen"** | Kostenlos, aber dieses Becken produziert 2 Tage lang 0 Shrimps ("HAERTUNGSPHASE"). |

**Schaltet frei:** Becken-Modus **"Drill-Becken"** (siehe Abschnitt 6).

---

### Stufe 2 — "ERNAEHRUNGS-DOKTRIN"

**Popup:**
> "Algen?! ALGEN?! Du fuetterst meine Truppe mit SALAT?! Kein Wunder, dass die so
> schlaff im Panzer sind. Soldaten brauchen PROTEIN. Doppelte Rationen, kein Pardon.
> Ja, das kostet Futter. Krieg ist teuer, Rekrut. Frieden ist teurer — der hat
> naemlich keine Shrimps."

**Ziel:** Halte **3 Tage in Folge** den Futter-Bestand ueber einer Schwelle (z.B. >= 30),
waehrend mindestens ein Drill-Becken laeuft.

**Optionen:**
| Option | Konsequenz |
|---|---|
| **"Doppelte Rationen!"** | Drill-Becken verbrauchen +50% Futter, produzieren aber bereits jetzt +10% groessere/schwerere Shrimps (kleiner Vorgeschmack auf Tier-Bonus). |
| **"Sparflamme, General."** | Krillkill ist enttaeuscht (Log-Spott), Stufe dauert laenger (5 statt 3 Tage), kein Frueh-Bonus. Spart aber Futter. |

**Schaltet frei:** Vorschau auf das Tier-System: Drill-Becken zeigen jetzt einen
**"Kampfwert"** pro Becken im Inspektor.

---

### Stufe 3 — "DAS BOOTCAMP"

**Popup:**
> "Die Tiere sind willig, aber meine Ausbilder sind UEBERFORDERT. Ein Mann gegen
> zehntausend Beine — selbst ICH habe Grenzen. Knapp. Wir brauchen ein BOOTCAMP.
> Echte Anlage. Schlamm. Hindernisse. Eine Kletterwand fuer Wesen ohne Arme.
> Da werden aus Zivilisten KAEMPFER, Rekrut."

**Ziel:** Baue das **Bootcamp** (neues Gebaeude, freigeschaltet ab dieser Stufe).

**Optionen:**
| Option | Konsequenz |
|---|---|
| **"Bootcamp errichten."** | Baukosten (~900). Schaltet das Arbeiter-Upgrade "Drill-Instructor" frei. |
| **"Erst sparen, General."** | Stufe pausiert, bis das Bootcamp steht. Krillkill kommentiert jeden Tag bissiger ("NOCH IMMER kein Bootcamp? Die Suppe lacht ueber uns."). |

**Schaltet frei:** Gebaeude **Bootcamp** + Arbeiter-Upgrade **"Drill-Instructor / Bootcamp"**
(Abschnitt 6).

---

### Stufe 4 — "FELDTEST"

**Popup:**
> "Theorie ist fuer Buchhalter. Es wird Zeit fuer einen FELDTEST. Wir lassen die erste
> Charge gegen einen echten Gegner antreten. Keine Sorge — der Gegner ist eine
> besonders aggressive Languste namens Brenda. Sie hat schon zwei meiner besten Maenner...
> aeh, Tiere... auf dem Gewissen. Heute zahlen wir es ihr heim."

**Ziel:** Verkaufe in einem Zeitfenster (z.B. 5 Tage) **mindestens X Shrimps** aus
Drill-Becken — der "Feldzug" gegen Brenda ist im Grunde dein erster gross angelegter
Verkauf von Kampf-Tieren.

**Optionen:**
| Option | Konsequenz |
|---|---|
| **"Frontalangriff!"** | Du verkaufst aggressiv. Bei Erfolg: +1500 Geld Bonus ("Kriegsbeute"), +5 Reputation ("Stadt feiert den Sieg ueber Brenda"). |
| **"Vorsichtig herantasten."** | Laengeres Zeitfenster, kein Geld-Bonus, aber -0 Risiko. Krillkill brummt: "Vorsicht hat noch keine Languste besiegt, aber gut." |
| **"Brenda... verhandeln?"** | Humor-Option: Krillkill ist fassungslos ("MIT DEM FEIND REDEN?!"), lehnt ab. Keine Wirkung ausser einem grossartigen Wuetend-Popup. |

**Schaltet frei:** **Militaer-Kontrakt-Markt** (Abschnitt 6) wird sichtbar — kauft nur
hohe Tiers, zahlt fett.

---

### Stufe 5 — "DIE PROTEIN-BOMBE"

**Popup:**
> "Wir sind nah dran, Rekrut. SO nah. Mir fehlt nur noch das letzte Mosaiksteinchen:
> der proteinreichste Shrimp, den die Schoepfung je verkraftet hat. Ein Tier so dicht,
> dass es im Wasser SINKT. Ein Tier, das eine Gabel verbiegt. Wir brauchen das LABOR.
> Wissenschaft, Rekrut. Ich HASSE Wissenschaft. Aber ich hasse die Suppe mehr."

**Ziel:** Betreibe mindestens **1 Forschungslabor** UND ein Drill-Becken gleichzeitig
fuer **3 Tage**, damit die "Genetische Aufruestung" abgeschlossen werden kann.

**Optionen:**
| Option | Konsequenz |
|---|---|
| **"Genetik hochfahren."** | Schaltet das hoechste Tier **Kampf-Krill** vor (kann ab Stufe 6 produziert werden). Reputation -4 ("Tierschuetzer stellen unbequeme Fragen"). |
| **"Nur natuerliche Zucht."** | Langsamer Pfad: Kampf-Krill wird erst spaeter und mit -10% Ausbeute freigeschaltet, dafuer keine Reputations-Strafe. |

**Schaltet frei:** Vorbereitung des Top-Tiers **Kampf-Krill**.

---

### Stufe 6 — "ERSTE GARDE"

**Popup:**
> "DA STEHT SIE. Die erste Generation. Schau sie dir an, Rekrut — Augen wie Stahl,
> Panzer wie Beton, und ein Bizeps an einem Bein, das eigentlich keinen Bizeps hat.
> KAMPF-KRILL. Ich koennte heulen. Tu ich aber nicht. Generaele heulen nicht.
> *(Er heult ein bisschen.)* Jetzt zuechte mir eine GARDE."

**Ziel:** Produziere insgesamt **X Kampf-Krill** (das neue Top-Tier) und liefere
mindestens eine Charge an den Militaer-Kontrakt-Markt.

**Optionen:**
| Option | Konsequenz |
|---|---|
| **"Massenproduktion."** | Maximaler Output, hoher Strom-/Futter-Verbrauch. Krillkill stolz. |
| **"Elite-Zucht, kleine Stueckzahl."** | Weniger Kampf-Krill, aber jeder bringt am Militaer-Markt einen Premium-Aufschlag. |

**Schaltet frei:** Voller Zugang zu **Kampf-Krill** (Top-Tier) + Militaer-Kontrakt-Markt
kauft zum Hoechstpreis.

---

### Stufe 7 — "DIE WAHRHEIT UEBER DIE SUPPE" (Reveal)

> Siehe Abschnitt 5 — diese Stufe enthuellt das Geheimnis und schliesst die Kette ab.

**Popup (Anfang):**
> "Setz dich, Rekrut. Mach die Tuer zu. Schick die Algen raus, die hoeren mit.
>
> Du fragst dich seit Stufe eins, was diese 'Suppe' eigentlich ist, gegen die ich
> mein Leben lang kaempfe. Heute, wo wir gesiegt haben, hast du es dir verdient,
> es zu erfahren..."

**Ziel:** Klicke dich durch das mehrteilige Reveal-Popup (kein Bau-Ziel — narrativer
Abschluss).

**Optionen am Ende:** siehe Abschnitt 5.

**Schaltet frei:** Krillkills Dauer-Buff **"Esprit de Corps"** + sein Denkmal (Abschnitt 6).

---

## 5. Das dunkle Geheimnis (tragikomisch)

Enthuellt in **Stufe 7**. Mehrteiliges Popup, langsam, dann eine Pointe, dann ein
warmer Schluss.

**Der Reveal:**

> "Vor langer, langer Zeit war ich der juengste Kuechenchef der Marine. Mein Talent:
> Suppe. Eine Hummersuppe, fuer die Admirale geweint haben.
>
> Eines Abends, grosses Bankett, hoechste Generalitaet. Ich hatte die Krone meiner
> Laufbahn vorbereitet: eine Bisque aus dreihundert handverlesenen Garnelen. Und dann...
> *(seine Stimme bricht)* ...dann sah ich in den Topf. Und einer von ihnen — der Kleinste,
> ein krummes Kerlchen — sah ZURUECK. Und salutierte. Mit einer Schere.
>
> Ich habe an dem Abend den Topf umgeworfen, den Sterne-Orden zurueckgegeben und
> geschworen: Nie wieder Vorspeise. NUR NOCH SOLDATEN. Wenn ich schon einen Shrimp
> in den Tod schicke, dann nicht als Suppe — sondern als HELD.
>
> 'Die Suppe', Rekrut... die Suppe bin ich. Oder war ich. Der General, der ich war,
> bevor mich ein krummes Kerlchen mit einer Schere zum besseren Menschen machte.
> Jeden Kampf-Krill, den wir zuechten, zuechte ich fuer IHN."

**Warum tragikomisch:** Sein lebenslanger Feldzug, seine Paranoia, sein ganzer Wahnsinn
wurzeln in echter Reue und einem absurden, ruehrenden Moment. Kein Kriegsverbrechen,
kein duesteres Trauma — sondern ein Koch, der einmal zu tief in einen Topf geschaut hat.
Liebenswert, leicht laecherlich, ueberraschend warm.

**Abschluss-Optionen:**

| Option | Konsequenz |
|---|---|
| **"Wir benennen den ersten Kampf-Krill nach ihm."** | Permanenter Buff **"Esprit de Corps"** (+5 Reputation einmalig, +5% Kampf-Krill-Wert). Krillkill bleibt als zufriedener Mentor mit gelegentlichen (jetzt sanfteren) Spruechen. |
| **"General, Sie brauchen Urlaub."** | Krillkill macht "Manoever am Strand" (verschwindet 10 Tage, dann zurueck). Buff faellt kleiner aus, aber lustiger Log-Strang ("Ansichtskarte aus Bremerhaven: Habe eine Languste namens Brenda getroffen. Wir verstehen uns jetzt."). |
| **"Danke, General."** | Schlichter, ernster Abschluss. Buff "Esprit de Corps" ohne Reputations-Bonus, dafuer wird Krillkills Denkmal freigeschaltet. |

---

## 6. Freischaltbare Inhalte (Effekte grob)

### Becken-Modus: "Drill-Becken"
- **Schaltet frei:** Stufe 1.
- Umschaltbarer Modus fuer Shrimp-Becken.
- **Effekt:** ~ -25% Stueckzahl, dafuer hoeherer **Kampfwert/Tier** pro Tier
  (Voraussetzung fuer Kampf-Krill). Hoeherer Futter-Verbrauch (+50% mit "Doppelte Rationen").
- Optik: kleiner Krillkill-Wimpel auf der Kachel.

### Arbeiter-Upgrade: "Bootcamp / Drill-Instructor"
- **Schaltet frei:** Stufe 3 (benoetigt Gebaeude Bootcamp).
- Verwandelt normale Arbeiter in **"Drill-Instructors"**.
- **Effekt:** Drill-Becken im Wirkbereich laufen mit +Effizienz (kompensiert die
  Stueckzahl-Strafe teilweise) und schalten den Kampf-Krill-Output frei. Leicht
  hoeherer Lohn/Upkeep ("Sold").

### Gebaeude: "Bootcamp"
- **Schaltet frei:** Stufe 3.
- Kosten ~900, Upkeep mittel, braucht Strom + ein paar Arbeiter.
- **Effekt:** Voraussetzung fuer Kampf-Krill; gibt umliegenden Drill-Becken einen
  Kampfwert-Bonus. Senkt Reputation leicht ("die Nachbarn beschweren sich ueber
  Trillerpfeifen um 5 Uhr morgens").

### Shrimp-Tier: "Kampf-Krill" (hoechstes Tier)
- **Schaltet frei:** Stufe 5 (Vorbereitung) / Stufe 6 (voll).
- Top-Qualitaetsstufe ueber den normalen Markt-Tiers.
- **Effekt:** Hoechster Verkaufswert pro Tier, aber nur **bestimmte Maerkte** kaufen ihn
  (v.a. der Militaer-Kontrakt-Markt). Produktion teuer (Strom + doppeltes Futter +
  Drill-Becken + Bootcamp + Labor-Genetik). Optik: Tier mit Stahlhelm-Pixel.

### Markt: "Militaer-Kontrakt-Markt"
- **Schaltet frei:** sichtbar ab Stufe 4, kauft zum Hoechstpreis ab Stufe 6.
- Spezial-Abnehmer (wie eine Premium-Boerse), der **ausschliesslich hohe Tiers /
  Kampf-Krill** kauft.
- **Effekt:** Sehr hoher Preis pro Stueck, aber feste Liefermengen/Kontrakte
  (planbare Grossauftraege statt Tagesverkauf). Ignoriert die normale Reputations-Kurve,
  haengt stattdessen an deinem "Kampfwert". Gelegentliche Kontrakt-Events
  ("Eilauftrag: Brenda ist zurueck.").

### Permanenter Buff: "Esprit de Corps"
- **Schaltet frei:** Stufe 7.
- **Effekt:** +5% Kampf-Krill-Wert dauerhaft; einmaliger Reputations-Bonus (je nach
  Abschluss-Option). Krillkill bleibt als Mentor mit (sanfteren) Zufalls-Spruechen.

### Deko/Denkmal: "Denkmal des krummen Kerlchens"
- **Schaltet frei:** Stufe 7.
- Kleines Standbild eines salutierenden Shrimps mit Schere.
- **Effekt:** rein kosmetisch + winziger Dauer-Reputations-Bonus ("ruehrt jeden Besucher").

---

## 7. Integrations-Notizen fuer die Umsetzung (knapp)

- **Neuer Story-/Charakter-Manager** noetig (analog zu `EventSystem`), der die
  Wirtschaftsgroesse pro Tick prueft und Quest-Stufen als gescriptete (nicht zufaellige)
  Popups feuert. Reuse der bestehenden Popup-/Log-Infrastruktur.
- **Threshold-Berechnung** kann in `GameState` als abgeleiteter Wert leben
  (`getShrimpEconomySize()`), gefuettert aus `shrimp`, Becken-Zaehlung und kumuliertem
  Verkauf (kumulierter Verkauf muesste neu mitgezaehlt werden).
- **Drill-Becken / Tiers / Maerkte** bauen auf dem in v2 ohnehin geplanten Tier- und
  Modus-System auf — dieser Strang ist der erste konkrete Abnehmer dafuer.
- **Balancing-Anker:** Schwelle 80 ~ Mittelphase; Bootcamp ~900 (zwischen Boerse 700
  und Labor 1100); Feldzug-Bonus 1500 in der Groessenordnung des Investor-Events.
- Alle Texte ohne Emoji (Java2D-Konvention des Projekts).
