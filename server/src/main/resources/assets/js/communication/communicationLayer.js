datChat.communication = (function () {
    var socket = new WebSocket(datChat.CONSTANTS.WEB_SOCKET_SERVER_URL);

    var messageHandlers = [NewMessageHandler, ErrorMessageHandler];

    var lastMessageId = null;

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
        },

        getLatest: function () {
            var message = {
                id: new Date().getTime(),
                type: 'GET_LATEST',
                payload: {
                    lastMessageId: lastMessageId,
                    count: datChat.CONSTANTS.PAGING_COUNT
                }
            };

            socket.send(JSON.stringify(message));
        }
    }
})();