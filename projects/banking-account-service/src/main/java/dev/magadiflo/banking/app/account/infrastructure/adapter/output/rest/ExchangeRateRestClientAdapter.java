package dev.magadiflo.banking.app.account.infrastructure.adapter.output.rest;

import dev.magadiflo.banking.app.account.application.port.output.ExchangeRatePort;
import dev.magadiflo.banking.app.account.infrastructure.adapter.output.rest.dto.ExchangeRateApiResponse;
import dev.magadiflo.banking.app.shared.exception.ExchangeRateUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExchangeRateRestClientAdapter implements ExchangeRatePort {

    private final RestClient exchangeRateRestClient;

    @Override
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency) {

        try {
            ExchangeRateApiResponse response = this.exchangeRateRestClient
                    .get()
                    .uri("/pair/{base}/{target}", baseCurrency, targetCurrency)
                    .retrieve()
                    .body(ExchangeRateApiResponse.class);

            if (Objects.isNull(response) || !"success".equals(response.result())) {
                throw new ExchangeRateUnavailableException(baseCurrency, targetCurrency);
            }

            return response.conversionRate();

        } catch (RestClientException e) {
            log.error("Error al consultar tipo de cambio {}/{}: {}", baseCurrency, targetCurrency, e.getMessage());
            throw new ExchangeRateUnavailableException(baseCurrency, targetCurrency);
        }
    }
}
