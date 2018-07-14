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
import com.mks.api.response.WorkItem;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityUtils.summarizeItemList;
import com.ptc.services.utilities.docgen.field.AllowedTypes;
import com.ptc.services.utilities.docgen.field.PickField;
import com.ptc.services.utilities.docgen.utils.HyperLinkFactory;
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

   String imageFileName = "";

   /**
    * Constructor 1
    *
    * @param workitem
    * @param objectType
    * @param fieldListFromText
    */
   public IntegrityObject(WorkItem workitem, Types objectType, List<Field> fieldListFromText) {
      this(workitem, objectType);
      List<Field> fieldlistneu = new ArrayList<>();
      // take what we already have
      // Iterator fields = object.getFields();
      for (Field field : fields) {
         fieldlistneu.add(field);
      }
      // add new text fields 
      for (Field field : fieldListFromText) {
         fieldlistneu.add(field);
      }
      this.fields = fieldlistneu; // VE !!! .iterator();
   }

   /**
    * Constructor 2
    *
    * @param workitem
    * @param objectType
    */
   public IntegrityObject(WorkItem workitem, Types objectType) {
      super(workitem, objectType);
      // out.println("iProjects: 3");
      log("  Processing " + this.getObjectType() + " '" + name + "' ...");

      if (workitem != null) {
         Iterator it = workitem.getFields();
         while (it.hasNext()) {
            fields.add((Field) it.next());
         }

         try {
            // workitem.getField("position");
            position = workitem.getField("position").getValueAsString();
         } catch (NoSuchElementException | NullPointerException ex) {
            try {
               position = workitem.getField("id").getValueAsString();
            } catch (NoSuchElementException | NullPointerException ex2) {
               position = String.valueOf(++globalId);
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
      StringObj sb = new StringObj();
      // Print out the detail about each item type
      sb.append("<table class='sortable'>");
      sb.setPath(CONTENT_DIR + fs + this.getDirectory());
      sb.addHeadings("Field,Value");
      // Put this on top of the values table
      if (!imageFileName.isEmpty()) {
         File file = new File(imageFileName);
         String fileName = (objectType.equals(Types.Chart) ? "../images/" + getType() + ".png" : file.getName());

         sb.addFieldValue("Image", "<img src=\"" + fileName + "\" alt=\"-\" onerror=\"this.src='../images/" + objectType.name() + ".png'\"/>");
      }

      for (Field field : fields) {
         // log("field.getName(): " + field.getName());
         if ((field.getName().equals("createdBy") || field.getName().equals("created") || field.getName().equals("lastModified")
                 || field.getName().equals("modifiedBy")) && IntegrityDocs.skipCreMoDetails) {
            continue;
         }

         // while (fields.hasNext()) {
         //     Field field = (Field) fields.next();
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
            case "Members": {  // MKS Domain group members
               sb.addFieldValue(field.getName(), getMembership());
               break;
            }
            case "assign": {
               sb.addFieldValue(field.getName(), summarizeItemList(Integrity.getSysFields(), field, "<br/>" + nl, false));
               break;
            }
            case "scriptParams": {
               sb.addFieldValue(field.getName(), summarizeItemList(Integrity.getSysFields(), field, "<br/>" + nl, false));
               break;
            }
            case "membership": {
               sb.addFieldValue(field.getName(), getMembership());
               break;
            }
            case "capabilities": {
               sb.addFieldValue(field.getName(), getFieldValue("capabilities"));
               break;
            }
            case "preview": {
               if (field.getString().endsWith("txt")) {
                  // <object data="rfc1459.txt" border="1" type="text/css" width="700" height="500"></object>
                  sb.addFieldValue(field.getName(), "<object data=\"" + field.getString() + "\" border=\"1\" type=\"text/css\" width=\"700\" alt=\"-\" />");
               } else {
                  sb.addFieldValue(field.getName(), "<img src=\"" + field.getString() + "\" alt=\"-\" />");
               }
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

   @Override
   protected String getOverview() {
      Types type = this.getObjectType();
      String additionalColumns = type.getAddColumns();
      List<IntegrityObject> objectList1 = getList(type);
      List<IntegrityObject> objectList2 = getList(type);
      StringObj sb = new StringObj();
      // Summary heading line
      String headings;
      Boolean showImage = false;
      sb.append(("<table class='sortable' id=\"" + type.getDisplayName().trim() + " Overview\">"));
      if (type.showAllFields()) {
         headings = objectList1.get(0).getFieldListString();
      } else {
         showImage = additionalColumns.contains("Image");
         additionalColumns = additionalColumns.replace(",Image", "").replace("Image,", "").replace("Image", "");
         headings = "ID,Name," + (showImage ? "Image," : "") + "Description" + (additionalColumns.isEmpty() ? "" : "," + additionalColumns);
      }
      sb.addHeadings(headings);
      sb.append(("<tbody>"));
      // Print out the summary about each trigger
      for (IntegrityObject object : objectList2) {
         sb.append(("<tr>"));
         if (type.showAllFields()) {
            for (Field fld : object.fields) {
               // while (object.fields.hasNext()) {
               //     Field fld = (Field) object.fields.next();
               if (fld.getName().equals("image")) {
                  sb.addTDborder("<img src=\"" + fld.getValueAsString() + "\" alt=\"-\"/>");
                  // 
               } else if (fld.getName().equals("view")) {
                  sb.addTDborder("<a href='" + fld.getValueAsString() + "'>View</a>");
               } else {
                  sb.addTDborder(fld.getValueAsString());
               }
            }
         } else {
            sb.addTDborder(object.getPosition());
            sb.addTDborder("<a href='" + object.getDirectory() + "/" + object.getPosition() + ".htm'>" + object.getName() + "</a>");
            if (showImage) {
               sb.addTDborder("<img src=\"" + object.getDirectory() + "/" + object.getName().replaceAll(" ", "_") + ".png\" alt=\"-\" onerror=\"this.src='images/" + object.getObjectType() + ".png'\"/>");
            }
            sb.addTDborder(HyperLinkFactory.convertHyperLinks(object.getDescription()));
            if (additionalColumns.contains("Type")) {
               sb.addTDborder(object.getType());
            }
            if (additionalColumns.contains("isActive")) {
               sb.addTDborder(object.isActive().toString());
            }
         }
         sb.append(("</tr>"));
      }
      sb.append(("</tbody></table>"));

      return sb.toString();
   }
}
