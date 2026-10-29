# UDfy Music — Backend

API REST de streaming musical para la comunidad de la Universidad Distrital.
Spring Boot 4.1.x (Java 21), PostgreSQL (Supabase), JWT (HS256) y autorización en dos
niveles: reglas por ruta en Spring Security + `SET LOCAL ROLE` de Postgres según el
rol del JWT.

## Requisitos

- Java 21 (último parche 21.0.x)
- Maven 3.9+
- Una base PostgreSQL con el esquema de UDfy (Supabase pooler, puerto 5432, SSL)

## Variables de entorno

Toda la configuración sensible va por variables de entorno. Crea un archivo `.env`
en la raíz de `backend/` (formato `CLAVE=valor`, no va al repo):

| Variable | Descripción |
| --- | --- |
| `DB_URL` | JDBC: `jdbc:postgresql://<host>:5432/postgres?sslmode=require` |
| `DB_USER` | Usuario de conexión del backend |
| `DB_PASSWORD` | Contraseña del usuario de conexión |
| `JWT_SECRET` | Secreto HS256, mínimo 32 bytes |
| `ADMIN_EMAIL` | (Opcional) Email de la cuenta ADMIN sembrada al arrancar |
| `ADMIN_PASSWORD` | (Opcional) Contraseña de esa cuenta |
| `PORT` | (Opcional) Puerto del servidor, por defecto 8080 |

Consulta `.env.example` como plantilla. El backend carga `.env` con
`spring.config.import=optional:file:.env[.properties]`.

## Correr el proyecto

```bash
mvn spring-boot:run
```

El servidor queda en `http://localhost:8080` (o el puerto de `PORT`).

## Tests

```bash
mvn verify
```

Son pruebas unitarias (JUnit + Mockito) sin contexto Spring y sin tocar la base de
Supabase.

## Documentación (Swagger)

Con el servidor corriendo:

- UI: `http://localhost:8080/swagger-ui`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Endpoints y roles

| Método y ruta | Rol |
| --- | --- |
| `POST /api/auth/login`, `POST /api/auth/registro/oyente`, `POST /api/auth/registro/artista` | público |
| `GET/PUT /api/usuarios/me` | OYENTE |
| `POST /api/reproducciones`, `GET /api/reproducciones/me` | OYENTE |
| `GET /api/recomendaciones/me` | OYENTE |
| `GET /api/artistas/me/top-canciones` | ARTISTA |
| `GET /api/reportes/reproducciones-por-cancion`, `/minutos-por-oyente`, `/minutos-por-genero`, `/reproducciones-por-mes`, `/usuarios-por-anio` | ANALISTA o ADMIN |
| `POST/GET /api/cuentas`, `PUT /api/cuentas/{id}/rol` | ADMIN |
| `GET /api/generos`, `GET /api/artistas`, `GET /api/canciones/**` | autenticado |
| `POST/PUT /api/generos`, `DELETE /api/artistas/**` | ADMIN |
| `POST/PUT /api/canciones`, `PUT /api/artistas/me` | ARTISTA |

El JWT trae los claims `sub` (id_cuenta), `rol`, `id_usuario` o `id_artista`; la
propiedad de los recursos se resuelve siempre con el id del token, nunca del body ni
de la URL.

## Roles de BD (SET LOCAL ROLE)

Cada método `@Transactional` de los services (excepto login y registro) ejecuta
`SET LOCAL ROLE` con un rol fijo según el claim `rol` del JWT:
`OYENTE→rol_oyente`, `ARTISTA→rol_artista`, `ANALISTA→rol_analista`,
`ADMIN→rol_admin`. Los permisos reales los aplica Postgres; si un rol no tiene un
GRANT, el backend traduce el SQLState `42501` a HTTP 403. **No** se modifican GRANTs
ni roles desde el código.

## Chequeo de dependencias vulnerables (OWASP)

El plugin `dependency-check-maven` está en el `pom.xml` pero **no** corre en
`mvn verify` (sin `<executions>`). Para ejecutarlo a mano:

```bash
export NVD_API_KEY=<clave gratuita de https://nvd.nist.gov/developers/request-an-api-key>
mvn org.owasp:dependency-check-maven:check -DnvdApiKey=$NVD_API_KEY
```

**Importante:** desde la versión 13.x el plugin exige una clave API de NVD; sin ella
falla con "Invalid API Key". Solicita una clave gratuita (tarda minutos) o configura
un espejo con `nvdDatafeedUrl`. El reporte queda en
`target/dependency-check-report.html`.

Alternativa rápida sin plugin: `mvn dependency:list` y revisar las versiones frente a
los avisos de cada proyecto (Spring, PostgreSQL, Nimbus JOSE).

## Errores

Todas las respuestas de error siguen el formato
`{ "timestamp", "status", "error", "message", "path" }` y los códigos de Postgres se
traducen a HTTP (p. ej. `23503→409`, `42501→403`). Los mensajes son en español.
