package uk.ac.warwick.dcs.sherlock.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.warwick.dcs.sherlock.api.filesystem.ISourceFile;
import uk.ac.warwick.dcs.sherlock.core.ModuleLoader;
import uk.ac.warwick.dcs.sherlock.lib.Reference;
import uk.ac.warwick.dcs.sherlock.model.base.detection.TestDetector;
import uk.ac.warwick.dcs.sherlock.model.core.TestResultsFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import java.util.List;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

@SpringBootApplication
public class SherlockApplication extends JFrame {

	public SherlockApplication() {
		initUI();
	}

	private void initUI() {
		setTitle("Sherlock");
		setPreferredSize(new Dimension(600, 400));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container pane = getContentPane();
		GroupLayout group = new GroupLayout(pane);
		pane.setLayout(group);

		group.setAutoCreateGaps(true);
		group.setAutoCreateContainerGaps(true);

		JButton openButton = new JButton("Open Dashboard");
		openButton.addActionListener((ActionEvent event) -> {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI("http://localhost:8080"));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});

		JButton quitButton = new JButton("Quit Sherlock");
		quitButton.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});

		group.setHorizontalGroup(group.createSequentialGroup().addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(openButton).addComponent(quitButton));

		group.setVerticalGroup(group.createSequentialGroup().addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(group.createParallelGroup().addComponent(openButton).addComponent(quitButton)));

		group.linkSize(SwingConstants.HORIZONTAL, openButton, quitButton);

		pack();

		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		ModuleLoader modules = new ModuleLoader();
		for (Class<?> c : modules.getModules()) {
			System.out.println(c.getName());
			try {
				c.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		/*ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SherlockApplication.class).headless(false).run(args);

		EventQueue.invokeLater(() -> {
			SherlockApplication ex = ctx.getBean(SherlockApplication.class);
			ex.setVisible(true);
		});*/
	}

	/**
	 * old main method for reference
	 */
	private String runSherlockTest() {
		if (Reference.isDevelEnv) {
			System.out.println("Sherlock vX.X.X [Development Version]\n");
		}
		else {
			System.out.println(String.format("Sherlock v%s\n", Reference.version));
		}

		String result = "";
		long startTime = System.currentTimeMillis();

		try {
			List<ISourceFile> fileList = Collections.synchronizedList(Arrays.asList(new TestResultsFactory.tmpFile("test.java"), new TestResultsFactory.tmpFile("test2.java")));
			result = TestResultsFactory.buildTestResults(fileList, TestDetector.class);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}

		assert result != null;
		return result.concat("\n\nTotal Runtime Time = " + (System.currentTimeMillis() - startTime) + "ms");
	}
}