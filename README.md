# Praktikum Software Engineering für Fortgeschrittene: The Bug is a Lie 🐞

Dieses Repository soll als Einstieg in das Praktikum dienen.
Es wird eine Beispielanwendung (Prototyp) bereitgestellt,
welche bereits einige der erforderlichen Technologien verwendet.

Weitere Informationen befinden sich auf der [SWEP Homepage](https://www.sosy-lab.org/Teaching/2019-SS-SWEP/).

## Spielanleitung

Eine Spielanleitung ist [hier als PDF](TBIAL_Spielanleitung.pdf) verfügbar.
Die Spielkarten finden sich [hier als PDF](TBIAL_Spielkarten.pdf).

## Beispielanwendung ausführen

Dieses Repository stellt eine lauffähige Beispielanwendung zur Verfügung,
die bereits auf den notwendigen Technologien (Apache Tomcat, Apache Derby, Wicket) basiert.
Die Beispielanwendung ist als Projekt für Eclipse verfügbar.
Nachfolgend ist grob skizziert, wie das Projekt in Eclipse ausgeführt wird.

### Vorbereitung

- *Eclipse IDE for Java EE Developers* (Paket [hier](https://www.eclipse.org/downloads/eclipse-packages/) verfügbar
  (**Achtung**: das EE Developers Paket wählen!)
- *Apache Tomcat 8* (oder neuer) in Eclipse einrichten
  (`Window/Preferences/Server/Runtime Environment -> Add -> Apache Tomcat 8` (oder neuer))
- *Apache Derby* installieren und einrichten, Anleitung [hier](https://db.apache.org/derby/quick_start.html) verfügbar
- *Maven* installieren und einrichten, falls notwendig

### Projekt Setup und Ausführung

- Dieses Projekt-Repository auschecken und den Unterordner
  [`de.lmu.ifi.sosy.tbial`](de.lmu.ifi.sosy.tbial)
  als Eclipse-Projekt einrichten - Apache Derby starten (als separaten Prozess!), ansonsten steht keine Datenbank zur Verfügung
- Aus dem Unterordner `de.lmu.ifi.sosy.tbial` heraus ausführen:
    + `mvn exec:exec@create-development-db` einmalig, um die Datenbank zu initialisieren
    + `mvn install`, um das Projekt zu kompilieren
- Eclipse: `Run on Server`, dann den eingerichteten Tomcat-Server wählen, um das Projekt zu deployen

Sollten bei der erstmaligen Ausführung Fehler wie `ClassNotFound` bzgl. Wicket auftreten,
kann es sein, dass Eclipse noch nicht alle benötigten Libraries erkannt hat.
Möglicherweise hilft dann `maven clean` oder ein Neustart von Eclipse,
gefolgt von den oben genannten Schritten.

### Screenshot

Die Webseite des gegebenen Prototyps sollte in etwa folgendermaßen aussehen:

![Screenshot des Prototyps border](Screenshot_Prototype.png)
