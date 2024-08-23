package com.wenbo.marketing.controller;

import com.wenbo.marketing.dao.MktActivityPrizeGrantDAO;
import com.wenbo.marketing.model.MktActivityPrizeGrant;
import com.wenbo.marketing.service.MktActivityService;
import com.wenbo.marketing.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Slf4j
@Controller
public class BasicController {

    @Autowired
    private MktActivityService mktActivityService;

    @Autowired
    private MktActivityPrizeGrantDAO mktActivityPrizeGrantDAO;

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
}
