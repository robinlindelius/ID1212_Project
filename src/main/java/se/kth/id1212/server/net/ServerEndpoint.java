package se.kth.id1212.server.net;

import se.kth.id1212.server.model.UserMessage;
import se.kth.id1212.server.model.User;

import javax.inject.Inject;
import javax.json.*;
import javax.websocket.*;
import java.io.StringReader;

/**
 * Created by Robin on 2018-01-03.
 */
@javax.websocket.server.ServerEndpoint("/actions")
public class ServerEndpoint {
    @Inject
    SessionHandler sessionHandler;

    private ServerMessageBuilder serverMessageBuilder = new ServerMessageBuilder();
    private User user;

    @OnOpen
    public void open(Session session) {
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeUser(user);
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
                user = new User(jsonMessage.getString("name"),session);
                sessionHandler.startChat(user);
            }
            else if ("join".equals(jsonMessage.getString("action"))) {
                user = new User(jsonMessage.getString("name"),session);
                sessionHandler.joinChat(Integer.parseInt(jsonMessage.getString("chatID")),user);
            }
            else if (("message").equals(jsonMessage.getString("action"))) {
                UserMessage userMessage = new UserMessage(user,jsonMessage.getString("message"));
                sessionHandler.broadcast(userMessage);

            }
            else if (("users").equals(jsonMessage.getString("action"))) {
                sessionHandler.getActiveUsers(jsonMessage.getInt("chatID"));
            }
    }
}
