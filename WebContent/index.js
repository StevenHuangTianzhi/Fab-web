/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    //resultDataJson = JSON.parse(resultDataString);

    //console.log("handle session response");
    //console.log(resultDataJson);
    //console.log(resultDataJson["sessionID"]);

    // show the session information 

}

var cachMap = new Map();

$.ajax({
    type: "POST",
    url: "api/index",
    success: (resultDataString) => handleSessionData(resultDataString)
});



function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	
	
	// TODO: if you want to check past query results first, you can do it here
	let title = query.trim();
	for(var key of cachMap.keys())
	{
		if(key == title)
		{
			found = true;
			var jsonData = cachMap.get(key);
			console.log(jsonData)
			console.log("Found suggestions in the cach.")
			doneCallback( { suggestions: jsonData } );
			return;
		}
	}
	// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
	// with the query data
	console.log("sending AJAX request to backend Java Servlet")
	jQuery.ajax({
		"method": "GET",
		// generate the request url from the query.
		// escape the query string to avoid errors caused by special characters 
		"url": "api/index?query=" + escape(query),
		"success": function(data) {
			// pass the data, query, and doneCallback function into the success handler
			handleLookupAjaxSuccess(data, query, doneCallback) 
		},
		"error": function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	console.log("Caching lookup result.")
	let title = query.trim();
	cachMap.set(title,jsonData);

	
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["id"])
	let url = "single-movie.html?id=" + suggestion["data"]["id"];
	window.location.replace(url);
	
}




$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    //triggerSelectOnValidInput = false,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});





function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
	let title = query.trim().replace(" ","+");
	let url = "movie-list.html?title=" + title + "&year=&director=&starname=&genre=&browse=False";
	window.location.replace(url);
	
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})




// Bind the submit action of the form to a event handler function
//$("#search").submit((event) => handleCartInfo(event));
//$("#checkout").submit(checkout());