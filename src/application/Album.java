package application;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is album class which has album name and photos
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class Album implements Serializable{
	/**
	 * Serialized album to save album data.
	 */
	private static final long serialVersionUID = 123451234512341151L;
	
	/**
	 * have album name.
	 * It will not be duplicated
	 */
	private String albumName;
	/**
	 * composition photos as array list.
	 */
	public ArrayList<Photo> photos = new ArrayList<>();
	
//	public ArrayList<String> userTagType = new ArrayList<String>();
	
	/**
	 * constructor to make new instance with album name
	 * @param albumName get album name
	 */
	public Album(String albumName) {
		this.albumName = albumName;
	}
	
	/**
	 * return album name
	 * @return album name
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * set album name 
	 * @param albumName get album name to set album name
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	/**
	 * return current whole photos in the album.
	 * @return arraylist of photo
	 */
	public ArrayList<Photo> getPhotos(){
		return this.photos;
	}
	
	/**
	 * Set photo to arraylist
	 * 
	 * @param photo get photo to add.
	 */
	public void setPhotos(Photo photo) {
		photos.add(photo);
	}
	
	/**
	 * return size of photos
	 * @return size of photos
	 */
	public int getIdxPhotos() {
		return photos.size();
	}
	
	/**
	 * return specific photo through index. 
	 * @param idx get index to find photo
	 * @return photo
	 */
	public Photo getPhotoWithIndex(int idx) {
		return photos.get(idx);
	}
	
	/**
	 * return specific index of photo
	 * @param photo get photo to find index
	 * @return index
	 */
	public int getIndex(Photo photo) {
		return photos.indexOf(photo);
	}
	
	/**
	 * return specific photo
	 * @param p get photo to find same one
	 * @return photo
	 */
	public Photo getSpecificPhoto(Photo p) {
		Photo ret = null;
		for(Photo a : photos) {
			String[] splitUrl = p.getUrl().split("/");
			String[] splitUrlPhoto = a.getUrl().split("/");
			if(splitUrlPhoto[splitUrlPhoto.length-1].equals(splitUrl[splitUrl.length-1]) && a.getCaption().equals(p.getCaption())) {
//				System.out.println("same");
				ret = a;
			}
		}
	
		return ret;
	}
}
