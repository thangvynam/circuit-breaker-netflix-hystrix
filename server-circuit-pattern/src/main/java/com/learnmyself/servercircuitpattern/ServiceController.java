package com.learnmyself.servercircuitpattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author namtv3
 */
@RestController
public class ServiceController {

    @Autowired
    private ServiceSample serviceSample;

    @Autowired
    private MyServiceFailure myServiceFailure;

    @Autowired
    private MyServiceTimeOut myServiceTimeOut;

    @RequestMapping(value = "/testcircuit/failure", method = RequestMethod.GET)
    public String getInfo() throws InterruptedException {
        int n = 20;
        System.out.println("Start time: " + new Date());
        for (int i = 0 ; i < n; i++) {
            myServiceFailure.doSomething(i);
            TimeUnit.MILLISECONDS.sleep(1000);
        }
        System.out.println("End time: " + new Date());
        return "namtv3";
    }

    @RequestMapping(value = "/testcircuit/timeout", method = RequestMethod.GET)
    public String getInfoTimeOut() throws IOException, InterruptedException {
        int n = 20;
        System.out.println("Start time: " + new Date());
        for (int i = 0 ; i < n; i++) {
            try {
                myServiceTimeOut.doSomeThingTimeOut(i);
            } catch (Exception ex) {
                System.out.println(new Date() + " [ERROR] ex: " + ex);
            }
            //TimeUnit.MILLISECONDS.sleep(1000);
        }
        System.out.println("End time: " + new Date());
        return "namtv3";
    }

}
