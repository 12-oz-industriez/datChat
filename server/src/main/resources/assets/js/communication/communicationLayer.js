datChat.communication = (function () {
    var socket = new WebSocket(datChat.CONSTANTS.WEB_SOCKET_SERVER_URL);

    var messageHandlers = [NewMessageHandler, ErrorMessageHandler, AuthHandler];

    socket.onmessage = function (event) {
        var message = JSON.parse(event.data);

        messageDispatch(message);
    };

    var messageDispatch = function (message) {
        messageHandlers.filter(function (handler) {
            return handler.supports(message.type);
        }).forEach(function (handler) {
            handler.handle(message);
        })
    };

    socket.onopen = function () {
        console.log("Opened connection");
    };

    socket.onclose = function () {
        console.log("Closed connection");
    };

    socket.onerror = function (error) {
        console.log("We got the error ", error);
    };

    var sendMessage = function (type, sessionId, payload) {
        var message = {
            id: new Date().getTime(),
            type: type,
            sessionId: sessionId,
            payload: payload
        };

        var data = JSON.stringify(message);

        console.log("Sending: ", data);
        socket.send(data);
    };

    return {
        lastMessageId: null,
        sessionId: null,

        sendChatMessage: function (messageBody) {
            sendMessage("NEW_MESSAGE", this.sessionId, {body: messageBody});
        },

        getLatest: function () {
            sendMessage("GET_LATEST", this.sessionId, {
                lastMessageId: this.lastMessageId,
                count: datChat.CONSTANTS.PAGING_COUNT
            });
        },

        register: function (username, password) {
            sendMessage('REGISTER', null, {
                username: username,
                password: password
            });
        },

        login: function (username, password) {
            sendMessage('AUTH', null, {
                username: username,
                password: password
            });
        }
    }
})();