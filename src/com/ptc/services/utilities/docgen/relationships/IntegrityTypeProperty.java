/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.relationships;

/**
 *
 * @author veckardt
 */
public class IntegrityTypeProperty {

    private String name;
    private String value;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegrityTypeProperty)) {
            return false;
        }
        IntegrityTypeProperty prop = (IntegrityTypeProperty) o;
        return name.equals(prop.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IntegrityTypeProperty(String name, String value) {
        this.name = name;
        this.value = escape(value);
        this.description = "";
    }

    public IntegrityTypeProperty(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public static String escape(String str) {
        str = str.replaceAll("\\:", "\\\\:");
        str = str.replaceAll("\\;", "\\\\;");
        return str;
    }

    @Override
    public String toString() {
        return name + ": " + value + " (" + description + ")";
    }
}
