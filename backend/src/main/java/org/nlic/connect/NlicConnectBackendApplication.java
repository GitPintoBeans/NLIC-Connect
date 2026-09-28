package org.nlic.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstraps the NLIC Connect Spring Boot backend application.
 * This is the entry point for the REST API, JPA configuration, and security setup.
 */
@SpringBootApplication
public class NlicConnectBackendApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(NlicConnectBackendApplication.class, args);
	}

}
