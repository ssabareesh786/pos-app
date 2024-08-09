// GLOBAL VARIABLES
// For add product
var $barcode = $('#product-form input[name=barcode]');
var $brand = $('#product-form input[name=brand]');
var $category = $('#product-form input[name=category]');
var $name = $('#product-form input[name=name]');
var $mrp = $('#product-form input[name=mrp]');
var $add = $('#add-product');
var $checkBarcode = $('#check-barcode');
// For edit product
var $editBarcode = $('#product-edit-form input[name=barcode]');
var $editBrand = $('#product-edit-form input[name=brand]');
var $editCategory = $('#product-edit-form input[name=category]');
var $editName = $('#product-edit-form input[name=name]');
var $editMrp = $('#product-edit-form input[name=mrp]');
var $update = $('#update-product');
var $checkBarcodeEdit = $('#check-barcode-edit');
// For tracking edit data
var oldData = null;
var newData = null;

// PRODUCT URL FUNCTION
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

// GET AND DISPLAY PRODUCT LIST FUNCTIONS
// Get functions
function getProductListUtil(){
	var pageSize = $('#inputPageSize').val();
	getProductList(0, pageSize);
}

function getProductList(pageNumber, pageSize){
	var url = getProductUrl() + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
	   		displayProductList(data.content, pageNumber*pageSize);
			$('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			paginator(data, "getProductList", pageSize);
	   },
	   error: function(response){
		handleAjaxError(response);
	   }
	});
}
// UI Display functions
function displayProductList(data, sno){
	$("#product-table-body").empty();
    var row = "";
	for (var i = 0; i < data.length; i++) {
	sno += 1;
	var buttonHtml = spanBegin + ' <button onclick="displayEditProduct(' + data[i].id + ')" class="btn btn-secondary only-supervisor">edit</button>' + spanEnd;
	row = "<tr><td>" 
	+ sno + "</td><td>" 
	+ data[i].barcode + "</td><td>"
	+ data[i].brand + "</td><td>"
	+ data[i].category + "</td><td>"
	+ data[i].name + "</td><td>" 
	+ data[i].mrp + "</td><td>"
	+ buttonHtml 
	+ "</td></tr>";
	$("#product-table-body").append(row);
	}
	enableOrDisable();
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);
	   },
	   error: handleAjaxError
	});	
}

function displayProduct(data){
	$("#product-edit-form input[name=id]").val(data.id)
	$("#product-edit-form input[name=barcode]").val(data.barcode);	
	$("#product-edit-form input[name=brand]").val(data.brand);
	$("#product-edit-form input[name=category]").val(data.category);
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$('#edit-product-modal').modal('toggle');

	$update.attr('disabled', true);
	oldData = {
		'barcode' : data.barcode,
		'brand' : data.brand,
		'category': data.category,
		'name': data.name,
		'mrp': data.mrp
	};
	newData = {
		'barcode' : data.barcode,
		'brand' : data.brand,
		'category': data.category,
		'name': data.name,
		'mrp': data.mrp
	};
}

// VALIDATION FUNCTIONS FOR ADD PRODUCT
// 1) Utils
function validateAddBarcodeUtil(){
	$barcode.off();
	$barcode.on('input', validateAddBarcode);
}

function validateAddBrandUtil(){
	$brand.off();
	$brand.on('input', validateAddBrand);
}

function validateAddCategoryUtil(){
	$category.off();
	$category.on('input', validateAddCategory);
}

function validateAddNameUtil(){
	$name.off();
	$name.on('input', validateAddProductName);
}

function validateAddMrpUtil(){
	$mrp.off();
	$mrp.on('input', validateAddMrp);
}
// 2) Actual functions
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

function validateAddBrand(){
	if($brand.val().length == 0){
		$brand.addClass('is-invalid');
		$('#brif1').attr('style', 'display:block;');
	}
	else{
		if($brand.hasClass('is-invalid')){
			$brand.removeClass('is-invalid');
		}
		$('#brif1').attr('style', 'display:none;');
	}
	enableOrDisableAdd();
}

function validateAddCategory(){
	if($category.val().length == 0){
		$category.addClass('is-invalid');
		$('#cif1').attr('style', 'display:block;');
	}
	else{
		if($category.hasClass('is-invalid')){
			$category.removeClass('is-invalid');
		}
		$('#cif1').attr('style', 'display:none;')
	}
	enableOrDisableAdd();
}

function validateAddProductName(){
	if($name.val().length == 0){
		$name.addClass('is-invalid');
		$('#pnif1').attr('style', 'display:block;');
	}
	else{
		if($name.hasClass('is-invalid')){
			$name.removeClass('is-invalid');
		}
		$('#pnif1').attr('style', 'display:none;')
	}
	enableOrDisableAdd();
}

function validateAddMrp(){
	if($mrp.val().length == 0){
		$mrp.addClass('is-invalid');
		$('#mrpif1').attr('style', 'display:block;');
	}
	else{
		if($mrp.hasClass('is-invalid')){
			$mrp.removeClass('is-invalid');
		}
		$('#mrpif1').attr('style', 'display:none;')
	}
	enableOrDisableAdd();
}

function validateAddProductForm(){
	if($brand.val().length == 0){
		$brand.addClass('is-invalid');
		$('#brif1').attr('style', 'display:block;');
	}
	else{
		if($brand.hasClass('is-invalid')){
			$brand.removeClass('is-invalid');
		}
		$('#brif1').attr('style', 'display:none;');
	}

	if($category.val().length == 0){
		$category.addClass('is-invalid');
		$('#cif1').attr('style', 'display:block;');
	}
	else{
		if($category.hasClass('is-invalid')){
			$category.removeClass('is-invalid');
		}
		$('#cif1').attr('style', 'display:none;');
	}

	if($name.val().length == 0){
		$name.addClass('is-invalid');
		$('#pnif1').attr('style', 'display:block;');
	}
	else{
		if($name.hasClass('is-invalid')){
			$name.removeClass('is-invalid');
		}
		$('#pnif1').attr('style', 'display:none;');
	}

	if($mrp.val().length == 0){
		$mrp.addClass('is-invalid');
		$('#mrpif1').attr('style', 'display:block;');
	}
	else{
		if($mrp.hasClass('is-invalid')){
			$mrp.removeClass('is-invalid');
		}
		$('#mrpif1').attr('style', 'display:none;');
	}
	enableOrDisableAdd();
}

// VALIDATION FUNCTIONS FOR EDIT PRODUCT
// Edit form validation
function validateEditProductForm(){
	console.log("validate edit product form");
	newData = {
		'barcode': $editBarcode.val(),
		'brand': $editBrand.val(),
		'category': $editCategory.val(),
		'name': $editName.val(),
		'mrp': $editMrp.val()
	};
	if($editBrand.val().length == 0){
		$editBrand.addClass('is-invalid');
		$('#ebrif1').attr('style', 'display:block;');
	}
	else{
		if($editBrand.hasClass('is-invalid')){
			$editBrand.removeClass('is-invalid');
		}
		$('#ebrif1').attr('style', 'display:none;');
	}

	if($editCategory.val().length == 0){
		$editCategory.addClass('is-invalid');
		$('#ecif1').attr('style', 'display:block;');
	}
	else{
		if($editCategory.hasClass('is-invalid')){
			$editCategory.removeClass('is-invalid');
		}
		$('#ecif1').attr('style', 'display:none;')
	}

	if($editName.val() == 0){
		$editName.addClass('is-invalid');
		$('#enif1').attr('style', 'display: block;');
	}
	else{
		if($editName.hasClass('is-invalid')){
			$editName.removeClass('is-invalid');
		}
		$('#enif1').attr('style', 'display: none;')
	}

	if($editMrp.val().length == 0){
		$editMrp.addClass('is-invalid');
		$('#emrpif1').attr('style', 'display: block;')
	}
	else{
		if($editMrp.hasClass('is-invalid')){
			$editMrp.removeClass('is-invalid');
		}
		$('#emrpif1').attr('style', 'display: none;');
	}
	enableOrDisableEdit();
}
// Barcode validation
function validateEditBarcode(){
	
	newData = {
		'barcode': $editBarcode.val(),
		'brand': $editBrand.val(),
		'category': $editCategory.val(),
		'name': $editName.val(),
		'mrp': $editMrp.val()
	};

	if($editBarcode.val().length == 0){
		if($editBarcode.hasClass('is-valid')){
			$editBarcode.removeClass('is-valid');
		}
		$editBarcode.addClass('is-invalid');
		$('#ebvf1').attr('style', 'display:none;');
		$('#ebif1').attr('style', 'display:block;');
		$('#ebif2').attr('style', 'display:none;');
		$checkBarcodeEdit.attr('disabled', true);
	}
	else{
		if($editBarcode.hasClass('is-invalid')){
			$editBarcode.removeClass('is-invalid');
		}
		$('#ebvf1').attr('style', 'display:none;');
		$('#ebif1').attr('style', 'display:none;');
		$('#ebif2').attr('style', 'display:none;');
		if($editBarcode.val() == oldData.barcode){
			$checkBarcodeEdit.attr('disabled', true);
		}
		else{
			$checkBarcodeEdit.attr('disabled', false);
		}
	}
	enableOrDisableEdit();
}

//CHECKING BARCODE AVAILABILITY
function checkBarcodeAvailability(){
	var barcode = null;
	if($('#add-product-modal').hasClass('show')){
		console.log('add barcode open');
		barcode = $barcode.val();
	}
	else{
		console.log('edit barcode open');
		barcode = $editBarcode.val();
		if(oldData.barcode == barcode){
			clearCommentsEditProduct();
			return;
		}
		
	}
	var url = getProductUrl() + '?barcode=' + barcode;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
			if($('#add-product-modal').hasClass('show')){
				$checkBarcode.attr('disabled', true);	
			}
			else{
				$checkBarcodeEdit.attr('disabled', true);
			}
			barcodeNotAvailable();
			console.log('Barcode is there->');
	   },
	   error: function(data){
			if($('#add-product-modal').hasClass('show')){
				$checkBarcode.attr('disabled', true);
			}
			else{
				$checkBarcodeEdit.attr('disabled', true);
			}
			barcodeAvailable();
			console.log('Barcode is not there->');
	   }
	});
	return false;
}

// BARCODE AVAILABILITY DISPLAYING FUNCTIONS
function barcodeAvailable(){
	if($('#add-product-modal').hasClass('show')){
		if($barcode.hasClass('is-invalid')){
			$barcode.removeClass('is-invalid');
		}
		$barcode.addClass('is-valid');
		$('#bif1').attr("style", "display:none;");
		$('#bif2').attr("style", "display:none;");
		$('#bvf1').attr('style', 'display:block;');
		enableOrDisableAdd();
	}
	else{
		if($editBarcode.hasClass('is-invalid')){
			$editBarcode.removeClass('is-invalid');
		}
		$editBarcode.addClass('is-valid');
		$('#ebvf1').attr('style', 'display:block;');
		$('#ebif1').attr('style', 'display: none;');
		$('#ebif2').attr('style', 'display: none;');
		enableOrDisableEdit();
	}
}

function barcodeNotAvailable(){
	if($('#add-product-modal').hasClass('show')){
		console.log('Add product modal -->');
		if($barcode.hasClass('is-valid')){
			$barcode.removeClass('is-valid');
		}
		$barcode.addClass("is-invalid");
		$('#bif2').attr("style", "display;block;");
		$('#bif1').attr('style', 'display:none;');
		$('#bvf1').attr('style', 'display:none;');
		enableOrDisableAdd();
	}
	else{
		console.log('Edit product modal -->');
		if($editBarcode.hasClass('is-valid')){
			$editBarcode.removeClass('is-valid');
		}
		$editBarcode.addClass('is-invalid');
		$('#ebif2').attr('style', 'display: block;');
		$('#ebif1').attr('style', 'display: none;');
		$('#ebvf1').attr('style', 'display: none;');
		enableOrDisableEdit();
	}
}

//BUTTON ACTIONS
// Add product button
function addProduct(event){
	//Set the values to update
	var $form = $("#product-form");
	var json = toJson($form);
	if(validator(json)){
		var url = getProductUrl();

		$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(response) {
			handleAjaxSuccess("Product added successfully");
			getProductListUtil();
			clearAddData();
		},
		error: function(response){
			handleAjaxError(response);
			$add.attr('disabled', true);
		}
		});
	}
	return false;
}
// Update product button
function updateProduct(event){
	console.log('Update button clicked');
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;
	console.log(url)

	//Set the values to update
	var $form = $("#product-edit-form");
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
			handleAjaxSuccess("Product updated successfully!!!");
			getProductListUtil();
			clearEditData();
		},
		error: function(response){
			handleAjaxError(response);
		}
		});
	}
	return false;
}

// ENABLE OR DISABLE FUNCTIONS
function enableOrDisableAdd(){
	var barcode = $barcode.val();
	var brand = $brand.val();
	var category = $category.val();
	var name = $name.val();
	var mrp = $mrp.val();
	if(barcode.length > 0 && brand.length > 0 && category.length > 0
		&& name.length > 0 && mrp.length > 0 && !$barcode.hasClass('is-invalid') 
		&& !$brand.hasClass('is-invalid') && !$category.hasClass('is-invalid')
		&& !$name.hasClass('is-invalid') && !$mrp.hasClass('is-invalid')
		&& $barcode.hasClass('is-valid')){
			$add.attr('disabled', false);
		}
	else{
		$add.attr('disabled', true);
	}
}

function enableOrDisableEdit(){

	if((oldData.barcode == newData.barcode 
		&& oldData.brand == newData.brand
		&& oldData.category == newData.category
		&& oldData.name == newData.name
		&& oldData.mrp == newData.mrp 
		)
		|| 
		($editBarcode.hasClass('is-invalid') 
		|| $editBrand.hasClass('is-invalid')
		|| $editCategory.hasClass('is-invalid')
		|| $editName.hasClass('is-invalid')
		|| $editMrp.hasClass('is-invalid'))
		){
		$update.attr('disabled', true);
		console.log("branch 1");
	}
	else if(oldData.barcode != newData.barcode && !$editBarcode.hasClass('is-valid') ){
		console.log("Branch 2");
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
		uploadObject.hasOwnProperty('brand') &&
		uploadObject.hasOwnProperty('category') &&
		uploadObject.hasOwnProperty('name') &&
		uploadObject.hasOwnProperty('mrp') &&
		Object.keys(uploadObject).length==5){
			return true;
	}
	return false;
}

function processData(){
	var file = $('#productFile')[0].files[0];
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
		getProductListUtil();
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getProductUrl();

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
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
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
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#process-data').attr('disabled', false);
	$('#productFileName').html(fileName.split('\\')[2]);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-product-modal').modal('toggle');
}

// RESETTING AND CLEARING FUNCTIONS
function clearCommentsAddProduct(){
	if($barcode.hasClass('is-valid')){
		$barcode.removeClass('is-valid');
	}
	if($barcode.hasClass('is-invalid')){
		$barcode.removeClass('is-invalid');
	}
	if($brand.hasClass('is-invalid')){
		$brand.removeClass('is-invalid');
	}
	if($category.hasClass('is-invalid')){
		$category.removeClass('is-invalid');
	}
	if($name.hasClass('is-invalid')){
		$name.removeClass('is-invalid');
	}
	if($mrp.hasClass('is-invalid')){
		$mrp.removeClass('is-invalid');
	}
	$('#bvf1').attr('style', 'display:none;');
	$('#bif1').attr('style', 'display:none;');
	$('#bif2').attr('style', 'display:none;');
	$('#brif1').attr('style', 'display:none;');
	$('#cif1').attr('style', 'display:none;');
	$('#pnif1').attr('style', 'display:none;');
	$('#mrpif1').attr('style', 'display:none;');
}

function clearCommentsEditProduct(){
	if($editBarcode.hasClass('is-valid')){
		$editBarcode.removeClass('is-valid');
	}
	if($editBarcode.hasClass('is-invalid')){
		$editBarcode.removeClass('is-invalid');
	}
	if($editBrand.hasClass('is-invalid')){
		$editBrand.removeClass('is-invalid');
	}
	if($editCategory.hasClass('is-invalid')){
		$editCategory.removeClass('is-invalid');
	}
	if($editName.hasClass('is-invalid')){
		$editName.removeClass('is-invalid');
	}
	if($editMrp.hasClass('is-invalid')){
		$editMrp.removeClass('is-invalid');
	}
	$('#ebvf1').attr('style', 'display:none;');
	$('#ebif1').attr('style', 'display:none;');
	$('#ebif2').attr('style', 'display:none;');
	$('#ebrif1').attr('style', 'display:none;');
	$('#ecif1').attr('style', 'display:none;');
	$('#enif1').attr('style', 'display:none;');
	$('#emrpif1').attr('style', 'display:none;');
}

function clearAddData(){
	$barcode.val('');
	$brand.val('');
	$category.val('');
	$name.val('');
	$mrp.val('');
	$add.attr('disabled', true);
	$checkBarcode.attr('disabled', true);

	clearCommentsAddProduct();
	if($('#add-product-modal').hasClass('show')){
		$('#add-product-modal').modal('toggle');
	}
}

function clearEditData(){
	$editBarcode.val('');
	$editBrand.val('');
	$editCategory.val('');
	$editName.val('');
	$editMrp.val('');
	$update.attr('disabled', true);
	$checkBarcodeEdit.attr('disabled', true);

	clearCommentsEditProduct();

	if($('#edit-product-modal').hasClass('show')){
		$('#edit-product-modal').modal('toggle');
	}
}

function clearUploadData(){
	$('#rowCount').text('0');
	$('#processCount').text('0');
	$('#errorCount').text('0');
	$('#productFile').val('');
	$('#upload-product-modal').modal('toggle');
	$('#process-data').attr('disabled', true);
	$('#download-errors').attr('disabled', true);
	if($('#upload-brand-modal').hasClass('show'))
	$('#upload-brand-modal').modal('toggle');
}

// ADD MODAL TOGGLER
function displayAddModal(){
	$('#add-product-modal').modal('toggle');
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
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
	$('#inputPageSize').on('change', getProductListUtil);
	$('#check-barcode').click(checkBarcodeAvailability);
	$('#check-barcode-edit').click(checkBarcodeAvailability);

	$editBarcode.on('input', validateEditBarcode);
	$editBrand.on('input', validateEditProductForm);
	$editCategory.on('input', validateEditProductForm);
	$editName.on('input', validateEditProductForm);
	$editMrp.on('input', validateEditProductForm);
}

$(document).ready(init);
$(document).ready(getProductListUtil);
$(document).ready(enableOrDisable);
$(document).ready(function(){
});