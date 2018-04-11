package com.ptc.services.utilities.docgen.models.workflow;

import com.nwoods.jgo.JGoLabeledLink;
import com.nwoods.jgo.JGoPort;

/**
 * A link in the workflow that represents a state transition link. This link
 * knows about the source and target types,
 */
public class WorkflowJGoLink extends JGoLabeledLink {

    private static final long serialVersionUID = 32342456L;

    public static final int thickness = 1;
    public static final int selectedThickness = 3;
    public static final int mainPathThickness = 3;

    /**
     * Constructor, use WorkflowJGoFactory.createLink() instead.
     *
     * @param from The source port of this link.
     * @param to The target port of this link.
     * @see WorkflowJGoFactory#createLink
     */
    public WorkflowJGoLink(JGoPort from, JGoPort to) {
        super(from, to);
        setArrowHeads(false, true);
        setSelectable(true);
        setDraggable(false);
    }

    /**
     * @return The source Type of this link.
     */
    public String getFromType() {
        JGoPort port = getFromPort();
        if (port instanceof WorkflowJGoPort) {
            return ((WorkflowJGoPort) port).getNode().getType();
        } else {
            return null;
        }
    }

    /**
     * @return The target Type of this link.
     */
    public String getToType() {
        JGoPort port = getToPort();
        if (port instanceof WorkflowJGoPort) {
            return ((WorkflowJGoPort) port).getNode().getType();
        } else {
            return null;
        }
    }
}
