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
public class GatewayMappings extends ArrayList implements WorkItemIterator {

   int len = 0;

   @Override
   public WorkItem next() throws APIException {
      return (GatewayMapping) this.get(len++);
   }

   @Override
   public boolean hasNext() {
      return len < this.size();
   }

   @Override
   public WorkItem getLast() {
      return (GatewayMapping) this.get(size() - 1);
   }

   /**
    * Get Gateway Mappings
    *
    * @param path
    * @return
    * @throws APIException
    */
   public GatewayMappings(String path) throws APIException {

      // this runs on server
      Response response = execute(new Command("im", "gatewayconfigurations"));
      // ResponseUtil.printResponse(response, 1, System.out);
      int cnt = 0;
      for (WorkItemIterator wii = response.getWorkItems(); wii.hasNext();) {
         try {
            WorkItem wi = wii.next();
            cnt++;
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
            NodeList nList = doc.getElementsByTagName("mapping");

            Node nNode = nList.item(0);
            if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;

               GatewayMapping gc = new GatewayMapping();
               gc.addField("name", eElement.getAttribute("name"));
               try {
                  gc.addField("description", eElement.getElementsByTagName("description").item(0).getTextContent());
               } catch (NullPointerException ex) {
                  gc.addField("description", "-");
               }
               gc.addField("position", String.valueOf(cnt));
               gc.addField("type", "mapping");
               gc.addField("isActive", "true");

               String fileName = eElement.getAttribute("name") + ".xml";

               FileUtils.resourceToFile(new ByteArrayInputStream(field.getString().getBytes(encoding)), path, fileName);
               gc.addField("Mapping File", "<a href=\"" + fileName + "\" download=\"" + fileName + ".txt\">" + fileName + "</a>");
               this.add(gc);
            }

         } catch (APIException | ParserConfigurationException | SAXException | IOException e) {
            log("ERROR: " + (new StringBuilder()).append("Skipping remote configuration due to error: ").append(e.getMessage()).toString(), 10);
            exception(Level.WARNING, 10, e);
         }
      }
   }

}
