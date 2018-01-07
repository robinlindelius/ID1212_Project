package se.kth.id1212.server.model;

import javax.websocket.Session;

/**
 * Created by Robin on 2018-01-06.
 */
public class User {
    private Session session;
    private String name;
    private int chatID;

    public User(String name, Session session) {
        this.session = session;
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public String getName() {
        return name;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }
}
