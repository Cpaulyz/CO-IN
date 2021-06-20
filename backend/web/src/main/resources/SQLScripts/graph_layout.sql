/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Schema         : coin

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 20/06/2021 12:23:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for graph_layout
-- ----------------------------
DROP TABLE IF EXISTS `graph_layout`;
CREATE TABLE `graph_layout`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `project_id` int NOT NULL,
  `layout_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `width` double NOT NULL DEFAULT 0,
  `height` double NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `nid`(`project_id`, `layout_type`) USING BTREE,
  INDEX `graph_layout_project_type`(`project_id`, `layout_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 411 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
