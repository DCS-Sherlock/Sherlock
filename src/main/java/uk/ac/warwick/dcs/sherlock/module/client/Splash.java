package uk.ac.warwick.dcs.sherlock.module.client;

import javax.swing.*;
import java.awt.*;

public class Splash extends JFrame {

	public Splash() {
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		int height = ((int) (Math.round(screenHeight * (screenHeight > 720 ? 0.35 : 0.45)) / 100) * 100);
		height = Math.max(200, Math.min(height, 500));
		int width = (height/9)*16;

		System.out.println(height);

		this.setPreferredSize(new Dimension(width, height));
		this.setUndecorated(true);

		this.add(new GifPanel("static/splash/Splash" + height + ".gif"));

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void close() {
		this.setVisible(false);
		this.removeAll();
	}

	class GifPanel extends JPanel {

		Image image;

		GifPanel(String resourcePath) {
			System.out.println(resourcePath);
			image = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource(resourcePath));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image, 0, 0, this);
			}
		}

	}

}
