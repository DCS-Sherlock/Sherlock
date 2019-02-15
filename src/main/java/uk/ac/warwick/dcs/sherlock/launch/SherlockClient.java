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

import javax.swing.*;

@SherlockModule (side = Side.CLIENT)
public class SherlockClient {

	@Instance
	public static SherlockClient instance;

	private static LocalDashboard dash;

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");

		SherlockClient.dash = new LocalDashboard();
		SherlockServer.engine = new SherlockEngine(Side.CLIENT);

		if (!SherlockServer.engine.isValidInstance()) {
			JFrame jf=new JFrame();
			jf.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(jf, "Sherlock is already running", "Sherlock error", JOptionPane.ERROR_MESSAGE);
			SherlockClient.dash.closeSplash();
			System.exit(1);
		}
		else {
			//If "-Dmodules" is in the JVM arguments, set the path to provided
			String modulesPath = System.getProperty("modules");
			if (modulesPath != null && !modulesPath.equals("")) {
				SherlockEngine.setModulesPath(modulesPath);
			}

			//If "-Doverride=True" is in the JVM arguments, make Spring thing it is running as a server
			String override = System.getProperty("override");
			if (override != null && override.equals("True")) {
				new SpringApplicationBuilder(SherlockServer.class).headless(false).profiles("server").run(args);
			}
			else {
				new SpringApplicationBuilder(SherlockServer.class).headless(false).profiles("client").run(args);
			}
		}
	}

	@EventHandler
	public void initialisation(EventInitialisation event) {

	}

	@EventHandler
	public void postInitialisation(EventPostInitialisation event) {
		SherlockClient.dash.setReady();
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
	}
}