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

import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItemIterator;
import com.mks.api.response.InterruptedException;

public class ExceptionHandler {

    // Private variables to provide diagnostics on the exception
    private String message;
    private String command;
    private int exitCode;

    /**
     * Constructor requires a single APIException to debug
     *
     * @param ex APIException
     */
    public ExceptionHandler(APIException ex) {

        // API Exceptions can be nested.  Hence we will need to recurse the 
        // exception hierarchy to dig for a conclusive message
        Response response = ex.getResponse();

        // Print the stack trace to standard out for debugging purposes
        ex.printStackTrace();

        // The API failed to execute the command (i.e. a real API error)
        if (null == response) {
            message = ex.getMessage();
            command = new java.lang.String();
            //exitCode = Integer.parseInt(ex.getExceptionId());
            exitCode = -1;
        } else {
            command = response.getCommandString();
            try {
                exitCode = response.getExitCode();
            } catch (InterruptedException ie) {
                // Just print out the stack trace
                ie.printStackTrace();
                exitCode = -1;
            }
            WorkItemIterator wit = response.getWorkItems();
            // In the event there is a problem with one of the command's elements
            // we have to dig deeper into the exception...
            try {
                while (wit.hasNext()) {
                    wit.next();
                }
                // If we got here then just extract the message
                if (ex.getMessage() != null) {
                    message = ex.getMessage();
                }
            } catch (APIException ae) {
                // This message will be the real reason for the exception
                String curMessage = ae.getMessage();
                if (curMessage != null) {
                    message = curMessage;
                }
                ae.printStackTrace();
            }
        }
    }

    /**
     * Returns the Message obtained from the APIException
     *
     * @return message APIException String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the executed command that caused the exception
     *
     * @return command Complete CLI Command String
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the exit codes associated with executing the command
     *
     * @return exitCode API/CLI Exit Code
     */
    public int getExitCode() {
        return exitCode;
    }
}
