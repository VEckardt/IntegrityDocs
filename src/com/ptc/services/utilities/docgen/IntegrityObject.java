/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Integrity.globalId;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author veckardt
 */
public class IntegrityObject extends IntegrityAdminObject {

    Iterator fields;
    String type;  // object individual type!
    WorkItem object;
    String isActive = "";
    int id = 0;

    public IntegrityObject(WorkItem workitem) {
        this(workitem, workitem.getModelType());
        // out.println("iProjects: 2");
        modelType = workitem.getModelType();
        if (modelType.indexOf('.') > 0) {
            modelType = modelType.substring(modelType.indexOf('.') + 1);
        }
    }

    public IntegrityObject(WorkItem workitem, List<Field> fieldListFromText) {
        this(workitem, workitem.getModelType().replaceAll("im.", ""));
        List<Field> fieldlistneu = new ArrayList<>();
        // take what we already have
        while (fields.hasNext()) {
            fieldlistneu.add((Field) fields.next());
        }
        // add new text fields 
        for (Field field : fieldListFromText) {
            fieldlistneu.add(field);
        }
        this.fields = fieldlistneu.iterator();
    }

    public IntegrityObject(WorkItem workitem, String modelType) {
        object = workitem;
        // out.println("iProjects: 3");
        fields = workitem.getFields();
        id = globalId++;
        name = workitem.getId();
        if (modelType.equals("Viewset")) {
            name = workitem.getField("name").getString();
        }
        this.modelType = modelType;

        System.out.println("  Processing " + modelType + " '" + name + "' ...");

        try {
            position = workitem.getField("position").getValueAsString();
        } catch (NoSuchElementException ex) {
            try {
                position = workitem.getField("id").getValueAsString();
            } catch (NoSuchElementException ex2) {
                position = workitem.getId().replace("#/", "").replace("/", "_");
            }
        }
        try {
            description = workitem.getField("description").getValueAsString();
        } catch (NoSuchElementException ex) {
            description = "-";
        }
        try {
            type = workitem.getField("type").getValueAsString();
        } catch (NoSuchElementException ex) {
            try {
                type = workitem.getField("verdictType").getValueAsString();
            } catch (NoSuchElementException ex2) {
                type = "-";
            }
        }
        try {
            isActive = workitem.getField("isActive").getValueAsString();
        }catch (NoSuchElementException ex) {
            
        }
        
    }

    @Override
    protected String getName() {
        return name;
    }

    protected String getType() {
        return type;
    }
    
    public String getIsActive() {
        return isActive;
    }

    @Override
    protected String getDirectory() {
        directory = modelType.replaceAll("im.", "") + "s";
        directory = directory.equals("Querys") ? "Queries" : directory;
        return directory;
    }

    @Override
    protected String getObjectType() {
        return modelType;
    }

    @Override
    protected String getGlobalID() {
        return String.valueOf(id);
    }

    @Override
    protected String getDescription() {
        return description;
    }

    @Override
    protected String getXMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Element getXML(Document job, Element command) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getModelType() {
        return modelType;
    }

    @Override
    protected String getPosition() {
        return position;
    }

    @Override
    protected String getDetails() {

        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append("<table class='display'>");
        sb.setPath(IntegrityDocs.CONTENT_DIR + IntegrityDocs.fs + modelType + "s");
        while (fields.hasNext()) {
            Field fld = (Field) fields.next();
            sb.addFieldValue(fld.getName(), fld.getValueAsString());
        }
        // Close out the triggers details table
        sb.append("</table>");

        return sb.toString();
    }

}
