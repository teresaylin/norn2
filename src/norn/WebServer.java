package norn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Server to handle web client requests to ListExpression system.
 */
public class WebServer {
    public static final int PORT = 5021;
    private final HttpServer server;
    private final Environment environment;
    
    /**
     * Creates an HTTP server to connect with web clients.
     * @throws IOException
     */
    public WebServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        addContext("/eval/", new EvalHandler());
        this.environment = new Environment();
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
     * Creates and manages server.
     * @param args
     */
    public static void main(String[] args) {
        WebServer server;
        try {
            server = new WebServer();
            server.start();
            // do stuff
            server.stop();
        } catch (IOException e) {
            System.out.println("Error creating server: " + e.getMessage());
        }
    }
}
