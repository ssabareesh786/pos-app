// GLOBAL VARIABLES
var $addModal = $('#add-user-modal');
var $email = $('#user-form input[name=email]');
var $password = $('#user-form input[name=password]');
var $add = $('#add-user');

var $editModal = $('#edit-user-modal');
var $editEmail = $('#edit-user-form input[name=email]');
var $editPassword = $('#edit-user-form input[name=password]');
var $editRole = $('#edit-user-form select[name=role]');
var $update = $('#update-user');

var oldData = "";
var newData = "";


function getUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/supervisor/user";
}

//BUTTON ACTIONS
function addUser(event){
	//Set the values to update
	var $form = $("#user-form");
	var json = toJson($form);
	if(validator(json)){
	var url = getUserUrl();
	
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			handleAjaxSuccess('User added successfully');
			$addModal.modal('toggle');
	   		getUserListUtil();   
	   },
	   error: function(response){
			handleAjaxError(response);
	   }
	});
	}
	return false;
}

function getUserListUtil(){
	var pageSize = $('#inputPageSize').val();
	getUserList(0, pageSize);
}

function getUserList(pageNumber, pageSize){
	var url = getUserUrl() + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
			console.log(data);
	   		displayUserList(data.content, pageNumber*pageSize); 
			$('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			paginator(data, "getUserList", pageSize);
		},
	   error: handleAjaxError
	});
}

function deleteUser(id){
	var url = getUserUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getUserListUtil();    
	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS

function displayUserList(data, sno){
	console.log(data);
	var $tbody = $('#user-table-body');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		sno += 1;
		var buttonHtml = ' <button onclick="displayEditUser(' + e.id + ')" class="btn btn-secondary">edit</button> &nbsp;&nbsp;';
		buttonHtml += '<button onclick="deleteUser(' + e.id + ')" class="btn btn-secondary">delete</button>';
		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.email + '</td>'
		+ '<td>' + e.role + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	enableOrDisable();
}

function displayEditUser(id){
	var url = getUserUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
			displayUserEdit(data);
	   },
	   error: function(response){
			handleAjaxError(response);
	   }
	});
}

function displayUserEdit(data){
	console.log(data);
	$('#edit-user-form input[name=id]').val(data.id);
	$editEmail.val(data.email);
	if(data.role == 'supervisor'){
		$('#supervisor').attr('selected', true);
		$('#operator').attr('selected', false)
	}
	else{
		$('#operator').attr('selected', true);
		$('#supervisor').attr('selected', false);
	}
	$update.attr('disabled', true);
	oldData = data;
	newData = data;
	$editModal.modal('toggle');
}

function setEmailInvalid(message){
	$editEmail.addClass('is-invalid');
	$('#eivf').attr('style', 'display: none;')
	$('#eivf').html(message);
}

function validateEditForm(){
	newData = {
		'email': $editEmail.val(),
		'role': $editRole.val()
	}
	console.log('New Data');
	console.log(newData);
	console.log('Password'+$editPassword.val());
	if($editEmail.val() == ''){
		setEmailInvalid('Please enter email');
	}
	else{
		if($editEmail.hasClass('is-invalid')){
			$editEmail.removeClass('is-invalid');
		}
		$update.attr('disabled', false);
	}
	if(oldData.email == newData.email && oldData.role == newData.role && $editPassword.val() == ''){
		$update.attr('disabled', true);
	}
	else{
		$update.attr('disabled', false);
	}
}

function addData(){
	$('#add-user-modal').modal('toggle');
}

function clearEditData(){
	$editPassword.val('');
	if($editModal.hasClass('show')){
		$editModal.modal('toggle');
	}
}

function validateEmailUtil(){
	$email.off();
	$email.on('input', validateEmail);
}

function validatePasswordUtil(){
	$password.off();
	$password.on('input', validatePassword);
}

function validateEmail(){
	if($email.val() == ''){
		$email.addClass('is-invalid');
		$('#eivf').html('Please enter email');
		$('#eivf').attr('style', 'display: block;');
	}
	else{
		if($email.hasClass('is-invalid')){
			$email.removeClass('is-invalid');
			$('#eivf').attr('style', 'display: none;');
		}
	}
	if(!$email.hasClass('is-invalid') && !$password.hasClass('is-invalid')){
		$add.attr('disabled', false);
	}
	else{
		$add.attr('disabled', true);
	}
}

function validatePassword(){
	if($password.val() == ''){
		$password.addClass('is-invalid');
		$('#pivf').html('Please enter password');
		$('#pivf').attr('style', 'display: block;');
	}
	else{
		if($password.hasClass('is-invalid')){
			$password.removeClass('is-invalid');
			$('#pivf').attr('style', 'display: none;');
		}
	}
	if(!$email.hasClass('is-invalid') && !$password.hasClass('is-invalid')){
		$add.attr('disabled', false);
	}
	else{
		$add.attr('disabled', true);
	}
}

function clearAddData(){
	$email.val('');
	$password.val('');
	$add.attr('disabled', true);
	clearAddComments();
	if($addModal.hasClass('show')){
		$addModal.modal('toggle');
	}
}

function clearAddComments(){
	if($email.hasClass('is-invalid')){
		$email.removeClass('is-invalid')
	}
	if($password.hasClass('is-invalid')){
		$password.removeClass('is-invalid');
	}
	$('#eivf').attr('style', 'display: none;');
	$('#pivf').attr('style', 'display: none;');
}

function validateUserEditForm(jsonStr){
	jsonObj = JSON.parse(jsonStr);
    for (var [key, value] of Object.entries(jsonObj)) {
		if(key == "password"){
			continue;
		}
        if(typeof(value) == "string"){
            value = value.trim();
        }
        if(value == null || value == undefined || value == ''){
            $.notify("Value for "+ key + " is not entered or can't be interpreted");
            return false;
        }
    }
    return true;
}

function updateUser(){
	var id = $("#edit-user-form input[name=id]").val();	
	var url = getUserUrl() + "/" + id;
	console.log("URL: "+url);

	//Set the values to update
	var $form = $("#edit-user-form");
	var json = toJson($form);
	console.log(json);
	if(validateUserEditForm(json)){
		$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			handleAjaxSuccess("Update successfull");
			getUserListUtil();
			clearEditData();
		},
		error: function(response){
			handleAjaxError(response);
		}
		});
	}
	
	return false;
}

//INITIALIZATION CODE
function init(){
	$('#cancel1').click(clearAddData);
	$('#cancel2').click(clearAddData);
	$('#cancel3').click(clearEditData);
	$('#cancel4').click(clearEditData);
	$('#add-data').click(addData);
	$('#add-user').click(addUser);
	$('#inputPageSize').on('change', getUserListUtil);
	$add.attr('disabled', true);
	$update.attr('disabled', true);
	$editEmail.on('input', validateEditForm);
	$editRole.on('change', validateEditForm);
	$editPassword.on('input', validateEditForm);
	$('#update-user').click(updateUser);
}

$(document).ready(init);
$(document).ready(getUserListUtil);
$(document).ready(enableOrDisable);