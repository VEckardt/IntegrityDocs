package com.ptc.services.utilities.docgen;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;

/**
 * Object represents an Integrity State The following attributes are supported:
 * capabilities description id name position
 */
public class IntegrityState extends IntegrityAdminObject {

    public static final String XML_PREFIX = "STATE_";
    private String capabilities;
    private String globalDescription;
    private String id;
    private String position;
    private String iTypeName;
    private String xmlTypeName;

    public IntegrityState(String typeName, WorkItem wi) {
        // Initialize the variables
        modelType = IMModelTypeName.STATE;
        capabilities = "";
        description = "";
        globalDescription = "";
        id = "";
        name = "";
        position = "";
        iTypeName = typeName;

        // Now set them to the correct values
        setCapabilities(wi.getField("capabilities"));
        if (Integrity.getStringFieldValue(wi.getField("description")).length() > 0) {
            globalDescription = Integrity.getStringFieldValue(wi.getField("description"));
        }
        id = wi.getField("id").getValueAsString();
        name = wi.getField("name").getValueAsString();
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
        xmlTypeName = XMLWriter.padXMLParamName(IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(iTypeName));
        position = wi.getField("position").getValueAsString();
    }

    private void setCapabilities(Field c) {
        if (null != c && null != c.getList()) {
            @SuppressWarnings("unchecked")
            Iterator<Item> it = c.getList().iterator();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                Item capability = it.next();
                if (capability.getId().equalsIgnoreCase("OpenChangePackages")) {
                    sb.append("MKSSI:OpenChangePackages");
                } else if (capability.getId().equalsIgnoreCase("ChangePackagesUnderReview")) {
                    sb.append("MKSSI:ChangePackagesUnderReview");
                } else if (capability.getId().equalsIgnoreCase("TimeTracking")) {
                    sb.append("MKSIM:TimeTracking");
                } else if (capability.getId().equalsIgnoreCase("ModifyTestResult")) {
                    sb.append("MKSTM:ModifyTestResult");
                } else {
                    sb.append(capability.getId());
                }
                sb.append(it.hasNext() ? "," : "");
            }
            capabilities = sb.toString();
        }
    }

    public void setFieldAttribute(String attrName, Field attrValue) {
        if (attrName.equalsIgnoreCase("capabilities")) {
            setCapabilities(attrValue);
        }

        if (attrName.equals("description")) {
            if (null != attrValue && attrValue.getValueAsString() != null && attrValue.getValueAsString().length() > 0) {
                description = attrValue.getValueAsString();
            }
        }
    }

    public String getCapabilities() {
        return capabilities;
    }

    public String getDescription() {
        return description;
    }

    public String getGlobalDescription() {
        return globalDescription;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getXMLName() {
        return xmlParamName;
    }

    public String getPosition() {
        return position;
    }

    public Element getOverridesXML(Document job, Element command) {
        // Setup the command to re-create the state via the Load Test Harness...
        Element app = job.createElement("app");
        app.appendChild(job.createTextNode(Command.IM));
        command.appendChild(app);

        Element cmdName = job.createElement("commandName");
        cmdName.appendChild(job.createTextNode("editstate"));
        command.appendChild(cmdName);

        // Only process the possible overridden values
        command.appendChild(XMLWriter.getOption(job, "overrideForType", xmlTypeName));

        // --capabilities=MKSSI:OpenChangePackages,MKSSI:ChangePackagesUnderReview,MKSIM:TimeTracking,MKSTM:ModifyTestResult
        if (capabilities.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "capabilities", capabilities));
        }

        //	--description=value  Short description
        if (description.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "description", description));
        }

        // Finally add the selection for the edit field command
        Element selection = job.createElement("selection");
        selection.appendChild(job.createTextNode(xmlParamName));
        command.appendChild(selection);

        return command;
    }

    public Element getXML(Document job, Element command) {
        // Add this field to the global resources hash
        XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

        // Setup the command to re-create the state via the Load Test Harness...
        Element app = job.createElement("app");
        app.appendChild(job.createTextNode(Command.IM));
        command.appendChild(app);

        Element cmdName = job.createElement("commandName");
        cmdName.appendChild(job.createTextNode("createstate"));
        command.appendChild(cmdName);

        // --description=value  Short description
        if (globalDescription.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "description", globalDescription));
        }

        // Finally add the name for this state
        command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));

        return command;
    }

    public String getModelType() {
        return modelType;
    }
}
