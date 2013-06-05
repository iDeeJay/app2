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



function login() {
	FB.login(function(response) {
		if (response.authResponse) {
			testAPI();
			
		} else {
			// cancelled
		}
	});
}


function fillView(response) {
	//fills mainpage with personalised data
		
		if (response.status === 'connected') {
			FB.api('/me', function(response) {
				//welcome_msg
				var welcome_msg_html = "<font color='white'>";
				welcome_msg_html += "Hello " + response.name + '!';
				welcome_msg_html += "</font>";
				$('#welcome_msg').html(welcome_msg_html);
				

				//party_list
				$.get("/get_party_list", { user_id : response.id},
						function(response){
							var partyListHtml = generatePartyListHtml(response);
							$('#party_list').html(partyListHtml);
					})
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

function generatePartyListHtml(partyList) {
	result = "<font color='white'>"
	partyArray = partyList.split(";");
	if (partyArray.length == 0) {
		result += "You have no parties nor friends : <"
	} else {
		result += "Your parties:"
		result += "<table border='1'>"
		result += "<tr>"
		result += "<th>Party name</th>"
		result += "<th>Party thrower</th>"
		result += "<th>Date</th>"
		result += "<th>Hour</th>"
		result += "</tr>"	
		partyArray.forEach(function(entry) {
			party = entry.split("|")
			result += "<tr>"
			result += "<th>" + party[0] + "</th>"
			result += "<th>" + party[1] + "</th>"
			result += "<th>" + party[2] + "</th>"
			result += "<th>" + party[3] + "</th>"
			result += "<th>"
					+ "<input type='button' onclick='showPlaylist(" + party[4] +")' " +
							"value='Show playlist!'>"
					+ "</th>"
			result += "</tr>"
		});
	}
		
	result += "</font>" 
	return result;
}


function showPlaylist(id) {
	$.get("/show_playlist", {party_id : id}, function(response) {
		if (response == "") {
			$('#playlist').html("<font color='white'>There is no palylist yet. </font>");
		} else {
			var playlist_html = generatePlaylistHTML(JSON.parse(response));
			$('#playlist').html(playlist_html);
		}
	})
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

