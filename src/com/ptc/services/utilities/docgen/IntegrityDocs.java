package com.ptc.services.utilities.docgen;

import com.mks.api.Command;
import static com.mks.api.Command.IM;
import static com.mks.api.Command.INTEGRITY;
import static com.mks.api.Command.SI;
import static com.mks.api.Command.TM;
import com.mks.api.response.APIException;
import com.ptc.services.utilities.CmdException;
import static com.ptc.services.utilities.docgen.Constants.INDEX_FILE;
import static com.ptc.services.utilities.docgen.Constants.REPORT_DIR;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_XML_DIR;
import static com.ptc.services.utilities.docgen.Constants.cleanupTempFiles;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.Constants.os;
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

public class IntegrityDocs {

    public enum Types {

        // Name(UID/CmdApp/Group/RetrieveIt/DisplayName/AddColumnsForSummary/HasSubStructure/showDetails/AllFieldsInOverview/Summary)
        Viewset(0, INTEGRITY, 1, true, "Viewset", "", 0, 1, 0, "This report lists all Viewsets configured in the Integrity Admin client."),
        SIProject(1, SI, 1, true, "Main CM Project", "", 0, 1, 0, "This report lists all Config Management Main Projects configured in the Integrity Admin client."),
        IMProject(2, IM, 1, true, "Main W&D Project", "", 0, 1, 0, "This report lists all W&D Main Projects configured in the Integrity Admin client."),
        State(3, IM, 1, true, null, "Image", 0, 1, 0, "This report lists all States configured in the Integrity Admin client."),
        Type(4, IM, 1, true, null, "", 1, 1, 0, "This report lists all Types configured in the Integrity Admin client."),
        Field(5, IM, 2, true, null, "Type", 1, 1, 0, "This report lists all Fields  configured in the Integrity Admin client."),
        Trigger(6, IM, 2, true, null, "", 1, 1, 0, "This report lists all Triggers configured in the Integrity Admin client."),
        Group(7, IM, 2, true, null, "isActive", 1, 1, 0, "This report lists all W&D Groups configured in the Integrity Admin client."),
        DynamicGroup(8, IM, 2, true, null, "", 0, 1, 0, "This report lists all Dynamic groups configured in the Integrity Admin client."),
        CPType(9, IM, 2, true, "Change Package Type", "", 0, 1, 0, "This report lists all Change Package Types configured in the Integrity Admin client."),
        Verdict(10, TM, 2, true, "Test Verdict", "Type,isActive", 0, 1, 0, "This report lists all Test Verdicts configured in the Integrity Admin client."),
        ResultField(11, TM, 2, true, "Test Result Field", "Type", 0, 1, 0, "This report lists all Test Result Fields configured in the Integrity Admin client."),
        Chart(12, IM, 2, true, null, "Image", 1, 1, 0, "This report lists all Charts configured in the Integrity Admin client."),
        Dashboard(13, IM, 2, true, null, "Image", 0, 1, 0, "This report lists all Dashboards configured in the Integrity Admin client."),
        Query(14, IM, 2, true, null, "Image", 0, 1, 0, "This report lists all Queries configured in the Integrity Admin client."),
        Report(15, IM, 2, true, null, "Image", 0, 1, 0, "This report lists all Reports configured in the Integrity Admin client."),
        GatewayMapping(16, IM, 2, true, "Gateway Mapping", "", 0, 1, 0, "This report lists all gateway mappings configured in mapping XML files."),
        GatewayImportConfig(17, IM, 2, true, "Gateway Import Config", "Type", 0, 1, 0, "This report lists all Gateway Import Configurations configured in gateway-tool-configuration.xml"),
        GatewayExportConfig(18, IM, 2, true, "Gateway Export Config", "Type", 0, 1, 0, "This report lists all Gateway Export Configurations configured in gateway-tool-configuration.xml"),
        GatewayTemplate(19, IM, 2, true, "Gateway Template", "", 0, 0, 1, "This report lists all import XSLTs and export Templates in Word or Excel, referred to in gateway-tool-configuration.xml"),
        TraceDefault(20, IM, 2, true, "Trace Default", "", 0, 0, 1, "This report lists all Trace Defaults configured in the MKS Solution type."),
        Metric(21, IM, 2, true, "Metric", "", 0, 0, 1, "This report lists all Metrics defined for the Integrity Server."),
        Image(22, IM, 2, true, "Image", "", 0, 0, 1, "This report lists all Images referred to in the Presentation Templates.");

        private final int id;
        private final int grp;
        private final boolean export;
        private final String modelType;
        private final String addColumns;
        private final int subStructure;
        private final int showDetails;
        private final String cmd;
        private final int allFields;
        private final String description;

        Types(int p, String cmd, int grp, boolean export, String modelType, String addColumns, int subStructure, int showDetails, int allFields, String description) {
            this.id = p;
            this.grp = grp;
            this.export = export;
            this.modelType = modelType;
            this.addColumns = addColumns;
            this.subStructure = subStructure;
            this.showDetails = showDetails;
            this.cmd = cmd;
            this.allFields = allFields;
            this.description = description;
        }

        int getID() {
            return id;
        }

        int getGrp() {
            return grp;
        }

        String getModelType() {
            return (modelType == null ? name() : modelType).replaceAll(" ", "");
        }

        String getDirectory() {
            return name().replace("ery", "erie") + "s";
        }

        String getDisplayName() {
            return (modelType == null ? name() : modelType);
        }

        String getAddColumns() {
            return addColumns;
        }

        Boolean showSubStructure() {
            return (subStructure == 1);
        }

        Boolean showDetails() {
            return (showDetails == 1);
        }

        Boolean showAllFields() {
            return (allFields == 1);
        }

        String getPlural() {
            return name().replace("ery", "erie") + "s";
        }

        String getCmd() {
            return cmd;
        }

        String getDescription() {
            return description;
        }
    }
    public static ArrayList<List<IntegrityAdminObject>> iObjectList = new ArrayList<>();
    private static List<String> typeList = new ArrayList<>();  // types to retrieve
    public static boolean[] doExport = new boolean[30];
    public static String solutionTypeName = "";

    // convert to XML 
    private boolean doXML = false;
    private static boolean removeUnusedFields = false;

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
        log("");
        log("IMPORTANT: Reports generated with IntegrityDocs will contain company confidential or privileged information. ");
        log("IMPORTANT: Please validate with your IT department before distributing the generated file set.");
        log("");

        // Print out the usage information
        log("Usage:");
        for (Types value : Types.values()) {
            String object = value.getPlural();
            log(String.format("%25s", "--no" + object) + ":   disable " + object + " scan and output");
        }
        log(String.format("%25s", "--stopAtType") + ":   Let IntegrityDocs stop after the mentioned type is processed");
        log(String.format("%25s", "--skipTypes") + ":   Let IntegrityDocs skip over listed types (comma delimited list), e.g. list all shared types");
        log(String.format("%25s", "--removeUnusedFields") + ":   Let IntegrityDocs remove from the reports unused fields");
        log("");

        try {
            IntegrityDocs iDocs = new IntegrityDocs();
            iDocs.generateDocs(args);
            log("Logfile " + Logger.getLogFile() + " written.");
            System.exit(0);
        } catch (APIException | IOException | CmdException | IntegrityException e) {
            exception(Level.SEVERE, 1, e);
            log("Logfile " + Logger.getLogFile() + " written.");
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + e.getMessage(),
                    "Integrity Docs - Generation Error",
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
    public static List<IntegrityAdminObject> getList(Types type) {
        return iObjectList.get(type.getID());
    }

    /**
     * Reads the Objects from Integrity and generates the output
     *
     * @param args
     * @throws APIException
     * @throws IOException
     * @throws CmdException
     * @throws
     * com.ptc.services.utilities.docgen.relationships.IntegrityException
     */
    public void generateDocs(String[] args) throws APIException, IOException, CmdException, IntegrityException {

        // Basis objects
        // List<IntegrityType> iTypes = new ArrayList<>();
        // List<Trigger> iTriggers = new ArrayList<>();
        // init list of other integrity objects
        for (Types value : Types.values()) {
            iObjectList.add(new ArrayList());
            doExport[value.getID()] = value.export;
        }

        // Construct the Integrity Application Connector
        Integrity i = new Integrity(); // , iFields);
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
                            doExport[value.getID()] = true;
                        }
                    }
                } else if (arg.startsWith("--stopAtType")) {
                    stopType = arg.substring(arg.lastIndexOf("=") + 1);
                } else if (arg.startsWith("--skipTypes")) {
                    skipTypes = arg.substring(arg.lastIndexOf("=") + 1);
                    // removeUnusedFields
                } else if (arg.startsWith("--removeUnusedFields")) {
                    removeUnusedFields = true;
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
        if (typeList.isEmpty() && doExport[Types.Type.getID()]) {
            typeList = i.getAdminList("types", skipTypes, stopType);
        }

        CONTENT_XML_DIR.mkdirs();

        // Read all Objects
        for (Types type : Types.values()) {
            i.retrieveObjects(type, doXML);
        }

        // Generate Transaction XML files for the Load Test Harness
        if (doXML) {
//            XMLWriter xWriter = new XMLWriter(iTypes, new List<?>[]{iTyQueries, iTriggers, iCharts, iViewsets});
//            xWriter.generate(sysFieldsHash);
//            // Open the folder containing the files
//            if (os.startsWith("Windows")) {
//                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + CONTENT_XML_DIR.getAbsolutePath());
//            }
        } else // Publish a report, if --xml is not specified
        {
            // Pass the abstraction to the DocWriter
            DocWriter doc = new DocWriter(i, iObjectList);
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
                // if (type.getFieldValue("visibleFields").isEmpty()) {
                //     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                // }
                // log (type.getFieldValue("visibleFields"));
                if (type.getFieldValue("visibleFields") != null) {
                    for (String name : type.getFieldValue("visibleFields").split(",")) {
                        if (name.equals(field.getName())) {
                            field.addUsedInType(type.getName());
                        }
                    }
                }
            }
//            if (field.usedInTypeIsEmpty()) {
//                log("> Field " + field.getName() + " is not used.");
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
        }
    }

    // Step 2: removeUnusedFields
    public static void removeUnusedFields() {
        // remove unused fields
        if (removeUnusedFields) {
            log("INFO: Removing unused fields ...");
            List<IntegrityAdminObject> intList = new ArrayList<>();
            for (IntegrityAdminObject field : getList(Types.Field)) {
                if (field.usedInTypeIsEmpty()) {
                    intList.add(field);
                }
            }
            for (IntegrityAdminObject field : intList) {
                IntegrityDocs.getList(Types.Field).remove(field);
            }
            log("INFO: " + intList.size() + " fields removed.");
        }
    }

    private boolean Do(Types type) {
        return doExport[type.getID()];
    }

    public static List<String> getTypeList() {
        return typeList;
    }

    public void generateResources() throws IOException {
        byte[] buf = new byte[1024];
        ZipInputStream zis = null;
        ZipEntry entry = null;
        if (!REPORT_DIR.isDirectory()) {
            REPORT_DIR.mkdirs();
        }
        try {
            zis = new ZipInputStream(getClass().getResourceAsStream("resources.zip"));
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
