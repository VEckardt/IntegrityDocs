package com.ptc.services.utilities.docgen;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.im.IMModelTypeName;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
import com.ptc.services.utilities.docgen.utils.StringObj;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;

/**
 * The Query class contains the following information about an Integrity Query:
 * createdBy description fields id isAdmin lastModified name queryDefinition
 * shareWith sortDirection sortField
 *
 * Note: We're only interested in admin queries either for reporting purposes or
 * xml export!
 */
public class TestVerdict extends IntegrityAdminObject {

    // Query's members
    public static final String XML_PREFIX = "TEST_VERDICT_";
    private Date lastModified;
    private String createdBy;
    private String displayName;
    private String verdicyType;

    public TestVerdict() {
        // modelType = IMModelTypeName.TEST_VERDICT;
        name = "";
        createdBy = "";
        lastModified = new Date();
        description = "";
        displayName = "";
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
        verdicyType = "";
        objectType = Types.Verdict;
    }

    // All setter functions
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastModifiedDate(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVerdicyType(String verdicyType) {
        this.verdicyType = verdicyType;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setName(String name) {
        this.name = name;
        this.xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
    }

//    public void setQueryDefinition(String queryDefinition) {
//        this.queryDefinition = queryDefinition;
//    }
//
//    public void setShareWith(String shareWith) {
//        this.shareWith = shareWith;
//    }
//
//    public void setSortDirection(String sortDirection) {
//        this.sortDirection = sortDirection;
//    }
//
//    public void setSortField(String sortField) {
//        this.sortField = sortField;
//    }
    // All getter/access functions...
    @Override
    public String getPosition() {
        return this.getID().replaceAll(" ", "_");
    }

    @Override
    public Element getXML(Document job, Element command) {
        // Add this query to the global resources hash
        XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

        // Setup the command to re-create the query via the Load Test Harness...
        Element app = job.createElement("app");
        app.appendChild(job.createTextNode(Command.IM));
        command.appendChild(app);

        Element cmdName = job.createElement("commandName");
        cmdName.appendChild(job.createTextNode("createquery"));
        command.appendChild(cmdName);

//        // --fields=field,field,...  The fields of the query's default column set.
//        if (fields.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "fields", Integrity.getXMLParamFieldValue(Integrity.convertStringToList(fields, ","), IntegrityField.XML_PREFIX, ",")));
//        }
//        // --queryDefinition=See documentation.  The string giving the complete query definition.
//        // TODO: Need to parameterize query definition rule
//        if (queryDefinition.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "queryDefinition", queryDefinition));
//        }
//        // --[no]sortAscending  The sort direction of the query's default column set.
//        if (sortDirection.length() > 0 && sortDirection.equalsIgnoreCase("Descending")) {
//            command.appendChild(XMLWriter.getOption(job, "nosortAscending", null));
//        } else {
//            command.appendChild(XMLWriter.getOption(job, "sortAscending", null));
//        }
//        // --sortField=field  The sort field of the query's default column set.
//        if (sortField.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "sortField", XMLWriter.padXMLParamName(IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(sortField))));
//        }
//        // --sharedAdmin  Set this object to be a shared admin object
//        if (isAdmin) {
//            command.appendChild(XMLWriter.getOption(job, "sharedAdmin", null));
//        }
//        // --shareWith=u=user1[:modify],user2[:modify],.. ;g=group1[:modify],group2[:modify],..   
//        //			Set the users and groups that can see and optionally modify this object.
//        if (shareWith.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "shareWith", shareWith));
//        }
//        // --description=value  Short description
//        if (description.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "description", description));
//        }
//        // --name=value  The name for this object		
//        if (name.length() > 0) {
//            command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
//        }
        return command;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastModifiedDate(SimpleDateFormat sdf) {
        return Integrity.getDateString(sdf, lastModified);
    }

    @Override
    public String getDescription() {
        return description;
    }

//    public String getFields() {
//        return fields;
//    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getXMLName() {
        return xmlParamName;
    }

//    public String getQueryDefinition() {
//        return queryDefinition;
//    }
//
//    public String getShareWith() {
//        return shareWith;
//    }
//
//    public String getSortDirection() {
//        return sortDirection;
//    }
//
//    public String getSortField() {
//        return sortField;
//    }
    public String getDisplayName() {
        return displayName;
    }

    public String getVerdictType() {
        return verdicyType;
    }

    @Override
    public String getDetails() {
        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append(appendNewLine("     <table class='display'>"));
        sb.addFieldValue("Verdict Type", this.getVerdictType());
        sb.addFieldValue("Name", this.getName());
        sb.addFieldValue("Display Name", this.getDisplayName());
        sb.addFieldValue("Description", HyperLinkFactory.convertHyperLinks(this.getDescription()));
        // Close out the triggers details table
        sb.append(appendNewLine("     </table>"));

        return sb.toString();
    }
}
