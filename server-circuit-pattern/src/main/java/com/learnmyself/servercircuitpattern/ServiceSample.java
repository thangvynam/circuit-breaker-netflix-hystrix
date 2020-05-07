package com.learnmyself.servercircuitpattern;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * @author namtv3
 */
@Service
public class ServiceSample {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "callMethod_Fallback")
    public String callOtherService() {
        System.out.println("Call main method");
        String response =
                restTemplate
                        .exchange(
                                "http://localhost:9000/testcircuit",
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<String>() {
                                },
                                "")
                        .getBody();

        return response;
    }

    private String callMethod_Fallback() {
        System.out.println(new Date() + "Other service is down!!! Fallback rout enabled: ");
        return "CIRCUIT BREAKER ENABLED!!! No Response From Other Service at this moment";
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
