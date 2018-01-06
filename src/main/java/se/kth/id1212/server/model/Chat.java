package se.kth.id1212.server.model;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Robin on 2018-01-06.
 */
public class Chat {
    private HashMap<String, User> users = new HashMap<>();


    public Collection<User> getUsers() {
        return users.values();
    }

    public boolean addUser(User user) {
        if (!users.containsKey(user.getName())) {
            users.put(user.getName(), user);
            return true;
        }
        else return false;
    }

    public boolean removeUser(User user) {
        if (users.containsKey(user.getName())) {
            users.remove(user.getName());
            return true;
        }
        else return false;
    }
}
