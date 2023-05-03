package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.User;

class UserTests {
	private String username = "username";
	private String password = "password";
	
	private String newUsername = "newUsername";
	private String newPassword = "newPassword";
	
	private User user;
	
	public UserTests() {
		user = new User(username, password);
	}

	@Test
	void testGetUsername() {
		assertEquals(user.getUsername(), username);
	}
	
	@Test
	void testGetPassword() {
		assertEquals(user.getPassword(), password);
	}
	
	@Test
	void testSetUsername() {
		user.setUsername(newUsername);
		
		assertEquals(user.getUsername(), newUsername);
	}
	
	@Test
	void testSetPassword() {
		user.setPassword(newPassword);
		
		assertEquals(user.getPassword(), newPassword);
	}

}
