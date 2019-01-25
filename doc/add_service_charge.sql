
--添加费率
alter table fy_trans_data
add column td_rate decimal(16,5);
--添加手续费
alter table fy_trans_data
add column service_charge decimal(16,2)


--添加费率
alter table trans_data
add column td_rate decimal(16,5);
--添加手续费
alter table trans_data
add column service_charge decimal(16,2)

