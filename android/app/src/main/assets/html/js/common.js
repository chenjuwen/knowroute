/**
* 判断是否是手机号码
*/
function isMobile(text){
    if(isNaN(text)){
        return false;
    }

    if(text.length != 11){
        return false;
    }

    return true;
}