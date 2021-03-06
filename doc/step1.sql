
create table merchant_main /** 商户请表*/
(
	mc_id bigint auto_increment
		primary key,
	parent_id bigint null comment '上级商户ID，一级商户为0',
	name varchar(50) null comment '部门名称',
	order_num int null comment '排序',
	del_flag tinyint default '0' null comment '是否删除  -1：已删除  0：正常',
	dept_type int default '1' null,
	legal_name varchar(64) null,
	mobile varchar(64) null,
	address varchar(128) null,
	industry varchar(64) null,
	path varchar(100) null
)
comment '商户请表'
;
ALTER TABLE sys_user ADD mc_id int NULL;

--ALTER TABLE sys_user
--ADD CONSTRAINT sys_user_merchant_main_mc_id_fk
--FOREIGN KEY (mc_id) REFERENCES merchant_main (mc_id);



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




INSERT INTO merchant_main (mc_id, parent_id, name, order_num, del_flag, dept_type, legal_name, mobile, address, industry, path) VALUES (1, 0, '力力数据', 9999999, 0, 0, null, null, null, null, '00000000');


delete from QRTZ_CRON_TRIGGERS;
delete from QRTZ_TRIGGERS;
delete from QRTZ_JOB_DETAILS;


###创建菜单
INSERT INTO sys_menu (menu_id, parent_id, name, url, perms, type, icon, order_num)
VALUES
  (90, 0, '商户管理', null, null, 0, 'fa fa-cog', 2),
  (91, 0, '新交易管理', null, null, 0, 'fa fa-cog', 2),
  (92, 0, '图表分析', null, null, 0, 'fa fa-cog', 2),

  (104, 90, '商户管理', 'modules/crm/newmerchinfo.html', 'merch:all', 1, 'fa fa-file-text-o', 1),
  (105, 91, '新交易记录', 'modules/crm/trans_datas.html', null, 1, 'fa fa-file-text-o', 1),
  (106, 91, '新分润列表', 'modules/crm/statistic.html', null, 1, 'fa fa-file-text-o', 1),
  (107, 92, '交易统计', 'modules/crm/charts.html', null, 1, 'fa fa-file-text-o', 1),
  (108, 92, '分润图表', 'modules/crm/sharepoint_charts.html', null, 1, 'fa fa-file-text-o', 1),
  (109, 92, '支付方式图表', 'modules/crm/paytype_charts.html', null, 1, 'fa fa-file-text-o', 1);


##创建角色

INSERT INTO sys_role (role_id, role_name, remark, create_time)
VALUES
  (10, '新代理商', null, now()),
  (11, '普通商户', null, now());

-- 创建角色与菜单对应关系
-- 2,15,16,17,18 管理员
INSERT INTO sys_role_menu ( role_id, menu_id)
VALUES
  (10, 1),
  (10, 2),
  (10, 15),
  (10, 16),
  (10, 17),
  (10, 18),
  (10, 90),
  (10, 91),
  (10, 92),
  (10, 104),
  (10, 105),
  (10, 106),
  (10, 107),
  (10, 108),
  (10, 109),
  (11, 91),
  (11, 92),
  (11, 105),
  (11, 107),
  (11, 109)
;


-- /**超级管理员*/
INSERT INTO sys_user (user_id, username, password, salt, email, mobile, status, mc_id, create_time, rate, leader)
VALUES (300, 'lili', 'eff23e3a161934f8a1a280ff2cf3f4aa8a54e9458c2197f809ec21eb8bd171d0', 'FGHufcChcebZNJi2yeUu', '461892464@qq.com', '18661203323', 1, 1, '2018-12-16 02:19:14', null, 1);
INSERT INTO sys_user_role ( user_id, role_id) VALUES ( 300, 10);
INSERT INTO sys_user_role ( user_id, role_id) VALUES ( 300, 11);
commit ;