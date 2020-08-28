use cyberbook_test;

DROP TABLE IF EXISTS `cyberbook_category`;
CREATE TABLE `cyberbook_category`
(
    `id`            varchar(50) NOT NULL COMMENT '类别id',
    `user_id`       varchar(50) NOT NULL COMMENT '用户id',
    `name`          varchar(50) NOT NULL COMMENT '类别名称',
    `icon`          varchar(50) NOT NULL COMMENT '图标名称',
    `color`         varchar(20) NOT NULL COMMENT '饼状图中的颜色',
    `is_spend`      boolean     NOT NULL COMMENT '是否是支出',
    `sort_order`    int(11)     NOT NULL COMMENT '排序位置',
    `added_by_user` boolean     NOT NULL COMMENT '是否是用户自己添加的类别',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `cyberbook_transaction`;
create table cyberbook_transaction
(
    `id`               varchar(50)    not null COMMENT '类别id',
    `amount`           decimal(20, 2) not null COMMENT '数额',
    `category_id`      varchar(50)    not null COMMENT '类别id',
    `date_created`     bigint         not null COMMENT '条目创建日期，使用UTC时间，毫秒值',
    `date_modified`    bigint         not null COMMENT '最后一次更新日期，使用UTC时间，毫秒值',
    `description`      varchar(50)    not null COMMENT '描述',
    `subscription_id`  varchar(50) COMMENT '周期性账目id',
    `transaction_date` bigint         not null COMMENT '账目发生日期，使用UTC时间，毫秒值',
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
    `description`   varchar(50) COMMENT '描述',
    `frequency`     int            not null COMMENT '频率：0 - 天，1 - 周，2 - 月， 3 - 年',
    `interval`      int            not null COMMENT '周期间隔',
    `start_date`    bigint         not null COMMENT '开始日期，使用UTC时间，毫秒值',
    `end_date`      bigint COMMENT '结束日期，使用UTC时间，毫秒值',
    `category_id`   varchar(50)    not null COMMENT '类别id',
    `date_created`  bigint         not null COMMENT '条目创建日期，使用UTC时间，毫秒值',
    `date_modified` bigint         not null COMMENT '最后一次更新日期，使用UTC时间，毫秒值',
    `nextDate`      bigint         not null COMMENT '下次发生日期，使用UTC时间，毫秒值',
    `summary`       varchar(50)    not null COMMENT '总结：例：每个月的16号',
    `totalAmount`   decimal(20, 2) COMMENT '该周期性账目一共产生的数额',
    primary key (`id`)
) engine = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `cyberbook_user`;
create table cyberbook_user
(
    `id`                varchar(50) not null COMMENT '用户id',
    `username`          varchar(50) COMMENT '用户名',
    `password`          varchar(50) COMMENT '密码',
    `email`             varchar(50) COMMENT '登录邮箱',
    `gender`            integer COMMENT '性别：0 - 未知，1 - 男， 2 - 女',
    `birthday`          varchar(50) COMMENT '生日, MM/DD/YYYY',
    `registered`        boolean     not null default false COMMENT '是否已注册',
    `profile_photo_url` varchar(500) COMMENT '头像url',
    `date_registered`   bigint      not null COMMENT '注册日期，使用UTC时间，毫秒值',
    primary key (`id`)
) engine = InnoDB
  DEFAULT CHARSET = utf8;

alter table user_roles add constraint FKm2k7ukjafxy6knbm70nrdn49r foreign key (user_id) references cyberbook_user (id)