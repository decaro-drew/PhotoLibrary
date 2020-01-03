package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * This is main class to run the program. It opens login page with title.
 * window size can't resizable.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class Photos extends Application {
	/**
	 * This is start method to show first window.
	 * 
	 * @param primaryStage this is primary stage to load login.fxml
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Photo Album - Jaehyun Kim, Drew Decaro");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	/**
	 * This is main method to run.
	 * @param args default java parameter.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
