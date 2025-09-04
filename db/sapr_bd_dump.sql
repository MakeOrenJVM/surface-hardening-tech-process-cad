-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: sapr_bd
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `operation_card`
--

DROP TABLE IF EXISTS `operation_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_card` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_name` varchar(100) DEFAULT NULL,
  `prep_surface` text,
  `setup` text,
  `induction_heating` text,
  `quenching` text,
  `tempering` text,
  `post_tempering_cooling` text,
  `hardness_control` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_card`
--

LOCK TABLES `operation_card` WRITE;
/*!40000 ALTER TABLE `operation_card` DISABLE KEYS */;
INSERT INTO `operation_card` VALUES (1,'Цилиндрическая деталь','Очистка от загрязнений, удаление заусенцев, обезжиривание поверхности','Установка в центрирующее приспособление, фиксация для точного позиционирования','Нагрев зоны до температуры закалки с использованием индукционного оборудования','Немедленное охлаждение с применением охлаждающей среды:','Термическая обработка для снятия внутренних напряжений после закалки','Охлаждение на воздухе до комнатной температуры','Измерение твёрдости поверхностного слоя, оценка равномерности и глубины закалки'),(4,'Вал-шестерня','Механическая очистка поверхности от загрязнений и окалины. Обеспечить чистую и сухую поверхность перед закалкой.','Установить вал-шестерню горизонтально на роликовую станину с фиксацией. Обеспечить зазор между индуктором и деталью 2–3 см.','Плавное включение тока в индукторе для прогрева зубьев и впадин. Деталь равномерно прогревается вращением или поступательным движением.','После достижения необходимой температуры произвести немедленное охлаждение.','Произвести отпуск для снятия внутренних напряжений.','Охладить деталь до комнатной температуры на воздухе или в масле.','Проверка твердости зубьев и зоны закалки, а также контроль глубины закалённого слоя.'),(5,'Поршневой шток','Удаление загрязнений и полировка поверхности.','Установка в патрон с зазором 20–30 мм до индукторного канала.','Нагрев по оси штока с использованием сканирования вдоль.','Быстрое водяное охлаждение после нагрева.','Произвести отпуск для снятия внутренних напряжений','Сушка на воздухе.','Проверить твердость по всей длине.'),(6,'Распределительный вал','Удаление масла, опилов, проверка камер и кулачков.','Установка в индуктор с вращением до 100 об/мин, зазор 2–3 см по всей длине.','Индукционный нагрев до требуемой температуры.','Охлаждение струёй равномерно по всей длине функционирующих контуров','Произвести отпуск для снятия внутренних напряжений.','Охлаждение на воздухе до ≈25 °C.','Измерение твердости на кулачках после охлаждения и отпуска.'),(7,'Коленчатый вал','Очистка поверхности от загрязнений и маслоохраняющих веществ; проверка отсутствия дефектов.','Установить вал в U‑образный индуктор, обеспечить зазор 2–3 см от рабочей поверхности до индукторной катушки.','Вращение вала при 100–300 об/мин с поддержанием зазора 2–3 см.','Мгновенное охлаждение: распыление.','Сразу после закалки провести низкотемпературный отпуск для снятия напряжений.','Охлаждение на воздухе до комнатной температуры, контроль геометрии.','Контроль твердости по каналу шатунной шейки после охлаждения.');
/*!40000 ALTER TABLE `operation_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `id` int NOT NULL AUTO_INCREMENT,
  `case_author` varchar(100) DEFAULT NULL,
  `case_created_at` datetime DEFAULT NULL,
  `part_type_name` varchar(100) DEFAULT NULL,
  `steel_grades_id` int NOT NULL,
  `quench_depths_id` int NOT NULL,
  `tvch_generators_id` int NOT NULL,
  `operation_card_id` int NOT NULL,
  `process_power_kw` float DEFAULT NULL,
  `process_frequency_khz` float DEFAULT NULL,
  `heating_time_sec` float DEFAULT NULL,
  `heating_temperature_c` int DEFAULT NULL,
  `part_diameter_mm` float DEFAULT NULL,
  `part_length_mm` float DEFAULT NULL,
  `inductor_speed_mm_per_sec` float DEFAULT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  `tempering_temperature_c` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_steel_grades` (`steel_grades_id`),
  KEY `fk_quench_depths` (`quench_depths_id`),
  KEY `fk_tvch_generators` (`tvch_generators_id`),
  KEY `fk_operation_card` (`operation_card_id`),
  CONSTRAINT `fk_operation_card` FOREIGN KEY (`operation_card_id`) REFERENCES `operation_card` (`id`),
  CONSTRAINT `fk_quench_depths` FOREIGN KEY (`quench_depths_id`) REFERENCES `quench_depths` (`id`),
  CONSTRAINT `fk_steel_grades` FOREIGN KEY (`steel_grades_id`) REFERENCES `steel_grades` (`id`),
  CONSTRAINT `fk_tvch_generators` FOREIGN KEY (`tvch_generators_id`) REFERENCES `tvch_generators` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,'Руслан','2025-06-09 22:41:44','Вал',1,1,1,1,50,200,5,850,50,120,2.5,'Проект №1',0),(2,'Олег','2025-06-19 00:00:00','Цилиндрическая деталь',5,24,11,1,18.9971,129.099,10,810,50,100,10,'Проект25',340),(3,'Иван','2025-06-19 00:00:00','Цилиндрическая деталь',1,10,5,1,8.97342,200,10,890,50,100,10,'Проект122',175),(13,'Руслан','2025-06-20 00:00:00','Вал-шестерня',2,19,11,4,31.9337,145.095,5,850,50,100,20,'Проект11',340),(14,'Руслан','2025-06-20 00:00:00','Вал-шестерня',3,17,7,4,13.9999,153.393,10,830,50,100,10,'Проект20',415),(16,'Руслан','2025-06-20 00:00:00','Вал-шестерня',3,17,7,4,13.9999,153.393,10,830,50,100,10,'Проект55',415),(18,'Руслан','2025-06-20 00:00:00','Вал-шестерня',2,17,7,4,14.3455,153.393,10,850,50,100,10,'Проект1001',340),(19,'Руслан','2025-06-20 00:00:00','Вал-шестерня',2,19,7,4,15.9669,145.095,10,850,50,100,10,'1002',340);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quench_depths`
--

DROP TABLE IF EXISTS `quench_depths`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quench_depths` (
  `id` int NOT NULL AUTO_INCREMENT,
  `depth_mm` decimal(3,1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `depth_mm` (`depth_mm`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quench_depths`
--

LOCK TABLES `quench_depths` WRITE;
/*!40000 ALTER TABLE `quench_depths` DISABLE KEYS */;
INSERT INTO `quench_depths` VALUES (1,0.1),(2,0.2),(3,0.3),(4,0.4),(5,0.5),(6,0.6),(7,0.7),(8,0.8),(9,0.9),(10,1.0),(11,1.1),(12,1.2),(13,1.3),(14,1.4),(15,1.5),(16,1.6),(17,1.7),(18,1.8),(19,1.9),(20,2.0),(21,2.1),(22,2.2),(23,2.3),(24,2.4),(25,2.5),(26,2.6),(27,2.7),(28,2.8),(29,2.9),(30,3.0),(31,3.1),(32,3.2),(33,3.3),(34,3.4),(35,3.5),(36,3.6),(37,3.7),(38,3.8),(39,3.9),(40,4.0),(41,4.1),(42,4.2),(43,4.3),(44,4.4),(45,4.5),(46,4.6),(47,4.7),(48,4.8),(49,4.9),(50,5.0);
/*!40000 ALTER TABLE `quench_depths` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `steel_grades`
--

DROP TABLE IF EXISTS `steel_grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `steel_grades` (
  `id` int NOT NULL AUTO_INCREMENT,
  `steel_grade` varchar(50) NOT NULL,
  `steel_type` varchar(50) NOT NULL,
  `hardening_temp_min` int NOT NULL,
  `hardening_temp_max` int NOT NULL,
  `hardening_temp_recommended` int NOT NULL,
  `cooling_medium` varchar(50) NOT NULL,
  `tempering_temp_min` int NOT NULL,
  `tempering_temp_max` int NOT NULL,
  `tempering_temp_recommended` int NOT NULL,
  `hardness_min` int NOT NULL,
  `hardness_max` int NOT NULL,
  `density_g_cm3` decimal(4,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `steel_grades`
--

LOCK TABLES `steel_grades` WRITE;
/*!40000 ALTER TABLE `steel_grades` DISABLE KEYS */;
INSERT INTO `steel_grades` VALUES (1,'Сталь 20','Углеродистые конструкционные',880,900,890,'Вода',150,200,175,18,22,7.85),(2,'Сталь 40','Углеродистые конструкционные',840,860,850,'Вода или масло',180,500,340,28,50,7.85),(3,'Сталь 45','Углеродистые конструкционные',820,840,830,'Вода или масло',180,650,415,25,52,7.85),(4,'Сталь 50','Углеродистые конструкционные',810,830,820,'Вода или масло',180,650,415,28,54,7.85),(5,'Сталь 60','Углеродистые конструкционные',800,820,810,'Масло',180,500,340,35,56,7.85),(6,'Сталь 65Г','Углеродистые конструкционные',790,810,800,'Масло',200,400,300,45,58,7.85),(7,'Сталь 40Х','Легированные конструкционные',840,860,850,'Масло',180,650,415,25,54,7.85),(8,'Сталь 30ХГСА','Легированные конструкционные',870,890,880,'Масло',200,650,425,30,55,7.85),(9,'Сталь 38ХС','Легированные конструкционные',850,870,860,'Масло',200,650,425,35,56,7.85),(10,'Сталь 18ХГТ','Легированные конструкционные',930,950,940,'Масло',180,200,190,58,62,7.85),(11,'У8А','Инструментальные стали',760,780,770,'Вода или масло',180,320,250,58,64,7.85),(12,'У10А','Инструментальные стали',760,780,770,'Масло',150,250,200,60,64,7.85),(13,'Х12М','Инструментальные стали',1020,1040,1030,'Масло или воздух',200,580,390,45,62,7.70),(14,'Р6М5','Инструментальные стали',1210,1230,1220,'Масло или воздух',550,570,560,62,65,8.10);
/*!40000 ALTER TABLE `steel_grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tvch_generators`
--

DROP TABLE IF EXISTS `tvch_generators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tvch_generators` (
  `id` int NOT NULL AUTO_INCREMENT,
  `number` int DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `power_kw` float DEFAULT NULL,
  `frequency_khz` float DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tvch_generators`
--

LOCK TABLES `tvch_generators` WRITE;
/*!40000 ALTER TABLE `tvch_generators` DISABLE KEYS */;
INSERT INTO `tvch_generators` VALUES (1,1,'ВЧИ-2,5/5,28-ЗП',2.5,5280,'Ламповые'),(2,3,'ВЧИ-4/1,76-ЗП',4,1760,'Ламповые'),(3,4,'ВЧИ-6,3/0,88-ЗП',6.3,880,'Ламповые'),(4,5,'ВЧИ-6,3/1,76-ЗП',6.3,1760,'Ламповые'),(5,6,'ВЧИ-4-10/0,44',10,440,'Ламповые'),(6,7,'ВЧИ-10/0,88-ЗП',10,880,'Ламповые'),(7,8,'ВЧИ-16/0,44-ЗП',16,440,'Ламповые'),(8,9,'ВЧИ-16/0,88-ЗП',16,880,'Ламповые'),(9,10,'ВЧИ-25/0,07-ЗП',25,66,'Ламповые'),(10,11,'ВЧИ-40/0,07-ЗП',40,66,'Ламповые'),(11,12,'ВЧИ-40/0,44-ЗП',40,440,'Ламповые'),(12,13,'ВЧГ1-60/0,066',60,66,'Ламповые'),(13,14,'ЛЗ-67-В',60,66,'Ламповые'),(14,15,'ВЧГ6-60/0,44-ЗП',60,440,'Ламповые'),(15,16,'ЛЗ-107-В',100,66,'Ламповые'),(16,17,'ВЧИ-100/0,07-ЗП',100,66,'Ламповые'),(17,18,'ВЧИ-100/0,44-ЗП',100,440,'Ламповые'),(18,19,'ВЧИ-160/0,07-ЗП',160,66,'Ламповые'),(19,20,'ВЧИ-160/0,44-ЗП',160,440,'Ламповые'),(20,21,'ЛЗ-207-В',200,66,'Ламповые'),(21,27,'ВПЧ-100-2400',100,2.4,'Ламповые'),(22,28,'ПВС-100/2500',100,2.5,'Ламповые'),(23,29,'ПВВ-100/8000',100,8,'Ламповые'),(24,30,'ВПЧ-100-8000',100,8,'Ламповые'),(25,31,'ВЭП-100/8000',100,8,'Ламповые'),(26,32,'ОПЧ-250-2,4-380',250,2.4,'Ламповые'),(27,33,'ВГО-250-2500',250,2.5,'Ламповые'),(28,34,'ОПЧ-250-10,0-380',250,10,'Ламповые'),(29,35,'ОПЧ-320-1,0-3000',320,1,'Ламповые'),(30,36,'ВГО-500-25-00',500,2.5,'Ламповые'),(31,37,'ОПЧ-500-4,0-3000',500,4,'Ламповые'),(32,38,'ВГО-1500-1000',1500,1,'Ламповые'),(33,39,'ВГВФ-1580-2500',1500,2.5,'Ламповые'),(34,22,'ВПЧ-50-2400',50,2.4,'Машинные'),(35,23,'ПВ-50/2500',50,2.5,'Машинные'),(36,24,'ВПЧ-50-8000',50,8,'Машинные'),(37,25,'ВЭП-60/2400',60,2.4,'Машинные'),(38,26,'ВЭП-60/8000',60,8,'Машинные'),(39,40,'ВТГ-5-0,066',5,66,'Тиристорные'),(40,41,'ВТГ-10-0,066',10,66,'Тиристорные'),(41,42,'ВТГ-25-0,044',25,44,'Тиристорные'),(42,43,'ВТГ-50-0,022',50,22,'Тиристорные'),(43,44,'ТПЧР-63-10',63,10,'Тиристорные'),(44,45,'ТПЧР-100-8',100,8,'Тиристорные'),(45,46,'ТПЧР-160-2,4',160,2.4,'Тиристорные'),(46,47,'ТПЧР-160-4,0',160,4,'Тиристорные'),(47,48,'ТПЧР-250-2,4',250,2.4,'Тиристорные'),(48,49,'ТПЧР-250-4,0',250,4,'Тиристорные'),(49,50,'ТПЧР-250-8,0',250,8,'Тиристорные'),(50,51,'ТПЧР-320-2,4',320,2.4,'Тиристорные'),(51,52,'ТПЧР-500-1,0',500,1,'Тиристорные');
/*!40000 ALTER TABLE `tvch_generators` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-04 23:47:08
