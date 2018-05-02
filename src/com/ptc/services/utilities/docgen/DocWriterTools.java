/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class DocWriterTools {

    static List<IntegrityType> iTypes = null;
    static List<IntegrityField> iFields = null;
    static List<Trigger> iTriggers = null;
    static ArrayList<List<IntegrityObject>> iObjectList = null;

    static Integrity i;
    private static final Date now = new Date();
    static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");

    public static final File objectTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "ObjectTemplate.txt");
    public static final File titleTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "title.txt");
    public static final File summaryTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "SummaryTemplate.txt");
    public static final File overviewTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "OverviewTemplate.txt");

    public DocWriterTools(Integrity i) {
        DocWriterTools.i = i;
    }

    public String getNow() {
        return sdf.format(now);
    }

    public void writeSummary() throws FileNotFoundException, IOException {
        try (BufferedReader triggerReader = new BufferedReader(new FileReader(summaryTemplate))) {
            BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "summary.htm"));
            String line;
            while (null != (line = triggerReader.readLine())) {
                triggerWriter.write((getFormattedContent(line, null)));
            }
            triggerWriter.flush();
            triggerWriter.close();
        }
    }

    // Resolves the parameterized report values
    public static String getFormattedContent(String str, IntegrityAdminObject adminObj) {
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
                    sb.append(sdf.format(now));
                } else if ("objecttype".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(adminObj.getObjectDisplayName());
                    }
                } else if (paramName.endsWith("overview")) {
                    if (null != adminObj) {
                        if (adminObj instanceof IntegrityType) {
                            sb.append(getTypesOverview());
                        } else if (adminObj instanceof Trigger) {
                            sb.append(getTriggersOverview());
                        } else if (adminObj instanceof IntegrityField) {
                            sb.append(getFieldOverview());
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
                    sb.append(getSummary());
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

    private static String getSummary() {
        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append("<table class='sortable'>");
        sb.addHeadings("Object,Count");
        sb.append("<tbody>");

        sb.addFieldValue("View Sets", getList(Types.Viewset).size());
        sb.addFieldValue("Main CM Projects", getList(Types.SIProject).size());
        sb.addFieldValue("Main W&D Projects", getList(Types.IMProject).size());
        sb.addFieldValue("", "");
        sb.addFieldValue("Groups", getList(Types.Group).size());
        sb.addFieldValue("Dynamic Groups", getList(Types.DynamicGroup).size());
        sb.addFieldValue("", "");
        sb.addFieldValue("States", getList(Types.State).size());
        sb.addFieldValue("Types", iTypes.size());
        sb.addFieldValue("Fields", iFields.size());
        sb.addFieldValue("Triggers", iTriggers.size());
        sb.addFieldValue("", "");
        sb.addFieldValue("Change Package Types", getList(Types.CPType).size());
        sb.addFieldValue("Test Verdicts", getList(Types.Verdict).size());
        sb.addFieldValue("Test Result Fields", getList(Types.ResultField).size());
        sb.addFieldValue("", "");
        sb.addFieldValue("Charts", getList(Types.Chart).size());
        sb.addFieldValue("Queries", getList(Types.Query).size());
        sb.addFieldValue("Reports", getList(Types.Report).size());
        sb.addFieldValue("Dashboards", getList(Types.Dashboard).size());
        sb.addFieldValue("", "");
        sb.addFieldValue("Gateway Mappings", getList(Types.GatewayMapping).size());
        sb.addFieldValue("Gateway Import Configurations", getList(Types.GatewayImportConfig).size());
        sb.addFieldValue("Gateway Export Configurations", getList(Types.GatewayExportConfig).size());

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
        for (IntegrityType iType : iTypes) {
            sb.append((" <tr>"));
            sb.addTDborder(iType.getPosition());
            sb.addTDborder("<a href='Types/" + iType.getPosition() + ".htm'>" + iType.getName() + "</a>");
            sb.addTDborder("<img src=\"Types/" + iType.getName().replaceAll(" ", "_") + ".png\" alt=\"-\">");
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(iType.getDescription()));
            sb.addTDborder((iType.getAllowChangePackages() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder(iType.getPermittedGroups());
            sb.addTDborder((iType.getTimeTrackingEnabled() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getShowWorkflow() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getCopyTreeEnabled() ? "&#10003;" : "&nbsp;"));
            //sb.addTDborder((iType.getBranchEnabled() ? "&#10003;" : "&nbsp;"));
            //sb.addTDborder((iType.getLabelEnabled() ? "&#10003;" : "&nbsp;"));
            sb.append((" </tr>"));
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
        for (Trigger iTrigger : iTriggers) {
            sb.append((" <tr>"));
            sb.addTDborder(iTrigger.getPosition());
            sb.addTDborder("<a href='Triggers/" + iTrigger.getPosition() + ".htm'>" + iTrigger.getName() + "</a>");
            sb.addTDborder(iTrigger.getType());
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(iTrigger.getDescription()));
            sb.addTDborder(iTrigger.getScript());
            sb.addTDborder(iTrigger.getScriptTiming());
            sb.append((" </tr>"));
        }
        sb.append(("</tbody></table>"));

        return sb.toString();
    }

    private static String getFieldOverview() {
        StringObj sb = new StringObj();
        // Summary heading line
        sb.append(("<table class='sortable'>"));
        sb.addHeadings("ID,Name,Description,Type");
        sb.append(("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityField object : iFields) {
            sb.append((" <tr>"));
            sb.addTDborder(object.getPosition());
            sb.addTDborder("<a href='Fields/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
            sb.addTDborder(object.getType());
            sb.append((" </tr>"));
        }
        sb.append(("</tbody></table>"));

        return sb.toString();
    }

    private static String getObjectOverview(Types type) {
        String additionalColumns = type.getAddColumns();
        List<IntegrityObject> objectList = iObjectList.get(type.getID());
        StringObj sb = new StringObj();
        // Summary heading line
        sb.append(("<table class='sortable'>"));
        Boolean showImage = additionalColumns.contains("Image");
        additionalColumns = additionalColumns.replace(",Image", "").replace("Image", "");
        String headings = "ID,Name," + (showImage ? "Image," : "") + "Description" + (additionalColumns.isEmpty() ? "" : "," + additionalColumns);
        sb.addHeadings(headings);
        sb.append(("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityObject object : objectList) {
            sb.append(("<tr>"));
            sb.addTDborder(object.getPosition());
            sb.addTDborder("<a href='" + object.getDirectory() + "/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
            if (showImage) {
                sb.addTDborder("<img src=\"" + object.getDirectory() + "/" + object.getName().replaceAll(" ", "_") + ".png\" alt=\"-\" onerror=\"this.src='images/" + object.getObjectType() + ".png'\">");
            }
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
            if (additionalColumns.contains("Type")) {
                sb.addTDborder(object.getType());
            }
            if (additionalColumns.contains("isActive")) {
                sb.addTDborder(object.getIsActive());
            }
            sb.append(("</tr>"));
        }
        sb.append(("</tbody></table>"));

        return sb.toString();
    }

    // Delete all the template files
    public void cleanupTempFiles() {
        log("Finishing publishing ...");
        titleTemplate.delete();
        overviewTemplate.delete();
        objectTemplate.delete();
        summaryTemplate.delete();
    }
}
