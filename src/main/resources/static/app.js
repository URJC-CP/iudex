//// Configuration

// Max number of charts rendered. Oldest charts get removed when limit is reached.
const max_charts = 10;

// Redraw cooldown in milliseconds.
// Example: If 100 changes are done to a chart in a second,
// and cooldown is 500, charts are only redrawn twice instead of 100 times.
const redraw_cooldown = 2000;

// Event batch download size
const event_batch_size = 1000;

//// End configuration

//// Start Chart data
let convergence_chart_series;
let convergence_chart;
let minimum_algorithm;

let current_chart_series;
let current_chart;

let current_solution_chart;
let current_solution_chart_data;
let bestValue = NaN;

let last_redraw = new Date() - 1000;

let progress_chart;
let nInstances;
let currentInstances;
let nAlgorithms;
let currentAlgorithms;
let nRepetitions;
let currentRepetitions;
//// End chart data

// Event handlers, see Github wiki page for more information about the event system.
// Each handler is called when an event from the corresponding type is received. Usual event order is:

// ExecutionStartedEvent --> ExperimentStartedEvent --> InstanceProcessingStarted --> SolutionGeneratedEvent(1 to N) --> InstanceProcessingEnded --> ExperimentEndedEvent --> ExecutionEndedEvent
//                                      A                            A                                                                 |                       |
//                                      |                             \________________________________________________________________/                       |
//                                      |                                                                                                                      |
//                                      \______________________________________________________________________________________________________________________/

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