package controllers;

import models.UserModel;
import models.inputType;

public class LoginController {
	
	public LoginController() {
		
	}
	
	public UserModel createUser(String username, String password) {
		UserModel user = new UserModel(username, password);
		return user;
	}
	
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
