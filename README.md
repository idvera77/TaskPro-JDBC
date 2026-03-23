# **TaskPro \- Enterprise Task Management System**

Este repositorio contiene el desarrollo de **TaskPro**, un sistema de gestión de tareas y proyectos diseñado bajo estándares de arquitectura empresarial en Java. El proyecto se enfoca en la robustez del código, la seguridad mediante roles y el manejo avanzado de persistencia de datos relacionales.

## **Tecnologías y Conceptos Dominados**

* **Arquitectura de Software:** Separación estricta de responsabilidades mediante los patrones **DAO (Data Access Object)** para la persistencia y **Service Layer** para la lógica de negocio y seguridad.  
* **Gestión Avanzada de Excepciones:** Implementación de una jerarquía de excepciones personalizadas (`AccessDenied`, `ResourceNotFound`, `Validation`) para centralizar el control de errores y garantizar la estabilidad del sistema.  
* **Persistencia y Bases de Datos Relacionales:** Diseño y manipulación de bases de datos **MySQL** mediante JDBC, gestionando relaciones complejas, claves foráneas y auditoría de cambios.  
* **Seguridad y Control de Acceso (RBAC):** Sistema de autenticación y autorización basado en roles, restringiendo operaciones críticas según el perfil del usuario.  
* **User Experience (UX) en Consola:** Desarrollo de un motor de entrada de datos (`leerInput`) con validación en tiempo real y soporte para cancelación de operaciones (flujo de "atrás") mediante excepciones de control.  
* **Programación Orientada a Objetos (OOP):** Uso avanzado de Enums, tipos de fecha modernos (`java.time`) y encapsulamiento para representar entidades del mundo real.

## **Estructura del Proyecto**

| com.taskpro.model | Entidades (Usuario, Proyecto, Tarea) y Enums de estado. |
| :---- | :---- |
| **com.taskpro.dao**  | Lógica de persistencia y consultas SQL mediante JDBC. |
| **com.taskpro.service**   | Reglas de negocio, validaciones y control de permisos. |
| **com.taskpro.exception** | Jerarquía de excepciones personalizadas del sistema. |
| **com.taskpro.ui**     | Interfaz de consola interactiva y motor de lectura de datos. |

## **Herramientas y Entorno**

* **Lenguaje:** Java 17+  
* **Entorno:** IntelliJ IDEA  
* **Base de Datos:** MySQL 8.0  
* **Tecnologías:** JDBC (Java Database Connectivity), MySQL Workbench (Modelado relacional)
