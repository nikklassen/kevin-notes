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

    // TODO: graph

    // time
    var time_span = document.getElementById('last_entry');
    var date = new Date(eventTime);
    var year = date.getFullYear();
    var months = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];
    var month = months[date.getMonth()];
    var day = date.getDay();
    var hour = date.getHours();
    var minute = date.getMinutes();
    time_span.innerHTML = month + ' ' + day + ', ' + year + ' ' + hour + ':' + minute
  }
  // TODO: remove
}

var graphSelectionListener = function(eventType, eventTime, eventData) {
  if (eventData == 'table') {
    document.getElementById('graph_div').style.display = 'none';
    document.getElementById('table_div').style.display = '';
  } else {
    document.getElementById('graph_div').style.display = '';
    document.getElementById('table_div').style.display = 'none';
  }
}

function initGraph() {
  // TODO: init empty
  var canvas = document.getElementById('graph');
  var context = canvas.getContext('2d');

  var width = canvas.width;
  var height = canvas.height;

  context.strokeStyle = 'black';
  context.moveTo(0, 0);
  context.lineTo(0, height);
  context.stroke();

  context.strokeStyle = 'black';
  context.moveTo(0, height);
  context.lineTo(width, height);
  context.stroke();
}

window.addEventListener('load', function() {
  analysisListener();
  navListener();

  activityStoreModel.addListener(activityListener);

  graphModel.addListener(graphSelectionListener);
  graphModel.selectGraph('table');

  initGraph();
});
