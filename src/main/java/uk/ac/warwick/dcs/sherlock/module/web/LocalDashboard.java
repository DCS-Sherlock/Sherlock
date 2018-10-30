package uk.ac.warwick.dcs.sherlock.module.web;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class LocalDashboard extends JFrame {

	public LocalDashboard() {
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

		JButton openButton = new JButton("Open LocalDashboard");
		openButton.addActionListener((ActionEvent event) -> {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI("http://localhost:2218"));
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
}
