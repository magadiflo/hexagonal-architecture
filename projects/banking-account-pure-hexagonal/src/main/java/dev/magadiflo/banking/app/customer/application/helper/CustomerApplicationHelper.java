package dev.magadiflo.banking.app.customer.application.helper;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class CustomerApplicationHelper {
    public String generateCustomerCode() {
        int year = LocalDateTime.now().getYear();
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "CUS-%d-%d".formatted(year, random);
    }
}
