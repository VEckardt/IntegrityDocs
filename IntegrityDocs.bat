@echo off

::
:: Usage: IntegrityDocs.bat [--noTriggers] [--noQueries] [--xml] [<type1>] [<type2>] [<type3>]
:: ---------------------------------------------------------------------------------------------------
:: Examples:
:: Report on all Types and Triggers:			IntegrityDocs.bat
:: Report on specific Types and all Triggers:	IntegrityDocs.bat <type1> <type2> <type3>
:: Report on all Types, but no Triggers:		IntegrityDocs.bat --noTriggers
:: Report on specific Types and no Triggers:	IntegrityDocs.bat --noTriggers <type1> <type2> <type3>
:: Generate xml for all Types and Triggers:		IntegrityDocs.bat --xml
:: Generate xml for selected types:				IntegrityDocs.bat --xml <type1> <type2> <type3>
::

:: Configure ICHOME to point to your client installation
SET IC_HOME=C:\Program Files (x86)\Integrity\IntegrityClient10

:: Change Directory to IC_HOME
cd /d "%IC_HOME%"

:: Do not edit below this line
"%IC_HOME%\jre\bin\java.exe" -XX:MaxPermSize=128m -Xss256k -Xms256m -Xmx512m -cp "lib\mksapi.jar;lib\mksclient.jar;lib\IntegrityDocs.jar" com.mks.services.utilities.docgen.IntegrityDocs %1 %2 %3 %4 %5 %6 %7 %8 %9

pause