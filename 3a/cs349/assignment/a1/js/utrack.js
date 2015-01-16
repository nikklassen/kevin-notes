'use strict';

/*
Put any interaction code here
 */

window.addEventListener('load', function() {
    // You should wire up all of your event handling code here, as well as any
    // code that initiates calls to manipulate the DOM (as opposed to responding
    // to events)
    console.log("Hello world!");

    // Canvas Demo Code. Can be removed, later
    var canvasButton = document.getElementById('run_canvas_demo_button');
    canvasButton.addEventListener('click', function() {
        runCanvasDemo();
    });
});

/**
 * This function can live outside the window load event handler, because it is
 * only called in response to a button click event
 */
function runCanvasDemo() {
    /*
    Useful references:
     http://www.w3schools.com/html/html5_canvas.asp
     http://www.w3schools.com/tags/ref_canvas.asp
     */
    var canvas = document.getElementById('canvas_demo');
    var context = canvas.getContext('2d');

    var width = canvas.width;
    var height = canvas.height;

    console.log("Painting on canvas at: " + new Date());
    console.log("Canvas size: " + width + "X" + height);

    context.fillStyle = 'grey';
    context.fillRect(0, 0, width, height);

    context.strokeStyle = 'red';
    context.moveTo(0, 0);
    context.lineTo(width, height);
    context.stroke();
}
