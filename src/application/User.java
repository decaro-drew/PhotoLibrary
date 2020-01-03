package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javafx.stage.Stage;

/**
 * This class is for user. 
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class User implements Serializable{
	/**
	 * Serialize number
	 */
	private static final long serialVersionUID = 12341234555L;
    
	/**
	 * has user own tag types.
	 */
	public ArrayList<String> tagType = new ArrayList<String>();
	/**
	 * has user name. 
	 * It will not be duplicated.
	 */
	private String userName;
	
	/**
	 * User has 0 to many albums.
	 */
	public ArrayList<Album> albums = new ArrayList<Album>();
		
	/**
	 * Constructor with user name
	 * @param userName get user name to set.
	 */
	public User(String userName) {
		this.userName = userName;
		this.tagType.add("Person");
		this.tagType.add("Location");
	}
	
	/**
	 * set User name.
	 * @param userName get user name to set up
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * return user name to use other classes.
	 * @return user name
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * override toString to show user class as string
	 * @return userName user name
	 */
	public String toString() {
		return userName;
	}
	
	/**
	 * add to album 
	 * @param album get album to add to arraylist
	 */
	public void addAlbum(Album album) {
		albums.add(album);
	}
	
	/**
	 * return album arraylist 
	 * @return return arraylist of album
	 */
	public ArrayList<Album> getAlbum(){
		return this.albums;
	}
	
	/**
	 * search specific album using album name.
	 * @param albumName get album name to find.
	 * @return album
	 */
	public Album getSpecificAlbum(String albumName) {
		Album ret = null;
		for(Album a : albums) {
			if(a.getAlbumName().equals(albumName))
				ret = a;
		}
	
		return ret;
	}
}	