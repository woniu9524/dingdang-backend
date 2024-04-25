package org.dingdang.common.annotations;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:05 星期二
 * @Description: 雪花id
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记属性，表示该属性的值需要通过IdWorker自动生成。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoSnowflakeId {
}
