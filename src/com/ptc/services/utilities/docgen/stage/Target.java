package com.ptc.services.utilities.docgen.stage;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.utils.PathMapping;

public class Target {

    private String activerequestid;
    private String activerequeststate;
    private String agentversion;
    private String connectionerrormessage;
    private String connectionstatus;
    private String deployrequeststatus;
    private String description;
    private String id;
    private String name;
    private String sync;
    private String targethostname;
    private String targetpatchstatusmessage;
    private String targetplatform;
    private String targetport;
    private String targetuser;
    private String lastdeployedrequestid;
    private String targetlastdeployedrequestid;
    private String targettransferrequestid;
    private String targetactiverequestid;
    private List<PathMapping> pathMappingsList;
    private Hashtable<String, String> properties;

    private Integrity i;

    public Target(WorkItem deployTarget) throws APIException {
        // Initialize the class variables
        i = null;
        activerequestid = new String();
        activerequeststate = new String();
        agentversion = new String();
        connectionerrormessage = new String();
        connectionstatus = new String();
        deployrequeststatus = new String();
        description = new String();
        id = new String();
        name = new String();
        sync = new String();
        targethostname = new String();
        targetpatchstatusmessage = new String();
        targetplatform = new String();
        targetport = new String();
        targetuser = new String();
        lastdeployedrequestid = new String();
        targetlastdeployedrequestid = new String();
        targettransferrequestid = new String();
        targetactiverequestid = new String();
        pathMappingsList = new ArrayList<PathMapping>();
        properties = new Hashtable<String, String>();

        // Parse the deploy target work item
        parseDeployTarget(null, null, null, deployTarget);
    }

    public Target(Integrity iToolkit, String stagingSystem, String stage, String target) throws APIException {
        // Initialize the class variables
        i = iToolkit;
        activerequestid = new String();
        activerequeststate = new String();
        agentversion = new String();
        connectionerrormessage = new String();
        connectionstatus = new String();
        deployrequeststatus = new String();
        description = new String();
        id = new String();
        name = new String();
        sync = new String();
        targethostname = new String();
        targetpatchstatusmessage = new String();
        targetplatform = new String();
        targetport = new String();
        targetuser = new String();
        lastdeployedrequestid = new String();
        targetlastdeployedrequestid = new String();
        targettransferrequestid = new String();
        targetactiverequestid = new String();
        pathMappingsList = new ArrayList<PathMapping>();
        properties = new Hashtable<String, String>();

        // Parse the deploy target work item
        parseDeployTarget(stagingSystem, stage, target, null);
    }

    private void parseDeployTarget(String stagingSystem, String stage, String target, WorkItem deployTarget) throws APIException {
        // Run a sd viewdeploytarget command to get information for the Target, if needed
        WorkItem sdDeployTarget;
        if (null == deployTarget) {
            sdDeployTarget = i.viewDeployTarget(stagingSystem, stage, target);
        } else {
            sdDeployTarget = deployTarget;
        }

        // Get the Active Request ID for the Deploy Target
        try {
            Field activeRequestIDFld = sdDeployTarget.getField("activerequestid");
            if (null != activeRequestIDFld && null != activeRequestIDFld.getItem()) {
                activerequestid = activeRequestIDFld.getItem().getId();
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        // Get the Active Request State for the Deploy Target
        Field activeRequestStateFld = sdDeployTarget.getField("activerequeststate");
        if (null != activeRequestStateFld && null != activeRequestStateFld.getValueAsString()) {
            activerequeststate = activeRequestStateFld.getValueAsString();
        }

        // Get the Agent Version for the Deploy Target
        Field agentVersionFld = sdDeployTarget.getField("agentversion");
        if (null != agentVersionFld && null != agentVersionFld.getValueAsString()) {
            agentversion = agentVersionFld.getValueAsString();
        }

        // Get the Connection Error Message for the Deploy Target
        Field connectionErrorMessageFld = sdDeployTarget.getField("connectionerrormessage");
        if (null != connectionErrorMessageFld && null != connectionErrorMessageFld.getValueAsString()) {
            connectionerrormessage = connectionErrorMessageFld.getValueAsString();
        }

        // Get the Connection Status for the Deploy Target 
        Field connectionStatusFld = sdDeployTarget.getField("connectionstatus");
        if (null != connectionStatusFld && null != connectionStatusFld.getValueAsString()) {
            connectionstatus = connectionStatusFld.getValueAsString();
        }

        // Get the Deploy Request Status for the Deploy Target
        Field deployRequestStatusFld = sdDeployTarget.getField("deployrequeststatus");
        if (null != deployRequestStatusFld && null != deployRequestStatusFld.getValueAsString()) {
            deployrequeststatus = deployRequestStatusFld.getValueAsString();
        }

        // Get the Description for the Deploy Target
        Field descriptionFld = sdDeployTarget.getField("description");
        if (null != descriptionFld && null != descriptionFld.getValueAsString()) {
            description = descriptionFld.getValueAsString();
        }

        // Get the ID for the Deploy Target
        Field idFld = sdDeployTarget.getField("id");
        if (null != idFld && null != idFld.getValueAsString()) {
            id = idFld.getValueAsString();
        }

        // Get the Name for the Deploy Target
        Field nameFld = sdDeployTarget.getField("name");
        if (null != nameFld && null != nameFld.getValueAsString()) {
            name = nameFld.getValueAsString();
        }

        // Get the Sync Status for the Deploy Target
        Field syncFld = sdDeployTarget.getField("sync");
        if (null != syncFld && null != syncFld.getValueAsString()) {
            sync = syncFld.getValueAsString();
        }

        // Get the Target Hostname for the Deploy Target
        Field targetHostnameFld = sdDeployTarget.getField("targethostname");
        if (null != targetHostnameFld && null != targetHostnameFld.getValueAsString()) {
            targethostname = targetHostnameFld.getValueAsString();
        }

        // Get the Target Patch Status Message for the Deploy Target
        Field targetPatchStatusMessageFld = sdDeployTarget.getField("targetpatchstatusmessage");
        if (null != targetPatchStatusMessageFld && null != targetPatchStatusMessageFld.getValueAsString()) {
            targetpatchstatusmessage = targetPatchStatusMessageFld.getValueAsString();
        }

        // Get the Target Platform for the Deploy Target
        Field targetPlatformFld = sdDeployTarget.getField("targetplatform");
        if (null != targetPlatformFld && null != targetPlatformFld.getValueAsString()) {
            targetplatform = targetPlatformFld.getValueAsString();
        }

        // Get the Target Port for the Deploy Target
        Field targetPortFld = sdDeployTarget.getField("targetport");
        if (null != targetPortFld && null != targetPortFld.getValueAsString()) {
            targetport = targetPortFld.getValueAsString();
        }

        // Get the Target User for the Deploy Target
        Field targetUserFld = sdDeployTarget.getField("targetuser");
        if (null != targetUserFld && null != targetUserFld.getValueAsString()) {
            targetuser = targetUserFld.getValueAsString();
        }

        // Get the Last Deployed Request ID
        try {
            Field lastDeployedReqIDFld = sdDeployTarget.getField("lastdeployedrequestid");
            if (null != lastDeployedReqIDFld && null != lastDeployedReqIDFld.getItem()) {
                lastdeployedrequestid = lastDeployedReqIDFld.getItem().getId();
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        // Get the Target Last Deployed Request ID
        try {
            Field targetlastdeployedReqIDFld = sdDeployTarget.getField("targetlastdeployedrequestid");
            if (null != targetlastdeployedReqIDFld && null != targetlastdeployedReqIDFld.getItem()) {
                targetlastdeployedrequestid = targetlastdeployedReqIDFld.getItem().getId();
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        // Get the Target Transfer Request ID
        try {
            Field targettransferReqIDFld = sdDeployTarget.getField("targettransferrequestid");
            if (null != targettransferReqIDFld && null != targettransferReqIDFld.getItem()) {
                targettransferrequestid = targettransferReqIDFld.getItem().getId();
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        // Get the Target Active Request ID
        try {
            Field targetactiveReqIDFld = sdDeployTarget.getField("targetactiverequestid");
            if (null != targetactiveReqIDFld && null != targetactiveReqIDFld.getItem()) {
                targetactiverequestid = targetactiveReqIDFld.getItem().getId();
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        // Get the Path Mappings
        try {
            Field pathMappingsFld = sdDeployTarget.getField("PathMappingsSubselection");
            if (null != pathMappingsFld && null != pathMappingsFld.getList()) {
                @SuppressWarnings("unchecked")
                List<Item> pathMapList = pathMappingsFld.getList();
                for (Iterator<Item> it = pathMapList.iterator(); it.hasNext();) {
                    PathMapping pm = new PathMapping(it.next());
                    pathMappingsList.add(pm);
                }
            }
        } catch (NoSuchElementException nsee) {
            // Ignore...
        }

        // Get the Target Properties
        try {
            // Get the properties for this Deploy Target		
            Field propertiesFld = sdDeployTarget.getField("PropertiesSubselection");
            if (null != propertiesFld && null != propertiesFld.getList()) {
                @SuppressWarnings("unchecked")
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

    // Return the information parsed	
    public String getActiveRequestID() {
        return activerequestid;
    }

    public String getActiveRequestState() {
        return activerequeststate;
    }

    public String getAgentVersion() {
        return agentversion;
    }

    public String getConnectionErrorMessage() {
        return connectionerrormessage;
    }

    public String getConnectionStatus() {
        return connectionstatus;
    }

    public String getDeployRequestStatus() {
        return deployrequeststatus;
    }

    public String getDescription() {
        return description;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSync() {
        return sync;
    }

    public String getTargetHostname() {
        return targethostname;
    }

    public String getTargetPatchStatusMessage() {
        return targetpatchstatusmessage;
    }

    public String getTargetPlatform() {
        return targetplatform;
    }

    public String getTargetPort() {
        return targetport;
    }

    public String getTargetUser() {
        return targetuser;
    }

    public String getLastDeployedRequestID() {
        return lastdeployedrequestid;
    }

    public String getTargetLastDeployedRequestID() {
        return targetlastdeployedrequestid;
    }

    public String getTargetTransferRequestID() {
        return targettransferrequestid;
    }

    public String getTargetActiveRequestID() {
        return targetactiverequestid;
    }

    public List<PathMapping> getPathMappingsList() {
        return pathMappingsList;
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }
}
