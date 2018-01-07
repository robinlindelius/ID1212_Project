package se.kth.id1212.server.net;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import java.util.Set;

/**
 * Created by Robin on 2018-01-06.
 */
class ServerMessageBuilder {

    JsonObject joinedMessage(int chatID, String name) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "joined")
                .add("chatID", chatID)
                .add("name", name)
                .build();
    }

    JsonObject failedToJoinMessage(int chatID, String name, boolean invalidChatID, boolean invalidName) {
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

    JsonObject serviceUnavailableMessage() {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "failed")
                .add("cause", "service")
                .build();
    }

    JsonObject broadcastMessage(UserMessageDTO userMessage) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "broadcast")
                .add("chatID", userMessage.getUser().getChatID())
                .add("name", userMessage.getUser().getName())
                .add("message", userMessage.getMessage())
                .build();
    }


    JsonObject activeUsersMessage(int chatID, Set<String> activeUsers) {
        JsonProvider provider = JsonProvider.provider();
        return provider.createObjectBuilder()
                .add("action", "users")
                .add("chatID", chatID)
                .add("users", generateJsonArray(activeUsers))
                .build();
    }

    private JsonArrayBuilder generateJsonArray(Set<String> strings) {
        JsonProvider provider = JsonProvider.provider();
        JsonArrayBuilder builder = provider.createArrayBuilder();
        strings.forEach(builder::add);
        return builder;
    }
}
