# ABC Kafka CRUD — Java 17 + Spring Boot + Kafka + PostgreSQL

Sistema **ABC (Alta-Baja-Cambio)** para gestión de productos con **event-driven architecture** usando Apache Kafka para publicación de eventos y PostgreSQL para persistencia.

## Stack Tecnológico

| Componente     | Tecnología              | Versión |
|----------------|-------------------------|---------|
| Lenguaje       | Java (LTS)              | 17      |
| Framework      | Spring Boot             | 3.2.5   |
| Mensajería     | Apache Kafka            | 7.6.1   |
| Base de Datos  | PostgreSQL              | 16      |
| API Docs       | springdoc-openapi       | 2.5.0   |
| Contenedores   | Docker + Docker Compose | 27+     |

## Inicio Rápido

### 1. Levantar Infraestructura
```bash
docker compose -f docker/docker-compose.yml up -d
```

### 2. Compilar y Ejecutar
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### 3. Verificar
- **API Health:** http://localhost:8080/api/actuator/health
- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **Kafka UI:** http://localhost:8090

## Endpoints Principales

| Método   | Endpoint                        | Descripción                      |
|----------|---------------------------------|----------------------------------|
| `POST`   | `/api/v1/productos`             | Alta de producto                 |
| `GET`    | `/api/v1/productos/{id}`        | Obtener por ID                   |
| `GET`    | `/api/v1/productos/sku/{sku}`   | Obtener por SKU                  |
| `GET`    | `/api/v1/productos`             | Listar (paginado)                |
| `GET`    | `/api/v1/productos/buscar?q=`   | Buscar por nombre/SKU/categoría  |
| `PUT`    | `/api/v1/productos/{id}`        | Cambio de producto               |
| `PATCH`  | `/api/v1/productos/{id}/stock`  | Actualizar stock                 |
| `DELETE` | `/api/v1/productos/{id}`        | Baja (soft delete)               |
| `PATCH`  | `/api/v1/productos/{id}/reactivar` | Reactivar producto            |
| `GET`    | `/api/v1/productos/categorias`  | Listar categorías                |
| `GET`    | `/api/v1/productos/estadisticas`| Estadísticas generales           |

## Kafka Topics

| Topic            | Particiones | Descripción                      |
|------------------|-------------|----------------------------------|
| `abc.productos`  | 6           | Eventos de productos (CRUD)      |
| `abc.audit`      | 3           | Trail de auditoría               |

## Ejemplo de Petición

```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Dell Inspiron 15",
    "descripcion": "Laptop para trabajo y estudio",
    "sku": "DELL-INS-15",
    "precio": 15999.99,
    "stock": 50,
    "categoria": "Electrónicos"
  }'
```

## Estructura del Proyecto

```
abc-kafka-crud/
├── pom.xml
├── Dockerfile
├── docker/docker-compose.yml
├── docs/diagrams/
└── src/main/java/com/example/abckafka/
    ├── AbcKafkaCrudApplication.java
    ├── config/KafkaConfig.java
    ├── controller/ProductoController.java
    ├── consumer/ProductoEventConsumer.java
    ├── dto/
    │   ├── ProductoEvent.java
    │   ├── ProductoRequest.java
    │   └── ProductoResponse.java
    ├── entity/Producto.java
    ├── exception/
    │   ├── DuplicateSkuException.java
    │   ├── GlobalExceptionHandler.java
    │   └── ProductoNotFoundException.java
    ├── producer/ProductoEventProducer.java
    ├── repository/ProductoRepository.java
    └── service/ProductoService.java
```
