package com.ptc.services.utilities.docgen.stage;

import com.mks.api.response.Item;
import com.mks.api.response.Field;

public class DeployType {

    private String name;
    private boolean isDefault;
    private String pattern;
    private String description;

    public DeployType(Item deployType) {
        // Initialize our class variables
        name = new String();
        isDefault = false;
        pattern = new String();
        description = new String();

        if (null != deployType) {
            Field nameFld = deployType.getField("name");
            if (null != nameFld && null != nameFld.getValueAsString()) {
                name = nameFld.getValueAsString();
            }

            Field isDefaultFld = deployType.getField("isdefault");
            if (null != isDefaultFld && null != isDefaultFld.getBoolean()) {
                isDefault = isDefaultFld.getBoolean().booleanValue();
            }

            Field patternFld = deployType.getField("pattern");
            if (null != patternFld && null != patternFld.getValueAsString()) {
                pattern = patternFld.getValueAsString();
            }

            Field descriptionFld = deployType.getField("description");
            if (null != descriptionFld && null != descriptionFld.getValueAsString()) {
                description = descriptionFld.getValueAsString();
            }
        }
    }

    // Return the information parsed
    public String getName() {
        return name;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public String getPattern() {
        return pattern;
    }

    public String getDescription() {
        return description;
    }
}
