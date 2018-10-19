package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.lib.Reference;

@SherlockModule
@SpringBootApplication
@ComponentScan ("uk.ac.warwick.dcs.sherlock.module.web")
public class SherlockServer extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SherlockEngine(args, Reference.Side.CLIENT);
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
		System.out.println(event.tmp);

		SpringApplication.run(SherlockServer.class, event.getLaunchArgs());
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SherlockServer.class);
	}
}