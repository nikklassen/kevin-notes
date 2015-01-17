'use strict';

function analysisListener() {
  var selector = '#analysis_div .radio input';

  var elems = document.querySelectorAll(selector);

  var switchAnalysisType = function() {
    graphModel.selectGraph(this.id);
  }

  for (var i = 0; i < elems.length; i++) {
    elems[i].addEventListener('click', switchAnalysisType);
  }
}

function navListener() {
  var selector = '.navbar-left li';

  var elems = document.querySelectorAll(selector);

  var makeActive = function() {
    for (var i = 0; i < elems.length; i++) {
      elems[i].classList.remove('active');

      document.getElementById(elems[i].id + '_div').style.display = 'none';
    }

    this.classList.add('active');

    document.getElementById(this.id + '_div').style.display = '';
  };

  for (var i = 0; i < elems.length; i++) {
    elems[i].addEventListener('click', makeActive);
  }
};

var activityListener = function(eventType, eventTime, eventData) {
  if (eventType == ACTIVITY_DATA_ADDED_EVENT) {
    // table
    var table = document.getElementById('table_ref').getElementsByTagName('tbody')[0];

    var newRow = table.insertRow(table.rows.length);

    var newCell0 = newRow.insertCell(0);
    var newText0 = document.createTextNode(eventData.activityType);
    newCell0.appendChild(newText0);

    var newCell1 = newRow.insertCell(1);
    var newText1 = document.createTextNode(eventData.activityDurationInMinutes);
    newCell1.appendChild(newText1);


    var newCell2 = newRow.insertCell(2);
    var button = document.createElement('input');
    button.type = 'button';
    button.value = 'Remove Data Point';
    button.onclick = function() {
      activityStoreModel.removeActivityDataPoint(eventData);
      table.deleteRow(table.rows.length - 1);
    }
    newCell2.appendChild(button);

    // time
    var time_span = document.getElementById('last_entry');
    var date = new Date(eventTime);
    var year = date.getFullYear();
    var months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    var month = months[date.getMonth()];
    var day = date.getDay();
    var hour = date.getHours();
    var minute = date.getMinutes();
    time_span.innerHTML = month + ' ' + day + ', ' + year + ' ' + hour + ':' + minute
  }

  drawGraph();
}

var graphSelectionListener = function(eventType, eventTime, eventData) {
  if (eventData == 'table') {
    document.getElementById('graph_div').style.display = 'none';
    document.getElementById('key_div').style.display = 'none';
    document.getElementById('table_div').style.display = '';
  } else {
    document.getElementById('graph_div').style.display = '';
    document.getElementById('key_div').style.display = '';
    document.getElementById('table_div').style.display = 'none';
  }
}

function drawGraph() {
  var canvas = document.getElementById('graph');
  var context = canvas.getContext('2d');

  var width = canvas.width;
  var height = canvas.height;

  context.clearRect(0, 0, width, height);

  // x axis
  context.strokeStyle = 'black';
  context.moveTo(20, height - 20);
  context.lineTo(width, height - 20);
  context.stroke();

  context.fillText('Coding', width * 2 / 12, height - 5);
  context.fillText('Eating', width * 5 / 12, height - 5);
  context.fillText('Sleeping', width * 8 / 12, height - 5);

  // y axis
  context.strokeStyle = 'black';
  context.moveTo(20, 0);
  context.lineTo(20, height - 20);
  context.stroke();

  for (var i = 1; i <= 5; i++) {
    context.fillText(i, 0, height * (6-i) / 6);
  }

  // data
  var points = activityStoreModel.getActivityDataPoints();

  var data = {
    Coding: [],
    Eating: [],
    Sleeping: []
  };
  _.each(points, function(obj) {
    data[obj.activityType].push(obj.activityDataDict);
  });

  for (var i = 2; i < 11; i += 3) {
    var activity = null;
    if (i == 2) {
      activity = 'Coding';
    } else if (i == 5) {
      activity = 'Eating';
    } else {
      activity = 'Sleeping';
    }

    var calc_energy = 0;
    var calc_stress = 0;
    var calc_happiness = 0;
    data[activity].forEach(function(obj) {
      calc_energy += obj['energy'];
      calc_stress += obj['stress'];
      calc_happiness += obj['happiness'];
    });
    calc_energy /= data[activity].length;
    calc_stress /= data[activity].length;
    calc_happiness /= data[activity].length;

    // energy
    context.beginPath();
    context.arc(18 + width * i / 12, -3 + height * (6-calc_energy) / 6, 3, 0, 2 * Math.PI, false);
    context.stroke();

    // stress
    context.beginPath();
    context.moveTo(15 + width * i / 12, -3 + height * (6-calc_stress) / 6);
    context.lineTo(20 + width * i / 12, 2 + height * (6-calc_stress) / 6);
    context.lineTo(20 + width * i / 12, -8 + height * (6-calc_stress) / 6);
    context.lineTo(15 + width * i / 12, -3 + height * (6-calc_stress) / 6);
    context.stroke();

    // happiness
    context.strokeRect(15 + width * i / 12, -6 + height * (6-calc_happiness) / 6, 6, 6);
  }
}

function drawKey() {
  var canvas = document.getElementById('key');
  var context = canvas.getContext('2d');

  var width = canvas.width;
  var height = canvas.height;

  // energy
  context.beginPath();
  context.arc(5, 5, 3, 0, 2 * Math.PI, false);
  context.stroke();
  context.fillText('Energy', 15, 10);

  // stress
  context.beginPath();
  context.moveTo(2.5, 20);
  context.lineTo(7.5, 25);
  context.lineTo(7.5, 15);
  context.lineTo(2.5, 20);
  context.stroke();
  context.fillText('Stress', 15, 25);

  // happiness
  context.strokeRect(2, 35, 6, 6);
  context.fillText('Happiness', 15, 40);
}

window.addEventListener('load', function() {
  analysisListener();
  navListener();

  activityStoreModel.addListener(activityListener);

  graphModel.addListener(graphSelectionListener);
  graphModel.selectGraph('table');

  drawGraph();
  drawKey();
});
