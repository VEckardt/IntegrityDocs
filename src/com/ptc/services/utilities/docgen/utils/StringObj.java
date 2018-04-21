/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import static com.ptc.services.utilities.docgen.IntegrityDocs.copyright;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author veckardt
 */
public class StringObj {

    static int fileId = 0;
    StringBuilder sb = new StringBuilder();
    String path = "";

    public void append(String text) {
        sb.append(appendNewLine(text));
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addFieldValue(String fieldName, int value) {
        addFieldValue(fieldName, String.valueOf(value));
    }

    public void addTDborder(String text) {
        sb.append(("   <td class='border'>" + text + "</td>"));
    }

    public void addFieldValue(String fieldName, String value) {
        if (!fieldName.equals("reportTemplate")) {

            fieldName = fieldName.replaceAll("([A-Z])", " $1");
            if (fieldName.length() > 3) {
                fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
            sb.append(appendNewLine("<tr><td class='bold_color'>" + fieldName + "</td>"));
            sb.append("<td>" + value + "</td></tr>");
            // add an additional blank line
            if (fieldName.equals("Modified By")) {
                sb.append("<tr><td class='bold_color'>&nbsp;</td><td>&nbsp;</td></tr>");
            }
        } else {
            fileId++;
            String content = value;
            if (content != null && !content.isEmpty()) {
                try {
                    Files.write(Paths.get(path + "\\File" + fileId + ".htm"), content.getBytes());
                    Files.write(Paths.get(path + "\\File" + fileId + ".txt"), content.getBytes());
                    sb.append("<tr><td class='bold_color'>Report Template</td><td><a href=\"file:///" + path + "\\File" + fileId + ".htm" + "\">Preview</a></td></tr>");
                    sb.append("<tr><td class='bold_color'>Report Template</td><td><a href=\"file:///" + path + "\\File" + fileId + ".txt" + "\">Download</a></td></tr>");
                } catch (IOException ex) {
                    Logger.getLogger(StringObj.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    public void addHeadings(String fields) {
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

    @Override
    public String toString() {
        return sb.toString();
    }
}
