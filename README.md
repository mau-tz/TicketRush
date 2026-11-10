# TicketRush

Backend de *reserva y venta de entradas con alta concurrencia*, construido con Spring Boot. Resuelve el problema
clásico de las plataformas de ticketing: *miles de personas intentando comprar los mismos asientos al mismo tiempo, sin
sobreventa y sin cobros duplicados.*

	⁠*Estado:* en desarrollo. Proyecto de aprendizaje enfocado en buenas prácticas de backend.

---

## ¿Qué problema resuelve?

| Problema                                   | Solución en TicketRush                                                    |
|--------------------------------------------|---------------------------------------------------------------------------|
| Dos usuarios compran el mismo asiento      | Control de concurrencia (locking optimista/pesimista) en la base de datos |
| Un usuario reserva y nunca paga            | Reserva temporal con expiración automática (Redis)                        |
| Doble clic o reintento cobra dos veces     | Pagos idempotentes                                                        |
| Enviar emails/tickets hace lenta la compra | Procesamiento asíncrono basado en eventos                                 |
| "¿Aguanta la carga?"                       | Pruebas de carga con k6 y métricas en Grafana                             |

## Funcionalidades

•⁠  ⁠Registro e inicio de sesión con *JWT* y roles (⁠ ADMIN ⁠, ⁠ ORGANIZADOR ⁠, ⁠ CLIENTE ⁠).
•⁠  ⁠Gestión de eventos y mapa de asientos.
•⁠  ⁠Reserva temporal de asientos (ej. 10 minutos).
•⁠  ⁠Pago simulado con idempotencia (integración opcional con Stripe en modo test).
•⁠  ⁠Generación de ticket con código QR y notificación por email.
•⁠  ⁠API documentada con OpenAPI/Swagger.
•⁠  ⁠Métricas y salud de la app con Actuator, Prometheus y Grafana.

## Arquitectura

*Monolito modular con arquitectura hexagonal (puertos y adaptadores)* y comunicación entre módulos mediante **eventos
de dominio**.


Cliente → [Adapter In: REST Controller]
↓
[Puerto In: Caso de uso]
↓
[Dominio: reglas de negocio puras]
↓
[Puerto Out: interfaces]
↓
[Adapter Out: JPA / Redis / RabbitMQ / Email]


Regla principal: *las dependencias solo apuntan hacia adentro* (⁠ infrastructure → application → domain ⁠). El dominio
no conoce Spring, JPA ni Redis.

### Módulos

| Módulo         | Responsabilidad                                         |
|----------------|---------------------------------------------------------|
| ⁠ auth ⁠         | Usuarios, autenticación y autorización                  |
| ⁠ events ⁠       | Eventos, recintos y asientos                            |
| ⁠ booking ⁠      | Reservas y control de concurrencia (núcleo del sistema) |
| ⁠ payment ⁠      | Pagos e idempotencia                                    |
| ⁠ notification ⁠ | Emails y generación de tickets                          |
| ⁠ shared ⁠       | Configuración, excepciones y eventos comunes            |

### Estructura de paquetes


src/main/java/com/mautz/ticketrush/
├── shared/
├── auth/          ┐
├── events/        │  cada módulo:
├── booking/       │  domain / application / infrastructure
├── payment/       │
└── notification/  ┘


## Stack tecnológico

| Área                           | Tecnología                                  |
|--------------------------------|---------------------------------------------|
| Lenguaje / Framework           | Java 24, Spring Boot, Maven                 |
| Persistencia                   | PostgreSQL, Spring Data JPA, Flyway         |
| Seguridad                      | Spring Security, JWT                        |
| Caché y expiración de reservas | Redis (planificado)                         |
| Mensajería                     | RabbitMQ o Kafka (planificado)              |
| Testing                        | JUnit 5, Mockito, Testcontainers            |
| Contenedores                   | Docker, Docker Compose                      |
| CI/CD                          | GitHub Actions (planificado)                |
| Observabilidad                 | Actuator, Prometheus, Grafana (planificado) |
| Pruebas de carga               | k6 (planificado)                            |
| Documentación                  | OpenAPI / Swagger                           |

## Cómo ejecutarlo

*Requisitos:* JDK 26, Maven (o el wrapper incluido) y Docker en ejecución.

⁠ bash
# 1. Clonar
git clone <url-del-repositorio>
cd TicketRush

# 2. Levantar la app (Spring Boot levanta PostgreSQL con compose.yaml automáticamente)
./mvnw spring-boot:run

# 3. Ejecutar tests (usan Testcontainers, requieren Docker)
./mvnw test
 ⁠

Documentación de la API (cuando esté disponible): ⁠ http://localhost:8080/swagger-ui.html ⁠

## Roadmap

•⁠  ⁠[x] Proyecto base con Spring Boot, PostgreSQL y Docker Compose
•⁠  ⁠[ ] Migraciones con Flyway y modelo de datos
•⁠  ⁠[ ] Módulo ⁠ events ⁠ (eventos y asientos)
•⁠  ⁠[ ] Autenticación con JWT y roles
•⁠  ⁠[ ] Módulo ⁠ booking ⁠ con control de concurrencia
•⁠  ⁠[ ] Expiración de reservas con Redis
•⁠  ⁠[ ] Pagos idempotentes
•⁠  ⁠[ ] Eventos asíncronos con RabbitMQ (emails y tickets QR)
•⁠  ⁠[ ] Tests de integración con Testcontainers
•⁠  ⁠[ ] CI con GitHub Actions
•⁠  ⁠[ ] Pruebas de carga (k6) y dashboard en Grafana
•⁠  ⁠[ ] Deploy y documentación final

## Decisiones técnicas

Las decisiones de diseño importantes se documentarán en ⁠ docs/decisiones/ ⁠
(por ejemplo: ¿por qué locking optimista?, ¿por qué monolito modular y no microservicios?).

## Autor

*Mauricio Teran Zavala* — [GitHub](https://github.com/mau-tz) · [LinkedIn](https://linkedin.com/in/mau-tz)
