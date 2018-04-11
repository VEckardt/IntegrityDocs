package com.ptc.services.utilities.docgen.models.relationship;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import javax.swing.UIManager;

import com.nwoods.jgo.JGoListPosition;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoViewListener;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoDocumentEvent;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoViewEvent;
import mks.swing.JGoViewEx;

/**
 * A view that shows the relationships in a graphical format, using the JGo
 * tools.
 */
public class RelationshipJGoView extends JGoViewEx implements JGoViewListener, ComponentListener {

    private static final long serialVersionUID = 82342456L;
    public static JGoPen normalPen = new JGoPen(JGoPen.SOLID, 1, Color.black);
    public static JGoPen noPen = new JGoPen(JGoPen.NONE, 3, Color.cyan);

    /**
     * Constructor.
     *
     * @param doc The model for this view.
     */
    public RelationshipJGoView(JGoDocument doc) {
        super(doc);
        addViewListener(this);
        addComponentListener(this);

        // Set some defaults.
        setDefaultPortGravity(50);
        setIncludingNegativeCoords(false);
        setHidingDisabledScrollbars(true);
        setBorder(UIManager.getBorder("TextField.border"));
    }

    /**
     * @see JGoViewListener#documentChanged
     */
    public void documentChanged(JGoDocumentEvent e) {
        // The default behavior of this method.
        super.documentChanged(e);
    }

    /**
     * Over-rides libjava.JGoViewEx. Fixes "Zoom to Fit" problems. Somehow there
     * were no "Zoom to Fit" problems in HistoryGo, which also extends
     * JGoViewEx. Whatever. Zooming and legend have been very annoying.
     */
    public void setZoom(int percent) {
        zoom = percent;
        if (zoom == 0) {
            setScale(1.);

            // Ensure that legend doesn't affect document size
            if (legend != null) {
                legend.setLeft(0);
                legend.setTop(0);
            }
            // This line is uncommented in JGoViewEx, but we turn it off.
            // It fixes "Zoom to Fit".
            //getDocument().setDocumentSize(getPrintDocumentSize());

            Rectangle r = getViewRect();
            Dimension d = getDocumentSize();
            double s1 = (double) r.width / d.width;
            double s2 = (double) r.height / d.height;
            setScale(s1 < s2 ? s1 : s2);
        } else {
            setScale(zoom / 100.);
        }
    }

    /**
     * Capture various events that happen in this view, and fire WorkflowEvents
     * to interested components.
     *
     * @see JGoViewListener#viewChanged
     */
    public void viewChanged(JGoViewEvent e) {
        if (e.getHint() == JGoViewEvent.POSITION_CHANGED) {
            positionLegend();
        }
    }

    /**
     * Convenience method for getting the document for this view.
     *
     * @return The model for this view.
     */
    public RelationshipJGoModel getDoc() {
        return (RelationshipJGoModel) getDocument();
    }

    /**
     * Opens the relationships model.
     */
    public void open() {
        getDoc().open();
        updateView();
    }

    /**
     * Returns the zoom factor.
     */
    public int getZoom() {
        return zoom;
    }

    /**
     * Runs the layout algorithm. The direction of the layout coincides with the
     * direction on the previous invocation.
     */
    public void performLayout() {
        getDoc().performLayout();
    }

    /**
     * @see ComponentListener#componentResized
     */
    public void componentResized(ComponentEvent e) {
        // If it was zoom to fit -- then re-zoom to fit.
        if (zoom == 0) {
            setZoom(zoom);
        }
        positionLegend();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    /**
     * Hook from JGoViewEx.
     *
     * @return Print header: type @ [hostname:port].
     */
    protected String getPrintHeader() {
        return "no print in headless mode";
    }

    /**
     * Remove listeners, etc.
     */
    public void dispose() {
    }

    /**
     * Override the preferred size. We are always within a JSplitPane which has
     * priority for us. If we return our actual document size, which is the
     * default, then the JSplitPane will give us priority over the other
     * component, which it will shrink down to its minimum size. Sine we never
     * exist except in the component which will grow us in preference, the
     * preferred size will never actually get used to size us.
     */
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Over-ride for JGoView in order to fix the jpeg size when producing the
     * image. Without this over-ride the legend is cut off.
     */
    public java.awt.Dimension getPrintDocumentSize() {
        Dimension docSize = new Dimension();
        double width = 0;
        double height = 0;
        RelationshipJGoModel doc = getDoc();
        for (JGoListPosition pos = doc.getFirstObjectPos(); pos != null; pos = doc.getNextObjectPosAtTop(pos)) {
            JGoObject obj = doc.getObjectAtPos(pos);
            if (obj instanceof RelationshipJGoNode) {
                RelationshipJGoNode node = (RelationshipJGoNode) obj;
                double nWidth = node.getLocation().getX() + node.getWidth();
                double nHeight = node.getLocation().getY() + node.getHeight();
                width = width > nWidth ? width : nWidth;
                height = height > nHeight ? height : nHeight;
            }
            if (obj instanceof RelationshipJGoLink) {
                RelationshipJGoLink link = (RelationshipJGoLink) obj;
                double lWidth = link.getLocation().getX() + link.getWidth();
                double lHeight = link.getLocation().getY() + link.getHeight();
                width = width > lWidth ? width : lWidth;
                height = height > lHeight ? height : lHeight;
            }
        }
        // Pad the final size by 1 so that the line does not get cut off.        
        docSize.setSize(width + 1, height + 1);
        return docSize;

    }
}
