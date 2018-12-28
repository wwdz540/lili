package io.renren;

import io.renren.modules.crm.dao.FyTransDataDao;
import io.renren.modules.crm.service.ITransDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FyTransDataDaoTest {

    @Autowired
    private FyTransDataDao transDataDao;
/*

    @Autowired
    @Qualifier("sfTransDataService")
    private ITransDataService iTransDataService;
*/


    @Autowired
    private Map<String,ITransDataService>  serviceMap;
    @Test
    public void testTransData(){
        Map<String,Object> parms = new HashMap<>();
        parms.put("path","00000000");
       int toatal = transDataDao.queryTotal(parms);
        System.out.println(toatal);
    }
    @Test
    public void testGroup(){
        /***
         * gfield
         */
        Map<String,Object> params = new HashMap<>();
        params.put("collection","count(1)");
        params.put("gfield","left(path,19)");
        List<Map<String, Object>> result = transDataDao.queryByGroup(params);
        result.forEach(System.out::println);
    }

    @Test
    public void testGroup2(){
       /** Map<String,String> params = new HashMap();
        List<Map<String,Object>> result =iTransDataService.createGroupQuery()
                .collection("count")
                //.collection("sum","total_fee")
                .group("left(path,19)")
                .query(params);

        result.forEach(System.out::println);**/
       serviceMap.forEach( (k,v)-> System.out.println(k+"v"));

    }

}
