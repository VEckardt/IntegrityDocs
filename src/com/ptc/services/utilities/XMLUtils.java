package com.ptc.services.utilities;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {

   public static void removeAllChildren(Node node) {
      NodeList nl = node.getChildNodes();
      for (int i = nl.getLength() - 1; i >= 0; i--) {
         Node n = nl.item(i);
         if (n.hasChildNodes()) {
            removeAllChildren(n);
         }
         node.removeChild(n);
      }
   }

   public static void clearSetting(Document doc, XPath xp, String attribute) throws XPathExpressionException {
      NodeList nlNodes = (NodeList) xp.evaluate("//Setting[@name='" + attribute + "']", doc, XPathConstants.NODESET);
      for (Node n : new IterableNodeList(nlNodes)) {
         n.setTextContent("");
      }
   }
}
