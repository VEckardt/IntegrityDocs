/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.2 $
 * Last changed:   $Date: 2017/05/12 00:20:03CEST $
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.Item;
import com.mks.api.response.ItemList;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.IntegrityDocsConfig.Types;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author veckardt
 */
public class DynamicGroup extends StaticGroup {

    public DynamicGroup(WorkItem wi) {
        super(wi, Types.DynamicGroup);
        try {
            membership = (ItemList) wi.getField("membership").getList();
        } catch (NoSuchElementException ex) {

        }

    }

    @Override
    public String getMembership() {
        return getMembership("", null, true);
    }

    public String getMembership(String userName, String staticGroups) {
        return getMembership(userName, staticGroups, true);
    }

    public String getMembership(String userName, String staticGroups, Boolean showDetails) {
        String data = "<table class='sortable'>";
        data += "<tr><th class='heading8'>Project</th><th class='heading8'>Users</th><th class='heading8'>Groups</th></tr>";
        String users = "";
        String groups = "";
        if (membership != null) {
            Iterator it = membership.getItems();
            while (it.hasNext()) {
                Item item = (Item) it.next();
                String project = item.getId();
                if (item.getField("Users") != null) {
                    users = item.getField("Users").getValueAsString().replaceAll(",", ", ");
                }
                if (item.getField("Groups") != null) {
                    groups = item.getField("Groups").getValueAsString().replaceAll(",", ", ");
                }
                if (userName.isEmpty() || (containsOneOf(users, userName))
                        || (containsOneOf(groups, staticGroups))) {
                    if (showDetails) {
                        data += "<tr><td>" + project + "</td><td>" + users + "</td><td>" + groups + "</td></tr>";
                    } else {
                        data += "\n" + getName() + ";" + project;
                    }
                }
            }
            data += "</table>";
            return data;
        }
        return "";
    }

    @Override
    public Boolean isMemberOfGroup(String userName, String groupNames) {
        // String data = "";
        String users = "";
        String groups = "";
        if (membership != null) {
            Iterator it = membership.getItems();
            while (it.hasNext()) {
                Item item = (Item) it.next();
                // String project = item.getId();

                // out.println(" SUB: Checking project '" + project + "' ...");
                if (item.getField("Users") != null) {
                    users = item.getField("Users").getValueAsString();
                }
                if (item.getField("Groups") != null) {
                    groups = item.getField("Groups").getValueAsString();
                }

                if (userName.isEmpty() || containsOneOf(users, userName) || containsOneOf(groups, groupNames)) {
                    return true;
                }
                // out.println(" SUB: Checking group '" + groups + "' for project '" + project + "' ...");
//                if (containsOneOf(groups, groupNames)) {
//                    return true;
//                }
            }
        }
        return false;
    }
}
