var UIUtils = {
    showMessage: function (payload) {
        var outputElement = $("#subscribe");
        var content = outputElement.html();
        var newContent = "<p>" + payload.author + ':' + payload.body + "</p></br>";

        outputElement.html(content + newContent);
    },

    showError: function () {
        var outputElement = $("#subscribe");
        outputElement.html(outputElement.html() + "<p>unable to deliver message</p>")
    }
};