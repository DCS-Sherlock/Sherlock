package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.common.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.common.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.common.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;

@SherlockModule (side = Side.SERVER)
@SpringBootApplication
@ComponentScan ("uk.ac.warwick.dcs.sherlock.module.web")
public class SherlockServer extends SpringBootServletInitializer {

	static SherlockEngine engine;

	public static void main(String[] args) {
	}

	@EventListener (ApplicationReadyEvent.class)
	public void afterStartup() {
		engine.initialise();
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
		engine = new SherlockEngine(Side.SERVER);
		return application.sources(SherlockServer.class);
	}
}