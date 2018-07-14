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

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityUtils.getStringFieldValue;

/**
 * Object represents an Integrity State The following attributes are supported:
 * capabilities description id name position
 */
public class IntegrityState extends IntegrityObject {

   public static final String XML_PREFIX = "STATE_";
   
   private String capabilities;
   private String globalDescription = "";
   private String displayName;
   private String iTypeName;
   private String xmlTypeName;

//    @Override
//    public String getDetails() {
//        super();
//        StringObj sb = new StringObj();
//        // Print out the detail about each item type
//        sb.append("<table class='display'>");
//        sb.addFieldValue("Name", getName());
//        sb.addFieldValue("Display Name", getDisplayName());
//        sb.addFieldValue("Description", HyperLinkFactory.convertHyperLinks(getDescription()));
//        // Close out the triggers details table
//        sb.append("</table>");
//
//        return sb.toString();
//    }
   public IntegrityState(WorkItem wi, String typeName) {
      super(wi, Types.State);
      // Initialize the variables
      // modelType = IMModelTypeName.STATE;
      setDescription(wi.getField("description").getString());
      id = wi.getField("id").getValueAsString();
      setName(wi.getField("name").getString());
      position = wi.getField("position").getValueAsString();
      setCapabilities(wi.getField("capabilities"));
      iTypeName = typeName;
      displayName = wi.getField("displayName").getString();

      // set globalDescription
      if (getStringFieldValue(wi.getField("description")).length() > 0) {
         globalDescription = getStringFieldValue(wi.getField("description"));
      }
      xmlTypeName = XMLWriter.padXMLParamName(IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(iTypeName));
      Iterator it = getList(Types.Type).iterator();
      // set used in type
      while (it.hasNext()) {
         IntegrityType iType = (IntegrityType) it.next();
         if (iType.usesState(name)) {
            addUsedInType(iType.getName());
         }
      }
   }

   // All setter functions
   public final void setName(String name) {
      this.name = name;
      this.xmlParamName = XMLWriter.padXMLParamName(getXMLPrefix() + XMLWriter.getXMLParamName(name));
   }

   public final void setDescription(String description) {
      this.description = description;
   }

   public void setDisplayName(String DisplayName) {
      this.displayName = DisplayName;
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

   @Override
   public String getDescription() {
      return description;
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getGlobalDescription() {
      return globalDescription;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getXMLName() {
      return xmlParamName;
   }

   @Override
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
      if (description != null && description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }

      // Finally add the selection for the edit field command
      Element selection = job.createElement("selection");
      selection.appendChild(job.createTextNode(xmlParamName));
      command.appendChild(selection);

      return command;
   }

   @Override
   public Element getXML(Document job, Element command) {
      // Add this field to the global resources hash
      XMLWriter.paramsHash.put(getXMLPrefix() + XMLWriter.getXMLParamName(name), name);

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

   @Override
   protected String getFieldValue(String fieldName) {
      if (fieldName.equals("capabilities")) {
         return getCapabilities().replaceAll(",", "<br>").replaceAll("MKS", "");
      }
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
}
