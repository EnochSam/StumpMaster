package controllers;

import models.User;
import models.inputType;
import Database.FakeUserDatabase;

public class LoginController {
	private FakeUserDatabase db = new FakeUserDatabase();
	
	public LoginController() {
		
	}
	
	// Add new User
	public void createUser(String username, String password) {
		db.createUser(username, password);
	}
	
	// Check if User exists
	public boolean checkExists(String username, String password) {
		return db.checkExists(username, password);
	}
	
	// Check if username exists
	public boolean checkUsernameExists(String username) {
		return db.checkUsernameExists(username);
	}
	
	// Get Specific user
	public User getUser(String username, String password) {
		return db.getUser(username, password);
	}
	
	// Validate input
	public boolean validate(String input, inputType type) {
		boolean isValid = false;
		
		// Check if the input type is a username
		if(type == inputType.USERNAME) {
			// Validate username
			isValid = validateUsername(input);
		}	// input type is a password
		else {
			// Validate password
			isValid = validatePassword(input);
		}
		
		return isValid;
	}
	
	// Validate Username
	private boolean validateUsername(String username) {
		boolean isValid = true;
		
		if(username == null) {
			isValid = false;
		}
		
		return isValid;
	}
	// Validate Password
	private boolean validatePassword(String password) {
		boolean isValid = true;
		
		if(password != null) {
			if(password.length() < 8) {
				isValid = false;
			}
		}
		else {
			isValid = false;
		}
		
		return isValid;
	}
}
