# Schedule Consumer Service - CalendarUgr

## Descripción
El **Schedule Consumer Service** es un microservicio dentro del sistema **CalendarUgr** encargado de la recolección, gestión y procesamiento de horarios académicos de los grados de la Universidad de Granada. Además, crea y administra eventos relacionados con los grupos de asignaturas.

## Características
- Extracción automática de horarios académicos.
- Gestión y almacenamiento de horarios.
- Creación de eventos académicos para grupos de asignaturas.
- Integración con otros microservicios de CalendarUgr.

## Requisitos previos
Para ejecutar este servicio, es necesario configurar las siguientes variables de entorno:

- `DB_USERNAME`: Nombre de usuario de la base de datos.
- `DB_PASSWORD`: Contraseña de la base de datos.
- `DB_URL`: URL de conexión a la base de datos.

## Instalación y ejecución
1. Clonar el repositorio:
   ```sh
   git clone <repository-url>
   cd schedule-consumer-service
   ```

2. Configurar variables de entorno:
   ```sh
   export DB_USERNAME=<your_db_username>
   export DB_PASSWORD=<your_db_password>
   export DB_URL=<your_db_url>
   ```

3. Construir y ejecutar el servicio:
   ```sh
   ./mvnw spring-boot:run
   ```

