package bgu.spl.net.objects;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.api.Connections;

public class
BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {
    private int connectionId;
    private ConnectionsImpl<String> connections;
    private boolean terminate;
    private String username = "";
    private commands command = commands.getInstance();

    public BidiMessagingProtocolImpl(){}

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl<String>) connections;
        terminate = false;
    }

    @Override
    public void process(T message) {
        String msg = message.toString();
        String content;
        String[] args = msg.split(" ");
        if(msg.equals(""))
            return;
        switch (Integer.parseInt(args[0])) {
            case 1:
                connections.send(connectionId, command.register(args[1], args[2], args[3]));
                break;
            case 2:
                String res;
                if(username.equals(""))
                    res = command.login(args[1], args[2], args[3]);
                else
                    res = "11 2";
                connections.send(connectionId, res);
                if(res.equals("10 2")){
                    username = args[1];
                    connections.addOnlineUser(connectionId, username);
                    command.sendUnseenPosts(username);
                }
                break;
            case 3:
                if(!command.logout(username))
                    connections.send(connectionId, "11 3");
                else {
                    connections.removeOnlineUser(connectionId, username);
                    connections.send(connectionId, "10 3");
                    terminate = true;
                }
                break;
            case 4:
                if(args[1].equals("0"))
                    connections.send(connectionId, command.follow(username, args[2]));
                else
                    connections.send(connectionId, command.unfollow(username, args[2]));
                break;
            case 5:
                content = msg.substring(2);
                connections.send(connectionId, command.post(username, content));
                break;
            case 6:
                content = msg.substring(args[0].length() + args[1].length() + 2);
                connections.send(connectionId, command.pm(username, args[1], content));
                break;
            case 7:
                if(command.logstat(username) != null)
                    connections.send(connectionId, "11 7");
                break;
            case 8:
                if(command.stat(username, args[1]) != null)
                    connections.send(connectionId, "11 8");
                break;
            case 12:
                connections.send(connectionId, command.block(username, args[1]));
                break;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }
}
