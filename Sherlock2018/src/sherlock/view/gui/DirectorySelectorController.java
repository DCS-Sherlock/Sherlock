package sherlock.view.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sherlock.SherlockApplication;
import sherlock.fileSystem.DirectoryProcessor;

public class DirectorySelectorController implements Initializable{
	@FXML
	private Button startSession ;
	@FXML
	private Button loadSession ;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startSession.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectDirectory();
			}
		});
		
		
		
		loadSession.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				loadDirectory();
			}
		});
	}
	
	
	private void selectDirectory() {
		System.out.println("Start Session button clicked");
		
		String userHome = System.getProperty("user.home");
		String destination = userHome + File.separator + "Sherlock" ;

		System.out.println("The select source button was clicked");
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		File selectedFile = directoryChooser.showDialog(null);
		
		if ( selectedFile != null ) {
			System.out.println("Chosen a file " + selectedFile.getName());
			
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream( selectedFile.toPath() ) ) {
		       if ( !dirStream.iterator().hasNext() ) {
		    	   		System.out.println("Chosen a file " + selectedFile.getName() + " is empty");
		       } else {
		    	   		// Put all the files in source directory into the Sherlock Directory
					DirectoryProcessor dp = new DirectoryProcessor(selectedFile, selectedFile.getName());
					
					SherlockApplication.goToMain();
		       }
		    } catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Not chosen a file");
		}
	}
	
	private void loadDirectory() {
		System.out.println("Load Session button clicked");
		
		String userHome = System.getProperty("user.home");
		String destination = userHome + File.separator + "Sherlock" ;
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(destination));
		directoryChooser.setTitle("Open Resource File");
		
		File selectedFile = directoryChooser.showDialog(null);
		if ( selectedFile != null ) {
			System.out.println("Chosen a file " + selectedFile.getName());	
			
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream( selectedFile.toPath() ) ) {
			       if ( !dirStream.iterator().hasNext() ) {
			    	   		System.out.println("Chosen a file " + selectedFile.getName() + " is empty");
			       } else {
			    	   		// Perform check that the directory is of the expected format
						if ( true ) {
							SherlockApplication.goToMain();	
						} else {
							System.out.println("Not chosen a directory of the correct format");
						}
			       }
			    } catch (IOException e) {
					e.printStackTrace();
				}
		} else {
			System.out.println("Not chosen a file");
		}
	}	
}