package models;

public class UserModel {
	private String username;
	private String password;
	
	
	public UserModel(String username, String password) {
		this.password = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
