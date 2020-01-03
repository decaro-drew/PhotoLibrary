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
import java.util.Optional;
import java.util.ResourceBundle;

import application.Album;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * This class is for album control.
 * Album controller is working with album.java file and album.fxml. 
 * It implements Serializable and Initializable interfaces.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class albumController implements Serializable, Initializable{
	/**
	 * This is reference for manage all of data. It is Singleton pattern, so it shares all data with other files.
	 */
	private ManageUser mgUsr;
	
	/**
	 * This is for holding current user.
	 */
	private User currentUser;
	
	/**
	 * This is for having user name. 
	 */
	private String userName;
	
	/**
	 * This is for keep previous stage to return.
	 */
	private Stage tmpStage;
	
	
	/**
	 * Button for create album
	 */
	@FXML private Button creteBT;
	
	/**
	 * Button for remove
	 */
	@FXML private Button removeBT;
	
	/**
	 * Button for open album
	 */
	@FXML private Button openBT;
	
	/**
	 * Button for rename album
	 */
	@FXML private Button renameBT;
	
	/**
	 * BUtton for logout
	 */
	@FXML private Button logoutBT;
	
	/**
	 * Button for clear text field
	 */
	@FXML private Button clearBT;
	
	/**
	 * Button for search Photo.
	 */
	@FXML private Button searchBT;

	/**
	 * Text field to get album name to create.
	 */
	@FXML private TextField albumNameTF;
	
	/**
	 * Text for show current user.
	 */
	@FXML private Text ID;
	
	/**
	 * ListView for showing current albums.
	 */
	@FXML private ListView<Album> list;
	
	/**
	 * ObservableList for setting up listView.
	 */
	private ObservableList<Album> obs;
	
	/* *****************************
	 * Button handler
	 * 	- clear
	 *  - create
	 *  - delete
	 *  - open
	 *  - rename
	 *  - logout
	 * ****************************/
	/**
	 * This is search Button handler.
	 * When user click search button, it will open search window. 
	 * 
	 * @param e ActionEvent.
	 * @throws Exception to throw exception if it has problem.
	 */
	@FXML private void searchBT_handler(ActionEvent e) throws Exception {
		FXMLLoader searchScene = new FXMLLoader(getClass().getResource("/view/dateSearch.fxml"));
		Parent parent = (Parent) searchScene.load();
		searchController search = searchScene.getController();
		Scene photoControllerScene = new Scene(parent);
		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		Album album = null;
		search.start(window, currentUser.getUserName(),album, true); //true means it is opened from Album window.
		window.setScene(photoControllerScene);
		window.show();
	}
	
	/**
	 * This is clear button handler.
	 * Clear text in text field. 
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void clearBT_handler(ActionEvent e) {
		albumNameTF.clear();
	}
	
	/**
	 * This is create button handler.
	 * When user click this button with text field, it make new Album instance with helper function.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void createBT_handler(ActionEvent e) {
		String getAlbum = albumNameTF.getText();

		if(getAlbum.trim().isEmpty()) {
			Alert error = new Alert(AlertType.ERROR, "Please enter album.", ButtonType.OK);
	        error.showAndWait();
			return;
		}
		
		if(currentUser.albums.isEmpty()) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new album?", ButtonType.YES, ButtonType.NO);
			 alert.showAndWait();
			 
			 if (alert.getResult() == ButtonType.NO) {
				 return;
			 }
			
			addToArrayList(getAlbum);
			albumNameTF.clear();
		}
		else if((duplicationCheck(getAlbum))==true) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to add new album?", ButtonType.YES, ButtonType.NO);
			 alert.showAndWait();
			 
			 if (alert.getResult() == ButtonType.NO) {
				 return;
			 }
			
			addToArrayList(getAlbum);
			albumNameTF.clear();
		}
		else if((duplicationCheck(getAlbum))==false) {
			Alert error = new Alert(AlertType.ERROR, "Input album is already in the list", ButtonType.OK);
	        error.showAndWait();
			
	        albumNameTF.clear();
			return;
		}
	}
	
	/**
	 * This is delete button handler.
	 * When user click the button with specific album from listview, then album will be removed from ArrayList and observableList.
	 * 
	 * @param e ActionEvent
	 */
	@FXML private void deleteBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
	        error.showAndWait();			
		}
		else {
			Alert warning = new Alert(AlertType.WARNING,"Delete this Album from list?", ButtonType.YES, ButtonType.NO);
	        warning.showAndWait();
	        if(warning.getResult() == ButtonType.NO){
	            return;
	        }
	
	        int idx = list.getSelectionModel().getSelectedIndex();
	        obs.remove(idx);
	        currentUser.albums.remove(idx);
	        mgUsr.conductSerializing();
		}
	}
	
	/**
	 * This is open button handler.
	 * User want to open specific album, then select album from listview and click this button.
	 * 
	 * @param e ActionEvent
	 * @throws Exception Throw exception. 
	 */
	@FXML private void openBT_handler(ActionEvent e) throws Exception {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
	        error.showAndWait();			
		}
		else {
			mgUsr.conductSerializing();
			
			Album selectedAlbum = list.getSelectionModel().getSelectedItem();
			String albumName = selectedAlbum.getAlbumName();
			FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
	        Parent parent = (Parent) photoAlbumScene.load();
	        photoAlbumController photoAlbum = photoAlbumScene.getController();
	        Scene photoAlbumControllerScene = new Scene(parent);
	        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
	        photoAlbum.start(window,selectedAlbum,userName, false); //false means that it is not open by searchController.
	        window.setScene(photoAlbumControllerScene);
	        window.show();
		}
		
	}
	
	/**
	 * This is rename button handler.
	 * 
	 * @param e ActionEvent
	 */
	@FXML private void renameBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
	        error.showAndWait();			
		}
		else {
			Album item = list.getSelectionModel().getSelectedItem();
			int index = list.getSelectionModel().getSelectedIndex();
			TextInputDialog dialog = new TextInputDialog(item.getAlbumName());
			dialog.initOwner(tmpStage); 
			dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
			dialog.setTitle("List Item");
//			dialog.setHeaderText("Selected Item (Index: " + index + ")");
			dialog.setContentText("Enter name: ");
			
			Optional<String> result = dialog.showAndWait();
			if(result.isPresent())
				item.setAlbumName(result.get());
			else
				return;
			if (result.isPresent())
				obs.set(index, item);
		}
	}
	
	/**
	 * Logout button handler. 
	 * If program exit then it is serialized all date.
	 * 
	 * @param e ActionEvent
	 * @throws Exception Throws exception
	 */
	public void logoutBT_handler(ActionEvent e) throws Exception {
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
	 * Helper function to create new Album instance and set on list view.
	 *  
	 * @param getName String type of album name.
	 */
	private void addToArrayList(String getName) {
		Album newAlbum = new Album(getName);
		currentUser.albums.add(newAlbum);
		obs = FXCollections.observableArrayList(currentUser.albums);
		
		setOnListView();
	}
	
	/**
	 * Set on the list view after working with addToArrayList method.
	 */
	private void setOnListView() {
		list.setItems(obs);
		list.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>(){

			@Override
			public ListCell<Album> call(ListView<Album> p) {
				
				ListCell<Album> cell = new ListCell<Album>() {
					
					@Override 
					protected void updateItem(Album s, boolean bln) {
						super.updateItem(s, bln);
						if(s != null) {
							setText(s.getAlbumName());
						}else
							setText("");
					}
				};
				return cell;
			}
		});
	}
	
	
	/**
	 * check duplicate album name.
	 * 
	 * @param albumName get album name to check duplication.
	 * @return boolean true or false
	 */
	private boolean duplicationCheck(String albumName) {
		boolean ret = true;
		if(obs.isEmpty()) {
			ret = true;
		}
		else {
			for(int i=0; i<obs.size(); i++) {
				if(obs.get(i).getAlbumName().compareTo(albumName) == 0) {
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
	 * initialize method
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	/**
	 * Start method to keep date with login page. 
	 * 
	 * @param mainStage keep login page 
	 * @param user get user name to get current user.
	 */
	public void start(Stage mainStage, String user) {
		tmpStage = mainStage;
		userName = user;
		mgUsr = mgUsr.getInstance();
		mgUsr.conductDeserializing();
		
		currentUser = mgUsr.getUser(user);
		obs = FXCollections.observableArrayList(currentUser.albums);
		setOnListView();
		
		ID.setText(user);
		
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				mgUsr.conductSerializing();
			}
		});
	}
}
