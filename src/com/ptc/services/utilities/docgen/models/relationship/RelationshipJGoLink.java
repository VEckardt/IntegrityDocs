package com.ptc.services.utilities.docgen.models.relationship;

import com.nwoods.jgo.JGoLabeledLink;
import com.nwoods.jgo.JGoLinkLabel;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoText;

/**
 * A link in the entity relationships that represents a relationship link. This
 * link knows about the source and target types,
 */
public class RelationshipJGoLink extends JGoLabeledLink {

    private static final long serialVersionUID = 32342456L;

    public static final int thickness = 1;
    public static final int selectedThickness = 3;
    public static final int mainPathThickness = 3;

    /**
     * Our label. Shows the relationship field name.
     */
    protected JGoLinkLabel label;

    /**
     * Is the label turned on?
     */
    protected boolean showLabel;

    /**
     * Constructor, use WorkflowJGoFactory.createLink() instead.
     *
     * @param from The source port of this link.
     * @param to The target port of this link.
     * @see WorkflowJGoFactory#createLink
     */
    public RelationshipJGoLink(JGoPort from, JGoPort to, String relName) {
        super(from, to);
        //setArrowHeads(false, true);
        setSelectable(true);
        setDraggable(false);
        label = new JGoLinkLabel();
        label.setAlignment(JGoText.ALIGN_CENTER);
        label.setSelectable(true);
        label.setMultiline(true);
        label.setText(relName);
        setMidLabel(label);
    }

    /**
     * @return The source Type of this link.
     */
    public String getFromType() {
        JGoPort port = getFromPort();
        if (port instanceof RelationshipJGoPort) {
            return ((RelationshipJGoPort) port).getNode().getType();
        } else {
            return null;
        }
    }

    /**
     * @return The target Type of this link.
     */
    public String getToType() {
        JGoPort port = getToPort();
        if (port instanceof RelationshipJGoPort) {
            return ((RelationshipJGoPort) port).getNode().getType();
        } else {
            return null;
        }
    }
}
