use cyberbook_test;

DROP TABLE IF EXISTS `cyberbook_category`;
CREATE TABLE `cyberbook_category`
(
    `id`            varchar(50) NOT NULL COMMENT '类别id',
    `added_by_user` bit     NOT NULL COMMENT '是否是用户自己添加的类别',
    `color`         varchar(20) NOT NULL COMMENT '饼状图中的颜色',
    `icon`          varchar(50) NOT NULL COMMENT '图标名称',
    `is_spend`      bit     NOT NULL COMMENT '是否是支出',
    `name`          varchar(225) NOT NULL COMMENT '类别名称',
    `sort_order`    int         NOT NULL COMMENT '排序位置',
    `user_id`       varchar(50) NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `cyberbook_transaction`;
create table cyberbook_transaction
(
    `id`               varchar(50)    not null COMMENT '类别id',
    `amount`           decimal(20, 2) not null COMMENT '数额',
    `category_id`      varchar(50)    not null COMMENT '类别id',
    `date_created`     varchar(50)    not null COMMENT '条目创建日期，使用UTC时间，毫秒值',
    `date_modified`    varchar(50)    not null COMMENT '最后一次更新日期，使用UTC时间，毫秒值',
    `description`      varchar(225)    null COMMENT '描述',
    `subscription_id`  varchar(50)    COMMENT '周期性账目id',
    `transaction_date` varchar(50)    not null COMMENT '账目发生日期，使用UTC时间，毫秒值',
    `user_id`          varchar(50)    not null COMMENT '用户id',
    primary key (`id`)
) engine = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `cyberbook_subscription`;
create table cyberbook_subscription
(
    `id`            varchar(50)    not null COMMENT '周期性账目id',
    `user_id`       varchar(50)    not null COMMENT '用户id',
    `amount`        decimal(20, 2) not null COMMENT '每次账目数额',
    `description`   varchar(225)   null COMMENT '描述',
    `frequency`     int            not null COMMENT '频率：1 - 天，2 - 周，3 - 月， 4 - 年',
    `period`        int            not null COMMENT '周期间隔',
    `start_date`    varchar(50)    not null COMMENT '开始日期，使用UTC时间，毫秒值',
    `end_date`      varchar(50)    null COMMENT '结束日期，使用UTC时间，毫秒值',
    `category_id`   varchar(50)    not null COMMENT '类别id',
    `date_created`  varchar(50)    not null COMMENT '条目创建日期，使用UTC时间，毫秒值',
    `date_modified` varchar(50)    not null COMMENT '最后一次更新日期，使用UTC时间，毫秒值',
    `next_date`     varchar(50)    not null COMMENT '下次发生日期，使用UTC时间，毫秒值',
    `summary`       varchar(200)   not null COMMENT '总结：例：每个月的16号',
    `total_amount`  decimal(20, 2) COMMENT '该周期性账目一共产生的数额',
    activate_status bit default b'1' not null comment '订阅状态 0 - 已结束或取消 1 - 正在进行',
    primary key (`id`)
) engine = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `cyberbook_user`;
create table cyberbook_user
(
    `id`                varchar(50) not null COMMENT '用户id',
    `username`          varchar(225) COMMENT '用户名',
    `password`          varchar(225) COMMENT '密码',
    `email`             varchar(225) COMMENT '登录邮箱',
    `gender`            integer default 0 COMMENT '性别：0 - 保密，1 - 男， 2 - 女',
    `birthday`          varchar(50) COMMENT '生日, MM/DD/YYYY',
    `registered`        bit     not null default false COMMENT '是否已注册',
    `profile_photo_url` varchar(500) COMMENT '头像url',
    `date_registered`   varchar(50)      not null COMMENT '注册日期，使用UTC时间，毫秒值',
    primary key (`id`)
) engine = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `user_roles`;
create table user_roles
(
    user_id varchar(50)  not null,
    roles   int          null,
    constraint FKm2k7ukjafxy6knbm70nrdn49r
        foreign key (user_id) references cyberbook_user (id)
) engine = InnoDB
  DEFAULT CHARSET = utf8;
