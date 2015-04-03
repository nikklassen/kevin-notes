'use strict';
var PROTOCOL_VERSION = '1.3';
var SERVER = '';

var PRIVATE = 'PRIVATE';
var PUBLIC = 'PUBLIC';
var CURRENT = '';

var POLLING_TIME = 30 * 1000;

var sups = [];
var supPtr = 0;
var supCount = 0;

var messageCount = 0;
var loadingCount = 0;
var loadingError = false;

var userid = '';
var fullname = '';

var selectors = document.getElementsByClassName('friend_list_selector');


window.addEventListener('load', function() {
    var loginBtn = document.getElementById('login_button');
    if (loginBtn) {
        // login page
        loginBtn.addEventListener('click', function() {
            document.cookie = 'full_name=' + document.getElementById('full_name').value;
        });
    } else {
        var cookie = '; ' + document.cookie;
        userid = cookie.split('; user_id=').pop().split(';').shift();
        fullname = cookie.split('; full_name=').pop().split(';').shift();

        // app page
        createCanvas(400, 400);

        switchServer(PRIVATE);

        var addFriendButton = document.getElementById('add_friend_button');
        addFriendButton.addEventListener('click', function() {
            var friendID = document.getElementById('add_friend_id').value;
            addFriend(friendID);
        });

        var removeFriendButton = document.getElementById('remove_friend_button');
        removeFriendButton.addEventListener('click', function() {
            var friendID = document.getElementById('add_friend_id').value;
            removeFriend(friendID);
        });

        var sendButton = document.getElementById('send_button');
        sendButton.addEventListener('click', function() {
            for (var i = 0; i < selectors.length; ++i) {
                for (var j = 0; j < selectors[i].length; ++j) {
                    if (selectors[i].options[j].selected) {
                        var friendID = selectors[i].options[j].value;
                        sendSup(friendID);
                    }
                }
            }
        });

        var prevBtn = document.getElementById('nav_prev');
        prevBtn.addEventListener('click', function() {
            --supPtr;
            drawSup();
        });

        var nextButton = document.getElementById('nav_next');
        nextButton.addEventListener('click', function() {
            ++supPtr;
            drawSup();
        });

        var canvas = document.getElementById('sup_canvas');
        canvas.addEventListener('mousedown', function(e) {
            var boundries = canvas.getBoundingClientRect();

            var x = e.x - boundries.left;
            var y = e.y - boundries.top;
            if (350 < x && x < 390 && 10 < y && y < 65) {
                removeSup(sups[supPtr].sup_id);
            }
        });

        var privateBtn = document.getElementById('private_button');
        privateBtn.addEventListener('click', function() {
            switchServer(PRIVATE);
        });

        var publicBtn = document.getElementById('public_button');
        publicBtn.addEventListener('click', function() {
            switchServer(PUBLIC);
        });
    }
});

// helper functions
// http://stackoverflow.com/questions/15661339/how-do-i-fix-blurry-text-in-my-html5-canvas
function createCanvas(w, h) {
    var ratio = getPixelRatio();

    var canvas = document.getElementById('sup_canvas');
    canvas.width = w * ratio;
    canvas.height = h * ratio;
    canvas.style.width = w + 'px';
    canvas.style.height = h + 'px';
    canvas.getContext('2d').setTransform(ratio, 0, 0, ratio, 0, 0);
}

function generateUUID() {
    // http://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

// http://stackoverflow.com/questions/15661339/how-do-i-fix-blurry-text-in-my-html5-canvas
function getPixelRatio() {
    var ctx = document.createElement('canvas').getContext('2d');
    var dpr = window.devicePixelRatio || 1;
    var bsr = ctx.webkitBackingStorePixelRatio ||
              ctx.mozBackingStorePixelRatio ||
              ctx.msBackingStorePixelRatio ||
              ctx.oBackingStorePixelRatio ||
              ctx.backingStorePixelRatio || 1;

    return dpr / bsr;
}

function getTimestamp(d) {
    return d.replace('T', ' ').slice(0, -8);
}

function handleAjaxRequest(request, callback) {
    var httpRequest = new XMLHttpRequest();

    httpRequest.addEventListener('readystatechange', function() {
        if (httpRequest.readyState === 4 && httpRequest.status === 200) {
            hideLoadingSpinner();

            var responseObj = JSON.parse(httpRequest.responseText);
            callback(responseObj);
        }
    });

    httpRequest.open('POST', 'http://' + SERVER + '/post');
    httpRequest.setRequestHeader('Content-Type', 'application/json');
    httpRequest.send(JSON.stringify(request));

    setTimeout(function() { showLoadingSpinner(); }, 500);
}

function pad(num) {
    if (num < 10) {
        return '0' + num;
    } else {
        return num;
    }
}

// loading spinner
function hideLoadingSpinner() {
    --loadingCount;

    if (loadingCount <= 0) {
        document.getElementById('loading_spinner').style.opacity = 0;

        hideLoadingError();
    }
}

function showLoadingSpinner() {
    ++loadingCount;

    if (loadingCount > 0) {
        document.getElementById('loading_spinner').style.opacity = 1;

        setTimeout(function() { showLoadingError(); }, 5000);
    }
}

function hideLoadingError() {
    if (loadingError) {
        var msg = document.getElementById('message_div');
        msg.style.bottom = '-' + msg.clientHeight + 'px';

        loadingeError = false;
    }
}

function showLoadingError() {
    if (loadingCount > 0) {
        document.getElementById('message').innerText = 'Server is unresponsive. Please wait.';

        var msg = document.getElementById('message_div');
        msg.classList.add('bg-warning');
        msg.classList.remove('bg-danger');
        msg.classList.remove('bg-success');
        msg.style.bottom = '0px';

        loadingError = true;
    }
}

function hideMessage() {
    --messageCount;

    if (messageCount == 0) {
        var msg = document.getElementById('message_div');
        msg.style.bottom = '-' + msg.clientHeight + 'px';
    }
}

function showError(message) {
    document.getElementById('message').innerText = 'ERROR: ' + message;

    var msg = document.getElementById('message_div');
    msg.classList.add('bg-danger');
    msg.classList.remove('bg-warning');
    msg.classList.remove('bg-success');
    msg.style.bottom = '0px';

    ++messageCount;

    setTimeout(function() { hideMessage(); }, 3000);
}

function showSuccess(message) {
    document.getElementById('message').innerText = message;

    var msg = document.getElementById('message_div');
    msg.classList.add('bg-success');
    msg.classList.remove('bg-danger');
    msg.classList.remove('bg-warning');
    msg.style.bottom = '0px';

    ++messageCount;

    setTimeout(function() { hideMessage(); }, 2000);
}

// server management
function switchServer(dest) {
    if (dest == CURRENT) {
        return;
    }
    CURRENT = dest;

    var privateBtn = document.getElementById('private_button');
    var publicBtn = document.getElementById('public_button');

    if (CURRENT === PRIVATE) {
        privateBtn.classList.add('active');
        publicBtn.classList.remove('active');

        SERVER = 'localhost:8080';
    } else if (CURRENT === PUBLIC) {
        publicBtn.classList.add('active');
        privateBtn.classList.remove('active');

        SERVER = '104.197.3.113';
    }

    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'create_user',
        'message_id': generateUUID(),
        'user_id': userid,
        'command_data': {
            'user_id': userid,
            'full_name': fullname,
        },
    };

    handleAjaxRequest(request, function(response) {
        if (response.error) {
            showError('Could create user on new server.');
        }
    });

    populateFriends();
    getSups();
    drawSup();
}

// initial population
function populateFriends() {
    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'get_friends',
        'user_id': userid,
        'message_id': generateUUID(),
        'command_data': null,
    };

    handleAjaxRequest(request, function(response) {
        if (response.error) {
            showError('Could not populate friend list.');
        } else {
            for (var i = 0; i < selectors.length; ++i) {
                for (var j = selectors[i].options.length - 1; j >= 0; --j) {
                    selectors[i].remove(j);
                }

                for (var j = 0; j < response.reply_data.length; ++j) {
                    var friend = document.createElement('option');
                    friend.text = response.reply_data[j].user_id;
                    selectors[i].add(friend);
                }
            }
        }
    });
}

// friend management
function addFriend(id) {
    if (userid == id) {
        showError('Can not add yourself as a friend.');
        return;
    }

    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'user_exists',
        'user_id': userid,
        'message_id': generateUUID(),
        'command_data': {
            'user_id': id,
        },
    };

    handleAjaxRequest(request, function(response) {
        if (!response.reply_data.exists) {
            showError('User does not exist.');
        } else {
            request = {
                'protocol_version': PROTOCOL_VERSION,
                'command': 'add_friend',
                'user_id': userid,
                'message_id': generateUUID(),
                'command_data': {
                    'user_id': id,
                },
            };

            handleAjaxRequest(request, function(response) {
                if (response.error) {
                    showError('Could not add friend.');
                } else {
                    for (var i = 0; i < selectors[0].options.length; ++i) {
                        if (selectors[0][i].value === id) {
                            showError('Attempted to add user already in friend list.');
                            return;
                        }
                    }

                    for (var i = 0; i < selectors.length; ++i) {
                        var friend = document.createElement('option');
                        friend.text = id;
                        selectors[i].add(friend);
                    }
                }
            });
        }
    });
}

function removeFriend(id) {
    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'remove_friend',
        'user_id': userid,
        'message_id': generateUUID(),
        'command_data': {
            'user_id': id,
        },
    };

    handleAjaxRequest(request, function(response) {
        if (response.error) {
            showError('Could not remove friend.');
        } else {
            var found = false;
            for (var i = 0; i < selectors[0].options.length; ++i) {
                if (selectors[0].options[i].value === id) {
                    found = true;
                    for (var j = 0; j < selectors.length; ++j) {
                        selectors[j].remove(i);
                    }
                    break;
                }
            }

            if (!found) {
                showError('Attempted to remove user not in friend list.');
            }
        }
    });
}

// sup management
function drawSup() {
    document.getElementById('nav_prev').disabled = false;
    document.getElementById('nav_next').disabled = false;
    if (supPtr == 0) {
        document.getElementById('nav_prev').disabled = true;
    }
    if (supPtr >= supCount - 1) {
        document.getElementById('nav_next').disabled = true;
    }

    var canvas = document.getElementById('sup_canvas');
    var context = canvas.getContext('2d');
    context.clearRect(0, 0, canvas.width, canvas.height);

    if (supCount > 0) {
        context.fillStyle = '#000';
        context.font = 'bold 1em sans-serif';
        context.textBaseline = 'bottom';
        var senderName = sups[supPtr].sender_id;
        context.fillText(
            '- from ' + senderName,
            canvas.width / 1.5,
            canvas.height / 1.2
        );

        context.textBaseline = 'top';
        var senderDate = getTimestamp(sups[supPtr].date);
        context.fillText(
            senderDate,
            canvas.width / 1.5,
            canvas.height / 1.2
        );

        context.save();
        // http://stackoverflow.com/questions/1484506/random-color-generator-in-javascript
        context.fillStyle = '#'+(Math.random()*0xFFFFFF<<0).toString(16);

        var font = ['normal', 'italic', 'oblique'][Math.floor(Math.random()*3)]
        font += ' '
        font += ['normal', 'bold'][Math.floor(Math.random()*2)]
        font += ' '
        font += ['6em', '6.1em', '6.2em', '6.3em', '6.4em', '6.5em', '6.6em', '6.7em', '6.8em', '6.9em', '7.0em'][Math.floor(Math.random()*11)]
        font += ' '
        font += ['sans-serif', 'serif', 'cursive', 'fantasy', 'monospace'][Math.floor(Math.random()*5)]
        context.font = font;

        var maxAngle = 25;
        context.rotate((Math.random() - 0.5) * maxAngle * Math.PI / 180);

        context.fillText(
            'SUP?',
            canvas.width / 4.2,
            canvas.height / 3.6
        );
        context.restore();

        var trash = new Image();
        trash.src = '/static/img/trash.png';
        trash.onload = function() {
            context.drawImage(
                trash,
                canvas.width - 45,
                10,
                35,
                40 * trash.height / trash.width
            );
        };
    }
}

function getSups() {
    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'get_sups',
        'user_id': userid,
        'message_id': generateUUID(),
        'command_data': null,
    };

    handleAjaxRequest(request, function(response) {
        var redraw = false;

        var newSupCount = response.reply_data.length;
        if (supCount == 0 && newSupCount > supCount) {
            redraw = true;
        }
        supCount = newSupCount;
        var counts = document.getElementsByClassName('sup_count');
        for (var i = 0; i < counts.length; ++i) {
            counts[i].innerText = supCount;
        }

        sups = [];
        for (var i = supCount - 1; i >= 0; --i) {
            sups.push(response.reply_data[i]);
        }

        if (redraw) {
            drawSup();
        }
    });

    setTimeout(function() { getSups(); }, POLLING_TIME);
}

function removeSup(id) {
    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'remove_sup',
        'user_id': userid,
        'message_id': generateUUID(),
        'command_data': {
            'sup_id': id,
        },
    };

    handleAjaxRequest(request, function(response) {
        if (response.error) {
            showError('Could not delete Sup.')
        } else {
            sups.splice(supPtr, 1);
            --supCount;

            var counts = document.getElementsByClassName('sup_count');
            for (var i = 0; i < counts.length; ++i) {
                counts[i].innerText = supCount;
            }

            if (supPtr > 0) {
                --supPtr;
            }

            drawSup();
        }
    });
}

function sendSup(id) {
    var request = {
        'protocol_version': PROTOCOL_VERSION,
        'command': 'send_sup',
        'user_id': userid,
        'message_id': generateUUID(),
        'command_data': {
            'user_id': id,
            'sup_id': generateUUID(),
            'date': new Date(),
        },
    };

    handleAjaxRequest(request, function(response) {
        if (response.error) {
            showError('Could not send Sup.');
        } else {
            showSuccess('Sup sent to ' + id + '!');
        }
    });
}
