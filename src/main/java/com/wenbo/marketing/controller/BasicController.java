package com.wenbo.marketing.controller;

import com.wenbo.marketing.mapper.MktActivityPrizeGrantMapper;
import com.wenbo.marketing.model.MktActivityInfo;
import com.wenbo.marketing.model.MktActivityRule;
import com.wenbo.marketing.service.ActivityCacheService;
import com.wenbo.marketing.service.MktActivityService;
import com.wenbo.marketing.utils.GsonUtil;
import com.wenbo.marketing.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Controller
public class BasicController {

    @Autowired
    private MktActivityService mktActivityService;

    @Autowired
    private MktActivityPrizeGrantMapper mktActivityPrizeGrantMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ActivityCacheService activityCacheService;

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        log.info("Hello Index");
        return "Hello Index";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        log.info("Hello World");
        return "Hello World";
    }

    @RequestMapping("/test/mysql/select")
    @ResponseBody
    public String testMysqlSelect() {
        return null;
    }

    @RequestMapping("/test/mysql/select2")
    @ResponseBody
    public String testMysqlSelect2() {
        MktActivityInfo activityInfo = activityCacheService.getActivityInfo();
        if (activityInfo != null) {
            String activityId = activityInfo.getActivityId();
            List<MktActivityRule> mktActivityRules = activityCacheService.listActivityRule(activityId);

            return GsonUtil.toJson(activityInfo) + " -- " + GsonUtil.toJson(mktActivityRules);
        }
	    return "";
    }

    @RequestMapping("/test/redis/set")
    @ResponseBody
    public String testRedisSet(String key, String val) {
        RedisUtils.setEx(key, val, 1000 * 60, stringRedisTemplate);
        return "ok";
    }

    @RequestMapping("/test/redis/get")
    @ResponseBody
    public String testRedisGet(String key) {
        String s = RedisUtils.get(key, stringRedisTemplate);
        log.info("val = {}", s);
        return StringUtils.isEmpty(s) ? "" : s;
    }

    @RequestMapping("/test/redis/lock")
    @ResponseBody
    public String testRedisLock() {
        ExecutorService es = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 2; i++) {
            es.submit(() -> lock());
        }

        return "ok";
    }

    private void lock() {
        try {
            RedisUtils.tryLock("key", redissonClient, () -> {
                log.info("get lock success");
                return "ok";
            });
        } catch (Exception e) {
            log.error("e = {}", e.getMessage(), e);
        }
    }
}