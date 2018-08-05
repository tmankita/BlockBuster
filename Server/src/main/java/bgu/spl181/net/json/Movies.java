package bgu.spl181.net.json;

import java.util.LinkedList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movies {
	// local variables
	@SerializedName("movies")
	@Expose
	private List<Movie> movies = null;
	
	// constuctor
	public Movies(List<Movie> movies) {
		this.movies = new LinkedList(movies);
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
}