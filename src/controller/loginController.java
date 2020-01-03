package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.ManageUser;
import application.User;

/**
 * This is login controller
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class loginController implements Initializable{
	
	/**
	 * This is reference for manage all of data. It is Singleton pattern, so it shares all data with other files.
	 */
	ManageUser mgUsr = null;

	/**
	 * Getting user name to login
	 */
	@FXML TextField gettingUserName;

	/**
	 * login to user's albums.
	 */
	@FXML Button loginButton;
	
	/**
	 * This is login button handler.
	 * If user input admin, it will open admin page else then specific user page.
	 * 
	 * @param e ActionEvent.
	 * @throws Exception Throw exception
	 */
	@FXML public void login_button_handler(ActionEvent e) throws Exception {
		Button b = (Button) e.getSource();
		if(b==loginButton) {
			String getUser = gettingUserName.getText();
			
			if(getUser.trim().isEmpty()) {
				Alert error = new Alert(AlertType.ERROR, "Please enter userName.", ButtonType.OK);
		        error.showAndWait();
			}
			else if(getUser.toLowerCase().equals("admin")) {
				FXMLLoader adminScene = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
				Parent parent = (Parent) adminScene.load();
				adminController admin = adminScene.getController();
				Scene adminControllerScene = new Scene(parent);
				Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
				admin.start(window);
				window.setScene(adminControllerScene);
				window.show();
			}
			else {
				if(mgUsr.checkUserToLogin(getUser)) {
					FXMLLoader albumScene = new FXMLLoader(getClass().getResource("/view/album.fxml"));
					Parent parent = (Parent) albumScene.load();
					albumController album = albumScene.getController();
					Scene albumControllerScene = new Scene(parent);
					Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
					album.start(window, getUser);
					window.setScene(albumControllerScene);
					window.show();
				}
				else {
					Alert error = new Alert(AlertType.ERROR, "No Matching ID", ButtonType.OK);
			        error.showAndWait();
				}
			}
			
		}
	}

	/**
	 * start method
	 * 
	 * @param window Stage.
	 */
	public void start(Stage window) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize page. 
	 */
	@Override public void initialize(URL arg0, ResourceBundle arg1) {	
		mgUsr = ManageUser.getInstance();
		mgUsr.conductDeserializing();
		if(mgUsr.isListEmpty() || mgUsr.checkUserToLogin("stock")==false) {
//			System.out.println("empty");
			User stock = new User("stock");
			mgUsr.arrList.add(stock);
		}
		
//		for(User u: mgUsr.arrList) {
//			System.out.println("  arrlist :"+u);
//		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				mgUsr.conductSerializing();
			}
		});
	}
}
