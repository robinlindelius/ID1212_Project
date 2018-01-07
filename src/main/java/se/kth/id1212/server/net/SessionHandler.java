package se.kth.id1212.server.net;

import se.kth.id1212.server.model.Chat;
import se.kth.id1212.server.model.UserMessage;
import se.kth.id1212.server.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robin on 2018-01-03.
 */
@ApplicationScoped
public class SessionHandler {
    private final int MAX_NUM_CHATS = 1;
    private final Map<Integer, Chat> chats = Collections.synchronizedMap(new HashMap<>());
    private final ServerMessageBuilder serverMessageBuilder = new ServerMessageBuilder();

    void startChat(User user) {
        for(int i = 0; i < MAX_NUM_CHATS; i++) {
            int chatID = generateChatID();
            if (!chats.containsKey(chatID)) {
                user.setChatID(chatID);
                Chat newChat = new Chat();
                newChat.addUser(user);
                chats.put(chatID,newChat);
                sendToSession(user.getSession(), serverMessageBuilder.joinedMessage(chatID,user.getName()));
                break;
            }
            else if (i == MAX_NUM_CHATS - 1) {
                sendToSession(user.getSession(), serverMessageBuilder.serviceUnavailableMessage());
            }
        }
    }

    void joinChat(int chatID, User user) {
        if (chats.containsKey(chatID)) {
            Chat chat = chats.get(chatID);
            if (!chat.containsUser(user)) {
                user.setChatID(chatID);
                chat.addUser(user);
                sendToSession(user.getSession(), serverMessageBuilder.joinedMessage(chatID,user.getName()));
            }
            else {
                boolean invalidChatID = false;
                boolean invalidName = true;
                sendToSession(user.getSession(),
                        serverMessageBuilder.failedToJoinMessage(chatID, user.getName(), invalidChatID, invalidName));
            }
        }
        else {
            boolean invalidChatID = true;
            boolean invalidName = false;
            sendToSession(user.getSession(),
                    serverMessageBuilder.failedToJoinMessage(chatID, user.getName(), invalidChatID, invalidName));
        }
    }

    void getActiveUsers(int chatID) {
        if (chats.containsKey(chatID)) {
            broadcastToChat(chatID, serverMessageBuilder.activeUsersMessage(chatID, chats.get(chatID).getUserNames()));
        }
    }

    void removeUser(User user) {
        int chatID = user.getChatID();
        if (chats.containsKey(chatID)) {
            Chat chat = chats.get(chatID);
            chat.removeUser(user);
            if (chat.isEmpty()) chats.remove(chatID);
            else getActiveUsers(chatID);
        }
    }

    void broadcast(UserMessage userMessage) {
        broadcastToChat(userMessage.getUser().getChatID(), serverMessageBuilder.broadcastMessage(userMessage));
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        }
        catch (IOException ioe) {
            System.out.println("Failed to send message: " + message.toString());
        }
    }

    private int generateChatID() {
        return (int) (Math.random()*MAX_NUM_CHATS);
    }

    private void broadcastToChat(int chatID, JsonObject broadcastMessage) {
        Chat chat = chats.get(chatID);
        for (User user : chat.getUsers()) {
            sendToSession(user.getSession(), broadcastMessage);
        }
    }
}
