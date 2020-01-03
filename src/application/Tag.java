package application;

import java.io.Serializable;


/**
 * This is Tag class. 
 * It has key, which is called type, and value.
 * Default keys are Person and Location. Each user can make new types as much as they want.
 * All Tags are saved to User class.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class Tag implements Serializable, Cloneable{
	/**
	 * serial number to save data
	 */
	private static final long serialVersionUID = 1112345612347111111L;

	/**
	 * This is type of tag.
	 */
	private String key;
	/**
	 * This is value of tag.
	 */
	private String value;
		
	/**
	 * Constructor that has two parameter
	 * @param key get key to set up key
	 * @param value get value to set up value.
	 */
	public Tag(String key, String value) {
		this.key=key;
		this.value=value;
	}
	/**
	 * Constructor with one parameter
	 * @param that get tag 
	 */
	public Tag(Tag that) {
		this(that.key,that.value);
	}
	
	/**
	 * return key
	 * @return key
	 */
	public String getkey() {
		return key;
	}
	
	/**
	 * set key
	 * @param key get key to set up
	 */
	public void setkey(String key) {
		this.key = key;
	}
	
	/**
	 * return value
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * set value
	 * @param value get value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * overriding equals method to compare tag object
	 * @param o object
	 * @return boolean
	 */
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Tag)) {
			return false;
		}
		Tag other =  (Tag) o;
		
		if( this.key.compareTo(other.getkey())==0 && this.value.compareTo(other.getValue())==0) {
			return true;
		}
		return false;
	}
	
	/**
	 * implement clone method to use deep clone to copy and move.
	 * @throws CloneNotSupportedException set
	 * @return super clone method.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
