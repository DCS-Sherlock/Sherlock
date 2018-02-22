package sherlock;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sherlock.view.gui.DirectorySelectorController;

/**
 * The main class to run the sherlock application. This class references the sherlock.viw.gui package 
 * @author Aliyah
 *
 */
public class SherlockApplication extends Application {

	// The Window the Application is presented in
	private static Stage stage;

	// This instance of the Sherlock Application
    private static SherlockApplication instance;
    
    private static BorderPane rootLayout;


    /**
	 * Return this instance of the Sherlock Application
     */
    public static SherlockApplication getInstance(){
    	return instance;

    }

    public static void main (String[] args){
    		launch(args);		   		 		// Calls the start method and runs other set up code
    }

	/**
	 *	The Entry point for the Sherlock Application
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Starting the Sherlock Application");

		try{
			stage = primaryStage;
//			goToDirectorySelector();				// Set up the directory Selection window
			goToMain();				// Set up the directory Selection window
			primaryStage.setTitle("Sherlock 2018");
			primaryStage.show();					// Present this window to the User
		} catch ( Exception e ){
			System.out.println("Unable open Directory Selector");
			e.printStackTrace();
		}
	}

//	public static void goToDirectorySelector(){
//		try {
//			replaceSceneContent("view/gui/DirectorySelector.fxml");
//		} catch ( Exception e ){
//			System.out.println("Unable replace scene with Directory Selector");
//			e.printStackTrace();
//		}
//	}	
//	
	public static void goToMain(){
		try {
			Parent root = replaceSceneContent("view/gui/GUILayout.fxml");
			
			FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SherlockApplication.class.getResource("view/gui/Dashboard.fxml"));
            AnchorPane overview = (AnchorPane) loader.load();
            System.out.println("Overview " + overview);
            ((BorderPane) root).setCenter(overview);
			System.out.println("The root "+ root);
		} catch ( Exception e ){
			System.out.println("Unable replace scene with Root Layout");
			e.printStackTrace();
		}
	}	
	
	public static void replaceCentre( String fxml ){ 
		try {
			FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SherlockApplication.class.getResource(fxml));
            AnchorPane overview = (AnchorPane) loader.load();
            
            ((BorderPane) rootLayout).setCenter(overview);
		} catch ( Exception e ){
			System.out.println("Unable replace scene with Root Layout");
			e.printStackTrace();
		}
	}	
	

	private static Parent replaceSceneContent( String fxml ) throws Exception{
		System.out.println(fxml);
		
		FXMLLoader loader = new FXMLLoader();
		Parent root = (Parent) loader.load(SherlockApplication.class.getResource(fxml), null, new JavaFXBuilderFactory());			// Get the root node of the scene replacement 
        Scene scene = stage.getScene();

        if ( scene == null ){
		 	System.out.println("Scene is null");
		 	
		 	// Create the scene with the new root node 
		 	scene = new Scene(root, 800, 600);
		 	
		 	// Set the scene to the stage
		 	stage.setScene(scene);
		} else {
		 	System.out.println("Scene is not null");
		 	System.out.println(root);
		 	scene = new Scene(root, 800, 600);
		 	stage.setScene(scene);
//		 	stage.getScene().setRoot(root);
		 	stage.sizeToScene();	
		}
        stage.sizeToScene();							// Set the size of the stage (the window) to fit the contents
        stage.centerOnScreen(); 						// Centre the stage so it is in the centre of the user screen
        rootLayout = (BorderPane) root;
        return root;
	}
}
