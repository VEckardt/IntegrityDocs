/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Copyright.copyright;
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

    public void appendTR(String text) {
        sb.append(appendNewLine("<tr>" + text + "</tr>"));
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
        // format the FieldName
        fieldName = getFormattedFieldName(fieldName);

        // in all cases except "Report Template" and "Script"
        if (!fieldName.equals("Report Template") && !fieldName.equals("Script")) {

            // Replace uppercase Chars with an additional blank for better readabiliry    
            appendTR("<td class='bold_color'>" + fieldName + "</td><td>" + value + "</td>");
            // add an additional blank line after Modified By
            if (fieldName.equals("Modified By")) {
                appendTR("<td class='bold_color'>&nbsp;</td><td>&nbsp;</td>");
            }
        } else {
            fileId++;
            String content = value;
            if (content != null && !content.isEmpty()) {
                try {
                    // default for Report Template
                    String link = "";
                    if (fieldName.equals("Report Template")) {
                        // Retrieve the report template files from server, as "html" and as "text"
                        Files.write(Paths.get(path + "\\File" + fileId + ".htm"), content.getBytes());
                        Files.write(Paths.get(path + "\\File" + fileId + ".txt"), content.getBytes());
                        // add first the html link
                        link = "File" + fileId + ".txt";
                        appendTR("<td class='bold_color'>" + fieldName + "</td><td><a href=\"" + link.replace(".txt", ".htm") + "\">Preview</a></td>");
                    } else {
                        // add the original fields
                        addFieldValue("Script Name", content);
                        if (content.toLowerCase().endsWith(".js")) {
                            link = "triggers" + fs + "scripts" + fs + content;
                        }
                    }
                    // add a downloadable link
                    if (!link.isEmpty()) {
                        appendTR("<td class='bold_color'>" + fieldName + "</td><td><a href=\"" + link + "\">Download</a></td>");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(StringObj.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    private String getFormattedFieldName(String fieldName) {
        if (!fieldName.toLowerCase().trim().equals("id")) {
            fieldName = fieldName.replaceAll("([A-Z])", " $1");
        }
        // now turn the first char also into uppercase
        if (fieldName.length() > 3) {
            fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        return fieldName.trim();
    }

    public void addHeadings(String fields) {
        // int cols = fields.split(",").length;
        // sb.append(appendNewLine(" <tr><td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append("<thead>");
        sb.append("<tr>");
        for (String field : fields.split(",")) {
            sb.append("<th class='heading1'>").append(getFormattedFieldName(field)).append("</th>");
        }
        sb.append("</tr>");
        sb.append("</thead>");
        // sb.append(appendNewLine(" <tr><td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        // sb.append(appendNewLine("<tfoot>"));
        // appendTR("<td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td>");
        // appendTR("<td colspan='" + cols + "' class='footer'>" + copyright + "</td>");
        // sb.append(appendNewLine("</tfoot>"));
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
