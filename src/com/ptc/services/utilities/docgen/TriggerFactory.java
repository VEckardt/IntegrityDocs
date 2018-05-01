package com.ptc.services.utilities.docgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.LinkedHashMap;

public class TriggerFactory {

    public static final String summarizeItemList(LinkedHashMap<String, IntegrityField> fieldsHash, Field itemList, String delim, boolean forXML) {
        StringBuilder sb = new StringBuilder();
        if (null != itemList && null != itemList.getList()) {
            String fieldName = itemList.getName();

            @SuppressWarnings("unchecked")
            List<Item> paramList = itemList.getList();
            for (Iterator<Item> pit = paramList.iterator(); pit.hasNext();) {
                Item param = pit.next();
                String assignmentField = Integrity.getStringFieldValue(param.getField("field"));
                if (fieldName.equalsIgnoreCase("assign") && forXML) {
                    sb.append(XMLWriter.padXMLParamName(IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(assignmentField)));
                } else {
                    sb.append(assignmentField);
                }

                sb.append("=");
                String value = Integrity.getStringFieldValue(param.getField("value"));
                if (forXML) {
                    sb.append(fieldName.equalsIgnoreCase("assign") ? Integrity.getXMLParamFieldValue(fieldsHash.get(assignmentField), value) : Integrity.fixTriggerValue(value));
                } else {
                    sb.append(fieldName.equalsIgnoreCase("assign") ? value : value.replaceAll("<%", "&lt;%").replaceAll("%>", "%&gt;"));
                }
                sb.append(pit.hasNext() ? delim : "");
            }
        }
        return sb.toString();
    }

    public static List<Trigger> parseTriggers(LinkedHashMap<String, IntegrityField> fieldsHash, WorkItemIterator wii, boolean forXML) throws APIException {
        List<Trigger> iTriggersList = new ArrayList<>();
        // Ensure we have a list of triggers to work with...
        if (null != wii && wii.hasNext()) {
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                // Create a new trigger object for this trigger
                Trigger iTrigger = new Trigger();
                iTrigger.setName(wi.getField("name").getValueAsString());
                log("Processing Trigger: " + iTrigger.getName());
                iTrigger.setPosition(Integrity.getStringFieldValue(wi.getField("position")));
                iTrigger.setDescription(Integrity.getStringFieldValue(wi.getField("description")));
                iTrigger.setType(Integrity.getStringFieldValue(wi.getField("type")));
                iTrigger.setScript(Integrity.getStringFieldValue(wi.getField("script")));
                iTrigger.setScriptTiming(Integrity.getStringFieldValue(wi.getField("scriptTiming")));
                if (forXML) {
                    iTrigger.setScriptParams(summarizeItemList(fieldsHash, wi.getField("scriptParams"), ";" + IntegrityDocs.nl + "\t\t\t", forXML));
                    iTrigger.setAssignments(summarizeItemList(fieldsHash, wi.getField("assign"), ";" + IntegrityDocs.nl + "\t\t\t", forXML));
                } else {
                    iTrigger.setScriptParams(summarizeItemList(fieldsHash, wi.getField("scriptParams"), "<br/>" + IntegrityDocs.nl, false));
                    iTrigger.setAssignments(summarizeItemList(fieldsHash, wi.getField("assign"), "<br/>" + IntegrityDocs.nl, false));
                }

                // Depending on rule v/s scheduled, get additional information
                if (iTrigger.getType().equalsIgnoreCase("rule")) {
                    iTrigger.setRule(Integrity.getStringFieldValue(wi.getField("rule")));
                } else if (iTrigger.getType().equalsIgnoreCase("scheduled")) {
                    iTrigger.setFrequency(Integrity.getStringFieldValue(wi.getField("frequency")));
                    iTrigger.setRunAs(Integrity.getFieldValue(wi.getField("runAs"), ""));
                    iTrigger.setQuery(Integrity.getFieldValue(wi.getField("query"), ""));
                }

                iTriggersList.add(iTrigger);
            }
        }

        return iTriggersList;
    }
}
