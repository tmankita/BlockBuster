package bgu.spl181.net.api.bidi;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import bgu.spl181.net.json.User;
import bgu.spl181.net.json.Users;

/**
 * this class holds all the data of the users
 */
public class UserSharedData {
	// local variables
	protected ConcurrentHashMap<String, User> _users;
	protected ConcurrentHashMap<String, Boolean> _isLogged;
	protected ConcurrentHashMap<String, Integer> _connectIdMap;
	protected ReentrantReadWriteLock _userLock;

	// constructor
	public UserSharedData(List<User> users) {
		_users = new ConcurrentHashMap();
		_isLogged = new ConcurrentHashMap();
		_connectIdMap = new ConcurrentHashMap();
		_userLock = new ReentrantReadWriteLock();
		for (User usr : users) {
			_users.put(usr.getUsername(), usr);
			_isLogged.put(usr.getUsername(), false);
		}
	}

	/**
	 * returns the user according to the user name
	 * 
	 * @param userName
	 *            - the user name
	 * @return - the user
	 */
	public User getUser(String userName) {
		_userLock.readLock().lock();
		try {
			return _users.get(userName);
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * returns true if the user logged in to the system. False, otherwise.
	 * 
	 * @param userName
	 *            - the user name of the User
	 * @return - true if logged in, False, otherwise.
	 */
	public boolean isLoggedIn(String userName) {
		if (_isLogged.containsKey(userName))
			return _isLogged.get(userName).booleanValue();
		else
			return false;
	}

	/**
	 * Checks if the user can register
	 * 
	 * @param userName
	 *            - the name of the user
	 * @return true if he can register. False, otherwise.
	 */
	public boolean tryToRegister(String userName) {
		_userLock.readLock().lock();
		try {
			// checks if the username is not exists and it's not log in
			return ((_users.get(userName) == null) && !isLoggedIn(userName));
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * Add user to the users map
	 * 
	 * @param usr
	 *            - the user to be added
	 */
	public void addUser(User usr) {
		_userLock.writeLock().lock();
		try {
			_isLogged.putIfAbsent(usr.getUsername(), false);
			_users.put(usr.getUsername(), usr);
		} finally {
			_userLock.writeLock().unlock();
		}

	}

	/**
	 * Operates log in to a user
	 * 
	 * @param userName
	 *            - the name of the user
	 * @param connectId
	 *            - the connectId of the user
	 */
	public void logIn(String userName, int connectId) {
		_userLock.writeLock().lock();
		try {
			_isLogged.replace(userName, true);
			_connectIdMap.putIfAbsent(userName, new Integer(connectId));
		} finally {
			_userLock.writeLock().unlock();
		}

	}

	/**
	 * Operates log out to a user
	 * 
	 * @param userName
	 *            - the name of the user
	 */
	public void logOut(String userName) {
		_userLock.writeLock().lock();
		try {
			_connectIdMap.remove(userName);
			_isLogged.replace(userName, false);
		} finally {
			_userLock.writeLock().unlock();
		}

	}

	/**
	 * Checks if the password given match the username
	 * 
	 * @param userName
	 *            - the username
	 * @param password
	 *            - the password given
	 * @return true if the password match the username. False, otherwise.
	 */
	public boolean checkUserPassword(String userName, String password) {
		String originalPassword = _users.get(userName).getPassword();
		return (originalPassword.equals(password));
	}

	/**
	 * Checks if the username exists
	 * 
	 * @param userName
	 *            - the username
	 * @return true if it exists. False, otherwise.
	 */
	public boolean checkIfUserExists(String userName) {
		return _users.containsKey(userName);
	}

	/**
	 * Check if user exists and the password match the username
	 * 
	 * @param userName
	 *            - the username
	 * @param password
	 *            - the given password
	 * @return true if user match the requirements.
	 */
	public boolean checkUser(String userName, String password) {
		if ((checkIfUserExists(userName)) && (checkUserPassword(userName, password)))
			return true;
		return false;
	}

	/**
	 * Checks if the user can log in
	 * 
	 * @param userName
	 *            - the username
	 * @param password
	 *            - the given password
	 * @return true if the user meet the requirements. False, otherwise.
	 */
	public boolean tryToLogIn(String userName, String password) {
		_userLock.readLock().lock();
		try {
			return (checkUser(userName, password) && (!isLoggedIn(userName)));
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * Checks if the user can sign out
	 * 
	 * @param userName
	 *            - the username
	 * @return true if he can. False, otherwise.
	 */
	public boolean tryToSignOut(String userName) {
		_userLock.readLock().lock();
		try {
			return (isLoggedIn(userName));
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * Update the Json of users
	 */
	public void updateJsonUser() {
		_userLock.readLock().lock();
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter write = null;
			try {
				write = new FileWriter("Database/Users.json");
			} catch (IOException e) {
			}
			List<User> users = new LinkedList<User>();
			for (Map.Entry<String, User> user : _users.entrySet()) {
				users.add(user.getValue());
			}
			Users usr = new Users(users);
			gson.toJson(usr, Users.class, write);
			try {
				write.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * Returns the connectId of a specified user
	 * 
	 * @return the connectId
	 */
	public ConcurrentHashMap<String, Integer> getConnectIdMap() {
		_userLock.readLock().lock();
		try {
			return this._connectIdMap;
		} finally {
			_userLock.readLock().unlock();
		}
	}
}
