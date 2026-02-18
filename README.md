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
de [Construyendo una RESTful API con Spring Boot: Integración de DDD y Arquitectura Hexagonal](https://medium.com/@juannegrin/construyendo-una-restful-api-con-spring-boot-integraci%C3%B3n-de-ddd-y-arquitectura-hexagonal-af824a3a4d05)  
![01.png](assets/01-teoria/01.png)


