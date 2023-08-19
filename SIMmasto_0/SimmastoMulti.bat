@echo off

rem Run en cours
set RUN=0
rem Nombre de runs à lancer
set nbRuns=500
rem Script lancement SimMasto
set batch="ScriptSimmasto.bat"

:boucle
call %batch% %RUN%
set /a RUN = %RUN%+1
rem echo %RUN%
IF not "%RUN%"=="%nbRuns%" goto boucle

pause