package io.renren.modules.crm.service.impl;

import io.renren.common.utils.CommonUtil;
import io.renren.modules.crm.dao.ITransDataDao;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import io.renren.modules.sys.dao.SysDeptDao;
import io.renren.modules.sys.entity.SysDeptEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTransDataServerImpl implements ITransDataService {

    @Autowired
    private SysDeptDao sysDeptDao;

    @Override
    public List<TransDataEntity> queryList(Map<String, Object> params) {
        fixQueryParam(params);
        List<TransDataEntity> list = getDao().queryList(params);
        list.forEach(this::fixTheResult);
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        fixQueryParam(params);
        return getDao().queryTotal(params);
    }

    @Override
    public TransDataEntity queryById(Long id){
        TransDataEntity td = getDao().queryObject(id);
        fixResult(td);
        return td;
    }


    @Override
    public GroupQuery createGroupQuery(final Map<String,Object> params) {
        fixQueryParam(params);
        return new GroupQuery() {

            Map<String,String> fieldName = new HashMap<>();
            Map<String,String> groupName = new HashMap<>();

            @Override
            public GroupQuery group(String field) {
                return this.group(field,field);
            }

            @Override
            public GroupQuery group(String field, String name) {
                fieldName.put(field,name);
                return this;
            }

            @Override
            public GroupQuery collection(String funcation) {
                return this.collection(funcation,"1");
            }

            @Override
            public GroupQuery collection(String funcation, String field) {
                return this.collection(funcation,field,funcation);
            }

            @Override
            public GroupQuery collection(String funcation, String field, String alienFileName) {
                 groupName.put(funcation+"("+field+")",alienFileName);
                 return this;
            }


            @Override
            public List<Map<String, Object>> query() {

                String f = StringUtils.join(fieldName.keySet(),",");
                String g = StringUtils.join(groupName.keySet(),",");

                params.put("gfield",f);
                params.put("collection",g);

                List<Map<String,Object>> list = getDao().queryByGroup(params);
                return list.stream().filter(m->m != null).map(m -> {
                    Map<String,Object> mm = new HashMap<>();

                    for(Map.Entry<String,String> e:fieldName.entrySet()){
                            mm.put(e.getValue(),m.get(e.getKey()));
                    }
                    for(Map.Entry<String,String> e:groupName.entrySet()){
                            mm.put(e.getValue(),m.get(e.getKey()));
                    }
                    return mm;
                }).collect(Collectors.toList());


            }
            public  GroupQuery collection4Amt(String funcation, String alien){
                this.collection(funcation,getField("amt"),alien);
                return this;
            }

            @Override
            public GroupQuery collection4Share(String funcation, String alien) {
                this.collection(funcation,"share_benefit",alien);
                return this;
            }


            public Map<String,Object> querySingleMap(){
                List<Map<String,Object>> list = this.query();
                if(list.size()>0){
                    return list.get(0);
                }
                return null;
           }
           public Object querySingleObject(){
                Map<String,Object> map = querySingleMap();
                Object result =0;
                if(map!=null){
                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        result = e.getValue();
                        break;
                    }
                }
                return result;
           }
        };
    }

    @Override
    public void updateSharePoint(Long id,double sharePoint){
        getDao().updateShareBenefit(id,sharePoint);
    }


    private void fixTheResult(TransDataEntity result){
        fixResult(result);
        result.setAmt(CommonUtil.formatAB(result.getAmt()));
        if(result.getShareBenefit()!=null) {

            result.setServiceCharge(CommonUtil.fixFeng2Yuan(result.getShareBenefit()));
               // result.setShareBenefit(result.getShareBenefit() / 100);

        }
        if(result.getServiceCharge() != 0) {
            result.setServiceCharge(CommonUtil.fixFeng2Yuan(result.getServiceCharge()));
              //  result.setServiceCharge(result.getServiceCharge() / 100);

        }
        SysDeptEntity mc = sysDeptDao.queryObject(result.getParentMc());
        if(mc != null)
        result.setAgencyName(mc.getName());
       // System.out.println(result.getParentMc());
    }


    protected abstract void fixQueryParam(Map<String, Object> params);
    protected abstract void fixResult(TransDataEntity result);
    protected abstract ITransDataDao getDao();

    protected abstract String getField(String field);
}
