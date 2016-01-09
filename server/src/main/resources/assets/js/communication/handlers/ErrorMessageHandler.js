var ErrorMessageHandler = {
    supports: function (messageType) {
        return 'ERROR' == messageType;
    },

    handle: function (message) {
        UIUtils.showError();

        console.log(message.payload);
    }
};
