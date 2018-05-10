/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.utils;

import static com.ptc.services.utilities.docgen.Constants.nl;
import com.ptc.services.utilities.docgen.IntegrityDocs;

/**
 *
 * @author veckardt
 */
public class Utils {

//    static SimpleDateFormat sdf;
//    public static String getObjectName(IntegrityAdminObject ao) {
//        String className = "";
//
//        if (ao != null) {
//            className = ao.getClass().getSimpleName().replace("Integrity", "");
//            // System.out.println("className = " + className);
//        }
//        return className;
//    }
    public static String appendNewLine(String line) {
        return line + nl;
    }

    public static String cap1stChar(String userIdea) {
        char[] stringArray = userIdea.toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        return new String(stringArray);
    }
}
