package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

@SherlockModule (side = Side.SERVER)
@SpringBootApplication
@ComponentScan ("uk.ac.warwick.dcs.sherlock.module.web")
public class SherlockServer extends SpringBootServletInitializer {

	public static SpringApplication server;

	public static void main(String[] args) {
		new SpringApplication(SherlockServer.class).run(args);
		new SherlockEngine(Side.SERVER);
	}

	@EventHandler
	public void initialisation(EventInitialisation event) {
	}

	@EventHandler
	public void postInitialisation(EventPostInitialisation event) {
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SherlockServer.class);
	}
}