datChat = (function () {
    var receivedMessage = [];
    var sentMessages = {};
    var socket  = new WebSocket(datChat.CONSTANTS.WEB_SOCKET_SERVER_URL);

    socket.onopen = function(){
        console.log("Opened connection");
    };

    socket.onclose = function(){
        console.log("Closed connection");
    };

    socket.onerror = function(error){
        console.log("We got the error ", error);
    };

    socket.onmessage = function(event){
        var message = JSON.parse(event.data);

        var payload = message.payload;

        if (sentMessages[message.id] && payload.status == "OK") {
            showMessage(sentMessages[message.id]);
            delete sentMessages[message.id];
        } else if (payload.status == "ERROR") {
            showError();
        } else {
            showMessage(payload);
        }
    };

    var showError = function () {
        var outputElement = $("#subscribe");
        outputElement.html(outputElement.html() + "<p>unable to deliver message</p>")
    };

    var showMessage = function (message) {
        receivedMessage.push(message);

        var outputElement = $("#subscribe");
        var content = outputElement.html();
        var newContent = "<p>" + message.body + "</p></br>";

        outputElement.html(content + newContent);
    };

    return {
        sendChatMessage: function (messageBody) {
            var message = {
                id: new Date().getTime(),
                type: "NEW_MESSAGE",
                payload: {
                    body: messageBody
                }
            };

            socket.send(JSON.stringify(message));

            sentMessages[message.id] = message;
        }
    }
})();

console.log(datChat);