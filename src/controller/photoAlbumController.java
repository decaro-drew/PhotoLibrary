package controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Album;
import application.ManageUser;
import application.Photo;
import application.SearchHelper;
import application.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * This is photo album controller
 * When user open specific album then it shows thumnail and photo with caption.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class photoAlbumController implements Initializable{
	/**
	 * This is reference for manage all of data. It is Singleton pattern, so it shares all data with other files.
	 */
	private ManageUser mgUsr;
	/**
	 * Reference of album to get current album.
	 */
	private Album currentAlbum;
	/**
	 * Reference of user to get current user.
	 */
	private User currentUser;
	/**
	 * Reference of photo to manage searched photo
	 */
	private Photo tempPhoto; //for searching
	/**
	 * Reference of album to manage searched album
	 */
	private Album tempAlbum;
	/**
	 * getting user name
	 */
	private String userName;
	/**
	 * date format as yyyy-mm--dd 
	 */
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat ( "yyyy-MM-dd");
	/**
	 * keep previous stage
	 */
	private Stage tmpStage;
	/**
	 * If it is true, then it is from result of search. If false, it is from original album.
	 */
	private boolean searchResult;
	
	/**
	 * Return to album
	 */
	@FXML private Button backBT;
	/**
	 * Add photo button
	 */
	@FXML private Button addBT;
	/**
	 * delete photo
	 */
	@FXML private Button deleteBT;
	/**
	 * open specific photo
	 */
	@FXML private Button openBT;
	/**
	 * move photo from current album to another album
	 */
	@FXML private Button moveBT;
	/**
	 * copy photo
	 */
	@FXML private Button copyBT;
	/**
	 * recaption button
	 */
	@FXML private Button recaptionBT;
	/**
	 * logout button
	 */
	@FXML private Button logoutBT;
	/**
	 * Search button
	 */
	@FXML private Button searchBT;
	/**
	 * To show album name
	 */
	@FXML private Text AL;
	
	/**
	 * List view for showing photo as thumbnail
	 */
	@FXML private ListView<Photo> list;
	/**
	 * keep photo 
	 */
	ArrayList<Photo> arrlist;
	/**
	 * Observable list to set on the list view.
	 */
	private ObservableList<Photo> obs;
	
	/* *****************************
	 * Button handler
	 * 	- add
	 *  - move
	 *  - copy
	 *  - recaption
	 *  - delete
	 *  - logout
	 *  - back
	 *  - open
	 * ****************************/
	
	/**
	 * This is search button handler
	 * 
	 * @param e ActionEvent
	 * @throws Exception throw exception
	 */
	@FXML private void searchBT_handler(ActionEvent e) throws Exception {
		FXMLLoader searchScene = new FXMLLoader(getClass().getResource("/view/dateSearch.fxml"));
		Parent parent = (Parent) searchScene.load();
		searchController search = searchScene.getController();
		Scene photoControllerScene = new Scene(parent);
		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		search.start(window, currentUser.getUserName(), currentAlbum, false); //false means it is opened from photo album window.
		window.setScene(photoControllerScene);
		window.show();
	}
	
	/**
	 * This is add button handler
	 * 
	 * @param e ActionEvent
	 * @throws Exception throws exceptions.
	 */
	@FXML private void addBT_handler(ActionEvent e) throws Exception {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("add photo");
		File file = chooser.showOpenDialog(null);
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.bmp", "*.png", "*.jpeg", "jpg"));

		if(file!=null) {
			String imagepath = file.toURI().toURL().toString();
			for(Photo a : currentAlbum.photos) {
				String[] splitUrl = imagepath.split("/");
				String[] splitUrlPhoto = a.getUrl().split("/");
				if(splitUrlPhoto[splitUrlPhoto.length-1].equals(splitUrl[splitUrl.length-1])) {
					Alert warning = new Alert(AlertType.WARNING,"Selected name of photo already exist in the list\nDo you want to add duplicate?", ButtonType.YES, ButtonType.NO);
					warning.showAndWait();
					if(warning.getResult() == ButtonType.YES)
						break;
					if(warning.getResult() == ButtonType.NO){
						return;
					}
				}
			}
			
			TextInputDialog dialog = new TextInputDialog("");
			dialog.initOwner(tmpStage); 
			dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
			dialog.setTitle("Add Photo");
			dialog.setContentText("Caption: ");
			
			Optional<String> result = dialog.showAndWait();
			if(result.isPresent())
				addToArrayList(imagepath,result.get());
			else
				return;

		}	
		else {
			Alert error = new Alert(AlertType.INFORMATION, "Nothing Selected.", ButtonType.OK);
			error.showAndWait();
		}
	}
	
	/**
	 * Move button handler
	 * 
	 * @param e ActionEvent
	 */ 
	@FXML private void moveBT_handler(ActionEvent e) {
		
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
	        error.showAndWait();			
		}
		else {
			ListView<Album> movinglistview = new ListView<Album>();
			Button okBT = new Button("Ok");
			ObservableList<Album> tmpObs = FXCollections.observableArrayList(currentUser.albums);
			
			movinglistview.setItems(tmpObs);
			movinglistview.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>(){

				@Override
				public ListCell<Album> call(ListView<Album> p) {
					
					ListCell<Album> cell = new ListCell<Album>() {
						
						@Override 
						protected void updateItem(Album s, boolean bln) {
							super.updateItem(s, bln);
							if(s != null) {
								setText(s.getAlbumName());
//								setDisable(true);
							}else
								setText("");
						}
					};
					return cell;
				}
			});
			VBox root = new VBox(10, movinglistview, okBT);
			Stage newDialog = new Stage();
			newDialog.setTitle("Move Photo");
			Scene newDialogScene = new Scene(root);
			newDialog.setScene(newDialogScene);
			newDialog.initModality(Modality.WINDOW_MODAL);
			newDialog.initOwner(tmpStage);
			newDialog.show();
			
			okBT.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent e) {
					if(movinglistview.getSelectionModel().getSelectedIndex() == -1) {
						Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
				        error.showAndWait();			
					}
					else {
						String getAlbumName = movinglistview.getSelectionModel().getSelectedItem().getAlbumName();
						if(getAlbumName != null)
							newDialog.close();
						
						Album toMoveAlbum = currentUser.getSpecificAlbum(getAlbumName);
						if(currentAlbum.getAlbumName().equals(movinglistview.getSelectionModel().getSelectedItem().getAlbumName())) {
							Alert error = new Alert(AlertType.ERROR, "You can't move to current album", ButtonType.OK);
					        error.showAndWait();
						}
						else {
							Photo movePhoto = list.getSelectionModel().getSelectedItem();
							Photo copyPhoto = null;
							copyPhoto = new Photo(movePhoto);
							
							int sidx= currentAlbum.getIndex(movePhoto); // need to know which photo is. (from search)
							if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
					            tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName());
					            tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName()).getSpecificPhoto(movePhoto);
							}
							
							Date time = new Date();
							if(searchResult)
								tempPhoto.setDate(time);
							copyPhoto.setDate(time);
							
							int idx = list.getSelectionModel().getSelectedIndex();
							for(Photo a : toMoveAlbum.photos) {
								String[] splitUrl = movePhoto.getUrl().split("/");
								String[] splitUrlPhoto = a.getUrl().split("/");
								if(splitUrlPhoto[splitUrlPhoto.length-1].equals(splitUrl[splitUrl.length-1])) {
									Alert warning = new Alert(AlertType.WARNING,"Selected name of photo already exist in the list\nDo you want to move anyway?", ButtonType.YES, ButtonType.NO);
									warning.showAndWait();
									if(warning.getResult() == ButtonType.YES)
										break;
									if(warning.getResult() == ButtonType.NO){
										return;
									}
								}
							}
							
							toMoveAlbum.photos.add(copyPhoto);
							
							obs.remove(idx);
							currentAlbum.photos.remove(idx);
							if(searchResult) {
//								Photo p = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName()).getSpecificPhoto(tempPhoto.getUrl());
								int ridx = tempAlbum.getIndex(tempPhoto);
								tempAlbum.photos.remove(ridx); 
							}
						}
					}
				}
			});
		}
	}
	
	/**
	 * Copy button handler.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void copyBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
	        error.showAndWait();			
		}
		else {
			ListView<Album> movinglistview = new ListView<Album>();
			Button okBT = new Button("Ok");
			ObservableList<Album> tmpObs = FXCollections.observableArrayList(currentUser.albums);
			
			movinglistview.setItems(tmpObs);
			movinglistview.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>(){

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
			VBox root = new VBox(10, movinglistview, okBT);
			Stage newDialog = new Stage();
			newDialog.setTitle("Copy Photo");
			Scene newDialogScene = new Scene(root);
			newDialog.setScene(newDialogScene);
			newDialog.initModality(Modality.WINDOW_MODAL);
			newDialog.initOwner(tmpStage);
			newDialog.show();
			
			okBT.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent e) {
					if(movinglistview.getSelectionModel().getSelectedIndex() == -1) {
						Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Album", ButtonType.OK);
				        error.showAndWait();			
					}
					else {
						String getAlbumName = movinglistview.getSelectionModel().getSelectedItem().getAlbumName();
						if(getAlbumName != null)
							newDialog.close();
						
						Album toMoveAlbum = currentUser.getSpecificAlbum(getAlbumName);
						if(currentAlbum.getAlbumName().equals(movinglistview.getSelectionModel().getSelectedItem().getAlbumName())) {
							Alert error = new Alert(AlertType.ERROR, "You can't copy to current album", ButtonType.OK);
					        error.showAndWait();
						}
						else {
							Photo movePhoto = list.getSelectionModel().getSelectedItem();
							Photo copyPhoto = null;
//							copyPhoto = (Photo) CloneUtils.clone(movePhoto);
							copyPhoto = new Photo(movePhoto);
							Date time = new Date();
							copyPhoto.setDate(time);
							
							for(Photo a : toMoveAlbum.photos) {
								String[] splitUrl = copyPhoto.getUrl().split("/");
								String[] splitUrlPhoto = a.getUrl().split("/");
								if(splitUrlPhoto[splitUrlPhoto.length-1].equals(splitUrl[splitUrl.length-1])) {
									Alert warning = new Alert(AlertType.WARNING,"Selected name of photo already exist in the list\nDo you want to copy anyway?", ButtonType.YES, ButtonType.NO);
									warning.showAndWait();
									if(warning.getResult() == ButtonType.YES)
										break;
									if(warning.getResult() == ButtonType.NO){
										return;
									}
								}
							}
							toMoveAlbum.photos.add(copyPhoto);
						}
					}
				}
			});
		}
	}
	
	/**
	 * Recaption button handler
	 * 
	 * @param e ActionEvent
	 */
	@FXML private void recaptionBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Photo", ButtonType.OK);
			error.showAndWait();			
		}
		else {
			Photo item = list.getSelectionModel().getSelectedItem();
			currentAlbum.getSpecificPhoto(item);

			int index = list.getSelectionModel().getSelectedIndex();
			TextInputDialog dialog = new TextInputDialog(item.getCaption());
			dialog.initOwner(tmpStage); 
			dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
			dialog.setTitle("Edit photo caption");
			dialog.setContentText("Caption: ");

			Photo recapPhoto = list.getSelectionModel().getSelectedItem(); 
			int sidx= currentAlbum.getIndex(recapPhoto);
			if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
	            tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName());
	            tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName()).getSpecificPhoto(recapPhoto);
			}
			
			Optional<String> result = dialog.showAndWait();
			if(result.isPresent()) {
				currentAlbum.photos.get(index).setCaption(result.get());
//				currentAlbum.getSpecificPhoto(item.getUrl()).setCaption(result.get());
				if(searchResult) 
					tempAlbum.photos.get(index).setCaption(result.get());
			}
			else
				return;

			Date time = new Date();
			currentAlbum.getSpecificPhoto(item).setDate(time);
			if(searchResult) 
				tempAlbum.photos.get(index).setCaption(result.get());
			
			if (result.isPresent())
				obs.set(index, item);
		}
	}
	
	/**
	 * Delete button handler 
	 * 
	 * @param e ActionEvent
	 */
	@FXML private void deleteBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Photo", ButtonType.OK);
			error.showAndWait();			
		}
		else {
			Alert warning = new Alert(AlertType.WARNING,"Delete this Photo from list?", ButtonType.YES, ButtonType.NO);
			warning.showAndWait();
			if(warning.getResult() == ButtonType.NO){
				return;
			}
			
			Photo deletePhoto = list.getSelectionModel().getSelectedItem();
			int sidx= currentAlbum.getIndex(deletePhoto); // need to know which photo is. (from search)
			if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
	            tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName());
	            tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(sidx).getAlbumName()).getSpecificPhoto(deletePhoto);
			}
			
			int idx = list.getSelectionModel().getSelectedIndex();
			obs.remove(idx);
			currentAlbum.photos.remove(idx);
			if(searchResult) {
				int ridx = tempAlbum.getIndex(tempPhoto);
				tempAlbum.photos.remove(ridx); 
			}
		}
	}
	
	/**
	 * Logout button handler
	 * 
	 * @param e ActionEvent
	 * @throws Exception throws exception
	 */
	@FXML private void logoutBT_handler(ActionEvent e) throws Exception {
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
	
	/**
	 * back button handler
	 * 
	 * @param e ActionEvent
	 * @throws Exception throws exception.
	 */
	@FXML private void backBT_handler(ActionEvent e) throws Exception {
		mgUsr.conductSerializing();
		
		FXMLLoader albumScene = new FXMLLoader(getClass().getResource("/view/album.fxml"));
		Parent parent = (Parent) albumScene.load();
		albumController album = albumScene.getController();
		Scene albumControllerScene = new Scene(parent);
		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		album.start(window, userName);
		window.setScene(albumControllerScene);
		window.show();
	}
	
	/**
	 * open button handler
	 * 
	 * @param e ActionEvent
	 * @throws Exception throws exception
	 */
	@FXML private void openBT_handler(ActionEvent e) throws Exception {
		mgUsr.conductSerializing();
		
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly or add new Photo", ButtonType.OK);
			error.showAndWait();			
		}
		else {
			Photo item = list.getSelectionModel().getSelectedItem();
			FXMLLoader photoScene = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
			Parent parent = (Parent) photoScene.load();
			photoController photo = photoScene.getController();
			Scene photoControllerScene = new Scene(parent);
			Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			photo.start(window, currentAlbum, item, currentUser.getUserName(),searchResult);
			window.setScene(photoControllerScene);
			window.show();
		}
	}
	
	/* *****************************
	 * Helper functions
	 * *****************************/
	
	/**
	 * Helper method
	 * create new instance of photo
	 * 
	 * @param url get url to add photo
	 * @param caption get caption to add photo
	 */
	private void addToArrayList(String url, String caption) {
		Photo newPhoto = new Photo(url,caption);
		Date time = new Date();
		newPhoto.setDate(time);
		
		currentAlbum.photos.add(newPhoto);
		obs = FXCollections.observableArrayList(currentAlbum.photos);
		
		setOnListView();
	}
	
	/**
	 * after add to arraylist method, it sets on the list view.
	 */
	private void setOnListView() {
		list.setItems(obs);
		list.setCellFactory(param -> new ListCell<Photo>() {
			private ImageView imageView = new ImageView();
			VBox v = new VBox();
			Pane p = new Pane();
			Label l = new Label("");
			Image image;
			{
				v.getChildren().addAll(imageView,l,p);
			}
			@Override
			public void updateItem(Photo inn, boolean empty) {
				super.updateItem(inn, empty);
				
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } 
                else {
                    image = new Image(inn.getUrl());
                    imageView.setFitHeight(183);
                    imageView.setFitWidth(278);
                    imageView.setImage(image);
                    l.setText(inn.toString());
                    setGraphic(v);                    
                }
			}
		});
		mgUsr.conductSerializing();
	}
	
	/**
	 * check duplicate of string.
	 * 
	 * @param url getting url to compare
	 * @return return boolean
	 */
	private boolean duplicationCheck(String url) {
		boolean ret = true;
		if(obs.isEmpty()) {
			ret = true;
		}
		else {
			for(int i=0; i<obs.size(); i++) {
				if(obs.get(i).getUrl().compareTo(url) == 0) {
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
	 * initialize
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	/**
	 * This is start method which have previous stage, album, user and is search result or not.
	 * @param mainStage previous stage
	 * @param album specific album
	 * @param user specific photo
	 * @param search is search or not
	 */
	public void start(Stage mainStage, Album album, String user, boolean search) {
		tmpStage = mainStage;
		mgUsr = mgUsr.getInstance();
		mgUsr.conductDeserializing();
		userName = user;
		searchResult = search;
		
		currentUser = mgUsr.getUser(user);
		if(!search) {
			currentAlbum = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName());

		}
		else {
			currentAlbum = album;
		}
		obs = FXCollections.observableArrayList(currentAlbum.photos);
		setOnListView();
		
		if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
            tempAlbum = currentUser.getSpecificAlbum(currentAlbum.getAlbumName());
		}
		
		AL.setText(album.getAlbumName());
		
//		if(album.getAlbumName().equals("stock")){
//			if(duplicationCheck("img/stock/1.png")) {
//				addToArrayList("img/stock/1.png", "rutgers1");
//			}
//			if(duplicationCheck("img/stock/2.png")) {
//				addToArrayList("img/stock/2.png", "rutgers1");
//			}
//			if(duplicationCheck("img/stock/3.png")) {
//				addToArrayList("img/stock/3.png", "rutgers1");
//			}
//			if(duplicationCheck("img/stock/4.png")) {
//				addToArrayList("img/stock/4.png", "rutgers1");
//			}
//			if(duplicationCheck("img/stock/5.png")) {
//				addToArrayList("img/stock/5.png", "rutgers1");
//			}
//		}
		if(search) {
			addBT.setDisable(true);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				mgUsr.conductSerializing();
			}
		});
	}
}
