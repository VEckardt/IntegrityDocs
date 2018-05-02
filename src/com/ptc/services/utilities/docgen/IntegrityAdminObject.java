package com.ptc.services.utilities.docgen;

import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class IntegrityAdminObject {

    protected String id = "";
    // protected String modelType;
    protected String name;
    protected String xmlParamName;
    protected String description;
    protected String position;
    protected String type; // object individual type, not objectType!
    protected Types objectType;

    protected abstract String getName();

    protected String getDirectory() {
        return objectType.getDirectory();
    }

    protected abstract String getDescription();

    protected abstract String getXMLName();

    protected abstract Element getXML(Document job, Element command);

    protected abstract String getPosition();

    // protected abstract String getOverview();
    protected abstract String getDetails();

    public void setID(String id) {
        this.id = id;
    }

    public void setType(String t) {
        type = t;
    }

    public String getID() {
        return id;
    }

    public String getObjectType() {
        return objectType.name();
    }

    public String getObjectDisplayName() {
        return objectType.getDisplayName();
    }

    public String getObjectsDisplayName() {
        return objectType.getDisplayName().replace("ery", "erie") + "s";
    }

    public String getType() {
        return type;
    }

    public String getAdminXMLPrefix() {
        return objectType.name().toUpperCase() + "_";
    }

    public String getTypeClassGroup() {
        return (getType().substring(0, 1).toUpperCase() + getType().substring(1)).replaceAll("([A-Z])", " $1");
    }
}
