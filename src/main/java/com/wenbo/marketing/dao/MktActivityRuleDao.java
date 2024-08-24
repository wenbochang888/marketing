package com.wenbo.marketing.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wenbo.marketing.mapper.MktActivityRuleMapper;
import com.wenbo.marketing.model.MktActivityRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class MktActivityRuleDao extends ServiceImpl<MktActivityRuleMapper, MktActivityRule> {


	public List<MktActivityRule> listMktActivityRule(List<String> activityIds) {
		if (CollectionUtils.isEmpty(activityIds)) {
			return Lists.newArrayList();
		}

		LambdaQueryWrapper<MktActivityRule> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(MktActivityRule::getActivityId, activityIds);
		return baseMapper.selectList(wrapper);
	}
}
