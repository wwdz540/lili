package io.renren.modules.crm.controller;

import io.renren.common.utils.CommonUtil;
import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.ITransDataService;
import io.renren.modules.crm.service.NewMerchInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class BaseController {

    @Autowired
    private NewMerchInfoService newMerchInfoService;
    @Autowired
    private Map<String,ITransDataService>  serviceMap;



    protected void fixParas(Map params){
       // NewMerchInfoEntity merchInfo = getCurrentMerch();

        if(getCurrentMerch().getId()!=1){
            String currentPath = getCurrentPath();
            Object path = params.get("path");
            if(path==null){
                params.put("path",getCurrentPath());
            }else{
                if(!path.toString().startsWith(currentPath)){
                    params.put("path",currentPath);
                }
            }
        }

    }

    protected String getCurrentPath(){
        return getCurrentMerch().getPath();
    }

    protected NewMerchInfoEntity getCurrentMerch(){
        long  deptId = CommonUtil.getUser().getDeptId();
        NewMerchInfoEntity merchInfo = newMerchInfoService.findOne(deptId);
        return merchInfo;
    }


    protected ITransDataService getService(Map<String,Object> params){
        return serviceMap.get(params.get("ds")); //获取数据源，富友或盛富通的
    }

}
