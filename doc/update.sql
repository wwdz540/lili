ALTER TABLE merch_info ADD dept_id int NULL;
ALTER TABLE sys_dept ADD dept_type int DEFAULT 1 NULL;
ALTER TABLE sys_dept ADD legal_name varchar(64);
ALTER TABLE sys_dept ADD mobile varchar(64);
ALTER TABLE sys_dept ADD address varchar(128);
ALTER TABLE sys_dept ADD industry varchar(64);


ALTER TABLE merch_info
ADD CONSTRAINT merch_info_sys_dept__fk
FOREIGN KEY (dept_id) REFERENCES sys_dept (dept_id);

CREATE TABLE rate_config
(
  id int PRIMARY KEY AUTO_INCREMENT,
  dept_id bigint(20) NOT NULL COMMENT '商铺id(商铺表复用了部门表)',
  pay_type varchar(20) NOT NULL COMMENT '支付方式',
  rate float COMMENT '费率',
  max float COMMENT '最高',
  share_benefit float COMMENT '最低',
  CONSTRAINT rate_config_sys_dept_dept_id_fk FOREIGN KEY (dept_id) REFERENCES sys_dept (dept_id)
);
CREATE UNIQUE INDEX rate_config_dept_id_pay_type_uindex ON rate_config (dept_id, pay_type);
ALTER TABLE rate_config COMMENT = '费率设置表';




