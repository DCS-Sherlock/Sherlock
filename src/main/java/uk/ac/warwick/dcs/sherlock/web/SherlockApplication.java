package uk.ac.warwick.dcs.sherlock.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.net.URI;

import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;

@SpringBootApplication
public class SherlockApplication extends JFrame {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SherlockApplication.class).headless(false).run(args);

        EventQueue.invokeLater(() -> {
            SherlockApplication ex = ctx.getBean(SherlockApplication.class);
            ex.setVisible(true);
        });
    }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        JButton quitButton = new JButton("Quit Sherlock");
        quitButton.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });

        group.setHorizontalGroup(group.createSequentialGroup()
                .addPreferredGap(RELATED,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(openButton)
                .addComponent(quitButton)
        );

        group.setVerticalGroup(group.createSequentialGroup()
                .addPreferredGap(RELATED,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(group.createParallelGroup()
                        .addComponent(openButton)
                        .addComponent(quitButton))
        );

        group.linkSize(SwingConstants.HORIZONTAL, openButton, quitButton);

        pack();

        setLocationRelativeTo(null);
    }
}