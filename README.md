# IntegrityDocs
Creates a set of HTML pages documenting the PTC Integrity Lifecycle Manager configuration.

## Version
11.0.1: Created for Integrity 10.9/11.x and with full support of Firefox / Chrome / IE

## Develpment Status 
Under Construction

## Purpose
IntegrityDocs exports the current Integrity Server configuration from W&D, and creates HTML pages. Each section under Worklows&Documents will become one node within the report tree. Each section starts with an "Overview" page, and then each element can be reviewed in more detail by drilling down. 

![IntegrityDocs](Doc/IntegrityDocs.png)

## Use Cases
- Document the current configuration status
- Validate and compare the configuration status
- Present the Integrity LM configuration status without the need to have direct Integrity access

## Install
In IntegrityClient folder
- Put the "lib/IntegrityDocs.jar" into your IntegrityClient/lib folder
- Put the "IntegrityDocs.bat" directly into your IntegrityClient folder
- Check and update the IntegrityDocs.bat with the correct client location:
```
SET IC_HOME=C:\Integrity\ILMClient11
```
- In case of any performance issue based on too many data in Integrity, you can also disable certain object types from beeing scanned, such as   
```
--noQueries:      disable Queries scan and output
--noTriggers:     disable Triggers scan and output
--noCharts:       disable Charts scan and output
--noViewsets:     disable Viewsets scan and output
--noGroups:       disable Groups scan and output
--noDynGroups:    disable DynGroups scan and output
--noStates:       disable States scan and output
--noReports:      disable Reports scan and output
--noFields:       disable Fields scan and output
--noTestVerdicts: disable TestVerdict scan and output
```

## How to run
- run the IntegrityDocs.bat in your IntegrityClient folder
- connect to the environment you like to scan
- Then review the outcome

##  Development environment
- PTC Integrity LM 11.0 (also 10.9 is fine)
- Netbeans 7.4 (or 8)
- Java 1.8

## Known Limitations
- the search does not work yet
- there is just a limited list of attributes extracted, for example for fields
- not all W&D objects are scanned yet, but the most important
- none of the SI or MKS Domain object is exported (primarily because of the expected data volume)
