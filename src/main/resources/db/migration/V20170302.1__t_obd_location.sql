CREATE TABLE t_obd_location
(
   id              INT         AUTO_INCREMENT COMMENT '主键',
   obdCode         VARCHAR(20) COMMENT 'OBD编号',
   vin             VARCHAR(20) COMMENT '发动机标识',
   longitude       VARCHAR(20) COMMENT '定位时经度',
   latitude        VARCHAR(20) COMMENT '定位时纬度',
   locationTime    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '定位时间',
   PRIMARY KEY (id)
)DEFAULT CHARSET=utf8  COMMENT = '账户表' ;