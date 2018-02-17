package sherlock.view.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RootController {
	@FXML
	private Button overviewButton;
	@FXML
	private Button matchGraphButton;
	@FXML
	private Button comparisonButton;
	@FXML
	private Button reportButton;
	
	@FXML
	public void selectOverviewScene() {
		System.out.println("Select the overview scene");
	}
	
	@FXML
	public void selectMatchGraphScene() {
		System.out.println("Select the Match graph scene");
	}
	
	@FXML
	public void selectReportScene() {
		System.out.println("Select the report scene");
	}
	
	@FXML
	public void selectSideComparisonScene() {
		System.out.println("Select the Side by Side comparison scene");
	}
}
