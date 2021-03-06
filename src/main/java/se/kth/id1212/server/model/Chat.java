package se.kth.id1212.server.model;

import java.util.*;

/**
 * Created by Robin on 2018-01-06.
 */
public class Chat {
    private Map<String, User> users = Collections.synchronizedMap(new HashMap<>());

    public Collection<User> getUsers() {
        return users.values();
    }

    public Set<String> getUserNames() {
        return users.keySet();
    }

    public boolean containsUser(User user) {
        return users.containsKey(user.getName());
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

    public boolean isEmpty() {
        return users.isEmpty();
    }
}
