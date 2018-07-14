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
import com.mks.api.response.Response;
import com.mks.api.util.ResponseUtil;
import com.ptc.services.utilities.docgen.utils.Logger;

/**
 *
 * @author veckardt
 */
public class GatewayTest {

   public static void main(String[] args) throws APIException {
      Logger.init();
      // WorkItemIterator wit = i.getGatewayConfigs("export", "exporter", "");
//        WorkItemIterator wit = i.getGatewayConfigs("parser", "parser", CONTENT_DIR + fs + "GatewayImportConfigs");
//        while (wit.hasNext()) {
//            WorkItem gc = wit.next();
//            out.println(gc.getId());
//            Iterator it = gc.getFields();
//            while (it.hasNext()) {
//                Field fld = (Field) it.next();
//                out.println(" " + fld.getName() + ": " + fld.getValueAsString());
//            }
//
//        }

      Command cmd = new Command("im", "viewdynamicgroup");
      cmd.addSelection("Project Team");
      Response resp = Integrity.execute(cmd);
      ResponseUtil.printResponse(resp, 1, System.out);
   }
}
