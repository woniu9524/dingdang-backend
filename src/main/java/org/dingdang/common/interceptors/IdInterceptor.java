package org.dingdang.common.interceptors;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:06 星期二
 * @Description: 雪花id拦截器
 */
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.dingdang.common.annotations.AutoSnowflakeId;
import org.dingdang.common.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * MyBatis拦截器，用于在执行insert操作前自动设置ID。
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Component
public class IdInterceptor implements Interceptor {

    @Autowired
    private IdWorker idWorker;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();// 获取方法参数
        MappedStatement mappedStatement = (MappedStatement) args[0];// 获取MappedStatement
        Object parameter = args[1];// 获取参数对象

        // 检查是否是INSERT操作
        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
            Field[] fields = parameter.getClass().getDeclaredFields();// 获取参数对象的所有字段
            for (Field field : fields) {
                if (field.isAnnotationPresent(AutoSnowflakeId.class)) {
                    field.setAccessible(true);// 设置字段可访问
                    // 如果ID字段为null，则生成新的ID
                    if (field.get(parameter) == null) {
                        long id = idWorker.nextId();
                        field.set(parameter, id);
                    }
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以通过配置文件设置属性
    }
}