/**
 * Created by Anaind on 10.08.2015.
 */
$.datChat.socket  = new WebSocket($.datChat.CONSTANTS.WEB_SOCKET_SERVER_URL);
console.log($.datChat.socket);
$.datChat.socket.onopen = function(){
    console.log("Opened connection");
    $.datChat.socket.send($.datChat.CONSTANTS.GREATING_TO_SERVER);
};
$.datChat.socket.onclose = function(event){
    console.log("Closed connection");
};
$.datChat.socket.onerror = function(error){
    console.log("We got the error ", error);
};
$.datChat.socket.onmessage = function(event){
    console.log("You got a maessage from server ", event)
};

//Send message
$.datChat.sendMessage = function(messageType, messageBody) {
    $.datChat.socket.send(messageType, messageBody);
};