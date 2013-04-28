'use strict';

var channel;

$(document).ready(function() {
	$('#playlist_btn').click(function() {
		var Content = $('code').text()
		$.post("http://localhost:8888/channel", {
			event : '100',
			thrower : 'True',
			content : Content
		}).done(function(response) {
			console.log(response);
		});
	});

	$("#tokenButton").click(function() {
		getToken();
	});

});

function getToken() { // dostaje token i ¸ˆczy si« z odpowiednim channelem
	/*
	 * var my_fbid; FB.api('/me', function(response) { console.log('Good to see
	 * you, ' + response.name + '.'); my_fbid = response.id; });
	 * 
	 * http://letsravenow.appspot.com/channel_create
	 */
	$.get("http://localhost:8888/channel_create", {
		event : '100',
		fbid : '300',
		thrower : 'True'
	}).done(function(token) {
		$("#token").html(token);
	});

};