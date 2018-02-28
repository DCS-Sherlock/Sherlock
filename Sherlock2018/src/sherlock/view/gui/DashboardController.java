package sherlock.view.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.sun.prism.paint.Color;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.DirectoryChooser;
import sherlock.SherlockApplication;
import sherlock.extraction.DirectoryProcessor;


import sherlock.model.analysis.preprocessing.Settings;
/**
 * @author Aliyah
 * The controller for the Dashboard scene
 */
public class DashboardController implements Initializable{
	
	Settings setting = new Settings();
	
	@FXML
	private Button newSession ;
	@FXML
	private Button loadSession ;
	@FXML
	private RadioButton cPlus;
	@FXML
	private RadioButton plainText;
	@FXML
	private RadioButton java ;
	@FXML
	private Button advancedSettings ;
	@FXML
	private Button completeSearch;
	@FXML
	private Button startPreProcessing;
	@FXML
	private Button startDetection ;
	@FXML
	private Label sessionChoice;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		newSession.setOnAction(new EventHandler<ActionEvent>() {
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
		
		advancedSettings.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				chooseAdvancedSettings();
			}
		});
		
		completeSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				startCompleteSearch();
			}
		});
		
		startPreProcessing.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				startPreProcessing();
			}
		});
		
		startDetection.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				startDetection();
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
					
					sessionChoice.setText(selectedFile.toString());
					
					setting.setSourceDirectory( new File(dp.returnNewSourcePathname(selectedFile.getName())) );
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
			    	   		// Perform check that the directory is of the expected format - must contain pre-processing folder (none-empty)
						if ( true ) {
							sessionChoice.setText(selectedFile.toString());
							setting.setSourceDirectory( selectedFile);
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
	
	private void chooseAdvancedSettings() {
		System.out.println("Choosing Advanced Settings");
	}
	
	private void startCompleteSearch() {
		System.out.println("Starting Complete Search");
	}
	
	/**
	 * Method to run when the pre-processing button has been pressed
	 * 
	 * Using the setting choices selected by the user, this method will run the specified
	 * pre-processing techniques. The setting choices are stored in the Settings and 
	 * SettingProfile classes.
	 *
	 */
	private void startPreProcessing() {
		System.out.println("Starting Pre-processing");
		
		
		
	}
	
	private void startDetection() {
		System.out.println("Starting Detection");
	}
	
}
