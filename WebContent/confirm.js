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

function handleResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#order_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["orderid"] + "</th>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";        	
        rowHTML += "</tr>";       
        
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}
let cnumber = getParameterByName("cnumber")
let fname = getParameterByName("fname")
let lname = getParameterByName("lname")
let edate = getParameterByName("edate")

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */


// Makes the HTTP GET request and registers on success callback function handleStarResult

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirm?" + "cnumber=" + cnumber + "&fname=" + fname + "&lname=" + lname + "&edate=" + edate,
    success: (resultData) => handleResult(resultData)
});
