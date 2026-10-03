package dev.magadiflo.banking.app.customer.infrastructure.config;

import dev.magadiflo.banking.app.customer.application.helper.CustomerApplicationHelper;
import dev.magadiflo.banking.app.customer.application.mapper.CustomerApplicationMapper;
import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.application.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerBeanConfig {
    @Bean
    public CustomerService customerService(CustomerRepositoryPort repositoryPort) {
        return new CustomerService(
                repositoryPort,
                new CustomerApplicationMapper(),
                new CustomerApplicationHelper()
        );
    }
}
