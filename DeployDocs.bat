@echo off

::
:: Usage
:: ----------------------------------------------------------------------------------------
:: All Staging Systems, Stages, and Targets: 
:: 					DeployDocs.bat <no arguments>
:: Specific Staging Systems, and associated Stages and Targets:
:: 					DepoyDocs.bat <stagingsystem1> <stagingsystem3> <stagingsystem3>
::

:: Configure ICHOME to point to your client installation
SET IC_HOME=C:\Program Files\MKS\IntegrityClient

:: Change Directory to IC_HOME
cd /d "%IC_HOME%"

:: Do not edit below this line
"%IC_HOME%\jre\bin\java.exe" -Xms128m -Xmx128m -cp "lib\mksapi.jar;lib\mksclient.jar;lib\IntegrityDocs.jar" com.mks.services.utilities.docgen.DeployDocs %1 %2 %3 %4 %5 %6 %7 %8 %9

pause