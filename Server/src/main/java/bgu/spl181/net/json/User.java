package bgu.spl181.net.json;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Serializable {
	// local variables
	@SerializedName("username")
	@Expose
	private String username;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("password")
	@Expose
	private String password;
	@SerializedName("country")
	@Expose
	private String country;
	@SerializedName("movies")
	@Expose
	private List<MovieOfUser> movies = null;
	@SerializedName("balance")
	@Expose
	private String balance;

	// constructor
	public User(String userName, String password, String country) {
		this.username = userName;
		this.password = password;
		this.country = country;
		this.balance = "0";
		this.movies = new LinkedList();
		this.type = "normal";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<MovieOfUser> getMovies() {
		return movies;
	}

	public void setMovies(List<MovieOfUser> movies) {
		this.movies = movies;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	/**
	 * Adds cash to the user balance
	 * 
	 * @param add
	 *            - the amount of cash should be added
	 */
	public void addToBalance(int add) {
		int oldBalance = Integer.parseInt(this.balance);
		int newBalance = oldBalance + add;
		this.balance = (new Integer(newBalance)).toString();
	}

	/**
	 * Rent a new movie
	 * 
	 * @param movieId
	 *            - the movie ID
	 * @param movieName
	 *            - the name of the movie
	 */
	public void rentMovie(String movieId, String movieName) {
		MovieOfUser newMovie = new MovieOfUser(movieId, movieName);
		movies.add(newMovie);
	}

	/**
	 * Returns a movie
	 * 
	 * @param movieId
	 *            - the movie ID
	 */
	public void returnMovie(String movieId) {
		MovieOfUser toRemove = null;
		for (MovieOfUser movie : movies) {
			if (movie.getId().equals(movieId))
				toRemove = movie;
		}
		movies.remove(toRemove);
	}

	/**
	 * Adds movie to the user movie's list
	 * 
	 * @param mov
	 */
	public void addMovie(MovieOfUser mov) {
		movies.add(mov);
	}

	/**
	 * Charge the balance of the user in case of rent
	 * 
	 * @param amount
	 *            - the price of the renting
	 */
	public void chargeBalance(int amount) {
		int balanceInt = Integer.parseInt(this.balance);
		Integer newBalance = new Integer(balanceInt - amount);
		this.balance = newBalance.toString();
	}

	/**
	 * Check if the user rent the given movie name
	 * 
	 * @param movieName
	 *            - the name of the movie
	 * @return true if he rented it, False, otherwise.
	 */
	public boolean checkRent(String movieName) {
		for (MovieOfUser mou : this.movies)
			if (((mou.getName()).equals(movieName)))
				return true;
		return false;
	}

	/**
	 * Removes a movie from the user movie's list
	 * 
	 * @param movieName
	 *            - the name of the movie
	 */
	public void removeMovie(String movieName) {
		for(int i = 0; i<movies.size(); i++) {
			if(((movies.get(i)).getName()).equals(movieName))
				movies.remove(i);
		}
	}

}