/**
 * Created by Robin on 2018-01-03.
 */
var ws = new WebSocket("ws://localhost:8080/ID1212_Project_war_exploded/actions");

var chatID;
var name;
var displayingActiveUsers = false;

ws.onmessage = function (event) {
    var serverMessage = JSON.parse(event.data);
    var action = serverMessage.action;

    if (action === "joined") {
        chatID = serverMessage.chatID;
        name = serverMessage.name;
        hideStartJoinButtons();
        displayChat();
        getActiveUsers();
    }
    else if (action === "failed") {
        var cause = serverMessage.cause;
        if (cause === "chatID") {
            displayErrorMessage("Chat " + serverMessage.chatID + " does not exist");
        }
        else if (cause === "name") {
            displayErrorMessage("Name: " + serverMessage.name + " is already taken in chat " + serverMessage.chatID);
        }
        else if (cause === "service") {
            displayErrorMessage("Service is unavailable. Please try again later.")
        }
    }
    else if (action === "broadcast") {
        var chatWindow = document.getElementById("chatWindow");
        var message = document.createElement("p");
        message.innerHTML = serverMessage.name + ": " + serverMessage.message;
        chatWindow.appendChild(message);
        chatWindow.scrollTop = chatWindow.scrollHeight;
    }
    else if (action === "users") {
        var activeUsersView = document.getElementById("activeUsers");
        var activeUsers = serverMessage.users;
        activeUsersView.innerHTML = "";
        document.getElementById("numActiveUsers").innerHTML = activeUsers.length + " ";
        activeUsers.forEach(function (username) {
            var user = document.createElement("p");
            user.classList.add("activeUser");
            user.innerHTML = '<i class="material-icons md-light userIcon">&#xE061;</i>' + username;
            activeUsersView.appendChild(user);
        });
    }
};

ws.onclose = function () {
    displayErrorMessage("Connection lost. Refresh to reconnect.");
};

function startChat() {
    var form = document.getElementById("startChatForm");
    var name = form.elements["startName"].value;

    var ChatAction = {
        action: "start",
        name: name
    };

    hideStartChatForm();
    form.reset();

    ws.send(JSON.stringify(ChatAction));
}


function getActiveUsers() {
    var ChatAction = {
        action: "users",
        chatID: chatID,
        name: name
    };

    ws.send(JSON.stringify(ChatAction));
}

function joinChat() {
    var form = document.getElementById("joinChatForm");
    var chatID = form.elements["chatID"].value;
    var name = form.elements["joinName"].value;

    var ChatAction = {
        action: "join",
        chatID: chatID,
        name: name
    };

    hideJoinChatForm();
    form.reset();

    ws.send(JSON.stringify(ChatAction));
}

function sendMessage() {
    var form = document.getElementById("chatForm");
    var message = form.elements["message"].value;

    var ChatAction = {
        action: "message",
        chatID: chatID,
        name: name,
        message: message
    };

    form.reset();
    document.getElementById("messageChatForm").focus();

    ws.send(JSON.stringify(ChatAction));
}

function returnToStart() {
    hideErrorMessage();
    hideChat();
    displayStartJoinButtons();

    var ChatAction = {
        action: "leave"
    };

    ws.send(JSON.stringify(ChatAction));
}

function displayStartChatForm() {
    hideJoinChatForm();
    hideErrorMessage();
    document.getElementById("startChatButton").classList.add("buttonActive");
    document.getElementById("startChatForm").style.display = "block";
    document.getElementById("startName").focus();
}

function hideStartChatForm() {
    document.getElementById("startChatButton").classList.remove("buttonActive");
    document.getElementById("startChatForm").style.display = "none";
}

function displayJoinChatForm() {
    hideStartChatForm();
    hideErrorMessage();
    document.getElementById("joinChatButton").classList.add("buttonActive");
    document.getElementById("joinChatForm").style.display = "block";
    document.getElementById("chatIDJoinForm").focus();
}

function hideJoinChatForm() {
    document.getElementById("joinChatButton").classList.remove("buttonActive");
    document.getElementById("joinChatForm").style.display = "none";
}

function displayStartJoinButtons() {
    document.getElementById("startJoinButtons").style.display = "block";
}

function hideStartJoinButtons() {
    document.getElementById("startJoinButtons").style.display = "none";
}

function displayErrorMessage(message) {
    var element = document.getElementById("errorMessage");

    element.innerHTML = message;
    element.style.display = "block";
}

function hideErrorMessage() {
    document.getElementById("errorMessage").style.display = "none";
}

function displayChat() {
    document.getElementById("chat").style.display = "block";
    document.getElementById("name").innerHTML = "Name: " + name;
    document.getElementById("chatID").innerHTML = "ChatID: " + chatID;
    document.getElementById("messageChatForm").focus();
    displayReturnButton();
}

function hideChat() {
    document.getElementById("chat").style.display = "none";
}

function displayReturnButton() {
    document.getElementById("returnIcon").style.display = "block";
}

function toggleActiveUsers() {
    if (displayingActiveUsers) {
        hideActiveUsers();
        displayingActiveUsers = false;
    }
    else {
        displayActiveUsers();
        displayingActiveUsers = true;
    }
}

function displayActiveUsers() {
    document.getElementById("activeUsersView").style.width = "250px";
    document.getElementById("container").style.marginLeft = "-250px";
}
function hideActiveUsers() {
    document.getElementById("activeUsersView").style.width = "0px";
    document.getElementById("container").style.marginLeft = "0px";
}