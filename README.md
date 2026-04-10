# TimAndRaphaProject
Anforderungsdokument: Datenhaltung für eine Schachturnierverwaltung
1. Was ist das Ziel?
Wir wollen eine Schachturnierverwaltung, die alle wichtigen Informationen zuverlässig speichert, damit nichts verloren geht - auch nicht, wenn der Computer neu gestartet wird oder jemand versehentlich etwas falsch eingibt.

2. Was muss das System grundsätzlich können?
Das System muss:
    - Turniere anlegen und speichern (z. B. „Vereinsmeisterschaft 2026“)
    - Gruppen verwalten (z. B. "Pokalmeisterschaft A, Pokalmeisterschaft B")
    - Spieler verwalten (wer macht mit?)
    - Runden verwalten (z. B. Runde 1 bis Runde 7)
    - Paarungen speichern (wer spielt gegen wen?)
    - Ergebnisse speichern (1–0, ½–½, 0–1, +--,- - +, - - -, 0 - 0)
    - Tabelle/Stand berechnen und anzeigen können

3. Welche Daten müssen gespeichert werden? (nicht final)
3.1 Turnierdaten (die Grundinfos)
für jedes Turnier sollen gespeichert werden:
    - Name des Turniers
    - Datum (Start und Ende)
    - Ort
    - Turnierart? (z. B. Schweizer System, Rundenturnier – genaue Begriffe sind egal, aber die Auswahl muss gespeichert werden)
    - Bedenkzeit (z. B. 10 + 0, 15 + 10)
    - Status: geplant / läuft / beendet
Optional:
    - Hinweise/Notizen zum Turnier



3.2 Spieler (wer spielt mit?)
für jeden Spieler sollen gespeichert werden:
    - Vorname und Nachname
    - Wertungszahl (FIDE)
    - Geschlecht
    - Geburtsjahr
    - Status im Turnier

3.3 Anmeldung zum Turnier
das System muss speichern:
    - Welcher Spieler spielt in welchem Turnier mit
    - Wie viel Startgeld gezahlt werden muss
    - Wie viel Startgeld gezahlt wurde

3.4 Runden
für jede Runde sollen gespeichert werden:
    - Rundennummer (1, 2, 3, …)
    - Geplanter Beginn (optional)
    - Status: noch nicht gestartet / läuft / fertig

3.5 Paarungen (wer spielt gegen wen?)
für jede Runde muss gespeichert werden:
    - Spieler A/Weiß 
    - Spieler B/Schwarz
    - Brettnummer / Tisch
    - Freilos (ein Spieler spielt nicht, bekommt aber Punkte oder nicht – je nach Regel)

3.6 Ergebnisse
für jede Partie muss gespeichert werden:
    - Ergebnis:
        - Weiß gewinnt (1–0)
        - Schwarz gewinnt (0–1)
        - Remis (½–½).
    - Optional: Grund/Notiz (z. B. „nicht erschienen“, „Rückzug“, „Schiedsrichterentscheid“)
    - Zeitpunkt der Eingabe
    - Wer hat das Ergebnis eingetragen (für Nachvollziehbarkeit)
