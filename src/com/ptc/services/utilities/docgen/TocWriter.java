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

import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.Constants.objectTemplate;
import static com.ptc.services.utilities.docgen.Constants.overviewTemplate;
import static com.ptc.services.utilities.docgen.DocWriterTools.getFormattedContent;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Overviews;
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
      addBook(name, size);
   }

   public void addBook(String name, int size) throws IOException {
      this.write(appendNewLine("<li class=\"hs-book\" title=\"" + name + "\">" + name + " (" + size + ")"));
   }

   public void endBook() throws IOException {
      this.write(appendNewLine("</li>"));
   }

   public void publishObject(Types type) throws IOException {
      // Part 1: Publish Viewset, if appropriate...
      List<IntegrityObject> aol = IntegrityDocs.getList(type);
      if (aol.size() > 0) {
         String typeClassGroup = "";
         log("Publishing " + type.getPlural() + " with " + aol.size() + " objects ...");
         this.addBook(aol.get(0), aol.size());
         this.addOverviewSectionAndFile(aol.get(0), 1, "");

         for (Overviews overview : Overviews.values()) {
            if (overview.getType().equals(type)) {
               this.addOverviewCloser();
               this.addOverviewSectionAndFile(getList(type).get(0), overview.getId(), overview.getText());
            }
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
      } else {
         log("INFO: Skipping object publishing of type '" + type.name() + "', because no objects retrieved.");
      }
   }

   public void addOverviewSectionAndFile(IntegrityAdminObject ao, int id, String title) throws IOException {
      String name = ao.getObjectTypeName();
      this.write(appendNewLine("<ul><li title=\"" + name + " Overview \" data-context=\"20\"><a href=\"WorkflowDocs/" + name + "_overview" + id + ".htm\" target=\"topic\">Overview " + title + "</a></li>"));

      // Next lets publish the types overview
      try (
              BufferedReader reader = new BufferedReader(new FileReader(overviewTemplate))) {
         BufferedWriter writer = new BufferedWriter(new FileWriter(CONTENT_DIR + fs + name + "_overview" + id + ".htm"));
         String line;
         while (null != (line = reader.readLine())) {
            writer.write(appendNewLine(getFormattedContent(line, ao, title, id)));
         }
         writer.flush();
         writer.close();
      }
   }

   /**
    * Closes the UL tag
    *
    * @throws IOException
    */
   public void addOverviewCloser() throws IOException {
      this.write("</ul>");
   }

   /**
    * Writes a HTML leaf for the given Object
    *
    * @param adminObject
    * @throws FileNotFoundException
    * @throws IOException
    */
   public void writeObjectHtml(IntegrityAdminObject adminObject) throws FileNotFoundException, IOException {

      this.write("<ul><li title=\"" + adminObject.getDirectory() + "\" data-context=\"20\"><a href=\"WorkflowDocs/" + adminObject.getDirectory() + "/" + adminObject.getPosition() + ".htm\" target=\"topic\">" + adminObject.getName() + "</a></li></ul>");

      File directory = new File(CONTENT_DIR + fs + adminObject.getDirectory());
      directory.mkdirs();

      try (BufferedReader triggerReader = new BufferedReader(new FileReader(objectTemplate))) {

         BufferedWriter writer = new BufferedWriter(new FileWriter(directory + fs + adminObject.getPosition() + ".htm"));
         String line;
         while (null != (line = triggerReader.readLine())) {
            writer.write((getFormattedContent(line, adminObject, "", 1) + nl));
         }
         writer.flush();
         writer.close();
      }
   }

   /**
    * Adds the content from the file File to the current Object (this)
    *
    * @param sourceFileName
    * @throws IOException
    */
   public void addFileContent(File sourceFileName) throws IOException {
      try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
         String line;
         while (null != (line = reader.readLine())) {
            this.write(appendNewLine(getFormattedContent(line, null, "", 1)));
         }
      }
   }

   /**
    * Returns all counters of the objects by given type class
    *
    * @param objectList
    * @param typeClass
    * @throws IOException
    */
   private int countByTypeClass(List<IntegrityObject> objectList, String typeClass) {
      int cnt = 0;
      for (IntegrityObject object : objectList) {
         if (object.getTypeClassGroup().equals(typeClass)) {
            cnt++;
         }
      }
      return cnt;
   }
}
