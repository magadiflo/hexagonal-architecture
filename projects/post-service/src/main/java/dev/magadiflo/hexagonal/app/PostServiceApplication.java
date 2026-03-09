package dev.magadiflo.hexagonal.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// [DEPRECADO desde Spring Cloud OpenFeign 5.0.0 / Spring Cloud 2025.1.0]
// Anteriormente en Spring Cloud OpenFeign 4.x (Spring Boot 3.x), esta anotación
// era obligatoria para que Spring escaneara y registrara las interfaces anotadas
// con @FeignClient como beans en el contexto de Spring.
//
// A partir de Spring Cloud OpenFeign 5.0.0, esta anotación fue eliminada como
// parte de la remoción de deprecaciones. El escaneo y registro de los clientes
// Feign ahora se realiza automáticamente via auto-configuración de Spring Boot,
// por lo que agregar @EnableFeignClients provoca un error de bean duplicado:
// "The bean could not be registered. A bean with that name has already been
// defined and overriding is disabled."
//
// Con Spring Cloud 2025.1.0 + Spring Boot 4.x ya no es necesario ni válido
// usar esta anotación. Las interfaces @FeignClient son detectadas automáticamente.
//
//import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableFeignClients
@SpringBootApplication
public class PostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }

}
