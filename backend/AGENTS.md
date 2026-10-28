# UDfy Music: Backend

Plataforma de streaming musical para la comunidad de la Universidad Distrital.
Este repositorio es el backend REST. El frontend se hará después.

## Stack
- Java 21 (último parche 21.0.x), Maven.
- Spring Boot: ÚLTIMA versión estable de la rama 4.1.x (generar el proyecto con
  start.spring.io o verificar en spring.io). No usar release candidates ni milestones.
- Spring Web, Spring Data JPA (CRUD), JdbcTemplate (reportes), Bean Validation,
  Spring Security con spring-boot-starter-oauth2-resource-server (JWT con Nimbus,
  firma HS256), springdoc-openapi, driver PostgreSQL.
- Spring Boot 4 / Spring Security 7 cambian cosas respecto a la 3.x: ante la duda,
  consultar la documentación oficial actual, no asumir de memoria.
- Base de datos: PostgreSQL en Supabase (pooler, puerto 5432, SSL obligatorio).

## Arquitectura (por capas)
controller -> service -> repository, más dto, exception, config y security.
- Los controllers no acceden a repositorios.
- Las entidades JPA nunca salen en las respuestas: usar DTOs (records).
- Cada método de service que toca la BD es @Transactional.
- Paquete base: co.edu.udistrital.udfy (ajustar si el usuario indica otro).

## Configuración y secretos
- Todo por variables de entorno: DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET.
- Archivo .env local (formato KEY=value), cargado con
  spring.config.import=optional:file:.env[.properties]. El .env va en .gitignore.
- NUNCA escribir credenciales, claves ni hashes reales en el código, en tests o en
  archivos versionados. Entregar un .env.example con valores falsos.
- JDBC: jdbc:postgresql://<host>:5432/postgres?sslmode=require

## Modelo de datos (ya existe en Supabase; NO crear ni alterar tablas)
- genero(id_genero PK, nombre UNIQUE)
- artista(id_artista PK, nombre, pais, edad, id_genero FK)
- cancion(id_cancion PK, titulo, id_artista FK, id_genero FK, duracion_segundos,
  fecha_lanzamiento, youtube_id VARCHAR(11) NULL)
- usuario(id_usuario PK, nombre, pais, edad, fecha_registro TIMESTAMPTZ default now())
- reproduccion(id_reproduccion PK, id_usuario FK, id_cancion FK, fecha_reproduccion,
  minutos_reproducidos NUMERIC(6,2), completada BOOLEAN)
- cuenta(id_cuenta PK, email UNIQUE en minúscula, password_hash, rol, id_usuario FK
  nullable, id_artista FK nullable, fecha_creacion).
  rol ∈ OYENTE | ARTISTA | ANALISTA | ADMIN. OYENTE tiene id_usuario, ARTISTA tiene
  id_artista, ANALISTA y ADMIN no tienen ninguno. La tabla `cuenta` solo la lee el
  backend para el login y la gestión de cuentas.
- Todos los id_* son GENERATED ALWAYS: NUNCA enviarlos en un INSERT.
  En JPA usar @GeneratedValue(strategy = GenerationType.IDENTITY) y no mapear el id
  como asignable. Usar spring.jpa.hibernate.ddl-auto=none (el esquema NO lo maneja JPA).
- Roles de Postgres (NOLOGIN): rol_oyente, rol_artista, rol_analista, rol_admin.
  El usuario de conexión del backend puede hacer SET ROLE a cualquiera de ellos.
  NO modificar GRANTs, roles ni constraints por cuenta propia; si hace falta un
  cambio en la BD, proponerlo como script SQL al usuario y esperar.

## Reglas de negocio (las impone la BD; el backend NO las reimplementa)
RN-02/08: canción pertenece a un artista; no se borra un artista con canciones
(ON DELETE RESTRICT). RN-03: duración > 0. RN-09: lanzamiento >= 1900-01-01.
RN-06: reproducción exige usuario y canción existentes. RN-10: trigger que impide
reproducciones anteriores al registro del usuario. edad entre 1 y 119.
- La BD es la fuente de verdad. Solo traducir sus errores a HTTP.
- Validar en Java únicamente lo que la BD no puede: estructura del request (Bean
  Validation) y el formato del link de YouTube.

## Errores (formato único)
Respuesta JSON: { "timestamp", "status", "error", "message", "path" }.
Mapeo desde el SQLState de Postgres:
23505 -> 409 (duplicado) | 23503 -> 409 (referencia/hijos) | 23514 -> 400 (CHECK)
23502 -> 400 (campo obligatorio) | P0001 -> 400 (trigger RN-10)
42501 -> 403 (permiso de rol de BD) | otro -> 500 sin filtrar detalles internos.
Borrar un artista con canciones debe dar 409 con mensaje claro.

## Seguridad
- Login: POST /api/auth/login (email + password) -> JWT con claims: sub (id_cuenta),
  rol, id_usuario o id_artista según corresponda. Expira en 2 horas. Sin refresh tokens.
- Contraseñas con BCrypt. Emails guardados siempre en minúscula.
- Registro público solo para OYENTE y ARTISTA (crea usuario/artista + cuenta en una
  sola transacción). ANALISTA y ADMIN solo los crea un ADMIN.
- Autorización en dos niveles: (1) reglas por endpoint en Spring Security para
  responder 403 rápido; (2) SET LOCAL ROLE de Postgres según el rol del JWT dentro de
  la transacción, para que los permisos reales los aplique la BD.
- Login y registro corren sin SET ROLE (aún no hay sesión).
- Un OYENTE/ARTISTA solo actúa sobre sus propios datos: el id sale SIEMPRE del JWT,
  nunca del body ni de la URL para decidir la propiedad.

## Canciones y YouTube
Se guarda solo youtube_id. El backend extrae el ID del link con regex
(youtube.com/watch?v=, youtu.be/, youtube.com/shorts/, youtube.com/embed/) y valida
^[A-Za-z0-9_-]{11}$. No se descarga ni se convierte audio. El frontend usa el
reproductor embebido oficial.

## Definición de terminado (cada fase)
1. Compila (`mvn -q verify`) y el servidor arranca.
2. Endpoints probados con tests o con ejemplos curl documentados.
3. Documentados en Swagger (/swagger-ui).
4. Sin secretos en el repo; `mvn dependency-check` o equivalente sin vulnerabilidades altas.
5. Al terminar, resumir qué se hizo y qué quedó pendiente.

## Forma de trabajar
Primero mostrar un plan breve y esperar aprobación. No agregar funcionalidades que
no se pidieron. No refactorizar código de fases anteriores sin avisar.
