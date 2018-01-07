package se.kth.id1212.server.model;

/**
 * Created by Robin on 2018-01-06.
 */
public class UserMessage {
    private User user;
    private String message;

    public UserMessage(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
