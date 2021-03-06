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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mks.api.Command;
import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import static com.ptc.services.utilities.docgen.IntegrityUtils.convertListToString;
import static com.ptc.services.utilities.docgen.IntegrityUtils.convertStringToList;
import static com.ptc.services.utilities.docgen.IntegrityUtils.fixPickValue;
import static com.ptc.services.utilities.docgen.IntegrityUtils.getStringFieldValue;
import com.ptc.services.utilities.docgen.field.AllowedTypes;
import com.ptc.services.utilities.docgen.field.PickField;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import com.ptc.services.utilities.docgen.utils.StringObj;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

/**
 * Object represents an Integrity Field The following attributes are supported:
 * id type name allowedTypes associatedField backedBy backingFilter
 * backingStates backingTextField backingTextFormat backingType computation
 * correlation cycleDetection default defaultAttachmentField defaultBrowseQuery
 * defaultColumns description displayAsLink displayAsProgress displayLocation
 * displayName displayPattern displayRows displayStyle editabilityRule isForward
 * isMultiValued isSystemManaged isTestResult linkFlags loggingText max
 * maxLength min pairedField paramSubstitution phases picks query ranges
 * relevanceRule richContent showDateTime staticComputation
 * storeToHistoryFrequency suggestions textindex trace
 */
public class IntegrityField extends IntegrityObject {

   public static final String XML_PREFIX = "FIELD_";
   // Integrity's Platform Fields
   public static final int ITEM_SIGNIFICANT_EDIT_DATE_ON_SHARED_ITEM = -45;
   public static final int SIGNIFICANT_CHANGE_SINCE_ITEM_REVISION = -44;
   public static final int SIGNIFICANT_EDIT_DATE = -43;
   public static final int REVISION_INCREMENT_DATE = -42;
   public static final int REVISION = -41;
   public static final int REFERENCED_BOOKMARKS = -40;
   public static final int BOOKMARKS = -39;
   public static final int TESTS_AS_OF_DATE = -38;
   public static final int TEST_STEPS = -37;
   public static final int SHARED_BY = -36;
   public static final int SHARES = -35;
   public static final int PARAMETER_VALUES = -34;
   public static final int PARAMETERS = -33;
   public static final int CATEGORY = -32;
   public static final int SHARED_CATEGORY = -31;
   public static final int TEST_CASES = -30;
   public static final int SHARED_TEST_STEPS = -29;
   public static final int TESTS_FOR = -28;
   public static final int TESTS = -27;
   public static final int REFERENCED_ITEM_TYPE = -26;
   public static final int SUBSEGMENT_NAME = -25;
   public static final int DOCUMENT_ID = -24;
   public static final int INPUT_REVISION_DATE = -23;
   public static final int ROOT_ID = -22;
   public static final int REFERENCE_MODE = -21;
   public static final int REFERENCED_BY = -20;
   public static final int REFERENCES = -19;
   public static final int CONTAINED_BY = -18;
   public static final int CONTAINS = -17;
   public static final int ATTACHMENTS = -16;
   public static final int SIGNATURE_COMMENT = -15;
   public static final int SIGNED_BY = -14;
   public static final int BACKWARD_RELATIONSHIPS = -13;
   public static final int FORWARD_RELATIONSHIPS = -12;
   public static final int TYPE = -11;
   public static final int SUMMARY = -10;
   public static final int STATE = -9;
   public static final int PROJECT = -8;
   public static final int ASSIGNED_USER = -7;
   public static final int ASSIGNED_GROUP = -6;
   public static final int ID = -5;
   public static final int MODIFIED_BY = -4;
   public static final int CREATED_BY = -3;
   public static final int MODIFIED_DATE = -2;
   public static final int CREATED_DATE = -1;

   private WorkItem wi;
   private String displayName;
   private String globalDescription;
   private String pairedField;
   private Boolean isMultiValued;
   private Boolean isPlatformField;
   private Boolean isForward;
   private List<String> allowedTypes; // NOTE:  This is calculated in context of the requested type
   private List<String> visibleGroups;
   private LinkedHashMap<String, Field> fieldDetailsHash;

   // Context of the Integrity Type
   private String iTypeName;
   private String xmlTypeName;

   @Override
   protected String getPosition() {
      return id;
   }

   @Override
   protected String getFieldValue(String fieldName) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   public enum FieldType {

      ID {
                 public String toString() {
                    return "id";
                 }
              },
      PROJECT {
                 public String toString() {
                    return "project";
                 }
              },
      INT {
                 public String toString() {
                    return "integer";
                 }
              },
      PICK {
                 public String toString() {
                    return "pick";
                 }
              },
      FLOAT {
                 public String toString() {
                    return "float";
                 }
              },
      LOGICAL {
                 public String toString() {
                    return "logical";
                 }
              },
      DATE {
                 public String toString() {
                    return "date";
                 }
              },
      SHORTTEXT {
                 public String toString() {
                    return "shorttext";
                 }
              },
      LONGTEXT {
                 public String toString() {
                    return "longtext";
                 }
              },
      USER {
                 public String toString() {
                    return "user";
                 }
              },
      GROUP {
                 public String toString() {
                    return "group";
                 }
              },
      RELATIONSHIP {
                 public String toString() {
                    return "relationship";
                 }
              },
      SIPROJECT {
                 public String toString() {
                    return "siproject";
                 }
              },
      RANGE {
                 public String toString() {
                    return "range";
                 }
              },
      PHASE {
                 public String toString() {
                    return "phase";
                 }
              },
      QBR {
                 public String toString() {
                    return "qbr";
                 }
              },
      IBPL {
                 public String toString() {
                    return "ibpl";
                 }
              },
      FVA {
                 public String toString() {
                    return "fva";
                 }
              },
      ATTACHMENT {
                 public String toString() {
                    return "attachment";
                 }
              },
      SOURCETRACE {
                 public String toString() {
                    return "sourcetrace";
                 }
              },
      TYPE {
                 public String toString() {
                    return "type";
                 }
              },
      STATE {
                 public String toString() {
                    return "state";
                 }
              },
      UNDEFINED {
                 public String toString() {
                    return "undefined";
                 }
              }
   }

   public static final FieldType stringToEnum(String fieldType) {
      for (FieldType ft : FieldType.values()) {
         if (fieldType.equalsIgnoreCase(ft.toString())) {
            return ft;
         }
      }
      return FieldType.UNDEFINED;
   }

   public static final List<FieldType> COMPUTED_FIELDS = new ArrayList<>();

   static {
      COMPUTED_FIELDS.add(FieldType.INT);
      COMPUTED_FIELDS.add(FieldType.FLOAT);
      COMPUTED_FIELDS.add(FieldType.LOGICAL);
      COMPUTED_FIELDS.add(FieldType.DATE);
      COMPUTED_FIELDS.add(FieldType.SHORTTEXT);
   }

   IntegrityField(WorkItem wi) {
      super(wi, Types.Field);
      // modelType = IMModelTypeName.FIELD;
      fieldDetailsHash = new LinkedHashMap<>();
      iTypeName = "";
      id = wi.getField("id").getValueAsString();
      isPlatformField = Integer.parseInt(id) < 0;
      type = wi.getField("type").getValueAsString();
      name = wi.getField("name").getValueAsString();
      xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
      xmlTypeName = XMLWriter.padXMLParamName(IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(iTypeName));
      displayName = wi.getField("displayName").getValueAsString();
      globalDescription = wi.getField("description").getValueAsString();

      visibleGroups = new ArrayList<>();
      try {
         allowedTypes = parseAllowedTypes(wi.getField("allowedTypes"));
      } catch (NoSuchElementException ex) {
         allowedTypes = new ArrayList<>();
      }
      try {
         pairedField = wi.getField("pairedField").getString();
      } catch (NoSuchElementException ex) {
         pairedField = "";
      }
      try {
         isMultiValued = wi.getField("isMultiValued").getBoolean();
      } catch (NoSuchElementException ex) {
         isMultiValued = false;
      }
      try {
         isForward = wi.getField("isForward").getBoolean();
      } catch (NoSuchElementException ex) {
         isForward = false;
      }

      // Initialize the fieldDetailsHash with the information from the Work Item
      for (Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
         Field field = fit.next();
         fieldDetailsHash.put(field.getName(), field);
      }
      objectType = Types.Field;
   }

   IntegrityField(String typeName, WorkItem wi) throws APIException {
      super(wi, Types.Field);
      // modelType = IMModelTypeName.FIELD;
      fieldDetailsHash = new LinkedHashMap<>();
      iTypeName = (null == typeName ? "" : typeName);

      if (typeName == null || typeName.isEmpty()) {
         throw new APIException("Bad.");
      }

      id = wi.getField("id").getValueAsString();
      isPlatformField = Integer.parseInt(id) < 0;
      type = wi.getField("type").getValueAsString();
      name = wi.getField("name").getValueAsString();
      xmlParamName = XMLWriter.padXMLParamName(XML_PREFIX + XMLWriter.getXMLParamName(name));
      xmlTypeName = XMLWriter.padXMLParamName(IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(iTypeName));
      displayName = wi.getField("displayName").getValueAsString();
      globalDescription = wi.getField("description").getValueAsString();
      allowedTypes = parseAllowedTypes(wi.getField("allowedTypes"));
      visibleGroups = new ArrayList<>();

      try {
         pairedField = wi.getField("pairedField").getString();
      } catch (NoSuchElementException ex) {
         pairedField = "";
      }
      try {
         isMultiValued = wi.getField("isMultiValued").getBoolean();
      } catch (NoSuchElementException ex) {
         isMultiValued = false;
      }
      try {
         isForward = wi.getField("isForward").getBoolean();
      } catch (NoSuchElementException ex) {
         isForward = false;
      }

      // Initialize the fieldDetailsHash with the information from the Work Item
      for (Iterator<Field> fit = wi.getFields(); fit.hasNext();) {
         Field field = fit.next();
         fieldDetailsHash.put(field.getName(), field);
      }
      objectType = Types.Field;
   }

   // typeClass
   @Override
   public String getTypeClassGroup() {
      return (type.substring(0, 1).toUpperCase() + type.substring(1)).replaceAll("([A-Z])", " $1");
   }

//    public Field getField(String fieldName) {
//        return wi.getField(fieldName);
//    }
   @Override
   public String getDetails() {
      StringObj sb = new StringObj();
      // Print out the detail about each item type
      sb.append("<table class='display'>");

      for (String fieldName : fieldDetailsHash.keySet()) {
         Field field = fieldDetailsHash.get(fieldName);
         if (field.getValue() != null) {

            // if (field. != null && field.getModelType().endsWith("List")) {
            if (field.getName().equals("allowedTypes")) {
               AllowedTypes pf = new AllowedTypes(field);
               sb.addFieldValue(fieldName, pf.getFormattedReport());
            } else if (field.getName().equals("picks")) {
               // log ("ITEM LIST found: " + field.getList());

               PickField pf = new PickField(field);
               sb.addFieldValue(fieldName, pf.getFormattedReport());
            } else {
               sb.addFieldValue(fieldName, field.getValueAsString());
            }

         }
      }
      sb.append("     </table>");

      return sb.toString();
   }

   // Special function to parse the list of relationships
   private List<String> parseAllowedTypes(Field allowedTypesFld) {
      List<String> allowedTypesList = new ArrayList<>();
      if (type.equalsIgnoreCase("relationship") && null != allowedTypesFld.getList()) {
         // Get the allowed types list
         @SuppressWarnings("unchecked")
         List<Item> relTypesList = allowedTypesFld.getList();
         for (Item relation : relTypesList) {
            String fromType = relation.getField("from").getValueAsString();
            // We're only interested in the relationships from this type
            if (iTypeName.isEmpty() || iTypeName.equals(fromType)) {
               @SuppressWarnings("unchecked")
               List<Item> toTypesList = relation.getField("to").getList();
               for (Item toType : toTypesList) {
                  // Only add the (to) types that we haven't already encountered for this relationship
                  if (!allowedTypesList.contains(toType.getId())) {
                     allowedTypesList.add(toType.getId());
                  }
               }
            }
         }
      }

      return allowedTypesList;
   }

   // Special parsing for pick values
   @SuppressWarnings("unchecked")
   private List<String> parsePickValues(Field picks) {
      List<String> pickListValues = new ArrayList<>();
      if (null != picks && null != picks.getDataType() && picks.getDataType().equals(Field.ITEM_LIST_TYPE)) {
         if (null != picks.getList()) {
            List<Item> pickList = picks.getList();
            for (Item curPickItem : pickList) {
               String pickLabel = curPickItem.getField("label").getValueAsString();
               boolean isActive = curPickItem.getField("active").getBoolean();
               // Only report on the active pick values
               if (isActive) {
                  pickListValues.add(pickLabel);
               }
            }
         }
      }
      return pickListValues;
   }

   public void setFieldAttribute(String fieldName, Field fieldValue) {
      fieldDetailsHash.put(fieldName, fieldValue);
   }

   public void setVisibleGroups(List<String> groupsList) {
      visibleGroups = groupsList;
   }

   public Field getIField(String fieldName) {
      return wi.getField(id);
   }

   public String getPairedField() {
      return this.pairedField;
   }

   public Boolean getIsMultiValued() {
      return isMultiValued;
   }

   public Boolean getIsForward() {
      return this.isForward;
   }

   public String getFieldSummary() {
      String computation = getAttributeAsString("computation");

      if (FieldType.PICK.equals(type)) {
         List<String> picks = parsePickValues(getAttribute("picks"));
         return "<b>Pick List Values:</b><br/>" + convertListToString(picks, "<br/>" + nl);
      } else if (null != computation && computation.length() > 0) {
         String computationSummary;
         String staticComputation = getAttributeAsString("staticComputation");
         String storeToHistoryFrequency = getAttributeAsString("storeToHistoryFrequency");

         if (staticComputation.equals("true")) {
            computationSummary = "<b>Static Computation Definition ("
                    + storeToHistoryFrequency + "):</b><br/>" + nl;
            computationSummary += computation + nl;
         } else {
            computationSummary = "<b>Dynamic Computation Definition ("
                    + storeToHistoryFrequency + "):</b><br/>" + nl;
            computationSummary += computation + nl;

         }
         return computationSummary;
      } else if (FieldType.RELATIONSHIP.equals(type) && getDefaultColumns().size() > 0) {
         return "<b>Columns:</b><br/>" + nl + convertListToString(getDefaultColumns(), "<br/>" + nl);
      } else {
         return "&nbsp;";
      }
   }

   public Element getOverridesXML(Document job, Element command) {
      // Setup the command to re-create the field via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.IM));
      command.appendChild(app);

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(job.createTextNode("editfield"));
      command.appendChild(cmdName);

      // Only process the possible overridden values
      command.appendChild(XMLWriter.getOption(job, "overrideForType", xmlTypeName));

      //	--default=value  Default value
      Field defaultValue = getAttribute("default");
      if (null != defaultValue && null != defaultValue.getValue()) {
         command.appendChild(XMLWriter.getOption(job, "default", IntegrityUtils.getFieldValue(defaultValue, ",")));
      }

      //	--defaultColumns=value  where value=field1,field2,...,mks:virtualField1,mks:virtualField2... are default columns for relationship or qbr fields
      Field defaultColumns = getAttribute("defaultColumns");
      if (null != defaultColumns && null != defaultColumns.getValue()) {
         command.appendChild(XMLWriter.getOption(job, "defaultColumns", Integrity.getXMLParamFieldValue(defaultValue, IntegrityField.XML_PREFIX, ",")));
      }

      //	--editabilityRule=See documentation.  Field editability rule.
      String editabilityRule = getAttributeAsString("editabilityRule");
      if (editabilityRule.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "editabilityRule", editabilityRule));
      }

      //	--relevanceRule=See documentation.  Field relevance rule.
      String relevanceRule = getAttributeAsString("relevanceRule");
      if (relevanceRule.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "relevanceRule", relevanceRule));
      }

      //	--description=value  Short description
      String description = getAttributeAsString("description");
      if (description.length() > 0) {
         command.appendChild(XMLWriter.getOption(job, "description", description));
      }

      // Finally add the selection for the edit field command
      Element selection = job.createElement("selection");
      selection.appendChild(job.createTextNode(xmlParamName));
      command.appendChild(selection);

      return command;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Element getXML(Document job, Element command) {
      // Add this field to the global resources hash
      XMLWriter.paramsHash.put(XML_PREFIX + XMLWriter.getXMLParamName(name), name);

      // Setup the command to re-create the field via the Load Test Harness...
      Element app = job.createElement("app");
      app.appendChild(job.createTextNode(Command.IM));
      command.appendChild(app);

      Element cmdName = job.createElement("commandName");
      cmdName.appendChild(isPlatformField ? job.createTextNode("editfield") : job.createTextNode("createfield"));
      command.appendChild(cmdName);

      // Lets go through the list of field attributes
      for (String strAttribute : Integrity.fieldAttributes) {
         Field fldAttribute = getAttribute(strAttribute);
         if (null != fldAttribute && null != fldAttribute.getDataType()) {
                // Currently there is no support for the following in the API (i.e. to read the values):
            // TODO: --addLinkFlags=name=value,displayChar=char,onImage=path,enabled=[true|false],suspect=[true|false];...
            // TODO: --[no]cycleDetection  Enforce directional relationships.
            // TODO: --displayStyle=value  Where 'value' is one of the following display styles (csv,table)
            // TODO: --[no]showTallRows  Specify whether the relationship field should display variable height rows.		
            // TODO: --[no]trace  Set relationship field as a trace

            if (strAttribute.equalsIgnoreCase("id") || // Can't really change the ID of a field
                    strAttribute.equalsIgnoreCase("isSystemManaged") || // Not something we need to export
                    strAttribute.equalsIgnoreCase("position") || // Not going to attempt to change the position either						
                    strAttribute.equalsIgnoreCase("backingTextField") || // This is now deprecated - should be using backingIBPLTextFormat
                    strAttribute.equalsIgnoreCase("linkFlags") || // API doesn't provide a value for this field!
                    strAttribute.equalsIgnoreCase("cycleDetection") || // API doesn't provide a value for this field!
                    strAttribute.equalsIgnoreCase("displayStyle") || // API doesn't provide a value for this field!
                    strAttribute.equalsIgnoreCase("trace") || // API doesn't provide a value for this field!						
                    strAttribute.equalsIgnoreCase("type") || // We will process this attribute in the end
                    strAttribute.equalsIgnoreCase("name") || // This attribute will be processes in the end						
                    strAttribute.equalsIgnoreCase("default") || // This attribute will be processes in the overrides
                    strAttribute.equalsIgnoreCase("defaultColumns") || // This attribute will be processed in the overrides
                    strAttribute.equalsIgnoreCase("editabilityRule") || // This attribute will be processes in the overrides
                    strAttribute.equalsIgnoreCase("relevanceRule") || // This attribute will be processes in the overrides						
                    strAttribute.equalsIgnoreCase("references") // Not something that we need to export
                    ) {
               // Ignore...
            } // Make the display name the same as the name
            else if (strAttribute.equalsIgnoreCase("displayName")) {
               command.appendChild(XMLWriter.getOption(job, strAttribute, xmlParamName));
            } // Only export computation if it makes sense
            else if (strAttribute.equalsIgnoreCase("computation")) {
               if (COMPUTED_FIELDS.contains(getFieldType())) {
                  String strAttributeValue = getAttributeAsString(strAttribute);
                  if (strAttributeValue.length() > 0) {
                     command.appendChild(XMLWriter.getOption(job, strAttribute, strAttributeValue));
                  }
               }
            } else if (strAttribute.equalsIgnoreCase("correlation")) {
               command.appendChild(XMLWriter.getOption(job, strAttribute, Integrity.getXMLParamFieldValue(fldAttribute, IntegrityField.XML_PREFIX, ":")));
            } else if (strAttribute.equalsIgnoreCase("description")) {
               command.appendChild(XMLWriter.getOption(job, strAttribute, globalDescription));
            } else if (strAttribute.equalsIgnoreCase("phases")) {
               StringBuilder sb = new StringBuilder();
               List<Item> phaseList = fldAttribute.getList();
               int phaseCount = 0;
               for (Item phase : phaseList) {
                  // Ignore the 'Out of Phase' phase
                  if (!phase.getId().equalsIgnoreCase("Out of Phase")) {
                     sb.append(phaseCount > 0 ? ";" + nl + "\t\t\t" : "");
                     sb.append(phase.getField("label").getValueAsString() + ":");
                     sb.append(Integrity.getXMLParamFieldValue(phase.getField("states").getList(), IntegrityState.XML_PREFIX, ","));
                     phaseCount++;
                  }
               }
               command.appendChild(XMLWriter.getOption(job, strAttribute, sb.toString()));
            } else if (strAttribute.equalsIgnoreCase("ranges")) {
               StringBuilder sb = new StringBuilder();
               List<Item> phaseList = fldAttribute.getList();
               for (Iterator<Item> it = phaseList.iterator(); it.hasNext();) {
                  Item phase = it.next();
                  sb.append(phase.getField("label").getValueAsString());
                  sb.append(":" + phase.getField("lowerValue").getValueAsString());
                  sb.append(";" + phase.getField("upperValue").getValueAsString());
                  sb.append(it.hasNext() ? "," + nl + "\t\t\t" : "");
               }
               command.appendChild(XMLWriter.getOption(job, strAttribute, sb.toString()));
            } else if (strAttribute.equalsIgnoreCase("isTestResult") && getAttributeAsString(strAttribute).equals("true")) {
               command.appendChild(XMLWriter.getOption(job, "testResult", null));
            } else if (strAttribute.equalsIgnoreCase("allowedTypes")) {

               command.appendChild(XMLWriter.getOption(job, strAttribute, xmlTypeName + ":" + Integrity.getXMLParamFieldValue(allowedTypes, IntegrityType.XML_PREFIX, ",")));
            } else if (strAttribute.equalsIgnoreCase("backedBy")) {
               String strAttributeValue = getStringFieldValue(fldAttribute);
               command.appendChild(XMLWriter.getOption(job, strAttribute,
                       Integrity.getXMLParamFieldValue(convertStringToList(strAttributeValue, "\\."), IntegrityField.XML_PREFIX, ".")));
            } else if (strAttribute.equalsIgnoreCase("backingTextFormat")) {
               String strAttributeValue = Integrity.getXMLParamFieldValue(getAttributeAsString(strAttribute));
               if (strAttributeValue.length() > 0) {
                  command.appendChild(XMLWriter.getOption(job, "backingIBPLTextFormat", strAttributeValue));
               }
            } else if (strAttribute.equalsIgnoreCase("backingStates")) {
               String backingStates = getStringFieldValue(fldAttribute);
               command.appendChild(XMLWriter.getOption(job, "backingStates", Integrity.getXMLParamFieldValue(convertStringToList(backingStates, ","), IntegrityState.XML_PREFIX, ",")));
            } else if (strAttribute.equalsIgnoreCase("backingType")) {
               String backingType = getStringFieldValue(fldAttribute);
               String xmlBackingType = IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(backingType);
               XMLWriter.paramsHash.put(xmlBackingType, backingType);
               command.appendChild(XMLWriter.getOption(job, "backingType", XMLWriter.padXMLParamName(xmlBackingType)));
            } else if (strAttribute.equalsIgnoreCase("isMultiValued") && getAttributeAsString(strAttribute).equals("true")) {
               command.appendChild(XMLWriter.getOption(job, "multiValued", null));
            } else if (strAttribute.equalsIgnoreCase("loggingText") && getAttributeAsString(strAttribute).equals("true")) {
               command.appendChild(XMLWriter.getOption(job, strAttribute, "mostRecentFirst"));
            } else if (strAttribute.equalsIgnoreCase("picks")) {
               if (null != fldAttribute.getList() && FieldType.PICK == getFieldType()) {
                  StringBuilder sb = new StringBuilder();
                  List<Item> pickList = fldAttribute.getList();
                  for (Iterator<Item> it = pickList.iterator(); it.hasNext();) {
                     Item curPickItem = it.next();
                     boolean isActive = curPickItem.getField("active").getBoolean();
                     // Only report on the active pick values
                     if (isActive) {
                        String pickLabel = fixPickValue(getStringFieldValue(curPickItem.getField("label")));
                        String pickValue = getStringFieldValue(curPickItem.getField("value"));
                        sb.append(pickLabel).append(":").append(pickValue);
                        sb.append(it.hasNext() ? "," + nl + "\t\t\t" : "");
                     }
                  }
                  command.appendChild(XMLWriter.getOption(job, strAttribute, sb.toString()));
               }
            } else if (strAttribute.equalsIgnoreCase("paramSubstitution") && getAttributeAsString(strAttribute).equals("true")) {
               command.appendChild(XMLWriter.getOption(job, strAttribute, "substituteParams"));
            } else if (strAttribute.equalsIgnoreCase("query") && getAttributeAsString(strAttribute).length() > 0) {
               String queryName = IntegrityQuery.XML_PREFIX + XMLWriter.getXMLParamName(getAttributeAsString(strAttribute));
               XMLWriter.paramsHash.put(queryName, getAttributeAsString(strAttribute));
               command.appendChild(XMLWriter.getOption(job, "query", XMLWriter.padXMLParamName(queryName)));
            } else if (strAttribute.equalsIgnoreCase("suggestions") && null != fldAttribute.getList()) {
               command.appendChild(XMLWriter.getOption(job, strAttribute, convertListToString(fldAttribute.getList(), ",")));
            } else if (fldAttribute.getDataType().equals(Field.BOOLEAN_TYPE) && null != fldAttribute.getBoolean()) {
                    // Generic case for boolean single option flags, example:
               // 	--displayAsLink
               // 	--displayAsProgress
               //	--richContent
               //	--showDateTime
               //	--staticComputation
               //	--textIndex
               if (fldAttribute.getBoolean()) {
                  command.appendChild(XMLWriter.getOption(job, strAttribute, null));
               }
            } else if (fldAttribute.getDataType().equals(Field.ITEM_TYPE) && null != fldAttribute.getItem()) {
               // Generic case for option name = value (using Item.getId())					
               command.appendChild(XMLWriter.getOption(job, strAttribute, fldAttribute.getItem().getId()));
            } else if (fldAttribute.getDataType().equals(Field.INTEGER_TYPE)
                    || fldAttribute.getDataType().equals(Field.DATE_TYPE)
                    || fldAttribute.getDataType().equals(Field.DOUBLE_TYPE)
                    || fldAttribute.getDataType().equals(Field.FLOAT_TYPE)
                    || fldAttribute.getDataType().equals(Field.LONG_TYPE)
                    || fldAttribute.getDataType().equals(Field.STRING_TYPE)) {
               // Generic case for option name = value (using Field.getValueAsString())					
               String strAttributeValue = getAttributeAsString(strAttribute);
               if (strAttributeValue.length() > 0) {
                  command.appendChild(XMLWriter.getOption(job, strAttribute, strAttributeValue));
               }
            } else {
               log("Ignoring attribute: " + strAttribute);
            }
         }
      }

      // Determine if the name needs to be an option or selection
      if (isPlatformField) {
         Element selection = job.createElement("selection");
         selection.appendChild(job.createTextNode(xmlParamName));
         command.appendChild(selection);
      } else {
         // Add the 'type' only for the create field command
         command.appendChild(XMLWriter.getOption(job, "type", type));
         command.appendChild(XMLWriter.getOption(job, "name", xmlParamName));
      }

      return command;
   }

   // Setup all the return functions
   public String getId() {
      return id;
   }

   public FieldType getFieldType() {
      return stringToEnum(type);
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getXMLName() {
      return xmlParamName;
   }

   public String getDisplayName() {
      return displayName;
   }

   public List<String> getAllowedTypes() {
      return allowedTypes;
   }

   public List<String> getVisibleGroups() {
      return visibleGroups;
   }

   // Generic field attribute getter function
   public Field getAttribute(String attributeName) {
      return fieldDetailsHash.get(attributeName);
   }

   public String getAttributeAsString(String attributeName) {
      return getStringFieldValue(fieldDetailsHash.get(attributeName));
   }

   // Possible overridden values
   @Override
   public String getDescription() {
      return getAttributeAsString("description");
   }

   public String getDefaultValue() {
      return getAttributeAsString("default");
   }

   public String getEditabilityRule() {
      return getAttributeAsString("editabilityRule");
   }

   public String getRelevanceRule() {
      return getAttributeAsString("relevanceRule");
   }

   // Special handling for the default columns field
   @SuppressWarnings("unchecked")
   public List<String> getDefaultColumns() {
      List<String> defaultColumnsList = new ArrayList<>();
      Field defaultColumns = fieldDetailsHash.get("defaultColumns");
      if (null != defaultColumns && null != defaultColumns.getList()) {
         return defaultColumns.getList();
      }
      return defaultColumnsList;
   }
}
