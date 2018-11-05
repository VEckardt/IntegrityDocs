/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen;

// Java Imports
import com.ptc.services.utilities.docgen.field.SimpleField;
import com.mks.api.Command;
import com.mks.api.FileOption;
import com.mks.api.MultiValue;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemList;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.utilities.CmdException;
import static com.ptc.services.utilities.docgen.ChartFactory.parseChart;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_IMAGES_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Copyright.copyright;
import static com.ptc.services.utilities.docgen.Images.getImages;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.doExport;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import static com.ptc.services.utilities.docgen.IntegrityDocs.iObjectList;
import static com.ptc.services.utilities.docgen.IntegrityDocs.solutionTypeName;
import static com.ptc.services.utilities.docgen.Metrics.getMetrics;
import com.ptc.services.utilities.docgen.relationships.IntegrityException;
import com.ptc.services.utilities.docgen.type.TypeProperties;
import com.ptc.services.utilities.docgen.utils.Html;
import static com.ptc.services.utilities.docgen.utils.ImageUtils.extractImage;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import static com.ptc.services.utilities.docgen.utils.Logger.print;
import com.ptc.services.utilities.docgen.utils.OSCommandHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;

public class Integrity extends IntegrityUtils {

    public static final String[] typeAttributes = new String[]{"addLabel", "allowChangePackages", "associatedType", "backsProject", "branch",
        "branchEnabled", "canRelateToTestResult", "copyFields", "copyTree", "copytreeEnabled",
        "createCPPolicy", "defaultReferenceMode", "deleteItem", "deleteItemEnabled", "deleteLabel",
        "description", "documentClass", "duplicateDetectionMandatory", "duplicateDetectionSearchField",
        "editPresentation", "fieldRelationships", "groupDocument", "id", "image", "isTestResult",
        "issueEditability", "labelEnabled",
        // R12: "majorRevision", 
        "mandatoryFields",
        // R12: "minorRevision",
        "modifyTestResultPolicy", "name", "notificationFields", "permittedAdministrators", "permittedGroups",
        "phaseField", "position", "printPresentation", "properties", /*"references",*/ "revisionEnabled",
        "showWorkflow", "significantEdit", "stateTransitions", "testCaseResultFields", "testRole",
        "testStepsEnabled", "timeTrackingEnabled", "typeClass", "viewPresentation", "visibleFields", "visibleFieldsForMe"};
    public static final String[] fieldAttributes = new String[]{"id", "name", "displayName",
        "displayPattern", "description", "type", "default", "position", "allowedTypes", "associatedField",
        "backedBy", "backingFilter", "backingStates",
        "backingTextField", "backingTextFormat", "backingType", "computation", "correlation",
        "cycleDetection", "defaultAttachmentField", "defaultBrowseQuery", "defaultColumns",
        "displayAsLink", "displayAsProgress", "displayLocation", "displayRows", "displayStyle", "editabilityRule", "isForward",
        "isMultiValued", "isSystemManaged", "isTestResult", "lastcompute", "linkFlags", "loggingText",
        "max", "min", "maxLength", "pairedField", "paramSubstitution", "phases", "picks",
        "query", "ranges", /*"references",*/ "relevanceRule", "richContent", "showDateTime",
        "staticComputation", "storeToHistoryFrequency", "suggestions", "textindex", "trace"};
    public static final String[] stateAttributes = new String[]{"capabilities", "description", "id", "name", "position", "displayName"/*,references*/};
    public static final String[] queryAttributes = new String[]{"createdBy", "description", "fields", "id", "isAdmin", "lastModified", "name",
        "queryDefinition", /*"references",*/ "shareWith", "sortDirection", "sortField"};
    public static final String[] groupAttributes = new String[]{"description", "email", "id", "image", "isActive", "isInRealm", "name", "notificationRule", "queryTimeout", /* "references", */ "sessionLimit"};
    public static final String[] dynGroupAttributes = new String[]{"description", "id", "name"};
    public static final String[] reportAttributes = new String[]{"description", "id", "name", "query", "shareWith", "sharedGroups"};
    public static final String[] chartAttributes = new String[]{"id", "name", "description", "isAdmin", "chartType", "createdBy", "graphStyle", /*"lastModified",*/
        "query", /*"references",*/ "shareWith"};
    public static final String[] chartAttributes2 = new String[]{"id", "Name", "Description", "Is Admin", "Chart Type", "Created By", "Graph Style", /*"lastModified",*/
        "Query", /*"references",*/ "Share With"};    // testAttributes
    public static final String[] testVerdictAttributes = new String[]{"description", "displayName", "id", "isActive", "name", "position", "verdictType"};
    // resultFieldsAttributes
    public static final String[] resultFieldsAttributes = new String[]{"description", "displayName", "id", "name", "position", "type"};

    public static final String[] cpTypesAttributes = {"id", "name", "description", "displayName", "attributes", "entryAttributes", "entryKey", "permittedAdministrators", "permittedGroups", "position"};
    public static final String[] projectAttributes = {"id", "name", "description", "isActive", "backingIssueID", "closedImage", "parent", "permittedAdministrators", "permittedGroups"};
    // public static final String[] projectAttributes = {"description", "id", "isActive", "name"};

    /**
     * Override connection to specific Integrity Application
     *
     * @param app
     * @throws APIException
     */
    public Integrity(String app) throws APIException {
        super(app);
    }

    /**
     * Read Type Fields
     *
     * @param typeName
     * @param fieldList
     * @param targetMap
     * @throws APIException
     */
    public void readTypeFields(String typeName, List<String> fieldList, Map<String, WorkItem> targetMap) throws APIException {
        // targetMap.clear();
        Command cmd = new Command(Command.IM, "viewfield");
        cmd.addOption(new Option("overrideforType", typeName));
        for (String fieldName : fieldList) {
            cmd.addSelection(fieldName);
        }
        // System.out.println("Reading field '" + fieldName + "' for type '" +typeNameList+ "' ..");
        Response respo = Integrity.execute(cmd);
        // ResponseUtil.printResponse(respo, 1, System.out);
        WorkItemIterator li = respo.getWorkItems();
        while (li.hasNext()) {
            WorkItem it = li.next();
            targetMap.put(it.getId(), it);
        }
    }

//    public void readAllDynamicGroups() throws APIException {
//        allDynamicGroups.clear();
//        Command cmd;
//        cmd = new Command(Command.IM, "dynamicgroups");
//        cmd.addOption(new Option("fields", "description,id,image,name"));
//
//        Response respo = api.runCommand(cmd);
//        // ResponseUtil.printResponse(respo, 1, System.out);
//        WorkItemIterator wit = respo.getWorkItems();
//        while (wit.hasNext()) {
//            WorkItem dynGroup = wit.next();
//            allDynamicGroups.put(dynGroup.getId(), new DynamicGroup(dynGroup));
//        }
//    }
//    public void readDynamicGroups(String dynGroupName, String showReferences) throws APIException {
//        allDynamicGroups.clear();
//        Command cmd;
//        if (dynGroupName.isEmpty() || dynGroupName.equals("All")) {
//            cmd = new Command(Command.IM, "dynamicgroups");
//            if (showReferences.equals("Yes")) {
//                cmd.addOption(new Option("fields", "description,id,image,membership,name,references"));
//            } else {
//                cmd.addOption(new Option("fields", "description,id,image,membership,name"));
//            }
//
//        } else {
//            cmd = new Command(Command.IM, "viewdynamicgroup");
//            if (showReferences.equals("Yes")) {
//                cmd.addOption(new Option("showReferences"));
//            }
//            cmd.addSelection(dynGroupName);
//        }
//
//        Response respo = api.runCommand(cmd);
//        // ResponseUtil.printResponse(respo, 1, System.out);
//        WorkItemIterator wit = respo.getWorkItems();
//        while (wit.hasNext()) {
//            WorkItem dynGroup = wit.next();
//            allDynamicGroups.put(dynGroup.getId(), new DynamicGroup(dynGroup));
//        }
//        log("INFO: Rows in allDynamicGroups: " + Integrity.allDynamicGroups.size());
//    }
    public static String getStateFieldPermission() {

        String row = "";
        row += getStateFieldPermission("segment") + "<br>" + getStateFieldPermission("node");

        if (!solutionTypeName.isEmpty()) {
            List<IntegrityObject> typeList = getList(Types.Type);
            String row2 = "<table>";
            row2 += "<tr><th>Property</th><th>Field</th><th>Description</th></tr>";
            for (IntegrityAdminObject ao : typeList) {
                if (ao.getName().equals(solutionTypeName)) {
                    row2 += "<br><br><h3>Legend:</h3>";
                    IntegrityType solutionType = (IntegrityType) ao;
                    TypeProperties tp = new TypeProperties(solutionType.getTypeFields().get("properties"));
                    List<SimpleField> props = tp.getPropList();
                    for (SimpleField prop : props) {
                        if (prop.getName().startsWith("MKS.RQ.Editability")) {
                            row2 += ("<tr><td>" + prop.getName() + "</td><td>" + prop.getValueAsString() + "</td><td>" + prop.getDescription() + "</td><tr>");
                        }
                    }

                }
            }
            row2 += "<table>";
            row = row + row2;
        }
        return row;
    }

    public static String getStateFieldPermission(String docClass) {
        StringBuilder sb = new StringBuilder();
        int height = "System Requirement Document".length() * 7;
        TreeSet<String> involvedFields = new TreeSet<>();

        List<IntegrityObject> typeList = getList(Types.Type);
        for (IntegrityObject ao : typeList) {
            IntegrityType type = (IntegrityType) ao;
            if (type.getDocClass().equals(docClass)) {
                TypeProperties tp = new TypeProperties(type.getTypeFields().get("properties"));
                List<SimpleField> props = tp.getPropList();
                for (SimpleField prop : props) {
                    if (prop.getName().startsWith("MKS.RQ.Editability")) {
                        for (String fieldName : prop.getValueAsString().split(",")) {
                            involvedFields.add(fieldName);
                        }
                    }
                }
            }
        }

        String row = "";
        row += ("<tr>");
        row += ("<th class='heading1'>Type</th>");
        row += ("<th class='heading1'>Property</th>");
        for (String th : involvedFields) {
            row += (("<th class=\"heading1 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + th + "</span></th>"));
        }
        row += ("<th class='heading1'>Description</th>");
        row += ("</tr>");

        for (IntegrityAdminObject ao : typeList) {
            IntegrityType type = (IntegrityType) ao;
            if (type.getDocClass().equals(docClass)) {
                TypeProperties tp = new TypeProperties(type.getTypeFields().get("properties"));
                List<SimpleField> props = tp.getPropList();
                String currType = "";
                for (SimpleField prop : props) {
                    if (prop.getName().startsWith("MKS.RQ.Editability")) {

                        row += "<tr>";
                        row += Html.td(currType.equals(type.getName()) ? "" : type.getName());
                        row += Html.td(prop.getName().replace("MKS.RQ.", ""));
                        for (String th : involvedFields) {
                            if (prop.getValueAsString().contains(th)) {
                                row += Html.td("<div style='text-align: center'>&#10003;</div>");
                            } else {
                                row += Html.td("&nbsp;");
                            }
                        }
                        currType = type.getName();
                        row += Html.td(prop.getDescription());
                        row += "</tr>";
                    }
                }
            }
        }

        // }
        sb.append("<h3>List of Field Permissions for " + (docClass.equals("segment") ? "Documents" : "Nodes") + ":</h3>");
        sb.append("<table class='display'>" + row + "</table>");

        return sb.toString();
    }

    /**
     * Get Triggers And Types
     *
     * @return
     */
    public static String getTriggersAndTypes() {
        try {
            StringBuilder sb = new StringBuilder();

            Map<String, List<Trigger>> triggerMap = new TreeMap<>();

            Command cmd = new Command(Command.IM, "triggers");
            cmd.addOption(new Option("fields", "assign,description,frequency,lastRunTime,name,position,query,rule,runAs,script,scriptParams,scriptTiming,type"));
            // cmd.addSelection(objectName);
            Response response = execute(cmd);
            // log(cmd.getApp() + " " + cmd.getCommandName() + " exit code: " + response.getExitCode());

            WorkItemIterator wit = response.getWorkItems();
            while (wit.hasNext()) {
                WorkItem wi = wit.next();

                Trigger td = new Trigger(wi);

                // summary: writeTriggerList(td, row++);
                for (String type : td.typeNameList) {
                    if (triggerMap.containsKey(type)) {
                        triggerMap.get(type).add(td);
                    } else {
                        List<Trigger> triggers = new ArrayList<>();
                        triggers.add(td);
                        triggerMap.put(type, triggers);
                    }
                }
                //
            }
            for (String typename : triggerMap.keySet()) {
                Collections.sort(triggerMap.get(typename), new Comparator<Trigger>() {
                    @Override
                    public int compare(Trigger tdef1, Trigger tdef2) {
                        return tdef1.compName().compareTo(tdef2.compName());
                    }
                });
            }
            sb.append("<hr><table class='display'>");

            int col = 0;
            for (String typename : triggerMap.keySet()) {
                col = col + 2;
                sb.append("<tr>");
                // writeXLSXData(1, 5, col, typename, true);
                sb.append("<th class=heading1>" + typename + "</th>");
                for (Trigger td : triggerMap.get(typename)) {
                    // log(td.position + ": " + td.name + ", " + td.rule + ", " + td.type + ", " + td.ruleType + " => " + td.typeNameList);
                    // writeXLSXData(1, 3 + row * 2, col, td.position + ": " + td.name + "\n" + td.getFlags(), td.isActive);
                    sb.append("<td>" + td.position + ": " + td.name + "\n" + td.getFlags() + "</td>");
                }
                sb.append("</tr>");
            }
            sb.append("</table>");

            // writeXLSXData(1, 3, 2, session.getServerInfo(), true);
            // writeXLSXData(2, 3, 2, session.getServerInfo(), true);
            // writeXLSXFile();
            return sb.toString();
        } catch (APIException ex) {
            Logger.getLogger(Integrity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    /**
     * Retrieves the object types from Integrity
     *
     * @param type
     * @param doXML
     * @param validTypeList
     * @throws APIException
     * @throws IntegrityException
     * @throws CmdException
     * @throws ParserConfigurationException
     */
    public void retrieveObjects(IntegrityDocs.Types type, Boolean doXML, List<String> validTypeList) throws APIException, IntegrityException, CmdException, ParserConfigurationException {
        if (doExport[type.getID()] && ((doXML && type.doExportXML()) || !doXML)) {
            List<IntegrityObject> iO = iObjectList.get(type.getID());

            if (type.equals(Types.Chart)) {
                List<Chart> list = getCharts2();
                for (Chart listEntry : list) {
                    iO.add(listEntry);
                }
            } else if (type.equals(Types.Type)) {
                // handle a limited type list
                Command cmd = new Command(Command.IM, "types");
                addFieldList(cmd, typeAttributes);
                Response resp = execute(cmd);
                WorkItemIterator wit = resp.getWorkItems();
                while (wit.hasNext()) {
                    WorkItem wi = wit.next();
                    if (validTypeList.contains(wi.getId())) {
                        iO.add(new IntegrityType(wi, doXML));
                    }
                }
            } else {
                // handle all other types
                WorkItemIterator objects = getObjects(type, doXML);
                if (objects != null) {
                    while (objects.hasNext()) {
                        WorkItem object = objects.next();
                        if (type.equals(Types.MKSDomainGroup)) {
                            iO.add(new DomainGroup(object));
                        } else if (type.equals(Types.DynamicGroup)) {
                            iO.add(new DynamicGroup(object));
                        } else if (type.equals(Types.Group)) {
                            iO.add(new StaticGroup(object, Types.Group));
                        } else if (type.equals(Types.ACL)) {
                            iO.add(new ACL(object));
                        } else if (type.equals(Types.State)) {
                            iO.add(new IntegrityState(object, ""));
                        } else if (type.equals(Types.Query)) {
                            iO.add(new IntegrityQuery(object));
                        } else if (type.equals(Types.Viewset)) {
                            iO.add(new Viewset(object, object.getId()));
                        } else if (type.equals(Types.SIProject)) {
                            iO.add(new SIProject(object));
                        } else if (type.equals(Types.IMProject)) {
                            iO.add(new IMProject(object));
                        } else if (type.equals(Types.Trigger)) {
                            iO.add(new Trigger(object));
                        } else if (type.equals(Types.Chart)) {
                            iO.add(new Chart(object));
                        } else if (type.equals(Types.Dashboard)) {
                            iO.add(new Dashboard(object));
                        } else if (type.equals(Types.Report)) {
                            iO.add(new Report(object, doXML));
                        } else if (type.equals(Types.CPType)) {
                            iO.add(new CPType(object));
                        } else if (type.equals(Types.Field)) {
                            iO.add(new IntegrityField(object));
                        } else if (type.equals(Types.Verdict)) {
                            iO.add(new Verdict(object));
                        } else if (type.equals(Types.ResultField)) {
                            iO.add(new ResultField(object));
                        } else {
                            // throw new UnsupportedOperationException("Missing assignment in Integrity.retrieveObjects for " + type.name());
                            iO.add(new IntegrityObject(object, type));
                        }
                    }
                }
                log("INFO: Retrieved " + iO.size() + " objects of type '" + type.name() + "'.");
            }
        } else {
            log("INFO: Skipping objects of type '" + type.name() + "'.");
        }
    }

    /**
     * Returns a string list of the specified admin object
     *
     * @param obj
     * @param ignoreList
     * @param stopElement
     * @return
     * @throws APIException
     */
    public List<String> getAdminList(String obj, String ignoreList, String stopElement) throws APIException {
        List<String> adminList = new ArrayList<>();
        Command imAdminList = new Command(Command.IM, obj);
        Response res = execute(imAdminList);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                WorkItem wi = wit.next();
                if (!("," + ignoreList + ",").contains("," + wi.getId() + ",")) {
                    adminList.add(wi.getId());
                }
                if (wi.getId().equals(stopElement)) {
                    return adminList;
                }
            }
        }
        return adminList;
    }

    /**
     * Returns the name and id for the specified admin object
     *
     * @param obj
     * @return
     * @throws APIException
     */
    public LinkedHashMap<String, String> getAdminIDList(String obj) throws APIException {
        LinkedHashMap<String, String> adminIDList = new LinkedHashMap<>();
        Command imAdminList = new Command(Command.IM, obj);
        imAdminList.addOption(new Option("fields", "id,name"));
        Response res = execute(imAdminList);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                WorkItem wi = wit.next();
                adminIDList.put(getStringFieldValue(wi.getField("id")), wi.getId());
            }
        }
        return adminIDList;
    }

    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Field> getTypeFields(WorkItem wi) throws APIException {
        LinkedHashMap<String, Field> typeFields = new LinkedHashMap<>();

        String typeName = wi.getId();

//      Command imTypes = new Command(Command.IM, "types");
//      // Construct the --fields=value,value,value option
//      addFieldList(imTypes, typeAttributes);
//      imTypes.addSelection(typeName);
//
//      Response res = execute(imTypes);
//      if (null != res && null != res.getWorkItem(typeName)) {
//         wi = res.getWorkItem(typeName);
        for (Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
            Field field = fit.next();
            print("\t... " + field.getName());
            typeFields.put(field.getName(), field);
            log(" done.");
        }

        // Run im view type to get the missing type attributes
        Command imViewType = new Command(Command.IM, "viewtype");
        imViewType.addSelection(typeName);
        Response viewRes = execute(imViewType);
        if (null != viewRes && null != viewRes.getWorkItem(typeName)) {
            WorkItem wi2 = viewRes.getWorkItem(typeName);
            // Only fetch the missing fields
            //	... created, createdBy, lastModified, modifiedBy
            addCreMoDetailsIfRequested(typeFields, wi2);

            Field image = wi2.getField("image");
            if (image.getItem().getId().contentEquals("custom")) {
                String fileName = CONTENT_DIR + "/Types/" + wi2.getId().replaceAll(" ", "_") + ".png";
                File imageFile = new File(fileName);
                extractImage(image, imageFile);
                typeFields.put("smallImage", new SimpleField("smallImage", fileName));
            } else {
                try {
                    typeFields.put("smallImage", new SimpleField("smallImage", CONTENT_DIR + fs + wi2.getField("type").getString()));
                } catch (NoSuchElementException ex) {
                    typeFields.put("smallImage", new SimpleField("smallImage", CONTENT_IMAGES_DIR + fs + "Type.png"));
                }
            }
        }
//      }
        return typeFields;
    }

//    public WorkItemIterator viewTriggers(List<String> triggerList) throws APIException {
//        Command imViewTrigger = new Command(Command.IM, "viewtrigger");
//        // Add each trigger selection to the view trigger command
//        for (Iterator<String> it = triggerList.iterator(); it.hasNext();) {
//            imViewTrigger.addSelection(it.next());
//        }
//        return api.runCommandWithInterim(imViewTrigger).getWorkItems();
//    }
    public WorkItemIterator getIMMainProjects() throws APIException {
        // CmdExecutor shell = new CmdExecutor();
        // ArrayList<String> lines = new ArrayList<>();

        Command cmd = new Command(Command.IM, "diag");
        cmd.addOption(new Option("diag", "runsql"));
        cmd.addOption(new Option("param", "select Name from Projects where ParentID is null"));
        Response response = execute(cmd);
        String message = response.getResult().getMessage();
        // ResponseUtil.printResponse(response, 1, System.out);

        cmd = new Command(Command.IM, "projects");
        addFieldList(cmd, projectAttributes);
        for (String line : message.split("\n")) {
            // out.println("line: " + line);
            line = line.trim();
            // ignore 
            if (!line.toUpperCase().equals("NAME") && !line.startsWith("-----")) {
                // lines.add(line);
                if (!line.isEmpty()) {
                    // add th eprojects you want to list
                    cmd.addSelection("/" + line.trim());
                }
            }
        }
        if (cmd.getSelectionList().size() > 0) {
            return execute(cmd).getWorkItems();
        }
        return new SimpleWorkItemList();
    }

    public LinkedHashMap<String, Field> viewField(String typeName, String fieldName) throws APIException {
        LinkedHashMap<String, Field> fieldDetails = new LinkedHashMap<>();
        Command imViewField = new Command(Command.IM, "viewfield");
        if (typeName != null) {
            imViewField.addOption(new Option("overrideForType", typeName));
        }
        imViewField.addSelection(fieldName);
        Response res = execute(imViewField);
        if (null != res && null != res.getWorkItem(fieldName)) {
            WorkItem wi = res.getWorkItem(fieldName);
            for (@SuppressWarnings("unchecked") Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
                Field field = fit.next();
                fieldDetails.put(field.getName(), field);
            }
        }
        return fieldDetails;
    }

    public static LinkedHashMap<String, IntegrityState> getStates(String typeName, List<String> statesList) throws APIException {
        LinkedHashMap<String, IntegrityState> stateDetails = new LinkedHashMap<>();
        Command imStates = new Command(Command.IM, "states");
        // Construct the --fields=value,value,value option
        addFieldList(imStates, stateAttributes);

        if (statesList.size() > 0) {
            // Add the list of selections
            for (Iterator<String> it = statesList.iterator(); it.hasNext();) {
                imStates.addSelection(it.next());
            }

            // Run the im states command to get the global details on the state
            Response res = execute(imStates);
            // Parse the response for the initial pass
            if (null != res && null != res.getWorkItems()) {
                WorkItemIterator wii = res.getWorkItems();
                while (wii.hasNext()) {
                    WorkItem wi = wii.next();
                    String stateName = wi.getId();
                    log("\t\tAnalyzing state: " + stateName, 1);
                    IntegrityState iState = new IntegrityState(wi, typeName);
                    stateDetails.put(stateName, iState);
                }
            }

            // Now run the im viewstate to get the overrides...
            Command imViewState = new Command(Command.IM, "viewstate");
            imViewState.addOption(new Option("overrideForType", typeName));
            imViewState.setSelectionList(imStates.getSelectionList());
            // Run the command and parse the response
            Response viewStateRes = execute(imViewState);
            for (String stateName : statesList) {
                // Get the Work Item representing the current State				
                WorkItem wi = viewStateRes.getWorkItem(stateName);
                // Get the encapsulated IntegrityField object for this State
                IntegrityState iState = stateDetails.get(stateName);
                // Reset any state's attribute that may be overridden for this type
                Item overrideForType = wi.getField("overrideForType").getItem();
                if (null != overrideForType && null != overrideForType.getField("overriddenFields")) {
                    // Get the list of overridden attributes for the current visible field
                    @SuppressWarnings("unchecked")
                    List<String> overrideFieldList = overrideForType.getField("overriddenFields").getList();
                    // For each overridden attribute update the state details hash
                    for (String orStateAttribute : overrideFieldList) {
                        iState.setFieldAttribute(orStateAttribute, wi.getField(orStateAttribute));
                    }
                }
                stateDetails.put(stateName, iState);
            }
        }

        return stateDetails;
    }

    public static LinkedHashMap<String, IntegrityField> getFields(String typeName, Field visibleFields, Field visibleFieldsForMe) throws APIException {
        // Initialize our return variable
        LinkedHashMap<String, IntegrityField> fieldDetails = new LinkedHashMap<>();

        // Get a unique list of all fields that we need to interrogate later...
        List<String> selectionList = new ArrayList<>();

        // Populate the visible fields hash with just the 'Visible To' values for quick access		
        LinkedHashMap<String, Field> visibleFieldsHash = new LinkedHashMap<>();
        @SuppressWarnings("unchecked")
        List<Item> partialVisibleFieldsList = visibleFields.getList();
        if (null != partialVisibleFieldsList) {
            for (Item fieldItem : partialVisibleFieldsList) {
                visibleFieldsHash.put(fieldItem.getId(), fieldItem.getField("groups"));
                selectionList.add(fieldItem.getId());
            }
        }

        // Setup the im fields command to get the global definition of the field
        Command imFields = new Command(Command.IM, "fields");
        // Construct the --fields=value,value,value option
        addFieldList(imFields, fieldAttributes);

        @SuppressWarnings("unchecked")
        List<Item> visibleFieldsList = visibleFieldsForMe.getList();
        // Ensure we have visible fields defined...
        if (null != visibleFieldsList) {
            // Loop thru all the visible fields "for me" to get some of the system fields 
            // that were missing from the visible fields list.  Example ID, Type, Created Date, etc.
            for (Item fld : visibleFieldsList) {
                if (!selectionList.contains(fld.getId())) {
                    selectionList.add(fld.getId());
                }
            }

            // Add the unique list of visible fields to the selection for the "im fields" command
            for (String fld : selectionList) {
                imFields.addSelection(fld);
            }

            // Run the im fields command to get the global details on the field
            Response res = execute(imFields);
            // Parse the response for the initial pass
            if (null != res && null != res.getWorkItems()) {
                WorkItemIterator wii = res.getWorkItems();
                while (wii.hasNext()) {
                    WorkItem wi = wii.next();
                    String fieldName = wi.getId();
                    log("\t\tAnalyzing visible field: " + fieldName, 1);
                    IntegrityField iField = new IntegrityField(typeName, wi);
                    fieldDetails.put(fieldName, iField);
                }
            }

            // Now run the im viewfield to get the overrides...
            Command imViewField = new Command(Command.IM, "viewfield");
            imViewField.addOption(new Option("overrideForType", typeName));
            imViewField.setSelectionList(imFields.getSelectionList());
            // Run the command and parse the response
            Response viewFieldRes = execute(imViewField);
            for (Item visibleField : visibleFieldsList) {
                String fieldName = visibleField.getId();
                // Get the Work Item representing the current visible field				
                WorkItem wi = viewFieldRes.getWorkItem(fieldName);
                // Get the encapsulated IntegrityField object for this visible field
                IntegrityField iField = fieldDetails.get(fieldName);
                // Get the list of "Visible To" group values
                List<String> groups = new ArrayList<>();
                Field visibleGroups = visibleFieldsHash.get(fieldName);
                if (null != visibleGroups) {
                    @SuppressWarnings("unchecked")
                    List<Item> visibleGroupsList = visibleGroups.getList();
                    for (Iterator<Item> glit = visibleGroupsList.iterator(); glit.hasNext();) {
                        groups.add(glit.next().getId());
                    }
                    // Set the visible groups on the iField
                    iField.setVisibleGroups(groups);
                } else {
                    groups.add("everyone");
                    iField.setVisibleGroups(groups);
                }

                // Reset any field's attribute that may be overridden for this type
                Item overrideForType = wi.getField("overrideForType").getItem();
                if (null != overrideForType && null != overrideForType.getField("overriddenFields")) {
                    // Get the list of overridden attributes for the current visible field
                    @SuppressWarnings("unchecked")
                    List<String> overrideFieldList = overrideForType.getField("overriddenFields").getList();
                    // For each overridden attribute update the field details hash
                    for (String orFieldAttribute : overrideFieldList) {
                        try {
                            iField.setFieldAttribute(orFieldAttribute, wi.getField(orFieldAttribute));
                        } catch (java.util.NoSuchElementException ex) {
                            // just go on
                        }
                    }
                }
                fieldDetails.put(fieldName, iField);
            }
        }

        return fieldDetails;
    }

    /**
     * Get ACLs
     *
     * @return
     * @throws APIException
     */
    public static SimpleWorkItemList getACLs() throws APIException {
        SimpleWorkItemList ims = new SimpleWorkItemList();
        int cnt = 0;
//        List<IntegrityAdminObject> aclList = getList(Types.ACL);
//        for (IntegrityAdminObject acl : aclList) {
//            addACLs(ims, acl, ++cnt);
//        }
        Command cmd = new Command(Command.AA, "viewacl");
        cmd.addOption(new Option("acl", "mks:im"));
        Response response = execute(cmd);
        for (WorkItemIterator wii = response.getWorkItems(); wii.hasNext();) {
            WorkItem wi = wii.next();
            ItemList itemList = (ItemList) wi.getField("entries").getList();
            for (Object object : itemList) {
                Item item = (Item) object;
                addACLs(ims, item, ++cnt);
            }
        }
        return ims;
    }

    private static void addACLs(SimpleWorkItemList ims, Item item, int cnt) {
        SimpleWorkItem itm = new SimpleWorkItem(Types.ACL.name(), String.valueOf(cnt));
        itm.add("id", String.valueOf(cnt));
        itm.add("permission", item.getField("permission").getItem().getDisplayId());
        itm.add("pricipalType", item.getField("principal").getItem().getModelType().replace("aa.Acl", ""));
        itm.add("pricipalName", item.getField("principal").getItem().getDisplayId());
        itm.add("permitted", item.getField("permitted").getBoolean().toString());

        ims.add(itm);
    }

    /**
     * Get Type Permissions
     *
     * @return
     */
    public static SimpleWorkItemList getTypePermissions() {
        SimpleWorkItemList ims = new SimpleWorkItemList();
        int cnt = 0;
        List<IntegrityObject> types = getList(Types.Type);
        List<IntegrityObject> groups = getList(Types.Group);
        for (IntegrityObject type : types) {
            for (IntegrityObject group : groups) {
//                Boolean canCreateItemsOfThisType = type.canCreateItemsOfThisType(group.getID());
//                Boolean hasGroupPermission = type.canViewItemsOfThisType(group.getID());
                if (type.canViewItemsOfThisType(group.getName())) {
                    addPermission(ims, "canViewItemsOfThisType", group, type, ++cnt);
                }
                if (type.canCreateItemsOfThisType(group.getName())) {
                    addPermission(ims, "canCreateItemsOfThisType", group, type, ++cnt);
                }
            }
        }
        return ims;
    }

    private static void addPermission(SimpleWorkItemList ims, String permission, IntegrityAdminObject group, IntegrityAdminObject type, int cnt) {
        SimpleWorkItem itm = new SimpleWorkItem(Types.TypePermission.name(), String.valueOf(cnt));
        itm.add("id", String.valueOf(cnt));
        itm.add("type", permission);
        itm.add("Permission", permission);
        itm.add("Group", group.getName());
        itm.add("Item Type", type.getName());
        ims.add(itm);
    }

    /**
     * Read the object list and if requested, also the individual objects
     *
     * @param type
     * @param doXML
     * @return
     * @throws APIException
     * @throws
     * com.ptc.services.utilities.docgen.relationships.IntegrityException
     */
    public WorkItemIterator getObjects(Types type, Boolean doXML) throws APIException, IntegrityException {
        log("Reading " + type.getPlural() + " ...", 1);
        if (type.equals(Types.IMProject)) {
            return getIMMainProjects();
        }
        if (type.equals(Types.GatewayExportConfig)) {
            return new GatewayConfigs("export", "exporter", CONTENT_DIR + fs + "GatewayExportConfigs");
        }
        if (type.equals(Types.GatewayImportConfig)) {
            return new GatewayConfigs("parser", "parser", CONTENT_DIR + fs + "GatewayImportConfigs");
        }
        if (type.equals(Types.GatewayMapping)) {
            return new GatewayMappings(CONTENT_DIR + fs + "GatewayMappings");
        }
        if (type.equals(Types.Image)) {
            return getImages(CONTENT_IMAGES_DIR.getAbsolutePath());
        }
        if (type.equals(Types.Metric)) {
            return getMetrics(this, Types.Metric, CONTENT_DIR);
        }
        if (type.equals(Types.GatewayTemplate)) {
            return new GatewayTemplates(CONTENT_DIR + fs + "GatewayExportConfigs", CONTENT_DIR + fs + "GatewayImportConfigs");
        }
        if (type.equals(Types.TraceDefault) && !IntegrityDocs.solutionTypeName.isEmpty()) {
            RelationshipAnalyser ra = new RelationshipAnalyser(this, IntegrityDocs.solutionTypeName);
            return ra.analyseTraces(IntegrityDocs.getList(Types.Field));
        }
//        if (type.equals(Types.DynamicGroupUsage)) {
//            SimpleWorkItemList ar = new SimpleWorkItemList();
//            ar.add(new SimpleWorkItem(Types.DynamicGroupUsage.name(), Types.DynamicGroupUsage.name()));
//            return ar;
//        }
        if (type.equals(Types.TypePermission)) {
            return getTypePermissions();
        }
        if (type.equals(Types.ACL)) {
            return getACLs();
        }
        Command command = new Command(type.getCmd(), type.getPlural().toLowerCase());

        if (type.equals(Types.SIProject)) {
            command.addOption(new Option("nodisplaySubs"));
            command.setCommandName("projects");
        }
        if (type.equals(Types.Type)) {
            addFieldList(command, typeAttributes);
        }

        // run first command
        WorkItemIterator wit = execute(command).getWorkItems();

        // stop here for ViewSet and SI Projects or Type
        if (type.equals(Types.Viewset) || type.equals(Types.SIProject) || type.equals(Types.Type)) {
            return wit;
        }

        if (type.equals(Types.ResultField)) {
            // in this case we read the standard field definition
            command = new Command(Command.IM, "viewfield");
        } else {
            // otherwise we continue in standard
            command = new Command(type.getCmd(), "view" + type.name().toLowerCase());
        }
        while (wit.hasNext()) {
            WorkItem wi = wit.next();
            // out.println("wi.getId(): " + wi.getDisplayId());
            command.addSelection(wi.getId());
        }
        // nothing found, then stop here and return null
        if (command.getSelectionList().size() == 0) {
            return null;
        }
        // run second command
        return execute(command).getWorkItems();
    }

    public static WorkItemIterator getWordTemplates(String typeName) throws APIException {
        Command cmd = new Command(Command.IM, "extractwordtemplates");
        cmd.addOption(new Option("overwriteExisting"));

        cmd.addOption(new Option("type", typeName));
        return execute(cmd).getWorkItems();
    }

//    public String getCharts() throws CmdException {
//        // Open a command shell to execute the im charts command
//        CmdExecutor shell = new CmdExecutor();
//
//        // Gather a list of attributes that we can easily parse
//        StringBuilder fields = new StringBuilder();
//        for (int i = 0; i < Integrity.chartAttributes.length; i++) {
//            fields.append(Integrity.chartAttributes[i]);
//            fields.append(i < (Integrity.chartAttributes.length + 1) ? "," : "");
//        }
//
//        // Execute the im charts command
//        String cmdString = "im charts " + api.getConnectionString() + " --fieldsDelim=| --fields=" + fields.toString();
//        shell.execute(cmdString);
//        // Return the output
//        return shell.getCommandOutput();
//    }
    public List<Chart> getCharts2() throws CmdException, APIException {
        List<Chart> result = new ArrayList<>();
        log("Reading " + "charts ...", 1);
        Command cmd = new Command(Command.IM, "charts");
        // Add each query selection to the view query command
        addFieldList(cmd, chartAttributes);
        WorkItemIterator wit = execute(cmd).getWorkItems();

        while (wit.hasNext()) {
            WorkItem chart = wit.next();
            // Execute the im charts command
            String cmdString = "im viewchart " + getConnectionString() + " \"" + chart.getId() + "\"";
            // out.println("CMD: " + cmdString + " ...");
            // Open a command shell to execute the im charts command
            OSCommandHandler osh = new OSCommandHandler();
            int retCode = osh.executeCmd(cmdString, true);
            // if (out.println("Return code: " + retCode);
            // out.println("Return text: \n" + osh.getUnfilteredResult());
            String output = osh.getUnfilteredResult();

            result.add(parseChart(this, chart, output));
        }
        // Return the output
        return result;
    }

    public String genChartPreviewFile(WorkItem chart, String graphStyle) {
        String query = "";
        if (chart.contains("query")) {
            query = chart.getField("query").getValueAsString();
        } // ((subquery[All Change Orders]) and (field["Solution Field"] = "Specification")
        File path = new File(CONTENT_DIR + fs + Types.Chart.getDirectory());
        path.mkdirs();
        try {
            Command runChart = new Command(Command.IM, "runchart");
            String fileName = chart.getId().replace(" ", "_").replaceAll("[^a-zA-Z0-9-_]", "") + "." + (graphStyle.equals("Table") ? "txt" : "png");
            runChart.addOption(new Option("overwriteOutputFile"));
            runChart.addOption(new Option("outputFile", CONTENT_DIR + fs + Types.Chart.getDirectory() + fs + fileName));
            if (query != null && !query.isEmpty() && !chart.getId().equals("Solution Usage Trend")) {
                runChart.addOption(new Option("querydefinition", "((subquery[" + query + "]) and (field[\"ID\"] < 1000))"));
            }
            runChart.addSelection(chart.getId());
            execute(runChart);
            return fileName;
        } catch (APIException ex) {
            Logger.getLogger(Integrity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * View Viewsets
     *
     * @return
     * @throws APIException
     */
    public WorkItemIterator viewViewSets() throws APIException {
        Command intViewsets = new Command(Command.INTEGRITY, "viewsets");
        return execute(intViewsets).getWorkItems();
    }

    public File fetchViewset(String viewset, File exportDir) throws APIException {
        File vsFile = new File(exportDir, viewset + ".vs");
        Command intFetchViewset = new Command(Command.INTEGRITY, "fetchviewset");
        intFetchViewset.addOption(new Option("destination", exportDir.getAbsolutePath()));
        intFetchViewset.addOption(new Option("overwriteExisting"));
        intFetchViewset.addSelection(viewset);
        Response res = execute(intFetchViewset);
        if (null != res && res.getWorkItemListSize() > 0) {
            // Correct the path to the exported viewset file
            WorkItem wi = res.getWorkItems().next();
            vsFile = new File(exportDir, wi.getId() + ".vs");
        } else {
            log("Failed to export viewset " + viewset, 1);
        }
        return vsFile;
    }

    public static File getDBFile(String file, File exportDir) throws APIException {
        File exportFile = new File(file);
        exportFile = new File(exportDir, exportFile.getName());

        // Check to see if this file already exists
        if (exportFile.exists()) {
            exportFile.delete();
        }

        // Export this db file
        Command getDBFile = new Command(Command.IM, "getdbfile");
        getDBFile.addOption(new FileOption("output", exportFile));
        getDBFile.addSelection(file);
        execute(getDBFile);

        // Return the location to the exported file
        return exportFile;
    }

    public String getAbout(String sectionName) {

        String result = "<hr><div style=\"font-size:x-small;white-space: nowrap;text-align:center;\">"
                + sectionName + "<br/>" + copyright
                + "<br/>" + "Current User: " + getUserName() + "<br/>";
        Command cmd = new Command("im", "about");
        try {
            Response response = execute(cmd);
            WorkItem wi = response.getWorkItem("ci");
            // get the details
            result = result + wi.getField("title").getValueAsString();
            result = result + ", Version: " + wi.getField("version").getValueAsString();
            try {  // there might not be a hotfix installed
                result = result + "<br/>HotFixes: " + wi.getField("hotfixes").getValueAsString().replaceAll(",", ", ") + "</br>";
            } catch  (NoSuchElementException ex) {}
            result = result + "API Version: " + wi.getField("apiversion").getValueAsString();

            return result + "</div>";
        } catch (APIException | NullPointerException| NoSuchElementException ex) {
            // Logger.getLogger(APISession.class.getName()).log(Level.SEVERE, null,
            // ex);
        }
        return result;
    }

    private static void addFieldList(Command cmd, String[] fieldList) {
        MultiValue mv = new MultiValue(",");
        for (String typeAttribute : fieldList) {
            mv.add(typeAttribute);
        }
        cmd.addOption(new Option("fields", mv));
    }
}
