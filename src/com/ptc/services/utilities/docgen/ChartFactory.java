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

import com.ptc.services.utilities.docgen.field.SimpleField;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.Integrity.chartAttributes2;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.skipChartPreview;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

public class ChartFactory {

    public static Chart parseChart(Integrity i, WorkItem chart, String chartOutput) throws APIException {
        StringTokenizer chartTokens = new StringTokenizer(chartOutput, nl);
        List<Field> fieldlist = new ArrayList<>();
        while (chartTokens.hasMoreTokens()) {
            String line = chartTokens.nextToken();
            String fieldName;
            String fieldValue;
            if (line.length() > 10 && line.indexOf(":") > 2) {

                if (line.startsWith("Created by")) {
                    fieldlist.add(new SimpleField("Created by", line.replace("Created by", "")));
                } else if (line.startsWith("Modified by")) {
                    fieldlist.add(new SimpleField("Modified by", line.replace("Modified by", "")));
                } else {

                    fieldName = line.substring(0, line.indexOf(":")).replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    if (!Arrays.asList(chartAttributes2).contains(fieldName)) {
                        fieldValue = line.substring(line.indexOf(":") + 1);
                        fieldlist.add(new SimpleField(fieldName, fieldValue));
                    }
                }
            }
        }
        if (!skipChartPreview) {
            String previewFile = i.genChartPreviewFile(chart, chart.getField("graphStyle").getString());
            fieldlist.add(new SimpleField("preview", previewFile));
        }

        return new Chart(chart, fieldlist);
    }

    public static List<Chart> parseCharts(WorkItem wi, String chartsOutput) {
        List<Chart> chartList = new ArrayList<>();
        StringTokenizer chartTokens = new StringTokenizer(chartsOutput, nl);
        while (chartTokens.hasMoreTokens()) {
            String line = chartTokens.nextToken().trim();
            StringTokenizer attributes = new StringTokenizer(line, "|");
            if (attributes.countTokens() == 8) {
                // Only process admin charts
                boolean isAdmin = Boolean.parseBoolean(attributes.nextToken().trim());
                if (isAdmin) {
                    Chart c = new Chart(wi);
                    c.setSharedAdmin(isAdmin);
                    c.setID(attributes.nextToken().trim());
                    c.setName(attributes.nextToken().trim());
                    log("Processing Chart: " + c.getName());
                    c.setChartType(attributes.nextToken().trim());
                    c.setCreatedBy(attributes.nextToken().trim());
                    c.setGraphStyle(attributes.nextToken().trim());
                    c.setQuery(attributes.nextToken().trim());
                    c.setShareWith(attributes.nextToken().trim());
                    // c.setDescription(Integrity.getStringFieldValue(wi.getField("description")));
                    chartList.add(c);
                }
            } else {
                log("ERROR: Failed to parse charts output - " + line);
            }
        }
        return chartList;
    }

    public static LinkedHashMap<String, String> getChartAdminIDs(String chartsOutput) {
        LinkedHashMap<String, String> chartAdminIDs = new LinkedHashMap<>();
        StringTokenizer chartTokens = new StringTokenizer(chartsOutput, nl);
        while (chartTokens.hasMoreTokens()) {
            String line = chartTokens.nextToken().trim();
            StringTokenizer attributes = new StringTokenizer(line, "|");
            if (attributes.countTokens() == 8) {
                // Process all charts
                // attributes.nextToken().trim(); // Ignore
                // Just grab the ID and Name
                chartAdminIDs.put(attributes.nextToken().trim(), attributes.nextToken().trim());
            } else {
                log("ERROR: Failed to parse charts output - " + line);
            }
        }
        return chartAdminIDs;

    }
}
