//Global variables
var downloadContent = "";
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api-all/reports/sales-report";
}

function toArrayOfJsonObjects(){
    var arr = [];
	for(var i=0; i < downloadContent.brands.length; i++){
		arr.push({});
		arr.at(-1)['sno'] = i+1;
		arr.at(-1)['brand'] = downloadContent.brands[i];
		arr.at(-1)['category'] = downloadContent.categories[i];
		arr.at(-1)['quantity'] = downloadContent.quantities[i];
		arr.at(-1)['revenue'] = downloadContent.totalAmounts[i];
		if(downloadContent.brands[i] == '\0'){
			arr.at(-1)['sno'] = '\0';
		}
	}
    return arr;
}
//BUTTON ACTIONS

function getSalesReportList(){
	var url = getSalesReportUrl() 
	+ '?page-number='
	+ '&page-size=';

	var $form = $('#sales-report-form');
	var json = toJson($form);
	console.log("JSON >> "+json);
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
			downloadContent = data;
		},
	   error: function(response){
			data = {'brands': ['\0'], 'categories': ['\0'], 
			'quantities': ['\0'],'totalAmounts': ['\0'], 
			'totalRevenue': '\0',
			'startdate': $('#sales-report-form input[name=startDate]').val(), 
			'endDate': $('#sales-report-form input[name=endDate]').val()};
			downloadContent = data;
	   }
	});
	return false;
}

function processDataUtil(){
	var pageSize = $('#inputPageSize').val();
	processData(0, pageSize);
}

function processData(pageNumber, pageSize){
	var url = getSalesReportUrl() + '?page-number=' + pageNumber + '&page-size=' + pageSize;
	var $form = $('#sales-report-form');
	var json = toJson($form);
	console.log(json);
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		dataType : 'json',
		headers: {
			'Content-Type': 'application/json'
		},	   
		success: function(data) {
				downloadContent = data;
				displaySalesReportList(data, pageNumber*pageSize);
				$('#selected-rows').html('Showing ' + 
				(pageNumber*pageSize + 1) + 
				' to ' + 
				(pageNumber*pageSize + data.brands.length) + 
				' of ' + 
				data.totalElements);
				paginate(data, pageNumber, pageSize);
				if($('#filter-modal').hasClass('show')){
					$('#filter-modal').modal('toggle');
				}
		},
		error: function(response){
			data = {'brands': ['\0'], 'categories': ['\0'], 
			'quantities': ['\0'],'totalAmounts': ['\0'], 
			'totalRevenue': '\0', 
			'startdate': $('#sales-report-form input[name=startDate]').val(), 
			'endDate': $('#sales-report-form input[name=endDate]').val()};
			downloadContent = data;
			$('#selected-rows').html('Nothing to show');
			displaySalesReportList(data, null);
			if($('#filter-modal').hasClass('show')){
				$('#filter-modal').modal('toggle');
			}
		}
	 });
 
	 return false;
}

// PAGINATING METHODS
function paginate(data,pageNumber, pageSize){
	var totalPages = Math.ceil(data.totalElements / pageSize);
	var pagination = '';
			for (var i = pageNumber - 2; i < pageNumber + 5 && i < totalPages; i++) {
				if(i < 0)
					continue;
				var active = "";
				if (i == pageNumber) {
				active = "active";
				}
				pagination += "<li class='page-item " + active 
                + "'><a class='page-link' href='#pageNumber=" + (i+1) 
                +"' onclick='processData(" + i + ", " + pageSize +")'>" + (i + 1) + "</a></li>";
			}
			if (pageNumber > 0) {
				pagination = "<li class='page-item'><a class='page-link' href='#pageNumber=" + pageNumber +"' id='previous'>Previous</a></li>" + pagination;
			}
			if (pageNumber < totalPages - 1) {    
				pagination = pagination + "<li class='page-item'><a class='page-link' href='#pageNumber=" + (pageNumber + 2) + "' id='next'>Next</a></li>";
			}
			$("#paginationContainer").html(pagination);
			$("#previous").click(function() {
				processData(pageNumber - 1, pageSize);
			});
			$("#next").click(function() {
				processData(pageNumber + 1, pageSize);
			});
}

//UI DISPLAY METHODS

function displaySalesReportList(data, sno){
	$('#start-date').empty();
	$('#start-date').append(data.startDate);
	$('#end-date').empty();
	$('#end-date').append(data.endDate);
	$("#sales-report-table-all-body").empty();
    var row = "";
	if(sno == null){
		return;
	}
	for (var i = 0; i < data.brands.length; i++) {
		sno += 1;
		row = "<tr><td>"
		+ sno + "</td><td>"
		+ data.brands[i] + "</td><td>"
		+ data.categories[i] + "</td><td>"
		+ data.quantities[i] + "</td><td>"
		+ parseFloat(data.totalAmounts[i]).toFixed(2) +"</td></tr>";
		$("#sales-report-table-all-body").append(row);
	}

}

function setdates(){
	$('#sales-report-form input[name=startDate]').val(getDateAsstring(6));
	$('#sales-report-form input[name=endDate]').val(getDateAsstring());
}

function getDateAsstring(offsetMonths=0){
	const d = new Date();
	var date = d.getDate();
	var month = d.getMonth();
	var year = d.getFullYear();
	var hour = d.getHours();
	var minute = d.getMinutes();
	var second = d.getSeconds();

	month += 1;
	month -= offsetMonths;
	while(month < 0){
		month += 12;
		year -= 1;
	}
	if(month == 2 && date > 28){
		date = 28;
	}

	if(date.toString().length == 1){
		date = '0' + date.toString();
	}
	if(month.toString().length == 1){
		month = '0' + month.toString();
	}
	if(hour.toString().length == 1){
		hour = '0' + hour.toString();
	}
	if(minute.toString().length == 1){
		minute = '0' + minute.toString();
	}
	if(second.toString().length == 1){
		second = '0' + second.toString();
	}

	year = year.toString();
	var dateString = year + "-"+ month + "-" + date +"T" + 
	hour + ":" + minute + ":" + second;
	console.log(dateString);
	return dateString;
}

function writeSalesReportFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tab-separated-values;charset=utf-8;'});
	var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'salesreport.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'salesreport.tsv');
    tempLink.click();
}

function downloadReport(){
	getSalesReportList();
	arr = toArrayOfJsonObjects();
	writeSalesReportFileData(arr);
}
function displayFilterModal(){
	$('#filter-modal').modal('toggle');
}

function clearData(){
	setdates();
	$('#sales-report-form input[name=brand]').val('');
	$('#sales-report-form input[name=category]').val('');
	processDataUtil();
}
function resetModal(){
	$('#filter-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
	$('#cancel1').click(resetModal);
	$('#cancel2').click(resetModal);
	$('#filter-data').click(displayFilterModal);
	$('#reset-data').click(clearData);
	$('#process-data').click(processDataUtil);
	$('#download-data').click(downloadReport);
	$('#inputPageSize').on('change', processDataUtil);
}

$(document).ready(init);
$(document).ready(setdates);
$(document).ready(processDataUtil);