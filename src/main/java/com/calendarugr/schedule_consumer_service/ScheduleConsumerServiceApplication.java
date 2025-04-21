package com.calendarugr.schedule_consumer_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.calendarugr.schedule_consumer_service.services.AllGradesScrapping;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
public class ScheduleConsumerServiceApplication {

	@Autowired
	private AllGradesScrapping bootScrapper;

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("API_KEY", dotenv.get("API_KEY"));
		System.setProperty("EUREKA_URL", dotenv.get("EUREKA_URL"));
		SpringApplication.run(ScheduleConsumerServiceApplication.class, args);

	}

	@Bean
    CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            //bootScrapper.runAllTasks();
        };
    }
}
