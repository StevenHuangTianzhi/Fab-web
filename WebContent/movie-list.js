/**
 * This example is following frontend and backend separation.

 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
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
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(40, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
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
        //rowHTML += '<th> <form Action = "#" id = "add"> <input type="hidden" name = "" value= "' + resultData[i]["movie_title"] + '"> </form> <input type="button" value="Add to Cart" /> </a> </th>';        	
        rowHTML += "</tr>";       
        
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function addHandler(event){
	
	
}


let title = getParameterByName("title")
let year = getParameterByName("year")
let director = getParameterByName("director")
let starname = getParameterByName("starname")
let genre = getParameterByName("genre")
let browse = getParameterByName("browse")
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */


// Makes the HTTP GET request and registers on success callback function handleStarResult

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list?" + "title=" + title + "&year=" + year + "&director=" + director + "&starname=" + starname + "&genre=" + genre + "&browse=" + browse, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

$("#add").submit((event)=>addHandler(event))
//$("#cart").submit((event) => handleCartInfo(event));