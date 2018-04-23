package com.ptc.services.utilities.docgen.models.workflow;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoLink;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoListPosition;
import com.nwoods.jgo.layout.JGoLayeredDigraphAutoLayout;

/**
 * The model (document) for WorkflowJGoView. The model contains nodes, links,
 * and self-loops, it interacts with them, and performs layout.
 */
public class WorkflowJGoModel extends JGoDocument {

    private static final long serialVersionUID = 92342456L;
    private Field transitions;

    /**
     * Constructor.
     */
    public WorkflowJGoModel(Field stateTransitions) {
        super();
        transitions = stateTransitions;
    }

    public void open() {
        deleteContents();
        load();
        performLayout();
    }

    /**
     * Loads the Workflow provided to the constructor.
     */
    @SuppressWarnings("unchecked")
    private void load() {
        // Temporary model for loading the states
        java.util.List<String> insertedStates = new ArrayList<String>();
        WorkflowJGoNode nodeFrom = null;
        WorkflowJGoNode nodeTo = null;

        // Ensure we're dealing with the right data type
        if (null != transitions && null != transitions.getDataType() && transitions.getDataType().equals(Field.ITEM_LIST_TYPE)) {
            List<Item> stateTransitionsList = transitions.getList();
            // Loop thru all the state transitions
            for (Iterator<Item> lit = stateTransitionsList.iterator(); lit.hasNext();) {
                // Get the "From State" value
                Item stateTransition = lit.next();
                // Get the list of "To State" values
                Field targetStates = stateTransition.getField("targetStates");
                List<Item> targetStatesList = targetStates.getList();
                for (Iterator<Item> tlit = targetStatesList.iterator(); tlit.hasNext();) {
                    // Insert the "From State" into the model, if it doesn't already exist
                    if (!insertedStates.contains(stateTransition.getId())) {
                        insertedStates.add(stateTransition.getId());
                        nodeFrom = new WorkflowJGoNode(stateTransition.getId());
                        addNode(nodeFrom);
                    } else // initialize the "From State" node
                    {
                        nodeFrom = getWorkflowJGoNode(stateTransition.getId());
                    }

                    // Get the value for "To State"
                    Item targetState = tlit.next();
                    // Insert the "To State" into the model, if it doesn't already exist
                    if (!insertedStates.contains(targetState.getId())) {
                        insertedStates.add(targetState.getId());
                        nodeTo = new WorkflowJGoNode(targetState.getId());
                        addNode(nodeTo);
                    } else // initialize the "To State" node
                    {
                        nodeTo = getWorkflowJGoNode(targetState.getId());
                    }

                    // Finally create the link between the source and target state
                    // Since this is for a documentation, we don't care for self transitions
                    if (!stateTransition.getId().equals(targetState.getId())) {
                        WorkflowJGoLink link = WorkflowJGoFactory.createLink(nodeFrom.getPort(), nodeTo.getPort());
                        addLink(link);
                    }
                }
            }
        }
    }

    /**
     * Gets a node based on state name that is in the node.
     *
     * @param state Name of the State.
     * @return A node that contains the State.
     */
    public WorkflowJGoNode getWorkflowJGoNode(String state) {
        for (JGoListPosition pos = getFirstObjectPos(); pos != null; pos = getNextObjectPosAtTop(pos)) {
            JGoObject obj = getObjectAtPos(pos);
            if (obj instanceof WorkflowJGoNode) {
                WorkflowJGoNode node = (WorkflowJGoNode) obj;
                if (node.getType().equals(state)) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Creates a link with an arrow.
     *
     * @return A new link.
     */
    public JGoLink newLink(JGoPort from, JGoPort to) {
        WorkflowJGoLink link = null;
        link = new WorkflowJGoLink(from, to);
        addObjectAtTail(link);
        return link;
    }

    /**
     * Adds a node to this document. The nodes are added behind all objects.
     *
     * @param obj A node to be added.
     */
    public void addNode(JGoObject obj) {
        addObjectAtHead(obj);
    }

    /**
     * Adds a link to this document. The links are added in front of all
     * objects, so that the labels are not blocked by the nodes, if there is an
     * overlap.
     *
     * @param obj A link to be added.
     */
    public void addLink(JGoObject obj) {
        addObjectAtTail(obj);
    }

    /**
     * Runs layout algorithm.
     *
     * @param direction The direction of the layout. See the constants on top of
     * this file.
     */
    public void performLayout() {
        JGoLayeredDigraphAutoLayout layout = new JGoLayeredDigraphAutoLayout(
                this, 10, 40,
                JGoLayeredDigraphAutoLayout.LD_DIRECTION_DOWN,
                JGoLayeredDigraphAutoLayout.LD_CYCLEREMOVE_DFS,
                JGoLayeredDigraphAutoLayout.LD_LAYERING_OPTIMALLINKLENGTH,
                JGoLayeredDigraphAutoLayout.LD_INITIALIZE_DFSOUT, 4,
                JGoLayeredDigraphAutoLayout.LD_AGGRESSIVE_FALSE);
        layout.performLayout();
    }
}
