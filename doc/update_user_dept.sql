update sys_user as u set u.mc_id = (select min( m.mc_id) from  merch_info m where m.merchno =u.username )
where exists(select * from merch_info m where m.merchno = u.username);


update sys_user_role set role_id=11 where role_id=8
