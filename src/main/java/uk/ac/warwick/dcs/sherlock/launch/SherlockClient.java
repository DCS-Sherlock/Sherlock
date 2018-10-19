package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.lib.Reference;
import uk.ac.warwick.dcs.sherlock.module.web.Dashboard;

import java.awt.*;

@SherlockModule
public class SherlockClient {

	public static void main(String[] args) {
		new SherlockEngine(args, Reference.Side.CLIENT);
	}

	@EventHandler
	public void initialisation(EventInitialisation event) {
		EventQueue.invokeLater(() -> {
			Dashboard dash = new Dashboard();
			dash.setVisible(true);
		});
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
		ConfigurableApplicationContext server = new SpringApplicationBuilder(SherlockServer.class).headless(false).run(event.getLaunchArgs());
	}
}