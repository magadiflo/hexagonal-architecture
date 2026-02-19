# 🏛️ Arquitectura Hexagonal (Puertos y Adaptadores)

Marco teórico elaborado por Martín Díaz (02/2026) utilizando IAs (ChatGPT, Copilot, Gemini, Claude).

---

## 📚 ¿Qué es la Arquitectura Hexagonal?

La `Arquitectura Hexagonal`, también conocida como `Arquitectura de Puertos y Adaptadores`, fue propuesta por
`Alistair Cockburn` en 2005. Su objetivo principal es **aislar completamente la lógica de negocio de los detalles
técnicos externos** (bases de datos, frameworks, APIs externas, interfaces de usuario, etc.).

> 💡 `Idea central`: Tu lógica de negocio no debería saber si está siendo llamada por una API REST, un mensaje de cola,
> una interfaz gráfica o un test unitario. Tampoco debería saber si los datos se guardan en `PostgreSQL`, `MongoDB`
> o en memoria.

La arquitectura establece que:

- El `núcleo` de la aplicación `(dominio + aplicación)` es completamente `independiente` del mundo exterior.
- La comunicación con el exterior se hace a través de `interfaces bien definidas` llamadas `Puertos`.
- Los detalles técnicos externos se implementan como `Adaptadores` que cumplen esas interfaces.

## 🔷 ¿Por qué se llama "Hexagonal"?

El hexágono es solo una metáfora visual, `no tiene un significado matemático especial`. Cockburn lo eligió simplemente
porque:

- Permite dibujar múltiples "lados" (entradas y salidas) de manera simétrica.
- Visualmente, representa que la aplicación puede tener múltiples formas de entrada y salida al mismo nivel de
  importancia.
- A diferencia de la arquitectura en capas (que sugiere una jerarquía vertical), el hexágono sugiere igualdad entre los
  adaptadores.

No significa que existan exactamente 6 lados técnicos. Lo importante es el concepto:
> 💡 El núcleo está en el centro y todo lo externo se conecta a través de puertos.

Imagen extraída
de [Construyendo una RESTful API con Spring Boot: Integración de DDD y Arquitectura Hexagonal (Juan Negrin)](https://medium.com/@juannegrin/construyendo-una-restful-api-con-spring-boot-integraci%C3%B3n-de-ddd-y-arquitectura-hexagonal-af824a3a4d05)  
![01.png](assets/01-teoria/01.png)

## 🔑 Conceptos Clave

### 🟣 Dominio

El `dominio` es el corazón de tu aplicación. Contiene las reglas de negocio puras, sin ninguna dependencia de
frameworks, bases de datos ni librerías externas.

> 📌 `Ejemplo`: En un sistema bancario, la regla `"no se puede transferir más dinero del que hay en cuenta"` es lógica
> de dominio pura. No importa si viene de una REST API o de un batch nocturno.

### 🔵 Puertos

- Los `puertos` son `interfaces` (contratos) que definen cómo se comunica el dominio con el mundo exterior. Son
  simplemente interfaces de `Java/Kotlin/etc`. que viven en la capa de dominio o aplicación.
- Definen `qué` quiere hacer la aplicación, pero no `cómo` se hace.

Existen dos tipos:

| Tipo                | También llamado              | Dirección     | ¿Quién lo define?  | ¿Quién lo implementa?            |
|---------------------|------------------------------|---------------|--------------------|----------------------------------|
| `Puerto de Entrada` | Driving Port / Inbound Port  | Hacia adentro | Capa de Aplicación | El propio caso de uso (Use Case) |
| `Puerto de Salida`  | Driven Port  / Outbound Port | Hacia afuera  | Capa de Aplicación | Adaptadores de Infraestructura   |

#### Puerto de Entrada — Ejemplo:

````java
// Puerto que define lo que puede hacer el mundo exterior con nuestros casos de uso
public interface CreateUserUseCase {
    UserResponse execute(CreateUserCommand command);
}
````

#### Puerto de Salida — Ejemplo:

````java
// Puerto que define lo que necesita el dominio del mundo exterior (ej: persistencia)
public interface UserRepository {
    Optional<User> findById(Long userId);

    User save(User user);
}
````

> 💡 `Clave`: Los puertos de salida son interfaces que viven en la capa de `aplicación/dominio`, pero su implementación
> vive en `infraestructura`. Esto es la `inversión de dependencias (DIP)`.

### 🟢 Adaptadores

Los `adaptadores` son las `implementaciones concretas` que conectan el mundo exterior con los puertos.
Hay dos tipos que corresponden a los puertos:

| Tipo                   | También llamado                   | Ejemplo                                        |
|------------------------|-----------------------------------|------------------------------------------------|
| `Adaptador de Entrada` | Driving Adapter / Inbound Adapter | Controlador REST, Consumidor Kafka, Scheduler  |
| `Adaptador de Salida`  | Driven Adapter / Outbound Adapter | Repositorio JPA, Cliente HTTP, Sender de Email |

#### Adaptador de Entrada — Ejemplo:

````java
// El controlador REST es un adaptador de entrada
@RestController
public class UserController {

    private final CreateUserUseCase createUserUseCase; // Usa el puerto de entrada

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = UserMapper.toCommand(request);
        return ResponseEntity.ok(createUserUseCase.execute(command));
    }
}
````

#### Adaptador de Salida — Ejemplo:

````java
// La implementación JPA es un adaptador de salida que implementa el puerto UserRepository
@Repository
public class UserJpaAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        return UserMapper.toDomain(jpaRepository.save(entity));
    }
}
````
