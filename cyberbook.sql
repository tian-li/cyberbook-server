-- cyberbook_category: table
CREATE TABLE `cyberbook_category`
(
    `id`            varchar(50)  NOT NULL COMMENT '类别id',
    `added_by_user` bit(1)       NOT NULL COMMENT '是否是用户自己添加的类别',
    `color`         varchar(20)  NOT NULL COMMENT '饼状图中的颜色',
    `icon`          varchar(50)  NOT NULL COMMENT '图标名称',
    `is_spend`      bit(1)       NOT NULL COMMENT '是否是支出',
    `name`          varchar(225) NOT NULL COMMENT '类别名称',
    `sort_order`    int          NOT NULL COMMENT '排序位置',
    `user_id`       varchar(50)  NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- cyberbook_message_thread: table
CREATE TABLE `cyberbook_message_thread`
(
    `id`                varchar(50)  NOT NULL COMMENT 'thread id',
    `type`              varchar(50)  NOT NULL COMMENT 'thread类型',
    `preview`           varchar(225) NOT NULL COMMENT '最新消息的预览',
    `last_message_date` varchar(50)  NOT NULL COMMENT '最新消息的创建日期，使用UTC时间，毫秒值',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- cyberbook_private_message: table
CREATE TABLE `cyberbook_private_message`
(
    `id`                varchar(50) NOT NULL COMMENT '消息id',
    `from_user_id`      varchar(50) NOT NULL COMMENT '消息发送用户id',
    `to_user_id`        varchar(50) NOT NULL COMMENT '消息接收用户id',
    `message`           text        NOT NULL COMMENT '消息内容',
    `message_thread_id` varchar(50) NOT NULL COMMENT '消息所属thread id',
    `date_created`      varchar(50) NOT NULL COMMENT '消息创建日期，使用UTC时间，毫秒值',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- cyberbook_subscription: table
CREATE TABLE `cyberbook_subscription`
(
    `id`              varchar(50)    NOT NULL COMMENT '周期性账目id',
    `user_id`         varchar(50)    NOT NULL COMMENT '用户id',
    `amount`          decimal(20, 2) NOT NULL COMMENT '每次账目数额',
    `description`     varchar(225)            DEFAULT NULL COMMENT '描述',
    `frequency`       int            NOT NULL COMMENT '频率：1 - 天，2 - 周，3 - 月， 4 - 年',
    `period`          int            NOT NULL COMMENT '周期间隔',
    `start_date`      varchar(50)    NOT NULL COMMENT '开始日期，使用UTC时间，毫秒值',
    `end_date`        varchar(50)             DEFAULT NULL COMMENT '结束日期，使用UTC时间，毫秒值',
    `category_id`     varchar(50)    NOT NULL COMMENT '类别id',
    `date_created`    varchar(50)    NOT NULL COMMENT '条目创建日期，使用UTC时间，毫秒值',
    `date_modified`   varchar(50)    NOT NULL COMMENT '最后一次更新日期，使用UTC时间，毫秒值',
    `next_date`       varchar(50)    NOT NULL COMMENT '下次发生日期，使用UTC时间，毫秒值',
    `summary`         varchar(200)   NOT NULL COMMENT '总结：例：每个月的16号',
    `total_amount`    decimal(20, 2)          DEFAULT NULL COMMENT '该周期性账目一共产生的数额',
    `activate_status` bit(1)         NOT NULL DEFAULT b'1' COMMENT '订阅状态 0 - 已结束或取消 1 - 正在进行',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- cyberbook_transaction: table
CREATE TABLE `cyberbook_transaction`
(
    `id`               varchar(50)    NOT NULL COMMENT '类别id',
    `amount`           decimal(20, 2) NOT NULL COMMENT '数额',
    `category_id`      varchar(50)    NOT NULL COMMENT '类别id',
    `date_created`     varchar(50)    NOT NULL COMMENT '条目创建日期，使用UTC时间，毫秒值',
    `date_modified`    varchar(50)    NOT NULL COMMENT '最后一次更新日期，使用UTC时间，毫秒值',
    `description`      varchar(225) DEFAULT NULL COMMENT '描述',
    `subscription_id`  varchar(50)  DEFAULT NULL COMMENT '周期性账目id',
    `transaction_date` varchar(50)    NOT NULL COMMENT '账目发生日期，使用UTC时间，毫秒值',
    `user_id`          varchar(50)    NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- cyberbook_user: table
CREATE TABLE `cyberbook_user`
(
    `id`                varchar(50) NOT NULL COMMENT '用户id',
    `username`          varchar(225)         DEFAULT NULL COMMENT '用户名',
    `password`          varchar(225)         DEFAULT NULL COMMENT '密码',
    `email`             varchar(225)         DEFAULT NULL COMMENT '登录邮箱',
    `gender`            int                  DEFAULT '0' COMMENT '性别：0 - 保密，1 - 男， 2 - 女',
    `birthday`          varchar(50)          DEFAULT NULL COMMENT '生日, MM/DD/YYYY',
    `registered`        bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否已注册',
    `profile_photo`     varchar(500)         DEFAULT NULL COMMENT '头像url',
    `date_registered`   varchar(50) NOT NULL COMMENT '注册日期，使用UTC时间，毫秒值',
    `theme`             varchar(50)          DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- cyberbook_user_message_threads: table
CREATE TABLE `cyberbook_user_message_threads`
(
    `user_id`            varchar(255) NOT NULL,
    `message_threads_id` varchar(255) NOT NULL,
    KEY `FKm2vxqxycm4yci7so3h4isg3l0` (`message_threads_id`),
    KEY `FKrb1mqc7ri23ub9wgtahq4fgin` (`user_id`),
    CONSTRAINT `FKm2vxqxycm4yci7so3h4isg3l0` FOREIGN KEY (`message_threads_id`) REFERENCES `cyberbook_message_thread` (`id`),
    CONSTRAINT `FKrb1mqc7ri23ub9wgtahq4fgin` FOREIGN KEY (`user_id`) REFERENCES `cyberbook_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- No native definition for element: FKrb1mqc7ri23ub9wgtahq4fgin (index)

-- No native definition for element: FKm2vxqxycm4yci7so3h4isg3l0 (index)

-- user_roles: table
CREATE TABLE `user_roles`
(
    `user_id` varchar(50) NOT NULL,
    `roles`   int DEFAULT NULL,
    KEY `user_roles` (`user_id`),
    CONSTRAINT `user_roles` FOREIGN KEY (`user_id`) REFERENCES `cyberbook_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- No native definition for element: user_roles (index)

