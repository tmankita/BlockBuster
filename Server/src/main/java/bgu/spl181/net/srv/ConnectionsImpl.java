package bgu.spl181.net.srv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.bidi.ConnectionHandler;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.json.User;

public class ConnectionsImpl<T> implements Connections<T> {
	// local variables
	ConcurrentHashMap<Integer ,ConnectionHandler> connectionHandlers;
	
	// constructor
	public ConnectionsImpl(){
		connectionHandlers= new ConcurrentHashMap<Integer, ConnectionHandler>();
	}
	
	/**
	 * Adds connectionHandler to the map
	 * @param handler - the ConnectionHandler
	 * @param connectId - the connect ID
	 */
	public void addConnectHandler(ConnectionHandler handler, int connectId) {
		connectionHandlers.putIfAbsent(connectId, handler);
	}

	/**
	 * Sends a replay to the client
	 */
	@Override
	public boolean send(int connectionId, T msg) {
		if(connectionHandlers.containsKey(connectionId)) {
		(connectionHandlers.get(connectionId)).send(msg);
		return true;
		}
		else
		return false;
	}

	/**
	 * Sends the same message to all active users
	 */
	@Override
	public void broadcast(T msg) {
		for(Map.Entry<Integer ,ConnectionHandler> handler : connectionHandlers.entrySet()) {
			handler.getValue().send(msg);
		}
		
	}

	/**
	 * Removes the ConnectionHandler from the map
	 */
	@Override
	public void disconnect(int connectionId) {
		connectionHandlers.remove(connectionId);
		
	}
}
