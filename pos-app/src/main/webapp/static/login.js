function getMessage(){
	return $("meta[name=message]").attr("content");
}
function getPageType(){
    return $("meta[name=pageType]").attr("content");
}
function init(){
    $('#signupButton').attr("style", "display:block;");
    $('#loginButton').attr("style", "display:none;");
    console.log("Page type: "+getPageType());
    if(getMessage() != '' && getPageType() == 'Login'){
        $.notify(getMessage(), {
            position: "top right",
            autoHideDelay: 5000,
            horizontalAlign: "right",
            zIndex: 9999999,
            className: "error"
        });
    }
}
$(document).ready(init);