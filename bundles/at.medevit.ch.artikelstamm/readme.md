# Elexis Artikelstamm Format

__Copyright 2017, MEDEVIT <office@medevit.at>__ 

## Elexis Artikelstamm Format 

Das Elexis Artikelstamm Format ist das Standard-Artikelaustausch Format, welches im Rahmen einer erweiterten
Analyse in Zusammenarbeit von [http://www.medelexis.ch](Medelexis) , Dr. Franz Marty und MEDEVIT entwickelt wurde.

Es existiert sowohl eine reine Open-Source als auch eine kommerzielle unterstützung für dieses Datenformat. Informationen dazu, die aktuelle Dokumentation zu diesem Format, sowie aktuelle Datensätze sind unter [http://artikelstamm.elexis.info](Elexis Artikelstamm Information) zu finden.

### Sponsor

The development of this plugin was sponsored by Dr. med. Franz Marty, "Medizinisches Zentrum, gleis d":http://www.mez-chur.com/ 

### Version

Version 3.3.0

### Generating java files under src-gen based on the Elexis_Artikelstamm_v5.xsd

Call `xjc -nv -disableXmlSecurity -d src-gen -p at.medevit.ch.artikelstamm ./lib/Elexis_Artikelstamm_v5.xsd`

