package com.ptc.services.utilities.docgen;

import java.util.ArrayList;
import java.util.List;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.QueryFactory.summarizeItemList;

public class ReportFactory {

    public static List<Report> parseReports(WorkItemIterator wii, boolean doXML) throws APIException {
        List<Report> objects = new ArrayList<>();

        System.out.println("Parsing Reports ... ");

        if (null != wii && wii.hasNext()) {
            while (wii.hasNext()) {
                WorkItem wi = wii.next();
                // Only process admin queries
                Report q = new Report();
                q.setName(Integrity.getStringFieldValue(wi.getField("name")));
                System.out.println("Processing Report: " + q.getName());
                q.setID(Integrity.getStringFieldValue(wi.getField("id")));
                // q.setIsActive(Integrity.getBooleanFieldValue(wi.getField("isActive")));
                // q.setCreatedBy(Integrity.getUserFullName(wi.getField("createdBy").getItem()));
                // q.setLastModifiedDate(wi.getField("lastModified").getDateTime());
                q.setDescription(Integrity.getStringFieldValue(wi.getField("description")));
                q.setQuery(Integrity.getStringFieldValue(wi.getField("query")));
                q.setShareWith(doXML ? summarizeItemList(wi.getField("shareWith"), ",")
                        : summarizeItemList(wi.getField("shareWith"), "<br/>" + IntegrityDocs.nl));
                objects.add(q);
            }
        }
        return objects;
    }

//    @SuppressWarnings("unchecked")
//    public static final String summarizeItemList(Field itemList, String delim) {
//        StringBuilder sb = new StringBuilder();
//        if (null != itemList && null != itemList.getList()) {
//            List<Item> principalList = itemList.getList();
//            List<String> usersList = new ArrayList<String>();
//            List<String> groupsList = new ArrayList<String>();
//            for (Iterator<Item> pit = principalList.iterator(); pit.hasNext();) {
//                Item principal = pit.next();
//                List<String> permissions = principal.getField("permissions").getList();
//                if (principal.getModelType().equals(IMModelTypeName.USER)) {
//                    if (delim.equals("<br/>" + IntegrityDocs.nl)) {
//                        usersList.add(principal.getId() + (permissions.contains("modify") ? ":modify" : ""));
//                    } else {
//                        usersList.add(XMLWriter.padXMLParamName(Integrity.USER_XML_PREFIX + XMLWriter.getXMLParamName(principal.getId()))
//                                + (permissions.contains("modify") ? ":modify" : ""));
//                    }
//                } else if (principal.getModelType().equals(IMModelTypeName.GROUP)) {
//                    if (delim.equals("<br/>" + IntegrityDocs.nl)) {
//                        groupsList.add(principal.getId() + (permissions.contains("modify") ? ":modify" : ""));
//                    } else {
//                        groupsList.add(XMLWriter.padXMLParamName(Integrity.GROUP_XML_PREFIX + XMLWriter.getXMLParamName(principal.getId()))
//                                + (permissions.contains("modify") ? ":modify" : ""));
//                    }
//                }
//            }
//
//            if (delim.equals("<br/>" + IntegrityDocs.nl)) {
//                sb.append(usersList.size() > 0 ? "Users:&nbsp;&nbsp;" + Integrity.convertListToString(usersList, ", ") : "");
//                sb.append(usersList.size() > 0 && groupsList.size() > 0 ? "<br/>" : "");
//                sb.append(groupsList.size() > 0 ? "Groups:&nbsp;&nbsp;" + Integrity.convertListToString(groupsList, ", ") : "");
//            } else {
//                sb.append(usersList.size() > 0 ? "u=" + Integrity.convertListToString(usersList, ",") : "");
//                sb.append(usersList.size() > 0 && groupsList.size() > 0 ? ";" : "");
//                sb.append(groupsList.size() > 0 ? "g=" + Integrity.convertListToString(groupsList, ",") : "");
//            }
//        }
//        return sb.toString();
//    }
}
