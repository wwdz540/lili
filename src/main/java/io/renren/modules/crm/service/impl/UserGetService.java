package io.renren.modules.crm.service.impl;

import io.renren.common.utils.Constant;
import io.renren.modules.sys.dao.SysUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: ufo Copyright (C) 2018.
 *
 * @Description:
 * @author: zhuhui
 * @Date: 2018-05-28 下午10:08
 * @version:
 */
@Component
public class UserGetService {

    @Autowired
    private SysUserDao sysUserDao;

    public List<Long> getUserId(Long leader){
        List<Long> userIdList = sysUserDao.queryByLeader(leader);
        if (CollectionUtils.isEmpty(userIdList)) {
            userIdList = new ArrayList<>();
        }
        userIdList.add(leader);
        return userIdList;
    }

    public Long getUser(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }
        return sysUserDao.queryByUserName(name).getUserId();
    }
}
