package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.ManageUser;
import application.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * This class is admin controller
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class adminController implements Initializable{
	/**
	 * This is reference for manage all of data. It is Singleton pattern, so it shares all data with other files.
	 */
	private ManageUser mgUsr;
	
	/**
	 * create button
	 */
	@FXML private Button createBT;
	/**
	 * delete button
	 */
	@FXML private Button deleteBT;
	/**
	 * logout button
	 */
	@FXML private Button logoutBT;
	/**
	 * clear button
	 */
	@FXML private Button clearBT;
	
	/**
	 * Text field for getting user name 
	 */
	@FXML private TextField userNameTF;
	
	/**
	 * List view to show user.
	 */
	@FXML private ListView<User> list;
	
	/**
	 * observable list to set up list view.
	 */
	private ObservableList<User> obs;
	
	/* *****************************
	 * Button handler
	 * 	- clear
	 *  - create
	 *  - delete
	 *  - logout
	 * ****************************/
	/**
	 * This is clear button handler.
	 * If user click this button, text field will be clear.
	 * 
	 * @param e ActionEvent
	 */
	@FXML void clearBT_handler(ActionEvent e) {
		userNameTF.clear();
	}
	
	/**
	 * Create button handler.
	 * Make new instance of user.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML public void createBT_handler(ActionEvent e) {
		String getUser = userNameTF.getText();

		if(getUser.trim().isEmpty()) {
			Alert error = new Alert(AlertType.ERROR, "Please enter userName.", ButtonType.OK);
	        error.showAndWait();
			return;
		}
		
		if(mgUsr.arrList.isEmpty()) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new user?", ButtonType.YES, ButtonType.NO);
			 alert.showAndWait();
			 
			 if (alert.getResult() == ButtonType.NO) {
				 return;
			 }
			 
			addToArrayList(getUser);
			userNameTF.clear();
		}
		else if((duplicationCheck(getUser))==true) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new user?", ButtonType.YES, ButtonType.NO);
			 alert.showAndWait();
			 
			 if (alert.getResult() == ButtonType.NO) {
				 return;
			 }
			 
			addToArrayList(getUser);
			userNameTF.clear();
		}
		else if((duplicationCheck(getUser))==false) {
			Alert error = new Alert(AlertType.ERROR, "Input userName is already in the list", ButtonType.OK);
	        error.showAndWait();
			
			userNameTF.clear();
			return;
		}
	}
	
	/**
	 * Delete button handler.
	 * Remove selected user from listview.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML public void deleteBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new User", ButtonType.OK);
	        error.showAndWait();
		}
		else {
			Alert warning = new Alert(AlertType.WARNING,"Delete this User from list?", ButtonType.YES, ButtonType.NO);
	        warning.showAndWait();
	        if(warning.getResult() == ButtonType.NO){
	            return;
	        }
	
	        int idx = list.getSelectionModel().getSelectedIndex();
	        if(list.getSelectionModel().getSelectedItem().getUserName().equals("stock")) {
	        	Alert error = new Alert(AlertType.ERROR, "You can't delete stock username", ButtonType.OK);
	            error.showAndWait();
	        	return;
	        }
	        obs.remove(idx);
	        mgUsr.arrList.remove(idx);
		}
	}
	
	/**
	 * logout button handler.
	 * It saves all data through serialize.
	 * 
	 * @param e ActionEvent.
	 * @throws Exception Throw exception.
	 */
	@FXML public void logoutBT_handler(ActionEvent e) throws Exception {
		mgUsr.conductSerializing();
		
		FXMLLoader loginScene = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent parent = (Parent) loginScene.load();
        loginController login = loginScene.getController();
        Scene loginControllerScene = new Scene(parent);
        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
        login.start(window);
        window.setScene(loginControllerScene);
        window.show();
	}
	
	/* *****************************
	 * Helper functions
	 * *****************************/
	/**
	 * Helper function.
	 * Add to ArrayList with create button handler.
	 * 
	 * @param getName getting user name to create new User instance.
	 */
	private void addToArrayList(String getName) {
		User newUser = new User(getName);
		mgUsr.arrList.add(newUser);
		obs = FXCollections.observableArrayList(mgUsr.arrList);

		setOnListView();
	}
	
	/**
	 * Set on list view after addToArrayList method.
	 */
	private void setOnListView() {
		list.setItems(obs);
		list.setCellFactory(new Callback<ListView<User>, ListCell<User>>(){

			@Override
			public ListCell<User> call(ListView<User> p) {
				
				ListCell<User> cell = new ListCell<User>() {
					
					@Override 
					protected void updateItem(User s, boolean bln) {
						super.updateItem(s, bln);
						if(s != null) {
							setText(s.getUserName());
						}else
							setText("");
					}
				};
				return cell;
			}
		});
	}
	
	/**
	 * Check duplicate user name.
	 * 
	 * @param userName getting user name to compare it.
	 * @return boolean type. True or false.
	 */
	private boolean duplicationCheck(String userName) {
		boolean ret = true;
		if(obs.isEmpty()) {
			ret = true;
		}
		else {
			for(int i=0; i<obs.size(); i++) {
				if(obs.get(i).getUserName().compareTo(userName) == 0) {
					ret = false;
					break;
				}
				else
					ret = true;
			}
		}
		return ret;
	}

	/**
	 * Initialize current controller
	 */
	@Override public void initialize(URL arg0, ResourceBundle arg1) {
		mgUsr = ManageUser.getInstance();
		mgUsr.conductDeserializing();
		obs = FXCollections.observableArrayList(mgUsr.arrList);
		setOnListView();
		
		if(mgUsr.isListEmpty() || mgUsr.checkUserToLogin("stock")==false) {
//			System.out.println("empty");
			addToArrayList("stock");
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				mgUsr.conductSerializing();
			}
		});
	}
	
	/**
	 * Start method not used
	 * @param mainStage stage
	 */
	public void start(Stage mainStage) {
		
	}
}