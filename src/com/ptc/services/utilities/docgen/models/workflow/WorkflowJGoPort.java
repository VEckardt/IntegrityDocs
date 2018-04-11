package com.ptc.services.utilities.docgen.models.workflow;

import java.awt.Point;
import java.awt.Rectangle;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoPort;

/**
 * An oval shaped port, where the link arrowheads connect to the perimeter of
 * the port.
 */
public class WorkflowJGoPort extends JGoPort {

    private static final long serialVersionUID = 92342456L;

    /**
     * The node that owns us.
     */
    private WorkflowJGoNode node;

    /**
     * Constructor.
     *
     * @param node The parent node for this port.
     */
    public WorkflowJGoPort(WorkflowJGoNode node) {
        super();
        this.node = node;
        setSelectable(false);
        setDraggable(false);
        setStyle(StyleHidden);
        setFromSpot(JGoObject.NoSpot);
        setToSpot(JGoObject.NoSpot);
    }

    /**
     * @return The node that owns this port.
     */
    public WorkflowJGoNode getNode() {
        return node;
    }

    /**
     * Computes the link entry point to this port on the edge of the ellipse.
     * Copied from jgo ../examples/layoutdemo.
     *
     * @see JGoPort#getLinkPointFromPoint
     */
    public Point getLinkPointFromPoint(int x, int y, Point p) {
        if (p == null) {
            p = new Point();
        }

        p.x = x;
        p.y = y;

        WorkflowJGoNode node = getNode();
        Rectangle rect = node.getBoundingRect();

        if (rect.contains(p)) {
            return p;
        }

        int a = rect.width / 2;
        int b = rect.height / 2;

        // bounding rectangle center 
        int cx = getLeft() + getWidth() / 2;
        int cy = getTop() + getHeight() / 2;

        // position of the "outside" point, relative to the bounding rectangle 
        // center. Note if j < 0 that means that point is "below" rectangle
        // otherwise it's "above" it. Also, if i < 0 the point is to the left
        // of the bounding rectangle, otherwise it is to the right
        int i = x - cx;
        int j = y - cy;

        // if i == 0 point is immediately above or below 
        // if j == 0 point is immediately to the right or left
        if (i == 0) {
            i = 1;
        }
        if (j == 0) {
            j = 1;
        }

        // final double Pi = 2.0d * Math.acos(0.0d);
        final double det = Math.atan((double) b / (double) a);

        // get the angle of the line from center to the outside point.
        double q = Math.atan(((double) j) / i);

        if (-det <= q && q <= det) {
            if (i > 0) {
                // if the point is to the right
                // -3 here is for the drop shadow thickness
                p.x = rect.width + rect.x - 3;
                p.y = (int) (rect.y + b + a * Math.tan(q));
            } else {
                // point is to the left 
                p.x = rect.x;
                p.y = (int) (rect.y + b - a * Math.tan(q));
            }
        } else {
            if (j > 0) {
                // the point is above the rect 
                // -3 here is for the drop shadow thickness
                p.y = rect.y + rect.height - 3;
                if (Math.tan(q) == 0) {
                    p.x = rect.x + a;
                } else {
                    p.x = (int) (rect.x + a + b / Math.tan(q));
                }
            } else {
                // point is below the rect 
                p.y = rect.y;

                if (Math.tan(q) == 0) {
                    p.x = rect.x + a;
                } else {
                    p.x = (int) (rect.x + a - b / Math.tan(q));
                }
            }
        }

        return p;
    }

    /**
     * Two WorkflowJGoPort objects are equal if their parent nodes are equal.
     */
    public boolean equals(Object obj) {
        return getNode().equals(((WorkflowJGoPort) obj).getNode());
    }
}
