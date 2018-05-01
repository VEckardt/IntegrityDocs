package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DocWriter extends DocWriterTools {

    private static final File tocHtmlFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "navi.htm");
    private static final File indexFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "index.htm");
    private static final File titleFile = new File(IntegrityDocs.REPORT_DIR + IntegrityDocs.fs + "title.htm");

    public DocWriter(Integrity i,
            List<IntegrityType> typeList,
            List<IntegrityField> fieldList,
            List<Trigger> triggerList,
            ArrayList<List<IntegrityObject>> iObjectList
    ) {
        super(i);

        DocWriter.iTypes = typeList;
        DocWriter.iFields = fieldList;
        DocWriter.iTriggers = triggerList;
        DocWriter.iObjectList = iObjectList;
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

        // Publish the first part of the Types
        for (Types type : Types.values()) {
            if (type.getGrp() == 1) {
                naviHtm.publishObject(type);
            }
        }

        // Part 7: Publish Types , if appropriate...
        if (null != iTypes && iTypes.size() > 0) {

            // sort at first:
            iTypes.sort((IntegrityType m1, IntegrityType m2) -> m1.getTypeClassGroup().compareTo(m2.getTypeClassGroup()));

            naviHtm.addBook(iTypes.get(0));

            // overview.htm
            naviHtm.addOverviewSectionAndFile(iTypes.get(0));
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

            naviHtm.addOverviewSectionAndFile(iFields.get(0));
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

            naviHtm.addOverviewSectionAndFile(iTriggers.get(0));
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

        // Publish the second part of the Types
        for (Types type : Types.values()) {
            if (type.getGrp() == 2) {
                naviHtm.publishObject(type);
            }
        }

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
