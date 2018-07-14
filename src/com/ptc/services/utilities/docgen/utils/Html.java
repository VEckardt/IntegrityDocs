/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.1 $
 * Last changed:   $Date: 2017/05/02 12:18:40CEST $
 */
package com.ptc.services.utilities.docgen.utils;

import java.util.Map;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;

public class Html {

//    static ReportList repList = new ReportList();
//    static Report report;
//
//    public static Report initReport(String reportName) {
//        return report = repList.getReport(reportName);
//    }

    // convert to html
    public static String toHtml(String text) {
        return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    // get td tags
    public static final String td(String text) {
        return ("<td>" + text + "</td>");
    }

    // get th tags
    public static final String th(String text) {
        return ("<th class='heading1'>" + text + "</th>");
    }

    // get h3 tags
    public static final String h3(String text) {
        return ("<h3>" + text + "</h3>");
    }

    // convert to uppercase
    public static final String capitalize(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

//    public static final String getTitleAndVersion() {
//        return report.getTitle() + " - " + report.getVersion();
//    }
//
//    // get title and description
//    public static final String getTitle(String sessionId) {
//        Date date = new java.util.Date();
//
//        return "<div id=\"index\"><a href=\"AllReports.jsp;jsessionid=" + sessionId + "\">Reports</a></div><div align=right>"
//                + date.toString() + "</div>" + "<h1>" + report.getTitle() + "</h1><div style='text-align:center;'>"
//                + report.getDescription() + "</div>";
//    }

    // get title and description
//    public static final String getTitle(String text, int index) {
//        Date date = new java.util.Date();
//        
//        return "<div id=\"index\"><a href=\"Index.jsp\">Index</a></div><div align=right>"
//                + date.toString() + "</div>" + "<h1>" + text + "</h1><div style='text-align:center;'>"
//                + Html.descrReports[index - 1] + "</div>";
//    }
    // get title and description
//    public static final String getTitle(int index) {
//        Date date = new java.util.Date();
//        return "<div id=\"index\"><a href=\"Index.jsp\">Index</a></div><div align=right>"
//                + date.toString() + "</div>" + "<h1>Integrity Workflow & Documents - " + titleReports[index - 1] + "</h1><div style='text-align:center;'>"
//                + Html.descrReports[index - 1] + "</div>";
//    }
    // returns the exception
    // has to be improved!
    public static String logException(APIException ae) {
        return ("<br>Exception logging " + "<br> message:     " + ae.getMessage()
                + "<br> class:       " + ae.getClass().getName() + "<br> exceptionId: " + ae
                .getExceptionId());
    }

    // generates a well looking header entry - short form
    public static String getHeaderField(WorkItem wi, String fieldName) {
        return getHeaderField(wi, fieldName, "");
    }

    // generates a well looking header entry
    public static String getHeaderField(WorkItem wi, String fieldName, String addText) {
        String value = wi.getField(fieldName).getValueAsString();
        String data = "";
        if (value != null && !value.equals("null")) {
            String fName = Html.capitalize(fieldName).replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");

            data = ("<tr><th style='text-align:left;'>" + fName + ":</th>");
            if (fName.contentEquals("Name")) {
                data = data + Html.td("<b>" + value + "&nbsp;" + addText + "</b>") + "</tr>";
            } else {
                data = data + Html.td(value + "&nbsp;" + addText) + "</tr>";
            }
        }
        return data;
    }

    // gets a standartized item select box
    public static String itemSelectField(String typeName, Map objects, Boolean addAll,
            String selected1, String link) {
        String data = "<hr><table border=1>";
        // BEGIN: Build the selection list for types
        data = data + ("<tr>");
        data = data + ("<form name=form1>");

        data = data + getOptionBox(typeName, objects, addAll, selected1, null);

        data = data + Html.td("<input type=\"submit\" value=\"Refresh\">");
        data = data + "</form>";
        if (!link.isEmpty()) {
            data = data + Html.td("<a href=\"../output/" + link + "\">Download: " + link + "</a><br>");
        }
        data = data + "</tr>";
        data = data + "</table>";
        return data;
    }

    // gets a standartized item select box
    public static String itemSelectField(String typeName, Map objects, Boolean addAll,
            String selected1, String[] selectionDefinition) {

        return itemSelectField(typeName, objects, addAll,
                selected1, selectionDefinition, null);

    }

    // gets a standartized item select box
    public static String itemSelectField(String typeName, Map objects, Boolean addAll,
            String selected1, String[] selectionDefinition, String link) {
        String data = "<hr><table border=1>";
        // BEGIN: Build the selection list for types
        data = data + ("<tr>");
        data = data + ("<form name=form1>");

        data = data + getOptionBox(typeName, objects, addAll, selected1, selectionDefinition);

        data = data + (Html.td("<input type=\"submit\" value=\"Refresh\">"));
        data = data + ("</form>");
        if (link != null && !link.isEmpty()) {
            data = data + Html.td("<a href=\"../" + link + "\">Download: " + link + "</a><br>");
        }
        data = data + ("</tr>");
        data = data + ("</table>");
        return data;
    }

    public static String getOptionBox(String typeName, Map objects, Boolean addAll,
            String selected1, String[] selectionDefinition) {
        String data = (Html.td("Display details for " + typeName));
        data = data + ("<td>");
        data = data + ("<select name=" + typeName + " onChange='this.form.submit()'>");

        if (addAll) {
            if (selected1 != null && !selected1.equals("") && selected1.equals("All")) {
                data = data + ("<option selected value='All'>* All *");
            } else {
                data = data + ("<option value='All'>* All *");
            }
        }

        for (Object key : objects.keySet()) {
            String display = key.toString();
            if (typeName.equals("user")) {
                WorkItem wi = (WorkItem) objects.get(key);
                display = wi.getField("fullname").getValueAsString();
                if (display == null) {
                    display = key.toString();
                } else {
                    display = key.toString() + " - " + display;
                }
            }
            if (selected1 != null && !selected1.equals("") && selected1.equals(key.toString())) {
                data = data + ("<option selected value='" + key.toString() + "'>" + display);
            } else {
                data = data + ("<option value='" + key.toString() + "'>" + display);
            }
        }

        data = data + ("</select>");
        data = data + ("</td>");

        if (selectionDefinition != null) {

            data = data + (Html.td(selectionDefinition[0]) + "<td>");
            data = data + ("<select name=" + selectionDefinition[1] + " onChange='this.form.submit()'>");

            for (int d = 3; d < selectionDefinition.length; d++) {
                if (selectionDefinition[2] != null && !selectionDefinition[2].equals("") && selectionDefinition[2].equals(selectionDefinition[d])) {
                    data = data + ("<option selected value='" + selectionDefinition[d] + "'>" + selectionDefinition[d]);
                } else {
                    data = data + ("<option value='" + selectionDefinition[d] + "'>" + selectionDefinition[d]);
                }
            }

            data = data + ("</select>");
            data = data + ("</td>");
        }
        return data;
    }

    // produces an index section
//    public static String getIndexSection(String sectionName, String groupName, Boolean offerDownload, String sessionId) {
//        String data = "<hr><h3>" + sectionName
//                + "</h3><table border=1><tr><th style=\"width: 250px;\">" + capitalize(groupName)
//                + "</th><th>Description</th>" + (offerDownload ? "<th>Direct download</th>" : "") + "</tr>";
//
//        for (Map.Entry<String, Report> entry : repList.getRepList().entrySet()) {
//            String name = entry.getKey();
//            Report report = entry.getValue();
//
//            if (report.getGroup().contentEquals(groupName)) {
//
//                String image = groupName.equals("tool") ? "Trigger" : "Report";
//                String download = "<td align=center>" + (report.getExcelFileName() != null ? "<img src='images/ok.png' alt='\"\"'>" : "") + "</td>";
//                // String download = offerDownload?"<td>" + ((report.getExcelFileName() != null ? "<img src='images/ok.png' alt='\"\"'>" : "") + "</td>"):"");
//                data = data + ("<tr>");
//                data = data + ("<td><img src='images/" + image + ".png' alt='\"\"'>&nbsp;&nbsp;<a href=\"" + report.getJspFile() + ";jsessionid=" + sessionId + "\">" + name.replaceAll("&", "&amp;").replaceAll(" ", "&nbsp;") + "</a></td>");
//                data = data + ("<td>" + report.getDescription() + "</td>" + (offerDownload ? download : ""));
//                data = data + ("</tr>");
//            }
//        }
//        data = data + "</table>";
//        return data;
//    }
}
