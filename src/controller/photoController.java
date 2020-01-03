package controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import application.Album;
import application.ManageUser;
import application.Photo;
import application.SearchHelper;
import application.Tag;
import application.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
/**
 * This is Photo controller.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class photoController {
	/**
	 * Keep stage from photo album.
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
	 * Reference of photo to get current photo.
	 */
	private Photo currentPhoto;
	/**
	 * Reference of photo to manage searched photo.
	 */
	private Photo tempPhoto; //for searching
	/**
	 * Reference of album to manage searched album.
	 */
	private Album tempAlbum;
	/**
	 * If it is true, then it is from result of search. If false, it is from original album.
	 */
	private boolean searchResult;
	/**
	 * keep index
	 */
	private int idx;
	/**
	 * get album index
	 */
	private int albumIdx;
	
	/**
	 * Setting static data format as yyyy-mm-dd
	 */
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat ("yyyy-MM-dd");
	
	/**
	 * to set image
	 */
	@FXML private Image image;
	/**
	 * To show image
	 */
	@FXML private ImageView iv;
	
	/**
	 * Button for return to previous page.
	 */
	@FXML private Button backBT;
	/**
	 * Button for editing caption
	 */
	@FXML private Button captionBT;
	/**
	 * Button for next photo
	 */
	@FXML private Button rightNextBT;
	/**
	 * Button for previous photo.
	 */
	@FXML private Button leftPrevBT;
	/**
	 * Button for adding tag
	 */
	@FXML private Button addTagBT;
	/**
	 * Button for delete tag
	 */
	@FXML private Button deleteTagBT;
	/**
	 * Button for add Tag type
	 */
	@FXML private Button addTagTypeBT;
	/**
	 * Button for editing tag type.
	 */
	@FXML private Button editTypeBT;
	
	/**
	 * TextArea to show caption
	 */
	@FXML private TextArea captionTA;
	
	/**
	 * To show modified date.
	 */
	@FXML private Text latestDate;
	
	/**
	 * List view to show all tags
	 */
	@FXML private ListView<Tag> list;
	/**
	 * Observable List to set up list view.
	 */
	private ObservableList<Tag> obs;
	/**
	 * observable list to set up tag types
	 */
	ObservableList<String> options;
	
	/* *****************************
	 * Button handler
	 *  - add tag
	 *  - edit type
	 *  - add type
	 *  - delete
	 *  - right
	 *  - left
	 *  - logout
	 *  - back
	 * ****************************/
	
	/**
	 * This is add tag type button handler.
	 * User can add own type of tags. 
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void addTagTypeBT_handler(ActionEvent e) {
		Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Tag Type");
        dialog.setHeaderText("Tag");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("");
        dialogPane.setContent(new VBox(8,textField));
        
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            	if(textField.getText().trim().isEmpty()) {
                	Alert error = new Alert(AlertType.ERROR, "Please fill the value\nTRY AGAIN!", ButtonType.OK);
        			error.showAndWait();
                }
            	else {
            		String check = textField.getText();
            		if(duplicationCheck(check))
            			return check;
            		else {
            			Alert error = new Alert(AlertType.ERROR, "Duplicate type is not allowed.", ButtonType.OK);
            			error.showAndWait();
            		}
            	}
            }
            return null;
        });
                
        Optional<String> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((String results) -> {
        	currentUser.tagType.add(results);
        	options = FXCollections.observableArrayList(currentUser.tagType);
        });
        
		Date time = new Date();
		currentPhoto.setDate(time);
		if(searchResult)
			tempPhoto.setDate(time);
		String time1 = FORMAT.format(currentPhoto.getDate());
		latestDate.setText(time1);
	}
	
	/**
	 * This is edit tag type handler.
	 * User can edit their own tag types.
	 * 
	 * @param e ActionEvent
	 */
	@FXML private void editTypeBT_handler(ActionEvent e) {
		
        
        if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
			error.showAndWait();			
		}
        else {
        	Dialog<Tag> dialog = new Dialog<>();
            dialog.setTitle("Tag");
            dialog.setHeaderText("Tag");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        	
            Tag getTag = list.getSelectionModel().getSelectedItem(); 
        	int idxOfgetTag = currentPhoto.tags.indexOf(getTag);
//        	System.out.println(idxOfgetTag);
        	TextField textField = new TextField(getTag.getValue());
            
            ComboBox<String> comboBox = new ComboBox<>(options);
            final Label explain = new Label();
            comboBox.getSelectionModel().select(getTag.getkey());
            if(comboBox.getValue().equals(comboBox.getValue())) {
            	explain.setText("Please enter "+ comboBox.getValue());
            	explain.setTextFill(Color.web("#0000FF"));
            }
            dialogPane.setContent(new VBox(8,comboBox, textField, explain));

            comboBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override public void handle(ActionEvent e)
                {
                	if(comboBox.getValue().equals(comboBox.getValue())) {
                    	explain.setText("Please enter "+ comboBox.getValue());
                    	explain.setTextFill(Color.web("#0000FF"));
                    }
                }
            });
            Platform.runLater(textField::requestFocus);
            dialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.OK) {
                	if(textField.getText().trim().isEmpty()) {
                    	Alert error = new Alert(AlertType.ERROR, "Please fill the value\nTRY AGAIN!", ButtonType.OK);
            			error.showAndWait();
                    }
                	else {
                		Tag check = new Tag(comboBox.getValue().toString(),textField.getText());
                		if(duplicationCheck(check))
                			return check;
                		else {
                			Alert error = new Alert(AlertType.ERROR, "Duplicate value is not allowed.", ButtonType.OK);
                			error.showAndWait();
                		}
                	}
                		
                }
                return null;
            });
//            System.out.println(currentAlbum.getAlbumName());
            if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
	            tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName());
	            tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName()).getSpecificPhoto(currentPhoto);
            }
            
//            System.out.println(tempPhoto.getSpecificTag(getTag).getkey());
            
            Optional<Tag> optionalResult = dialog.showAndWait();
            optionalResult.ifPresent((Tag results) -> {
            	currentPhoto.getSpecificTag(getTag).setkey(results.getkey());
            	currentPhoto.getSpecificTag(getTag).setValue(results.getValue());

            	obs = FXCollections.observableArrayList(currentPhoto.tags);
            	setOnListView();                
            });
            
//            System.out.println(currentPhoto.getSpecificTag(getTag).getValue());
            if(searchResult) {
                tempPhoto.tags.get(idxOfgetTag).setkey(currentPhoto.getSpecificTag(getTag).getkey());
    			tempPhoto.tags.get(idxOfgetTag).setValue(currentPhoto.getSpecificTag(getTag).getValue());

            }
            
            Date time = new Date();
    		currentPhoto.setDate(time);
    		if(searchResult)
    			tempPhoto.setDate(time);
    		String time1 = FORMAT.format(currentPhoto.getDate());
    		latestDate.setText(time1);
        }
        
        
	}
	
	/**
	 * Add tag button handler.
	 * 
	 * @param e ActionEvent
	 * @throws Exception throw exception.
	 */
	@FXML private void addTagBT_handler(ActionEvent e) throws Exception {
		Dialog<Tag> dialog = new Dialog<>();
        dialog.setTitle("Tag");
        dialog.setHeaderText("Tag");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("");
        
        ComboBox<String> comboBox = new ComboBox<>(options);
        final Label explain = new Label();
        comboBox.getSelectionModel().selectFirst();
        explain.setText("Please enter person.");
    	explain.setTextFill(Color.web("#0000FF"));
        dialogPane.setContent(new VBox(8,comboBox, textField, explain));

        comboBox.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override public void handle(ActionEvent e)
            {
            	if(comboBox.getValue().equals(comboBox.getValue())) {
                	explain.setText("Please enter "+ comboBox.getValue());
                	explain.setTextFill(Color.web("#0000FF"));
                }
            }
        });
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
            	if(textField.getText().trim().isEmpty()) {
                	Alert error = new Alert(AlertType.ERROR, "Please fill the value\nTRY AGAIN!", ButtonType.OK);
        			error.showAndWait();
                }
            	else {
            		Tag check = new Tag(comboBox.getValue().toString(),textField.getText());
            		if(duplicationCheck(check))
            			return check;
            		else {
            			Alert error = new Alert(AlertType.ERROR, "Duplicate value is not allowed.", ButtonType.OK);
            			error.showAndWait();
            		}
            	}
            		
            }
            return null;
        });
        
        Optional<Tag> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Tag results) -> {
            addToArrayList(results);
            
        });
        
        Date time = new Date();
		currentPhoto.setDate(time);
		if(searchResult)
			tempPhoto.setDate(time);
		String time1 = FORMAT.format(currentPhoto.getDate());
		latestDate.setText(time1);
	}
	
	/**
	 * This is delete tag button handler.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void deleteTagBT_handler(ActionEvent e) {
		if(list.getSelectionModel().getSelectedIndex() == -1) {
			Alert error = new Alert(AlertType.ERROR, "Nothing Selected.\nPlease select correctly", ButtonType.OK);
			error.showAndWait();			
		}
		else {
			Alert warning = new Alert(AlertType.WARNING,"Delete this Tag from list?", ButtonType.YES, ButtonType.NO);
			warning.showAndWait();
			if(warning.getResult() == ButtonType.NO){
				return;
			}

			int idx = list.getSelectionModel().getSelectedIndex();
			obs.remove(idx);
			currentPhoto.tags.remove(idx);
				
			Date time = new Date();
			if(searchResult) {
				tempPhoto.tags.remove(idx);
				tempPhoto.setDate(time);
			}
			currentPhoto.setDate(time);
			String time1 = FORMAT.format(currentPhoto.getDate());
			latestDate.setText(time1);
		}
	}
	
	/**
	 * Right Next button handler.
	 * It shows next photo.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void rightNextBT_handler(ActionEvent e) {
		int indexforRight = currentAlbum.getIndex(currentPhoto);
		
		if(indexforRight < currentAlbum.getIdxPhotos()-1) {
			currentPhoto = currentAlbum.getPhotoWithIndex(indexforRight+1);
		}
		
		image = new Image(currentPhoto.getUrl());
		iv.setImage(image);
		
		captionTA.setText(currentPhoto.getCaption());
		obs = FXCollections.observableArrayList(currentPhoto.tags);
		setOnListView();
		String time1 = FORMAT.format(currentPhoto.getDate());
		latestDate.setText(time1);
	}
	
	/**
	 * This is left previous button handler.
	 * It shows previous photo.
	 * 
	 * @param e ActionEvent
	 */
	@FXML private void leftPrevBT_handler(ActionEvent e) {
		int indexforleft = currentAlbum.getIndex(currentPhoto);
		
		if(indexforleft > 0) {
			currentPhoto = currentAlbum.getPhotoWithIndex(indexforleft-1);
		}
		
		image = new Image(currentPhoto.getUrl());
		iv.setImage(image);
		
		captionTA.setText(currentPhoto.getCaption());
		obs = FXCollections.observableArrayList(currentPhoto.tags);
		setOnListView();
		String time1 = FORMAT.format(currentPhoto.getDate());
		latestDate.setText(time1);
	}
	
	/**
	 * This is caption button handler.
	 * User can edit, add, and delete their caption.
	 * 
	 * @param e ActionEvent.
	 */
	@FXML private void captionBT_handler(ActionEvent e) {
		TextInputDialog dialog = new TextInputDialog(currentPhoto.getCaption());
		dialog.initOwner(tmpStage); 
		dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());
		dialog.setTitle("Edit photo caption");
		dialog.setContentText("Caption: ");

		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			currentPhoto.setCaption(result.get());
			if(searchResult) {
				tempPhoto.setCaption(result.get());
			}
		}
			
		else
			return;
		Date time = new Date();
		if(searchResult) 
			tempPhoto.setDate(time);
		currentPhoto.setDate(time);
		String time1 = FORMAT.format(currentPhoto.getDate());
		latestDate.setText(time1);
		
		captionTA.setText(currentPhoto.getCaption());
	}
	
	
	/**
	 * This is back button handler.
	 * It returns to previous window.
	 * 
	 * @param e ActionEvent
	 * @throws Exception Throws exception.
	 */
	@FXML private void backBT_handler(ActionEvent e) throws Exception {
		mgUsr.conductSerializing();
		
		FXMLLoader photoAlbumScene = new FXMLLoader(getClass().getResource("/view/openAlbum.fxml"));
        Parent parent = (Parent) photoAlbumScene.load();
        photoAlbumController photoAlbum = photoAlbumScene.getController();
        Scene photoAlbumControllerScene = new Scene(parent);
        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
        photoAlbum.start(window,currentAlbum,currentUser.getUserName(),searchResult);
        window.setScene(photoAlbumControllerScene);
        window.show();
	}
	
	
	/* *****************************
	 * Helper functions
	 * *****************************/
	
	/**
	 * Helper method
	 * Add new tag to arrayList.
	 *  
	 * @param newTag get tag to add to list.
	 */
	private void addToArrayList(Tag newTag) {
		if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
            tempPhoto.tags.add(newTag);
		}
		
		currentPhoto.tags.add(newTag);
		obs = FXCollections.observableArrayList(currentPhoto.tags);
		
		
		setOnListView();
	}
	
	/**
	 * set tag on list view
	 */
	private void setOnListView() {
		list.setItems(obs);
		list.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>(){

			@Override
			public ListCell<Tag> call(ListView<Tag> p) {
				
				ListCell<Tag> cell = new ListCell<Tag>() {
					
					@Override 
					protected void updateItem(Tag s, boolean bln) {
						super.updateItem(s, bln);
						if(s != null) {
							setText(s.getkey()+": "+s.getValue());
						}else
							setText("");
					}
				};
				return cell;
			}

		});
	}
	
	/**
	 * Check duplicate of tags.
	 * 
	 * @param tag get tag to compare
	 * @return return boolean
	 */
	private boolean duplicationCheck(Tag tag) {
		boolean ret = true;
		if(tag == null) {
			ret = true;
		}
		else {
			for(Tag t:currentPhoto.tags) {
				if(t.getkey().toLowerCase().equals(tag.getkey().toLowerCase()) 
						&& t.getValue().toLowerCase().equals(tag.getValue().toLowerCase())) {
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
	 * Check duplicate string
	 * 
	 * @param str get string to compare
	 * @return boolean
	 */
	private boolean duplicationCheck(String str) {
		boolean ret = true;
		if(str == null) {
			ret = true;
		}
		else {
			for(String s:currentUser.tagType) {
				if(s.equals(str)) {
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
	 * This is start method 
	 * 
	 * @param mainStage stage from photo album
	 * @param album get specific album to open
	 * @param photo get specific photo
	 * @param user get user name
	 * @param search is search result or not
	 */
	public void start(Stage mainStage, Album album, Photo photo, String user, boolean search) {
		tmpStage = mainStage;
		
		mgUsr = mgUsr.getInstance();
		mgUsr.conductDeserializing();
		currentUser = mgUsr.getUser(user);
		searchResult = search;
		 
		if(!search) {
			currentAlbum = mgUsr.getUser(user).getSpecificAlbum(album.getAlbumName());
			currentPhoto = mgUsr.getUser(user)
					.getSpecificAlbum(album.getAlbumName())
					.getSpecificPhoto(photo);
		}
		else {
			currentAlbum = album;
			currentPhoto = album.getSpecificPhoto(photo);
		}
//		System.out.println(currentPhoto.getUrl());
		
		idx= currentAlbum.getIndex(photo); // need to know which photo is. (from search)
		if(searchResult) { //this is for changing all information when user search photo. Which means that if searched photos are changed, then originals are also changed. 
            tempAlbum = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName());
            tempPhoto = currentUser.getSpecificAlbum(SearchHelper.getAlbum.get(idx).getAlbumName()).getSpecificPhoto(currentPhoto);
		}
		
		
		image = new Image(currentPhoto.getUrl());
		iv.setImage(image);
		
		captionTA.setText(photo.getCaption());
		
		obs = FXCollections.observableArrayList(currentPhoto.tags);
		setOnListView();
		
		String time1 = FORMAT.format(currentPhoto.getDate());
		latestDate.setText(time1);
		
		options = FXCollections.observableArrayList(currentUser.tagType);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				mgUsr.conductSerializing();
			}
		});
	}
}
