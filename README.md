# MENOS – Tienda en línea (Spring Boot + Thymeleaf + MySQL)

## 1. Crear la base de datos
1. Abre **MySQL Workbench** y conéctate a tu servidor local.
2. `File > Open SQL Script...` y elige `db/menos_db.sql`.
3. Pulsa el rayo (**Execute**). Se crea la base `menos_db` con sus 10 tablas y datos de ejemplo.

## 2. Conectar el proyecto
En `src/main/resources/application.properties` cambia:
- `spring.datasource.username` (normalmente `root`)
- `spring.datasource.password` (la contraseña de tu MySQL)

## 3. Ejecutar
`mvn spring-boot:run` (o corre `MenosApplication` desde VS Code) y abre http://localhost:8080

- Cliente: cliente@menos.pe / cliente123
- Administrador: admin@menos.pe / admin123

Pago simulado: tarjeta de 16 dígitos = aprobado; terminada en 0000 = rechazado.
