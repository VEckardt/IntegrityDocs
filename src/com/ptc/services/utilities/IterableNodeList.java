package com.ptc.services.utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IterableNodeList implements Iterable<Node>, Iterator<Node> {

   private final NodeList nodeList;
   private int index = 0;

   public IterableNodeList(final NodeList nodeList) {
      this.nodeList = nodeList;
   }

   public boolean hasNext() {
      return index < nodeList.getLength();
   }

   public Iterator<Node> iterator() {
      return this;
   }

   public Node next() throws NoSuchElementException {
      if (!hasNext()) {
         throw new NoSuchElementException();
      }
      return nodeList.item(index++);
   }

   public void remove() throws IllegalStateException, UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
