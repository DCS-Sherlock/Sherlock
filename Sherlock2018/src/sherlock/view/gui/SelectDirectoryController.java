package sherlock.view.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;

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
		String userHome = System.getProperty("user.home");
		String destination = userHome + File.separator + "Sherlock" ;

		System.out.println("The select source button was clicked");
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		File selectedFile = directoryChooser.showDialog(null);
		
		if ( selectedFile != null ) {
			System.out.println("Chosen a file");
			DirectoryProcessor dp = new DirectoryProcessor(selectedFile);
//			Collection<File> files = dp.processDirectory();												// Determine whether the file needs extracting
			
//			// For each file in the selected directory, copy it to the Sherlock Directory using the same name
//			for (File f : files) {
//				Path source = f.toPath();
//	     		Path dest = (new File(destination)).toPath();
//
//				try {
//					Files.copy(source, dest.resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);
//				} catch ( FileAlreadyExistsException ae ){
//					System.out.println("The file already exists in "+ destination);
//				} catch (IOException e) {
//					System.out.println("Unable to copy File to "+ destination);
//					e.printStackTrace();
//				} 
//			}
		} else {
			System.out.println("Not chosen a file");
		}
	}
	
	@FXML
	protected void loadResult() {
		System.out.println("The load button was clicked");
	}
	
}
