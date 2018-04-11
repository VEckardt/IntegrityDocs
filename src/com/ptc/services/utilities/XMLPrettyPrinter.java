package com.ptc.services.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLPrettyPrinter {

    public static void serialize(File xmlFile, Document doc, boolean doDocType) {
        BufferedWriter xmlFileWriter = null;
        StringWriter sw = null;
        try {
            // Make sure the export directory exists
            if (!xmlFile.getParentFile().isDirectory()) {
                xmlFile.getParentFile().mkdirs();
            }
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer = tfactory.newTransformer();
            xmlFileWriter = new BufferedWriter(new FileWriter(xmlFile));
            // Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            if (doDocType) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "Job.dtd");
                serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            }
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            sw = new StringWriter();
            serializer.transform(new DOMSource(doc), new StreamResult(sw));
            xmlFileWriter.write(sw.toString());
        } catch (TransformerException te) {
            System.out.println("TransformerException: " + te.getMessage());
            te.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
            ioe.printStackTrace();
        } finally {
            try {
                if (null != sw) {
                    sw.flush();
                    sw.close();
                }
                if (null != xmlFileWriter) {
                    xmlFileWriter.flush();
                    xmlFileWriter.close();
                }
            } catch (IOException ioe) {
                System.out.println("IOException: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }
    }
}
