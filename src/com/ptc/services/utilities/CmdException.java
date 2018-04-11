package com.ptc.services.utilities;

public class CmdException extends Exception {

    private static final long serialVersionUID = 3290083837380510265L;
    private String message;

    public CmdException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
