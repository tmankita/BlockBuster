package bgu.spl181.net.api.bidi;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bgu.spl181.net.json.Movie;
import bgu.spl181.net.srv.ConnectionsImpl;

public class MovieRentalServiceProtocol extends UserTextServiceProtocol {
	// variables
	protected MovieSharedData _movieData;

	// constructor
	public MovieRentalServiceProtocol(MovieSharedData data) {
		super(data);
		_movieData = data;
	}

	/**
	 * navigate the REQUEST command into the specific requested REQUEST
	 */
	protected void request(String[] request) {
		// in case which the user is not logged in - error
		if (_user == null || !_movieData.isLoggedIn(_user.getUsername())) {
			if (request[1] == null)
				error("request failed");
			else if (request[2] != null)
				error("request " + request[1] + " " + request[2] + " failed");
			else
				error("request " + request[1] + " failed");
		} else {
			if (request[1].equals("balance")) {
				if (request[2].equals("info"))
					balanceInfo(request);
				else if (request[2].equals("add"))
					balanceAdd(request);
			} else if (request[1].equals("info"))
				movieInfo(request);
			else if (request[1].equals("rent"))
				movieRent(request);
			else if (request[1].equals("return"))
				returnMovie(request);
			else if (request[1].equals("addmovie"))
				addMovie(request);
			else if (request[1].equals("remmovie"))
				removeMovie(request);
			else if (request[1].equals("changeprice"))
				changePrice(request);
		}
	}

	/**
	 * An admin permitted function. this method change the price of a specified
	 * movie to the given value.
	 * 
	 * @param request
	 *            - the REQUEST String
	 */
	private void changePrice(String[] request) {
		String movieName = request[2];
		int price;
		if(request[3] != null)
		 price = Integer.parseInt(request[3]);
		else
			price = -1;
		// check if the REQUEST meets the requirements
		if (_movieData.tryToChangeAPrice(_user, movieName, price)) {
			// change the price of the movie
			_movieData.changePrice(movieName, price);
			_movieData.updateJsonMovie();
			ack("changeprice " + '"' + movieName + '"' + " success");
			int copies = _movieData.getNumberOfCopies(movieName);
			brodcastToLoggedIn("movie " + '"' + movieName + '"' + " " + copies + " " + price);

		} else
			error("request changeprice failed");
	}

	/**
	 * An admin permitted function. Removes a movie from the movie list.
	 * 
	 * @param request
	 *            - thr REQUEST String
	 */
	private void removeMovie(String[] request) {
		String movieName = request[2];
		// check if the REQUEST meets the requirements
		if (_movieData.TryToRemoveAMovie(_user, movieName)) {
			// removes the movie from the list
			_movieData.removeMovie(movieName);
			_movieData.updateJsonMovie();
			ack("remmovie " + '"' + movieName + '"' + " success");
			brodcastToLoggedIn("movie " + '"' + movieName + '"' + " removed");
		} else
			error("request remmovie failed");
	}

	/**
	 * An admin permitted function. Add a movie to the movie's list.
	 * 
	 * @param request
	 *            - the REQUEST String
	 */
	private void addMovie(String[] request) {
		String movieName = request[2];
		// check if the REQUEST meets the requirements
		if (_movieData.tryToAddAMovie(_user, movieName)) {
			// check if there is amount and price fields
			if (request[3] != null && request[4] != null) {
				int amount = Integer.parseInt(request[3]);
				int price = Integer.parseInt(request[4]);
				// check if the price or the amount is not negative
				if (price > 0 && amount > 0) {
					LinkedList<String> bannedMovies = new LinkedList();
					if (request[5] != null) {
						for (int i = 5; i < request.length; i++)
							if (request[i] != null)
								bannedMovies.add(request[i]);
					}
					// creates new movie Object
					Movie mov = new Movie(movieName, amount, price, _movieData.getNextId(), bannedMovies);
					// add the movie to the list
					_movieData.addMovie(mov);
					_movieData.updateJsonMovie();
					ack("addmovie " + '"' + movieName + '"' + " success");
					brodcastToLoggedIn("movie " + '"' + movieName + '"' + " " + amount + " " + price);
					return;
				}
			}
		}
		error("request addmovie failed");
	}

	/**
	 * This method represents a return of a movie by a client.
	 * 
	 * @param returnMovie
	 *            - the REQUEST String
	 */
	private void returnMovie(String[] returnMovie) {
		String movieName = returnMovie[2];
		// check if the REQUEST meets the requirements
		if (_movieData.TryToReturnAMovie(_user, movieName)) {
			// return the movie
			_movieData.returnMovie(_user, movieName);
			_userData.updateJsonUser();
			_movieData.updateJsonMovie();
			ack("return " + '"' + movieName + '"' + " success");
			int copies = _movieData.getNumberOfCopies(movieName);
			int price = _movieData.getMoviePrice(movieName);
			brodcastToLoggedIn("movie " + '"' + movieName + '"' + " " + copies + " " + price);
		} else
			error("request return failed");
	}

	/**
	 * this method represents a rent of a movie
	 * 
	 * @param rent
	 *            - the REQUEST String
	 */
	private void movieRent(String[] rent) {
		String movieName = rent[2];
		// check if the REQUEST meets the requirements
		if (_movieData.TryToRentAMovie(_user, movieName)) {
			// rent the movie by the user
			_movieData.rentMovie(_user, movieName);
			_movieData.updateJsonMovie();
			_userData.updateJsonUser();
			ack("rent " + '"' + movieName + '"' + " success");
			int copies = _movieData.getNumberOfCopies(movieName);
			int price = _movieData.getMoviePrice(movieName);
			brodcastToLoggedIn("movie " + '"' + movieName + '"' + " " + copies + " " + price);
		} else
			error("request rent failed");
	}

	/**
	 * This method sends to the user the information about a specific movie or of
	 * all current movies
	 * 
	 * @param info
	 *            - the REQUEST String
	 */
	private void movieInfo(String[] info) {
		// checks if the user is logged in
		if (_movieData.checkLog(_user.getUsername())) {
			// check if a specified movie has been requested
			if (info[2] != null) {
				String movieName = info[2];
				// check if the REQUEST meets the requirements
				if (_movieData.tryToGetInfo(movieName)) {
					int copies = _movieData.getNumberOfCopies(movieName);
					int price = _movieData.getMoviePrice(movieName);
					String banned = _movieData.getBannedCountries(movieName);
					ack("info " + '"' + movieName + '"' + " " + copies + " " + price + " " + banned);
				} else {
					error("request info failed");
				}
			} else {
				// if no movie where mentioned - print the information of the existing movies in
				// the list
				ack("info " + _movieData.printAllMovies());
			}
		} else
			error("request info failed");
	}

	/**
	 * This method adds cash to the user balance
	 * 
	 * @param balanceAdd
	 *            - the REQUEST String
	 */
	private void balanceAdd(String[] balanceAdd) {
		// check if the REQUEST meets the requirements
		if (_movieData.tryToBalanceAdd(_user.getUsername())) {
			// add cash to the balance
			ack("balance " + _movieData.addBalance(_user, Integer.parseInt(balanceAdd[3])) + " added " + balanceAdd[3]);
			_userData.updateJsonUser();
		} else
			error("request balance add failed");
	}

	/**
	 * This method return the balance info of the user
	 * 
	 * @param balanceInfo
	 *            - the REQUEST String
	 */
	private void balanceInfo(String[] balanceInfo) {
		// check if the REQUEST meets the requirements
		if (_movieData.tryToBalanceInfo(_user.getUsername()))
			ack("balance " + _movieData.getBalance(_user));
		else
			error("request balance info failed");
	}

	/**
	 * This class links between the Connection to the Protocol
	 */
	@Override
	public void start(int connectionId, Connections<String> connections) {
		this._connectId = connectionId;
		this._connect = (ConnectionsImpl<String>) connections;
	}

	/**
	 * This method is being called once there is a full message decoded and it
	 * should be processed by the Protocol
	 */
	@Override
	public void process(String message) {
		// change a String into array of strings
		String[] msg = stringToArray(message);
		// handle the message
		operate(msg);
	}

	/**
	 * This method change the String into array of strings
	 * 
	 * @param message
	 *            - the String given
	 * @return - array of strings
	 */
	public String[] stringToArray(String message) {
		String[] msg = new String[1000];
		int index = 0;
		// add a sub-string to the array in case if it met the specific pattern
		Matcher match = (Pattern.compile("(-?\\d+)|(\\w+)|\"(.*?)\"")).matcher(message);
		while (match.find()) {
			msg[index] = (match.group(0)).replace("\"", "");
			index++;
		}
		return msg;
	}

	/**
	 * Return true if the user signed out. False, if not.
	 */
	@Override
	public boolean shouldTerminate() {
		return _shouldTerminate;
	}
}
