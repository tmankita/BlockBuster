package bgu.spl181.net.json;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Users implements Serializable {
	// local variables
	@SerializedName("users")
	@Expose
	private List<User> users = null;
	
	// constructor
	public Users(List<User> users) {
		this.users = new LinkedList(users);
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}