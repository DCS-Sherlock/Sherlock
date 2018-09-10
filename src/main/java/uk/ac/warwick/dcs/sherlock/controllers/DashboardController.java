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
import java.util.Collections;
import java.util.Comparator;

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
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.graph.implementations.MultiGraph;
import uk.ac.warwick.dcs.sherlock.services.fileSystem.DirectoryProcessor;
import uk.ac.warwick.dcs.sherlock.Settings;
import uk.ac.warwick.dcs.sherlock.services.detection.MyEdge;
import uk.ac.warwick.dcs.sherlock.services.detection.NGramsStrategy;
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
	private Button startPreProcessing;
	@FXML
	private Button startDetection ;
	@FXML
	private Label sessionChoice;
	@FXML
	private Button generateGraph;
	@FXML
	private Button showTopSimilarities;
	@FXML
	private ProgressIndicator preprocessingIndicator;
	@FXML
	private ProgressIndicator generateReportIndicator;
	@FXML
	private ProgressIndicator generateGraphIndicator;
	@FXML
	private ProgressIndicator topSimilarityIndicator;
	@FXML
	private GridPane advancedSettingsList;
	@FXML
	private TextField minNgramLengthTextField;
	private boolean advancedSettingsVisibility = false;
	@FXML
	private ChoiceBox similarityMetricChooser;
	@FXML
	private Label selectFilePrompt;
	private int ngramLength;
	private Viewer graphViewer;
	private  ArrayList<MyEdge> edgeList;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		similarityMetricChooser.setValue("# Similar lines");
		similarityMetricChooser.setItems(similarityMetricList);
		startPreProcessing.setDisable(true);
		startDetection.setDisable(true);
		loadSession.setDisable(true);
		showTopSimilarities.setDisable(true);
		generateGraph.setDisable(true);

		newSession.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectDirectory();
				startPreProcessing.setDisable(false);
				startDetection.setDisable(false);
				System.out.println("--------------------------TRied to set the buttons to be disabled");
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
				if (!advancedSettingsVisibility) {
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
				generateGraph.setDisable(false);
				showTopSimilarities.setDisable(false);
				double size = startDetection.getHeight();
				generateReportIndicator.setProgress(-1.0f);
				generateReportIndicator.setPrefSize(size, size);
				generateReportIndicator.setVisible(true);
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

		generateGraph.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				double size = generateGraph.getHeight();
				generateGraphIndicator.setProgress(-1.0f);
				generateGraphIndicator.setPrefSize(size, size);
				generateGraphIndicator.setVisible(true);
				Task<Void> task = new Task<Void>() {
					@Override
					public Void call() throws Exception {
						generateGraph();
						return null ;
					}
				};
				task.setOnSucceeded(e -> {

					double size2 = generateGraph.getHeight()*1.5;
					generateGraphIndicator.setPrefSize(size2, size2);
					generateGraphIndicator.setProgress(1.0f);
				});
				new Thread(task).start();

			}
		});
		showTopSimilarities.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				double size = showTopSimilarities.getHeight();
				topSimilarityIndicator.setProgress(-1.0f);
				topSimilarityIndicator.setPrefSize(size, size);
				topSimilarityIndicator.setVisible(true);

				Task<ArrayList<String>> task = new Task<ArrayList<String>>() {
					@Override
					public ArrayList<String> call() throws Exception {
					return getTopSimilarities(edgeList);
					}
				};
				task.setOnSucceeded(e -> {

					double size2 = showTopSimilarities.getHeight()*1.5;
					topSimilarityIndicator.setPrefSize(size2, size2);
					topSimilarityIndicator.setProgress(1.0f);
					final Stage dialog = new Stage();
					dialog.initModality(Modality.APPLICATION_MODAL);
					VBox dialogVbox = new VBox(20);
					for (int i = 0; i < task.getValue().size(); i++){
						dialogVbox.getChildren().add(new Text(task.getValue().get(i)));
					}
					Scene dialogScene = new Scene(dialogVbox, 400, 200);
					dialog.setScene(dialogScene);
					dialog.show();
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
			try {
				String n = minNgramLengthTextField.getText();
				ngramLength = Integer.parseInt(n);
			}catch (Exception e){
				System.out.println("text field is null");
				ngramLength = 20;
			}
			NGramsStrategy nGramsStrategy = new NGramsStrategy();
			DetectionHandler dh = new DetectionHandler(setting, ngramLength, nGramsStrategy);
			System.out.println("created detection handler");
			edgeList = dh.runDetectionStrategies();
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Please ensure pre-processing has completed before attempting to run detection");
			alert.showAndWait();
		}
	}

	private void generateGraph(){
		if ( setting.isPreprocessingComplete() ) {
			MultiGraph graph = createGraph(edgeList);
			graphViewer = graph.display();
			// need to set the close policy to hide only to make it not close the entire application.
			graphViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Please ensure pre-processing has completed before attempting to run detection");
			alert.showAndWait();
		}
	}

	public MultiGraph createGraph(ArrayList<MyEdge> edgeList){
		MultiGraph similarityGraph = new MultiGraph("returnGraph");
		similarityGraph.setStrict(false);
		similarityGraph.setAutoCreate(true);
		ArrayList<MyEdge> sortedList = sortByEdgeWeight(edgeList);
		float largestEdge = (float) sortedList.get(0).getDistance()+ 1;

		for (int i = 0; i < edgeList.size(); i++){
			MyEdge edge = edgeList.get(i);
			String edgeID = Integer.toString(i);
			//add graph creates nodes if they do not exist
			similarityGraph.addEdge(edgeID, edge.getNode1(), edge.getNode2());
			// we create the edges and then get them from the graph so we don't need to make the nodes
			Edge e = similarityGraph.getEdge(edgeID);
			double weight = (double) Math.max(largestEdge-edge.getDistance(), 1)/largestEdge;
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
	private ArrayList<String> getTopSimilarities(ArrayList<MyEdge> edgeList){
		ArrayList<MyEdge> sortedList = sortByEdgeWeight(edgeList);
		ArrayList<MyEdge> topSimilarities = new ArrayList<MyEdge>(sortedList.subList(0, Math.min(10, edgeList.size())));
		ArrayList<String> stringRepresentation = new ArrayList<String>();
		for (int i = 0; i<topSimilarities.size(); i++){
			String s = topSimilarities.get(i).getNode1() + " && " + topSimilarities.get(i).getNode2() + ", similarity score: " + topSimilarities.get(i).getDistance();
			stringRepresentation.add(s);
		}
		return stringRepresentation;
	}
	private ArrayList<MyEdge> sortByEdgeWeight(ArrayList<MyEdge> edgeList){
		ArrayList<MyEdge> sortedList = new ArrayList<MyEdge>(edgeList);
		Collections.sort(sortedList, new Comparator<MyEdge>() {
			@Override
			public int compare(MyEdge z1, MyEdge z2) {
				if (z1.getDistance() < z2.getDistance())
					return 1;
				if (z1.getDistance() > z2.getDistance())
					return -1;
				return 0;
			}
		});
		return sortedList;
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
