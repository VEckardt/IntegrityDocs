/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.Constants.INDEX_FILE;
import static com.ptc.services.utilities.docgen.Constants.TITLE_FILE;
import static com.ptc.services.utilities.docgen.Constants.TOC_FILE;
import static com.ptc.services.utilities.docgen.Constants.getNow;
import static com.ptc.services.utilities.docgen.Constants.indexEndFile;
import static com.ptc.services.utilities.docgen.Constants.indexStartFile;
import static com.ptc.services.utilities.docgen.Constants.naviEndFile;
import static com.ptc.services.utilities.docgen.Constants.naviStartFile;
import static com.ptc.services.utilities.docgen.Constants.titleTemplate;
import static com.ptc.services.utilities.docgen.IntegrityDocs.integrity;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.removeUnusedFields;
import static com.ptc.services.utilities.docgen.IntegrityDocs.setUsedInTypeForFields;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DocWriter extends DocWriterTools {

   public DocWriter() {
      super();
   }

   public void publish() throws IOException {
      // First lets publish the report title
      try (
              BufferedReader reader = new BufferedReader(new FileReader(titleTemplate))) {
         BufferedWriter writer = new BufferedWriter(new FileWriter(TITLE_FILE));
         String line;
         while (null != (line = reader.readLine())) {
            writer.write(getFormattedContent(line, null, "", 1));
         }
         writer.flush();
         writer.close();
      }

      // Open the table of content files
      TocWriter naviHtm = new TocWriter(new FileWriter(TOC_FILE));
      TocWriter indexHtm = new TocWriter(new FileWriter(INDEX_FILE));

//        whtdata0xml.write(appendNewLine("<?xml version='1.0' encoding='utf-8' ?>"));
//        whtdata0xml.write(appendNewLine("<tocdata>"));
//        whtdata0xml.write(appendNewLine("<book name=\"Integrity Docs (" + svrInfo + ")\">"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Title\" url=\"WorkflowDocs/title.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <item name=\"Overview\" url=\"WorkflowDocs/overview.htm\" />"));
//        whtdata0xml.write(appendNewLine("  <book name=\"Types\">"));
      naviHtm.addFileContent(naviStartFile);
      indexHtm.addFileContent(indexStartFile);

      indexHtm.write(appendNewLine("Integrity Docs (" + IntegrityDocs.integrity.getHostName() + ":" + IntegrityDocs.integrity.getPort() + ")  <small>as of " + getNow() + "</small>"));

      // navi file
      naviHtm.addLeaf("Title");
      naviHtm.addLeaf("Summary");

      writeSummaryPage();

      // Publish the first part of the Types with group 1
      for (Types type : Types.values()) {
         if (type.getGrp() == 1) {
            naviHtm.publishObject(type);
         }
      }

      // flag where the field is used (Type names)
      setUsedInTypeForFields();

      // remove the unused fields from the fields stack
      removeUnusedFields();

      // Publish the second part of the Types
      for (Types type : Types.values()) {
         if (type.getGrp() == 2) {
            naviHtm.publishObject(type);
         }
      }

        // whtdata0xml.write(appendNewLine("</book>"));
      // whtdata0xml.write(appendNewLine("</tocdata>"));
      // write navi end
      indexHtm.addFileContent(indexEndFile);

      // write navi end
      naviHtm.addFileContent(naviEndFile);

      // Close the table of content files
      naviHtm.flush();

      naviHtm.close();

      indexHtm.flush();

      indexHtm.close();
        // whtdata0xml.flush();
      // whtdata0xml.close();
   }
}
