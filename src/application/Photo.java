package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javafx.scene.image.Image;

/**
 * This is Photo class which include url, caption, date, and tags.
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class Photo implements Serializable, Cloneable{
	/**
	 * Serial number to save data
	 */
	private static final long serialVersionUID = 1234541234567111111L;
	
	/**
	 * Save url 
	 */
	private String url;
	/**
	 * photo has caption
	 */
	private String caption;
	/**
	 * photo has modified date
	 */
	Date lastModifiedDate;
	/**
	 * photo has 0 to many tags
	 */
	public ArrayList<Tag> tags = new ArrayList<Tag>();

	
	/**
	 * add tags 
	 * @param tag get tag to add to arrayList
	 */
	public void addTags(Tag tag) {
		tags.add(tag);
	}
	
	/**
	 * Photo constructor with one parameter
	 * @param url get url to set up url
	 */
	public Photo(String url) {
		this.url = url;
	}
	
	/**
	 * Photo constructor with two parameter.
	 * @param url get url
	 * @param caption get caption
	 */
	public Photo(String url, String caption) {
		this.url = url;
		this.caption = caption;
	}
	
	/**
	 * Photo constructor with three parameter
	 * @param url get url
	 * @param caption get caption
	 * @param lastModifiedDate get date
	 */
	public Photo(String url, String caption, Date lastModifiedDate) {
		this.url = url;
		this.caption = caption;
		this.lastModifiedDate = lastModifiedDate;
	}
	
	//for deep clone
	/**
	 * Photo construction for using deep clone
	 * @param that get photo to copy of this to another photo.
	 */
	public Photo(Photo that) {
		this(that.getUrl(), that.getCaption(), that.getDate() );
		for(Tag copyTag : that.tags) {
			tags.add(new Tag(copyTag.getkey(), copyTag.getValue()));
		}
//		this.tags = that.tags;
	}
	
	/**
	 * Get specific tag.
	 * @param t get tag
	 * @return return matched tag
	 */
	public Tag getSpecificTag(Tag t) {
		Tag ret = null;
		for(Tag a : tags) {
			
			if(a.getkey().equals(t.getkey()) && a.getValue().equals(t.getValue())) {
//				System.out.println("same");
				ret = a;
			}
		}
	
		return ret;
	}
	
	/**
	 * Override toString to check Photo object
	 * @return caption
	 */
	public String toString() {
		return this.caption;
	}

	/**
	 * return url
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * set url 
	 * @param url get url as string type
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * return caption
	 * @return caption
	 */
	public String getCaption() {
		return this.caption;
	}
	
	/**
	 * set caption
	 * @param caption get catption to set up
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	/**
	 * set date 
	 * @param lastModifiedDate get system date to set up 
	 */
	public void setDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * return date
	 * @return date
	 */
	public Date getDate() {
		return lastModifiedDate;
	}
	
	/**
	 * implement clone method to use deep clone
	 * @throws CloneNotSupportedException set
	 * @return super clone method
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}