package dev.magadiflo.banking.app.account.application.helper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountApplicationHelper {
    /**
     * Genera el número de cuenta con formato: BNK-XXXXXXXXXXXXXX
     * Ejemplo: BNK-20250001234567
     * Los 14 dígitos son: año(4) + número secuencial random(10)
     */
    public String generateAccountNumber() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long random = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
        return "BNK-%s%d".formatted(year, random);
    }

    /**
     * Genera el número de referencia de transacción: TXN-XXXXXXXXXXXXXX
     * Ejemplo: TXN-20250001234567
     */
    public String generateReferenceNumber() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long random = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
        return "TXN-%s%d".formatted(year, random);
    }
}
