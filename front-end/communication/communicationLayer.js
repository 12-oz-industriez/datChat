/**
 * Created by Anaind on 10.08.2015.
 */
window.onload = function(){
    var socket = new WebSocket($.datChat.CONSTANTS.WEB_SOCKET_SERVER_URL);
    console.log(socket);
    socket.onopen = function(){
        console.log("Opened connection");
        socket.send("Nibiru strong!");
    };
    socket.onclose = function(){
        console.log("Closed connection");
    };

};