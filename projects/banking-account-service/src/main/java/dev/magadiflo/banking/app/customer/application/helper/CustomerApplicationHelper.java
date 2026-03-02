package dev.magadiflo.banking.app.customer.application.helper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CustomerApplicationHelper {
    /**
     * Genera el código único de cliente con formato: CUS-YYYY-XXXXXX
     * Ejemplo: CUS-2025-000001
     * La generación del código es responsabilidad de la capa de
     * aplicación, no del dominio ni de la infraestructura.
     */
    public String generateCustomerCode() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "CUS-%s-%d".formatted(year, random);
    }
}
