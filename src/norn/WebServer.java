package norn;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 
 */
public class WebServer {
    public static final int PORT = 5021;
    final HttpServer server;
    // Have an environment field
    
    /**
     * 
     * @throws IOException
     */
    public WebServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        addContext("/eval/", new EvalHandler());
    }
    
    /**
     * 
     */
    public void start() {
        server.start();
    }
    
    /**
     * 
     */
    public void stop() {
        server.stop(0);
    }
    
    /**
     * 
     * @return
     */
    public int port() {
        return PORT;
    }

    public void addContext(String prefix, HttpHandler handler) {
        server.createContext(prefix, handler);
    }
}
