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
 * Tests the web server for different evaluation inputs from web clients.
 */
public class WebServerTest {
    /*
     * Testing strategy:
     *  GET requests:
     *      valid GET request
     *      invalid GET request
     *          invalid list expression
     *          invalid request in general
     *  Evaluates:
     *      Undefined list --> {}
     *      Reassigned list
     *      Mail loops --> error message
     *      Operations
     *          Union (,) --> resulting set
     *          Difference (!) --> resulting set
     *          Intersection (*) --> resulting set
     *          Definition (=) --> resulting set
     *          Sequence (;) --> resulting set
     *          Grouping ( ) --> resulting set
     *      Nested subexpressions
     *          Nested union
     *          Nested difference
     *          Nested intersection
     *          Nested definition
     *          Nested sequence
     *          Nested grouping
     *      
     */

    // tests valid GET request, 1 web user
    @Test
    public void testValid() throws IOException {
        final WebServer server = new WebServer(new Environment());
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
        assertEquals("<a href=\"mailto:tlin15@mit.edu\">email these recipients</a><br>tlin15@mit.edu", reader.readLine());
        assertEquals("end of stream", null, reader.readLine());
        server.stop();
    }

    // tests invalid GET request (invalid expression after port)
    @Test
    public void testInvalid() throws IOException {
        final WebServer server = new WebServer(new Environment());
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
        final WebServer server = new WebServer(new Environment());

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
        assertEquals("<p>Invalid list expression (after http://localhost ... eval/). Please change to a valid list expression."
                + " For valid list expressions, see specifications for Norn1 and Norn2.</p>", reader.readLine());
        assertEquals("end of stream", null, reader.readLine());
        server.stop();
    }    

    // tests 0 console users, >1 web user
    // tests undefined list, reassignment, mail loop, union, difference, intersection, sequence, grouping, definition, nested definition
    @Test
    public void testMultipleWebUser() throws IOException {
        final WebServer server = new WebServer(new Environment());

        final String addr1 = "http://localhost:" + server.port() + "/eval/a=b@c,d@e;b";     // definition, union, sequence, undefined list
        final String addr2 = "http://localhost:" + server.port() + "/eval/b=a";             // definition
        final String addr3 = "http://localhost:" + server.port() + "/eval/a=x@y";           // reassignment
        final String addr4 = "http://localhost:" + server.port() + "/eval/b";               
        final String addr5 = "http://localhost:" + server.port() + "/eval/c!(d=b*m@n)";     // difference, intersection, grouping, nested definition 
        final String addr6 = "http://localhost:" + server.port() + "/eval/a=(c=b)";         // mail loop, nested definition

        URL url1, url2, url3, url4, url5, url6;
        try {
            url1 = new URL(new URI(addr1).toASCIIString());
            url2 = new URL(new URI(addr2).toASCIIString());
            url3 = new URL(new URI(addr3).toASCIIString());
            url4 = new URL(new URI(addr4).toASCIIString());
            url5 = new URL(new URI(addr5).toASCIIString());
            url6 = new URL(new URI(addr6).toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            url1 = new URL(addr1);
            url2 = new URL(addr2);
            url3 = new URL(addr3);
            url4 = new URL(addr4);
            url5 = new URL(addr5);
            url6 = new URL(addr6);
        }

        // reading input1
        final InputStream input1 = url1.openStream();
        final BufferedReader reader1 = new BufferedReader(new InputStreamReader(input1));
        assertEquals("<a href=\"mailto:\">email these recipients</a><br>{}", reader1.readLine());
        assertEquals("end of stream", null, reader1.readLine());

        // reading input2
        final InputStream input2 = url2.openStream();
        final BufferedReader reader2 = new BufferedReader(new InputStreamReader(input2));
        assertEquals("<a href=\"mailto:b@c,d@e\">email these recipients</a><br>b@c, d@e", reader2.readLine());
        assertEquals("end of stream", null, reader2.readLine());

        // reading input3
        final InputStream input3 = url3.openStream();
        final BufferedReader reader3 = new BufferedReader(new InputStreamReader(input3));
        assertEquals("<a href=\"mailto:x@y\">email these recipients</a><br>x@y", reader3.readLine());
        assertEquals("end of stream", null, reader3.readLine());

        // reading input4
        final InputStream input4 = url4.openStream();
        final BufferedReader reader4 = new BufferedReader(new InputStreamReader(input4));
        assertEquals("<a href=\"mailto:x@y\">email these recipients</a><br>x@y", reader4.readLine());
        assertEquals("end of stream", null, reader4.readLine());

        // reading input5
        final InputStream input5 = url5.openStream();
        final BufferedReader reader5 = new BufferedReader(new InputStreamReader(input5));
        assertEquals("<a href=\"mailto:\">email these recipients</a><br>{}", reader5.readLine());
        assertEquals("end of stream", null, reader5.readLine());

        // reading input6
        final InputStream input6 = url6.openStream();
        final BufferedReader reader6 = new BufferedReader(new InputStreamReader(input6));
        assertEquals("<p>Oops! You created a mail loop. Mutually recursive definitions are not allowed."
                + " For valid list expressions, see specifications for Norn1 and Norn2.</p>", reader6.readLine());
        assertEquals("end of stream", null, reader6.readLine());

        server.stop();
    }

    // covers nested subexpressions
    @Test
    public void testNestedExpressions() throws IOException {
        final WebServer server = new WebServer(new Environment());

        final String addr1 = "http://localhost:" + server.port() + "/eval/b=((b@c,d@e)!d@e)*f@g;b";     // nested grouping, union, difference, intersection
        final String addr2 = "http://localhost:" + server.port() + "/eval/d=(a;(b;c))";                   // nested sequence
        final String addr3 = "http://localhost:" + server.port() + "/eval/c=x@y";           
        final String addr4 = "http://localhost:" + server.port() + "/eval/d";

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
        assertEquals("<a href=\"mailto:\">email these recipients</a><br>{}", reader1.readLine());
        assertEquals("end of stream", null, reader1.readLine());

        // reading input2
        final InputStream input2 = url2.openStream();
        final BufferedReader reader2 = new BufferedReader(new InputStreamReader(input2));
        assertEquals("<a href=\"mailto:\">email these recipients</a><br>{}", reader2.readLine());
        assertEquals("end of stream", null, reader2.readLine());

        // reading input3
        final InputStream input3 = url3.openStream();
        final BufferedReader reader3 = new BufferedReader(new InputStreamReader(input3));
        assertEquals("<a href=\"mailto:x@y\">email these recipients</a><br>x@y", reader3.readLine());
        assertEquals("end of stream", null, reader3.readLine());

        // reading input4
        final InputStream input4 = url4.openStream();
        final BufferedReader reader4 = new BufferedReader(new InputStreamReader(input4));
        assertEquals("<a href=\"mailto:x@y\">email these recipients</a><br>x@y", reader4.readLine());
        assertEquals("end of stream", null, reader4.readLine());

        server.stop();
    }
}
