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

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityUtils.getConnectionString;
import com.ptc.services.utilities.docgen.utils.Html;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import com.ptc.services.utilities.docgen.utils.OSCommandHandler;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author veckardt
 */
public class ACL extends IntegrityObject {

   public ACL(WorkItem workitem) {
      super(workitem, Types.ACL);
   }

   /**
    * getOverview
    *
    * @return
    */
   @Override
   public String getOverview() {
      int height = "Modify Delete Item Rule".length() * 7;
      TreeSet<String> aclNameList = new TreeSet<>();

      StringObj sb = new StringObj();
      sb.append("<table class='sortable' id=\"Access Control List Overview\">");
      sb.append("<tr>" + Html.th("Type") + Html.th("Name"));

      OSCommandHandler osc = new OSCommandHandler();
      String osCommand = "aa availablepermissions  " + getConnectionString() + " mks:im";
      log("Exit Code: " + osc.executeCmd(osCommand, true));
        // setCommandUsed("CLI: " + osCommand);

      // log (osc.getUnfilteredResult());
      String[] fr = osc.getUnfilteredResult().split("\n");
      for (String fr1 : fr) {
         if (fr1.length() > 5 && !fr1.contains(":")) {
            aclNameList.add(fr1.trim());
         }
      }
      log("INFO: Found " + fr.length + " permissions.");

      // print table header
//        Iterator it = IntegrityDocs.getList(Types.Type).iterator();
//        while (it.hasNext()) {
//            IntegrityType type = (IntegrityType) it.next();
//            String typeName = type.getName();
//            if (!typeName.contains("Shared") && !typeName.equals(solutionTypeName)) {
//                typeName = typeName.replaceAll(" ", "&nbsp;");
//                sb.append(("<th class=\"heading1 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + typeName + "</span></th>"));
//                typeList.add(type);
//            }
//        }
      for (String aclName : aclNameList) {
         aclName = aclName.replaceAll(" ", "&nbsp;");
         sb.append(("<th class=\"heading1 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + aclName + "</span></th>"));
      }

      sb.append(("<th class=\"heading8 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + "Count:" + "</span></th>"));
      sb.append("</tr>");

      TreeMap<String, String> aclList = new TreeMap<>();
      Iterator itACLs = IntegrityDocs.getList(Types.ACL).iterator();
      while (itACLs.hasNext()) {
         ACL acl = (ACL) itACLs.next();
         String principal = acl.getFieldValue("pricipalType") + ":" + acl.getFieldValue("pricipalName") + "";
         if (aclList.containsKey(principal)) {
            aclList.put(principal, (aclList.get(principal) + "," + acl.getFieldValue("permission") + ":" + acl.getFieldValue("permitted")));
         } else {
            aclList.put(principal, "," + acl.getFieldValue("permission") + ":" + acl.getFieldValue("permitted"));
         }
      }

      String row = "";
      for (String key : aclList.keySet()) {
         int cnt = 0;
         row += "<tr>" + Html.td(key.replace(":", "</td><td>"));
         String value = aclList.get(key);
         for (String aclName : aclNameList) {
            if (value.contains("," + aclName + ":true")) {
               row += Html.td("<div style='text-align: center'><img src='images/Permission_Allowed.png' alt=\"-\" /></div>");
               cnt++;
            } else if (value.contains("," + aclName + ":false")) {
               row += Html.td("<div style='text-align: center'><img src='images/Permission_Denied.png' alt=\"-\" /></div>");
               cnt++;
            } else {
               row += Html.td("");
            }
         }
         row += Html.td("<div style='text-align: right'><b>" + cnt + "</b></div>");
         row += "</tr>";
      }
      sb.append(row);

//        sb.append("<tr>");
//        sb.append("<td class='heading8' ><b>Count:</b></td>");
//        for (IntegrityType type : typeList) {
//            sb.append(Html.td("<b><div style='text-align: center'>" + String.valueOf(type.getStateCount()) + "</div></b>"));
//        }
//        sb.append("</tr>");
      sb.append("</table>");
      return sb.toString();
   }

   @Override
   public String getFieldValue(String fieldName) {
      for (Field field : fields) {
         if (field.getName().equals(fieldName)) {
            return field.getValueAsString();
         }
      }
      throw new UnsupportedOperationException("Not supported yet.");
   }

}
