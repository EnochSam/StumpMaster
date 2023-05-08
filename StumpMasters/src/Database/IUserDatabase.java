package Database;

import models.User;

public interface IUserDatabase {

	boolean checkUsernameExists(String username);

	User getUser(String username, String password);

	void createUser(String username, String password);

	boolean checkExists(String username, String password);

}
