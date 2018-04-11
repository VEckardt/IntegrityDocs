package com.ptc.services.utilities.docgen.models.workflow;

import java.awt.Frame;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

import com.mks.api.response.Field;
import com.ptc.services.utilities.docgen.IntegrityDocs;
import com.ptc.services.utilities.docgen.IntegrityType;

/**
 * @author dsouza
 *
 */
public class WorkflowModel extends Frame {

    private static final long serialVersionUID = 7897471239923L;

    public WorkflowModel() {
        setBounds(32, 32, 128, 128);
    }

    public void display(IntegrityType type, Field stateTransitions) {
        WorkflowJGoModel relModel = new WorkflowJGoModel(stateTransitions);
        WorkflowJGoView relView = new WorkflowJGoView(relModel);
        add(relView);
        relModel.open();
        relView.performLayout();
        try {
            File imagesDir = new File(IntegrityDocs.REPORT_DIR.getAbsolutePath() + IntegrityDocs.fs + "WorkflowDocs" + IntegrityDocs.fs + "Types");
            // Create a directory if the images directory does not exist
            if (!imagesDir.isDirectory()) {
                imagesDir.mkdirs();
            }
            // Create a jpeg file to save the workflow image
            FileOutputStream fos = new FileOutputStream(imagesDir.getAbsolutePath() + IntegrityDocs.fs + type.getPosition() + "_Workflow.jpeg");
            relView.produceImage(fos, "jpeg");
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            System.out.println("Caught I/O Exception!");
            ioe.printStackTrace();
        }
    }

}
