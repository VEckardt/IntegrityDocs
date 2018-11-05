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

import com.mks.api.Command;
import com.mks.api.response.APIException;
import com.ptc.services.utilities.CmdException;
import static com.ptc.services.utilities.docgen.Constants.INDEX_FILE;
import static com.ptc.services.utilities.docgen.Constants.REPORT_DIR;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_XML_DIR;
import static com.ptc.services.utilities.docgen.Constants.cleanupTempFiles;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.Constants.os;
import static com.ptc.services.utilities.docgen.Integrity.getSysFields;
import com.ptc.services.utilities.docgen.relationships.IntegrityException;
import com.ptc.services.utilities.docgen.session.APISession;
import com.ptc.services.utilities.docgen.utils.Logger;
import static com.ptc.services.utilities.docgen.utils.Logger.exception;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class IntegrityDocs extends IntegrityDocsConfig {

    // List for all retrieved Objects
    public static ArrayList<List<IntegrityObject>> iObjectList = new ArrayList<>();
    // types to retrieve, might be less based on parameters
    private static List<String> typeList = new ArrayList<>();
    // determines if the type will be retrieved
    public static boolean[] doExport = new boolean[30];
    // name of the Integrity Solution Type, like MKS Solution
    public static String solutionTypeName = "";
    public static Integrity integrity;

    // convert to XML 
    private static boolean doXML = false;
    private static boolean removeUnusedFields = false;
    public static boolean skipChartPreview = false;
    public static boolean skipCreMoDetails = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
        Logger.init();

        // Only supporting Integrity 10 and newer releases
        log("IntegrityDocs Version" + Copyright.version);
        log("Copyright: " + Copyright.copyrightText);
        log("API Version: " + APISession.VERSION);
        log("Writing to logfile " + Logger.getLogFile());
        // log(String.format("%25s", "--xml")+":   Generates an XML represenation of the Integrity data (not maintained!!)");
        Constants.printUsageInfo();

        try {
            integrity = new Integrity(Command.IM);
            generateDocs(args);
            log("Logfile " + Logger.getLogFile() + " written.");
            System.exit(0);
        } catch (APIException | IOException | CmdException | IntegrityException | ParserConfigurationException | SAXException e) {
            exception(Level.SEVERE, 1, e);
            log("Logfile " + Logger.getLogFile() + " written.");
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + e.getMessage(),
                    "IntegrityDocs - Generation Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(128);
        }
    }

    /**
     * Returns the specified Object List, such as Charts, Queries etc.
     *
     * @param type
     * @return
     */
    public static List<IntegrityObject> getList(Types type) {
        return iObjectList.get(type.getID());
    }

    /**
     * Reads the Objects from Integrity and generates the output
     *
     * @param args
     * @throws com.mks.api.response.APIException
     * @throws IOException
     * @throws CmdException
     * @throws
     * com.ptc.services.utilities.docgen.relationships.IntegrityException
     */
    public static void generateDocs(String[] args) throws APIException, IOException, CmdException, IntegrityException, ParserConfigurationException, SAXException {

        // All objects
        for (Types value : Types.values()) {
            iObjectList.add(new ArrayList());
            doExport[value.getID()] = (doGlobal ? true : value.getExportFlag());
        }

        // Construct the Integrity Application Connector
        String stopType = "";
        String skipTypes = "";
        // Get a string list of types

        if (null != args && args.length > 0) {
            for (String arg : args) {
                if (arg.compareToIgnoreCase("--xml") == 0) {
                    doXML = true;
                } else if (arg.startsWith("--no")) {
                    for (Types value : Types.values()) {
                        if (arg.compareToIgnoreCase("--no" + (value.getPlural())) == 0) {
                            doExport[value.getID()] = (1 == 2);
                        }
                    }
                } else if (arg.startsWith("--stopAtType")) {
                    stopType = arg.substring(arg.lastIndexOf("=") + 1);
                } else if (arg.startsWith("--skipTypes")) {
                    skipTypes = arg.substring(arg.lastIndexOf("=") + 1);
                    // removeUnusedFields
                } else if (arg.startsWith("--removeUnusedFields")) {
                    removeUnusedFields = true;
                    // skipChartPreview
                } else if (arg.startsWith("--skipChartPreview")) {
                    skipChartPreview = true;
                } else if (arg.startsWith("--skipCreMoDetails")) {
                    skipCreMoDetails = true;
                } else {
                    typeList.add(arg);
                }
            }
        }

        if (!stopType.isEmpty()) {
            log("INFO: Will stop at Type: " + stopType);
        }
        if (!skipTypes.isEmpty()) {
            log("INFO: Will skip the following Types: " + skipTypes);
        }

      // for testing only
        // typeList.add("Defect");
        // In case no types are specified, then run the report for all types
        if (typeList.isEmpty() && Do(Types.Type)) {
            typeList = integrity.getAdminList("types", skipTypes, stopType);
        }

        CONTENT_XML_DIR.mkdirs();

        // Read all Objects from Integrity
        for (Types type : Types.values()) {
            integrity.retrieveObjects(type, doXML, typeList);
        }

        // Generate Transaction XML files for the Load Test Harness
        if (doXML) {
            XMLWriter xWriter = new XMLWriter(getList(Types.Type), iObjectList, getSysFields());
            xWriter.generate();
            // Open the folder containing the files
            if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + CONTENT_XML_DIR.getAbsolutePath());
            }
        } else // Publish a report, if --xml is not specified
        {
            // Pass the abstraction to the DocWriter
            DocWriter doc = new DocWriter();
            // Generate the report resources
            generateResources();

            // Publish the report content
            doc.publish();
            // Clean up the temporary files
            cleanupTempFiles();

            // Open the report, if this is a windows client
            if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + INDEX_FILE.getAbsolutePath());
            }
        }
    }

    // Step 1: set UsedInType For Fields
    public static void setUsedInTypeForFields() {
        // for all fields
        log("INFO: Setting UsedInTypeForFields ...", 1);
        for (IntegrityAdminObject field : getList(Types.Field)) {
            for (IntegrityAdminObject type : getList(Types.Type)) {
                if (type.getFieldValue("visibleFields") != null) {
                    for (String name : type.getFieldValue("visibleFields").split(",")) {
                        if (name.equals(field.getName())) {
                            field.addUsedInType(type.getName());
                        }
                    }
                }
            }
        }
    }

    // Step 2: removeUnusedFields
    public static void removeUnusedFields() {
        // remove unused fields
        if (removeUnusedFields) {
            log("INFO: Removing unused fields ...");
            List<IntegrityObject> intList = new ArrayList<>();
            for (IntegrityObject field : getList(Types.Field)) {
                if (field.usedInTypeIsEmpty()) {
                    intList.add(field);
                }
            }
            for (IntegrityObject field : intList) {
                IntegrityDocs.getList(Types.Field).remove(field);
            }
            log("INFO: " + intList.size() + " unused fields removed.");
        }
    }

    private static boolean Do(Types type) {
        return doExport[type.getID()];
    }

    public static List<String> getTypeList() {
        return typeList;
    }

    public static void generateResources() throws IOException {
        byte[] buf = new byte[1024];
        ZipInputStream zis = null;
        ZipEntry entry = null;
        if (!REPORT_DIR.isDirectory()) {
            REPORT_DIR.mkdirs();
        }
        try {
            zis = new ZipInputStream(IntegrityDocs.class.getResourceAsStream("resources.zip"));
            while (null != (entry = zis.getNextEntry())) {
                // Extract each resource file
                File resFile = new File(REPORT_DIR, entry.getName());
                if (entry.isDirectory()) {
                    resFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(resFile)) {
                        int len;
                        while ((len = zis.read(buf, 0, 1024)) > -1) {
                            fos.write(buf, 0, len);
                        }
                    }
                    zis.closeEntry();
                }
            }
        } finally {
            if (null != zis) {
                zis.close();
            }
        }
    }
}
