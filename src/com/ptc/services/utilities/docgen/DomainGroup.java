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

import com.mks.api.Command;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author veckardt
 */
public class DomainGroup extends IntegrityObject {

   public DomainGroup(WorkItem wi) {
      super(wi, Types.MKSDomainGroup);
      name = Integrity.getStringFieldValue(wi.getField("name"));
      xmlParamName = XMLWriter.padXMLParamName(getXMLPrefix() + XMLWriter.getXMLParamName(name));
      type = name.substring(0, 1);
   }

   @Override
   public Element getXML(Document job, Element command) {
      // Add this field to the global resources hash
      XMLWriter.paramsHash.put(getXMLPrefix() + XMLWriter.getXMLParamName(name), name);

      // Setup the command to re-create the state via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.INTEGRITY));
      command.appendChild(app);

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(job.createTextNode("createmksdomaingroup"));
      command.appendChild(cmdName);

      // Finally add the name for this state
      command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
      if (description != null && description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }
      return command;
   }

   public List<String> getSubGroups() {

      List<String> subGroups = new ArrayList<>();
      if (membership != null) {
         Iterator it = membership.getItems();
         while (it.hasNext()) {
            Item item = (Item) it.next();
            if (item.getModelType().equals("group")) {
               subGroups.add(item.getId());
            }
         }
      }
      return subGroups;
   }

   @Override
   public String getMembership() {
      String data = "<table class='sortable'>";
      data += "<tr><th class='heading8'>Users</th><th class='heading8'>Groups</th></tr>";
      String users = "";
      String groups = "";
      if (membership != null) {
         Iterator it = membership.getItems();
         while (it.hasNext()) {
            Item item = (Item) it.next();
            if (item.getModelType().equals("user")) {
               users += (users.isEmpty() ? "" : ", ") + item.getId();
            }
            if (item.getModelType().equals("group")) {
               groups += (groups.isEmpty() ? "" : ", ") + item.getId();
            }
         }
         data += "<tr><td>" + users + "</td><td>" + groups + "</td></tr>";
         data += "</table>";
         return data;
      }
      return "";
   }
}
