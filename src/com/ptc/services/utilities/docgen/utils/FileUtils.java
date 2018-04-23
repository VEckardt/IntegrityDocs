/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import static com.ptc.services.utilities.docgen.IntegrityDocs.fs;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author veckardt
 */
public class FileUtils {

    public static void inputStreamToFile(String templateFile, String targetFolder)
            throws IOException {
        String fileName = templateFile.substring(templateFile.lastIndexOf('/') + 1);

        // FileInputStream inputDocStream = null;
        InputStream inputStream;
        if (templateFile.toLowerCase().startsWith("http")) {
            // URL url = new URL(URLEncoder.encode(templateFile, "UTF-8"));
            URL url = new URL(templateFile.replace(" ", "%20"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = new FileInputStream(new File(templateFile));
        }

        File targetFile = new File(targetFolder + fs + fileName);
        targetFile.mkdirs();
        java.nio.file.Files.copy(
                inputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        inputStream.close();
    }

    public static void resourceToFile(InputStream inputStream, String targetFolder, String fileName)
            throws IOException {

        File targetFile = new File(targetFolder + fs + fileName);
        targetFile.mkdirs();
        java.nio.file.Files.copy(
                inputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }
}
