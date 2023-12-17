/*
 Navicat Premium Data Transfer

 Source Server         : SJXM
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3306
 Source Schema         : bilibilipro

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 17/12/2023 15:48:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `uid` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id 唯一标识',
  `account_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登陆用用户名',
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登陆用密码',
  `birthday` date NOT NULL COMMENT '用户生日',
  `follow_num` int NOT NULL DEFAULT '0' COMMENT '关注人数',
  `fans_num` int NOT NULL DEFAULT '0' COMMENT '粉丝人数',
  `dynamic_num` int NOT NULL DEFAULT '0' COMMENT '动态数',
  `account_brief` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '谢谢你的关注！！' COMMENT '简介',
  `avatar` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '头像',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `like_num` int DEFAULT '0' COMMENT '用户获赞数',
  `click_num` int DEFAULT '0' COMMENT '用户总播放数',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique_account` (`account_id`) USING BTREE COMMENT '用户名唯一索引',
  UNIQUE KEY `unique_user_name` (`account_name`) USING BTREE COMMENT '用户昵称唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for barrage
-- ----------------------------
DROP TABLE IF EXISTS `barrage`;
CREATE TABLE `barrage` (
  `barrage_id` bigint NOT NULL AUTO_INCREMENT COMMENT '弹幕id',
  `time` double NOT NULL COMMENT '推送弹幕在视频中的时间点',
  `uid` bigint NOT NULL COMMENT '弹幕发布者id',
  `like_num` int NOT NULL DEFAULT '0' COMMENT '弹幕点赞数',
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '弹幕内容',
  `post_time` datetime NOT NULL COMMENT '弹幕发送的时间',
  `video_id` bigint NOT NULL COMMENT '弹幕属于哪个视频',
  `type` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'scroll' COMMENT '弹幕类型\nscroll 滚动\ntop 顶部\nbottom 底部\n默认scroll滚动',
  `color` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#fff' COMMENT '弹幕颜色，默认白色',
  PRIMARY KEY (`barrage_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名',
  `home_show` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '该分类标签是否展示在主页\n0代表未展示在主页 1代表展示在主页',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `unique_name` (`name`) USING BTREE COMMENT '分类名唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `uid` bigint NOT NULL COMMENT '评论发布者',
  `content` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论主体内容',
  `post_time` datetime NOT NULL COMMENT '发布评论的时间',
  `like_num` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `video_id` bigint DEFAULT NULL COMMENT '视频id\n针对视频的父评论有',
  `dynamic_id` bigint DEFAULT NULL COMMENT '动态id\n针对动态的父评论有',
  `father_id` bigint DEFAULT NULL COMMENT '父评论id\n子评论有',
  `total_father_id` bigint DEFAULT NULL COMMENT '总的父评论的id\n',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for dynamic
-- ----------------------------
DROP TABLE IF EXISTS `dynamic`;
CREATE TABLE `dynamic` (
  `dynamic_id` bigint NOT NULL AUTO_INCREMENT COMMENT '动态id',
  `uid` bigint NOT NULL COMMENT '动态发布者',
  `post_time` datetime NOT NULL COMMENT '动态发布时间',
  `text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '我发布了一条新动态，快来评论吧！' COMMENT '动态文字内容',
  `video_id` bigint DEFAULT NULL COMMENT '视频id\n若动态存在视频则有',
  `like_num` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_num` int NOT NULL DEFAULT '0' COMMENT '评论数',
  PRIMARY KEY (`dynamic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for fav
-- ----------------------------
DROP TABLE IF EXISTS `fav`;
CREATE TABLE `fav` (
  `fav_id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏id',
  `uid` bigint DEFAULT NULL COMMENT '收藏者uid',
  `is_dic` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否为收藏夹目录\n0 否 1 是',
  `video_id` bigint DEFAULT NULL COMMENT '视频id',
  `father_dic` bigint DEFAULT NULL COMMENT '父目录id',
  `fav_title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收藏夹目录名',
  `fav_num` int DEFAULT '0' COMMENT '收藏夹目录中视频数量',
  `fav_poster` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收藏夹海报地址',
  `is_public` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '收藏夹目录是否公开\n0 公开 1 私密',
  `create_time` datetime DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`fav_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `follow_id` bigint NOT NULL AUTO_INCREMENT COMMENT '关注列表\n',
  `follower_uid` bigint DEFAULT NULL COMMENT '关注者的uid',
  `followed_uid` bigint DEFAULT NULL COMMENT '被关注者uid',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`follow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史记录主键，唯一标识',
  `uid` bigint DEFAULT NULL COMMENT '视频观看者uid\n',
  `video_id` bigint DEFAULT NULL COMMENT '视频id',
  `create_time` datetime DEFAULT NULL COMMENT '记录创建时间',
  PRIMARY KEY (`history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for like
-- ----------------------------
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like` (
  `like_id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞id',
  `from_uid` bigint DEFAULT NULL COMMENT '点赞者uid',
  `to_uid` bigint DEFAULT NULL COMMENT '点赞目标对象的uid',
  `type` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '点赞对象类型\n0 视频 1 动态 2 评论 3 弹幕',
  `video_id` bigint DEFAULT NULL COMMENT '点赞视频id',
  `dynamic_id` bigint DEFAULT NULL COMMENT '点赞的动态id',
  `comment_id` bigint DEFAULT NULL COMMENT '点赞评论id',
  `barrage_id` bigint DEFAULT NULL COMMENT '点赞弹幕id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '点赞状态\n0 点赞 1 取消',
  `create_time` datetime DEFAULT NULL COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '数据更新时间',
  PRIMARY KEY (`like_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息id 主键 单调递增',
  `from_uid` bigint DEFAULT NULL COMMENT '发信人uid\n',
  `to_uid` bigint DEFAULT NULL COMMENT '收信人uid\n',
  `content` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消息内容',
  `post_time` datetime DEFAULT NULL COMMENT '发送消息的时间',
  `is_read` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否已读。\n0为未读\n1为已读',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '信息类型\n0 私信 1 点赞 2 收藏 3 评论 4 动态 5 服务器视频处理完成',
  `video_id` bigint DEFAULT NULL COMMENT '视频id',
  `dynamic_id` bigint DEFAULT NULL COMMENT '动态id\n',
  `comment_id` bigint DEFAULT NULL COMMENT '评论id',
  `barrage_id` bigint DEFAULT NULL COMMENT '弹幕id',
  `is_system` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '是否为系统消息\n0 不是 1 是',
  `now_comment_id` bigint DEFAULT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB AUTO_INCREMENT=289 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for search
-- ----------------------------
DROP TABLE IF EXISTS `search`;
CREATE TABLE `search` (
  `search_id` bigint NOT NULL AUTO_INCREMENT COMMENT '搜索id 主键 非空',
  `search_content` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '搜索的具体内容',
  `search_num` int DEFAULT '0' COMMENT '搜索次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`search_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for video
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `video_id` bigint NOT NULL AUTO_INCREMENT COMMENT '视频id，BV号',
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频标题',
  `src` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频资源地址',
  `post_time` datetime NOT NULL COMMENT '投稿时间',
  `barrage_num` int NOT NULL DEFAULT '0' COMMENT '弹幕数',
  `last_time` int NOT NULL COMMENT '视频持续时长\n以秒作为单位',
  `click_num` int NOT NULL DEFAULT '0' COMMENT '点击量，播放量',
  `uid` bigint NOT NULL COMMENT '投稿人',
  `video_brief` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '这么好看的视频不来个三连吗？' COMMENT '视频简介',
  `like_num` int NOT NULL DEFAULT '0' COMMENT '点赞量',
  `coin_num` int NOT NULL DEFAULT '0' COMMENT '投币量',
  `fav_num` int NOT NULL DEFAULT '0' COMMENT '收藏量',
  `tags` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `comment_num` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `poster` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '封面海报',
  `in_suggest` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '在右侧推荐位（上限4个）\n0代表不在 1代表在',
  `in_carousel` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '在左侧轮播图展示（上限6个）\n0代表不在 1代表在',
  PRIMARY KEY (`video_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
