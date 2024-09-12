package com.wenbo.marketing.aspect;

import com.google.common.collect.Sets;
import com.wenbo.marketing.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author changwenbo
 * @date 2024/8/31 15:26
 */
@Slf4j
@Component
public class IPBlockInterceptor implements HandlerInterceptor {

    /** 3s内访问10次，认为是刷接口，就要进行一个限制 */
    private static final long TIME = 3;
    private static final long CNT = 10;
    private static final Object LOCK = new Object();

    private static final Set<String> CHECK_URL = Sets.newHashSet("/marketing/activity/grant");

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String s = UUID.randomUUID().toString();
        MDC.put("sessionId", s);

        if (checkIP(request)) {
            return true;
        } else {
            throw new RuntimeException("请勿恶意请求接口, 半个小时之后恢复请求");
        }
    }

    private boolean checkIP(HttpServletRequest request) throws Exception {
        String ip = getClientIp(request);
        String url = request.getRequestURI().toString();
        String param = getAllParam(request);

        if (!CHECK_URL.contains(url)) {
            return true;
        }

        synchronized (LOCK) {
            String referer = request.getHeader("referer");
            String ua = request.getHeader("user-agent");
            if (StringUtils.isAnyEmpty(referer, ua)) {
                return false;
            }



            boolean isExist = StringUtils.isNotEmpty(RedisUtils.get(ip, redisTemplate));
            if (isExist) {
                long cnt = RedisUtils.incr(ip, redisTemplate);
                // 修复 incr的时候  key正好失效
                if (cnt == 1) {
                    RedisUtils.setEx(ip, "2", TIME, TimeUnit.SECONDS, redisTemplate);
                }
                if (cnt > CNT) {
                    log.error("ip = {}, 请求过快，被限制", ip);
                    cnt--;
                    RedisUtils.setEx(ip, cnt + "", 24 * RedisUtils.ONE_HOURS, redisTemplate);
                    return false;
                }
                log.info("ip = {}, {}s之内第{}次请求{}，参数为{}，通过", ip, TIME, cnt, url, param);
            } else {
                RedisUtils.setEx(ip, 1 + "", TIME, TimeUnit.SECONDS, redisTemplate);
                log.info("ip = {}, {}s之内第1次请求{}，参数为{}，通过", ip, TIME, url, param);
            }
        }
        return true;
    }

    private String getAllParam(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        StringBuilder sb = new StringBuilder("[");
        map.forEach((x, y) -> {
            String s = StringUtils.join(y, ",");
            sb.append(x + " = " + s + ";");
        });
        sb.append("]");
        return sb.toString();
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
