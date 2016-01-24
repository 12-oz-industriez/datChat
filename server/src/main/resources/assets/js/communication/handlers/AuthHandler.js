var AuthHandler = {
    supports: function (messageType) {
        return messageType == 'AUTH';
    },

    handle: function (message) {
        datChat.communication.sessionId = message.payload.sessionId;
    }
};