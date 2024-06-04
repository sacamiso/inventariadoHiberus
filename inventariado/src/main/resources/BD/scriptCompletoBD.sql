CREATE DATABASE  IF NOT EXISTS `inventariado_bd` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `inventariado_bd`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: inventariado_bd
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `articulo`
--

DROP TABLE IF EXISTS `articulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `articulo` (
  `codigo_articulo` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `precio_unitario` double NOT NULL,
  `referencia` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `cod_categoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `cod_subcategoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `iva` double NOT NULL,
  `fabricante` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `modelo` varchar(25) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_articulo`),
  KEY `fk_categoria_idx` (`cod_categoria`),
  KEY `fk_subcategoria_idx` (`cod_subcategoria`,`cod_categoria`),
  CONSTRAINT `fk_categoria` FOREIGN KEY (`cod_categoria`) REFERENCES `categoria` (`codigo_categoria`),
  CONSTRAINT `fk_subcategoria` FOREIGN KEY (`cod_subcategoria`, `cod_categoria`) REFERENCES `subcategoria` (`codigo_subcategoria`, `codigo_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articulo`
--

LOCK TABLES `articulo` WRITE;
/*!40000 ALTER TABLE `articulo` DISABLE KEYS */;
/*!40000 ALTER TABLE `articulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asignacion`
--

DROP TABLE IF EXISTS `asignacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asignacion` (
  `id_asignacion` int NOT NULL AUTO_INCREMENT,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  `id_empleado` int NOT NULL,
  `cod_unidad` int NOT NULL,
  PRIMARY KEY (`id_asignacion`),
  KEY `fk_empleado_a_idx` (`id_empleado`),
  KEY `fk_unidad_a_idx` (`cod_unidad`),
  CONSTRAINT `fk_empleado_a` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`),
  CONSTRAINT `fk_unidad_a` FOREIGN KEY (`cod_unidad`) REFERENCES `unidad` (`codigo_interno`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asignacion`
--

LOCK TABLES `asignacion` WRITE;
/*!40000 ALTER TABLE `asignacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `asignacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `codigo_categoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nombre` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES ('CAR','Cargador'),('ORD','Ordenador'),('RAT','Ratón'),('TEC','Teclado');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `condicion_pago`
--

DROP TABLE IF EXISTS `condicion_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `condicion_pago` (
  `codigo_condicion` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `descripcion` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_condicion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `condicion_pago`
--

LOCK TABLES `condicion_pago` WRITE;
/*!40000 ALTER TABLE `condicion_pago` DISABLE KEYS */;
INSERT INTO `condicion_pago` VALUES ('A30','Pago a 30 días'),('A60','Pago a 60 días'),('CONT','Al contado');
/*!40000 ALTER TABLE `condicion_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleado`
--

DROP TABLE IF EXISTS `empleado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleado` (
  `id_empleado` int NOT NULL AUTO_INCREMENT,
  `dni` varchar(9) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nombre` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `apellidos` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `usuario` varchar(45) NOT NULL,
  `contrasena` varchar(200) NOT NULL,
  `cod_rol` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `id_oficina` int NOT NULL,
  `correo` varchar(100) NOT NULL,
  PRIMARY KEY (`id_empleado`),
  UNIQUE KEY `dni_UNIQUE` (`dni`),
  UNIQUE KEY `usuario_UNIQUE` (`usuario`),
  KEY `fk_cod_rol_idx` (`cod_rol`),
  KEY `fk_id_oficina_idx` (`id_oficina`),
  CONSTRAINT `fk_cod_rol` FOREIGN KEY (`cod_rol`) REFERENCES `rol` (`codigo_rol`),
  CONSTRAINT `fk_id_oficina` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleado`
--

LOCK TABLES `empleado` WRITE;
/*!40000 ALTER TABLE `empleado` DISABLE KEYS */;
INSERT INTO `empleado` VALUES (14,'22470256S','María','García López','magarci','$2a$10$gH4Bvsr9YqlcB1DsRdPGh.0H4TaxGkCGzMioWMyyNPDKb8fwJ7srG','ADM',1,'magarci@gmail.com');
/*!40000 ALTER TABLE `empleado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estado`
--

DROP TABLE IF EXISTS `estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estado` (
  `codigo_estado` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nombre` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_estado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estado`
--

LOCK TABLES `estado` WRITE;
/*!40000 ALTER TABLE `estado` DISABLE KEYS */;
INSERT INTO `estado` VALUES ('MANT','En mantenimiento'),('OP','Operativo'),('S','Salida');
/*!40000 ALTER TABLE `estado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_inventario`
--

DROP TABLE IF EXISTS `historial_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_inventario` (
  `cod_articulo` int NOT NULL,
  `id_oficina` int NOT NULL,
  `fecha` datetime NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`cod_articulo`,`id_oficina`,`fecha`),
  KEY `fk_oficina_historial_idx` (`id_oficina`),
  CONSTRAINT `fk_articulo_historial` FOREIGN KEY (`cod_articulo`) REFERENCES `articulo` (`codigo_articulo`),
  CONSTRAINT `fk_oficina_historial` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_inventario`
--

LOCK TABLES `historial_inventario` WRITE;
/*!40000 ALTER TABLE `historial_inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `historial_inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `cod_articulo` int NOT NULL,
  `id_oficina` int NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`cod_articulo`,`id_oficina`),
  KEY `fk_oficina_idx` (`id_oficina`),
  KEY `fk_oficina_idx2` (`id_oficina`),
  KEY `fk_oficina_id` (`id_oficina`),
  CONSTRAINT `fk_articulo_inventario` FOREIGN KEY (`cod_articulo`) REFERENCES `articulo` (`codigo_articulo`),
  CONSTRAINT `fk_oficina_inventario` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linea`
--

DROP TABLE IF EXISTS `linea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linea` (
  `numero_pedido` int NOT NULL,
  `numero_linea` int NOT NULL,
  `codigo_articulo` int NOT NULL,
  `numero_unidades` int NOT NULL,
  `precio_linea` double NOT NULL,
  `descuento` double NOT NULL,
  PRIMARY KEY (`numero_pedido`,`numero_linea`),
  KEY `fk_articulo_l_idx` (`codigo_articulo`),
  CONSTRAINT `fk_articulo_l` FOREIGN KEY (`codigo_articulo`) REFERENCES `articulo` (`codigo_articulo`),
  CONSTRAINT `fk_pedido_l` FOREIGN KEY (`numero_pedido`) REFERENCES `pedido` (`numero_pedido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linea`
--

LOCK TABLES `linea` WRITE;
/*!40000 ALTER TABLE `linea` DISABLE KEYS */;
/*!40000 ALTER TABLE `linea` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medio_pago`
--

DROP TABLE IF EXISTS `medio_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medio_pago` (
  `codigo_medio` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `descripcion` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_medio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medio_pago`
--

LOCK TABLES `medio_pago` WRITE;
/*!40000 ALTER TABLE `medio_pago` DISABLE KEYS */;
INSERT INTO `medio_pago` VALUES ('G','Pago por giro'),('TB','Transferencia bancaria'),('TJ','Tarjeta');
/*!40000 ALTER TABLE `medio_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oficina`
--

DROP TABLE IF EXISTS `oficina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oficina` (
  `id_oficina` int NOT NULL AUTO_INCREMENT,
  `codigo_postal` int DEFAULT NULL,
  `direccion` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `localidad` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `provincia` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pais` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id_oficina`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oficina`
--

LOCK TABLES `oficina` WRITE;
/*!40000 ALTER TABLE `oficina` DISABLE KEYS */;
INSERT INTO `oficina` VALUES (1,26005,'Gran Vía Juan Carlos I, 41, Entreplanta 1','Logroño','La Rioja','España');
/*!40000 ALTER TABLE `oficina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `numero_pedido` int NOT NULL AUTO_INCREMENT,
  `fecha_pedido` date NOT NULL,
  `iva_pedido` double NOT NULL,
  `coste_total` double NOT NULL,
  `id_empleado` int NOT NULL,
  `plazo_entrega` int NOT NULL,
  `costes_envio` double NOT NULL,
  `id_proveedor` int NOT NULL,
  `id_oficina` int NOT NULL,
  `fecha_recepcion` date DEFAULT NULL,
  `condicion_pago` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `medio_pago` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `devuelto` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`numero_pedido`),
  KEY `fk_empleado_p_idx` (`id_empleado`),
  KEY `fk_proveedor_p_idx` (`id_proveedor`),
  KEY `fk_oficina_p_idx` (`id_oficina`),
  KEY `fk_condicionpago_idx` (`condicion_pago`),
  KEY `fk_mediopago_idx` (`medio_pago`),
  CONSTRAINT `fk_condicionpago` FOREIGN KEY (`condicion_pago`) REFERENCES `condicion_pago` (`codigo_condicion`),
  CONSTRAINT `fk_empleado_p` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`),
  CONSTRAINT `fk_mediopago` FOREIGN KEY (`medio_pago`) REFERENCES `medio_pago` (`codigo_medio`),
  CONSTRAINT `fk_oficina_p` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`),
  CONSTRAINT `fk_proveedor_p` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `pedido_vw`
--

DROP TABLE IF EXISTS `pedido_vw`;
/*!50001 DROP VIEW IF EXISTS `pedido_vw`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `pedido_vw` AS SELECT 
 1 AS `numero_pedido`,
 1 AS `fecha_pedido`,
 1 AS `iva_pedido`,
 1 AS `coste_total`,
 1 AS `id_empleado`,
 1 AS `plazo_entrega`,
 1 AS `costes_envio`,
 1 AS `id_proveedor`,
 1 AS `id_oficina`,
 1 AS `fecha_recepcion`,
 1 AS `condicion_pago`,
 1 AS `medio_pago`,
 1 AS `devuelto`,
 1 AS `coste_unitario`,
 1 AS `numero_unidades`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `cif` varchar(9) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `razon_social` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `direccion` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `codigo_postal` int DEFAULT NULL,
  `localidad` varchar(60) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `telefono` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `email` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id_proveedor`),
  UNIQUE KEY `cif_UNIQUE` (`cif`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor`
--

LOCK TABLES `proveedor` WRITE;
/*!40000 ALTER TABLE `proveedor` DISABLE KEYS */;
/*!40000 ALTER TABLE `proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `codigo_rol` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nombre` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES ('ADM','Administrador'),('VIS','Visualizador');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salida`
--

DROP TABLE IF EXISTS `salida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salida` (
  `id_salida` int NOT NULL AUTO_INCREMENT,
  `numero_unidades` int NOT NULL,
  `coste_total` double NOT NULL,
  `coste_unitario` double NOT NULL,
  `fecha_salida` date NOT NULL,
  `cod_articulo` int NOT NULL,
  `id_oficina` int NOT NULL,
  PRIMARY KEY (`id_salida`),
  KEY `fk_articulo_idx` (`cod_articulo`),
  KEY `fk_oficina_idx` (`id_oficina`),
  CONSTRAINT `fk_articulo` FOREIGN KEY (`cod_articulo`) REFERENCES `articulo` (`codigo_articulo`),
  CONSTRAINT `fk_oficina` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salida`
--

LOCK TABLES `salida` WRITE;
/*!40000 ALTER TABLE `salida` DISABLE KEYS */;
/*!40000 ALTER TABLE `salida` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_seguridad`
--

DROP TABLE IF EXISTS `stock_seguridad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_seguridad` (
  `cod_categoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `cod_subcategoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `id_oficina` int NOT NULL,
  `cantidad` int NOT NULL,
  `plazo_entrega_medio` int NOT NULL,
  PRIMARY KEY (`cod_categoria`,`cod_subcategoria`,`id_oficina`),
  KEY `fk_oficina_stock_idx` (`id_oficina`),
  CONSTRAINT `fk_categoria_stock` FOREIGN KEY (`cod_categoria`) REFERENCES `categoria` (`codigo_categoria`),
  CONSTRAINT `fk_oficina_stock` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`),
  CONSTRAINT `fk_subcategoria_stock` FOREIGN KEY (`cod_categoria`, `cod_subcategoria`) REFERENCES `subcategoria` (`codigo_categoria`, `codigo_subcategoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_seguridad`
--

LOCK TABLES `stock_seguridad` WRITE;
/*!40000 ALTER TABLE `stock_seguridad` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_seguridad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subcategoria`
--

DROP TABLE IF EXISTS `subcategoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subcategoria` (
  `codigo_subcategoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nombre` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `codigo_categoria` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`codigo_subcategoria`,`codigo_categoria`),
  KEY `fk_codigo_categoria_idx` (`codigo_categoria`),
  CONSTRAINT `fk_codigo_categoria` FOREIGN KEY (`codigo_categoria`) REFERENCES `categoria` (`codigo_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subcategoria`
--

LOCK TABLES `subcategoria` WRITE;
/*!40000 ALTER TABLE `subcategoria` DISABLE KEYS */;
INSERT INTO `subcategoria` VALUES ('C','Cargador tipo C','CAR'),('CAB','Ratón con cable ','RAT'),('CAB','Teclado con cable','TEC'),('INAL','Ratón inalámbrico','RAT'),('INAL','Teclado inalámbrico','TEC'),('PORT','Ordenador portátil','ORD'),('TORR','Ordenador de torre','ORD'),('USB','Cargador USB','CAR');
/*!40000 ALTER TABLE `subcategoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unidad`
--

DROP TABLE IF EXISTS `unidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidad` (
  `codigo_interno` int NOT NULL,
  `cod_estado` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `num_pedido` int DEFAULT NULL,
  `id_salida` int DEFAULT NULL,
  `id_oficina` int NOT NULL,
  `cod_articulo` int NOT NULL,
  PRIMARY KEY (`codigo_interno`),
  KEY `fk_estado_u_idx` (`cod_estado`),
  KEY `fk_pedido_u_idx` (`num_pedido`),
  KEY `fk_salida_u_idx` (`id_salida`),
  KEY `fk_oficina_u_idx` (`id_oficina`),
  KEY `fk_articulo_u_idx` (`cod_articulo`),
  CONSTRAINT `fk_articulo_u` FOREIGN KEY (`cod_articulo`) REFERENCES `articulo` (`codigo_articulo`),
  CONSTRAINT `fk_estado_u` FOREIGN KEY (`cod_estado`) REFERENCES `estado` (`codigo_estado`),
  CONSTRAINT `fk_oficina_u` FOREIGN KEY (`id_oficina`) REFERENCES `oficina` (`id_oficina`),
  CONSTRAINT `fk_pedido_u` FOREIGN KEY (`num_pedido`) REFERENCES `pedido` (`numero_pedido`),
  CONSTRAINT `fk_salida_u` FOREIGN KEY (`id_salida`) REFERENCES `salida` (`id_salida`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unidad`
--

LOCK TABLES `unidad` WRITE;
/*!40000 ALTER TABLE `unidad` DISABLE KEYS */;
/*!40000 ALTER TABLE `unidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'inventariado_bd'
--

--
-- Dumping routines for database 'inventariado_bd'
--

--
-- Final view structure for view `pedido_vw`
--

/*!50001 DROP VIEW IF EXISTS `pedido_vw`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `pedido_vw` (`numero_pedido`,`fecha_pedido`,`iva_pedido`,`coste_total`,`id_empleado`,`plazo_entrega`,`costes_envio`,`id_proveedor`,`id_oficina`,`fecha_recepcion`,`condicion_pago`,`medio_pago`,`devuelto`,`coste_unitario`,`numero_unidades`) AS select `p`.`numero_pedido` AS `numero_pedido`,`p`.`fecha_pedido` AS `fecha_pedido`,`p`.`iva_pedido` AS `iva_pedido`,`p`.`coste_total` AS `coste_total`,`p`.`id_empleado` AS `id_empleado`,`p`.`plazo_entrega` AS `plazo_entrega`,`p`.`costes_envio` AS `costes_envio`,`p`.`id_proveedor` AS `id_proveedor`,`p`.`id_oficina` AS `id_oficina`,`p`.`fecha_recepcion` AS `fecha_recepcion`,`p`.`condicion_pago` AS `condicion_pago`,`p`.`medio_pago` AS `medio_pago`,`p`.`devuelto` AS `devuelto`,(select (sum(`l`.`precio_linea`) / sum(`l`.`numero_unidades`)) from `linea` `l` where (`p`.`numero_pedido` = `l`.`numero_pedido`)) AS `coste_unitario`,(select sum(`l`.`numero_unidades`) from `linea` `l` where (`p`.`numero_pedido` = `l`.`numero_pedido`)) AS `numero_unidades` from `pedido` `p` order by `p`.`fecha_pedido` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-04 11:54:13
