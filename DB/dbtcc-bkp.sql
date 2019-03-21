-- MySQL dump 10.13  Distrib 8.0.15, for macos10.14 (x86_64)
--
-- Host: localhost    Database: dbtcc
-- ------------------------------------------------------
-- Server version	8.0.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Collect`
--

DROP TABLE IF EXISTS `Collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Collect` (
  `idCollect` int(11) NOT NULL AUTO_INCREMENT,
  `accx` int(11) NOT NULL,
  `accy` int(11) NOT NULL,
  `accz` int(11) NOT NULL,
  `girx` int(11) NOT NULL,
  `giry` int(11) NOT NULL,
  `girz` int(11) NOT NULL,
  `temp` int(11) NOT NULL,
  `data` datetime NOT NULL,
  `Gateway_idGateway` int(11) NOT NULL,
  `Place_idPlace` int(11) NOT NULL,
  PRIMARY KEY (`idCollect`),
  KEY `fk_Collect_Gateway_idx` (`Gateway_idGateway`),
  KEY `fk_Collect_Place1_idx` (`Place_idPlace`),
  CONSTRAINT `fk_Collect_Gateway` FOREIGN KEY (`Gateway_idGateway`) REFERENCES `gateway` (`idGateway`),
  CONSTRAINT `fk_Collect_Place1` FOREIGN KEY (`Place_idPlace`) REFERENCES `place` (`idPlace`)
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Collect`
--

LOCK TABLES `Collect` WRITE;
/*!40000 ALTER TABLE `Collect` DISABLE KEYS */;
INSERT INTO `Collect` VALUES (147,99,54,38,73,18,88,72,'2019-03-19 22:17:48',1,1),(148,99,54,38,73,18,88,72,'2019-03-19 22:27:48',1,1),(149,99,96,47,57,49,71,17,'2019-03-19 22:32:16',1,1),(150,99,3,42,37,81,35,22,'2019-03-19 22:32:36',1,1),(151,99,89,18,84,14,74,51,'2019-03-19 22:32:49',1,1),(152,99,30,46,38,50,71,97,'2019-03-19 22:45:36',1,1),(153,99,19,52,47,75,59,20,'2019-03-19 22:57:01',1,1),(154,99,32,41,66,32,24,63,'2019-03-19 23:00:21',1,1),(155,99,6,78,31,9,90,95,'2019-03-19 23:00:31',1,1),(156,99,76,6,93,30,79,2,'2019-03-19 23:00:38',1,1);
/*!40000 ALTER TABLE `Collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Endpoint`
--

DROP TABLE IF EXISTS `Endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Endpoint` (
  `idEndpoint` int(11) NOT NULL AUTO_INCREMENT,
  `mac` varchar(45) NOT NULL,
  `Place_idPlace` int(11) NOT NULL,
  PRIMARY KEY (`idEndpoint`),
  KEY `fk_Endpoint_Place1_idx` (`Place_idPlace`),
  CONSTRAINT `fk_Endpoint_Place1` FOREIGN KEY (`Place_idPlace`) REFERENCES `place` (`idPlace`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Endpoint`
--

LOCK TABLES `Endpoint` WRITE;
/*!40000 ALTER TABLE `Endpoint` DISABLE KEYS */;
INSERT INTO `Endpoint` VALUES (1,'00:15:85:14:9C:09',1);
/*!40000 ALTER TABLE `Endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Equipment`
--

DROP TABLE IF EXISTS `Equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Equipment` (
  `idEquipment` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`idEquipment`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Equipment`
--

LOCK TABLES `Equipment` WRITE;
/*!40000 ALTER TABLE `Equipment` DISABLE KEYS */;
INSERT INTO `Equipment` VALUES (1,'Gerador 1');
/*!40000 ALTER TABLE `Equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Gateway`
--

DROP TABLE IF EXISTS `Gateway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Gateway` (
  `idGateway` int(11) NOT NULL AUTO_INCREMENT,
  `mac` varchar(45) NOT NULL,
  PRIMARY KEY (`idGateway`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Gateway`
--

LOCK TABLES `Gateway` WRITE;
/*!40000 ALTER TABLE `Gateway` DISABLE KEYS */;
INSERT INTO `Gateway` VALUES (1,'24:0A:C4:32:02:D8');
/*!40000 ALTER TABLE `Gateway` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Place`
--

DROP TABLE IF EXISTS `Place`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `Place` (
  `idPlace` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) NOT NULL,
  `Equipment_idEquipment` int(11) NOT NULL,
  PRIMARY KEY (`idPlace`),
  KEY `fk_Place_Equipment1_idx` (`Equipment_idEquipment`),
  CONSTRAINT `fk_Place_Equipment1` FOREIGN KEY (`Equipment_idEquipment`) REFERENCES `equipment` (`idEquipment`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Place`
--

LOCK TABLES `Place` WRITE;
/*!40000 ALTER TABLE `Place` DISABLE KEYS */;
INSERT INTO `Place` VALUES (1,'Sala 1',1);
/*!40000 ALTER TABLE `Place` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-03-19 23:02:43
