$(function () {
    $("#sendMessage").on("click", function () {
        var messageBody = $("#message").val();

        datChat.sendChatMessage(messageBody);
    });
});

