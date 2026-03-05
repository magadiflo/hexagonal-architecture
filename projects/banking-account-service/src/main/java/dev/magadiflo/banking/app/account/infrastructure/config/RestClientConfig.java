package dev.magadiflo.banking.app.account.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${exchange-rate.api.base-url}")
    private String baseUrl;

    @Value("${exchange-rate.api.api-key}")
    private String apiKey;

    @Value("${exchange-rate.api.timeout-seconds}")
    private int timeoutSeconds;

    @Bean
    public RestClient exchangeRateRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(this.timeoutSeconds));
        factory.setReadTimeout(Duration.ofSeconds(this.timeoutSeconds));

        return RestClient.builder()
                .baseUrl(this.baseUrl + "/" + this.apiKey)
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
