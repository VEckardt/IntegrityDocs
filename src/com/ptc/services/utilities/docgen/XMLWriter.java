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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mks.api.response.APIException;
import com.ptc.services.utilities.XMLPrettyPrinter;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_XML_DIR;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class XMLWriter {

   public static LinkedHashMap<String, String> paramsHash = new LinkedHashMap<>();
   private final LinkedHashMap<String, IntegrityField> sysFieldsHash;
   private final List<IntegrityObject> iTypes;
   private final ArrayList<List<IntegrityObject>> iObjects;

   /**
    * Constructor
    *
    * @param typesList
    * @param adminObjects
    * @param sysFieldsHash
    */
   public XMLWriter(List<IntegrityObject> typesList, ArrayList<List<IntegrityObject>> adminObjects, LinkedHashMap<String, IntegrityField> sysFieldsHash) {
      iTypes = typesList;
      iObjects = adminObjects;
      this.sysFieldsHash = sysFieldsHash;
   }
   
   public static final String padXMLParamName(String name) {
      return "${" + getXMLParamName(name) + '}';
   }

   public static final String getXMLParamName(String name) {
      if (null != name && name.length() > 0) {
         return name.toUpperCase().replace(' ', '_');
      } else {
         return name;
      }
   }

   /**
    * Get Option
    *
    * @param job
    * @param attribute
    * @param value
    * @return
    */
   public static final Element getOption(Document job, String attribute, String value) {
      Element option = job.createElement("option");
      option.setAttribute("name", attribute);
      if (null != value && value.length() > 0) {
         Element optionValue = job.createElement("option-value");
         optionValue.appendChild(job.createCDATASection(value));
         option.appendChild(optionValue);
      }
      return option;
   }



   private Element getInstruction2(Document job, Element instruction, LinkedHashMap<String, IntegrityState> list, boolean overrides) {
      String instructionID = instruction.getAttribute("id").substring(1);
      int i = 1; // command counter
      for (IntegrityObject object : list.values()) {
         Element command = job.createElement("Command");
         command.setAttribute("id", "c" + instructionID + "." + i);
         command.setAttribute("user", "${username}");
         command.setAttribute("host", "${hostname}");
         command.setAttribute("port", "${port}");
         command.setAttribute("pwd", "${pwd}");

         if (overrides) {
            if (object.getObjectType().equals(Types.Field)) {
               command = ((IntegrityField) object).getOverridesXML(job, command);
            }
            if (object.getObjectType().equals(Types.State)) {
               command = ((IntegrityState) object).getOverridesXML(job, command);
            }
         } else {
            command = object.getXML(job, command);
         }
         // Add the command to the instruction
         instruction.appendChild(command);
         // Increment the command counter
         i++;
      }
      return instruction;
   }

   private Element getInstruction(Document job, Element instruction, LinkedHashMap<String, IntegrityField> list, boolean overrides) {
      String instructionID = instruction.getAttribute("id").substring(1);
      int i = 1; // command counter
      for (IntegrityObject object : list.values()) {
         Element command = job.createElement("Command");
         command.setAttribute("id", "c" + instructionID + "." + i);
         command.setAttribute("user", "${username}");
         command.setAttribute("host", "${hostname}");
         command.setAttribute("port", "${port}");
         command.setAttribute("pwd", "${pwd}");

         // IntegrityObject o = (IntegrityObject) field;
         if (overrides) {
            if (object.getObjectType().equals(Types.Field)) {
               command = ((IntegrityField) object).getOverridesXML(job, command);
            }
            if (object.getObjectType().equals(Types.State)) {
               command = ((IntegrityState) object).getOverridesXML(job, command);
            }
         } else {
            command = object.getXML(job, command);
         }

         // Add the command to the instruction
         instruction.appendChild(command);
         // Increment the command counter
         i++;
      }

      return instruction;
   }

   private Element getInstruction(Document job, Element instruction, IntegrityObject adminObject) {
      String instructionID = instruction.getAttribute("id").substring(1);
      Element command = job.createElement("Command");
      command.setAttribute("id", "c" + instructionID + "." + 1);
      command.setAttribute("user", "${username}");
      command.setAttribute("host", "${hostname}");
      command.setAttribute("port", "${port}");
      command.setAttribute("pwd", "${pwd}");
//      log("adminObject: " + adminObject.getObjectTypeName());
//      if (!adminObject.getObjectTypeName().equals("Field")
//              && !adminObject.getObjectTypeName().equals("Verdict")
//              && !adminObject.getObjectTypeName().equals("ResultField") // && !adminObject.getObjectTypeName().equals("Query")
//              ) {
      command = adminObject.getXML(job, command);
      // }
      // Add the command to the instruction
      instruction.appendChild(command);
      return instruction;
   }

   public void generate() throws ParserConfigurationException, APIException, SAXException, IOException {
      // First process the types
      Iterator<IntegrityObject> it = iTypes.iterator();
      while (it.hasNext()) {
         IntegrityType type = (IntegrityType) it.next();
         DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder xmlBuilder = domFactory.newDocumentBuilder();
         // Set the XML Error Handler to manage XML parsing errors
         xmlBuilder.setErrorHandler(new XMLErrorHandler());
         Document xmlJob = xmlBuilder.newDocument();
         Element job = xmlJob.createElement("Job");
         job.setAttribute("id", "j" + type.getPosition());
         job.setAttribute("start-instruction", "i1");
         job.setAttribute("stop-instruction", "i5");
         // <Job-Declaration>
         Element jobDecl = xmlJob.createElement("Job-Declaration");
         Element createVar = xmlJob.createElement("create-var");
         createVar.setAttribute("type", "java.util.Hashtable");
         createVar.setAttribute("name", "jobHash");
         Element initVar = xmlJob.createElement("initialize-var");
         initVar.setAttribute("name", "jobHash");
         initVar.setAttribute("file", type.getName() + "-jobHash.properties");
         initVar.setAttribute("exportOnError", "true");
         jobDecl.appendChild(createVar);
         jobDecl.appendChild(initVar);
         // Add the <Job-Declaration> to the <Job>
         job.appendChild(jobDecl);

         // First we'll setup the instruction to create states...
         job.appendChild(xmlJob.createComment(" States - create pass  "));
         Element createStates = xmlJob.createElement("Instruction");
         createStates.setAttribute("id", "i1");
         // Add this instruction to the job
         job.appendChild(getInstruction2(xmlJob, createStates, type.getStates(), false));

         // Next, we'll setup the instruction to edit states...
         job.appendChild(xmlJob.createComment(" States - edit pass - overrides for the type "));
         Element editStates = xmlJob.createElement("Instruction");
         editStates.setAttribute("id", "i2");
         // Add this instruction to the job
         job.appendChild(getInstruction2(xmlJob, editStates, type.getStates(), true));

         // Next, we'll setup the instruction to create fields...
         job.appendChild(xmlJob.createComment(" Fields - create pass or edit platform fields "));
         Element createFields = xmlJob.createElement("Instruction");
         createFields.setAttribute("id", "i3");
         // Add this instruction to the job
         job.appendChild(getInstruction(xmlJob, createFields, type.getFields(), false));

         // Next, we'll setup the instruction to edit fields...
         job.appendChild(xmlJob.createComment(" Fields - edit pass - overrides for the type "));
         Element editFields = xmlJob.createElement("Instruction");
         editFields.setAttribute("id", "i4");
         // Add this instruction to the job
         job.appendChild(getInstruction(xmlJob, editFields, type.getFields(), true));

         // Finally, we'll setup the instruction to create the type...
         job.appendChild(xmlJob.createComment(" Type - create pass "));
         Element createType = xmlJob.createElement("Instruction");
         createType.setAttribute("id", "i5");
         // Add this instruction to the job
         job.appendChild(getInstruction(xmlJob, createType, type));

         // Append the job to the xml document
         xmlJob.appendChild(job);

         // Write out the job xml file
         XMLPrettyPrinter.serialize(new File(CONTENT_XML_DIR.getAbsolutePath(), type.getPosition() + "-" + type.getName() + ".xml"), xmlJob, true);

         // Export the presentation templates
         List<String> presentations = type.getUniquePresentations();
         for (Iterator<String> pit = presentations.iterator(); pit.hasNext();) {
            type.exportPresentation(pit.next(), CONTENT_XML_DIR, sysFieldsHash);
         }
      }

      // Process the rest of the admin objects provided (queries, charts, triggers, viewsets...)
      for (List<IntegrityObject> objectList : iObjects) {
         if (objectList.size() > 0 && objectList.get(0).getObjectType().doExportXML()) {
            // if (!iAdminObject.get(0).getObjectType().equals(Types.Trigger)) {
            generateObject(objectList);
            // }
         }
      }

      // Generate the resources properties
      Properties globalRes = new Properties() {
         private static final long serialVersionUID = 3094652219774363378L;

         @SuppressWarnings({"unchecked", "rawtypes"})
         @Override
         public synchronized Enumeration keys() {
            Enumeration keysEnum = super.keys();
            Vector keyList = new Vector();
            while (keysEnum.hasMoreElements()) {
               keyList.add(keysEnum.nextElement());
            }
            Collections.sort(keyList);
            return keyList.elements();
         }
      };
      globalRes.putAll(paramsHash);
      File resPropsFile = new File(CONTENT_XML_DIR.getAbsolutePath(), "resources.properties");
      BufferedWriter wtr = null;
      try {
         wtr = new BufferedWriter(new FileWriter(resPropsFile));
         globalRes.store(wtr, "Integrity Docs automatically generated resources");
      } finally {
         if (null != wtr) {
            wtr.flush();
            wtr.close();
         }
      }
   }

   private void generateObject(List<IntegrityObject> objectList) throws ParserConfigurationException {
      // Process the appropriate admin object list (Queries, Triggers, etc.)
      if (objectList.size() > 0) {
         @SuppressWarnings("unchecked")
         Iterator<IntegrityObject> it = (Iterator<IntegrityObject>) objectList.iterator();
         // Get the first admin object so we know what admin object we're working with...
         IntegrityObject adminObject = it.next();
         String adminType = adminObject.getObjectTypeName();
         // Create the xml document for this admin object list
         DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder xmlBuilder = domFactory.newDocumentBuilder();
         // Set the XML Error Handler to manage XML parsing errors
         xmlBuilder.setErrorHandler(new XMLErrorHandler());
         Document xmlJob = xmlBuilder.newDocument();
         Element job = xmlJob.createElement("Job");
         job.setAttribute("id", adminType + "1");
         job.setAttribute("start-instruction", "i1");
         job.setAttribute("stop-instruction", "i" + objectList.size());
         // <Job-Declaration>
         Element jobDecl = xmlJob.createElement("Job-Declaration");
         Element createVar = xmlJob.createElement("create-var");
         createVar.setAttribute("type", "java.util.Hashtable");
         createVar.setAttribute("name", "jobHash");
         Element initVar = xmlJob.createElement("initialize-var");
         initVar.setAttribute("name", "jobHash");
         initVar.setAttribute("file", adminType + "-jobHash.properties");
         initVar.setAttribute("exportOnError", "true");
         jobDecl.appendChild(createVar);
         jobDecl.appendChild(initVar);
         // Add the <Job-Declaration> to the <Job>
         job.appendChild(jobDecl);

         int i = 0;
         // Process each admin object in the list as an instruction with one command each
         while (it.hasNext()) {
            adminObject = it.next();
            // Create an instruction for each Trigger...
            job.appendChild(xmlJob.createComment(" " + adminObject.getName() + " "));
            Element instruction = xmlJob.createElement("Instruction");
            // Increment the admin object counter
            instruction.setAttribute("id", "i" + (++i));
            // Add this instruction to the job
            job.appendChild(getInstruction(xmlJob, instruction, adminObject));
         }

         // Append the job to the xml document
         xmlJob.appendChild(job);

         // Write out the admin object as a single job xml file
         log("Writing " + adminType + ".xml with " + i + " objects ...");
         XMLPrettyPrinter.serialize(new File(CONTENT_XML_DIR.getAbsolutePath(), adminType + ".xml"), xmlJob, true);
      }
   }
}
