package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.session.APISession;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.utilities.CmdException;
import java.io.FileOutputStream;
import static java.lang.System.out;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class IntegrityDocs {

    public static final String iDOCS_REV = "$Revision: 11.0.2 $";
    public static final String copyright = "Copyright &copy; 2018 PTC Inc. All rights reserved.";
    private static final String os = System.getProperty("os.name");
    public static final String nl = System.getProperty("line.separator");
    public static final String fs = System.getProperty("file.separator");
    public static final File REPORT_DIR = new File(System.getProperty("user.home") + fs + "Desktop" + fs + "IntegrityDocs");
    public static final File REPORT_FILE = new File(REPORT_DIR.getAbsolutePath() + fs + "index.htm");
    public static final File CONTENT_DIR = new File(REPORT_DIR.getAbsolutePath() + fs + "WorkflowDocs");

    public static final File XML_CONTENT_DIR = new File(REPORT_DIR.getAbsolutePath() + fs + "WorkflowDocs-XML");
    public static final File XML_VIEWSETS_DIR = new File(XML_CONTENT_DIR.getAbsolutePath() + fs + "viewsets");

    // Activate specific lines for individual testing only
    private boolean doTypes = true;
    private boolean doQueries = true;
    private boolean doTriggers = true;
    private boolean doGroups = true;
    private boolean doStates = true;
    private boolean doDynGroups = true;
    private boolean doCharts = true;
    private boolean doReports = true;
    private boolean doViewsets = true;
    private boolean doFields = true;
    private boolean doTestVerdicts = true;
    private boolean doTestResultFields = true;
    private boolean doDashboards = true;
    private boolean doCPTypes = true;
    private boolean doIMProjects = true;
    private boolean doSIProjects = true;
    // convert to XML 
    private boolean doXML = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Only supporting Integrity 10 and newer releases
        out.println("Integrity Docs Version" + iDOCS_REV.substring(iDOCS_REV.lastIndexOf(':'), iDOCS_REV.lastIndexOf('$')));
        out.println("API Version: " + APISession.VERSION);
        out.println("Usage:");
        out.println("--xml: Generates an XML represenation of the Integrity data (not well maintained)");
        out.println("--noIMProjects:       disable IMProjects scan and output");
        out.println("--noSIProjects:       disable SIProjects scan and output");
        out.println("--noViewsets:         disable Viewsets scan and output");
        out.println("--noQueries:          disable Queries scan and output");
        out.println("--noTriggers:         disable Triggers scan and output");
        out.println("--noCharts:           disable Charts scan and output");
        out.println("--noGroups:           disable Groups scan and output");
        out.println("--noDynGroups:        disable DynGroups scan and output");
        out.println("--noStates:           disable States scan and output");
        out.println("--noReports:          disable Reports scan and output");
        out.println("--noFields:           disable Fields scan and output");
        out.println("--noTestVerdicts:     disable TestVerdict scan and output");
        out.println("--noTestResultFields: disable TestResultFields scan and output");
        out.println("--noCPTypes:          disable CPTypes scan and output");

        try {
            IntegrityDocs iDocs = new IntegrityDocs();
            iDocs.generateDocs(args);
            System.exit(0);
        } catch (APIException | ParserConfigurationException | IOException | SAXException | CmdException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + e.getMessage(),
                    "Integrity Docs - Generation Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(128);
        }
    }

    public void generateDocs(String[] args) throws APIException, ParserConfigurationException, IOException, SAXException, CmdException {

        List<IntegrityType> iTypes = new ArrayList<>();
        List<IntegrityField> iFields = new ArrayList<>();
        List<Trigger> iTriggers = new ArrayList<>();

        // List<List<IntegrityObject>> iObjects = new ArrayList();
        List<IntegrityObject> iCharts = new ArrayList<>();
        List<IntegrityObject> iViewsets = new ArrayList<>();
        List<IntegrityObject> iGroups = new ArrayList<>();
        List<IntegrityObject> iDynGroups = new ArrayList<>();
        List<IntegrityObject> iStates = new ArrayList<>();
        List<IntegrityObject> iTestVerdicts = new ArrayList<>();
        List<IntegrityObject> iTestResultFields = new ArrayList<>();
        List<IntegrityObject> iQueries = new ArrayList<>();
        List<IntegrityObject> iDashboards = new ArrayList<>();
        List<IntegrityObject> iReports = new ArrayList<>();
        List<IntegrityObject> iCPTypes = new ArrayList<>();
        List<IntegrityObject> iIMProjects = new ArrayList<>();
        List<IntegrityObject> iSIProjects = new ArrayList<>();

//        try {
        // Construct the Integrity Application
        Integrity i = new Integrity();

        // Get a string list of types
        List<String> typeList = new ArrayList<>();
        if (null != args && args.length > 0) {
            for (String arg : args) {
                if (arg.compareToIgnoreCase("--xml") == 0) {
                    doXML = true;
                } else if (arg.compareToIgnoreCase("--noQueries") == 0) {
                    doQueries = false;
                } else if (arg.compareToIgnoreCase("--noTriggers") == 0) {
                    doTriggers = false;
                } else if (arg.compareToIgnoreCase("--noCharts") == 0) {
                    doCharts = false;
                } else if (arg.compareToIgnoreCase("--noViewsets") == 0) {
                    doViewsets = false;
                } else if (arg.compareToIgnoreCase("--noGroups") == 0) {
                    doGroups = false;
                } else if (arg.compareToIgnoreCase("--noDynGroups") == 0) {
                    doDynGroups = false;
                } else if (arg.compareToIgnoreCase("--noStates") == 0) {
                    doStates = false;
                } else if (arg.compareToIgnoreCase("--noReports") == 0) {
                    doReports = false;
                } else if (arg.compareToIgnoreCase("--noFields") == 0) {
                    doFields = false;
                } else if (arg.compareToIgnoreCase("--noTestVerdicts") == 0) {
                    doTestVerdicts = false;
                } else if (arg.compareToIgnoreCase("--noTestResultFields") == 0) {
                    doTestResultFields = false;
                } else if (arg.compareToIgnoreCase("--noDashboards") == 0) {
                    doDashboards = false;
                } else if (arg.compareToIgnoreCase("--noCPTypes") == 0) {
                    doCPTypes = false;
                } else if (arg.compareToIgnoreCase("--noIMProjects") == 0) {
                    doIMProjects = false;
                } else if (arg.compareToIgnoreCase("--noSIProjects") == 0) {
                    doSIProjects = false;
                } else {
                    typeList.add(arg);
                }
            }
        }

        // Fetch the list of fields without the type context
        LinkedHashMap<String, IntegrityField> sysFieldsHash = i.getFields();

        // In case no types are specified, then run the report for all types
        if (typeList.isEmpty() && doTypes) {
            typeList = i.getAdminList("types");
        }

        // For each type, abstract all relevant information
        if (doTypes) {
            for (String typeName : typeList) {
                System.out.println("Processing Type: " + typeName);
                iTypes.add(new IntegrityType(i, i.viewType(typeName), doXML));
            }
        }

        // Get a list of queries, if asked for
        // iQueries = QueryFactory.parseQueries(i.getQueries(), doXML);
        retrieveObjects(doQueries, i, iQueries, "query", null);

        //iGroups = GroupFactory.parseGroups(i.getGroups(), doXML);
        retrieveObjects(doGroups, i, iGroups, "group", null);

        // Get a list of queries, if asked for
        // iDynGroups = DynamicGroupFactory.parseDynGroups(i.getDynGroups(), doXML);
        retrieveObjects(doDynGroups, i, iDynGroups, "dynamicgroup", null);

        // Get a list of triggers, if asked for
        if (doTriggers) {
            iTriggers = TriggerFactory.parseTriggers(sysFieldsHash, i.viewTriggers(i.getAdminList("triggers")), doXML);
        }

        // Get a list of charts, if asked for
        if (doCharts) {
//                iCharts = ChartFactory.parseCharts(i.getCharts());
            iCharts = i.getCharts2();
//                WorkItemIterator objects = i.getObjects("chart");
//                while (objects.hasNext()) {
//                    WorkItem object = objects.next();
//                    iCharts.add(new IntegrityObject(object));
//                }
        }

        // iStates = IntegrityStateFactory.parseStates(i.getStates(), doXML);
        retrieveObjects(doStates, i, iStates, "state", null);
        // Get a list of viewsets, if asked for
        retrieveObjects(doViewsets, i, iViewsets, "viewset", "Viewset");
        // iReports = ReportFactory.parseReports(i.getReports(), doXML);
        retrieveObjects(doReports, i, iReports, "report", null);

        retrieveObjects(doCPTypes, i, iCPTypes, "cptype", "ChangePackageType");

        // iTestVerdicts = TestVerdictFactory.parseTestVerdicts(i.getTestVerdicts(), doXML);
        retrieveObjects(doTestVerdicts, i, iTestVerdicts, "verdict", null);
        // doTestResultFields
        retrieveObjects(doTestResultFields, i, iTestResultFields, "resultfield", "TestResultField");

        retrieveObjects(doIMProjects, i, iIMProjects, "improject", "MainW&DProject");

        retrieveObjects(doSIProjects, i, iSIProjects, "project", "MainCMProject");

        // dashboards
        retrieveObjects(doDashboards, i, iDashboards, "dashboard", null);

        // Get a list of Fields, if asked for
        if (doFields) {
            LinkedHashMap<String, IntegrityField> fields = i.getFields();
            // For each type, abstract all relevant information
            for (String fieldName : fields.keySet()) {
                System.out.println("Processing Field: " + fieldName);
                iFields.add(fields.get(fieldName));
            }
        }

        // Generate Transaction XML files for the Load Test Harness
        if (doXML) {
            XMLWriter xWriter = new XMLWriter(iTypes, new List<?>[]{iQueries, iTriggers, iCharts, iViewsets});
            xWriter.generate(sysFieldsHash);
            // Open the folder containing the files
            if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + XML_CONTENT_DIR.getAbsolutePath());
            }
        } else // Publish a report, if --xml is not specified
        {
            // Pass the abstraction to the DocWriter
            DocWriter doc = new DocWriter(i, iTypes, iTriggers, iQueries,
                    iViewsets, iCharts, iGroups, iDynGroups,
                    iStates, iReports, iFields, iTestVerdicts,
                    iTestResultFields, iDashboards, iCPTypes, iIMProjects, iSIProjects);
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
//        } catch (APIException e) {
//            ExceptionHandler eh = new ExceptionHandler(e);
//            System.out.println(eh.getMessage());
//            System.out.println(eh.getCommand());
//            JOptionPane.showMessageDialog(null,
//                    "Failed to generate report!" + nl + eh.getMessage(),
//                    "Integrity Workflow Report - Error",
//                    JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        } catch (CmdException | ParserConfigurationException | SAXException | IOException ex) {
//            System.out.println("Caught " + ex.getClass().getName() + "!");
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(null,
//                    "Failed to generate report!" + nl + ex.getMessage(),
//                    "Integrity Workflow Report - Error",
//                    JOptionPane.ERROR_MESSAGE);
//        } finally {
//            if (null != i) {
//                i.exit();
//            }
//        }
    }

    private void retrieveObjects(Boolean doIt, Integrity i, List<IntegrityObject> iO, String name, String modelType) throws APIException {
        if (doIt) {
            // iTestResultFields = TestResultFieldFactory.parseTestResultFields(i.getTestResultFields(), doXML);
            WorkItemIterator objects = i.getObjects(name);
            while (objects.hasNext()) {
                WorkItem object = objects.next();
                if (modelType != null) {
                    iO.add(new IntegrityObject(object, modelType));
                } else {
                    iO.add(new IntegrityObject(object));
                }
            }
        }
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
