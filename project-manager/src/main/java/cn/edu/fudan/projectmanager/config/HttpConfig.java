package cn.edu.fudan.projectmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * @author WZY
 * @version 1.0
 **/
@Configuration
public class HttpConfig {

    @Value("${inner.header.key}")
    private String headerKey;
    @Value("${inner.header.value}")
    private String headerValue;

    @Bean("httpHeader")
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(headerKey, headerValue);
        return headers;
    }
}
