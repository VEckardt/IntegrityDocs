/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

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

    List<IntegrityType> iTypes = null;
    List<Trigger> iTriggers = null;
    List<Query> iQueries = null;
    List<Viewset> iViewsets = null;
    List<Chart> iCharts = null;
    List<Group> iGroups = null;
    List<DynGroup> iDynGroups = null;
    List<IntegrityState> iStates = null;
    List<Report> iReports = null;
    List<IntegrityField> iFields = null;
    List<TestVerdict> iTestVerdicts = null;

    Date now;
    SimpleDateFormat sdf;
    String svrInfo;

    private static final File objectTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "ObjectTemplate.txt");
    public static final File titleTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "title.txt");
    public static final File summaryTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "SummaryTemplate.txt");
    public static final File overviewTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "OverviewTemplate.txt");

    public DocWriterTools(String svrInfo) {
        this.svrInfo = svrInfo;
        this.now = new Date();
        this.sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
    }

    public void writeObjectHtml(IntegrityAdminObject adminObject, BufferedWriter naviHtm) throws FileNotFoundException, IOException {

        naviHtm.write("<ul><li title=\"" + adminObject.getDirectory() + "\" data-context=\"20\"><a href=\"WorkflowDocs/" + adminObject.getDirectory() + "/" + adminObject.getPosition() + ".htm\" target=\"topic\">" + adminObject.getName() + "</a></li></ul>");

        File directory = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + adminObject.getDirectory());

        try (BufferedReader triggerReader = new BufferedReader(new FileReader(objectTemplate))) {
            BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(directory + IntegrityDocs.fs + adminObject.getPosition() + ".htm"));
            String line;
            while (null != (line = triggerReader.readLine())) {
                triggerWriter.write((getFormattedContent(line, adminObject)));
            }
            triggerWriter.flush();
            triggerWriter.close();
        }
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
    public String getFormattedContent(String str, IntegrityAdminObject adminObj) {
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        int currentIndex;

        String className = "";

        if (adminObj != null) {
            className = adminObj.getClass().getSimpleName().replace("Integrity", "");
            // System.out.println("className = " + className);
        }

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
                    sb.append(svrInfo);
                } else if ("now".equals(paramName)) {
                    sb.append(sdf.format(now));
                } else if ("objecttype".equals(paramName)) {
                    sb.append(className);
                } else if ("type".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(((IntegrityType) adminObj).getName());
                    }
                } else if (paramName.endsWith("overview")) {
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
                    } else if (adminObj instanceof DynGroup) {
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
                    }

                } else if ("details".equals(paramName)) {
                    if (null != adminObj) {
                        if (adminObj instanceof IntegrityType) {
                            sb.append(getTypeDetails(((IntegrityType) adminObj)));
                        } else if (adminObj instanceof Trigger) {
                            sb.append(getTriggerDetails(((Trigger) adminObj)));
                        } else if (adminObj instanceof Query) {
                            sb.append(getQueryDetails(((Query) adminObj)));
                        } else if (adminObj instanceof Viewset) {
                            sb.append(getViewsetDetails(((Viewset) adminObj)));
                        } else if (adminObj instanceof Chart) {
                            sb.append(getChartDetails(((Chart) adminObj)));
                        } else if (adminObj instanceof Group) {
                            sb.append(getGroupDetails(((Group) adminObj)));
                        } else if (adminObj instanceof DynGroup) {
                            sb.append(getDynGroupDetails(((DynGroup) adminObj)));
                        } else if (adminObj instanceof Report) {
                            sb.append(getReportDetails(((Report) adminObj)));
                        } else if (adminObj instanceof IntegrityState) {
                            sb.append(getStateDetails(((IntegrityState) adminObj)));
                        } else if (adminObj instanceof IntegrityField) {
                            sb.append(getFieldDetails(((IntegrityField) adminObj)));
                        } else if (adminObj instanceof TestVerdict) {
                            sb.append(getTestVerdictDetails(((TestVerdict) adminObj)));
                        }
                    }
                } else if ("objectname".equals(paramName)) {
                    if (null != adminObj) {

                        if (adminObj instanceof IntegrityType) {
                            sb.append(((IntegrityType) adminObj).getName());
                        } else if (adminObj instanceof Trigger) {
                            sb.append(((Trigger) adminObj).getName());
                        } else if (adminObj instanceof Query) {
                            sb.append(((Query) adminObj).getName());
                        } else if (adminObj instanceof Viewset) {
                            sb.append(((Viewset) adminObj).getName());
                        } else if (adminObj instanceof Chart) {
                            sb.append(((Chart) adminObj).getName());
                        } else if (adminObj instanceof Group) {
                            sb.append(((Group) adminObj).getName());
                        } else if (adminObj instanceof DynGroup) {
                            sb.append(((DynGroup) adminObj).getName());
                        } else if (adminObj instanceof Report) {
                            sb.append(((Report) adminObj).getName());
                        } else if (adminObj instanceof IntegrityState) {
                            sb.append(((IntegrityState) adminObj).getName());
                        } else if (adminObj instanceof IntegrityField) {
                            sb.append(((IntegrityField) adminObj).getName());
                        } else if (adminObj instanceof TestVerdict) {
                            sb.append(((TestVerdict) adminObj).getName());
                        }
                    }
                } else if ("summary".equals(paramName)) {
                    sb.append(getSummary());
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

    private String getTypesOverview() {
        StringBuilder sb = new StringBuilder();

        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "Name,Description,Change Packages,Permitted Groups,Time Tracking,Show Workflow,Copy Tree,Branch,Label");
        sb.append(appendNewLine("<tbody>"));

        // Print out the summary about each item type
        for (IntegrityType iType : iTypes) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
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

    private void addHeadings(StringBuilder sb, String fields) {
        int cols = fields.split(",").length;
        sb.append(appendNewLine(" <tr><td colspan='" + cols + "'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
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
        sb.append(appendNewLine(" <tr><td colspan='" + cols + "' class='footer'>Copyright (c) 2018 PTC Inc. All Rights Reserved.</td></tr>"));
        sb.append(appendNewLine("</tfoot>"));
    }

    private String getTypeDetails(IntegrityType iType) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        sb.append(appendNewLine("      <tr><td colspan='2'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));

        addFieldValue(sb, "Created By", iType.getCreatedBy() + " on " + iType.getCreatedDate(sdf));
        addFieldValue(sb, "Modified By", iType.getModifiedBy() + " on " + iType.getModifiedDate(sdf));
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(iType.getDescription()));
        addFieldValue(sb, "Administrators", iType.getPermittedAdministrators());
        addFieldValue(sb, "Permitted Groups", iType.getPermittedGroups());
        addFieldValue(sb, "Notification Fields", iType.getNotificationFields());
        addFieldValue(sb, "Change Packages Allowed?", iType.getAllowChangePackages()
                + (iType.getAllowChangePackages() ? "&nbsp;&nbsp;<b>Policy:</b> " + iType.getCreateCPPolicy() : ""));
        addFieldValue(sb, "Copy Tree?", iType.getCopyTreeEnabled()
                + (iType.getCopyTreeEnabled() ? "&nbsp;&nbsp;<b>Rule:</b> " + iType.getCopyTree() : ""));
        addFieldValue(sb, "Branch Allowed?", iType.getBranchEnabled()
                + (iType.getBranchEnabled() ? "&nbsp;&nbsp;<b>Rule:</b> " + iType.getBranch() : ""));
        addFieldValue(sb, "Labelling Allowed?", iType.getLabelEnabled()
                + (iType.getLabelEnabled() ? "&nbsp;&nbsp;<b>Rule:</b> " + iType.getAddLabel() : ""));
        addFieldValue(sb, "Time Tracking Enabled?", String.valueOf(iType.getTimeTrackingEnabled()));
        addFieldValue(sb, "Show Workflow?", String.valueOf(iType.getShowWorkflow()));
        addFieldValue(sb, "Backs Projects?", String.valueOf(iType.getBacksProject()));
        addFieldValue(sb, "Phase Field", iType.getPhaseField());
        addFieldValue(sb, "Presentation Templates", "<b>View:</b> " + iType.getViewPresentation() + "&nbsp;&nbsp;<b>Edit:</b> "
                + iType.getEditPresentation() + "&nbsp;&nbsp;<b>Print:</b> " + iType.getPrintPresentation());
        addFieldValue(sb, "Item Editability Rule", iType.getIssueEditability());

        // The output for relationship fields is broken in 2007
        // Only supporting 2009 and newer releases for relationship diagrams		    
        if (APISession.MINOR_VERSION > 9) {
            addFieldValue(sb, "Relationships", "<img src=\"" + iType.getPosition() + "_Relationships.jpeg\"/>");
        }
        addFieldValue(sb, "Visible Fields", iType.getVisibleFields());
        addFieldValue(sb, "Workflow", "<img src=\"" + iType.getPosition() + "_Workflow.jpeg\"/>");
        addFieldValue(sb, "State Transitions", iType.getStateTransitions());
        addFieldValue(sb, "Mandatory Fields", iType.getMandatoryFields());
        addFieldValue(sb, "Field Relationships", iType.getFieldRelationships());
        addFieldValue(sb, "Type Properties", iType.getTypeProperties());

        // Close out the type details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getTriggersOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "Position,Name,Type,Description,Script,Script Timing");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Trigger iTrigger : iTriggers) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
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

    private String getTriggerDetails(Trigger trigger) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("<table class='display'>"));
        addFieldValue(sb, "Type", trigger.getType());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(trigger.getDescription()));
        if (trigger.getType().equalsIgnoreCase("rule")) {
            addFieldValue(sb, "Rule", trigger.getRule());
        } else if (trigger.getType().equalsIgnoreCase("scheduled")) {
            addFieldValue(sb, "Run As", trigger.getRunAs());
            addFieldValue(sb, "Query", trigger.getQuery());
            addFieldValue(sb, "Frequency", trigger.getFrequency());
        }
        addFieldValue(sb, "Script", trigger.getScript());
        addFieldValue(sb, "Script Timing", trigger.getScriptTiming());
        addFieldValue(sb, "Script Parameters", trigger.getScriptParams());
        addFieldValue(sb, "Assignments", trigger.getAssignments());

        // Close out the triggers details table
        sb.append(appendNewLine("</table>"));

        return sb.toString();
    }

    private String getQueryDetails(Query query) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("<table class='display'>"));
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(query.getDescription()));
        addFieldValue(sb, "QueryDefinition", query.getQueryDefinition());
        addFieldValue(sb, "Fields", query.getFields());
        addFieldValue(sb, "ShareWith", query.getShareWith());
        addFieldValue(sb, "SortField", query.getSortField() + " (" + query.getSortDirection() + ")");
        // Close out the triggers details table
        sb.append(appendNewLine("</table>"));

        return sb.toString();
    }

    private String getViewsetDetails(Viewset viewset) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Name", viewset.getName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(viewset.getDescription()));
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getGroupDetails(Group group) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Name", group.getName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(group.getDescription()));
        addFieldValue(sb, "Is Active", String.valueOf(group.isActive()));
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getSummary() {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "View Sets", String.valueOf(iViewsets.size()));
        addFieldValue(sb, "Groups", String.valueOf(iGroups.size()));
        addFieldValue(sb, "Dynamic Groups", String.valueOf(iDynGroups.size()));
        addFieldValue(sb, "States", String.valueOf(iStates.size()));

        addFieldValue(sb, "Types", String.valueOf(iTypes.size()));
        addFieldValue(sb, "Triggers", String.valueOf(iTriggers.size()));
        addFieldValue(sb, "Charts", String.valueOf(iCharts.size()));
        addFieldValue(sb, "Queries", String.valueOf(iQueries.size()));
        addFieldValue(sb, "Reports", String.valueOf(iReports.size()));

        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getDynGroupDetails(DynGroup group) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Name", group.getName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(group.getDescription()));
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getReportDetails(Report object) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Name", object.getName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(object.getDescription()));
        addFieldValue(sb, "Query", object.getQuery());
        addFieldValue(sb, "ShareWith", object.getShareWith());
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getReportOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description,Share With");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Report object : iReports) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Reports/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getShareWith() + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getFieldOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description,Type");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityField object : iFields) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Fields/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getType()+ "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }    
    
    private String getStateOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (IntegrityState object : iStates) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='States/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getGroupOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Group object : iGroups) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Groups/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getQueryOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Query object : iQueries) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Queries/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getChartOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Chart object : iCharts) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Charts/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getViewsetOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (Viewset object : iViewsets) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='Viewsets/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getDynGroupOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (DynGroup object : iDynGroups) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='DynGroups/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }

    private String getChartDetails(Chart chart) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "ChartType", chart.getChartType());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(chart.getDescription()));
        addFieldValue(sb, "GraphStyle", chart.getGraphStyle());
        addFieldValue(sb, "ShareWith", chart.getShareWith());
        addFieldValue(sb, "Query", chart.getQuery());
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }
    
    private String getTestVerdictDetails(TestVerdict object) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Verdict Type", object.getVerdictType());
        addFieldValue(sb, "Name", object.getName());
        addFieldValue(sb, "Display Name", object.getDisplayName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(object.getDescription()));
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }    
    
    private String getTestVerdictOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        addHeadings(sb, "ID,Name,Description,Type");
        sb.append(appendNewLine("<tbody>"));
        // Print out the summary about each trigger
        for (TestVerdict object : iTestVerdicts) {
            sb.append(appendNewLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getPosition() + "</td>"));
            sb.append(appendNewLine("   <td class='border'><a href='DynGroups/" + object.getPosition() + ".htm'>" + object.getName() + "</a></td>"));
            
            sb.append(appendNewLine("   <td class='border'>" + HyperLinkFactory.convertHyperLinks(object.getDescription()) + "</td>"));
            sb.append(appendNewLine("   <td class='border'>" + object.getVerdictType()+ "</td>"));
            sb.append(appendNewLine(" </tr>"));
        }
        sb.append(appendNewLine("</tbody></table>"));

        return sb.toString();
    }    
    
    private String getFieldDetails(IntegrityField object) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Name", object.getName());
        addFieldValue(sb, "Display Name", object.getDisplayName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(object.getDescription()));
        addFieldValue(sb, "Type", object.getType());
        addFieldValue(sb, "Default Value", object.getDefaultValue());
        addFieldValue(sb, "Editability Rule", object.getEditabilityRule());
        addFieldValue(sb, "Relevance Rule", object.getRelevanceRule());
        // addFieldValue(sb, "Query", object.getQuery());
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }    

    private String getStateDetails(IntegrityState state) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Name", state.getName());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(state.getDescription()));
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    public String appendNewLine(String line) {
        return line + IntegrityDocs.nl;
    }

    private void addFieldValue(StringBuilder sb, String fieldName, String value) {
        sb.append(appendNewLine("<tr><td class='bold_color'>" + fieldName + "</td>"));
        sb.append(appendNewLine("<td>" + value + "</td></tr>"));
    }

    // Delete all the template files
    public void cleanupTempFiles() {
        titleTemplate.delete();
        overviewTemplate.delete();
        objectTemplate.delete();
        summaryTemplate.delete();
    }
}
