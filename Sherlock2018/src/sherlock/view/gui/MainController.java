package sherlock.view.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sherlock.SherlockApplication;

public class MainController implements Initializable{
	@FXML
	private Button dashboardButton;
	@FXML
	private Button overviewButton;
	@FXML
	private Button matchGraphButton;
	@FXML
	private Button comparisonButton;
	@FXML
	private Button reportButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dashboardButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectDashboardScene();
			}
		});
		
		overviewButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectOverviewScene();
			}
		});
		
		matchGraphButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectMatchGraphScene();
			}
		});
		
		comparisonButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectComparisonScene();
			}
		});
		
		reportButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle( ActionEvent event ) {
				selectReportScene();
			}
		});
		
	}
	
	public void selectDashboardScene() {
		System.out.println("Select the Dashboard scene");
		SherlockApplication.replaceCentre("view/gui/Dashboard.fxml");
	}
	
	
	public void selectOverviewScene() {
		System.out.println("Select the overview scene");
		SherlockApplication.replaceCentre("view/gui/Overview.fxml");
	}
	
	public void selectMatchGraphScene() {
		System.out.println("Select the Match graph scene");
		SherlockApplication.replaceCentre("view/gui/MatchGraph.fxml");
	}
	
	public void selectReportScene() {
		System.out.println("Select the report scene");
//		SherlockApplication.replaceCentre("");
	}
	
	public void selectComparisonScene() {
		System.out.println("Select the Side by Side comparison scene");
//		SherlockApplication.replaceCentre("");
	}
}
