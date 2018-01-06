package se.kth.id1212.server.net;

import se.kth.id1212.server.model.User;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by Robin on 2018-01-03.
 */
@ApplicationScoped
public class SessionHandler {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
