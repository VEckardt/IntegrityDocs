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

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Result;
import com.mks.api.response.SubRoutine;
import com.mks.api.response.SubRoutineIterator;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.utils.StringField;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author veckardt
 */
public class SimpleWorkItem implements WorkItem {

    String className = "";
    String id = "";
    Map<String, StringField> fields = new LinkedHashMap();
    List<SimpleWorkItem> childs = new ArrayList();

    public SimpleWorkItem(String className, String id) {
        this.className = className;
        this.id = id;
    }

    public void add(String fieldName, String value) {
        fields.put(fieldName, new StringField(fieldName, value));
    }

    public void addChild(SimpleWorkItem si) {
        childs.add(si);
    }

    @Override
    public Result getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        // return fields.get("name").getValueAsString();
        return id;
    }

    @Override
    public String getModelType() {
        return "SimpleField";
    }

    @Override
    public String getContext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDisplayId() {
        return fields.get("name").getValueAsString();
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
        return (StringField) fields.get(string);
    }

    @Override
    public Iterator getFields() {
        List<StringField> lsf = new ArrayList<>();
        for (String name : fields.keySet()) {
            StringField sf = fields.get(name);
            lsf.add(sf);
        }

        return lsf.iterator();
    }

    @Override
    public int getFieldListSize() {
        return fields.size();
    }

    @Override
    public boolean contains(String string) {
        return fields.containsKey(string);
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
