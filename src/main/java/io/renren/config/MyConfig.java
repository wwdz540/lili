package io.renren.config;

import io.renren.common.utils.CommonUtil;
import io.renren.modules.sys.service.SysDeptService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {
    @Bean
    CommonUtil commonUtil(SysDeptService service){
        CommonUtil commonUtil = new CommonUtil();
        CommonUtil.setSysDeptService(service);
        return commonUtil;
    }
}
