package com.wenbo.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wenbo.marketing.model.MktActivityPrize;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author changwenbo
 * @date 2024/8/23 16:14
 */
@Repository
public interface MktActivityPrizeMapper extends BaseMapper<MktActivityPrize> {

	Integer occupyActivityPrize(@Param("activityId") String activityId, @Param("prizeId") String prizeId);

}
