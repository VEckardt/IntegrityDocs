/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import com.ptc.services.utilities.docgen.IntegrityAdminObject;

/**
 *
 * @author veckardt
 */
public class Utils {

    public static String getObjectName(IntegrityAdminObject ao) {
        String className = "";

        if (ao != null) {
            className = ao.getClass().getSimpleName().replace("Integrity", "");
            // System.out.println("className = " + className);
        }
        return className;
    }
}
