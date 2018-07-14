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
import static com.ptc.services.utilities.docgen.Constants.fs;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author veckardt
 */
public class GatewayTemplates
        extends ArrayList implements WorkItemIterator {

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

   /**
    * Gateway Templates
    *
    * @param path1
    * @param path2
    */
   public GatewayTemplates(String path1, String path2) {
      int cnt = 0;
      File filePath = new File(path2);
      if (filePath.exists()) {
         File[] listOfFiles = filePath.listFiles();
         if (listOfFiles != null) {
            for (File file : listOfFiles) {
               if (file.isFile() && (file.getName().toLowerCase().contains(".xsl"))) {
                  // String fname = path2 + fs + file.getName();
                  SimpleWorkItem itm = new SimpleWorkItem(Types.GatewayTemplate.name(), file.getName());
                  itm.add("id", String.valueOf(++cnt));
                  itm.add("name", file.getName());
                  itm.add("type", "Import");
                  itm.add("view", Types.GatewayImportConfig.getPlural() + fs + file.getName());
                  this.add(itm);
               }
            }
         }
      } else {
         log("INFO: no Gateway Import Templates found.");
      }
      filePath = new File(path1);
      if (filePath.exists()) {
         File[] listOfFiles = filePath.listFiles();
         if (listOfFiles != null) {
            for (File file : listOfFiles) {
               if (file.isFile() && (file.getName().toLowerCase().contains(".xls")
                       || file.getName().toLowerCase().contains(".doc"))) {
                  // String fname = path1 + fs + file.getName();
                  SimpleWorkItem itm = new SimpleWorkItem(Types.GatewayTemplate.name(), file.getName());
                  itm.add("id", String.valueOf(++cnt));
                  itm.add("name", file.getName());
                  itm.add("type", "Export");
                  itm.add("view", Types.GatewayExportConfig.getPlural() + fs + file.getName());
                  this.add(itm);
               }
            }
         }
      } else {
         log("INFO: no Gateway Export Templates found.");
      }
   }
}
