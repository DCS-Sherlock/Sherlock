package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.lib.Reference;

@SherlockModule
@SpringBootApplication
@ComponentScan ("uk.ac.warwick.dcs.sherlock.module.web")
public class SherlockServer extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SherlockEngine(args, Reference.Side.SERVER);
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
		SpringApplication server = new SpringApplication(SherlockServer.class);
		server.run(event.getLaunchArgs());
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		System.out.println("Server configure");
		return application.sources(SherlockServer.class);
	}
}