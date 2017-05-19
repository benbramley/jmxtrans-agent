package org.jmxtrans.agent.splunk;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.HashMap;
import java.util.Map;

import org.jmxtrans.agent.testutils.FixedTimeClock;
import org.jmxtrans.agent.util.time.Clock;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


/**
 *  @author <a href="mailto:benbramley@gmail.com">Ben Bramley</a>
 */

public class SplunkHECOutputWriterTest {

    private static final String HttpEventCollectorUriPath = "/services/collector/event/1.0";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().wireMockConfig().dynamicPort().dynamicHttpsPort());

    @Test
    public void simpleRequest() throws Exception {
        Map<String, String> s = new HashMap<>();
        s.put("url", "http://localhost:" + wireMockRule.port());
        s.put("token", "6F0D57E5-D40A-4B18-B110-3FF2A22A9DB0");
        s.put("index", "main");
        s.put("source", "jmx_trans");
        s.put("sourcetype", "jmx");
        s.put("hostname", "localhost");
        s.put("appid", "myapp");
        s.put("jvmid", "myjvm");

        stubFor(post(urlPathEqualTo(HttpEventCollectorUriPath)).willReturn(aResponse().withStatus(200)));
        SplunkHECOutputWriter writer = new SplunkHECOutputWriter();
        writer.postConstruct(s);
        writer.writeQueryResult("foo", null, 1);
        writer.postCollect();
        verify(postRequestedFor(urlPathEqualTo(HttpEventCollectorUriPath))
                                .withRequestBody(containing("\"foo\":\"1")));
        
    }    

}