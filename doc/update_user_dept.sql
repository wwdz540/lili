update sys_user as u set u.dept_id = (select min( m.dept_id) from  merch_info m where m.merchno =u.username )
where u.dept_id = 10

