package org.dingdang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dingdang.domain.SysUser;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 1:59 星期二
 * @Description:
 */
@Mapper
public interface AuthMapper {
    int register(SysUser sysUser);
}
