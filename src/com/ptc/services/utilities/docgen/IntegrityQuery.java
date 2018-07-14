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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityUtils.convertStringToList;
import static com.ptc.services.utilities.docgen.IntegrityUtils.getDateString;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;

/**
 * The IntegrityQuery class contains the following information about an
 * Integrity IntegrityQuery: createdBy description fields id isAdmin
 * lastModified name queryDefinition shareWith sortDirection sortField
 *
 * Note: We're only interested in admin queries either for reporting purposes or
 * xml export!
 */
public class IntegrityQuery extends IntegrityObject {

   // IntegrityQuery's members
   public static final String XML_PREFIX = "QUERY_";
   private String createdBy;
   private Date lastModified;
   private boolean isAdmin;
   private String queryDefinition;
   private String queryFields;
   private String shareWith;
   private String sortDirection;
   private String sortField;

   public IntegrityQuery(WorkItem wi) {
      super(wi, Types.Query);
      createdBy = "";
      lastModified = new Date();
      isAdmin = true;
      setName(wi.getId());
      setQueryDefinition(Integrity.getStringFieldValue(wi.getField("queryDefinition")));
      shareWith = "";
      setSortDirection(Integrity.getStringFieldValue(wi.getField("sortDirection")));
      setSortField(Integrity.getStringFieldValue(wi.getField("sortField")));
      type = name.substring(0, 1);
   }

   @Override
   public String getObjectTypeName() {
      return objectType.name();
   }

   // All setter functions
   public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
   }

   public void setLastModifiedDate(Date lastModified) {
      this.lastModified = lastModified;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setQueryFields(String fields) {
      this.queryFields = fields;
   }

   public void setSharedAdmin(boolean isAdmin) {
      this.isAdmin = isAdmin;
   }

   public final void setName(String name) {
      this.name = name;
      this.xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
   }

   public final void setQueryDefinition(String queryDefinition) {
      this.queryDefinition = queryDefinition;
   }

   public void setShareWith(String shareWith) {
      this.shareWith = shareWith;
   }

   public final void setSortDirection(String sortDirection) {
      this.sortDirection = sortDirection;
   }

   public final void setSortField(String sortField) {
      this.sortField = sortField;
   }

   // All getter/access functions...
//   @Override
//   public String getPosition() {
//      return this.getID().replaceAll(" ", "_");
//   }

   @Override
   public Element getXML(Document job, Element command) {
      // Add this query to the global resources hash
      XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

      // Setup the command to re-create the query via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.IM));
      command.appendChild(app);

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(job.createTextNode("createquery"));
      command.appendChild(cmdName);

      // --fields=field,field,...  The fields of the query's default column set.
      if (queryFields != null && queryFields.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "fields", Integrity.getXMLParamFieldValue(convertStringToList(queryFields, ","), IntegrityField.XML_PREFIX, ",")));
      }
      // --queryDefinition=See documentation.  The string giving the complete query definition.
      // TODO: Need to parameterize query definition rule
      if (queryDefinition.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "queryDefinition", queryDefinition));
      }
      // --[no]sortAscending  The sort direction of the query's default column set.
      if (sortDirection.length() > 0 && sortDirection.equalsIgnoreCase("Descending")) {
         command.appendChild(XMLWriter.getOption(job, "nosortAscending", null));
      } else {
         command.appendChild(XMLWriter.getOption(job, "sortAscending", null));
      }
      // --sortField=field  The sort field of the query's default column set.
      if (sortField.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "sortField", XMLWriter.padXMLParamName(IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(sortField))));
      }
      // --sharedAdmin  Set this object to be a shared admin object
      if (isAdmin) {
         command.appendChild(XMLWriter.getOption(job, "sharedAdmin", null));
      }
      // --shareWith=u=user1[:modify],user2[:modify],.. ;g=group1[:modify],group2[:modify],..   
      //			Set the users and groups that can see and optionally modify this object.
      if (shareWith.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "shareWith", shareWith));
      }
      // --description=value  Short description
      if (description != null && description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }
      // --name=value  The name for this object		
      if (name.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
      }

      return command;
   }

   public String getCreatedBy() {
      return createdBy;
   }

   public String getLastModifiedDate(SimpleDateFormat sdf) {
      return getDateString(sdf, lastModified);
   }

   @Override
   public String getDescription() {
      return description;
   }

   public String getQueryFields() {
      return queryFields;
   }

   public boolean isAdmin() {
      return isAdmin;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getXMLName() {
      return xmlParamName;
   }

   public String getQueryDefinition() {
      return queryDefinition;
   }

   public String getShareWith() {
      return shareWith;
   }

   public String getSortDirection() {
      return sortDirection;
   }

   public String getSortField() {
      return sortField;
   }

   @Override
   public String getDetails() {
      StringObj sb = new StringObj();
      // Print out the detail about each item type
      sb.append(appendNewLine("<table class='display'>"));
      sb.addFieldValue("Description", HyperLinkFactory.convertHyperLinks(getDescription()));
      sb.addFieldValue("QueryDefinition", getQueryDefinition());
      sb.addFieldValue("Fields", getQueryFields());
      sb.addFieldValue("ShareWith", getShareWith());
      sb.addFieldValue("SortField", getSortField() + " (" + getSortDirection() + ")");
      // Close out the triggers details table
      sb.append(appendNewLine("</table>"));

      return sb.toString();
   }

   @Override
   protected String getFieldValue(String fieldName) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
}
