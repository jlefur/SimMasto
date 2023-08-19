@echo off

rem The version of Repast Simphony being used.
set VERSION=2.0.0

rem Java path
set JAVAPATH="C:\Program Files (x86)\Java\jre6\bin\java.exe"

rem The plugins path of Eclipse.
set PLUGINS=C:\RepastSimphony-2.0-beta/eclipse/plugins

rem The name of the model. This might be case-sensitive.
set MODELNAME=SIMmasto_0

rem The folder of the model. This might be case-sensitive.
set MODELFOLDER=C:\Users\cbgp-z400\workspaceRepast2.0\%MODELNAME%

rem The file containing the batch parameters.
set BATCHPARAMS=%MODELFOLDER%/batch/batch_params%RUN%.xml

rem The repast.simphony.runtime librairie
set RUNTIME_LIB=%PLUGINS%/repast.simphony.runtime_2.0.0/lib/

REM Define the Core Repast Simphony Directories and JARs
SET CP=%PLUGINS%/repast.simphony.batch_%VERSION%/bin

SET CP=%CP%;%RUNTIME_LIB%saf.core.runtime.jar
SET CP=%CP%;%RUNTIME_LIB%commons-logging-1.0.4.jar
SET CP=%CP%;%RUNTIME_LIB%groovy-all-1.7.5.jar
SET CP=%CP%;%RUNTIME_LIB%javassist-3.7.0.GA.jar
SET CP=%CP%;%RUNTIME_LIB%jpf.jar
SET CP=%CP%;%RUNTIME_LIB%jpf-boot.jar
SET CP=%CP%;%RUNTIME_LIB%log4j-1.2.13.jar
SET CP=%CP%;%RUNTIME_LIB%xpp3_min-1.1.4c.jar
SET CP=%CP%;%RUNTIME_LIB%xstream-1.3.jar
SET CP=%CP%;%RUNTIME_LIB%commons-cli-1.0.jar

SET CP=%CP%;%PLUGINS%/repast.simphony.core_%VERSION%/lib/*
SET CP=%CP%;%PLUGINS%/repast.simphony.core_%VERSION%/bin
SET CP=%CP%;%PLUGINS%/repast.simphony.bin_and_src_%VERSION%/*
SET CP=%CP%;%PLUGINS%\libs.bsf_2.0.0/lib/bsh-2.0b4.jar
SET CP=%CP%;%PLUGINS%/repast.simphony.essentials_2.0.0/lib/*

SET CP=%CP%;%MODELFOLDER%/lib/commons-math-2.2/commons-math-2.2.jar
SET CP=%CP%;%MODELFOLDER%/lib/jfreechart-1.0.13/*
rem SET CP=%CP%;%MODELFOLDER%/lib/commons-math-2.2.jar
rem SET CP=%CP%;%MODELFOLDER%/lib/jfreechart-1.0.13.jar
SET CP=%CP%;%MODELFOLDER%/bin

rem Execute in batch mode.
%JAVAPATH% -Xss10M -Xmx400M -cp %CP% repast.simphony.batch.BatchMain -params %BATCHPARAMS% %MODELFOLDER%/%MODELNAME%.rs

pause 
echo Le run %RUN% est fini
echo fichier xml : %BATCHPARAMS%