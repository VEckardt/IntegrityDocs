package com.ptc.services.utilities.docgen.stage;

import static com.ptc.services.utilities.docgen.Copyright.copyright;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.IOException;

public class DeployDocWriter {

    private BufferedWriter doc;
    private List<StagingSystem> sdStagingSystems;
    private Date now;
    private SimpleDateFormat sdf;
    private String svrInfo;

    public DeployDocWriter(String hostInfo, BufferedWriter writer, List<StagingSystem> stagingSystemsList) {
        svrInfo = hostInfo;
        doc = writer;
        sdStagingSystems = stagingSystemsList;
        now = new Date();
        sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
    }

    public void write() throws IOException {
        writeLine("<html>");
        writeLine("<head>");
        writeLine("  <title>PTC Integrity LM - Deploy Configuration @ " + svrInfo + "</title>");
        writeLine("  <script type='text/javascript'>var line_count=0;</script>");
        writeLine("  <style type='text/css' media='screen'>");
        writeLine("    body, table { font-family: Arial, Helvetica, Tahoma; font-size: 9pt; }");
        writeLine("    table.display { font-family: Arial, Helvetica, Tahoma; font-size: 9pt; width: 100%;}");
        writeLine("    .display td { vertical-align: top; font-size: 9pt; }");
        writeLine("    table.list { font-family: Arial, Helvetica, Tahoma; font-size: 9pt; width: 95%; border-width: 1px; "
                + "border-style: solid; border-color: #333399; border-collapse: collapse; padding: 5px 5px 5px 5px; }");
        writeLine("    .list th { font-size: 9pt; color: #000000; background-color: #5bb73b; border-width: 1px; border-style: solid; "
                + "text-align: left; vertical-align: middle; border-color: #333399; padding: 2px 8px 2px 8px; }");
        writeLine("    .list td { background-color: #F9F7F7; vertical-align: top; border-width: 1px; border-style: solid; "
                + "border-color: #333399; padding: 5px 5px 5px 5px; }");
        writeLine("    td.logo { width: 10%; }");
        writeLine("    .border { border-width: 0px; border-style: solid; border-color: #333399; padding: 2px 8px 2px 8px; }");
        writeLine("    .center { text-align: center; }");
        writeLine("    .right { text-align: right; }");
        writeLine("    .nowrap { white-space: nowrap; }");
        writeLine("    .title { font-size: 16pt; color: #333399; font-weight: bold; text-align: center; }");
        writeLine("    .date { font-size: 10pt; color: #333399; font-weight: bold; text-align: right; }");
        writeLine("    .header { font-size: 11pt; color: #000000; text-align: center;}");
        writeLine("    .footer { font-size: 9pt; color: #999999; text-align: left; }");
        writeLine("    .heading1 { font-size: 11pt; color: #000000; font-weight: bold; background-color: #F6E78C; "
                + "text-align: left; vertical-align: middle; white-space: nowrap; padding: 2px 8px 2px 8px; }");
        writeLine("    .heading2 { font-size: 11pt; color: #333399; font-weight: bold; background-color: #F6E78C; }");
        writeLine("    .heading3 { font-size: 11pt; color: #000000; font-weight: bold; background-color: #E6E6CB; }");
        writeLine("    .heading4 { font-size: 11pt; color: #333399; font-weight: bold; background-color: #E6E6CB; }");
        writeLine("    .heading5 { font-size: 11pt; color: #000000; font-weight: bold; background-color: #CFCF9D; }");
        writeLine("    .heading6 { font-size: 11pt; color: #333399; font-weight: bold; background-color: #CFCF9D; }");
        writeLine("    .heading7 { font-size: 15pt; color: #000000; font-weight: bold; background-color: #CFCF9D; }");
        writeLine("    .heading8 { font-size: 10pt; color: #000000; font-weight: bold; text-align: center; background-color: #F5EFCA; }");
        writeLine("    .heading9 { font-size: 10pt; color: #000000; font-weight: bold; background-color: #F6E78C; }");
        writeLine("    .grouping1 { font-size: 10pt; color: #333399; font-weight: bold; background-color: #CFCF9D; }");
        writeLine("    .grouping2 { font-size: 10pt; color: #000000; font-weight: bold; background-color: #CFCF9D; }");
        writeLine("    .grouping3 { font-size: 9pt; color: #333399; font-weight: bold; background-color: #E6E6CB; }");
        writeLine("    .odd_row { background-color: #F8F3F7; }");
        writeLine("    .even_row { background-color: #FFFBFF; }");
        writeLine("    .bold_color { color: #333399; font-weight: bold; white-space: nowrap; }");
        writeLine("    .bold_color_italic { color: #333399; font-weight: bold; font-style: italic; }");
        writeLine("    .bold_color_underline { color: #333399; font-weight: bold; text-decoration: underline; }");
        writeLine("    .hr_default { height:1px; width:100%; color: #000000; background:#000000; }");
        writeLine("    .calendar_no_date_cell { background-color: #F3F3F3; }");
        writeLine("  </style>");
        writeLine("</head>");
        writeLine("<body>");
        writeLine("<table class='display'>");
        writeLine("<thead>");
        writeLine(" <tr>");
        writeLine("   <td colspan='5'>");
        writeLine("     <table class='display'>");
        writeLine("      <tr>");
        writeLine("        <td class='logo'>&nbsp;</td>");
        writeLine("        <td>");
        writeLine("          <div class='title'>Integrity Deploy Configuration Report</div><p>");
        writeLine("          <div class='header'>" + svrInfo + "</div><p>");
        writeLine("          <div class='header'>A generated self documented configuration report for all <a href='#ss'>Deploy Staging Systems</a></div>");
        writeLine("        </td>");
        writeLine("      </tr>");
        writeLine("     </table>");
        writeLine("   </td>");
        writeLine(" </tr>");
        writeLine(" <tr><td colspan='5' class='date'>" + sdf.format(now) + "</td></tr>");
        writeLine(" <tr><td colspan='5'><a name='ss'><hr class='hr_default'></a></td></tr>");

        writeTypeSummary();
        writeTypeDetail();

        // Close the table body
        writeLine("</tbody>");
        writeLine("</table>");
        writeLine("</body>");
        writeLine("</html>");
    }

    private void writeLine(String line) throws IOException {
        doc.write(line + IntegrityDocs.nl);
    }

    private void writeTypeSummary() throws IOException {
        // Summary heading line
        writeLine(" <tr>");
        writeLine("   <th class='heading1'>Name</th>");
        writeLine("   <th class='heading1'>Project</th>");
        writeLine("   <th class='heading1'>Stages</th>");
        writeLine("   <th class='heading1'>Description</th>");
        writeLine("   <th class='heading1'>Default Deploy Type</th>");
        writeLine(" </tr>");
        writeLine(" <tr><td colspan='5'><hr class='hr_default'></td></tr>");
        writeLine("</thead>");
        writeLine("<tfoot>");
        writeLine(" <tr><td colspan='5'><hr class='hr_default'></td></tr>");
        writeLine(" <tr><td colspan='5' class='footer'>" + copyright + "</td></tr>");
        writeLine("</tfoot>");
        writeLine("<tbody>");

        // Print out the summary about each Staging System
        for (Iterator<StagingSystem> it = sdStagingSystems.iterator(); it.hasNext();) {
            StagingSystem ss = it.next();
            writeLine("<script type='text/javascript'>line_count++; if (line_count%2 == 0) "
                    + "{document.write(\"<tr class='even_row'>\");} else {document.write(\"<tr class='odd_row'>\");}</script>");
            writeLine("   <td nowrap class='border'><a href='#ss" + ss.getID() + "'>" + ss.getName() + "</a></td>");
            writeLine("   <td class='border'>" + ss.getProject() + "</td>");
            writeLine("   <td class='border'>" + ss.getStageNames() + "</td>");
            writeLine("   <td class='border'>" + ss.getDescription() + "</td>");
            writeLine("   <td class='border'>" + ss.getDefaultDeployType() + "</td>");
            writeLine(" </tr>");
        }
    }

    private void writeTypeDetail() throws IOException {
        // Print out the detail about each Staging System
        writeLine(" <tr>");
        writeLine("   <td colspan='5'>");
        writeLine("     <table class='display'>");
        for (Iterator<StagingSystem> it = sdStagingSystems.iterator(); it.hasNext();) {
            StagingSystem ss = it.next();
            writeLine("      <tr><td colspan='2'><a name='ss" + ss.getID() + "'><hr class='hr_default'></a></td></tr>");
            writeLine("      <tr>");
            writeLine("        <th class='heading1' colspan='2'>" + ss.getName() + "</th>");
            writeLine("      </tr>");
            writeLine("      <tr><td colspan='2'><hr class='hr_default'></td></tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>ID</td>");
            writeLine("        <td>" + ss.getID() + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Project</td>");
            writeLine("        <td>" + ss.getProject() + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Description</td>");
            writeLine("        <td>" + ss.getDescription() + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Default Deploy Type</td>");
            writeLine("        <td>" + ss.getDefaultDeployType() + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Deploy Types</td>");
            writeLine("        <td>" + ss.getFormattedDeployTypeReport() + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Properties</td>");
            writeLine("        <td>" + ss.getFormattedPropertiesReport() + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Pre Incremental Actions</td>");
            writeLine("        <td>" + ss.getFormattedActionsReport(ss.getPreIncrementalActions()) + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Post Incremental Actions</td>");
            writeLine("        <td>&nbsp;" + ss.getFormattedActionsReport(ss.getPostIncrementalActions()) + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Pre Full Actions</td>");
            writeLine("        <td>" + ss.getFormattedActionsReport(ss.getPreFullActions()) + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Post Full Actions</td>");
            writeLine("        <td>&nbsp;" + ss.getFormattedActionsReport(ss.getPostFullActions()) + "</td>");
            writeLine("      </tr>");
            writeLine("      <tr>");
            writeLine("        <td class='bold_color'>Stages</td>");
            writeLine("        <td>&nbsp;" + ss.getFormattedStageReport() + "</td>");
            writeLine("      </tr>");
        }
        // Close out the type details table
        writeLine("     </table>");
        writeLine("   </td>");
        writeLine(" </tr>");
    }
}
