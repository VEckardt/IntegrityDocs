package com.ptc.services.utilities;

public class CmdException extends Exception {

   private static final long serialVersionUID = 3290083837380510265L;
   private final String message;

   public CmdException(String message) {
      this.message = message;
   }

   @Override
   public String getMessage() {
      return message;
   }
}
