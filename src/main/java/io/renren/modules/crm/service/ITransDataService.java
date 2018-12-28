package io.renren.modules.crm.service;

import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.sys.entity.SysUserEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ITransDataService {
    String QUERY_PAY_TYPE="payType";

    List<TransDataEntity> queryList(Map<String, Object> params);


    int queryTotal(Map<String, Object> params);

     GroupQuery createGroupQuery(Map<String, Object> params);

   // public <T> List<T> queryByGroup(Map<String, Object> params, Function<Map,T> function);

    interface GroupQuery{
        GroupQuery group(String field);
        GroupQuery group(String field, String name);

        GroupQuery collection(String funcation);
        GroupQuery collection(String funcation, String field);
        GroupQuery collection(String funcation, String field, String alienFileName);
        /***
         * 要据金额汇总
         * @return
         */
        GroupQuery collection4Amt(String funcation, String alien);

        /**
         * 根据分润汇总
         * @param funcation
         * @return
         */
        GroupQuery collection4Share(String funcation, String alien);


        List<Map<String,Object>> query();
        Map<String,Object> querySingleMap();
        Object querySingleObject();
    }
}
