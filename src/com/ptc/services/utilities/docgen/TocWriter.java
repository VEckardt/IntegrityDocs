/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.DocWriterTools.getFormattedContent;
import static com.ptc.services.utilities.docgen.DocWriterTools.objectTemplate;
import static com.ptc.services.utilities.docgen.DocWriterTools.overviewTemplate;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class TocWriter extends BufferedWriter {

    public TocWriter(Writer writer) {
        super(writer);
    }

    public void addLeaf(String name) throws IOException {
        this.write("<li title=\"" + name + "\" data-context=\"20\"><a href=\"" + name.toLowerCase() + ".htm\" target=\"topic\">" + name + "</a></li>");
    }

    public void addBook(IntegrityAdminObject ao) throws IOException {
        String name = ao.getDirectory().replaceAll("([A-Z])", " $1");
        this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + ""));
    }

    public void addBook(String name) throws IOException {
        this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + ""));
    }

    public void endBook() throws IOException {
        this.write(appendNewLine("</li>"));
    }

    public void publishObject(List<IntegrityObject> aol) throws IOException {
        // Part 1: Publish Viewset, if appropriate...
        if (null != aol && aol.size() > 0) {
            this.addBook(aol.get(0));
            this.addOverview(aol.get(0));
            this.addOverviewCloser();
            for (IntegrityObject object : aol) {
                // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                // Publish the individual trigger details
                this.writeObjectHtml(object);
            }
            this.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }
    }

    public void addOverview(IntegrityAdminObject ao) throws IOException {
        addOverviewHeader(ao);
        addOverviewData(ao);
    }

    public void addOverviewHeader(IntegrityAdminObject ao) throws IOException {
        String name = ao.getObjectType();
        this.write(appendNewLine("<ul><li title=\"" + name + " Overview\" data-context=\"20\"><a href=\"WorkflowDocs/" + name + "_overview.htm\" target=\"topic\">Overview</a></li>"));
    }

    public void addOverviewData(IntegrityAdminObject adminObject) throws FileNotFoundException, IOException {
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

    public void addOverviewCloser() throws IOException {
        this.write("</ul>");
    }

    public void writeObjectHtml(IntegrityAdminObject adminObject) throws FileNotFoundException, IOException {

        this.write("<ul><li title=\"" + adminObject.getDirectory() + "\" data-context=\"20\"><a href=\"WorkflowDocs/" + adminObject.getDirectory() + "/" + adminObject.getPosition() + ".htm\" target=\"topic\">" + adminObject.getName() + "</a></li></ul>");

        File directory = new File(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + adminObject.getDirectory());
        directory.mkdirs();

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
    
    public void addFileContent(String sourceFileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
            String line;
            while (null != (line = reader.readLine())) {
                this.write(appendNewLine(getFormattedContent(line, null)));
            }
        }
    }    

}
