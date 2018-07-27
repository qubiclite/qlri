// TODO
function block_form(form_identifier) {
	$(form_identifier + " input[type=button]").addClass('hidden');
}

// TODO
function unblock_form(form_identifier) {
	$(form_identifier + " input[type=button]").removeClass('hidden');
}

function generate_oracle_box(oracle) {

	var oracle_id = oracle['id'];
	var state = oracle['state'];
	var qubic = oracle['qubic'];

	var box = "<div class='box oracle_box' id='oracle_"+oracle_id+"'><div class='entity_id'>"+oracle_id+"</div>"
	 	+ "<div class='entity_property'>qubic: "+qubic+"</div>"
	 	+ "<div class='entity_property'>state: "+state+"</div>"
		+"<div class='inline_button  delete_button' onclick=' delete_oracle(\""+oracle_id+"\")'> delete</div>";

	if(state == 'running')
		box += "<div class='inline_button   pause_button' onclick='  pause_oracle(\""+oracle_id+"\")'>  pause</div>";
	else if(state == 'paused')
		box += "<div class='inline_button restart_button' onclick='restart_oracle(\""+oracle_id+"\")'>restart</div>";

	box += "</div>";

	return box;
}

function generate_iam_box(iam_id) {

	var box = "<div class='box' id='iam_"+iam_id+"'><div class='entity_id'>"+iam_id+"</div>"
		+"<div class='inline_button   delete_button' onclick='delete_iam(\""+iam_id+"\")'>delete</div>"
		+"<div class='inline_button assembly_button' onclick='write_iam_form(\""+iam_id+"\")'>write</div></div>"

	return box;
}

function generate_qubic_box(qubic) {

	var qubic_id = qubic['id'];
	var state = qubic['state'];

	var box = "<div class='box' id='qubic_"+qubic_id+"'><div class='entity_id'>"+qubic_id+"</div>"
	 	+"<div class='entity_property'>state: "+state+"</div>"
		+"<div class='inline_button   delete_button' onclick=' delete_qubic(\""+qubic_id+"\")'>delete</div>"
		+"<div class='inline_button  details_button' onclick='qubic_details(\""+qubic_id+"\")'>details</div>";

	if(state == 'assembly phase')
		box += "<div class='inline_button assembly_button' onclick='show_assembly(\""+qubic_id+"\")'>applications</div>"

	box += "</div>";

	return box;
}

function generate_application_box(qubic_id, application) {

	var oracle_id = application['oracle_id'];
	var oracle_name = application['oracle_name'];

	box = "<div class='box' id='qubic_"+oracle_id+"'><div class='entity_id'>"+oracle_id+"</div>"
		+ "<div class='entity_property'>name: "+oracle_name+"</div>"
		+ "<div class='checkbox_container'><input type='checkbox' class='assembly_checkbox' /> <label>include into assembly</label></div></div>";

	return box;
}

function generate_epoch_box(epoch) {

	var quorum = epoch['quorum'];
	var quorum_max = epoch['quorum_max'];
	var result = epoch['result'] ? epoch['result'] : undefined;

	var box = "<div class='box'><div class='epoch'>epoch #"+epoch['epoch']+" (quorum: "+quorum+"/"+quorum_max+", "+(quorum == 0 ? 0 : 100.0*quorum/quorum_max).toFixed(1)+"%)</div>";
	if(result !== undefined)
		box += "<div class='epoch_result'>"+result+"</div>";
	box += "</div>";

	return box;
}

function qubic_details(qubic_id) {
	$('#button_qr_form').click();
	$('#form_qr_qubic_id').val(qubic_id);
	$('#button_qr').click();
}

function list_oracles() {
	var $list = $('#oracle_view .scroll');
	var html = "";

	send_ajax(
	{'command': 'ol'},
	function(data) {
		data['list'].forEach(function(oracle) {
		  html += generate_oracle_box(oracle);
		});
		$list.html(html);
	});
};

function list_qubics() {
	var $list = $('#qubic_view .scroll');
	var html = "";

	send_ajax(
	{'command': 'ql'},
	function(data) {
		data['list'].forEach(function(oracle) {
		  html += generate_qubic_box(oracle);
		});
		$list.html(html);
	});
};

function write_iam_form(id) {
	$('#form_iw_iam_handle').val(id);
	unblock_form('#window_iw form');
	$('#window_iw').removeClass('hidden');
}

function delete_iam(iam_id) {
	send_ajax(
	{'command': 'id', 'iam stream handle': iam_id},
	function(data) {
		$('#button_il').click();
	});
}

function delete_oracle(oracle_id) {
	send_ajax(
	{'command': 'od', 'oracle handle': oracle_id},
	function(data) {
		$('#oracle_'+oracle_id).remove();
	});
}

function delete_qubic(qubic_id) {
	send_ajax(
	{'command': 'qd', 'qubic handle': qubic_id},
	function(data) {
		$('#qubic_'+qubic_id).remove();
	});
}

function pause_oracle(oracle_id) {
	send_ajax(
	{'command': 'op', 'oracle handle': oracle_id},
	function(data) {
		$('#oracle_'+oracle_id + " .pause_button").addClass("hidden");
		$('#oracle_'+oracle_id + " .restart_button").removeClass("hidden");
	});
}

function restart_oracle(oracle_id) {
	send_ajax(
	{'command': 'or', 'oracle handle': oracle_id},
	function(data) {
		$('#oracle_'+oracle_id + " .pause_button").removeClass("hidden");
		$('#oracle_'+oracle_id + " .restart_button").addClass("hidden");
	});
}

function show_assembly(qubic_id) {

	send_ajax(
	{'command': 'qla', 'qubic handle': qubic_id},
	function(data) {
		$scroll = $('#window_qla .scroll');
		$scroll.html("");
		$('#application_amount').html(data['list'].length);
		data['list'].forEach(function(application) {
		  $scroll.append(generate_application_box(qubic_id, application));
		});
		$('#window_qla').removeClass("hidden");
		$('#form_qla_qubic_id').val(qubic_id);
	});
}

function open_create_oracle_form() {
	unblock_form('#window_oc form');
	$('#window_oc').removeClass('hidden');
}

function open_create_qubic_form() {
	unblock_form('#window_qc form');
	$('#window_qc').removeClass('hidden');
}

function unix_to_date(unix){
	var a = new Date(unix * 1000);
	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	var y = a.getFullYear();
	var mo = months[a.getMonth()];
	var d = a.getDate();
	var h = a.getHours();
	var m = a.getMinutes();
	var s = a.getSeconds();
	return y + '/' + mo + '/' + (d<10?"0"+d:d) + ' ' + (h<10?"0"+h:h) + ':' + (m<10?"0"+m:m) + ':' + (s<10?"0"+s:s);
}

function install_app(app_id) {
	$('#qapps').html("");
	send_ajax({
		'command': 'ai',
		'url': $('#app_url').val(),
	}, function(data) {
		list_qapps();
	}, function(data) {
		list_qapps();
	}); 
}

function uninstall_app(app_id) {
	$('#qapps').html("");
	send_ajax({
		'command': 'au',
		'app': app_id,
	}, function(data) {
		list_qapps();
	}, function(data) {
		list_qapps();
	}); 
}
	
function load_qapp(name) {
	$('#page_qapp iframe').attr("src", "qapps/"+name+"/index.html");
	$('nav').addClass("hidden");
	$('footer').addClass("hidden");
	$('#page_qapp_list').addClass("hidden");
	$('#page_qapp').removeClass("hidden");
}

function list_qapps() {
	send_ajax({
		'command': 'al',
	}, function(data) {
		$('#qapps').html("");
		data['list'].forEach(function(element) {
			details = "";
			var qapp = $("<div class='qapp'></div>");

			qapp.append($("<img src='qapps/"+element['id']+"/imgs/thumbnail.png'>"));

			var details = $("<div class='details'></div>");
			details.append($("<div class='title'></div>").text(element['title']));
			details.append($("<div class='description'></div>").text(element['description']));
			details.append($("<a class='website' href='"+element['url']+"' target='_blank'></a>").text(element['url']));
			details.append($("<div class='license'></div>").text(element['license']));
			details.append('<a class="button" href="qapps/'+element['id']+'/" target="_blank"> start</a>');
			details.append('<div class="button" onclick="uninstall_app(\''+element['id']+'\')">uninstall</div>');
			qapp.append(details);

			$('#qapps').append(qapp);
		})
	}); 
}