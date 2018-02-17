package sherlock;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The main class to run the sherlock application. This class references the sherlock.viw.gui package 
 * @author Aliyah
 *
 */
public class SherlockApplication extends Application {

	private Stage primaryStage;
//	private BorderPane rootLayout;
	private AnchorPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sherlock 2018");
		
		initRootLayout();
		
//		showOverview();
	}
	
	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SherlockApplication.class.getResource("view/gui/SelectDirectory.fxml"));
            rootLayout = (AnchorPane) loader.load();
            
            establishWorkingDirectory();								// Create the Sherlock folder in the Users home directory if not already existing
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	private void showOverview() {
//		try {
//			FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(SherlockApplication.class.getResource("view/gui/OverviewScene.fxml"));
//            AnchorPane overviewScene = (AnchorPane) loader.load();
//
//            rootLayout.setCenter(overviewScene);
//		} catch (IOException e ) {
//			e.printStackTrace();
//		}
//		
//	}
//	
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Ensures that the Sherlock Directory exists in the users home directory. 
	 * @return			- True if the Sherlock directory exists in the users home directory
	 * 					- False if the Sherlock directory has failed to have been created in the users home directory
	 */
	private boolean establishWorkingDirectory() {
		String userHome = System.getProperty("user.home");
		String newDirectory = "Sherlock";
		
		String path = userHome + File.separator + newDirectory ;
		
		if ( ! new File(path).exists() ) {
			if (new File(path).mkdir() ) {
				System.out.println("Success Making Sherlock");
				return true;
			} else {
				System.out.println("Failed to create directory!");
			}
		} else {
			System.out.println("Directory Exists");
			return true ;
		}
		
		return false ;
	}
	
	
}
