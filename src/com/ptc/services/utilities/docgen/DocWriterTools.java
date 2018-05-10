/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import static com.ptc.services.utilities.docgen.Constants.SUMMARY_FILE;
import static com.ptc.services.utilities.docgen.Constants.getNow;
import static com.ptc.services.utilities.docgen.Constants.summaryTemplate;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class DocWriterTools {

    static ArrayList<List<IntegrityAdminObject>> iObjectList = null;
    static Integrity i;

    public DocWriterTools(Integrity i, ArrayList<List<IntegrityAdminObject>> iObjectList) {
        DocWriterTools.i = i;
        DocWriterTools.iObjectList = iObjectList;
    }

    public void writeSummary() throws FileNotFoundException, IOException {
        try (BufferedReader triggerReader = new BufferedReader(new FileReader(summaryTemplate))) {
            BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(SUMMARY_FILE));
            String line;
            while (null != (line = triggerReader.readLine())) {
                triggerWriter.write((getFormattedContent(line, null, 1)));
            }
            triggerWriter.flush();
            triggerWriter.close();
        }
    }

    // Resolves the parameterized report values
    public static String getFormattedContent(String str, IntegrityAdminObject adminObj, int id) {
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        int currentIndex;

        while ((currentIndex = str.indexOf("<%", startIndex)) >= 0) {
            if (currentIndex > 0) {
                sb.append(str.substring(startIndex, currentIndex));
            }

            if (currentIndex == (str.length() - 2)) {
                sb.append("<%");
                startIndex = currentIndex + 2;
            } else {
                int endIndex = str.indexOf("%>", currentIndex);
                if (endIndex < 0) {
                    // no matching closing token, don't expand
                    break;
                }
                String paramName = str.substring(currentIndex + 2, endIndex);

                // Expand the field name or symbolic
                if ("hostport".equals(paramName)) {
                    sb.append(i.getHostName()).append(":").append(i.getPort());
                } else if ("now".equals(paramName)) {
                    sb.append(getNow());
                } else if ("objecttype".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(adminObj.getObjectTypeDisplayName());
                    }
                } else if ("description".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(adminObj.getObjectTypeDescription());
                    }
                } else if (paramName.endsWith("overview")) {
                    if (null != adminObj) {
                        if (adminObj instanceof IntegrityType && id == 1) {
                            sb.append(getTypesOverview());
                        } else if (adminObj instanceof IntegrityType && id == 2) {
                            sb.append(getTypeImageOverview());
                        } else if (adminObj instanceof Trigger) {
                            sb.append(getTriggersOverview());
                        } else {
                            // for all other types
                            sb.append(getObjectOverview(adminObj.objectType));
                        }
                    }
                } else if ("details".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(adminObj.getDetails());
                    }
                } else if ("objectname".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(adminObj.getName());
                    }
                } else if ("summary".equals(paramName)) {
                    sb.append(getObjectSummary());
                } else if ("about".equals(paramName)) {
                    sb.append(i.getAbout("IntegrityDocs" + Copyright.version));
                } else {
                    // Unknown parameter
                    sb.append(paramName);
                }
                startIndex = endIndex + 2;
            }
        }

        if (startIndex < str.length()) {
            sb.append(str.substring(startIndex));
        }

        return sb.toString();
    }

    private static String getObjectSummary() {
        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append("<table class='sortable'>");
        sb.addHeadings("Object,Count,Description");
        sb.append("<tbody>");

        for (Types type : Types.values()) {
            sb.append(" <tr>");
            sb.addTDborder(type.getDisplayName());
            sb.addTDborder(String.valueOf(getList(type).size()));
            sb.addTDborder(type.getDescription().replace("This report lists all ", ""));
            sb.append(" </tr>");
        }
        // Close out the table
        sb.append("</tbody></table>");

        return sb.toString();
    }

    private static String getTypesOverview() {
        StringObj sb = new StringObj();

        // Summary heading line
        sb.append("<table class='sortable'>");
        sb.addHeadings("ID,Name,Image,Description,Change Packages,Permitted Groups,Time Tracking,Show Workflow,Copy Tree");
        sb.append("<tbody>");

        // Print out the summary about each item type
        for (IntegrityAdminObject iType : getList(Types.Type)) {
            sb.append((" <tr>"));
            sb.addTDborder(iType.getPosition());
            sb.addTDborder("<a href='Types/" + iType.getPosition() + ".htm'>" + iType.getName() + "</a>");
            sb.addTDborder("<img src=\"Types/" + iType.getName().replaceAll(" ", "_") + ".png\" alt=\"-\"/>");
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(iType.getDescription()));
            sb.addTDborder((iType.getFieldValue("allowChangePackages").equals("true") ? "&#10003;" : "&nbsp;"));
            sb.addTDborder(iType.getFieldValue("permittedGroups"));
            sb.addTDborder((iType.getFieldValue("timeTrackingEnabled").equals("true") ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getFieldValue("showWorkflow").equals("true") ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getFieldValue("copyTreeEnabled").equals("true") ? "&#10003;" : "&nbsp;"));
            //sb.addTDborder((iType.getBranchEnabled() ? "&#10003;" : "&nbsp;"));
            //sb.addTDborder((iType.getLabelEnabled() ? "&#10003;" : "&nbsp;"));
            sb.append((" </tr>"));
        }
        sb.append("</tbody></table>");

        return sb.toString();
    }

    private static String getTypeImageOverview() {
        StringObj sb = new StringObj();

        // Summary heading line
        sb.append("<table class='sortable'>");
        sb.addHeadings("ID,Name,Image,Main Image,Description");
        sb.append("<tbody>");

        // Print out the summary about each item type
        for (IntegrityAdminObject iType : IntegrityDocs.getList(Types.Type)) {
            sb.append(("<tr>"));
            sb.addTDborder(iType.getPosition());
            sb.addTDborder("<a href='Types/" + iType.getPosition() + ".htm'>" + iType.getName() + "</a>");
            sb.addTDborder("<img src=\"" + iType.getFieldValue("smallImagePath") + "\" alt=\"-\"/>");
            sb.addTDborder("<img src=\"" + iType.getFieldValue("mainImagePath") + "\" alt=\"-\"/>");
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(iType.getDescription()));
            sb.append(("</tr>"));
        }
        sb.append("</tbody></table>");

        return sb.toString();
    }

    public static String getTriggersOverview() {
        StringObj sb = new StringObj();
        // Summary heading line
        sb.append(("<table class='sortable'>"));
        sb.addHeadings("Position,Name,Type,Description,Script,Script Timing");
        sb.append(("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityAdminObject object : IntegrityDocs.getList(Types.Trigger)) {
            sb.append((" <tr>"));
            sb.addTDborder(object.getPosition());
            sb.addTDborder("<a href='Triggers/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
            sb.addTDborder(object.getType());
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
            sb.addTDborder(object.getFieldValue("script"));
            sb.addTDborder(object.getFieldValue("scriptTiming"));
            sb.append((" </tr>"));
        }
        sb.append(("</tbody></table>"));

        return sb.toString();
    }

    private static String getObjectOverview(Types type) {
        String additionalColumns = type.getAddColumns();
        List<IntegrityAdminObject> objectList1 = iObjectList.get(type.getID());
        List<IntegrityAdminObject> objectList2 = iObjectList.get(type.getID());
        StringObj sb = new StringObj();
        // Summary heading line
        String headings;
        Boolean showImage = false;
        sb.append(("<table class='sortable'>"));
        if (type.showAllFields()) {
            headings = objectList1.get(0).getFieldListString();
        } else {
            showImage = additionalColumns.contains("Image");
            additionalColumns = additionalColumns.replace(",Image", "").replace("Image", "");
            headings = "ID,Name," + (showImage ? "Image," : "") + "Description" + (additionalColumns.isEmpty() ? "" : "," + additionalColumns);
        }
        sb.addHeadings(headings);
        sb.append(("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityAdminObject object : objectList2) {
            sb.append(("<tr>"));
            if (type.showAllFields()) {
                while (object.fields.hasNext()) {
                    Field fld = (Field) object.fields.next();
                    if (fld.getName().equals("image")) {
                        sb.addTDborder("<img src=\"" + fld.getValueAsString() + "\" alt=\"-\"/>");
                        // 
                    } else if (fld.getName().equals("view")) {
                        sb.addTDborder("<a href='" + fld.getValueAsString() + "'>View</a>");
                    } else {
                        sb.addTDborder(fld.getValueAsString());
                    }
                }
            } else {
                sb.addTDborder(object.getPosition());
                sb.addTDborder("<a href='" + object.getDirectory() + "/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
                if (showImage) {
                    sb.addTDborder("<img src=\"" + object.getDirectory() + "/" + object.getName().replaceAll(" ", "_") + ".png\" alt=\"-\" onerror=\"this.src='images/" + object.getObjectType() + ".png'\"/>");
                }
                sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
                if (additionalColumns.contains("Type")) {
                    sb.addTDborder(object.getType());
                }
                if (additionalColumns.contains("isActive")) {
                    sb.addTDborder(object.isActive().toString());
                }
            }
            sb.append(("</tr>"));
        }
        sb.append(("</tbody></table>"));

        return sb.toString();
    }
}
