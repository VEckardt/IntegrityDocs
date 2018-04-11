package com.ptc.services.utilities.docgen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.im.IMModelTypeName;

/**
 * The Trigger class contains the following information about an Integrity
 * Trigger: position type name description runAs query rule assign frequency
 * script scriptParams scriptTiming
 */
public class Trigger extends IntegrityAdminObject {

    // Trigger's members
    public static final String XML_PREFIX = "TRIGGER_";
    private String position;
    private String type;
    private String runAs;
    private String query;
    private String rule;
    private String assign;
    private String frequency;
    private String script;
    private String scriptParams;
    private String scriptTiming;

    public Trigger() {
        modelType = IMModelTypeName.TRIGGER;
        name = "undefined";
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
        position = "undefined";
        type = "undefined";
        description = "";
        runAs = "";
        query = "";
        rule = "";
        assign = "";
        frequency = "";
        script = "";
        scriptParams = "";
        scriptTiming = "";
    }

    // All setter functions
    public void setPosition(String pos) {
        position = pos;
    }

    public void setType(String t) {
        type = t;
    }

    public void setName(String n) {
        name = n;
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public void setRunAs(String rAs) {
        runAs = rAs;
    }

    public void setQuery(String qry) {
        query = qry;
    }

    public void setRule(String r) {
        rule = r;
    }

    public void setAssignments(String ass) {
        assign = ass;
    }

    public void setFrequency(String freq) {
        frequency = freq;
    }

    public void setScript(String s) {
        script = s;
    }

    public void setScriptParams(String params) {
        scriptParams = params;
    }

    public void setScriptTiming(String timing) {
        scriptTiming = timing;
    }

    // All getter/access functions...
    public String getModelType() {
        return modelType;
    }

    public Element getXML(Document job, Element command) {
        // Add this trigger to the global resources hash
        XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

        // Setup the command to re-create the trigger via the Load Test Harness...
        Element app = job.createElement("app");
        app.appendChild(job.createTextNode(Command.IM));
        command.appendChild(app);

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
        if (frequency.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "frequency", frequency));
        }
        // --query=[user:]query  The query name to associate with the trigger
        if (query.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            String queryName = Query.XML_PREFIX + XMLWriter.getXMLParamName(query);
            XMLWriter.paramsHash.put(queryName, query);
            command.appendChild(XMLWriter.getOption(job, "query", XMLWriter.padXMLParamName(queryName)));
        }
        // --rule=See documentation.  The rule to associate with the trigger
        if (rule.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            command.appendChild(XMLWriter.getOption(job, "rule", rule));
        }
        // --runAs=user  Set the user used to run the trigger
        if (runAs.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            String userID = Integrity.USER_XML_PREFIX + XMLWriter.getXMLParamName(runAs);
            XMLWriter.paramsHash.put(userID, runAs);
            command.appendChild(XMLWriter.getOption(job, "runAs", XMLWriter.padXMLParamName(userID)));
        }
        // --script=value  The script filename
        if (script.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            command.appendChild(XMLWriter.getOption(job, "script", script));
        }
        // --scriptParams=[arg=value[;arg2=value2...]]  The list of script arguments
        if (scriptParams.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            command.appendChild(XMLWriter.getOption(job, "scriptParams", scriptParams));
        }
        // --scriptTiming=[pre|post|pre,post|none]  Whether the script should be run pre or post issue commit
        if (scriptTiming.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            command.appendChild(XMLWriter.getOption(job, "scriptTiming", scriptTiming));
        }
        // --type=[scheduled|rule|timeentry|copytree|branch|label|testresult]  The trigger type
        if (type.length() > 0 && !script.equalsIgnoreCase("mks:im:computationHistory")) {
            command.appendChild(XMLWriter.getOption(job, "type", type));
        }
        // --description=value  Short description
        if (description.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "description", description));
        }

        // --name=value  The name for this object
        if (script.equalsIgnoreCase("mks:im:computationHistory")) {
            Element selection = job.createElement("selection");
            selection.appendChild(job.createTextNode(xmlParamName));
            command.appendChild(selection);
        } else {
            command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
        }

        return command;
    }

    public String getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getXMLName() {
        return xmlParamName;
    }

    public String getDescription() {
        return description;
    }

    public String getRunAs() {
        return runAs;
    }

    public String getQuery() {
        return query;
    }

    public String getRule() {
        return rule;
    }

    public String getAssignments() {
        return assign;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getScript() {
        return script;
    }

    public String getScriptParams() {
        return scriptParams;
    }

    public String getScriptTiming() {
        return scriptTiming;
    }
}
