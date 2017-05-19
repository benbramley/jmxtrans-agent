package org.jmxtrans.agent.splunk;

import java.net.URLConnection;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *  @author <a href="mailto:benbramley@gmail.com">Ben Bramley</a>
 */
public class SplunkHECConnection {

    private static final String HttpContentType = "application/json; profile=urn:splunk:event:1.0; charset=utf-8";
    private static final String AuthorizationHeaderScheme = "Splunk %s";
    private static final String AuthorizationHeaderTag = "Authorization";
 
    public String doPost(String url, String token, String event) throws Exception {

        StringBuilder result = new StringBuilder();
        URL hecurl = new URL(url);
        
        URLConnection conn;
        conn = hecurl.openConnection();
        
        conn.setDoOutput(true);

        conn.setRequestProperty(AuthorizationHeaderTag, String.format(AuthorizationHeaderScheme, token));
        conn.setRequestProperty("Content-Type", HttpContentType);
        conn.setRequestProperty("Connection", "Keep-Alive");

        conn.connect();

        OutputStream output = conn.getOutputStream();
        output.write(event.getBytes(Charset.forName("UTF-8")));
        output.flush();

        InputStream is;

        is = conn.getInputStream();
        
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } finally {
            is.close();
        }
        
        return result.toString();

    }    

}