package com.ptc.services.utilities.docgen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mks.api.Command;
import com.ptc.services.utilities.IterableNodeList;
import com.ptc.services.utilities.XMLPrettyPrinter;
import com.ptc.services.utilities.XMLUtils;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_XML_VIEWSETS_DIR;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;

import java.util.LinkedHashMap;

/**
 * The Query class contains the following information about an Integrity Query:
 * publishedState name creator modifiedDate description mandatory customizable
 *
 * Note: We're only interested in the published server viewsets either for
 * reporting purposes or xml export!
 */
public class Viewset extends IntegrityAdminObject {

    // Viewset's members
    public static final String XML_PREFIX = "VIEWSET_";
    private String publishedState;
    private String creator;
    private Date modifiedDate;
    private boolean mandatory;
    private boolean customizable;
    private Document vsDoc;
    public static final List<String> adminRefs = new ArrayList<>();

    static {
        adminRefs.add("CIQuery:");
        adminRefs.add("CIChart:");
        adminRefs.add("CIRelationshipField:");
        adminRefs.add("CIQBRField:");
        adminRefs.add("CIType:");
    }

    public Viewset(String id) throws ParserConfigurationException {
        this.id = id;
        publishedState = "Published (Server)";
        creator = "";
        modifiedDate = new Date();
        description = "";
        mandatory = false;
        customizable = true;
        name = "";
        vsDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
        objectType = Types.Viewset;
    }

    // All setter functions
    public void setPublishedState(String publishedState) {
        this.publishedState = publishedState;
    }

    public void setCreatedBy(String createdBy) {
        this.creator = createdBy;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setCustomizable(boolean customizable) {
        this.customizable = customizable;
    }

    public void setName(String name) {
        this.name = name;
        this.xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
    }

    public void setViewsetLayout(LinkedHashMap<String, IntegrityField> fieldsHash,
            LinkedHashMap<String, String> typeIDs,
            LinkedHashMap<String, String> queryIDs,
            LinkedHashMap<String, String> chartIDs, Document vsDoc) throws XPathExpressionException {
        // Lets clean up the raw viewset definition and sanitize it for a clean import
        XPath xp = XPathFactory.newInstance().newXPath();
        // Remove <MRUEntries>
        NodeList nlMRU = vsDoc.getElementsByTagName("MRUEntries");
        for (Node n : new IterableNodeList(nlMRU)) {
            XMLUtils.removeAllChildren(n);
        }
        // Remove <Setting name="server.hostname">localhost</Setting>
        XMLUtils.clearSetting(vsDoc, xp, "server.hostname");
        // Remove <Setting name="server.port">7001</Setting>
        XMLUtils.clearSetting(vsDoc, xp, "server.port");
        // Remove <Setting name="server.user">administrator</Setting>
        XMLUtils.clearSetting(vsDoc, xp, "server.user");
        //<Property key="Checksum">261888610232387726792064694740222194243</Property>
        NodeList nlChk = (NodeList) xp.evaluate("//Property[@key='Checksum']", vsDoc, XPathConstants.NODESET);
        for (Node n : new IterableNodeList(nlChk)) {
            n.getParentNode().removeChild(n);
        }

        // Substitute field IDs for their corresponding parameterized field names
        normalizeElement(fieldsHash, typeIDs, queryIDs, chartIDs, vsDoc);
        vsDoc.normalize();

        // Re-initialize our local document object
        this.vsDoc = vsDoc;
    }

    // Substitutes the name equivalent for the field ID
    private void substituteIDForName(LinkedHashMap<String, IntegrityField> fieldsHash, Node node) {
        String fieldID = node.getFirstChild().getNodeValue();
        if (Integer.parseInt(fieldID) > -100) {
            String fieldName = getFieldName(fieldsHash, fieldID);
            if (fieldID != fieldName) {
                String xmlParamName = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(fieldName);
                XMLWriter.paramsHash.put(xmlParamName, fieldName);
                node.getFirstChild().setNodeValue(XMLWriter.padXMLParamName(xmlParamName));
            }
        }
    }

    // Provides the parameterized name string for the supplied admin object ID reference
    private String getParamNameForID(LinkedHashMap<String, IntegrityField> fieldsHash,
            LinkedHashMap<String, String> typeIDs,
            LinkedHashMap<String, String> queryIDs,
            LinkedHashMap<String, String> chartIDs,
            String adminID,
            String adminClass) {
        String xmlParamName = adminID;
        // Dont bother swapping system fields
        if (Integer.parseInt(adminID) > -100) {
            if (adminClass.equalsIgnoreCase("CIQuery:")) {
                String name = queryIDs.get(adminID);
                if (null != name && name.length() > 0) {
                    xmlParamName = Query.XML_PREFIX + XMLWriter.getXMLParamName(name);
                    XMLWriter.paramsHash.put(xmlParamName, name);
                    xmlParamName = XMLWriter.padXMLParamName(xmlParamName);
                }
            } else if (adminClass.equalsIgnoreCase("CIChart:")) {
                String name = chartIDs.get(adminID);
                if (null != name && name.length() > 0) {
                    xmlParamName = Chart.XML_PREFIX + XMLWriter.getXMLParamName(name);
                    XMLWriter.paramsHash.put(xmlParamName, name);
                    xmlParamName = XMLWriter.padXMLParamName(xmlParamName);
                }

            } else if (adminClass.equalsIgnoreCase("CIType:")) {
                String name = typeIDs.get(adminID);
                if (null != name && name.length() > 0) {
                    xmlParamName = IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(name);
                    XMLWriter.paramsHash.put(xmlParamName, name);
                    xmlParamName = XMLWriter.padXMLParamName(xmlParamName);
                }
            } else if (adminClass.equalsIgnoreCase("CIRelationshipField:") || adminClass.equalsIgnoreCase("CIQBRField:")) {
                String name = getFieldName(fieldsHash, adminID);
                if (name != adminID) {
                    xmlParamName = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(name);
                    XMLWriter.paramsHash.put(xmlParamName, name);
                    xmlParamName = XMLWriter.padXMLParamName(xmlParamName);
                }
            }
        }
        return xmlParamName;
    }

    // Substitutes the name equivalent for the admin object ID
    private void substituteIDForName(LinkedHashMap<String, IntegrityField> fieldsHash,
            LinkedHashMap<String, String> typeIDs,
            LinkedHashMap<String, String> queryIDs,
            LinkedHashMap<String, String> chartIDs, Node node) {
        // Search and replace instances of
        //	CIQuery:<ID>
        //	CIChart:<ID>
        //	CIRelationshipField:<ID>
        //	CIQBRField:<ID>
        //	CIType:<ID>
        // with the equivalent names

        Element e = (Element) node;
        if (e.hasAttribute("id")) {
            StringBuilder sanitizedID = new StringBuilder(e.getAttribute("id"));
            for (String adminClass : adminRefs) {
                // Get the current state of the attribute value "id"
                String attributeValue = sanitizedID.toString();

                // Reset the sanitizedID to hold any fixes for the current admin class
                sanitizedID = new StringBuilder();

                // Look for the current admin reference in the attribute value
                int i = attributeValue.indexOf(adminClass);

                int startPos = i + adminClass.length();

                // Re-build the sanitizedID based on the current position
                sanitizedID.append(i >= 0 ? attributeValue.substring(0, startPos) : attributeValue);

                // Build the string for the admin ID
                StringBuilder adminID = new StringBuilder();
                while (i != -1 && startPos < attributeValue.length()) {
                    char nextChar = attributeValue.charAt(startPos);
                    if (Character.isDigit(nextChar) || nextChar == '-') {
                        adminID.append(nextChar);
                        startPos++;
                    } else {
                        // Swap the ID for name, if this is a positive ID
                        sanitizedID.append(getParamNameForID(fieldsHash, typeIDs, queryIDs, chartIDs, adminID.toString(), adminClass));

                        // Reset the admin ID
                        adminID = new StringBuilder();

                        // Continue the search for the current admin class in the attribute value
                        i = attributeValue.indexOf(adminClass, startPos);

                        // Continue the construction of the sanitizedID based on the new position
                        sanitizedID.append(i >= 0 ? attributeValue.substring(startPos, (i + adminClass.length())) : attributeValue.substring(startPos, attributeValue.length()));

                        // Update the start position based on the new find
                        startPos = i + adminClass.length();
                    }
                }

                if (adminID.length() > 0) {
                    // Swap the ID for name, if this is a positive ID
                    sanitizedID.append(getParamNameForID(fieldsHash, typeIDs, queryIDs, chartIDs, adminID.toString(), adminClass));
                }
            }

            // Finally update the id attribute
            e.setAttribute("id", sanitizedID.toString());
        }
    }

    // Substitutes the name with a parameterized name
    private void substituteParamNameForName(Node node, String xmlPrefix, String delimiter) {
        if (null != node.getFirstChild() && null != node.getFirstChild().getNodeValue()) {
            String paramValue = node.getFirstChild().getNodeValue();
            if (null != delimiter && delimiter.length() > 0) {
                StringTokenizer tokens = new StringTokenizer(paramValue, delimiter);
                StringBuilder sb = new StringBuilder();
                while (tokens.hasMoreTokens()) {
                    String paramToken = tokens.nextToken().trim();
                    String paramName = xmlPrefix + XMLWriter.getXMLParamName(paramToken);
                    XMLWriter.paramsHash.put(paramName, paramToken);
                    sb.append(XMLWriter.padXMLParamName(paramName));
                    sb.append(tokens.hasMoreTokens() ? delimiter : "");
                }

                if (sb.length() > 0) {
                    node.getFirstChild().setNodeValue(sb.toString());
                }
            } else {
                String paramName = xmlPrefix + XMLWriter.getXMLParamName(paramValue);
                XMLWriter.paramsHash.put(paramName, paramValue);
                node.getFirstChild().setNodeValue(XMLWriter.padXMLParamName(paramName));
            }
        }
    }

    private void substituteParamNameForPattern(Node node, String xmlPrefix) {
        // Only process if we've got something to resolve...
        if (null != node.getFirstChild() && null != node.getFirstChild().getNodeValue() && node.getFirstChild().getNodeValue().indexOf('{') != -1) {
            StringBuilder parameterizedString = new StringBuilder();
            String cdata = node.getFirstChild().getNodeValue();
            int startIndx = 0;
            int curIndx = cdata.indexOf("{", startIndx);
            while (curIndx >= 0) {
                if (curIndx > 0) {
                    parameterizedString.append(cdata.substring(startIndx, curIndx));
                }

                if (curIndx == (cdata.length() - 1)) {
                    parameterizedString.append("{");
                    startIndx = curIndx + 1;
                } else {
                    int endIndx = cdata.indexOf('}', curIndx);
                    if (endIndx < 0) {
                        // Matching closing token not found, parse error!
                        break;
                    }

                    // Parameterize the string found in {...}
                    String paramValue = cdata.substring(curIndx + 1, endIndx);
                    String paramName = xmlPrefix + XMLWriter.getXMLParamName(paramValue);
                    XMLWriter.paramsHash.put(paramName, paramValue);
                    parameterizedString.append('{' + XMLWriter.padXMLParamName(paramName) + '}');
                    startIndx = endIndx + 1;
                }

                curIndx = cdata.indexOf("{", startIndx);
            }

            if (startIndx < cdata.length()) {
                parameterizedString.append(cdata.substring(startIndx));
            }

            // Update the node with the parameterized string
            node.getFirstChild().setNodeValue(parameterizedString.toString());
        }
    }

    // Converts the field id to field name for exporting viewset xml layout files
    private void normalizeElement(LinkedHashMap<String, IntegrityField> fieldsHash,
            LinkedHashMap<String, String> typeIDs,
            LinkedHashMap<String, String> queryIDs,
            LinkedHashMap<String, String> chartIDs, Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int j = 0; j < nodeList.getLength(); j++) {
            Node currentNode = nodeList.item(j);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                // Check to see if this node contains text that we need to fix
                Element e = (Element) currentNode;
                if (e.getNodeName().equalsIgnoreCase("Property") && e.hasAttribute("key") && e.getAttribute("key").equalsIgnoreCase("SortField")) {
                    substituteIDForName(fieldsHash, e);
                } else if (e.getNodeName().equalsIgnoreCase("Field")) {
                    substituteIDForName(fieldsHash, e);
                } else if (e.getNodeName().equalsIgnoreCase("ColumnSetDef")) {
                    substituteIDForName(fieldsHash, typeIDs, queryIDs, chartIDs, currentNode);
                } // Process the <Setting> tags
                else if (e.getNodeName().equalsIgnoreCase("Setting") && e.hasAttribute("name")) {
                    String nameAttr = e.getAttribute("name");

                    // <Setting name="query">All Test Documents</Setting>
                    if (nameAttr.equalsIgnoreCase("query")) {
                        substituteParamNameForName(currentNode, Query.XML_PREFIX, "");
                    } // <Setting name="traverseFields">Plans,Organizes,Test Sessions,Tests</Setting>
                    else if (nameAttr.equalsIgnoreCase("traverseFields")) {
                        substituteParamNameForName(currentNode, IntegrityField.XML_PREFIX, ",");
                    } // <Setting name="tabTitle">{Type}: {ID}</Setting>
                    // <Setting name="structureFieldDisplayFormat">{ID} {Type}</Setting>
                    // <Setting name="title">{Type}: {ID}</Setting>
                    else if (nameAttr.equalsIgnoreCase("tabTitle") || nameAttr.equalsIgnoreCase("title")
                            || nameAttr.equalsIgnoreCase("structureFieldDisplayFormat")) {
                        substituteParamNameForPattern(currentNode, IntegrityField.XML_PREFIX);
                    }
                }

                // Recursively process all the children
                normalizeElement(fieldsHash, typeIDs, queryIDs, chartIDs, currentNode);
            }
        }
    }

    @Override
    public String getPosition() {
        return this.getID().replaceAll(" ", "_");
    }

    // Returns a field name based on its field ID
    private String getFieldName(LinkedHashMap<String, IntegrityField> fieldsHash, String fieldID) {
        String fieldName = fieldID;
        for (String field : fieldsHash.keySet()) {
            IntegrityField iField = fieldsHash.get(field);
            if (iField.getId().equals(fieldID)) {
                fieldName = iField.getName();
                break;
            }
        }
        return fieldName;
    }

    // All getter/access functions...
    @Override
    public Element getXML(Document job, Element command) {
        // Add this viewset to the global resources hash
        XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

        // Setup the command to re-create the query via the Load Test Harness...
        Element app = job.createElement("app");
        app.appendChild(job.createTextNode(Command.INTEGRITY));
        command.appendChild(app);

        Element cmdName = job.createElement("commandName");
        cmdName.appendChild(job.createTextNode("publishviewset"));
        command.appendChild(cmdName);

        // --canImport=value  Principals who can import the ViewSet
        // TODO: This is empty for now as the api doesn't provide this information
        command.appendChild(XMLWriter.getOption(job, "canImport", ""));

        // --canModify=value  Principals who can edit and publish the ViewSet
        // TODO: This is empty for noe as the api doesn't provide this information
        command.appendChild(XMLWriter.getOption(job, "canModify", ""));

        //--[no]customizable  Flag the ViewSet as customizable
        if (customizable) {
            command.appendChild(XMLWriter.getOption(job, "customizable", null));
        } else {
            command.appendChild(XMLWriter.getOption(job, "nocustomizable", null));
        }

        // --description=value  Description of the ViewSet
        if (description.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "description", description));
        }

        // --[no]mandatory  Flag the ViewSet as mandatory
        if (mandatory) {
            command.appendChild(XMLWriter.getOption(job, "mandatory", null));
        } else {
            command.appendChild(XMLWriter.getOption(job, "nomandatory", null));
        }

        // --[no|confirm]overwriteExisting  Overwrite existing server ViewSet
        command.appendChild(XMLWriter.getOption(job, "overwriteExisting", null));

        // --name=value  Name of the ViewSet
        if (name.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
        }

        // Add the selection for the viewset file name
        String xmlViewsetFileParam = XML_PREFIX + XMLWriter.getXMLParamName(name + "_file");
        File exportFile = new File(CONTENT_XML_VIEWSETS_DIR, name + ".vs");
        XMLWriter.paramsHash.put(xmlViewsetFileParam, exportFile.getAbsolutePath());

        Element selection = job.createElement("selection");
        selection.appendChild(job.createCDATASection(XMLWriter.padXMLParamName(xmlViewsetFileParam)));
        command.appendChild(selection);

        // Export the physical viewset layout file
        exportViewsetLayout(exportFile);

        return command;

    }

    public String getPublishedState() {
        return publishedState;
    }

    public String getCreatedBy() {
        return creator;
    }

    public String getModifiedDate(SimpleDateFormat sdf) {
        return Integrity.getDateString(sdf, modifiedDate);
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getXMLName() {
        return xmlParamName;
    }

    public void exportViewsetLayout(File exportFile) {
        XMLPrettyPrinter.serialize(exportFile, vsDoc, false);
    }

    @Override
    public String getDetails() {
        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append("<table class='display'>");
        sb.addFieldValue("Name", getName());
        sb.addFieldValue("Description", HyperLinkFactory.convertHyperLinks(getDescription()));
        // Close out the triggers details table
        sb.append("</table>");

        return sb.toString();
    }

    @Override
    protected String getFieldValue(String fieldName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
