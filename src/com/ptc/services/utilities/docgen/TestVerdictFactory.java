/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class TestVerdictFactory {

    public static List<TestVerdict> parseTestVerdicts(WorkItemIterator wii, boolean doXML) throws APIException {
        List<TestVerdict> queryList = new ArrayList<>();
        if (null != wii && wii.hasNext()) {
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                // Only process admin queries
                TestVerdict q = new TestVerdict();
                q.setName(Integrity.getStringFieldValue(wi.getField("name")));
                q.setDisplayName(Integrity.getStringFieldValue(wi.getField("displayName")));
                System.out.println("Processing TestVerdict: " + q.getName());
                q.setID(Integrity.getStringFieldValue(wi.getField("id")));
                // q.setCreatedBy(Integrity.getUserFullName(wi.getField("createdBy").getItem()));
                // q.setLastModifiedDate(wi.getField("lastModified").getDateTime());
                q.setDescription(Integrity.getStringFieldValue(wi.getField("description")));
                q.setVerdicyType(Integrity.getStringFieldValue(wi.getField("verdictType")));
                queryList.add(q);
            }
        }
        return queryList;
    }

    @SuppressWarnings("unchecked")
    public static final String summarizeItemList(Field itemList, String delim) {
        StringBuilder sb = new StringBuilder();
        if (null != itemList && null != itemList.getList()) {
            List<Item> principalList = itemList.getList();
            List<String> usersList = new ArrayList<>();
            List<String> groupsList = new ArrayList<>();
            for (Item principal : principalList) {
                List<String> permissions = principal.getField("permissions").getList();
                if (principal.getModelType().equals(IMModelTypeName.USER)) {
                    if (delim.equals("<br/>" + IntegrityDocs.nl)) {
                        usersList.add(principal.getId() + (permissions.contains("modify") ? ":modify" : ""));
                    } else {
                        usersList.add(XMLWriter.padXMLParamName(Integrity.USER_XML_PREFIX + XMLWriter.getXMLParamName(principal.getId()))
                                + (permissions.contains("modify") ? ":modify" : ""));
                    }
                } else if (principal.getModelType().equals(IMModelTypeName.GROUP)) {
                    if (delim.equals("<br/>" + IntegrityDocs.nl)) {
                        groupsList.add(principal.getId() + (permissions.contains("modify") ? ":modify" : ""));
                    } else {
                        groupsList.add(XMLWriter.padXMLParamName(Integrity.GROUP_XML_PREFIX + XMLWriter.getXMLParamName(principal.getId()))
                                + (permissions.contains("modify") ? ":modify" : ""));
                    }
                }
            }

            if (delim.equals("<br/>" + IntegrityDocs.nl)) {
                sb.append(usersList.size() > 0 ? "Users:&nbsp;&nbsp;" + Integrity.convertListToString(usersList, ", ") : "");
                sb.append(usersList.size() > 0 && groupsList.size() > 0 ? "<br/>" : "");
                sb.append(groupsList.size() > 0 ? "Groups:&nbsp;&nbsp;" + Integrity.convertListToString(groupsList, ", ") : "");
            } else {
                sb.append(usersList.size() > 0 ? "u=" + Integrity.convertListToString(usersList, ",") : "");
                sb.append(usersList.size() > 0 && groupsList.size() > 0 ? ";" : "");
                sb.append(groupsList.size() > 0 ? "g=" + Integrity.convertListToString(groupsList, ",") : "");
            }
        }
        return sb.toString();
    }
}
