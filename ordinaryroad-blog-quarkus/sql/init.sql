/*
 * MIT License
 *
 * Copyright (c) 2021 苗锦洲
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

DROP DATABASE IF EXISTS or_blog_dev;
CREATE DATABASE or_blog_dev;
USE or_blog_dev;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blog_article
-- ----------------------------
DROP TABLE if exists `blog_article`;
CREATE TABLE `blog_article`
(
    `id`           bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uuid`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs   NOT NULL COMMENT '主键UUID',
    `created_time` datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci   NULL DEFAULT NULL COMMENT '创建者ID',
    `update_time`  datetime                                                       NULL DEFAULT NULL COMMENT '更新时间',
    `update_by`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci   NULL DEFAULT NULL COMMENT '更新者ID',

    `title`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NULL DEFAULT NULL COMMENT '标题',
    `cover_image`  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NULL DEFAULT NULL COMMENT '封面图片',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `blog_article_uuid_uindex` (`uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_as_cs COMMENT = '博客文章表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog_oauth_user
-- ----------------------------
DROP TABLE if exists `blog_oauth_user`;
CREATE TABLE `blog_oauth_user`
(
    `id`           bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uuid`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs   NOT NULL COMMENT '主键UUID',
    `created_time` datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci   NULL DEFAULT NULL COMMENT '创建者ID',
    `update_time`  datetime                                                       NULL DEFAULT NULL COMMENT '更新时间',
    `update_by`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci   NULL DEFAULT NULL COMMENT '更新者ID',

    `username`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NULL DEFAULT NULL COMMENT '用户名',
    `avatar`       varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '头像地址',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NULL DEFAULT NULL COMMENT '邮箱',
    `openid`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NULL DEFAULT NULL COMMENT 'OpenID',
    `provider`     varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs   NULL DEFAULT NULL COMMENT '提供者',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `blog_oauth_user_uuid_uindex` (`uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_as_cs COMMENT = '博客OAuth用户表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for blog_user
-- ----------------------------
DROP TABLE if exists `blog_user`;
CREATE TABLE `blog_user`
(
    `id`           bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uuid`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs   NOT NULL COMMENT '主键UUID',
    `created_time` datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci   NULL DEFAULT NULL COMMENT '创建者ID',
    `update_time`  datetime                                                       NULL DEFAULT NULL COMMENT '更新时间',
    `update_by`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci   NULL DEFAULT NULL COMMENT '更新者ID',

    `username`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NULL DEFAULT NULL COMMENT '用户名',
    `avatar`       varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '头像地址',
    `email`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `blog_user_uuid_uindex` (`uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_as_cs COMMENT = '博客用户表'
  ROW_FORMAT = Dynamic;

/*
 * MIT License
 *
 * Copyright (c) 2021 苗锦洲
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

-- ----------------------------
-- Table structure for blog_user_oauth_users
-- ----------------------------
DROP TABLE if exists `blog_user_oauth_users`;
CREATE TABLE `blog_user_oauth_users`
(
    `id`            bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uuid`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs  NOT NULL COMMENT '主键UUID',
    `created_time`  datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci  NULL DEFAULT NULL COMMENT '创建者ID',
    `update_time`   datetime                                                      NULL DEFAULT NULL COMMENT '更新时间',
    `update_by`     varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci  NULL DEFAULT NULL COMMENT '更新者ID',

    `user_id`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '用户ID',
    `oauth_user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT 'OAuth用户ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `blog_user_oauth_users_uuid_uindex` (`uuid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_as_cs COMMENT = '博客用户OAuth用户关联关系表'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
