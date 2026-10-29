-- =====================================================================
--  MENOS · Plataforma Web Integrada de Ventas e Inventario
--  Script de base de datos para MySQL 8 / MySQL Workbench
--
--  CÓMO USARLO:
--    1) Abre MySQL Workbench y conéctate a tu servidor local.
--    2) File > Open SQL Script... > elige este archivo.
--    3) Pulsa el rayo (Execute) o Ctrl + Shift + Enter.
--
--  ATENCIÓN: la línea DROP DATABASE borra la base "menos_db" si ya existía,
--  para poder volver a ejecutar el script desde cero.
-- =====================================================================

DROP DATABASE IF EXISTS menos_db;
CREATE DATABASE menos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE menos_db;

-- ---------------------------------------------------------------------
--  TABLAS
-- ---------------------------------------------------------------------

CREATE TABLE usuario (
  id_usuario   BIGINT       NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(100) NOT NULL,
  email        VARCHAR(120) NOT NULL,
  password     VARCHAR(100) NOT NULL,              -- hash BCrypt (Spring Security)
  rol          VARCHAR(20)  NOT NULL,              -- ADMIN | CLIENTE
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uq_usuario_email (email)
) ENGINE=InnoDB;

CREATE TABLE direccion_envio (
  id_direccion BIGINT       NOT NULL AUTO_INCREMENT,
  id_usuario   BIGINT       NOT NULL,
  direccion    VARCHAR(200) NOT NULL,
  distrito     VARCHAR(100),
  referencia   VARCHAR(200),
  PRIMARY KEY (id_direccion),
  CONSTRAINT fk_direccion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE metodo_pago (
  id_metodo_pago BIGINT      NOT NULL AUTO_INCREMENT,
  nombre         VARCHAR(60) NOT NULL,
  activo         TINYINT(1)  NOT NULL DEFAULT 1,
  PRIMARY KEY (id_metodo_pago),
  UNIQUE KEY uq_metodo_pago_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE cupon_descuento (
  id_cupon             BIGINT        NOT NULL AUTO_INCREMENT,
  codigo               VARCHAR(30)   NOT NULL,
  porcentaje_descuento DECIMAL(5,2)  NOT NULL,
  fecha_expiracion     DATE,
  activo               TINYINT(1)    NOT NULL DEFAULT 1,
  PRIMARY KEY (id_cupon),
  UNIQUE KEY uq_cupon_codigo (codigo)
) ENGINE=InnoDB;

CREATE TABLE categoria (
  id_categoria BIGINT       NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(80)  NOT NULL,
  descripcion  VARCHAR(255),
  imagen       VARCHAR(255),
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uq_categoria_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE proveedor (
  id_proveedor BIGINT       NOT NULL AUTO_INCREMENT,
  ruc          VARCHAR(11),
  nombre       VARCHAR(120) NOT NULL,
  telefono     VARCHAR(20),
  PRIMARY KEY (id_proveedor),
  UNIQUE KEY uq_proveedor_ruc (ruc)
) ENGINE=InnoDB;

CREATE TABLE producto (
  id_producto  BIGINT        NOT NULL AUTO_INCREMENT,
  id_categoria BIGINT        NOT NULL,
  id_proveedor BIGINT        NULL,
  nombre       VARCHAR(120)  NOT NULL,
  descripcion  VARCHAR(255),
  precio       DECIMAL(10,2) NOT NULL,
  stock        INT           NOT NULL DEFAULT 0,
  emoji        VARCHAR(16),
  imagen       VARCHAR(255),
  PRIMARY KEY (id_producto),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria),
  CONSTRAINT fk_producto_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
) ENGINE=InnoDB;

CREATE TABLE pedido (
  id_pedido         BIGINT        NOT NULL AUTO_INCREMENT,
  id_usuario        BIGINT        NOT NULL,
  id_direccion      BIGINT        NULL,            -- opcional: hoy la tienda aún no pide dirección
  id_metodo_pago    BIGINT        NOT NULL,
  id_cupon          BIGINT        NULL,            -- opcional
  fecha             DATETIME      NOT NULL,
  total             DECIMAL(10,2) NOT NULL,
  estado            VARCHAR(20)   NOT NULL,        -- PAGADO | RECHAZADO | ENTREGADO
  referencia_pago   VARCHAR(30),
  tarjeta_ultimos4  VARCHAR(4),
  PRIMARY KEY (id_pedido),
  CONSTRAINT fk_pedido_usuario     FOREIGN KEY (id_usuario)     REFERENCES usuario (id_usuario),
  CONSTRAINT fk_pedido_direccion   FOREIGN KEY (id_direccion)   REFERENCES direccion_envio (id_direccion),
  CONSTRAINT fk_pedido_metodo_pago FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago (id_metodo_pago),
  CONSTRAINT fk_pedido_cupon       FOREIGN KEY (id_cupon)       REFERENCES cupon_descuento (id_cupon)
) ENGINE=InnoDB;

CREATE TABLE pedido_item (
  id_item         BIGINT        NOT NULL AUTO_INCREMENT,
  id_pedido       BIGINT        NOT NULL,
  id_producto     BIGINT        NOT NULL,
  cantidad        INT           NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id_item),
  CONSTRAINT fk_item_pedido   FOREIGN KEY (id_pedido)   REFERENCES pedido (id_pedido) ON DELETE CASCADE,
  CONSTRAINT fk_item_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
) ENGINE=InnoDB;

CREATE TABLE movimiento_inventario (
  id_movimiento BIGINT       NOT NULL AUTO_INCREMENT,
  id_producto   BIGINT       NOT NULL,
  id_usuario    BIGINT       NOT NULL,
  id_pedido     BIGINT       NULL,                 -- opcional: solo si el movimiento viene de una venta
  id_proveedor  BIGINT       NULL,                 -- opcional: solo si es una entrada de un proveedor
  tipo          VARCHAR(20)  NOT NULL,             -- ENTRADA | SALIDA
  cantidad      INT          NOT NULL,
  fecha         DATETIME     NOT NULL,
  motivo        VARCHAR(255),
  PRIMARY KEY (id_movimiento),
  CONSTRAINT fk_mov_producto  FOREIGN KEY (id_producto)  REFERENCES producto (id_producto),
  CONSTRAINT fk_mov_usuario   FOREIGN KEY (id_usuario)   REFERENCES usuario (id_usuario),
  CONSTRAINT fk_mov_pedido    FOREIGN KEY (id_pedido)    REFERENCES pedido (id_pedido),
  CONSTRAINT fk_mov_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  DATOS INICIALES
-- ---------------------------------------------------------------------

-- Usuarios (contraseñas con BCrypt: admin123 / cliente123)
INSERT INTO usuario (nombre, email, password, rol) VALUES
 ('Administrador', 'admin@menos.pe',   '$2a$10$o8sLQj/1bIEQKGuyK.IcR.PAo6vGXjXBslW2nC5E..rLVY96nNqym', 'ADMIN'),
 ('Cliente Demo',  'cliente@menos.pe', '$2a$10$qO9Nae4NBUitBs55ATtQk.plv7vTG/SellDOH5eBpDHJAqvdqqSve', 'CLIENTE');

INSERT INTO categoria (nombre, descripcion) VALUES
 ('Abarrotes',         'Arroz, aceite, fideos y azúcar'),
 ('Lácteos',           'Leche, quesos y yogures'),
 ('Frutas y verduras', 'Productos frescos'),
 ('Bebidas',           'Gaseosas y agua'),
 ('Limpieza',          'Detergentes y desinfectantes');

-- Datos de EJEMPLO (ficticios) para poder probar la relación Proveedor - Producto
INSERT INTO proveedor (ruc, nombre, telefono) VALUES
 ('20000000001', 'Distribuidora Ejemplo Abarrotes', '999000001'),
 ('20000000002', 'Distribuidora Ejemplo Frescos',   '999000002'),
 ('20000000003', 'Distribuidora Ejemplo Limpieza',  '999000003');

INSERT INTO metodo_pago (nombre, activo) VALUES ('Tarjeta', 1);

-- Cupón de EJEMPLO (la tienda todavía no lo aplica en el pago)
INSERT INTO cupon_descuento (codigo, porcentaje_descuento, fecha_expiracion, activo) VALUES
 ('MENOS10', 10.00, '2026-12-31', 1);

-- Productos (id_categoria: 1 Abarrotes, 2 Lácteos, 3 Frutas y verduras, 4 Bebidas, 5 Limpieza)
INSERT INTO producto (id_categoria, id_proveedor, nombre, descripcion, precio, stock, emoji) VALUES
 (1, 1, 'Arroz costeño 5 kg',      'Grano seleccionado',  21.90, 50, '🍚'),
 (1, 1, 'Aceite vegetal 1 L',      'Botella pet',          8.50, 40, '🛢️'),
 (1, 1, 'Fideos spaghetti 500 g',  'Pasta de sémola',      3.20, 80, '🍝'),
 (1, 1, 'Azúcar rubia 1 kg',       'Bolsa',                4.30, 60, '🧂'),
 (2, 2, 'Leche entera 1 L',        'Caja tetra',           4.90, 70, '🥛'),
 (2, 2, 'Queso fresco 250 g',      'Artesanal',            9.90, 25, '🧀'),
 (2, 2, 'Yogurt fresa 1 L',        'Botella',              7.20, 35, '🍓'),
 (3, 2, 'Plátano de seda 1 kg',    'De la selva',          3.90, 45, '🍌'),
 (3, 2, 'Palta fuerte 1 kg',       'Calidad extra',       12.90, 30, '🥑'),
 (3, 2, 'Tomate 1 kg',             'Fresco',               4.50, 40, '🍅'),
 (4, 1, 'Gaseosa 3 L',             'Sabor cola',          10.50, 50, '🥤'),
 (4, 1, 'Agua sin gas 2.5 L',      'Botella',              3.50, 90, '💧'),
 (5, 3, 'Detergente 2 kg',         'Ropa blanca y color', 19.90, 30, '🧴'),
 (5, 3, 'Lejía 1 L',               'Desinfectante',        4.20, 55, '🧽');

-- Comprobación rápida (opcional)
-- SELECT p.nombre, c.nombre AS categoria, pr.nombre AS proveedor, p.precio, p.stock
--   FROM producto p JOIN categoria c USING (id_categoria) LEFT JOIN proveedor pr USING (id_proveedor);
