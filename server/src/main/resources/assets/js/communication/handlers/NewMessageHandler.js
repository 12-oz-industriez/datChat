var NewMessageHandler = {
    supports: function (messageType) {
        return 'NEW_MESSAGE' == messageType
            || 'NEW_MESSAGES' == messageType;
    },

    handle: function (message) {
        var payload = message.payload;

        if (message.type == 'NEW_MESSAGE') {
            UIUtils.showMessage(payload);
        } else {
            payload
                .reverse()
                .forEach(function (message) {
                    UIUtils.showMessage(message);
                })
        }
    }
};
