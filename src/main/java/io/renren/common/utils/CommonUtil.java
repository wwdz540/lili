package io.renren.common.utils;

import com.alibaba.fastjson.JSON;
import io.renren.modules.crm.utils.TypeUtils;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysDeptService;
import org.apache.shiro.SecurityUtils;

import java.math.BigDecimal;
import java.util.List;

public class CommonUtil {

    private static SysDeptService sysDeptService;

    public static void setSysDeptService(SysDeptService sysDeptService) {
        CommonUtil.sysDeptService = sysDeptService;
    }

    /***
     * 把分转换成元
     * @param obj
     * @return
     */
    public static String formatAB(Object obj){ //@TODO 方法名待重构
        if(obj == null) return "0";
        BigDecimal b = new BigDecimal(obj.toString());
        BigDecimal result = b.divide(BigDecimal.valueOf(100l),2,BigDecimal.ROUND_HALF_UP);
        return result.toString();
    }
    public static BigDecimal sharePoint(BigDecimal amt, String cardTypeCode){
        return sharePoint(amt,cardTypeCode,null);
    }

    public static BigDecimal sharePoint(BigDecimal amt,String cardTypeCode,SysUserEntity userEntity){
        BigDecimal sharePoint = BigDecimal.ZERO;

        String cardType = TypeUtils.cardType(cardTypeCode);
        if ("其他".equals(cardType)){
            return sharePoint;
        }

        RateModel model  = getRateMap(cardType,userEntity);
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

    public static RateModel getRateMap(String cardType,SysUserEntity userEntity){
        String rateJosn = getUser().getRate();
        if (userEntity != null){
            rateJosn = userEntity.getRate();
        }
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

    public static boolean isAdmin(){
        return getUser().getMcId() == 1;
    }

    public static boolean isDL(){
        int  type = sysDeptService.queryObject(getUser().getMcId()).getDeptType() ;
        return type == 2;
    }

    public static boolean isMerch(){
       int  type = sysDeptService.queryObject(getUser().getMcId()).getDeptType() ;
        //return getUser().getMcId() == 10;
        return type ==1
                || type==4
                || type==5
                || type==3;
    }
}
