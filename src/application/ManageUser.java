package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * The core data class.
 * All of data will save with this class.
 * 
 * @author Jaehyun Kim
 * @author Drew Decaro
 *
 */
public class ManageUser implements Serializable{
	/**
	 * data saved location
	 */
	private static final String USERINFO_SER = "src/data/user.ser";
	/**
	 * serial number
	 */
	private static final long serialVersionUID = 1233L;
	
	/**
	 * make singleton
	 */
	private static ManageUser instance;
	/**
	 * get user
	 */
	private User user;
	
	/**
	 * User list
	 */
	public ArrayList<User> arrList = new ArrayList<>();

	/**
	 * private constructor
	 */
	private ManageUser() {}
	
	/**
	 * return singleton instance
	 * @return instance
	 */
	public static ManageUser getInstance() {
		if(instance == null){
            instance = new ManageUser();
        }
        return instance;
	}
	

	/**
	 * return user List.
	 * @return user list
	 */
	public ArrayList<User> getUserList() {
		return arrList;
	}
	
	/**
	 * set list
	 * @param user get list to set up
	 */
	public void setUserList(ArrayList<User> user) {
		arrList = user;
	}
	
	/**
	 * add user list
	 * @param user get uset to add to array list 
	 */
	public void addUserList(User user) {
		arrList.add(user);
	}
	
	/**
	 * check is empty
	 * @return boolean
	 */
	public boolean isListEmpty() {
		if(arrList.isEmpty())
			return true;
		else
			return false;
	}
	
	/**
	 * return specific user
	 * @param getUser get name parameter to find user
	 * @return user instance
	 */
	public User getUser(String getUser) {
		for(User u: arrList) {
			if(u.getUserName().equals(getUser)) {
				user = u;
			}
		}
		return user;
	}
	
	/**
	 * check user to login
	 * @param getUserName get name to find matching id.
	 * @return boolean
	 */
	public boolean checkUserToLogin(String getUserName) {
//		System.out.println("Test - check User to Login");
		for(User u: arrList) {
			if(u.getUserName().equals(getUserName)) {
				user = u;
//				System.out.println("Test - check User to Login");
				return true;
			}
		}
		return false;
	}
	/**
	 * to serialize data
	 */
	public void conductSerializing() {
		
		try {
			FileOutputStream fos = new FileOutputStream(USERINFO_SER);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream out = new ObjectOutputStream(bos);

			out.writeObject(arrList);
			
			out.close();
//			System.out.println("Test - Serialized!");
//			System.out.println(arrList.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * read data using deserialize
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public void conductDeserializing(){
		
		try {
//			File file = new File(USERINFO_DAT);
			FileInputStream fis = new FileInputStream(USERINFO_SER);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(bis);

			arrList = (ArrayList<User>) in.readObject();
//			System.out.println(arrList.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
