package com.ptc.services.utilities.docgen.models.relationship;

import static com.ptc.services.utilities.docgen.Constants.REPORT_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import com.ptc.services.utilities.docgen.IntegrityType;
import static com.ptc.services.utilities.docgen.utils.Logger.exception;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author dsouza
 *
 */
public class RelationshipModel extends Frame {

    private static final long serialVersionUID = 7897471239923L;

    public RelationshipModel() {
        setBounds(32, 32, 128, 128);
    }

    public void display(IntegrityType type, LinkedHashMap<String, List<String>> relationshipFields) {
        RelationshipJGoModel relModel = new RelationshipJGoModel(type, relationshipFields);
        RelationshipJGoView relView = new RelationshipJGoView(relModel);
        add(relView);
        relModel.open();
        relView.performLayout();
        try {
            File imagesDir = new File(REPORT_DIR.getAbsolutePath() + fs + "WorkflowDocs" + fs + "Types");
            // Create a directory if the images directory does not exist
            if (!imagesDir.isDirectory()) {
                imagesDir.mkdirs();
            }
            // Create a jpeg file to save the workflow image
            FileOutputStream fos = new FileOutputStream(imagesDir.getAbsolutePath() + fs + type.getPosition() + "_Relationships.jpeg");
            relView.produceImage(fos, "jpeg");
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            exception(java.util.logging.Level.SEVERE, 1, ioe);
        }
    }

}
