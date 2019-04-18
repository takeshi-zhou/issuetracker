-- MySQL dump 10.13  Distrib 5.7.20, for Linux (x86_64)
--
-- Host: localhost    Database: issueTracker
-- ------------------------------------------------------
-- Server version	5.7.20-log

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `uuid` varchar(36) NOT NULL,
  `accountName` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL,
  `email` varchar(64) NOT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `accountName` (`accountName`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bug_recommendation`
--

DROP TABLE IF EXISTS `bug_recommendation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bug_recommendation` (
  `uuid` varchar(36) NOT NULL,
  `type` varchar(128) NOT NULL,
  `location` varchar(512) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `start_line` varchar(128) DEFAULT NULL,
  `end_line` varchar(128) DEFAULT NULL,
  `prev_code` text,
  `curr_code` text,
  `next_commitid` varchar(256) DEFAULT NULL,
  `curr_commitid` varchar(256) DEFAULT NULL,
  `bug_lines` varchar(256) DEFAULT NULL,
  `nextstart_line` varchar(128) DEFAULT NULL,
  `nextend_line` varchar(128) DEFAULT NULL,
  `code` text,
  `repoid` varchar(256) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clone_info`
--

DROP TABLE IF EXISTS `clone_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clone_info` (
  `id` int(11) NOT NULL,
  `added_clone_num` int(11) DEFAULT NULL,
  `clone_group_num` int(11) DEFAULT NULL,
  `clone_line` int(11) DEFAULT NULL,
  `clone_num` int(11) DEFAULT NULL,
  `clone_ratio` double NOT NULL,
  `commitid` varchar(255) DEFAULT NULL,
  `commit_time` varchar(255) DEFAULT NULL,
  `deleted_clone_num` int(11) DEFAULT NULL,
  `local_addr` varchar(255) DEFAULT NULL,
  `projectid` varchar(255) DEFAULT NULL,
  `total_code_line` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commit`
--

DROP TABLE IF EXISTS `commit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit` (
  `uuid` varchar(36) NOT NULL,
  `commit_id` varchar(64) NOT NULL,
  `message` mediumtext,
  `developer` varchar(64) DEFAULT NULL,
  `commit_time` datetime DEFAULT NULL,
  `repo_id` varchar(36) DEFAULT NULL,
  `developer_email` varchar(255) DEFAULT NULL,
  `self_index` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `id` varchar(36) NOT NULL,
  `category` varchar(45) DEFAULT NULL,
  `event_type` varchar(45) DEFAULT NULL,
  `target_type` varchar(45) DEFAULT NULL,
  `target_id` varchar(36) DEFAULT NULL,
  `target_display_id` varchar(36) DEFAULT NULL,
  `target_committer` varchar(45) DEFAULT NULL,
  `repo_id` varchar(36) DEFAULT NULL,
  `commit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ignore_record`
--

DROP TABLE IF EXISTS `ignore_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ignore_record` (
  `uuid` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `level` int(11) NOT NULL,
  `type` varchar(128) NOT NULL,
  `repo_id` varchar(36) DEFAULT NULL,
  `repo_name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue`
--

DROP TABLE IF EXISTS `issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue` (
  `uuid` varchar(36) NOT NULL,
  `type` varchar(128) NOT NULL,
  `category` varchar(45) DEFAULT NULL,
  `start_commit` varchar(64) DEFAULT NULL,
  `start_commit_date` datetime DEFAULT NULL,
  `end_commit` varchar(64) DEFAULT NULL,
  `end_commit_date` datetime DEFAULT NULL,
  `raw_issue_start` varchar(36) DEFAULT NULL,
  `raw_issue_end` varchar(36) DEFAULT NULL,
  `repo_id` varchar(36) DEFAULT NULL,
  `target_files` varchar(512) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `priority` tinyint(4) DEFAULT NULL,
  `display_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issueType`
--

DROP TABLE IF EXISTS `issueType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issueType` (
  `uuid` varchar(36) NOT NULL,
  `type` varchar(128) DEFAULT NULL,
  `tool` varchar(36) DEFAULT NULL,
  `category` varchar(128) DEFAULT NULL,
  `description` mediumtext,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `uuid` varchar(36) NOT NULL,
  `start_line` mediumint(9) DEFAULT NULL,
  `end_line` mediumint(9) DEFAULT NULL,
  `bug_lines` varchar(256) DEFAULT NULL,
  `start_token` mediumint(9) DEFAULT NULL,
  `end_token` mediumint(9) DEFAULT NULL,
  `file_path` varchar(512) NOT NULL,
  `class_name` varchar(256) DEFAULT NULL,
  `method_name` varchar(256) DEFAULT NULL,
  `rawIssue_id` varchar(36) NOT NULL,
  `code` text,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `uuid` varchar(36) NOT NULL,
  `name` varchar(64) NOT NULL,
  `url` varchar(256) NOT NULL,
  `language` varchar(36) DEFAULT NULL,
  `vcs_type` varchar(64) DEFAULT NULL,
  `type` varchar(36) NOT NULL,
  `account_id` varchar(36) DEFAULT NULL,
  `download_status` varchar(64) DEFAULT NULL,
  `scan_status` varchar(64) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `till_commit_time` datetime DEFAULT NULL,
  `last_scan_time` datetime DEFAULT NULL,
  `description` text,
  `repo_id` varchar(36) DEFAULT NULL,
  `branch` varchar(64) NOT NULL,
  `first_auto_scan` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uuid`) USING BTREE,
  UNIQUE KEY `uuid` (`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rawIssue`
--

DROP TABLE IF EXISTS `rawIssue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rawIssue` (
  `uuid` varchar(36) NOT NULL,
  `type` varchar(128) NOT NULL,
  `category` varchar(45) DEFAULT NULL,
  `detail` mediumtext,
  `file_name` varchar(512) DEFAULT NULL,
  `scan_id` varchar(36) NOT NULL,
  `issue_id` varchar(36) DEFAULT NULL,
  `commit_id` varchar(64) NOT NULL,
  `repo_id` varchar(36) NOT NULL,
  `code_lines` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relation`
--

DROP TABLE IF EXISTS `relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relation` (
  `repository_id` int(10) DEFAULT NULL,
  `repository_uuid` varchar(36) NOT NULL,
  `branch` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`repository_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repo_measure`
--

DROP TABLE IF EXISTS `repo_measure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repo_measure` (
  `uuid` varchar(36) NOT NULL,
  `files` int(11) DEFAULT NULL,
  `ncss` int(11) DEFAULT NULL,
  `classes` int(11) DEFAULT NULL,
  `functions` int(11) DEFAULT NULL,
  `ccn` double DEFAULT NULL,
  `java_docs` int(11) DEFAULT NULL,
  `java_doc_lines` int(11) DEFAULT NULL,
  `single_comment_lines` int(11) DEFAULT NULL,
  `multi_comment_lines` int(11) DEFAULT NULL,
  `commit_id` varchar(64) NOT NULL,
  `commit_time` datetime NOT NULL,
  `repo_id` varchar(36) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `uuid` varchar(36) NOT NULL,
  `repository_id` int(11) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `description` text,
  `url` varchar(512) NOT NULL,
  `local_addr` varchar(512) DEFAULT NULL,
  `is_private` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan`
--

DROP TABLE IF EXISTS `scan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan` (
  `uuid` varchar(36) NOT NULL,
  `category` varchar(45) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` varchar(32) NOT NULL,
  `result_summary` mediumtext,
  `repo_id` varchar(36) DEFAULT NULL,
  `commit_id` varchar(64) DEFAULT NULL,
  `commit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_result`
--

DROP TABLE IF EXISTS `scan_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(45) NOT NULL,
  `repo_id` varchar(36) NOT NULL,
  `scan_date` date NOT NULL,
  `commit_date` date NOT NULL,
  `new_count` int(11) DEFAULT '0',
  `eliminated_count` int(11) DEFAULT '0',
  `remaining_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag` (
  `uuid` varchar(36) NOT NULL,
  `name` varchar(45) NOT NULL,
  `scope` varchar(45) NOT NULL,
  `color` varchar(45) NOT NULL,
  `icon` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tagged`
--

DROP TABLE IF EXISTS `tagged`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tagged` (
  `item_id` varchar(36) NOT NULL,
  `tag_id` varchar(36) NOT NULL,
  PRIMARY KEY (`item_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-18 10:34:03
