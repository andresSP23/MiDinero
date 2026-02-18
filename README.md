# Midinero - Backend Financiero de Alto Rendimiento

> Un sistema de gestión financiera robusto, seguro y escalable construido con **Java 21**, **Spring Boot 3** y **PostgreSQL**.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://jdk.java.net/21/) [![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.5-green.svg)](https://spring.io/projects/spring-boot) [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/) [![Security](https://img.shields.io/badge/Security-JWT%20%2B%20Rate%20Limit-red.svg)]()

## Características Destacadas

### Seguridad Avanzada
*   **Autenticación JWT:** Stateless y segura con `io.jsonwebtoken`.
*   **Rate Limiting (Bucket4j):** Protección contra ataques de fuerza bruta y DDOS (50 requests/minuto por IP en endpoints críticos).
*   **Recuperación de Contraseña Segura:** Flujo completo con tokens de un solo uso y expiración en 15 minutos, previniendo ataques de replay.
*   **Validación Estricta:** Uso de **DTOs** (Data Transfer Objects) con `Jakarta Validation` para asegurar la integridad de los datos en la entrada.

### Performance & Optimización
*   **N+1 Solution:** Consultas optimizadas con `JOIN FETCH` usando `JPQL` personalizado en repositorios críticos (Transacciones y Categorías).
*   **Soft Deletes:** Implementación de borrado lógico manteniendo la integridad referencial y performance.

### Comunicación
*   **Motor de Email Asíncrono:** Integración con **Thymeleaf** para el renderizado de plantillas HTML dinámicas (Activación de cuenta, Reset Password).
*   **SMTP Relay:** Configuración lista para producción (Brevo/SendGrid).

---

## Stack Tecnológico

*   **Core:** Java 21, Spring Boot 3.5.10
*   **Bases de Datos:** PostgreSQL
*   **ORM:** Hibernate / Spring Data JPA
*   **Seguridad:** Spring Security 6, JWT (JJWT 0.12.6), Bucket4j 7.6.0
*   **Documentación:** SpringDoc OpenAPI (Swagger UI)
*   **Utilidades:** Lombok, Apache POI (Reportes Excel), Spring Boot Actuator
*   **Testing:** JUnit 5, Mockito, Spring Security Test
*   **Herramientas:** Maven, Docker

---

## Instalación y Configuración Paso a Paso

### 1. Prerrequisitos
Asegúrate de tener instalado lo siguiente:
*   **Java 21 SDK**: [Descargar aquí](https://jdk.java.net/21/)
*   **Maven 3.8+**: (Opcional, el proyecto incluye `mvnw`)
*   **PostgreSQL 15+**: Una instancia local o en Docker.
*   **Cuenta de Brevo (Sendinblue):** Necesaria para el envío de correos (Obtén tu API Key SMTP en Brevo).

### 2. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/midinero-backend.git
cd midinero-backend
```

### 3. Configuración de Variables de Entorno
El proyecto utiliza un archivo `.env` para gestionar secretos de forma segura.

1.  Copia el archivo de ejemplo:
    ```bash
    cp .env.example .env
    ```
    *(En Windows: copia y pega el archivo manual o usa `copy .env.example .env`)*

2.  Edita el archivo `.env` con tus credenciales:
    ```properties
    # Base de Datos
    DB_PASSWORD=tu_password_segura

    # Seguridad (JWT) - Genera una cadena larga aleatoria
    JWT_SECRET=tu_secreto_jwt_muy_largo_y_seguro

    # Correo
    MAIL_FROM=no-reply@midinero.com

    # SMTP (Brevo)
    BREVO_SMTP_USER=tu_usuario_smtp@brevo.com
    BREVO_SMTP_PASS=tu_clave_smtp_master

    # Frontend (CORS)
    CORS_ORIGIN=http://localhost:4200
    ```

### 4. Configuración de Base de Datos
Si tienes Docker instalado, puedes iniciar una base de datos PostgreSQL rápidamente:

```bash
docker run --name midinero-db -e POSTGRES_PASSWORD=tu_password_segura -p 5435:5432 -d postgres:15
```

> **Nota Importante:** El puerto **5435** expuesto en el comando Docker corresponde a la **Base de Datos PostgreSQL**, no a la aplicación Spring Boot. Se utiliza este puerto para evitar conflictos si ya tienes una instancia de Postgres corriendo en el puerto estándar (5432). La aplicación backend está configurada para conectarse automáticamente a `localhost:5435`.

### 5. Compilación y Ejecución
Para iniciar la aplicación usando el wrapper de Maven (recomendado):

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

La aplicación iniciará en el puerto **8081**: `http://localhost:8081`.

---

## Documentación API

La documentación interactiva de la API está disponible via **Swagger UI**:
`http://localhost:8081/api/v1/swagger-ui/index.html#/`

---

## Testing

Ejecutar la suite de pruebas unitarias:
```bash
./mvnw test
```

---

Hecho por [Andrés] - 2026
