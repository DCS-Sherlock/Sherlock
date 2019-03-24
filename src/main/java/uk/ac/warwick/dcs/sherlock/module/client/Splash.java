package uk.ac.warwick.dcs.sherlock.module.client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.*;

public class Splash extends JFrame {

	private GifPanel gif;
	private TextPanel text;
	private Thread textThread;

	public Splash() {
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		int height = ((int) (Math.round(screenHeight * (screenHeight > 720 ? 0.35 : 0.4) + 49) / 100) * 100);
		height = Math.max(200, Math.min(height, 500));
		int width = (height / 9) * 16;

		this.setPreferredSize(new Dimension(width, height));
		this.setUndecorated(true);
		this.setAlwaysOnTop(false);

		this.gif = new GifPanel("static/splash/Splash" + height + ".gif");
		this.text = new TextPanel(height, width);
		this.gif.add(this.text);

		this.add(this.gif);

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		this.textThread = new Thread(this.text);
		this.textThread.start();
	}

	public void close() {
		this.setVisible(false);
		SwingUtilities.invokeLater(() -> {
			this.text.stop();
			try {
				this.textThread.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.removeAll();
		});
	}

	class TextPanel extends JLabel implements Runnable {

		private final int[] hpos = { 84, 124, 170, 210 };
		private final int[] vpos = { 120, 180, 242, 310 };
		private final int[] size = { 11, 14, 16, 18 };

		private boolean running;

		private List<String> textOptions;
		private Random ran;
		private String oriText;

		TextPanel(int height, int width) {
			super("", LEFT);

			int index = height / 100 - 2;
			this.setBounds(hpos[index], vpos[index], width - hpos[index], 40);
			this.setFont(new Font("Sans-Serif", Font.BOLD, size[index]));
			this.setForeground(new Color(162, 161, 161));

			this.ran = new Random();
			this.ran.setSeed(System.currentTimeMillis());
			this.textOptions = new ArrayList<>();
			try {
				InputStream msgFile = this.getClass().getClassLoader().getResourceAsStream("static/splash/msgs.txt");
				if (msgFile != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(msgFile));

					String cl;
					while ((cl = br.readLine()) != null) {
						if (!cl.equals("")) {
							this.textOptions.add(cl);
						}
					}

					br.close();
				}
				else {
					this.textOptions.add("Loading");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			Collections.shuffle(this.textOptions);

			this.oriText = this.getRandomMessage();
			this.setText(this.oriText);
		}

		@Override
		public void run() {
			this.running = true;

			int count = 0;
			while (this.running) {

				if (count == 9) {
					count = 0;
					this.oriText = this.getRandomMessage();

					this.setText(this.oriText);
				}
				else if (count % 3 == 0) {
					this.setText(this.oriText);
				}

				count++;
				this.setText(this.getText() + ".");

				try {
					Thread.sleep(400);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		void stop() {
			this.running = false;
		}

		private String getRandomMessage() {
			String s = this.textOptions.get(this.ran.nextInt(this.textOptions.size()));
			return s.equals(this.oriText) && this.textOptions.size() > 1 ? this.getRandomMessage() : s;
		}
	}

	class GifPanel extends JPanel {

		Image image;

		GifPanel(String resourcePath) {
			image = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource(resourcePath));
			this.setLayout(null);
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
