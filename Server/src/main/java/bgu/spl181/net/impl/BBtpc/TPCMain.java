package bgu.spl181.net.impl.BBtpc;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import bgu.spl181.net.api.bidi.MovieMessageEncoderDecoder;
import bgu.spl181.net.api.bidi.MovieRentalServiceProtocol;
import bgu.spl181.net.api.bidi.MovieSharedData;
import bgu.spl181.net.json.Movies;
import bgu.spl181.net.json.Users;
import bgu.spl181.net.srv.Server;

public class TPCMain {
	// takes the data from the Json and insert it into the SharedData
	public static void main(String[] args) {
		Gson gson = new Gson();
		int port = Integer.parseInt(args[0]);
		String movies = "Database/Movies.json";
		String users = "Database/Users.json";
		JsonReader moviesReader = null;
		JsonReader usersReader = null;
		try {
			moviesReader = new JsonReader(new FileReader(movies));
			usersReader = new JsonReader(new FileReader(users));
		} catch (FileNotFoundException e) {
		}
		Movies moviesJson = gson.fromJson(moviesReader, Movies.class);
		Users usersJson = gson.fromJson(usersReader, Users.class);
		
		// create the SharedData
		MovieSharedData _data = new MovieSharedData(moviesJson.getMovies(), usersJson.getUsers());
		
		// creates a TPC Server and operates
        Server.threadPerClient(
                port, //port
                () ->  new MovieRentalServiceProtocol(_data), //protocol factory
                MovieMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
	}
}