package com.ptc.services.utilities.docgen.utils;

import com.mks.api.response.Field;
import com.mks.api.response.Item;

public class PathMapping {

    private String memberPathPrefix;
    private String targetPathPrefix;

    public PathMapping(Item pathMapping) {
        if (null != pathMapping) {
            Field memberPathPrefixFld = pathMapping.getField("memberPathPrefix");
            if (null != memberPathPrefixFld && null != memberPathPrefixFld.getValueAsString()) {
                memberPathPrefix = memberPathPrefixFld.getValueAsString();
            } else {
                memberPathPrefix = new String();
            }

            Field targetPathPrefixFld = pathMapping.getField("targetPathPrefix");
            if (null != targetPathPrefixFld && null != targetPathPrefixFld.getValueAsString()) {
                targetPathPrefix = targetPathPrefixFld.getValueAsString();
            } else {
                targetPathPrefix = new String();
            }
        }
    }

    // Return the information parsed
    public String getMemberPathPrefix() {
        return memberPathPrefix;
    }

    public String getTargetPathPrefix() {
        return targetPathPrefix;
    }
}
