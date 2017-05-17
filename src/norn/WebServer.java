package norn;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
    
    // AF(PORT, server, environment) = a web server connected to HTTP server server
    //                                 that accepts connections at port number PORT
    //                                 and maintains list name definitions in environment
    // RI: true
    // Protection from rep exposure: all mutable fields are private and final, and PORT is final
    //                               HTTP message-passing only with web clients
    //                               environment passed to backend methods that does not expose to client
    // Thread safety argument: environment is the only shared mutable structure
    //                         all accesses and mutations to environment acquire lock on the environment object.
    //
    
    
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
        final String path = exchange.getRequestURI().getPath();
        final String expression = path.substring(exchange.getHttpContext().getPath().length());
        
        String response;
        // Get recipients of list expression from this GET request
        try {
            Set<Recipient> recipients = parseInput(expression);
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
        PrintWriter out = new PrintWriter(exchange.getResponseBody(), true);
        out.println(response);
        out.close();
    }
    
    /**
     * Parses input stream into valid list expression
     * @param in InputStream from http request where
     *  in contains list-expression is a list expression as defined in Norn2, 
     *  but with all whitespace omitted.
     * @return Set<Recipient> representing recipients specified by evaluated list expression
     * @throws IllegalArgumentException 
     */
    private Set<Recipient> parseInput(String expression) {
        ListExpression parsed = ListExpression.parse(expression);
        Set<Recipient> recipients;
        synchronized (environment) {
            recipients = parsed.recipients(environment);
        }
        return recipients;
    }
    
    /**
     * @return current server environment of list names
     */
    public Environment getEnvironment(){
        return environment;     
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
