package bgu.spl.net.objects;

import bgu.spl.net.api.ConnectionHandler;
import bgu.spl.net.api.Connections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> activeConnections;
    private ConcurrentHashMap<T, Integer> users; //online users
    private AtomicInteger id;
    private static ConnectionsImpl connections = new ConnectionsImpl<>();

    public static ConnectionsImpl getInstance(){
        if (connections == null)
            connections = new ConnectionsImpl();
        return connections;
    }

    public ConnectionsImpl(){
        activeConnections = new ConcurrentHashMap<>();
        users = new ConcurrentHashMap<>();
        id = new AtomicInteger(0);
    }

    public void connect(int connectionId, ConnectionHandler<T> connectionHandler ) {
        activeConnections.putIfAbsent(connectionId, connectionHandler);
    }

    public int getId() {
        return id.getAndIncrement();
    }

    public void addOnlineUser(int connectionId, T username){
        users.put(username, connectionId);
    }
    public void removeOnlineUser(int connectionId, T username){
        users.remove(username, connectionId);
    }
    @Override
    public boolean send(int connectionId, T msg) {
        if(activeConnections.containsKey(connectionId)) {
            activeConnections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler<T> client: activeConnections.values()){
            client.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        activeConnections.remove(connectionId);
    }

    public int getUserID(String user) {
        return users.get(user);
    }
}
