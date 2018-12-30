package io.renren.modules.crm.service.impl;

import io.renren.modules.crm.dao.ITransDataDao;
import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTransDataServerImpl implements ITransDataService {

    @Override
    public List<TransDataEntity> queryList(Map<String, Object> params) {
        fixQueryParam(params);
        List<TransDataEntity> list = getDao().queryList(params);
        list.forEach(this::fixResult);
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        fixQueryParam(params);
        return getDao().queryTotal(params);
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

    //    public <T> List queryByGroup(Map<String, Object> params, Function<Map,T> function){
//        fixQueryParam(params);
//        List<Map<String,Object>> list = getDao().queryByGroup(params);
//        if(function == null) return list;
//        return list
//                .stream()
//                .map(function)
//                .collect(Collectors.toList());
//    }

    protected abstract void fixQueryParam(Map<String, Object> params);
    protected abstract void fixResult(TransDataEntity result);
    protected abstract ITransDataDao getDao();

    protected abstract String getField(String field);
}
