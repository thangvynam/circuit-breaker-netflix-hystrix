package com.learnmyself.servercircuitpattern;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author namtv3
 */
@Service
public class MyServiceFailure {

    public static boolean tripped = false;

    @HystrixCommand(
            fallbackMethod = "defaultDoSomething",
            commandKey = "doSomething",
            commandProperties = {
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "70"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "3000"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "7000"),
            }
    )

    public void doSomething(int input) {
        System.out.println(new Date() + " main method the input: " + (input + 1));
        //in case of exception fallbackMethod is called
        //System.out.println("output: " + 10 / input);

        if (input % 10 < 8) {
            int a = input / 0;
        } else {
            if (tripped) {
                System.out.println(getHystrixMetrics());
                System.out.println("=== CLOSED CIRCUIT BREAKER ===");
            } else {
                System.out.println(getHystrixMetrics());
            }
        }

        System.out.println();
    }

    public void defaultDoSomething(int input, Throwable throwable) {
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("doSomething");
        HystrixCircuitBreaker breaker = HystrixCircuitBreaker.Factory.getInstance(key);
        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(key);
        System.out.println(getHystrixMetrics());
        boolean isOpen = breaker.isOpen();
        if (isOpen) {
            System.out.println("=== OPEN CIRCUIT BREAKER ===");
            tripped = true;
        }

        if (throwable instanceof ArithmeticException) {
            System.out.println("Message: " + throwable.toString());
        }

        System.out.println(new Date() + " fallback the input number: " + (input + 1));
        System.out.println();
    }

//    public void defaultDoSomething(int input, int count) {
//        try {
//            HystrixCommandKey key = HystrixCommandKey.Factory.asKey("doSomething");
//            HystrixCircuitBreaker breaker = HystrixCircuitBreaker.Factory.getInstance(key);
//            HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(key);
//            System.out.println(getHystrixMetrics());
//            boolean isOpen = breaker.isOpen();
//            if (isOpen) {
//                System.out.println("=== OPEN CIRCUIT BREAKER ===");
//                tripped = true;
//            }
//            int a = 2 / 0;
//        } catch (Exception ex) {
//            System.out.println(String.format("Exception: %s", ex));
//        }
//    }

    public Map<String, Object> getHystrixMetrics() {
        Map<String, Object> result = new HashMap<>();
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("doSomething");
        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(key);

        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts counts = metrics.getHealthCounts();
            HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(key);
//            result.put("health", counts.toString());
            result.put("circuitOpen", circuitBreaker.isOpen());
//            result.put("totalRequest", counts.getTotalRequests());
            result.put("errorPercentage", counts.getErrorPercentage());
            result.put("success", metrics.getRollingCount(HystrixRollingNumberEvent.SUCCESS));
            result.put("timeout", metrics.getRollingCount(HystrixRollingNumberEvent.TIMEOUT));
            result.put("failure", metrics.getRollingCount(HystrixRollingNumberEvent.FAILURE));
//            metricsMap.put("shortCircuited", metrics.getRollingCount(HystrixRollingNumberEvent.SHORT_CIRCUITED));
//            metricsMap.put("threadPoolRejected", metrics.getRollingCount(HystrixRollingNumberEvent.THREAD_POOL_REJECTED));
//            metricsMap.put("semaphoreRejected", metrics.getRollingCount(HystrixRollingNumberEvent.SEMAPHORE_REJECTED));
//            metricsMap.put("latency50", metrics.getTotalTimePercentile(50));
//            metricsMap.put("latency90", metrics.getTotalTimePercentile(90));
//            metricsMap.put("latency100", metrics.getTotalTimePercentile(100));
        }
        return result;
    }
}
