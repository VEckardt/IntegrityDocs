package com.ptc.services.utilities.docgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.utilities.CmdException;
import java.util.LinkedHashMap;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ViewsetFactory {

    private static final File tmpExportDir = new File(IntegrityDocs.XML_VIEWSETS_DIR.getAbsolutePath() + ".tmp");

    public static List<Viewset> parseViewsets(Integrity i, WorkItemIterator wii, LinkedHashMap<String, IntegrityField> fieldsHash, boolean doXML)
            throws APIException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, CmdException {
        List<Viewset> vsList = new ArrayList<>();
        Hashtable<String, String> typeIDs = new Hashtable<>();
        Hashtable<String, String> queryIDs = new Hashtable<>();
        Hashtable<String, String> chartIDs = new Hashtable<>();

        if (null != wii && wii.hasNext()) {
            // Get a list of all Type, Query, and Chart Admin IDs which is required to scrub the viewset layout xml
            if (doXML) {
                typeIDs = i.getAdminIDList("types");
                queryIDs = i.getAdminIDList("queries");
                chartIDs = ChartFactory.getChartAdminIDs(i.getCharts());
            }

            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                // Only process published server viewsets
                if (Integrity.getStringFieldValue(wi.getField("publishedState")).equalsIgnoreCase("Published (Server)")) {
                    Viewset v = new Viewset(wi.getId());
                    v.setName(Integrity.getStringFieldValue(wi.getField("name")));
                    System.out.println("Processing Viewset: " + v.getName());
                    v.setPublishedState(Integrity.getStringFieldValue(wi.getField("publishedState")));
                    v.setMandatory(Integrity.getBooleanFieldValue(wi.getField("mandatory")));
                    v.setCustomizable(Integrity.getBooleanFieldValue(wi.getField("customizable")));
                    v.setCreatedBy(Integrity.getStringFieldValue(wi.getField("creator")));
                    v.setModifiedDate(wi.getField("modifiedDate").getDateTime());
                    v.setDescription(Integrity.getStringFieldValue(wi.getField("description")));

                    // Get the XML layout of the viewset for the XML export mode
                    if (doXML) {
                        v.setViewsetLayout(fieldsHash, typeIDs, queryIDs, chartIDs, fetchViewset(i, v.getName()));
                    }
                    vsList.add(v);
                }
            }

            // Clean up temporary files generated
            cleanup();
        }
        return vsList;
    }

    public static Document fetchViewset(Integrity i, String viewset) throws APIException, ParserConfigurationException, SAXException, IOException {
        // Create a temporary folder for the initial export
        if (!tmpExportDir.isDirectory()) {
            tmpExportDir.mkdirs();
        }
        File vsFile = i.fetchViewset(viewset, tmpExportDir);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setValidating(false);
        DocumentBuilder docBuilder = domFactory.newDocumentBuilder();
        return docBuilder.parse(vsFile);

    }

    private static void cleanup() {
        if (tmpExportDir.isDirectory()) {
            // First remove all the files in the temporary folder
            File[] tmpFiles = tmpExportDir.listFiles();
            for (int i = 0; i < tmpFiles.length; i++) {
                tmpFiles[i].delete();
            }

            // Finally, remove the temporary folder itself
            tmpExportDir.delete();
        }
    }
}
