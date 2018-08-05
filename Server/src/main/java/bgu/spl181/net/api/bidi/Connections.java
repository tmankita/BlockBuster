package bgu.spl181.net.api.bidi;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl181.net.srv.ConnectionHandler;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);
}
