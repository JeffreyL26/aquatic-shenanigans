# ShrimpTopia v2 — Quests & Handlungsstraenge

Tropico-Stil-Popups fuer ShrimpTopia. Jede Quest erscheint als Popup mit Geber-Portrait,
Titel, Text und 1-3 Auswahl-Buttons. Konsequenzen wirken sofort oder ueber Folge-Ticks.

**Konventionen**
- Geld = G, Reputation = R (0-100), Strom/Wasser/Futter = Lagerwerte.
- Shrimp-Tiers (v2): **T1 Pfuetzen-Shrimp**, **T2 Standard-Shrimp**, **T3 Gourmet-Shrimp**, **T4 Wagyu-Shrimp** (legendaer).
- "Folgequest" verweist auf die naechste Stufe einer Kette. Ketten sind unten zusammengefasst.
- Diese Datei enthaelt **bewusst NICHT** General Krillkill und Aussenminister Akwanov (separater Auftrag).

**Inhalt:** 16 Quests, davon 3 mehrstufige Storylines (Behoerde, Tierschutz, Influencer)
plus mehrere Einzelquests und kleinere Ketten.

---

## Storyline-Uebersicht (Ketten)

| Kette | Quests | Thema |
|---|---|---|
| **A — Der Behoerden-Dschungel** | A1 -> A2 -> A3 | Buerokratie, Gewerbeaufsicht, Bestechung-oder-nicht |
| **B — Die Tierschutz-Saga** | B1 -> B2 -> B3 | Garnelen-Wuerde, Demos, Shrimp-Gewerkschaft |
| **C — Der Influencer-Aufstieg** | C1 -> C2 -> C3 | Viral gehen, Sponsoring, Cancel-Culture |
| **D — Konkurrenz-Fehde** | D1 -> D2 | Krabbo Inc., Preiskrieg |
| Einzelquests | E1-E7 | Naturkatastrophen, absurde Ideen, Presse, Behoerde |

---

# KETTE A — Der Behoerden-Dschungel

## A1 — Das Formular B-12/Shrimp

- **id:** `beh_formular`
- **Ausloese-Bedingung:** ab 3 Shrimp-Becken ODER spaetestens Tag 10
- **Geber:** Sachbearbeiterin Frau Dr. Quallmann (Gewerbeaufsicht)
- **Titel:** "Willkommen im Schalentier-Register"
- **Popup-TEXT:**
  > "Guten Tag. Mir ist aufgefallen, dass Sie Garnelen halten, ohne das **Formular B-12/Shrimp**
  > in dreifacher Ausfertigung eingereicht zu haben. Eines davon bitte handschriftlich.
  > Das ist Vorschrift seit 1987 und niemand weiss mehr, warum."
- **Ziel(e):** Entscheide, wie du mit der Buerokratie umgehst.
- **Optionen:**
  1. **"Formular brav ausfuellen."** -> Kostet 1 Arbeiter fuer 3 Tage (Papierkram), aber +5 R. Behoerde ist zufrieden. *(loest A2 aus)*
  2. **"Ich kenne da jemanden..." (Schmiergeld)** -> -800 G sofort. Quest sofort weg, kein A2. Aber 10 % Chance auf spaetere Steuerpruefung (Einzelquest E5 wird wahrscheinlicher).
  3. **"Welches Formular?" (ignorieren)** -> Nichts passiert... 5 Tage lang. Dann zwingend A2 mit verschaerften Tonfall, -3 R.
- **Folgequest:** A2 (bei Option 1 oder 3)

## A2 — Die Betriebspruefung

- **id:** `beh_pruefung`
- **Ausloese-Bedingung:** 4 Tage nach A1 (Option 1 oder 3)
- **Geber:** Frau Dr. Quallmann (jetzt mit Klemmbrett und Begleitung)
- **Titel:** "Routinemaessige Unangemeldete Pruefung"
- **Popup-TEXT:**
  > "Wir fuehren eine voellig routinemaessige, voellig unangemeldete Pruefung durch.
  > Bitte zeigen Sie mir die **Schwimmlizenzen** Ihrer Garnelen. Was, die haben keine?
  > Schwimmen die etwa schwarz?"
- **Ziel(e):** Pruefung bestehen.
- **Optionen:**
  1. **"Lizenzen nachkaufen."** -> -600 G, Pruefung bestanden, +4 R. *(loest A3 aus)*
  2. **"Charmant durchquatschen."** -> Wuerfelwurf: 50 % Erfolg (+6 R, Quest endet positiv, KEIN A3), 50 % Misserfolg (-1000 G Bussgeld, -5 R, dann A3 zwingend).
  3. **"Garnelen verstecken."** -> Halle wird kurz "geschlossen": 2 Tage kein Verkauf. Danach A3 zwingend, -8 R. (Sehr lustig, sehr teuer.)
- **Folgequest:** A3

## A3 — Die goldene Schalentier-Plakette

- **id:** `beh_plakette`
- **Ausloese-Bedingung:** nach A2
- **Geber:** Buergermeisterin Frau Schalk
- **Titel:** "Auszeichnung oder Anzeige?"
- **Popup-TEXT:**
  > "Ihr Betrieb ist der Behoerde mehrfach aufgefallen. Wir koennen Sie jetzt entweder
  > mit der **Goldenen Schalentier-Plakette** ehren ODER eine Akte anlegen, die dicker ist
  > als Sie. Sie duerfen sich was wuenschen - oder, naja, eben nicht."
- **Ziel(e):** Bringe die Behoerde endgueltig auf deine Seite.
- **Optionen:**
  1. **"Spende fuer das Rathaus-Aquarium."** -> -1500 G, dafuer **dauerhaft -1 Tag Buerokratie** bei allen kuenftigen Behoerdenquests und +10 R. Plakette haengt jetzt im HQ.
  2. **"Ich verzichte hoeflich."** -> Nichts kostet, aber Behoerden-Quests bleiben naervig. Neutral.
  3. **"Die Behoerde ist das Problem - ich gehe an die Presse."** -> 50 % +15 R (Held des kleinen Mannes), 50 % -10 R (Behoerde raecht sich). Risiko-Option.
- **Folgequest:** keine (Kette endet, Belohnung wirkt dauerhaft)

---

# KETTE B — Die Tierschutz-Saga

## B1 — Romantische Beleuchtung reicht nicht

- **id:** `tier_wuerde`
- **Ausloese-Bedingung:** ab 5 Shrimp-Becken
- **Geber:** Aktivistin Lena von "Krill Lives Matter"
- **Titel:** "Haben Ihre Shrimps eigentlich Hobbys?"
- **Popup-TEXT:**
  > "Wir haben Ihre Farm beobachtet. Ihre Garnelen schwimmen den ganzen Tag nur im Kreis -
  > das ist **Beschaeftigungsmangel**! Garnelen brauchen Anregung, Selbstverwirklichung,
  > vielleicht ein kleines Podcast-Mikrofon. Tun Sie endlich was fuer die Shrimp-Wuerde!"
- **Ziel(e):** Reagiere auf den ersten Tierschutz-Vorwurf.
- **Optionen:**
  1. **"Becken-Spielzeug installieren."** -> -400 G, +8 R, Shrimps "gluecklicher": +5 % Wachstum in allen Becken fuer 10 Tage. *(loest B2 aus, freundlicher Pfad)*
  2. **"Das sind Garnelen, Lena."** -> +0 G, aber -6 R und Lena ist beleidigt. *(loest B2 aus, feindlicher Pfad)*
- **Folgequest:** B2

## B2 — Die Garnelen-Demo

- **id:** `tier_demo`
- **Ausloese-Bedingung:** 6 Tage nach B1
- **Geber:** Presse (vor dem Werkstor) / Lena im Hintergrund
- **Titel:** "Veganer-Demo vor dem Werkstor"
- **Popup-TEXT:**
  > "Eine Menschentraube blockiert die Einfahrt. Schilder: 'SHRIMPS SIND FREUNDE, KEIN COCKTAIL'
  > und 'FREE WILLY... aeh... FREE SHRIMPY'. Ein Demonstrant hat sich an dein Tor geklebt.
  > Mit Bio-Klebstoff, immerhin."
- **Ziel(e):** Demo aufloesen, ohne dass es eskaliert.
- **Optionen:**
  1. **"Kostenlose Shrimp-Streichelzoo-Fuehrung anbieten."** -> -200 G, +10 R, Demo loest sich in Begeisterung auf. (Funktioniert nur gut, wenn B1 freundlich war: dann zusaetzlich +5 R.) *(loest B3 aus)*
  2. **"Polizei rufen."** -> -3 R, 1 Tag Betriebsstoerung (kein Verkauf), aber spart Geld. *(loest B3 aus, harter Pfad)*
  3. **"Mit Lena verhandeln."** -> Verpflichtet dich, **ein Becken zum 'Bio-Wellness-Becken' umzuwidmen** (produziert -20 % Menge, aber +1 Tier-Stufe der Shrimps darin). Dauerhaft. +12 R. *(loest B3 aus, Kompromiss-Pfad)*
- **Folgequest:** B3

## B3 — Die Shrimp-Gewerkschaft

- **id:** `tier_gewerkschaft`
- **Ausloese-Bedingung:** nach B2
- **Geber:** "Shrimp-Betriebsrat" (Lena hat es uebertrieben)
- **Titel:** "Die Garnelen haben sich organisiert"
- **Popup-TEXT:**
  > "Schlechte Nachrichten, Chef: Ihre Garnelen haben eine **Gewerkschaft** gegruendet.
  > Forderungen: 4-Tage-Woche, mehr Algen-Snacks, ein Mitspracherecht bei der Beleuchtungsfarbe.
  > Ihr Sprecher heisst 'Klausi' und hat einen winzigen Aktenkoffer."
- **Ziel(e):** Den ersten Shrimp-Arbeitskampf der Geschichte loesen.
- **Optionen:**
  1. **"Forderungen akzeptieren."** -> -10 % Produktionsmenge dauerhaft, ABER +15 R und Shrimps steigen alle um **1 Tier** auf (zufriedene Premium-Shrimps). Tierschutz-Quests treten nie wieder auf.
  2. **"Klausi 'befoerdern' (= ins beste Becken stecken)."** -> -300 G, Gewerkschaft loest sich auf (Korruption), keine R-Aenderung. Lustige Lognachricht.
  3. **"Aussperrung!"** -> 2 Tage Produktionsstopp, -12 R, danach laeuft alles normal weiter. Du bist jetzt der Boesewicht. (Schaltet evtl. spaeteren bissigen Presse-Event frei.)
- **Folgequest:** keine

---

# KETTE C — Der Influencer-Aufstieg

## C1 — Dein Meme geht viral

- **id:** `inf_viral`
- **Ausloese-Bedingung:** ab 10.000 G ODER ab Reputation 60
- **Geber:** deine Assistentin Mira (stuermt aufgeregt ins Buero)
- **Titel:** "CHEF! Wir sind TRENDING!"
- **Popup-TEXT:**
  > "Jemand hat ein Video von unserem Becken gepostet - 'Guy gets rich farming shrimp indoors' -
  > und es geht DURCH DIE DECKE. 4 Millionen Views. Die Kommentare sind zu 60 % beeindruckt
  > und zu 40 % besorgt um unsere geistige Gesundheit. Was machen wir?"
- **Ziel(e):** Den viralen Moment nutzen.
- **Optionen:**
  1. **"Voll drauf einsteigen - Eigener Kanal!"** -> -500 G (Equipment), danach **+R-Schub von +1/Tag fuer 10 Tage**. *(loest C2 aus)*
  2. **"Bescheiden bleiben, einfach weiterarbeiten."** -> +3 R einmalig, Hype verpufft. KEIN C2. (Sichere, langweilige Option.)
  3. **"Merch verkaufen!" (Shrimp-T-Shirts)** -> Sofort +1200 G, aber -4 R (zu kommerziell, 'Sellout'). *(loest C2 aus)*
- **Folgequest:** C2

## C2 — Der Sponsoring-Deal

- **id:** `inf_sponsor`
- **Ausloese-Bedingung:** 5 Tage nach C1 (Option 1 oder 3)
- **Geber:** "AquaBro Supplements" (Energy-Drink-Marke)
- **Titel:** "Bro, wollen wir was zusammen machen?"
- **Popup-TEXT:**
  > "Yo! Wir sind AquaBro, der Energy-Drink mit echtem Garnelen-Extrakt (don't ask).
  > Wir wollen dich sponsern. Du musst nur in jedem Video 'AquaBro - shrimp your day!'
  > sagen und dieses leicht radioaktive Logo aufs Becken kleben."
- **Ziel(e):** Ueber den Deal entscheiden.
- **Optionen:**
  1. **"Deal! Geld ist Geld."** -> **+300 G/Tag fuer 8 Tage**, aber -1 R/Tag im selben Zeitraum (Glaubwuerdigkeit sinkt). *(loest C3 aus)*
  2. **"Nur, wenn ihr unsere Shrimps NICHT verfuettert."** -> -150 G einmalig (Anwaltskosten), +6 R (Integritaet!), kleinerer Deal: +120 G/Tag fuer 8 Tage. *(loest C3 aus)*
  3. **"Ihr seid mir zu dubios."** -> Nichts passiert, +2 R. KEIN C3.
- **Folgequest:** C3

## C3 — Der Shitstorm

- **id:** `inf_cancel`
- **Ausloese-Bedingung:** nach C2 (Option 1 oder 2)
- **Geber:** Mira (diesmal panisch, Handy gluehend)
- **Titel:** "Chef. CHEF. Wir werden gecancelt."
- **Popup-TEXT:**
  > "Ein altes Video ist aufgetaucht, in dem du eine Garnele 'Brian' genannt und dann
  > verkauft hast. Das Internet ist... nicht amused. #JusticeForBrian trendet.
  > Brian hat jetzt mehr Follower als wir. Brian ist tot, Chef. Brian war Suppe."
- **Ziel(e):** Den Shitstorm ueberstehen.
- **Optionen:**
  1. **"Aufrichtige Entschuldigung + Brian-Gedenkbecken."** -> -700 G, -5 R sofort, aber nach 4 Tagen +18 R (Comeback-Story, alle lieben Reue). Neues Deko-Becken im HQ.
  2. **"Doppelt durchziehen: Brian-Suppe als Limited Edition."** -> 50 % genial (+2000 G, +8 R, 'absolute Legende'), 50 % Desaster (-15 R, Verkaufspreis 5 Tage -20 %). Maximales Risiko.
  3. **"Internet ausschalten und Tee trinken."** -> -10 R (du wirkst ignorant), aber nichts kostet Geld und nach 6 Tagen ist alles vergessen. Stoische Option.
- **Folgequest:** keine

---

# KETTE D — Die Krabbo-Fehde (zweistufig)

## D1 — Die Konkurrenz zieht ein

- **id:** `konk_krabbo`
- **Ausloese-Bedingung:** ab 20.000 G
- **Geber:** Mira (liest Lokalzeitung)
- **Titel:** "Krabbo Inc. baut nebenan"
- **Popup-TEXT:**
  > "Schlechte Nachrichten: Die Krabbo Inc. - gefuehrt vom schmierigen Chad Krabbowski -
  > eroeffnet eine Mega-Shrimp-Fabrik direkt gegenueber. Ihr Slogan: 'Billiger. Groesser.
  > Vermutlich illegal.' Sie unterbieten dich schon bei den Preisen."
- **Ziel(e):** Reagiere auf die neue Konkurrenz.
- **Optionen:**
  1. **"Qualitaetsoffensive: in Tiers investieren."** -> -1000 G, dauerhaft **+8 % Verkaufspreis fuer T3/T4-Shrimps**. *(loest D2 aus)*
  2. **"Preiskrieg starten."** -> Verkaufspreis 6 Tage -15 %, aber Krabbo verliert Marktanteile: danach +5 % Preis fuer 10 Tage. *(loest D2 aus)*
  3. **"Industriespionage." (Mira als Maulwurf)** -> -500 G, 30 % Chance entdeckt zu werden (-10 R + D2 sofort feindlich), 70 % du klaust ein Rezept (+1 Tier-Slot Forschung gratis).
- **Folgequest:** D2

## D2 — Chads Angebot

- **id:** `konk_angebot`
- **Ausloese-Bedingung:** 8 Tage nach D1
- **Geber:** Chad Krabbowski persoenlich (Goldkette, Hai-Krawatte)
- **Titel:** "Lass uns reden, Kleiner"
- **Popup-TEXT:**
  > "Hoer zu, Champ. Du hast Talent, aber du spielst in der Kreisliga. Ich biete dir an:
  > Verkauf mir deine Halle, werd reich und leg dich an einen Strand. ODER wir machen
  > Business zusammen. ODER... du bleibst mein niedlicher kleiner Rivale. Deine Wahl."
- **Ziel(e):** Die Fehde beenden - so oder so.
- **Optionen:**
  1. **"Niemals verkaufen - Fusion zu gleichen Teilen."** -> +5000 G einmalig, aber **-20 % deiner Produktion gehoeren ab jetzt Chad** (dauerhaft kleiner Abzug). +5 R (cleverer Deal).
  2. **"In deine Traeume, Chad. (Rivalitaet)"** -> Nichts kostet, aber Krabbo sabotiert gelegentlich (kleine Chance auf negative Events steigt). Dafuer +10 R bei der Bevoelkerung ('David gegen Goliath').
  3. **"Ich kaufe DICH."** -> Nur waehlbar ab 40.000 G. Kostet -25.000 G, dafuer +50 % Produktionskapazitaet (Krabbos Hallen) und Endgame-Bragging-Rights. Chad weint.
- **Folgequest:** keine

---

# EINZELQUESTS

## E1 — Der Stromausfall-Schock

- **id:** `kat_blackout`
- **Ausloese-Bedingung:** wenn Stromnachfrage > Erzeugung an 1 Tag (Brownout) UND ab Tag 8
- **Geber:** Technischer Leiter Olaf (mit Taschenlampe)
- **Titel:** "Es wird dunkel im Becken"
- **Popup-TEXT:**
  > "Chef, wir haben einen Brownout. Die Shrimps schwimmen im Dunkeln und werden langsam...
  > nervoes. Eine Garnele hat gerade eine andere angeschaut. So fangen Massenpaniken an.
  > Wir brauchen JETZT Strom oder bald Garnelen-Therapie."
- **Ziel(e):** Stromproblem akut loesen.
- **Optionen:**
  1. **"Notaggregat anwerfen."** -> -600 G, deckt Strom fuer 5 Tage, aber -3 R (laut & stinkt).
  2. **"Shrimps einfach beruhigen (Kerzen)."** -> -100 G, romantisch, aber 20 % Chance auf Becken-Brand (-1 Becken). Hochrisiko-Sparoption.
  3. **"Aushalten."** -> -10 % Shrimp-Bestand durch Stress, aber kostenlos. Olaf ist enttaeuscht.
- **Folgequest:** keine

## E2 — Die Algenbluete

- **id:** `kat_algen`
- **Ausloese-Bedingung:** ab 2 Algen-Futterfarmen, zufaellig ab Tag 15
- **Geber:** zufaelliger NPC (der Algen-Praktikant)
- **Titel:** "Aehm... die Algen machen Probleme"
- **Popup-TEXT:**
  > "Also. Es gibt jetzt mehr Algen. VIEL mehr Algen. Sie wachsen aus den Steckdosen.
  > Eine hat einen Namen bekommen. Gute Nachricht: Wir haben Futter im Ueberfluss.
  > Schlechte Nachricht: Die Algen wollen Miete."
- **Ziel(e):** Algenbluete managen.
- **Optionen:**
  1. **"Ueberschuss-Futter verkaufen."** -> +400 G, +1 Tag Futter-Bonus, dann wieder normal.
  2. **"Algen-Smoothies an Touristen verkaufen."** -> 50 % +700 G (Hype), 50 % -4 R (jemand wird gruen im Gesicht).
- **Folgequest:** keine

## E3 — Der Gourmet-Kritiker

- **id:** `presse_kritiker`
- **Ausloese-Bedingung:** ab 1 Restaurant UND ab Reputation 50
- **Geber:** Presse (anonym)
- **Titel:** "Ein Kritiker kommt incognito"
- **Popup-TEXT:**
  > "Geruechten zufolge testet morgen der gefuerchtete Gourmet-Kritiker Sebastian Schlemmerle
  > anonym dein Restaurant. Er hat schon Sterne-Koeche zum Weinen gebracht und einmal eine
  > Auster persoenlich beleidigt. Bereite dich vor."
- **Ziel(e):** Den Kritiker beeindrucken.
- **Optionen:**
  1. **"Nur T3/T4-Shrimps servieren."** -> Nur waehlbar mit T3+ im Lager. -300 G, dann 80 % Chance: +20 R + dauerhaft +6 % Preis ('Empfohlen von Schlemmerle').
  2. **"Mit Charme und Show ablenken."** -> 50 % +10 R, 50 % -8 R (Show floppt).
  3. **"Ihm einfach die normale Karte geben."** -> Neutral, kleine Chance (+3 R) wenn Reputation schon hoch ist.
- **Folgequest:** keine

## E4 — Die Moewen-Invasion

- **id:** `kat_moewen`
- **Ausloese-Bedingung:** zufaellig ab Tag 20, hoehere Chance bei vielen Becken
- **Geber:** Olaf (jagt etwas mit einem Besen)
- **Titel:** "Sie sind durchs Dach!"
- **Popup-TEXT:**
  > "MOEWEN, CHEF! Ein ganzer Schwarm hat das Hallendach geknackt und haelt unsere Becken
  > fuer ein All-you-can-eat-Buffet. Eine hat sich auf dem HQ niedergelassen und sieht
  > aus, als wuerde sie hier jetzt wohnen. Sie heisst angeblich Kevin."
- **Ziel(e):** Die Moewen vertreiben, bevor sie alles fressen.
- **Optionen:**
  1. **"Profi-Falkner engagieren."** -> -500 G, Moewen sofort weg, kein Verlust. +2 R (humane Loesung).
  2. **"Becken mit Netzen sichern."** -> -250 G, verliert 5 % Shrimps, aber **dauerhaft -50 % Moewen-Schaden** kuenftig.
  3. **"Kevin adoptieren und gewaehren lassen."** -> -15 % Shrimp-Bestand, aber +8 R (Maskottchen!). Kevin taucht in spaeteren Logs auf.
- **Folgequest:** keine

## E5 — Die Steuerpruefung

- **id:** `beh_steuer`
- **Ausloese-Bedingung:** ab 25.000 G, Chance erhoeht durch A1-Option 2 (Schmiergeld)
- **Geber:** Finanzamt (Herr Pfennig, sehr grau)
- **Titel:** "Wir haetten da ein paar Fragen"
- **Popup-TEXT:**
  > "Guten Tag. Uns ist aufgefallen, dass Sie '14.000 Garnelen als Geschaeftspartner'
  > steuerlich geltend gemacht haben. Auch die Position 'romantische Beckenbeleuchtung -
  > Bewirtungskosten' wirft Fragen auf. Wir muessten da mal genauer hinschauen."
- **Ziel(e):** Pruefung ueberstehen.
- **Optionen:**
  1. **"Saubere Buecher vorlegen."** -> -5 % Geld (Nachzahlung), aber +5 R und keine Folgen.
  2. **"Steuerberater einfliegen lassen."** -> -1500 G, dafuer 90 % Chance auf 0 Schaden + Herr Pfennig wird sogar fast freundlich.
  3. **"Es drauf ankommen lassen."** -> 50 % nichts, 50 % -15 % Geld Bussgeld + -8 R.
- **Folgequest:** keine

## E6 — Die absurde Geschaeftsidee

- **id:** `idee_spa`
- **Ausloese-Bedingung:** ab 30.000 G, nach Freischaltung Forschung
- **Geber:** Mira (mit einem viel zu aufwendigen Pitch-Deck)
- **Titel:** "Chef, hoeren Sie mir KURZ zu"
- **Popup-TEXT:**
  > "Ich hab da was. Stell dir vor: ein **Shrimp-Spa**. Menschen legen sich in ein Becken
  > und lassen sich von Garnelen die Fuesse maniküren. 'Fish-Pedicure' gibt's schon -
  > wir machen das mit Shrimps. Premium-Wellness! Was kann schon schiefgehen?"
- **Ziel(e):** Ueber die absurde Idee entscheiden.
- **Optionen:**
  1. **"Lass es uns wagen!"** -> -2000 G Umbau, dann 60 % Chance auf neue Geldquelle (+250 G/Tag, +R) ODER 40 % 'Vorfall' (-10 R, Idee gestoppt). Hoher Einsatz.
  2. **"Zu riskant. Aber netter Versuch, Mira."** -> +1 R (gute Mitarbeiterfuehrung), nichts passiert.
  3. **"Mach es - aber MIT Haftungsausschluss."** -> -1200 G, kleinere, sichere Einnahme (+120 G/Tag), kein R-Risiko. Die langweilig-kluge Variante.
- **Folgequest:** keine

## E7 — Der Investor klopft

- **id:** `geld_investor`
- **Ausloese-Bedingung:** ab 35.000 G UND Reputation 70
- **Geber:** Risikokapitalgeberin Frau Goldberg (Sonnenbrille, drinnen)
- **Titel:** "Ich rieche hier Einhorn-Potenzial"
- **Popup-TEXT:**
  > "Ich investiere normalerweise in Apps, aber dein 'Shrimp-as-a-Service'-Modell ist
  > disruptiv. Ich biete dir Kapital - im Tausch gegen Anteile und ein paar... 'Synergien'.
  > Wir koennten ein Garnelen-Imperium aufbauen. Oder zumindest ein huebsches IPO."
- **Ziel(e):** Investitionsangebot bewerten.
- **Optionen:**
  1. **"Deal annehmen (Anteile abgeben)."** -> Sofort +10.000 G, aber **-15 % aller kuenftigen Verkaufseinnahmen** (Investorenanteil, dauerhaft).
  2. **"Nur ein Kredit, keine Anteile."** -> +5000 G jetzt, -200 G/Tag fuer 30 Tage Rueckzahlung. Du behaeltst die Kontrolle.
  3. **"Ich brauche niemanden. (Ablehnen)"** -> +5 R (Selfmade-Image), Frau Goldberg respektiert das und taucht spaeter eventuell mit besserem Angebot wieder auf.
- **Folgequest:** keine

---

## Balancing-Hinweise fuer die Umsetzung

- **Trigger-Entkopplung:** Ketten A/B/C/D nie gleichzeitig starten lassen - max. 1 aktive Hauptquest. Einzelquests duerfen parallel laufen.
- **Cooldown:** Nach jeder Quest mind. 3-4 Tage Pause, damit Popups nicht nerven (Tropico-Gefuehl: selten, aber praegnant).
- **R-Schwankungen** sollten meist im Bereich +/- 5 bis +/- 18 bleiben, damit der Preis-Faktor (0,6x-1,4x) nicht zu schnell kippt.
- **Tier-Belohnungen** (B3 Opt.1, B2 Opt.3, D1 Opt.1, E3 Opt.1) sind die zentralen Bruecken zum neuen Tier-System - hier den Spieler spueren lassen, dass Qualitaet sich lohnt.
- **Wiederkehrende NPCs** als roter Faden: **Mira** (Assistentin, Comic-Relief), **Olaf** (Technik, Katastrophen), **Frau Dr. Quallmann** (Behoerde), **Lena** (Tierschutz), **Chad Krabbowski** (Rivale). Konsistenz erhoeht die Tropico-Atmosphaere.
