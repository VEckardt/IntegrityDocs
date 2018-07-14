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

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import java.util.ArrayList;

/**
 *
 * @author veckardt
 */
public class Relationships extends ArrayList implements WorkItemIterator {

   int len = 0;

   @Override
   public WorkItem next() throws APIException {
      return (SimpleWorkItem) this.get(len++);
   }

   @Override
   public boolean hasNext() {
      return len < this.size();
   }

   @Override
   public WorkItem getLast() {
      return (SimpleWorkItem) this.get(size() - 1);
   }

}
