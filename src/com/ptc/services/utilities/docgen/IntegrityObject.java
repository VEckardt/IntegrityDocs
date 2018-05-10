/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Integrity.globalId;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import com.ptc.services.utilities.docgen.field.AllowedTypes;
import com.ptc.services.utilities.docgen.field.PickField;
import static com.ptc.services.utilities.docgen.utils.ImageUtils.extractImage;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author veckardt
 */
public class IntegrityObject extends IntegrityAdminObject {

    String imageFileName = "";

    public IntegrityObject(WorkItem workitem, Types objectType, List<Field> fieldListFromText) {
        this(workitem, objectType);
        List<Field> fieldlistneu = new ArrayList<>();
        // take what we already have
        // Iterator fields = object.getFields();
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
            // workitem.getField("position");
            position = workitem.getField("position").getValueAsString();
        } catch (NoSuchElementException | NullPointerException ex) {
            try {
                position = workitem.getField("id").getValueAsString();
            } catch (NoSuchElementException | NullPointerException ex2) {
                position = workitem.getId().replace("#/", "").replace("/", "_");
            }
        }
        try {
            description = workitem.getField("description").getValueAsString();
        } catch (NoSuchElementException | NullPointerException ex) {
            description = "-";
        }
        try {
            type = workitem.getField("type").getValueAsString();
        } catch (NoSuchElementException | NullPointerException ex) {
            try {
                type = workitem.getField("verdictType").getValueAsString();
            } catch (NoSuchElementException | NullPointerException ex2) {
                try {
                    type = workitem.getField("chartType").getValueAsString();
                } catch (NoSuchElementException | NullPointerException ex3) {
                    type = "-";
                }
            }
        }
        if (objectType.equals(Types.Query)) {
            type = name.substring(0, 1);
        }
        if (objectType.equals(Types.Group)) {
            type = name.substring(0, 1);
        }
        try {
            isActive = workitem.getField("isActive").getBoolean();
        } catch (NoSuchElementException | NullPointerException ex) {

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

    public void setInActive() {
        isActive = false;
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

        String heading = "Field,Value";
        StringObj sb = new StringObj();
        // Print out the detail about each item type
        sb.append("<table class='sortable'>");
        sb.setPath(CONTENT_DIR + fs + this.getDirectory());
        sb.append("<thead>");
        sb.append("<tr>");
        for (String field : heading.split(",")) {
            sb.append("<th class='heading1'>" + field + "</th>");
        }
        sb.append("</tr>");
        sb.append("</thead>");
        if (!imageFileName.isEmpty()) {
            File file = new File(imageFileName);
            String fileName = "";
            if (objectType.equals(Types.Chart)) {
                fileName = "../images/" + getType() + ".png";
            } else {
                fileName = file.getName();
            }
            sb.addFieldValue("Image", "<img src=\"" + fileName + "\" alt=\"-\" onerror=\"this.src='../images/" + objectType.name() + ".png'\"/>");
        }

        while (fields.hasNext()) {
            Field field = (Field) fields.next();
            switch (field.getName()) {
                case "allowedTypes": {
                    AllowedTypes pf = new AllowedTypes(field);
                    sb.addFieldValue(field.getName(), pf.getFormattedReport());
                    break;
                }
                case "picks": {
                    PickField pf = new PickField(field);
                    sb.addFieldValue(field.getName(), pf.getFormattedReport());
                    break;
                }
                default:
                    sb.addFieldValue(field.getName(), field.getValueAsString());
                    break;
            }
        }
//        if (isActive.equals("no")) {
//            sb.addFieldValue("*In use", isActive);
//        }
        if (!usedInTypes.isEmpty()) {
            sb.addFieldValue("*Used In Types", usedInTypes.toString() + " (" + usedInTypes.size() + ")");
        }
        // Close out the triggers details table
        sb.append("</table>");

        return sb.toString();
    }

    @Override
    protected String getFieldValue(String fieldName) {
        if (fieldName.equals("visibleFields")) {
            return visibleFields;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
