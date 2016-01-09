$(function () {
    $("#sendMessage").on("click", function () {
        var messageBody = $("#message").val();

        datChat.communication.sendChatMessage(messageBody);
    });

    console.log(datChat.CONSTANTS.WEB_SOCKET_SERVER_URL);
    console.log(datChat.CONSTANTS.PAGING_COUNT);

    // FIXME: awful hack. should consider some form of backpressure
    setTimeout(datChat.communication.getLatest, 1000);
});

