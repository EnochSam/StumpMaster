package database;

import models.User;

public interface IUser {
	public void createUser(String username, String password);
	public boolean checkExists(String username, String password);
	public User getUser(String username, String password);
	public boolean checkUsernameExists(String username);
}
