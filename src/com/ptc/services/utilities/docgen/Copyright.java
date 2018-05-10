/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen;

/**
 *
 * @author veckardt
 */
public class Copyright {

    public static final String iDOCS_REV = "$Revision: 11.0.6.3 $";
    public static final String copyright = "Copyright &copy; 2018 PTC Inc. All rights reserved.";
    public static final String copyrightText = "Copyright (c) 2018 PTC Inc. All rights reserved.";
    public static String programName = "IntegrityDocs";
    public static String author = "Authors: Cletus D'Souza, Volker Eckardt";
    public static String email = "emails: cdsouza@ptc.com, veckardt@ptc.com";

    public static String version = iDOCS_REV.substring(iDOCS_REV.lastIndexOf(':'), iDOCS_REV.lastIndexOf('$'));
}
