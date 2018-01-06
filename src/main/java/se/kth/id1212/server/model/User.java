package se.kth.id1212.server.model;

import javax.websocket.Session;

/**
 * Created by Robin on 2018-01-06.
 */
public class User {
    private Session session;
    private String name;

    public User(Session session, String name) {
        this.session = session;
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
