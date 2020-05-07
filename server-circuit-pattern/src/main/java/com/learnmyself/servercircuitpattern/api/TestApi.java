package com.learnmyself.servercircuitpattern.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author namtv3
 */
@RestController
public class TestApi {

    @RequestMapping(value = "/testapi", method = RequestMethod.GET)
    public String testApi() throws InterruptedException {
        Thread.sleep(5000);
        return "namtv333";
    }
}
