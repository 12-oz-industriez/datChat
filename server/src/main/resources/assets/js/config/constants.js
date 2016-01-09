datChat.CONSTANTS = (function () {
    var currentHostname = window.location.host;
    var currentProtocol = window.location.protocol;

    var wsProtocol = 'ws';
    if (currentProtocol == 'https') {
        wsProtocol = 'wss';
    }

    var wsUrl = wsProtocol + '://' + currentHostname + '/chat';

    return {
        WEB_SOCKET_SERVER_URL: wsUrl,

        PAGING_COUNT: 20
    };
})();
