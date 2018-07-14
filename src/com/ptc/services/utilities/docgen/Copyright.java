/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen;

/**
 *
 * @author veckardt
 */
public class Copyright {

    public static final String iDOCS_REV = "$Revision: 11.0.6.8 $";
    public static final String copyright = "Copyright &copy; 2018 PTC Inc. All rights reserved.";
    public static final String copyrightText = "Copyright (c) 2018 PTC Inc. All rights reserved.";
    public static String programName = "IntegrityDocs";
    public static String author = "Authors: Cletus D'Souza, Volker Eckardt";
    public static String email = "emails: cdsouza@ptc.com, veckardt@ptc.com";

    public static String version = iDOCS_REV.substring(iDOCS_REV.lastIndexOf(':'), iDOCS_REV.lastIndexOf('$'));
}
