/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author veckardt
 */
public class SimpleItem implements WorkItem {

    String className = "";
    String id = "";
    Map<String, StringField> fields = new LinkedHashMap();
    List<SimpleItem> childs = new ArrayList();

    public SimpleItem(String className, String id) {
        this.id = id;
        this.className = className;
    }

    public void add(String fieldName, String value) {
        fields.put(fieldName, new StringField(fieldName, value));
    }

    public void addChild(SimpleItem si) {
        childs.add(si);
    }

    @Override
    public Result getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        return fields.get("name").getValueAsString();
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
