package com.ptc.services.utilities;

import java.util.Comparator;

import org.w3c.dom.Node;

public class DefaultNodeNameComparator implements Comparator<Node> {

   @Override
   public int compare(Node n1, Node n2) {
      if ((null == n1) || (null == n2)) {
         return 0;
      } else {
         return n1.getNodeName().compareTo(n2.getNodeName());
      }
   }
}
