package com.wenbo.marketing.cron;

import com.wenbo.marketing.dao.MktActivityPrizeGrantDao;
import com.wenbo.marketing.model.MktActivityPrizeGrant;
import com.wenbo.marketing.utils.GsonUtil;
import com.wenbo.marketing.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestIpBlock {

	public static final String CNT = 100 + "";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MktActivityPrizeGrantDao mktActivityPrizeGrantDao;


	@Scheduled(cron = "*/10 * * * * *")
	public void cron() {
		List<MktActivityPrizeGrant> mktActivityPrizeGrants = mktActivityPrizeGrantDao.listIpBlock();
		if (CollectionUtils.isEmpty(mktActivityPrizeGrants)) {
			return;
		}

		List<String> ipList = mktActivityPrizeGrants.stream().map(x -> x.getGrantId()).collect(Collectors.toList());
		for (String ip : ipList) {
			RedisUtils.setEx(ip, CNT + "", 24 * RedisUtils.ONE_HOURS, redisTemplate);
		}

		for (MktActivityPrizeGrant mktActivityPrizeGrant : mktActivityPrizeGrants) {
			mktActivityPrizeGrant.setActivityId("1");
			mktActivityPrizeGrant.setPrizeId("1");
		}

		mktActivityPrizeGrantDao.updateBatchById(mktActivityPrizeGrants);

		log.info("ip block success ip size = {}", GsonUtil.toJson(ipList));
	}
}


