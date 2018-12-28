package io.renren.modules.crm.controller;

import io.renren.common.utils.CommonUtil;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.NewMerchInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class BaseController {

    @Autowired
    private NewMerchInfoService newMerchInfoService;


    protected void fixParas(Map params){
        NewMerchInfoEntity merchInfo = getCurrentMerch();
        if(merchInfo.getId() != 1){
            params.put("path",merchInfo.getPath());
        }
    }

    NewMerchInfoEntity getCurrentMerch(){
        long  deptId = CommonUtil.getUser().getDeptId();
        NewMerchInfoEntity merchInfo = newMerchInfoService.findOne(deptId);
        return merchInfo;
    }
}
