package com.wenbo.marketing.service;

import cn.hutool.core.util.StrUtil;
import com.wenbo.marketing.dao.MktActivityPrizeGrantDao;
import com.wenbo.marketing.model.MktActivityInfo;
import com.wenbo.marketing.model.MktActivityPrizeGrant;
import com.wenbo.marketing.model.MktActivityRule;
import com.wenbo.marketing.model.rule.ActivityRuleContext;
import com.wenbo.marketing.service.factory.BaseRuleFactory;
import com.wenbo.marketing.service.rule.BaseRuleService;
import com.wenbo.marketing.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.wenbo.marketing.constant.MktActivityConstants.ACTIVITY_PHONE_LOCK;
import static com.wenbo.marketing.constant.MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE_NUM;
import static com.wenbo.marketing.constant.MktActivityConstants.CACHE_MKT_ACTIVITY_PRIZE_RANDOM;
import static com.wenbo.marketing.constant.MktActivityConstants.ERROR_MSG;

/**
 * @author changwenbo
 * @date 2024/8/23 16:16
 */
@Slf4j
@Service
public class MktActivityService {
    @Autowired
    private ActivityCacheService activityCacheService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MktActivityPrizeGrantDao mktActivityPrizeGrantDao;


    public MktActivityInfo checkActivityRule(String phone) {
        MktActivityInfo activityInfo = activityCacheService.getActivityInfo();
        if (activityInfo == null || StringUtils.isEmpty(activityInfo.getActivityId())) {
            return null;
        }

        ActivityRuleContext context = new ActivityRuleContext();
        context.setPhone(phone);
        List<MktActivityRule> mktActivityRules = activityCacheService.listActivityRule(activityInfo.getActivityId());
        for (MktActivityRule mktActivityRule : mktActivityRules) {
            BaseRuleService baseRuleService = BaseRuleFactory.getBaseRuleService(mktActivityRule.getRuleKey());
            if (baseRuleService == null || !baseRuleService.check(context)) {
                return null;
            }
        }

        return activityInfo;
    }

    public boolean grantPrize(String phone, String activity) {
        if (StringUtils.isAnyEmpty(activity, phone)) {
            throw new RuntimeException(ERROR_MSG);
        }

        // phone为幂等键
        String key = StrUtil.format(ACTIVITY_PHONE_LOCK, activity, phone);
        boolean success = RedisUtils.tryLock(key, redissonClient, () -> {
            //1. 幂等处理
            MktActivityPrizeGrant mktActivityPrizeGrant = mktActivityPrizeGrantDao.getMktActivityPrizeGrant(phone);
            if (mktActivityPrizeGrant != null && StringUtils.isNotEmpty(mktActivityPrizeGrant.getGrantId())) {
                throw new RuntimeException("请勿重复领取");
            }

            // 2. 这里一个优化, 随机比例获取奖品，可以随时调整
            int seed = ThreadLocalRandom.current().nextInt(0, 100) + 1; // 1-100
            int random = NumberUtils.toInt(RedisUtils.get(CACHE_MKT_ACTIVITY_PRIZE_RANDOM, stringRedisTemplate));
            if (seed > random) {
                throw new RuntimeException(ERROR_MSG);
            }


            // 3. 缓存预减库存
            Long num = RedisUtils.decr(CACHE_MKT_ACTIVITY_PRIZE_NUM, stringRedisTemplate);
            if (num == null || num < 0) {
                throw new RuntimeException(ERROR_MSG);
            }

            // 4. 真正数据库减库存，并且插入发奖记录
            transactionTemplate.execute(status -> {

                return null;
            });

            return true;
        });


        return success;
    }
}



























