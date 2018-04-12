package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.type.FieldRelationships;
import com.ptc.services.utilities.docgen.type.VisibleFields;
import com.ptc.services.utilities.docgen.type.TypeProperties;
import com.ptc.services.utilities.docgen.type.StateTransitions;
import com.ptc.services.utilities.docgen.type.MandatoryFields;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mks.api.Command;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import com.mks.api.im.IMModelTypeName;
import com.ptc.services.utilities.XMLPrettyPrinter;
import com.ptc.services.utilities.docgen.models.relationship.RelationshipModel;
import com.ptc.services.utilities.docgen.models.workflow.WorkflowModel;
import java.util.LinkedHashMap;

public class IntegrityType extends IntegrityAdminObject {

    public static final String TEMPLATES_DIR = "data/im/issue/templates/";
    public static final String XML_PREFIX = "TYPE_";
    public static final String IPT_LBL_PREFIX = "IPT_LABEL_";
    public static final String IPT_TAB_PREFIX = "IPT_TAB_";
    private Integrity i;
    private Hashtable<String, Field> iType;
    private String mandatoryFields;
    private String visibleFields;
    private String stateTransitions;
    private String fieldRelationships;
    private String typeProperties;
    private Hashtable<String, List<String>> relationshipFields;
    private Hashtable<String, IntegrityField> fieldsHash;
    private LinkedHashMap<String, IntegrityField> allFieldsHash;
    private Hashtable<String, IntegrityState> statesHash;

    public static final String getXMLGenericPolicy(String genericPolicy) {
        String xmlParamPolicy = genericPolicy;
        if (xmlParamPolicy.indexOf('=') > 0) {
            String[] tokens = xmlParamPolicy.split("=");
            if (tokens[0].equalsIgnoreCase("userField")) {
                String userParam = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(tokens[1]);
                XMLWriter.paramsHash.put(userParam, tokens[1]);
                xmlParamPolicy = tokens[0] + '=' + XMLWriter.padXMLParamName(userParam);
            } else if (tokens[0].equalsIgnoreCase("groupField")) {
                String groupParam = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(tokens[1]);
                XMLWriter.paramsHash.put(groupParam, tokens[1]);
                xmlParamPolicy = tokens[0] + '=' + XMLWriter.padXMLParamName(groupParam);
            } else if (tokens[0].equalsIgnoreCase("groups")) {
                if (tokens[1].indexOf(',') > 0) {
                    String[] groups = tokens[1].split(",");
                    StringBuilder sb = new StringBuilder(tokens[0]);
                    sb.append("=");
                    for (int i = 0; i < groups.length; i++) {
                        String groupParam = Integrity.GROUP_XML_PREFIX + XMLWriter.getXMLParamName(groups[i].trim());
                        XMLWriter.paramsHash.put(groupParam, groups[i].trim());
                        sb.append(XMLWriter.padXMLParamName(groupParam));
                        sb.append(i < groups.length - 1 ? "," : "");
                    }
                } else {
                    String groupParam = Integrity.GROUP_XML_PREFIX + XMLWriter.getXMLParamName(tokens[1]);
                    XMLWriter.paramsHash.put(groupParam, tokens[1]);
                    xmlParamPolicy = tokens[0] + '=' + XMLWriter.padXMLParamName(groupParam);
                }
            } else {
                System.out.println("WARNING: Unknown policy encountered: " + tokens[0]);
            }
        }
        return xmlParamPolicy;
    }

    public IntegrityType(Integrity i, Hashtable<String, Field> typeDetails, boolean doXML) {
        this.i = i;
        modelType = IMModelTypeName.TYPE;
        iType = typeDetails;
        name = Integrity.getStringFieldValue(iType.get("name"));
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
        relationshipFields = new Hashtable<>();
        allFieldsHash = new LinkedHashMap<>();
        fieldsHash = new Hashtable<>();
        statesHash = new Hashtable<>();
        directory = "Types";

        try {
            // Parsing for Mandatory Fields
            System.out.print("\tParsing... mandatoryFields... ");
            MandatoryFields mf = new MandatoryFields(iType.get("mandatoryFields"));
            mandatoryFields = (doXML ? mf.getStringMandatoryFields() : mf.getFormattedReport());
            System.out.println("done.");

            // Parsing for Visible Fields
            System.out.println("\tParsing... visibleFields... ");
            VisibleFields vf = new VisibleFields(getName(), i, iType.get("visibleFields"), iType.get("visibleFieldsForMe"));
            visibleFields = (doXML ? vf.getStringVisibleFields() : vf.getFormattedReport());
            fieldsHash = vf.getList();
            System.out.println("\tParsing... visibleFields... done.");

            // Generate Relationship diagrams
            if (!doXML) {
                System.out.print("\tParsing... relationshipFields... ");
                relationshipFields = vf.getRelationshipFields();
                RelationshipModel rm = new RelationshipModel();
                rm.display(this, relationshipFields);
                System.out.println("done.");
            }

            // Parsing for State Transitions
            System.out.println("\tParsing... stateTransitions... ");
            if (!doXML) {
                // Generate Workflow diagrams
                WorkflowModel wm = new WorkflowModel();
                wm.display(this, iType.get("stateTransitions"));
            }
            StateTransitions st = new StateTransitions(getName(), i, iType.get("stateTransitions"));
            stateTransitions = (doXML ? st.getStringTransitions() : st.getFormattedReport());
            statesHash = (doXML ? st.getList() : statesHash);
            System.out.println("\tParsing... stateTransitions... done.");

            // Parsing for Field Relationships
            System.out.print("\tParsing... fieldRelationships... ");
            FieldRelationships fr = new FieldRelationships(fieldsHash, iType.get("fieldRelationships"));
            fieldRelationships = (doXML ? fr.getStringFieldRelationships() : fr.getFormattedReport());
            System.out.println("done.");

            // Parsing for Type Properties
            System.out.print("\tParsing... properties... ");
            TypeProperties tp = new TypeProperties(iType.get("properties"));
            typeProperties = (doXML ? tp.getStringTypeProperties() : tp.getFormattedReport());
            System.out.println("done.");
        } catch (ItemNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private String getItemListValues(String fieldName, String delimiter, boolean fullNames) {
        StringBuilder values = new StringBuilder();
        Field itemListField = iType.get(fieldName);
        if (null != itemListField && null != itemListField.getDataType() && itemListField.getDataType().equals(Field.ITEM_LIST_TYPE)) {
            List<Item> fieldsList = itemListField.getList();
            for (Iterator<Item> it = fieldsList.iterator(); it.hasNext();) {
                Item valueItem = it.next();
                if (fullNames && valueItem.getModelType().equals(IMModelTypeName.USER)) {
                    values.append(Integrity.getUserFullName(valueItem));
                } else {
                    values.append(valueItem.getId());
                }
                // Append the delimiter, if there are more values
                values.append(it.hasNext() ? delimiter : "");
            }
        }

        return values.toString();
    }

    @Override
    public Element getXML(Document job, Element command) {
        // Add this type to the global resources hash
        String typeXMLName = XML_PREFIX + XMLWriter.getXMLParamName(name);
        XMLWriter.paramsHash.put(typeXMLName, name);

        // Setup the command to re-create the type via the Load Test Harness...
        Element app = job.createElement("app");
        app.appendChild(job.createTextNode(Command.IM));
        command.appendChild(app);

        Element cmdName = job.createElement("commandName");
        cmdName.appendChild(isTestResult() ? job.createTextNode("edittype") : job.createTextNode("createtype"));
        command.appendChild(cmdName);

        // Currently there is no support for the following in the API (i.e. to read the values):
        // TODO: --addWordTemplate=name=templateName,path=templatePath[,description=description][,defaultEdit]  Adds a Word template to this type
        // TODO: --image=[none|<path>]  Type image for GUI
        // TODO: --printReport=report  The report used to print this document
        // --testResult  This type is used for test result only.
        if (isTestResult()) {
            command.appendChild(XMLWriter.getOption(job, "testResult", null));
        }

        // --addLabelRule=See documentation.  Add Label rule.
        if (getAddLabel().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "addLabelRule", getAddLabel()));
        }

        // --[no]allowChangePackages  Permit association of change packages with type
        if (getAllowChangePackages()) {
            command.appendChild(XMLWriter.getOption(job, "allowChangePackages", null));
        }

        // --associatedType=type  If document Class is Segment, node type; if Node, shared item type.
        if (getTypeClass().equalsIgnoreCase("segment") || getTypeClass().equalsIgnoreCase("node")) {
            String typeParam = XML_PREFIX + XMLWriter.getXMLParamName(getAssociatedType());
            XMLWriter.paramsHash.put(typeParam, getAssociatedType());
            command.appendChild(XMLWriter.getOption(job, "associatedType", XMLWriter.padXMLParamName(typeParam)));
        }

        // --branchRule=See documentation.  Branch rule.
        if (getBranch().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "branchRule", getBranch()));
        }

        // --copyFields=field,field,...  Copy field list for documents
        String copyFields = Integrity.getXMLParamFieldValue(iType.get("copyFields"), IntegrityField.XML_PREFIX, ",");
        if (copyFields.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "copyFields", copyFields));
        }

        // --copyTreeRule=See documentation.  Copy Tree rule.
        if (getCopyTree().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "copyTreeRule", getCopyTree()));
        }

        // --createCPPolicy=value  Set the Change Package creation policy
        if (getCreateCPPolicy().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "createCPPolicy", getXMLGenericPolicy(getCreateCPPolicy())));
        }

        // --defaultReferenceMode=[Reuse|Share]  Default Reference mode
        if (getDefaultReferenceMode().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "defaultReferenceMode", getDefaultReferenceMode()));
        }

        // --deleteItemRule=See documentation.  Delete Item rule.
        if (getDeleteItem().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "deleteItemRule", getDeleteItem()));
        }

        // --deleteLabelRule=See documentation.  Delete Label rule.
        if (getDeleteLabel().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "deleteLabelRule", getDeleteLabel()));
        }

        // --documentClass=[none|segment|node|shareditem]  If part of a document, set its class.
        if (!getTypeClass().equalsIgnoreCase("none")) {
            command.appendChild(XMLWriter.getOption(job, "documentClass", getTypeClass()));
        }

        // --editabilityRule=See documentation.  Issue editability rule.\
        if (getIssueEditability().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "editabilityRule", getIssueEditability()));
        }

        // --editPresentation[=value]  The presentation template to use to edit this type (default: default)
        String editIPT = getEditPresentation();
        editIPT = editIPT.equals("default") ? editIPT : XMLWriter.padXMLParamName(typeXMLName + "_EDIT_IPT");
        if (!editIPT.equals("default")) {
            XMLWriter.paramsHash.put(typeXMLName + "_EDIT_IPT", getEditPresentation());
        }
        command.appendChild(XMLWriter.getOption(job, "editPresentation", editIPT));

        // --[no]enableBranch  Enable this type to be branched
        if (getBranchEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableBranch", null));
        }

        // --[no]enableCopyTree  Enable this type for copying
        if (getCopyTreeEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableCopyTree", null));
        }

        // --[no]enableDeleteItem  Enable this type to permit delete item.
        if (getDeleteItemEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableDeleteItem", null));
        }

        // --[no]enableLabel  Enable this type to permit Labels
        if (getLabelEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableLabel", null));
        }

        // --[no]enableProjectBacking  Allow issues of this type to back Projects
        if (getBacksProject()) {
            command.appendChild(XMLWriter.getOption(job, "enableProjectBacking", null));
        }

        // --[no]enableTestResultRelationship  Items of this type can be related to test results.
        if (getCanRelateToTestResult()) {
            command.appendChild(XMLWriter.getOption(job, "enableTestResultRelationship", null));
        }

        // --[no]enableTestSteps  Enable this type to use test steps.
        if (getTestStepsEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableTestSteps", null));
        }

        // --[no]enableTimeTracking  Enable this type for Time Tracking
        if (getTimeTrackingEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableTimeTracking", null));
        }

        // --fieldRelationships=sourcefield=value,value,...:targetfield=value,value,...;sourcefield=value...
        if (getFieldRelationships().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "fieldRelationships", getFieldRelationships()));
        }

        // --[no]groupDocument  Mark as group document
        if (getGroupDocument()) {
            command.appendChild(XMLWriter.getOption(job, "groupDocument", null));
        }

        // --mandatoryFields=state:field,field,...[;...]  Mandatory field specification
        if (getMandatoryFields().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "mandatoryFields", getMandatoryFields()));
        }

        // --modifyTestResultPolicy=value  Set the test result modification policy.
        if (getModifyTestResultPolicy().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "modifyTestResultPolicy", getXMLGenericPolicy(getModifyTestResultPolicy())));
        }

        // --notificationFields=field,field,...  Notification field specification
        String notificationFields = Integrity.getXMLParamFieldValue(iType.get("notificationFields"), IntegrityField.XML_PREFIX, ",");
        if (notificationFields.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "notificationFields", notificationFields));
        }

        // --permittedAdministrators=u=user1,user2,.. ;g=group1,group2,..   Type administrator permissions
        String permittedAdministrators = getPermittedAdministrators(",");
        if (permittedAdministrators.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "permittedAdministrators", permittedAdministrators));
        }

        // --permittedGroups=group,group,...  Groups with visibility to this type
        String permittedGroups = Integrity.getXMLParamFieldValue(iType.get("permittedGroups"), Integrity.GROUP_XML_PREFIX, ",");
        if (permittedGroups.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "permittedGroups", permittedGroups));
        }

        // --phaseField=field  Phase Field for Workflow display
        if (getPhaseField().length() > 0) {
            String phaseField = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(getPhaseField());
            XMLWriter.paramsHash.put(phaseField, getPhaseField());
            command.appendChild(XMLWriter.getOption(job, "phaseField", XMLWriter.padXMLParamName(phaseField)));
        }

        // --printPresentation[=value]  The presentation template to use to print this type (default: defaultprint)
        String printIPT = getPrintPresentation();
        printIPT = printIPT.equals("defaultprint") ? printIPT : XMLWriter.padXMLParamName(typeXMLName + "_PRINT_IPT");
        if (!printIPT.equals("defaultprint")) {
            XMLWriter.paramsHash.put(typeXMLName + "_PRINT_IPT", getPrintPresentation());
        }
        command.appendChild(XMLWriter.getOption(job, "printPresentation", printIPT));

        // --properties=name:value:description[;...]  The properties for this type
        if (getTypeProperties().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "properties", getTypeProperties()));
        }

        // --[no]showWorkflow  Display Workflow in Issue
        if (getShowWorkflow()) {
            command.appendChild(XMLWriter.getOption(job, "showWorkflow", null));
        }

        // --significantFields=field,field,...  Significant edit fields for documents
        String significantFields = Integrity.getXMLParamFieldValue(iType.get("significantFields"), IntegrityField.XML_PREFIX, ",");
        if (significantFields.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "significantFields", significantFields));
        }

        // --stateTransitions=state:state:group|dynamic group,group|dynamic group,...[;...]  State transition specification
        if (getStateTransitions().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "stateTransitions", getStateTransitions()));
        }

        // --testCaseResultFields=field,field,...  Which test case fields appear in the test result detail view
        String testCaseResultFields = Integrity.getXMLParamFieldValue(iType.get("testCaseResultFields"), IntegrityField.XML_PREFIX, ",");
        if (testCaseResultFields.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "testCaseResultFields", testCaseResultFields));
        }

        // --testRole=[none|testSession|testCase|testStep|testSuite]  Set the test role for this type.
        if (!getTestRole().equalsIgnoreCase("none")) {
            command.appendChild(XMLWriter.getOption(job, "testRole", getTestRole()));
        }

        // --viewPresentation[=value]  The presentation template to use to view this type (default: default)
        String viewIPT = getViewPresentation();
        viewIPT = viewIPT.equals("default") ? viewIPT : XMLWriter.padXMLParamName(typeXMLName + "_VIEW_IPT");
        if (!viewIPT.equals("default")) {
            XMLWriter.paramsHash.put(typeXMLName + "_VIEW_IPT", getViewPresentation());
        }
        command.appendChild(XMLWriter.getOption(job, "viewPresentation", viewIPT));

        // --visibleFields=field:group,group,...[;...]  Field visibility permissions
        if (getVisibleFields().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "visibleFields", getVisibleFields()));
        }

        // --description=value  Short description
        if (getDescription().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "description", getDescription()));
        }

        // --[no]enableRevision  Enable this type to permit revisioning
        if (getRevisionEnabled()) {
            command.appendChild(XMLWriter.getOption(job, "enableRevision", null));
        }

        // --majorRevisionRule=See documentation
        if (getMajorRevision().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "majorRevisionRule", getMajorRevision()));
        }

        // --minorRevisionRule=See documentation
        if (getMinorRevision().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "minorRevisionRule", getMinorRevision()));
        }

        // --[no]duplicateDetectionMandatory  Make it mandatory for users to view potential duplicates before they create a new item.
        if (getDuplicateDetectionMandatory()) {
            command.appendChild(XMLWriter.getOption(job, "duplicateDetectionMandatory", null));
        }

        // --duplicateDetectionSearchField=field  The field to search when using duplicate detection. Setting an empty field will disable duplicate detection.
        String dupSearchField = Integrity.getXMLParamFieldValue(iType.get("duplicateDetectionSearchField"), IntegrityField.XML_PREFIX, "");
        if (getDuplicateDetectionSearchField().length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "duplicateDetectionSearchField", dupSearchField));
        }

        // --name=value  The name for this object
        if (isTestResult()) {
            Element selection = job.createElement("selection");
            selection.appendChild(job.createTextNode(xmlParamName));
            command.appendChild(selection);
        } else {
            command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
        }

        return command;
    }

    // Returns a field name based on its field ID
    private String getFieldName(String fieldID) {
        String fieldName = fieldID;
        for (String field : allFieldsHash.keySet()) {
            IntegrityField iField = allFieldsHash.get(field);
            if (iField.getID().equals(fieldID)) {
                fieldName = iField.getName();
                break;
            }
        }
        return fieldName;
    }

    // Converts the field id to field name for exporting presentation templates
    private void normalizeElement(Node node) throws MalformedURLException {
        NodeList nodeList = node.getChildNodes();
        for (int j = 0; j < nodeList.getLength(); j++) {
            Node currentNode = nodeList.item(j);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                // Check to see if this node contains any attributes that we need to fix
                Element e = (Element) currentNode;

                // First process the overridden field label names
                if (e.getNodeName().equalsIgnoreCase("FieldLabel") && e.hasAttribute("text") && e.getAttribute("text").length() > 0) {
                    String fieldLabelOverride = e.getAttribute("text");
                    String fieldName = e.hasAttribute("fieldID") ? getFieldName(e.getAttribute("fieldID")) : fieldLabelOverride;
                    fieldName = IPT_LBL_PREFIX + XMLWriter.getXMLParamName(fieldName);
                    // Write this mapping to the global resources properties
                    XMLWriter.paramsHash.put(fieldName, fieldLabelOverride);
                    e.setAttribute("text", XMLWriter.padXMLParamName(fieldName));
                }

                if (e.getNodeName().equalsIgnoreCase("Tab") && e.hasAttribute("name") && e.getAttribute("name").length() > 0) {
                    String tabName = e.getAttribute("name");
                    tabName = IPT_TAB_PREFIX + XMLWriter.getXMLParamName(tabName);
                    // Write this mapping to the global resources properties
                    XMLWriter.paramsHash.put(tabName, e.getAttribute("name"));
                    e.setAttribute("name", XMLWriter.padXMLParamName(tabName));
                }

                if (e.getNodeName().equalsIgnoreCase("Label") && e.hasAttribute("textStyle") && e.hasAttribute("text")) {
                    String labelName = e.getAttribute("text");
                    if (e.getAttribute("textStyle").equalsIgnoreCase("heading")) {
                        labelName = IPT_LBL_PREFIX + XMLWriter.getXMLParamName(labelName);
                        // Write this mapping to the global resources properties
                        XMLWriter.paramsHash.put(labelName, e.getAttribute("text"));
                        e.setAttribute("text", XMLWriter.padXMLParamName(labelName));
                    }
                }

                if (e.hasAttribute("fieldID")) {
                    String fieldName = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(getFieldName(e.getAttribute("fieldID")));
                    // This field mapping should already be written to the global resources properties
                    e.setAttribute("fieldID", XMLWriter.padXMLParamName(fieldName));
                }

                if (e.hasAttribute("visibleFieldID")) {
                    String fieldName = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(getFieldName(e.getAttribute("visibleFieldID")));
                    // This field mapping should already be written to the global resources properties
                    e.setAttribute("visibleFieldID", XMLWriter.padXMLParamName(fieldName));
                }

                if (e.hasAttribute("href")) {
                    URL url = new URL(e.getAttribute("href"));
                    if (url.getHost().equalsIgnoreCase(i.getHostName())) {
                        String urlRef = e.getAttribute("href").replaceFirst(url.getHost(), "TEMPLATE_BUILDER_HOSTNAME");
                        urlRef = urlRef.replaceFirst(String.valueOf(url.getPort()), "TEMPLATE_BUILDER_PORT");
                        e.setAttribute("href", urlRef);
                    }
                }

                // Recursively process all the children
                normalizeElement(currentNode);
            }
        }
    }

    // Export Presentation Template
    public void exportPresentation(String template, File exportDir, LinkedHashMap<String, IntegrityField> sysFieldsHash) throws APIException, ParserConfigurationException, SAXException, IOException {
        // Initialize the global list of fields, so we can resolve field IDs that may not be visible to the Type
        allFieldsHash = sysFieldsHash;
        // Construct the db file path for the presentation template
        String file = (template.indexOf(".xml") > 0 ? TEMPLATES_DIR + template : TEMPLATES_DIR + template + ".xml");
        // Export the presentation template to disk
        File presTemplateFile = i.getDBFile(file, exportDir);
        // Create the Document Builder Factory
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        // Create the xmlBuilder
        DocumentBuilder xmlBuilder = domFactory.newDocumentBuilder();
        // Parse the presentation template xml file
        Document xmlDoc = xmlBuilder.parse(presTemplateFile);
        // Get the root element
        normalizeElement(xmlDoc.getDocumentElement());
        // Write out the fixed up presentation template
        XMLPrettyPrinter.serialize(new File(IntegrityDocs.XML_CONTENT_DIR.getAbsolutePath(), "templates" + IntegrityDocs.fs + presTemplateFile.getName()), xmlDoc, false);
        // Clean up the working copy
        presTemplateFile.delete();
    }

    // All access functions....
    public String getCreatedDate(SimpleDateFormat dateFormat) {
        return Integrity.getDateString(dateFormat, iType.get("created").getDateTime());
    }

    public String getCreatedBy() {
        return Integrity.getUserFullName(iType.get("createdBy").getItem());
    }

    public String getModifiedDate(SimpleDateFormat dateFormat) {
        return Integrity.getDateString(dateFormat, iType.get("lastModified").getDateTime());
    }

    public String getModifiedBy() {
        return Integrity.getUserFullName(iType.get("modifiedBy").getItem());
    }

    public String getPosition() {
        return iType.get("position").getInteger().toString();
    }

    // name
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getXMLName() {
        return xmlParamName;
    }

    // description
    @Override
    public String getDescription() {
        return Integrity.getStringFieldValue(iType.get("description"));
    }

    // allowChangePackages
    public boolean getAllowChangePackages() {
        return Integrity.getBooleanFieldValue(iType.get("allowChangePackages"));
    }

    // createCPPolicy
    public String getCreateCPPolicy() {
        return Integrity.getStringFieldValue(iType.get("createCPPolicy"));
    }

    // mandatoryFields
    public String getMandatoryFields() {
        return mandatoryFields;
    }

    // visibleFields
    public String getVisibleFields() {
        return visibleFields;
    }

    public Hashtable<String, List<String>> getRelationshipFields() {
        return relationshipFields;
    }

    public Hashtable<String, IntegrityField> getFields() {
        return fieldsHash;
    }

    // stateTransitions
    public String getStateTransitions() {
        return stateTransitions;
    }

    public Hashtable<String, IntegrityState> getStates() {
        return statesHash;
    }

    // fieldRelationships
    public String getFieldRelationships() {
        return fieldRelationships;
    }

    // notificationFields
    public String getNotificationFields() {
        return getItemListValues("notificationFields", "<br/>" + IntegrityDocs.nl, false);
    }

    // permittedGroups
    public String getPermittedGroups() {
        return getItemListValues("permittedGroups", "<br>" + IntegrityDocs.nl, false);
    }

    // permittedAdministrators
    public String getPermittedAdministrators() {
        return Integrity.summarizePermissionsList(iType.get("permittedAdministrators"), "<br/>" + IntegrityDocs.nl);
    }

    public String getPermittedAdministrators(String delimiter) {
        return Integrity.summarizePermissionsList(iType.get("permittedAdministrators"), delimiter);
    }

    // viewPresentation
    public String getViewPresentation() {
        return Integrity.getStringFieldValue(iType.get("viewPresentation"));
    }

    // editPresentation
    public String getEditPresentation() {
        return Integrity.getStringFieldValue(iType.get("editPresentation"));
    }

    // printPresentation
    public String getPrintPresentation() {
        return Integrity.getStringFieldValue(iType.get("printPresentation"));
    }

    public List<String> getUniquePresentations() {
        List<String> uniquePresentationsList = new ArrayList<String>();
        if (!uniquePresentationsList.contains(getViewPresentation())) {
            uniquePresentationsList.add(getViewPresentation());
        }
        if (!uniquePresentationsList.contains(getEditPresentation())) {
            uniquePresentationsList.add(getEditPresentation());
        }
        if (!uniquePresentationsList.contains(getPrintPresentation())) {
            uniquePresentationsList.add(getPrintPresentation());
        }
        return uniquePresentationsList;
    }

    // showWorkflow
    public boolean getShowWorkflow() {
        return Integrity.getBooleanFieldValue(iType.get("showWorkflow"));
    }

    // phaseField
    public String getPhaseField() {
        return Integrity.getStringFieldValue(iType.get("phaseField"));
    }

    // timeTrackingEnabled
    public boolean getTimeTrackingEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("timeTrackingEnabled"));
    }

    // copytreeEnabled
    public boolean getCopyTreeEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("copytreeEnabled"));
    }

    // branchEnabled
    public boolean getBranchEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("branchEnabled"));
    }

    // labelEnabled
    public boolean getLabelEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("labelEnabled"));
    }

    // deleteItemEnabled
    public boolean getDeleteItemEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("deleteItemEnabled"));
    }

    // backsProject
    public boolean getBacksProject() {
        return Integrity.getBooleanFieldValue(iType.get("backsProject"));
    }

    // issueEditability
    public String getIssueEditability() {
        return Integrity.getStringFieldValue(iType.get("issueEditability"));
    }

    // addLabel
    public String getAddLabel() {
        return Integrity.getStringFieldValue(iType.get("addLabel"));
    }

    // deleteLabel
    public String getDeleteLabel() {
        return Integrity.getStringFieldValue(iType.get("deleteLabel"));
    }

    // copyTree
    public String getCopyTree() {
        return Integrity.getStringFieldValue(iType.get("copyTree"));
    }

    // branch
    public String getBranch() {
        return Integrity.getStringFieldValue(iType.get("branch"));
    }

    // deleteItem
    public String getDeleteItem() {
        return Integrity.getStringFieldValue(iType.get("deleteItem"));
    }

    // modifyTestResultPolicy
    public String getModifyTestResultPolicy() {
        return Integrity.getStringFieldValue(iType.get("modifyTestResultPolicy"));
    }

    // testRole
    public String getTestRole() {
        return Integrity.getStringFieldValue(iType.get("testRole"));
    }

    // typeClass
    public String getTypeClass() {
        return null == iType.get("typeClass").getValueAsString() ? "none" : iType.get("typeClass").getValueAsString();
    }

    // associatedType
    public String getAssociatedType() {
        return Integrity.getFieldValue(iType.get("associatedType"), "");
    }

    // significantEdit
    public String getSignificantEdit() {
        return getItemListValues("significantEdit", "<br>" + IntegrityDocs.nl, false);
    }

    public String getSignificantEdit(String delimiter) {
        return getItemListValues("significantEdit", delimiter, false);
    }

    // canRelateToTestResult
    public boolean getCanRelateToTestResult() {
        return Integrity.getBooleanFieldValue(iType.get("canRelateToTestResult"));
    }

    // defaultReferenceMode
    public String getDefaultReferenceMode() {
        return getTypeClass().equalsIgnoreCase("segment") ? iType.get("defaultReferenceMode").getValueAsString() : "";
    }

    // copyFields
    public String getCopyFields() {
        return getItemListValues("copyFields", "<br>" + IntegrityDocs.nl, false);
    }

    // testCaseResultFields
    public String getTestCaseResultFields() {
        return getItemListValues("testCaseResultFields", "<br>" + IntegrityDocs.nl, false);
    }

    public String getTestCaseResultFields(String delimiter) {
        return getItemListValues("testCaseResultFields", delimiter, false);
    }

    // testStepsEnabled
    public boolean getTestStepsEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("testStepsEnabled"));
    }

    // groupDocument
    public boolean getGroupDocument() {
        return Integrity.getBooleanFieldValue(iType.get("groupDocument"));
    }

    // isTestResult
    public boolean isTestResult() {
        return Integrity.getBooleanFieldValue(iType.get("isTestResult"));
    }

    // properties
    public String getTypeProperties() {
        return typeProperties;
    }

    // revisionEnabled
    public boolean getRevisionEnabled() {
        return Integrity.getBooleanFieldValue(iType.get("revisionEnabled"));
    }

    // majorRevision
    public String getMajorRevision() {
        return Integrity.getStringFieldValue(iType.get("majorRevision"));
    }

    // minorRevision
    public String getMinorRevision() {
        return Integrity.getStringFieldValue(iType.get("minorRevision"));
    }

    // duplicateDetectionMandatory
    public boolean getDuplicateDetectionMandatory() {
        return Integrity.getBooleanFieldValue(iType.get("duplicateDetectionMandatory"));
    }

    // duplicateDetectionSearchField
    public String getDuplicateDetectionSearchField() {
        return Integrity.getStringFieldValue(iType.get("duplicateDetectionSearchField"));
    }

    // Model Type
    @Override
    public String getModelType() {
        return modelType;
    }

    public String getDirectory() {
        return directory;
    }
}
