Weisseseiten Plugin
===========================
Author:
  Medshare GmbH (Michael Imhof)
Plugin-Name: 
  ch.medshare.elexis_directories
Abhängigkeiten:
  ch.elexis
Beschreibung:
  Das Plugin besteht aus einer View mit zwei Suchfelder und einer 
  Liste. Bei der Suche (Suchknopf oder Return) werden über die Internet-Seite
  www.directories.ch/weisseseiten Adressen gesucht.
Probleme:
  Die Suchresultate werden aus der retournierten Html-Seite herausgeparst. Ändert
  sich was, so muss das Plugin angepasst werden.
  Die Anpassungen betreffen nur die Klasse DirectoriesContentParser.class.