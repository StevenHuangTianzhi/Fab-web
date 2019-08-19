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
    let starTableBodyElement = jQuery("#item_table_body");
    
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(100, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" + resultData[i]['title'] +     // display star_name for the link text
            "</th>";
        rowHTML += '<th> <label> ' + resultData[i]["quantity"] + ' </label> </th>'
        //rowHTML += '<th> <label> New quantity: </label> <form Action = "#" id = "change"> <input type="hidden" name = "' + resultData[i]["title"] + '" value= "' + resultData[i]["title"] + '"> <input type = "text" name = "numbers"> <input type="submit" value="Update" /> </form> </th>';   
        rowHTML += '<th> <label> New quantity: </label> ' + "<input id = 'index' type = 'hidden' value ='" +i+ "'>" +'<input id = "quantity'+i+'"'+' type = "text" name = "quantity">'+"<input id = 'name"+ i +"'" + "type = 'hidden' value ='" +resultData[i]["title"] + "'>" + '<a href="#" onclick = "addHandler('+i+')">' + "update";  
        rowHTML += "</tr>";       
        
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function addHandler(i){
	//let index = document.getElementById('index').value;
	let index = i;
	let num = document.getElementById('quantity'+index).value;
	let title = document.getElementById('name'+index).value;

	event.preventDefault();
	
	jQuery.ajax({
		dataType:"json",
		method:"POST",
		url: "api/cart?" + "title=" + title + "&num=" + num
	})
	
	location.reload(true);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */


// Makes the HTTP GET request and registers on success callback function handleStarResult

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData)// Setting callback function to handle data returned successfully by the StarsServlet
});

$("#change").submit((event) => addHandler(event));
//$("#cart").submit((event) => handleCartInfo(event));