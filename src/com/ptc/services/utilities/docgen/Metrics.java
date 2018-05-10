/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.api.util.ResponseUtil;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author veckardt
 */
public class Metrics extends ArrayList implements WorkItemIterator {

    int len = 0;

    @Override
    public WorkItem next() throws APIException {
        return (SimpleItem) this.get(len++);
    }

    @Override
    public boolean hasNext() {
        return len < this.size();
    }

    @Override
    public WorkItem getLast() {
        return (SimpleItem) this.get(size() - 1);
    }

    private static void addLine(Metrics metrics, int cnt, String line) {
        SimpleItem item = new SimpleItem("Metrics", String.valueOf(++cnt));
        item.add("Line", String.valueOf(cnt));
        item.add("Output", "<font face=\"Lucida Console\">" + line.replaceAll(" ", "&nbsp;") + "</font>");
        metrics.add(item);
    }

    public static Metrics getMetrics(Integrity session, Types type, File path) throws APIException {
        Metrics metrics = new Metrics();

        String outputPath = path + fs + type.getPlural();

        // im diag --diag=metrics --target=server
        log("Reading " + "metrics ...", 1);
        Command cmd = new Command(Command.IM, "diag");
        // Add each query selection to the view query command
        cmd.addOption(new Option("diag", "metrics"));
        cmd.addOption(new Option("target", "server"));
        Response response = session.getAPI().runCommand(cmd);
        // ResponseUtil.printResponse(response, 1, System.out);
        String output = response.getResult().getMessage();

        // int cnt = 0;
        // StringTokenizer tokens = new StringTokenizer(output, nl);
        ArrayList<String> lineArray = new ArrayList<>();
        String[] lines = output.split("\n");
        for (int i = 0; i < lines.length; i++) {
            // while (tokens.hasMoreTokens()) {
            lineArray.add(lines[i]);
            // }
        }
        Boolean tableMode = false;
        SimpleItem si = null;
        for (int i = 0; i < lineArray.size() - 1; i++) {
            if (lineArray.get(i + 1).startsWith("------")) {
                tableMode = true;
            } else if (lineArray.get(i).isEmpty()) {
                tableMode = false;
            }
            si = new SimpleItem("Metric", String.valueOf(i));
            si.add("Line", String.valueOf(i));
            si.add("Section", tableMode?"-":"");
            if (tableMode) // if (lineArray.get(i+1).substring(0,3).);
            {
                si.add("Text", "<font face=\"Lucida Console\">" + lineArray.get(i).replaceAll(" ", "&nbsp;") + "</font>");
                metrics.add(si);
            } else {
                si.add("Text", "<font face=\"Lucida Console\">" + lineArray.get(i).replaceAll(" ", "&nbsp;") + "</font>");
                metrics.add(si);
            }
        }
        si.add("Text", lineArray.get(lineArray.size()-1).replaceAll(" ", "&nbsp;") );

//                if (line.startsWith("Created by")) {
//                    fieldlist.add(new SimpleField("Created by", line.replace("Created by", "")));
//                } else if (line.startsWith("Modified by")) {
//                    fieldlist.add(new SimpleField("Modified by", line.replace("Modified by", "")));
//                } else {
//                fieldName = line.substring(0, line.indexOf(":")).replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//                if (!Arrays.asList(chartAttributes2).contains(fieldName)) {
//                    fieldValue = line.substring(line.indexOf(":") + 1);
//                    fieldlist.add(new SimpleField(fieldName, fieldValue));
//                    //   }
//                }
        return metrics;
    }

}
