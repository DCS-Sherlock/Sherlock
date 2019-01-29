package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.builder.SpringApplicationBuilder;
import uk.ac.warwick.dcs.sherlock.api.annotation.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotation.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.annotation.SherlockModule.Instance;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.module.web.LocalDashboard;

@SherlockModule (side = Side.CLIENT)
public class SherlockClient {

	@Instance
	public static SherlockClient instance;

	private LocalDashboard dash;

	public static void main(String[] args) {
		SherlockServer.engine = new SherlockEngine(Side.CLIENT);

		//If "-Dmodules" is in the JVM arguments, set the path to provided
		String modulesPath = System.getProperty("modules");
		if (modulesPath != null && !modulesPath.equals("")) {
			SherlockEngine.setModulesPath(modulesPath);
		}

		//If "-Doverride=True" is in the JVM arguments, make Spring thing it is running as a server
		String override = System.getProperty("override");
		if (override != null && override.equals("True")) {
			new SpringApplicationBuilder(SherlockServer.class).headless(false).profiles("server").run(args);
		} else {
			new SpringApplicationBuilder(SherlockServer.class).headless(false).profiles("client").run(args);
		}
	}

	@EventHandler
	public void initialisation(EventInitialisation event) {
		this.dash = new LocalDashboard();
	}

	@EventHandler
	public void postInitialisation(EventPostInitialisation event) {
		this.dash.setVisible(true);
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
	}
}