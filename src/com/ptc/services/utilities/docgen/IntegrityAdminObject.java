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
    protected Types objectType;

    protected abstract String getName();

    protected String getDirectory() {
        return objectType.getDirectory();
    }

    ;

    protected abstract String getDescription();

    protected abstract String getXMLName();

    protected abstract Element getXML(Document job, Element command);

    protected abstract String getPosition();

    // protected abstract String getOverview();
    protected abstract String getDetails();

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public String getObjectType() {
        return objectType.name();
    }

    public String getAdminXMLPrefix() {
        return objectType.name().toUpperCase() + "_";
    }
}
