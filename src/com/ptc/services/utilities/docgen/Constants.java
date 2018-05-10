/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author veckardt
 */
public class Constants {

    // Global Consts
    private static final Date now = new Date();
    static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
    public static final String os = System.getProperty("os.name");
    public static final String nl = System.getProperty("line.separator");
    public static final String fs = System.getProperty("file.separator");

    // statis directories and files
    public static final File REPORT_DIR = new File(System.getProperty("user.home") + fs + "Desktop" + fs + "IntegrityDocs");
    public static final File CONTENT_DIR = new File(REPORT_DIR.getAbsolutePath() + fs + "WorkflowDocs");
    public static final File INDEX_FILE = new File(REPORT_DIR.getAbsolutePath() + fs + "index.htm");
    public static final File SUMMARY_FILE = new File(REPORT_DIR + fs + "summary.htm");
    public static final File TOC_FILE = new File(REPORT_DIR + fs + "navi.htm");
    public static final File TITLE_FILE = new File(REPORT_DIR + fs + "title.htm");

    // temporarly files only, will be deleted afterwards
    public static final File objectTemplate = new File(CONTENT_DIR + fs + "ObjectTemplate.txt");
    public static final File titleTemplate = new File(CONTENT_DIR + fs + "title.txt");
    public static final File summaryTemplate = new File(CONTENT_DIR + fs + "SummaryTemplate.txt");
    public static final File overviewTemplate = new File(CONTENT_DIR + fs + "OverviewTemplate.txt");
    public static final File indexStartFile = new File(CONTENT_DIR + fs + "index_start.htm");
    public static final File indexEndFile = new File(CONTENT_DIR + fs + "index_end.htm");
    public static final File naviStartFile = new File(CONTENT_DIR + fs + "navi_start.htm");
    public static final File naviEndFile = new File(CONTENT_DIR + fs + "navi_end.htm");

    // sub directories
    public static final File CONTENT_IMAGES_DIR = new File(CONTENT_DIR.getAbsolutePath() + fs + "images");
    public static final File CONTENT_XML_DIR = new File(CONTENT_DIR.getAbsolutePath() + fs + "xml");
    public static final File CONTENT_XML_VIEWSETS_DIR = new File(CONTENT_XML_DIR.getAbsolutePath() + fs + "viewsets");

    /**
     * getNow
     *
     * @return
     */
    public static String getNow() {
        return sdf.format(now);
    }

    /**
     * cleanupTempFiles
     */
    public static void cleanupTempFiles() {
        log("Finishing publishing ...");
        indexStartFile.delete();
        indexEndFile.delete();
        naviStartFile.delete();
        naviEndFile.delete();
        titleTemplate.delete();
        overviewTemplate.delete();
        objectTemplate.delete();
        summaryTemplate.delete();
    }
}
