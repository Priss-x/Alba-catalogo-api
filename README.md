# Alba Catálogo API

API REST en Java y Spring Boot para el catálogo de la app Android **Alba Conecta**. La app consulta este servicio mediante Retrofit para mostrar el nombre, la descripción y el precio de cada producto. Las fotos se mantienen en la app.

Este es un proyecto académico. Los productos son de demostración y se definen en este servicio. La API no consulta la tienda oficial de CORPALIV ni procesa compras. Por ahora, el catálogo se carga desde el código y no utiliza una base de datos.

## Requisitos

- Java 21, según la versión configurada en `pom.xml`.
- El proyecto incluye Maven Wrapper para ejecutar la API.

## Ejecutar en local

Abre una terminal en la carpeta que contiene `pom.xml`.

**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux o macOS:**

```bash
./mvnw spring-boot:run
```

Luego abre [http://localhost:8080/api/productos](http://localhost:8080/api/productos).

## Endpoint

| Método | Ruta | Respuesta |
| --- | --- | --- |
| `GET` | `/api/productos` | Lista de productos del catálogo en formato JSON. |

Cada producto incluye:

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `id` | Entero | Identificador estable del producto. |
| `nombre` | Texto | Nombre visible en el catálogo. |
| `descripcion` | Texto | Detalles del producto. |
| `precio` | Entero | Precio de referencia en pesos chilenos (CLP). |

## Integración con Android

El emulador de Android consulta el servidor que corre en el PC mediante `http://10.0.2.2:8080/api/productos`.

## Despliegue

Pendiente de publicación. La URL pública se agregará aquí una vez que el servicio esté desplegado.