package com.video.processing;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
@EnableAsync
public class ProcessingApplication {
	public static void main(String[] args) {
		// Load .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        // Export to system properties so Spring can use them
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );

		SpringApplication.run(ProcessingApplication.class, args);
	}

}
