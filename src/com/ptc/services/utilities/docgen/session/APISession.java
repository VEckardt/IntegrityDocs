/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen.session;

import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.IntegrationPoint;
import com.mks.api.IntegrationPointFactory;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.Session;
import com.mks.api.response.WorkItemIterator;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.IOException;

/**
 * This class represents the Integration Point to a server. It also contains a
 * Session object
 */
public class APISession {

   // Store the API Version
   public static final String VERSION = "4.12";
   public static final int MAJOR_VERSION = 4;
   public static final int MINOR_VERSION = 12;

   // Class variables used to create an API Session
   private final String hostName;
   private final int port;
   private final String userName;

   // API Specific Objects
   private final IntegrationPoint ip;
   private final Session session;
   private CmdRunner icr;

   /**
    * Constructor for the API Session Object for a specific MKS Integrity
    * Application
    *
    * @param app MKS Integrity Application
    * @throws APIException
    */
   public APISession(String app) throws APIException {
      // Create a Local Integration Point
      ip = IntegrationPointFactory.getInstance().createLocalIntegrationPoint(MAJOR_VERSION, MINOR_VERSION);
      // Auto start Integrity Client
      ip.setAutoStartIntegrityClient(true);
      // Create a common Session
      session = ip.getCommonSession();
      // Open a connection to the MKS Integrity Server
      Command iConnect = new Command(app, "connect");
      iConnect.addOption(new Option("gui"));
      CmdRunner cmdRunner = session.createCmdRunner();
      Response res = runCommand(iConnect);
      // Initialize class variables
      hostName = res.getConnectionHostname();
      port = res.getConnectionPort();
      userName = res.getConnectionUsername();
      cmdRunner.release();
   }

   /**
    * This function executes a generic API/CLI Command
    *
    * @param cmd MKS API Command Object representing a CLI command
    * @return MKS API Response Object
    * @throws APIException
    */
   public Response runCommand(Command cmd) throws APIException {

      CmdRunner cmdRunner = session.createCmdRunner();
      cmdRunner.setDefaultHostname(hostName);
      cmdRunner.setDefaultPort(port);
      cmdRunner.setDefaultUsername(userName);
      //cmdRunner.setDefaultPassword(m_password);

      long timestamp;
      String cmdArgs[] = cmd.toStringArray();
      StringBuilder cmdDebug1 = new StringBuilder();
      cmdDebug1.append("Command [ ");
      for (String cmdArg : cmdArgs) {
         cmdDebug1.append(cmdArg).append(' ');
      }
      cmdDebug1.append("] at " + cmdRunner.getDefaultHostname() + ":" + cmdRunner.getDefaultPort());
      StringBuilder cmdDebug2 = new StringBuilder();
      timestamp = System.currentTimeMillis();

      Response res;
      try {
         res = cmdRunner.execute(cmd);
         // ResponseUtil.printResponse(res, 1, System.out);
      } catch (APIException ex) {
         Response response = ex.getResponse();
         if (response != null) {
            WorkItemIterator wii = response.getWorkItems();
            if (wii != null && response.getWorkItemListSize() == 1) {
               try {
                  wii.next();
               } catch (APIException wex) {
                  ex = wex;
               }
            }
         }
         log(cmdDebug1.toString(), 1);
         cmdDebug2.append(ex.getMessage()).append(" (").append(ex.getExceptionId()).append(")");
         timestamp = System.currentTimeMillis() - timestamp;
         cmdDebug2.append("[").append(timestamp).append("ms]");
         log(cmdDebug2.toString(), 1);
         throw ex;
      }
      // in case it was positive
      timestamp = System.currentTimeMillis() - timestamp;
      cmdDebug1.append("[").append(timestamp).append("ms]");
      log(cmdDebug1.toString(), 1);
      cmdRunner.release();
      return res;
   }

   /**
    * This function executes a generic API/CLI Command with interim
    *
    * @param cmd MKS API Command Object representing a CLI command
    * @return MKS API Response Object
    * @throws APIException
    */
//   public Response runXCommandWithInterim(Command cmd) throws APIException {
//      // Terminate the previous command runner, if applicable
//      if (null != icr) {
//         icr.interrupt();
//         icr.release();
//      }
//      icr = session.createCmdRunner();
//      icr.setDefaultHostname(hostName);
//      icr.setDefaultPort(port);
//      icr.setDefaultUsername(userName);
//      long timestamp;
//      String cmdArgs[] = cmd.toStringArray();
//      StringBuilder cmdDebug1 = new StringBuilder();
//      cmdDebug1.append("Command [ ");
//      for (String cmdArg : cmdArgs) {
//         cmdDebug1.append(cmdArg).append(' ');
//      }
//      cmdDebug1.append("]");
//      StringBuilder cmdDebug2 = new StringBuilder();
//      timestamp = System.currentTimeMillis();
//
//      Response res;
//      try {
//         res = icr.executeWithInterim(cmd, false);
//         // ResponseUtil.printResponse(res, 1, System.out);
//      } catch (APIException ex) {
//         Response response = ex.getResponse();
//         if (response != null) {
//            WorkItemIterator wii = response.getWorkItems();
//            if (wii != null && response.getWorkItemListSize() == 1) {
//               try {
//                  wii.next();
//               } catch (APIException wex) {
//                  ex = wex;
//               }
//            }
//         }
//         log(cmdDebug1.toString(), 1);
//         cmdDebug2.append(ex.getMessage()).append(" (").append(ex.getExceptionId()).append(")");
//         timestamp = System.currentTimeMillis() - timestamp;
//         cmdDebug2.append("[").append(timestamp).append("ms]");
//         log(cmdDebug2.toString(), 1);
//         throw ex;
//      }
//      // in case it was positive
//      timestamp = System.currentTimeMillis() - timestamp;
//      cmdDebug1.append("[").append(timestamp).append("ms]");
//      log(cmdDebug1.toString(), 1);
//      return res;
//   }

   /**
    * This function executes a generic API/CLI Command impersonating another
    * user
    *
    * @param cmd MKS API Command Object representing a CLI command
    * @param impersonateUser The user to impersonate
    * @return MKS API Response Object
    * @throws APIException
    */
   public Response runCommandAs(Command cmd, String impersonateUser) throws APIException {

      CmdRunner cmdRunner = session.createCmdRunner();
      cmdRunner.setDefaultHostname(hostName);
      cmdRunner.setDefaultPort(port);
      cmdRunner.setDefaultUsername(userName);
      //cmdRunner.setDefaultPassword(m_password);
      cmdRunner.setDefaultImpersonationUser(impersonateUser);
      Response res = cmdRunner.execute(cmd);
      cmdRunner.release();
      return res;
   }

   /**
    * Terminate the API Session and Integration Point
    *
    * @throws APIException
    * @throws IOException
    */
   public void Terminate() throws APIException, IOException {
      if (null != icr) {
         icr.interrupt();
         icr.release();
      }

      if (null != session) {
         session.release();
      }

      if (null != ip) {
         ip.release();
      }
   }

   public String getHostName() {
      return hostName;
   }

   public String getPort() {
      return String.valueOf(port);
   }

   public String getUserName() {
      return userName;
   }

   public String getConnectionString() {
      StringBuilder connectStr = new StringBuilder();
      connectStr.append(" --hostname=");
      connectStr.append(hostName);
      connectStr.append(" --port=");
      connectStr.append(port);
      connectStr.append(" --user=");
      connectStr.append(userName);
      connectStr.append(" --password=");
      connectStr.append("secret ");
      return connectStr.toString();
   }
}
