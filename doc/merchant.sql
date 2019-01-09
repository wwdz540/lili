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

