CREATE TABLE `mkt_activity_info` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `activity_id` varchar(50) NOT NULL DEFAULT '' COMMENT '活动id',
     `activity_name` varchar(50) NOT NULL DEFAULT '' COMMENT '活动名称',
     `validity_date_start` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效期起始时间',
     `validity_date_end` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效期结束时间',
     `enable` tinyint(4) DEFAULT '0' COMMENT '是否开启 0：关闭 1开启',
     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_activity_id` (`activity_id`),
     KEY `ix_created_at` (`created_at`),
     KEY `ix_updated_at` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

INSERT INTO `mkt_activity_info` (`id`, `activity_id`, `activity_name`, `validity_date_start`, `validity_date_end`, `enable`, `updated_at`, `created_at`)
VALUES
    (1, 'A1000001', '抽奖活动', '2020-01-14 20:16:17', '2030-10-20 00:00:00', 1, '2024-08-23 16:13:49', '2024-08-23 16:13:49');


CREATE TABLE `mkt_activity_rule` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `activity_id` varchar(50) NOT NULL DEFAULT '' COMMENT '活动id',
     `rule_key` varchar(50) NOT NULL DEFAULT '' COMMENT '规则key',
     `rule_name` varchar(50) NOT NULL DEFAULT '' COMMENT '规则名称',
     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     PRIMARY KEY (`id`),
     KEY `ix_activity_id` (`activity_id`),
     KEY `ix_created_at` (`created_at`),
     KEY `ix_updated_at` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动准入规则表';

INSERT INTO `mkt_activity_rule` (`id`, `activity_id`, `rule_key`, `rule_name`, `updated_at`, `created_at`)
VALUES
    (1, 'A1000001', 'check_phone', '检查手机号', '2024-08-24 14:23:14', '2024-08-24 14:23:14');



CREATE TABLE `mkt_activity_prize` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
      `activity_id` varchar(50) NOT NULL DEFAULT '' COMMENT '活动id',
      `prize_id` varchar(50) NOT NULL DEFAULT '' COMMENT '奖品id',
      `prize_name` varchar(50) NOT NULL DEFAULT '' COMMENT '奖品名称',
      `prize_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '奖品价值, 单位分',
      `prize_total_num` int(4) NOT NULL DEFAULT '0' COMMENT '活动奖品总个数',
      `prize_remaining_num` int(4) NOT NULL DEFAULT '0' COMMENT '活动奖品剩余个数',
      `prize_occupy_num` int(4) NOT NULL DEFAULT '0' COMMENT '活动奖品占用个数',
      `enable` tinyint(4) DEFAULT '0' COMMENT '是否可使用 0：不能使用 1可以使用',
      `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_activity_id_prize_id` (`activity_id`,`prize_id`),
      KEY `ix_created_at` (`created_at`),
      KEY `ix_updated_at` (`updated_at`),
      KEY `ix_prize_id` (`prize_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动奖品表';

INSERT INTO `mkt_activity_prize` (`id`, `activity_id`, `prize_id`, `prize_name`, `prize_amount`, `prize_total_num`, `prize_remaining_num`, `prize_occupy_num`, `enable`, `updated_at`, `created_at`)
VALUES
    (1, 'A1000001', 'P1000001', '5元红包', 500, 100, 100, 0, 1, '2024-08-24 16:42:55', '2024-08-24 16:42:36');



CREATE TABLE `mkt_activity_prize_grant` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `grant_id` varchar(50) NOT NULL DEFAULT '' COMMENT '中奖id，幂等键',
    `activity_id` varchar(50) NOT NULL DEFAULT '' COMMENT '活动id',
    `prize_id` varchar(50) NOT NULL DEFAULT '' COMMENT '奖品id',
    `grant_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '中奖时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_grant_id` (`grant_id`),
    KEY `ix_activity_id` (`activity_id`),
    KEY `ix_prize_id` (`prize_id`),
    KEY `ix_updated_at` (`updated_at`),
    KEY `ix_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动奖品发放表';
