package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class IntegrityAdminObject {

    protected Iterator fields;
    protected WorkItem object;
    protected String id = "";
    // protected String modelType;
    protected String name;
    protected String xmlParamName;
    protected String description;
    protected String position;
    protected Boolean isActive;
    protected String type; // object individual type, not objectType!
    protected Types objectType;
    protected String visibleFields = "";
    protected List<String> usedInTypes = new ArrayList<>();

    protected abstract String getName();

    protected abstract String getDescription();

    protected abstract String getXMLName();
    
    protected abstract String getFieldValue(String fieldName);

    protected abstract Element getXML(Document job, Element command);

    protected abstract String getPosition();

    // protected abstract String getOverview();
    protected abstract String getDetails();

    /**
     * Setters
     */
    public void setID(String id) {
        this.id = id;
    }

    public void setType(String t) {
        type = t;
    }

    public void addUsedInType(String name) {
        usedInTypes.add(name);
    }
    
    public Boolean usedInTypeIsEmpty() {
        return usedInTypes.isEmpty();
    }    
    
    /**
     * Getters
     */
    public String getID() {
        return id;
    }

    public String getObjectType() {
        return objectType.name();
    }
    
    protected String getDirectory() {
        return objectType.getDirectory();
    }    
    
    public String getFieldListString() {
        Iterator it = object.getFields();
        String fieldList = "";
        while (it.hasNext()) {
            Field field = (Field) it.next();
            fieldList += (fieldList.isEmpty() ? "" : ",") + field.getName();
        }
        return fieldList;
    }    

    public String getObjectTypeDescription() {
        return objectType.getDescription();
    }

    public String getObjectTypeDisplayName() {
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
    
    public Boolean isActive () {
        return isActive;
    }   
}
