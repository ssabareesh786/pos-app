function getMessage(){
	return $("meta[name=message]").attr("content");
}
function getPageType(){
    return $("meta[name=pageType]").attr("content");
}
function init(){
    $('#loginButton').attr("style", "display:block;")
    $('#signupButton').attr("style", "display:none;");
    if(getMessage() != '' && getPageType() == 'Sign Up'){
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