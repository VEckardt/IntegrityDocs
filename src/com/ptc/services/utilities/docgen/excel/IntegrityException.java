// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IntegrityException.java
package com.ptc.services.utilities.docgen.excel;

import com.mks.api.response.Response;

public class IntegrityException extends Exception {

    public IntegrityException(String message) {
        super(message);
    }

    public IntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntegrityException(Response response, String message) {
        super(createMessage(response, message));
    }

    private static String createMessage(Response response, String message) {
        if (response == null) {
            return message;
        }
        if (response.getCommandString() == null) {
            return message;
        } else {
            return (new StringBuilder()).append(message).append(" (offending command: ").append(response.getCommandName()).append(")").toString();
        }
    }

    public String getAppendableMessage() {
        return getAppendableMessage(".", ". ");
    }

    public String getAppendableMessage(String resultIfNullOrEmpty, String prefixIfNotNull) {
        if (getMessage() == null || getMessage().length() == 0) {
            return resultIfNullOrEmpty;
        } else {
            return (new StringBuilder()).append(prefixIfNotNull).append(getMessage()).toString();
        }
    }
}
