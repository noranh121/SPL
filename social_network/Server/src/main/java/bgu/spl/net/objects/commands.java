package bgu.spl.net.objects;

import java.time.LocalDate;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class commands {
    private static commands commands = new commands();
    private ConcurrentHashMap<String, Pair<String, Date>> users; //<username, <password, birthday>>
    private ConcurrentHashMap<String, Boolean> status; //<username, isOnline>
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> followers;
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> following;
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> posts;
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> pms;
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> blockList;
    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> unseenPosts;
    private String[] censoredWords = {"Trump", "war"};

    public commands() {
        users = new ConcurrentHashMap<>();
        status = new ConcurrentHashMap<>();
        followers = new ConcurrentHashMap<>();
        following = new ConcurrentHashMap<>();
        posts = new ConcurrentHashMap<>();
        pms = new ConcurrentHashMap<>();
        blockList = new ConcurrentHashMap<>();
        unseenPosts = new ConcurrentHashMap<>();
    }

    public static commands getInstance() {
        if (commands == null)
            commands = new commands();
        return commands;
    }

    //Opcode number 1
    public String register(String username, String password, String date) {
        Date date1 = new Date(date);
        if (!isRegistered(username)) {
            users.put(username, new Pair<>(password, date1));
            status.put(username, false);
            following.put(username, new LinkedBlockingQueue<>());
            followers.put(username, new LinkedBlockingQueue<>());
            posts.put(username, new LinkedBlockingQueue<>());
            pms.put(username, new LinkedBlockingQueue<>());
            blockList.put(username, new LinkedBlockingQueue<>());
            unseenPosts.put(username, new LinkedBlockingQueue<>());
            return "10 1";
        }
        return "11 1";
    }

    //Opcode number 2
    public String login(String username, String password, String captcha) {
        if (isRegistered(username) && !isLogged(username) & password.equals(users.get(username).getFirst())
                & captcha.equals("1")) {
            status.replace(username, true);
            return "10 2";
        }
        return "11 2";
    }

    //Opcode number 3
    public boolean logout(String username) {
        if (isRegistered(username) && isLogged(username)) {
            status.replace(username, false);
            return true;
        }
        return false;
    }

    //Opcode number 4
    //Follower is attempting to follow the followed
    public String follow(String follower, String followed) {
        try {
            if (isRegistered(follower) && isRegistered(followed)
                    && isLogged(follower) && !isBlocked(followed, follower) && !following.get(follower).contains(followed)) {
                followers.get(followed).put(follower);
                following.get(follower).put(followed);
                return "10 4 " + followed;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "11 4";
    }

    //Follower is attempting to follow followed
    public String unfollow(String follower, String followed) {
        if (isRegistered(follower) && isRegistered(followed)
                && isLogged(follower) && !isBlocked(followed, follower) && following.get(follower).contains(followed)) {
            followers.get(followed).remove(follower);
            following.get(follower).remove(followed);
            return "10 4 " + followed;
        }
        return "11 4";
    }

    //add filters
    //Opcode number 5
    public String post(String sender, String content) {
        try {
            if (isRegistered(sender) && isLogged(sender)) {
                for (String follower : followers.get(sender)) {
                    if (isLogged(follower))
                        ConnectionsImpl.getInstance().send(ConnectionsImpl.getInstance().getUserID(follower), "9 0 " + sender + " " + content);
                    else
                        unseenPosts.get(follower).put("9 1 " + sender + " " + content);
                }
                posts.get(sender).put(content);
                checkTagsAndSend(sender, content);
                return "10 5";
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "11 5";
    }

    //Opcode number 6
    public String pm(String sender, String receiver, String content) {
        try {
            content = censorContent(content);
            if (isRegistered(sender) && isRegistered(receiver)
                    && isLogged(sender) && isLogged(receiver) && !isBlocked(receiver, sender)) {
                ConnectionsImpl.getInstance().send(ConnectionsImpl.getInstance().getUserID(receiver), "9 0 " + sender + " " + content);
                pms.get(sender).put(content);
                return "10 6";
            }
        } catch (Exception e) {
        }
        return "11 6";
    }

    //Opcode number 7
    public String logstat(String username) {
        if(isRegistered(username) && isLogged(username)) {
            int id = ConnectionsImpl.getInstance().getUserID(username);
            Enumeration<String> users = this.users.keys();
            while (users.hasMoreElements()) {
                String user = users.nextElement();
                if (isLogged(user) && !isBlocked(user, username))
                    ConnectionsImpl.getInstance().send(id, "10 7 " + statContent(user));
            }
            return null;
        }
        return "11 7";
    }

    //Opcode number 8
    public String stat(String username, String list) {
        if (isRegistered(username) && isLogged(username)) {
            String[] usersList = list.split("\\|");
            int id = ConnectionsImpl.getInstance().getUserID(username);
            for (int i = 0; i < usersList.length; i++) {
                String user = usersList[i];
                if (!isBlocked(user, username))
                    ConnectionsImpl.getInstance().send(id, "10 8 " + statContent(user));
            }
            return null;
        }
        return "11 8";
    }

    //Opcode number 12
    public String block(String blocker, String blocked) {
        if (isRegistered(blocker) && isRegistered(blocked) && !blockList.get(blocker).contains(blocked)) {
            try {
                blockList.get(blocker).put(blocked);
                followers.get(blocked).remove(blocker);
                following.get(blocker).remove(blocked);
                followers.get(blocker).remove(blocked);
                following.get(blocked).remove(blocker);
            } catch (Exception e) {
            }
            return "10 12";
        }
        return "11 12";
    }

    /* Help Functions*/
    private int getAge(String username) {
        Date birthday = users.get(username).getSecond();
        return birthday.getAge();
    }

    private String statContent(String username) {
        int age = getAge(username);
        int numOfPosts = posts.get(username).size();
        int followers = this.followers.get(username).size();
        int following = this.following.get(username).size();
        return age + " " + numOfPosts + " " + followers + " " + following;
    }

    private boolean isRegistered(String username) {
        return users.containsKey(username);
    }

    private boolean isLogged(String username) {
        return status.get(username);
    }

    private boolean isBlocked(String user1, String user2) {
        return blockList.get(user1).contains(user2);
    }

    private String censorContent(String content){
        for (int i = 0; i < censoredWords.length; i++) {
            content = content.replaceAll(censoredWords[i], "<filtered>");
        }
        return content;
    }
    private void checkTagsAndSend(String sender, String content) {
        try {
            int start = content.indexOf("@");
            while (start >= 0) {
                int end = content.indexOf(" ", start);
                String user = content.substring(start+1, end);
                if (isRegistered(user) && !followers.get(sender).contains(user) && !isBlocked(user, sender)) {
                    if (isLogged(user))
                        ConnectionsImpl.getInstance().send(ConnectionsImpl.getInstance().getUserID(user), "9 0 " + sender + " " + content);
                    else
                        unseenPosts.get(user).put("9 0 " + sender + " " + content);
                }
                start = content.indexOf("@", end);
            }
        } catch (Exception e) {
        }
    }

    public void sendUnseenPosts(String username) {
        String content;
        while ((content = unseenPosts.get(username).poll()) != null)
            ConnectionsImpl.getInstance().send(ConnectionsImpl.getInstance().getUserID(username), content);
    }
}
