package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Integrity.chartAttributes2;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

public class ChartFactory {

    public static IntegrityObject parseChart(WorkItem chart, String chartOutput) {
        StringTokenizer chartTokens = new StringTokenizer(chartOutput, IntegrityDocs.nl);
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
        return new IntegrityObject(chart, Types.Chart, fieldlist);
    }

    public static List<Chart> parseCharts(String chartsOutput) {
        List<Chart> chartList = new ArrayList<>();
        StringTokenizer chartTokens = new StringTokenizer(chartsOutput, IntegrityDocs.nl);
        while (chartTokens.hasMoreTokens()) {
            String line = chartTokens.nextToken().trim();
            StringTokenizer attributes = new StringTokenizer(line, "|");
            if (attributes.countTokens() == 8) {
                // Only process admin charts
                boolean isAdmin = Boolean.parseBoolean(attributes.nextToken().trim());
                if (isAdmin) {
                    Chart c = new Chart();
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

    public static Hashtable<String, String> getChartAdminIDs(String chartsOutput) {
        Hashtable<String, String> chartAdminIDs = new Hashtable<>();
        StringTokenizer chartTokens = new StringTokenizer(chartsOutput, IntegrityDocs.nl);
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
