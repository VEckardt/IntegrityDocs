/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.Constants.CONTENT_DIR;
import static com.ptc.services.utilities.docgen.Constants.fs;
import com.ptc.services.utilities.docgen.utils.Logger;
import static java.lang.System.out;
import java.util.Iterator;

/**
 *
 * @author veckardt
 */
public class GatewayTest {

    public static void main(String[] args) throws APIException {
        Logger.init();
        Integrity i = new Integrity();
        // WorkItemIterator wit = i.getGatewayConfigs("export", "exporter", "");
        WorkItemIterator wit = i.getGatewayConfigs("parser", "parser", CONTENT_DIR + fs + "GatewayImportConfigs");
        while (wit.hasNext()) {
            WorkItem gc = wit.next();
            out.println(gc.getId());
            Iterator it = gc.getFields();
            while (it.hasNext()) {
                Field fld = (Field)it.next();
                out.println(" "+fld.getName()+ ": "+fld.getValueAsString());
            }
            
        }
    }
}
