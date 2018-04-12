package com.ptc.services.utilities.docgen;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.im.IMModelTypeName;

/**
 * The Query class contains the following information about an Integrity Query:
 * createdBy description fields id isAdmin lastModified name queryDefinition
 * shareWith sortDirection sortField
 *
 * Note: We're only interested in admin queries either for reporting purposes or
 * xml export!
 */
public class Query extends IntegrityAdminObject {

    // Query's members
    public static final String XML_PREFIX = "QUERY_";
    private String id;
    private String createdBy;
    private Date lastModified;
    private String fields;
    private boolean isAdmin;
    private String queryDefinition;
    private String shareWith;
    private String sortDirection;
    private String sortField;

    public Query() {
        modelType = IMModelTypeName.QUERY;
        id = "";
        createdBy = "";
        lastModified = new Date();
        description = "";
        fields = "";
        isAdmin = true;
        name = "";
        xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
        queryDefinition = "";
        shareWith = "";
        sortDirection = "";
        sortField = "";
        directory = "Queries";
    }

    // All setter functions
    public void setID(String id) {
        this.id = id;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastModifiedDate(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public void setSharedAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setName(String name) {
        this.name = name;
        this.xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
    }

    public void setQueryDefinition(String queryDefinition) {
        this.queryDefinition = queryDefinition;
    }

    public void setShareWith(String shareWith) {
        this.shareWith = shareWith;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    // All getter/access functions...
    public String getModelType() {
        return modelType;
    }
    
    public String getPosition() {
        return this.getID().replaceAll(" ", "_");
    }    

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

        // --fields=field,field,...  The fields of the query's default column set.
        if (fields.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "fields", Integrity.getXMLParamFieldValue(Integrity.convertStringToList(fields, ","), IntegrityField.XML_PREFIX, ",")));
        }
        // --queryDefinition=See documentation.  The string giving the complete query definition.
        // TODO: Need to parameterize query definition rule
        if (queryDefinition.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "queryDefinition", queryDefinition));
        }
        // --[no]sortAscending  The sort direction of the query's default column set.
        if (sortDirection.length() > 0 && sortDirection.equalsIgnoreCase("Descending")) {
            command.appendChild(XMLWriter.getOption(job, "nosortAscending", null));
        } else {
            command.appendChild(XMLWriter.getOption(job, "sortAscending", null));
        }
        // --sortField=field  The sort field of the query's default column set.
        if (sortField.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "sortField", XMLWriter.padXMLParamName(IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(sortField))));
        }
        // --sharedAdmin  Set this object to be a shared admin object
        if (isAdmin) {
            command.appendChild(XMLWriter.getOption(job, "sharedAdmin", null));
        }
        // --shareWith=u=user1[:modify],user2[:modify],.. ;g=group1[:modify],group2[:modify],..   
        //			Set the users and groups that can see and optionally modify this object.
        if (shareWith.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "shareWith", shareWith));
        }
        // --description=value  Short description
        if (description.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "description", description));
        }
        // --name=value  The name for this object		
        if (name.length() > 0) {
            command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
        }

        return command;
    }

    public String getID() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastModifiedDate(SimpleDateFormat sdf) {
        return Integrity.getDateString(sdf, lastModified);
    }

    public String getDescription() {
        return description;
    }

    public String getFields() {
        return fields;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getXMLName() {
        return xmlParamName;
    }

    public String getQueryDefinition() {
        return queryDefinition;
    }

    public String getShareWith() {
        return shareWith;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSortField() {
        return sortField;
    }
    public String getDirectory() {
        return directory;
    }}
