package com.ptc.services.utilities.docgen.type;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import com.ptc.services.utilities.docgen.utils.ExceptionHandler;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import com.ptc.services.utilities.docgen.IntegrityField;

public class VisibleFields {

    private Hashtable<String, IntegrityField> fieldsHash;
    private String strVisibleFields;

    public VisibleFields(String typeName, Integrity i, Field visibleFields, Field visibleFieldsForMe) {
        fieldsHash = new Hashtable<String, IntegrityField>();
        strVisibleFields = new String();
        try {
            // Generate the visible fields hash
            fieldsHash = i.getFields(typeName, visibleFields, visibleFieldsForMe);

            // Re-construct the visible fields to recreate this type
            StringBuilder sb = new StringBuilder();
            @SuppressWarnings("unchecked")
            List<Item> partialVisibleFieldsList = visibleFields.getList();
            if (null != partialVisibleFieldsList) {
                for (Iterator<Item> it = partialVisibleFieldsList.iterator(); it.hasNext();) {
                    Item fieldItem = it.next();
                    String fieldName = fieldItem.getId();
                    sb.append(fieldsHash.get(fieldName).getXMLName() + ":");
                    sb.append(Integrity.getXMLParamFieldValue(fieldsHash.get(fieldName).getVisibleGroups(), Integrity.GROUP_XML_PREFIX, ","));
                    sb.append(it.hasNext() ? ";" + IntegrityDocs.nl + "\t\t\t" : "");
                }
            }
            strVisibleFields = sb.toString();
        } catch (APIException aex) {
            ExceptionHandler eh = new ExceptionHandler(aex);
            System.out.println(eh.getMessage());
            System.out.println(eh.getCommand());
            aex.printStackTrace();
        }
    }

    public Hashtable<String, IntegrityField> getList() {
        return fieldsHash;
    }

    public String getStringVisibleFields() {
        return strVisibleFields;
    }

    // Returns a hash table containing IBPL and Relationship fields including the associated types
    // NOTE:  QBRs cannot be evaluated to a specific type, hence are not supported this this view.
    public Hashtable<String, List<String>> getRelationshipFields() {
        Hashtable<String, List<String>> relationshipFields = new Hashtable<String, List<String>>();
        Enumeration<IntegrityField> iFields = fieldsHash.elements();
        while (iFields.hasMoreElements()) {
            IntegrityField field = iFields.nextElement();
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
        report.append("<table class='list'>" + IntegrityDocs.nl);
        report.append("  <tr>" + IntegrityDocs.nl);
        report.append("    <th>ID</th>" + IntegrityDocs.nl);
        report.append("    <th>Type</th>" + IntegrityDocs.nl);
        report.append("    <th>Name</th>" + IntegrityDocs.nl);
        report.append("    <th>Display Name</th>" + IntegrityDocs.nl);
        report.append("    <th>Visible To</th>" + IntegrityDocs.nl);
        report.append("    <th>Relevance</th>" + IntegrityDocs.nl);
        report.append("    <th>Editability</th>" + IntegrityDocs.nl);
        report.append("    <th>Default Value</th>" + IntegrityDocs.nl);
        report.append("    <th>Details</th>" + IntegrityDocs.nl);
        report.append("    <th>Description</th>" + IntegrityDocs.nl);
        report.append("  </tr>" + IntegrityDocs.nl);

        Enumeration<IntegrityField> iFields = fieldsHash.elements();
        while (iFields.hasMoreElements()) {
            IntegrityField field = iFields.nextElement();
            // Write out the new table row and write all information about the field
            report.append("  <tr>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getID() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getType() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getName() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getDisplayName() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + Integrity.convertListToString(field.getVisibleGroups(), "<br/>") + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getRelevanceRule() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getEditabilityRule() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getDefaultValue() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + field.getFieldSummary() + "</td>" + IntegrityDocs.nl);
            report.append("    <td>" + HyperLinkFactory.convertHyperLinks(field.getDescription()) + "</td>" + IntegrityDocs.nl);
            report.append("  </tr>" + IntegrityDocs.nl);
        }
        // Close the table tag
        report.append("</table>" + IntegrityDocs.nl);
        return report.toString();
    }
}
