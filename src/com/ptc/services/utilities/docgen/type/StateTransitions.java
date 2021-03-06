/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemNotFoundException;
import static com.ptc.services.utilities.docgen.Constants.GROUP_XML_PREFIX;
import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.utils.ExceptionHandler;
import com.ptc.services.utilities.docgen.Integrity;
import static com.ptc.services.utilities.docgen.Integrity.getXMLParamFieldValue;
import com.ptc.services.utilities.docgen.IntegrityState;
import static com.ptc.services.utilities.docgen.IntegrityUtils.getFieldValue;
import com.ptc.services.utilities.docgen.XMLWriter;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

public class StateTransitions {

    private Field transitions;
    private List<String> statesList;
    private String strTransitions;
    private LinkedHashMap<String, IntegrityState> statesHash;

    public StateTransitions(String typeName, Field stateTransitions) {
        transitions = stateTransitions;
        statesList = new ArrayList<>();
        strTransitions = new String();
        statesHash = new LinkedHashMap<>();
        setUniqueStatesAndTransitions();

        try {
            statesHash = Integrity.getStates(typeName, statesList);
        } catch (APIException aex) {
            ExceptionHandler eh = new ExceptionHandler(aex);
            log(eh.getMessage());
            log(eh.getCommand());
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
                    sb.append(XMLWriter.padXMLParamName(xmlFromState)).append(":");

                    // Add the target state to our string builder representation of the state transitions
                    String xmlToState = IntegrityState.XML_PREFIX + XMLWriter.getXMLParamName(targetState.getId());
                    sb.append(XMLWriter.padXMLParamName(xmlToState)).append(":");

                    // Add the permitted groups for this state transition
                    try {
                        sb.append(getXMLParamFieldValue(targetState.getField("permittedGroups"), GROUP_XML_PREFIX, ","));
                    } catch (NoSuchElementException e) {
                        sb.append("");
                    }
                    // Add the delimiter for the next state transition in the list
                    sb.append(tlit.hasNext() ? ";" + nl + "\t\t\t" : "");
                }

                // Add the delimiter for the next set of state transitions in the list
                sb.append(lit.hasNext() ? ";" + nl + "\t\t\t" : "");
            }

            // Set the strTransitions variable
            strTransitions = sb.toString();
        }
    }

    public LinkedHashMap<String, IntegrityState> getList() {
        return statesHash;
    }

    public String getStringTransitions() {
        return strTransitions;
    }
    
    public List<String> getStateList() {
        return statesList;
    }    

    @SuppressWarnings("unchecked")
    public String getFormattedReport() throws ItemNotFoundException {
        StringBuilder report = new StringBuilder();
        // Construct the open table and heading line
        report.append("<table class='list'>").append(nl);
        report.append("  <tr>").append(nl);
        report.append("    <th>From State</th>").append(nl);
        report.append("    <th>To State</th>").append(nl);
        report.append("    <th>Permitted Groups</th>").append(nl);
        report.append("  </tr>").append(nl);
        // Ensure we're dealing with some valid data
        if (null != transitions && null != transitions.getList()) {
            List<Item> stateTransitionsList = transitions.getList();
            // Loop thru all the state transitions
            for (Item stateTransition : stateTransitionsList) {
                // Get the list of "To State" values
                Field targetStates = stateTransition.getField("targetStates");
                List<Item> targetStatesList = targetStates.getList();
                for (Iterator<Item> tlit = targetStatesList.iterator(); tlit.hasNext();) {
                    // Write out the new table row
                    report.append("  <tr>" + nl);
                    // Get the value for "To State"
                    Item targetState = tlit.next();
                    // Write out the "From State" value
                    report.append("    <td>" + stateTransition.getId() + "</td>" + nl);
                    // Write out the "To State" value
                    report.append("    <td>" + targetState.getId() + "</td>" + nl);
                    // Finally write out the "Permitted Groups" value
                    report.append("    <td>" + getFieldValue(targetState.getField("permittedGroups"), "<br/>") + "</td>" + nl);
                    // Close out the table row
                    report.append("  </tr>" + nl);
                }
            }
        }
        // Close the table tag
        report.append("</table>" + nl);
        return report.toString();
    }
}
