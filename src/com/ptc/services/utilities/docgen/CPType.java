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
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author veckardt
 */
public class CPType extends IntegrityObject {

   public CPType(WorkItem workitem) {
      super(workitem, Types.CPType);

   }

   @Override
   public Element getXML(Document job, Element command) {
      // Add this field to the global resources hash
      
      //
      //  Not supported yet
      //
      
      
//      XMLWriter.paramsHash.put(getXMLPrefix() + XMLWriter.getXMLParamName(name), name);
//
//      // Setup the command to re-create the state via the Load Test Harness...
//      Element app = job.createElement("app");
//      app.appendChild(job.createTextNode(Command.IM));
//      command.appendChild(app);
//
//      Element cmdName = job.createElement("commandName");
//      cmdName.appendChild(job.createTextNode("creategroup"));
//      command.appendChild(cmdName);
//
//      // Finally add the name for this state
//      command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
//      if (description != null && description.length() > 0) {
//         command.appendChild(XMLWriter.getOption(job, "description", description));
//      }
      return command;
   }
}
