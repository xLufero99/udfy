# UDfy Music Backend

Plataforma de streaming musical para la comunidad de la Universidad Distrital.

## Stack
- Java 21, Spring Boot 4.1.2
- Spring Web, Spring Data JPA, JdbcTemplate, Bean Validation
- Spring Security + OAuth2 Resource Server (JWT HS256)
- springdoc-openapi
- PostgreSQL

## Configuración
1. Copiar `.env.example` a `.env` y completar las variables
2. Ejecutar con `./mvnw spring-boot:run`

## Seguridad
- **Swagger UI debe restringirse antes de desplegar a producción.** Actualmente está expuesto en `/swagger-ui/**` para desarrollo.

## Endpoints (Fase 1)
- `GET /api/health` - Público
- `POST /api/auth/login` - Público
- `POST /api/auth/registro/oyente` - Público
- `POST /api/auth/registro/artista` - Público
