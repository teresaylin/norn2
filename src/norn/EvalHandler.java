/* Copyright (c) 2007-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Multi-client ListExpression-evaluating web interface.
 */
public class EvalHandler implements HttpHandler {

    private static final String MAIL_TO_DELIMITER = ",";
    private static final String RECIPIENT_LIST_DELIMITER = ", ";
    private static final String LINE_BREAK = "<br>";

    // TODO modify this part
    // AF(serverSocket) = serverSocket is channel through which server receives HTML messages from client 
    //
    // Rep invariant: true
    //
    // Protected from rep exposure: fields are private and final except for numPlayers,
    //          which each new connection should update for all players.
    //
    // Thread safety 
    //   each client can only access the shared environment through parse(), which is synchronized
    //   fields are private and final

    /**
     * Verifies the rep invariant.
     */
    private void checkRep(){
        assert true;
    }

     @Override
     /**
      * Handles HTTP requests from web.
      * @param exchange to connect server with requests and responses
      */
     public void handle(HttpExchange exchange) throws IOException {
         // call parseInput and createResponse
         createResponse(exchange);
     }

     /**
      * Parses input stream into valid list expression
      * @param in InputStream from http request where
      *  in contains list-expression is a list expression as defined in Norn2, 
      *  but with all whitespace omitted.
      * @return Set<Recipient> representing recipients specified by evaluated list expression
      */
     private Set<Recipient> parseInput(InputStream in) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         ListExpression parsed = ListExpression.parse(reader.readLine()); // TODO make sure global env modified by new defs
         return parsed.recipients(environment); // TODO resolve environment issue
         // TODO environment should be a field of WebServer since all clients share the same environment
     }

     /**
      * Writes output to exchange
      * @param parsed parsed mailing list
      * @param exchange the HttpExchange to write the response to
     *  @throws IOException 
      */
     private void createResponse(HttpExchange exchange) throws IOException {
         // Get recipients of list expression from this GET request
         Set<Recipient> recipients = parseInput(exchange.getRequestBody());
         // Create mailto and recipient lists for output
         String mailToList = "";
         String recipientList = "";
         for (Recipient r : recipients) {
             mailToList += r + MAIL_TO_DELIMITER;
             recipientList += r + RECIPIENT_LIST_DELIMITER;
         }
         // Remove trailing commas
         mailToList = mailToList.substring(0, mailToList.length() - MAIL_TO_DELIMITER.length());
         recipientList = recipientList.substring(0, recipientList.length() - RECIPIENT_LIST_DELIMITER.length());
         // Create mailto message
         String mailToMessage = "<a href=\"mailto:" + mailToList + "\">email these recipients</a>";
         // Create full response message
         String response = mailToMessage + LINE_BREAK + recipientList;
         // Set exchange headers
         exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8"); // TODO put type of response
         exchange.sendResponseHeaders(200, 0);
         // Write the message
         PrintWriter out = new PrintWriter(exchange.getResponseBody(), true);
         out.println(response);
         out.close();
     }
}
