/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import com.ptc.services.utilities.docgen.IntegrityAdminObject;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import static com.ptc.services.utilities.docgen.IntegrityDocs.copyright;

/**
 *
 * @author veckardt
 */
public class Utils {

    public static String getObjectName(IntegrityAdminObject ao) {
        String className = "";

        if (ao != null) {
            className = ao.getClass().getSimpleName().replace("Integrity", "");
            // System.out.println("className = " + className);
        }
        return className;
    }
    
    public static void addFieldValue(StringBuilder sb, String fieldName, String value) {
        sb.append(appendNewLine("<tr><td class='bold_color'>" + fieldName + "&nbsp;</td>"));
        sb.append(appendNewLine("<td>" + value + "</td></tr>"));
    }    

    public static void addHeadings(StringBuilder sb, String fields) {
        int cols = fields.split(",").length;
        // sb.append(appendNewLine(" <tr><td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append("<thead>");
        sb.append("<tr>");
        for (String field : fields.split(",")) {
            sb.append("<th class='heading1'>").append(field).append("</th>");
        }
        sb.append("</tr>");
        sb.append("</thead>");
        // sb.append(appendNewLine(" <tr><td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append(appendNewLine("<tfoot>"));
        sb.append(appendNewLine(" <tr><td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append(appendNewLine(" <tr><td colspan='" + cols + "' class='footer'>" + copyright + "</td></tr>"));
        sb.append(appendNewLine("</tfoot>"));
    }
    
    public static String appendNewLine(String line) {
        return line + IntegrityDocs.nl;
    }    
}
