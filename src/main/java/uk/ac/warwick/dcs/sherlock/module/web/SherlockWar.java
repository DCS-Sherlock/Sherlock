package uk.ac.warwick.dcs.sherlock.module.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

@SherlockModule
@SpringBootApplication
public class SherlockWar extends SpringBootServletInitializer  {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SherlockWar.class);
    }

    public static void main(String[] args) {
        SherlockEngine.init(args);
        SpringApplication.run(SherlockWar.class, args);
    }
}