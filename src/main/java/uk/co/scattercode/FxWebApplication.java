package uk.co.scattercode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the web application in an embedded Tomcat. Component scanning from
 * this package picks up the controllers and the IBAN validators, as the
 * {@code <context:component-scan base-package="uk.co.scattercode"/>} in the
 * old servlet XML did.
 */
@SpringBootApplication
public class FxWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxWebApplication.class, args);
    }
}
