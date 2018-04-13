/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.DocWriterTools.getFormattedContent;
import static com.ptc.services.utilities.docgen.DocWriterTools.objectTemplate;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import static com.ptc.services.utilities.docgen.utils.Utils.getObjectName;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

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

//    public void addBook(String name) throws IOException {
//        this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + ""));
//    }
    public void addBook(IntegrityAdminObject ao) throws IOException {
        String name = ao.getDirectory().replaceAll("([A-Z])", " $1");;
        this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + ""));
    }    
    public void addOverviewHeader(IntegrityAdminObject ao) throws IOException {
        String name = getObjectName(ao);
        this.write(appendNewLine("<ul><li title=\"" + name + " Overview\" data-context=\"20\"><a href=\"WorkflowDocs/" + name + "_overview.htm\" target=\"topic\">Overview</a></li></ul>"));
    }    
    public void endBook() throws IOException {
        this.write(appendNewLine("</li>"));
    }
    public void writeObjectHtml(IntegrityAdminObject adminObject) throws FileNotFoundException, IOException {

        this.write("<ul><li title=\"" + adminObject.getDirectory() + "\" data-context=\"20\"><a href=\"WorkflowDocs/" + adminObject.getDirectory() + "/" + adminObject.getPosition() + ".htm\" target=\"topic\">" + adminObject.getName() + "</a></li></ul>");

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
    
}
