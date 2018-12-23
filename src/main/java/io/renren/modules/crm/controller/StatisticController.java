package io.renren.modules.crm.controller;

import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.NewMerchInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/***
 * @Author wangzhipig
 * 这个添是我新加的，用来做查询分析之用。若用mybitis 做查询分析实在太浪时间，也不能发挥mybatis的优势
 */
@RestController
@RequestMapping("/crm/statistic")
public class StatisticController  {

    private static final String GREP_SQL = "select  %s from sys_dept as dept\n" +
            "  left join merch_info  as m on m.dept_id = dept.dept_id\n" +
            "  left join trans_data as td   on td.merchantId = m.merchno \n" +
            " where %s \n"
            ;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NewMerchInfoService merchInfoService;

    @RequestMapping("/list")
    public List list(){
        return query("count(td.amt) as count,FORMAT(sum(td.amt)/100,2) as sum ,FORMAT(avg(td.amt)/100,2) as avg");
    }
    
/*
    private List<NewMerchInfoEntity> groupQuery(){
        Map<String,Object> params = new HashMap<>();
        params.put("deptType",1);
        List<NewMerchInfoEntity>  list = merchInfoService.queryList(params);
        
        //Map<String,Object> params = new HashMap<>();
        params.put("deptType",2);
        List<NewMerchInfoEntity> list1 = merchInfoService.queryList(params);
        list.addAll(list1);
        
        return list;
    }
*/

    private List query(String groupField){
      //  * 商铺分组 ，直属商铺 deptType = 1 *
        String directGroup = getSql(" m.dept_id as deptId " +
                        ", dept.name as name,1 as dtype," +groupField
                , " dept.dept_type=1"
                ,"dept.dept_id;");

        System.out.println(directGroup);

        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(directGroup);
      //  *代理商*
        String agencyGroup = getSql("dept.parent_id, (select d.name from sys_dept as d where d.dept_id= dept.parent_id) as name, 2 as dtype ,"+ groupField,
                "dept.dept_type=4 or dept.dept_type=5",
                "dept.parent_id");
        System.out.println(agencyGroup);
        List<Map<String, Object>> list2 = jdbcTemplate.queryForList(agencyGroup);
        list2.addAll(list1);
        return  list2;
    }

    private String getSql(String fields,String where,String group){
        String result = String.format(GREP_SQL,fields,where);
        if(StringUtils.isNotBlank(group)){
            result += "  group by %s\n";
        }
        return  result;
    }
}
