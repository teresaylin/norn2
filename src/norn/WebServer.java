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
 * Multi-client ListExpression evaluating web interface.
 */
public class WebServer implements HttpHandler {

    /** Default server port. */
    private static final int DEFAULT_PORT = 5021;

    /** Socket for receiving client connections. */
    private final ServerSocket serverSocket;

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
    
    /**
     * Make a new web server that listens for connections on port.
     * 
     * @param port port number, requires 0 <= port <= 65535
     * @throws IOException if an error occurs opening the server socket
     */
     public WebServer(int port) throws IOException{
         serverSocket = new ServerSocket(port);
     };
    
     @Override
     /**
      * Handles HTTP requests from web.
      * @param exchange to connect server with requests and responses
      */
     public void handle(HttpExchange exchange) throws IOException {
         // have an output stream here for responses
         // call parseInput and createResponse
     }

     /**
      * Parses input stream into valid list expression
      * @param in InputStream from web request
      * @return String representing list expression
      */
     private String parseInput(String in) {
         // Should take in an InputStream
         return "";
     }

     /**
      * Creates an output stream from parsed output
      * @param out parsed mailing list
      * @return output stream to be returned
      */
     private String createResponse(String out) {
         // Should return an OutputStream
        return "";
     }
     
    /**
     * Run the server, listening for and handling client connections.
     * Never returns, unless an exception is thrown.
     * 
     * @throws IOException if an error occurs waiting for a connection
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        while (true) {    
            // block until a client connects
            final Socket socket = serverSocket.accept();
            // create a new thread to handle that client
            new Thread(new Runnable() {
                public void run() {
                    try {
                        try {
                            // Call handle here
                        } finally {
                            socket.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }).start();
            checkRep();
        }
    }


    /**
     * Create and start the web server.
     * 
     * @param args arguments (not supported)
     */
    public static void main(String[] args) {
    }
}
