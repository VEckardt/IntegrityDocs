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
import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Constants.GROUP_XML_PREFIX;
import static com.ptc.services.utilities.docgen.Constants.USER_XML_PREFIX;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author veckardt
 */
public class Report extends IntegrityObject {

   // private Date lastModified;
   private String createdBy;
   private final String query;
   private String recipeParams;
   private final String shareWith;
   private String sharedGroups;

   public Report(WorkItem wi, Boolean doXML) {
      super(wi, Types.Report);
      name = Integrity.getStringFieldValue(wi.getField("name"));
      //          log("Processing Report: " + q.getName());
      setID(Integrity.getStringFieldValue(wi.getField("id")));
      // q.setIsActive(Integrity.getBooleanFieldValue(wi.getField("isActive")));
      // q.setCreatedBy(Integrity.getUserFullName(wi.getField("createdBy").getItem()));
      // q.setLastModifiedDate(wi.getField("lastModified").getDateTime());
      description = Integrity.getStringFieldValue(wi.getField("description"));
      query = Integrity.getStringFieldValue(wi.getField("query"));
      shareWith = (doXML ? summarizeItemList(wi.getField("shareWith"), ",")
              : summarizeItemList(wi.getField("shareWith"), "<br/>" + nl));
   }

   @Override
   public Element getXML(Document job, Element command) {
      // Add this query to the global resources hash
      XMLWriter.paramsHash.put(getXMLPrefix() + XMLWriter.getXMLParamName(name), name);

      // Setup the command to re-create the query via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.IM));
      command.appendChild(app);

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(job.createTextNode("createquery"));
      command.appendChild(cmdName);

//        // --fields=field,field,...  The fields of the query's default column set.
//        if (fields.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "fields", Integrity.getXMLParamFieldValue(Integrity.convertStringToList(fields, ","), IntegrityField.XML_PREFIX, ",")));
//        }
//        // --queryDefinition=See documentation.  The string giving the complete query definition.
//        // TODO: Need to parameterize query definition rule
//        if (queryDefinition.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "queryDefinition", queryDefinition));
//        }
//        // --[no]sortAscending  The sort direction of the query's default column set.
//        if (sortDirection.length() > 0 && sortDirection.equalsIgnoreCase("Descending")) {
//            command.appendChild(XMLWriter.getOption(job, "nosortAscending", null));
//        } else {
//            command.appendChild(XMLWriter.getOption(job, "sortAscending", null));
//        }
//        // --sortField=field  The sort field of the query's default column set.
//        if (sortField.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "sortField", XMLWriter.padXMLParamName(IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(sortField))));
//        }
//        // --sharedAdmin  Set this object to be a shared admin object
//        if (isAdmin) {
//            command.appendChild(XMLWriter.getOption(job, "sharedAdmin", null));
//        }
      // --shareWith=u=user1[:modify],user2[:modify],.. ;g=group1[:modify],group2[:modify],..   
      //			Set the users and groups that can see and optionally modify this object.
      if (shareWith.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "shareWith", shareWith));
      }
      // --description=value  Short description
      if (description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }
      // --name=value  The name for this object		
      if (name.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
      }
      return command;
   }

   @SuppressWarnings("unchecked")
   public static final String summarizeItemList(Field itemList, String delim) {
      StringBuilder sb = new StringBuilder();
      if (null != itemList && null != itemList.getList()) {
         List<Item> principalList = itemList.getList();
         List<String> usersList = new ArrayList<>();
         List<String> groupsList = new ArrayList<>();
         for (Item principal : principalList) {
            List<String> permissions = principal.getField("permissions").getList();
            switch (principal.getModelType()) {
               case IMModelTypeName.USER:
                  if (delim.equals("<br/>" + nl)) {
                     usersList.add(principal.getId() + (permissions.contains("modify") ? ":modify" : ""));
                  } else {
                     usersList.add(XMLWriter.padXMLParamName(USER_XML_PREFIX + XMLWriter.getXMLParamName(principal.getId()))
                             + (permissions.contains("modify") ? ":modify" : ""));
                  }
                  break;
               case IMModelTypeName.GROUP:
                  if (delim.equals("<br/>" + nl)) {
                     groupsList.add(principal.getId() + (permissions.contains("modify") ? ":modify" : ""));
                  } else {
                     groupsList.add(XMLWriter.padXMLParamName(GROUP_XML_PREFIX + XMLWriter.getXMLParamName(principal.getId()))
                             + (permissions.contains("modify") ? ":modify" : ""));
                  }
                  break;
            }
         }

         if (delim.equals("<br/>" + nl)) {
            sb.append(usersList.size() > 0 ? "Users:&nbsp;&nbsp;" + Integrity.convertListToString(usersList, ", ") : "");
            sb.append(usersList.size() > 0 && groupsList.size() > 0 ? "<br/>" : "");
            sb.append(groupsList.size() > 0 ? "Groups:&nbsp;&nbsp;" + Integrity.convertListToString(groupsList, ", ") : "");
         } else {
            sb.append(usersList.size() > 0 ? "u=" + Integrity.convertListToString(usersList, ",") : "");
            sb.append(usersList.size() > 0 && groupsList.size() > 0 ? ";" : "");
            sb.append(groupsList.size() > 0 ? "g=" + Integrity.convertListToString(groupsList, ",") : "");
         }
      }
      return sb.toString();
   }
}
