package sherlock.view.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.prism.paint.Color;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.DirectoryChooser;
import sherlock.SherlockApplication;
import sherlock.fileSystem.DirectoryProcessor;
import sherlock.model.analysis.Settings;
import sherlock.model.analysis.detection.DetectionHandler;
import sherlock.model.analysis.preprocessing.Preprocessor;
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
	private CheckBox originalOpt;
	@FXML
	private CheckBox NoWSOpt;
	@FXML
	private CheckBox NoWSComsOpt ;
	@FXML
	private CheckBox NoComOpt ;
	@FXML
	private CheckBox CommentsOpt ;
	@FXML
	private CheckBox tokenOpt ;
	@FXML
	private CheckBox WSPatternOpt ;
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
		
		originalOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				try {
					setting.getOriginalProfile().setInUse( originalOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					originalOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
			}
		});
		
		NoWSOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				try{
					setting.getNoWSProfile().setInUse( NoWSOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					NoWSOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
			}
		});
		
		NoWSComsOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				try {
					setting.getNoCWSProfile().setInUse( NoWSComsOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					NoWSComsOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
			}
		});
		
		NoComOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {	
				try {
					setting.getNoCommentsProfile().setInUse( NoComOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					NoComOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
			}
		});
		
		CommentsOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {	
				try {
					setting.getCommentsProfile().setInUse( CommentsOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					CommentsOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
			}
		});
		
		tokenOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				try {
					setting.getTokenisedProfile().setInUse( tokenOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					tokenOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
			}
				
		});
		
		WSPatternOpt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				try{
					setting.getWSPatternProfile().setInUse( WSPatternOpt.isSelected() );
				} catch ( IndexOutOfBoundsException e ) {
					WSPatternOpt.setSelected(false);
					Alert alert = new Alert(AlertType.ERROR, "Please select a session to use before choosing settings!");
					alert.showAndWait();
				}
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
					
					setting.setSourceDirectory( new File(dp.returnNewSourceDirectory(selectedFile.getName())) );

					//	Initialise Settings
					initialiseSettings(true);
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
							setting.setSourceDirectory( selectedFile );

							//	Load Settings
							initialiseSettings(false);
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
	
	private void initialiseSettings(boolean isNewSession) {
		if ( isNewSession ) {
			setting.initialiseDefault();
		} else {
			System.out.println("Load Previous Settings");
			setting.loadSettings();

		}
		updateSettingDisplay();
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
		Preprocessor p = new Preprocessor(setting);
		
		/*
		 * Update GUI to say that pre-processing is complete
		 */
	}
	
	private void startDetection() {
		
		/*Check that preprocessing has completed */
		if ( setting.isPreprocessingComplete() ) {
			System.out.println("Starting Detection");
			
			DetectionHandler dh = new DetectionHandler(setting);
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Please ensure pre-processing has completed before attempting to run detection");
			alert.showAndWait();
		}
	}
	
	/**
	 * When a session has been loaded, the setting configuration (which may differ from default) must be displayed
	 * in the GUIs check-boxes
	 */
	private void updateSettingDisplay() {
		System.out.println("Update Setting Display");
		List<Boolean> statuses = setting.getInUseStatus();
		
		List<CheckBox> checkboxes = getAllSettingsCheckboxes();
		System.out.println("Checkboxes: " + checkboxes.size());
		/* For each setting profile, set the respective checkbox to the actual status */
		for ( int i = 0; i < statuses.size() - 1; i++ ) {
			boolean b = statuses.get(i);
			System.out.println("Status: " + b);
			CheckBox setting = checkboxes.get(i);
			setting.setSelected(b);
		}
	}
	
	private List<CheckBox> getAllSettingsCheckboxes(){
		List<CheckBox> cb = new LinkedList<CheckBox>();
		
		cb.add(originalOpt);
		cb.add(NoWSOpt);
		cb.add(NoWSComsOpt);
		cb.add(NoComOpt);
		cb.add(CommentsOpt);
		cb.add(tokenOpt);
		cb.add(WSPatternOpt);
		return cb;
	}
	
}
