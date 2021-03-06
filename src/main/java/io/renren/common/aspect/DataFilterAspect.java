package io.renren.common.aspect;

import io.renren.common.annotation.DataFilter;
import io.renren.common.exception.RRException;
import io.renren.common.utils.ShiroUtils;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysDeptService;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 数据过滤，切面处理类
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/9/17 15:02
 */
@Aspect
@Component
public class DataFilterAspect {
    @Autowired
    private SysDeptService sysDeptService;

    @Pointcut("@annotation(io.renren.common.annotation.DataFilter)")
    public void dataFilterCut() {

    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) throws Throwable {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){
            SysUserEntity user = ShiroUtils.getUserEntity();

            //如果不是超级管理员，则只能查询本部门及子部门数据
           // if(user.getUserId() != Constant.SUPER_ADMIN){
            if(user.getMcId()!=1){
                Map map = (Map)params;
                map.put("filterSql", getFilterSQL(user, point));
            }

            return ;
        }

        throw new RRException("要实现数据权限接口的参数，只能是Map类型，且不能为NULL");
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getFilterSQL(SysUserEntity user, JoinPoint point){

        MethodSignature signature = (MethodSignature) point.getSignature();

        Method method=((MethodSignature)point.getSignature()).getMethod();
        Method realMethod;
        try {
             realMethod = point.getTarget().getClass().getDeclaredMethod(signature.getName(),
                    method.getParameterTypes());
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        SysDeptEntity curDept = sysDeptService.queryObject(user.getMcId());

        DataFilter dataFilter = realMethod.getAnnotation(DataFilter.class);

        //获取表的别名
        String tableAlias = dataFilter.tableAlias();
        if(StringUtils.isNotBlank(tableAlias)){
            tableAlias +=  ".";
        }


        //获取子部门ID
        //String subDeptIds = sysDeptService.getSubDeptIdList(user.getMcId());
        StringBuilder filterSql = new StringBuilder();
        filterSql.append(" and ");
        filterSql.append(tableAlias).append("path like '"+curDept.getPath()+"%'");



        return filterSql.toString();
    }
}
