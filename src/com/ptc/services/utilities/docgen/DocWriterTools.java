/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import static com.ptc.services.utilities.docgen.utils.Utils.addFieldValue;
import static com.ptc.services.utilities.docgen.utils.Utils.addHeadings;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import static com.ptc.services.utilities.docgen.utils.Utils.getObjectName;
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

    static List<IntegrityType> iTypes = null;
    static List<Trigger> iTriggers = null;
    static List<Query> iQueries = null;
    static List<Viewset> iViewsets = null;
    static List<Chart> iCharts = null;
    static List<Group> iGroups = null;
    static List<DynamicGroup> iDynGroups = null;
    static List<IntegrityState> iStates = null;
    static List<Report> iReports = null;
    static List<IntegrityField> iFields = null;
    static List<TestVerdict> iTestVerdicts = null;
    static List<TestResultField> iTestResultFields = null;

    private static Date now;
    static SimpleDateFormat sdf;
    static Integrity i;

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

        String className = getObjectName(adminObj);

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
                    sb.append(className);
                } else if (paramName.endsWith("overview")) {
                    if (null != adminObj) {
                        if (adminObj instanceof IntegrityType) {
                            sb.append(getTypesOverview());
                        } else if (adminObj instanceof Trigger) {
                            sb.append(getTriggersOverview());
                        } else if (adminObj instanceof Report) {
                            sb.append(getReportOverview());
                        } else if (adminObj instanceof IntegrityState) {
                            sb.append(getStateOverview());
                        } else if (adminObj instanceof Group) {
                            sb.append(getGroupOverview());
                        } else if (adminObj instanceof DynamicGroup) {
                            sb.append(getDynGroupOverview());
                        } else if (adminObj instanceof Query) {
                            sb.append(getQueryOverview());
                        } else if (adminObj instanceof Chart) {
                            sb.append(getChartOverview());
                        } else if (adminObj instanceof Viewset) {
                            sb.append(getViewsetOverview());
                        } else if (adminObj instanceof IntegrityField) {
                            sb.append(getFieldOverview());
                        } else if (adminObj instanceof TestVerdict) {
                            sb.append(getTestVerdictOverview());
                        } else if (adminObj instanceof TestResultField) {
                            sb.append(getTestResultFieldOverview());
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
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='sortable'>"));
        addFieldValue(sb, "View Sets", String.valueOf(iViewsets.size()));
        addFieldValue(sb, "Groups", String.valueOf(iGroups.size()));
        addFieldValue(sb, "Dynamic Groups", String.valueOf(iDynGroups.size()));
        addFieldValue(sb, "", "");
        addFieldValue(sb, "States", String.valueOf(iStates.size()));
        addFieldValue(sb, "Types", String.valueOf(iTypes.size()));
        addFieldValue(sb, "Triggers", String.valueOf(iTriggers.size()));
        addFieldValue(sb, "", "");
        addFieldValue(sb, "Test Verdicts", String.valueOf(iTestVerdicts.size()));
        addFieldValue(sb, "Test Result Fields", String.valueOf(iTestResultFields.size()));
        addFieldValue(sb, "", "");
        addFieldValue(sb, "Charts", String.valueOf(iCharts.size()));
        addFieldValue(sb, "Queries", String.valueOf(iQueries.size()));
        addFieldValue(sb, "Reports", String.valueOf(iReports.size()));

        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private static String getTypesOverview() {
        StringBuilder sb = new StringBuilder();

        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "Name,Description,Change Packages,Permitted Groups,Time Tracking,Show Workflow,Copy Tree,Branch,Label");
        sb.append(appendNewLine("<tbody>"));

        // Print out the summary about each item type
        for (IntegrityType iType : iTypes) {
            sb.append(appendNewLine("   <td nowrap class='border'><a href='Types/" + iType.getPosition() + ".htm'>" + iType.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(iType.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + (iType.getAllowChangePackages() ? "&#10003;" : "&nbsp;") + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + iType.getPermittedGroups() + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + (iType.getTimeTrackingEnabled() ? "&#10003;" : "&nbsp;") + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + (iType.getShowWorkflow() ? "&#10003;" : "&nbsp;") + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + (iType.getCopyTreeEnabled() ? "&#10003;" : "&nbsp;") + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + (iType.getBranchEnabled() ? "&#10003;" : "&nbsp;") + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + (iType.getLabelEnabled() ? "&#10003;" : "&nbsp;") + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    public static String getTriggersOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "Position,Name,Type,Description,Script,Script Timing");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Trigger iTrigger : iTriggers) {
            sb.append(appendNewLine("   <td class='border'>" + iTrigger.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Triggers/" + iTrigger.getPosition() + ".htm'>" + iTrigger.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + iTrigger.getType() + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(iTrigger.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + iTrigger.getScript() + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + iTrigger.getScriptTiming() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    public static String getTestResultFieldOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description,Type");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (TestResultField object : iTestResultFields) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='TestVerdicts/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));

            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getType() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getReportOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description,Share With");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Report object : iReports) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Reports/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getShareWith() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getFieldOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description,Type");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityField object : iFields) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Fields/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getType() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getStateOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description,Display Name");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityState object : iStates) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='States/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getDisplayName() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getGroupOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Group object : iGroups) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Groups/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getQueryOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Query object : iQueries) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Queries/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getChartOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Chart object : iCharts) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Charts/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getViewsetOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Viewset object : iViewsets) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Viewsets/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getDynGroupOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (DynamicGroup object : iDynGroups) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='" + object.getDirectory() + "/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private static String getTestVerdictOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='sortable'>"));
        addHeadings(sb, "ID,Name,Description,Type");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (TestVerdict object : iTestVerdicts) {
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='TestVerdicts/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));

            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getVerdictType() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

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
