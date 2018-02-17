package sherlock.view.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import sherlock.extraction.DirectoryProcessor;

public class SelectDirectoryController {
	@FXML
	private Button startSession;
	@FXML
	private Button loadSession;
	
	@FXML
	protected void selectSource() {
		
		System.out.println("The select source button was clicked");
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		File selectedFile = directoryChooser.showDialog(null);
		
		if ( selectedFile != null ) {
			System.out.println("Chosen a file");
			DirectoryProcessor dp = new DirectoryProcessor(selectedFile);
			dp.processDirectory();												// Determine whether the file needs extracting
			
			
//			Files.createDirectory(userHome);
		} else {
			System.out.println("Not chosen a file");
		}
	}
	
	@FXML
	protected void loadResult() {
		System.out.println("The load button was clicked");
	}
	
}
