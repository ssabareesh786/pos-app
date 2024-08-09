// GLOBAL VARIABLES
// 1) For add brand
var $brand = $('#brand-form input[name=brand]');
var $category = $('#brand-form input[name=category]');
var $add = $('#add-brand');
// 2) For edit brand
var $editBrand = $('#brand-edit-form input[name=brand]');
var $editCategory = $('#brand-edit-form input[name=category]');
var $update = $('#update-brand');
// 3) For tracking edit data
var oldData = "";
var newData = "";

// BRAND URL FUNCTION
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

// GET AND DISPLAY BRAND LIST FUNCTIONS
// 1) Get functions
function getBrandListUtil(){
	var pageSize = $('#inputPageSize').val();
	getBrandList(0, pageSize);
}

function getBrandList(pageNumber, pageSize){
	var url = getBrandUrl() + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json; charset=utf-8',
	   success: function(data) {
	   		displayBrandList(data.content,pageNumber*pageSize);
			$('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			paginator(data, "getBrandList", pageSize);
	   },
	   error: handleAjaxError
	});
}
// 2) UI Display functions
function displayBrandList(data, sno){
	$("#brand-table-body").empty();
    var row = "";
	for (var i = 0; i < data.length; i++) {
	sno += 1;
	var buttonHtml = spanBegin + '<button onclick="displayEditBrand(' + data[i].id + ')" class="btn btn-secondary only-supervisor">edit</button>' + spanEnd;
	row = "<tr><td>" 
	+ sno + "</td><td>" 
	+ data[i].brand + "</td><td>"
	+ data[i].category + "</td><td>" 
	+ buttonHtml 
	+ "</td></tr>";
	$("#brand-table-body").append(row);
	}
	enableOrDisable();
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: function(response){
			handleAjaxError(response);
	   }
	});
}

function displayBrand(data){
	$("#brand-edit-form input[name=brand]").val(data.brand);	
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
	$update.attr('disabled', true);
	oldData = {
		'brand': $editBrand.val(),
		'category': $editCategory.val()
	};
	newData = {
		'brand': $editBrand.val(),
		'category': $editCategory.val()
	};
}

// VALIDATION FUNCTIONS FOR ADD BRAND
// 1) Utils
function validateAddBrandUtil(){
	$brand.off();
	$brand.on('input', validateAddBrand);
}

function validateAddCategoryUtil(){
	$category.off();
	$category.on('input', validateAddCategory);
}
// 2) Actual functions
function validateAddBrand(){
	if($brand.val().length == 0){
		$brand.addClass('is-invalid');
		$('#bivf1').attr('style', 'display:block;');
	}
	else{
		if($brand.hasClass('is-invalid')){
			$brand.removeClass('is-invalid');
		}
		$('#bivf1').attr('style', 'display:none;');
	}
	enableOrDisableAdd();
}

function validateAddCategory(){
	if($category.val().length == 0){
		$category.addClass('is-invalid');
		$('#civf1').attr('style', 'display:block;');
	}
	else{
		if($category.hasClass('is-invalid')){
			$category.removeClass('is-invalid');
		}
		$('#civf1').attr('style', 'display:none;');
	}
	enableOrDisableAdd();
}

// VALIDATION FUNCTIONS FOR EDIT BRAND
function validateEditForm(){
	if($editBrand.val().length == 0){
		$editBrand.addClass('is-invalid');
		$('#ebif1').attr("style", "display:block;");
	}
	else{
		if($editBrand.hasClass('is-invalid')){
			$editBrand.removeClass('is-invalid');
		}
		$('#ebif1').attr("style", "display:none;");
	}
	if($editCategory.val().length == 0){
		$editCategory.addClass('is-invalid');
		$('#ecif1').attr("style", "display:block;");
	}
	else{
		if($editCategory.hasClass('is-invalid')){
			$editCategory.removeClass('is-invalid');
		}
		$('#ecif1').attr("style", "display:none;");
	}
	
	newData = {
		'brand': $editBrand.val(),
		'category': $editCategory.val()
	};

	if((oldData.brand == newData.brand && oldData.category == newData.category)
		|| ($editBrand.hasClass('is-invalid') || $editCategory.hasClass('is-invalid'))){
		$update.attr('disabled', true);
	}
	else{
		$update.attr('disabled', false);
	}
}

//BUTTON ACTIONS
// 1) Add brand button
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	if(validator(json)){
		var url = getBrandUrl();
		
		$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			handleAjaxSuccess("Brand "+response.brand+" and category "+response.category+" added successfully");
			getBrandListUtil();
			clearAddData();
		},
		error: function(response){
			handleAjaxError(response);
			disableAdd();
			}
		});
	}
	return false;
}
// 2) Update brand button
function updateBrand(event){
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();	
	var url = getBrandUrl() + "/" + id;


	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);
	if(validator(json)){
		$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			handleAjaxSuccess("Update successfull!!!");
			getBrandListUtil();
			clearEditData();
		},
		error: function(response){
			handleAjaxError(response);
		}
		});
	}
	
	return false;
}

// ENABLE AND DISABLE FUNCTIONS
function enableAdd(){
	$add.attr('disabled', false);
}

function disableAdd(){
	$add.attr('disabled', true);
}

function enableOrDisableAdd(){
	var brand = $brand.val();
	var category = $category.val();
	if(brand.length > 0 && category.length > 0 && 
		!$brand.hasClass('is-invalid') && !$category.hasClass('is-invalid')){
		$add.attr('disabled', false);
	}
	else{
		$add.attr('disabled', true);
	}
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

function isValid(uploadObject) {
	if(uploadObject.hasOwnProperty('brand') &&
		uploadObject.hasOwnProperty('category') &&
		Object.keys(uploadObject).length==2){
			return true;
	}
	return false;
}

function processData(){
	var file = $('#brandFile')[0].files[0];
	resetUploadDialog();
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results;
	if(isValid(fileData[0])){
		uploadRows();
	}
	else{
		$.notify("Invalid file", "error");
	}
	$('#process-data').attr('disabled', true);
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		getBrandListUtil();
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getBrandUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){
			var response = JSON.parse(response.responseText);
			row.error = response.message;
	   		errorData.push(row);
			$('#download-errors').attr('disabled', false);
			uploadRows();
	   }
	});
}

function downloadErrors(){
	writeFileData(errorData);
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	// Reset buttons
	$('#process-data').attr('disabled', true);
	$('#download-errors').attr('disabled', true);
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#process-data').attr('disabled', false);
	$('#brandFileName').html(fileName.split('\\')[2]);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

// RESETTING AND CLEARING FUNCTIONS
function clearEditData(){
	$editBrand.val('');
	$editCategory.val('');
	$update.attr('disabled', true);
	clearCommentsEditForm();
	if($('#edit-brand-modal').hasClass('show')){
		$('#edit-brand-modal').modal('toggle');
	}
}

function clearCommentsAddForm(){
	if($brand.hasClass('is-valid')){
		$brand.removeClass('is-valid');
	}
	if($brand.hasClass('is-invalid')){
		$brand.removeClass('is-invalid');
	}
	if($category.hasClass('is-valid')){
		$category.removeClass('is-valid');
	}
	if($category.hasClass('is-invalid')){
		$category.removeClass('is-invalid');
	}
	$('#bivf1').attr('style', 'display:none;');
	$('#civf1').attr('style', 'display:none;')
}

function clearCommentsEditForm(){
	if($editBrand.hasClass('is-valid')){
		$editBrand.removeClass('is-valid');
	}
	if($editBrand.hasClass('is-invalid')){
		$editBrand.removeClass('is-invalid');
	}
	if($editCategory.hasClass('is-valid')){
		$editCategory.removeClass('is-valid');
	}
	if($editCategory.hasClass('is-invalid')){
		$editCategory.removeClass('is-invalid');
	}
	$('#ebif1').attr('style', 'display:none;');
	$('#ecif1').attr('style', 'display:none;');
} 

function clearAddData(){
	$brand.val('');
	$category.val('');
	$add.attr('disabled', true);

	clearCommentsAddForm();

	if($('#add-brand-modal').length){
		$('#add-brand-modal').modal('toggle');
	}
}

function clearUploadData(){
	$('#rowCount').text('0');
	$('#processCount').text('0');
	$('#errorCount').text('0');
	$('#brandFile').val('');
	$('#process-data').attr('disabled', true);
	$('#download-errors').attr('disabled', true);
	if($('#upload-brand-modal').hasClass('show'))
	$('#upload-brand-modal').modal('toggle');
}

// ADD MODAL TOGGLER
function addData(){
	$('#add-brand-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
	$('#cancel1').click(clearAddData);
	$('#cancel2').click(clearAddData);
	$('#cancel3').click(clearEditData);
	$('#cancel4').click(clearEditData);
	$('#cancel5').click(clearUploadData);
	$('#cancel6').click(clearUploadData);
	$('#add-data').click(addData);
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName);
	$('#inputPageSize').on('change', getBrandListUtil);
	$editBrand.on('input', validateEditForm);
	$editCategory.on('input', validateEditForm);
}

$(document).ready(init);
$(document).ready(getBrandListUtil);
$(document).ready(enableOrDisable);
$(document).ready(function(){
	$add.attr('disabled', true);
	$update.attr('disabled', true);
})