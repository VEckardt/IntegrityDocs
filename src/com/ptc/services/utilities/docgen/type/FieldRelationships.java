package com.ptc.services.utilities.docgen.type;

import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.IntegrityField;
import com.ptc.services.utilities.docgen.IntegrityState;
import com.ptc.services.utilities.docgen.IntegrityType;
import com.ptc.services.utilities.docgen.XMLWriter;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.LinkedHashMap;

public class FieldRelationships {

    private Field fieldRelationships;
    private String strFieldRelationships;

    public FieldRelationships(LinkedHashMap<String, IntegrityField> fieldsHash, Field fldRelationships) {
        // log("1");
        fieldRelationships = fldRelationships;
        // log("2");
        strFieldRelationships = new String();
        // log("3");
        initStringFieldRelationships(fieldsHash);
    }

    public String getStringFieldRelationships() {
        return strFieldRelationships;
    }

    private String getRule(Item sourceField) {
        // Determines if this field relationship is a "rule" based field relationship
        String rule = new String();
        try {
            // Attempt to read the value for rule.
            Field ruleFld = sourceField.getField("rule");
            rule = ruleFld.getValueAsString();
        } catch (NoSuchElementException nosee) {
            // This is a regular field relationship
        }
        return rule;
    }

    @SuppressWarnings("unchecked")
    private void initStringFieldRelationships(LinkedHashMap<String, IntegrityField> fieldsHash) {
        // log("4");
        StringBuilder sb = new StringBuilder();
        if (null != fieldRelationships && null != fieldRelationships.getList()) {
            // log("5");
            List<Item> fieldRelList = fieldRelationships.getList();
            // Loop thru all the field relationships
            // log("6");
            for (Iterator<Item> lit = fieldRelList.iterator(); lit.hasNext();) {
                // Get the "Source Field" names
                // log("7");
                Item sourceField = lit.next();
                String rule = getRule(sourceField);
                // log("8");
                try {
                    if (rule.length() > 0) {
                        // Append the rule based relationship to the list of field relationships
                        // log("9a");
                        sb.append("rule=" + rule);
                        sb.append(Integrity.getFieldValue(sourceField.getField("targetField"), ""));
                        // log("9b");
                    } else {
                        // Get the "Target Fields"
                        Field targetFields = sourceField.getField("targetFields");
                        // log("10");
                        List<Item> targetFieldList = targetFields.getList();
                        // log("11a");
                        for (Iterator<Item> tlit = targetFieldList.iterator(); tlit.hasNext();) {
                            // log("12a");
                            // Get the value for "Target Field" name
                            Item targetField = tlit.next();
                            // Get the values for "Source Values" and "Target Values"
                            Field sourceValues = targetField.getField("sourceValues");
                            Field targetValues = targetField.getField("targetValues");
                            Field targetValueType = targetField.getField("targetValueType");

                            // Append the value for the source field and source values
                            String xmlSourceParam = IntegrityField.XML_PREFIX + XMLWriter.getXMLParamName(sourceField.getId());
                            XMLWriter.paramsHash.put(xmlSourceParam, sourceField.getId());
                            sb.append(XMLWriter.padXMLParamName(xmlSourceParam) + "=");

                            // Check to see if this source value is a Type, State, User, or Group objects
                            // log("13a => " + sourceField.getId());
                            IntegrityField iField = fieldsHash.get(sourceField.getId());
                            // log("13b");
                            if (iField == null) {
                                log("iField == null => sourceField.getId() = " + sourceField.getId());
                            }
                            switch (iField.getFieldType()) {
                                case TYPE:
                                    sb.append(Integrity.getXMLParamFieldValue(sourceValues, IntegrityType.XML_PREFIX, ",") + ":");
                                    break;

                                case STATE:
                                    sb.append(Integrity.getXMLParamFieldValue(sourceValues, IntegrityState.XML_PREFIX, ",") + ":");
                                    break;

                                case USER:
                                    sb.append(Integrity.getXMLParamFieldValue(sourceValues, Integrity.USER_XML_PREFIX, ",") + ":");
                                    break;

                                case GROUP:
                                    sb.append(Integrity.getXMLParamFieldValue(sourceValues, Integrity.GROUP_XML_PREFIX, ",") + ":");
                                    break;

                                default:
                                    sb.append(Integrity.getFieldValue(sourceValues, ",") + ":");
                            }

                            // Append the value for the target field and target values
                            sb.append(targetField.getId() + "=");
                            // This is the case where you have a member of group
                            if (Integrity.getFieldValue(targetValueType, "").equals("memberOf")) {
                                sb.append("memberOf(" + Integrity.getXMLParamFieldValue(targetValues, Integrity.GROUP_XML_PREFIX, ",") + ")");
                            } // This is the case where you have members of a group that is derived from the value of a group field
                            else if (Integrity.getFieldValue(targetValueType, "").equals("valueOf")) {
                                sb.append("valueOf(" + Integrity.getXMLParamFieldValue(targetValues, IntegrityField.XML_PREFIX, "") + ")");
                            } else {
                                sb.append(Integrity.getFieldValue(targetValues, ","));
                            }

                            sb.append(tlit.hasNext() ? ";" + nl + "\t\t\t" : "");
                        }
                        // log("11b");
                    }

                    sb.append(lit.hasNext() ? ";" + nl + "\t\t\t" : "");
                } catch (NoSuchElementException nsee) {
                    log("Failed to parse Field Relationship: " + sourceField.getId());
                }
            }
        }
        // log("8");
        strFieldRelationships = sb.toString();
    }

    @SuppressWarnings("unchecked")
    public String getFormattedReport() throws ItemNotFoundException {
        StringBuffer report = new StringBuffer();
        // Construct the open table and heading line
        report.append("<table class='list'>" + nl);
        report.append("  <tr>" + nl);
        report.append("    <th>Source Field</th>" + nl);
        report.append("    <th>Source Values</th>" + nl);
        report.append("    <th>Target Field</th>" + nl);
        report.append("    <th>Target Values</th>" + nl);
        report.append("  </tr>" + nl);
        // Ensure we're dealing with the right data type
        if (null != fieldRelationships && null != fieldRelationships.getList()) {
            List<Item> fieldRelList = fieldRelationships.getList();
            // Loop thru all the field relationships
            for (Iterator<Item> lit = fieldRelList.iterator(); lit.hasNext();) {
                // Get the "Source Field" names
                Item sourceField = lit.next();
                String rule = getRule(sourceField);
                try {
                    if (rule.length() > 0) {
                        // Get the targetField name for this rule based field relationship
                        Field targetField = sourceField.getField("targetField");
                        // Write out the new table row
                        report.append("  <tr>" + nl);
                        // Write out the value for the "Source Field" name
                        report.append("    <td>" + sourceField.getId() + "</td>" + nl);
                        // Leave the Source Field Value blank for rule based field relationships
                        report.append("    <td>&nbsp;</td>" + nl);
                        // Get the value for "Target Field" name
                        report.append("    <td>" + Integrity.getFieldValue(targetField, "<br/>") + "</td>" + nl);
                        // For the "Target Values", enter the rule information
                        report.append("    <td>" + rule + "</td>" + nl);
                        // Close out the table row
                        report.append("  </tr>" + nl);
                    } else {
                        // Get the "Target Fields"
                        Field targetFields = sourceField.getField("targetFields");
                        List<Item> targetFieldList = targetFields.getList();
                        for (Iterator<Item> tlit = targetFieldList.iterator(); tlit.hasNext();) {
                            // Write out the new table row
                            report.append("  <tr>" + nl);
                            // Write out the value for the "Source Field" name
                            report.append("    <td>" + sourceField.getId() + "</td>" + nl);
                            // Get the value for "Target Field" name
                            Item targetField = tlit.next();
                            // Get the values for "Source Values" and "Target Values"
                            Field sourceValues = targetField.getField("sourceValues");
                            Field targetValues = targetField.getField("targetValues");
                            Field targetValueType = targetField.getField("targetValueType");
                            // Finally, write out the "Source Values", "Target Field", and "Target Values"
                            report.append("    <td>" + Integrity.getFieldValue(sourceValues, "<br/>") + "</td>" + nl);
                            report.append("    <td>" + targetField.getId() + "</td>" + nl);
                            if (Integrity.getFieldValue(targetValueType, "").equals("memberOf") || Integrity.getFieldValue(targetValueType, "").equals("valueOf")) {
                                report.append("    <td>memberOf(" + Integrity.getFieldValue(targetValues, "<br/>") + ")</td>" + nl);
                            } else {
                                report.append("    <td>" + Integrity.getFieldValue(targetValues, "<br/>") + "</td>" + nl);
                            }
                            // Close out the table row
                            report.append("  </tr>" + nl);
                        }
                    }
                } catch (NoSuchElementException nsee) {
                    log("Failed to parse Field Relationship: " + sourceField.getId());
                    nsee.printStackTrace();
                }
            }
        }
        // Close the table tag
        report.append("</table>" + nl);
        return report.toString();
    }
}
