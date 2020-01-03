package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.GregorianCalendar;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import application.Album;
import application.ManageUser;
import application.Photo;
import application.SearchHelper;
import application.Tag;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.util.GregorianCalendar;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class searchController implements Initializable{
	/**
	 * keep previous stage
	 */
	private Stage tmpStage;
	/**
	 * This is reference for manage all of data. It is Singleton pattern, so it shares all data with other files.
	 */
	private ManageUser mgUsr;
	/**
	 * Reference of user to get current user.
	 */
	private User currentUser;
	/**
	 * Reference of album to get current album.
	 */
	private Album currentAlbum;
	/**
	 * getting user name
	 */
	private String userName;
	/**
	 * if all album search then true, if specific album then false
	 */
	private boolean windowFromAlbum; //if all album search then true, if specific album then false
	
	/**
	 * Return to album
	 */
	@FXML private Button backBT;
	/**
	 * button for tag search
	 */
	@FXML private Button tagSearchBT;
	/**
	 * button for date search
	 */
	@FXML private Button dateSearchBT;

	/**
	 * Radio button for one type
	 */
	@FXML private RadioButton oneTypeRB;
	/**
	 * Radio button for two type
	 */
	@FXML private RadioButton twoTypeRB;
	/**
	 * Radio button for and search
	 */
	@FXML private RadioButton andRB;
	/**
	 * Radio button for or search
	 */
	@FXML private RadioButton orRB;
	/**
	 * Radio button for creating new album from search result
	 */
	@FXML private RadioButton tagCreatetAlbumRB;
	/**
	 * Radio button for creating new album from search result
	 */
	@FXML private RadioButton dateCreatetAlbumRB;
	/**
	 * Radio button for date
	 */
	@FXML private RadioButton dateRB;
	/**
	 * Radio button for tag
	 */
	@FXML private RadioButton tagRB;

	/**
	 * Label from date
	 */
	@FXML private Label fromLB;
	/**
	 * label to date
	 */
	@FXML private Label toLB;

	/**
	 * Date picker
	 */
	@FXML private DatePicker fromDP;
	/**
	 * Date picker
	 */
	@FXML private DatePicker toDP;
	
	/**
	 * get tag value
	 */
	@FXML private TextField oneTF;
	/**
	 * get tag value
	 */
	@FXML private TextField twoTF;
	
	/**
	 * combo box for tag types
	 */
	@FXML private ComboBox<String> oneCB;
	/**
	 * combo box for tag types
	 */
	@FXML private ComboBox<String> twoCB;
	
	/**
	 * ObservableList for showing tags
	 */
	private ObservableList<String> options;
	/**
	 * Searched photos are saved to this arrayList.
	 */
	ArrayList<Photo> searched;
	
	
	/**
	 * This method is for date search button handler.
	 * @param e ActionEvent
	 * @throws Exception Exception
	 */
	@FXML private void dateSearchBT_handler(ActionEvent e) throws Exception{
		String s;
		Album searchAlbum = new Album("Search Result");
		try {
		  s = fromDP.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}catch(Exception b) {
			Alert error = new Alert(AlertType.ERROR, "Please enter 'from' date with proper fromat", ButtonType.OK);
	        error.showAndWait();
			return;
		}
		if(s.length() == 0) {
			Alert error = new Alert(AlertType.ERROR, "Please enter 'from' date", ButtonType.OK);
	        error.showAndWait();
			return;
		}
	
		Calendar from = new GregorianCalendar(Integer.parseInt(s.substring(0,4)), Integer.parseInt(s.substring(5,7)) -1, Integer.parseInt(s.substring(8,s.length())));

		try {
			s = toDP.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}catch(Exception b) {
				Alert error = new Alert(AlertType.ERROR, "Please enter 'to' date with proper fromat", ButtonType.OK);
		        error.showAndWait();
				return;
			}
		if(s.length() == 0) {
			Alert error = new Alert(AlertType.ERROR, "Please enter 'to' date", ButtonType.OK);
	        error.showAndWait();
			return;
		}
		Calendar to = new GregorianCalendar(Integer.parseInt(s.substring(0,4)), Integer.parseInt(s.substring(5,7))-1 , Integer.parseInt(s.substring(8,s.length())), 24, 00, 00);
		
		if(windowFromAlbum == true) { //it is from album. Search entire photo in User's album.
			int cnt = 0;
			for(Album a: currentUser.albums) {
				for(Photo p: a.photos) {
					Calendar c = Calendar.getInstance();
					c.setTime(p.getDate());
						if(c.compareTo(from) >=0 && c.compareTo(to) <=0) {
//						System.out.println("same");
						
						
						SearchHelper.getAlbum.add(a);
						SearchHelper.getPhoto.add(p);
//						SearchHelper.idx++;
						
						
						searchAlbum.photos.add(p);
						cnt++;

					}
					
				}
			}
			if(cnt == 0) {
				Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
		        error.showAndWait();
				return;
			}
			else {
				String name = null;
				if(dateCreatetAlbumRB.isSelected()) {
					TextInputDialog dialog = new TextInputDialog("");
					dialog.initOwner(tmpStage); 
					dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
					dialog.setTitle("Add Album");
					dialog.setContentText("Name: ");
					
					Optional<String> result = dialog.showAndWait();
					if(result.isPresent())
						name = result.get();
					else
						return;
					searchAlbum.setAlbumName(name);
					currentUser.albums.add(searchAlbum);
					mgUsr.conductSerializing();
					
					FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
			        Parent parent = (Parent) photoAlbumScene.load();
			        photoAlbumController photoAlbum = photoAlbumScene.getController();
			        Scene photoAlbumControllerScene = new Scene(parent);
			        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
			        window.setScene(photoAlbumControllerScene);
			        window.show();
				}
				else {
					FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
			        Parent parent = (Parent) photoAlbumScene.load();
			        photoAlbumController photoAlbum = photoAlbumScene.getController();
			        Scene photoAlbumControllerScene = new Scene(parent);
			        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
			        window.setScene(photoAlbumControllerScene);
			        window.show();
				}				
			}
		}
		else { //from photo album.
			int cnt = 0;
			for(Photo p: currentAlbum.photos) {
				Calendar c = Calendar.getInstance();
				c.setTime(p.getDate());
				if(c.compareTo(from) >=0 && c.compareTo(to) <=0) {
//					System.out.println("same  "+ p.getUrl());

					SearchHelper.getAlbum.add(currentAlbum);
					SearchHelper.getPhoto.add(p);
//					SearchHelper.idx++;
					
					
					searchAlbum.photos.add(p);
					cnt++;
				}
				
				
			}
			if(cnt == 0) {
				Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
		        error.showAndWait();
				return;
			}
			else {
				String name = null;
				if(dateCreatetAlbumRB.isSelected()) {
					TextInputDialog dialog = new TextInputDialog("");
					dialog.initOwner(tmpStage); 
					dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
					dialog.setTitle("Add Album");
					dialog.setContentText("Name: ");
					
					Optional<String> result = dialog.showAndWait();
					if(result.isPresent())
						name = result.get();
					else
						return;
					searchAlbum.setAlbumName(name);
					currentUser.albums.add(searchAlbum);
					mgUsr.conductSerializing();
					
					FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
			        Parent parent = (Parent) photoAlbumScene.load();
			        photoAlbumController photoAlbum = photoAlbumScene.getController();
			        Scene photoAlbumControllerScene = new Scene(parent);
			        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
			        window.setScene(photoAlbumControllerScene);
			        window.show();
				}
				else {
					FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
			        Parent parent = (Parent) photoAlbumScene.load();
			        photoAlbumController photoAlbum = photoAlbumScene.getController();
			        Scene photoAlbumControllerScene = new Scene(parent);
			        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
			        window.setScene(photoAlbumControllerScene);
			        window.show();
				}		
			}
		}
	}
		
	/**
	 * Tag search button handler
	 * 
	 * @param e ActionEvent
	 * @throws Exception exception
	 */
	@FXML private void tagSearchBT_handler(ActionEvent e) throws Exception {
//		SearchHelper.idx = 0;
		SearchHelper.getAlbum.clear(); // to keep searched albums' and photos' name and location.
		SearchHelper.getPhoto.clear();
		
		if(tagRB.isSelected()) {
			
//			searched = new ArrayList<Photo>();
			Album searchAlbum = new Album("search");
			if(oneTypeRB.isSelected()) {
				String getValue = oneTF.getText();
				
				if(getValue.trim().isEmpty()) {
					Alert error = new Alert(AlertType.ERROR, "Please enter first value", ButtonType.OK);
			        error.showAndWait();
					return;
				}
				
				Tag oneCmpTag = new Tag(oneCB.getValue(), oneTF.getText());
				if(windowFromAlbum == true) { //it is from album. Search entire photo in User's album.
					int cnt = 0;
					for(Album a: currentUser.albums) {
						for(Photo p: a.photos) {
							if(p.tags.contains(oneCmpTag)) {
//								System.out.println("same");
								
								Photo copy = new Photo(p);
								SearchHelper.getAlbum.add(a);
								SearchHelper.getPhoto.add(p);								
								
								searchAlbum.photos.add(copy);
								cnt++;

							}
							
						}
					}
					if(cnt == 0) {
						Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
				        error.showAndWait();
						return;
					}
					else {
						String name = null;
						if(tagCreatetAlbumRB.isSelected()) {
							TextInputDialog dialog = new TextInputDialog("");
							dialog.initOwner(tmpStage); 
							dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
							dialog.setTitle("Add Album");
							dialog.setContentText("Name: ");
							
							Optional<String> result = dialog.showAndWait();
							if(result.isPresent())
								name = result.get();
							else
								return;
							searchAlbum.setAlbumName(name);
							currentUser.albums.add(searchAlbum);
							mgUsr.conductSerializing();
							
							FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
					        Parent parent = (Parent) photoAlbumScene.load();
					        photoAlbumController photoAlbum = photoAlbumScene.getController();
					        Scene photoAlbumControllerScene = new Scene(parent);
					        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
					        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
					        window.setScene(photoAlbumControllerScene);
					        window.show();
						}else {
							FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
					        Parent parent = (Parent) photoAlbumScene.load();
					        photoAlbumController photoAlbum = photoAlbumScene.getController();
					        Scene photoAlbumControllerScene = new Scene(parent);
					        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
					        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
					        window.setScene(photoAlbumControllerScene);
					        window.show();
						}
						
					}
				}
				else { //from photo album.
					int cnt = 0;
					for(Photo p: currentAlbum.photos) {
						if(p.tags.contains(oneCmpTag)) {
//							System.out.println("same  "+ p.getUrl());
								
							SearchHelper.getAlbum.add(currentAlbum);
							SearchHelper.getPhoto.add(p);
//							SearchHelper.idx++;
							
							Photo copy = new Photo(p);
							searchAlbum.photos.add(copy);
							cnt++;
						}
						
						
					}
					if(cnt == 0) {
						Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
				        error.showAndWait();
						return;
					}
					else {
						String name = null;
						if(tagCreatetAlbumRB.isSelected()) {
							TextInputDialog dialog = new TextInputDialog("");
							dialog.initOwner(tmpStage); 
							dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
							dialog.setTitle("Add Album");
							dialog.setContentText("Name: ");
							
							Optional<String> result = dialog.showAndWait();
							if(result.isPresent())
								name = result.get();
							else
								return;
							searchAlbum.setAlbumName(name);
							currentUser.albums.add(searchAlbum);
							mgUsr.conductSerializing();
							
							FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
					        Parent parent = (Parent) photoAlbumScene.load();
					        photoAlbumController photoAlbum = photoAlbumScene.getController();
					        Scene photoAlbumControllerScene = new Scene(parent);
					        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
					        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
					        window.setScene(photoAlbumControllerScene);
					        window.show();
						}else {
							FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
					        Parent parent = (Parent) photoAlbumScene.load();
					        photoAlbumController photoAlbum = photoAlbumScene.getController();
					        Scene photoAlbumControllerScene = new Scene(parent);
					        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
					        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
					        window.setScene(photoAlbumControllerScene);
					        window.show();
						}
					}
				}
				
				//doing make searching album and display using arraylist.
			}
			else if(twoTypeRB.isSelected()) {
//				System.out.println("click twotype");

				String getValue = twoTF.getText();
				String getValue1 = oneTF.getText();

				if(getValue.trim().isEmpty() && getValue1.trim().isEmpty()) {
					Alert error = new Alert(AlertType.ERROR, "Please enter second value", ButtonType.OK);
					error.showAndWait();
					return;
				}
				Tag oneCmpTag = new Tag(oneCB.getValue(), oneTF.getText());
				Tag twoCmpTag = new Tag(twoCB.getValue(), twoTF.getText());
				
				if(windowFromAlbum == true) { //from album
					if(andRB.isSelected()) {
						int cnt = 0;						
						for(Album a: currentUser.albums) {
							for(Photo p: a.photos) {
								if(p.tags.contains(oneCmpTag) && p.tags.contains(twoCmpTag)) {
//									System.out.println("have");
									SearchHelper.getAlbum.add(a);
									SearchHelper.getPhoto.add(p);									
									
									Photo copy = new Photo(p);
									searchAlbum.photos.add(copy);
									cnt++;
								}
							}
						}
						if(cnt == 0) {
							Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
					        error.showAndWait();
							return;
						}
						else {
							String name = null;
							if(tagCreatetAlbumRB.isSelected()) {
								TextInputDialog dialog = new TextInputDialog("");
								dialog.initOwner(tmpStage); 
								dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
								dialog.setTitle("Add Album");
								dialog.setContentText("Name: ");
								
								Optional<String> result = dialog.showAndWait();
								if(result.isPresent())
									name = result.get();
								else
									return;
								searchAlbum.setAlbumName(name);
								currentUser.albums.add(searchAlbum);
								mgUsr.conductSerializing();
								
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}else {
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}
						}
					}
					else if(orRB.isSelected()){
						int cnt = 0;
						for(Album a: currentUser.albums) {
							for(Photo p: a.photos) {
								if(p.tags.contains(oneCmpTag) || p.tags.contains(twoCmpTag)) {
//									System.out.println("have or");
									SearchHelper.getAlbum.add(a);
									SearchHelper.getPhoto.add(p);									
									
									Photo copy = new Photo(p);
									searchAlbum.photos.add(copy);
									cnt++;
								}				
							}
						}
						if(cnt == 0) {
							Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
					        error.showAndWait();
							return;
						}
						else {
							String name = null;
							if(tagCreatetAlbumRB.isSelected()) {
								TextInputDialog dialog = new TextInputDialog("");
								dialog.initOwner(tmpStage); 
								dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
								dialog.setTitle("Add Album");
								dialog.setContentText("Name: ");
								
								Optional<String> result = dialog.showAndWait();
								if(result.isPresent())
									name = result.get();
								else
									return;
								searchAlbum.setAlbumName(name);
								currentUser.albums.add(searchAlbum);
								mgUsr.conductSerializing();
								
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}else {
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}
						}
					}
					else {
						Alert error = new Alert(AlertType.ERROR, "Please select one of and / or", ButtonType.OK);
				        error.showAndWait();
						return;
					}
				}
				else { //from photo album
					if(andRB.isSelected()) {
						int cnt = 0;						
						for(Photo p: currentAlbum.photos) {
							if(p.tags.contains(oneCmpTag) && p.tags.contains(twoCmpTag)) {
//								System.out.println("have");
								SearchHelper.getAlbum.add(currentAlbum);
								SearchHelper.getPhoto.add(p);
								
								Photo copy = new Photo(p);
								searchAlbum.photos.add(copy);
								cnt++;
							}
							
						}
						if(cnt == 0) {
							Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
					        error.showAndWait();
							return;
						}
						else {
							String name = null;
							if(tagCreatetAlbumRB.isSelected()) {
								TextInputDialog dialog = new TextInputDialog("");
								dialog.initOwner(tmpStage); 
								dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
								dialog.setTitle("Add Album");
								dialog.setContentText("Name: ");
								
								Optional<String> result = dialog.showAndWait();
								if(result.isPresent())
									name = result.get();
								else
									return;
								searchAlbum.setAlbumName(name);
								currentUser.albums.add(searchAlbum);
								mgUsr.conductSerializing();
								
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}else {
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}
						}
					}
					else if(orRB.isSelected()){
						int cnt = 0;
						for(Photo p: currentAlbum.photos) {
							if(p.tags.contains(oneCmpTag) || p.tags.contains(twoCmpTag)) {
//								System.out.println("have or");
								SearchHelper.getAlbum.add(currentAlbum);
								SearchHelper.getPhoto.add(p);
								
								Photo copy = new Photo(p);
								searchAlbum.photos.add(copy);
								cnt++;
							}
														
						}
						
						if(cnt == 0) {
							Alert error = new Alert(AlertType.ERROR, "No matching photos found.", ButtonType.OK);
					        error.showAndWait();
							return;
						}
						else {
							String name = null;
							if(tagCreatetAlbumRB.isSelected()) {
								TextInputDialog dialog = new TextInputDialog("");
								dialog.initOwner(tmpStage); 
								dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
								dialog.setTitle("Add Album");
								dialog.setContentText("Name: ");
								
								Optional<String> result = dialog.showAndWait();
								if(result.isPresent())
									name = result.get();
								else
									return;
								searchAlbum.setAlbumName(name);
								currentUser.albums.add(searchAlbum);
								mgUsr.conductSerializing();
								
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,false); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}else {
								FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
						        Parent parent = (Parent) photoAlbumScene.load();
						        photoAlbumController photoAlbum = photoAlbumScene.getController();
						        Scene photoAlbumControllerScene = new Scene(parent);
						        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
						        photoAlbum.start(window,searchAlbum,userName,true); // true means that it is open by searchController.
						        window.setScene(photoAlbumControllerScene);
						        window.show();
							}
						}
					}
					else {
						Alert error = new Alert(AlertType.ERROR, "Please select one of and / or", ButtonType.OK);
				        error.showAndWait();
						return;
					}
				}
				
				
			}
			else{
				Alert error = new Alert(AlertType.ERROR, "Please select one type or two type", ButtonType.OK);
		        error.showAndWait();
				return;
			}
		}
		else {
			Alert error = new Alert(AlertType.ERROR, "Please select Tag or Date", ButtonType.OK);
	        error.showAndWait();
			return;
		}
		
		
		
	}
	
	/**
	 * Date search handler
	 */
	@FXML private void dateSearch_handler() {
		 if(dateRB.isSelected()) {
			 selectdateSMethod(false);
			 selectSearchMethod(true);
		 }
	}
	
	/**
	 * tag search handler
	 */
	@FXML private void tagSearch_handler() {
		if(tagRB.isSelected()) {
			selectSearchMethod(false);
			selectdateSMethod(true);
		}
	}
	
	/**
	 * One type radio button handler
	 */
	@FXML private void oneTypeRB_handler() {
		if(oneTypeRB.isSelected()) {
//			System.out.println("one type");
			
			setDisableMethod(true);
		}
	}
	
	/**
	 * Two type radio button handler
	 */
	@FXML private void twoTypeRB_handler() {
		if(twoTypeRB.isSelected()) {
//			System.out.println("two type");
			
			setDisableMethod(false);
		}
	}
	
	
	/**
	 * Back button handler to return to photo list or album list
	 * 
	 * @param e ActionEvent
	 * @throws Exception Exception
	 */
	@FXML private void backBT_handler(ActionEvent e) throws Exception {
		mgUsr.conductSerializing();
		if(windowFromAlbum == true) {
			FXMLLoader albumScene = new FXMLLoader(getClass().getResource("/view/album.fxml"));
			Parent parent = (Parent) albumScene.load();
			albumController album = albumScene.getController();
			Scene albumControllerScene = new Scene(parent);
			Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
			album.start(window, userName);
			window.setScene(albumControllerScene);
			window.show();
		}
		else {
			FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
	        Parent parent = (Parent) photoAlbumScene.load();
	        photoAlbumController photoAlbum = photoAlbumScene.getController();
	        Scene photoAlbumControllerScene = new Scene(parent);
	        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
	        photoAlbum.start(window,currentAlbum,userName,false);
	        window.setScene(photoAlbumControllerScene);
	        window.show();
		}
		
	}
	
	/**
	 * selected date Searched based handler use or not
	 * @param tf boolean type true or false
	 */
	private void selectdateSMethod(boolean tf) {
		fromLB.setDisable(tf);
		fromDP.setDisable(tf);
		toLB.setDisable(tf);
		toDP.setDisable(tf);
		dateCreatetAlbumRB.setDisable(tf);
		dateSearchBT.setDisable(tf);
	}
	
	/**
	 * selected tag Searched based handler use or not
	 * @param tf boolean type true or false
	 */
	private void selectSearchMethod(boolean tf) {
		setDisableMethod(tf);
		oneTF.setDisable(tf);
		oneCB.setDisable(tf);
		tagCreatetAlbumRB.setDisable(tf);
		oneTypeRB.setDisable(tf);
		twoTypeRB.setDisable(tf);
		tagSearchBT.setDisable(tf);
	}
	
	/**
	 * set disable method for search function
	 * @param tf boolean true or false
	 */
	private void setDisableMethod(boolean tf) {
		andRB.setDisable(tf);
		orRB.setDisable(tf);
		twoTF.setDisable(tf);
		twoCB.setDisable(tf);
	}
	
	/**
	 * Start method 
	 * 
	 * @param mainStage get previous stage to return there
	 * @param user get user name
	 * @param album  get specific album
	 * @param windowFromAlbum to know this is from album which is true or photo album
	 */
	public void start(Stage mainStage, String user, Album album ,boolean windowFromAlbum) {
		tmpStage = mainStage;
		this.windowFromAlbum = windowFromAlbum;
		mgUsr = mgUsr.getInstance();
		mgUsr.conductDeserializing();
		userName = user;
		currentUser = mgUsr.getUser(user);
		
		if(album != null) { //check null because album controller send null album. and photo album send current album.
			currentAlbum = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName());
		}
		
//		currentUser = mgUsr.getUser(user);
		
		options = FXCollections.observableArrayList(currentUser.tagType);
		
		oneCB.setItems(options);
		twoCB.setItems(options);

		oneCB.getSelectionModel().selectFirst();
		twoCB.getSelectionModel().selectFirst();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				mgUsr.conductSerializing();
			}
		});
	}

	/**
	 * initialize not use
	 */
	@Override public void initialize(URL arg0, ResourceBundle arg1) {

	}
}
