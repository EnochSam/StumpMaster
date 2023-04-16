package Database;

import java.util.ArrayList;

import models.User;
public class FakeUserDatabase implements IUser {
	
	// List of Users
	private ArrayList<User> users = new ArrayList<User>();
	
	// Default values for default user
	private String defaultUsername = "defaultUsername";
	private String defaultPassword = "defaultPassword";
	
	public FakeUserDatabase() {
		// Initialize default user
		User defaultUser = new User(defaultUsername, defaultPassword);
		
		// Add default user to user list
		users.add(defaultUser);
	}
	
	@Override
	public void createUser(String username, String password) {
		// Create user object
		User newUser = new User(username, password);
		
		// Add user object to user list
		users.add(newUser);
	}
	
	@Override
	public boolean checkExists(String username, String password) {
				
		boolean exists = false;
		for(User user: users) {
			if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
				exists = true;
				break;
			}
		}
		// Check if it exists in user list
		return exists;
	}
	
	@Override
	public boolean checkUsernameExists(String username) {
		boolean exists = false;
		for(User user: users) {
			if(user.getUsername().equals(username)) {
				exists = true;
				break;
			}
		}
		// Check if it exists in user list
		return exists;
	}
	
	@Override
	public User getUser(String username, String password) {
		User user = new User(username, password);
		return user;
		//return users.get(users.indexOf(user));
	}
	
//	public ArrayList<User>getUsers() {
//		return users;
//	}
	
}
