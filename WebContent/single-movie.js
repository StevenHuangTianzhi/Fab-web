/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>");
    
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    
    //rowHTML += '<th> <form action = "#" method = "post" id = "add"> <input type="hidden" name = "title" value= "'+ id +'"><input type="submit" value="Add to Cart"> </form></th>';
    
    
    
    let movieTableBodyElement = jQuery("#movie_table_body");
    
    
    
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(1, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>"
            for (let j = 0; j < resultData[i]["movie_stars"].length; j++)
            	rowHTML += '<a href="single-star.html?id=' + resultData[i]['movie_stars'][j] + '">'
            	+ resultData[i]["movie_stars"][j]+ ', ' + '</a>';
            //rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";
            rowHTML += "</th>";
            rowHTML += "<th>";
            for (let j = 0; j < resultData[i]["movie_genres"].length; j++)
            	rowHTML += '<a href="single-genre.html?id=' + resultData[i]["movie_genres"][j] + '">'
            	+ resultData[i]["movie_genres"][j]+ ', ' + '</a>'; 
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        //rowHTML += "<th>" + '<form id = "add"> <input type="submit" value="Add to Cart"> </form>' + "</th>"
        //rowHTML += '<th> <form action = "#" method = "post" id = "add"> <input type="hidden" name = "title" value= "'+ resultData[i]["movie_title"]+'"><input type="submit" value="Add to Cart"> </form></th>';
        document.getElementById("info").value = resultData[i]["movie_title"];
       
        rowHTML += "</tr>";
        
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

function addHandler(addEvent)
{
	console.log("adding item");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    addEvent.preventDefault();

    $.post(
        "api/single-movie",
        // Serialize the login form to the data sent by POST request
        $("#add").serialize(),
        (resultDataString) => handleAddResult(resultDataString)
    );
}

function handleAddResult(resultDataString)
{
	
}
// Get id from URL
let movieId = getParameterByName('id');

$("#add").submit((event)=>addHandler(event));

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

