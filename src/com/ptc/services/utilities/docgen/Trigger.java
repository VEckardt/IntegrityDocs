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
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Constants.USER_XML_PREFIX;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityQuery.XML_PREFIX;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static java.lang.String.format;

/**
 *
 * @author veckardt
 */
public class Trigger extends IntegrityObject {

   public static final String XML_PREFIX = "TRIGGER_";

   private String script = "";
   private String assign = "";
   private String frequency = "";
   private String runAs = "";
   private String query = "";
   private String rule = "";
   private String scriptParams = "";
   private String scriptTiming = "";

   public List<String> typeNameList = new ArrayList<>();

   public Trigger(WorkItem wi) {
      super(wi, Types.Trigger);
      setName(wi.getField("name").getValueAsString());
      rule = wi.getField("rule").getValueAsString();
      type = wi.getField("type").getValueAsString();
      query = wi.getField("query").getValueAsString();
      position = wi.getField("position").getValueAsString();
      script = wi.getField("script").getValueAsString();
      description = wi.getField("description").getValueAsString();

      for (Object param : wi.getField("scriptParams").getList()) {
         Item item = (Item) param;
         scriptParams += scriptParams.isEmpty() ? "" : ",\n";
         scriptParams += item.getId() + "=" + item.getField("value").getValueAsString();
      }

      scriptTiming = wi.getField("scriptTiming").getValueAsString();
      for (Object param : wi.getField("assign").getList()) {
         Item item = (Item) param;
         assign += assign.isEmpty() ? "" : ",\n";
         assign += item.getId() + "=" + item.getField("value").getValueAsString();
      }

      if (rule != null && rule.contains("item is segment")) {
         typeNameList.add("All Documents");
      } else if (rule != null && (rule.contains("item is node") || rule.contains("item is content"))) {
         typeNameList.add("All Document Nodes");
      } else if (rule != null && (rule.contains("[\"Type\"] =") || rule.contains("[\"Typ\"] ="))) {
         // typeNameList.add("Type related");
         Pattern p = Pattern.compile("field'?\\[\"Type?\"\\] = (\"[a-zA-Z ]+\")");
         Matcher m = p.matcher(rule.replaceAll("==", "="));
         while (m.find()) {
            // System.out.println();
            typeNameList.add(m.group(1).replace("\"", ""));
         }
      } else {
         typeNameList.add(type);
      }
      if (type.equals("rule")) {
         if (scriptTiming != null && !scriptTiming.equals("none")) {

         } else {
            isActive = false;
         }
      }
   }

   @Override
   public Element getXML(Document job, Element command) {
      // Add this trigger to the global resources hash
      XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

      // Setup the command to re-create the trigger via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.IM));
      command.appendChild(app);

      if (script == null) {
         script = "";
      }

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(script.equalsIgnoreCase("mks:im:computationHistory")
              ? job.createTextNode("edittrigger") : job.createTextNode("createtrigger"));
      command.appendChild(cmdName);

      // --assign=[field=value[;field2=value2...]]  The list of assignments
      if (assign.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "assign", assign));
      }
      // --frequency=[manual]
      //             [hourly  [start=[00:]mm]          [hours=00,01,...,23]]
      //             [daily   [start=hh:mm]            [days=mon,tue,...]]
      //             [monthly [start=hh:mm] [day=1-31] [months=jan,feb,...]]  The scheduled frequency
      if (frequency != null && frequency.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "frequency", frequency));
      }
      // --query=[user:]query  The query name to associate with the trigger
      if (query != null && query.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         String queryName = IntegrityQuery.XML_PREFIX + XMLWriter.getXMLParamName(query);
         XMLWriter.paramsHash.put(queryName, query);
         command.appendChild(XMLWriter.getOption(job, "query", XMLWriter.padXMLParamName(queryName)));
      }
      // --rule=See documentation.  The rule to associate with the trigger
      if (rule != null && rule.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         command.appendChild(XMLWriter.getOption(job, "rule", rule));
      }
      // --runAs=user  Set the user used to run the trigger
      if (runAs != null && runAs.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         String userID = USER_XML_PREFIX + XMLWriter.getXMLParamName(runAs);
         XMLWriter.paramsHash.put(userID, runAs);
         command.appendChild(XMLWriter.getOption(job, "runAs", XMLWriter.padXMLParamName(userID)));
      }
      // --script=value  The script filename
      if (script != null && script.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         command.appendChild(XMLWriter.getOption(job, "script", script));
      }
      // --scriptParams=[arg=value[;arg2=value2...]]  The list of script arguments
      if (scriptParams != null && scriptParams.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         command.appendChild(XMLWriter.getOption(job, "scriptParams", scriptParams));
      }
      // --scriptTiming=[pre|post|pre,post|none]  Whether the script should be run pre or post issue commit
      if (scriptTiming != null && scriptTiming.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         command.appendChild(XMLWriter.getOption(job, "scriptTiming", scriptTiming));
      }
      // --type=[scheduled|rule|timeentry|copytree|branch|label|testresult]  The trigger type
      if (type != null && type.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
         command.appendChild(XMLWriter.getOption(job, "type", type));
      }
      // --description=value  Short description
      if (description != null && description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }

      // --name=value  The name for this object
      if (script != null && script.equalsIgnoreCase("mks:im:computationHistory")) {
         Element selection = job.createElement("selection");
         selection.appendChild(job.createTextNode(xmlParamName));
         command.appendChild(selection);
      } else {
         command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
      }

      return command;
   }

   public String getFlags() {
      String flags = "";
      if (script != null && !script.isEmpty()) {
         flags += (flags.isEmpty() ? "" : ",") + "s";
      }
      if (!scriptParams.isEmpty()) {
         flags += (flags.isEmpty() ? "" : ",") + "p";
      }
      if (query != null && !query.isEmpty()) {
         flags += (flags.isEmpty() ? "" : ",") + "q";
      }
      if (!assign.isEmpty()) {
         flags += (flags.isEmpty() ? "" : ",") + "a";
      }
      if (scriptTiming != null && !scriptTiming.equals("none")) {
         flags += (flags.isEmpty() ? "" : ",") + scriptTiming;
      }
      if (typeNameList.size() > 1) {
         flags += (flags.isEmpty() ? "" : " ") + "(" + typeNameList.size() + ")";
      }

      return "[" + flags + "]";
   }

   public String compName() {
      String compStr = "5";
      if (type.equals("rule") || type.equals("branch")) {
         if (scriptTiming.equals("pre")) {
            compStr = "1";
         }
         if (scriptTiming.equals("post")) {
            compStr = "2";
         }
      }
      compStr += format("%08d%n", Integer.getInteger(position));
      return compStr;
   }

   public final void setName(String name) {
      this.name = name;
      this.xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
   }
}
