/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

import com.mks.api.response.APIException;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static java.lang.System.out;

/**
 *
 * @author veckardt
 */
public class GatewayTest {

    public static void main(String[] args) throws APIException {
        Integrity i = new Integrity();
        WorkItemIterator wit = i.getGatewayConfigs("export", "exporter", "");
        while (wit.hasNext()) {
            WorkItem gc = wit.next();
            out.println(gc.getId());
        }
    }
}
