# TempusUGR - Schedule Consumer Service

Este repositorio contiene el código fuente del `schedule-consumer-service`, el microservicio del proyecto **TempusUGR** encargado de actuar como la **fuente de datos para todos los horarios académicos oficiales** de la Universidad de Granada.

Su función principal es extraer, procesar y almacenar de forma automatizada la información de grados, asignaturas, grupos y clases desde el portal web de la UGR. Posteriormente, expone estos datos a través de una API REST para que otros servicios, como el `academic-subscription-service`, puedan consumirlos.

---

## ✨ Funcionalidad Principal y Flujo de Trabajo

### Web Scraping con Jsoup
El núcleo de este servicio es un potente proceso de **web scraping** construido con la biblioteca **Jsoup**. Este proceso navega sistemáticamente por el portal `grados.ugr.es` para recopilar la siguiente información:
* Lista de todos los grados y facultades.
* Para cada grado, la lista completa de asignaturas con su curso, semestre y departamento.
* Para cada asignatura, los diferentes grupos (teoría, prácticas), los profesores asignados y, lo más importante, los horarios detallados de cada clase (días, horas, aulas y fechas de vigencia).

![Image](https://github.com/user-attachments/assets/723520cc-f075-4d39-ba12-d4163ded0e18)

### Actualización Diaria Automatizada
Para garantizar que la información esté siempre actualizada, el servicio implementa un **Cron Job** utilizando la anotación `@Scheduled` de Spring Boot.
* **Frecuencia:** El proceso de scraping completo se ejecuta automáticamente **todos los días a medianoche**.
* **Consistencia de Datos:** La operación de actualización (borrado de datos antiguos e inserción de los nuevos) está envuelta en una **transacción (`@Transactional`)**. Esto asegura que la base de datos se mantenga en un estado consistente y que los usuarios no experimenten interrupciones o datos corruptos durante el proceso.

---

## 🛠️ Pila Tecnológica

* **Lenguaje/Framework**: Java 21, Spring Boot 3.4.4
* **Base de Datos**: **MySQL** para almacenar de forma estructurada toda la información académica.
* **Web Scraping**: Biblioteca **Jsoup** para el parseo de HTML.
* **Persistencia de Datos**: **Spring Data JPA** para la interacción con la base de datos MySQL.
* **Tareas Programadas**: **Spring Boot Scheduler** para las actualizaciones diarias.
* **Descubrimiento de Servicios**: Cliente de **Eureka** para el registro en la red de microservicios.

---

## 🏗️ Arquitectura y Esquema de Datos

Este servicio actúa como un proveedor de datos fundamental dentro de la arquitectura. Su principal consumidor es el `academic-subscription-service`, que lo consulta para construir los calendarios personalizados de los usuarios.

### Esquema de la Base de Datos
La información se organiza en una base de datos relacional con las siguientes entidades principales: `Grade`, `Subject`, `Subject_group` y `Class_info`.

![Image](https://github.com/user-attachments/assets/e3e72b99-058e-4593-9ed7-cfb441236c9f)
---

## 🔌 API Endpoints Principales

El servicio expone endpoints bajo el prefijo `/schedule-consumer`.

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET`| `/grades` | Devuelve una lista de todos los grados disponibles en la UGR. |
| `GET`| `/subjects-groups` | Devuelve las asignaturas y grupos de un grado específico. |
| `GET`| `/classes-from-group` | Obtiene los detalles de las clases de un grupo de asignatura. |
| `GET`| `/teacher-classes` | Busca y devuelve todas las clases impartidas por un profesor. |

---

## 🚀 Puesta en Marcha Local

### **Prerrequisitos**

* Java 21 o superior.
* Maven 3.x.
* Una instancia de **MySQL** en ejecución.
* Un servidor **Eureka** (`eureka-service`) en ejecución.

### **Configuración**

Configura los siguientes parámetros en el archivo `src/main/resources/application.properties`:

```properties
# -- CONFIGURACIÓN DEL SERVIDOR --
server.port=8085 # O el puerto deseado

# -- CONFIGURACIÓN DE EUREKA --
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka

# -- CONFIGURACIÓN DE MYSQL --
spring.datasource.url=jdbc:mysql://<host>:<port>/db_schedule_consumer_service?createDatabaseIfNotExist=true
spring.datasource.username=<user>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
