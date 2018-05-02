/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Integrity.globalId;
import static com.ptc.services.utilities.docgen.IntegrityDocs.CONTENT_DIR;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.fs;
import static com.ptc.services.utilities.docgen.utils.ImageUtils.extractImage;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.io.File;
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

    WorkItem object;
    Iterator fields;
    String isActive = "";
    String imageFileName = "";

    public IntegrityObject(WorkItem workitem, Types objectType, List<Field> fieldListFromText) {
        this(workitem, objectType);
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

    public IntegrityObject(WorkItem workitem, Types objectType) {
        object = workitem;
        // out.println("iProjects: 3");
        fields = workitem.getFields();
        id = String.valueOf(globalId++);
        name = workitem.getId();
        this.objectType = objectType;
        if (objectType.equals(Types.Viewset)) {
            name = workitem.getField("name").getString();
        }

        log("  Processing " + this.getObjectType() + " '" + name + "' ...");

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
                try {
                    type = workitem.getField("chartType").getValueAsString();
                } catch (NoSuchElementException ex3) {
                    type = "-";
                }
            }
        }
        try {
            isActive = workitem.getField("isActive").getValueAsString();
        } catch (NoSuchElementException ex) {

        }
        try {
            Field image = workitem.getField("image");

            if (image.getItem().getId().contentEquals("custom")) {
                imageFileName = CONTENT_DIR + "/" + this.getDirectory() + "/" + workitem.getId().replaceAll(" ", "_").replaceAll("\\W", "") + ".png";
                File imageFile = new File(imageFileName);
                extractImage(image, imageFile);
            }
        } catch (Exception ex) {
            imageFileName = CONTENT_DIR + "/images/" + this.getObjectType() + ".png";
        }
    }

    @Override
    protected String getName() {
        return name;
    }

    public String getIsActive() {
        return isActive;
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
    protected String getPosition() {
        return position;
    }

    @Override
    protected String getDetails() {

        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append("<table class='display'>");
        sb.setPath(CONTENT_DIR + fs + this.getDirectory());
        if (!imageFileName.isEmpty()) {
            sb.addFieldValue("Image", "<img src=\"" + imageFileName + "\" alt=\"-\">");
        }
        while (fields.hasNext()) {
            Field fld = (Field) fields.next();
            sb.addFieldValue(fld.getName(), fld.getValueAsString());
        }
        // Close out the triggers details table
        sb.append("</table>");

        return sb.toString();
    }

}
