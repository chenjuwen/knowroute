/**
* 判断是否是手机号码
*/
function isMobile(phone){
    if(!(/^[1][3-9][0-9]{9}$/.test(phone))){
        return false;
    }
    return true;
}