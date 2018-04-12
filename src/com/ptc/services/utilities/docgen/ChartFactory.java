package com.ptc.services.utilities.docgen;

import java.util.ArrayList;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

public class ChartFactory {

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
                    System.out.println("Processing Chart: " + c.getName());
                    c.setChartType(attributes.nextToken().trim());
                    c.setCreatedBy(attributes.nextToken().trim());
                    c.setGraphStyle(attributes.nextToken().trim());
                    c.setQuery(attributes.nextToken().trim());
                    c.setShareWith(attributes.nextToken().trim());
                    // c.setDescription(Integrity.getStringFieldValue(wi.getField("description")));
                    chartList.add(c);
                }
            } else {
                System.out.println("ERROR: Failed to parse charts output - " + line);
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
                attributes.nextToken().trim(); // Ignore
                // Just grab the ID and Name
                chartAdminIDs.put(attributes.nextToken().trim(), attributes.nextToken().trim());
            } else {
                System.out.println("ERROR: Failed to parse charts output - " + line);
            }
        }
        return chartAdminIDs;

    }
}
