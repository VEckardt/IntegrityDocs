/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen.type;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import static com.ptc.services.utilities.docgen.Constants.GROUP_XML_PREFIX;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.utils.ExceptionHandler;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.Integrity;
import static com.ptc.services.utilities.docgen.Integrity.getXMLParamFieldValue;
import com.ptc.services.utilities.docgen.IntegrityField;
import static com.ptc.services.utilities.docgen.IntegrityUtils.convertListToString;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.LinkedHashMap;

public class VisibleFields {

   private LinkedHashMap<String, IntegrityField> fieldsHash;
   private String strVisibleFields;

   public VisibleFields(String typeName, Field visibleFields, Field visibleFieldsForMe) {
      fieldsHash = new LinkedHashMap<>();
      strVisibleFields = new String();
      try {
         // Generate the visible fields hash
         fieldsHash = Integrity.getFields(typeName, visibleFields, visibleFieldsForMe);

         // Re-construct the visible fields to recreate this type
         StringBuilder sb = new StringBuilder();
         @SuppressWarnings("unchecked")
         List<Item> partialVisibleFieldsList = visibleFields.getList();
         if (null != partialVisibleFieldsList) {
            for (Iterator<Item> it = partialVisibleFieldsList.iterator(); it.hasNext();) {
               Item fieldItem = it.next();
               String fieldName = fieldItem.getId();
               sb.append(fieldsHash.get(fieldName).getXMLName() + ":");
               sb.append(getXMLParamFieldValue(fieldsHash.get(fieldName).getVisibleGroups(), GROUP_XML_PREFIX, ","));
               sb.append(it.hasNext() ? ";" + nl + "\t\t\t" : "");
            }
         }
         strVisibleFields = sb.toString();
      } catch (APIException aex) {
         ExceptionHandler eh = new ExceptionHandler(aex);
         log(eh.getMessage());
         log(eh.getCommand());
         aex.printStackTrace();
      }
   }

   public LinkedHashMap<String, IntegrityField> getList() {
      return fieldsHash;
   }

   public String getStringVisibleFields() {
      return strVisibleFields;
   }

    // Returns a hash table containing IBPL and Relationship fields including the associated types
   // NOTE:  QBRs cannot be evaluated to a specific type, hence are not supported this this view.
   public LinkedHashMap<String, List<String>> getRelationshipFields() {
      LinkedHashMap<String, List<String>> relationshipFields = new LinkedHashMap<String, List<String>>();
      // Enumeration<IntegrityField> iFields = fieldsHash.elements();
      for (IntegrityField field : fieldsHash.values()) {
         if (field.getType().equalsIgnoreCase("relationship")) {
            relationshipFields.put(field.getName(), field.getAllowedTypes());
         } else if (field.getType().equalsIgnoreCase("ibpl")) {
            List<String> relatedTypes = new ArrayList<String>();
            relatedTypes.add(field.getAttributeAsString("backingType"));
            relationshipFields.put(field.getName(), relatedTypes);
         }
      }
      return relationshipFields;
   }

   public String getFormattedReport() throws ItemNotFoundException {
      StringBuffer report = new StringBuffer();
      // Construct the open table and heading line
      report.append("<table class='list'>" + nl);
      report.append("  <tr>" + nl);
      report.append("    <th>ID</th>" + nl);
      report.append("    <th>Type</th>" + nl);
      report.append("    <th>Name</th>" + nl);
      report.append("    <th>Display Name</th>" + nl);
      report.append("    <th>Visible To</th>" + nl);
      report.append("    <th>Relevance</th>" + nl);
      report.append("    <th>Editability</th>" + nl);
      report.append("    <th>Default Value</th>" + nl);
      report.append("    <th>Details</th>" + nl);
      report.append("    <th>Description</th>" + nl);
      report.append("  </tr>" + nl);

        // Enumeration<IntegrityField> iFields = fieldsHash.elements();
      // while (iFields.hasMoreElements()) {
      //    IntegrityField field = iFields.nextElement();
      for (IntegrityField field : fieldsHash.values()) {
         // Write out the new table row and write all information about the field
         report.append("  <tr>" + nl);
         report.append("    <td>" + field.getId() + "</td>" + nl);
         report.append("    <td>" + field.getType() + "</td>" + nl);
         report.append("    <td>" + field.getName() + "</td>" + nl);
         report.append("    <td>" + field.getDisplayName() + "</td>" + nl);
         report.append("    <td>" + convertListToString(field.getVisibleGroups(), "<br/>") + "</td>" + nl);
         report.append("    <td>" + field.getRelevanceRule() + "</td>" + nl);
         report.append("    <td>" + field.getEditabilityRule() + "</td>" + nl);
         report.append("    <td>" + field.getDefaultValue() + "</td>" + nl);
         report.append("    <td>" + field.getFieldSummary() + "</td>" + nl);
         report.append("    <td>" + HyperLinkFactory.convertHyperLinks(field.getDescription()) + "</td>" + nl);
         report.append("  </tr>" + nl);
      }
      // Close the table tag
      report.append("</table>" + nl);
      return report.toString();
   }
}
