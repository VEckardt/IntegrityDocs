package com.ptc.services.utilities.docgen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class IntegrityAdminObject {

    protected String modelType;
    protected String name;
    protected String xmlParamName;
    protected String description;
    protected String directory;
    protected String position;
    protected String objectType;
    protected String globalID;

    protected abstract String getGlobalID();
    
    protected abstract String getName();
    
    protected abstract String getDirectory();

    protected abstract String getDescription();

    protected abstract String getXMLName();

    protected abstract Element getXML(Document job, Element command);

    protected abstract String getModelType();
    
    protected abstract String getObjectType();
    
    protected abstract String getPosition();
    
    // protected abstract String getOverview();
    
    protected abstract String getDetails();

    public String getAdminObjectType() {
        if (null != modelType && modelType.indexOf('.') > 0) {
            return modelType.substring(modelType.indexOf('.') + 1);
        }

        return (null != modelType && modelType.length() > 0 ? modelType : "");
    }

    public String getAdminXMLPrefix() {
        return getAdminObjectType().toUpperCase() + "_";
    }
}
