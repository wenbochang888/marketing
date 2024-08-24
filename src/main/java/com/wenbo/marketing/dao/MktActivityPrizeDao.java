package com.wenbo.marketing.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wenbo.marketing.constant.EnableEnum;
import com.wenbo.marketing.mapper.MktActivityPrizeMapper;
import com.wenbo.marketing.model.MktActivityPrize;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class MktActivityPrizeDao extends ServiceImpl<MktActivityPrizeMapper, MktActivityPrize> {

	public List<MktActivityPrize> listMktActivityPrize(String activityId) {
		if (StringUtils.isEmpty(activityId)) {
			return Lists.newArrayList();
		}

		LambdaQueryWrapper<MktActivityPrize> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MktActivityPrize::getActivityId, activityId)
				.eq(MktActivityPrize::getEnable, EnableEnum.ENABLE.getCode());
		return baseMapper.selectList(wrapper);
	}

	public MktActivityPrize getMktActivityPrize(String activityId, String prizeId) {
		if (StringUtils.isAnyEmpty(activityId, prizeId)) {
			return null;
		}

		LambdaQueryWrapper<MktActivityPrize> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MktActivityPrize::getActivityId, activityId)
				.eq(MktActivityPrize::getEnable, EnableEnum.ENABLE.getCode())
				.eq(MktActivityPrize::getPrizeId, prizeId)
		;
		return baseMapper.selectOne(wrapper);
	}
}
