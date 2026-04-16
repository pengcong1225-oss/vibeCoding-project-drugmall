package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.Address;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 地址Mapper接口
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    /**
     * 根据用户ID查询地址列表
     */
    List<Address> selectByUserId(String userId);
}
