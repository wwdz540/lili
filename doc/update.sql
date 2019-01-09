ALTER TABLE merch_info ADD mc_id int NULL;


ALTER TABLE trans_data ADD share_benefit DECIMAL(16,2);
ALTER TABLE fy_trans_data ADD share_benefit DECIMAL(16,2);


ALTER TABLE merch_info
ADD CONSTRAINT merch_info_merchant_main__fk
FOREIGN KEY (mc_id) REFERENCES merchant_main (mc_id);

CREATE TABLE rate_config
(
  id int PRIMARY KEY AUTO_INCREMENT,
  mc_id bigint(20) NOT NULL COMMENT '商铺id(商铺表复用了部门表)',
  pay_type varchar(20) NOT NULL COMMENT '支付方式',
  rate float COMMENT '费率',
  max float COMMENT '最高',
  share_benefit float COMMENT '分润',
  CONSTRAINT rate_config_merchant_main_mc_id_fk FOREIGN KEY (mc_id) REFERENCES merchant_main (mc_id)
);
CREATE UNIQUE INDEX rate_config_mc_id_pay_type_uindex ON rate_config (mc_id, pay_type);
ALTER TABLE rate_config COMMENT = '费率设置表';