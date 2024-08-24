package com.wenbo.marketing.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenbo.marketing.mapper.MktActivityPrizeGrantMapper;
import com.wenbo.marketing.model.MktActivityPrizeGrant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MktActivityPrizeGrantDao extends ServiceImpl<MktActivityPrizeGrantMapper, MktActivityPrizeGrant> {

	public MktActivityPrizeGrant getMktActivityPrizeGrant(String grantId) {
		if (StringUtils.isEmpty(grantId)) {
			return null;
		}

		LambdaQueryWrapper<MktActivityPrizeGrant> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MktActivityPrizeGrant::getGrantId, grantId);
		return baseMapper.selectOne(wrapper);
	}

}
