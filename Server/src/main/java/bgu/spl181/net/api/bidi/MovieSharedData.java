package bgu.spl181.net.api.bidi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import bgu.spl181.net.json.Movie;
import bgu.spl181.net.json.MovieOfUser;
import bgu.spl181.net.json.Movies;
import bgu.spl181.net.json.User;

/**
 * this class holds all the data of the movies
 */
public class MovieSharedData extends UserSharedData {
	// local variables
	protected ConcurrentHashMap<String, Movie> _movies;
	private ReentrantReadWriteLock _movieLock;
	private int movieNextId;

	// constructor
	public MovieSharedData(List<Movie> movies, List<User> users) {
		super(users);
		_movies = new ConcurrentHashMap();
		_movieLock = new ReentrantReadWriteLock();
		this.movieNextId = 0;
		for (Movie m : movies) {
			_movies.put(m.getName(), m);
			if (Integer.parseInt(m.getId()) > this.movieNextId)
				this.movieNextId = Integer.parseInt(m.getId());
		}
		this.movieNextId++;
	}

	/**
	 * Returns the user balance
	 * 
	 * @param user
	 *            - the user required
	 * @return - the user current balance
	 */
	public int getBalance(User user) {
		_movieLock.readLock().lock();
		try {
			return Integer.parseInt(user.getBalance());
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * returns the movie according to a specific movie id
	 * 
	 * @param movieId
	 *            - the id of the movie
	 * @return - movie
	 */
	public Movie getMovie(String movieId) {
		return _movies.get(movieId);
	}

	/**
	 * This method update the Json file
	 */
	public void updateJsonMovie() {
		_movieLock.readLock().lock();
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter write = null;
			try {
				write = new FileWriter("Database/Movies.json");
			} catch (IOException e) {
			}
			List<Movie> movies = new LinkedList<Movie>();
			for (Map.Entry<String, Movie> movie : _movies.entrySet()) {
				movies.add(movie.getValue());
			}
			Movies mov = new Movies(movies);
			gson.toJson(mov, Movies.class, write);
			try {
				write.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			_movieLock.readLock().unlock();
		}

	}

	/**
	 * This method adds cash to the user balance
	 * @param user - the user required
	 * @param amount - the amount should be added
	 * @return - the new amount
	 */
	public int addBalance(User user, int amount) {
		_movieLock.writeLock().lock();
		try {
			user.addToBalance(amount);
			return Integer.parseInt(user.getBalance());
		} finally {
			_movieLock.writeLock().unlock();
		}
	}

	/**
	 * Checks if the movies exists in the movie's list
	 * @param movieName - the name of the movie need to be checked
	 * @return true if exists. False, otherwise.
	 */
	public boolean isMovieExists(String movieName) {
		if (_movies.containsKey(movieName))
			return true;
		else
			return false;
	}

	/**
	 * This method checks if the Info command meets the requirements
	 * @param movieName - the name of the movie we need to send info about
	 * @return true if meet the requirements. False, otherwise.
	 */
	public boolean tryToGetInfo(String movieName) {
		_movieLock.readLock().lock();
		try {
			return isMovieExists(movieName);
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Returns the number of copies of specified movie
	 * @param movieName - the name of the movie
	 * @return the number of copies available
	 */
	public int getNumberOfCopies(String movieName) {
		_movieLock.readLock().lock();
		try {
			Movie mov = getMovie(movieName);
			return Integer.parseInt(mov.getAvailableAmount());
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Return the price of the movie
	 * @param movieName - the name of the movie
	 * @return the price of the movie
	 */
	public int getMoviePrice(String movieName) {
		_movieLock.readLock().lock();
		try {
			Movie mov = getMovie(movieName);
			return Integer.parseInt(mov.getPrice());
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Returns the banned countries of a specified movie
	 * @param movieName - the name of the movie
	 * @return - the banned countries
	 */
	public String getBannedCountries(String movieName) {
		_movieLock.readLock().lock();
		try {
			Movie mov = getMovie(movieName);
			String toReturn = "";
			for (String banned : mov.getBannedCountries()) {
				toReturn += '"' + banned + '"' + " ";
			}
			return toReturn;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Returns a String of all movies in the list
	 * @return
	 */
	public String printAllMovies() {
		_movieLock.readLock().lock();
		try {
			String movies = "";
			for (Map.Entry<String, Movie> movie : _movies.entrySet()) {
				movies += '"' + movie.getKey() + '"' + " ";
			}
			return movies;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Adds movie to the user movie's list
	 * @param usr - the User
	 * @param mov - the movie which should be added
	 */
	public void addMovieToUser(User usr, Movie mov) {
		MovieOfUser mou = new MovieOfUser(mov.getId(), mov.getName());
		usr.addMovie(mou);
	}

	/**
	 * This method charge cash from the user balance in case of renting a movie
	 * @param usr - the User
	 * @param mov - the movie
	 */
	public void charge(User usr, Movie mov) {
		usr.chargeBalance(Integer.parseInt(mov.getPrice()));
	}

	/**
	 * Reduces the amount of the available copies
	 * @param mov - the movie
	 */
	public void rentCopy(Movie mov) {
		mov.reduceAmount();
	}

	/**
	 * Check if there are available copies of the specified movie
	 * @param mov - the movie
	 * @return true if there is available copy. False, otherwise.
	 */
	public boolean checkCopiesAmount(Movie mov) {
		return (Integer.parseInt(mov.getAvailableAmount()) > 0);
	}

	/**
	 * Checks if the user is not banned to watch the given movie
	 * @param usr - the User
	 * @param mov - the Movie
	 * @return true, if the user can rent the movie. False, otherwise.
	 */
	public boolean checkBanned(User usr, Movie mov) {
		for (String str : mov.getBannedCountries())
			if (str.equals(usr.getCountry()))
				return true;
		return false;
	}

	/**
	 * Checks if the movie already was rented by the movie
	 * @param usr - the User
	 * @param mov - the Movie
	 * @return - true if the users already rented the movie. False, otherwise.
	 */
	public boolean checkIfRented(User usr, Movie mov) {
		return usr.checkRent(mov.getName());
	}

	/**
	 * Checks if the user have enough cash to rent a specified movie
	 * @param usr - the User
	 * @param mov - the Movie
	 * @return true if the user have enough money
	 */
	public boolean checkCash(User usr, Movie mov) {
		return (Integer.parseInt(usr.getBalance()) >= Integer.parseInt(mov.getPrice()));
	}

	/**
	 * Checks if the user meets the requirements of renting a movie
	 * @param usr - the User
	 * @param movieName - the name of the movie
	 * @return true if meets the requirements. False, otherwise.
	 */
	public boolean TryToRentAMovie(User usr, String movieName) {
		_movieLock.readLock().lock();
		try {
			if(movieName != null)
			// checks if the user is logged in
			if (isLoggedIn(usr.getUsername()))
				// checks if the movies exists
				if (isMovieExists(movieName)) {
					Movie mov = getMovie(movieName);
					// checks if there are available copies of the movie
					if (checkCopiesAmount(mov))
						// checks if the user is not banned from renting this movie
						if (!checkBanned(usr, mov))
							// checks if the user haven't rented the movie already
							if (!checkIfRented(usr, mov))
								// checks if the user have enough cash
								if (checkCash(usr, mov))
									return true;
				}
			return false;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Removes movie from the user's movie list
	 * @param usr - the User
	 * @param movieName - the name of the movie
	 */
	public void removeMovieFromUser(User usr, String movieName) {
		usr.removeMovie(movieName);
	}

	/**
	 * Increases the amount of copies available once the user returns the movie
	 * @param mov - the Movie
	 */
	public void returnCopy(Movie mov) {
		mov.increaseAmount();
	}

	/**
	 * Checks if the user can return a movie
	 * @param usr - the User
	 * @param movieName - the name of the movie
	 * @return true if the user can return the movie. False, otherwise.
	 */
	public boolean TryToReturnAMovie(User usr, String movieName) {
		_movieLock.readLock().lock();
		try {
			if(movieName != null)
			// checks if the user is logged in
			if (isLoggedIn(usr.getUsername()))
				// checks if the movie exists
				if (isMovieExists(movieName)) {
					Movie mov = getMovie(movieName);
					// checks if the user rented this movie
					if (checkIfRented(usr, mov))
						return true;
				}
			return false;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * This method return a movie by a user
	 * @param usr - the User
	 * @param movieName - the name of the movie
	 */
	public void returnMovie(User usr, String movieName) {
		_movieLock.writeLock().lock();
		try {
			returnCopy(getMovie(movieName));
			removeMovieFromUser(usr, movieName);
		} finally {
			_movieLock.writeLock().unlock();
		}
	}

	/**
	 * THis method rent a movie by a user
	 * @param user - the User
	 * @param movieName - the name of the movie
	 */
	public void rentMovie(User user, String movieName) {
		_movieLock.writeLock().lock();
		try {
			Movie mov = getMovie(movieName);
			rentCopy(mov);
			charge(user, mov);
			addMovieToUser(user, mov);
		} finally {
			_movieLock.writeLock().unlock();
		}
	}

	/**
	 * Checks if the user is an admin
	 * @param usr - the User
	 * @return true if the user is admin. False, otherwise.
	 */
	public boolean checkIfUserIsAdmin(User usr) {
		return (usr.getType()).equals("admin");
	}

	/**
	 * Checks if the admin can add a movie to the movie's list
	 * @param usr - The admin User
	 * @param movieName - the name of the movie
	 * @return true if the admin can add the movie. False, otherwise.
	 */
	public boolean tryToAddAMovie(User usr, String movieName) {
		_movieLock.readLock().lock();
		try {
			// checks if the admin is logged in
			if (isLoggedIn(usr.getUsername()))
				// checks if the user is admin
				if (checkIfUserIsAdmin(usr))
					// checks if the movie exists
					if (!isMovieExists(movieName))
						return true;
			return false;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Checks if the movie is rented
	 * @param mov - the Movie
	 * @return true if the movie is being rented. False, otherwise.
	 */
	public boolean checkIfMovieIsRented(Movie mov) {
		return (Integer.parseInt(mov.getAvailableAmount()) < Integer.parseInt(mov.getTotalAmount()));
	}

	/**
	 * Checks if an admin can remove the movie from the movie's list
	 * @param usr - the User
	 * @param movieName - the name of the movie
	 * @return true if the movie can be removed. False, otherwise.
	 */
	public boolean TryToRemoveAMovie(User usr, String movieName) {
		_movieLock.readLock().lock();
		try {
			if(movieName != null)
			// checks if the user is logged in
			if (isLoggedIn(usr.getUsername()))
				// checks if the user is admin
				if (checkIfUserIsAdmin(usr))
					// checks if the movie exists
					if (isMovieExists(movieName)) {
						Movie mov = getMovie(movieName);
						// check if the movie is being rented
						if (!checkIfMovieIsRented(mov))
							return true;
					}
			return false;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Returns the next ID for a new added movie
	 * @return the next ID
	 */
	public int getNextId() {
		return this.movieNextId;
	}

	/**
	 * Adds a movie to the movie's list
	 * @param mov - the movie to be added
	 */
	public void addMovie(Movie mov) {
		_movieLock.writeLock().lock();
		try {
			_movies.put(mov.getName(), mov);
			this.movieNextId++;
		} finally {
			_movieLock.writeLock().unlock();
		}
	}

	/**
	 * Removes a movie from the movie's list
	 * @param movieName
	 */
	public void removeMovie(String movieName) {
		_movieLock.writeLock().lock();
		try {
			_movies.remove(movieName);
			this.movieNextId--;
		} finally {
			_movieLock.writeLock().unlock();
		}
	}

	/**
	 * Checks if the admin can change the price of a movie
	 * @param usr - The admin User
	 * @param movieName - the name of the movie
	 * @param price - the new price of the movie
	 * @return true if it's ok to change the price. False, otherwise.
	 */
	public boolean tryToChangeAPrice(User usr, String movieName, int price) {
		_movieLock.readLock().lock();
		try {
			if(movieName != null)
			// checks if the user logged in
			if (isLoggedIn(usr.getUsername()))
				// checks if the user is admin
				if (checkIfUserIsAdmin(usr))
					// checks if the movie exists
					if (isMovieExists(movieName))
						if (price > 0)
							return true;
			return false;
		} finally {
			_movieLock.readLock().unlock();
		}
	}

	/**
	 * Changes the price of a movie
	 * @param movieName - the name of the movie we want to change it's price
	 * @param price - the new price
	 */
	public void changePrice(String movieName, int price) {
		_movieLock.writeLock().lock();
		try {
			Movie mov = getMovie(movieName);
			mov.setPrice((new Integer(price).toString()));
		} finally {
			_movieLock.writeLock().unlock();
		}
	}

	/**
	 * Checks if the user can see his balance info
	 * @param userName - the username of the user
	 * @return true if he can. False, otherwise.
	 */
	public boolean tryToBalanceInfo(String userName) {
		_userLock.readLock().lock();
		try {
			return (isLoggedIn(userName));
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * Checks if the user can add cash to it's balance
	 * @param userName - the name of the user
	 * @return true if cash can be added. False, otherwise.
	 */
	public boolean tryToBalanceAdd(String userName) {
		_userLock.readLock().lock();
		try {
			return (isLoggedIn(userName));
		} finally {
			_userLock.readLock().unlock();
		}
	}

	/**
	 * Checks if the user is logged in
	 * @param userName - the name of the user
	 * @return true if the user logged in. False, otherwise.
	 */
	public boolean checkLog(String userName) {
		_userLock.readLock().lock();
		try {
			return (isLoggedIn(userName));
		} finally {
			_userLock.readLock().unlock();
		}
	}
}
