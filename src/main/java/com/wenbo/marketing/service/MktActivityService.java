package com.wenbo.marketing.service;

import cn.hutool.core.util.StrUtil;
import com.wenbo.marketing.dao.MktActivityPrizeDao;
import com.wenbo.marketing.dao.MktActivityPrizeGrantDao;
import com.wenbo.marketing.model.MktActivityInfo;
import com.wenbo.marketing.model.MktActivityPrize;
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

import java.time.LocalDateTime;
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


    @Autowired
    private MktActivityPrizeDao mktActivityPrizeDao;

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

    public List<MktActivityPrizeGrant> listAllMktActivityPrizeGrant() {
        return mktActivityPrizeGrantDao.listAllMktActivityPrizeGrant();
    }

    public boolean grantPrize(String phone, String activity) {
        if (StringUtils.isAnyEmpty(activity, phone)) {
            throw new RuntimeException(ERROR_MSG);
        }

        if (checkActivityRule(phone) == null) {
            throw new RuntimeException("请勿恶意请求接口");
        }

        // phone为幂等键
        String key = StrUtil.format(ACTIVITY_PHONE_LOCK, activity, phone);
        boolean success = RedisUtils.tryLock(key, redissonClient, () -> {
            //1. 幂等处理，这里还可以优化，因为grantId是一个唯一索引，插入失败就是重复领取，但可能失败次数会比较多
            MktActivityPrizeGrant mktActivityPrizeGrant = mktActivityPrizeGrantDao.getMktActivityPrizeGrant(phone);
            if (mktActivityPrizeGrant != null && StringUtils.isNotEmpty(mktActivityPrizeGrant.getGrantId())) {
                throw new RuntimeException("请勿重复领取");
            }

            // 2. 这里一个优化, 随机比例获取奖品，可以随时调整
            int seed = ThreadLocalRandom.current().nextInt(0, 100) + 1; // 1-100
            int random = NumberUtils.toInt(RedisUtils.get(CACHE_MKT_ACTIVITY_PRIZE_RANDOM, stringRedisTemplate));
            if (seed > random) {
                //log.warn("随机比例被拦截 seed = {}, random = {}", seed, random);
                throw new RuntimeException("随机比例拦截 - " + ERROR_MSG);
            }


            // 3. 缓存预减库存
            Long num = RedisUtils.decr(CACHE_MKT_ACTIVITY_PRIZE_NUM, stringRedisTemplate);
            if (num == null || num < 0) {

                // 将redis库存加回，可做可不做，看业务需求
                RedisUtils.incr(CACHE_MKT_ACTIVITY_PRIZE_NUM, stringRedisTemplate);
                throw new RuntimeException("redis库存不足 - " + ERROR_MSG);
            }

            MktActivityPrize activityPrize = activityCacheService.getActivityPrize();


            // 4. 真正数据库减库存，并且插入发奖记录
            // 如果redis预减库存成功，这里大概率会成功，基本不会失败，如果失败，放弃重试，失败重试会影响系统性能，重试次数越多，对系统性能的影响越大。
            Boolean execute = transactionTemplate.execute(status -> {
                // 4.1 扣减库存
                Integer update = mktActivityPrizeDao.occupyActivityPrize(activityPrize.getActivityId(), activityPrize.getPrizeId());
                if (update == null || update <= 0) {
                    //log.warn("mysql 扣减库存失败 update = {}", update);
                    throw new RuntimeException("mysql库存扣减失败 - " + ERROR_MSG);
                }

                // 4.2 插入发奖记录
                MktActivityPrizeGrant grant = buildMktActivityPrizeGrant(phone, activityPrize);
                Integer insert = mktActivityPrizeGrantDao.insert(grant);
                if (insert == null || insert <= 0) {
                    //log.warn("mysql 插入发奖记录失败 insert = {}", insert);
                    throw new RuntimeException("mysql 插入发奖记录失败 - " + ERROR_MSG);
                }

                return true;
            });

            return execute;
        });


        return success;
    }

    private MktActivityPrizeGrant buildMktActivityPrizeGrant(String phone, MktActivityPrize activityPrize) {
        MktActivityPrizeGrant grant = new MktActivityPrizeGrant();
        grant.setActivityId(activityPrize.getActivityId());
        grant.setPrizeId(activityPrize.getPrizeId());
        grant.setGrantId(phone);
        grant.setGrantTime(LocalDateTime.now());
        return grant;
    }
}



























