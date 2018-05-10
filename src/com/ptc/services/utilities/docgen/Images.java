/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.Constants.fs;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author veckardt
 */
public class Images extends ArrayList implements WorkItemIterator {

    int len = 0;

    @Override
    public WorkItem next() throws APIException {
        return (SimpleItem) this.get(len++);
    }

    @Override
    public boolean hasNext() {
        return len < this.size();
    }

    @Override
    public WorkItem getLast() {
        return (SimpleItem) this.get(size() - 1);
    }

    /**
     *
     * @param path
     * @return
     */
    public static Images getImages(String path) {
        Images ims = new Images();
        int cnt = 0;
        File filePath = new File(path);
        File[] listOfFiles = filePath.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fname = "images" + fs + file.getName();
                    SimpleItem itm = new SimpleItem("Image", file.getName());
                    itm.add("id", String.valueOf(++cnt));
                    itm.add("name", file.getName());
                    itm.add("image", fname);
                    ims.add(itm);
                }
            }
        } else {
            log ("No images!!!  :(");
        }

        return ims;
    }
}
