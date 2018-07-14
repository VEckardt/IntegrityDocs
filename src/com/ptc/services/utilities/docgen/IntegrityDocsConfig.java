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

import static com.mks.api.Command.AA;
import static com.mks.api.Command.IM;
import static com.mks.api.Command.INTEGRITY;
import static com.mks.api.Command.SI;
import static com.mks.api.Command.TM;

/**
 * This type defines how and if IntegrityDocs generates the output (e.g. Display
 * Names, SubTypes, Description)
 *
 * @author veckardt
 */
public class IntegrityDocsConfig {

   // set to false only for debugging of individual exports
   static Boolean doGlobal = true;

   public enum Overviews {

      ID2(Types.Type, DocWriterTools.class, "getTypeImageOverview", 2, "with Images"),
      ID3(Types.Type, Integrity.class, "getStateFieldPermission", 3, "with Field Permission"),
      ID4(Types.Trigger, Integrity.class, "getTriggersAndTypes", 2, "with Type Usage"),
      ID5(Types.MKSDomainGroup, DocWriterTools.class, "getGroupHierarchy", 2, "with Group Hierarchy"),
      ID6(Types.State, DocWriterTools.class, "getTypeStateUsage", 2, "with Type Usage"),
      ID7(Types.DynamicGroup, DocWriterTools.class, "getDynGroupAssignment", 2, "with Type Usage");

      private final Types type;
      private final Integer id;
      private final String text;
      private final Class<?> clazz;
      private final String function;

      Overviews(Types type, Class<?> clazz, String function, Integer id, String text) {
         this.type = type;
         this.id = id;
         this.text = text;
         this.clazz = clazz;
         this.function = function;
      }

      Types getType() {
         return type;
      }

      Integer getId() {
         return id;
      }

      String getText() {
         return text;
      }

      Class<?> getClassObject() {
         return clazz;
      }

      String getFunction() {
         return function;
      }
   }

   public enum Types {

      // Name(UID/CmdApp/Group/RetrieveIt/DisplayName/AddColumnsForSummary/HasSubStructure/showDetails/AllFieldsInOverview/newListType/Summary)
      Viewset(0, INTEGRITY, 1, false, "Viewset", "", 0, 1, 0, 1, "This report lists all Viewsets configured in the Integrity Admin client."),
      MKSDomainGroup(1, INTEGRITY, 1, false, "Domain Group", "", 1, 1, 0, 1, "This report lists all MKS Domain Groups configured in the Integrity Admin client."),
      SIProject(2, SI, 1, false, "Main CM Project", "", 0, 1, 0, 1, "This report lists all Config Management Main Projects configured in the Integrity Admin client."),
      IMProject(3, IM, 1, false, "Main W&amp;D Project", "", 0, 1, 0, 1, "This report lists all W&amp;D Main Projects configured in the Integrity Admin client."),
      Type(4, IM, 1, true, null, "", 1, 1, 0, 1, "This report lists all Types configured in the Integrity Admin client."),
      State(5, IM, 1, false, null, "Image", 0, 1, 0, 1, "This report lists all States configured in the Integrity Admin client."),
      Field(6, IM, 2, false, null, "displayName,Type", 1, 1, 0, 1, "This report lists all Fields  configured in the Integrity Admin client."),
      Trigger(7, IM, 2, false, null, "Type", 1, 1, 0, 1, "This report lists all Triggers configured in the Integrity Admin client."),
      Group(8, IM, 2, false, null, "isActive", 1, 1, 0, 1, "This report lists all W&amp;D Groups configured in the Integrity Admin client."),
      DynamicGroup(9, IM, 2, false, "Dynamic Group", "", 0, 1, 0, 1, "This report lists all Dynamic groups configured in the Integrity Admin client."),
      CPType(10, IM, 2, false, "Change Package Type", "", 0, 1, 0, 1, "This report lists all Change Package Types configured in the Integrity Admin client."),
      Verdict(11, TM, 2, true, "Test Verdict", "Type,isActive", 0, 1, 0, 1, "This report lists all Test Verdicts configured in the Integrity Admin client."),
      ResultField(12, TM, 2, true, "Test Result Field", "Type", 0, 1, 0, 1, "This report lists all Test Result Fields configured in the Integrity Admin client."),
      Chart(13, IM, 2, false, null, "Image,Type", 1, 1, 0, 1, "This report lists all Charts configured in the Integrity Admin client."),
      Dashboard(14, IM, 2, false, null, "Image", 0, 1, 0, 0, "This report lists all Dashboards configured in the Integrity Admin client."),
      Query(15, IM, 2, false, null, "Image", 0, 1, 0, 1, "This report lists all Queries configured in the Integrity Admin client."),
      Report(16, IM, 2, false, null, "Image", 0, 1, 0, 1, "This report lists all Reports configured in the Integrity Admin client."),
      GatewayMapping(17, IM, 2, false, "Gateway Mapping", "", 0, 1, 0, 0, "This report lists all gateway mappings configured in mapping XML files."),
      GatewayImportConfig(18, IM, 2, false, "Gateway Import Config", "Type", 0, 1, 0, 0, "This report lists all Gateway Import Configurations configured in gateway-tool-configuration.xml"),
      GatewayExportConfig(19, IM, 2, false, "Gateway Export Config", "Type", 0, 1, 0, 0, "This report lists all Gateway Export Configurations configured in gateway-tool-configuration.xml"),
      GatewayTemplate(20, IM, 2, false, "Gateway Template", "", 0, 0, 1, 0, "This report lists all import XSLTs and export Templates in Word or Excel, referred to in gateway-tool-configuration.xml"),
      TraceDefault(21, IM, 2, false, "Trace Default", "", 0, 0, 1, 0, "This report lists all Trace Defaults configured in the Solution type."),
      TypePermission(22, IM, 2, false, null, "", 1, 0, 1, 0, "This report lists all Type Permissions configured in the Integrity Admin client."),
      Metric(23, IM, 2, false, "Metric", "", 0, 0, 1, 0, "This report lists all Metrics defined for the Integrity Server."),
      Image(24, IM, 2, false, "Image", "", 0, 0, 1, 0, "This report lists all Images referred to in the Presentation Templates."),
      ACL(25, AA, 2, false, "Access Control List", "", 0, 0, 1, 0, "This report lists the Access Control elements defined in the Integrity Admin client.");

      private final int id;
      private final int grp;
      private final boolean export;
      private final String modelType;
      private final String addColumns;
      private final int subStructure;
      private final int showDetails;
      private final String cmd;
      private final int allFields;
      private final String description;
      private final int exportXML;

      Types(int p, String cmd, int grp, boolean export, String modelType, String addColumns, int subStructure, int showDetails, int allFields, int exportXML, String description) {
         this.id = p;
         this.grp = grp;
         this.export = export;
         this.modelType = modelType;
         this.addColumns = addColumns;
         this.subStructure = subStructure;
         this.showDetails = showDetails;
         this.cmd = cmd;
         this.allFields = allFields;
         this.description = description;
         this.exportXML = exportXML;
      }

      int getID() {
         return id;
      }

      int getGrp() {
         return grp;
      }

      Boolean getExportFlag() {
         return export;
      }

      String getModelType() {
         return (modelType == null ? name() : modelType).replaceAll(" ", "");
      }

      String getDirectory() {
         return name().replace("ery", "erie") + "s";
      }

      String getDisplayName() {
         return (modelType == null ? name() : modelType);
      }

      String getAddColumns() {
         return addColumns;
      }

      Boolean showSubStructure() {
         return (subStructure == 1);
      }

      Boolean showDetails() {
         return (showDetails == 1);
      }

      Boolean showAllFields() {
         return (allFields == 1);
      }

      String getPlural() {
         return name().replace("ery", "erie") + "s";
      }

      String getCmd() {
         return cmd;
      }

      String getDescription() {
         return description;
      }

      Boolean doExportXML() {
         return exportXML == 1;
      }
   }
}
