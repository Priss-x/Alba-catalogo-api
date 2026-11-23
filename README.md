# Alba Conecta — Backend - API

API REST de **Alba Conecta**, una aplicación móvil para consultar los productos y programas de Alba Lab y enviar solicitudes de colaboración, como voluntariado, donaciones, alianzas empresariales y consultas por productos o servicios.

El backend está desarrollado en **Java con Spring Boot**. La aplicación Android se conecta a la API mediante **Retrofit**.

- **API desplegada:** [Alba Conecta en Render](https://alba-catalogo-api.onrender.com/api/programas)
- **Repositorio del backend:** [Priss-x/Alba-catalogo-api](https://github.com/Priss-x/Alba-catalogo-api)
- **Repositorio de Android:** [Domz0/Apps-Moviles](https://github.com/Domz0/Apps-Moviles)

## Integrantes

- Priscila Contreras
- José Santander ([Domz0](https://github.com/Domz0))

## Funcionalidades

- Listar los productos del catálogo y los programas de Alba Conecta.
- Registrar cuentas, iniciar sesión y actualizar el perfil.
- Enviar solicitudes como invitado, sin necesidad de registrarse.
- Crear, consultar, editar y eliminar solicitudes con una cuenta. Cada usuario puede acceder únicamente a sus propias solicitudes.

## Tecnologías

- **Java 21 y Spring Boot:** desarrollo de la API REST.
- **Spring Security y BCrypt:** autenticación y protección de contraseñas.
- **Spring Data JPA y PostgreSQL:** almacenamiento de cuentas y solicitudes.
- **H2:** base de datos en memoria para pruebas y ejecución local.
- **Maven Wrapper:** compilación, ejecución y pruebas.
- **Render:** alojamiento de la API y de PostgreSQL.
- **pgAdmin:** consulta y administración de PostgreSQL.

## Base de datos: PostgreSQL y pgAdmin

La base de datos **PostgreSQL está alojada en Render** y se consulta desde **pgAdmin**. La aplicación Android accede a los datos a través de la API.

| Tabla | Datos almacenados |
| --- | --- |
| `public.cuentas` | Identificador, nombre, correo y hash BCrypt de la contraseña. |
| `public.solicitudes` | Identificador, modalidad, mensaje y propietario. Las solicitudes de invitados se guardan sin propietario. |

Para consultar las solicitudes en pgAdmin, abre la conexión a la base de Render y navega a **Databases → [nombre de la base] → Schemas → public → Tables**. Haz clic derecho sobre `solicitudes` y selecciona **View/Edit Data → All Rows**.

También puedes usar **Query Tool**:

```sql
SELECT id, modalidad, mensaje, propietario
FROM public.solicitudes
ORDER BY id DESC;
```

## Seguridad y contraseñas con BCrypt

Al registrar una cuenta o actualizar su contraseña, el backend genera un **hash BCrypt** antes de guardarla en PostgreSQL. La contraseña original no se guarda ni se devuelve en las respuestas de la API.

- **`AutenticacionConfig.java`:** configura el `PasswordEncoder` mediante `PasswordEncoderFactories.createDelegatingPasswordEncoder()`, que usa BCrypt por defecto para generar nuevos hashes.
- **`CuentaController.java`:** usa `passwordEncoder.encode(clave)` antes de guardar la cuenta. En PostgreSQL, la columna `clave_hash` guarda el resultado con el prefijo `{bcrypt}`.

El inicio de sesión usa **HTTP Basic sobre HTTPS** en Render. Las operaciones protegidas que modifican datos requieren un token **CSRF**. El backend identifica al usuario autenticado y verifica que la solicitud le pertenezca antes de editarla o eliminarla.

## Endpoints

URL base: `https://alba-catalogo-api.onrender.com`

| Método | Ruta | Descripción | Acceso |
| --- | --- | --- | --- |
| `GET` | `/api/productos` | Lista los productos del catálogo. | Público |
| `GET` | `/api/programas` | Lista los programas de Alba Conecta. | Público |
| `POST` | `/api/cuentas/registro` | Registra una cuenta. | Público |
| `GET` | `/api/cuentas/me` | Valida las credenciales y devuelve el perfil. | Con cuenta |
| `PUT` | `/api/cuentas/me` | Actualiza los datos del perfil. | Con cuenta |
| `GET` | `/api/cuentas/csrf` | Obtiene el token CSRF para operaciones protegidas. | Con cuenta |
| `POST` | `/api/solicitudes/invitado` | Envía una solicitud sin iniciar sesión. | Público |
| `GET` | `/api/solicitudes` | Lista las solicitudes del usuario autenticado. | Con cuenta |
| `POST` | `/api/solicitudes` | Crea una solicitud para el usuario autenticado. | Con cuenta |
| `PUT` | `/api/solicitudes/{id}` | Edita una solicitud propia. | Con cuenta |
| `DELETE` | `/api/solicitudes/{id}` | Elimina una solicitud propia. | Con cuenta |

## Ejecutar en IntelliJ IDEA

1. Clona este repositorio y abre la carpeta `alba-catalogo-api` en IntelliJ IDEA.
2. Configura un **JDK 21** y deja que IntelliJ importe las dependencias de Maven.
3. En la terminal de IntelliJ, ejecuta en Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Cuando la aplicación inicie, abre [http://localhost:8080/api/programas](http://localhost:8080/api/programas) para comprobar que la API responde.

Con la configuración por defecto se usa **H2 en memoria**. Los datos creados en esa ejecución se pierden al detener el servidor.

### Ejecutar con PostgreSQL

Configura estas variables de entorno antes de iniciar la API:

| Variable | Valor o descripción |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `postgres` |
| `DB_URL` | URL JDBC de PostgreSQL, por ejemplo `jdbc:postgresql://host:5432/base_de_datos`. |
| `DB_USER` | Usuario de PostgreSQL. |
| `DB_PASSWORD` | Contraseña de PostgreSQL. |
| `ALBA_SOLICITUDES_HABILITAR_CUENTAS` | `true` para habilitar la gestión de solicitudes con cuenta. |

En Render, estas variables se configuran en el entorno del servicio. Si ejecutas el backend desde tu computador y quieres conectarlo a la base de Render, usa los datos de **conexión externa**. Las credenciales reales no se incluyen en el repositorio.

## Pruebas del backend

Para ejecutar las pruebas automatizadas:

```powershell
.\mvnw.cmd test
```

El backend incluye pruebas de seguridad de solicitudes con **MockMvc y H2**. También se comprobó desde el emulador que una cuenta puede crear, consultar, editar y eliminar sus solicitudes, y que una cuenta nueva no ve las solicitudes de otra.

## Estructura del proyecto

| Ubicación | Contenido |
| --- | --- |
| `config` | Configuración de seguridad, autenticación y BCrypt. |
| `controller` | Endpoints de productos, programas, cuentas y solicitudes. |
| `model` | Entidades de cuentas y solicitudes. |
| `repository` | Acceso a datos con Spring Data JPA. |
| `src/main/resources` | Configuración general y perfil de PostgreSQL. |
| `src/test` | Pruebas automatizadas del backend. |

El código Android, las pruebas unitarias de la app, la documentación de la API externa y la evidencia del APK firmado corresponden al **repositorio de la aplicación móvil**.
