package org.komponente.rentalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RentalserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentalserviceApplication.class, args);
	}

}
