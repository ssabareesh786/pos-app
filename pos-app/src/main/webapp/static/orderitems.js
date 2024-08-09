//GLOBAL VARIABLES
var quantityVF = 'Available quantity: ';
var quantityIVF = 'Quantity cannot exceed ';
var sellingPriceVF = 'MRP: ';
var sellingPriceIVF = 'Selling price is greater than MRP. MRP: ';

var canAddActionCol = true;
var $editBarcode = $("#edit-order-item-form input[name=barcode]");
var $editQuantity = $('#edit-order-item-form input[name=quantity]');
var $editSellingPrice = $('#edit-order-item-form input[name=sellingPrice]');
var $editModal = $('#edit-order-item-modal');
var $update = $('#update-order-item');

var oldData = null;
var newData = null;

var dataOfOrderItem = null;


function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getOrderItemsUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/order-items";
}

function getOrderId(){
	var orderId = $("meta[name=orderId]").attr("content");
	return orderId;
}

function getMode(){
	var mode = $("meta[name=mode]").attr("content");
	return mode;
}

function getOrderItemsUtil(){
	var pageSize = $('#inputPageSize').val();
	getOrderItems(0, pageSize);
}

function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

function getOrderItems(pageNumber, pageSize){
	var url = getOrderItemsUrl() + "?order-id=" + getOrderId() + '&page-number=' + pageNumber + '&page-size=' + pageSize;
	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
			displayOrderItems(data.content, pageNumber*pageSize);
			$('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			var pagination = "";
			   for (var i = data.number; i < data.number + 3 && i < data.totalPages; i++) {
				   var active = "";
				   if (i == data.number) {
				   active = "active";
				   }
				   pagination += "<li class='page-item " + active + "'><a class='page-link' href='#pageNumber=" + (i+1) +"' onclick='getOrderItems(" + i + ", " + pageSize + ")'>" + (i + 1) + "</a></li>";
			   }
			   if (data.number > 0) {
				   pagination = "<li class='page-item'><a class='page-link' href='#pageNumber=" + data.number +"' id='previous'>Previous</a></li>" + pagination;
			   }
			   if (data.number < data.totalPages - 1) {
				   pagination = pagination + "<li class='page-item'><a class='page-link' href='#pageNumber=" + (data.number + 2) + "' id='next'>Next</a></li>";
			   }
			   $("#paginationContainer").html(pagination);
			   $("#previous").click(function() {
				   getOrderItems(data.number - 1, pageSize);
			   });
			   $("#next").click(function() {
				   getOrderItems(data.number + 1, pageSize);
			   }); 
	   },
	   error: handleAjaxError
	});	
}

function getProductForEdit(barcode){
	console.log('Barcode->>'+barcode);
	var url = getProductUrl() + '?barcode=' + barcode + '&inventory-status=' + true;
	console.log("URL: "+url);

	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
			dataOfOrderItem = data;
			clearCommentsOrderEditForm();
			setData();
	   },
	   error: function(response){
			dataOfOrderItem = null;
			console.log("error");
	   }
	});
}

// VALIDATION FUNCTIONS
function validateEditForm(){
	var quantity = $editQuantity.val();
	var sp = $editSellingPrice.val();
	newData = {
		'quantity': quantity,
		'sellingPrice': sp
	}
	
	if(quantity == ''){
		setItemQuantityInvalid('Please enter quantity');
	}
	else if(parseInt(quantity) > dataOfOrderItem.quantity + oldData.quantity){
		setItemQuantityInvalid(quantityIVF + (dataOfOrderItem.quantity + oldData.quantity));
	}
	else if(parseInt(quantity) <= 0){
		setItemQuantityInvalid('Quantity must be greater than zero');
	}
	else{
		setItemQuantityValid();
	}

	if(sp == ''){
		setItemSellingPriceInvalid('Please enter selling price');
	}
	else if(parseFloat(sp) > dataOfOrderItem.mrp){
		setItemSellingPriceInvalid(sellingPriceIVF + dataOfOrderItem.mrp);
	}
	else if(parseFloat(sp) <= 0){
		setItemSellingPriceInvalid('Selling price must be greater than zero');
	}
	else{
		setItemSellingPriceValid();
	}
	enableAddOrEdit();
}

function enableAddOrEdit(){
	if((oldData.quantity == newData.quantity && oldData.sellingPrice == newData.sellingPrice) || (!$editQuantity.hasClass('is-valid') || !$editSellingPrice.hasClass('is-valid'))){
		$update.attr('disabled', true);
	}
	else{
		$update.attr('disabled', false);
	}
}

function setItemQuantityInvalid(message){
	if($editQuantity.hasClass('is-valid')){
		$editQuantity.removeClass('is-valid');
	}
	$editQuantity.addClass('is-invalid');
	$('#eqvf').attr('style', 'display: none;');
	$('#eqif').html(message);
	$('#eqif').attr('style', 'display: block;');
}

function setItemSellingPriceInvalid(message){
	if($editSellingPrice.hasClass('is-valid')){
		$editSellingPrice.removeClass('is-valid');
	}
	$editSellingPrice.addClass('is-invalid');
	$('#espvf').attr('style', 'display: none;');
	$('#espif').html(message);
	$('#espif').attr('style', 'display: block;');
}

//UI DISPLAY METHODS

function displayOrderItems(data, sno){
	$('#order-items-table-body').empty();
	if(getMode() == 'edit' && canAddActionCol){
		$('#order-items-table-head').append('<th scope="col">Actions</th>');
		canAddActionCol = false;
	}
	var total = 0;
	var row = '';
	for(var i = 0; i < data.length; i++){
		total = parseFloat(parseFloat(data[i].sellingPrice) * parseInt(data[i].quantity)).toFixed(2);
		var buttonHtml = spanBegin + 
		' <button onclick="displayEditOrderItem(' 
		+ data[i].id 
		+ ')" class="btn btn-secondary only-supervisor">Edit</button>' 
		+ spanEnd
		+ '&nbsp;'
		+ spanBegin 
		+ '<button onclick="deleteOrderItem('
		+ data[i].id
		+ ')" class="btn btn-secondary only-supervisor">Delete</button>';
		sno += 1;
		row = '<tr><td>' + sno + '</td>'
		+ '<td>' + data[i].barcode + '</td>'
		+ '<td>' + data[i].productName + '</td>'
		+ '<td>' + data[i].quantity + '</td>'
		+ '<td>' + parseFloat(data[i].sellingPrice).toFixed(2) + '</td>'
		+ '<td>' + data[i].mrp + '</td>'
		+ '<td>' + total + '</td>';
		if(getMode() == 'edit'){
			row += '<td>' + buttonHtml + '</td></tr>';
		}
		else{
			row += '</tr>';
		}
		$('#order-items-table-body').append(row);
	}
	$('#total').html(total);
	$('#total').attr('style', 'font-weight: bold;');
}

function deleteOrderItem(id){
	var url = getOrderItemsUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
			handleAjaxSuccess("Deleted successfully");
			getOrderItemsUtil();
	   },
	   error: function(response){
			handleAjaxError(response);
	   }
	});	
}

function offAll(){
	$editBarcode.off();
	$editQuantity.off();
	$editSellingPrice.off();
}
function displayEditOrderItem(id){
	var url = getOrderItemsUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
		oldData = data;
		executeInOrder(data);
	   },
	   error: function(response){
			handleAjaxError(response);
	   }
	});	
}

function executeInOrder(data){
	console.log(data);
	offAll();
	getProductForEdit(data.barcode);
	displayOrderItem(data);
	$editBarcode.attr('disabled', true);
	$editQuantity.on('input', validateEditForm);
	$editSellingPrice.on('input', validateEditForm);
	console.log("First");
	if(dataOfOrderItem != null){
		setData();
		return;
	}
}

function setItemQuantityValid(){
	if($editQuantity.hasClass('is-invalid')){
		$editQuantity.removeClass('is-invalid');
	}
	$('#eqif').attr('style', 'display:none;');
	$editQuantity.addClass('is-valid');
	$('#eqvf').attr('style', 'display: block;');
	if(dataOfOrderItem != null){
		$('#eqvf').html(quantityVF + (dataOfOrderItem.quantity + oldData.quantity));
	}
}

function setData(){
	setItemQuantityValid();
	setItemSellingPriceValid();
}

function setItemSellingPriceValid(){
	if($editSellingPrice.hasClass('is-invalid')){
		$editSellingPrice.removeClass('is-invalid');
	}
	$('#espif').attr('style', 'display: none');
	$editSellingPrice.addClass('is-valid');
	$('#espvf').attr('style', 'display: block;');
	$('#espvf').html(sellingPriceVF + dataOfOrderItem.mrp);
}

function displayOrderItem(data){
	$editBarcode.attr('disabled', false);
	$editBarcode.val(data.barcode);	
	$editQuantity.val(data.quantity);
	$editSellingPrice.val(parseFloat(data.sellingPrice).toFixed(2));
	$("#edit-order-item-form input[name=id]").val(data.id);
	$update.attr('disabled', true);
	$editModal.modal('toggle');
}

function updateOrderItem(){
	$editBarcode.attr('disabled', false);
	var id = $("#edit-order-item-form input[name=id]").val();

	var url = getOrderItemsUrl() + "/edit/" + id;


	//Set the values to update
	var $form = $("#edit-order-item-form");
	var json = toJson($form);
	console.log(json);
	if(validator(json)){
		$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function(response) {
			handleAjaxSuccess("Updated Successfully");
			canAddActionCol = false;
			getOrderItemsUtil();
			$('#edit-order-item-modal').modal('toggle');
		},
		error: function(response){
			handleAjaxError(response);
		}
		});
	}
	return false;
}

function clearCommentsOrderEditForm(){
	if($editBarcode.hasClass('is-valid')){
		$editBarcode.removeClass('is-valid');
	}
	if($editBarcode.hasClass('is-invalid')){
		$editBarcode.removeClass('is-invalid');
	}
	if($editQuantity.hasClass('is-valid')){
		$editQuantity.removeClass('is-valid');
	}
	if($editQuantity.hasClass('is-invalid')){
		$editQuantity.removeClass('is-invalid');
	}
	if($editSellingPrice.hasClass('is-valid')){
		$editSellingPrice.removeClass('is-valid');
	}
	if($editSellingPrice.hasClass('is-invalid')){
		$editSellingPrice.removeClass('is-invalid');
	}

	$('#eqvf').html('');
	$('#eqvf').attr('style', 'display: none;');
	$('#eqif').html('');
	$('#eqif').attr('style', 'display: none;');
	$('#espvf').html('');
	$('#espvf').attr('style', 'display: none;');
	$('#espif').html('');
	$('#espif').attr('style', 'display: none;');
}

function closeModal(){
	$editBarcode.val('');
	$editQuantity.val('');
	$editSellingPrice.val('');
	clearCommentsOrderEditForm();
	if($editModal.hasClass('show')){
		$editModal.modal('toggle');
	}
}

//INITIALIZATION CODE
function init(){
	$('#cancel1').click(closeModal);
	$('#cancel2').click(closeModal);
	$('#update-order-item').click(updateOrderItem);
	$('#inputPageSize').on('change', getOrderItemsUtil);
	$editQuantity.on('input', validateEditForm);
	$editSellingPrice.on('input', validateEditForm);
}

$(document).ready(init);
$(document).ready(getOrderItemsUtil);