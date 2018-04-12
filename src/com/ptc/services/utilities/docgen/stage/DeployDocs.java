package com.ptc.services.utilities.docgen.stage;

import com.ptc.services.utilities.docgen.stage.StagingSystem;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

import com.mks.api.Command;
import com.mks.api.response.APIException;
import com.ptc.services.utilities.docgen.utils.ExceptionHandler;
import com.ptc.services.utilities.docgen.Integrity;
import java.util.Arrays;

public class DeployDocs {

    private static final String os = System.getProperty("os.name");
    public static final String nl = System.getProperty("line.separator");
    public static final String fs = System.getProperty("file.separator");
    private static final String REPORT_FILE = System.getProperty("user.home") + fs
            + "Desktop" + fs + "DeploySetup.html";
    public static final String REPORT_DIR = System.getProperty("user.home") + fs + "Desktop";

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            DeployDocs sdDocs = new DeployDocs();
            sdDocs.generateDocs(args);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + e.getMessage(),
                    "Staging and Deploy Configuration Report - Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public DeployDocs() {

    }

    public void generateDocs(String[] args) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILE));

            // Construct the Integrity Application
            Integrity i = new Integrity(Command.SI);

            // Get a string list of staging systems
            List<String> stagingSystemsList = new ArrayList<>();
            if (null != args && args.length > 0) {
                stagingSystemsList.addAll(Arrays.asList(args));
            }

            // In case no staging systems are specified, then run the report for all staging systems
            if (stagingSystemsList.isEmpty()) {
                stagingSystemsList = i.getStagingSystems();
            }

            List<StagingSystem> sdStagingSystems = new ArrayList<>();
            // For each Staging System, abstract all relevant information
            for (String stagingSystem : stagingSystemsList) {
                System.out.println("Processing Staging System: " + stagingSystem);
                StagingSystem ss = new StagingSystem(i, stagingSystem);
                sdStagingSystems.add(ss);
            }

            // Pass the abstraction to the DeployDocWriter
            DeployDocWriter doc = new DeployDocWriter(i.getHostName() + ':' + i.getPort(), writer, sdStagingSystems);
            doc.write();
            // Close the file handle on the report
            writer.flush();
            writer.close();

            // Open the report, if this is a windows client
            if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + REPORT_FILE);
            }
        } catch (APIException e) {
            ExceptionHandler eh = new ExceptionHandler(e);
            System.out.println(eh.getMessage());
            System.out.println(eh.getCommand());
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + eh.getMessage(),
                    "Staging and Deploy Configuration Report - Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Caught I/O Exception!");
            JOptionPane.showMessageDialog(null,
                    "Failed to generate report!" + nl + ioe.getMessage(),
                    "Staging and Deploy Configuration Report - Error",
                    JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        }
    }
}
