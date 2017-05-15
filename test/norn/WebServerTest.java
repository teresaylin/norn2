package norn;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

/*
 * Tests the web server with multiple web users and console users.
 */
public class WebServerTest {
    /*
     * Testing strategy:
     *  GET requests:
     *      valid GET request
     *      invalid GET request
     *          invalid list expression
     *          invalid request in general
     *  Number of users:
     *      Web users: 0, 1 (cannot test multiple web users)
     *      Console users: 0, 1, >1
     */
    
    // tests valid GET request, 1 web user
    @Test
    public void testValid() throws IOException {
        final WebServer server = new WebServer();
        server.start();

        final String valid = "http://localhost:" + server.port() + "/eval/tlin15@mit.edu";
        
        URL url;
        // in case request contains non-ASCII characters
        try {
            url = new URL(new URI(valid).toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            url = new URL(valid);
        }

        // in this test, we will just assert correctness of the server's output
        final InputStream input = url.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        assertEquals("<a href=mailto:tlin15@mit.edu>email these recipients</a>", reader.readLine());
        assertEquals("<br>", reader.readLine());
        assertEquals("tlin15@mit.edu", reader.readLine());
        assertEquals("end of stream", null, reader.readLine());
        server.stop();
    }
    
    // tests invalid GET request (invalid expression after port)
    @Test
    public void testInvalid() throws IOException {
        final WebServer server = new WebServer();
        server.start();

        final String invalid = "http://localhost:" + server.port() + "/???/!!!";
        URL url;
        // in case request contains non-ASCII characters
        try {
            url = new URL(new URI(invalid).toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            url = new URL(invalid);
        }

        // in this test, we will just assert correctness of the response code
        // unfortunately, an unsafe cast is required here to go from general
        //   URLConnection to the HTTP-specific HttpURLConnection that will
        //   always be returned when we connect to a "http://" URL
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        assertEquals("response code", 404, connection.getResponseCode());
        server.stop();
    }
    
    // tests invalid GET request (invalid list expression)
    @Test
    public void testEvalInvalidListExpression() throws IOException {
        final WebServer server = new WebServer();
        server.start();

        final String invalid = "http://localhost:" + server.port() + "/eval/tlin15@";
        URL url;
        // in case request contains non-ASCII characters
        try {
            url = new URL(new URI(invalid).toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            url = new URL(invalid);
        }

        final InputStream input = url.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        assertEquals("Invalid list expression (after http://localhost ... eval/). Please change to a valid list expression."
                + "For valid list expressions, see specifications for Norn1 and Norn2.", reader.readLine());
        assertEquals("tlin15@mit.edu", reader.readLine());
        assertEquals("end of stream", null, reader.readLine());
        server.stop();
    }    
    
    // tests 0 console users, >1 web user
    @Test
    public void testMultipleWebUser() throws IOException {
        final WebServer server = new WebServer();
        server.start();

        final String addr1 = "http://localhost:" + server.port() + "/eval/a=b@c,d@e;b";
        final String addr2 = "http://localhost:" + server.port() + "/eval/b=a";
        final String addr3 = "http://localhost:" + server.port() + "/eval/a=x@y";
        final String addr4 = "http://localhost:" + server.port() + "/eval/b";
        
        URL url1, url2, url3, url4;
        try {
            url1 = new URL(new URI(addr1).toASCIIString());
            url2 = new URL(new URI(addr2).toASCIIString());
            url3 = new URL(new URI(addr3).toASCIIString());
            url4 = new URL(new URI(addr4).toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            url1 = new URL(addr1);
            url2 = new URL(addr2);
            url3 = new URL(addr3);
            url4 = new URL(addr4);
        }
        
        // reading input1
        final InputStream input1 = url1.openStream();
        final BufferedReader reader1 = new BufferedReader(new InputStreamReader(input1));
        assertEquals("<a href=mailto:{}>email these recipients</a>", reader1.readLine());
        assertEquals("<br>", reader1.readLine());
        assertEquals("{}", reader1.readLine());
        assertEquals("end of stream", null, reader1.readLine());
        
        // reading input2
        final InputStream input2 = url2.openStream();
        final BufferedReader reader2 = new BufferedReader(new InputStreamReader(input2));
        assertEquals("<a href=mailto:b@c,d@e>email these recipients</a>", reader2.readLine());
        assertEquals("<br>", reader2.readLine());
        assertEquals("b@c, d@e", reader2.readLine());
        assertEquals("end of stream", null, reader2.readLine());
        
        // reading input3
        final InputStream input3 = url3.openStream();
        final BufferedReader reader3 = new BufferedReader(new InputStreamReader(input3));
        assertEquals("<a href=mailto:x@y>email these recipients</a>", reader3.readLine());
        assertEquals("<br>", reader3.readLine());
        assertEquals("x@y", reader3.readLine());
        assertEquals("end of stream", null, reader3.readLine());
        
        // reading input4
        final InputStream input4 = url4.openStream();
        final BufferedReader reader4 = new BufferedReader(new InputStreamReader(input4));
        assertEquals("<a href=mailto:x@y>email these recipients</a>", reader1.readLine());
        assertEquals("<br>", reader1.readLine());
        assertEquals("x@y", reader1.readLine());
        assertEquals("end of stream", null, reader1.readLine());

        server.stop();
    }
    
    
    // tests 1 console user, 0 web users
    @Test
    public void testSingleConsoleUser() throws IOException {
        
    }
    
    // tests > 1 console user, 0 web users
    @Test
    public void testMultipleConsoleUsers() throws IOException {
        
    }
    
    // tests >1 console users, 1 web users
    @Test
    public void testMultipleConsoleAndWebUsers() throws IOException {
        
    }
}
