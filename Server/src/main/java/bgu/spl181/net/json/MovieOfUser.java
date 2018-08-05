package bgu.spl181.net.json;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieOfUser implements Serializable {
	// local variables
	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("name")
	@Expose
	private String name;
	
	// constructor
	public MovieOfUser(String movieId, String movieName) {
		this.name = movieName;
		this.id = movieId;
	}

	public String getId() {
	return id;
	}

	public void setId(String id) {
	this.id = id;
	}

	public String getName() {
	return name;
	}

	public void setName(String name) {
	this.name = name;
	}
}
