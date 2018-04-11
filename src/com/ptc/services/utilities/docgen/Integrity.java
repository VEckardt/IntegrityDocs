package com.ptc.services.utilities.docgen;

// Java Imports
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

// MKS API (mksapi.jar) Imports
import com.mks.api.Command;
import com.mks.api.FileOption;
import com.mks.api.Option;
import com.mks.api.MultiValue;
import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.Item;
import com.mks.api.response.Response;
import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import com.mks.api.response.APIException;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.utilities.CmdException;
import com.ptc.services.utilities.CmdExecutor;

public class Integrity {

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
    public static final String[] fieldAttributes = new String[]{"allowedTypes", "associatedField", "backedBy", "backingFilter", "backingStates",
        "backingTextField", "backingTextFormat", "backingType", "computation", "correlation",
        "cycleDetection", "default", "defaultAttachmentField", "defaultBrowseQuery", "defaultColumns",
        "description", "displayAsLink", "displayAsProgress", "displayLocation", "displayName",
        "displayPattern", "displayRows", "displayStyle", "editabilityRule", "id", "isForward",
        "isMultiValued", "isSystemManaged", "isTestResult", "lastcompute", "linkFlags", "loggingText",
        "max", "maxLength", "min", "name", "pairedField", "paramSubstitution", "phases", "picks",
        "position", "query", "ranges", /*"references",*/ "relevanceRule", "richContent", "showDateTime",
        "staticComputation", "storeToHistoryFrequency", "suggestions", "textindex", "trace", "type"};
    public static final String[] stateAttributes = new String[]{"capabilities", "description", "id", "name", "position" /*,references*/};
    public static final String[] queryAttributes = new String[]{"createdBy", "description", "fields", "id", "isAdmin", "lastModified", "name",
        "queryDefinition", /*"references",*/ "shareWith", "sortDirection", "sortField"};
    public static final String[] chartAttributes = new String[]{"isAdmin", "id", "name", "chartType", "createdBy", /*"description",*/ "graphStyle", /*"lastModified",*/
        "query", /*"references",*/ "shareWith"};
    public static final String USER_XML_PREFIX = "USER_";
    public static final String GROUP_XML_PREFIX = "GROUP_";

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
        List<String> list = new ArrayList<String>();
        if (null != str && str.length() > 0) {
            String[] tokens = str.split(delim);
            for (int i = 0; i < tokens.length; i++) {
                list.add(tokens[i].trim());
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
            return fld.getBoolean().booleanValue();
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
            List<String> usersList = new ArrayList<String>();
            List<String> groupsList = new ArrayList<String>();
            for (Iterator<Item> pit = principalList.iterator(); pit.hasNext();) {
                Item principal = pit.next();
                if (principal.getModelType().equals(IMModelTypeName.USER)) {
                    usersList.add(principal.getId());
                } else if (principal.getModelType().equals(IMModelTypeName.GROUP)) {
                    groupsList.add(principal.getId());
                }
            }

            if (delim.equals("<br/>" + IntegrityDocs.nl)) {
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
        List<String> listOfStrings = new ArrayList<String>();
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
        if (null != textFormat && textFormat.indexOf("{") >= 0) {
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
        api = new APISession();
    }

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
    public List<String> getAdminList(String obj) throws APIException {
        List<String> adminList = new ArrayList<String>();
        Command imAdminList = new Command(Command.IM, obj);
        Response res = api.runCommand(imAdminList);
        if (null != res && res.getWorkItemListSize() > 0) {
            WorkItemIterator wit = res.getWorkItems();
            while (wit.hasNext()) {
                adminList.add(wit.next().getId());
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
    public Hashtable<String, String> getAdminIDList(String obj) throws APIException {
        Hashtable<String, String> adminIDList = new Hashtable<String, String>();
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
    public Hashtable<String, Field> viewType(String typeName) throws APIException {
        Hashtable<String, Field> typeDetails = new Hashtable<String, Field>();
        Command imTypes = new Command(Command.IM, "types");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (int i = 0; i < Integrity.typeAttributes.length; i++) {
            mv.add(Integrity.typeAttributes[i]);
        }
        imTypes.addOption(new Option("fields", mv));
        imTypes.addSelection(typeName);
        Response res = api.runCommand(imTypes);
        if (null != res && null != res.getWorkItem(typeName)) {
            WorkItem wi = res.getWorkItem(typeName);
            for (Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
                Field field = fit.next();
                System.out.print("\t... " + field.getName());
                typeDetails.put(field.getName(), field);
                System.out.println(" done.");
            }
        }

        // Run im view type to get the missing type attributes
        Command imViewType = new Command(Command.IM, "viewtype");
        imViewType.addSelection(typeName);
        Response viewRes = api.runCommand(imViewType);
        if (null != viewRes && null != viewRes.getWorkItem(typeName)) {
            WorkItem wi = viewRes.getWorkItem(typeName);
            // Only fetch the missing fields
            //	... created
            //	... createdBy
            //	... lastModified
            //	... modifiedBy
            Field created = wi.getField("created");
            System.out.print("\t... " + created.getName());
            typeDetails.put(created.getName(), created);
            System.out.println(" done.");

            Field createdBy = wi.getField("createdBy");
            System.out.print("\t... " + createdBy.getName());
            typeDetails.put(createdBy.getName(), createdBy);
            System.out.println(" done.");

            Field lastModified = wi.getField("lastModified");
            System.out.print("\t... " + lastModified.getName());
            typeDetails.put(lastModified.getName(), lastModified);
            System.out.println(" done.");

            Field modifiedBy = wi.getField("modifiedBy");
            System.out.print("\t... " + modifiedBy.getName());
            typeDetails.put(modifiedBy.getName(), modifiedBy);
            System.out.println(" done.");
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

    public Hashtable<String, Field> viewField(String typeName, String fieldName) throws APIException {
        Hashtable<String, Field> fieldDetails = new Hashtable<String, Field>();
        Command imViewField = new Command(Command.IM, "viewfield");
        imViewField.addOption(new Option("overrideForType", typeName));
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

    public Hashtable<String, IntegrityState> getStates(String typeName, List<String> statesList) throws APIException {
        Hashtable<String, IntegrityState> stateDetails = new Hashtable<String, IntegrityState>();
        Command imStates = new Command(Command.IM, "states");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (int i = 0; i < Integrity.stateAttributes.length; i++) {
            mv.add(Integrity.stateAttributes[i]);
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
                    System.out.println("\t\tAnalyzing state: " + stateName);
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
            for (Iterator<String> lit = statesList.iterator(); lit.hasNext();) {
                String stateName = lit.next();
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
                    for (Iterator<String> it = overrideFieldList.iterator(); it.hasNext();) {
                        String orStateAttribute = it.next();
                        iState.setFieldAttribute(orStateAttribute, wi.getField(orStateAttribute));
                    }
                }
                stateDetails.put(stateName, iState);
            }
        }

        return stateDetails;
    }

    public Hashtable<String, IntegrityField> getFields() throws APIException {
        // Initialize our return variable
        Hashtable<String, IntegrityField> fieldDetails = new Hashtable<String, IntegrityField>();
        // Setup the im fields command to get the global definition of the field
        Command imFields = new Command(Command.IM, "fields");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (int i = 0; i < Integrity.fieldAttributes.length; i++) {
            mv.add(Integrity.fieldAttributes[i]);
        }
        imFields.addOption(new Option("fields", mv));

        // Run the im fields command to get the global details on the field
        Response res = api.runCommandWithInterim(imFields);
        // Parse the response for the initial pass
        if (null != res && null != res.getWorkItems()) {
            WorkItemIterator wii = res.getWorkItems();
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                String fieldName = wi.getId();
                IntegrityField iField = new IntegrityField(null, wi);
                fieldDetails.put(fieldName, iField);
            }
        }
        return fieldDetails;
    }

    public Hashtable<String, IntegrityField> getFields(String typeName, Field visibleFields, Field visibleFieldsForMe) throws APIException {
        // Initialize our return variable
        Hashtable<String, IntegrityField> fieldDetails = new Hashtable<String, IntegrityField>();

        // Get a unique list of all fields that we need to interrogate later...
        List<String> selectionList = new ArrayList<String>();

        // Populate the visible fields hash with just the 'Visible To' values for quick access		
        Hashtable<String, Field> visibleFieldsHash = new Hashtable<String, Field>();
        @SuppressWarnings("unchecked")
        List<Item> partialVisibleFieldsList = visibleFields.getList();
        if (null != partialVisibleFieldsList) {
            for (Iterator<Item> it = partialVisibleFieldsList.iterator(); it.hasNext();) {
                Item fieldItem = it.next();
                visibleFieldsHash.put(fieldItem.getId(), fieldItem.getField("groups"));
                selectionList.add(fieldItem.getId());
            }
        }

        // Setup the im fields command to get the global definition of the field
        Command imFields = new Command(Command.IM, "fields");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (int i = 0; i < Integrity.fieldAttributes.length; i++) {
            mv.add(Integrity.fieldAttributes[i]);
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
                    System.out.println("\t\tAnalyzing visible field: " + fieldName);
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
                List<String> groups = new ArrayList<String>();
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
                    for (Iterator<String> it = overrideFieldList.iterator(); it.hasNext();) {
                        String orFieldAttribute = it.next();
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

    public WorkItemIterator getQueries() throws APIException {
        Command imQueries = new Command(Command.IM, "queries");
        // Construct the --fields=value,value,value option
        MultiValue mv = new MultiValue(",");
        for (int i = 0; i < Integrity.queryAttributes.length; i++) {
            mv.add(Integrity.queryAttributes[i]);
        }
        imQueries.addOption(new Option("fields", mv));
        return api.runCommandWithInterim(imQueries).getWorkItems();
    }

    public WorkItemIterator viewQueries(List<String> queryList) throws APIException {
        Command imViewQuery = new Command(Command.IM, "viewquery");
        // Add each query selection to the view query command
        for (Iterator<String> it = queryList.iterator(); it.hasNext();) {
            imViewQuery.addSelection(it.next());
        }
        return api.runCommandWithInterim(imViewQuery).getWorkItems();
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
            System.out.println("Failed to export viewset " + viewset);
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
        List<String> stagingSystems = new ArrayList<String>();
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
        List<WorkItem> stageList = new ArrayList<WorkItem>();
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
        List<WorkItem> deployTargetList = new ArrayList<WorkItem>();
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
