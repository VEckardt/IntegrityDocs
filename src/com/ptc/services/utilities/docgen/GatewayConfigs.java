/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.Command;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.IntegrityUtils.execute;
import com.ptc.services.utilities.docgen.utils.FileUtils;
import static com.ptc.services.utilities.docgen.utils.Logger.exception;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author veckardt
 */
public class GatewayConfigs extends ArrayList implements WorkItemIterator {

   int len = 0;

   @Override
   public WorkItem next() throws APIException {
      return (GatewayConfig) this.get(len++);
   }

   @Override
   public boolean hasNext() {
      return len < this.size();
   }

   @Override
   public WorkItem getLast() {
      return (GatewayConfig) this.get(size() - 1);
   }

   public GatewayConfigs(String gcType, String elem, String path) throws APIException {

      // this runs on server
      Response response = execute(new Command("im", "gatewaywizardconfigurations"));
      // ResponseUtil.printResponse(response, 1, System.out);

      for (WorkItemIterator wii = response.getWorkItems(); wii.hasNext();) {
         WorkItem wi = wii.next();
         try {
            String encoding = null;
            try {
               encoding = wi.getField("Encoding").getString();
            } catch (NoSuchElementException ignored) {
            }
            if (encoding == null) {
               encoding = "UTF-8";
            }
            Field field = wi.getField("Content");
            java.io.InputStream xmlReader = new ByteArrayInputStream(field.getString().getBytes(encoding));
            // registerConfig("<remote>", (new ItemMapperParser(xmlReader)).process());

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlReader);
            doc.getDocumentElement().normalize();
            // log("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName(gcType + "-config");

            for (int temp = 0; temp < nList.getLength(); temp++) {
               Node nNode = nList.item(temp);
               // log("\nCurrent Element :" + nNode.getNodeName());

               if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                  Element eElement = (Element) nNode;

                  GatewayConfig gc = new GatewayConfig();
                  gc.addField("name", eElement.getElementsByTagName("name").item(0).getTextContent());
                  gc.addField("description", eElement.getElementsByTagName("description").item(0).getTextContent());
                  gc.addField("position", String.valueOf(temp + 1));
                  for (int ext = 0; ext < eElement.getElementsByTagName("extension").getLength(); ext++) {
                     gc.addField("extensions (" + (ext + 1) + ")", eElement.getElementsByTagName("extension").item(ext).getTextContent());
                  }
                  String exporterClass = "";
                  try {
                     exporterClass = eElement.getElementsByTagName(elem).item(0).getAttributes().getNamedItem("class").getTextContent();
                  } catch (NullPointerException skip) {

                  }
                  String id = "";
                  try {
                     id = eElement.getElementsByTagName(elem).item(0).getAttributes().getNamedItem("id").getTextContent();
                  } catch (NullPointerException skip) {

                  }

                  gc.addField("type", exporterClass.length() > 10 ? "custom" : "standard");
                  gc.addField("isActive", "true");
                  gc.addField("gateway-configuration-name", eElement.getElementsByTagName("gateway-configuration-name").item(0).getTextContent());
                  gc.addField(elem + " class", exporterClass);
                  gc.addField(elem + " id", id);

                  NodeList pList = eElement.getElementsByTagName(elem).item(0).getChildNodes();
                  for (int propId = 0; propId < pList.getLength(); propId++) {
                     Node pNode = pList.item(propId);
                     if (pNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element pElement = (Element) pNode;

                        if (pElement.getAttribute("name").equals("template") || pElement.getAttribute("name").equals("xslt")) {
                           FileUtils.inputStreamToFile(path, pElement.getTextContent());
                           String fileName = pElement.getTextContent().substring(pElement.getTextContent().lastIndexOf('/') + 1);
                           gc.addField(elem + " property '" + pElement.getAttribute("name") + "'", "<a href=\"" + fileName + "\">" + pElement.getTextContent() + "</a>");
                        } else {
                           gc.addField(elem + " property '" + pElement.getAttribute("name") + "'", pElement.getTextContent());
                        }
                     }
                  }
                  this.add(gc);
               }
            }
         } catch (ParserConfigurationException | SAXException | IOException e) {
            log("ERROR: " + (new StringBuilder()).append("Skipping remote configuration due to error: ").append(e.getMessage()).toString(), 10);
            exception(Level.WARNING, 10, e);
         }
      }
   }

}
