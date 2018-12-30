package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.ITransDataDao;
import io.renren.modules.crm.dao.SfTransDataDao;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import io.renren.modules.crm.utils.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sf")
public class SfTransDataServiceImpl extends AbstractTransDataServerImpl
        implements ITransDataService {
    @Autowired
    private SfTransDataDao  transDataDao;

    /***
     * @param params
     */
    protected void fixQueryParam(Map<String, Object> params){
        if(params.containsKey(QUERY_PAY_TYPE)){
            String payType= params.get(QUERY_PAY_TYPE).toString();
            params.remove(QUERY_PAY_TYPE);
            switch (payType){
                case "微信":
                    params.put("issuerCode","wx");
                    break;
                case "支付宝":
                    params.put("issuerCode","ZFB01");
                    break;
                case "借记卡":
                    params.put("cardType","DC");
                    break;
                case "贷记卡":
                    params.put("cardType","CC");
                    break;
            }

        }

    }
    protected void fixResult(TransDataEntity entity){
        entity.setCardType(TypeUtils.cardType(entity.getCardType()));
        entity.setIssuerCode(TypeUtils.payType(entity.getIssuerCode()));
    }
    protected ITransDataDao getDao(){
        return transDataDao;
    }

    @Override
    protected String getField(String field) {
        return field;
    }
}
