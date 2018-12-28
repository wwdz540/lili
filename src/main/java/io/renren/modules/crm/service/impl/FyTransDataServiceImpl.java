package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.FyTransDataDao;
import io.renren.modules.crm.dao.ITransDataDao;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;



@Service("fy")
public class FyTransDataServiceImpl extends AbtractTransDataServerImpl
        implements ITransDataService {
    @Autowired
    private FyTransDataDao fyTransDataDao;

    protected void fixQueryParam(Map<String, Object> params){
        if(params.containsKey(QUERY_PAY_TYPE)){
            String payType = params.get(QUERY_PAY_TYPE).toString();
            params.remove(QUERY_PAY_TYPE);
            switch (payType){
                case "微信":
                    params.put("pay_type","1");
                    break;
                case "支付宝":
                    params.put("pay_type","2");
                    break;
                case "借记卡":
                    params.put("cardType","01");
                    break;
                case "贷记卡":
                    params.put("cardType","02");
                    break;
            }
        }
    }
    protected void fixResult(TransDataEntity result){
    }
    protected ITransDataDao getDao(){
        return fyTransDataDao;
    }


    @Override
    protected String getField(String field) {
        switch (field){
            case "amt":
                return "total_fee";
               // break;
        }
        throw new RuntimeException("没有做好映射");
    }
}
