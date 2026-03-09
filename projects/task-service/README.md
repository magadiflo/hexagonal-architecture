# [Arquitectura Hexagonal](https://www.youtube.com/watch?v=JD_ZL3Bnaog)

- Plataforma: [Youtube](https://www.youtube.com/watch?v=JD_ZL3Bnaog)
- Canal: `Daniel Españadero`

---

## Teoría

- La arquitectura hexagonal se adhiere perfectamente a cada uno de los principios SOLID
- La arquitectura hexagonal está dividida en 3 capas de lo más interno a lo externo son: **dominio, aplicación y
  infraestructura.**

- **Regla de dependencia:** Las capas interiores no pueden depender de capas exteriores. En pocas palabras, la
  comunicación
  siempre será de las capas Externas a las Internas
- **Arquitectura Hexagonal** o llamada también Arquitectura de **Puertos y Adaptadores.**

### Puertos y Adaptadores

- **Puertos:** permiten comunicar cada una de las capas de la aplicación. Por ejemplo, si queremos acceder a algún dato
  de la capa de aplicación, desde la capa de infraestructura, pues lo hacemos a través de un puerto. ¿Qué son los
  puertos?, en esta arquitectura serían sencillamente interfaces.

- **Adaptadores:** puerta de comunicación con aplicaciones externas. Es decir, es cualquier cosa que tenga que ver con
  algo externo a nuestra aplicación, por ejemplo: una conexión con base de datos, una petición a un servicio REST
  externo, etc. Esto se realiza desde la capa de infraestructura.

### Capas

- **Dominio:** capa más interna del software, es el core. Cualquier cosa que cambiemos en las otras capas, no tiene que
  alterar esta capa, ya que esta capa no debe depender de ninguna otra capa si estamos siguiendo correctamente la regla
  de dependencia. Qué nos podemos encontrar en esta capa: Modelos (Usuario, Cliente, etc). Cuando queremos implementar
  una lógica que no es lógica de negocio sino una lógica interna de la aplicación, es posible que lo encontremos en la
  capa de Dominio, otra cosa que podemos encontrar son puertos (interfaces) para que desde la capa de Aplicación podamos
  acceder a ellos. Otra definición que podría causar confusión es que la capa de dominio es donde se
  "define" la lógica de negocio, no donde se implementa ya que donde se implementa es en la capa de aplicación.

- **Aplicación:** es la capa intermedia, aquí se da la implementación de los servicios, la lógica de negocio y podemos
  encontrar puertos, los que son usados para poder acceder desde la capa de infraestructura.

- **Infraestructura:** Responsable de gestionar la comunicación con servicios externos, por ejemplo: BBDD, Api REST,
  interfaces de usuario, etc. Aquí podemos encontrar Controladores, entidades, mapeos de estas entidades con modelos,
  peticiones a api rest a través de adaptadores.

**Core**

> El core en la arquitectura de software se refiere al núcleo central de un sistema, donde se encuentran los componentes
> y módulos esenciales que ejecutan las funciones principales del software. Esta parte central se encarga de coordinar
> y gestionar las interacciones entre las diversas capas y elementos del sistema, asegurando la eficiencia, la
> escalabilidad y estabilidad de la aplicación, facilitando la integración de nuevas funcionalidades y permitiendo una
> fácil mantenibilidad y actualización del software a lo largo del tiempo.

