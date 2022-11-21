/*
 Navicat Premium Data Transfer

 Source Server         : mysql_127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : 127.0.0.1:3306
 Source Schema         : matcheasy

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 16/12/2021 10:06:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for p_exam
-- ----------------------------
DROP TABLE IF EXISTS `p_exam`;
CREATE TABLE `p_exam`  (
  `exam_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '测试ID',
  `exam_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '测试名称',
  `logo_path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片路径',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标志: 0-存在,1-删除',
  PRIMARY KEY (`exam_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1500 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '测试demo表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p_exam
-- ----------------------------
INSERT INTO `p_exam` VALUES (1491, '111项目必填项', '/asktrue/img/mobile/lekao/hbimg/aaa.jpg', 100000002, '2020-09-07 09:50:40', 100000002, '2020-09-12 14:22:38', 0);
INSERT INTO `p_exam` VALUES (1497, '111eee信息必填项', '/asktrue/img/mobile/lekao/ccc.jpg', 100000002, '2020-09-09 11:41:12', 100000002, '2020-09-18 16:54:41', 0);
INSERT INTO `p_exam` VALUES (1498, '111评估匿名', '/asktrue/img/mobile/dwa/awdw.jpg', 100000002, '2020-09-10 14:01:39', 100000002, '2020-10-24 13:43:25', 0);
INSERT INTO `p_exam` VALUES (1499, '111评估匿名', '/asktrue/img/mobile/lekao/hbimg/lk2221.jpg', 100000002, '2021-01-13 11:30:19', 100000002, '2021-05-06 15:20:37', 0);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门ID',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志: 0-存在,1-删除',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传文件名',
  `file_real_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储真实文件名',
  `access_url` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件访问地址',
  `storage_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件存储绝对路径',
  `relative_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件存储相对路径',
  `file_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint(64) NULL DEFAULT NULL COMMENT '文件大小',
  `status` tinyint(2) NULL DEFAULT NULL COMMENT '状态',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志: 0-存在,1-删除',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件上传' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (12, '数据权限.txt', '88e800ba17b846d68a49e796b5de4c14.txt', 'http://127.0.0.1:80/file/my/88e800ba17b846d68a49e796b5de4c14.txt', 'D:/matcheasy/upload/my/88e800ba17b846d68a49e796b5de4c14.txt', 'my/88e800ba17b846d68a49e796b5de4c14.txt', 'txt', 382, 1, 0, '2020-12-25 10:50:59', '', NULL, '0', NULL);
INSERT INTO `sys_file` VALUES (13, '数据权限.txt', 'ea2125d9e2ef42beadb745521fb3ecdd.txt', 'http://127.0.0.1:80/file/my/ea2125d9e2ef42beadb745521fb3ecdd.txt', 'D:/matcheasy/upload/my/ea2125d9e2ef42beadb745521fb3ecdd.txt', 'my/ea2125d9e2ef42beadb745521fb3ecdd.txt', 'txt', 382, 1, 0, '2020-12-25 10:54:36', '', NULL, '0', NULL);
INSERT INTO `sys_file` VALUES (14, 'aaa.xlsx', '8ac5ca170b7043d3a69c068fbe7d5388.xlsx', 'http://127.0.0.1:80/file/mydir/8ac5ca170b7043d3a69c068fbe7d5388.xlsx', 'D:/matcheasy/upload/mydir/8ac5ca170b7043d3a69c068fbe7d5388.xlsx', 'mydir/8ac5ca170b7043d3a69c068fbe7d5388.xlsx', 'xlsx', 10169, 1, 0, '2021-01-20 17:22:34', '', NULL, '0', NULL);
INSERT INTO `sys_file` VALUES (15, '数据权限.txt', '07f1d994d28849459ff12fded0bc2471.txt', 'http://127.0.0.1:80/file/mydir/07f1d994d28849459ff12fded0bc2471.txt', 'D:/matcheasy/upload/mydir/07f1d994d28849459ff12fded0bc2471.txt', 'mydir/07f1d994d28849459ff12fded0bc2471.txt', 'txt', 382, 1, 0, '2021-01-20 17:22:35', '', NULL, '0', NULL);
INSERT INTO `sys_file` VALUES (16, 'ezha.jpg', '820fe7ae52f047ecb4433fca93491abc.jpg', 'http://127.0.0.1:80/file/mydir/820fe7ae52f047ecb4433fca93491abc.jpg', 'D:/matcheasy/upload/mydir/820fe7ae52f047ecb4433fca93491abc.jpg', 'mydir/820fe7ae52f047ecb4433fca93491abc.jpg', 'jpg', 107211, 1, 0, '2021-01-20 17:22:35', '', NULL, '0', NULL);
INSERT INTO `sys_file` VALUES (17, 'aaa.xlsx', 'dccacf1284624da08336345a3e904676.xlsx', 'http://127.0.0.1:80/file/mydir/dccacf1284624da08336345a3e904676.xlsx', 'D:/matcheasy/upload/mydir/dccacf1284624da08336345a3e904676.xlsx', 'mydir/dccacf1284624da08336345a3e904676.xlsx', 'xlsx', 10169, 1, 0, '2021-01-20 17:28:36', '', NULL, '0', NULL);
INSERT INTO `sys_file` VALUES (18, '数据权限.txt', 'e59af3a29acb4eb082f19e2ff1e14b9d.txt', 'http://127.0.0.1:80/file/mydir/e59af3a29acb4eb082f19e2ff1e14b9d.txt', 'D:/matcheasy/upload/mydir/e59af3a29acb4eb082f19e2ff1e14b9d.txt', 'mydir/e59af3a29acb4eb082f19e2ff1e14b9d.txt', 'txt', 382, 1, 0, '2021-01-20 17:28:37', '', NULL, '0', NULL);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '请求地址',
  `target` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '打开方式（menuItem页签 menuBlank新窗口）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 'exportUserInfo', 0, 0, '#', '', '', '0', NULL, '#', '', NULL, '', NULL, '');

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志: 0-存在,1-删除',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志: 0-存在,1-删除',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', 'aaa', 1, '1', '1', '', NULL, '', NULL, '0', NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `login_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像路径',
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '盐加密',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登陆IP',
  `last_login_date` datetime NULL DEFAULT NULL COMMENT '最后登陆时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志: 0-存在,1-删除',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `login_code`(`login_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1397425263733690369 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1397425263733690368, NULL, 'admin', '202cb962ac59075b964b07152d234b70', '00', '阿尔法狗', '3313@qq.com', '18340088315', '0', '', '', '0', '', NULL, '', NULL, '3', '2021-05-26 05:34:17', 0, NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (3, 1);

-- ----------------------------
-- Table structure for t_cron
-- ----------------------------
DROP TABLE IF EXISTS `t_cron`;
CREATE TABLE `t_cron`  (
  `cron_id` bigint(20) NOT NULL COMMENT '任务id',
  `cron` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定时任务表达式',
  PRIMARY KEY (`cron_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '动态定时任务配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cron
-- ----------------------------
INSERT INTO `t_cron` VALUES (1, '0/10 * * * * ?');

-- ----------------------------
-- Table structure for t_dwz
-- ----------------------------
DROP TABLE IF EXISTS `t_dwz`;
CREATE TABLE `t_dwz`  (
  `api_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '短链接码',
  `long_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '长url',
  `short_url` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '短url',
  `call_count` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调用数量',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人ID',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志: 0-存在,1-删除',
  PRIMARY KEY (`api_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短网址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dwz
-- ----------------------------
INSERT INTO `t_dwz` VALUES ('7ktSm9Cz', 'https://mp.weixin.qq.com/s?__biz=MzI4Njc5NjM1NQ==&mid=2247493692&idx=1&sn=cf5adb980ac7be5670ad5f52012726c9&chksm=ebd5d710dca25e06b3f6d4fb6967f08920ee39485c254132204c9dd0167d2f052cb02d9575e1&scene=126&sessionid=1593322309&key=d9abbbe4b9a3fb83e1092495778ab21c10a153cf131cab0e3397d775d436b0e59f3409ccabc3b5c7701317813a2900d47a858fa9cb59a2b6669a1c316a669df4ced8c1efe04d3ad41f9811cf9b859906&ascene=1&uin=MTEyMTAyOTcwOA%3D%3D&devicetype=Windows+10+x64&version=62090523&lang=zh_CN&exportkey=AR9eyUgwiPYJ6KQeA8EM%2BfY%3D&pass_ticket=8GHChjZx%2FX5w56rus45rgYA6fYN52LDq%2BoIf%2Fjd98GY1IqjQAxCPWLdS4Z0D3pDh', 'http://127.0.0.1/dwz/7ktSm9Cz', '11', '1', '', NULL, '', NULL, 0);
INSERT INTO `t_dwz` VALUES ('EQuZv6KT', 'http://easybug.org/Home/Index', 'http://127.0.0.1/dwz/EQuZv6KT', '11', '1', '', NULL, '', NULL, 0);
INSERT INTO `t_dwz` VALUES ('NRU6SzKS', 'https://www.jetbrains.com/help/idea/discover-intellij-idea.html', 'http://127.0.0.1/dwz/NRU6SzKS', '2', '1', '', NULL, '', NULL, 0);
INSERT INTO `t_dwz` VALUES ('qBgBvtEP', 'https://i.csdn.net/#/uc/follow-list', 'http://127.0.0.1/dwz/qBgBvtEP', '6', '1', '', NULL, '', NULL, 0);
INSERT INTO `t_dwz` VALUES ('reu3TUDL', 'https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent/2.3.1.RELEASE', 'http://127.0.0.1/dwz/reu3TUDL', '3', '1', '', NULL, '', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
