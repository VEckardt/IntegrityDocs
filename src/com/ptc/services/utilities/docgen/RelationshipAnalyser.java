/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.APIException;
import com.mks.api.response.Item;
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.IntegrityDocs.getList;
import com.ptc.services.utilities.docgen.relationships.IntegrityException;
import com.ptc.services.utilities.docgen.relationships.IntegrityTypeProperty;
import com.ptc.services.utilities.docgen.relationships.IntegrityTypePropertyList;
import com.ptc.services.utilities.docgen.utils.ApplicationProperties;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author veckardt
 */
public class RelationshipAnalyser {

    // Globals
    String header1 = "From Type;From Category;Relationship Name;To Type;To Category;Trace;Forward;Field Type;Related Field;n:m;Description";
    String header2 = "From Type;From Category;Trace Name;To Type;To Category;Trace;Forward;Field Type;Related Field;n:m;Description";
    //
    // private IntegrityCommands ic = null;
    private ApplicationProperties props = null;
    // Global Variables
    private final String prefix;
    private final String solutionTypeName;
    private final Integrity session;

    public RelationshipAnalyser(Integrity session, String newSolutionTypeName) throws APIException, IntegrityException {
        this.session = session;
        // not needed:  props = new ApplicationProperties(RelationshipAnalyser.class);
        // not needed:  prefix = props.getProperty("TracePropId", "MKS.RQ.trace.");
        prefix = "MKS.RQ.trace.";
        // not needed:  solutionTypeName = props.getProperty("SolutionTypeName", newSolutionTypeName);
        solutionTypeName = newSolutionTypeName;
    }

    public Relationships analyse() throws APIException, IntegrityException {
        Relationships rels = new Relationships();

        int cnt = 0;
        log("Begin of Listing\n", 1);
        LinkedHashMap<String, IntegrityField> fields = session.getSysFields();

        log("Part 1: Integrity Relationships and Traces (Relationship Field Definitions)\n\n" + header1);
        for (IntegrityField field : fields.values()) {
            // ic.log(fromField.getId() + " ...", 1);
            if (!field.getAllowedTypes().isEmpty()) {
                for (Object object : field.getIField("allowedTypes").getList()) {
                    Item fromType = (Item) object;
                    for (String toType : fromType.getField("to").getValueAsString().split(",")) {

                        String trace = field.getIField("trace").getBoolean() ? "Y" : "-";
                        String isForward = field.getIField("isForward").getBoolean() ? "f" : "b";
                        String pairedField = field.getIField("pairedField").getValueAsString();
                        String type = field.getIField("type").getValueAsString();
                        String n2m = "";

                        IntegrityField ifld = fields.get(field.getId());
                        if (ifld != null) {
                            n2m = ifld.getIsMultiValued() ? "n:" : "1:";
                        }

                        IntegrityField ifld2 = fields.get(pairedField);
                        // ifld2.getIsMultiValued();
                        if (ifld2 != null && ifld2.getIsMultiValued() != null) {
                            n2m = n2m + (ifld2.getIsMultiValued() ? "m" : "1");
                        } else {
                            n2m = "";
                        }
                        cnt++;
                        SimpleItem item = new SimpleItem("CONTENT", Integer.toString(cnt));
                        item.add("ID", Integer.toString(cnt));
                        item.add("Type", "Relationship");

                        // ;Trace;Forward;Field Type;Related Field;n:m;Description";
                        item.add("From Type", fromType.getId());
                        item.add("From Category", "");
                        item.add("Relationship Name", field.getId());
                        item.add("To Type", toType);
                        item.add("To Category", "");
                        item.add("Trace", trace);
                        item.add("Forward", isForward);
                        item.add("Field Type", type);
                        item.add("Related Field", pairedField);
                        item.add("n:m", n2m);
                        item.add("Description", field.getIField("description").getString());
                        rels.add(item);

                        log(fromType.getId() + ";;" + field.getId() + ";" + toType + ";;" + trace + ";" + isForward + ";" + type + ";" + pairedField + ";" + n2m + ";" + field.getIField("description").getString());

                    }
                }
            }
            if (field.getIField("Type").getString().contentEquals("ibpl")) {
                // ic.log("Working in: "+errData,1);
                cnt++;
                String backingType = field.getIField("backingType").getString();
                String n2m = field.getIField("isMultiValued").getBoolean() ? "1:n" : "1:1";

                String fieldTypes = getFieldTypes(field.getId());

                if (fieldTypes.length() < 1) {
                    log("** undefined **" + ";;" + field.getId() + ";" + backingType + ";;" + "" + ";" + "f" + ";" + "ibpl" + ";" + "n/a" + ";" + n2m + ";" + field.getIField("description").getString());
                    SimpleItem item = new SimpleItem("CONTENT", Integer.toString(cnt));
                    item.add("ID", Integer.toString(cnt));
                    item.add("Type", "Relationship");
                    item.add("From Type", "** undefined **");
                    item.add("From Category", "");
                    item.add("Relationship Name", field.getId());
                    item.add("To Type", backingType);
                    item.add("To Category", "");
                    item.add("Trace", "");
                    item.add("Forward", "f");
                    item.add("Field Type", "ibpl");
                    item.add("Related Field", "n/a");
                    item.add("n:m", n2m);
                    item.add("Description", field.getIField("description").getString());
                    rels.add(item);

                } else {
                    for (String type : fieldTypes.split(",")) {
                        SimpleItem item = new SimpleItem("CONTENT", Integer.toString(cnt));
                        item.add("ID", Integer.toString(cnt));
                        item.add("Type", "Relationship");
                        item.add("From Type", type);
                        item.add("From Category", "");
                        item.add("Relationship Name", field.getId());
                        item.add("To Type", backingType);
                        item.add("To Category", "");
                        item.add("Trace", "");
                        item.add("Forward", "f");
                        item.add("Field Type", "ibpl");
                        item.add("Related Field", "n/a");
                        item.add("n:m", n2m);
                        item.add("Description", field.getIField("description").getString());
                        rels.add(item);

                        log(type + ";;" + field.getId() + ";" + backingType + ";;" + "" + ";" + "f" + ";" + "ibpl" + ";" + "n/a" + ";" + n2m + ";" + field.getIField("description").getString());
                        cnt++;
                    }
                }
            }
        }
        log(cnt + " rows.");
        log("\nEnd of Listing.", 1);
        return rels;
    }

    public Relationships analyseTraces(List<IntegrityAdminObject> fieldList) throws APIException, IntegrityException {
        Relationships rels = new Relationships();

        int cnt = 0;
        log("Begin of Listing\n", 1);
        // if (fieldList.isEmpty()) {
        LinkedHashMap<String, IntegrityField> fields = session.getFields();
        // } else {
// 
        // }

        IntegrityTypePropertyList tpl = new IntegrityTypePropertyList(session.getAPI(), solutionTypeName, prefix);
        cnt = 0;
        log("\nPart 2: Trace Definitions in MKS Solution Type\n\n" + header2);
        for (Map.Entry entry : tpl.entrySet()) {
            IntegrityTypeProperty prop = (IntegrityTypeProperty) entry.getValue();

            String key = prop.getName().replace(prefix + "<", "").replace(">", "");
            String fromType = splitProp(key, ",", 0);
            String fromCategory = splitProp(key, ",", 1);
            for (String value : prop.getValue().split(";")) {

                String traceName = splitProp(value, ":", 0);
                String typeCategory = splitProp(value, ":", 1);
                String toType = splitProp(typeCategory, ",", 0);
                String toCategory = splitProp(typeCategory, ",", 1);

                IntegrityField ifld = fields.get(traceName);
                String pairedField = ifld.getPairedField();
                String n2m = ifld.getIsMultiValued() ? "n:" : "1:";
                IntegrityField ifld2 = fields.get((pairedField));
                // ifld2.getIsMultiValued();
                if (ifld2.getIsMultiValued() != null) {
                    n2m = n2m + (ifld2.getIsMultiValued() ? "m" : "1");
                } else {
                    n2m = "";
                }
                cnt++;

                String fieldType = ifld.getType();
                // if (ifld.getIsForward()) //  || !cbNormalize.isSelected()) {

                String isForward = ifld.getIsForward() ? "f" : "b";
                if (ifld.getIsForward()) {
                    SimpleItem item = new SimpleItem("CONTENT", Integer.toString(cnt));
                    item.add("id", Integer.toString(cnt));
                    // item.add("type", "Trace");
                    item.add("From Type", fromType);
                    item.add("From Category", fromCategory);
                    item.add("Relationship Name", traceName);
                    item.add("To Type", toType);
                    item.add("To Category", toCategory);
                    // item.add("Trace", "Y");
                    item.add("Forward", isForward);
                    // item.add("Field Type", fieldType);
                    item.add("Related Field", pairedField);
                    item.add("n:m", n2m);
                    item.add("description", prop.getDescription());
                    rels.add(item);
                    log(fromType + ";" + fromCategory + ";" + traceName + ";" + toType + ";" + toCategory + ";" + "Y;" + isForward + ";" + fieldType + ";" + pairedField + ";" + n2m + ";" + prop.getDescription());
                } else {
                    if (!n2m.isEmpty()) {
                        if (n2m.contentEquals("1:m")) {
                            n2m = "m:1";
                        } else if (n2m.contentEquals("n:1")) {
                            n2m = "1:n";
                        }
                    }
                    SimpleItem item = new SimpleItem("CONTENT", Integer.toString(cnt));
                    item.add("id", Integer.toString(cnt));
                    // item.add("type", "Trace");
                    item.add("From Type", toType);
                    item.add("From Category", toCategory);
                    item.add("Relationship Name", pairedField);
                    item.add("To Type", fromType);
                    item.add("To Category", fromCategory);
                    // item.add("Trace", "Y");
                    item.add("Forward", isForward);
                    // item.add("Field Type", fieldType);
                    item.add("Related Field", traceName);
                    item.add("n:m", n2m);
                    item.add("description", prop.getDescription());
                    rels.add(item);
                    log(toType + ";" + toCategory + ";" + pairedField + ";" + fromType + ";" + fromCategory + ";" + "Y;" + isForward + ";" + fieldType + ";" + traceName + ";" + n2m + ";" + prop.getDescription());
                }
            }

        }
        log(cnt + " rows.");
        log("\nEnd of Listing.", 1);
        return rels;
    }

    /**
     * Get all Types where this field belongs to
     *
     * @param fieldName
     * @return
     */
    public String getFieldTypes(String fieldName) {

        String ret = "";
        for (IntegrityAdminObject type : getList(Types.Type)) {
            // WorkItem wi = entry.getValue();
            // ic.log("fieldName: " + fieldName+ ", entry:"+entry.getKey(), 1);
            if (type.getFieldValue("visibleFields") != null) {
                for (String visibleField : type.getFieldValue("visibleFields").split(",")) {
                    if (visibleField.contentEquals(fieldName)) {
                        ret = ret + (ret.isEmpty() ? "" : ",") + type.getName();
                        // break;
                    }
                }
            }
        }
        return ret;
    }

    public static String splitProp(String prop, String character, int num) {
        try {
            return removeChars(prop.split(character)[num]);

        } catch (ArrayIndexOutOfBoundsException ex) {
            return "";
        }
    }

    public static String removeChars(String text) {
        return text.replace("<", "").replace(">", "");
    }
}
