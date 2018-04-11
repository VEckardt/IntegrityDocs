package com.ptc.services.utilities.docgen;

import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DocWriter {

    private final List<IntegrityType> iTypes;
    private final List<Trigger> iTriggers;
    private final List<Query> iQueries;
    private final List<Viewset> iViewsets;
    private final List<Chart> iCharts;
    private final Date now;
    private final SimpleDateFormat sdf;
    private final String svrInfo;
    private static final File tocHtmlFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "navi.htm");
    private static final File indexFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "index.htm");
    private static final File titleTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "title.txt");
    private static final File overviewTemplate = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "overview.txt");
    private static final File typeTemplate = new File(IntegrityDocs.TYPES_DIR + IntegrityDocs.fs + "TypeTemplate.txt");
    private static final File triggerTemplate = new File(IntegrityDocs.TRIGGERS_DIR + IntegrityDocs.fs + "TriggerTemplate.txt");
    private static final File queryTemplate = new File(IntegrityDocs.QUERIES_DIR + IntegrityDocs.fs + "QueryTemplate.txt");
    private static final File viewsetTemplate = new File(IntegrityDocs.VIEWSETS_DIR + IntegrityDocs.fs + "ViewsetTemplate.txt");
    private static final File chartTemplate = new File(IntegrityDocs.CHARTS_DIR + IntegrityDocs.fs + "ChartTemplate.txt");

    public DocWriter(String hostInfo,
            List<IntegrityType> typeList,
            List<Trigger> triggersList,
            List<Query> queriesList,
            List<Viewset> viewSetsList,
            List<Chart> chartsList
    ) {
        svrInfo = hostInfo;
        iTypes = typeList;
        iTriggers = triggersList;
        iQueries = queriesList;
        iViewsets = viewSetsList;
        iCharts = chartsList;
        now = new Date();
        sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
    }

    // Resolves the parameterized report values
    private String getFormattedContent(String str, Object adminObj) {
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
                    sb.append(svrInfo);
                } else if ("now".equals(paramName)) {
                    sb.append(sdf.format(now));
                } else if ("type".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(((IntegrityType) adminObj).getName());
                    }
                } else if ("type-overview".equals(paramName)) {
                    sb.append(getTypesOverview());
                } else if ("type-detail".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(getTypeDetails(((IntegrityType) adminObj)));
                    }
                } else if ("trigger".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(((Trigger) adminObj).getName());
                    }
                } else if ("query".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(((Query) adminObj).getName());
                    }
                } else if ("viewset".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(((Viewset) adminObj).getName());
                    }
                } else if ("chart".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(((Chart) adminObj).getName());
                    }
                } else if ("trigger-overview".equals(paramName)) {
                    sb.append(getTriggersOverview());
                } else if ("trigger-detail".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(getTriggerDetails(((Trigger) adminObj)));
                    }
                } else if ("query-overview".equals(paramName)) {
                    // sb.append(getQueryOverview());
                } else if ("query-detail".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(getQueryDetails(((Query) adminObj)));
                    }
                } else if ("viewset-overview".equals(paramName)) {
                    // sb.append(getQueryOverview());
                } else if ("viewset-detail".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(getViewsetDetails(((Viewset) adminObj)));
                    }
                } else if ("chart-overview".equals(paramName)) {
                    // sb.append(getQueryOverview());
                } else if ("chart-detail".equals(paramName)) {
                    if (null != adminObj) {
                        sb.append(getChartDetails(((Chart) adminObj)));
                    }
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

    public void publish() throws IOException {
        // First lets publish the report title
        BufferedReader reader = new BufferedReader(new FileReader(titleTemplate));
        BufferedWriter writer = new BufferedWriter(new FileWriter(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "title.htm"));
        String line;
        while (null != (line = reader.readLine())) {
            writer.write(appendNewLine(getFormattedContent(line, null)));
        }
        writer.flush();
        writer.close();
        reader.close();

        // Open the table of content files
        BufferedWriter naviHtm = new BufferedWriter(new FileWriter(tocHtmlFile));
        BufferedWriter indexHtm = new BufferedWriter(new FileWriter(indexFile));

//        whtdata0xml.write(appendNewLine("<?xml version='1.0' encoding='utf-8' ?>"));
//        whtdata0xml.write(appendNewLine("<tocdata>"));
//        whtdata0xml.write(appendNewLine("<book name=\"Integrity Docs (" + svrInfo + ")\">"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Title\" url=\"WorkflowDocs/title.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Overview\" url=\"WorkflowDocs/overview.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <book name=\"Types\">"));
        addFileContent(naviHtm, IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "navi_start.htm");
        addFileContent(indexHtm, IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "index_start.htm");

        indexHtm.write(appendNewLine("Integrity Docs (" + svrInfo + ")"));

        // navi file
        // whtdata0htm.write(appendNewLine("<html>"));
        // whtdata0htm.write(appendNewLine("<body>"));
        // whtdata0htm.write(appendNewLine("<script language='javascript' src='whtdata.js'/>"));
        // whtdata0htm.write(appendNewLine("<script language='javascript'>"));
        // whtdata0htm.write(appendNewLine("<!--"));
        // whtdata0htm.write(appendNewLine(" aTE(1," + (null != iTriggers && iTriggers.size() > 0 ? 2 + iTypes.size() + iTriggers.size() : 1 + iTypes.size())
        //         + ",'Integrity Docs (" + svrInfo + ")');"));
        // whtdata0htm.write(appendNewLine("   aTE(2,0,'Title','WorkflowDocs/title.htm');"));
        // whtdata0htm.write(appendNewLine("   aTE(2,0,'Overview','WorkflowDocs/overview.htm');"));
        // whtdata0htm.write(appendNewLine("   aTE(1," + iTypes.size() + ",'Types');"));
        naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Types\">Types"));

        // overview.htm
        naviHtm.write(appendNewLine("<ul><li title=\"Type Overview\" data-context=\"20\"><a href=\"WorkflowDocs/overview.htm\" target=\"topic\">Overview</a></li></ul>"));

        for (Iterator<IntegrityType> it = iTypes.iterator(); it.hasNext();) {
            IntegrityType type = it.next();
            // indexHtm.write(appendNewLine("	<item name=\"" + type.getName()
            //         + "\" url=\"WorkflowDocs/Types/" + type.getPosition() + ".htm\" />"));
            // naviHtm.write(appendNewLine("     aTE(2,0,'" + type.getName() + "','WorkflowDocs/Types/" + type.getPosition() + ".htm');"));

            naviHtm.write(appendNewLine("<ul><li title=\"" + type.getName() + "\" data-context=\"20\"><a href=\"WorkflowDocs/Types/" + type.getPosition() + ".htm\" target=\"topic\">" + type.getName() + "</a></li></ul>"));

            // Publish the individual type details
            BufferedReader typeReader = new BufferedReader(new FileReader(typeTemplate));
            BufferedWriter typeWriter = new BufferedWriter(new FileWriter(IntegrityDocs.TYPES_DIR + IntegrityDocs.fs + type.getPosition() + ".htm"));
            while (null != (line = typeReader.readLine())) {
                typeWriter.write(appendNewLine(getFormattedContent(line, type)));
            }
            typeWriter.flush();
            typeWriter.close();
            typeReader.close();
        }

        naviHtm.write(appendNewLine("</li>"));
        // whtdata0xml.write(appendNewLine("  </book>"));

        // Next lets publish the types overview
        reader = new BufferedReader(new FileReader(overviewTemplate));
        writer = new BufferedWriter(new FileWriter(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "overview.htm"));
        while (null != (line = reader.readLine())) {
            writer.write(appendNewLine(getFormattedContent(line, null)));
        }
        writer.flush();
        writer.close();
        reader.close();

        // Publish triggers, if appropriate...
        if (null != iTriggers && iTriggers.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Triggers\">Triggers"));

            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (Iterator<Trigger> it = iTriggers.iterator(); it.hasNext();) {
                Trigger trigger = it.next();
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // naviHtm.write(appendNewLine("     aTE(2,0,'" + trigger.getName() + "','WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm');"));
                naviHtm.write(appendNewLine("<ul><li title=\"New Item3\" data-context=\"20\"><a href=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" target=\"topic\">" + trigger.getName() + "</a></li></ul>"));
                // Publish the individual trigger details
                BufferedReader triggerReader = new BufferedReader(new FileReader(triggerTemplate));
                BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(IntegrityDocs.TRIGGERS_DIR + IntegrityDocs.fs + trigger.getPosition() + ".htm"));
                while (null != (line = triggerReader.readLine())) {
                    triggerWriter.write(appendNewLine(getFormattedContent(line, trigger)));
                }
                triggerWriter.flush();
                triggerWriter.close();
                triggerReader.close();
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish Query, if appropriate...
        if (null != iQueries && iQueries.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Queries\">Queries"));

            for (Iterator<Query> it = iQueries.iterator(); it.hasNext();) {
                Query query = it.next();
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // naviHtm.write(appendNewLine("     aTE(2,0,'" + trigger.getName() + "','WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm');"));
                naviHtm.write(appendNewLine("<ul><li title=\"Query\" data-context=\"20\"><a href=\"WorkflowDocs/Queries/" + query.getPosition() + ".htm\" target=\"topic\">" + query.getName() + "</a></li></ul>"));
                // Publish the individual trigger details
                BufferedReader triggerReader = new BufferedReader(new FileReader(queryTemplate));
                BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(IntegrityDocs.QUERIES_DIR + IntegrityDocs.fs + query.getPosition() + ".htm"));
                while (null != (line = triggerReader.readLine())) {
                    triggerWriter.write(appendNewLine(getFormattedContent(line, query)));
                }
                triggerWriter.flush();
                triggerWriter.close();
                triggerReader.close();
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish Viewset, if appropriate...
        if (null != iViewsets && iViewsets.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Viewset\">Viewset"));

            for (Iterator<Viewset> it = iViewsets.iterator(); it.hasNext();) {
                Viewset viewset = it.next();
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // naviHtm.write(appendNewLine("     aTE(2,0,'" + trigger.getName() + "','WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm');"));
                naviHtm.write(appendNewLine("<ul><li title=\"Query\" data-context=\"20\"><a href=\"WorkflowDocs/Viewsets/" + viewset.getPosition() + ".htm\" target=\"topic\">" + viewset.getName() + "</a></li></ul>"));
                // Publish the individual trigger details
                BufferedReader triggerReader = new BufferedReader(new FileReader(viewsetTemplate));
                BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(IntegrityDocs.VIEWSETS_DIR + IntegrityDocs.fs + viewset.getPosition() + ".htm"));
                while (null != (line = triggerReader.readLine())) {
                    triggerWriter.write(appendNewLine(getFormattedContent(line, viewset)));
                }
                triggerWriter.flush();
                triggerWriter.close();
                triggerReader.close();
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish Chart, if appropriate...
        if (null != iCharts && iCharts.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Charts\">Charts"));

            for (Iterator<Chart> it = iCharts.iterator(); it.hasNext();) {
                Chart chart = it.next();
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // naviHtm.write(appendNewLine("     aTE(2,0,'" + trigger.getName() + "','WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm');"));
                naviHtm.write(appendNewLine("<ul><li title=\"Chart\" data-context=\"20\"><a href=\"WorkflowDocs/Charts/" + chart.getPosition() + ".htm\" target=\"topic\">" + chart.getName() + "</a></li></ul>"));
                // Publish the individual trigger details
                BufferedReader triggerReader = new BufferedReader(new FileReader(chartTemplate));
                BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(IntegrityDocs.CHARTS_DIR + IntegrityDocs.fs + chart.getPosition() + ".htm"));
                while (null != (line = triggerReader.readLine())) {
                    triggerWriter.write(appendNewLine(getFormattedContent(line, chart)));
                }
                triggerWriter.flush();
                triggerWriter.close();
                triggerReader.close();
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }
        // whtdata0xml.write(appendNewLine("</book>"));
        // whtdata0xml.write(appendNewLine("</tocdata>"));
        ;
        // write navi end
        addFileContent(indexHtm, IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "index_end.htm");

        // write navi end
        addFileContent(naviHtm, IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "navi_end.htm");

        // Close the table of content files
        naviHtm.flush();
        naviHtm.close();
        indexHtm.flush();
        indexHtm.close();
        // whtdata0xml.flush();
        // whtdata0xml.close();

    }

    private String appendNewLine(String line) {
        return line + IntegrityDocs.nl;
    }

    private void addFileContent(BufferedWriter naviHtm, String sourceFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(sourceFileName));
        String line;
        while (null != (line = reader.readLine())) {
            naviHtm.write(appendNewLine(getFormattedContent(line, null)));
        }
        reader.close();
    }

    private String getTypesOverview() {
        StringBuilder sb = new StringBuilder();

        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        sb.append(appendNewLine(" <tr>"));
        sb.append(appendNewLine("   <th class='heading1'>Name</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Description</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Change Packages</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Permitted Groups</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Time Tracking</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Show Workflow</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Copy Tree</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Branch</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Label</th>"));
        sb.append(appendNewLine(" </tr>"));
        sb.append(appendNewLine(" <tr><td colspan='9'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append(appendNewLine("</thead>"));
        sb.append(appendNewLine("<tfoot>"));
        sb.append(appendNewLine(" <tr><td colspan='9'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append(appendNewLine(" <tr><td colspan='9' class='footer'>Copyright (c) 2012 Parametric Technology Corporation and/or its Subsidiary Companies. All Rights Reserved.</td></tr>"));
        sb.append(appendNewLine("</tfoot>"));
        sb.append(appendNewLine("<tbody>"));

        // Print out the summary about each item type
        for (Iterator<IntegrityType> it = iTypes.iterator(); it.hasNext();) {
            IntegrityType iType = it.next();
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
        sb.append(appendNewLine("</table>"));

        return sb.toString();
    }

    private String getTypeDetails(IntegrityType iType) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        sb.append(appendNewLine("      <tr><td colspan='2'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Created By</td>"));
        sb.append(appendNewLine("        <td>" + iType.getCreatedBy() + " on " + iType.getCreatedDate(sdf) + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Modified By</td>"));
        sb.append(appendNewLine("        <td>" + iType.getModifiedBy() + " on " + iType.getModifiedDate(sdf) + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Description</td>"));
        sb.append(appendNewLine("        <td>" + HyperLinkFactory.convertHyperLinks(iType.getDescription()) + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Administrators</td>"));
        sb.append(appendNewLine("        <td>" + iType.getPermittedAdministrators() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Permitted Groups</td>"));
        sb.append(appendNewLine("        <td>" + iType.getPermittedGroups() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Notification Fields</td>"));
        sb.append(appendNewLine("        <td>" + iType.getNotificationFields() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Change Packages Allowed?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getAllowChangePackages()
                + (iType.getAllowChangePackages() ? "&nbsp;&nbsp;<b>Policy:</b> " + iType.getCreateCPPolicy() : "") + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Copy Tree?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getCopyTreeEnabled()
                + (iType.getCopyTreeEnabled() ? "&nbsp;&nbsp;<b>Rule:</b> " + iType.getCopyTree() : "") + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Branch Allowed?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getBranchEnabled()
                + (iType.getBranchEnabled() ? "&nbsp;&nbsp;<b>Rule:</b> " + iType.getBranch() : "") + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Labelling Allowed?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getLabelEnabled()
                + (iType.getLabelEnabled() ? "&nbsp;&nbsp;<b>Rule:</b> " + iType.getAddLabel() : "") + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Time Tracking Enabled?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getTimeTrackingEnabled() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Show Workflow?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getShowWorkflow() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Backs Projects?</td>"));
        sb.append(appendNewLine("        <td>" + iType.getBacksProject() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Phase Field</td>"));
        sb.append(appendNewLine("        <td>" + iType.getPhaseField() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Presentation Templates</td>"));
        sb.append(appendNewLine("        <td><b>View:</b> " + iType.getViewPresentation() + "&nbsp;&nbsp;<b>Edit:</b> "
                + iType.getEditPresentation() + "&nbsp;&nbsp;<b>Print:</b> " + iType.getPrintPresentation() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Item Editability Rule</td>"));
        sb.append(appendNewLine("        <td>" + iType.getIssueEditability() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        // The output for relationship fields is broken in 2007
        // Only supporting 2009 and newer releases for relationship diagrams		    
        if (APISession.MINOR_VERSION > 9) {
            sb.append(appendNewLine("      <tr>"));
            sb.append(appendNewLine("        <td class='bold_color'>Relationships</td>"));
            sb.append(appendNewLine("        <td><img src=\"" + iType.getPosition() + "_Relationships.jpeg\"/></td>"));
            sb.append(appendNewLine("      </tr>"));
        }
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Visible Fields</td>"));
        sb.append(appendNewLine("        <td>" + iType.getVisibleFields() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Workflow</td>"));
        sb.append(appendNewLine("        <td><img src=\"" + iType.getPosition() + "_Workflow.jpeg\"/></td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>State Transitions</td>"));
        sb.append(appendNewLine("        <td>" + iType.getStateTransitions() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Mandatory Fields</td>"));
        sb.append(appendNewLine("        <td>" + iType.getMandatoryFields() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Field Relationships</td>"));
        sb.append(appendNewLine("        <td>" + iType.getFieldRelationships() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>Type Properties</td>"));
        sb.append(appendNewLine("        <td>&nbsp;" + iType.getTypeProperties() + "</td>"));
        sb.append(appendNewLine("      </tr>"));
        // Close out the type details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getTriggersOverview() {
        StringBuilder sb = new StringBuilder();
        // Summary heading line
        sb.append(appendNewLine("<table class='display'>"));
        sb.append(appendNewLine(" <tr><td colspan='6'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));
        sb.append(appendNewLine(" <tr>"));
        sb.append(appendNewLine("   <th class='heading1'>Position</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Name</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Type</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Description</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Script</th>"));
        sb.append(appendNewLine("   <th class='heading1'>Script Timing</th>"));
        sb.append(appendNewLine(" </tr>"));
        sb.append(appendNewLine(" <tr><td colspan='6'><hr style='color: #d7d7d7; background-color: #d7d7d7; float: aligncenter;' align='center'/></td></tr>"));

        // Print out the summary about each trigger
        for (Iterator<Trigger> it = iTriggers.iterator(); it.hasNext();) {
            Trigger iTrigger = it.next();
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
        sb.append(appendNewLine("</table>"));

        return sb.toString();
    }

    private String getTriggerDetails(Trigger trigger) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
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
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }

    private String getQueryDetails(Query query) {
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        addFieldValue(sb, "Fields", query.getFields());
        addFieldValue(sb, "Description", HyperLinkFactory.convertHyperLinks(query.getDescription()));
        addFieldValue(sb, "QueryDefinition", query.getQueryDefinition());
        addFieldValue(sb, "ShareWith", query.getShareWith());
        addFieldValue(sb, "SortDirection", query.getSortDirection());
        addFieldValue(sb, "SortField", query.getSortField());
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

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

    private void addFieldValue(StringBuilder sb, String fieldName, String value) {
        sb.append(appendNewLine("      <tr>"));
        sb.append(appendNewLine("        <td class='bold_color'>" + fieldName + "</td>"));
        sb.append(appendNewLine("        <td>" + value + "</td>"));
        sb.append(appendNewLine("      </tr>"));

    }

    // Delete all the template files
    public void cleanupTempFiles() {
        titleTemplate.delete();
        overviewTemplate.delete();
        typeTemplate.delete();
        triggerTemplate.delete();
    }
}
