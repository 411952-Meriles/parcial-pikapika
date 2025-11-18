# Parcial Pikapika - Docker Setup

## Arquitectura
- **Frontend**: Angular 19 (Puerto 4200)
- **Backend**: Spring Boot 3.5.7 con Java 17 (Puerto 8080)
- **Base de datos**: MySQL 8.0 (Puerto 3306)

## Requisitos previos
- Docker
- Docker Compose

## Configuración de la Base de Datos
El proyecto usa MySQL con las siguientes credenciales (definidas en `docker-compose.yml`):
- **Base de datos**: `parcial_db`
- **Usuario**: `usuario`
- **Contraseña**: `password123`
- **Root password**: `rootpassword`

## Comandos Docker

### Iniciar todos los servicios
```bash
docker-compose up -d
```

### Ver logs
```bash
# Todos los servicios
docker-compose logs -f

# Un servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### Detener los servicios
```bash
docker-compose down
```

### Detener y eliminar volúmenes (borra la base de datos)
```bash
docker-compose down -v
```

### Reconstruir las imágenes
```bash
docker-compose build --no-cache
docker-compose up -d
```

## Acceso a los servicios
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3306

## Estructura del proyecto
```
parcial-pikapika/
├── back/
│   ├── Dockerfile          # Imagen del backend
│   ├── pom.xml            # Dependencias Maven
│   └── src/               # Código fuente Spring Boot
├── front/
│   ├── Dockerfile         # Imagen del frontend
│   ├── nginx.conf         # Configuración de Nginx
│   ├── package.json       # Dependencias npm
│   └── src/               # Código fuente Angular
├── docker-compose.yml     # Orquestación de servicios
└── .dockerignore         # Archivos excluidos de Docker

```

## Notas importantes
1. El backend espera a que MySQL esté saludable antes de iniciar (healthcheck)
2. El frontend usa Nginx como servidor web en producción
3. Las peticiones a `/api` en el frontend se redirigen automáticamente al backend
4. Los datos de MySQL persisten en un volumen Docker (`mysql_data`)

## Solución de problemas

### El backend no conecta con MySQL
Espera unos segundos más, el healthcheck verifica que MySQL esté listo.

### Cambios en el código no se reflejan
Reconstruye las imágenes:
```bash
docker-compose build
docker-compose up -d
```

### Ver el estado de los contenedores
```bash
docker-compose ps
```

### Acceder a la base de datos
```bash
docker exec -it parcial-mysql mysql -u usuario -p
# Password: password123
```
