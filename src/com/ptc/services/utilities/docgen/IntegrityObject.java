/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Integrity.globalId;
import static com.ptc.services.utilities.docgen.utils.Utils.addFieldValue;
import static com.ptc.services.utilities.docgen.utils.Utils.appendNewLine;
import java.util.Iterator;
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
    int id = 0;

    public IntegrityObject(WorkItem workitem) {
        this(workitem, workitem.getModelType());
        modelType = workitem.getModelType();
        if (modelType.indexOf('.') > 0) {
            modelType = modelType.substring(modelType.indexOf('.') + 1);
        }
    }

    public IntegrityObject(WorkItem workitem, String modelType) {
        object = workitem;
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
                position = workitem.getId();
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
    }

    @Override
    protected String getName() {
        return name;
    }

    protected String getType() {
        return type;
    }

    @Override
    protected String getDirectory() {
        directory = modelType + "s";
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
        StringBuilder sb = new StringBuilder();
        // Print out the detail about each item type
        sb.append(appendNewLine("<table class='display'>"));
        while (fields.hasNext()) {
            Field fld = (Field) fields.next();
            addFieldValue(sb, fld.getName(), fld.getValueAsString());
        }
        // Close out the triggers details table
        sb.append(appendNewLine("</table>"));

        return sb.toString();
    }

}
