package com.ptc.services.utilities.docgen.utils;

import com.mks.api.response.Item;
import com.mks.api.response.Field;

public class Action {

    private String script;
    private String parameters;

    public Action(Item actionsSubselection) {
        // Initialize our class variables
        script = new String();
        parameters = new String();

        if (null != actionsSubselection) {
            Field scriptFld = actionsSubselection.getField("script");
            if (null != scriptFld && null != scriptFld.getValueAsString()) {
                script = scriptFld.getValueAsString();
            }

            Field parametersFld = actionsSubselection.getField("parameters");
            if (null != parametersFld && null != parametersFld.getValueAsString()) {
                parameters = parametersFld.getValueAsString();
            }
        }
    }

    // Return the information parsed
    public String getScript() {
        return script;
    }

    public String getParameters() {
        return parameters;
    }
}
