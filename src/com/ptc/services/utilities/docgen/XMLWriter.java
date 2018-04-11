package com.ptc.services.utilities.docgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
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

import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.APIException;
import com.ptc.services.utilities.XMLPrettyPrinter;

public class XMLWriter {

    public static Hashtable<String, String> paramsHash = new Hashtable<String, String>();
    private List<IntegrityType> iTypes;
    private List<?>[] iAdminObjects;

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

    public XMLWriter(List<IntegrityType> typesList, List<?>[] adminObjects) {
        iTypes = typesList;
        iAdminObjects = adminObjects;
    }

    private Element getInstruction(Document job, Element instruction, Enumeration<?> list, boolean overrides) {
        String instructionID = instruction.getAttribute("id").substring(1);
        int i = 1; // command counter
        while (list.hasMoreElements()) {
            Element command = job.createElement("Command");
            command.setAttribute("id", "c" + instructionID + "." + i);
            command.setAttribute("user", "${username}");
            command.setAttribute("host", "${hostname}");
            command.setAttribute("port", "${port}");
            command.setAttribute("pwd", "${pwd}");

            IntegrityAdminObject o = (IntegrityAdminObject) list.nextElement();
            if (overrides) {
                if (o.getModelType().equals(IMModelTypeName.FIELD)) {
                    command = ((IntegrityField) o).getOverridesXML(job, command);
                }
                if (o.getModelType().equals(IMModelTypeName.STATE)) {
                    command = ((IntegrityState) o).getOverridesXML(job, command);
                }
            } else {
                command = o.getXML(job, command);
            }

            // Add the command to the instruction
            instruction.appendChild(command);
            // Increment the command counter
            i++;
        }

        return instruction;
    }

    private Element getInstruction(Document job, Element instruction, IntegrityAdminObject adminObject) {
        String instructionID = instruction.getAttribute("id").substring(1);
        Element command = job.createElement("Command");
        command.setAttribute("id", "c" + instructionID + "." + 1);
        command.setAttribute("user", "${username}");
        command.setAttribute("host", "${hostname}");
        command.setAttribute("port", "${port}");
        command.setAttribute("pwd", "${pwd}");
        command = adminObject.getXML(job, command);
        // Add the command to the instruction
        instruction.appendChild(command);
        return instruction;
    }

    public void generate(Hashtable<String, IntegrityField> sysFieldsHash) throws ParserConfigurationException, APIException, SAXException, IOException {
        // First process the types
        Iterator<IntegrityType> it = iTypes.iterator();
        while (it.hasNext()) {
            IntegrityType t = it.next();
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlBuilder = domFactory.newDocumentBuilder();
            // Set the XML Error Handler to manage XML parsing errors
            xmlBuilder.setErrorHandler(new XMLErrorHandler());
            Document xmlJob = xmlBuilder.newDocument();
            Element job = xmlJob.createElement("Job");
            job.setAttribute("id", "j" + t.getPosition());
            job.setAttribute("start-instruction", "i1");
            job.setAttribute("stop-instruction", "i5");
            // <Job-Declaration>
            Element jobDecl = xmlJob.createElement("Job-Declaration");
            Element createVar = xmlJob.createElement("create-var");
            createVar.setAttribute("type", "java.util.Hashtable");
            createVar.setAttribute("name", "jobHash");
            Element initVar = xmlJob.createElement("initialize-var");
            initVar.setAttribute("name", "jobHash");
            initVar.setAttribute("file", t.getName() + "-jobHash.properties");
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
            job.appendChild(getInstruction(xmlJob, createStates, t.getStates().elements(), false));

            // Next, we'll setup the instruction to edit states...
            job.appendChild(xmlJob.createComment(" States - edit pass - overrides for the type "));
            Element editStates = xmlJob.createElement("Instruction");
            editStates.setAttribute("id", "i2");
            // Add this instruction to the job
            job.appendChild(getInstruction(xmlJob, editStates, t.getStates().elements(), true));

            // Next, we'll setup the instruction to create fields...
            job.appendChild(xmlJob.createComment(" Fields - create pass or edit platform fields "));
            Element createFields = xmlJob.createElement("Instruction");
            createFields.setAttribute("id", "i3");
            // Add this instruction to the job
            job.appendChild(getInstruction(xmlJob, createFields, t.getFields().elements(), false));

            // Next, we'll setup the instruction to edit fields...
            job.appendChild(xmlJob.createComment(" Fields - edit pass - overrides for the type "));
            Element editFields = xmlJob.createElement("Instruction");
            editFields.setAttribute("id", "i4");
            // Add this instruction to the job
            job.appendChild(getInstruction(xmlJob, editFields, t.getFields().elements(), true));

            // Finally, we'll setup the instruction to create the type...
            job.appendChild(xmlJob.createComment(" Type - create pass "));
            Element createType = xmlJob.createElement("Instruction");
            createType.setAttribute("id", "i5");
            // Add this instruction to the job
            job.appendChild(getInstruction(xmlJob, createType, t));

            // Append the job to the xml document
            xmlJob.appendChild(job);

            // Write out the job xml file
            XMLPrettyPrinter.serialize(new File(IntegrityDocs.XML_CONTENT_DIR.getAbsolutePath(), t.getPosition() + "-" + t.getName() + ".xml"), xmlJob, true);

            // Export the presentation templates
            List<String> presentations = t.getUniquePresentations();
            for (Iterator<String> pit = presentations.iterator(); pit.hasNext();) {
                t.exportPresentation(pit.next(), IntegrityDocs.XML_CONTENT_DIR, sysFieldsHash);
            }
        }

        // Process the rest of the admin objects provided (queries, charts, triggers, viewsets...)
        for (int i = 0; i < iAdminObjects.length; i++) {
            generateAdminObject(iAdminObjects[i]);
        }

        // Generate the resources properties
        Properties globalRes = new Properties() {
            private static final long serialVersionUID = 3094652219774363378L;

            @SuppressWarnings({"unchecked", "rawtypes"})
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
        File resPropsFile = new File(IntegrityDocs.XML_CONTENT_DIR.getAbsolutePath(), "resources.properties");
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

    private void generateAdminObject(List<?> adminObjectList) throws ParserConfigurationException {
        // Process the appropriate admin object list (Queries, Triggers, etc.)
        if (adminObjectList.size() > 0) {
            @SuppressWarnings("unchecked")
            Iterator<IntegrityAdminObject> it = (Iterator<IntegrityAdminObject>) adminObjectList.iterator();
            // Get the first admin object so we know what admin object we're working with...
            IntegrityAdminObject adminObject = it.next();
            String adminType = adminObject.getAdminObjectType();
            // Create the xml document for this admin object list
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlBuilder = domFactory.newDocumentBuilder();
            // Set the XML Error Handler to manage XML parsing errors
            xmlBuilder.setErrorHandler(new XMLErrorHandler());
            Document xmlJob = xmlBuilder.newDocument();
            Element job = xmlJob.createElement("Job");
            job.setAttribute("id", adminType + "1");
            job.setAttribute("start-instruction", "i1");
            job.setAttribute("stop-instruction", "i" + adminObjectList.size());
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

            int i = 1;
            // Process each admin object in the list as an instruction with one command each
            do {
                // Create an instruction for each Trigger...
                job.appendChild(xmlJob.createComment(" " + adminObject.getName() + " "));
                Element instruction = xmlJob.createElement("Instruction");
                instruction.setAttribute("id", "i" + i);
                // Add this instruction to the job
                job.appendChild(getInstruction(xmlJob, instruction, adminObject));
                // Increment the admin object counter
                i++;
                // Go to the next admin object in the list, if we've got another one
                adminObject = (it.hasNext() ? it.next() : null);
            } while (null != adminObject);

            // Append the job to the xml document
            xmlJob.appendChild(job);

            // Write out the admin object as a single job xml file
            XMLPrettyPrinter.serialize(new File(IntegrityDocs.XML_CONTENT_DIR.getAbsolutePath(), adminType + ".xml"), xmlJob, true);
        }
    }
}
