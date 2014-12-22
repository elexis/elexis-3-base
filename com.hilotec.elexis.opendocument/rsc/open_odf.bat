rem Script for Windows for the Elexis opendocument plugin
rem (c) copyright 2012-2013 by Niklaus Giger niklaus.giger@member.fsf.org
set EXE_TO_USE=%1
set FILE_TO_EDIT=%2
time /t >> test.log
echo open %FILE_TO_EDIT% with  %EXE_TO_USE% >> test.log
dir %EXE_TO_USE%
del %FILE_TO_EDIT%.marker
start %1 %2 %3 %4 %5 %6 %7 %8 %9
sleep 5
:versuche_datei_zu_bewegen
move %FILE_TO_EDIT% %FILE_TO_EDIT%.marker
if exist %FILE_TO_EDIT%.marker goto datei_bewegt
:fehlschlag
rem echo Konnte Datei nicht bewegen
sleep 2
goto versuche_datei_zu_bewegen

:datei_bewegt
rem echo Konnte Datei bewegen
move %FILE_TO_EDIT%.marker %FILE_TO_EDIT%
time /t >> test.log
echo close %FILE_TO_EDIT% >> test.log
exit 0

:ende
echo Bin am Ende

:exit
