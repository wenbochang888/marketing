package com.wenbo.marketing.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenbo.marketing.mapper.MktActivityPrizeGrantMapper;
import com.wenbo.marketing.model.MktActivityPrizeGrant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

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

	public Integer insert(MktActivityPrizeGrant grant) {
		if (grant == null || StringUtils.isEmpty(grant.getGrantId())) {
			return 0;
		}

		return baseMapper.insert(grant);
	}

	public List<MktActivityPrizeGrant> listAllMktActivityPrizeGrant() {
		LambdaQueryWrapper<MktActivityPrizeGrant> wrapper = new LambdaQueryWrapper<>();
		wrapper.orderByDesc(MktActivityPrizeGrant::getId);
		wrapper.last(" limit 5");
		return baseMapper.selectList(wrapper);
	}


	public List<MktActivityPrizeGrant> listIpBlock() {
		LambdaQueryWrapper<MktActivityPrizeGrant> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MktActivityPrizeGrant::getActivityId, "0");
		wrapper.eq(MktActivityPrizeGrant::getPrizeId, "0");
		return baseMapper.selectList(wrapper);
	}

}
