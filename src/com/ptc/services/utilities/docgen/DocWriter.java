package com.ptc.services.utilities.docgen;

import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DocWriter extends DocWriterTools {

    private static final File tocHtmlFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "navi.htm");
    private static final File indexFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "index.htm");
    private static final File titleFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "title.htm");

    public DocWriter(String hostInfo,
            List<IntegrityType> typeList,
            List<Trigger> triggersList,
            List<Query> queriesList,
            List<Viewset> viewSetsList,
            List<Chart> chartsList,
            List<Group> groupsList,
            List<DynGroup> dynGroupsList,
            List<IntegrityState> statesList,
            List<Report> reportsList,
            List<IntegrityField> fieldList,
            List<TestVerdict> testVerdictList
    ) {
        super(hostInfo);

        iTypes = typeList;
        iTriggers = triggersList;
        iQueries = queriesList;
        iViewsets = viewSetsList;
        iCharts = chartsList;
        iGroups = groupsList;
        iDynGroups = dynGroupsList;
        iStates = statesList;
        iReports = reportsList;
        iFields = fieldList;
        iTestVerdicts = testVerdictList;
    }

    public void publish() throws IOException {
        // First lets publish the report title
        BufferedReader reader = new BufferedReader(new FileReader(titleTemplate));
        BufferedWriter writer = new BufferedWriter(new FileWriter(titleFile));
        String line;
        while (null != (line = reader.readLine())) {
            writer.write(getFormattedContent(line, null));
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

        indexHtm.write(appendNewLine("Integrity Docs (" + svrInfo + ") <small>as of " + sdf.format(now) + "</small>"));

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
        naviHtm.write("<li title=\"Title\" data-context=\"20\"><a href=\"title.htm\" target=\"topic\">" + "Title" + "</a></li>");
        naviHtm.write("<li title=\"Summary\" data-context=\"20\"><a href=\"summary.htm\" target=\"topic\">" + "Summary" + "</a></li>");

        writeSummary();

        // Part 1: Publish Viewset, if appropriate...
        if (null != iViewsets && iViewsets.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Viewsets\">Viewsets"));
            addOverviewHeader("Viewset", naviHtm);
            for (Viewset object : iViewsets) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(object, naviHtm);
            }
            addOverviewData("Viewset", iViewsets.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 2: Publish Group, if appropriate...
        if (null != iGroups && iGroups.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Groups\">Groups"));
            addOverviewHeader("Group", naviHtm);
            for (Group object : iGroups) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(object, naviHtm);
            }
            addOverviewData("Group", iGroups.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish Dynamic Groups, if appropriate...
        if (null != iDynGroups && iDynGroups.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Dynamic Groups\">Dynamic Groups"));
            addOverviewHeader("DynGroup", naviHtm);
            for (DynGroup object : iDynGroups) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(object, naviHtm);
            }
            addOverviewData("DynGroup", iDynGroups.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish States, if appropriate...
        if (null != iStates && iStates.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"States\">States"));
            addOverviewHeader("State", naviHtm);
            for (IntegrityState object : iStates) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(object, naviHtm);
            }
            addOverviewData("State", iStates.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 7: Publish Types , if appropriate...
        if (null != iTypes && iTypes.size() > 0) {

            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Types\">Types"));

            // overview.htm
            addOverviewHeader("Type", naviHtm);

            for (IntegrityType type : iTypes) {
                // indexHtm.write(appendNewLine("	<item name=\"" + type.getName()
                //         + "\" url=\"WorkflowDocs/Types/" + type.getPosition() + ".htm\" />"));
                // Publish the individual type details
                writeObjectHtml(type, naviHtm);
            }

            // whtdata0xml.write(appendNewLine("  </book>"));
            addOverviewData("Type", iTypes.get(0));
            naviHtm.write(appendNewLine("</li>"));
        }
        
        // Part 8: Publish Fields, if appropriate...
        if (null != iFields && iFields.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Fields\">Fields"));

            addOverviewHeader("Field", naviHtm);
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (IntegrityField field : iFields) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(field, naviHtm);
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData("Field", iFields.get(0));
        }        

        // Part 9: Publish triggers, if appropriate...
        if (null != iTriggers && iTriggers.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Triggers\">Triggers"));

            addOverviewHeader("Trigger", naviHtm);
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (Trigger trigger : iTriggers) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(trigger, naviHtm);
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData("Trigger", iTriggers.get(0));
        }
        
        // Part 11: Publish TestVerdict, if appropriate...
        if (null != iTestVerdicts && iTestVerdicts.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Test Verdicts\">Test Verdicts"));

            addOverviewHeader("TestVerdict", naviHtm);
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (TestVerdict object : iTestVerdicts) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(object, naviHtm);
            }
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData("TestVerdict", iTestVerdicts.get(0));
        }        

        // Part 13: Publish Chart, if appropriate...
        if (null != iCharts && iCharts.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Charts\">Charts"));
            addOverviewHeader("Chart", naviHtm);
            for (Chart chart : iCharts) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(chart, naviHtm);
            }
            addOverviewData("Chart", iCharts.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 15: Publish Query, if appropriate...
        if (null != iQueries && iQueries.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Queries\">Queries"));
            addOverviewHeader("Query", naviHtm);
            for (Query query : iQueries) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(query, naviHtm);
            }
            addOverviewData("Query", iQueries.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 15: Publish Query, if appropriate...
        if (null != iReports && iReports.size() > 0) {
            naviHtm.write(appendNewLine("<li class=\"hs-book\" title=\"Reports\">Reports"));
            addOverviewHeader("Report", naviHtm);
            for (Iterator<Report> it = iReports.iterator(); it.hasNext();) {
                Report object = it.next();
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                writeObjectHtml(object, naviHtm);
            }
            addOverviewData("Report", iReports.get(0));
            naviHtm.write(appendNewLine("</li>"));
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // whtdata0xml.write(appendNewLine("</book>"));
        // whtdata0xml.write(appendNewLine("</tocdata>"));
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

    private void addFileContent(BufferedWriter naviHtm, String sourceFileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
            String line;
            while (null != (line = reader.readLine())) {
                naviHtm.write(appendNewLine(getFormattedContent(line, null)));
            }
        }
    }

    private void addOverviewHeader(String objectName, BufferedWriter naviHtm) throws IOException {
        naviHtm.write(appendNewLine("<ul><li title=\"" + objectName + " Overview\" data-context=\"20\"><a href=\"WorkflowDocs/" + objectName + "_overview.htm\" target=\"topic\">Overview</a></li></ul>"));
    }

    private void addOverviewData(String objectName, IntegrityAdminObject adminObject) throws FileNotFoundException, IOException {
        try ( // Next lets publish the types overview
                BufferedReader reader = new BufferedReader(new FileReader(overviewTemplate))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + objectName + "_overview.htm"));
            String line;
            while (null != (line = reader.readLine())) {
                writer.write(appendNewLine(getFormattedContent(line, adminObject)));
            }
            writer.flush();
            writer.close();
        }
    }
}
