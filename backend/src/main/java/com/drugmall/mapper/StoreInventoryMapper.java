package com.drugmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drugmall.entity.StoreInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店药品库存Mapper
 */
@Mapper
public interface StoreInventoryMapper extends BaseMapper<StoreInventory> {
}
