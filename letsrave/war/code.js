// Additional JS functions here
window.fbAsyncInit = function() {
	FB.init({
		appId : '359136884196472', // App ID
		channelUrl : '//WWW.letsravenow.appspot.com/channel.html', // Channel File
		status : true, // check login status
		cookie : true, // enable cookies to allow the server to access the session
		xfbml : true
	// parse XFBML
	});
	
	FB.Event.subscribe('auth.statusChange',
		    function(response) {
		        fillView(response);
		    }
		);
};

'use strict';

function login() {
	FB.login(function(response) {
		if (response.authResponse) {
			testAPI();
			
		} else {
			// cancelled
		}
	}, {scope: 'user_events'});
}


function fillView(response) {
	//fills mainpage with personalised data
		
		if (response.status === 'connected') {
			FB.api('/me', function(response) {
				//welcome_msg
				var welcome_msg_html = "<h2>Hi " + response.first_name + '!';
				welcome_msg_html += "</h2><h4>Time to do some playlist shuffling</h4>";
				$('#welcome_msg').html(welcome_msg_html);
				localStorage.myId = response.id;
				
				FB.api('/me/events', function(response) {
					var event_list = "";
					for(var i = 0; i<response.data.length; i++) {
						event_list += response.data[i].id + ",";
					}
					$.get('/filter_events', {events: event_list}, 
							function(filtered_events) {
								$('#partylist').html(generatePartylistHtml(filtered_events, response));
								fillThrowers(filtered_events);
								$('#partylist').show();
							}
					);
				});
				
			});
			var uid = response.authResponse.userID;
		    var accessToken = response.authResponse.accessToken;
		} else if (response.status === 'not_authorized') {
			//TODO
		} else {
			// not logged in
		}


	$('#tokenButton').css("display","block");
}

//REWRITE USING SINGLE FQL
function fillThrowers(partyList) {
	var partyIdArray = partyList.split(",");
	partyIdArray.forEach(function(id) {
		if (id == "")
			return;
		else {
			FB.api('/' + id + '?fields=owner', function(response) {
				$('#thrower' + id).html(response.owner.name);
				
			})
		}
	});
	
}

function generatePartylistHtml(partyList, eventInfo) {
	var result = "";
	var partyIdArray = partyList.split(",");
	if (partyIdArray.length == 0) {
		result += "<h2>You have no incoming parties? Throw your own!</h2>"
	} else {
		result += "<h2>Your parties, choose one:</h2>";
		result += "<div id='list'>";
		partyIdArray.forEach(function(id) {
			if (id == "") return;
			else result += addPartylistRow(id, eventInfo);
		});
		result += "</div>";
	} 
	return result;
}


function addPartylistRow(id, eventInfo) {
	var party = null
	for(var i = 0; i<eventInfo.data.length; i++) {
		if (eventInfo.data[i].id == id) {
			party = eventInfo.data[i]
			break
		}
	}

	var result = "<button class='btn btn-inverse' onclick='showPlaylist(" + party.id + ", \"" + getHour(party.start_time) +
				"\", \"" + getDate(party.start_time) + "\")'>"
	result += '<div class="party_name">'
	result += "<h4>" + party.name + "</h4>"
	result += "by <span id='thrower" + party.id + "'>loading thrower...</span></div>"
	result += "<div class='party_date'>at " + getHour(party.start_time) +'<br> '+ getDate(party.start_time) + '</div>'
	result += "</button>"
		
	return result;
}


function showPlaylist(id, hour, date) {
	$('#splashScreen').hide();
	
	localStorage.actParty = id.toString();
	
	if (isPartyStarting(hour, date))
		getToken();
	
	$.get("/playlist", {event : id.toString()}, function(playlist) {
		if (playlist.substring(0,3) == "ERR") {
			$('#playlist').html("There is no such a party!");
		} else {
			var playlist_html = generatePlaylistHTML(JSON.parse(playlist));
			$('#playlist').html(playlist_html);	
			disableAlreadyVoted();
		}
	})
}

function disableAlreadyVoted() {
	if (localStorage[localStorage.actParty]) {
		var dict = JSON.parse(localStorage[localStorage.actParty]);
		for(var key in dict) {
			var upOrDown = dict[key];
			disableVoteButtons(upOrDown, key);
		} 
	}
	
};

function disableVoteButtons(upOrDown, uri) {

	var btn = document.getElementById(uri + upOrDown);
	btn.className = "btn btn-small btn-success"
		
	document.getElementById(uri + "U").disabled = true;
	document.getElementById(uri + "D").disabled = true;
}

// Load the SDK Asynchronously
(function(d) {
	var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
	if (d.getElementById(id)) {
		return;
	}
	js = d.createElement('script');
	js.id = id;
	js.async = true;
	js.src = "//connect.facebook.net/en_US/all.js";
	ref.parentNode.insertBefore(js, ref);
}(document));


function generatePlaylistHTML(tracks){
	var html = '';
	console.log(tracks);
	if(tracks.data != undefined ) tracks = tracks.data;
	for(var i = 0; i<tracks.length; ++i) {
		html += '<div class="song" id="track'+i+'"><div class="songInfo">';
		html += '<div class="artist">'+tracks[i].artists[0].name;
		html +=	'</div><div class="song-name">'+tracks[i].name+'</div></div>';
		var imgHref = tracks[i].album.cover
		html +=	'<img class="cover" src="http://o.scdn.co/300/'+ imgHref.substring(14,imgHref.length) +'">';
		html +=	'<div class=" btn-group vote">'
			  + '<button id="' + tracks[i].uri + 'U" class="btn btn-inverse btn-small" onclick="vote(true, \'' 
			  + tracks[i].uri +'\')">&#128077</button>';
		html += '<button id="' + tracks[i].uri + 'D" class="btn btn-inverse btn-small" onclick="vote(false, \'' 
		  	  + tracks[i].uri +'\')">&#128078</button>' 
		  	  + '</div></div>';
	}
	
	html += '<div style="text-align:center; margin-top:50px; margin-bottom:50px">'
		+ '<button class="btn btn-inverse" onclick="window.location.reload()">'
		+ '<font color="white">Back to party list</font></button></div>'
	return html
}

function vote(up, uri) {
	$.post("/vote", {event: localStorage.actParty, user: localStorage.myId, 
					song: uri, up: up.toString()})
	.always(function() {				
		
		var upOrDown = 'U'
		if (!up)
			upOrDown = 'D'						
						
		//disabling buttons
		disableVoteButtons(upOrDown, uri);
						
		// notifying local storage about votes
		saveVote(upOrDown, uri);
	});
}


function saveVote(upOrDown, uri) {

	var dict = {}
	if (localStorage[localStorage.actParty]) {
		dict = JSON.parse(localStorage[localStorage.actParty]);
	}
	
	dict[uri] = upOrDown;
	
	localStorage[localStorage.actParty] = JSON.stringify(dict);
}


function serveMsgFromServer(data) {
	var html = generatePlaylistHTML(JSON.parse(data.data))
	$("#playlist").html(html);
	disableAlreadyVoted();
};

var channel;

function getToken() { 
	$.post("/channel_create", {event: localStorage.actParty, user: localStorage.myId, thrower: 'false'} )
	.done(function(token) {
		createChannel(token);
	});

}; 

function createChannel(token) {
	channel = new goog.appengine.Channel(token);
	var socket = channel.open();
	socket.onopen = function() {};
	socket.onmessage = serveMsgFromServer;
	socket.onerror = function() {};
	socket.onclose = function() {};
}

