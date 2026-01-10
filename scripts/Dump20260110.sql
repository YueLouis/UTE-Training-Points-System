-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: shortline.proxy.rlwy.net    Database: railway
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `event_categories`
--

DROP TABLE IF EXISTS `event_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_categories`
--

LOCK TABLES `event_categories` WRITE;
/*!40000 ALTER TABLE `event_categories` DISABLE KEYS */;
INSERT INTO `event_categories` VALUES (1,'Hoạt động phong trào','Văn nghệ, thể thao, hội trại...'),(2,'Công tác xã hội','Hiến máu, Mùa hè xanh, Tiếp sức mùa thi...'),(3,'Chuyên đề doanh nghiệp','Talkshow, workshop cùng doanh nghiệp đối tác');
/*!40000 ALTER TABLE `event_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_registrations`
--

DROP TABLE IF EXISTS `event_registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_registrations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `registration_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('REGISTERED','APPROVED','CHECKED_IN','COMPLETED','ABSENT','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'REGISTERED',
  `checkin_time` datetime DEFAULT NULL,
  `checkout_time` datetime DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_event_student` (`event_id`,`student_id`),
  KEY `fk_reg_student` (`student_id`),
  KEY `idx_reg_event_status` (`event_id`,`status`),
  KEY `idx_reg_student_status` (`student_id`,`status`),
  KEY `idx_reg_checkin` (`checkin_time`),
  KEY `idx_reg_checkout` (`checkout_time`),
  CONSTRAINT `fk_reg_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  CONSTRAINT `fk_reg_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_registrations`
--

LOCK TABLES `event_registrations` WRITE;
/*!40000 ALTER TABLE `event_registrations` DISABLE KEYS */;
INSERT INTO `event_registrations` VALUES (1,1,2,'2025-12-07 00:26:25','CHECKED_IN','2025-12-03 07:50:00','2025-12-03 11:05:00','Tham dự đầy đủ buổi đối thoại.'),(2,1,3,'2025-12-07 00:26:25','COMPLETED','2025-12-03 07:55:00','2025-12-03 11:00:00','Tham dự đầy đủ buổi đối thoại.'),(3,1,4,'2025-12-07 00:26:25','COMPLETED','2025-12-03 08:00:00','2025-12-03 11:00:00','Tham dự đầy đủ buổi đối thoại.'),(4,1,5,'2025-12-07 00:26:25','COMPLETED','2025-12-03 08:02:00','2025-12-03 11:00:00','Tham dự đầy đủ buổi đối thoại.'),(5,5,2,'2025-12-26 08:23:00','CHECKED_IN','2025-12-26 08:23:58','2025-12-26 14:45:12','#dbcdrl #tpnaml'),(6,4,2,'2025-12-26 15:32:32','CHECKED_IN','2025-12-26 15:33:01','2025-12-26 15:33:22','test event 4'),(7,7,2,'2025-12-27 15:44:23','COMPLETED',NULL,'2025-12-27 15:44:23','ONLINE_SURVEY'),(8,6,2,'2025-12-27 16:43:35','CHECKED_IN','2025-12-27 16:43:35','2025-12-27 16:44:41',NULL),(9,11,2,'2026-01-06 05:01:20','COMPLETED',NULL,'2026-01-06 05:01:20','ONLINE_SURVEY'),(10,11,5,'2026-01-06 05:12:54','COMPLETED',NULL,'2026-01-06 05:12:54','ONLINE_SURVEY'),(15,12,3,'2026-01-07 04:19:34','COMPLETED',NULL,'2026-01-07 04:19:34','ONLINE_SURVEY'),(16,14,3,'2026-01-07 04:39:03','COMPLETED',NULL,'2026-01-07 04:39:03','ONLINE_SURVEY'),(17,13,4,'2026-01-07 14:38:05','COMPLETED','2026-01-07 14:45:25','2026-01-07 14:49:25',NULL),(18,13,2,'2026-01-07 14:45:11','COMPLETED','2026-01-07 14:45:11','2026-01-07 14:49:15',NULL),(19,13,3,'2026-01-07 14:45:21','COMPLETED','2026-01-07 14:45:21','2026-01-07 14:49:21',NULL),(20,13,5,'2026-01-07 14:45:28','COMPLETED','2026-01-07 14:45:28','2026-01-07 14:49:28',NULL),(21,13,6,'2026-01-07 14:45:33','COMPLETED','2026-01-07 14:45:33','2026-01-07 14:49:30',NULL),(23,13,7,'2026-01-07 14:48:47','COMPLETED','2026-01-07 14:48:47','2026-01-07 14:49:33',NULL),(24,13,8,'2026-01-07 14:48:52','COMPLETED','2026-01-07 14:48:52','2026-01-07 14:49:36',NULL),(25,13,9,'2026-01-07 14:48:55','COMPLETED','2026-01-07 14:48:55','2026-01-07 14:49:40',NULL),(26,13,10,'2026-01-07 14:48:57','COMPLETED','2026-01-07 14:48:57','2026-01-07 14:49:42',NULL),(27,13,11,'2026-01-07 14:49:00','COMPLETED','2026-01-07 14:49:00','2026-01-07 14:49:45',NULL),(28,13,12,'2026-01-07 15:33:30','COMPLETED','2026-01-07 15:33:30','2026-01-07 17:17:28',NULL),(29,13,13,'2026-01-07 15:33:35','COMPLETED','2026-01-07 15:33:35','2026-01-07 17:29:49',NULL),(30,13,14,'2026-01-07 15:33:40','COMPLETED','2026-01-07 15:33:40','2026-01-07 17:29:52',NULL),(31,13,15,'2026-01-07 15:33:43','COMPLETED','2026-01-07 15:33:43','2026-01-07 17:32:00',NULL),(32,18,4,'2026-01-08 15:58:47','REGISTERED',NULL,NULL,NULL),(33,14,4,'2026-01-08 11:20:52','CANCELLED',NULL,NULL,NULL),(34,18,2,'2026-01-08 15:42:43','CANCELLED',NULL,NULL,NULL),(35,20,3,'2026-01-08 17:06:03','CANCELLED',NULL,NULL,NULL);
/*!40000 ALTER TABLE `event_registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `semester_id` bigint NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `banner_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `registration_deadline` datetime DEFAULT NULL,
  `max_participants` int DEFAULT NULL,
  `point_type_id` bigint NOT NULL,
  `point_value` int NOT NULL DEFAULT '0',
  `created_by` bigint DEFAULT NULL,
  `status` enum('DRAFT','OPEN','CLOSED','DONE','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT',
  `event_mode` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ATTENDANCE',
  `survey_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `survey_secret_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `org_unit_id` bigint DEFAULT NULL COMMENT 'Đơn vị tổ chức',
  `scope_level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'SCHOOL' COMMENT 'SCHOOL, FACULTY, CLUB',
  PRIMARY KEY (`id`),
  KEY `fk_events_semester` (`semester_id`),
  KEY `fk_events_category` (`category_id`),
  KEY `fk_events_point_type` (`point_type_id`),
  KEY `fk_events_created_by` (`created_by`),
  KEY `idx_event_org` (`org_unit_id`),
  KEY `idx_event_scope` (`scope_level`),
  CONSTRAINT `fk_event_org` FOREIGN KEY (`org_unit_id`) REFERENCES `org_units` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_events_category` FOREIGN KEY (`category_id`) REFERENCES `event_categories` (`id`),
  CONSTRAINT `fk_events_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_events_point_type` FOREIGN KEY (`point_type_id`) REFERENCES `point_types` (`id`),
  CONSTRAINT `fk_events_semester` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,1,1,'Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026','[+3 ĐIỂM RÈN LUYỆN] ĐĂNG KÝ THAM DỰ GẶP GỠ ĐỐI THOẠI GIỮA LÃNH ĐẠO NHÀ TRƯỜNG VỚI SINH VIÊN HỌC KỲ I NĂM HỌC 2025 - 2026 | Phòng Tuyển sinh và Công tác Sinh viên SPKT','Hội trường lớn khu A','','2026-01-07 13:23:00','2026-01-08 13:23:00','2026-01-07 02:23:00',50,1,3,1,'CLOSED','ATTENDANCE','',NULL,NULL,'SCHOOL'),(2,1,2,'Phân phát ba lô trường','[+5 ĐIỂM CTXH] ĐĂNG KÝ PHÂN PHÁT BA LÔ TRƯỜNG | Phòng Quản trị Cơ sở Vật chất - ĐH Sư phạm Kỹ thuật TP. HCM [30 FCFS]','Sảnh tòa nhà trung tâm','','2025-11-14 06:00:00','2025-11-14 10:00:00','2025-11-13 16:59:59',30,2,5,1,'CLOSED','ATTENDANCE','',NULL,NULL,'SCHOOL'),(3,1,3,'ĐĂNG KÝ HỘI THẢO CHUYÊN ĐỀ - NGÀNH BIÊN PHIÊN DỊCH','[+1 ĐIỂM CDDN] ĐĂNG KÝ HỘI THẢO CHUYÊN ĐỀ - NGÀNH BIÊN PHIÊN DỊCH - HK2/2024-2025 | Khoa Ngoại Ngữ','Phòng họp 2 tầng 6 toà nhà trung tâm',NULL,'2025-10-18 11:00:00','2025-10-24 21:00:00','2025-10-20 23:59:59',100,3,1,1,'CLOSED','ATTENDANCE',NULL,NULL,NULL,'SCHOOL'),(4,1,1,'ĐĂNG KÝ THAM GIA TỌA ĐÀM: VƯƠN MÌNH HỘI NHẬP - TỰ TIN CHINH PHỤC SV5T | Tuổi trẻ UTE','[+3 ĐIỂM RÈN LUYỆN] | ĐĂNG KÝ THAM GIA TỌA ĐÀM: VƯƠN MÌNH HỘI NHẬP - TỰ TIN CHINH PHỤC SV5T | Tuổi trẻ UTE','Hội trường lớn Khu A',NULL,'2025-12-31 08:30:00','2025-12-31 12:00:00',NULL,150,1,3,1,'CLOSED','ATTENDANCE',NULL,NULL,NULL,'SCHOOL'),(5,1,1,'KHẢO SÁT NHU CẦU THỰC TẬP VÀ VIỆC LÀM CỦA SINH VIÊN TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM NĂM 2026','[+3 ĐIỂM RÈN LUYỆN] | KHẢO SÁT NHU CẦU THỰC TẬP VÀ VIỆC LÀM CỦA SINH VIÊN TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM NĂM 2026 | ERO - Phòng Quan hệ Doanh nghiệp trường ĐH SPKT TP.HCM','','','2025-12-16 00:00:00','2025-12-31 00:00:00','2025-12-31 00:00:01',50,1,3,1,'CLOSED','ONLINE','https://docs.google.com/forms/d/e/1FAIpQLSezG7iBGivyheUwsBcIGflO2AmEZFp3SDCSL_L4dNNEJ3aGYg/viewform',NULL,NULL,'SCHOOL'),(6,1,1,'ĐĂNG KÝ THAM GIA BUỔI TRAINING-DAY | Cuộc thi Marketing - M-Rise','[+3 ĐIỂM RÈN LUYỆN] | ĐĂNG KÝ THAM GIA BUỔI TRAINING-DAY | Cuộc thi Marketing - M-Rise. Đối tượng: Các đội thi và sinh viên trường Đại học Sư phạm Kỹ thuật Tp. HCM','Phòng A122, Tòa nhà khu A','','2026-01-07 12:05:00','2026-01-08 12:05:00','2025-12-27 17:00:00',100,1,3,1,'OPEN','ATTENDANCE','',NULL,NULL,'SCHOOL'),(7,1,1,'KHẢO SÁT NHU CẦU THỰC TẬP VÀ VIỆC LÀM CỦA SINH VIÊN TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM NĂM 2026','[+3 ĐIỂM RÈN LUYỆN] | KHẢO SÁT NHU CẦU THỰC TẬP VÀ VIỆC LÀM CỦA SINH VIÊN TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM NĂM 2026 | ERO - Phòng Quan hệ Doanh nghiệp trường ĐH SPKT TP.HCM',NULL,NULL,'2025-12-16 00:00:00','2026-01-31 00:00:00','2026-01-10 00:00:00',5,1,3,1,'OPEN','ONLINE','https://docs.google.com/forms/d/e/1FAIpQLSezG7iBGivyheUwsBcIGflO2AmEZFp3SDCSL_L4dNNEJ3aGYg/viewform?fbclid=IwY2xjawO8zkFleHRuA2FlbQIxMABicmlkETF5cXpkdmZEVllnR0dJaGN1c3J0YwZhcHBfaWQQMjIyMDM5MTc4ODIwMDg5MgABHuE00_-mG9uospx04ZtrQZNffGU9_efWKM58V-R9veE6W81r6mIblFYKiqrx_aem_8X95k0q0AqEgYRXBzg5anQ','ERO_2025',NULL,'SCHOOL'),(8,1,2,'Hien mau nhan dao','[+5 DIEM CTXH] Sinh vien thuc hien hanh dong hien mau voi nghia cu cao dep','Phong Y Te','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcStEA5FaiWDcSJhyDjgiByqw8WKylh6kODV9w&s','2026-01-15 00:30:01','2026-01-16 03:30:01','2026-01-12 16:59:00',300,2,5,1,'OPEN','ATTENDANCE','https://googleforms/dangkyhienmau.com',NULL,NULL,'SCHOOL'),(9,1,1,'THAM GIA CỔ VŨ VĂN NGHỆ','[+4 ĐIỂM RÈN LUYỆN] THAM GIA CỔ VŨ CUỘC THI VĂN NGHỆ \"HỘI XUÂN 2026 - TIẾNG HÁT SINH VIÊN\"','Hội trường lớn khu A','https://cms.piklab.vn/resources/Tai%20nguyen%20Piklab/File%20design%20TMDT/piklab25g.jpg','2026-01-22 11:00:00','2026-01-22 13:00:00','2026-01-21 16:59:00',50,1,4,1,'OPEN','ATTENDANCE','https://googleforms/dangkycovu/hoixuan2026.com',NULL,NULL,'SCHOOL'),(10,2,3,' ĐĂNG KÝ THAM GIA HỘI THẢO SỨC KHOẺ DA LIỄU','[+1 ĐIỂM CDDN] | ĐĂNG KÝ THAM GIA HỘI THẢO SỨC KHOẺ DA LIỄU “NGẠI HỎI NHƯNG MUỐN BIẾT” | Sacute - Trung tâm Tư vấn & Hỗ trợ sinh viên ĐH CNKT Tp.HCM X Trạm Y Tế Trường Đại học Công nghệ Kỹ thuật TP.HCM [? FCFS]','Hội trường lớn khu A',NULL,'2026-01-07 08:30:00','2026-01-07 11:30:00','2026-01-06 23:59:00',300,3,1,1,'CLOSED','ATTENDANCE','https://forms.gle/ayHi8dp8qMgBHP4N6',NULL,NULL,'SCHOOL'),(11,1,1,'SỰ KIỆN KHẢO SÁT TEST SỐ 11','Nội dung khảo sát thử nghiệm hệ thống mã bí mật','Online',NULL,'2025-12-31 17:00:00','2026-01-31 16:59:59','2026-01-31 16:59:59',2,1,3,1,'CLOSED','ONLINE','https://docs.google.com/forms/d/e/1FAIpQLSezG7iBGivyheUwsBcIGflO2AmEZFp3SDCSL_L4dNNEJ3aGYg/viewform','HELLOWORLD',NULL,'SCHOOL'),(12,1,1,'Hội thảo Chống gian lận Online','Làm khảo sát để nhận 3 điểm rèn luyện',NULL,NULL,'2026-01-07 00:00:00','2026-01-09 23:59:59',NULL,50,1,3,1,'OPEN','ONLINE','https://forms.gle/test','UTE-PRO-2026',NULL,'SCHOOL'),(13,1,2,'Ngày hội Việc làm HCMUTE','Tham gia trực tiếp tại sảnh A','Sảnh A - HCMUTE','','2026-01-07 16:15:00','2026-01-08 18:00:00','2026-01-06 16:15:00',200,1,5,1,'OPEN','ATTENDANCE','',NULL,NULL,'SCHOOL'),(14,1,1,'Test rank ','Khảo sát test xếp hạng cho sinh viên id 3',NULL,NULL,'2026-01-07 00:00:00','2026-01-09 23:59:59',NULL,5,1,3,1,'OPEN','ONLINE','https://forms.gle/testrankid3','ID03_100',NULL,'SCHOOL'),(15,1,1,'Khảo sát Ý kiến Sinh viên về Cơ sở vật chất 2026','Tham gia đóng góp ý kiến để nâng cao chất lượng học tập. Nhận ngay 2 điểm rèn luyện.',NULL,NULL,'2026-01-07 07:00:00','2026-01-09 06:59:59',NULL,500,1,2,1,'OPEN','ONLINE','https://forms.gle/cs-vc-hcmute','SURVEY-15',NULL,'SCHOOL'),(16,1,2,'XUÂN TÌNH NGUYỆN','[+5 ĐIỂM CTXH] XUÂN YÊU THƯƠNG - TRAO QUÀ CHO CÁC HOÀN CẢNH KHÓ KHĂN','Hội trường lầu 6','https://lkt.uel.edu.vn/Resources/Images/SubDomain/lkt/TinTuc/12509282_661749550594315_7503753794027895775_n.jpg','2026-01-07 19:06:00','2026-01-09 20:29:00','2026-01-07 16:00:00',100,1,5,1,'OPEN','ATTENDANCE','','',NULL,'SCHOOL'),(17,1,3,'CHUYÊN ĐỀ DOANH NGHIỆP','[+3 ĐIỂM CDNN] CHUYÊN ĐỀ DOANH NGHIỆP: CV, PHỎNG VẤN & LỘ TRÌNH NGHỀ NGHIỆP','Hội trường A5-301','https://images.unsplash.com/photo-1521737604893-d14cc237f11d','2026-01-08 00:30:00','2026-01-08 09:30:00','2026-01-07 23:00:00',200,3,3,1,'OPEN','ATTENDANCE',NULL,NULL,NULL,'SCHOOL'),(18,1,3,'TEST TITLE','TEST DES','TEST LOCATION',NULL,'2026-01-09 07:00:00','2026-01-09 17:00:00','2026-01-09 00:30:00',100,3,3,1,'OPEN','ATTENDANCE',NULL,NULL,NULL,'SCHOOL'),(19,1,2,'TRỰC THƯ VIỆN','Trực thư viện cơ sở chính','Cơ sở 1',NULL,'2026-01-10 07:00:00','2026-01-10 11:00:00','2026-01-09 23:00:00',5,2,5,1,'OPEN','ATTENDANCE',NULL,NULL,NULL,'SCHOOL'),(20,1,1,'KHẢO SÁT NHU CẦU VỀ QUÊ BẰNG CHUYẾN XE ĐOÀN VIÊN','KHẢO SÁT NHU CẦU VỀ QUÊ BẰNG CHUYẾN XE ĐOÀN VIÊN tại trường ĐHSPKT','Cơ sở 1',NULL,'2026-02-08 07:00:00','2026-02-08 23:00:00','2026-01-31 23:00:00',500,1,1,1,'OPEN','ONLINE','https://chuyenxedoanvien.tetamlich.com',NULL,NULL,'SCHOOL');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2026-01-10 09:57:47',0,1),(2,'2','add indexes','SQL','V2__add_indexes.sql',-273359200,'root','2026-01-10 09:57:51',1928,1),(3,'3','add password reset tokens','SQL','V3__add_password_reset_tokens.sql',-1464260616,'root','2026-01-10 09:57:53',749,1),(4,'4','upgrade semesters and cumulative','SQL','V4__upgrade_semesters_and_cumulative.sql',-333202848,'root','2026-01-10 09:57:59',3378,1),(5,'5','add org units and rbac','SQL','V5__add_org_units_and_rbac.sql',993420411,'root','2026-01-10 09:58:05',3965,1),(6,'6','seed org units roles permissions','SQL','V6__seed_org_units_roles_permissions.sql',1791327681,'root','2026-01-10 09:58:11',3913,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `type` enum('SYSTEM','EVENT','REMINDER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SYSTEM',
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_notif_user` (`user_id`),
  KEY `idx_notif_user_created` (`user_id`,`created_at` DESC),
  KEY `idx_notif_type` (`type`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,2,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(2,3,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(3,4,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(4,5,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(5,5,'Bạn đã được cộng 3 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"SỰ KIỆN KHẢO SÁT TEST SỐ 11\" và được cộng 3 Điểm rèn luyện.','EVENT',0,'2026-01-06 05:12:55'),(6,3,'Bạn đã được cộng 3 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Hội thảo Chống gian lận Online\" và được cộng 3 Điểm rèn luyện.','EVENT',0,'2026-01-07 04:19:34'),(7,3,'Bạn đã được cộng 3 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Test rank \" và được cộng 3 Điểm rèn luyện.','EVENT',0,'2026-01-07 04:39:03'),(8,2,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:15'),(9,3,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:21'),(10,4,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:25'),(11,5,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:28'),(12,6,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:30'),(13,7,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:33'),(14,8,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:36'),(15,9,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:40'),(16,10,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:42'),(17,11,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 14:49:45'),(18,12,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 17:17:28'),(19,13,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 17:29:49'),(20,14,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 17:29:52'),(21,15,'Bạn đã được cộng 5 Điểm rèn luyện','Bạn đã hoàn thành sự kiện \"Ngày hội Việc làm HCMUTE\" và được cộng 5 Điểm rèn luyện.','EVENT',0,'2026-01-07 17:32:00');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `org_units`
--

DROP TABLE IF EXISTS `org_units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `org_units` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SCHOOL, FACULTY, OFFICE, UNION_SCHOOL, UNION_FACULTY, CLUB',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CNTT, CTXH, ERO, DOAN_TRUONG...',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tên đầy đủ',
  `parent_id` bigint DEFAULT NULL COMMENT 'Đơn vị cha (Đoàn khoa thuộc Khoa)',
  `description` text COLLATE utf8mb4_unicode_ci,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_org_type` (`type`),
  KEY `idx_org_code` (`code`),
  KEY `idx_org_parent` (`parent_id`),
  CONSTRAINT `fk_org_parent` FOREIGN KEY (`parent_id`) REFERENCES `org_units` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Đơn vị tổ chức (Khoa, Phòng, Đoàn, CLB)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `org_units`
--

LOCK TABLES `org_units` WRITE;
/*!40000 ALTER TABLE `org_units` DISABLE KEYS */;
INSERT INTO `org_units` VALUES (1,'SCHOOL','SCHOOL','Trường Đại học Sư phạm Kỹ thuật TP.HCM',NULL,'Cấp trường',1,'2026-01-10 09:58:07'),(10,'FACULTY','CNTT','Khoa Công nghệ Thông tin',1,'Faculty of Information Technology',1,'2026-01-10 09:58:07'),(11,'FACULTY','COKHI','Khoa Cơ khí',1,'Mechanical Engineering',1,'2026-01-10 09:58:07'),(12,'FACULTY','DIEN','Khoa Điện - Điện tử',1,'Electrical & Electronics Engineering',1,'2026-01-10 09:58:07'),(13,'FACULTY','XDDD','Khoa Xây dựng - Điện dân dụng',1,'Civil & Domestic Electrical Engineering',1,'2026-01-10 09:58:07'),(14,'FACULTY','NN','Khoa Ngoại ngữ',1,'Foreign Languages',1,'2026-01-10 09:58:07'),(15,'FACULTY','CN_MAY','Khoa Công nghệ May - Thời trang',1,'Garment Technology & Fashion',1,'2026-01-10 09:58:07'),(20,'OFFICE','CTSV','Phòng Công tác Sinh viên',1,'Student Affairs Office',1,'2026-01-10 09:58:07'),(21,'OFFICE','ERO','Phòng Quan hệ Doanh nghiệp',1,'Enterprise Relations Office',1,'2026-01-10 09:58:07'),(22,'OFFICE','DAOTAO','Phòng Đào tạo',1,'Training Office',1,'2026-01-10 09:58:07'),(23,'OFFICE','KHCN','Phòng Khoa học Công nghệ',1,'Science & Technology Office',1,'2026-01-10 09:58:07'),(30,'UNION_SCHOOL','DOAN_TRUONG','Đoàn Thanh niên Cộng sản Hồ Chí Minh',1,'Communist Youth Union - School Level',1,'2026-01-10 09:58:07'),(31,'UNION_SCHOOL','HOI_TRUONG','Hội Sinh viên',1,'Student Association - School Level',1,'2026-01-10 09:58:07'),(40,'UNION_FACULTY','DOAN_CNTT','Đoàn khoa CNTT',10,'Youth Union - Faculty of IT',1,'2026-01-10 09:58:07'),(41,'UNION_FACULTY','HOI_CNTT','Hội sinh viên khoa CNTT',10,'Student Association - Faculty of IT',1,'2026-01-10 09:58:07'),(42,'UNION_FACULTY','DOAN_COKHI','Đoàn khoa Cơ khí',11,'Youth Union - Mechanical Engineering',1,'2026-01-10 09:58:07'),(43,'UNION_FACULTY','DOAN_DIEN','Đoàn khoa Điện',12,'Youth Union - Electrical Engineering',1,'2026-01-10 09:58:07'),(50,'CLUB','CLB_TINH_NGUYEN','Câu lạc bộ Tình nguyện',1,'Volunteering Club',1,'2026-01-10 09:58:07'),(51,'CLUB','CLB_AN_SINH','Câu lạc bộ An sinh xã hội',1,'Social Welfare Club',1,'2026-01-10 09:58:07'),(52,'CLUB','CLB_AI','Câu lạc bộ Trí tuệ nhân tạo',10,'AI Club - Faculty of IT',1,'2026-01-10 09:58:07');
/*!40000 ALTER TABLE `org_units` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_codes`
--

DROP TABLE IF EXISTS `password_reset_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code_hash` varchar(64) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(100) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_reset_email_created` (`email`,`created_at` DESC),
  KEY `idx_reset_expires` (`expires_at`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_codes`
--

LOCK TABLES `password_reset_codes` WRITE;
/*!40000 ALTER TABLE `password_reset_codes` DISABLE KEYS */;
INSERT INTO `password_reset_codes` VALUES (1,'db74292dac9a0be963743b927212c88b6c3d0ccf807054ae3bee551ffd4800f5','2026-01-04 17:57:57.101614','23162102@student.hcmute.edu.vn','2026-01-04 18:07:57.101614','2026-01-04 17:59:24.835109'),(2,'5e54d877d43e7e8e7f3e0c84dd2f3c15e27d1adb71481d66c49d0e8790c01290','2026-01-04 18:15:01.307884','23162102@student.hcmute.edu.vn','2026-01-04 18:25:01.307884','2026-01-04 18:15:13.011773'),(3,'b0c9d207bb13331640ab959b705552e1415718d33e41a683fbda54282cf6180c','2026-01-05 16:56:54.307078','23162102@student.hcmute.edu.vn','2026-01-05 16:59:54.307078','2026-01-05 16:59:21.474492'),(4,'30f20c83f11ef9186d6b890c5ee90e462426acb7ce97fc0fa714eebf9c2be9b4','2026-01-07 03:37:58.958123','phttrongtin.nguyen@gmail.com','2026-01-07 03:39:58.958123','2026-01-07 03:39:02.128094'),(5,'53ba0b52105d4264401c34a6d8984e16d1b7db11e996efe32e56d7ae93986477','2026-01-07 21:50:30.002690','phttrongtin.nguyen@gmail.com','2026-01-07 21:52:30.002690','2026-01-07 21:51:51.910498'),(6,'3644269129154a71c257624e0f00e4a0e4b69ded0d7252140bc9ec815083331f','2026-01-07 21:52:56.313608','phttrongtin.nguyen@gmail.com','2026-01-07 21:54:56.313608','2026-01-07 21:54:13.374157'),(17,'c6c68078b2ec4c7940109f22548b1a4eaee75bcdddb9997b023dd0f08fe4b0ab','2026-01-07 22:22:42.595381','phttrongtin.nguyen@gmail.com','2026-01-07 22:24:42.595381','2026-01-07 22:23:35.783047'),(18,'160c288c3340d829705e2a40861cea78bb0167feb34b9ca930ace3d3d2ee01a1','2026-01-07 23:15:55.959130','phttrongtin.nguyen@gmail.com','2026-01-07 23:17:55.959130','2026-01-07 23:16:38.911680'),(19,'2b799ec1e3c5aa74dd8b36d34373d77e82d5455d0e36abf1c842564f97942b75','2026-01-07 23:20:59.583319','phttrongtin.nguyen@gmail.com','2026-01-07 23:22:59.583319','2026-01-07 23:22:12.995412'),(20,'c1f126b8d7c3d5e6eae2da6391d1ea55e51defa511661b0fddd7c48dbf052b28','2026-01-07 23:28:17.485798','phttrongtin.nguyen@gmail.com','2026-01-07 23:30:17.485798','2026-01-07 23:29:38.963088'),(21,'8d554ca4c73b73ff46da376014e56c87df73a232b23bf6fd5b9e92f47638cee0','2026-01-08 01:39:35.788920','phttrongtin.nguyen@gmail.com','2026-01-08 01:41:35.788920','2026-01-08 01:41:10.353657'),(32,'34db914a9fdad04b4119b1c5db95a12710063f6f8166a301d1d7705cad8808c9','2026-01-08 04:07:23.035488','phttrongtin.nguyen@gmail.com','2026-01-08 04:09:23.035488','2026-01-08 04:08:13.458469');
/*!40000 ALTER TABLE `password_reset_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `used_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `request_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_prt_user` (`user_id`),
  KEY `idx_prt_hash` (`token_hash`),
  KEY `idx_prt_expires` (`expires_at`),
  KEY `idx_prt_created` (`created_at`),
  CONSTRAINT `fk_prt_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'EVENT.CREATE, DRL.FINALIZE...',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'EVENT, DRL, POINT, SYSTEM',
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_perm_code` (`code`),
  KEY `idx_perm_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách quyền hệ thống';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'EVENT.READ','Xem sự kiện','EVENT','Xem danh sách & chi tiết sự kiện'),(2,'EVENT.CREATE','Tạo sự kiện','EVENT','Tạo sự kiện mới'),(3,'EVENT.UPDATE','Sửa sự kiện','EVENT','Chỉnh sửa thông tin sự kiện'),(4,'EVENT.DELETE','Xóa sự kiện','EVENT','Xóa sự kiện'),(5,'EVENT.CLOSE','Đóng sự kiện','EVENT','Đóng sự kiện'),(10,'REG.CREATE','Đăng ký sự kiện','REGISTRATION','Đăng ký tham gia sự kiện'),(11,'REG.CANCEL_SELF','Hủy đăng ký của mình','REGISTRATION','Hủy đăng ký'),(12,'REG.APPROVE','Duyệt đăng ký','REGISTRATION','Phê duyệt đăng ký sinh viên'),(13,'REG.REJECT','Từ chối đăng ký','REGISTRATION','Từ chối đăng ký'),(20,'CHECKIN.EXECUTE','Điểm danh vào','ATTENDANCE','Check-in sinh viên'),(21,'CHECKOUT.EXECUTE','Điểm danh ra','ATTENDANCE','Check-out sinh viên'),(30,'POINT.AWARD_AUTO','Cộng điểm tự động','POINT','Cộng điểm khi hoàn thành sự kiện'),(31,'POINT.AWARD_MANUAL','Cộng điểm thủ công','POINT','Cộng điểm thủ công cho sinh viên'),(32,'POINT.ADJUST','Điều chỉnh điểm','POINT','Sửa/trừ điểm'),(33,'POINT.VIEW_ALL','Xem điểm tất cả','POINT','Xem điểm mọi sinh viên'),(34,'POINT.VIEW_SELF','Xem điểm của mình','POINT','Sinh viên xem điểm cá nhân'),(40,'DRL.SUBMIT','Nộp DRL','DRL','Sinh viên nộp bảng DRL'),(41,'DRL.REVIEW_ADVISOR','Duyệt DRL (CVHT)','DRL','Cố vấn duyệt DRL'),(42,'DRL.REVIEW_FACULTY','Duyệt DRL (Khoa)','DRL','Khoa duyệt DRL'),(43,'DRL.FINALIZE','Chốt điểm cuối','DRL','CTSV chốt điểm học kỳ'),(50,'SYSTEM.SETTINGS','Cấu hình hệ thống','SYSTEM','Cài đặt hệ thống'),(51,'USER.MANAGE','Quản lý người dùng','SYSTEM','Tạo/sửa/xóa user'),(52,'AUDIT.VIEW','Xem log','SYSTEM','Xem audit trail'),(53,'REPORT.EXPORT','Xuất báo cáo','SYSTEM','Xuất Excel/PDF');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `point_transactions`
--

DROP TABLE IF EXISTS `point_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `point_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `semester_id` bigint NOT NULL,
  `point_type_id` bigint NOT NULL,
  `event_id` bigint DEFAULT NULL,
  `points` int NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `scope` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'SEMESTER' COMMENT 'SEMESTER or CUMULATIVE',
  `point_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'DRL, CTXH, CDNN',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_point_tx_student_semester_event` (`student_id`,`semester_id`,`event_id`),
  KEY `fk_pt_student` (`student_id`),
  KEY `fk_pt_semester` (`semester_id`),
  KEY `fk_pt_point_type` (`point_type_id`),
  KEY `fk_pt_event` (`event_id`),
  KEY `fk_pt_created_by` (`created_by`),
  KEY `idx_tx_student_type` (`student_id`,`point_type_id`),
  KEY `idx_tx_semester_type` (`semester_id`,`point_type_id`),
  KEY `idx_pt_scope` (`scope`),
  KEY `idx_pt_code` (`point_code`),
  KEY `idx_pt_student_semester` (`student_id`,`semester_id`),
  CONSTRAINT `fk_pt_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_pt_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pt_point_type` FOREIGN KEY (`point_type_id`) REFERENCES `point_types` (`id`),
  CONSTRAINT `fk_pt_semester` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`),
  CONSTRAINT `fk_pt_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_transactions`
--

LOCK TABLES `point_transactions` WRITE;
/*!40000 ALTER TABLE `point_transactions` DISABLE KEYS */;
INSERT INTO `point_transactions` VALUES (1,2,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1,'SEMESTER','DRL'),(2,3,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1,'SEMESTER','DRL'),(3,4,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1,'SEMESTER','DRL'),(4,5,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1,'SEMESTER','DRL'),(5,2,1,1,5,3,'Tham gia sự kiện: KHẢO SÁT NHU CẦU THỰC TẬP VÀ VIỆC LÀM CỦA SINH VIÊN TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM NĂM 2026','2025-12-26 14:45:12',1,'SEMESTER','DRL'),(6,2,1,1,4,3,'Tham gia sự kiện: ĐĂNG KÝ THAM GIA TỌA ĐÀM: VƯƠN MÌNH HỘI NHẬP - TỰ TIN CHINH PHỤC SV5T | Tuổi trẻ UTE','2025-12-26 15:33:23',1,'SEMESTER','DRL'),(7,2,1,1,7,3,'Tham gia sự kiện: KHẢO SÁT NHU CẦU THỰC TẬP VÀ VIỆC LÀM CỦA SINH VIÊN TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM NĂM 2026','2025-12-27 15:44:23',1,'SEMESTER','DRL'),(8,2,1,1,6,3,'Tham gia sự kiện: ĐĂNG KÝ THAM GIA BUỔI TRAINING-DAY | Cuộc thi Marketing - M-Rise','2025-12-27 16:44:41',1,'SEMESTER','DRL'),(9,2,1,1,11,3,'Tham gia sự kiện: SỰ KIỆN KHẢO SÁT TEST SỐ 11','2026-01-06 05:01:20',1,'SEMESTER','DRL'),(10,5,1,1,11,3,'Tham gia sự kiện: SỰ KIỆN KHẢO SÁT TEST SỐ 11','2026-01-06 05:12:55',NULL,'SEMESTER','DRL'),(11,3,1,1,12,3,'Tham gia sự kiện: Hội thảo Chống gian lận Online','2026-01-07 04:19:34',NULL,'SEMESTER','DRL'),(12,3,1,1,14,3,'Tham gia sự kiện: Test rank ','2026-01-07 04:39:03',NULL,'SEMESTER','DRL'),(13,2,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:15',1,'SEMESTER','DRL'),(14,3,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:21',1,'SEMESTER','DRL'),(15,4,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:25',1,'SEMESTER','DRL'),(16,5,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:28',1,'SEMESTER','DRL'),(17,6,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:30',1,'SEMESTER','DRL'),(18,7,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:33',1,'SEMESTER','DRL'),(19,8,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:36',1,'SEMESTER','DRL'),(20,9,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:40',1,'SEMESTER','DRL'),(21,10,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:42',1,'SEMESTER','DRL'),(22,11,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 14:49:45',1,'SEMESTER','DRL'),(23,12,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 17:17:28',1,'SEMESTER','DRL'),(24,13,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 17:29:49',1,'SEMESTER','DRL'),(25,14,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 17:29:52',1,'SEMESTER','DRL'),(26,15,1,1,13,5,'Tham gia sự kiện: Ngày hội Việc làm HCMUTE','2026-01-07 17:32:00',1,'SEMESTER','DRL');
/*!40000 ALTER TABLE `point_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `point_types`
--

DROP TABLE IF EXISTS `point_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `point_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_types`
--

LOCK TABLES `point_types` WRITE;
/*!40000 ALTER TABLE `point_types` DISABLE KEYS */;
INSERT INTO `point_types` VALUES (1,'DRL','Điểm rèn luyện','Điểm đánh giá ý thức và thái độ học tập'),(2,'CTXH','Điểm công tác xã hội','Điểm cho các hoạt động tình nguyện, CTXH'),(3,'CDDN','Điểm chuyên đề doanh nghiệp','Điểm tham gia talkshow, workshop doanh nghiệp');
/*!40000 ALTER TABLE `point_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_role_permission` (`role_id`,`permission_id`),
  KEY `idx_rp_role` (`role_id`),
  KEY `idx_rp_permission` (`permission_id`),
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Role có quyền gì';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES (7,1,1),(8,1,2),(9,1,3),(10,1,4),(11,1,5),(17,1,10),(18,1,11),(19,1,12),(20,1,13),(1,1,20),(2,1,21),(12,1,30),(13,1,31),(14,1,32),(15,1,33),(16,1,34),(3,1,40),(4,1,41),(5,1,42),(6,1,43),(21,1,50),(22,1,51),(23,1,52),(24,1,53),(32,2,1),(33,2,10),(34,2,11),(35,2,34),(36,2,40),(37,10,1),(38,10,2),(39,10,3),(40,10,4),(41,10,5),(42,10,12),(43,10,13),(44,10,20),(45,10,21),(46,11,1),(47,11,2),(48,11,3),(49,11,4),(50,11,5),(51,11,12),(52,11,13),(53,11,20),(54,11,21),(55,12,1),(56,12,2),(57,12,3),(58,12,5),(59,12,20),(60,12,21),(61,13,1),(62,13,2),(63,13,3),(64,13,5),(65,13,20),(66,13,21),(67,14,1),(68,14,2),(69,14,3),(70,14,5),(71,14,20),(72,14,21),(73,15,1),(74,15,2),(75,15,3),(76,15,5),(77,15,20),(78,15,21),(79,20,1),(80,20,12),(81,20,13),(82,20,30),(83,20,31),(84,20,32),(85,20,33),(86,20,41),(87,20,42),(88,20,43),(89,20,50),(90,20,51),(91,20,52),(92,20,53),(93,21,1),(94,21,33),(95,21,41),(96,22,1),(97,22,33),(98,22,42),(99,23,1),(100,23,20),(101,23,21),(102,24,1),(103,24,33),(104,24,52),(105,24,53);
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SUPER_ADMIN, FACULTY_EVENT_ADMIN...',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `is_system` tinyint(1) DEFAULT '0' COMMENT 'Role hệ thống không được xóa',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_role_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Vai trò trong hệ thống';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'SUPER_ADMIN','Quản trị viên hệ thống','Toàn quyền',1),(2,'STUDENT','Sinh viên','Người dùng thường',1),(10,'SCHOOL_EVENT_ADMIN','Ban tổ chức cấp trường','Tạo/quản lý sự kiện cấp trường',0),(11,'FACULTY_EVENT_ADMIN','Ban tổ chức cấp khoa','Tạo/quản lý sự kiện cấp khoa',0),(12,'YOUTH_UNION_SCHOOL','Đoàn - Hội trường','Tạo sự kiện phong trào cấp trường',0),(13,'YOUTH_UNION_FACULTY','Đoàn - Hội khoa','Tạo sự kiện phong trào cấp khoa',0),(14,'ERO_EVENT_ADMIN','ERO - Quan hệ doanh nghiệp','Tạo sự kiện hội thảo/CDNN',0),(15,'CLUB_EVENT_ADMIN','Quản lý Câu lạc bộ','Tạo sự kiện CLB',0),(20,'STUDENT_AFFAIRS_ADMIN','Phòng CTSV','Chốt điểm, cấu hình hệ thống',0),(21,'ADVISOR','Cố vấn học tập','Duyệt DRL cấp lớp',0),(22,'FACULTY_REVIEWER','Giáo vụ/CTSV khoa','Duyệt cấp khoa',0),(23,'CHECKIN_STAFF','Nhân viên điểm danh','Check-in/out',0),(24,'POINT_AUDITOR','Kiểm tra điểm','Xem log, báo cáo',0);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semesters`
--

DROP TABLE IF EXISTS `semesters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semesters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` int DEFAULT NULL,
  `academic_year` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '2025-2026' COMMENT 'Năm học: 2025-2026',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semesters`
--

LOCK TABLES `semesters` WRITE;
/*!40000 ALTER TABLE `semesters` DISABLE KEYS */;
INSERT INTO `semesters` VALUES (1,'2025-2026-HK1','Học kỳ 1 năm học 2025–2026','2025-08-11','2026-01-17',1,'2025-2026'),(2,'2025-2026-HK2','Học kỳ 2 năm học 2025–2026','2026-01-19','2026-07-11',1,'2025-2026'),(3,'2026-2027-HK1','Học kỳ 1 năm học 2026–2027','2026-08-11','2027-01-17',1,'2025-2026'),(4,'2026-2027-HK2','Học kỳ 2 năm học 2026–2027','2027-01-19','2027-07-11',1,'2025-2026');
/*!40000 ALTER TABLE `semesters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_points_cumulative`
--

DROP TABLE IF EXISTS `student_points_cumulative`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_points_cumulative` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `point_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CTXH, CDNN',
  `current_points` int NOT NULL DEFAULT '0' COMMENT 'Điểm hiện tại',
  `max_points` int NOT NULL COMMENT 'Điểm tối đa (CTXH=40, CDNN=8)',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_student_point` (`student_id`,`point_code`),
  KEY `idx_spc_student` (`student_id`),
  KEY `idx_spc_code` (`point_code`),
  CONSTRAINT `fk_spc_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Điểm tích lũy CTXH/CDNN (có trần)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_points_cumulative`
--

LOCK TABLES `student_points_cumulative` WRITE;
/*!40000 ALTER TABLE `student_points_cumulative` DISABLE KEYS */;
INSERT INTO `student_points_cumulative` VALUES (1,2,'CTXH',0,40,'2026-01-10 09:57:56'),(2,3,'CTXH',0,40,'2026-01-10 09:57:56'),(3,4,'CTXH',0,40,'2026-01-10 09:57:56'),(4,5,'CTXH',0,40,'2026-01-10 09:57:56'),(5,6,'CTXH',0,40,'2026-01-10 09:57:56'),(6,7,'CTXH',0,40,'2026-01-10 09:57:56'),(7,8,'CTXH',0,40,'2026-01-10 09:57:56'),(8,9,'CTXH',0,40,'2026-01-10 09:57:56'),(9,10,'CTXH',0,40,'2026-01-10 09:57:56'),(10,11,'CTXH',0,40,'2026-01-10 09:57:56'),(11,12,'CTXH',0,40,'2026-01-10 09:57:56'),(12,13,'CTXH',0,40,'2026-01-10 09:57:56'),(13,14,'CTXH',0,40,'2026-01-10 09:57:56'),(14,15,'CTXH',0,40,'2026-01-10 09:57:56'),(16,2,'CDNN',0,8,'2026-01-10 09:57:57'),(17,3,'CDNN',0,8,'2026-01-10 09:57:57'),(18,4,'CDNN',0,8,'2026-01-10 09:57:57'),(19,5,'CDNN',0,8,'2026-01-10 09:57:57'),(20,6,'CDNN',0,8,'2026-01-10 09:57:57'),(21,7,'CDNN',0,8,'2026-01-10 09:57:57'),(22,8,'CDNN',0,8,'2026-01-10 09:57:57'),(23,9,'CDNN',0,8,'2026-01-10 09:57:57'),(24,10,'CDNN',0,8,'2026-01-10 09:57:57'),(25,11,'CDNN',0,8,'2026-01-10 09:57:57'),(26,12,'CDNN',0,8,'2026-01-10 09:57:57'),(27,13,'CDNN',0,8,'2026-01-10 09:57:57'),(28,14,'CDNN',0,8,'2026-01-10 09:57:57'),(29,15,'CDNN',0,8,'2026-01-10 09:57:57');
/*!40000 ALTER TABLE `student_points_cumulative` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_semester_summary`
--

DROP TABLE IF EXISTS `student_semester_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_semester_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `semester_id` bigint NOT NULL,
  `total_drl` int NOT NULL DEFAULT '0',
  `total_ctxh` int NOT NULL DEFAULT '0',
  `total_cdnn` int NOT NULL DEFAULT '0',
  `rank_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_sss` (`student_id`,`semester_id`),
  UNIQUE KEY `uk_student_semester` (`student_id`,`semester_id`),
  KEY `fk_sss_semester` (`semester_id`),
  CONSTRAINT `fk_sss_semester` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`),
  CONSTRAINT `fk_sss_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_semester_summary`
--

LOCK TABLES `student_semester_summary` WRITE;
/*!40000 ALTER TABLE `student_semester_summary` DISABLE KEYS */;
INSERT INTO `student_semester_summary` VALUES (1,2,1,23,0,0,'Kém','2026-01-07 14:49:15'),(2,5,1,8,0,0,'Kém','2026-01-07 14:49:28'),(3,3,1,100,40,8,'Xuất sắc','2026-01-07 14:49:21'),(4,4,1,5,0,0,'Kém','2026-01-07 14:49:25'),(5,6,1,5,0,0,'Kém','2026-01-07 14:49:30'),(6,7,1,5,0,0,'Kém','2026-01-07 14:49:33'),(7,8,1,5,0,0,'Kém','2026-01-07 14:49:36'),(8,9,1,5,0,0,'Kém','2026-01-07 14:49:40'),(9,10,1,5,0,0,'Kém','2026-01-07 14:49:42'),(10,11,1,5,0,0,'Kém','2026-01-07 14:49:45'),(11,12,1,5,0,0,'Kém','2026-01-07 17:17:28'),(12,13,1,5,0,0,'Kém','2026-01-07 17:29:49'),(13,14,1,5,0,0,'Kém','2026-01-07 17:29:52'),(14,15,1,5,0,0,'Kém','2026-01-07 17:32:00');
/*!40000 ALTER TABLE `student_semester_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_org_units`
--

DROP TABLE IF EXISTS `user_org_units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_org_units` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `org_unit_id` bigint NOT NULL,
  `position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Trưởng ban, Cộng tác viên, Thành viên...',
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_org` (`user_id`,`org_unit_id`),
  KEY `idx_uou_user` (`user_id`),
  KEY `idx_uou_org` (`org_unit_id`),
  CONSTRAINT `fk_uou_org` FOREIGN KEY (`org_unit_id`) REFERENCES `org_units` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_uou_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Người dùng thuộc đơn vị nào';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_org_units`
--

LOCK TABLES `user_org_units` WRITE;
/*!40000 ALTER TABLE `user_org_units` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_org_units` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles_scoped`
--

DROP TABLE IF EXISTS `user_roles_scoped`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles_scoped` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `scope_org_unit_id` bigint DEFAULT NULL COMMENT 'NULL = toàn trường, có ID = phạm vi đơn vị',
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `assigned_by` bigint DEFAULT NULL COMMENT 'Admin nào gán',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_role_scope` (`user_id`,`role_id`,`scope_org_unit_id`),
  KEY `idx_urs_user` (`user_id`),
  KEY `idx_urs_role` (`role_id`),
  KEY `idx_urs_scope` (`scope_org_unit_id`),
  CONSTRAINT `fk_urs_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_urs_scope` FOREIGN KEY (`scope_org_unit_id`) REFERENCES `org_units` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_urs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User có role gì ở phạm vi nào';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles_scoped`
--

LOCK TABLES `user_roles_scoped` WRITE;
/*!40000 ALTER TABLE `user_roles_scoped` DISABLE KEYS */;
INSERT INTO `user_roles_scoped` VALUES (1,2,2,NULL,'2026-01-10 09:58:10',NULL),(2,3,2,NULL,'2026-01-10 09:58:10',NULL),(3,4,2,NULL,'2026-01-10 09:58:10',NULL),(4,5,2,NULL,'2026-01-10 09:58:10',NULL),(5,6,2,NULL,'2026-01-10 09:58:10',NULL),(6,7,2,NULL,'2026-01-10 09:58:10',NULL),(7,8,2,NULL,'2026-01-10 09:58:10',NULL),(8,9,2,NULL,'2026-01-10 09:58:10',NULL),(9,10,2,NULL,'2026-01-10 09:58:10',NULL),(10,11,2,NULL,'2026-01-10 09:58:10',NULL),(11,12,2,NULL,'2026-01-10 09:58:10',NULL),(12,13,2,NULL,'2026-01-10 09:58:10',NULL),(13,14,2,NULL,'2026-01-10 09:58:10',NULL),(14,15,2,NULL,'2026-01-10 09:58:10',NULL),(16,1,1,NULL,'2026-01-10 09:58:10',NULL);
/*!40000 ALTER TABLE `user_roles_scoped` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `faculty` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `faculty_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mã khoa (CNTT, COKHI, DIEN...)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_user_faculty` (`faculty_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'','Nguyễn Quản Trị','phttrongtin.nguyen@gmail.com','0349707863','https://i.pravatar.cc/150?u=admin','$2a$10$rs7bcsLvwg4QP5TdXRBn2ea.a1lXkpzJv0kkgY9R/5MsaapNNTpkO','ADMIN',NULL,'Phòng CTSV',1,'2025-12-06 23:33:43','2026-01-07 20:36:08',NULL),(2,'23162102','Nguyễn Trọng Tín','23162102@student.hcmute.edu.vn','0909348665','https://i.pravatar.cc/150?u=student','$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-06 23:46:56','2026-01-07 09:58:37',NULL),(3,'23162005','Trịnh Trâm Anh','23162005@student.hcmute.edu.vn','0767348970',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-07 00:01:48','2026-01-06 09:42:21',NULL),(4,'23162099','Hoàng Văn Vương Thu','23162099@student.hcmute.edu.vn','0348222276',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-07 00:01:48','2026-01-06 09:42:21',NULL),(5,'24162086','Trần Ngọc Nhất','24162086@student.hcmute.edu.vn','0387128288',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','24162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-07 00:01:48','2026-01-06 09:42:21',NULL),(6,'23124194','Vũ Quang Kỳ','23124194@student.hcmute.edu.vn','0834567890',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23124A','KINH TẾ',1,'2025-12-29 15:00:43','2026-01-06 09:42:21',NULL),(7,'23142247','Lê Gia Bảo','23142247@student.hcmute.edu.vn','0845678901',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23142C','ĐIỆN - ĐIỆN TỬ',1,'2025-12-29 15:16:50','2026-01-07 07:46:20',NULL),(8,'23143001','Nguyễn Văn An','23143001@student.hcmute.edu.vn','0903124587',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23143A','CƠ KHÍ CHẾ TẠO MÁY',1,'2025-12-29 15:17:31','2026-01-07 07:46:20',NULL),(9,'23145008','Trần Thị Bích','23145008@student.hcmute.edu.vn','0912345678',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23145B','CƠ KHÍ ĐỘNG LỰC',1,'2025-12-29 15:17:31','2026-01-07 07:46:20',NULL),(10,'23128012','Lê Quốc Cường','23128012@student.hcmute.edu.vn','0923456789',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23128C','CÔNG NGHỆ HÓA HỌC VÀ THỰC PHẨM',1,'2025-12-29 15:17:31','2026-01-07 07:46:20',NULL),(11,'23158021','Phạm Minh Đức','23158021@student.hcmute.edu.vn','0934567890',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23142D','IN VÀ TRUYỀN THÔNG',1,'2025-12-29 15:17:31','2026-01-07 07:46:21',NULL),(12,'23130036','Võ Thị Hạnh','23130036@student.hcmute.edu.vn','0945678901',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23130A','KHOA HỌC ỨNG DỤNG',1,'2025-12-29 15:17:31','2026-01-07 07:46:21',NULL),(13,'23131052','Nguyễn Hoàng Long','23131052@student.hcmute.edu.vn','0961234567',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23131B','NGOẠI NGỮ',1,'2025-12-29 15:17:31','2026-01-07 07:46:21',NULL),(14,'23123056','Trần Nhật Minh','23123056@student.hcmute.edu.vn','0972345678',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23123C','THỜI TRANG VÀ DU LỊCH',1,'2025-12-29 15:17:31','2026-01-07 07:46:21',NULL),(15,'23160078','Đặng Thùy Trang','23160078@student.hcmute.edu.vn','0981234567',NULL,'$2a$10$ut/XnXjLxhThOqRZlTg5se81yzkHOfKlW4jw2NMsG/eNjlexr3EEa','STUDENT','23160D','XÂY DỰNG',1,'2025-12-29 15:17:31','2026-01-07 07:46:21',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-10 17:14:31
