package com.learnmyself.servercircuitpattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
public class ServerCircuitPatternApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerCircuitPatternApplication.class, args);
	}

}
