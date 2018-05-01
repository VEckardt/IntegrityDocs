/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import com.ptc.services.utilities.docgen.Copyright;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author veckardt
 */
public class ApplicationProperties extends Properties {

    private static final long serialVersionUID = 1L;

    // static Properties properties;
    static String propertiesFileName = "ApplicationProperties.properties";
    static String propertiesFilePath = "";
    private String fileToRead = propertiesFilePath + propertiesFileName;
    // static Map<String, String> env = System.getenv();

    public ApplicationProperties(Class mainClass) {
        propertiesFileName = "Integrity" + mainClass.getSimpleName().replace("Integrity", "") + ".properties";
        try {
            // this.mainClass = mainClass;
            propertiesFilePath = getJarContainingFolder(mainClass) + File.separator;
        } catch (Exception ex) {
            propertiesFilePath = "";
        }
        fileToRead = propertiesFilePath + propertiesFileName;
        loadProperties();
    }

    public static String getJarContainingFolder(Class aclass) throws Exception {
        CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

        File jarFile;

        if (codeSource.getLocation() != null) {
            jarFile = new File(codeSource.getLocation().toURI());
        } else {
            String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
            String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
            jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
            jarFile = new File(jarFilePath);
        }
        return jarFile.getParentFile().getAbsolutePath();
    }

    public String getPropFile() {
        return fileToRead;
    }

    public boolean saveProperties() {
        boolean written = false;
        OutputStream out = null;
        try {
            out = new FileOutputStream(fileToRead);
            this.store(out, Copyright.copyright + ", " + Copyright.author);
            written = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ApplicationProperties.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            log(ex.getMessage(), 1);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationProperties.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            log(ex.getMessage(), 1);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(ApplicationProperties.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return written;
    }

    /**
     * Loads the properties from the current directory, if not exists, then from
     * the java class path
     *
     * @return
     */
    public final boolean loadProperties() {
        log("Reading Properties ...", 1);
        try {
            // properties = new Properties();
            // iso encoding!!!
            InputStream in = this.getClass().getResourceAsStream(fileToRead);
            try {
                log("Reading from file: " + fileToRead + " (P1)", 2);
                this.load(in);
                log(fileToRead + " loaded.", 2);
            } catch (NullPointerException e) {
                log("Property file in default path not found.", 2);
                String classpaths = System.getenv().get("CLASSPATH");
                if (classpaths != null) {
                    String classpath = classpaths.split(";")[0];
                    if (classpath != null && !classpath.isEmpty()) {
                        fileToRead = classpath + File.separator + propertiesFileName;
                        log("Reading from file: " + fileToRead + " (P2)", 2);
                        in = new FileInputStream(fileToRead);
                        // in = this.getClass().getResourceAsStream("AdminTool.properties");
                        this.load(in);
                    }
                } else {
                    log("CLASSPATH not defined, skipping property file search in classpath. (P2)", 2);
                }
            }
            Enumeration em = this.keys();
            while (em.hasMoreElements()) {
                String str = (String) em.nextElement();
                log(str + ": " + this.get(str), 2);
            }
            return true;
        } catch (IOException e) {
            log("WARNING: " + e.getMessage(), 1);
            return false;
        }
    }

    private void log(String text, int level) {
        String str = "          ".substring(0, level - 1);
        // if (GatewayLogger.getLogFile() == null) {
        Logger.getLogger(ApplicationProperties.class.getName()).log(Level.INFO, "(" + level + ") " + str + text);
        // log("(" + level + ") " + str + string);
        // } else {
        //     GatewayLogger.logMessage(str + text, level);
        // }

    }

    @Override
    public String getProperty(String property, String defaultValue) {
        String value = super.getProperty(property, defaultValue);
        this.put(property, value);
        return value;
    }

//    @Override
//    public void setProperty(String property, String value) {
//        properties.put(property, value);
//    }
    @Override
    public Enumeration<Object> keys() {
        Enumeration<Object> keysEnum = super.keys();
        Vector<Object> keyList = new Vector<>();

        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }

        Collections.sort(keyList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return keyList.elements();
    }

}
