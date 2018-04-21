# IntegrityDocs
Creates a set of HTML pages documenting the PTC Integrity Lifecycle Manager configuration.

## Version
11.0.1: Created for Integrity 10.9/11.x and with full support of Firefox / Chrome / IE

## Development Status 
Under Construction

## Purpose
IntegrityDocs exports the current Integrity Server configuration from W&D, and creates HTML pages. Each section under Worklows&Documents will become one node within the report tree. Each section starts with an "Overview" page, and then each element can be reviewed in more detail by drilling down. 

![IntegrityDocs](Doc/IntegrityDocs.png)

## Use Cases
- Document the current Integrity LM configuration status
- Validate and compare the Integrity LM configuration status
- Present the Integrity LM configuration status without the need to have direct Integrity access

## Install
In IntegrityClient folder
- Put the "IntegrityDocs.jar" into your IntegrityClient/lib folder
- Put the "IntegrityDocs.bat" directly into your IntegrityClient folder

- Check and update the IntegrityDocs.bat with the correct client location:
```
SET IC_HOME=C:\Integrity\ILMClient11
```
- In case of any performance issue based on too many data in Integrity, you can also disable certain object types from beeing scanned, such as   
```
--noIMProjects:       disable Workflows & Documents Main Projects scan and output
--noSIProjects:       disable Config Management Main Projects scan and output
--noTypes:            disable Types scan and output
--noQueries:          disable Queries scan and output
--noTriggers:         disable Triggers scan and output
--noCharts:           disable Charts scan and output
--noViewsets:         disable Viewsets scan and output
--noGroups:           disable Groups scan and output
--noDynGroups:        disable DynGroups scan and output
--noStates:           disable States scan and output
--noReports:          disable Reports scan and output
--noFields:           disable Fields scan and output
--noTestVerdicts:     disable TestVerdict scan and output
--noTestResultFields: disable TestResultFields scan and output

```
- You can add these parameters to the bat command itself or put them directly into IntegrityDocs.bat, 
```
... ityDocs.jar" com.ptc.services.utilities.docgen.IntegrityDocs --noFields --noGroups
```
- It's also possible to generate the output just for one or specific types, then the command will look like this
```
... ityDocs.jar" com.ptc.services.utilities.docgen.IntegrityDocs Defect Project
```

## How to run
- execute the IntegrityDocs.bat in your IntegrityClient folder
- connect to the Integrity environment you like to scan
- then review the outcome

## Verson Info
- 11.0.1: You can sort the Overview tables by clicking at the light green headings 
- 11.0.2: Added Main Project for W&D
- 11.0.2: Reports now previewable and downloadable
- 11.0.3: Edit-In-Word Templates download now possible (links provided in the Type form)
- 11.0.3: Added Main Project for CM

##  Development Environment
- PTC Integrity LM 11.0 (also 10.9 is fine)
- Netbeans 8
- Java 1.8

## Known Limitations
- the search does not work yet
- none of the SI or MKS Domain object is exported (primarily because of the expected high data volume)
- the "deploy" reporting is no more supported since 10.5 or so 
