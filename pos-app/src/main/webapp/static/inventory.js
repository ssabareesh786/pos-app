// GLOBAL VARIABLES
// 1) For add inventory
var $barcode = $('#inventory-form input[name=barcode]');
var $quantity = $('#inventory-form input[name=quantity]');
var $add = $('#add-inventory');
var $checkBarcode = $('#check-barcode');
// 2) For edit inventory
var $editQuantity = $('#inventory-edit-form input[name=quantity]');
var $update = $('#update-inventory');
// For tracking edit data
var oldData = null;
var newData = null;

//INVENTORY URL FUNCTION
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

// PRODUCT URL FUNCTION
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

// GET AND DISPLAY INVENTORY LIST FUNCTIONS
// Get functions
function getInventoryListUtil(){
	var pageSize = $('#inputPageSize').val();
	getInventoryList(0, pageSize);
}

function getInventoryList(pageNumber, pageSize){
	var url = getInventoryUrl() + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
	   		displayInventoryList(data.content, pageNumber*pageSize);
			$('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			paginator(data, "getInventoryList", pageSize);
	   },
	   error: function(response){
		handleAjaxError(response);
	   }
	});
}
// UI Display functions
function displayInventoryList(data, sno){
	console.log(data);
	$("#inventory-table-body").empty();
    var row = "";
	for (var i = 0; i < data.length; i++) {
		sno += 1;
		var buttonHtml = spanBegin + ' <button onclick="displayEditInventory(' + data[i].productId + ')" class="btn btn-secondary only-supervisor">edit</button>' + spanEnd;
		row = "<tr><td>" 
		+ sno + "</td><td>" 
		+ data[i].barcode + "</td><td>"
		+ data[i].name + "</td><td>"
		+ data[i].quantity + "</td><td>" 
		+ buttonHtml 
		+ "</td></tr>";
		$("#inventory-table-body").append(row);
	}
	enableOrDisable();
}

function displayEditInventory(productId){
	var url = getInventoryUrl() + "/" + productId;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: function(response){
			handleAjaxError(response);
	   }
	});	
}

function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=productId]").val(data.productId);
	$('#edit-inventory-modal').modal('toggle');

	$('#inventory-edit-form input[name=barcode]').attr('disabled', true);
	$update.attr('disabled', true);
	oldData = {
		'quantity': data.quantity
	};
	newData = {
		'quantity': data.quantity
	}
}

// VALIDATION FUNCTIONS FOR ADD INVENTORY
// Utils
function validateAddBarcodeUtil(){
	$barcode.off();
	$barcode.on('input', validateAddBarcode);
}
function validateAddQuantityUtil(){
	$quantity.off();
	$quantity.on('input', validateAddQuantity);
}
// Actual functions
function validateAddBarcode(){
	if($barcode.val().length == 0){
		if($barcode.hasClass('is-valid')){
			$barcode.removeClass('is-valid');
		}
		$barcode.addClass('is-invalid');
		$('#bvf1').attr('style', 'display:none;');
		$('#bif1').attr('style', 'display:block;');
		$('#bif2').attr('style', 'display:none;');
		$checkBarcode.attr('disabled', true);
	}
	else{
		if($barcode.hasClass('is-invalid')){
			$barcode.removeClass('is-invalid');
		}
		if($barcode.hasClass('is-valid')){
			$barcode.removeClass('is-valid');
		}
		$('#bvf1').attr('style', 'display:none;');
		$('#bif1').attr('style', 'display:none;');
		$('#bif2').attr('style', 'display:none;');
		$checkBarcode.attr('disabled', false);
	}
	enableOrDisableAdd();
}
function validateAddQuantity(){
	if($quantity.val().length == 0){
		$quantity.addClass('is-invalid');
		$('#qif1').attr('style', 'display:block;');
	}
	else{
		if($quantity.hasClass('is-invalid')){
			$quantity.removeClass('is-invalid');
		}
		$('#qif1').attr('style', 'display:none;');
	}
	enableOrDisableAdd();
}

// VALIDATION FUNCTIONS FOR EDIT INVENTORY 
// Edit form validation
function validateEditInventoryForm(){
	newData = {
		'quantity': $editQuantity.val()
	}
	if($editQuantity.val().length == 0){
		$editQuantity.addClass('is-invalid');
		$('#eqif1').attr('style', 'display:block;');
	}
	else{
		if($editQuantity.hasClass('is-invalid')){
			$editQuantity.removeClass('is-invalid');
		}
		$('#eqif1').attr('style', 'display:none;');
	}
	enableOrDisableEdit();
}

//CHECKING BARCODE AVAILABILITY
function checkBarcodeAvailability(){
	var barcode = $barcode.val();
	var url = getProductUrl() + '?barcode=' + barcode;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
			$checkBarcode.attr('disabled', true);
			barcodeAvailable(data);
	   },
	   error: function(data){
			$checkBarcode.attr('disabled', true);
			barcodeNotAvailable();
	   }
	});
	return false;
}

// BARCODE AVAILABILITY DISPLAYING FUNCTIONS
function barcodeAvailable(data){
		if($barcode.hasClass('is-invalid')){
			$barcode.removeClass('is-invalid');
		}
		$barcode.addClass('is-valid');
		$('#bvf1').html('Product name: '+data.name);
		$('#bif1').attr("style", "display:none;");
		$('#bif2').attr("style", "display:none;");
		$('#bvf1').attr('style', 'display:block;');
		enableOrDisableAdd();
}

function barcodeNotAvailable(){
	if($barcode.hasClass('is-valid')){
		$barcode.removeClass('is-valid');
	}
	$barcode.addClass("is-invalid");
	$('#bif2').attr("style", "display;block;");
	$('#bif1').attr('style', 'display:none;');
	$('#bvf1').attr('style', 'display:none;');
	enableOrDisableAdd();
}

//BUTTON ACTIONS
// Add inventory button
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);
	if(validator(json)){
		var url = getInventoryUrl();

		$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			handleAjaxSuccess("Inventory updated successfully");
			getInventoryListUtil(); 
			clearAddData();
		},
		error: function(response){
			handleAjaxError(response);
		}
		});
	}
	return false;
}
// Update inventory button
function updateInventory(event){
	$('#inventory-edit-form input[name=barcode]').attr('disabled', false);
	//Get the ProductID
	var productId = $("#inventory-edit-form input[name=productId]").val();
	var url = getInventoryUrl() + "/" + productId;

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);
	$('#inventory-edit-form input[name=barcode]').attr('disabled', true);
	if(validator(json)){
		$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			handleAjaxSuccess("Inventory updated successfully");
			getInventoryListUtil();   
			clearEditData();
		},
		error: function(response){
			handleAjaxError(response);
		}
		});
	}
	return false;
}

//ENABLE OR DISABLE FUNCTIONS
function enableOrDisableAdd(){
	var barcode = $barcode.val();
	var quantity = $quantity.val();
	if(barcode.length > 0 && quantity.length > 0
		&& !$barcode.hasClass('is-invalid')
		&& $barcode.hasClass('is-valid')
		&& !$quantity.hasClass('is-invalid')){
			$add.attr('disabled', false);
		}
	else{
		$add.attr('disabled', true);
	}
}

function enableOrDisableEdit(){
	if((oldData.quantity == newData.quantity)
	||
	($editQuantity.hasClass('is-invalid'))){
		$update.attr('disabled', true);
	}
	else{
		$update.attr('disabled', false);
	}
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

function isValid(uploadObject) {
	if(uploadObject.hasOwnProperty('barcode') &&
		uploadObject.hasOwnProperty('quantity') &&
		Object.keys(uploadObject).length==2){
			return true;
	}
	return false;
}

function processData(){
	var file = $('#inventoryFile')[0].files[0];
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
		handleAjaxSuccess("Uploaded completed successfully");
		getInventoryListUtil();
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getInventoryUrl();

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
			getInventoryListUtil();
	   },
	   error: function(response){
		var response = JSON.parse(response.responseText);
	   		row.error=response.message;
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
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Reset buttons
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
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#process-data').attr('disabled', false);
	$('#inventoryFileName').html(fileName.split('\\')[2]);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-inventory-modal').modal('toggle');
}

//RESETTING AND CLEARING FUNCTIONS
function clearCommentsAddInventory(){
	if($barcode.hasClass('is-valid')){
		$barcode.removeClass('is-valid');
	}
	if($barcode.hasClass('is-invalid')){
		$barcode.removeClass('is-invalid');
	}
	if($quantity.hasClass('is-invalid')){
		$quantity.removeClass('is-invalid');
	}
	$('#bvf1').attr('style', 'display:none;');
	$('#bif1').attr('style', 'display:none;');
	$('#bif2').attr('style', 'display:none;');
	$('#qif1').attr('style', 'display: none;');
}

function clearCommentsEditInventory(){
	if($editQuantity.hasClass('is-invalid')){
		$editQuantity.removeClass('is-invalid');
	}
	$('#eqif1').attr('style', 'display:none;');
}

function clearAddData(){
	$barcode.val('');
	$quantity.val('');
	$add.attr('disabled', true);
	$checkBarcode.attr('disabled', true);

	clearCommentsAddInventory();
	if($('#add-inventory-modal').hasClass('show')){
		$('#add-inventory-modal').modal('toggle');
	}
}

function clearEditData(){
	$('#inventory-edit-form input[name=barcode]').val('');
	$editQuantity.val('');
	$update.attr('disabled', true);

	clearCommentsEditInventory();
	if($('#edit-inventory-modal').hasClass('show')){
		$('#edit-inventory-modal').modal('toggle');
	}
}

function clearUploadData(){
	$('#rowCount').text('0');
	$('#processCount').text('0');
	$('#errorCount').text('0');
	$('#inventoryFile').val('');
	$('#upload-inventory-modal').modal('toggle');
	$('#process-data').attr('disabled', true);
	$('#download-errors').attr('disabled', true);
}

// ADD MODAL TOGGLER
function displayAddModal(){
	$('#add-inventory-modal').modal('toggle');
}
//INITIALIZATION CODE
function init(){
	$('#cancel1').click(clearAddData);
	$('#cancel2').click(clearAddData);
	$('#cancel3').click(clearEditData);
	$('#cancel4').click(clearEditData);
	$('#cancel5').click(clearUploadData);
	$('#cancel6').click(clearUploadData);
	$('#add-data').click(displayAddModal);
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName);
	$('#inputPageSize').on('change', getInventoryListUtil);
	$('#check-barcode').click(checkBarcodeAvailability);

	$editQuantity.on('input', validateEditInventoryForm);
}

$(document).ready(init);
$(document).ready(getInventoryListUtil);
$(document).ready(enableOrDisable);