package com.ptc.services.utilities.docgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mks.api.im.IMModelTypeName;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.utils.Logger.log;

public class QueryFactory {

    public static List<Query> parseQueries(WorkItemIterator wii, boolean doXML) throws APIException {
        List<Query> queryList = new ArrayList<Query>();
        if (null != wii && wii.hasNext()) {
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                // Only process admin queries
                if (Integrity.getBooleanFieldValue(wi.getField("isAdmin"))) {
                    Query q = new Query();
                    q.setName(Integrity.getStringFieldValue(wi.getField("name")));
                    log("Processing Query: " + q.getName());
                    q.setID(Integrity.getStringFieldValue(wi.getField("id")));
                    q.setSharedAdmin(Integrity.getBooleanFieldValue(wi.getField("isAdmin")));
                    q.setCreatedBy(Integrity.getUserFullName(wi.getField("createdBy").getItem()));
                    q.setLastModifiedDate(wi.getField("lastModified").getDateTime());
                    q.setDescription(Integrity.getStringFieldValue(wi.getField("description")));
                    q.setFields(doXML ? Integrity.getFieldValue(wi.getField("fields"), ",")
                            : Integrity.getFieldValue(wi.getField("fields"), "<br/>" + IntegrityDocs.nl));
                    q.setQueryDefinition(Integrity.getStringFieldValue(wi.getField("queryDefinition")));
                    q.setShareWith(doXML ? summarizeItemList(wi.getField("shareWith"), ",")
                            : summarizeItemList(wi.getField("shareWith"), "<br/>" + IntegrityDocs.nl));
                    q.setSortDirection(Integrity.getStringFieldValue(wi.getField("sortDirection")));
                    q.setSortField(Integrity.getFieldValue(wi.getField("sortField"), ""));
                    queryList.add(q);
                }
            }
        }
        return queryList;
    }

    @SuppressWarnings("unchecked")
    public static final String summarizeItemList(Field itemList, String delim) {
        StringBuilder sb = new StringBuilder();
        if (null != itemList && null != itemList.getList()) {
            List<Item> principalList = itemList.getList();
            List<String> usersList = new ArrayList<String>();
            List<String> groupsList = new ArrayList<String>();
            for (Iterator<Item> pit = principalList.iterator(); pit.hasNext();) {
                Item principal = pit.next();
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
