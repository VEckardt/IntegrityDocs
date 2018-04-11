package com.ptc.services.utilities.docgen.models.workflow;

import java.awt.Point;
import com.nwoods.jgo.JGoPort;
import com.nwoods.jgo.JGoStroke;

/**
 * A self-loop in the entity workflow that represents a self transition.
 */
public class WorkflowJGoSelfLoop extends WorkflowJGoLink {

    private static final long serialVersionUID = 72342456L;

    /**
     * Constructor, use WorkflowJGoFactory.createLink() instead.
     *
     * @param node The node that gets the self-loop.
     * @see WorkflowJGoFactory#createLink
     */
    public WorkflowJGoSelfLoop(WorkflowJGoNode node) {
        // NOTE: toPort is a dummy.
        // It has to be different from the other port.
        super(node.getPort(), new JGoPort());
        setResizable(false);
    }

    /**
     * Over-rides the method in parent class, in order for getFromType() and
     * getToType() to return the same Type.
     *
     * @see WorkflowJGoLink#getToType
     */
    public String getToType() {
        return getFromType();
    }

    /**
     * Over-rides the method in ancestor class, in order for getFromPort() and
     * getToPort() to return the same port.
     *
     * @see JGoPort#getToPort
     */
    public JGoPort getToPort() {
        return getFromPort();
    }

    /**
     * Calculates the shape of this self-loop. Over-rides the default behavior,
     * which is to draw a straight line, in JGoLink.
     *
     * @see JGoLink#calculateStroke
     */
    protected void calculateStroke() {
        // The self-loop is calculated from the center of the node.
        foredate(JGoStroke.ChangedAllPoints);
        setSuspendUpdates(true);
        removeAllPoints();

        Point p1 = new Point(10, 0);
        Point p2 = new Point(10, -25);
        Point p3 = new Point(-10, -25);
        Point p4 = new Point(-10, 0);

        // Center of the node.
        int x = getFromPort().getLocation().x + getFromPort().getWidth() / 2;
        int y = getFromPort().getLocation().y;

        p1.translate(x, y);
        p2.translate(x, y);
        p3.translate(x, y);
        p4.translate(x, y);

        insertPoint(0, (int) p1.getX(), (int) p1.getY());
        insertPoint(1, (int) p2.getX(), (int) p2.getY());
        insertPoint(2, (int) p3.getX(), (int) p3.getY());
        insertPoint(3, (int) p4.getX(), (int) p4.getY());

        // The problem with setCubic() is that, although the link looks
        // nice, it's selection points are computed from the straight
        // line link. So the user has to deliberately click next to the
        // link, in order to select it. We turn it off for now.
        setCubic(true);

        setSuspendUpdates(false);
        update(JGoStroke.ChangedAllPoints, 0, null);
    }
}
