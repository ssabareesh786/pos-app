// function deleteOrder(id){
// 	var url = getOrderUrl() + "/" + id;

// 	$.ajax({
// 	   url: url,
// 	   type: 'DELETE',
// 	   success: function(data) {
// 	   		getOrderList();  
// 	   },
// 	   error: handleAjaxError
// 	});
// }

// //BUTTON ACTIONS
// function addOrder(event){
// 	var $form = $("#place-order-form");
// 	var json = toJsonArray($form);
// 	for(var i=0; i < json.length; i++){
// 		json[i]['quantity'] = parseInt(json[i]['quantity']);
// 		json[i]['sellingPrice'] = parseFloat(json[i]['sellingPrice']);
// 	}
// 	json = JSON.stringify(json);
// 	var url = getOrderUrl();

// 	$.ajax({
// 	   url: url,
// 	   type: 'POST',
// 	   data: json,
// 	   headers: {
//        	'Content-Type': 'application/json'
//        },	   
// 	   success: function(response) {
// 	   		getOrderList();
// 			window.close();
// 	   },
// 	   error: handleAjaxError
// 	});

// 	return false;
// }

// function deleteOrder(id){
    // 	var url = getOrderUrl() + "/" + id;
    
    // 	$.ajax({
    // 	   url: url,
    // 	   type: 'DELETE',
    // 	   success: function(data) {
    // 	   		getOrderList();  
    // 	   },
    // 	   error: handleAjaxError
    // 	});
    // }

    function updateOrder(event){
        $('#edit-order-modal').modal('toggle');
        //Get the ID
        var id = $("#order-edit-form input[name=id]").val();	
        var url = getOrderUrl() + "/" + id;
    
    
        //Set the values to update
        var $form = $("#order-edit-form");
        var json = toJson($form);
    
        $.ajax({
           url: url,
           type: 'PUT',
           data: json,
           headers: {
               'Content-Type': 'application/json'
           },	   
           success: function(response) {
                   getOrderList();   
           },
           error: handleAjaxError
        });
    
        return false;
    }