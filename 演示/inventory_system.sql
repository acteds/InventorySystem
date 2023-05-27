/*
 Navicat Premium Data Transfer

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 127.0.0.1:3306
 Source Schema         : inventory_system

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 20/05/2023 13:39:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `gid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `explanation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`gid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (1, '土豆', '-');
INSERT INTO `goods` VALUES (2, '大米', '-');
INSERT INTO `goods` VALUES (3, '面粉', '-');
INSERT INTO `goods` VALUES (4, '胡萝卜', '-');
INSERT INTO `goods` VALUES (5, '牛奶', '-');
INSERT INTO `goods` VALUES (6, '苹果', '-');
INSERT INTO `goods` VALUES (7, '坚果', '-');
INSERT INTO `goods` VALUES (8, '罐头', '-');
INSERT INTO `goods` VALUES (9, '腌菜', '-');
INSERT INTO `goods` VALUES (10, '花生', '-');
INSERT INTO `goods` VALUES (11, '玉米', '-');

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory`  (
  `iid` int(11) NOT NULL AUTO_INCREMENT,
  `gid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `explanation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '出库标记,若为入库则设置为0.若为出库则指向要出库的iid.',
  `createTime` datetime(0) NOT NULL,
  `review` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`iid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES (1, 1, 1, 70, '1-103', '-', 0, '2023-03-23 07:03:12', 11);
INSERT INTO `inventory` VALUES (2, 2, 1, 10, '1-103', '-', 0, '2023-03-23 18:34:27', 0);
INSERT INTO `inventory` VALUES (3, 3, 1, 10, '1-103', '-', 0, '2023-03-23 18:42:53', 10);
INSERT INTO `inventory` VALUES (4, 1, 1, 10, '1-103', '-', 1, '2023-03-31 13:15:00', 0);
INSERT INTO `inventory` VALUES (5, 1, 1, 10, '1-104', '-', 0, '2023-04-19 18:45:51', 10);
INSERT INTO `inventory` VALUES (6, 4, 1, 1200, '1-102', '-', 0, '2023-04-20 15:12:09', 1);
INSERT INTO `inventory` VALUES (7, 11, 2, 120, '2-103', '-', 0, '2023-04-20 16:11:45', 10);
INSERT INTO `inventory` VALUES (8, 10, 2, 1200, '2-104', '-', 0, '2023-04-20 16:12:07', 1);
INSERT INTO `inventory` VALUES (9, 9, 2, 118, '2-105', '-', 0, '2023-04-20 16:12:46', 10);
INSERT INTO `inventory` VALUES (10, 8, 2, 56, '2-106', '-', 0, '2023-04-20 16:13:09', 10);
INSERT INTO `inventory` VALUES (11, 7, 2, 315, '1-106', '-', 0, '2023-04-20 16:13:33', 10);
INSERT INTO `inventory` VALUES (12, 6, 8, 600, '1-104', '-', 0, '2023-04-20 16:20:58', 0);
INSERT INTO `inventory` VALUES (13, 5, 8, 120, '2-106', '-', 0, '2023-04-20 16:21:15', 10);
INSERT INTO `inventory` VALUES (14, 4, 8, 100, '1-103', '-', 0, '2023-04-20 16:21:29', 10);
INSERT INTO `inventory` VALUES (15, 8, 8, 100, '2-104', '-', 0, '2023-04-20 16:21:42', 0);
INSERT INTO `inventory` VALUES (16, 2, 8, 100, '2-107', '--', 0, '2023-04-20 16:22:04', 0);
INSERT INTO `inventory` VALUES (17, 1, 1, 10, '1-103', '-', 1, '2023-04-20 16:32:12', 1);
INSERT INTO `inventory` VALUES (18, 7, 9, 50, '1-106', '-', 11, '2023-04-20 16:32:56', 10);
INSERT INTO `inventory` VALUES (19, 1, 9, 30, '1-103', '-', 1, '2023-04-20 16:33:20', 10);
INSERT INTO `inventory` VALUES (20, 9, 9, 2, '2-105', '-', 9, '2023-04-20 16:33:29', 10);
INSERT INTO `inventory` VALUES (21, 7, 9, 100, '1-106', '-', 11, '2023-04-20 16:33:41', 0);
INSERT INTO `inventory` VALUES (22, 1, 3, 20, '1-103', '-', 1, '2023-04-20 17:08:46', 0);
INSERT INTO `inventory` VALUES (23, 7, 3, 135, '1-106', '-', 11, '2023-04-20 17:08:59', 10);
INSERT INTO `inventory` VALUES (24, 9, 3, 45, '2-105', '-', 9, '2023-04-20 17:09:09', 0);
INSERT INTO `inventory` VALUES (25, 4, 3, 41, '1-103', '-', 14, '2023-04-20 17:09:24', 0);

-- ----------------------------
-- Table structure for inventory_review
-- ----------------------------
DROP TABLE IF EXISTS `inventory_review`;
CREATE TABLE `inventory_review`  (
  `irid` int(11) NOT NULL AUTO_INCREMENT,
  `iid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `explanation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`irid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory_review
-- ----------------------------
INSERT INTO `inventory_review` VALUES (1, 1, 1, '-');
INSERT INTO `inventory_review` VALUES (3, 4, 1, '???');
INSERT INTO `inventory_review` VALUES (4, 3, 2, '');
INSERT INTO `inventory_review` VALUES (5, 2, 2, '');
INSERT INTO `inventory_review` VALUES (6, 5, 1, '-');
INSERT INTO `inventory_review` VALUES (7, 11, 4, '已通过');
INSERT INTO `inventory_review` VALUES (8, 10, 4, '已通过');
INSERT INTO `inventory_review` VALUES (9, 9, 4, '已通过');
INSERT INTO `inventory_review` VALUES (10, 8, 4, '数量错误');
INSERT INTO `inventory_review` VALUES (11, 18, 11, '-');
INSERT INTO `inventory_review` VALUES (12, 20, 11, '-');
INSERT INTO `inventory_review` VALUES (13, 17, 11, '数量错误');
INSERT INTO `inventory_review` VALUES (14, 21, 11, '数量错误');
INSERT INTO `inventory_review` VALUES (15, 7, 10, '-');
INSERT INTO `inventory_review` VALUES (16, 6, 10, '-');
INSERT INTO `inventory_review` VALUES (17, 16, 10, '-');
INSERT INTO `inventory_review` VALUES (18, 13, 10, '-');
INSERT INTO `inventory_review` VALUES (19, 14, 10, '-');
INSERT INTO `inventory_review` VALUES (20, 23, 11, '-');
INSERT INTO `inventory_review` VALUES (21, 19, 11, '-');

-- ----------------------------
-- Table structure for parameters_main
-- ----------------------------
DROP TABLE IF EXISTS `parameters_main`;
CREATE TABLE `parameters_main`  (
  `pmid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `explanation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pmid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parameters_main
-- ----------------------------
INSERT INTO `parameters_main` VALUES (2, '用户权限', '用户的权限类型');
INSERT INTO `parameters_main` VALUES (3, '审核状态', '货品的审核状态以及确认状态');

-- ----------------------------
-- Table structure for parameters_sub
-- ----------------------------
DROP TABLE IF EXISTS `parameters_sub`;
CREATE TABLE `parameters_sub`  (
  `psid` int(11) NOT NULL AUTO_INCREMENT,
  `pmid` int(11) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `explanation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`psid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parameters_sub
-- ----------------------------
INSERT INTO `parameters_sub` VALUES (1, 2, '超级管理员', '1', '全局权限');
INSERT INTO `parameters_sub` VALUES (2, 2, '入库管理员', '2', '入库管理;库存总览');
INSERT INTO `parameters_sub` VALUES (3, 2, '出库管理员', '3', '出库管理;库存总览');
INSERT INTO `parameters_sub` VALUES (4, 2, '入库审核员', '4', '入库审核;库存总览');
INSERT INTO `parameters_sub` VALUES (5, 2, '出库审核员', '5', '出库审核;库存总览');
INSERT INTO `parameters_sub` VALUES (6, 2, '用户管理员', '6', '用户管理');
INSERT INTO `parameters_sub` VALUES (7, 3, '审核中', '0', '货品等待审核');
INSERT INTO `parameters_sub` VALUES (8, 3, '审核未通过', '1', '货品审核未通过');
INSERT INTO `parameters_sub` VALUES (9, 3, '审核通过', '10', '审核通过');
INSERT INTO `parameters_sub` VALUES (10, 3, '审核通过,已确认', '11', '审核通过,录入人员已确认');
INSERT INTO `parameters_sub` VALUES (11, 2, '货品类别维护员', '7', '类别管理;库存总览');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rank` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', '13888764232', '1', '1');
INSERT INTO `user` VALUES (2, '入库员', '123456', '13961235526', '2', '1');
INSERT INTO `user` VALUES (3, '出库员', '123456', '15031445221', '3', '1');
INSERT INTO `user` VALUES (4, '审核员', '123456', '15332526856', '4', '1');
INSERT INTO `user` VALUES (5, '审核员2', '123456', '13657426651', '5', '1');
INSERT INTO `user` VALUES (6, '用户管理员', '123456', '15662254153', '6', '1');
INSERT INTO `user` VALUES (7, '类别维护', '123456', '13976835469', '7', '1');
INSERT INTO `user` VALUES (8, '胡杨', '123456', '13986567421', '2', '1');
INSERT INTO `user` VALUES (9, '王芳', '123456', '15076605856', '3', '1');
INSERT INTO `user` VALUES (10, '赵鑫', '123456', '13746365653', '4', '1');
INSERT INTO `user` VALUES (11, '张宇', '123456', '15655364123', '5', '1');

SET FOREIGN_KEY_CHECKS = 1;
