function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle add response");
    console.log(resultDataJson);
    

    
    $("#add_star_error_message").text("");
    $("#add_movie_error_message").text("");
    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["add_status"] === "success") {
    	let type = resultDataJson["success_type"];
    	if(type == "star"){
        	$("#add_star_error_message").text(resultDataJson["success_message"]);
        }
        if(type == "movie"){
        	$("#add_movie_error_message").text(resultDataJson["success_message"]);
    	}
    } else {
        console.log("show error message");
        console.log(resultDataJson["add_message"]);
        let type = resultDataJson["fail_type"];
        console.log(type);
        if(type == "star"){
        	$("#add_star_error_message").text(resultDataJson["add_message"]);
        }
        if(type == "movie"){
        	$("#add_movie_error_message").text(resultDataJson["add_message"]);
    	}
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddStarForm(formSubmitEvent) {
    console.log("submit add form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.post(
        "api/dashboard",
        // Serialize the login form to the data sent by POST request
        $("#add_star_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString)
    );
}

function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.post(
        "api/dashboard",
        // Serialize the login form to the data sent by POST request
        $("#add_movie_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString)
    );
}

function handleResult(resultData) {

	let table_content = "<ul>";
	for(let i = 0; i<resultData.length;i++)
	{
		table_content +="<li>" + "Table: " + resultData[i]["table_name"] + "<ul>";
		for(let j = 0; j < resultData[i]["column_name"].length;j++)
		{
			table_content += "<li>" + "Column name--" + resultData[i]["column_name"][j] + " Type name--" + resultData[i]["data_type"][j] + " Column size--" + resultData[i]["column_size"][j] + " Is Nullable--" + resultData[i]["is_nullable"][j] + "</li>";
		}
		table_content += "</ul>";
		table_content += "</li>"
	}
	table_content += "</ul>"
	$("#metadata_table").html("");
	$("#metadata_table").append(table_content);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/dashboard",
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
// Bind the submit action of the form to a handler function
$("#add_star_form").submit((event) => submitAddStarForm(event));
$("#add_movie_form").submit((event) => submitAddMovieForm(event));