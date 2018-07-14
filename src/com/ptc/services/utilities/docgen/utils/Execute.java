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

import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Execute {

    private String szResult;

    public Execute() {
        szResult = new String();
    }

    public String getResult() {
        return szResult;
    }

    public int run(String cmd) {
        int exitValue = 128;

        try {
            //log("Executing Command: " + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            StreamReadThread errorReadingThread = new StreamReadThread(new InputStreamReader(process.getErrorStream()));
            StreamReadThread inputReadingThread = new StreamReadThread(new InputStreamReader(process.getInputStream()));

            inputReadingThread.start();
            errorReadingThread.start();

            try {
                // Wait for the process to complete
                process.waitFor();
                exitValue = process.exitValue();
                //log("Command Executed With Exit Value: " + exitValue);
            } catch (InterruptedException ex) {
                log("ReadThread: " + ex.getMessage());
            }

            inputReadingThread.interrupt();
            errorReadingThread.interrupt();

            while (inputReadingThread.isAlive());
            while (errorReadingThread.isAlive());

            String szErrorsResult = errorReadingThread.getResult();
            if (szErrorsResult.length() > 0) {
                szResult = szErrorsResult;
            } else {
                szResult = inputReadingThread.getResult();
            }
        } catch (IOException ioEx) {
            log("ReadThread: " + ioEx.getMessage());
        }

        return exitValue;
    }

    class StreamReadThread extends Thread {

        private BufferedReader stream;
        private String szResult;

        public StreamReadThread(InputStreamReader reader) {
            stream = new BufferedReader(reader);
        }

        public void run() {
            szResult = "";
            try {
                String line;
                // Keep reading the output until we're told to stop
                while (!interrupted()) {
                    line = stream.readLine();
                    if (line != null) {
                        szResult += line + nl;
                    }
                }
                // Read any left over output
                line = stream.readLine();
                while (null != (line = stream.readLine())) {
                    szResult += line + nl;
                }
            } catch (Exception ex) {
                log("ReadThread: " + ex.getMessage());
            }
        }

        public String getResult() {
            return szResult;
        }
    }
}
