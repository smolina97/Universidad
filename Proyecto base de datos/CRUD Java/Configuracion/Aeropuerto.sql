CREATE DATABASE  IF NOT EXISTS `mydb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `mydb`;
-- MySQL dump 10.13  Distrib 5.7.26, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: mydb
-- ------------------------------------------------------
-- Server version	5.7.26-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `avion_comercial`
--

DROP TABLE IF EXISTS `avion_comercial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avion_comercial` (
  `id_torre` int(7) NOT NULL,
  `id_comercial` int(7) NOT NULL,
  `tipo` varchar(45) NOT NULL COMMENT 'Marca y tipo de avion (Boeing 747)',
  `capacidad_equipaje` int(11) NOT NULL,
  `id_piloto_comercial` int(7) NOT NULL,
  PRIMARY KEY (`id_torre`,`id_comercial`,`id_piloto_comercial`),
  UNIQUE KEY `id_piloto_comercial_UNIQUE` (`id_piloto_comercial`),
  UNIQUE KEY `id_comercial_UNIQUE` (`id_comercial`),
  CONSTRAINT `id_piloto_comercial_a` FOREIGN KEY (`id_piloto_comercial`) REFERENCES `piloto_comercial` (`id_piloto_comercial`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `id_torre_ac` FOREIGN KEY (`id_torre`) REFERENCES `torre_control` (`id_torre`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avion_comercial`
--

LOCK TABLES `avion_comercial` WRITE;
/*!40000 ALTER TABLE `avion_comercial` DISABLE KEYS */;
INSERT INTO `avion_comercial` VALUES (1,1,'Boeing 747',100,12),(1,2,'Boeing 564',100,13),(1,4,'FF',13,15),(2,3,'Boeing 333',50,14);
/*!40000 ALTER TABLE `avion_comercial` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `mydb`.`comercial_AFTER_INSERT` AFTER INSERT ON `avion_comercial` FOR EACH ROW
BEGIN
 update torre_control set aviones = aviones+1 where id_torre = new.id_torre;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `avion_fuerza`
--

DROP TABLE IF EXISTS `avion_fuerza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avion_fuerza` (
  `id_torre` int(7) NOT NULL,
  `id_fuerza` int(7) NOT NULL COMMENT 'Codigo del avion militar',
  `modelo` varchar(45) NOT NULL,
  `armas` varchar(45) NOT NULL,
  `id_piloto_fuerza` int(7) NOT NULL,
  PRIMARY KEY (`id_torre`,`id_fuerza`,`id_piloto_fuerza`),
  UNIQUE KEY `codigo_UNIQUE` (`id_fuerza`),
  KEY `id_piloto_fuerza_a_idx` (`id_piloto_fuerza`),
  CONSTRAINT `id_piloto_fuerza_a` FOREIGN KEY (`id_piloto_fuerza`) REFERENCES `piloto_fuerza` (`id_piloto_fuerza`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `id_torre_af` FOREIGN KEY (`id_torre`) REFERENCES `torre_control` (`id_torre`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avion_fuerza`
--

LOCK TABLES `avion_fuerza` WRITE;
/*!40000 ALTER TABLE `avion_fuerza` DISABLE KEYS */;
INSERT INTO `avion_fuerza` VALUES (2,1,'Jet','Misiles',21),(2,2,'Cazadora','Ametralladora',22),(2,3,'Jet','Ametralladora',23);
/*!40000 ALTER TABLE `avion_fuerza` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `mydb`.`fuerza_AFTER_INSERT` AFTER INSERT ON `avion_fuerza` FOR EACH ROW
BEGIN
 update torre_control set aviones = aviones+1 where id_torre = new.id_torre;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `director`
--

DROP TABLE IF EXISTS `director`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `director` (
  `id_director` int(7) NOT NULL COMMENT 'Identificador del director\\n',
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  PRIMARY KEY (`id_director`),
  UNIQUE KEY `idDirector_UNIQUE` (`id_director`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `director`
--

LOCK TABLES `director` WRITE;
/*!40000 ALTER TABLE `director` DISABLE KEYS */;
INSERT INTO `director` VALUES (1,'Santiago','Molina'),(2,'Juan','Alzate');
/*!40000 ALTER TABLE `director` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleados`
--

DROP TABLE IF EXISTS `empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empleados` (
  `id` int(7) DEFAULT NULL,
  `nombre` varchar(45) DEFAULT NULL,
  `apellido` varchar(45) DEFAULT NULL,
  `cargo` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados`
--

LOCK TABLES `empleados` WRITE;
/*!40000 ALTER TABLE `empleados` DISABLE KEYS */;
INSERT INTO `empleados` VALUES (1,'Santiago','Molina','director'),(2,'Juan','Alzate','director'),(12,'Fernando','Molina','piloto comercial'),(13,'Juan','Gomez','piloto comercial'),(14,'Stiven','Soto','piloto comercial'),(15,'FFF','FF','piloto comercial'),(21,'Carlos','Gomez','piloto fuerza'),(22,'Felipe','Agudelo','piloto fuerza'),(23,'Stiven','Hernandez','piloto fuerza');
/*!40000 ALTER TABLE `empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipaje`
--

DROP TABLE IF EXISTS `equipaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `equipaje` (
  `id_equipaje` int(7) NOT NULL COMMENT 'Codigo del equipaje',
  `id_pasajero` int(7) NOT NULL,
  `id_vuelo` int(7) NOT NULL,
  `peso` int(11) NOT NULL,
  `tipo_equipaje` varchar(45) NOT NULL COMMENT 'si es de mano, maletin, etc',
  PRIMARY KEY (`id_equipaje`,`id_pasajero`,`id_vuelo`),
  UNIQUE KEY `idEquipaje_UNIQUE` (`id_equipaje`),
  KEY `id_pasajero_e_idx` (`id_pasajero`),
  KEY `id_vuelo_e_idx` (`id_vuelo`),
  CONSTRAINT `id_pasajero_e` FOREIGN KEY (`id_pasajero`) REFERENCES `pasajeros` (`id_pasajero`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `id_vuelo_e` FOREIGN KEY (`id_vuelo`) REFERENCES `vuelos` (`id_vuelo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipaje`
--

LOCK TABLES `equipaje` WRITE;
/*!40000 ALTER TABLE `equipaje` DISABLE KEYS */;
INSERT INTO `equipaje` VALUES (1,33,1,20,'mano');
/*!40000 ALTER TABLE `equipaje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pasajeros`
--

DROP TABLE IF EXISTS `pasajeros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pasajeros` (
  `id_pasajero` int(7) NOT NULL COMMENT 'Identificador del pasajero',
  `id_vuelo` int(7) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  `clase` varchar(45) NOT NULL COMMENT 'Si es primera clase o no',
  PRIMARY KEY (`id_pasajero`,`id_vuelo`),
  UNIQUE KEY `idPasajeros_UNIQUE` (`id_pasajero`),
  KEY `id_vuelo_p_idx` (`id_vuelo`),
  CONSTRAINT `id_vuelo_p` FOREIGN KEY (`id_vuelo`) REFERENCES `vuelos` (`id_vuelo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pasajeros`
--

LOCK TABLES `pasajeros` WRITE;
/*!40000 ALTER TABLE `pasajeros` DISABLE KEYS */;
INSERT INTO `pasajeros` VALUES (33,1,'Camilo','Cuestas','Economica'),(34,1,'Fernando','Hernadez','Ejecutiva'),(35,1,'Stiven','Mejia','Economica'),(36,2,'Camilo','Hernandez','Ejecutiva');
/*!40000 ALTER TABLE `pasajeros` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `mydb`.`pasajeros_AFTER_INSERT` AFTER INSERT ON `pasajeros` FOR EACH ROW
BEGIN
 update vuelos set cantidad_pasajeros = cantidad_pasajeros+1 where id_vuelo = new.id_vuelo;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `piloto_comercial`
--

DROP TABLE IF EXISTS `piloto_comercial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `piloto_comercial` (
  `id_piloto_comercial` int(7) NOT NULL COMMENT 'Identificador del piloto comercial\\n',
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  `horas_vuelo` int(11) NOT NULL,
  PRIMARY KEY (`id_piloto_comercial`),
  UNIQUE KEY `idPiloto comercial_UNIQUE` (`id_piloto_comercial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `piloto_comercial`
--

LOCK TABLES `piloto_comercial` WRITE;
/*!40000 ALTER TABLE `piloto_comercial` DISABLE KEYS */;
INSERT INTO `piloto_comercial` VALUES (12,'Fernando','Molina',200),(13,'Juan','Gomez',150),(14,'Stiven','Soto',200),(15,'FFF','FF',33);
/*!40000 ALTER TABLE `piloto_comercial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `piloto_fuerza`
--

DROP TABLE IF EXISTS `piloto_fuerza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `piloto_fuerza` (
  `id_piloto_fuerza` int(7) NOT NULL COMMENT 'Identificador del piloto militar',
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  `horas_vuelo` int(11) NOT NULL,
  `rango` varchar(45) NOT NULL,
  PRIMARY KEY (`id_piloto_fuerza`),
  UNIQUE KEY `idPiloto Fuerza Aerea_UNIQUE` (`id_piloto_fuerza`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `piloto_fuerza`
--

LOCK TABLES `piloto_fuerza` WRITE;
/*!40000 ALTER TABLE `piloto_fuerza` DISABLE KEYS */;
INSERT INTO `piloto_fuerza` VALUES (21,'Carlos','Gomez',200,'Sargento'),(22,'Felipe','Agudelo',40,'Cabo'),(23,'Stiven','Hernandez',1000,'General');
/*!40000 ALTER TABLE `piloto_fuerza` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `torre_control`
--

DROP TABLE IF EXISTS `torre_control`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `torre_control` (
  `id_torre` int(7) NOT NULL COMMENT 'Identificador Torre de control',
  `id_director` int(7) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `Aviones` int(7) DEFAULT '0',
  PRIMARY KEY (`id_torre`,`id_director`),
  UNIQUE KEY `Nombre_UNIQUE` (`Nombre`),
  UNIQUE KEY `IdTorre_UNIQUE` (`id_torre`),
  UNIQUE KEY `id_director_UNIQUE` (`id_director`),
  KEY `id_director_t_idx` (`id_director`),
  CONSTRAINT `id_director_t` FOREIGN KEY (`id_director`) REFERENCES `director` (`id_director`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `torre_control`
--

LOCK TABLES `torre_control` WRITE;
/*!40000 ALTER TABLE `torre_control` DISABLE KEYS */;
INSERT INTO `torre_control` VALUES (1,1,'Torre 1',3),(2,2,'Torre 2',4);
/*!40000 ALTER TABLE `torre_control` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vuelos`
--

DROP TABLE IF EXISTS `vuelos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vuelos` (
  `id_vuelo` int(7) NOT NULL COMMENT 'Identificador del vuelo',
  `id_comercial` int(7) NOT NULL,
  `tipo_vuelo` varchar(45) NOT NULL COMMENT 'Tipo de vuelo ( si es directo, charter,etc)',
  `cantidad_pasajeros` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_vuelo`,`id_comercial`),
  UNIQUE KEY `codigo_vuelo_UNIQUE` (`id_vuelo`),
  KEY `id_comercial_v_idx` (`id_comercial`),
  CONSTRAINT `id_comercial_v` FOREIGN KEY (`id_comercial`) REFERENCES `avion_comercial` (`id_comercial`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vuelos`
--

LOCK TABLES `vuelos` WRITE;
/*!40000 ALTER TABLE `vuelos` DISABLE KEYS */;
INSERT INTO `vuelos` VALUES (1,2,'Charter',3),(2,1,'Directo',1);
/*!40000 ALTER TABLE `vuelos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'mydb'
--

--
-- Dumping routines for database 'mydb'
--
/*!50003 DROP FUNCTION IF EXISTS `devolver_nombre_Torre` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `devolver_nombre_Torre`(id_avion int) RETURNS varchar(45) CHARSET utf8
BEGIN
DECLARE nombre_torre VARCHAR(45) default "none";
DECLARE id_t INT(7);

select id_torre
into id_t
from avion_comercial where
id_avion = id_comercial;

select nombre
into nombre_torre
from torre_control where
id_t = id_torre;

return nombre_torre;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `devolver_nombre_Torre_fuerza` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `devolver_nombre_Torre_fuerza`(id_avion int) RETURNS varchar(45) CHARSET utf8
BEGIN
DECLARE nombre_torre VARCHAR(45) default "none";
DECLARE id_t INT(7);

select id_torre
into id_t
from avion_fuerza where
id_avion = id_fuerza;

select nombre
into nombre_torre
from torre_control where
id_t = id_torre;

return nombre_torre;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `director_apellido` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `director_apellido`(torre int) RETURNS varchar(45) CHARSET utf8
BEGIN
DECLARE apellido_d VARCHAR(45) default "none";
DECLARE id_d INT(7);

select id_director
into id_d
from torre_control where
torre = id_torre;

select apellido
into apellido_d
from director where
id_d = id_director;

return apellido_d;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `director_nombre` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `director_nombre`(torre int) RETURNS varchar(45) CHARSET utf8
BEGIN
DECLARE nombre_d VARCHAR(45) default "none";
DECLARE id_d INT(7);

select id_director
into id_d
from torre_control where
torre = id_torre;

select nombre
into nombre_d
from director where
id_d = id_director;

return nombre_d;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `empleados` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `empleados`()
BEGIN

declare cargo varchar(45);
delete from empleados;

insert into empleados(id,nombre,apellido,cargo) Select id_director,nombre , apellido ,'director' from director;
insert into empleados(id,nombre,apellido,cargo) Select id_piloto_comercial,nombre , apellido,'piloto comercial' from piloto_comercial;
insert into empleados(id,nombre,apellido,cargo) Select id_piloto_fuerza,nombre , apellido,'piloto fuerza' from piloto_fuerza;
select * from empleados;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-06-08 17:52:57
