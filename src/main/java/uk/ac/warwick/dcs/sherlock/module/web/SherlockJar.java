package uk.ac.warwick.dcs.sherlock.module.web;

import org.springframework.boot.builder.SpringApplicationBuilder;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

@SherlockModule
public class SherlockJar extends JFrame {

	public SherlockJar() {
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

	public static void create() {
		new SpringApplicationBuilder(SherlockWar.class).headless(false).run();

		EventQueue.invokeLater(() -> {
			SherlockJar ex = new SherlockJar();
			ex.setVisible(true);
		});
	}

	@EventHandler
	public void initialisation(EventInitialisation event) {
		System.out.println(event.tmp);
	}
}