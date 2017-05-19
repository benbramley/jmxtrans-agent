package org.jmxtrans.agent.splunk;

import org.jmxtrans.agent.util.json.JsonObject;

/**
 *  @author <a href="mailto:benbramley@gmail.com">Ben Bramley</a>
 */
public class SplunkEventBuilder {

    private String time;
    private String host;
    private String index;
    private String source;
    private String sourcetype;
    private String appid;
    private String jvmid;

    public SplunkEventBuilder(String host, String index, String source, String sourcetype, String appid, String jvmid)
    {
        this.host = host;
        this.index = index;
        this.source = source;
        this.sourcetype = sourcetype;
        this.appid = appid;
        this.jvmid = jvmid;
    }

    public String getTime()
    {
        return time;
    }
    public String getHost()
    {
        return host;
    }
    public String getIndex()
    {
        return index;
    }
    public String getSource()
    {
        return source;
    }
    public String getSourceType()
    {
        return sourcetype;
    }

    public String getAppId()
    {
        return appid;
    }

    public String getJvmId()
    {
        return jvmid;
    }

    private static void addIfPresent(JsonObject event, String key, String value) {
        if (value != null && value != "null" && value.length() > 0) {
            event.add(key, value);
        }
    }

    public String buildEvent(String metricName, Object value, long timestamp)
    {
        String valueToString = "null";
        if (value != null ) { 
            valueToString = value.toString();
        }

        JsonObject jsonEvent = new JsonObject();
        jsonEvent.add("time", timestamp);
        addIfPresent(jsonEvent, "host", getHost());
        addIfPresent(jsonEvent, "index", getIndex());
        addIfPresent(jsonEvent, "source", getSource());
        addIfPresent(jsonEvent, "sourcetype", getSourceType());

        JsonObject jsonBody = new JsonObject();
        addIfPresent(jsonBody, "jvm.app.id", getAppId());
        addIfPresent(jsonBody, "jvm.id", getJvmId());
        addIfPresent(jsonBody, metricName, valueToString);
        
        jsonEvent.add("event", jsonBody);

        return jsonEvent.toString();
    }
    
    
}