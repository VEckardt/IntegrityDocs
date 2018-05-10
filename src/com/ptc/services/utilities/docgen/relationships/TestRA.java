/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.relationships;

import com.ptc.services.utilities.docgen.RelationshipAnalyser;
import com.mks.api.response.APIException;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.SimpleItem;
import com.ptc.services.utilities.docgen.utils.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author veckardt
 */
public class TestRA {

    public static void main(String[] args) throws APIException, IntegrityException {

        Logger.init();
        Integrity i = new Integrity();

        List<SimpleItem> items = new ArrayList<>();
        RelationshipAnalyser ra = new RelationshipAnalyser(i, "MKS Solution");
        
        
        

        // out.println(gc.getId());
    }

}
