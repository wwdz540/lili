package io.renren;

import com.alibaba.fastjson.JSON;
import io.renren.common.utils.CommonUtil;
import io.renren.common.utils.RateModel;
import io.renren.modules.api.service.UserService;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import io.renren.modules.crm.service.TransDataService;
import io.renren.modules.crm.service.impl.ShareTransDataServerImpl;
import io.renren.modules.crm.utils.TypeUtils;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShareTransDataTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private Map<String,ITransDataService> serviceMap;
    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void fixSharePoint(){
        ITransDataService dataService  =  serviceMap.get("fy");
        List<TransDataEntity> transDatas = dataService.queryList(new HashMap<>());

        for (TransDataEntity transData : transDatas) {
            BigDecimal amt =  new BigDecimal(transData.getAmt());
            Double sharePoint = sharePoint(amt,transData.getCardType()).doubleValue();
            Double serverCharge = computeMoney(amt.doubleValue(),transData.getCardType());
            String sql = "update fy_trans_data set share_benefit = ? , service_charge = ? where id = ? ";
            jdbcTemplate.update(sql,sharePoint,serverCharge,transData.getId());
        }

        dataService  =  serviceMap.get("sf");
        transDatas = dataService.queryList(new HashMap<>());

        for (TransDataEntity transData : transDatas) {
            BigDecimal amt =  new BigDecimal(transData.getAmt());
            Double sharePoint = sharePoint(amt,transData.getCardType()).doubleValue();
            Double serverCharge = computeMoney(amt.doubleValue(),transData.getCardType());
            String sql = "update trans_data set share_benefit = ? , service_charge = ? where id = ? ";
            jdbcTemplate.update(sql,sharePoint,serverCharge,transData.getId());
        }
    }

    public static BigDecimal sharePoint(BigDecimal amt,String cardType){
        BigDecimal sharePoint = BigDecimal.ZERO;

       // String cardType = TypeUtils.cardType(cardTypeCode);
        if ("其他".equals(cardType)){
            return sharePoint;
        }

        RateModel model  = getRateMap(cardType);

        if (model == null){
            return sharePoint;
        }

        if ("费率".equals(model.getType())){
            sharePoint = amt.multiply(model.getRate());
            if (BigDecimal.ZERO.compareTo(model.getMax()) != 0 && sharePoint.compareTo(model.getMax()) >0){
                sharePoint = model.getMax();
            }

        }

        if ("每笔".equals(model.getType())){
            sharePoint = model.getMin();
        }
        sharePoint = sharePoint.setScale(2, BigDecimal.ROUND_HALF_UP);
        return sharePoint;
    }

    public static RateModel getRateMap(String cardType){
        System.out.println(cardType);
        String rateJosn = "[{\"cardType\":\"扫码\",\"type\":\"费率\",\"rate\":0.00048,\"min\":0,\"max\":0},{\"cardType\":\"借记卡\",\"type\":\"每笔\",\"rate\":0.,\"min\":0.8,\"max\":0.8},{\"cardType\":\"贷记卡\",\"type\":\"费率\",\"rate\":0.00048,\"min\":0,\"max\":0},{\"cardType\":\"准贷记卡\",\"type\":\"费率\",\"rate\":0.00048,\"min\":0,\"max\":0}]";
        List<RateModel> modelList = JSON.parseArray(rateJosn,RateModel.class);
        if (modelList == null || modelList.size() == 0){
            return null;
        }
        for(RateModel model : modelList){
            if (model.getCardType().equals(cardType)){
                return model;
            }
        }
        return null;
    }

    public static SysUserEntity getUser(){
        SysUserEntity userEntity =  (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        return userEntity;
    }

    /**
     * 手续费计算
     * @param money
     * @param cardType
     * @return
     */
    private Double computeMoney(Double money, String cardType){
        //判断支付类型
        Double chargeMoney = 0D;
        if ("贷记卡".equals(cardType)){
            chargeMoney = money / 1000 * 6 ;
            return chargeMoney;
        }

        if ("借记卡".equals(cardType)){
            chargeMoney = money / 1000 * 5 ;
            if (chargeMoney.compareTo(Double.valueOf(25)) >0){
                return Double.valueOf(25);
            }else{
                return chargeMoney;
            }
        }

        if ("扫码".equals(cardType)){
            chargeMoney = money / 1000 * 3.8 ;
            return chargeMoney;
        }

        return chargeMoney;
    }

    //@Test
   public void test1() throws ShareTransDataServerImpl.SharePointException {
        ITransDataService dataService = serviceMap.get("fy");

        ShareTransDataServerImpl sd = new ShareTransDataServerImpl();
        sd.setJdbcTemplate(jdbcTemplate);
        sd.setTransDataService(dataService);

        TransDataEntity item = dataService.queryById(13924l);
        sd.fixSharePoint(item);



   }


}
