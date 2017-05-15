package norn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    // TODO write AF, RI, Rep exposure
    
    /**
     * Creates an HTTP server to connect with web clients.
     * @throws IOException
     */
    public WebServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        addContext("/eval/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                createResponse(exchange);
            }
        });
        this.environment = new Environment();
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
    
    /**
     * Parses input stream into valid list expression
     * @param in InputStream from http request where
     *  in contains list-expression is a list expression as defined in Norn2, 
     *  but with all whitespace omitted.
     * @return Set<Recipient> representing recipients specified by evaluated list expression
     * @throws IOException 
     */
    private Set<Recipient> parseInput(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        ListExpression parsed = ListExpression.parse(reader.readLine()); 
        return parsed.recipients(environment); 
    }
    
    /**
     * @return current server environment of list names
     */
    public Environment getEnvironment(){
        return new Environment();
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

    /**
     * Creates context mapping URIs to handlers.
     * @param prefix of path
     * @param handler of path HTTP information
     */
    public void addContext(String prefix, HttpHandler handler) {
        server.createContext(prefix, handler);
    }
    
    /**
     * Reads in a file and parses the file, if the file contains a valid list expression.
     * 
     * @param file The file to be loaded. Cannot contain newlines. 
     * The contents of the file should be a single valid list expression. 
     */
    public void load(File file) {
        try {
            environment.load(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
    
    /**
     * Saves all currently-defined named lists to a file.
     * @param filename The name of the file to be created and written to
     */
    public void save(String filename) {
        environment.save(filename);
    }

}
