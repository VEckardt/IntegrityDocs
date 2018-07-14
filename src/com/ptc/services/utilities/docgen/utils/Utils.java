/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen.utils;

import static com.ptc.services.utilities.docgen.Constants.nl;

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
