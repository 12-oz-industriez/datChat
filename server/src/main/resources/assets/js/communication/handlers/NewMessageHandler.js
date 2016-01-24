var NewMessageHandler = {
    supports: function (messageType) {
        return 'NEW_MESSAGES' == messageType;
    },

    handle: function (message) {
        message.payload
            .reverse()
            .forEach(function (message) {
                UIUtils.showMessage(message);
            })
    }
};
