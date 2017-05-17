package norn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import lib6005.parser.UnableToParseException;

/**
 * Server to handle web client requests to ListExpression system.
 */
public class WebServer {
    public static final int PORT = 5021;
    private final HttpServer server;
    private final Environment environment;
    private static final String MAIL_TO_DELIMITER = ",";
    private static final String RECIPIENT_LIST_DELIMITER = ", ";
    private static final String LINE_BREAK = "<br>";
    
    // TODO write AF, RI, Rep exposure
    
    /**
     * Creates an HTTP server to connect with web clients.
     * @throws IOException
     */
    public WebServer(Environment environment) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        System.out.println("server created on port " + PORT);
        server.createContext("/eval/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                System.out.println("GOT HERE");
                createResponse(exchange);
            }
        });
        server.start();
        this.environment = environment;
    }
    
    /**
     * Writes output to exchange
     * @param parsed parsed mailing list
     * @param exchange the HttpExchange to write the response to
    *  @throws IOException 
     */
    private void createResponse(HttpExchange exchange) throws IOException {
        System.out.println("got here");
        final String path = exchange.getRequestURI().getPath();
        final String expression = path.substring(exchange.getHttpContext().getPath().length());
        
        String response;
        // Get recipients of list expression from this GET request
        try {
            Set<Recipient> recipients = parseInput(expression);
            System.out.println("found recipients");
            System.out.println(recipients);
            // Create mailto and recipient lists for output
            String mailToList = "";
            String recipientList;
            if (recipients.size() == 0) {
                recipientList = "{}";
            } else {
                recipientList = "";
                for (Recipient r : recipients) {
                    mailToList += r + MAIL_TO_DELIMITER;
                    recipientList += r + RECIPIENT_LIST_DELIMITER;
                }
                // Remove trailing commas
                mailToList = mailToList.substring(0, mailToList.length() - MAIL_TO_DELIMITER.length());
                recipientList = recipientList.substring(0, recipientList.length() - RECIPIENT_LIST_DELIMITER.length());
                
            }
            
            System.out.println(mailToList);
            
            // Create mailto message
            String mailToMessage = "<a href=\"mailto:" + mailToList + "\">email these recipients</a>";
            // Create full response message
            response = mailToMessage + LINE_BREAK + recipientList;
        } catch (IllegalArgumentException e) {
            response = "<p>Invalid list expression (after http://localhost ... eval/). Please change to a valid list expression."
                + " For valid list expressions, see specifications for Norn1 and Norn2.</p>";
        }
        
        // Set exchange headers
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        // Write the message
        System.out.println("writing message");
        PrintWriter out = new PrintWriter(exchange.getResponseBody(), true);
        out.println(response);
        System.out.println("print response" + response);
        out.close();
    }
    
    /**
     * Parses input stream into valid list expression
     * @param in InputStream from http request where
     *  in contains list-expression is a list expression as defined in Norn2, 
     *  but with all whitespace omitted.
     * @return Set<Recipient> representing recipients specified by evaluated list expression
     * @throws IOException 
     */
//    private Set<Recipient> parseInput(InputStream in) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        ListExpression parsed = ListExpression.parse(reader.readLine()); 
//        return parsed.recipients(environment); 
//    }
    
    private Set<Recipient> parseInput(String expression) {
        try {
            ListExpression parsed = ListExpression.parse(expression);
            return parsed.recipients(environment);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid expression");
        } 
    }
    
    /**
     * @return current server environment of list names
     */
    public Environment getEnvironment(){
        return environment;     // TODO rep exposure? 
    }
    
    /**
     * Starts the WebServer for client connection.
     */
    public void start() {
        server.start();
    }
    
    /**
     * Closes WebServer service.
     */
    public void stop() {
        server.stop(0);
    }   
    
    /**
     * @return port number at which server listens for connections
     */
    public int port() {
        return PORT;
    }
}
