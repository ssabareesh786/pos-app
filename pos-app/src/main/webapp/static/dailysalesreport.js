//Global variables
var downloadContent = null;

function getDailySalesReportUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api-all/reports/daily-sales-report";
}

//BUTTON ACTIONS

function getDailySalesReportList() {
	var url = getDailySalesReportUrl() + '?page-number=&page-size=';
	var $form = $('#daily-sales-report-form');
	var json = toJson($form);

	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		dataType: 'json',
		contentType: 'application/json',
		success: function (data) {
			downloadContent = data.content;
			writeReportData(addSno());
		},
		error: function(response){
			downloadContent = [{'sno':'\0', 'date': '\0', 'invoicedOrdersCount': '\0', 'invoicedItemsCount': '\0', 'totalRevenue': '\0'}];
			writeReportData(addSno());
		}
	});
	return false;
}
function processDataUtil() {
	var pageSize = $('#inputPageSize').val();
	processData(0, pageSize);
	console.log("page size"+pageSize);
}

function processData(pageNumber, pageSize) {
	var url = getDailySalesReportUrl() + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	var $form = $('#daily-sales-report-form');
	var json = toJson($form);
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		dataType: 'json',
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (data) {
			console.log(data);
			displaySchedulerReportList(data.content, pageNumber*pageSize);
			$('#selected-rows').html(
				'Showing ' 
				+ (pageNumber*pageSize + 1) 
				+ ' to ' 
				+ (pageNumber*pageSize 
				+ data.content.length) 
				+ ' of ' 
				+ data.totalElements);
			paginator(data, "processData", pageSize);
			if($('#filter-modal').hasClass('show')){
				$('#filter-modal').modal('toggle');
			}
		},
		error: function(response){
			$("#scheduler-report-table-all-body").empty();
			$('#selected-rows').html('Nothing to show');
		if($('#filter-modal').hasClass('show')){
			$('#filter-modal').modal('toggle');
		}
		}
	});

	return false;
}

function getDateAsStringStandardFormat(d){
	var d = new Date(d);
	var date = d.getDate().toString();
	var month = d.getMonth().toString();
	var year = d.getFullYear().toString();
	if(date.toString().length == 1){
		date = '0' + date.toString();
	}
	if(month.toString().length == 1){
		month = '0' + month.toString();
	}
	var dateString = date + '/' + month + '/' + year;
	return dateString;
}

//UI DISPLAY METHODS

function displaySchedulerReportList(data, sno) {
	$('#start-date').html(getDateAsStringStandardFormat($('#daily-sales-report-form input[name=startDate]').val()));
	$('#end-date').html(getDateAsStringStandardFormat($('#daily-sales-report-form input[name=endDate]').val()));
	$("#scheduler-report-table-all-body").empty();
	var row = "";
	for (var i = 0; i < data.length; i++) {
		sno += 1
		row = "<tr><td>"
			+ sno + "</td><td>"
			+ data[i].date + "</td><td>"
			+ data[i].invoicedOrdersCount + "</td><td>"
			+ data[i].invoicedItemsCount + "</td><td>"
			+ parseFloat(data[i].totalRevenue).toFixed(2) + "</td></tr>";
		$("#scheduler-report-table-all-body").append(row);
	}
}

function setdates() {
	$('#daily-sales-report-form input[name=startDate]').val(getDateAsstring(6));
	$('#daily-sales-report-form input[name=endDate]').val(getDateAsstring());
}

function getDateAsstring(offsetMonths = 0) {
	const d = new Date();
	var date = d.getDate();
	var month = d.getMonth();
	var year = d.getFullYear();
	var hour = d.getHours();
	var minute = d.getMinutes();
	var second = d.getSeconds();

	month += 1;
	month -= offsetMonths;
	while (month < 0) {
		month += 12;
		year -= 1;
	}
	if (month == 2 && date > 28) {
		date = 28;
	}

	if (date.toString().length == 1) {
		date = '0' + date.toString();
	}
	if (month.toString().length == 1) {
		month = '0' + month.toString();
	}
	if (hour.toString().length == 1) {
		hour = '0' + hour.toString();
	}
	if (minute.toString().length == 1) {
		minute = '0' + minute.toString();
	}
	if (second.toString().length == 1) {
		second = '0' + second.toString();
	}

	year = year.toString();
	var dateString = year + "-" + month + "-" + date + "T" +
		hour + ":" + minute + ":" + second;
	console.log(dateString);
	return dateString;
}

function writeReportData(arr) {
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};

	var data = Papa.unparse(arr, config);
	var blob = new Blob([data], { type: 'text/tab-separated-values;charset=utf-8;' });
	var fileUrl = null;

	if (navigator.msSaveBlob) {
		fileUrl = navigator.msSaveBlob(blob, 'posdaysales.tsv');
	} else {
		fileUrl = window.URL.createObjectURL(blob);
	}
	var tempLink = document.createElement('a');
	tempLink.href = fileUrl;
	tempLink.setAttribute('download', 'posdaysales.tsv');
	tempLink.click();
}

function addSno(){
	if(downloadContent == null){
		return null;
	}
	var downloadContentEdited = [];
	for(var i=0; i < downloadContent.length; i++){
		var editedObj = {};
		editedObj.sno = i+1;
		editedObj.date = downloadContent[i].date;
		editedObj.invoicedOrdersCount = downloadContent[i].invoicedOrdersCount;
		editedObj.invoicedItemsCount = downloadContent[i].invoicedItemsCount;
		editedObj.totalRevenue = parseFloat(downloadContent[i].totalRevenue).toFixed(2);
		if(downloadContent[i].sno == '\0'){
			editedObj.sno = '\0';
			editedObj.totalRevenue = '\0';
		}
		downloadContentEdited.push(editedObj);
	}
	return downloadContentEdited;
}

function downloadReport() {
	getDailySalesReportList();
}

function displayFilterModal(){
	$('#filter-modal').modal('toggle');
}

function clearData(){
	setdates();
	processDataUtil();
}

function resetModal(){
	$('#filter-modal').modal('toggle');
}

//INITIALIZATION CODE
function init() {
	$('#cancel1').click(resetModal);
	$('#cancel2').click(resetModal);
	$('#filter-data').click(displayFilterModal);
	$('#process-data').click(processDataUtil);
	$('#reset-data').click(clearData);
	$('#download-data').click(downloadReport);
	$('#inputPageSize').on('change', processDataUtil);
}

$(document).ready(init);
$(document).ready(setdates);
$(document).ready(processDataUtil);