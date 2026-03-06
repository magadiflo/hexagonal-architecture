package dev.magadiflo.banking.app.account.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
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
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(this.timeoutSeconds)) // timeout de conexión
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(this.timeoutSeconds));        // timeout de lectura

        return RestClient.builder()
                .baseUrl(String.format("%s/%s", this.baseUrl, apiKey))
                .requestFactory(factory) //Cambio importante a partir de Spring Boot 3.4.x porque es ahí donde se consolida y se vuelve una recomendación oficial y más necesaria
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
