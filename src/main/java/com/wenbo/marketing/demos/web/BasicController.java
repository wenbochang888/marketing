package com.wenbo.marketing.demos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
public class BasicController {

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
}
