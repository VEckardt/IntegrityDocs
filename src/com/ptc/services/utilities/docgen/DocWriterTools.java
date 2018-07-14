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
import static com.ptc.services.utilities.docgen.Constants.SUMMARY_FILE;
import static com.ptc.services.utilities.docgen.Constants.getNow;
import static com.ptc.services.utilities.docgen.Constants.summaryTemplate;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import static com.ptc.services.utilities.docgen.IntegrityDocs.solutionTypeName;
import com.ptc.services.utilities.docgen.utils.ExceptionHandler;
import com.ptc.services.utilities.docgen.utils.Html;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author veckardt
 */
public class DocWriterTools {

//   DocWriterTools() {
//
//   }
   public void writeSummaryPage() throws FileNotFoundException, IOException {
      try (BufferedReader triggerReader = new BufferedReader(new FileReader(summaryTemplate))) {
         BufferedWriter triggerWriter = new BufferedWriter(new FileWriter(SUMMARY_FILE));
         String line;
         while (null != (line = triggerReader.readLine())) {
            triggerWriter.write((getFormattedContent(line, null, "", 1)));
         }
         triggerWriter.flush();
         triggerWriter.close();
      }
   }

   // Resolves the parameterized report values
   public static String getFormattedContent(String str, IntegrityAdminObject adminObj, String title, int id) {
      StringBuilder sb = new StringBuilder();
      int startIndex = 0;
      int currentIndex;

      while ((currentIndex = str.indexOf("<%", startIndex)) >= 0) {
         if (currentIndex > 0) {
            sb.append(str.substring(startIndex, currentIndex));
         }

         if (currentIndex == (str.length() - 2)) {
            sb.append("<%");
            startIndex = currentIndex + 2;
         } else {
            int endIndex = str.indexOf("%>", currentIndex);
            if (endIndex < 0) {
               // no matching closing token, don't expand
               break;
            }
            String paramName = str.substring(currentIndex + 2, endIndex);

            // Expand the field name or symbolic
            if ("hostport".equals(paramName)) {
               sb.append(IntegrityDocs.integrity.getHostName()).append(":").append(IntegrityDocs.integrity.getPort());
            } else if ("now".equals(paramName)) {
               sb.append(getNow());
            } else if ("objecttype".equals(paramName)) {
               if (null != adminObj) {
                  sb.append((adminObj.getObjectTypeDisplayName() + " " + title).trim());
               }
            } else if ("description".equals(paramName)) {
               if (null != adminObj) {
                  sb.append(adminObj.getObjectTypeDescription());
               }
            } else if (paramName.endsWith("overview")) {
               if (null != adminObj) {
                  // log("INFO: OVERVIEW A " + adminObj.objectType + " - " + id);
                  String data = callOverviewMethod(adminObj, id);
                  if (data.isEmpty()) {
                     // log("INFO: OVERVIEW B " + adminObj.objectType + " - " + id);
                     data = adminObj.getOverview();
                  }
                  sb.append(data);
               }
            } else if ("details".equals(paramName)) {
               if (null != adminObj) {
                  sb.append(adminObj.getDetails());
               }
            } else if ("objectname".equals(paramName)) {
               if (null != adminObj) {
                  sb.append(adminObj.getName());
               }
            } else if ("summary".equals(paramName)) {
               sb.append(getObjectSummary());
            } else if ("about".equals(paramName)) {
               sb.append(IntegrityDocs.integrity.getAbout("IntegrityDocs" + Copyright.version));
            } else {
               // Unknown parameter
               sb.append(paramName);
            }
            startIndex = endIndex + 2;
         }
      }

      if (startIndex < str.length()) {
         sb.append(str.substring(startIndex));
      }

      return sb.toString();
   }

   public static String callOverviewMethod(IntegrityAdminObject adminObj, int i) {
      String strg = "";
      try {
         // for (Overviews overview : Overviews.values()) {
         // if (overview.getType().equals(adminObj.getObjectType()) && overview.getId().equals(i)) {
//               Class<?> clazz = overview.getClassObject();
//               Object item = clazz.newInstance();
//               Method method = clazz.getDeclaredMethod(overview.getFunction());
//               strg = (String) method.invoke(item);

         if (adminObj.getObjectType().equals(Types.Type) && i == 2) {
            strg = DocWriterTools.getTypeImageOverview();
         }
         if (adminObj.getObjectType().equals(Types.Type) && i == 3) {
            strg = Integrity.getStateFieldPermission();
         }
         if (adminObj.getObjectType().equals(Types.Trigger) && i == 2) {
            strg = Integrity.getTriggersAndTypes();
         }
         if (adminObj.getObjectType().equals(Types.MKSDomainGroup) && i == 2) {
            strg = DocWriterTools.getGroupHierarchy();
         }
         if (adminObj.getObjectType().equals(Types.State) && i == 2) {
            strg = DocWriterTools.getTypeStateUsage();
         }
         if (adminObj.getObjectType().equals(Types.DynamicGroup) && i == 2) {
            strg = DocWriterTools.getDynGroupAssignment();
         }
         // }
         // }
         // } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
      } catch (APIException ex) {
         ExceptionHandler eh = new ExceptionHandler(ex);
         Logger.getLogger(DocWriterTools.class.getName()).log(Level.SEVERE, eh.getMessage(), eh);
         System.exit(1);
      }
      return strg;
   }

   /**
    * Returns the Group Hierarchy Picture
    *
    * @return
    */
   public static String getGroupHierarchy() {
      StringBuilder sb = new StringBuilder();
      sb.append("");

      sb.append("<div id='sample'>\n");
      // <!-- The DIV for the Diagram needs an explicit size or else we won't see anything.\n");
      // This also adds a border to help see the edges of the viewport. -->\n");
      sb.append("    <div id='GroupHierarchy' style='border: solid 1px black; width:100%; height:400px'></div>\n");
      sb.append("</div>\n");
      sb.append("<script src='../js/go.js' type=\"text/javascript\"></script>\n");

      sb.append("<script id='code' type=\"text/javascript\">\n");
      // function init() {
      //    if (window.goSamples) goSamples();  // init for these samples -- you don't need to call this

      sb.append("  var $ = go.GraphObject.make; // for conciseness in defining templates\n");
      sb.append("  var myDiagram = $(go.Diagram, 'GroupHierarchy', // id of DIV\n");
      sb.append("    { // Automatically lay out the diagram as a tree;\n");
      // sb.append("    initialContentAlignment: go.Spot.Center, // center the content\n");
      sb.append("    'undoManager.isEnabled': true, // enable undo and redo\n");
      sb.append("    // separate trees are arranged vertically above each other.\n");
      sb.append("    layout: $(go.TreeLayout, {\n");
      sb.append("      nodeSpacing: 3\n");
      sb.append("    })\n");
      sb.append("    });\n");
      sb.append("  myDiagram.nodeTemplate =\n");
      sb.append("    $(go.Node,\n");
      sb.append("    'Auto',\n");
      // compute the URL to open for the documentation
      // function(node) {
      //  return "../api/symbols/" + node.data.key + ".html";
      // },
      // define the visuals for the hyperlink, basically the whole node:
      sb.append("    $(go.Shape, 'RoundedRectangle', {\n");
      sb.append("      fill: '#ABFF8F',\n");
      sb.append("      stroke: null\n");
      sb.append("    }, new go.Binding('fill', 'color')),\n");
      sb.append("    $(go.TextBlock, {\n");
      sb.append("       font: '13px Helvetica, Arial, sans-serif',\n");
      sb.append("      stroke: 'black',\n");
      sb.append("      margin: 6\n");
      sb.append("      },\n");
      sb.append("      new go.Binding('text', 'key'))\n");
      sb.append("    );\n");

      // Define a trivial link template with no arrowhead
      sb.append("  myDiagram.linkTemplate =\n");
      sb.append("    $(go.Link, {\n");
      sb.append("      curve: go.Link.Bezier,\n");
      sb.append("      toEndSegmentLength: 30,\n");
      sb.append("      fromEndSegmentLength: 30\n");
      sb.append("    },\n");
      sb.append("    $(go.Shape, {\n");
      sb.append("      strokeWidth: 1.5\n");
      sb.append("    }) // the link shape, with the default black stroke\n");
      sb.append("    );\n");

      // but use the default Link template, by not setting Diagram.linkTemplate
      sb.append("   var nodeDataArray = [];\n");
//    sb.append("        nodeDataArray.push({
//    sb.append("            key: 'Data',
//    sb.append("            color: 'orange'
//    sb.append("        });
//    sb.append("        nodeDataArray.push({
//    sb.append("            key: 'Sub',
//    sb.append("            parent: 'Data'
//    sb.append("        });

      List<IntegrityObject> groupList = getList(Types.MKSDomainGroup);
      List<String> mark = new ArrayList<>();
      for (IntegrityObject ao : groupList) {
         DomainGroup sg = (DomainGroup) ao;
         List<String> subGroups = sg.getSubGroups();
         for (String subGroup : subGroups) {
            sb.append("   nodeDataArray.push({key: \"" + subGroup + " \", parent: \"" + sg.getName() + " \"});" + "\n");
            mark.add(subGroup);  // track this group to avoid showing it later on twice
         }
      }

      sb.append("   nodeDataArray.push({key: \"Domain Groups\", color: \"orange\"});" + "\n");
      for (IntegrityObject ao : groupList) {
         DomainGroup sg = (DomainGroup) ao;
         List<String> subGroups = sg.getSubGroups();
         for (String subGroup : subGroups) {
            if (!mark.contains(sg.getName())) {
               sb.append("   nodeDataArray.push({key: \"" + sg.getName() + " \", parent: 'Domain Groups'});" + "\n");
               // sb.append("nodeDataArray.push({key: \"" + sg.getName() + " \"});" + "\n");
            }
            break;
         }
      }

      sb.append("   myDiagram.model = new go.TreeModel(nodeDataArray);\n");
      sb.append("</script>\n");
      return (sb.toString());
   }

   /**
    * Returns the Dynamic Group Assignment
    *
    * @return
    * @throws APIException
    */
   public static String getDynGroupAssignment() throws APIException {
      int height = "System Requirement Document".length() * 7;
      // Html.initReport("Dynamic Group and Object Refs");
      // out.println(Html.getTitle(session.getId()));
      // String[] elementClasses = new String[]{"Type", "Field"};
      StringObj sb = new StringObj();

      // String group = "All";
      String showAllTypes = "No";
      // String showReferences = "No";

      // intSession.readAllDynamicGroups();
      // String[] showAllTypesDefiniton = new String[]{"Show all Types", "showAllTypes", showAllTypes, "Yes", "No"};
      //  out.println(Html.itemSelectField("group", intSession.allDynamicGroups, true, group, showAllTypesDefiniton));
//        IntegrityDocs.i.readDynamicGroups(group, showReferences);
      // read names and description only
      // IntegrityDocs.i.readAllObjects(Types.Type, Integrity.allTypes);
      // Place to store used Types only
      List<IntegrityObject> allUsedTypes = new ArrayList<>();

      // Fill map for used types
      Iterator iDynamicGroup = getList(Types.DynamicGroup).iterator();
      while (iDynamicGroup.hasNext()) {
         // for (String groupName : Integrity.allDynamicGroups.keySet()) {
         // DynamicGroup dynGroup = intSession.allDynamicGroups.get(groupName);
         DynamicGroup dynGroup = (DynamicGroup) iDynamicGroup.next();
         // String references = dynGroup.getReferences("Type", elementClasses);
         Iterator iType2 = getList(Types.Type).iterator();
         while (iType2.hasNext()) {
            IntegrityType type = (IntegrityType) iType2.next();
            String typeName = type.getName();
            // for (String typeName : allUsedTypes.keySet()) {
            if (!typeName.startsWith("Shared") && !typeName.equals(solutionTypeName)) {
               // if (references.indexOf(typeName) > 0) {
               if (!type.getUsedString(dynGroup.getName()).isEmpty()) {
                  if (!allUsedTypes.contains(type)) {
                     allUsedTypes.add(type);
                  }
               }
            }
         }
      }

      // Show all Types, turn back to all then?
      if (showAllTypes.endsWith("Yes")) {
         allUsedTypes = getList(Types.Type);
      }

      sb.append("<table class='sortable' id=\"Dynamic Group with Type Usage Overview\">");
      sb.append("<tr>" + Html.th(""));

      // print table header
      Iterator iType = allUsedTypes.iterator();
      while (iType.hasNext()) {
         // for (String typeName : allUsedTypes.keySet()) {
         IntegrityType type = (IntegrityType) iType.next();
         String typeName = type.getName();
         if (!typeName.startsWith("Shared") && !typeName.equals(solutionTypeName)) {
            typeName = typeName.replaceAll(" ", "&nbsp;");
            sb.append(("<th class=\"heading1 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + typeName + "</span></th>"));
         }
      }
      sb.append("</tr>");

      // print table data
      Iterator iDynamicGroup2 = getList(Types.DynamicGroup).iterator();
      while (iDynamicGroup2.hasNext()) {
         // for (String groupName : Integrity.allDynamicGroups.keySet()) {
         // DynamicGroup dynGroup = intSession.allDynamicGroups.get(groupName);
         DynamicGroup dynGroup = (DynamicGroup) iDynamicGroup2.next();

         sb.append("<tr>");
         sb.append(Html.td(dynGroup.getName()));

         // String references = dynGroup.getReferences("Type", elementClasses);
         Iterator iType2 = allUsedTypes.iterator();
         while (iType2.hasNext()) {
            IntegrityType type = (IntegrityType) iType2.next();
            String typeName = type.getName();
            // for (String typeName : allUsedTypes.keySet()) {
            if (!typeName.startsWith("Shared") && !typeName.equals(solutionTypeName)) {
               // if (references.indexOf(typeName) > 0) {
               // Type type = IntegrityDocs.i.getType(typeName);
               sb.append(Html.td(type.getUsedString(dynGroup.getName())));
               // } else {
               // sb.append(Html.td(""));
               // }
            }
         }
         sb.append("</tr>");
      }
      sb.append("</table>");

      // Legend only
      sb.append("<br><table class='sortable'>");
      sb.append("<tr>" + Html.th("Legend") + "</tr>");

      sb.append("<tr>" + Html.td("A") + Html.td("Used in Type Administrators") + "</tr>");
      sb.append("<tr>" + Html.td("E") + Html.td("Used in Type Editability Rule") + "</tr>");
      sb.append("<tr>" + Html.td("P") + Html.td("Used in Type Permitted Groups") + "</tr>");
      sb.append("<tr>" + Html.td("W") + Html.td("Used in Type Workflow Transitions") + "</tr>");
      sb.append("<tr>" + Html.td("C") + Html.td("Used in Type Constraints") + "</tr>");
      sb.append("<tr>" + Html.td("FE") + Html.td("Used in Field Editability") + "</tr>");
      sb.append("<tr>" + Html.td("FR") + Html.td("Used in Field Relevance") + "</tr>");
      sb.append("</table>");

      return sb.toString();
   }

   /**
    * Returns the Type State Usage Matrix
    *
    * @return
    */
   public static String getTypeStateUsage() {
      int height = "System Requirement Document".length() * 7;
      List<IntegrityType> typeList = new ArrayList<>();

      StringObj sb = new StringObj();
      sb.append("<br><table class='sortable' id=\"State with Type Usage Overview\">");
      sb.append("<tr>" + Html.th(""));

      // print table header
      Iterator it = IntegrityDocs.getList(Types.Type).iterator();
      while (it.hasNext()) {
         IntegrityType type = (IntegrityType) it.next();
         String typeName = type.getName();
         if (!typeName.contains("Shared") && !typeName.equals(solutionTypeName)) {
            typeName = typeName.replaceAll(" ", "&nbsp;");
            sb.append(("<th class=\"heading1 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + typeName + "</span></th>"));
            typeList.add(type);
         }
      }
      sb.append(("<th class=\"heading8 verticalText\" style=\"height:" + height + "px\"><span class=\"verticalText\">" + "Count:" + "</span></th>"));
      sb.append("</tr>");
      Iterator itState = IntegrityDocs.getList(Types.State).iterator();
      while (itState.hasNext()) {
         IntegrityObject state = (IntegrityObject) itState.next();
         String row = "<tr>" + Html.td(state.getName());
         int cnt = 0;
         for (IntegrityType type : typeList) {
            // String references = dynGroup.getReferences("Type", elementClasses);
            if (type.usesState(state.getName())) {
               row += Html.td("<div style='text-align: center'>&#10003;</div>");
               cnt++;
            } else {
               row += Html.td("");
            }
         }
         row += Html.td("<div style='text-align: right'><b>" + cnt + "</b></div>");
         row += "</tr>";
         sb.append(cnt > 0 ? row : "");
      }
      sb.append("<tr>");
      sb.append("<td class='heading8' ><b>Count:</b></td>");
      for (IntegrityType type : typeList) {
         sb.append(Html.td("<b><div style='text-align: center'>" + String.valueOf(type.getStateCount()) + "</div></b>"));
      }
      sb.append("</tr>");
      sb.append("</table>");
      return sb.toString();
   }

   private static String getObjectSummary() {
      StringObj sb = new StringObj();
      // Print out the detail about each item type
      sb.append("<table class='sortable' id=\"Summary_Overview\">");
      sb.addHeadings("Object,Count,Description");
      sb.append("<tbody>");

      for (Types type : Types.values()) {
         sb.append(" <tr>");
         sb.addTDborder(type.getDisplayName());
         sb.addTDborder(String.valueOf(getList(type).size()));
         sb.addTDborder(type.getDescription().replace("This report lists all ", ""));
         sb.append(" </tr>");
      }
      // Close out the table
      sb.append("</tbody></table>");

      return sb.toString();
   }

   private static String getTypeImageOverview() {
      StringObj sb = new StringObj();

      // Summary heading line
      sb.append("<table class='sortable'>");
      sb.addHeadings("ID,Name,Image,Main Image,Description");
      sb.append("<tbody>");

      // Print out the summary about each item type
      for (IntegrityAdminObject iType : IntegrityDocs.getList(Types.Type)) {
         sb.append(("<tr>"));
         sb.addTDborder(iType.getPosition());
         sb.addTDborder("<a href='Types/" + iType.getPosition() + ".htm'>" + iType.getName() + "</a>");
         sb.addTDborder("<img src=\"" + iType.getFieldValue("smallImagePath") + "\" alt=\"-\"/>");
         sb.addTDborder("<img src=\"" + iType.getFieldValue("mainImagePath") + "\" alt=\"-\"/>");
         sb.addTDborder(HyperLinkFactory.convertHyperLinks(iType.getDescription()));
         sb.append(("</tr>"));
      }
      sb.append("</tbody></table>");

      return sb.toString();
   }

//    public static String getTriggersOverview() {
//        StringObj sb = new StringObj();
//        // Summary heading line
//        sb.append(("<table class='sortable'>"));
//        sb.addHeadings("Position,Name,Type,Description,Script,Script Timing");
//        sb.append(("<tbody>"));
//        // Print out the summary about each trigger
//        for (IntegrityAdminObject object : IntegrityDocs.getList(Types.Trigger)) {
//            sb.append((" <tr>"));
//            sb.addTDborder(object.getPosition());
//            sb.addTDborder("<a href='Triggers/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
//            sb.addTDborder(object.getType());
//            sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
//            sb.addTDborder(object.getFieldValue("script"));
//            sb.addTDborder(object.getFieldValue("scriptTiming"));
//            sb.append((" </tr>"));
//        }
//        sb.append(("</tbody></table>"));
//
//        return sb.toString();
//    }
}
