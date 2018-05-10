/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import com.mks.api.response.Field;
import com.mks.api.response.Item;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author veckardt
 */
public class StringField implements Field {

    String name;
    String value;

    public StringField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getModelType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDataType() {
        return "String";
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }

    @Override
    public Date getDateTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getDouble() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Float getFloat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getInteger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long getLong() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public byte[] getBytes() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageIcon getImage() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List getList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Item getItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getValueAsString() {
        return value;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public boolean hasDisplayValue() {
        return !name.isEmpty();
    }

}
