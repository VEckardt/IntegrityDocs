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
import com.ptc.services.utilities.docgen.IntegrityDocs.Types;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author veckardt
 */
public class GatewayTemplates
        extends ArrayList implements WorkItemIterator {

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
    public static GatewayTemplates getGatewayTemplates(String path1, String path2) {
        GatewayTemplates ims = new GatewayTemplates();
        int cnt = 0;
        File filePath = new File(path2);
        if (filePath.exists()) {
            File[] listOfFiles = filePath.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && (file.getName().toLowerCase().contains(".xsl"))) {
                        String fname = path2 + fs + file.getName();
                        SimpleItem itm = new SimpleItem(Types.GatewayTemplate.name(), file.getName());
                        itm.add("id", String.valueOf(++cnt));
                        itm.add("name", file.getName());
                        itm.add("type", "Import");
                        itm.add("view", Types.GatewayImportConfig.name() + fs + file.getName());
                        ims.add(itm);
                    }
                }
            }
        } else {
            log("INFO: no Gateway Import Templates found.");
        }
        filePath = new File(path1);
        if (filePath.exists()) {
            File[] listOfFiles = filePath.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && (file.getName().toLowerCase().contains(".xls")
                            || file.getName().toLowerCase().contains(".doc"))) {
                        String fname = path1 + fs + file.getName();
                        SimpleItem itm = new SimpleItem(Types.GatewayTemplate.name(), file.getName());
                        itm.add("id", String.valueOf(++cnt));
                        itm.add("name", file.getName());
                        itm.add("type", "Export");
                        itm.add("view", Types.GatewayExportConfig.name() + fs + file.getName());
                        ims.add(itm);
                    }
                }
            }
        } else {
            log("INFO: no Gateway Export Templates found.");
        }

        return ims;
    }
}
