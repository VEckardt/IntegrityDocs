/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import com.ptc.services.utilities.docgen.IntegrityDocs;
import static com.ptc.services.utilities.docgen.IntegrityDocs.copyright;
import java.text.SimpleDateFormat;

/**
 *
 * @author veckardt
 */
public class Utils {

//    static SimpleDateFormat sdf;

//    public static String getObjectName(IntegrityAdminObject ao) {
//        String className = "";
//
//        if (ao != null) {
//            className = ao.getClass().getSimpleName().replace("Integrity", "");
//            // System.out.println("className = " + className);
//        }
//        return className;
//    }
    public static void addFieldValue(StringBuilder sb, String fieldName, String value) {
        if (!fieldName.equals("reportTemplate")) {

            fieldName = fieldName.replaceAll("([A-Z])", " $1");
            if (fieldName.length() > 3) {
                fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
            sb.append(appendNewLine("<tr><td class='bold_color'>" + fieldName + "&nbsp;</td>"));
            sb.append(appendNewLine("<td>" + value + "</td></tr>"));
            if (fieldName.equals("Modified By")) {
                sb.append("<tr><td class='bold_color'>&nbsp;</td><td>&nbsp;</td></tr>");
            }
        }
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
