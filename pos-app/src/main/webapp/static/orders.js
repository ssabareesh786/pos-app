//Global variables
var dataOfItem = null;
var dataOfItemForEdit = null;
var dataOfItemForEditOld = null;

var barcodes = [];
var quantities = [];
var availableQuantities = [];
var sellingPrices = [];
var names = [];
var totals = [];
var mrps = [];
var barcodeSet = new Set();

var $barcode = $('#place-order-form input[name=barcode]');
var $quantity = $('#place-order-form input[name=quantity]');
var $sp = $('#place-order-form input[name=sellingPrice]');

// General functions
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/orders";
}


function getInvoiceUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/invoice/download";
}
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

function getOrderListUtil(){
	var pageSize = $('#inputPageSize').val();
	getOrderList(0, pageSize);
}

function getOrderList(pageNumber, pageSize){
	var url = getOrderUrl()  + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
	   		displayOrderList(data.content, pageNumber*pageSize);
			   $('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			   paginator(data, "getOrderList", pageSize);
	   },
	   error: function(){
			handleAjaxError("Can't obtain the data");
	   }
	});
	return false;
}

function updateAddedItemsTable(){
	$('#added-items').empty();
	var sno = 0;
	for(var i=0; i < barcodes.length; i++){
		sno += 1;
		var buttonHtml = '<button onclick="editAddedItem(' + i + ')" class="btn btn-secondary">Edit</button>'
		+ '&nbsp;<button onclick="deleteAddedItem(' + i + ')" class="btn btn-secondary">Delete</button>';
		var row = "<tr><td>"
		+ sno + "</td><td>"
		+ barcodes[i] + "</td><td>"
		+ names[i] + "</td><td>"
		+ quantities[i] + "</td><td>"
		+ parseFloat(sellingPrices[i]).toFixed(2) + "</td><td>"
		+ parseFloat(totals[i]).toFixed(2) + "</td><td>"
		+ buttonHtml + "</td></tr>";
		$('#added-items').append(row);
	}
	placeOrderEnableDisable();
}

function displayOrderModal(){
	$('#add-item').attr("disabled", true);
	clearAll();
	updateAddedItemsTable();
}
// <-----------------------Product details getting functions-------------------------------->
function getProduct(){
	var barcode = $('#place-order-form input[name=barcode]').val();
	var url = getProductUrl() + '?barcode=' + barcode + '&inventory-status=' + true;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
			dataOfItem = data;
			$('#bif1').attr("style", "display:none;");
			$('#bif2').attr("style", "display:none;");
			$('#bif3').attr("style", "display:none;");
			$('#searchForBarcode').attr('disabled', true);
			validateItemRequest(data);
	   },
	   error: function(data){
			dataOfItem = null;
			$barcode.addClass("is-invalid");
			$('#searchForBarcode').attr('disabled', true);
			$('#bif2').attr("style", "display;block;");
	   }
	});
	return false;
}

function removeComments(){

	if($barcode.hasClass('is-valid'))
		$barcode.removeClass('is-valid');
	if($barcode.hasClass('is-invalid'))
		$barcode.removeClass('is-invalid');
	if($quantity.hasClass('is-valid'))
		$quantity.removeClass('is-valid');
	if($quantity.hasClass('is-invalid'))
		$quantity.removeClass('is-invalid');
	if($sp.hasClass('is-valid'))
		$sp.removeClass('is-valid');
	if($sp.hasClass('is-invalid'))
		$sp.removeClass('is-invalid');
	
	$('#bif1').attr("style", "display:none;");
	$('#bif2').attr("style", "display:none;");
	$('#bif3').attr("style", "display:none;");
	$('#bvf1').attr("style", "display:none;");

	$('#qif1').attr("style", "display:none;");
	$('#qif2').attr("style", "display:none;");
	$('#qvf1').attr("style", "display:none;");

	$('#spif1').attr("style", "display:none;");
	$('#spif2').attr("style", "display:none;");
	$('#spvf1').attr("style", "display:none;");
}

function loadOriginal(){
	$quantity.val(dataOfItemForEditOld.quantity);
	$sp.val(dataOfItemForEditOld.sellingPrice);
}

function getProductForEdit(){
	var barcode = $barcode.val().toLowerCase();
	if(dataOfItemForEditOld.barcode == barcode){
		loadOriginal();
		removeComments();
		enableOrDisableAddOrEdit();
		dataOfItemForEdit = dataOfItemForEditOld;
		validateItemRequest(dataOfItemForEditOld);
		// $quantity.val();
		return false;
	}
	var url = getProductUrl() + '?barcode=' + barcode + '&inventory-status=' + true;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
			dataOfItemForEdit = data;
			$('#bif1').attr("style", "display:none;");
			$('#bif2').attr("style", "display:none;");
			$('#bif3').attr("style", "display:none;");
			validateItemRequest(data);
	   },
	   error: function(response){
			dataOfItemForEdit = null;
			$barcode.addClass("is-invalid");
			$('#bif2').attr("style", "display;block;");
			resetToDefaults();
	   }
	});
	return false;
}

// <-----------------------------------Setting values functions ---------------------------->
function setBarcodeInvalid(){
	if($barcode.hasClass('is-valid')){
		$barcode.removeClass('is-valid');
	}
	$('#bvf1').attr("style", "display:none;");
	$barcode.addClass('is-invalid');
}

function setBarcodeValid(){
	if($barcode.hasClass('is-invalid')){
		$barcode.removeClass('is-invalid');
	}
	$('#bif1').attr("style", "display:none;");
	$('#bif2').attr("style", "display:none;");
	$('#bif3').attr("style", "display:none;");
	$('#bvf1').attr("style", "display:block;");
	$barcode.addClass('is-valid');
}

function setQuantityInvalid(){
	if($quantity.hasClass('is-valid'))
		$quantity.removeClass('is-valid');
	$('#qvf1').attr("style", "display:none;");
	$quantity.addClass('is-invalid');
}

function setQuantityValid(){
	if($quantity.hasClass('is-invalid'))
		$quantity.removeClass('is-invalid');
	$('#qif1').attr("style", "display:none;");
	$('#qif2').attr("style", "display:none;");
	$quantity.addClass('is-valid');
	$('#qvf1').attr("style", "display:block;");
	$quantity.attr('readonly', false);
}
function setSellingPriceInvalid(){
	if($sp.hasClass('is-valid'))
		$sp.removeClass('is-valid');
	$('#spvf1').attr("style", "display:none;");
	$sp.addClass('is-invalid');
}
function setSellingPriceValid(){
	if($sp.hasClass('is-invalid'))
		$sp.removeClass('is-invalid');
	$('#spif1').attr("style", "display:none;");
	$('#spif2').attr("style", "display:none;");
	$sp.addClass('is-valid');
	$('#spvf1').attr("style", "display:block;");
	$sp.attr('readonly', false);
}

// <---------------------------------------Resetting functions -------------------------------->


function resetToDefaults(){
	dataOfItem = null;
	dataOfItemForEdit = null;

	if($barcode.hasClass('is-valid'))
		$barcode.removeClass('is-valid');
	$('#bvf1').attr("style", "display:none;");

	if($barcode.hasClass('is-invalid'))
		$barcode.removeClass('is-invalid');
	$('#bif1').attr("style", "display:none;");
	$('#bif2').attr("style", "display:none;");
	$('#bif3').attr("style", "display:none;");
	
	if($quantity.hasClass('is-valid'))
		$quantity.removeClass('is-valid');
	$('#qvf1').attr("style", "display:none;");
	
	if($quantity.hasClass('is-invalid'))
		$quantity.removeClass('is-invalid');
	$('#qif1').attr("style", "display:none;");
	$('#qif2').attr("style", "display:none;");
	
	if($sp.hasClass('is-valid'))
		$sp.removeClass('is-valid');
	$('#spvf1').attr("style", "display:none;");

	if($sp.hasClass('is-invalid'))
		$sp.removeClass('is-invalid');
	$('#spif1').attr("style", "display:none;");
	$('#spif2').attr("style", "display:none;");
	
	$quantity.val('');
	$quantity.attr("readonly", true);
	$sp.val('');
	$sp.attr("readonly", true);
	disableAddOrEdit();
}

function removeQuantityComments(){
	if($quantity.hasClass('is-invalid')){
		$quantity.removeClass('is-invalid');
	}
	if($quantity.hasClass('is-valid')){
		$quantity.removeClass('is-valid');
	}
	$('#qvf1').attr('style', 'display: none;')
	$('#qif1').attr('style', 'display: none;');
	$('#qif2').attr('style', 'display: none;');
}

function removeSpComments(){
	if($sp.hasClass('is-invalid')){
		$sp.removeClass('is-invalid');
	}
	if($sp.hasClass('is-valid')){
		$sp.removeClass('is-valid');
	}
	$('#spvf1').attr('style', 'display: none;')
	$('#spif1').attr('style', 'display: none;');
	$('#spif2').attr('style', 'display: none;');
}

function resetQuantityAndSp(){
	$quantity.val('');
	$quantity.attr('readonly', true);
	$sp.val('');
	$sp.attr('readonly', true);
	removeQuantityComments();
	removeSpComments();
}

function clearAll(){
	dataOfItem = null;
	dataOfItemForEdit = null;
	dataOfItemForEditOld = null;
	barcodes = [];
	quantities = [];
	sellingPrices = [];
	names = [];
	totals = [];
	mrps = [];
	availableQuantities = [];
	barcodeSet = new Set();
	if($('#place-order-form').length){
		$('#place-order-form input[name=barcode]').val('');
	}
	else{
		$('#edit-added-item-form input[name=barcode]').val('');

	}
	removeComments();
	resetToDefaults();
	$("#added-items").empty();
	$('#place-order-modal').modal('toggle');
}

// <-----------------------------------Enable or disable Functions----------------------------->

function placeOrderEnableDisable(){
	if($('#place-order-confirm').length){
		if(barcodes.length == 0){
			$('#place-order-confirm').attr("disabled", true);
		}
		else{
			$('#place-order-confirm').attr("disabled", false);
		}
	}
}

function enableAddOrEdit(){
	if($('#add-item').length){
		$('#add-item').attr("disabled", false);
	}
	else if($('#update-added-item').length){
		$('#update-added-item').attr("disabled", false);
	}
}

function disableAddOrEdit(){
	if($('#add-item').length){
		$('#add-item').attr("disabled", true);
	}
	else if($('#update-added-item').length){
		$('#update-added-item').attr("disabled", true);
	}
}

function isOldAndNewSame(){
	if(dataOfItemForEditOld == null || dataOfItemForEdit == null){
		return false;
	}
	if(dataOfItemForEditOld.barcode == dataOfItemForEdit.barcode 
		&& dataOfItemForEditOld.quantity == dataOfItemForEdit.quantity
		&& dataOfItemForEditOld.sellingPrice == dataOfItemForEdit.sellingPrice){
			removeComments();
			return true;
		}
	return false;
}

function enableOrDisableAddOrEdit(){
	if($barcode.hasClass('is-valid') 
	&& $quantity.hasClass('is-valid') 
	&& $sp.hasClass('is-valid') 
	&& !isOldAndNewSame()
	){
		enableAddOrEdit();
		return true;
	}
	else if(!$barcode.hasClass('is-invalid') 
	&& !$quantity.hasClass('is-invalid') 
	&& !$sp.hasClass('is-invalid')
	&& !isOldAndNewSame()){
		enableAddOrEdit();
		return true;
	}
	else{
		disableAddOrEdit();
		return false;
	}
}
// <--------------------------------Validating functions----------------------------------->

function validateBarcodeUtil(){
	$barcode.off();
	$barcode.on('input', validateBarcode);
}

function validateBarcode(){
	console.log("validate barcode");
	if($barcode.val().length == 0){
		if($barcode.hasClass('is-valid')){
			$barcode.removeClass('is-valid');
		}
		$barcode.addClass('is-invalid');
		$('#bvf1').attr('style', 'display:none;');
		$('#bif1').attr('style', 'display:block;');
		$('#bif2').attr('style', 'display:none;');
		$('#bif3').attr('style', 'display:none;');

		if($('#searchForBarcode').length){
			$('#searchForBarcode').attr('disabled', true);
		}
		else{
			$('#searchForBarcodeEdit').attr('disabled', true);
		}
	}
	else{
		if($barcode.hasClass('is-invalid')){
			$barcode.removeClass('is-invalid');
		}
		$('#bvf1').attr('style', 'display:none;');
		$('#bif1').attr('style', 'display:none;');
		$('#bif2').attr('style', 'display:none;');
		$('#bif3').attr('style', 'display:none;');

		if($('#searchForBarcode').length){
			$('#searchForBarcode').attr('disabled', false);
		}
		else{
			$('#searchForBarcodeEdit').attr('disabled', false);
		}
	}
	resetQuantityAndSp();
}

function checkQuantity(){
	var data = null;
	if($('#place-order-modal').length){
		data = Object.assign({}, dataOfItem);
	}
	else{
		if(dataOfItemForEditOld.barcode != dataOfItemForEdit.barcode)
			data = dataOfItemForEdit;
		else{
			var index = barcodes.indexOf(dataOfItemForEdit.barcode);
			data = {
				"barcode": dataOfItemForEditOld.barcode,
				"quantity": parseInt(availableQuantities[index]) + parseInt(quantities[index]),
				"mrp": mrps[index],
				"sellingPrice": parseFloat(sellingPrices[index]).toFixed(2)
			}
			dataOfItemForEdit = Object.assign({}, data);
			dataOfItemForEdit.quantity = $quantity.val();
			dataOfItemForEdit.sellingPrice = $sp.val();
		}
	}

	if($quantity.val() == ""){
		setQuantityInvalid();
		$('#qif1').attr("style", "display:block;");
		$('#qif2').attr("style", "display:none;");
	}
	else if(parseInt($quantity.val()) > data.quantity){
		setQuantityInvalid();
		$('#qif1').attr("style", "display:none;");
		$('#qif2').attr("style", "display:block;");
		$('#qif2').empty();
		$('#qif2').append("Provided quantity not available. Available: "+data.quantity);
	}
	else if(parseInt($quantity.val()) <= 0){
		setQuantityInvalid();
		$('#qif1').attr("style", "display:none;");
		$('#qif2').attr("style", "display:block;");
		$('#qif2').empty();
		$('#qif2').append("Quantity must be greater than zero");
	}
	else{
		setQuantityValid();
		$('#qvf1').empty();
		$('#qvf1').append("Looks good! Available quantity: "+data.quantity);
	}
	enableOrDisableAddOrEdit();
}

function checkSellingPrice(){
	if($('#place-order-modal').length){
		data = Object.assign({}, dataOfItem);
	}
	else{
		if(dataOfItemForEditOld.barcode != dataOfItemForEdit.barcode)
			data = dataOfItemForEdit;
		else{
			var index = barcodes.indexOf(dataOfItemForEdit.barcode);
			data = {
				"barcode": dataOfItemForEditOld.barcode,
				"quantity": parseInt(availableQuantities[index]) + parseInt(quantities[index]),
				"mrp": mrps[index],
				"sellingPrice": parseFloat(sellingPrices[index]).toFixed(2)
			}
			dataOfItemForEdit = Object.assign({}, data);
			dataOfItemForEdit.quantity = $quantity.val();
			dataOfItemForEdit.sellingPrice = $sp.val();
		}
	}
	if($sp.val() == ""){
		setSellingPriceInvalid()
		$('#spif1').attr("style", "display:block;");
		$('#spif2').attr("style", "display:none;");
	}
	else if(parseFloat($sp.val()) > data.mrp){
		setSellingPriceInvalid();
		$('#spif1').attr("style", "display:none;");
		$('#spif2').attr("style", "display:block;");
		$('#spif2').empty();
		$('#spif2').append("Selling price is greater than MRP. MRP: "+data.mrp);
	}
	else if(parseFloat($sp.val()) <= 0.0){
		setSellingPriceInvalid();
		$('#spif1').attr("style", "display:none;");
		$('#spif2').attr("style", "display:block;");
		$('#spif2').empty();
		$('#spif2').append("Selling price must be greater than zero");
	}
	else{
		setSellingPriceValid();
		$('#spvf1').empty();
		$('#spvf1').append("Looks good! MRP: "+data.mrp);
	}
	enableOrDisableAddOrEdit();
}

function validateItemRequest(data){
	var barcode = $barcode.val().toLowerCase();
	if(barcodeSet.has(barcode)){
		var index = barcodes.indexOf(barcode);
		if($('#edit-added-item-modal').hasClass('show')){
			data.quantity = parseInt(availableQuantities[index]) + parseInt(quantities[index]);
		}
		else{
			data.quantity = parseInt(availableQuantities[index]);
		}
		if(dataOfItem != null)
			dataOfItem.quantity = parseInt(availableQuantities[index]);
		else
			dataOfItemForEdit.quantity = parseInt(availableQuantities[index]) + parseInt(quantities[index]);
	}
	if(data.quantity == null || data.quantity == undefined || data.quantity <= 0 || 
		data.mrp == null || data.mrp == undefined || data.mrp <= 0.00){
		setBarcodeInvalid();
		$('#bif1').attr("style", "display:none;");
		$('#bif2').attr("style", "display:none;");
		$('#bif3').attr("style", "display:block;");
		disableAddOrEdit();
	}
	else{
		setBarcodeValid();
		setQuantityValid();
		setSellingPriceValid();

		$quantity.val('1');
		$('#qvf1').empty();
		$('#qvf1').append("Available quantity: "+data.quantity);

		$sp.val(data.mrp);
		$('#spvf1').empty();
		$('#spvf1').append("MRP: "+data.mrp);

		enableAddOrEdit();
	}
}

// <--------------------------------------Delete function---------------------------------->
function deleteAddedItem(i){
	barcodeSet.delete(barcodes[i]);
	barcodes.splice(i, 1);
	quantities.splice(i, 1);
	sellingPrices.splice(i, 1);
	names.splice(i, 1);
	totals.splice(i, 1);
	availableQuantities.splice(i, 1);
	mrps.splice(i, 1);
	updateAddedItemsTable();
}

// <--------------------------------------For Placing orders ------------------------------>

function placeOrder(){
	$('#place-order-modal').modal('toggle');
	var json = { 'barcodes':barcodes, 
				'quantities': quantities, 
				'sellingPrices': sellingPrices
			};
	var json = JSON.stringify(json);
	console.log(json);
	if(validator(json)){
		var url = getOrderUrl();
		$.ajax({
			url: url,
			type: 'POST',
			data: json,
			headers: {
				'Content-Type': 'application/json'
			},	   
			success: function(response) {
				handleAjaxSuccess("Order Placed Successsfully!!!");
				getOrderListUtil();
			},
			error: function(response){
				handleAjaxError(response);
			}
	 });
	}
	 return false;
}

function addItem(){
	if(enableOrDisableAddOrEdit()){
		var barcode = $barcode.val().toLowerCase();
		var quantity = parseInt($quantity.val());
		var sellingPrice =parseFloat($sp.val()).toFixed(2);
		if(barcodeSet.has(barcode)){
			var json = {'barcode': barcode, 
						'quantity': quantity, 
						'sellingPrice': sellingPrice
					};
			json = JSON.stringify(json);
			var index = barcodes.indexOf(barcode);
			if(validator(json)){
				barcodes[index] = barcode;
				quantities[index] = parseInt(quantities[index]) + parseInt(quantity);
				sellingPrices[index] = parseFloat(sellingPrice);
				totals[index] = parseFloat( parseInt(quantities[index]) * parseFloat(sellingPrices[index])).toFixed(2);
				availableQuantities[index] = parseInt(availableQuantities[index]) - parseInt(quantity);
				$barcode.val('');
				resetToDefaults();
				updateAddedItemsTable();
			}
			else{
				handleAjaxError("Enter valid data");
			}
		}
		else{
			barcodeSet.add(barcode);
			var json = {'barcode': barcode, 
						'quantity': quantity, 
						'sellingPrice': sellingPrice
					};
			json = JSON.stringify(json);
			if(validator(json)){
				barcodes.push(barcode);
				quantities.push(parseInt(quantity));
				sellingPrices.push(parseFloat(sellingPrice).toFixed(2));
				names.push(dataOfItem.name);
				totals.push(parseFloat(quantity) * parseFloat(sellingPrice));
				availableQuantities.push(parseInt(dataOfItem.quantity) - parseInt(quantity));
				mrps.push(dataOfItem.mrp);
				$barcode.val('');
				resetToDefaults();
				updateAddedItemsTable();
			}
			else{
				handleAjaxError("Enter valid data");
			}
		}
	}
	else{
		resetToDefaults();
		$('#add-item').attr("disabled", true);
	}
}

// <---------------------------Edit added items functions------------------------------------>

function changeToEditAddedItemsAttributes(){
	if($('#place-order-modal').length){

		$('.modal-title').text('Edit order');
		
		$('#searchForBarcode').attr("id", "searchForBarcodeEdit");
		$('#searchForBarcodeEdit').off();
		$('#searchForBarcodeEdit').attr('disabled', true);
		$('#searchForBarcodeEdit').click(getProductForEdit);

		$('#table-div').attr("style", "display:none;");

		$('#place-order-confirm').attr("style", "display:none;");

		$('#cancel1').off();
		$('#cancel2').off();
		$('#cancel1').removeAttr("data-dismiss");
		$('#cancel2').removeAttr("data-dismiss");

		$('#cancel1').attr("id", "cancel3");
		$('#cancel2').attr("id", "cancel4");

		$('#cancel3').click(changeToPlaceOrderAttributes);
		$('#cancel4').click(changeToPlaceOrderAttributes);

		$('#place-order-form').append('<input type="hidden" name="i">');

		$('#add-item').empty();
		$('#add-item').append("Edit");
		$('#add-item').attr("class", "btn btn-warning col-md-2");
		$('#add-item').attr("id", "update-added-item");
		
		$('#place-order-form input[name=barcode]').attr("id", "edit-added-item-form input[name=barcode]");
		$('#place-order-form input[name=quantity]').attr("id", "edit-added-item-form input[name=quantity]");
		$('#place-order-form input[name=sellingPrice]').attr("id", "edit-added-item-form input[name=sellingPrice]");

		$('#place-order-form').attr("id", "edit-added-item-form");

		$('#place-order-modal').attr("id", "edit-added-item-modal");

		$barcode = $("#edit-added-item-form input[name=barcode]");
		$quantity = $("#edit-added-item-form input[name=quantity]");
		$sp = $("#edit-added-item-form input[name=sellingPrice]");

		$("#edit-added-item-form input[name=barcode]").off();
		$("#edit-added-item-form input[name=quantity]").off();
		$("#edit-added-item-form input[name=sellingPrice]").off();
		
		$('#edit-added-item-form input[name=barcode]').removeAttr('onfocus');
		$("#edit-added-item-form input[name=barcode]").attr('disabled', true);
		$("#edit-added-item-form input[name=barcode]").on('input', validateBarcode);
		$("#edit-added-item-form input[name=quantity]").on('input', checkQuantity);
		$("#edit-added-item-form input[name=sellingPrice]").on('input', checkSellingPrice);

		$('#update-added-item').off();
		$('#update-added-item').click(updateAddedItem);

		removeComments();
	}
}

function changeToPlaceOrderAttributes(){
	if($('#edit-added-item-modal').length){

		$('.modal-title').text('Place order');

		$('#searchForBarcodeEdit').attr("id", "searchForBarcode");
		$('#searchForBarcode').off();
		$('#searchForBarcode').attr('disabled', true);
		$('#searchForBarcode').click(getProduct);

		$('#table-div').attr("style", "display:block;");
		$('#place-order-confirm').attr("style", "display:block;");
		
		$('#cancel3').off();
		$('#cancel4').off();

		$('#cancel3').attr("id", "cancel1");
		$('#cancel4').attr("id", "cancel2");

		$('#edit-added-item-form input[name=i]').remove();

		$('#update-added-item').empty();
		$('#update-added-item').append("+&nbsp;Add&nbsp;item");
		$('#update-added-item').attr("class", "btn btn-primary col-md-2");
		$('#update-added-item').attr("id", "add-item");

		$("#edit-added-item-form input[name=barcode]").attr("id", "place-order-form input[name=barcode]");
		$("#edit-added-item-form input[name=quantity]").attr("id", "place-order-form input[name=quantity]");
		$("#edit-added-item-form input[name=sellingPrice]").attr("id", "place-order-form input[name=sellingPrice]");

		$("#edit-added-item-form").attr("id", "place-order-form");
		$("#edit-added-item-modal").attr("id", "place-order-modal");

		$barcode = $('#place-order-form input[name=barcode]');
		$quantity = $('#place-order-form input[name=quantity]');
		$sp = $('#place-order-form input[name=sellingPrice]');
		
		$('#place-order-form input[name=barcode]').off();
		$('#place-order-form input[name=quantity]').off();
		$('#place-order-form input[name=sellingPrice]').off();

		$('#place-order-form input[name=barcode]').attr('disabled', false);
		$('#add-item').off();

		$('#place-order-confirm').off();

		$('#add-item').click(addItem);
		$('#place-order-confirm').click(placeOrder);
		resetToDefaults();

		updateAddedItemsTable();

		$('#place-order-form input[name=barcode]').on('input', validateBarcode);
		$('#place-order-form input[name=quantity]').on('input', checkQuantity);
		$('#place-order-form input[name=sellingPrice]').on('input', checkSellingPrice);

		$('#cancel1').click(clearAll);
		$('#cancel2').click(clearAll);

		removeComments();
	}
	else{
	}
}

function editAddedItem(i){
	dataOfItemForEdit = {
		"barcode": barcodes[i],
		"quantity": (parseInt(availableQuantities[i]) + parseInt(quantities[i])),
		"mrp": mrps[i],
		"name": names[i],
		"sellingPrice": sellingPrices[i]
	}
	dataOfItemForEditOld = Object.assign({}, dataOfItemForEdit);
	dataOfItemForEditOld.quantity = parseInt(quantities[i]);
	changeToEditAddedItemsAttributes();
	validateItemRequest(dataOfItemForEdit);
	displayEditAddedItem(i);
}

function displayEditAddedItem(i){
	$barcode.val(barcodes[i]);
	$quantity.val(quantities[i]);
	$sp.val(sellingPrices[i]);
	$('#edit-added-item-form input[name=i]').val(i);
	$quantity.attr("readonly", false);
	$sp.attr("readonly", false);
}

// <---------------------------------Updating funtions------------------------------------->

function updateAddedItem(){
	if(enableOrDisableAddOrEdit()){
		var i = $('#edit-added-item-form input[name=i]').val();
		var originalQuantity = quantities[i];
		barcodeSet.delete(barcodes[i]);
		barcodes[i] = $('#edit-added-item-form input[name=barcode]').val();
		quantities[i] = parseInt($('#edit-added-item-form input[name=quantity]').val());
		sellingPrices[i] = parseFloat($('#edit-added-item-form input[name=sellingPrice]').val()).toFixed(2);
		totals[i] = parseFloat(parseInt(quantities[i]) * parseFloat(sellingPrices[i])).toFixed(2);
		availableQuantities[i] = parseInt(availableQuantities[i]) + parseInt(originalQuantity) - parseInt(quantities[i]);
		barcodeSet.add(barcodes[i]);
		$barcode.val('');
		console.log('Available quantities: '+availableQuantities[i]);
		changeToPlaceOrderAttributes();
	}
}

//<---------------------------UI DISPLAY METHODS---------------------------------------------->

function displayOrderList(data, sno){
	$("#order-table-body").empty();
	var spanBeginEdit = '<span class="d-inline-block" tabindex="0" data-toggle="tooltip" title="Cannot edit invoiced orders">';
    var row = "";
	for (var i = 0; i < data.length; i++) {
	sno += 1;
	var editButton = spanBegin 
					+ '&nbsp;&nbsp;<button onclick="displayOrderItemsEdit(' 
					+ data[i].id 
					+ ')" class="btn btn-secondary only-supervisor" class="btn btn-secondary">Edit</button>&nbsp;&nbsp;' 
					+ spanEnd;
	var invoiceButton = '<button onclick="generateInvoicePdf(' + data[i].id + ')" class="btn btn-primary only-supervisor">Generate Invoice</button>';
	if(data[i].orderStatus == 'INVOICED'){
		editButton = spanBeginEdit 
		+ '<button onclick="displayOrderItemsEdit(' 
		+ data[i].id
		+ ')" class="btn btn-secondary only-supervisor disabled" class="btn btn-secondary">Edit</button>' 
		+ spanEnd + '&nbsp;&nbsp;';

		invoiceButton = '<button onclick="generateInvoicePdf(' + data[i].id + ')" class="btn btn-primary only-supervisor">Download Invoice</button>';
	}
	var buttonHtml = '<button onclick="displayOrderItemsView(' + data[i].id + ')" class="btn btn-secondary">View</button>&nbsp;&nbsp;'
					 + editButton 
					 + spanBegin + invoiceButton + spanEnd;
	row = "<tr><td>" 
	+ sno + "</td><td>" 
	+ data[i].id + "</td><td>"
	+ data[i].time + "</td><td>" 
	+ data[i].totalAmount + "</td><td>"
	+ buttonHtml
	+ "</td></tr>";
	$("#order-table-body").append(row);
	}
	enableOrDisable();
}

function displayOrderItemsOfanId(data){
	var rows = '';
	var sno = 0;
	for(var i = 0; i < data.length; i++){
		sno += 1;
		rows += '<tr><td>' + sno + '</td>'
		+ '<td>' + data.productName + '</td>'
		+ '<td>' + data.sellingPrice + '</td>'
		+ '<td>' + data.mrp + '</td></tr>';
		
	}
	return rows;
}

function displayOrderItemsView(id){
	window.location.href = "./order-items/" + id + '/' + 'view';
}

function displayOrderItemsEdit(id){
	window.location.href = "./order-items/" + id + '/' + 'edit';
}

function generateInvoicePdf(id){
	var url = getInvoiceUrl() + "/" + id;
	$.ajax({
		url: url,
		type: 'GET',
		xhrFields: {
			responseType: 'blob'
		 },
		success: function(blob) {
			console.log(blob.length);
			var link=document.createElement('a');
			link.href=window.URL.createObjectURL(blob);
			link.download="Invoice" + new Date() + ".pdf";
			link.click();
			getOrderListUtil();
				
		},
		error: handleAjaxError
	 });
	 return false;
}


//INITIALIZATION CODE
function init(){
	$('#place-order').click(displayOrderModal);
	$('#add-item').click(addItem);
	$('#cancel1').click(clearAll);
	$('#cancel2').click(clearAll);
	$('#place-order-confirm').click(placeOrder);
	$('#inputPageSize').on('change', getOrderListUtil);
	$('#place-order-form input[name=barcode]').on('input',resetToDefaults);
	$('#place-order-form input[name=quantity]').on('input', checkQuantity);
	$('#place-order-form input[name=sellingPrice]').on('input', checkSellingPrice);
	$('#searchForBarcode').click(getProduct);

}

$(document).ready(init);
$(document).ready(getOrderListUtil);
$(document).ready(enableOrDisable);
