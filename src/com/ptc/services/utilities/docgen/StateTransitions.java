package com.ptc.services.utilities.docgen;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;

public class StateTransitions {

    private Field transitions;
    private List<String> statesList;
    private String strTransitions;
    private Hashtable<String, IntegrityState> statesHash;

    public StateTransitions(String typeName, Integrity i, Field stateTransitions) {
        transitions = stateTransitions;
        statesList = new ArrayList<String>();
        strTransitions = new String();
        statesHash = new Hashtable<String, IntegrityState>();
        setUniqueStatesAndTransitions();

        try {
            statesHash = i.getStates(typeName, statesList);
        } catch (APIException aex) {
            ExceptionHandler eh = new ExceptionHandler(aex);
            System.out.println(eh.getMessage());
            System.out.println(eh.getCommand());
            aex.printStackTrace();
        }
    }

    private void addUniqueState(String name) {
        if (!name.equalsIgnoreCase("Unspecified") && !statesList.contains(name)) {
            statesList.add(name);
        }
    }

    @SuppressWarnings("unchecked")
    private void setUniqueStatesAndTransitions() {
        //state:state:group|dynamic group,group|dynamic group,...[;
        if (null != transitions && null != transitions.getList()) {
            List<Item> stateTransitionsList = transitions.getList();
            StringBuilder sb = new StringBuilder();
            // Loop thru all the state transitions
            for (Iterator<Item> lit = stateTransitionsList.iterator(); lit.hasNext();) {
                // Get the "From State" value
                Item stateTransition = lit.next();

                // Add the 'Unspecified' state to the list of resources
                if (stateTransition.getId().equalsIgnoreCase("unspecified")) {
                    XMLWriter.paramsHash.put(IntegrityState.XML_PREFIX
                            + XMLWriter.getXMLParamName(stateTransition.getId()), stateTransition.getId());
                }

                // Add the from state to our list of unique states
                addUniqueState(stateTransition.getId());

                // Get the list of "To State" values
                Field targetStates = stateTransition.getField("targetStates");
                List<Item> targetStatesList = targetStates.getList();

                // Loop thru the target list of the transitions
                for (Iterator<Item> tlit = targetStatesList.iterator(); tlit.hasNext();) {
                    // Get the value for "To State"
                    Item targetState = tlit.next();
                    // Add the to state to our list of unique states
                    addUniqueState(stateTransition.getId());

                    // Add the from state to our string builder representation of the state transitions
                    String xmlFromState = IntegrityState.XML_PREFIX + XMLWriter.getXMLParamName(stateTransition.getId());
                    sb.append(XMLWriter.padXMLParamName(xmlFromState) + ":");

                    // Add the target state to our string builder representation of the state transitions
                    String xmlToState = IntegrityState.XML_PREFIX + XMLWriter.getXMLParamName(targetState.getId());
                    sb.append(XMLWriter.padXMLParamName(xmlToState) + ":");

                    // Add the permitted groups for this state transition
                    sb.append(Integrity.getXMLParamFieldValue(targetState.getField("permittedGroups"), Integrity.GROUP_XML_PREFIX, ","));

                    // Add the delimiter for the next state transition in the list
                    sb.append(tlit.hasNext() ? ";" + IntegrityDocs.nl + "\t\t\t" : "");
                }

                // Add the delimiter for the next set of state transitions in the list
                sb.append(lit.hasNext() ? ";" + IntegrityDocs.nl + "\t\t\t" : "");
            }

            // Set the strTransitions variable
            strTransitions = sb.toString();
        }
    }

    public Hashtable<String, IntegrityState> getList() {
        return statesHash;
    }

    public String getStringTransitions() {
        return strTransitions;
    }

    @SuppressWarnings("unchecked")
    public String getFormattedReport() throws ItemNotFoundException {
        StringBuffer report = new StringBuffer();
        // Construct the open table and heading line
        report.append("<table class='list'>" + IntegrityDocs.nl);
        report.append("  <tr>" + IntegrityDocs.nl);
        report.append("    <th>From State</th>" + IntegrityDocs.nl);
        report.append("    <th>To State</th>" + IntegrityDocs.nl);
        report.append("    <th>Permitted Groups</th>" + IntegrityDocs.nl);
        report.append("  </tr>" + IntegrityDocs.nl);
        // Ensure we're dealing with some valid data
        if (null != transitions && null != transitions.getList()) {
            List<Item> stateTransitionsList = transitions.getList();
            // Loop thru all the state transitions
            for (Iterator<Item> lit = stateTransitionsList.iterator(); lit.hasNext();) {
                // Get the "From State" value
                Item stateTransition = lit.next();
                // Get the list of "To State" values
                Field targetStates = stateTransition.getField("targetStates");
                List<Item> targetStatesList = targetStates.getList();
                for (Iterator<Item> tlit = targetStatesList.iterator(); tlit.hasNext();) {
                    // Write out the new table row
                    report.append("  <tr>" + IntegrityDocs.nl);
                    // Get the value for "To State"
                    Item targetState = tlit.next();
                    // Write out the "From State" value
                    report.append("    <td>" + stateTransition.getId() + "</td>" + IntegrityDocs.nl);
                    // Write out the "To State" value
                    report.append("    <td>" + targetState.getId() + "</td>" + IntegrityDocs.nl);
                    // Finally write out the "Permitted Groups" value
                    report.append("    <td>" + Integrity.getFieldValue(targetState.getField("permittedGroups"), "<br/>") + "</td>" + IntegrityDocs.nl);
                    // Close out the table row
                    report.append("  </tr>" + IntegrityDocs.nl);
                }
            }
        }
        // Close the table tag
        report.append("</table>" + IntegrityDocs.nl);
        return report.toString();
    }
}
