/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.ItemList;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class IntegrityAdminObject {

    // Used when the element itself has no unique id
    protected static int globalId = 0;

    // protected Iterator fields;
    protected List<Field> fields = new ArrayList<>();
    protected WorkItem object;
    protected String id = "";
    // protected String modelType;
    protected String name;
    protected String xmlParamName;
    protected ItemList membership = null;
    protected String description = "";
    protected String position;
    protected Boolean isActive;
    protected String type; // object individual type, not objectType!
    protected Types objectType;
    protected String visibleFields = "";
    protected List<String> usedInTypes = new ArrayList<>();

    IntegrityAdminObject(WorkItem workItem, Types objectType) {
        this.object = workItem;
        this.objectType = objectType;
        this.name = (workItem != null ? workItem.getId() : "");
        this.id = (workItem != null ? workItem.getId() : "");
    }

    protected abstract String getName();
    
    protected WorkItem getWorkItem() {
        return object;
    }

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
    public void setObject(WorkItem wi) {
        this.object = wi;
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

    public Types getObjectType() {
        return objectType;
    }

    protected String getOverview() {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
    public String getObjectTypeName() {
        return objectType.name();
    }

    protected String getDirectory() {
        return objectType.getDirectory();
    }

    protected String getMembership() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public String getXMLPrefix() {
        return objectType.name().toUpperCase() + "_";
    }

    public String getTypeClassGroup() {
        return (getType().substring(0, 1).toUpperCase() + getType().substring(1)).replaceAll("([A-Z])", " $1");
    }

    public Boolean isActive() {
        return isActive;
    }

    public static int countByTypeClass(List<IntegrityAdminObject> adminObjectList, String typeClass) {
        int cnt = 0;
        for (IntegrityAdminObject object : adminObjectList) {
            if (object.getTypeClassGroup().equals(typeClass)) {
                cnt++;
            }
        }
        return cnt;
    }

    public boolean canViewItemsOfThisType(String groupName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canCreateItemsOfThisType(String groupName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
