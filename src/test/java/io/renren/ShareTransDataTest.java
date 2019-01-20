package io.renren;

import io.renren.modules.crm.entity.TransDataEntity;
import io.renren.modules.crm.service.ITransDataService;
import io.renren.modules.crm.service.impl.ShareTransDataServerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShareTransDataTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private Map<String,ITransDataService> serviceMap;

    @Test
   public void test1() throws ShareTransDataServerImpl.SharePointException {
        ITransDataService dataService = serviceMap.get("fy");

        ShareTransDataServerImpl sd = new ShareTransDataServerImpl();
        sd.setJdbcTemplate(jdbcTemplate);
        sd.setTransDataService(dataService);

        TransDataEntity item = dataService.queryById(13924l);
        sd.fixSharePoint(item);



   }


}
