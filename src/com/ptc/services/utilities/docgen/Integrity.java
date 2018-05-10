package com.ptc.services.utilities.docgen;

// Java Imports
import com.mks.api.Command;
import com.mks.api.FileOption;
import com.mks.api.MultiValue;
import com.mks.api.Option;
import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.utilities.CmdException;
import com.ptc.services.utilities.CmdExecutor;
import static com.ptc.services.utilities.docgen.ChartFactory.parseChart;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_IMAGES_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.Copyright.copyright;
import static com.ptc.services.utilities.docgen.GatewayTemplates.getGatewayTemplates;
import static com.ptc.services.utilities.docgen.Images.getImages;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.doExport;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getTypeList;
import static com.ptc.services.utilities.docgen.IntegrityDocs.iObjectList;
import static com.ptc.services.utilities.docgen.Metrics.getMetrics;
import com.ptc.services.utilities.docgen.relationships.IntegrityException;
import com.ptc.services.utilities.docgen.session.APISession;
import com.ptc.services.utilities.docgen.utils.FileUtils;
import static com.ptc.services.utilities.docgen.utils.ImageUtils.extractImage;
import static com.ptc.services.utilities.docgen.utils.Logger.exception;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import static com.ptc.services.utilities.docgen.utils.Logger.print;
import com.ptc.services.utilities.docgen.utils.OSCommandHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Integrity {

    public static int globalId = 0;

    // Static objects for escaping pick values
    private static final CharSequence equals = new StringBuffer("=");
    private static final CharSequence equalsFix = new StringBuffer("\\=");
    private static final CharSequence colon = new StringBuffer(":");
    private static final CharSequence colonFix = new StringBuffer("\\:");
    private static final CharSequence semicolon = new StringBuffer(";");
    private static final CharSequence semicolonFix = new StringBuffer("\\;");
    private static final CharSequence comma = new StringBuffer(",");
    private static final CharSequence commaFix = new StringBuffer("\\,");

    private APISession api = null;
    public static final String[] typeAttributes = new String[]{"addLabel", "allowChangePackages", "associatedType", "backsProject", "branch",
        "branchEnabled", "canRelateToTestResult", "copyFields", "copyTree", "copytreeEnabled",
        "createCPPolicy", "defaultReferenceMode", "deleteItem", "deleteItemEnabled", "deleteLabel",
        "description", "documentClass", "duplicateDetectionMandatory", "duplicateDetectionSearchField",
        "editPresentation", "fieldRelationships", "groupDocument", "id", "image", "isTestResult",
        "issueEditability", "labelEnabled", "majorRevision", "mandatoryFields", "minorRevision",
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

    public static final String USER_XML_PREFIX = "USER_";
    public static final String GROUP_XML_PREFIX = "GROUP_";

    // public static List<IntegrityType> iTypes = new ArrayList<>();
    public static LinkedHashMap<String, IntegrityField> sysFieldsHash;
    // public static List<IntegrityField> iFields = new ArrayList<>();

    public APISession getAPI() {
        return api;
    }
    public LinkedHashMap<String, IntegrityField> getSysFields () {
        return sysFieldsHash;
    }

    /**
     * Utility function that escapes an equals or semicolon within a string
     *
     * @param value String containing an equals or semicolon
     * @return
     */
    public static final String fixTriggerValue(String value) {
        return (null != value && value.length() > 0) ? value.replace(equals, equalsFix).replace(semicolon, semicolonFix) : value;
    }

    /**
     * Utility function that escapes a colon or comma within a Pick name/value
     *
     * @param value Pick name/value string
     * @return
     */
    public static final String fixPickValue(String value) {
        return (null != value && value.length() > 0) ? value.replace(colon, colonFix).replace(comma, commaFix) : value;
    }

    /**
     * Utility function that escapes a colon or semicolon within a Property
     * name/value
     *
     * @param value Property name/value string
     * @return
     */
    public static final String fixPropertyValue(String value) {
        return (null != value && value.length() > 0) ? value.replace(colon, colonFix).replace(semicolon, semicolonFix) : value;
    }

    public static final String convertListToString(List<String> list, String delim) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            sb.append(it.next());
            sb.append(it.hasNext() ? delim : "");
        }
        return sb.toString();
    }

    public static final List<String> convertStringToList(String str, String delim) {
        List<String> list = new ArrayList<>();
        if (null != str && str.length() > 0) {
            String[] tokens = str.split(delim);
            for (String token : tokens) {
                list.add(token.trim());
            }
        }
        return list;
    }

    public static final String getUserFullName(Item iUser) {
        Field fullName = iUser.getField("fullname");
        if (null != fullName && null != fullName.getValueAsString() && fullName.getValueAsString().length() > 0) {
            return fullName.getValueAsString();
        } else {
            return iUser.getId();
        }
    }

    public static final String getDateString(SimpleDateFormat sdf, Date fieldValue) {
        if (null == sdf) {
            return fieldValue.toString();
        } else {
            return sdf.format(fieldValue);
        }
    }

    public static final boolean getBooleanFieldValue(Field fld) {
        if (null != fld && null != fld.getDataType() && fld.getDataType().endsWith(Field.BOOLEAN_TYPE)) {
            return fld.getBoolean();
        }

        return false;
    }

    public static final String getStringFieldValue(Field fld) {
        if (null != fld && null != fld.getDataType()) {
            if (fld.getDataType().equals(Field.ITEM_TYPE) && null != fld.getItem()) {
                return fld.getItem().getId();
            } else {
                return null == fld.getValueAsString() ? "" : fld.getValueAsString();
            }
        } else {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static final String summarizePermissionsList(Field itemList, String delim) {
        StringBuilder sb = new StringBuilder();
        if (null != itemList && null != itemList.getList()) {
            List<Item> principalList = itemList.getList();
            List<String> usersList = new ArrayList<>();
            List<String> groupsList = new ArrayList<>();
            for (Item principal : principalList) {
                if (principal.getModelType().equals(IMModelTypeName.USER)) {
                    usersList.add(principal.getId());
                } else if (principal.getModelType().equals(IMModelTypeName.GROUP)) {
                    groupsList.add(principal.getId());
                }
            }

            if (delim.equals("<br/>" + nl)) {
                sb.append(usersList.size() > 0 ? "Users:&nbsp;&nbsp;" + convertListToString(usersList, ", ") : "");
                sb.append(usersList.size() > 0 && groupsList.size() > 0 ? "<br/>" : "");
                sb.append(groupsList.size() > 0 ? "Groups:&nbsp;&nbsp;" + convertListToString(groupsList, ", ") : "");
            } else {
                sb.append(usersList.size() > 0 ? "u=" + getXMLParamFieldValue(usersList, USER_XML_PREFIX, ",") : "");
                sb.append(usersList.size() > 0 && groupsList.size() > 0 ? ";" : "");
                sb.append(groupsList.size() > 0 ? "g=" + getXMLParamFieldValue(groupsList, GROUP_XML_PREFIX, ",") : "");
            }
        }
        return sb.toString();
    }

    public static final List<String> getListOfStrings(Field fld, String delim) {
        List<String> listOfStrings = new ArrayList<>();
        if (null != fld && null != fld.getDataType()) {
            // First determine the field type we're dealing with
            if (fld.getDataType().equals(Field.VALUE_LIST_TYPE)) {
                List<?> valuesList = fld.getList();
                for (Iterator<?> vlit = valuesList.iterator(); vlit.hasNext();) {
                    listOfStrings.add(String.valueOf(vlit.next()));
                }
            } else if (fld.getDataType().equals(Field.ITEM_LIST_TYPE)) {
                @SuppressWarnings("unchecked")
                List<Item> valuesList = fld.getList();
                for (Iterator<Item> iit = valuesList.iterator(); iit.hasNext();) {
                    listOfStrings.add(iit.next().getId());
                }
            } else if (fld.getDataType().equals(Field.ITEM_TYPE)) {
                listOfStrings.add(fld.getItem().getId());
            } else {
                listOfStrings.addAll(convertStringToList(getStringFieldValue(fld), delim));
            }
        }

        return listOfStrings;
    }

    public static final String getFieldValue(Field fld, String delimiter) {
        StringBuilder values = new StringBuilder();
        for (Iterator<?> vlit = getListOfStrings(fld, delimiter).iterator(); vlit.hasNext();) {
            values.append(String.valueOf(vlit.next()) + (vlit.hasNext() ? delimiter : ""));
        }
        return values.toString();
    }

    public void retrieveObjects(IntegrityDocs.Types type, Boolean doXML) throws APIException, IntegrityException, CmdException {
        if (doExport[type.getID()]) {
            List<IntegrityAdminObject> iO = iObjectList.get(type.getID());

            if (type.equals(Types.Trigger) || type.equals(Types.Type) || type.equals(Types.Chart)) {
                List<IntegrityAdminObject> list = getObjectList(type, doXML);
                for (IntegrityAdminObject listEntry : list) {
                    iO.add(listEntry);
                }
            } else {

                WorkItemIterator objects = getObjects(type, doXML);
                while (objects.hasNext()) {
                    WorkItem object = objects.next();
                    iO.add(new IntegrityObject(object, type));
                }
            }
        }
    }

    public static final String getXMLParamFieldValue(Field fld, String xmlPrefix, String delimiter) {
        StringBuilder values = new StringBuilder();
        for (Iterator<?> vlit = getListOfStrings(fld, delimiter).iterator(); vlit.hasNext();) {
            String val = String.valueOf(vlit.next());
            if (xmlPrefix.indexOf("FIELD") > 0 && val.indexOf("mks:") > 0) {
                values.append(val + (vlit.hasNext() ? delimiter : ""));
            } else {
                String xmlParam = xmlPrefix + XMLWriter.getXMLParamName(val);
                XMLWriter.paramsHash.put(xmlParam, val);
                values.append(XMLWriter.padXMLParamName(xmlParam) + (vlit.hasNext() ? delimiter : ""));
            }
        }
        return values.toString();
    }

    public static final String getXMLParamFieldValue(List<String> list, String xmlPrefix, String delimiter) {
        StringBuilder values = new StringBuilder();
        for (Iterator<String> vlit = list.iterator(); vlit.hasNext();) {
            String val = vlit.next();
            String xmlParam = xmlPrefix + XMLWriter.getXMLParamName(val);
            XMLWriter.paramsHash.put(xmlParam, val);
            values.append(XMLWriter.padXMLParamName(xmlParam) + (vlit.hasNext() ? delimiter : ""));
        }
        return values.toString();
    }

    public static String getXMLParamFieldValue(String textFormat) {
        // Only process if we've got something to resolve...
        if (null != textFormat && textFormat.contains("{")) {
            StringBuilder resolvedString = new StringBuilder();
            int startIndx = 0;
            int curIndx = textFormat.indexOf("{", startIndx);
            while (curIndx >= 0) {
                if (curIndx > 0) {
                    resolvedString.append(textFormat.substring(startIndx, curIndx));
                }

                if (curIndx == (textFormat.length() - 1)) {
                    resolvedString.append("{");
                    startIndx = curIndx + 1;
                } else {
                    int endIndx = textFormat.indexOf('}', curIndx);
                    if (endIndx < 0) {
                        // Matching closing token not found, parse error!
                        break;
                    }

                    String rawFieldName = textFormat.substring(curIndx + 1, endIndx);
                    String xmlParam = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(rawFieldName);
                    XMLWriter.paramsHash.put(xmlParam, rawFieldName);
                    resolvedString.append(XMLWriter.padXMLParamName(xmlParam));
                    startIndx = endIndx + 1;
                }

                curIndx = textFormat.indexOf("{", startIndx);
            }

            if (startIndx < textFormat.length()) {
                resolvedString.append(textFormat.substring(startIndx));
            }

            return resolvedString.toString();
        } else {
            return textFormat;
        }
    }

    public static String getXMLParamFieldValue(IntegrityField iField, String iFieldValue) {
        StringBuilder sb = new StringBuilder();
        switch (iField.getFieldType()) {
            case TYPE:
                sb.append(XMLWriter.padXMLParamName(IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
                break;

            case STATE:
                sb.append(XMLWriter.padXMLParamName(IntegrityState.XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
                break;

            case USER:
                sb.append(XMLWriter.padXMLParamName(Integrity.USER_XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
                break;

            case GROUP:
                sb.append(XMLWriter.padXMLParamName(Integrity.GROUP_XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
                break;

            default:
                sb.append(iFieldValue);
        }

        return sb.toString();
    }

    /**
     * Default connection to Integrity
     *
     * @throws APIException
     */
    public Integrity() throws APIException {
        this.api = new APISession();
        sysFieldsHash = this.getFields();
    }

//    public List<IntegrityType> getTypeList() {
//        return iTypes;
//    }
    /**
     * Override connection to specific Integrity Application
     *
     * @param app
     * @throws APIException
     */
    public Integrity(String app) throws APIException {
        api = new APISession(app);
    }

    /**
     * Returns a string list of the specified admin object
     *
     * @param obj
     * @return
     * @throws APIException
     */
    public List<String> getAdminList(String obj, String ignoreList, String stopElement) throws APIException {
        List<String> adminList = new ArrayList<>();
        Command imAdminList = new Command(Command.IM, obj);
        Response res = api.runCommand(imAdminList);
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
        Response res = api.runCommand(imAdminList);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                WorkItem wi = wit.next();
                adminIDList.put(Integrity.getStringFieldValue(wi.getField("id")), wi.getId());
            }
        }
        return adminIDList;
    }

    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Field> viewType(String typeName) throws APIException {
        LinkedHashMap<String, Field> typeDetails = new LinkedHashMap<>();
        Command imTypes = new Command(Command.IM, "types");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (String typeAttribute : Integrity.typeAttributes) {
            mv.add(typeAttribute);
        }
        imTypes.addOption(new Option("fields", mv));
        imTypes.addSelection(typeName);
        Response res = api.runCommand(imTypes);
        if (null != res && null != res.getWorkItem(typeName)) {
            WorkItem wi = res.getWorkItem(typeName);
            for (Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
                Field field = fit.next();
                print("\t... " + field.getName());
                typeDetails.put(field.getName(), field);
                log(" done.");
            }

            // Run im view type to get the missing type attributes
            Command imViewType = new Command(Command.IM, "viewtype");
            imViewType.addSelection(typeName);
            Response viewRes = api.runCommand(imViewType);
            if (null != viewRes && null != viewRes.getWorkItem(typeName)) {
                WorkItem wi2 = viewRes.getWorkItem(typeName);
                // Only fetch the missing fields
                //	... created
                //	... createdBy
                //	... lastModified
                //	... modifiedBy
                Field created = wi2.getField("created");
                print("\t... " + created.getName());
                typeDetails.put(created.getName(), created);
                log(" done.", 1);

                Field createdBy = wi2.getField("createdBy");
                print("\t... " + createdBy.getName());
                typeDetails.put(createdBy.getName(), createdBy);
                log(" done.", 1);

                Field lastModified = wi2.getField("lastModified");
                print("\t... " + lastModified.getName());
                typeDetails.put(lastModified.getName(), lastModified);
                log(" done.", 1);

                Field modifiedBy = wi2.getField("modifiedBy");
                print("\t... " + modifiedBy.getName());
                typeDetails.put(modifiedBy.getName(), modifiedBy);
                log(" done.", 1);

                Field image = wi2.getField("image");
                if (image.getItem().getId().contentEquals("custom")) {
                    String fileName = CONTENT_DIR + "/Types/" + wi.getId().replaceAll(" ", "_") + ".png";
                    File imageFile = new File(fileName);
                    extractImage(image, imageFile);
                    typeDetails.put("smallImage", new SimpleField("smallImage", fileName));
                } else {
                    try {
                        typeDetails.put("smallImage", new SimpleField("smallImage", CONTENT_DIR + fs + wi.getField("type").getString()));
                    } catch (NoSuchElementException ex) {
                        typeDetails.put("smallImage", new SimpleField("smallImage", CONTENT_IMAGES_DIR + fs + "Type.png"));
                    }
                }

            }
        }
        return typeDetails;
    }

    public WorkItemIterator viewTriggers(List<String> triggerList) throws APIException {
        Command imViewTrigger = new Command(Command.IM, "viewtrigger");
        // Add each trigger selection to the view trigger command
        for (Iterator<String> it = triggerList.iterator(); it.hasNext();) {
            imViewTrigger.addSelection(it.next());
        }
        return api.runCommandWithInterim(imViewTrigger).getWorkItems();
    }

    public WorkItemIterator getIMMainProjects() throws APIException {
        // CmdExecutor shell = new CmdExecutor();
        ArrayList<String> lines = new ArrayList<>();
        MultiValue mv = new MultiValue(",");
        for (String typeAttribute : Integrity.projectAttributes) {
            mv.add(typeAttribute);
        }

        Command cmd = new Command(Command.IM, "diag");
        cmd.addOption(new Option("diag", "runsql"));
        cmd.addOption(new Option("param", "select Name from Projects where ParentID is null"));
        Response response = api.runCommandWithInterim(cmd);
        String message = response.getResult().getMessage();
        // ResponseUtil.printResponse(response, 1, System.out);

        cmd = new Command(Command.IM, "projects");
        cmd.addOption(new Option("fields", mv));

        for (String line : message.split("\n")) {
            // out.println("line: " + line);
            line = line.trim();
            // ignore 
            if (!line.toUpperCase().equals("NAME") && !line.startsWith("-----")) {
                lines.add(line);
                if (!line.isEmpty()) {
                    // add th eprojects you want to list
                    cmd.addSelection("/" + line.trim());
                }
            }
        }
        if (cmd.getSelectionList().size() > 0) {
            return api.runCommandWithInterim(cmd).getWorkItems();
        }
        return new EmptyList();
    }

    public LinkedHashMap<String, Field> viewField(String typeName, String fieldName) throws APIException {
        LinkedHashMap<String, Field> fieldDetails = new LinkedHashMap<>();
        Command imViewField = new Command(Command.IM, "viewfield");
        if (typeName != null) {
            imViewField.addOption(new Option("overrideForType", typeName));
        }
        imViewField.addSelection(fieldName);
        Response res = api.runCommand(imViewField);
        if (null != res && null != res.getWorkItem(fieldName)) {
            WorkItem wi = res.getWorkItem(fieldName);
            for (@SuppressWarnings("unchecked") Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
                Field field = fit.next();
                fieldDetails.put(field.getName(), field);
            }
        }
        return fieldDetails;
    }

    public LinkedHashMap<String, IntegrityState> getStates(String typeName, List<String> statesList) throws APIException {
        LinkedHashMap<String, IntegrityState> stateDetails = new LinkedHashMap<>();
        Command imStates = new Command(Command.IM, "states");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (String stateAttribute : Integrity.stateAttributes) {
            mv.add(stateAttribute);
        }
        imStates.addOption(new Option("fields", mv));

        if (statesList.size() > 0) {
            // Add the list of selections
            for (Iterator<String> it = statesList.iterator(); it.hasNext();) {
                imStates.addSelection(it.next());
            }

            // Run the im states command to get the global details on the state
            Response res = api.runCommandWithInterim(imStates);
            // Parse the response for the initial pass
            if (null != res && null != res.getWorkItems()) {
                WorkItemIterator wii = res.getWorkItems();
                while (wii.hasNext()) {
                    WorkItem wi = wii.next();
                    String stateName = wi.getId();
                    log("\t\tAnalyzing state: " + stateName, 1);
                    IntegrityState iState = new IntegrityState(typeName, wi);
                    stateDetails.put(stateName, iState);
                }
            }

            // Now run the im viewstate to get the overrides...
            Command imViewState = new Command(Command.IM, "viewstate");
            imViewState.addOption(new Option("overrideForType", typeName));
            imViewState.setSelectionList(imStates.getSelectionList());
            // Run the command and parse the response
            Response viewStateRes = api.runCommand(imViewState);
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

    public LinkedHashMap<String, IntegrityField> getFields() throws APIException {
        // Initialize our return variable
        LinkedHashMap<String, IntegrityField> fieldDetails = new LinkedHashMap<>();
        // Setup the im fields command to get the global definition of the field
        Command imFields = new Command(Command.IM, "fields");
        // Run the im fields command to get the global details on the field
        Response res = api.runCommandWithInterim(imFields);
        // Parse the response for the initial pass
        if (null != res && null != res.getWorkItems()) {
            WorkItemIterator wii = res.getWorkItems();
            List<String> selectionList = new ArrayList<>();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                selectionList.add(wi.getId());
            }
            Command imViewField = new Command(Command.IM, "viewfield");
            for (String fld : selectionList) {
                imViewField.addSelection(fld);
            }
            // imViewField.addSelection("Spawns");
            Response viewFieldRes = api.runCommand(imViewField);
            // ResponseUtil.printResponse(viewFieldRes, 1, System.out);
            // System.exit(1);
            WorkItemIterator wit = viewFieldRes.getWorkItems();
            while (wit.hasNext()) {
                WorkItem wi = wit.next();
                fieldDetails.put(wi.getId(), new IntegrityField(wi));
            }
        }
        log("Fields added: " + fieldDetails.size(), 1);
        return fieldDetails;
    }

    public LinkedHashMap<String, IntegrityField> getFields(String typeName, Field visibleFields, Field visibleFieldsForMe) throws APIException {
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
        MultiValue mv = new MultiValue(",");
        for (String fieldAttribute : Integrity.fieldAttributes) {
            mv.add(fieldAttribute);
        }
        imFields.addOption(new Option("fields", mv));

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
            Response res = api.runCommandWithInterim(imFields);
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
            Response viewFieldRes = api.runCommand(imViewField);
            for (Iterator<Item> lit = visibleFieldsList.iterator(); lit.hasNext();) {
                Item visibleField = lit.next();
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

    public List<IntegrityAdminObject> getObjectList(Types type, Boolean doXML) throws APIException, CmdException {
        if (type.equals(Types.Trigger)) {
            return TriggerFactory.parseTriggers(sysFieldsHash, this.viewTriggers(this.getAdminList("triggers", "", "")), doXML);
        } else if (type.equals(Types.Chart)) {
            return getCharts2();
        } else if (type.equals(Types.Type)) {
            List<IntegrityAdminObject> ao = new ArrayList<>();
            for (String typeName : getTypeList()) {
                log("Processing Type: " + typeName);
                ao.add(new IntegrityType(this, this.viewType(typeName), doXML));
            }
            return ao;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        // if (type.equals(Types.Type)) {
        if (type.equals(Types.IMProject)) {
            return getIMMainProjects();
        }
        if (type.equals(Types.GatewayExportConfig)) {
            return getGatewayConfigs("export", "exporter", CONTENT_DIR + fs + "GatewayExportConfigs");
        }
        if (type.equals(Types.GatewayImportConfig)) {
            return getGatewayConfigs("parser", "parser", CONTENT_DIR + fs + "GatewayImportConfigs");
        }
        if (type.equals(Types.GatewayMapping)) {
            return getGatewayMappings(CONTENT_DIR + fs + "GatewayMappings");
        }
        if (type.equals(Types.Image)) {
            return getImages(CONTENT_IMAGES_DIR.getAbsolutePath());
        }
        if (type.equals(Types.Metric)) {
            return getMetrics(this, Types.Metric, CONTENT_DIR);
        }
        if (type.equals(Types.GatewayTemplate)) {
            return getGatewayTemplates(CONTENT_DIR + fs + "GatewayExportConfigs", CONTENT_DIR + fs + "GatewayImportConfigs");
        }
        if (type.equals(Types.TraceDefault) && !IntegrityDocs.solutionTypeName.isEmpty()) {
            RelationshipAnalyser ra = new RelationshipAnalyser(this, IntegrityDocs.solutionTypeName);
            return ra.analyseTraces(IntegrityDocs.getList(Types.Field));
        }

        Command command = new Command(type.getCmd(), type.getPlural().toLowerCase());

        if (type.equals(Types.SIProject)) {
            command.addOption(new Option("nodisplaySubs"));
            command.setCommandName("projects");
        }

        // run first command
        WorkItemIterator wit = api.runCommandWithInterim(command).getWorkItems();

        // stop here for ViewSet and SI Projects
        if (type.equals(Types.Viewset) || type.equals(Types.SIProject)) {
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
        // run second command
        return api.runCommandWithInterim(command).getWorkItems();
    }

    public WorkItemIterator getWordTemplates(String typeName) throws APIException {
        Command cmd = new Command(Command.IM, "extractwordtemplates");
        cmd.addOption(new Option("overwriteExisting"));

        cmd.addOption(new Option("type", typeName));
        return api.runCommand(cmd).getWorkItems();
    }

    public GatewayConfigs getGatewayConfigs(String gcType, String elem, String path) throws APIException {
        GatewayConfigs lgc = new GatewayConfigs();

        // this runs on server
        Response response = api.runCommand(new Command("im", "gatewaywizardconfigurations"));
        // ResponseUtil.printResponse(response, 1, System.out);

        for (WorkItemIterator wii = response.getWorkItems(); wii.hasNext();) {
            WorkItem wi = wii.next();
            try {
                String encoding = null;
                try {
                    encoding = wi.getField("Encoding").getString();
                } catch (NoSuchElementException ignored) {
                }
                if (encoding == null) {
                    encoding = "UTF-8";
                }
                Field field = wi.getField("Content");
                java.io.InputStream xmlReader = new ByteArrayInputStream(field.getString().getBytes(encoding));
                // registerConfig("<remote>", (new ItemMapperParser(xmlReader)).process());

                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(xmlReader);
                doc.getDocumentElement().normalize();
                // log("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nList = doc.getElementsByTagName(gcType + "-config");

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    // log("\nCurrent Element :" + nNode.getNodeName());

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        GatewayConfig gc = new GatewayConfig();
                        gc.addField("name", eElement.getElementsByTagName("name").item(0).getTextContent());
                        gc.addField("description", eElement.getElementsByTagName("description").item(0).getTextContent());
                        gc.addField("position", String.valueOf(temp + 1));
                        for (int ext = 0; ext < eElement.getElementsByTagName("extension").getLength(); ext++) {
                            gc.addField("extensions (" + (ext + 1) + ")", eElement.getElementsByTagName("extension").item(ext).getTextContent());
                        }
                        String exporterClass = "";
                        try {
                            exporterClass = eElement.getElementsByTagName(elem).item(0).getAttributes().getNamedItem("class").getTextContent();
                        } catch (NullPointerException skip) {

                        }
                        String id = "";
                        try {
                            id = eElement.getElementsByTagName(elem).item(0).getAttributes().getNamedItem("id").getTextContent();
                        } catch (NullPointerException skip) {

                        }

                        gc.addField("type", exporterClass.length() > 10 ? "custom" : "standard");
                        gc.addField("isActive", "true");
                        gc.addField("gateway-configuration-name", eElement.getElementsByTagName("gateway-configuration-name").item(0).getTextContent());
                        gc.addField(elem + " class", exporterClass);
                        gc.addField(elem + " id", id);

                        NodeList pList = eElement.getElementsByTagName(elem).item(0).getChildNodes();
                        for (int propId = 0; propId < pList.getLength(); propId++) {
                            Node pNode = pList.item(propId);
                            if (pNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element pElement = (Element) pNode;

                                if (pElement.getAttribute("name").equals("template") || pElement.getAttribute("name").equals("xslt")) {
                                    FileUtils.inputStreamToFile(path, pElement.getTextContent());
                                    String fileName = pElement.getTextContent().substring(pElement.getTextContent().lastIndexOf('/') + 1);
                                    gc.addField(elem + " property '" + pElement.getAttribute("name") + "'", "<a href=\"" + fileName + "\">" + pElement.getTextContent() + "</a>");
                                } else {
                                    gc.addField(elem + " property '" + pElement.getAttribute("name") + "'", pElement.getTextContent());
                                }
                            }
                        }
                        lgc.add(gc);
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                log("ERROR: " + (new StringBuilder()).append("Skipping remote configuration due to error: ").append(e.getMessage()).toString(), 10);
                exception(Level.WARNING, 10, e);
            }
        }
        return lgc;
    }

    public GatewayMappings getGatewayMappings(String path) throws APIException {
        GatewayMappings lgc = new GatewayMappings();

        // this runs on server
        Response response = api.runCommand(new Command("im", "gatewayconfigurations"));
        // ResponseUtil.printResponse(response, 1, System.out);
        int cnt = 0;
        for (WorkItemIterator wii = response.getWorkItems(); wii.hasNext();) {
            WorkItem wi;
            try {
                wi = wii.next();
                cnt++;
                String encoding = null;
                try {
                    encoding = wi.getField("Encoding").getString();
                } catch (NoSuchElementException ignored) {
                }
                if (encoding == null) {
                    encoding = "UTF-8";
                }
                Field field = wi.getField("Content");
                java.io.InputStream xmlReader = new ByteArrayInputStream(field.getString().getBytes(encoding));
                // registerConfig("<remote>", (new ItemMapperParser(xmlReader)).process());

                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(xmlReader);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("mapping");

                Node nNode = nList.item(0);
                if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    GatewayMapping gc = new GatewayMapping();
                    gc.addField("name", eElement.getAttribute("name"));
                    try {
                        gc.addField("description", eElement.getElementsByTagName("description").item(0).getTextContent());
                    } catch (NullPointerException ex) {
                        gc.addField("description", "-");
                    }
                    gc.addField("position", String.valueOf(cnt));
                    gc.addField("type", "mapping");
                    gc.addField("isActive", "true");

                    String fileName = eElement.getAttribute("name") + ".xml";

                    FileUtils.resourceToFile(new ByteArrayInputStream(field.getString().getBytes(encoding)), path, fileName);
                    gc.addField("Mapping File", "<a href=\"" + fileName + "\" download=\"" + fileName + ".txt\">" + fileName + "</a>");
                    lgc.add(gc);
                }

            } catch (APIException | ParserConfigurationException | SAXException | IOException e) {
                log("ERROR: " + (new StringBuilder()).append("Skipping remote configuration due to error: ").append(e.getMessage()).toString(), 10);
                exception(Level.WARNING, 10, e);
            }
        }
        return lgc;
    }

    public String getCharts() throws CmdException {
        // Open a command shell to execute the im charts command
        CmdExecutor shell = new CmdExecutor();

        // Gather a list of attributes that we can easily parse
        StringBuilder fields = new StringBuilder();
        for (int i = 0; i < Integrity.chartAttributes.length; i++) {
            fields.append(Integrity.chartAttributes[i]);
            fields.append(i < (Integrity.chartAttributes.length + 1) ? "," : "");
        }

        // Execute the im charts command
        String cmdString = "im charts " + api.getConnectionString() + " --fieldsDelim=| --fields=" + fields.toString();
        shell.execute(cmdString);
        // Return the output
        return shell.getCommandOutput();
    }

    public List<IntegrityAdminObject> getCharts2() throws CmdException, APIException {
        List<IntegrityAdminObject> result = new ArrayList<>();
        log("Reading " + "charts ...", 1);
        Command cmd = new Command(Command.IM, "charts");
        // Add each query selection to the view query command
        String fields = "";
        for (int i = 0; i < Integrity.chartAttributes.length; i++) {
            fields += (Integrity.chartAttributes[i]);
            fields += (i < (Integrity.chartAttributes.length + 1) ? "," : "");
        }
        cmd.addOption(new Option("fields", fields));
        WorkItemIterator wit = api.runCommandWithInterim(cmd).getWorkItems();

        while (wit.hasNext()) {
            WorkItem chart = wit.next();
            // Execute the im charts command
            String cmdString = "im viewchart " + api.getConnectionString() + " \"" + chart.getId() + "\"";
            // out.println("CMD: " + cmdString + " ...");
            // Open a command shell to execute the im charts command
            OSCommandHandler osh = new OSCommandHandler();
            int retCode = osh.executeCmd(cmdString, true);
            // if (out.println("Return code: " + retCode);
            // out.println("Return text: \n" + osh.getUnfilteredResult());
            String output = osh.getUnfilteredResult();

            result.add(parseChart(chart, output));
        }
        // Return the output
        return result;
    }

    public WorkItemIterator viewViewSets() throws APIException {
        Command intViewsets = new Command(Command.INTEGRITY, "viewsets");
        return api.runCommandWithInterim(intViewsets).getWorkItems();
    }

    public File fetchViewset(String viewset, File exportDir) throws APIException {
        File vsFile = new File(exportDir, viewset + ".vs");
        Command intFetchViewset = new Command(Command.INTEGRITY, "fetchviewset");
        intFetchViewset.addOption(new Option("destination", exportDir.getAbsolutePath()));
        intFetchViewset.addOption(new Option("overwriteExisting"));
        intFetchViewset.addSelection(viewset);
        Response res = api.runCommand(intFetchViewset);
        if (null != res && res.getWorkItemListSize() > 0) {
            // Correct the path to the exported viewset file
            WorkItem wi = res.getWorkItems().next();
            vsFile = new File(exportDir, wi.getId() + ".vs");
        } else {
            log("Failed to export viewset " + viewset, 1);
        }
        return vsFile;
    }

    public File getDBFile(String file, File exportDir) throws APIException {
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
        api.runCommand(getDBFile);

        // Return the location to the exported file
        return exportFile;
    }

    public List<String> getStagingSystems() throws APIException {
        List<String> stagingSystems = new ArrayList<>();
        Command sdStagingSystems = new Command(Command.SD, "stagingsystems");
        Response res = api.runCommand(sdStagingSystems);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                stagingSystems.add(wit.next().getId());
            }
        }
        return stagingSystems;
    }

    public WorkItem viewStagingSystem(String stagingSystem) throws APIException {
        WorkItem stagingSystemWI = null;
        Command sdViewStagingSystem = new Command(Command.SD, "viewstagingsystem");
        sdViewStagingSystem.addSelection(stagingSystem);
        Response res = api.runCommand(sdViewStagingSystem);
        if (null != res && res.getWorkItemListSize() > 0) {
            stagingSystemWI = res.getWorkItem(stagingSystem);
        }
        return stagingSystemWI;
    }

    public List<WorkItem> getStages(String stagingSystem) throws APIException {
        List<WorkItem> stageList = new ArrayList<>();
        Command sdStages = new Command(Command.SD, "stages");
        MultiValue mv = new MultiValue(",");
        mv.add("automaticrollbacktimeout");
        mv.add("autopromote");
        mv.add("deployfrequency");
        mv.add("deploymode");
        mv.add("deploypolicy");
        mv.add("description");
        mv.add("id");
        mv.add("lastscheduleddeploytime");
        mv.add("lastscheduledtransfertime");
        mv.add("name");
        mv.add("position");
        mv.add("project");
        mv.add("transferfrequency");
        mv.add("transfermode");
        sdStages.addOption(new Option("fields", mv));
        sdStages.addOption(new Option("stagingSystem", stagingSystem));

        Response res = api.runCommand(sdStages);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                stageList.add(wit.next());
            }
        }
        return stageList;
    }

    public WorkItem viewStage(String stagingSystem, String stage) throws APIException {
        WorkItem stageWI = null;
        Command sdViewStage = new Command(Command.SD, "viewstage");
        sdViewStage.addOption(new Option("stagingSystem", stagingSystem));
        sdViewStage.addSelection(stage);
        Response res = api.runCommand(sdViewStage);
        if (null != res && res.getWorkItemListSize() > 0) {
            stageWI = res.getWorkItem(stage);
        }
        return stageWI;
    }

    public List<WorkItem> getDeployTargets(String stagingSystem, String stage) throws APIException {
        List<WorkItem> deployTargetList = new ArrayList<>();
        Command sdDeployTargets = new Command(Command.SD, "deploytargets");
        MultiValue mv = new MultiValue(",");
        //mv.add("activerequestid");
        mv.add("activerequeststate");
        mv.add("agentversion");
        mv.add("connectionerrormessage");
        mv.add("connectionstatus");
        mv.add("deployrequeststatus");
        mv.add("description");
        mv.add("id");
        mv.add("name");
        mv.add("sync");
        mv.add("targethostname");
        mv.add("targetpatchstatusmessage");
        mv.add("targetplatform");
        mv.add("targetport");
        mv.add("targetuser");
        sdDeployTargets.addOption(new Option("fields", mv));
        sdDeployTargets.addOption(new Option("stagingSystem", stagingSystem));
        sdDeployTargets.addOption(new Option("stage", stage));

        Response res = api.runCommand(sdDeployTargets);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                deployTargetList.add(wit.next());
            }
        }
        return deployTargetList;
    }

    public WorkItem viewDeployTarget(String stagingSystem, String stage, String deployTarget) throws APIException {
        WorkItem deployTargetWI = null;
        Command sdViewDeployTarget = new Command(Command.SD, "viewdeploytarget");
        sdViewDeployTarget.addOption(new Option("stagingSystem", stagingSystem));
        sdViewDeployTarget.addOption(new Option("stage", stage));
        sdViewDeployTarget.addSelection(deployTarget);
        Response res = api.runCommand(sdViewDeployTarget);
        if (null != res && res.getWorkItemListSize() > 0) {
            deployTargetWI = res.getWorkItem(deployTarget);
        }
        return deployTargetWI;
    }

    public String getAbout(String sectionName) {

        String result = "<hr><div style=\"font-size:x-small;white-space: nowrap;text-align:center;\">"
                + sectionName + "<br/>" + copyright
                + "<br/>" + "Current User: " + getUserName() + "<br/>";
        Command cmd = new Command("im", "about");
        try {
            Response response = api.runCommand(cmd);
            WorkItem wi = response.getWorkItem("ci");
            // get the details
            result = result + wi.getField("title").getValueAsString();
            result = result + ", Version: " + wi.getField("version").getValueAsString();
            result = result + "<br/>HotFixes: " + wi.getField("hotfixes").getValueAsString().replaceAll(",", ", ") + "</br>";
            result = result + "API Version: " + wi.getField("apiversion").getValueAsString();

            return result + "</div>";
        } catch (APIException | NullPointerException ex) {
            // Logger.getLogger(APISession.class.getName()).log(Level.SEVERE, null,
            // ex);
        }
        return result;
    }

    public String getHostName() {
        return api.getHostName();
    }

    public String getPort() {
        return api.getPort();
    }

    public String getUserName() {
        return api.getUserName();
    }

    public void exit() {
        if (null != api) {
            try {
                api.Terminate();
            } catch (APIException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
