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

import com.mks.api.util.MKSLogger;
import static java.lang.System.out;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

/**
 *
 * @author veckardt
 */
public class Logger {

    private static final String tmpDir = System.getProperty("java.io.tmpdir");
    private static MKSLogger logger;
    public static String LOGFILE = tmpDir + "IntegrityDocs_" + getDate() + ".log";

    public final static void init() {
        logger = new MKSLogger(LOGFILE);
        // IntegrityDocs.log("APISession");
        logger.configure(getLoggerProperties());
    }

    public final static void log(String text) {
        out.println(text);
        logger.message(text);
    }

    public final static void log(String text, int level) {
        out.println(text);
        logger.message(text);
    }

    public final static void print(String text) {
        out.print(text);
        logger.message(text);
    }

//    public final static void log(String text, int level, Exception e) {
//        log(text);
//        log(e.getMessage());
//    }

    public final static void exception(Level level, int priority, Exception e) {
        e.printStackTrace();
        log(e.getMessage());
        logger.exception(level.toString(), priority, e);
    }

    public final static String getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("CET"));
        return df.format(new Date());
    }

    public final static Properties getLoggerProperties() {
        // Initialize logger properties
        Properties loggerProps = new Properties();

        // Logging Categories
        loggerProps.put("mksis.logger.message.includeCategory.DEBUG", "10");
        loggerProps.put("mksis.logger.message.includeCategory.WARNING", "10");
        loggerProps.put("mksis.logger.message.includeCategory.GENERAL", "10");
        loggerProps.put("mksis.logger.message.includeCategory.ERROR", "10");
        // Output Format
        loggerProps.put("mksis.logger.message.defaultFormat", "{2}({3}): {4}");
        loggerProps.put("mksis.logger.message.format.DEBUG", "{2}({3}): {4}");
        loggerProps.put("mksis.logger.message.format.WARNING", "* * * * {2} * * * * ({3}): {4}");
        loggerProps.put("mksis.logger.message.format.ERROR", "* * * * {2} * * * * ({3}): {4}");

        return loggerProps;
    }

    public static String getLogFile() {
        return LOGFILE;
    }
}
