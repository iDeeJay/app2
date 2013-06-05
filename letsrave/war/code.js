'use strict';

var channel;

$(document).ready(function() {
	
	$("#tokenButton").click(function() {
	    getToken();
	  }); 	
	
});



function generatePlaylistHTML(tracks){
	var html = '';
	
	for(var i = 0; i<tracks.length; ++i) {
		html += '<div class="song" id="track'+i+'"><div class="songInfo">';
		html += '<div class="artist">'+tracks[i].artists[0].name;
		html +=	'</div><div class="song-name">'+tracks[i].name+'</div></div>';
		html +=	'<img class="cover" src="http://o.scdn.co/300/'+tracks[i].album.cover+'">';
		html +=	'<div class=" btn-group vote"><button class="btn btn-inverse btn-small">&#128077</button>';
		html += '<button class="btn btn-inverse btn-small">&#128078</button></div></div>';
	}
	return html
}

function serveMsgFromServer(data) {
	var html = generatePlaylistHTML(JSON.parse(data.data))
	$("#playlist").html(html);
};

function getToken() { // dostaje token i ��czy si� z odpowiednim channelem
	/*var my_fbid;
	FB.api('/me', function(response) {
        console.log('Good to see you, ' + response.name + '.');
        my_fbid = response.id;
    });
    
    http://letsravenow.appspot.com/channel_create
   */
	$.get("http://letsravenow.appspot.com/channel_create", {event:'100', fbid:'200', thrower:'False'} )
	.done(function(token) {
		$("#token").html(token);
		channel = new goog.appengine.Channel(token);
		var socket = channel.open();
		socket.onopen = function() {};
		socket.onmessage = serveMsgFromServer;
		socket.onerror = function() {};
		socket.onclose = function() {};
	});
	
	
};  