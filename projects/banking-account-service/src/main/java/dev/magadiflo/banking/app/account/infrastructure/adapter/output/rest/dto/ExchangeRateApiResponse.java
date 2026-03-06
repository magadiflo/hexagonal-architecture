package dev.magadiflo.banking.app.account.infrastructure.adapter.output.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ExchangeRateApiResponse(String result,

                                      @JsonProperty("base_code")
                                      String baseCode,

                                      @JsonProperty("target_code")
                                      String targetCode,

                                      @JsonProperty("conversion_rate")
                                      BigDecimal conversionRate) {
}
