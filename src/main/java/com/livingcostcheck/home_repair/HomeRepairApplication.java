package com.livingcostcheck.home_repair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.web.bind.annotation.RestController
public class HomeRepairApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeRepairApplication.class, args);
	}

	@org.springframework.web.bind.annotation.GetMapping("/debug")
	public String debug() {
		return "Server is running!";
	}
}
