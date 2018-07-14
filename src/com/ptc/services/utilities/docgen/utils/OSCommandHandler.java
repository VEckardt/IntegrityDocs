/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen.utils;

import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author veckardt
 */
public class OSCommandHandler {

    private String[] outputFilter;
    private String filteredResult;
    private String unfilteredResult;

    /**
     * Constructor
     *
     * @param outputFilter
     */
    public OSCommandHandler(String[] outputFilter) {
        this.outputFilter = outputFilter;
        this.filteredResult = "";
        this.unfilteredResult = "";
    }

    public OSCommandHandler() {
        this.outputFilter = null;
        this.filteredResult = "";
        this.unfilteredResult = "";
    }

    public String getFilteredResult() {
        return filteredResult;
    }

    public String getUnfilteredResult() {
        return unfilteredResult;
    }

    /**
     *
     * @param command
     * @param wait
     * @return
     */
    public int executeCmd(String command, Boolean wait) {

        int exitCode;
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
            // pb.environment().put("MKSSI_ISSUE0", "617");
            pb.redirectErrorStream();
            Process p = pb.start();
            // any error message?
            StreamConsumer ise = new StreamConsumer(p.getErrorStream(), "ERROR");

            StreamConsumer isc = new StreamConsumer(p.getInputStream(), "OUTPUT");
            isc.start();
            ise.start();

            if (wait) {
                exitCode = p.waitFor();
            } else {
                exitCode = p.exitValue();
            }

            isc.join();
            ise.join();
            if (exitCode != 0) {
                log("OS Process terminated with " + exitCode);
            }
            return exitCode;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(OSCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    /**
     * Inner Class to handle Output and Error
     */
    public class StreamConsumer extends Thread {

        private InputStream is;
        private String type;

        public StreamConsumer(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.isEmpty()) {
                        // log(type + "> " + line);
                        if (outputFilter != null) {
                            for (int i = 0; i < outputFilter.length; i++) {
                                if (line.contains(outputFilter[i])) {
                                    filteredResult = filteredResult.concat(ltrim(line)) + "\n";
                                }
                            }
                        }
                        unfilteredResult = unfilteredResult.concat(line) + "\n";
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(OSCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Trims the left part of the string
     *
     * @param text
     * @return
     */
    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    public static String ltrim(String text) {
        return LTRIM.matcher(text).replaceAll("");
    }
}
