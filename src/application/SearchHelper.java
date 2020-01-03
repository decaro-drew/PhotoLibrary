package application;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is for search Helper class.
 * All members are public static because it will be used other classes to get original album and class.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class SearchHelper {
	/**
	 * This album array list have original album from search result.
	 */
	public static ArrayList<Album> getAlbum = new ArrayList<Album>();
	/**
	 * This photo array list have original photo which is located in original album to distinguish from search result.
	 */
	public static ArrayList<Photo> getPhoto = new ArrayList<Photo>();
	
}
