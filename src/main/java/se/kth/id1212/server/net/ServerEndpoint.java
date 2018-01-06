package se.kth.id1212.server.net;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.*;
import javax.json.spi.JsonProvider;
import javax.websocket.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robin on 2018-01-03.
 */
@ApplicationScoped
@javax.websocket.server.ServerEndpoint("/actions")
public class ServerEndpoint {
    @Inject
    SessionHandler sessionHandler;

    private final int MAX_NUM_CHATS = 10;

    private static HashMap<Integer, HashMap<String, Session>> chats = new HashMap<>();

    @OnOpen
    public void open(Session session) {
    }

    @OnClose
    public void close(Session session) {

    }

    @OnError
    public void onError(Throwable error) {
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        System.out.println("Received message: " + message);

            JsonReader reader = Json.createReader(new StringReader(message));
            JsonObject jsonMessage = reader.readObject();

            if ("start".equals(jsonMessage.getString("action"))) {
                startChat(jsonMessage.getString("name"), session);
            }
            else if ("join".equals(jsonMessage.getString("action"))) {
                joinChat(Integer.parseInt(jsonMessage.getString("chatID")),jsonMessage.getString("name"),session);
            }
            else if (("message").equals(jsonMessage.getString("action"))) {
                broadcastToChat(jsonMessage.getInt("chatID"), generateBroadcastMessage(jsonMessage.getInt("chatID"),
                        jsonMessage.getString("name"), jsonMessage.getString("message")));
            }
            else if (("users").equals(jsonMessage.getString("action"))) {
                getActiveUsers(jsonMessage.getInt("chatID"), jsonMessage.getString("name"), session);
            }
    }

    private void startChat(String name, Session session) {
        boolean started = false;
        while (!started) {
            int chatID = generateChatID();
            if (!chats.containsKey(chatID)) {
                HashMap<String, Session> newChat = new HashMap<>();
                newChat.put(name, session);
                chats.put(chatID,newChat);
                System.out.println("Chat contains key " + chatID + ": " + chats.containsKey(chatID));
                started = true;
                sendToSession(session, generateJoinedMessage(chatID,name));
            }
        }
    }

    private void joinChat(int chatID, String name, Session session) {
        System.out.println("Received join command");

        if (chats.containsKey(chatID)) {
            System.out.println("Contains id " + chatID);
            HashMap<String, Session> chat = chats.get(chatID);
            if (!chat.containsKey(name)) {
                System.out.println("Does not contain name " + name);
                chat.put(name,session);
                sendToSession(session,generateJoinedMessage(chatID,name));
            }
            else {
                System.out.println("Contains name " + name);
                boolean invalidChatID = false;
                boolean invalidName = true;
                sendToSession(session, generateFailedToJoinMessage(chatID, name, invalidChatID, invalidName));
            }
        }

        else {
            System.out.println("Does not contain id " + chatID);
            boolean invalidChatID = true;
            boolean invalidName = false;
            sendToSession(session, generateFailedToJoinMessage(chatID, name, invalidChatID, invalidName));
        }
    }

    private void getActiveUsers(int chatID, String name, Session session) {
        if (chats.containsKey(chatID)) {
            ArrayList<String> activeUsers = new ArrayList<>();
            HashMap<String, Session> currentChat = chats.get(chatID);
            activeUsers.addAll(currentChat.keySet());
            broadcastToChat(chatID, generateActiveUsersMessage(chatID, activeUsers));
        }
    }

    private JsonObject generateJoinedMessage(int chatID, String name) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "joined")
                .add("chatID", chatID)
                .add("name", name)
                .build();
    }

    private JsonObject generateFailedToJoinMessage(int chatID, String name, boolean invalidChatID, boolean invalidName) {
        JsonProvider provider = JsonProvider.provider();
        if (invalidChatID) {
            return provider.createObjectBuilder()
                    .add("action", "failed")
                    .add("cause", "chatID")
                    .add("chatID", chatID)
                    .build();
        }
        else if (invalidName) {
            return provider.createObjectBuilder()
                    .add("action", "failed")
                    .add("cause", "name")
                    .add("chatID", chatID)
                    .add("name", name)
                    .build();
        }
        return null;
    }

    private JsonObject generateBroadcastMessage(int chatID, String name, String message) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "broadcast")
                .add("chatID", chatID)
                .add("name", name)
                .add("message", message)
                .build();
    }


    private JsonObject generateActiveUsersMessage(int chatID, ArrayList<String> activeUsers) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "users")
                .add("chatID", chatID)
                .add("users", generateJsonArray(activeUsers))
                .build();
    }

    private JsonArrayBuilder generateJsonArray(ArrayList<String> strings) {
        JsonProvider provider = JsonProvider.provider();
        JsonArrayBuilder builder = provider.createArrayBuilder();
        strings.forEach(builder::add);
        return builder;
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
            System.out.println("Sent message: " + message.toString());
        }
        catch (IOException ioe) {
            System.out.println("Failed to send message: " + message.toString());
        }
    }

    private int generateChatID() {
        return (int) (Math.random()*MAX_NUM_CHATS);
    }

    private void broadcastToChat(int chatID, JsonObject broadcastMessage) {
        HashMap<String, Session> chat = chats.get(chatID);

        for (Session session : chat.values()) {
            sendToSession(session, broadcastMessage);
        }
    }
}
