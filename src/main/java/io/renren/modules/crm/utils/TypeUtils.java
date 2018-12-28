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


    public static String fyCardType(String code){
        if (StringUtils.isEmpty(code)){ return "扫码"; }
        switch (code){
            case "01" : return "借记卡";
            case "02" : return "贷记卡";
            case "03" : return "准贷记卡";
            case "04" : return "预付卡";
        }
        return "其他";
    }

    public static String fyPayType(String code){
        if (StringUtils.isEmpty(code)){ return "未知"; }
        switch (code){
            case "1" : return "微信";
            case "2" : return "支付宝";
            case "3" : return "银行卡";
            case "4" : return "现金";
            case "5" : return "无卡支付";
            case "6" : return "qq钱包";
            case "7" : return "百度钱包";
            case "8" : return "京东钱包";
        }
        return "未知"; }
}
