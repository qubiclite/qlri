const API_VERSION = "ql-0.2";
const url = window.location.origin;
const headers = {'X-QLITE-API-Version': API_VERSION};
const error_callback = function(err) {
	toastr.error("an error occured: check your ql-node terminal and your web browser console");
	console.log("ERROR:");
	console.log(err);
};

function html_entities(str) {

    return str.replace(new RegExp('<', 'g'), '&lt;').replace(new RegExp('>', 'g'), '&gt;');
}

function send_ajax(request, success_callback, final_callback = null) {

	console.log("curl -X POST "+url+" -H 'X-QLITE-API-Version: "+API_VERSION+"' -d '"+JSON.stringify(request)+"'");

	var success_callback_wrapper = function(data) {
		if(data['success']) {
			success_callback(JSON.parse(html_entities(JSON.stringify(data))));
			if(final_callback != null) final_callback();
		} else {
			if(final_callback != null) final_callback();
			toastr.error(data['error']);
		}
	}

	var error_callback_wrapper = function(err) {
		error_callback(err);
		final_callback();
	}

	$.ajax({
	    type: 'POST', 
		url: url,
		headers: headers,
		processData: true,
	    data: JSON.stringify(request),
	    dataType: 'json',
	    success: success_callback_wrapper,
	    error: error_callback
	});
}