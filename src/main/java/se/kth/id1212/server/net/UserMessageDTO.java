package se.kth.id1212.server.net;

import se.kth.id1212.server.model.User;

/**
 * Created by Robin on 2018-01-06.
 */
public class UserMessageDTO {
    private User user;
    private String message;

    public UserMessageDTO(User user, String message) {
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
