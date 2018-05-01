/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.excel;

import com.mks.api.response.APIException;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.SimpleItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class TestRA {

    public static void main(String[] args) throws APIException, IntegrityException {
        Integrity i = new Integrity();

        List<SimpleItem> items = new ArrayList<>();
        RelationshipAnalyser ra = new RelationshipAnalyser(i, items);

        // out.println(gc.getId());
    }

}
