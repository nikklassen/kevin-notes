'use strict';

var activityStoreModel = new ActivityStoreModel();
var graphModel = new GraphModel();

window.addEventListener('load', function() {
  var form = document.forms['activity_form'];

  var submit = document.getElementById('submit');
  submit.addEventListener('click', function() {
    var dict = {
      energy: parseInt(form.elements['energy'].value),
      stress: parseInt(form.elements['stress'].value),
      happiness: parseInt(form.elements['happiness'].value)
    };

    var data = new ActivityData(form.activity.value, dict, parseInt(form.time_spent.value));
    activityStoreModel.addActivityDataPoint(data);
  });

  var input = document.getElementById('input');
  input.addEventListener('keypress', function(e) {
    if(e && e.keyCode == 13) {
      submit.click();
    }
  });
});
