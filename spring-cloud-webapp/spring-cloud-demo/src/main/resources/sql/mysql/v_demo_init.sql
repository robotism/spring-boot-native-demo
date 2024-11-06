

CREATE TABLE IF NOT EXISTS t_worker_node1
(
    `id`           INT UNSIGNED AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY COMMENT '节点id',
    `port`         INT UNSIGNED                       NOT NULL COMMENT '端口',
    `os_name`      VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统名称',
    `os_arch`      VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统平台',
    `os_version`   VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统版本',
    `host_name`    VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '主机名称',
    `docker`       BOOLEAN                            NOT NULL DEFAULT FALSE COMMENT 'IP地址',
    `ip`           VARCHAR(16)                        NOT NULL DEFAULT '' COMMENT 'IP地址',
    `mac`          VARCHAR(16)                        NOT NULL DEFAULT '' COMMENT 'MAC地址',
    `tag`          VARCHAR(32) UNIQUE                 NOT NULL DEFAULT '' COMMENT '节点标签',
    `desc`         VARCHAR(1024)                      NOT NULL DEFAULT '' COMMENT '节点描述',
    `time_created` TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `time_updated` TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = 'GUID工作节点';

CREATE TABLE IF NOT EXISTS t_worker_node2
(
    `id`           INT UNSIGNED AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY COMMENT '节点id',
    `port`         INT UNSIGNED                       NOT NULL COMMENT '端口',
    `os_name`      VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统名称',
    `os_arch`      VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统平台',
    `os_version`   VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统版本',
    `host_name`    VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '主机名称',
    `docker`       BOOLEAN                            NOT NULL DEFAULT FALSE COMMENT 'IP地址',
    `ip`           VARCHAR(16)                        NOT NULL DEFAULT '' COMMENT 'IP地址',
    `mac`          VARCHAR(16)                        NOT NULL DEFAULT '' COMMENT 'MAC地址',
    `tag`          VARCHAR(32) UNIQUE                 NOT NULL DEFAULT '' COMMENT '节点标签',
    `desc`         VARCHAR(1024)                      NOT NULL DEFAULT '' COMMENT '节点描述',
    `time_created` TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `time_updated` TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = 'GUID工作节点';

CREATE TABLE IF NOT EXISTS t_worker_node3
(
    `id`           INT UNSIGNED AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY COMMENT '节点id',
    `port`         INT UNSIGNED                       NOT NULL COMMENT '端口',
    `os_name`      VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统名称',
    `os_arch`      VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统平台',
    `os_version`   VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '系统版本',
    `host_name`    VARCHAR(128)                       NOT NULL DEFAULT '' COMMENT '主机名称',
    `docker`       BOOLEAN                            NOT NULL DEFAULT FALSE COMMENT 'IP地址',
    `ip`           VARCHAR(16)                        NOT NULL DEFAULT '' COMMENT 'IP地址',
    `mac`          VARCHAR(16)                        NOT NULL DEFAULT '' COMMENT 'MAC地址',
    `tag`          VARCHAR(32) UNIQUE                 NOT NULL DEFAULT '' COMMENT '节点标签',
    `desc`         VARCHAR(1024)                      NOT NULL DEFAULT '' COMMENT '节点描述',
    `time_created` TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `time_updated` TIMESTAMP                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE = INNODB
    DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = 'GUID工作节点';
