package io.renren.modules.crm.utils;

import org.apache.commons.lang.StringUtils;

public class TypeUtils {

    public static String cardType(String code){
        if (StringUtils.isBlank(code)){
            return "其他";
        }
        switch (code){
            case "OA" :
                return "扫码";
            case "CC" :
                return "贷记卡";
            case "DC" :
                return "借记卡";
            case "SCC" :
                return "准贷记卡";
        }
        return "其他";
    }

    public static String payType(String code){
        if (StringUtils.isBlank(code)){
            return "其他";
        }
        switch (code){
            case "WX" :
                return "微信";
            case "ZFB01" :
                return "支付宝";
            case "SFT01" :
                return "盛付通";
            case "CUP" :
                return "银行卡";
        }
        return "其他";
    }
}
