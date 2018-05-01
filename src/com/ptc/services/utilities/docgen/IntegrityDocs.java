package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.session.APISession;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

import com.mks.api.response.APIException;
import com.ptc.services.utilities.CmdException;
import static com.ptc.services.utilities.docgen.Copyright.iDOCS_REV;
import com.ptc.services.utilities.docgen.utils.Logger;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class IntegrityDocs {

    private static final String os = System.getProperty("os.name");
    public static final String nl = System.getProperty("line.separator");
    public static final String fs = System.getProperty("file.separator");
    public static final File REPORT_DIR = new File(System.getProperty("user.home") + fs + "Desktop" + fs + "IntegrityDocs");
    public static final File REPORT_FILE = new File(REPORT_DIR.getAbsolutePath() + fs + "index.htm");
    public static final File CONTENT_DIR = new File(REPORT_DIR.getAbsolutePath() + fs + "WorkflowDocs");

    public static final File XML_CONTENT_DIR = new File(REPORT_DIR.getAbsolutePath() + fs + "WorkflowDocs-XML");
    public static final File XML_VIEWSETS_DIR = new File(XML_CONTENT_DIR.getAbsolutePath() + fs + "viewsets");

    public enum Types {

        Type(0, 0, true, null, ""),
        Field(1, 0, true, null, ""),
        Trigger(2, 0, true, null, ""),
        IMProject(3, 1, true, "MainW&DProject", ""),
        SIProject(4, 1, true, "MainCMProject", ""),
        Viewset(5, 1, true, "Viewset", ""),
        Group(6, 1, true, null, "isActive"),
        DynamicGroup(7, 1, true, null, ""),
        State(8, 1, true, null, "Image"),
        CPType(9, 2, true, "ChangePackageType", ""),
        Verdict(10, 2, true, "TestVerdict", "Type,isActive"),
        ResultField(11, 2, true, "TestResultField", "Type"),
        Chart(12, 2, true, null, ""),
        Query(13, 2, true, null, "Image"),
        Dashboard(14, 2, true, null, ""),
        Report(15, 2, true, null, ""),
        GatewayMapping(18, 2, true, "GatewayMapping", ""),
        GatewayImportConfig(16, 2, true, "GatewayImportConfig", "Type"),
        GatewayExportConfig(17, 2, true, "GatewayExportConfig", "Type");

        int id;
        int grp;
        boolean export;
        String modelType;
        String addColumns;

        Types(int p, int grp, boolean export, String modelType, String addColumns) {
            this.id = p;
            this.grp = grp;
            this.export = export;
            this.modelType = modelType;
            this.addColumns = addColumns;
        }

        int getID() {
            return id;
        }

        int getGrp() {
            return grp;
        }

        String getModelType() {
            return modelType == null ? name() : modelType;
        }

        String getDirectory() {
            return (modelType == null ? name() : modelType).replace("ery", "erie") + "s";
        }

        String getAddColumns() {
            return addColumns;
        }
    }
    public static ArrayList<List<IntegrityObject>> iObjectList = new ArrayList<>();
    public static boolean[] doExport = new boolean[20];

    // convert to XML 
    private boolean doXML = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
        Logger.init();

        // Only supporting Integrity 10 and newer releases
        log("IntegrityDocs Version" + iDOCS_REV.substring(iDOCS_REV.lastIndexOf(':'), iDOCS_REV.lastIndexOf('$')));
        log("API Version: " + APISession.VERSION);
        log("Writing to logfile " + Logger.getLogFile());
        log("Usage:");
        log("  --xml: Generates an XML represenation of the Integrity data (not well maintained)");

        for (Types value : Types.values()) {
            String object = value.toString().replace("ery", "erie") + "s";
            log("  --no" + object + ":       disable " + object + " scan and output");
        }

        try {
            IntegrityDocs iDocs = new IntegrityDocs();
            iDocs.generateDocs(args);
            log("Logfile " + Logger.getLogFile() + " written.");
            System.exit(0);
        } catch (APIException | ParserConfigurationException | IOException | SAXException | CmdException e) {
            log(e.getMessage(), 1, e);
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + e.getMessage(),
                    "Integrity Docs - Generation Error",
                    JOptionPane.ERROR_MESSAGE);
            log("Logfile " + Logger.getLogFile() + " written.");
            System.exit(128);
        }
    }

    public static List<IntegrityObject> getList(Types type) {
        return iObjectList.get(type.getID());
    }

    public void generateDocs(String[] args) throws APIException, ParserConfigurationException, IOException, SAXException, CmdException {

        // Basis objects
        List<IntegrityType> iTypes = new ArrayList<>();
        List<IntegrityField> iFields = new ArrayList<>();
        List<Trigger> iTriggers = new ArrayList<>();

        // init list of other integrity objects
        for (Types value : Types.values()) {
            iObjectList.add(new ArrayList());
            doExport[value.getID()] = value.export;
        }

        // Construct the Integrity Application
        Integrity i = new Integrity(iTypes, iFields);

        // Get a string list of types
        List<String> typeList = new ArrayList<>();
        if (null != args && args.length > 0) {
            for (String arg : args) {
                if (arg.compareToIgnoreCase("--xml") == 0) {
                    doXML = true;
                } else if (arg.startsWith("--no")) {
                    for (Types value : Types.values()) {
                        if (arg.compareToIgnoreCase("--no" + (value.toString().replace("ery", "erie")) + "s") == 0) {
                            doExport[value.getID()] = false;
                        }
                    }
                } else {
                    typeList.add(arg);
                }
            }
        }

        // Fetch the list of fields without the type context
        LinkedHashMap<String, IntegrityField> sysFieldsHash = i.getFields();

        // In case no types are specified, then run the report for all types
        if (typeList.isEmpty() && doExport[Types.Type.getID()]) {
            typeList = i.getAdminList("types");
        }

        // For each type, abstract all relevant information
        if (Do(Types.Type)) {
            for (String typeName : typeList) {
                log("Processing Type: " + typeName);
                iTypes.add(new IntegrityType(i, i.viewType(typeName), doXML));
            }
        }
        // Get a list of Fields, if asked for
        if (Do(Types.Field)) {
            LinkedHashMap<String, IntegrityField> fields = i.getFields();
            // For each type, abstract all relevant information
            for (String fieldName : fields.keySet()) {
                log("Processing Field: " + fieldName);
                iFields.add(fields.get(fieldName));
            }
        }
        // Get a list of triggers, if asked for
        if (Do(Types.Trigger)) {
            iTriggers = TriggerFactory.parseTriggers(sysFieldsHash, i.viewTriggers(i.getAdminList("triggers")), doXML);
        }

        // Get a list of queries, if asked for
        // iQueries = QueryFactory.parseQueries(i.getQueries(), doXML);
        i.retrieveObjects(Types.Query);

        //iGroups = GroupFactory.parseGroups(i.getGroups(), doXML);
        i.retrieveObjects(Types.Group);

        // Get a list of queries, if asked for
        // iDynGroups = DynamicGroupFactory.parseDynGroups(i.getDynGroups(), doXML);
        i.retrieveObjects(Types.DynamicGroup);

        // Get a list of charts, if asked for
        if (Do(Types.Chart)) {
//                iCharts = ChartFactory.parseCharts(i.getCharts());
            iObjectList.set(Types.Chart.getID(), i.getCharts2());
//                WorkItemIterator objects = i.getObjects("chart");
//                while (objects.hasNext()) {
//                    WorkItem object = objects.next();
//                    iCharts.add(new IntegrityObject(object));
//                }
        }

        // iStates = IntegrityStateFactory.parseStates(i.getStates(), doXML);
        i.retrieveObjects(Types.State);
        // Get a list of viewsets, if asked for
        i.retrieveObjects(Types.Viewset);
        // iReports = ReportFactory.parseReports(i.getReports(), doXML);
        i.retrieveObjects(Types.Report);

        i.retrieveObjects(Types.CPType);

        // iTestVerdicts = TestVerdictFactory.parseTestVerdicts(i.getTestVerdicts(), doXML);
        i.retrieveObjects(Types.Verdict);
        // doTestResultFields
        i.retrieveObjects(Types.ResultField);

        i.retrieveObjects(Types.IMProject);

        i.retrieveObjects(Types.SIProject);

        // dashboards
        i.retrieveObjects(Types.Dashboard);
        i.retrieveObjects(Types.GatewayImportConfig);
        i.retrieveObjects(Types.GatewayExportConfig);
        i.retrieveObjects(Types.GatewayMapping);

        // Generate Transaction XML files for the Load Test Harness
        if (doXML) {
//            XMLWriter xWriter = new XMLWriter(iTypes, new List<?>[]{iTyQueries, iTriggers, iCharts, iViewsets});
//            xWriter.generate(sysFieldsHash);
//            // Open the folder containing the files
//            if (os.startsWith("Windows")) {
//                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + XML_CONTENT_DIR.getAbsolutePath());
//            }
        } else // Publish a report, if --xml is not specified
        {
            // Pass the abstraction to the DocWriter
            DocWriter doc = new DocWriter(i, iTypes, iFields, iTriggers, iObjectList);
            // Generate the report resources
            generateResources();

            // Publish the report content
            doc.publish();
            // Clean up the temporary files
            doc.cleanupTempFiles();

            // Open the report, if this is a windows client
            if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + REPORT_FILE.getAbsolutePath());
            }
        }
    }

    private boolean Do(Types type) {
        return doExport[type.getID()];
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
