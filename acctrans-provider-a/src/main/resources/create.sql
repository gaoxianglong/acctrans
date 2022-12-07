CREATE DATABASE IF NOT EXISTS `acctrans`;
USE `acctrans`;
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account`
(
    `id`            BIGINT(20)     NOT NULL AUTO_INCREMENT,
    `account_id`    BIGINT(20)     NOT NULL COMMENT '账户ID',
    `account_type`  INT(10)        NOT NULL DEFAULT 0 COMMENT '账户状态',
    `balance`       DECIMAL(10, 2) NOT NULL COMMENT '账户余额',
    `freeze_amount` DECIMAL(10, 2) NOT NULL COMMENT '冻结金额',
    `sys_amount`    DECIMAL(10, 2) NOT NULL COMMENT '系统金额',
    `create_time`   DATETIME       NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME                DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `t_account`(`account_id`, `balance`, `freeze_amount`, `sys_amount`, `create_time`)
VALUES (1001, 100.00, 0.00, 0.00, now());
INSERT INTO `t_account`(`account_id`, `balance`, `freeze_amount`, `sys_amount`, `create_time`)
VALUES (1002, 0.00, 0.00, 0.00, now());

DROP TABLE IF EXISTS `t_freeze`;
CREATE TABLE `t_freeze`
(
    `id`         BIGINT(20)     NOT NULL AUTO_INCREMENT,
    `account_id` BIGINT(20)     NOT NULL COMMENT '账户ID',
    `tid`        BIGINT(20)     NOT NULL COMMENT '事务ID',
    `type`       INT(10)        NOT NULL COMMENT '交易类型',
    `amount`     DECIMAL(10, 2) NOT NULL COMMENT '交易金额',
    `currency`   INT(10)        NOT NULL COMMENT '币种',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `t_try_log`;
CREATE TABLE `t_try_log`
(
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
    `tid`         BIGINT(20) NOT NULL COMMENT '事务ID',
    `create_time` DATETIME   NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `t_confirm_log`;
CREATE TABLE `t_confirm_log`
(
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
    `tid`         BIGINT(20) NOT NULL COMMENT '事务ID',
    `create_time` DATETIME   NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `t_cancle_log`;
CREATE TABLE `t_cancle_log`
(
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
    `tid`         BIGINT(20) NOT NULL COMMENT '事务ID',
    `create_time` DATETIME   NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;