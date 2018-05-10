package com.ptc.services.utilities.docgen.type;

import java.util.List;
import java.util.Iterator;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.IntegrityDocs;

public class TypeProperties {

    private Field typeProperties;
    private String strTypeProperties;

    public TypeProperties(Field typeProps) {
        typeProperties = typeProps;
        strTypeProperties = new String();
        initSringTypeProperties();
    }

    private void initSringTypeProperties() {
        StringBuilder sb = new StringBuilder();
        if (null != typeProperties && null != typeProperties.getList()) {
            @SuppressWarnings("unchecked")
            Iterator<Item> it = typeProperties.getList().iterator();
            while (it.hasNext()) {
                // Get the "Property Name" value
                Item propertyName = it.next();
                // Get the details for the property
                String propertyValue = propertyName.getField("value").getValueAsString();
                propertyValue = (null == propertyValue ? "" : Integrity.fixPropertyValue(propertyValue));
                String propertyDesc = propertyName.getField("description").getValueAsString();
                propertyDesc = (null == propertyDesc ? "" : Integrity.fixPropertyValue(propertyDesc));
                sb.append(Integrity.fixPropertyValue(propertyName.getId()) + ":");
                sb.append(propertyValue + ":");
                sb.append(propertyDesc);
                sb.append(it.hasNext() ? ";" + nl + "\t\t\t" : "");
            }
        }
        strTypeProperties = sb.toString();
    }

    private String format(String str) {
        String formattedStr = str;
        while (formattedStr.indexOf('<') >= 0) {
            formattedStr = formattedStr.substring(0, formattedStr.indexOf('<')) + "&lt;"
                    + formattedStr.substring(formattedStr.indexOf('<') + 1);
        }
        while (formattedStr.indexOf('>') >= 0) {
            formattedStr = formattedStr.substring(0, formattedStr.indexOf('>')) + "&gt;"
                    + formattedStr.substring(formattedStr.indexOf('>') + 1);
        }

        // Need to insert a break for strings larger than 25 at some logical point (' ' or '.' or  ';' or ',' or ':')
        StringBuffer spaced = new StringBuffer();
        int counter = 0;

        for (int i = 0; i < formattedStr.length(); i++) {
            if (formattedStr.charAt(i) == ' ') {
                counter = 0;
            } else {
                counter++;
            }

            if (counter > 50) {
                // Intentionally using the same i to move the index of chars forward
                // Find the ' ' or '.' or  ';' or ',' or ':' after the 25 char limit to insert a break
                for (; i < formattedStr.length(); i++) {
                    // Append each char until the needed char is found
                    spaced.append(formattedStr.substring(i, i + 1));
                    // Break out when the needed char is found
                    if (formattedStr.charAt(i) == ' ' || formattedStr.charAt(i) == '.'
                            || formattedStr.charAt(i) == ';' || formattedStr.charAt(i) == ',' || formattedStr.charAt(i) == ':') {
                        spaced.append("<br/>");
                        break;
                    }
                }
                counter = 0;
            } else {
                spaced.append(formattedStr.substring(i, i + 1));
            }
        }

        return spaced.toString();

    }

    public String getStringTypeProperties() {
        return strTypeProperties;
    }

    @SuppressWarnings("unchecked")
    public String getFormattedReport() throws ItemNotFoundException {
        StringBuffer report = new StringBuffer();
        // Construct the open table and heading line
        report.append("<table class='list' width='100%'>" + nl);
        report.append("  <tr>" + nl);
        report.append("    <th>Property Name</th>" + nl);
        report.append("    <th>Property Value</th>" + nl);
        report.append("    <th>Description</th>" + nl);
        report.append("  </tr>" + nl);
        // Ensure we're dealing with the right data type
        if (null != typeProperties && null != typeProperties.getDataType()
                && typeProperties.getDataType().equals(Field.ITEM_LIST_TYPE)
                && typeProperties.getList().size() > 0) {
            List<Item> propertiesList = typeProperties.getList();
            // Loop thru all the type properties
            for (Iterator<Item> lit = propertiesList.iterator(); lit.hasNext();) {
                // Get the "Property Name" value
                Item propertyName = lit.next();
                // Get the details for the property
                Field propValueFld = propertyName.getField("value");
                Field propDescFld = propertyName.getField("description");
                String propertyValue = (null == propValueFld || null == propValueFld.getValueAsString() ? "" : propValueFld.getValueAsString());
                String propertyDesc = (null == propDescFld || null == propDescFld.getValueAsString() ? "" : propDescFld.getValueAsString());
                // Write out the new table row
                report.append("  <tr>");
                report.append("    <td>" + format(propertyName.getId()) + "</td>" + nl);
                report.append("    <td>" + format(propertyValue) + "</td>" + nl);
                report.append("    <td>" + format(propertyDesc) + "</td>" + nl);
                // Close out the table row
                report.append("  </tr>" + nl);
            }
        } else {
            report.append("<tr><td>* none *</td></tr>");
        }
        // Close the table tag
        report.append("</table>" + nl);
        return report.toString();
    }
}
