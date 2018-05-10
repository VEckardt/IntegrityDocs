package com.ptc.services.utilities.docgen.models.relationship;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoLink;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoListPosition;
import com.nwoods.jgo.layout.JGoLayeredDigraphAutoLayout;
import com.ptc.services.utilities.docgen.IntegrityType;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * The model (document) for WorkflowJGoView. The model contains nodes, links,
 * and self-loops, it interacts with them, and performs layout.
 */
public class RelationshipJGoModel extends JGoDocument {

    private static final long serialVersionUID = 92342456L;
    private String type;
    private LinkedHashMap<String, List<String>> relationshipFields;

    /**
     * Constructor.
     */
    public RelationshipJGoModel(IntegrityType type, LinkedHashMap<String, List<String>> relFields) {
        super();
        this.type = type.getName();
        relationshipFields = relFields;
    }

    public void open() {
        deleteContents();
        load();
        performLayout();
    }

    /**
     * Loads the relationships.
     */
    private void load() {
        // Temporary model for loading the types
        java.util.List<String> insertedTypes = new ArrayList<String>();
        RelationshipJGoNode nodeFrom = null;
        RelationshipJGoNode nodeTo = null;

        // First insert the Type, whose context is used to generate the relationship view
        insertedTypes.add(type);
        nodeFrom = new RelationshipJGoNode(type, Color.GREEN);
        addNode(nodeFrom);

        // Now enumerate thru the rest of the related types and add them to the model
        Set<String> relFields = relationshipFields.keySet();

        // Save the current relationship field name
        for (String fieldName : relFields) {
            // Get the list of related Types using this relationship field
            List<String> typesList = relationshipFields.get(fieldName);
            // Iterate thru the list of target types
            for (Iterator<String> it = typesList.iterator(); it.hasNext();) {
                // Insert the type, if it is not already inserted.
                String typeTo = it.next();
                if (!insertedTypes.contains(typeTo)) {
                    insertedTypes.add(typeTo);
                    nodeTo = new RelationshipJGoNode(typeTo, Color.YELLOW);
                    addNode(nodeTo);
                } else {
                    // Check to see if this is a self relationship
                    if (type.equals(typeTo)) {
                        nodeTo = getRelationshipJGoNode(typeTo);
                        // If we didn't find a self relationship node, create one
                        if (null == nodeTo) {
                            nodeTo = new RelationshipJGoNode(typeTo, Color.GREEN);
                            nodeTo.setSelfRelatedNode();
                            addNode(nodeTo);
                        }
                    } else {
                        // Use the uniquely named node
                        nodeTo = getRelationshipJGoNode(typeTo);
                    }
                }

                // Create the link between the source and target
                RelationshipJGoLink link = RelationshipJGoFactory.createLink(nodeFrom.getPort(), nodeTo.getPort(), fieldName);
                addLink(link);
            }
        }
    }

    /**
     * Gets a node based on type name that is in the node.
     *
     * @param type Name of the Type.
     * @return A node that contains the Type.
     */
    public RelationshipJGoNode getRelationshipJGoNode(String typeName) {
        for (JGoListPosition pos = getFirstObjectPos(); pos != null; pos = getNextObjectPosAtTop(pos)) {
            JGoObject obj = getObjectAtPos(pos);
            if (obj instanceof RelationshipJGoNode) {
                RelationshipJGoNode node = (RelationshipJGoNode) obj;
                // Check to see if this is a relationship to self.
                if (typeName.equals(type) && node.getType().equals(typeName)) {
                    // Only return a self relationship node
                    if (node.isSelfRelationship()) {
                        return node;
                    }
                } else if (node.getType().equals(typeName)) {
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
    public JGoLink newLink(JGoPort from, JGoPort to, String relName) {
        RelationshipJGoLink link = null;
        link = new RelationshipJGoLink(from, to, relName);
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
                this, 40, 40,
                JGoLayeredDigraphAutoLayout.LD_DIRECTION_RIGHT,
                JGoLayeredDigraphAutoLayout.LD_CYCLEREMOVE_DFS,
                JGoLayeredDigraphAutoLayout.LD_LAYERING_OPTIMALLINKLENGTH,
                JGoLayeredDigraphAutoLayout.LD_INITIALIZE_DFSOUT, 4,
                JGoLayeredDigraphAutoLayout.LD_AGGRESSIVE_FALSE);
        layout.performLayout();
    }
}
