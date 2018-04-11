package com.ptc.services.utilities.docgen.models.relationship;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.Dimension;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoView;
import com.nwoods.jgo.JGoDrawable;
import com.nwoods.jgo.JGoText;
import com.nwoods.jgo.JGoBrush;
import com.nwoods.jgo.JGoArea;
import com.nwoods.jgo.JGoRoundRect;

/**
 * A node in the entity relationship that a type
 */
public class RelationshipJGoNode extends JGoArea {

    private static final long serialVersionUID = 12342456L;
    public static JGoBrush shadowBrush;
    private static int DEFAULT_LENGTH = 80;

    // Our ellipse.
    private JGoDrawable visibleObj;
    private JGoDrawable visibleDblObj; // dbl outline
    //Our shadow for the ellipse.
    private JGoDrawable myShadow;
    // Our label. Shows the node's name.
    private JGoText myLabel;
    // Our port. All links connect to this port.
    private RelationshipJGoPort myPort;

    // Boolean to return whether or not this node is being duplicated to represent
    // Self relationships
    private boolean relatedToSelf = false;

    // Color for the node
    private Color nodeColor;

    /**
     * Constructor.
     *
     * @param type The Type name for this node
     */
    public RelationshipJGoNode(String type, Color color) {
        super();

        // Set the color
        nodeColor = color;

        // Set the length and height of the ellipse.
        setSize(DEFAULT_LENGTH, DEFAULT_LENGTH / 2);

        // The area as a whole is not directly selectable using a mouse,
        // but the area can be selected by trying to select any of its
        // children, all of whom are currently !isSelectable().
        setSelectable(false);
        setGrabChildSelection(true);
        setDraggable(false);
        setResizable(false);
        myLabel = new JGoText(type);
        myLabel.setSelectable(false);
        myLabel.setDraggable(false);
        myLabel.setMultiline(false);
        myLabel.setAlignment(JGoText.ALIGN_CENTER);
        myLabel.setTransparent(true);
        myLabel.setBold(true);

        myPort = new RelationshipJGoPort(this);

        setShape();
        addObjectAtTail(myLabel);
        addObjectAtTail(myPort);

        visibleObj.setPen(RelationshipJGoView.normalPen);
        visibleDblObj.setPen(RelationshipJGoView.normalPen);
    }

    /**
     * Adjusts the length of the node to account for long labels.
     */
    private void updateLength() {
        int newWidth = myLabel.getWidth() + 10 > DEFAULT_LENGTH ? myLabel.getWidth() + 10 : DEFAULT_LENGTH;
        visibleObj.setWidth(newWidth);
        visibleDblObj.setWidth(visibleObj.getWidth() - 10);
        visibleDblObj.setHeight(visibleObj.getHeight() - 10);
        myShadow.setWidth(visibleObj.getWidth());
        getPort().setWidth(visibleObj.getWidth());

    	// Leave the ellipse where it is, and place the
        // other stuff relative to the ellipse.
        myPort.setSpotLocation(JGoObject.Center, visibleObj, JGoObject.Center);
        myShadow.setLeft(visibleObj.getLeft() + 4);
        myShadow.setTop(visibleObj.getTop() + 4);
        myLabel.setSpotLocation(JGoObject.Center, visibleObj, JGoObject.Center);
        visibleDblObj.setSpotLocation(JGoObject.Center, visibleObj, JGoObject.Center);
    }

    /**
     * @return The Type name associated with this node.
     */
    public String getType() {
        return myLabel.getText();
    }

    public Color getColor() {
        return RelationshipJGoView.normalPen.getColor();
    }

    /**
     * Changes the name of the Type
     */
    public void setType(String type) {
        myLabel.setText(type);
        updateLength();
    }

    /**
     * Get the port. The input and output ports are the same which means that
     * both the incoming and outgoing links connect to the same port.
     *
     * @return The only port of this node.
     */
    public RelationshipJGoPort getPort() {
        return myPort;
    }

    /**
     * Two WorkflowJGoNode objects are equal if their Type names are equal.
     */
    public boolean equals(Object obj) {
        return getType().equalsIgnoreCase(((RelationshipJGoNode) obj).getType())
                && isSelfRelationship() == ((RelationshipJGoNode) obj).isSelfRelationship();
    }

    public boolean isSelfRelationship() {
        return relatedToSelf;
    }

    public void setSelfRelatedNode() {
        relatedToSelf = true;
    }

    /**
     * Set node colors.
     *
     * @see JGoStroke#paint
     */
    public void paint(Graphics2D g, JGoView view) {
        JGoBrush b = visibleObj.getBrush();
        if (b == null) {
            Rectangle rect = getBoundingRect();
            b = new JGoBrush(new GradientPaint(rect.x + rect.width / 2, rect.y, Color.white,
                    rect.x + rect.width / 2, rect.y + rect.height, nodeColor));
            visibleObj.setBrush(b);
        }
        myShadow.setBrush(shadowBrush);
        super.paint(g, view);
    }

    /**
     * Sets node shape. In WorkflowJGoPort we need to add code that computes the
     * link entry point, similarly to the ellipse (see
     * WorkflowJGoPort#getLinkPointFromPoint).
     */
    private void setShape() {
        // Save old visible obj and restore it later on new visible obj.
        Point oldVisibleObjLoc = new Point();
        if (visibleObj != null) {
            oldVisibleObjLoc = visibleObj.getLocation();
        }
        removeObject(visibleObj);
        removeObject(myShadow);
        Dimension arcDim = new Dimension(15, 15);
        visibleObj = new JGoRoundRect(getTopLeft(), getSize(), arcDim);
        visibleDblObj = new JGoRoundRect(getTopLeft(), getSize(), arcDim);
        myShadow = new JGoRoundRect(getTopLeft(), getSize(), arcDim);

        // Ellipse.
        visibleObj.setSelectable(false);
        visibleObj.setDraggable(false);
        visibleObj.setSpotLocation(JGoObject.Center, new Point());

        // Restore location.
        visibleObj.setLocation(oldVisibleObjLoc);

        // Shadow.
        myShadow.setSelectable(false);
        myShadow.setDraggable(false);
        myShadow.setPen(RelationshipJGoView.noPen);

        // Double border
        visibleDblObj.setSelectable(false);
        visibleDblObj.setDraggable(false);

        // Port.
        myPort.setSize(visibleObj.getSize());

        // Add all the children to the area.
        addObjectAtHead(visibleObj);
        addObjectAtHead(myShadow);

        updateLength();
    }
}
