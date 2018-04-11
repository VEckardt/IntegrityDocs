package com.ptc.services.utilities.docgen;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import com.mks.api.response.APIException;

public class StagingSystem {

    private String id;
    private String name;
    private String project;
    private String description;
    private String defaultDeployType;
    private String stageNames;
    private List<Stage> stageList;
    private List<DeployType> deployTypes;
    private List<Action> preIncrementalActions;
    private List<Action> postIncrementalActions;
    private List<Action> preFullActions;
    private List<Action> postFullActions;
    private Hashtable<String, String> properties;
    private Integrity i;

    public StagingSystem(Integrity iToolkit, String stagingSystem) throws APIException {
        // Initialize our class variables
        i = iToolkit;
        id = new String();
        name = new String();
        project = new String();
        description = new String();
        defaultDeployType = new String();
        stageList = new ArrayList<Stage>();
        deployTypes = new ArrayList<DeployType>();
        preIncrementalActions = new ArrayList<Action>();
        postIncrementalActions = new ArrayList<Action>();
        preFullActions = new ArrayList<Action>();
        postFullActions = new ArrayList<Action>();
        properties = new Hashtable<String, String>();
        // Parse the Staging System Work Item
        parseStagingSystem(stagingSystem);
        // Get a list of the Stages - Workaround
        //parseStages(stagingSystem);
    }

    @SuppressWarnings("unchecked")
    private void parseStagingSystem(String stagingSystem) throws APIException {
        // Run a sd viewstaging system command to get information for the staging system
        WorkItem sdStagingSystem = i.viewStagingSystem(stagingSystem);
        // Get the Staging System ID
        Field idFld = sdStagingSystem.getField("id");
        if (null != idFld && null != idFld.getValueAsString()) {
            id = idFld.getValueAsString();
        }

        // Get the name of the Staging System (redundant)
        Field nameFld = sdStagingSystem.getField("name");
        if (null != nameFld && null != nameFld.getValueAsString()) {
            name = nameFld.getValueAsString();
        }

        // Get the project that backs this Staging System
        Field projectFld = sdStagingSystem.getField("project");
        if (null != projectFld && null != projectFld.getValueAsString()) {
            project = projectFld.getValueAsString();
        }

        // Get the description for the Staging System
        Field descriptionFld = sdStagingSystem.getField("description");
        if (null != descriptionFld && null != descriptionFld.getValueAsString()) {
            description = descriptionFld.getValueAsString();
        }

        // Get the default deploy type for the Staging System
        Field defDepTypeFld = sdStagingSystem.getField("defaultdeploytype");
        if (null != defDepTypeFld && null != defDepTypeFld.getValueAsString()) {
            defaultDeployType = defDepTypeFld.getValueAsString();
        }

        // Get the list of Stages for this Staging System and instantiate a Stage object,
        // which will in turn fetch the details for the Stage
        Field stagesFld = sdStagingSystem.getField("StagesSubselection");
        if (null != stagesFld && null != stagesFld.getList()) {
            List<Item> stagesList = stagesFld.getList();
            StringBuffer stageNamesBuf = new StringBuffer();
            for (Iterator<Item> it = stagesList.iterator(); it.hasNext();) {
                String strStage = it.next().getId();
                Stage stage = new Stage(i, stagingSystem, strStage);
                stageList.add(stage);
                stageNamesBuf.append(stageNamesBuf.length() > 0 ? ", " + strStage : strStage);
            }
            stageNames = stageNamesBuf.toString();
        }

        // Get the Deploy Types for this Staging System
        Field deployTypesFld = sdStagingSystem.getField("DeployTypesSubselection");
        if (null != deployTypesFld && null != deployTypesFld.getList()) {
            List<Item> deployTypesList = deployTypesFld.getList();
            for (Iterator<Item> it = deployTypesList.iterator(); it.hasNext();) {
                DeployType dt = new DeployType(it.next());
                deployTypes.add(dt);
            }
        }

        // Get the pre-incremental actions for this Staging System
        Field preIncrActionsFld = sdStagingSystem.getField("PreIncrementalActionsSubselection");
        if (null != preIncrActionsFld && null != preIncrActionsFld.getList()) {
            List<Item> preIncrActionsList = preIncrActionsFld.getList();
            for (Iterator<Item> it = preIncrActionsList.iterator(); it.hasNext();) {
                Action a = new Action(it.next());
                preIncrementalActions.add(a);
            }
        }

        // Get the post incremental actions for this Staging System		
        Field postIncrActionsFld = sdStagingSystem.getField("PostIncrementalActionsSubselection");
        if (null != postIncrActionsFld && null != postIncrActionsFld.getList()) {
            List<Item> postIncrActionsList = postIncrActionsFld.getList();
            for (Iterator<Item> it = postIncrActionsList.iterator(); it.hasNext();) {
                Action a = new Action(it.next());
                postIncrementalActions.add(a);
            }
        }

        // Get the pre full actions for this Staging System		
        Field preFullActionsFld = sdStagingSystem.getField("PreFullActionsSubselection");
        if (null != preFullActionsFld && null != preFullActionsFld.getList()) {
            List<Item> preFullActionsList = preFullActionsFld.getList();
            for (Iterator<Item> it = preFullActionsList.iterator(); it.hasNext();) {
                Action a = new Action(it.next());
                preFullActions.add(a);
            }
        }

        // Get the post full actions for this Staging System		
        Field postFullActionsFld = sdStagingSystem.getField("PostFullActionsSubselection");
        if (null != postFullActionsFld && null != postFullActionsFld.getList()) {
            List<Item> postFullActionsList = postFullActionsFld.getList();
            for (Iterator<Item> it = postFullActionsList.iterator(); it.hasNext();) {
                Action a = new Action(it.next());
                postFullActions.add(a);
            }
        }

        // Get the properties for this Staging System		
        Field propertiesFld = sdStagingSystem.getField("PropertiesSubselection");
        if (null != propertiesFld && null != propertiesFld.getList()) {
            List<Item> propertiesList = propertiesFld.getList();
            for (Iterator<Item> it = propertiesList.iterator(); it.hasNext();) {
                Item propertyItem = it.next();
                Field keyFld = propertyItem.getField("key");
                Field valueFld = propertyItem.getField("value");
                if (null != keyFld && null != keyFld.getValueAsString() && null != valueFld && null != valueFld.getValueAsString()) {
                    properties.put(keyFld.getValueAsString(), valueFld.getValueAsString());
                }
            }
        }
    }

    /**
     * A workaround parsing alternative to the sd viewstage API Error
     *
     * @param stagingSystem
     * @throws APIException
     */
    /*
     private void parseStages(String stagingSystem) throws APIException
     {
     List<WorkItem> deployStageList = i.getStages(stagingSystem);
     for( Iterator<WorkItem> it = deployStageList.iterator(); it.hasNext(); )
     {
     Stage stage = new Stage(i, stagingSystem, it.next());
     stageList.add(stage);
     }		
     }
     */
    // Return the information parsed
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProject() {
        return project;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultDeployType() {
        return defaultDeployType;
    }

    public String getStageNames() {
        return stageNames;
    }

    public List<Stage> getStages() {
        return stageList;
    }

    public List<DeployType> getDeployTypes() {
        return deployTypes;
    }

    public List<Action> getPreIncrementalActions() {
        return preIncrementalActions;
    }

    public List<Action> getPostIncrementalActions() {
        return postIncrementalActions;
    }

    public List<Action> getPreFullActions() {
        return preFullActions;
    }

    public List<Action> getPostFullActions() {
        return postFullActions;
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }

    // Get a formatted report for the deploy types
    public String getFormattedDeployTypeReport() {
        StringBuffer report = new StringBuffer();
        // Ensure we've got some deploy types to work with
        if (deployTypes.size() > 0) {
            // Construct the open table and heading line
            report.append("<table class='list'>" + DeployDocs.nl);
            report.append("  <tr>" + DeployDocs.nl);
            report.append("    <th>Name</th>" + DeployDocs.nl);
            report.append("    <th>Default</th>" + DeployDocs.nl);
            report.append("    <th>Pattern</th>" + DeployDocs.nl);
            report.append("    <th>Description</th>" + DeployDocs.nl);
            report.append("  </tr>" + DeployDocs.nl);

            // Loop thru all the deploy types
            for (Iterator<DeployType> it = deployTypes.iterator(); it.hasNext();) {
                // Get the Deploy Type object
                DeployType deployType = it.next();
                // Write out the new table row
                report.append("  <tr>" + DeployDocs.nl);
                // Get the value for the Name
                report.append("    <td>" + deployType.getName() + "</td>" + DeployDocs.nl);
                // Get the default value
                report.append("    <td>" + deployType.getIsDefault() + "</td>" + DeployDocs.nl);
                // Get the pattern value
                report.append("    <td>" + deployType.getPattern() + "</td>" + DeployDocs.nl);
                // Get the description
                report.append("    <td>" + deployType.getDescription() + "</td>" + DeployDocs.nl);
                // Close out the table row
                report.append("  </tr>" + DeployDocs.nl);
            }

            // Close the table tag
            report.append("</table>" + DeployDocs.nl);
        }
        return report.toString();
    }

    // Get a formatted report for the various actions
    public String getFormattedActionsReport(List<Action> actions) {
        StringBuffer report = new StringBuffer();
        // Ensure we've got some deploy types to work with
        if (actions.size() > 0) {
            // Construct the open table and heading line
            report.append("<table class='list'>" + DeployDocs.nl);
            report.append("  <tr>" + DeployDocs.nl);
            report.append("    <th>Script</th>" + DeployDocs.nl);
            report.append("    <th>Parameters</th>" + DeployDocs.nl);
            report.append("  </tr>" + DeployDocs.nl);

            // Loop thru all the actions types
            for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
                // Get the Action object
                Action a = it.next();
                // Write out the new table row
                report.append("  <tr>" + DeployDocs.nl);
                // Get the value for the Script
                report.append("    <td>" + a.getScript() + "</td>" + DeployDocs.nl);
                // Get the value for the Parameters
                report.append("    <td>" + a.getParameters() + "</td>" + DeployDocs.nl);
                // Close out the table row
                report.append("  </tr>" + DeployDocs.nl);
            }

            // Close the table tag
            report.append("</table>" + DeployDocs.nl);
        }

        return report.toString();
    }

    // Get a formatted report for the properties
    public String getFormattedPropertiesReport() {
        StringBuffer report = new StringBuffer();
        // Ensure we've got some properties to work with
        if (properties.size() > 0) {
            // Construct the open table and heading line
            report.append("<table class='list'>" + DeployDocs.nl);
            report.append("  <tr>" + DeployDocs.nl);
            report.append("    <th>Key</th>" + DeployDocs.nl);
            report.append("    <th>Value</th>" + DeployDocs.nl);
            report.append("  </tr>" + DeployDocs.nl);

            Enumeration<String> keys = properties.keys();
            List<String> propList = new ArrayList<String>();
            while (keys.hasMoreElements()) {
                propList.add(keys.nextElement());
            }
            Collections.sort(propList);
            // Loop thru all the properties
            for (Iterator<String> propIt = propList.iterator(); propIt.hasNext();) {
                String key = propIt.next();
                // Write out the new table row
                report.append("  <tr>" + DeployDocs.nl);
                // Get the Key name
                report.append("    <td>" + key + "</td>" + DeployDocs.nl);
                // Get the value of the Key
                report.append("    <td>" + properties.get(key) + "</td>" + DeployDocs.nl);
                // Close out the table row
                report.append("  </tr>" + DeployDocs.nl);
            }

            // Close the table tag
            report.append("</table>" + DeployDocs.nl);
        }
        return report.toString();
    }

    // Get a formatted report for the Stages
    public String getFormattedStageReport() {
        StringBuffer report = new StringBuffer();
        // Ensure we've got some stages to work with
        if (stageList.size() > 0) {
            // Construct the open table and heading line
            report.append("<table class='list'>" + DeployDocs.nl);
            report.append("  <tr>" + DeployDocs.nl);
            report.append("    <th>Position</th>" + DeployDocs.nl);
            report.append("    <th>Name</th>" + DeployDocs.nl);
            report.append("    <th>Deploy Policy</th>" + DeployDocs.nl);
            report.append("    <th>Transfer Mode</th>" + DeployDocs.nl);
            report.append("    <th>Transfer Frequency</th>" + DeployDocs.nl);
            report.append("    <th>Deploy Mode</th>" + DeployDocs.nl);
            report.append("    <th>Deploy Frequency</th>" + DeployDocs.nl);
            report.append("    <th>Auto Promote?</th>" + DeployDocs.nl);
            report.append("    <th>Details</th>" + DeployDocs.nl);
            report.append("    <th>Properties</th>" + DeployDocs.nl);
            report.append("  </tr>" + DeployDocs.nl);

            // Loop thru all the stages
            for (Iterator<Stage> it = stageList.iterator(); it.hasNext();) {
                // Get the Stage object
                Stage stage = it.next();
                // Write out the new table row
                report.append("  <tr>" + DeployDocs.nl);
                // Get the value for the Position
                report.append("    <td rowspan='2'>" + stage.getPosition() + "</td>" + DeployDocs.nl);
                // Get the Stage Name
                report.append("    <td>" + stage.getName() + "</td>" + DeployDocs.nl);
                // Get the Deploy Policy
                report.append("    <td>" + stage.getDeployPolicy() + "</td>" + DeployDocs.nl);
                // Get the Transfer Mode
                report.append("    <td>" + stage.getTransferMode() + "</td>" + DeployDocs.nl);
                // Get the Transfer Frequency
                report.append("    <td>" + stage.getTransferFrequency() + "</td>" + DeployDocs.nl);
                // Get the Deploy Mode
                report.append("    <td>" + stage.getDeployMode() + "</td>" + DeployDocs.nl);
                // Get the Deploy Frequency
                report.append("    <td>" + stage.getDeployFrequency() + "</td>" + DeployDocs.nl);
                // Get the Auto Promote Flag
                report.append("    <td>" + stage.getAutoPromote() + "</td>" + DeployDocs.nl);
                // Get the Details on this Stage:  ID, Description, Automatic Rollback Timeout, Last Scheduled Transfer Time, Last Scheduled Deploy Time, Project
                report.append("    <td><b>Stage ID: </b>" + stage.getID() + "<br>" + DeployDocs.nl);
                report.append("    <b>Description: </b>" + stage.getDescription() + "<br>" + DeployDocs.nl);
                report.append("    <b>Automatic Rollback Timeout: </b>" + stage.getAutomaticRollbackTimeout() + "<br>" + DeployDocs.nl);
                if (stage.getTransferMode().equalsIgnoreCase("scheduled")) {
                    report.append("    <b>Last Scheduled Transfer Time: </b>" + stage.getLastScheduledTransferTime() + "<br>" + DeployDocs.nl);
                }
                if (stage.getDeployMode().equals("scheduled")) {
                    report.append("    <b>Last Scheduled Deploy Time: </b>" + stage.getLastScheduledDeployTime() + "<br>" + DeployDocs.nl);
                }
                report.append("    <b>Project: </b>" + stage.getProject() + "<br>" + DeployDocs.nl);
                List<String> reviewerList = stage.getReviewers();
                if (reviewerList.size() > 0) {
                    report.append("    <b>Reviewers: </b><br>" + DeployDocs.nl);
                    for (Iterator<String> revIt = reviewerList.iterator(); revIt.hasNext();) {
                        report.append("    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + revIt.next() + "<br>" + DeployDocs.nl);
                    }
                }
                report.append("    </td>" + DeployDocs.nl);
                report.append("    <td>" + DeployDocs.nl);
                Hashtable<String, String> stageProps = stage.getProperties();
                Enumeration<String> keys = stageProps.keys();
                List<String> propList = new ArrayList<String>();
                while (keys.hasMoreElements()) {
                    propList.add(keys.nextElement());
                }
                Collections.sort(propList);
                for (Iterator<String> propIt = propList.iterator(); propIt.hasNext();) {
                    String key = propIt.next();
                    report.append("    " + key + " = " + stageProps.get(key) + "<br>" + DeployDocs.nl);
                }
                report.append("    </td>" + DeployDocs.nl);
                // Close out the table row
                report.append("  </tr>" + DeployDocs.nl);

                // Open a new table row for the Stage's targets
                report.append("  <tr>" + DeployDocs.nl);
                // Write out the heading...
                report.append("    <th>Deploy Targets</th>" + DeployDocs.nl);
                report.append("    <td colspan='8' style='padding: 1px 1px 1px 1px;'>" + stage.getFormattedTargetReport() + "</td>" + DeployDocs.nl);
                report.append("  </tr>" + DeployDocs.nl);
            }
            // Close the table tag
            report.append("</table>" + DeployDocs.nl);
        }
        return report.toString();
    }
}
