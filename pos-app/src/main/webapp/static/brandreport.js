function getBrandReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api-all/reports/brand-report";
}

function getBrandListUtil(){
	var pageSize = $('#inputPageSize').val();
	var brand = $('#brand-report-form input[name=brand]').val();
	var category = $('#brand-report-form input[name=category]').val();
	if(brand == undefined)
		brand = '';
	if(category == undefined)
		category = '';
	getBrandList(brand, category, 0, pageSize);
}

function getBrandList(brand, category, pageNumber, pageSize){
	var url = getBrandReportUrl() + 
	'?brand=' + brand + 
	'&category=' + category + 
	'&page-number=' + pageNumber + 
	'&page-size=' + pageSize;
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
	   		displayBrandList(data.content,pageNumber*pageSize);
			$('#selected-rows').html('Showing ' + (pageNumber*pageSize + 1) + ' to ' + (pageNumber*pageSize + data.content.length) + ' of ' + data.totalElements);
			paginatorForReport(data, "getBrandList", brand, category, pageSize);
			if($('#filter-modal').hasClass('show')){
				$('#filter-modal').modal('toggle');
			}
		},
	   error: function(response){
			displayBrandList([],0);
			$('#selected-rows').html('Nothing to show');
			paginatorForReport([], "getBrandList", brand, category, pageSize);
			if($('#filter-modal').hasClass('show')){
				$('#filter-modal').modal('toggle');
			}
	   }
	});
	return false;
}

//UI DISPLAY METHODS

function displayBrandList(data, sno){
	$("#brand-report-table-body").empty();
    var row = "";
	for (var i = 0; i < data.length; i++) {
		sno += 1;
		row = "<tr><td>" 
		+ sno + "</td><td>" 
		+ data[i].brand + "</td><td>"
		+ data[i].category + "</td></tr>";
		$("#brand-report-table-body").append(row);
	}
}

function writeBrandReportFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tab-separated-values;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'brandreport.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'brandreport.tsv');
    tempLink.click();
}

function rearrange(downloadContent){
	var downloadContentRearranged = [];
	for(var i=0; i < downloadContent.length; i++){
		var rearrangedObj = {};
		rearrangedObj.sno = i+1;
		rearrangedObj.brand = downloadContent[i].brand;
		rearrangedObj.category = downloadContent[i].category;
		downloadContentRearranged.push(rearrangedObj);
	}
	return downloadContentRearranged;
}

function downloadReport(){
	var brand = $('#brand-report-form input[name=brand]').val();
	var category = $('#brand-report-form input[name=category]').val();
	if(brand == undefined)
		brand = '';
	if(category == undefined)
		category = '';
	var url = getBrandReportUrl() + 
	'?brand=' + brand + 
	'&category=' + category + 
	'&page-number='+ 
	'&page-size=';

	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
	   dataType : 'json',
	   contentType : 'application/json',
	   success: function(data) {
		console.log(data);
		downloadContent = rearrange(data.content);
		writeBrandReportFileData(downloadContent);
	   },
	   error: function(response){
			downloadContent = [{'sno': '\0', 'brand': '\0', 'category': '\0'}];
			writeBrandReportFileData(downloadContent);
	   }
	});
	return false;
}

function displayFilterModal(){
	$('#filter-modal').modal('toggle');
}

function resetModal(){
	$('#filter-modal').modal('toggle');
}

function clearData(){
	$('#brand-report-form input[name=brand]').val('');
	$('#brand-report-form input[name=category]').val('');
	getBrandListUtil();
}

//INITIALIZATION CODE
function init(){
	$('#cancel1').click(resetModal);
	$('#cancel2').click(resetModal);
	$('#filter-data').click(displayFilterModal);
	$('#reset-data').click(clearData);
	$('#process-data').click(getBrandListUtil);
	$('#download-data').click(downloadReport);
	$('#inputPageSize').on('change', getBrandListUtil);
}

$(document).ready(init);
$(document).ready(getBrandListUtil);