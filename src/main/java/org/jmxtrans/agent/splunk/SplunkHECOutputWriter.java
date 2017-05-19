package org.jmxtrans.agent.splunk;

import org.jmxtrans.agent.AbstractOutputWriter;
import org.jmxtrans.agent.OutputWriter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  @author <a href="mailto:benbramley@gmail.com">Ben Bramley</a>
 */

public class SplunkHECOutputWriter extends AbstractOutputWriter implements OutputWriter {
    
    private SplunkEventBuilder eventBuilder;
    private static final String AuthorizationHeaderTag = "Authorization";
    private static final String AuthorizationHeaderScheme = "Splunk %s";
    private static final String HttpEventCollectorUriPath = "/services/collector/event/1.0";
    private static final String HttpContentType = "application/json; profile=urn:splunk:event:1.0; charset=utf-8";

    private String url = "http://localhost:8088";
    private String token;
    private String index = "main";
    private String source = "jmxtrans_hec";
    private String sourcetype = "jmx";
    private String hostname;
    private String appid;
    private String jvmid;

    public void postConstruct(Map<String, String> settings)
    {
        super.postConstruct(settings);

        url = settings.get("url");
        token = settings.get("token");
        index = settings.get("index");
        source = settings.get("source");
        sourcetype = settings.get("sourcetype");
        hostname = settings.get("hostname");
        appid = settings.get("appid");
        jvmid = settings.get("jvmid");

        eventBuilder = new SplunkEventBuilder(hostname, index, source, sourcetype, appid, jvmid);
        
    }

    @Override
    public void writeInvocationResult(String invocationName, Object value) throws IOException {
        writeQueryResult(invocationName, null, value);
    }

    @Override
    public void writeQueryResult(String metricName, String metricType, Object value) throws IOException {
        
        String event = eventBuilder.buildEvent(metricName, value, 
            TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS));
        
        writeEvent(event);
    }

    private void writeEvent(String event) throws IOException {
        SplunkHECConnection connection = new SplunkHECConnection();
        String hecurl = url + HttpEventCollectorUriPath;
        try {
            connection.doPost(hecurl, token, event);
        }
        catch (Exception  e) {
            throw new RuntimeException("Failed to connect to Splunk HEC: " + url + " Error: " + e.getMessage());

        }

    }
}