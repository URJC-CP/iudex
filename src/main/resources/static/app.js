function onMessage(event) {
    console.log(event);
}

function  connectAndSubscribe(callback) {
    const brokerURL = "ws://" + window.location.host + "/websocket";
    stompClient = new StompJs.Client({brokerURL: brokerURL});
    stompClient.reconnectDelay = 1000;
   
    stompClient.onConnect = function () {
        stompClient.onMessage(Event);
        const subscription = stompClient.subscribe('/topic/events', function (event) {
            const payload = JSON.parse(event.body);
            callback(payload);
        });
    }
}

$(() => {
    console.log("Connecting...");
    $('#running-status').text('CONNECTING');
    connectAndSubscribe(interceptor);
})