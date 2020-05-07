package com.learnmyself.servercircuitpattern;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author namtv3
 */
@Service
public class MyServiceTimeOut {

    public static boolean tripped = false;

    @HystrixCommand(
            fallbackMethod = "defaultDoSomethingTimeOut",
            commandKey = "doSomethingTimeOut",
            commandProperties = {
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "70"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000"),
                    @HystrixProperty(
                            name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "3000"),
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    @HystrixProperty(
                            name = "execution.isolation.thread.interruptOnTimeout",
                            value = "true")
            })
    public void doSomeThingTimeOut(int input) throws IOException, InterruptedException {
        // System.out.println(new Date() + " main method the input: " + input + " | count: " +
        // (input + 1));
        System.out.println(new Date() + " Before -  process method : " + input);
        callHttpClient(2);
        System.out.println(
                "Thread"
                        + Thread.currentThread().getName()
                        + " is "
                        + (Thread.currentThread().isInterrupted()
                        ? "interrupted"
                        : "not ainterrupted"));

        //        if (input % 10 < 8) {
        //            try {
        //                //TimeUnit.MILLISECONDS.sleep(4000);
        //                Thread.sleep(4000);
        //            } catch (Exception ex) {
        //                System.out.println("Exception: " +  ex);
        //                return;
        //            }
        //            System.out.println("Hello");
        //        } else {
        //            if (tripped) {
        //                System.out.println(getHystrixMetrics());
        //                System.out.println("=== CLOSED CIRCUIT BREAKER ===");
        //            } else {
        //                System.out.println(getHystrixMetrics());
        //            }
        //        }
    }

    private void callHttpClient(int type) throws IOException, InterruptedException {
        if (type == 1) { // CALL_API
            try {
                String uri = "http://localhost:9098/testapi";
                HttpGet get = new HttpGet(uri);
                HttpResponse response = getHttpClinent().execute(get);
                System.out.println(new Date() + " " + response);
            } catch (Exception ex) {
                System.out.println(new Date() + " ERROR" + ex);
            }
        } else { // Thread sleep
            Thread.sleep(5000);
        }
    }

    public void defaultDoSomethingTimeOut(int input, Throwable throwable)
            throws InterruptedException {
        //        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("doSomethingTimeOut");
        //        HystrixCircuitBreaker breaker = HystrixCircuitBreaker.Factory.getInstance(key);
        //        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(key);
        //        System.out.println(getHystrixMetrics());
        //        boolean isOpen = breaker.isOpen();
        //        if (isOpen) {
        //            System.out.println("=== OPEN CIRCUIT BREAKER ===");
        //            tripped = true;
        //        }
        //
        //        if (throwable instanceof HystrixTimeoutException) {
        //            System.out.println("Message: " + throwable.toString());
        //        }

        TimeUnit.MILLISECONDS.sleep(1000);
        System.out.println(
                new Date() + " fallback the input number: " + input + " | count: " + (input + 1));
        System.out.println();
    }

    public Map<String, Object> getHystrixMetrics() {
        Map<String, Object> result = new HashMap<>();
        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("doSomethingTimeOut");
        HystrixCommandMetrics metrics = HystrixCommandMetrics.getInstance(key);

        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts counts = metrics.getHealthCounts();
            HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(key);

            result.put("circuitOpen", circuitBreaker.isOpen());

            result.put("errorPercentage", counts.getErrorPercentage());
            result.put("success", metrics.getRollingCount(HystrixRollingNumberEvent.SUCCESS));
            result.put("timeout", metrics.getRollingCount(HystrixRollingNumberEvent.TIMEOUT));
            result.put("failure", metrics.getRollingCount(HystrixRollingNumberEvent.FAILURE));
            //            result.put("totalRequest", counts.getTotalRequests());
            //            result.put("health", counts.toString());
            //            metricsMap.put("shortCircuited",
            //            metrics.getRollingCount(HystrixRollingNumberEvent.SHORT_CIRCUITED));
            //            metricsMap.put("threadPoolRejected",
            //            metrics.getRollingCount(HystrixRollingNumberEvent.THREAD_POOL_REJECTED));
            //            metricsMap.put("semaphoreRejected",
            //            metrics.getRollingCount(HystrixRollingNumberEvent.SEMAPHORE_REJECTED));
            //            metricsMap.put("latency50", metrics.getTotalTimePercentile(50));
            //            metricsMap.put("latency90", metrics.getTotalTimePercentile(90));
            //            metricsMap.put("latency100", metrics.getTotalTimePercentile(100));
        }
        return result;
    }

    private HttpClient getHttpClinent() {
        HttpParams httpParams = new BasicHttpParams();

        httpParams.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.TRUE);
        httpParams.setParameter(CoreProtocolPNames.USER_AGENT, "ABC");

        HttpConnectionParams.setStaleCheckingEnabled(httpParams, Boolean.TRUE);
        SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, sf));

        // Initialize the http connection pooling
        PoolingClientConnectionManager connectionManager =
                new PoolingClientConnectionManager(schemeRegistry);

        // Initialize the connection parameters for performance tuning
        connectionManager.setMaxTotal(12);
        connectionManager.setDefaultMaxPerRoute(10);

        final int timeout = 3;
        RequestConfig requestConfig =
                RequestConfig.custom()
                        .setSocketTimeout(timeout * 1000)
                        .setConnectTimeout(timeout * 1000)
                        .setConnectionRequestTimeout(timeout * 1000)
                        .setStaleConnectionCheckEnabled(true)
                        .build();
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }
}
