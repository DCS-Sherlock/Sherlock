package uk.ac.warwick.dcs.sherlock.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.DirectoryChooser;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import uk.ac.warwick.dcs.sherlock.SherlockApplication;
import uk.ac.warwick.dcs.sherlock.services.detection.MyEdge;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;

import uk.ac.warwick.dcs.sherlock.Settings;

import uk.ac.warwick.dcs.sherlock.services.detection.DetectionHandler;
import uk.ac.warwick.dcs.sherlock.services.preprocessing.Preprocessor;

/**
 * @author Aliyah
 * The controller for the Dashboard scene
 */
public class DashboardController implements Initializable{
	
	private Settings setting = new Settings();
	private ObservableList<String> similarityMetricList = FXCollections.observableArrayList("# Similar lines", "Longest Run Length");
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
	@FXML
	private ProgressIndicator preprocessingIndicator;
	@FXML
	private ProgressIndicator generateReportIndicator;
	@FXML
	private GridPane advancedSettingsList;
	private boolean advancedSettingsVisibility = false;
	@FXML
	private ChoiceBox similarityMetricChooser;
	@FXML
	private Label selectFilePrompt;

	private  ArrayList<MyEdge> edgeList;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		similarityMetricChooser.setValue("# Similar lines");
		similarityMetricChooser.setItems(similarityMetricList);
		newSession.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectDirectory();
				startPreProcessing.setDisable(false);
				startDetection.setDisable(false);
			}
		});
		
		loadSession.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				loadDirectory();
				startPreProcessing.setDisable(false);
				startDetection.setDisable(false);
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
		
		advancedSettings.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (advancedSettingsVisibility == false) {
					advancedSettingsList.setVisible(true);
					selectFilePrompt.setVisible(false);
					advancedSettingsVisibility = true;
				}else {
					advancedSettingsList.setVisible(false);
					selectFilePrompt.setVisible(true);
					advancedSettingsVisibility = false;
				}
				chooseAdvancedSettings();
			}
		});
		
//		completeSearch.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle( ActionEvent event ) {
//				startCompleteSearch();
//			}
//		});
		
		startPreProcessing.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	
		    	double size = startPreProcessing.getHeight();
		    	preprocessingIndicator.setProgress(-1.0f);
				preprocessingIndicator.setPrefSize(size, size);
				preprocessingIndicator.setVisible(true);
		    	Task<Void> task = new Task<Void>() {
		    	    @Override
		    	    public Void call() throws Exception {
		    	        startPreProcessing();
		    	        return null ;
		    	    }
		    	};
		    	task.setOnSucceeded(e -> {

					double size2 = startPreProcessing.getHeight()*1.5;
					preprocessingIndicator.setPrefSize(size2, size2);
					preprocessingIndicator.setProgress(1.0f);
		    	});
		    	new Thread(task).start();
		    	
		    }
		});
		
		startDetection.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				double size = startDetection.getHeight();
				generateReportIndicator.setProgress(-1.0f);
				generateReportIndicator.setPrefSize(size, size);
				generateReportIndicator.setVisible(true);;
		    	Task<Void> task = new Task<Void>() {
		    	    @Override
		    	    public Void call() throws Exception {
		    	    	startDetection();
		    	        return null ;
		    	    }
		    	};
		    	task.setOnSucceeded(e -> {

					double size2 = startDetection.getHeight()*1.5;
					generateReportIndicator.setPrefSize(size2, size2);
					generateReportIndicator.setProgress(1.0f);
		    	});
		    	new Thread(task).start();
				
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
			System.out.println("created detection handler");
			edgeList = dh.runDetectionStrategies();

			MultiGraph graph = createGraph(edgeList);
			graph.display();
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Please ensure pre-processing has completed before attempting to run detection");
			alert.showAndWait();
		}
	}
	public static MultiGraph createGraph(ArrayList<MyEdge> edgeList){
		MultiGraph similarityGraph = new MultiGraph("returnGraph");
		similarityGraph.addAttribute("ui.stylesheet", "url('file:///C:/Users/MingXiu/IdeaProjects/test/src/main/java/styling.css')");
		similarityGraph.setStrict(false);
		similarityGraph.setAutoCreate(true);
		for (int i = 0; i < edgeList.size(); i++){
			MyEdge edge = edgeList.get(i);
			String edgeID = Integer.toString(i);
			//add graph creates nodes if they do not exist
			similarityGraph.addEdge(edgeID, edge.getNode1(), edge.getNode2());
			// we create the edges and then get them from the graph so we don't need to make the nodes
			Edge e = similarityGraph.getEdge(edgeID);
			double weight = (double) Math.max(1000-edge.getDistance(), 1)/1000.0;
			System.out.println("edge distances  are: " + edge.getDistance());
			e.addAttribute("ui.label",weight);
			e.addAttribute("layout.weight",weight);
		}
		for (Node node : similarityGraph) {
			node.addAttribute("ui.label", node.getId());
			node.addAttribute("ui.class",  "important");
		}
		return similarityGraph;
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
