/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.field;

import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import java.util.ListIterator;

/**
 *
 * @author veckardt
 */
public class PickField {

    Field field;

    public PickField(Field field) {
        this.field = field;
    }

    public String getFormattedReport() {
        StringBuilder report = new StringBuilder();
        // Construct the open table and heading line
        report.append("<table class='sortable'>" + IntegrityDocs.nl);
        report.append("  <tr>" + IntegrityDocs.nl);
        report.append("    <th class='heading1'>Label</th>");
        report.append("    <th class='heading1'>Value</th>");
        report.append("    <th class='heading1'>Active</th>");
        report.append("  </tr>" + IntegrityDocs.nl);

        ListIterator list = field.getList().listIterator();
        while (list.hasNext()) {
            Item item = (Item) list.next();
            // Write out the new table row and write all information about the field
            report.append("  <tr>" + IntegrityDocs.nl);
            report.append("    <td>" + item.getField("label").getValueAsString() + "</td>");
            report.append("    <td>" + item.getField("value").getValueAsString() + "</td>");
            report.append("    <td>" + item.getField("active").getValueAsString() + "</td>");
            report.append("  </tr>" + IntegrityDocs.nl);
        }
        // Close the table tag
        report.append("</table>" + IntegrityDocs.nl);
        return report.toString();
    }
}
