package com.lq.yingge_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lq.yingge_backend.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
