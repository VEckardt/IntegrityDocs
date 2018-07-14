/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.2 $
 * Last changed:   $Date: 2017/05/12 00:20:03CEST $
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.Command;
import com.mks.api.response.Item;
import com.mks.api.response.ItemList;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author veckardt
 */
public class StaticGroup extends IntegrityObject {

   // WorkItem groupWorkItem;
   // ItemList membership = null;
   ItemList references = null;

   public StaticGroup(WorkItem wi, Types objType) {
      super(wi, objType);
      description = wi.getField("description").getValueAsString();
      name = wi.getId();
      try {
         membership = (ItemList) wi.getField("Members").getList();
      } catch (NoSuchElementException ex) {

      }
      try {
         references = (ItemList) wi.getField("references").getList();
      } catch (NoSuchElementException ex) {

      }
      type = name.substring(0, 1);
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

   public Boolean isMemberOfGroup(String userName, String groupNames) {
      // String data;
      String users = "";
      String groups = "";
      if (membership != null) {
         Iterator it = membership.getItems();
         while (it.hasNext()) {
            Item item = (Item) it.next();
            if (item.getModelType().equals("user")) {
               users += (users.isEmpty() ? "" : ",") + item.getId();
            }
            if (item.getModelType().equals("group")) {
               groups += (groups.isEmpty() ? "" : ",") + item.getId();
            }
         }
         if (userName.isEmpty() || containsOneOf(users, userName)) {
            return true;
         }
         // out.println("Checking " + groupNames + " in " + groups + " ...");

         if (containsOneOf(groups, groupNames)) {
            return true;
         }

      }
      return false;
   }

   public String getReferences(String object, String[] elementClasses) {
      List<String[]> elementList = new ArrayList<>();

      String data = "";
      String elementType = "";
      String elementName = "";
      if (references != null) {
         Iterator it = references.getItems();
         while (it.hasNext()) {
            Item item = (Item) it.next();
            String element = item.getId();
            // out.println(element);
            elementType = element.split(":")[0];
            elementName = element.split(":")[1];
            if (elementType.contains(object)) {
               elementList.add(new String[]{elementType, elementName});
            }
            if (object.equals("Other") && !hasElement(elementClasses, elementType.replaceAll("Admin ", ""))) {
               elementList.add(new String[]{elementType, elementName});
            }
         }
         // java.util.Collections.sort(elementList);
         for (String[] element : elementList) {
            data = data + "\n;" + element[0] + ";" + element[1];
         }
         return data;
      }
      return "";
   }

   boolean hasElement(String[] array, String element) {
      for (String elem : array) {
         if (element.contains(elem)) {
            return true;
         }
      }
      return false;
   }

   public static boolean containsOneOf(String baseList, String checkList) {
      if (!baseList.isEmpty() && !checkList.isEmpty()) {
         for (String group : checkList.split(",")) {
            if (("," + baseList + ",").contains("," + group + ",")) {
               return true;
            }
         }
      }
      return false;
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
      cmdName.appendChild(job.createTextNode("creategroup"));
      command.appendChild(cmdName);

      // --description=value  Short description
      if (description != null && description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }

      // Finally add the name for this state
      command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));

      return command;
   }
}
