/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.2 $
 * Last changed:   $Date: 2017/05/12 00:20:04CEST $
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemList;
import com.mks.api.response.Result;
import com.mks.api.response.SubRoutine;
import com.mks.api.response.SubRoutineIterator;
import com.mks.api.response.WorkItem;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author veckardt
 */
public class Type implements WorkItem {

    WorkItem typeWI = null;
    Map<String, WorkItem> typeFields = new HashMap<>();

    public Type(Integrity intSession, WorkItem wi) throws APIException {
        typeWI = wi;
        getFieldOverrides(intSession);
    }

    private void getFieldOverrides(Integrity intSession) throws APIException {
        ItemList fieldRelationships = (ItemList) typeWI.getField("visibleFields").getList();
        List<String> fieldList = new ArrayList<>();
        if (fieldRelationships != null) {
            Iterator it = fieldRelationships.getItems();
            while (it.hasNext()) {
                Item field = (Item) it.next();
                String fieldName = field.getId();
                // out.println("Checking "+fieldName+" ...");
                fieldList.add(fieldName);
            }
            intSession.readTypeFields(typeWI.getId(), fieldList, typeFields);
        }
    }

    public boolean containsAtLeastOneFieldOf(String reference) {
        for (String fieldName : typeFields.keySet()) {
            if (reference.contains(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUsedInConstraint(String groupName) {
        // fieldRelationships
        ItemList fieldRelationships = (ItemList) typeWI.getField("fieldRelationships").getList();
        Iterator it = fieldRelationships.getItems();
        while (it.hasNext()) {
            Item rule = (Item) it.next();
            String ruleName = rule.getId();
            if (ruleName.contains("\"" + groupName + "\"")) {
                return true;
            }
        }
        return false;
    }

    public boolean isUsedInEditability(String groupName) {
        String editability = "," + typeWI.getField("issueEditability").getValueAsString() + ",";
        return editability.contains("\"" + groupName + "\"");
    }

    public boolean isUsedInPermission(String groupName) {
        String permission = "," + typeWI.getField("permittedGroups").getValueAsString() + ",";
        return permission.contains("," + groupName + ",");
    }

    public boolean isUsedInAdministrators(String groupName) {
        String permission = "," + typeWI.getField("permittedAdministrators").getValueAsString() + ",";
        return permission.contains("," + groupName + ",");
    }

    public boolean isUsedInWorkflow(String groupName) {
        ItemList stateTransitions = (ItemList) typeWI.getField("stateTransitions").getList();
        if (stateTransitions != null) {
            Iterator it = stateTransitions.getItems();
            while (it.hasNext()) {
                ItemList targetStates = (ItemList) ((Item) it.next()).getField("targetStates").getList();
                if (targetStates != null) {
                    Iterator its = targetStates.getItems();
                    while (its.hasNext()) {
                        Item state = (Item) its.next();
                        String permittedGroups = "," + state.getField("permittedGroups").getValueAsString() + ",";
                        // out.println("permittedGroups: "+ permittedGroups);
                        if (permittedGroups.contains("," + groupName + ",")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isUsedInOverwriteFieldEditability(String groupName) {
        // out.println("Count: " + typeFields.size());
        for (String fieldName : typeFields.keySet()) {
            // out.println("isUsedInOverwriteFieldEditability: " + fieldName);
            WorkItem field = typeFields.get(fieldName);
            if (field.getField("editabilityRule") != null) {
                String editabilityRule = field.getField("editabilityRule").getValueAsString();
                if (editabilityRule != null && editabilityRule.contains("\"" + groupName + "\"")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUsedInOverwriteFieldRelevance(String groupName) {
        // out.println("Count: " + typeFields.size());
        for (String fieldName : typeFields.keySet()) {
            // out.println("isUsedInOverwriteFieldRelevance: " + fieldName);
            WorkItem field = typeFields.get(fieldName);
            if (field.getField("relevanceRule") != null) {
                String relevanceRule = field.getField("relevanceRule").getValueAsString();
                if (relevanceRule != null && relevanceRule.contains("\"" + groupName + "\"")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUsedString(String groupName) {
        String result = "";
        if (isUsedInAdministrators(groupName)) {
            result += ",A";
        }

        if (isUsedInConstraint(groupName)) {
            result += ",C";
        }
        if (isUsedInEditability(groupName)) {
            result += ",E";
        }
        if (isUsedInPermission(groupName)) {
            result += ",P";
        }
        if (isUsedInWorkflow(groupName)) {
            result += ",W";
        }
        if (isUsedInOverwriteFieldEditability(groupName)) {
            result += ",FE";
        }
        if (isUsedInOverwriteFieldRelevance(groupName)) {
            result += ",FR";
        }
        if (result.startsWith(",")) {
            result = result.substring(1);
        }

        return result;
    }

    @Override
    public Result getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        return typeWI.getId();
    }

    @Override
    public String getModelType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDisplayId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContext(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration getContextKeys() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Field getField(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator getFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getFieldListSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSubRoutineListSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SubRoutineIterator getSubRoutines() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SubRoutine getSubRoutine(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsSubRoutine(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public APIException getAPIException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
