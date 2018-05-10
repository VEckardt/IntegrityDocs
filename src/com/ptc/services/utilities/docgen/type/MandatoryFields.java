package com.ptc.services.utilities.docgen.type;

import com.ptc.services.utilities.docgen.XMLWriter;
import java.util.List;
import java.util.Iterator;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import com.ptc.services.utilities.docgen.IntegrityField;
import com.ptc.services.utilities.docgen.IntegrityState;

public class MandatoryFields {

    private Field fields;
    private String strMandatoryFields;

    public MandatoryFields(Field mandatoryFields) {
        fields = mandatoryFields;
        strMandatoryFields = new String();

        // Initialize the string representation of the mandatory fields 
        if (null != fields && null != fields.getList()) {
            StringBuilder sb = new StringBuilder();
            for (@SuppressWarnings("unchecked") Iterator<Item> it = fields.getList().iterator(); it.hasNext();) {
                Item state = it.next();
                String xmlParam = IntegrityState.XML_PREFIX + XMLWriter.getXMLParamName(state.getId());
                sb.append(XMLWriter.padXMLParamName(XMLWriter.getXMLParamName(xmlParam)) + ":");
                sb.append(Integrity.getXMLParamFieldValue(state.getField("fields"), IntegrityField.XML_PREFIX, ","));
                sb.append(it.hasNext() ? ";" + nl + "\t\t\t" : "");
            }
            strMandatoryFields = sb.toString();
        }
    }

    public String getStringMandatoryFields() {
        return strMandatoryFields;
    }

    @SuppressWarnings("unchecked")
    public String getFormattedReport() throws ItemNotFoundException {
        StringBuffer report = new StringBuffer();
        // Construct the open table and heading line
        report.append("<table class='list'>" + nl);
        report.append("  <tr>" + nl);
        report.append("    <th>State</th>" + nl);
        report.append("    <th>Mandatory Fields</th>" + nl);
        report.append("  </tr>" + nl);
        // Ensure we're dealing with the right data type
        if (null != fields && null != fields.getList()) {
            List<Item> stateList = fields.getList();
            // Loop thru all the state names
            for (Item state : stateList) {
                // Write out the new table row
                report.append("  <tr>" + nl);
                report.append("    <td>" + state.getId() + "</td>" + nl);
                // Write out the "Mandatory Fields"
                report.append("    <td>" + Integrity.getFieldValue(state.getField("fields"), "<br/>") + "</td>" + nl);
                // Close out the table row
                report.append("  </tr>" + nl);
            }
        }
        // Close the table tag
        report.append("</table>" + nl);
        return report.toString();
    }
}
