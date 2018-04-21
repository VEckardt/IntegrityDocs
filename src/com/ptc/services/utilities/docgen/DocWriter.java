package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
            List<IntegrityObject> chartsList,
            List<IntegrityObject> groupsList,
            List<IntegrityObject> dynGroupsList,
            List<IntegrityObject> statesList,
            List<IntegrityObject> reportsList,
            List<IntegrityField> fieldList,
            List<IntegrityObject> testVerdictList,
            List<IntegrityObject> testResultFieldList,
            List<IntegrityObject> iDashboardsFieldList,
            List<IntegrityObject> iCPTypesList,
            List<IntegrityObject> iIMProjectsList,
            List<IntegrityObject> iSIProjectsList
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
        iCPTypes = iCPTypesList;
        iIMProjects = iIMProjectsList;
        iSIProjects = iSIProjectsList;
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
        TocWriter indexHtm = new TocWriter(new FileWriter(indexFile));

//        whtdata0xml.write(appendNewLine("<?xml version='1.0' encoding='utf-8' ?>"));
//        whtdata0xml.write(appendNewLine("<tocdata>"));
//        whtdata0xml.write(appendNewLine("<book name=\"Integrity Docs (" + svrInfo + ")\">"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Title\" url=\"WorkflowDocs/title.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Overview\" url=\"WorkflowDocs/overview.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <book name=\"Types\">"));
        naviHtm.addFileContent(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "navi_start.htm");
        indexHtm.addFileContent(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "index_start.htm");

        indexHtm.write(appendNewLine("Integrity Docs (" + i.getHostName() + ":" + i.getPort() + ")  <small>as of " + getNow() + "</small>"));

        // navi file
        naviHtm.addLeaf("Title");
        naviHtm.addLeaf("Summary");

        writeSummary();

        // Projects
        naviHtm.publishObject(iIMProjects);
        naviHtm.publishObject(iSIProjects);
        
        // Part 1: Publish Viewset, if appropriate...
        naviHtm.publishObject(iViewsets);

        // Part 2: Publish Group, if appropriate...
        naviHtm.publishObject(iGroups);

        // Part 3: Publish Dynamic Groups, if appropriate...
        naviHtm.publishObject(iDynGroups);

        // Part 4: Publish Dynamic Groups, if appropriate...

        // Part 5: Publish States, if appropriate...
        naviHtm.publishObject(iStates);

        // Part 7: Publish Types , if appropriate...
        if (null != iTypes && iTypes.size() > 0) {

            // sort at first:
            iTypes.sort((IntegrityType m1, IntegrityType m2) -> m1.getTypeClassGroup().compareTo(m2.getTypeClassGroup()));

            naviHtm.addBook(iTypes.get(0));

            // overview.htm
            naviHtm.addOverview(iTypes.get(0));
            String typeClassGroup = "";

            for (IntegrityType object : iTypes) {
                // indexHtm.write(appendNewLine("	<item name=\"" + type.getName()
                //         + "\" url=\"WorkflowDocs/Types/" + type.getPosition() + ".htm\" />"));
                // Publish the individual type details
                if (!object.getTypeClassGroup().equals(typeClassGroup)) {
                    if (!typeClassGroup.isEmpty()) {
                        naviHtm.endBook();
                    }
                    naviHtm.addBook(object.getTypeClassGroup() + "s");
                    typeClassGroup = object.getTypeClassGroup();
                }
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            naviHtm.addOverviewCloser();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // naviHtm.addOverviewData(iTypes.get(0));
            naviHtm.endBook();
        }

        // Part 8: Publish Fields, if appropriate...
        if (null != iFields && iFields.size() > 0) {

            // sort at first:
            iFields.sort((IntegrityField m1, IntegrityField m2) -> m1.getTypeClassGroup().compareTo(m2.getTypeClassGroup()));

            naviHtm.addBook(iFields.get(0));

            naviHtm.addOverview(iFields.get(0));
            String typeClassGroup = "";
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (IntegrityField object : iFields) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                if (!object.getTypeClassGroup().equals(typeClassGroup)) {
                    if (!typeClassGroup.isEmpty()) {
                        naviHtm.endBook();
                    }
                    naviHtm.addBook(object.getTypeClassGroup() + "s");
                    typeClassGroup = object.getTypeClassGroup();
                }
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            naviHtm.addOverviewCloser();
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
        }

        // Part 9: Publish triggers, if appropriate...
        if (null != iTriggers && iTriggers.size() > 0) {

            // sort at first:
            iTriggers.sort((Trigger m1, Trigger m2) -> m2.getTypeClassGroup().compareTo(m1.getTypeClassGroup()));

            naviHtm.addBook(iTriggers.get(0));

            naviHtm.addOverview(iTriggers.get(0));
            String typeClassGroup = "";
            // whtdata0xml.write(appendNewLine("  <book name=\"Triggers\" >"));
            for (Trigger object : iTriggers) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                if (!object.getTypeClassGroup().equals(typeClassGroup)) {
                    if (!typeClassGroup.isEmpty()) {
                        naviHtm.endBook();
                    }
                    naviHtm.addBook(object.getTypeClassGroup());
                    typeClassGroup = object.getTypeClassGroup();
                }
                naviHtm.writeObjectHtml(object);
            }
            naviHtm.endBook();
            naviHtm.addOverviewCloser();
            naviHtm.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
            // Next lets publish the types overview
        }

        // Part 10: Publish CPTypes, if appropriate...
        naviHtm.publishObject(iCPTypes);

        // Part 11: Publish TestVerdict, if appropriate...
        naviHtm.publishObject(iTestVerdicts);

        // Part 12: Publish TestResultFields, if appropriate...
        naviHtm.publishObject(iTestResultFields);

        // Part 13: Publish Chart, if appropriate...
        naviHtm.publishObject(iCharts);

        // Part 15: Publish Query, if appropriate...
        naviHtm.publishObject(iQueries);

        // Part 15: Publish Query, if appropriate...
        naviHtm.publishObject(iReports);

        // Part 15: Publish Query, if appropriate...
        naviHtm.publishObject(iDashboards);

        // whtdata0xml.write(appendNewLine("</book>"));
        // whtdata0xml.write(appendNewLine("</tocdata>"));
        // write navi end
        indexHtm.addFileContent(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "index_end.htm");

        // write navi end
        naviHtm.addFileContent(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + "navi_end.htm");

        // Close the table of content files
        naviHtm.flush();
        naviHtm.close();
        indexHtm.flush();
        indexHtm.close();
        // whtdata0xml.flush();
        // whtdata0xml.close();
    }
}
