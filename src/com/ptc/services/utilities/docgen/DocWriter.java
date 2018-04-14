package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
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

    public DocWriter(Integrity i,
            List<IntegrityType> typeList,
            List<Trigger> triggersList,
            List<IntegrityObject> queriesList,
            List<IntegrityObject> viewSetsList,
            List<Chart> chartsList,
            List<IntegrityObject> groupsList,
            List<IntegrityObject> dynGroupsList,
            List<IntegrityObject> statesList,
            List<IntegrityObject> reportsList,
            List<IntegrityField> fieldList,
            List<IntegrityObject> testVerdictList,
            List<IntegrityObject> testResultFieldList,
            List<IntegrityObject> iDashboardsFieldList
    ) {
        super(i);

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
        iTestResultFields = testResultFieldList;
        iDashboards = iDashboardsFieldList;
    }

    public void publish() throws IOException {
        // First lets publish the report title
        try (
                BufferedReader reader = new BufferedReader(new FileReader(titleTemplate))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(titleFile));
            String line;
            while (null != (line = reader.readLine())) {
                writer.write(getFormattedContent(line, null));
            }
            writer.flush();
            writer.close();
        }

        // Open the table of content files
        TocWriter naviHtm = new TocWriter(new FileWriter(tocHtmlFile));
        BufferedWriter indexHtm = new BufferedWriter(new FileWriter(indexFile));

//        whtdata0xml.write(appendNewLine("<?xml version='1.0' encoding='utf-8' ?>"));
//        whtdata0xml.write(appendNewLine("<tocdata>"));
//        whtdata0xml.write(appendNewLine("<book name=\"Integrity Docs (" + svrInfo + ")\">"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Title\" url=\"WorkflowDocs/title.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Overview\" url=\"WorkflowDocs/overview.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <book name=\"Types\">"));
        addFileContent(naviHtm, IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "navi_start.htm");
        addFileContent(indexHtm, IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "index_start.htm");

        indexHtm.write(appendNewLine("Integrity Docs (" + i.getHostName() + ":" + i.getPort() + ")  <small>as of " + getNow() + "</small>"));

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
        naviHtm.addLeaf("Title");
        naviHtm.addLeaf("Summary");

        writeSummary();

        // publishObject (naviHtm, iViewsets);
        // Part 1: Publish Viewset, if appropriate...
        if (null != iViewsets && iViewsets.size() > 0) {
            naviHtm.addBook(iViewsets.get(0));
            naviHtm.addOverviewHeader(iViewsets.get(0));
            for (IntegrityObject object : iViewsets) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iViewsets.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 2: Publish Group, if appropriate...
        if (null != iGroups && iGroups.size() > 0) {
            naviHtm.addBook(iGroups.get(0));
            naviHtm.addOverviewHeader(iGroups.get(0));
            for (IntegrityObject object : iGroups) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iGroups.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish Dynamic Groups, if appropriate...
        if (null != iDynGroups && iDynGroups.size() > 0) {
            naviHtm.addBook(iDynGroups.get(0));
            naviHtm.addOverviewHeader(iDynGroups.get(0));
            for (IntegrityObject object : iDynGroups) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iDynGroups.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Publish States, if appropriate...
        if (null != iStates && iStates.size() > 0) {
            naviHtm.addBook(iStates.get(0));
            naviHtm.addOverviewHeader(iStates.get(0));
            for (IntegrityObject object : iStates) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iStates.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 7: Publish Types , if appropriate...
        if (null != iTypes && iTypes.size() > 0) {

            naviHtm.addBook(iTypes.get(0));

            // overview.htm
            naviHtm.addOverviewHeader(iTypes.get(0));

            for (IntegrityType object : iTypes) {
                // indexHtm.write(appendNewLine("	<item name=\"" + type.getName()
                //         + "\" url=\"WorkflowDocs/Types/" + type.getPosition() + ".htm\" />"));
                // Publish the individual type details
                naviHtm.writeObjectHtml(object);
            }

            // whtdata0xml.write(appendNewLine("  </book>"));
            addOverviewData(iTypes.get(0));
            naviHtm.endBook();
        }

        // Part 8: Publish Fields, if appropriate...
        if (null != iFields && iFields.size() > 0) {
            naviHtm.addBook(iFields.get(0));

            naviHtm.addOverviewHeader(iFields.get(0));
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (IntegrityField object : iFields) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData(iFields.get(0));
        }

        // Part 9: Publish triggers, if appropriate...
        if (null != iTriggers && iTriggers.size() > 0) {
            naviHtm.addBook(iTriggers.get(0));

            naviHtm.addOverviewHeader(iTriggers.get(0));
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (Trigger object : iTriggers) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData(iTriggers.get(0));
        }

        // Part 11: Publish TestVerdict, if appropriate...
        if (null != iTestVerdicts && iTestVerdicts.size() > 0) {
            naviHtm.addBook(iTestVerdicts.get(0));

            naviHtm.addOverviewHeader(iTestVerdicts.get(0));
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (IntegrityObject object : iTestVerdicts) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData(iTestVerdicts.get(0));
        }

        // Part 11: Publish TestVerdict, if appropriate...
        if (null != iTestResultFields && iTestResultFields.size() > 0) {
            naviHtm.addBook(iTestResultFields.get(0));

            naviHtm.addOverviewHeader(iTestResultFields.get(0));
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (IntegrityObject object : iTestResultFields) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
            addOverviewData(iTestResultFields.get(0));
        }

        // Part 13: Publish Chart, if appropriate...
        if (null != iCharts && iCharts.size() > 0) {
            naviHtm.addBook(iCharts.get(0));
            naviHtm.addOverviewHeader(iCharts.get(0));
            for (Chart object : iCharts) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iCharts.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 15: Publish Query, if appropriate...
        if (null != iQueries && iQueries.size() > 0) {
            naviHtm.addBook(iQueries.get(0));
            naviHtm.addOverviewHeader(iQueries.get(0));
            for (IntegrityObject object : iQueries) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iQueries.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 15: Publish Query, if appropriate...
        if (null != iReports && iReports.size() > 0) {
            naviHtm.addBook(iReports.get(0));
            naviHtm.addOverviewHeader(iReports.get(0));
            for (IntegrityObject object : iReports) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iReports.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }

        // Part 15: Publish Query, if appropriate...
        // publishObject(TocWriter naviHtm, List<IntegrityAdminObject> aol)
        if (null != iDashboards && iDashboards.size() > 0) {
            naviHtm.addBook(iDashboards.get(0));
            naviHtm.addOverviewHeader(iDashboards.get(0));
            for (IntegrityObject object : iDashboards) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(iDashboards.get(0));
            naviHtm.endBook();
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
    
    private void publishObject(TocWriter naviHtm, List<IntegrityAdminObject> aol) throws IOException {
        // Part 1: Publish Viewset, if appropriate...
        if (null != aol && aol.size() > 0) {
            naviHtm.addBook(aol.get(0));
            naviHtm.addOverviewHeader(aol.get(0));
            for (IntegrityAdminObject object : aol) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                naviHtm.writeObjectHtml(object);
            }
            addOverviewData(aol.get(0));
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }
    }

    private void addFileContent(BufferedWriter naviHtm, String sourceFileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
            String line;
            while (null != (line = reader.readLine())) {
                naviHtm.write(appendNewLine(getFormattedContent(line, null)));
            }
        }
    }

//    private void addOverviewHeader(String objectName, BufferedWriter naviHtm) throws IOException {
//        naviHtm.write(appendNewLine("<ul><li title=\"" + objectName + " Overview\" data-context=\"20\"><a href=\"WorkflowDocs/" + objectName + "_overview.htm\" target=\"topic\">Overview</a></li></ul>"));
//    }
    private void addOverviewData(IntegrityAdminObject adminObject) throws FileNotFoundException, IOException {
        String objectName = adminObject.getObjectType();

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
