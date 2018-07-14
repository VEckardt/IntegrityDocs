package com.ptc.services.utilities;

import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class CmdExecutor implements Runnable {

   public static final String NL = System.getProperty("line.separator");
   private Process process;
   private BufferedReader processInputReader;
   private BufferedReader processErrorReader;
   private Thread inputThread;
   private Thread errorThread;
   private StringBuffer buf;

   public CmdExecutor() {
      inputThread = new Thread(this);
      errorThread = new Thread(this);
      buf = new StringBuffer();
   }

   public static void main(String[] args) throws Exception {
      CmdExecutor cmd = new CmdExecutor();
      String cmdStr = new String();
      for (int i = 0; i < args.length; i++) {
         cmdStr += args[i] + ' ';
      }
      log("Command: " + cmdStr + "executed with return code " + cmd.execute(cmdStr) + '.');
   }

   public int execute(String cmd) throws CmdException {
      int exitValue = -1;
      try {
         buf = new StringBuffer();
         process = Runtime.getRuntime().exec(cmd);
         inputThread.start();
         errorThread.start();
         while (inputThread.isAlive() || errorThread.isAlive()) {
            // Wait for process to terminate...
         }

         try {
            exitValue = process.exitValue();
         } catch (Exception e) {
            // Wait for the process to complete...
            process.waitFor();
            // Attempt to get the exit value again				
            exitValue = process.exitValue();
         }
      } catch (Exception e) {
         throw new CmdException(e.getMessage());
      }

      return exitValue;
   }

   public String getCommandOutput() {
      return buf.toString();
   }

   public void run() {
      if (null != process) {
         String line;
         if (Thread.currentThread() == errorThread) {
            processErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            try {
               while (null != (line = processErrorReader.readLine())) {
                  buf.append(line + NL);
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
         if (Thread.currentThread() == inputThread) {
            processInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            try {
               while (null != (line = processInputReader.readLine())) {
                  buf.append(line + NL);
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
}
