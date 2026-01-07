-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: training_points_db
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `event_categories`
--

DROP TABLE IF EXISTS `event_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
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
  `status` enum('REGISTERED','APPROVED','CHECKED_IN','COMPLETED','ABSENT','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'REGISTERED',
  `checkin_time` datetime DEFAULT NULL,
  `checkout_time` datetime DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_event_student` (`event_id`,`student_id`),
  KEY `fk_reg_student` (`student_id`),
  CONSTRAINT `fk_reg_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  CONSTRAINT `fk_reg_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_registrations`
--

LOCK TABLES `event_registrations` WRITE;
/*!40000 ALTER TABLE `event_registrations` DISABLE KEYS */;
INSERT INTO `event_registrations` VALUES (1,1,2,'2025-12-07 00:26:25','COMPLETED','2025-12-03 07:50:00','2025-12-03 11:05:00','Tham dự đầy đủ buổi đối thoại.'),(2,1,3,'2025-12-07 00:26:25','COMPLETED','2025-12-03 07:55:00','2025-12-03 11:00:00','Tham dự đầy đủ buổi đối thoại.'),(3,1,4,'2025-12-07 00:26:25','COMPLETED','2025-12-03 08:00:00','2025-12-03 11:00:00','Tham dự đầy đủ buổi đối thoại.'),(4,1,5,'2025-12-07 00:26:25','COMPLETED','2025-12-03 08:02:00','2025-12-03 11:00:00','Tham dự đầy đủ buổi đối thoại.');
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
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `banner_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `registration_deadline` datetime DEFAULT NULL,
  `max_participants` int DEFAULT NULL,
  `point_type_id` bigint NOT NULL,
  `point_value` int NOT NULL DEFAULT '0',
  `created_by` bigint DEFAULT NULL,
  `status` enum('DRAFT','OPEN','CLOSED','DONE','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT',
  PRIMARY KEY (`id`),
  KEY `fk_events_semester` (`semester_id`),
  KEY `fk_events_category` (`category_id`),
  KEY `fk_events_point_type` (`point_type_id`),
  KEY `fk_events_created_by` (`created_by`),
  CONSTRAINT `fk_events_category` FOREIGN KEY (`category_id`) REFERENCES `event_categories` (`id`),
  CONSTRAINT `fk_events_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_events_point_type` FOREIGN KEY (`point_type_id`) REFERENCES `point_types` (`id`),
  CONSTRAINT `fk_events_semester` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,1,1,'Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026','[+3 ĐIỂM RÈN LUYỆN] ĐĂNG KÝ THAM DỰ GẶP GỠ ĐỐI THOẠI GIỮA LÃNH ĐẠO NHÀ TRƯỜNG VỚI SINH VIÊN HỌC KỲ I NĂM HỌC 2025 - 2026 | Phòng Tuyển sinh và Công tác Sinh viên SPKT','Hội trường lớn khu A',NULL,'2025-12-03 08:00:00','2025-12-03 11:00:00','2025-12-02 23:59:59',50,1,3,1,'CLOSED'),(2,1,2,'Phân phát ba lô trường','[+5 ĐIỂM CTXH] ĐĂNG KÝ PHÂN PHÁT BA LÔ TRƯỜNG | Phòng Quản trị Cơ sở Vật chất - ĐH Sư phạm Kỹ thuật TP. HCM [30 FCFS]','Sảnh tòa nhà trung tâm',NULL,'2025-11-14 13:00:00','2025-11-14 17:00:00','2025-11-13 23:59:59',30,2,5,1,'CLOSED'),(3,2,3,'ĐĂNG KÝ HỘI THẢO CHUYÊN ĐỀ - NGÀNH BIÊN PHIÊN DỊCH','[+1 ĐIỂM CDDN] ĐĂNG KÝ HỘI THẢO CHUYÊN ĐỀ - NGÀNH BIÊN PHIÊN DỊCH - HK2/2024-2025 | Khoa Ngoại Ngữ','Phòng họp 2 tầng 6 toà nhà trung tâm',NULL,'2025-04-18 11:00:00','2025-04-24 21:00:00','2025-04-20 23:59:59',100,3,1,1,'CLOSED');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
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
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `type` enum('SYSTEM','EVENT','REMINDER') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SYSTEM',
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_notif_user` (`user_id`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,2,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(2,3,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(3,4,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06'),(4,5,'Bạn đã được cộng 3 điểm ĐRL','Bạn đã hoàn thành buổi \"Gặp gỡ đối thoại giữa lãnh đạo nhà trường và sinh viên HKI 2025–2026\" và được cộng 3 điểm rèn luyện.','EVENT',0,'2025-12-07 00:27:06');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
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
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pt_student` (`student_id`),
  KEY `fk_pt_semester` (`semester_id`),
  KEY `fk_pt_point_type` (`point_type_id`),
  KEY `fk_pt_event` (`event_id`),
  KEY `fk_pt_created_by` (`created_by`),
  CONSTRAINT `fk_pt_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_pt_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  CONSTRAINT `fk_pt_point_type` FOREIGN KEY (`point_type_id`) REFERENCES `point_types` (`id`),
  CONSTRAINT `fk_pt_semester` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`),
  CONSTRAINT `fk_pt_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_transactions`
--

LOCK TABLES `point_transactions` WRITE;
/*!40000 ALTER TABLE `point_transactions` DISABLE KEYS */;
INSERT INTO `point_transactions` VALUES (1,2,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1),(2,3,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1),(3,4,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1),(4,5,1,1,1,3,'Hoàn thành buổi đối thoại giữa lãnh đạo và sinh viên HKI 2025–2026','2025-12-07 00:28:03',1);
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
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_types`
--

LOCK TABLES `point_types` WRITE;
/*!40000 ALTER TABLE `point_types` DISABLE KEYS */;
INSERT INTO `point_types` VALUES (1,'DRL','Điểm rèn luyện','Điểm đánh giá ý thức và thái độ học tập'),(2,'CTXH','Điểm công tác xã hội','Điểm cho các hoạt động tình nguyện, CTXH'),(3,'DN','Điểm chuyên đề doanh nghiệp','Điểm tham gia talkshow, workshop doanh nghiệp');
/*!40000 ALTER TABLE `point_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semesters`
--

DROP TABLE IF EXISTS `semesters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semesters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semesters`
--

LOCK TABLES `semesters` WRITE;
/*!40000 ALTER TABLE `semesters` DISABLE KEYS */;
INSERT INTO `semesters` VALUES (1,'2025-2026-HK1','Học kỳ 1 năm học 2025–2026','2025-08-11','2026-01-17',1),(2,'2025-2026-HK2','Học kỳ 2 năm học 2025–2026','2026-01-19','2026-07-11',1);
/*!40000 ALTER TABLE `semesters` ENABLE KEYS */;
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
  `total_dn` int NOT NULL DEFAULT '0',
  `rank_label` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_sss` (`student_id`,`semester_id`),
  KEY `fk_sss_semester` (`semester_id`),
  CONSTRAINT `fk_sss_semester` FOREIGN KEY (`semester_id`) REFERENCES `semesters` (`id`),
  CONSTRAINT `fk_sss_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_semester_summary`
--

LOCK TABLES `student_semester_summary` WRITE;
/*!40000 ALTER TABLE `student_semester_summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_semester_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('STUDENT','ADMIN','STAFF') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT',
  `class_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `faculty` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'Nguyễn Văn Quản Trị','admin@ute.edu.vn','0900000000','admin123','ADMIN',NULL,'Phòng CTSV',1,'2025-12-06 23:33:43','2025-12-06 23:33:43'),(2,'23162102','Nguyễn Trọng Tín','23162102@student.hcmute.edu.vn','0349707863','Tin@1867','STUDENT','23162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-06 23:46:56','2025-12-07 00:00:31'),(3,'23162005','Trịnh Trâm Anh','23162005@student.hcmute.edu.vn','0767348970','student123','STUDENT','23162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-07 00:01:48','2025-12-07 00:05:28'),(4,'23162099','Hoàng Văn Vương Thu','23162099@student.hcmute.edu.vn','0348222276','student123','STUDENT','23162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-07 00:01:48','2025-12-07 00:05:28'),(5,'24162086','Trần Ngọc Nhất','24162086@student.hcmute.edu.vn',NULL,'student123','STUDENT','24162B','CÔNG NGHỆ THÔNG TIN',1,'2025-12-07 00:01:48','2025-12-07 00:05:28');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-07  1:06:33
