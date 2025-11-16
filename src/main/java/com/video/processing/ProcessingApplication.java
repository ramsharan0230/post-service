package com.video.processing;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProcessingApplication {
	public static void main(String[] args) {
		Random random = new Random();
		System.out.println("random int: "+random.nextInt(100000));
		SpringApplication.run(ProcessingApplication.class, args);
	}

}
