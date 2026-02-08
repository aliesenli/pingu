# Pingu - Currency Exchange System

## 1. Einleitung

### 1.1 Zweck des Dokuments

Dieses Dokument beschreibt die Software Requirements Specification (SRS) für das System **Pingu Finance**. Es dient als verbindliche Grundlage für Planung, Entwicklung, Implementierung, Test und Abnahme der Software und richtet sich an alle relevanten Stakeholder, insbesondere Auftraggeber, Projektteam, Entwickler, Tester und Dozierende des Moduls Software Engineering.

### 1.2 Ziel des Systems

Ziel von Pingu Finance ist es, Consultants ein zentrales, sicheres und benutzerfreundliches Werkzeug zur Verwaltung von Währungstransaktionen ihrer Kunden bereitzustellen. Das System soll Transparenz schaffen, manuelle Fehler reduzieren und eine konsistente Datenbasis für Auswertungen und Berichte liefern.

### 1.3 Produktüberblick

Pingu Finance ist eine Desktop-Applikation zur Erfassung, Verwaltung und Auswertung von Währungstransaktionen. Die Anwendung wird mit Java und JavaFX umgesetzt, stellt Wechselkurse über einen dateibasierten Import bereit, ermöglicht Umrechnungen und unterstützt Consultants bei der strukturierten Verwaltung ihrer Kundendaten. Die erste Version (MVP) wird bis Januar 2026 bereitgestellt.

---

## 2. Gesamtbeschreibung

### 2.1 Benutzergruppen

- **Consultant**: Führt Währungsumrechnungen durch, erstellt Transaktionen und kann eigene Transaktionen einsehen und filtern.
- **Administrator (Admin)**: Verwaltet Benutzer, Rollen und Wechselkursdaten. Kann alle Transaktionen systemweit einsehen, filtern und bei Bedarf revertieren.

### 2.2 Betriebsumgebung

- Desktop-Applikation auf Basis von Java und JavaFX
- Lauffähig auf gängigen Desktop-Betriebssystemen (Windows, macOS, Linux) mit unterstützter Java-Laufzeitumgebung
- Keine permanente Internetverbindung erforderlich
- Lokale Installation ohne externe Systemabhängigkeiten

### 2.3 Annahmen und Abhängigkeiten

- Verfügbarkeit von Wechselkursdaten im JSON-Format zum manuellen Import durch Admins
- Vorhandene Java-Laufzeitumgebung auf dem Zielsystem
- Einhaltung geltender Datenschutz- und Sicherheitsvorgaben

### 2.4 Abgrenzung

Nicht Bestandteil der initialen Version sind webbasierte oder mobile Anwendungen, externe API-Integrationen zu Bank- oder Buchhaltungssystemen, CSV-Export und aggregierte Berichte sowie KI-basierte Prognose- oder Analysefunktionen. Diese können in späteren Versionen ergänzt werden.

---

## 3. Funktionale Anforderungen

### 3.1 Benutzer- und Rollenmanagement

- Das System muss Benutzer mittels Benutzername und Passwort authentifizieren.
- Das System muss mindestens die Rollen **Consultant** und **Admin** unterstützen.
- Das System muss Systemfunktionen rollenbasiert autorisieren.
- Benutzeraktionen müssen einem eindeutig identifizierbaren Benutzer zugeordnet werden.

### 3.2 Wechselkurse

- Das System muss es Admins ermöglichen, Wechselkurse als JSON-Datei hochzuladen.
- Die hochgeladenen Wechselkurse müssen auf einer definierten Basiswährung basieren.
- Das System muss die Struktur, Vollständigkeit und fachliche Korrektheit der hochgeladenen Wechselkursdaten validieren.
- Das System muss Wechselkurse versioniert und eindeutig identifizierbar speichern.
- Das System muss ausschliesslich die aktuell aktive Wechselkursversion für neue Umrechnungen verwenden.
- Das System muss die verwendete Kursbasis transparent darstellen.

### 3.3 Währungsumrechnung

- Das System muss es einem Consultant ermöglichen, einen Betrag in einer Quellwährung einzugeben und diesen in eine Zielwährung umzurechnen.
- Das System muss Umrechnungen zwischen beliebigen unterstützten Währungen auf Basis der gespeicherten Wechselkurse ermöglichen.
- Die Umrechnung muss deterministisch auf Basis der aktiven Wechselkursversion erfolgen.
- Das System muss Start- und Zielbetrag anzeigen.

### 3.4 Transaktionsmanagement

- Das System muss jede Währungsumrechnung als Transaktion in einer Datenbank persistieren.
- Das System muss Transaktionen inklusive des verwendeten Wechselkurses und der Wechselkursversion speichern.
- Das System muss jede Transaktion eindeutig mit einem fachlichen Status kennzeichnen.
- Consultants dürfen Transaktionen nicht fachlich verändern.
- Admins können Transaktionen revertieren. Eine revertierte Transaktion:
    - bleibt persistent gespeichert,
    - ist unveränderlich,
    - wird fachlich nicht mehr berücksichtigt.
- Jede Revertierung erfordert eine Begründung und wird revisionssicher protokolliert.

### 3.5 Zugriff, Filter und Historie

- Consultants können ihre eigenen Transaktionen abrufen und filtern.
- Admins können alle Transaktionen systemweit abrufen und filtern.
- Filterkriterien umfassen mindestens Datum, Währung, Status und Benutzer.

---

## 4. Nicht-funktionale Anforderungen

### 4.1 Benutzbarkeit (Usability)

- Das System muss ohne Schulung nutzbar sein und den Benutzer durch klar beschriftete Eingabefelder und Aktionen führen.
- Für alle zentralen Funktionen (Umrechnung, Transaktionsübersicht, Kurs-Upload) müssen kontextsensitive Hilfetexte zur Verfügung stehen.
- Fehlbedienungen (z. B. ungültige Beträge, unbekannte Währungen) müssen durch verständliche Fehlermeldungen abgefangen werden.
- Die Anzahl der notwendigen Benutzerinteraktionen für eine Standard-Umrechnung beträgt maximal:
    - Eingabe Betrag
    - Auswahl Quellwährung
    - Auswahl Zielwährung
    - Auslösen der Umrechnung

### 4.2 Performance und Reaktionszeiten

- Eine Währungsumrechnung muss lokal innerhalb von < 500 ms durchgeführt werden.
- Das Laden der Transaktionsübersicht mit bis zu 10.000 Transaktionen muss innerhalb von < 2 Sekunden erfolgen.
- Der Upload und die Validierung einer Wechselkurs-JSON-Datei mit bis zu 500 Währungen muss innerhalb von < 5 Sekunden erfolgen.

### 4.3 Zuverlässigkeit und Stabilität

- Das System muss ungültige oder fehlerhafte JSON-Dateien ohne Absturz verarbeiten.
- Bei fehlerhaften Wechselkursdaten darf keine Teilübernahme durchgeführt werden (Alles-oder-Nichts-Prinzip).
- Persistierte Transaktionen dürfen auch bei unerwartetem Applikationsabbruch nicht verloren gehen.
- Revertierte Transaktionen müssen dauerhaft nachvollziehbar bleiben.

### 4.4 Sicherheit

- Passwörter dürfen nicht im Klartext gespeichert werden.
- Nach dreimaliger fehlerhafter Anmeldung muss der Benutzer temporär gesperrt werden.
- Rollenbasierte Zugriffe müssen verhindern, dass Consultants administrative Funktionen ausführen.
- Revertierungen von Transaktionen sind ausschliesslich Admins erlaubt und werden protokolliert.

### 4.5 Wartbarkeit und Erweiterbarkeit

- Die Wechselkursverarbeitung muss fachlich von der Benutzeroberfläche getrennt sein.
- Das System muss die Erweiterung um zusätzliche Währungen ohne Codeänderung erlauben.
- Neue Benutzerrollen müssen ohne grundlegende Architekturänderung ergänzt werden können.
- Fachliche Regeln (z. B. Revert-Logik) müssen zentral konfiguriert und nicht in der UI verankert sein.

### 4.6 Portabilität und Plattform

- Das System muss auf gängigen Desktop-Betriebssystemen (Windows, macOS, Linux) mit unterstützter Java-Laufzeitumgebung lauffähig sein.
- Die Applikation darf keine permanente Internetverbindung benötigen.
- Die Installation muss lokal ohne externe Systemabhängigkeiten erfolgen.

### 4.7 Nachvollziehbarkeit und Auditierbarkeit

- Jede Transaktion muss eindeutig identifizierbar sein.
- Jede Revertierung muss Benutzer, Zeitstempel und Begründung enthalten.
- Änderungen an Systemzuständen müssen revisionssicher nachvollziehbar sein.

---

## 5. Systemvoraussetzungen (Prerequisites)

### 5.1 Client-Seite

- Betriebssystem: Windows 10/11, macOS 10.15+, Linux
- Java-Laufzeitumgebung (unterstützte Version)
- Keine permanente Internetverbindung erforderlich

### 5.2 Datenhaltung

- Lokale relationale Datenbank (z. B. eingebettete Datenbank wie H2 oder SQLite)
- Mindestens 1 GB freier Speicherplatz (skalierbar)

---

## 6. Preis und Betrieb

> **TODO**: Neue Preisangaben ausstehend – wird aktualisiert, sobald Zahlen vorliegen.
> 

---

## 7. Zukünftige Erweiterungen (nicht Teil des MVP)

> **TODO**: Inhalt wird noch geliefert.
> 

---

## 8. Abnahmezustand

Das System gilt als fertiggestellt, wenn alle in diesem Dokument beschriebenen funktionalen und nicht-funktionalen Anforderungen umgesetzt, getestet und dokumentiert sind und das System im vorgesehenen Betriebsumfeld stabil betrieben werden kann.
