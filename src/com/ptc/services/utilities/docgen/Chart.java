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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Constants.CHART_XML_PREFIX;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.util.List;

/**
 * The Chart class contains the following information about an Integrity Chart:
 * chartType createdBy description graphStyle id isAdmin name query shareWith
 *
 * Note: We're only interested in admin charts either for reporting purposes or
 * xml export!
 */
public class Chart extends IntegrityObject {

   // Query's members
   private String createdBy;
   private String graphStyle;
   private boolean isAdmin;
   private String query;
   private String shareWith;

   public Chart(WorkItem wi) {
      super(wi, Types.Chart);
      // modelType = IMModelTypeName..CHART;
      type = "";
      createdBy = "";
      graphStyle = "";
      isAdmin = true;
      setName(wi.getId());
      query = "";
      shareWith = "";
      xmlParamName = XMLWriter.padXMLParamName(CHART_XML_PREFIX + XMLWriter.getXMLParamName(name));
   }

   public Chart(WorkItem wi, List<Field> fieldList) {
      super(wi, Types.Chart, fieldList);
   }

   // All setter functions
   public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
   }

   public void setChartType(String chartType) {
      this.type = chartType;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setGraphStyle(String graphStyle) {
      this.graphStyle = graphStyle;
   }

   public void setSharedAdmin(boolean isAdmin) {
      this.isAdmin = isAdmin;
   }

   public final void setName(String name) {
      this.name = name;
      this.xmlParamName = XMLWriter.padXMLParamName(CHART_XML_PREFIX + XMLWriter.getXMLParamName(name));
   }

   public void setQuery(String query) {
      this.query = query;
   }

   public void setShareWith(String shareWith) {
      this.shareWith = shareWith;
   }

   // All getter/access functions...
   @Override
   public Element getXML(Document job, Element command) {
      // Add this chart to the global resources hash
      XMLWriter.paramsHash.put(CHART_XML_PREFIX + XMLWriter.getXMLParamName(name), name);

      // Setup the command to re-create the query via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.IM));
      command.appendChild(app);

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(job.createTextNode("createchart"));
      command.appendChild(cmdName);

      // --chartType=[Distribution|Trend|Issue Fields|Issue Fields Trend]  The chart type.
      if (type.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "chartType", type));
      }

      // TODO --bgColor=value  The background color of the graph.
      // TODO --chartFootnote=value  The footnote for this chart.
      // TODO --chartTitle=value  The title for this chart.
      // TODO --computations=value  The computations and all related attributes formatted as expression:name:pattern:axis name:minRangeValue:maxRangeValue:tickUnitValue.
      // TODO --dataColors=value  The custom data colors to be used (e.g. 'R,G,B;R,G,B;R,G,B' where R,G and B are within the range 0-255).
      // TODO --[no]deltasOnly  If true display only the difference between the actual and previous value, only for Issue Fields Trend charts.
      // TODO --descriptionFont=value  The font to be used for the description. Use a 'name,style,size' format, where style is 0 for plain, 1 for bold and 2 for italic.
      // TODO --[no]displayColumnTotals  If true display column totals in table charts.
      // TODO --[no]displayDescription  If true display the chart description.
      // TODO --[no]displayLabels  If true display labels with values in chart graphs.
      // TODO --[no]displayLegend  If true display a legend.
      // TODO --[no]displayRowTotals  If true display row totals in table charts.
      // TODO --[no]displayShapesForLineGraphs  If true display shapes for line graphs.
      // TODO --endDate=value  End date for trend charts (use 'MM/dd/yyyy h:mm:ss [AM|PM]' format). See documentation for additional formats.
      // TODO --fieldFilter=field=[value,value,...]  A supplementary project filter at run time.
      // TODO --fieldValues=value  The field, field values and aliases to graph by (e.g --fieldValues=FieldName=value1, alias1[value2,value3], value4, alias2[value5,value6], etc).
      // TODO --footnoteFont=value  The font to be used for the footnote. Use a 'name,style,size' format, where style is 0 for plain, 1 for bold and 2 for italic.
      // --graphStyle=[VerticalBar|VerticalStackedBar|HorizontalBar|HorizontalStackedBar|Pie|Line|Table|XY|Bubble]  The graph style.
      if (graphStyle != null && graphStyle.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "graphStyle", graphStyle));
      }

      // TODO --groupingValues=value  The field, field values and aliases to group by, only for distribution charts (e.g --groupingValues=FieldName=value1, alias1[value2,value3], value4, alias2[value5,value6], etc).
      // TODO --[no]is3D  If true render bar and pie charts as 3D.
      // TODO --[no]isAutoColors  If false you must provide data colors.
      // TODO --[no]isShowZeroFieldCount  If false do not show field values where count is zero (only for distribution charts).
      // TODO --[no]isShowZeroGroupingCount  If false do not show grouping values where count is zero (only for distribution charts).
      // TODO --issueIdentifier=value  The display string to use to identify issues for issue fields and issue fields trend charts.
      // TODO --legendBgColor=value  Legend background color.
      // TODO --legendPosition=[Right|Bottom|Left|Top]  The legend position in relation to the graph.
      // TODO --legendTitle=value  The title for the legend.
      // TODO --numberOfSteps=value  The number of steps in order to define the time span.
      // TODO --outlineColor=value  The color to outline graphical value elements.
      // TODO --projectedTrendExpressions=value  The projected trend attributes formatted as chartedExpression:startValueExpression:endValueExpression:projectedTrendLabel:showUpdatedTrend:updatedTrendLabel.
      // --query=[user:]query  The name of the query that defines the selection criteria.
      if (query != null && query.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "query", query));
      }

      // TODO --rangeDefinitions=value  Range definitions for a computation in table charts.
      // TODO --runDateIsEndDate  Use run chart date as the end date.
      // TODO --startDate=value  Start date for trend charts (use 'MM/dd/yyyy h:mm:ss [AM|PM]' format). See documentation for additional formats.
      // TODO --startDateField=field  The issue field that contains the start date, only for Issue Fields Trend charts.
      // TODO --[no]swapRowsAndColumns  If true swap rows and columns in table charts.
      // TODO --titleFont=value  The font to be used for the title. Use a 'name,style,size' format, where style is 0 for plain, 1 for bold and 2 for italic.
      // TODO --trendStep=[Hour|Day|Week|Month|Quarter|Year]  The granularity for trend chart.
      // TODO --[no]useIssueDefinedOrigin  If true use the start date defined in an issue field, only for Issue Fields Trend charts.
      // TODO --xLabelRotation=[Horizontal|VerticalDown|VerticalUp|45Down|45Up]  Set x axis label orientation.
      // TODO --[no]xReverse  Reverse x axis.
      // TODO --[no]xShowGrid  Show gridlines parallel to x axis.
      // TODO --[no]xShowTitle  Show x axis title.
      // TODO --yLabelRotation=[Horizontal|VerticalUp]  Set y axis label orientation.
      // TODO --[no]yReverse  Reverse y axis.
      // TODO --[no]yShowGrid  Show gridlines parallel to y axis.
      // TODO --[no]yShowTitle  Show y axis title.
      // --sharedAdmin  Set this object to be a shared admin object
      if (isAdmin) {
         command.appendChild(XMLWriter.getOption(job, "sharedAdmin", null));
      }

      // --shareWith=u=user1[:modify],user2[:modify],.. ;g=group1[:modify],group2[:modify],..   
      // Set the users and groups that can see and optionally modify this object.
      if (shareWith != null && shareWith.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "shareWith", shareWith));
      }

      // --description=value  Short description
      if (description != null && description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }

      // --name=value  The name for this object
      if (name.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
      }

      return command;
   }

//   @Override
//   public String getPosition() {
//      return this.getID().replaceAll(" ", "_");
//   }
   public String getCreatedBy() {
      return createdBy;
   }

   public String getChartType() {
      return type;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public String getGraphStyle() {
      return graphStyle;
   }

   public boolean isAdmin() {
      return isAdmin;
   }

   @Override
   public String getName() {
      return name;
   }

   public String getQuery() {
      return query;
   }

   public String getShareWith() {
      return shareWith;
   }

   @Override
   public String getXMLName() {
      return xmlParamName;
   }

//   @Override
//   public String getDetails() {
//      StringObj sb = new StringObj();
//      // Print out the detail about each item type
//      sb.append("<table class='display'>");
//      sb.addFieldValue("ChartType", this.getChartType());
//      sb.addFieldValue("Description", HyperLinkFactory.convertHyperLinks(this.getDescription()));
//      sb.addFieldValue("GraphStyle", this.getGraphStyle());
//      sb.addFieldValue("ShareWith", this.getShareWith());
//      sb.addFieldValue("Query", this.getQuery());
//      // Close out the triggers details table
//      sb.append("</table>");
//
//      return sb.toString();
//   }
   @Override
   public String getFieldValue(String fieldName) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
}
