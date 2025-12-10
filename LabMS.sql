-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: labms
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_id` varchar(255) DEFAULT NULL COMMENT '管理员ID',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `gender` varchar(255) DEFAULT NULL COMMENT '性别',
  `department` varchar(255) DEFAULT NULL COMMENT '部门',
  `position` varchar(255) DEFAULT NULL COMMENT '职位',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `hire_date` varchar(255) DEFAULT NULL COMMENT '入职时间',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `created_at` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updated_at` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `role` varchar(255) DEFAULT NULL COMMENT '身份',
  `role_id` varchar(255) DEFAULT NULL COMMENT '身份ID',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `admin_id` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1,'admin','管','男','WER','RQWER','13177777777','1522311390@qq.com',NULL,'在职','$2a$10$JnI6wHzTGk9eNJrLjycI1.M09bQSz1ZFHkkE3DdrlNSeG.qPHpNiq','2025-11-10 01:56:33','2025-11-10 02:08:20','admin','admin','default_avatar.png');
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `class_id` varchar(255) DEFAULT NULL COMMENT '班级ID',
  `name` varchar(255) DEFAULT NULL COMMENT '班级名',
  `student_count` int DEFAULT '0' COMMENT '人数',
  `created_at` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updated_at` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `college` varchar(255) DEFAULT NULL COMMENT '所属学院',
  PRIMARY KEY (`id`),
  UNIQUE KEY `class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES (1,'23102','软工2班',35,'2025-11-20 08:47:53','2025-11-20 08:47:53','计算机科学与工程学院');
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_schedules`
--

DROP TABLE IF EXISTS `course_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_schedules` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` varchar(255) DEFAULT NULL COMMENT '课程ID',
  `class_id` varchar(255) DEFAULT NULL COMMENT '班级ID',
  `semester_id` varchar(255) DEFAULT NULL COMMENT '学期ID',
  `weekday` varchar(255) DEFAULT NULL COMMENT '星期',
  `start_section` int NOT NULL COMMENT '开始节次',
  `duration` int NOT NULL COMMENT '持续节次',
  `weeks` varchar(255) DEFAULT NULL COMMENT '上课周次',
  `classroom` varchar(255) DEFAULT NULL COMMENT '授课教室',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `created_at` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updated_at` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `teacher_id` varchar(255) NOT NULL COMMENT '老师ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='选课表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_schedules`
--

LOCK TABLES `course_schedules` WRITE;
/*!40000 ALTER TABLE `course_schedules` DISABLE KEYS */;
INSERT INTO `course_schedules` VALUES (1,'0500201026','23102','2025202601','1',7,2,'1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19','4502','正常','2025-11-27 22:56:36','2025-11-27 22:56:36','teacher'),(2,'0500201026','23102','2025202601','4',3,2,'1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19','4502','正常',NULL,NULL,'teacher');
/*!40000 ALTER TABLE `course_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` varchar(255) DEFAULT NULL COMMENT '课程ID',
  `name` varchar(255) DEFAULT NULL COMMENT '课程名称',
  `type` varchar(255) DEFAULT NULL COMMENT '备注',
  `college` varchar(255) DEFAULT NULL COMMENT '开设学院',
  `credits` decimal(3,1) DEFAULT NULL,
  `total_hours` int NOT NULL DEFAULT '0' COMMENT '总学时',
  `term` varchar(255) DEFAULT NULL COMMENT '学期',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `teacher_id` varchar(255) NOT NULL COMMENT '授课老师ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `course_id` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'0500201026','JavaWeb开发基础','必修','计算机科学与工程学院',3.0,32,'2025202601','正常','teacher','2025-11-27 14:52:35','2025-12-10 15:00:32');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `permission_string` varchar(100) NOT NULL COMMENT '权限字符串',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `group_name` varchar(50) DEFAULT NULL COMMENT '分组',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `http_method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法',
  `api_path` varchar(200) DEFAULT NULL COMMENT 'API路径',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删除：0-未删除，1-已删除',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_string` (`permission_string`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reservation_id` varchar(30) NOT NULL COMMENT '预约代码',
  `room_id` varchar(20) NOT NULL COMMENT '预约机房ID',
  `seat_id` varchar(20) NOT NULL COMMENT '座位ID',
  `user_id` varchar(255) NOT NULL COMMENT '预约人员ID',
  `reservation_date` date NOT NULL COMMENT '预约日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` enum('待确认','已确认','已完成','已取消','已过期','已驳回') DEFAULT '待确认' COMMENT '状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约情况表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` varchar(50) NOT NULL COMMENT '名称',
  `full_name` varchar(100) NOT NULL COMMENT '全称',
  `modules` varchar(255) DEFAULT NULL COMMENT '拥有读写权限的模块',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删除：0-未删除，1-已删除',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='身份表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_id` varchar(20) NOT NULL COMMENT '机房号',
  `name` varchar(100) NOT NULL COMMENT '机房名称',
  `equipment_info` text COMMENT '机房信息',
  `total_seats` int NOT NULL DEFAULT '0' COMMENT '座位数',
  `available_seats` int NOT NULL DEFAULT '0' COMMENT '可预约座位数',
  `allowed_roles` text COMMENT '可预约用户身份',
  `manager_id` varchar(255) DEFAULT NULL COMMENT '负责人ID',
  `status` enum('正常','维护','停用') DEFAULT '正常' COMMENT '状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `nice_seats` int DEFAULT '0' COMMENT '可用座位数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `room_id` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机房情况表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,'4502','软件开发实验室','中',10,10,'all','teacher','正常','2025-11-27 14:58:39','2025-12-10 15:13:41',10);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seats`
--

DROP TABLE IF EXISTS `seats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seats` (
  `id` int NOT NULL AUTO_INCREMENT,
  `seat_id` varchar(20) NOT NULL COMMENT '机位号',
  `room_id` varchar(20) NOT NULL COMMENT '所属机房ID',
  `status` enum('空闲','占用','故障') DEFAULT '空闲' COMMENT '状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_seat_lab` (`seat_id`,`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='座位情况表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seats`
--

LOCK TABLES `seats` WRITE;
/*!40000 ALTER TABLE `seats` DISABLE KEYS */;
INSERT INTO `seats` VALUES (61,'01','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(62,'02','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(63,'03','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(64,'04','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(65,'05','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(66,'06','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(67,'07','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(68,'08','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(69,'09','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41'),(70,'10','4502','空闲','2025-12-10 07:13:41','2025-12-10 07:13:41');
/*!40000 ALTER TABLE `seats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `teacher_id` varchar(255) DEFAULT NULL COMMENT '教师ID',
  `name` varchar(255) DEFAULT NULL COMMENT '教师姓名',
  `gender` varchar(255) DEFAULT NULL COMMENT '性别',
  `department` varchar(255) DEFAULT NULL COMMENT '部门',
  `title` varchar(255) DEFAULT NULL COMMENT '职称',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `degree` varchar(255) DEFAULT NULL COMMENT '学历',
  `hire_date` varchar(255) DEFAULT NULL COMMENT '入校时间',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `created_at` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updated_at` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `role` varchar(255) DEFAULT NULL COMMENT '身份',
  `role_id` varchar(255) DEFAULT NULL COMMENT '身份ID',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'teacher','老师','女','88','99','13122222222','1522311390@qq.com',NULL,'2025-12-04','在职','2025-11-10 01:46:46','2025-11-10 01:46:46','$2a$10$o8Zy8g0gse4na1QVkV2RNeJM29mZryqDcBvMzshCGZCp83jedUMja','teacher','teacher','teacher.jpg');
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terms`
--

DROP TABLE IF EXISTS `terms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `semester_id` varchar(255) DEFAULT NULL COMMENT '学期ID',
  `name` varchar(255) DEFAULT NULL COMMENT '学期名',
  `start_date` date NOT NULL COMMENT '开始时间',
  `end_date` date NOT NULL COMMENT '结束时间',
  `created_at` date DEFAULT NULL COMMENT '创建时间',
  `updated_at` date DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `semester_id` (`semester_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学期表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terms`
--

LOCK TABLES `terms` WRITE;
/*!40000 ALTER TABLE `terms` DISABLE KEYS */;
INSERT INTO `terms` VALUES (1,'2025202601','2025-2026学年第一学期','2025-08-25','2025-12-24','2025-11-27','2025-11-27'),(2,'2025202602','2025-2026学年第二学期','2026-03-18','2026-07-21','2025-11-28','2025-11-28');
/*!40000 ALTER TABLE `terms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户ID',
  `name` varchar(255) DEFAULT NULL COMMENT '用户姓名',
  `gender` varchar(255) DEFAULT NULL COMMENT '性别',
  `class_id` varchar(255) DEFAULT NULL COMMENT '班级ID',
  `major` varchar(255) DEFAULT NULL COMMENT '专业',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `role_id` varchar(255) DEFAULT NULL COMMENT '身份ID',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `created_at` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updated_at` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `role` varchar(255) DEFAULT NULL COMMENT '身份',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'user','李书毅','女','23102','软件工程','18711111111','1522311390@qq.com','student','$2a$10$WW2MWL1zMwwNuTGV8KOvOuWPsYPEWU7Z2W4x6cwk/2BsD2k/11Qnq','2025-12-09T13:20:27.497846300','2025-12-09T13:20:27.497846300','student','user.jpg');
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

-- Dump completed on 2025-12-10 23:25:35
