/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.Constants.objectTemplate;
import static com.ptc.services.utilities.docgen.Constants.overviewTemplate;
import static com.ptc.services.utilities.docgen.DocWriterTools.getFormattedContent;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
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

    public void addBook(IntegrityAdminObject ao, int size) throws IOException {
        String name = ao.getObjectsDisplayName();
        this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + " (" + size + ")"));
    }

    public void addBook(String name, int size) throws IOException {
        this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + " (" + size + ")"));
    }

    public void endBook() throws IOException {
        this.write(appendNewLine("</li>"));
    }

    public void publishObject(Types type) throws IOException {
        // Part 1: Publish Viewset, if appropriate...
        List<IntegrityAdminObject> aol = IntegrityDocs.getList(type);
        if (null != aol && aol.size() > 0) {
            String typeClassGroup = "";
            log("Publishing " + type.getPlural() + " with " + aol.size() + " objects ...");
            this.addBook(aol.get(0), aol.size());
            this.addOverviewSectionAndFile(aol.get(0), 1);
            if (type.equals(Types.Type)) {
                this.addOverviewCloser();
                this.addOverviewSectionAndFile(getList(Types.Type).get(0), 2);
            }

            if (type.showDetails()) {

                if (type.showSubStructure()) {
                    aol.sort((IntegrityAdminObject m1, IntegrityAdminObject m2) -> m1.getTypeClassGroup().compareTo(m2.getTypeClassGroup()));
                } else {
                    this.addOverviewCloser();
                }
                for (IntegrityAdminObject object : aol) {
                    // whtdata0xml.write(appendNewLine("    <item name=\"" + trigger.getName()
                    //        + "\" url=\"WorkflowDocs/Triggers/" + trigger.getPosition() + ".htm\" />"));
                    if (type.showSubStructure() && !object.getTypeClassGroup().equals(typeClassGroup)) {
                        if (!typeClassGroup.isEmpty()) {
                            this.endBook();
                        }
                        typeClassGroup = object.getTypeClassGroup();
                        this.addBook(typeClassGroup, countByTypeClass(aol, typeClassGroup));

                    }
                    // Publish the individual trigger details
                    this.writeObjectHtml(object);
                }
                if (type.showSubStructure()) {
                    this.endBook();
                    this.addOverviewCloser();
                }
            } else {
                this.addOverviewCloser();
            }
            this.endBook();
            // whtdata0xml.write(appendNewLine("  </book>"));
        }
    }

    public void addOverviewSectionAndFile(IntegrityAdminObject ao, int id) throws IOException {
        String name = ao.getObjectType();
        this.write(appendNewLine("<ul><li title=\"" + name + " Overview\" data-context=\"20\"><a href=\"WorkflowDocs/" + name + "_overview" + id + ".htm\" target=\"topic\">Overview" + (id == 2 ? " with Images" : "") + "</a></li>"));

        // Next lets publish the types overview
        try (
                BufferedReader reader = new BufferedReader(new FileReader(overviewTemplate))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CONTENT_DIR + fs + name + "_overview" + id + ".htm"));
            String line;
            while (null != (line = reader.readLine())) {
                writer.write(appendNewLine(getFormattedContent(line, ao, id)));
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

        File directory = new File(CONTENT_DIR + fs + adminObject.getDirectory());
        directory.mkdirs();

        try (BufferedReader triggerReader = new BufferedReader(new FileReader(objectTemplate))) {

            BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(directory + fs + adminObject.getPosition() + ".htm"));
            String line;
            while (null != (line = triggerReader.readLine())) {
                triggerWriter.write((getFormattedContent(line, adminObject, 1) + nl));
            }
            triggerWriter.flush();
            triggerWriter.close();
        }
    }

    public void addFileContent(File sourceFileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
            String line;
            while (null != (line = reader.readLine())) {
                this.write(appendNewLine(getFormattedContent(line, null, 1)));
            }
        }
    }

    private int countByTypeClass(List<IntegrityAdminObject> objectList, String typeClass) {
        int cnt = 0;
        for (IntegrityAdminObject object : objectList) {
            if (object.getTypeClassGroup().equals(typeClass)) {
                cnt++;
            }
        }
        return cnt;
    }
}
