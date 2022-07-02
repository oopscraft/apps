package org.oopscraft.apps.core.support;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;

public class RestTemplateFactory {

    private static final int CONNECT_TIMEOUT = 1000;

    private static final int READ_TIMEOUT = 1000*10;

    /**
     * getRestTemplate
     * @return
     */
    public static RestTemplate getRestTemplate() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            clientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
            clientHttpRequestFactory.setReadTimeout(READ_TIMEOUT);
            clientHttpRequestFactory.setHttpClient(httpClient);
            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
            return restTemplate;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
