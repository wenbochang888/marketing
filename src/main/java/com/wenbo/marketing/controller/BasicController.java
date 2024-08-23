package com.wenbo.marketing.controller;

import com.wenbo.marketing.dao.MktActivityPrizeGrantDAO;
import com.wenbo.marketing.model.MktActivityPrizeGrant;
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
    private MktActivityPrizeGrantDAO mktActivityPrizeGrantDAO;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

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
        return mktActivityService.getNewestActivity();
    }

    @RequestMapping("/test/mysql/select2")
    @ResponseBody
    public String testMysqlSelect2() {
        List<MktActivityPrizeGrant> mktActivityPrizeGrants = mktActivityPrizeGrantDAO.selectList(null); // 空的list
        boolean isNull = mktActivityPrizeGrants == null;
        log.info("mktActivityPrizeGrants = {}, isNull = {}", mktActivityPrizeGrants, isNull);

        return GsonUtil.toJson(mktActivityPrizeGrants);
    }

    @RequestMapping("/test/redis/set")
    @ResponseBody
    public String testRedisSet(String key, String val) {
        RedisUtils.set(key, val, stringRedisTemplate);
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






















