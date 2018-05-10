package com.ptc.services.utilities.docgen.stage;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.utils.PathMapping;

public class Stage {

    private String id;
    private String name;
    private String position;
    private String description;
    private boolean autoPromote;
    private String deployPolicy;
    private String automaticRollbackTimeout;
    private String transferMode;
    private String transferFrequency;
    private String lastScheduledTransferTime;
    private String deployMode;
    private String deployFrequency;
    private String lastScheduledDeployTime;
    private String project;
    private List<Target> targetList;
    private List<String> reviewers;
    private Hashtable<String, String> properties;
    private Integrity i;

    public Stage(Integrity iToolkit, String stagingSystem, String stage) throws APIException {
        // Initialize class variables
        i = iToolkit;
        id = new String();
        name = new String();
        position = new String();
        description = new String();
        autoPromote = false;
        deployPolicy = new String();
        automaticRollbackTimeout = new String();
        transferMode = new String();
        transferFrequency = new String();
        lastScheduledTransferTime = new String();
        deployMode = new String();
        deployFrequency = new String();
        lastScheduledDeployTime = new String();
        project = new String();
        targetList = new ArrayList<Target>();
        reviewers = new ArrayList<String>();
        properties = new Hashtable<String, String>();
        parseStage(null, stagingSystem, stage);
        // Get a list of the Deploy Targets - Workaround		
        //parseDeployTargets(stagingSystem, stage);
    }

    public Stage(Integrity iToolkit, String stagingSystem, WorkItem deployStage) throws APIException {
        // Initialize class variables
        i = iToolkit;
        id = new String();
        name = new String();
        position = new String();
        description = new String();
        autoPromote = false;
        deployPolicy = new String();
        automaticRollbackTimeout = new String();
        transferMode = new String();
        transferFrequency = new String();
        lastScheduledTransferTime = new String();
        deployMode = new String();
        deployFrequency = new String();
        lastScheduledDeployTime = new String();
        project = new String();
        targetList = new ArrayList<Target>();
        reviewers = new ArrayList<String>();
        properties = new Hashtable<String, String>();
        parseStage(deployStage, stagingSystem, deployStage.getField("name").getValueAsString());
        // Get a list of the Deploy Targets - Workaround
        //parseDeployTargets(stagingSystem, deployStage.getField("name").getValueAsString());

    }

    @SuppressWarnings("unchecked")
    private void parseStage(WorkItem deployStage, String stagingSystem, String stage) throws APIException {
        // Run a sd viewstage command to get information for the Stage, if needed
        WorkItem sdStage;
        if (null == deployStage) {
            sdStage = i.viewStage(stagingSystem, stage);
        } else {
            sdStage = deployStage;
        }

        // Get the Stage ID
        Field idFld = sdStage.getField("id");
        if (null != idFld && null != idFld.getValueAsString()) {
            id = idFld.getValueAsString();
        }

        // Get the name of the Stage (redundant)
        Field nameFld = sdStage.getField("name");
        if (null != nameFld && null != nameFld.getValueAsString()) {
            name = nameFld.getValueAsString();
        }

        // Get the position for this Stage
        Field positionFld = sdStage.getField("position");
        if (null != positionFld && null != positionFld.getValueAsString()) {
            position = positionFld.getValueAsString();
        }

        // Get the description for the Stage
        Field descriptionFld = sdStage.getField("description");
        if (null != descriptionFld && null != descriptionFld.getValueAsString()) {
            description = descriptionFld.getValueAsString();
        }

        // Get the auto promote for the Stage
        Field autoPromoteFld = sdStage.getField("autopromote");
        if (null != autoPromoteFld && null != autoPromoteFld.getBoolean()) {
            autoPromote = autoPromoteFld.getBoolean().booleanValue();
        }

        // Get the deploy policy for the Stage
        Field deployPolicyFld = sdStage.getField("deploypolicy");
        if (null != deployPolicyFld && null != deployPolicyFld.getValueAsString()) {
            deployPolicy = deployPolicyFld.getValueAsString();
        }

        // Get the automatic rollback timeout for the Stage
        Field automaticRollbackTimeoutFld = sdStage.getField("automaticrollbacktimeout");
        if (null != automaticRollbackTimeoutFld && null != automaticRollbackTimeoutFld.getValueAsString()) {
            automaticRollbackTimeout = automaticRollbackTimeoutFld.getValueAsString();
        }

        // Get the description for the Stage
        Field transferModeFld = sdStage.getField("transfermode");
        if (null != transferModeFld && null != transferModeFld.getValueAsString()) {
            transferMode = transferModeFld.getValueAsString();
        }

        // Get the transfer frequency for the Stage
        Field transferFrequencyFld = sdStage.getField("transferfrequency");
        if (null != transferFrequencyFld && null != transferFrequencyFld.getValueAsString()) {
            transferFrequency = transferFrequencyFld.getValueAsString();
        }

        // Get the last scheduled transfer time for the Stage
        Field lastScheduledTransferTimeFld = sdStage.getField("lastscheduledtransfertime");
        if (null != lastScheduledTransferTimeFld && null != lastScheduledTransferTimeFld.getValueAsString()) {
            lastScheduledTransferTime = lastScheduledTransferTimeFld.getValueAsString();
        }

        // Get the deploy mode for the Stage
        Field deployModeFld = sdStage.getField("deploymode");
        if (null != deployModeFld && null != deployModeFld.getValueAsString()) {
            deployMode = deployModeFld.getValueAsString();
        }

        // Get the deploy frequency for the Stage
        Field deployFrequencyFld = sdStage.getField("deployfrequency");
        if (null != deployFrequencyFld && null != deployFrequencyFld.getValueAsString()) {
            deployFrequency = deployFrequencyFld.getValueAsString();
        }

        // Get the last scheduled deploy time for the Stage
        Field lastScheduledDeployTimeFld = sdStage.getField("lastscheduleddeploytime");
        if (null != lastScheduledDeployTimeFld && null != lastScheduledDeployTimeFld.getValueAsString()) {
            lastScheduledDeployTime = lastScheduledDeployTimeFld.getValueAsString();
        }

        // Get the project that backs this Stage
        Field projectFld = sdStage.getField("project");
        if (null != projectFld && null != projectFld.getValueAsString()) {
            project = projectFld.getValueAsString();
        }

        // Get the list of Targets for this Stage and instantiate a Target object,
        // which will in turn fetch the details for the Target
        Field targetsFld = sdStage.getField("DeployTargetsSubselection");
        if (null != targetsFld && null != targetsFld.getList()) {
            List<Item> targetsList = targetsFld.getList();
            for (Iterator<Item> it = targetsList.iterator(); it.hasNext();) {
                Target tgt = new Target(i, stagingSystem, stage, it.next().getId());
                targetList.add(tgt);
            }
        }

        try {
            // Get the Deploy Types for this Stage
            Field reviewersFld = sdStage.getField("ReviewersSubselection");
            if (null != reviewersFld && null != reviewersFld.getList()) {
                List<Item> reviewersList = reviewersFld.getList();
                for (Iterator<Item> it = reviewersList.iterator(); it.hasNext();) {
                    reviewers.add(it.next().getId());
                }
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        try {
            // Get the properties for this Stage		
            Field propertiesFld = sdStage.getField("PropertiesSubselection");
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
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }
    }

    /**
     * A workaround parsing alternative to the sd viewdeploytarget API Error
     *
     * @param stagingSystem
     * @param stage
     * @throws APIException
     */
    /*
     private void parseDeployTargets(String stagingSystem, String stage) throws APIException
     {
     List<WorkItem> tgtList = i.getDeployTargets(stagingSystem, stage);
     for( Iterator<WorkItem> it = tgtList.iterator(); it.hasNext(); )
     {
     Target deployTarget = new Target(it.next());
     targetList.add(deployTarget);
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

    public String getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public boolean getAutoPromote() {
        return autoPromote;
    }

    public String getDeployPolicy() {
        return deployPolicy;
    }

    public String getAutomaticRollbackTimeout() {
        return automaticRollbackTimeout;
    }

    public String getTransferMode() {
        return transferMode;
    }

    public String getTransferFrequency() {
        return transferFrequency;
    }

    public String getLastScheduledTransferTime() {
        return lastScheduledTransferTime;
    }

    public String getDeployMode() {
        return deployMode;
    }

    public String getDeployFrequency() {
        return deployFrequency;
    }

    public String getLastScheduledDeployTime() {
        return lastScheduledDeployTime;
    }

    public String getProject() {
        return project;
    }

    public List<Target> getTargets() {
        return targetList;
    }

    public int getTargetCount() {
        return targetList.size();
    }

    public List<String> getReviewers() {
        return reviewers;
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }

    // Get a formatted report for the targets
    public String getFormattedTargetReport() {
        StringBuffer report = new StringBuffer();
        // Ensure we've got some targets to work with
        if (targetList.size() > 0) {
            // Construct the open table and heading line
            report.append("<table class='list' style='width: 100%;'>" + DeployDocs.nl);
            report.append("  <tr>" + DeployDocs.nl);
            report.append("    <th>ID</th>" + DeployDocs.nl);
            report.append("    <th>Name</th>" + DeployDocs.nl);
            report.append("    <th>Connection Status</th>" + DeployDocs.nl);
            report.append("    <th>Sync Status</th>" + DeployDocs.nl);
            report.append("    <th>Deploy Request Status</th>" + DeployDocs.nl);
            report.append("    <th>Active Request ID</th>" + DeployDocs.nl);
            report.append("    <th>Active Request State</th>" + DeployDocs.nl);
            report.append("    <th>Details</th>" + DeployDocs.nl);
            report.append("    <th>Path Mappings/Properties</th>" + DeployDocs.nl);
            report.append("  </tr>" + DeployDocs.nl);

            // Loop thru all the targets
            for (Iterator<Target> it = targetList.iterator(); it.hasNext();) {
                // Get the Target object
                Target deployTarget = it.next();
                // Write out the new table row
                report.append("  <tr>" + DeployDocs.nl);
                // Get the value for the ID
                report.append("    <td>" + deployTarget.getID() + "</td>" + DeployDocs.nl);
                // Get the Deploy Target Name
                report.append("    <td>" + deployTarget.getName() + "</td>" + DeployDocs.nl);
                // Get the current Connection Status
                report.append("    <td>" + deployTarget.getConnectionStatus() + "</td>" + DeployDocs.nl);
                // Get the Sync Status
                report.append("    <td>" + deployTarget.getSync() + "</td>" + DeployDocs.nl);
                // Get the Deploy Request Status
                report.append("    <td>" + deployTarget.getDeployRequestStatus() + "</td>" + DeployDocs.nl);
                // Get the Active Request ID
                report.append("    <td>" + deployTarget.getActiveRequestID() + "</td>" + DeployDocs.nl);
                // Get the Active Request State
                report.append("    <td>" + deployTarget.getActiveRequestState() + "</td>" + DeployDocs.nl);
                // Get the Details on this Target:  Description, Agent Version, Connection Error Message, Platform, Hostname, Port, User, and Patch Status Message
                report.append("    <td><b>Description: </b>" + deployTarget.getDescription() + "<br/>" + DeployDocs.nl);
                report.append("    <b>Agent Version: </b>" + deployTarget.getAgentVersion() + "<br/>" + DeployDocs.nl);
                if (deployTarget.getConnectionStatus().equalsIgnoreCase("offline")) {
                    report.append("    <b>Connection Error Message: </b>" + deployTarget.getConnectionErrorMessage() + "<br>" + DeployDocs.nl);
                }
                report.append("    <b>Last Deployed Request ID: </b>" + deployTarget.getLastDeployedRequestID() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target Platform: </b>" + deployTarget.getTargetPlatform() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target Hostname: </b>" + deployTarget.getTargetHostname() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target Port: </b>" + deployTarget.getTargetPort() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target User: </b>" + deployTarget.getTargetUser() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target Last Deployed Request ID: </b>" + deployTarget.getTargetLastDeployedRequestID() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target Transfer Request ID: </b>" + deployTarget.getTargetTransferRequestID() + "<br>" + DeployDocs.nl);
                report.append("    <b>Target Active Request ID: </b>" + deployTarget.getTargetActiveRequestID() + "<br>" + DeployDocs.nl);
                if (deployTarget.getTargetPatchStatusMessage().length() > 0) {
                    report.append("    <b>Target Patch Status Message: </b>" + deployTarget.getTargetPatchStatusMessage() + "<br>" + DeployDocs.nl);
                }
                report.append("    </td>" + DeployDocs.nl);
                report.append("    <td>" + DeployDocs.nl);
                report.append("    <b>Path Mappings:</b><br>" + DeployDocs.nl);
                for (Iterator<PathMapping> mapIt = deployTarget.getPathMappingsList().iterator(); mapIt.hasNext();) {
                    PathMapping pm = mapIt.next();
                    report.append("    &nbsp;&nbsp;&nbsp;&nbsp;" + pm.getMemberPathPrefix() + " = " + pm.getTargetPathPrefix() + "<br>" + DeployDocs.nl);
                }
                report.append("    <br>" + DeployDocs.nl);
                report.append("    <b>Properties:</b><br>" + DeployDocs.nl);
                Hashtable<String, String> tgtProps = deployTarget.getProperties();
                Enumeration<String> keys = tgtProps.keys();
                List<String> propList = new ArrayList<String>();
                while (keys.hasMoreElements()) {
                    propList.add(keys.nextElement());
                }
                Collections.sort(propList);
                for (Iterator<String> propIt = propList.iterator(); propIt.hasNext();) {
                    String key = propIt.next();
                    report.append("    &nbsp;&nbsp;&nbsp;&nbsp;" + key + " = " + tgtProps.get(key) + "<br>" + DeployDocs.nl);
                }
                report.append("    </td>" + DeployDocs.nl);
                // Close out the table row
                report.append("  </tr>" + DeployDocs.nl);
            }

            // Close the table tag
            report.append("</table>" + DeployDocs.nl);
        }

        return report.toString();
    }
}
