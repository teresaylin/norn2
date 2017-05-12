package norn;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * TODO
 */
public class WebServer {
    public static final int PORT = 5021;
    private final HttpServer server;
    private final Environment environment;
    
    /**
     * TODO
     * @throws IOException
     */
    public WebServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        addContext("/eval/", new EvalHandler());
        this.environment = new Environment();
    }
    
    /**
     * TODO
     */
    public void start() {
        server.start();
    }
    
    /**
     * TODO
     */
    public void stop() {
        server.stop(0);
    }
    
    /**
     * TODO
     * @return
     */
    public int port() {
        return PORT;
    }

    /**
     * TODO
     * @param prefix
     * @param handler
     */
    public void addContext(String prefix, HttpHandler handler) {
        server.createContext(prefix, handler);
    }
    
    /**
     * TODO
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
