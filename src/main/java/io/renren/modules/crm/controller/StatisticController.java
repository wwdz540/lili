package io.renren.modules.crm.controller;

import io.renren.modules.crm.entity.NewMerchInfoEntity;
import io.renren.modules.crm.service.NewMerchInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
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

    //private static final

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NewMerchInfoService merchInfoService;

    @RequestMapping("/list")
    public List list(){
        return query("count(td.amt) as count,sum(td.amt)/100 as sum ,avg(td.amt)/100 as avg");
    }






    private List query(String groupField){

        String agencyGroup = getSql("dept.parent_id, (select d.name from sys_dept as d where d.dept_id= dept.parent_id) as name, 2 as dtype ,"+ groupField,
                "dept.dept_type=4 or dept.dept_type=5",
                "dept.parent_id");
        System.out.println(agencyGroup);
        List<Map<String, Object>> list2 = jdbcTemplate.query(agencyGroup, new MyColumnMapRowMapper());
        return  list2;
    }

    private String getSql(String fields,String where,String group){
        String result = String.format(GREP_SQL,fields,where);
        if(StringUtils.isNotBlank(group)){
            result += "  group by "+group;
        }
        return  result;
    }


    protected static class MyColumnMapRowMapper implements RowMapper<Map<String, Object>> {

        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            Map<String, Object> mapOfColValues = createColumnMap(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
                Object obj = getColumnValue(rs, i);

               // System.out.println(obj.getClass());

                mapOfColValues.put(key, obj);
            }
            return mapOfColValues;
        }

        /**
         * Create a Map instance to be used as column map.
         * <p>By default, a linked case-insensitive Map will be created.
         * @param columnCount the column count, to be used as initial
         * capacity for the Map
         * @return the new Map instance
         * @see org.springframework.util.LinkedCaseInsensitiveMap
         */
        protected Map<String, Object> createColumnMap(int columnCount) {
            return new LinkedCaseInsensitiveMap<Object>(columnCount);
        }

        /**
         * Determine the key to use for the given column in the column Map.
         * @param columnName the column name as returned by the ResultSet
         * @return the column key to use
         * @see java.sql.ResultSetMetaData#getColumnName
         */
        protected String getColumnKey(String columnName) {
            return columnName;
        }

        /**
         * Retrieve a JDBC object value for the specified column.
         * <p>The default implementation uses the {@code getObject} method.
         * Additionally, this implementation includes a "hack" to get around Oracle
         * returning a non standard object for their TIMESTAMP datatype.
         * @param rs is the ResultSet holding the data
         * @param index is the column index
         * @return the Object returned
         * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue
         */
        protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
            return JdbcUtils.getResultSetValue(rs, index);
        }

    }

}
