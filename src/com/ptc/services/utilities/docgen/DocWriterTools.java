/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class DocWriterTools {

    static List<IntegrityObject> iViewsets = null;
    static List<IntegrityType> iTypes = null;
    static List<IntegrityField> iFields = null;
    static List<Trigger> iTriggers = null;
    static List<IntegrityObject> iCharts = null;

    static List<IntegrityObject> iGroups = null;
    static List<IntegrityObject> iDynGroups = null;
    static List<IntegrityObject> iStates = null;
    static List<IntegrityObject> iTestVerdicts = null;
    static List<IntegrityObject> iTestResultFields = null;
    static List<IntegrityObject> iDashboards = null;
    static List<IntegrityObject> iQueries = null;
    static List<IntegrityObject> iReports = null;
    static List<IntegrityObject> iCPTypes = null;
    static List<IntegrityObject> iIMProjects = null;
    static List<IntegrityObject> iSIProjects = null;
    static List<IntegrityObject> iGatewayImportConfigs = null;
    static List<IntegrityObject> iGatewayExportConfigs = null;
    static List<IntegrityObject> iGatewayMappings = null;

    static Integrity i;
    private static Date now;
    static SimpleDateFormat sdf;

    public static final File objectTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "ObjectTemplate.txt");
    public static final File titleTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "title.txt");
    public static final File summaryTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "SummaryTemplate.txt");
    public static final File overviewTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "OverviewTemplate.txt");

    public DocWriterTools(Integrity i) {
        this.i = i;
        this.now = new Date();
        this.sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
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
                        sb.append(adminObj.getObjectType());
                    }
                } else if (paramName.endsWith("overview")) {
                    if (null != adminObj) {
                        if (adminObj instanceof IntegrityType) {
                            sb.append(getTypesOverview());
                        } else if (adminObj instanceof Trigger) {
                            sb.append(getTriggersOverview());
                        } else if (adminObj instanceof IntegrityField) {
                            sb.append(getFieldOverview());
                        } else if (adminObj.getObjectType().equals("Report")) {
                            sb.append(getObjectOverview(iReports, ""));
                        } else if (adminObj.getObjectType().equals("MainW&DProject")) {
                            sb.append(getObjectOverview(iIMProjects, ""));
                        } else if (adminObj.getObjectType().equals("MainCMProject")) {
                            sb.append(getObjectOverview(iSIProjects, ""));
                        } else if (adminObj.getObjectType().equals("State")) {
                            sb.append(getObjectOverview(iStates, ""));
                        } else if (adminObj.getObjectType().equals("Group")) {
                            sb.append(getObjectOverview(iGroups, "isActive"));
                        } else if (adminObj.getObjectType().equals("DynamicGroup")) {
                            sb.append(getObjectOverview(iDynGroups, ""));
                        } else if (adminObj.getObjectType().equals("Query")) {
                            sb.append(getObjectOverview(iQueries, ""));
                        } else if (adminObj.getObjectType().equals("Chart")) {
                            // sb.append(getChartOverview());
                            sb.append(getObjectOverview(iCharts, ""));
                        } else if (adminObj.getObjectType().equals("Viewset")) {
                            sb.append(getObjectOverview(iViewsets, ""));
                        } else if (adminObj.getObjectType().equals("ChangePackageType")) {
                            sb.append(getObjectOverview(iCPTypes, ""));
                        } else if (adminObj.getObjectType().equals("TestVerdict")) {
                            sb.append(getObjectOverview(iTestVerdicts, "Type,isActive"));
                        } else if (adminObj.getObjectType().equals("TestResultField")) {
                            sb.append(getObjectOverview(iTestResultFields, "Type"));
                        } else if (adminObj.getObjectType().equals("Dashboard")) {
                            sb.append(getObjectOverview(iDashboards, ""));
                        } else if (adminObj.getObjectType().equals("GatewayImportConfig")) {
                            sb.append(getObjectOverview(iGatewayImportConfigs, "Type"));
                        } else if (adminObj.getObjectType().equals("GatewayExportConfig")) {
                            sb.append(getObjectOverview(iGatewayExportConfigs, "Type"));
                        } else if (adminObj.getObjectType().equals("GatewayMapping")) {
                            sb.append(getObjectOverview(iGatewayMappings, ""));
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
                    sb.append(i.getAbout(""));
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
        sb.append("     <table class='sortable'>");
        sb.addHeadings("Object,Count");
        sb.append("<tbody>");

        sb.addFieldValue("Main W&D Projects", (iIMProjects.size()));
        sb.addFieldValue("Main CM Projects", (iSIProjects.size()));
        sb.addFieldValue("", "");
        sb.addFieldValue("View Sets", (iViewsets.size()));
        sb.addFieldValue("Groups", (iGroups.size()));
        sb.addFieldValue("Dynamic Groups", (iDynGroups.size()));
        sb.addFieldValue("", "");
        sb.addFieldValue("States", (iStates.size()));
        sb.addFieldValue("Types", (iTypes.size()));
        sb.addFieldValue("Fields", (iFields.size()));
        sb.addFieldValue("Triggers", (iTriggers.size()));
        sb.addFieldValue("", "");
        sb.addFieldValue("Change Package Types", (iCPTypes.size()));
        sb.addFieldValue("Test Verdicts", (iTestVerdicts.size()));
        sb.addFieldValue("Test Result Fields", (iTestResultFields.size()));
        sb.addFieldValue("", "");
        sb.addFieldValue("Charts", (iCharts.size()));
        sb.addFieldValue("Queries", (iQueries.size()));
        sb.addFieldValue("Reports", (iReports.size()));
        sb.addFieldValue("Dashboards", (iDashboards.size()));
        sb.addFieldValue("", "");
        sb.addFieldValue("Gateway Import Configurations", (iGatewayImportConfigs.size()));
        sb.addFieldValue("Gateway Export Configurations", (iGatewayExportConfigs.size()));
        sb.addFieldValue("Gateway Mappings", (iGatewayMappings.size()));

        // Close out the table
        sb.append("</tbody></table>");

        return sb.toString();
    }

    private static String getTypesOverview() {
        StringObj sb = new StringObj();

        // Summary heading line
        sb.append("<table class='sortable'>");
        sb.addHeadings("Name,Description,Change Packages,Permitted Groups,Time Tracking,Show Workflow,Copy Tree,Branch,Label");
        sb.append("<tbody>");

        // Print out the summary about each item type
        for (IntegrityType iType : iTypes) {
            sb.append((" <tr>"));
            sb.addTDborder("<a href='Types/" + iType.getPosition() + ".htm'>" + iType.getName() + "</a>");
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(iType.getDescription()));
            sb.addTDborder((iType.getAllowChangePackages() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder(iType.getPermittedGroups());
            sb.addTDborder((iType.getTimeTrackingEnabled() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getShowWorkflow() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getCopyTreeEnabled() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getBranchEnabled() ? "&#10003;" : "&nbsp;"));
            sb.addTDborder((iType.getLabelEnabled() ? "&#10003;" : "&nbsp;"));
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

    private static String getObjectOverview(List<IntegrityObject> objectList, String additionalColumns) {
        StringObj sb = new StringObj();
        // Summary heading line
        sb.append(("<table class='sortable'>"));
        String headings = "ID,Name,Description" + (additionalColumns.isEmpty() ? "" : "," + additionalColumns);
        sb.addHeadings(headings);
        sb.append(("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityObject object : objectList) {
            sb.append((" <tr>"));
            sb.addTDborder(object.getPosition());
            sb.addTDborder("<a href='" + object.getDirectory() + "/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
            if (additionalColumns.contains("Type")) {
                sb.addTDborder(object.getType());
            }
            if (additionalColumns.contains("isActive")) {
                sb.addTDborder(object.getIsActive());
            }
            sb.append((" </tr>"));
        }
        sb.append(("</tbody></table>"));

        return sb.toString();
    }

    // Delete all the template files
    public void cleanupTempFiles() {
        titleTemplate.delete();
        overviewTemplate.delete();
        objectTemplate.delete();
        summaryTemplate.delete();
    }
}
