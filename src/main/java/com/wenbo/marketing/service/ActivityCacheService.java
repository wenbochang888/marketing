package com.wenbo.marketing.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.wenbo.marketing.constant.MktActivityConstants;
import com.wenbo.marketing.dao.MktActivityPrizeDao;
import com.wenbo.marketing.dao.MktActivityRuleDao;
import com.wenbo.marketing.mapper.MktActivityInfoMapper;
import com.wenbo.marketing.model.MktActivityInfo;
import com.wenbo.marketing.model.MktActivityPrize;
import com.wenbo.marketing.model.MktActivityRule;
import com.wenbo.marketing.utils.GsonUtil;
import com.wenbo.marketing.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActivityCacheService {

	@Autowired
	private MktActivityInfoMapper mktActivityInfoMapper;

	@Autowired
	private MktActivityRuleDao MktActivityRuleDao;

	@Autowired
	private MktActivityPrizeDao mktActivityPrizeDao;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;


	public MktActivityInfo getActivityInfo() {
		String key = StrUtil.format(MktActivityConstants.CACHE_MKT_ACTIVITY_INFO);
		String value = RedisUtils.get(key, stringRedisTemplate);
		log.debug("key = {}, value = {}", key, value);
		if (StrUtil.isBlank(value)) {
			return null;
		}
		return GsonUtil.fromJson(value, MktActivityInfo.class);
	}

	public List<MktActivityRule> listActivityRule(String activityId) {
		String ruleKey = StrUtil.format(MktActivityConstants.CACHE_MKT_ACTIVITY_RULE, activityId);
		String value = RedisUtils.get(ruleKey, stringRedisTemplate);
		log.debug("key = {}, value = {}", ruleKey, value);
		if (StrUtil.isBlank(value)) {
			return Lists.newArrayList();
		}

		TypeToken<List<MktActivityRule>> typeToken = new TypeToken<List<MktActivityRule>>() {};
		return GsonUtil.fromJson(value, typeToken);
	}

	public MktActivityPrize getActivityPrize() {
		String value = RedisUtils.get(MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE, stringRedisTemplate);
		log.debug("key = {}, value = {}", MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE, value);
		if (StrUtil.isBlank(value)) {
			return null;
		}
		return GsonUtil.fromJson(value, MktActivityPrize.class);
	}

	private String activityId;

	@PostConstruct
	public void init() {
		// 1. 缓存活动信息
		cacheAllActivity();

		// 2. 缓存库存信息
		cachePrizeInfo();


		// 随机比例获取奖品
		RedisUtils.set(MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE_RANDOM, "100", stringRedisTemplate);
	}

	private void cachePrizeInfo() {
		List<MktActivityPrize> mktActivityPrizes = mktActivityPrizeDao.listMktActivityPrize(activityId);
		if (CollectionUtils.isEmpty(mktActivityPrizes)) {
			return;
		}

		// 这里我们以一个奖品为例，多个奖品类似
		MktActivityPrize mktActivityPrize = mktActivityPrizes.get(0);
		RedisUtils.set(MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE, GsonUtil.toJson(mktActivityPrize), stringRedisTemplate);
		RedisUtils.set(MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE_NUM,
				GsonUtil.toJson(mktActivityPrize.getPrizeRemainingNum()), stringRedisTemplate);
	}

	private void cacheAllActivity() {
		List<MktActivityInfo> activityInfoList = mktActivityInfoMapper.selectList(null);
		if (CollectionUtils.isEmpty(activityInfoList)) {
			return;
		}

		// 选择一个最新的活动信息, 缓存活动信息
		MktActivityInfo mktActivityInfo = activityInfoList.stream().max((x, y) -> y.getUpdatedAt().compareTo(x.getUpdatedAt())).get();
		String key = StrUtil.format(MktActivityConstants.CACHE_MKT_ACTIVITY_INFO);
		RedisUtils.set(key, GsonUtil.toJson(mktActivityInfo), stringRedisTemplate);

		activityId = mktActivityInfo.getActivityId();

		// 缓存规则信息
		List<String> activityIdList = Lists.newArrayList(mktActivityInfo.getActivityId());
		List<MktActivityRule> mktActivityRuleList = MktActivityRuleDao.listMktActivityRule(activityIdList);
		if (CollectionUtils.isEmpty(mktActivityRuleList)) {
			return;
		}

		// 将规则信息按活动ID分组，并且进行缓存
		Map<String, List<MktActivityRule>> ruleMap =
				mktActivityRuleList.stream().collect(Collectors.groupingBy(MktActivityRule::getActivityId));
		for (String activityId : ruleMap.keySet()) {
			String ruleKey = StrUtil.format(MktActivityConstants.CACHE_MKT_ACTIVITY_RULE, activityId);
			RedisUtils.set(ruleKey, GsonUtil.toJson(ruleMap.get(activityId)), stringRedisTemplate);
		}
	}
}


























