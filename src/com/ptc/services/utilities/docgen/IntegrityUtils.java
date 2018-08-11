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

import com.mks.api.Command;
import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.Constants.GROUP_XML_PREFIX;
import static com.ptc.services.utilities.docgen.Constants.USER_XML_PREFIX;
import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.IntegrityDocs.skipCreMoDetails;
import com.ptc.services.utilities.docgen.session.APISession;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import static com.ptc.services.utilities.docgen.utils.Logger.print;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class IntegrityUtils {

   private static APISession api = null;
   private static final LinkedHashMap<String, IntegrityField> sysFieldsHash = new LinkedHashMap<>();

   // Static objects for escaping pick values
   private static final CharSequence equals = new StringBuffer("=");
   private static final CharSequence equalsFix = new StringBuffer("\\=");
   private static final CharSequence colon = new StringBuffer(":");
   private static final CharSequence colonFix = new StringBuffer("\\:");
   private static final CharSequence semicolon = new StringBuffer(";");
   private static final CharSequence semicolonFix = new StringBuffer("\\;");
   private static final CharSequence comma = new StringBuffer(",");
   private static final CharSequence commaFix = new StringBuffer("\\,");

   IntegrityUtils(String app) throws APIException {
      IntegrityUtils.api = new APISession(app);
      readFields();
   }

   /**
    * Execute command
    *
    * @param cmd
    * @return
    * @throws APIException
    */
   public static Response execute(Command cmd) throws APIException {
      return api.runCommand(cmd);
   }

//   public static Response executeInterim(Command cmd) throws APIException {
//      return api.runCommandWithInterim(cmd);
//   }
   public static String getConnectionString() {
      return api.getConnectionString();
   }

   public static LinkedHashMap<String, IntegrityField> getSysFields() {
      return sysFieldsHash;
   }

   public static void addCreMoDetailsIfRequested(LinkedHashMap<String, Field> typeFields, WorkItem wi) {
      if (!skipCreMoDetails) {
         Field created = wi.getField("created");
         // print("\t... " + created.getName());
         typeFields.put(created.getName(), created);
         // log(" done.", 1);

         Field createdBy = wi.getField("createdBy");
         // print("\t... " + createdBy.getName());
         typeFields.put(createdBy.getName(), createdBy);
         // log(" done.", 1);

         Field lastModified = wi.getField("lastModified");
         // print("\t... " + lastModified.getName());
         typeFields.put(lastModified.getName(), lastModified);
         // log(" done.", 1);

         Field modifiedBy = wi.getField("modifiedBy");
         // print("\t... " + modifiedBy.getName());
         typeFields.put(modifiedBy.getName(), modifiedBy);
         // log(" done.", 1);
      }
   }

   /**
    * Get Fields
    *
    * @return
    * @throws APIException
    */
   private void readFields() throws APIException {
      // Initialize our return variable
      // LinkedHashMap<String, IntegrityField> fieldDetails = new LinkedHashMap<>();
      // Setup the im fields command to get the global definition of the field
      Command imFields = new Command(Command.IM, "fields");
      // Run the im fields command to get the global details on the field
      Response res = execute(imFields);
      // Parse the response for the initial pass
      if (null != res && null != res.getWorkItems()) {
         WorkItemIterator wii = res.getWorkItems();
         List<String> selectionList = new ArrayList<>();
         while (wii.hasNext()) {
            WorkItem wi = wii.next();
            selectionList.add(wi.getId());
         }
         Command imViewField = new Command(Command.IM, "viewfield");
         for (String fld : selectionList) {
            imViewField.addSelection(fld);
         }
         // imViewField.addSelection("Spawns");
         Response viewFieldRes = execute(imViewField);
         // ResponseUtil.printResponse(viewFieldRes, 1, System.out);
         // System.exit(1);
         WorkItemIterator wit = viewFieldRes.getWorkItems();
         while (wit.hasNext()) {
            WorkItem wi = wit.next();
            sysFieldsHash.put(wi.getId(), new IntegrityField(wi));
         }
      }
      log("Fields added: " + sysFieldsHash.size(), 1);
   }

   /**
    * Utility function that escapes an equals or semicolon within a string
    *
    * @param value String containing an equals or semicolon
    * @return
    */
   public static final String fixTriggerValue(String value) {
      return (null != value && value.length() > 0) ? value.replace(equals, equalsFix).replace(semicolon, semicolonFix) : value;
   }

   /**
    * Utility function that escapes a colon or comma within a Pick name/value
    *
    * @param value Pick name/value string
    * @return
    */
   public static final String fixPickValue(String value) {
      return (null != value && value.length() > 0) ? value.replace(colon, colonFix).replace(comma, commaFix) : value;
   }

   /**
    * Utility function that escapes a colon or semicolon within a Property
    * name/value
    *
    * @param value Property name/value string
    * @return
    */
   public static final String fixPropertyValue(String value) {
      return (null != value && value.length() > 0) ? value.replace(colon, colonFix).replace(semicolon, semicolonFix) : value;
   }

   public static final String convertListToString(List<String> list, String delim) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = list.iterator(); it.hasNext();) {
         sb.append(it.next());
         sb.append(it.hasNext() ? delim : "");
      }
      return sb.toString();
   }

   public static final List<String> convertStringToList(String str, String delim) {
      List<String> list = new ArrayList<>();
      if (null != str && str.length() > 0) {
         String[] tokens = str.split(delim);
         for (String token : tokens) {
            list.add(token.trim());
         }
      }
      return list;
   }

   public static final String getUserFullName(Item iUser) {
      Field fullName = iUser.getField("fullname");
      if (null != fullName && null != fullName.getValueAsString() && fullName.getValueAsString().length() > 0) {
         return fullName.getValueAsString();
      } else {
         return iUser.getId();
      }
   }

   public static final String getDateString(SimpleDateFormat sdf, Date fieldValue) {
      if (null == sdf) {
         return fieldValue.toString();
      } else {
         return sdf.format(fieldValue);
      }
   }

   public static final boolean getBooleanFieldValue(Field fld) {
      if (null != fld && null != fld.getDataType() && fld.getDataType().endsWith(Field.BOOLEAN_TYPE)) {
         return fld.getBoolean();
      }

      return false;
   }

   public static final String getStringFieldValue(Field fld) {
      if (null != fld && null != fld.getDataType()) {
         if (fld.getDataType().equals(Field.ITEM_TYPE) && null != fld.getItem()) {
            return fld.getItem().getId();
         } else {
            return null == fld.getValueAsString() ? "" : fld.getValueAsString();
         }
      } else {
         return "";
      }
   }

   public static final String summarizeItemList(LinkedHashMap<String, IntegrityField> fieldsHash, Field itemList, String delim, boolean forXML) {
      StringBuilder sb = new StringBuilder();
      if (null != itemList && null != itemList.getList()) {
         String fieldName = itemList.getName();

         @SuppressWarnings("unchecked")
         List<Item> paramList = itemList.getList();
         for (Iterator<Item> pit = paramList.iterator(); pit.hasNext();) {
            Item param = pit.next();
            String assignmentField = getStringFieldValue(param.getField("field"));
            if (fieldName.equalsIgnoreCase("assign") && forXML) {
               sb.append(XMLWriter.padXMLParamName(IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(assignmentField)));
            } else {
               sb.append(assignmentField);
            }

            sb.append("=");
            String value = getStringFieldValue(param.getField("value"));
            if (forXML) {
               sb.append(fieldName.equalsIgnoreCase("assign") ? getXMLParamFieldValue(fieldsHash.get(assignmentField), value) : fixTriggerValue(value));
            } else {
               sb.append(fieldName.equalsIgnoreCase("assign") ? value : value.replaceAll("<%", "&lt;%").replaceAll("%>", "%&gt;"));
            }
            sb.append(pit.hasNext() ? delim : "");
         }
      }
      return sb.toString();
   }

   public static final List<String> getListOfStrings(Field fld, String delim) {
      List<String> listOfStrings = new ArrayList<>();
      if (null != fld && null != fld.getDataType()) {
         // First determine the field type we're dealing with
         if (fld.getDataType().equals(Field.VALUE_LIST_TYPE)) {
            List<?> valuesList = fld.getList();
            for (Iterator<?> vlit = valuesList.iterator(); vlit.hasNext();) {
               listOfStrings.add(String.valueOf(vlit.next()));
            }
         } else if (fld.getDataType().equals(Field.ITEM_LIST_TYPE)) {
            @SuppressWarnings("unchecked")
            List<Item> valuesList = fld.getList();
            for (Iterator<Item> iit = valuesList.iterator(); iit.hasNext();) {
               listOfStrings.add(iit.next().getId());
            }
         } else if (fld.getDataType().equals(Field.ITEM_TYPE)) {
            listOfStrings.add(fld.getItem().getId());
         } else {
            listOfStrings.addAll(convertStringToList(getStringFieldValue(fld), delim));
         }
      }

      return listOfStrings;
   }

   public static final String getFieldValue(Field fld, String delimiter) {
      StringBuilder values = new StringBuilder();
      for (Iterator<?> vlit = getListOfStrings(fld, delimiter).iterator(); vlit.hasNext();) {
         values.append(String.valueOf(vlit.next()) + (vlit.hasNext() ? delimiter : ""));
      }
      return values.toString();
   }

   @SuppressWarnings("unchecked")
   public static final String summarizePermissionsList(Field itemList, String delim) {
      StringBuilder sb = new StringBuilder();
      if (null != itemList && null != itemList.getList()) {
         List<Item> principalList = itemList.getList();
         List<String> usersList = new ArrayList<>();
         List<String> groupsList = new ArrayList<>();

         // sb.append("principalList: " + principalList.size() + "IMModelTypeName.USER" + IMModelTypeName.USER);
         for (Item principal : principalList) {
            String modelType = principal.getModelType();
            if (modelType.equals(IMModelTypeName.USER) || modelType.equals("user")) {
               usersList.add(principal.getId());
            } else if (modelType.equals(IMModelTypeName.GROUP) || modelType.equals("group")) {
               groupsList.add(principal.getId());
            }
         }

         if (delim.equals("<br/>" + nl)) {
            sb.append(usersList.size() > 0 ? "Users:&nbsp;&nbsp;" + convertListToString(usersList, ", ") : "");
            sb.append(usersList.size() > 0 && groupsList.size() > 0 ? "<br/>" : "");
            sb.append(groupsList.size() > 0 ? "Groups:&nbsp;&nbsp;" + convertListToString(groupsList, ", ") : "");
         } else {
            sb.append(usersList.size() > 0 ? "u=" + getXMLParamFieldValue(usersList, USER_XML_PREFIX, ",") : "");
            sb.append(usersList.size() > 0 && groupsList.size() > 0 ? ";" : "");
            sb.append(groupsList.size() > 0 ? "g=" + getXMLParamFieldValue(groupsList, GROUP_XML_PREFIX, ",") : "");
         }
      }
      return sb.toString();
   }

   public static final String getXMLParamFieldValue(Field fld, String xmlPrefix, String delimiter) {
      StringBuilder values = new StringBuilder();
      for (Iterator<?> vlit = getListOfStrings(fld, delimiter).iterator(); vlit.hasNext();) {
         String val = String.valueOf(vlit.next());
         if (xmlPrefix.indexOf("FIELD") > 0 && val.indexOf("mks:") > 0) {
            values.append(val + (vlit.hasNext() ? delimiter : ""));
         } else {
            String xmlParam = xmlPrefix + XMLWriter.getXMLParamName(val);
            XMLWriter.paramsHash.put(xmlParam, val);
            values.append(XMLWriter.padXMLParamName(xmlParam) + (vlit.hasNext() ? delimiter : ""));
         }
      }
      return values.toString();
   }

   public static final String getXMLParamFieldValue(List<String> list, String xmlPrefix, String delimiter) {
      StringBuilder values = new StringBuilder();
      for (Iterator<String> vlit = list.iterator(); vlit.hasNext();) {
         String val = vlit.next();
         String xmlParam = xmlPrefix + XMLWriter.getXMLParamName(val);
         XMLWriter.paramsHash.put(xmlParam, val);
         values.append(XMLWriter.padXMLParamName(xmlParam) + (vlit.hasNext() ? delimiter : ""));
      }
      return values.toString();
   }

   public static String getXMLParamFieldValue(String textFormat) {
      // Only process if we've got something to resolve...
      if (null != textFormat && textFormat.contains("{")) {
         StringBuilder resolvedString = new StringBuilder();
         int startIndx = 0;
         int curIndx = textFormat.indexOf("{", startIndx);
         while (curIndx >= 0) {
            if (curIndx > 0) {
               resolvedString.append(textFormat.substring(startIndx, curIndx));
            }

            if (curIndx == (textFormat.length() - 1)) {
               resolvedString.append("{");
               startIndx = curIndx + 1;
            } else {
               int endIndx = textFormat.indexOf('}', curIndx);
               if (endIndx < 0) {
                  // Matching closing token not found, parse error!
                  break;
               }

               String rawFieldName = textFormat.substring(curIndx + 1, endIndx);
               String xmlParam = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(rawFieldName);
               XMLWriter.paramsHash.put(xmlParam, rawFieldName);
               resolvedString.append(XMLWriter.padXMLParamName(xmlParam));
               startIndx = endIndx + 1;
            }

            curIndx = textFormat.indexOf("{", startIndx);
         }

         if (startIndx < textFormat.length()) {
            resolvedString.append(textFormat.substring(startIndx));
         }

         return resolvedString.toString();
      } else {
         return textFormat;
      }
   }

   public static String getXMLParamFieldValue(IntegrityField iField, String iFieldValue) {
      StringBuilder sb = new StringBuilder();
      switch (iField.getFieldType()) {
         case TYPE:
            sb.append(XMLWriter.padXMLParamName(IntegrityType.XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
            break;

         case STATE:
            sb.append(XMLWriter.padXMLParamName(IntegrityState.XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
            break;

         case USER:
            sb.append(XMLWriter.padXMLParamName(USER_XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
            break;

         case GROUP:
            sb.append(XMLWriter.padXMLParamName(GROUP_XML_PREFIX + XMLWriter.getXMLParamName(iFieldValue)));
            break;

         default:
            sb.append(iFieldValue);
      }

      return sb.toString();
   }

   public static String getHostName() {
      return api.getHostName();
   }

   public String getPort() {
      return api.getPort();
   }

   public String getUserName() {
      return api.getUserName();
   }

   public void exit() {
      if (null != api) {
         try {
            api.Terminate();
         } catch (APIException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
