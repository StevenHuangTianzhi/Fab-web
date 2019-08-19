function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle checkout response");
    console.log(resultDataJson);
    console.log(resultDataJson["checkout_status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["checkout_status"] === "success") {
    	console.log("checkout Succeed");
    	let cnumber = document.getElementById('cnumber').value;
    	let fname = document.getElementById('fname').value;
    	let lname = document.getElementById('lname').value;
    	let edate = document.getElementById('edate').value;
    	let url = "confirm.html?";
    	url += "cnumber=" + cnumber + "&fname=" + fname + "&lname=" + lname + "&edate=" + edate;
        window.location.replace(url);
    } else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["checkout_message"]);
        $("#checkout_error_message").text(resultDataJson["checkout_message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit checkout form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.post(
        "api/checkout",
        // Serialize the login form to the data sent by POST request
        $("#checkout_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString)
    );
}

// Bind the submit action of the form to a handler function
$("#checkout_form").submit((event) => submitLoginForm(event));