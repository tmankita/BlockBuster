package bgu.spl181.net.api.bidi;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bgu.spl181.net.json.User;
import bgu.spl181.net.srv.ConnectionsImpl;

abstract public class UserTextServiceProtocol implements BidiMessagingProtocol<String> {
	// local variables
	protected UserSharedData _userData;
	protected User _user;
	protected ConnectionsImpl<String> _connect;
	protected boolean _shouldTerminate = false;
	protected int _connectId;

	// constructor
	public UserTextServiceProtocol(UserSharedData data) {
		_userData = data;
	}

	/**
	 * Match the required command to it's function
	 * 
	 * @param message
	 *            - the command given
	 */
	protected void operate(String[] message) {
		if (message[0].equals("REGISTER"))
			register(message);
		else if (message[0].equals("LOGIN"))
			login(message);
		else if (message[0].equals("SIGNOUT"))
			signOut(message);
		else if (message[0].equals("REQUEST"))
			request(message);
	}

	/**
	 * This method register a new user
	 * 
	 * @param register
	 *            - the given String
	 */
	public void register(String[] register) {
		if (register[1] != null) {
			// synchronized on the username to prevent the situation of 2 pepole
			// trying to register with the same username at the same time
			synchronized (register[1].intern()) {
				// checks if the user is already logged in or already registered or details
				// missing
				String userName = register[1];
				if (_user != null || register[2] == null || register[3] == null
						|| !(register[3].equals("country")) || register[4] == null || !_userData.tryToRegister(userName))
					error("registration failed");
				else {
					// if all above is right, register a new User to the data base
					User newUser = new User(register[1], register[2], register[4]);
					_userData.addUser(newUser);
					_userData.updateJsonUser();
					ack("registration succeeded");
				}
			}
		}
		else
			error("registration failed");
	}

	/**
	 * This method log in a user to the system
	 * 
	 * @param login
	 *            - the given String
	 */
	public void login(String[] login) {
		if (!_userData.tryToLogIn(login[1], login[2]))
			error("login failed");
		else {
			_userData.logIn(login[1], _connectId);
			_user = _userData.getUser(login[1]);
			ack("login succeeded");
		}
	}

	/**
	 * This method sign out a user
	 * 
	 * @param signout
	 *            - the given String
	 */
	public void signOut(String[] signout) {
		if (_user == null || !_userData.tryToSignOut(_user.getUsername()))
			error("signout failed");
		else {
			_shouldTerminate = true;
			_userData.logOut(_user.getUsername());
			ack("signout succeeded");
			_user = null;
			_connect.disconnect(_connectId);
		}
	}

	/**
	 * This method sends an error message to the client
	 * 
	 * @param error
	 *            - the error String
	 */
	public void error(String error) {
		_connect.send(_connectId, "ERROR " + error);
	}

	/**
	 * This method sends an ACK message to the client
	 * 
	 * @param ack
	 *            - the ACK String
	 */
	public void ack(String ack) {
		_connect.send(_connectId, "ACK " + ack);
	}

	/**
	 * This method broadcast a message to all logged in users
	 * 
	 * @param broad
	 *            - the String need to be broadcasts
	 */
	public void brodcastToLoggedIn(String broad) {
		broad = "BROADCAST " + broad;
		for (Map.Entry<String, Integer> connect : (_userData.getConnectIdMap()).entrySet()) {
			_connect.send((connect.getValue()).intValue(), broad);
		}
	}

	abstract protected void request(String[] request);
}
