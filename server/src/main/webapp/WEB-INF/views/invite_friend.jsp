<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>知途 > 邀请好友</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<!-- 适应移动端页面展示 -->
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		
		<link rel="stylesheet" type="text/css" href="css/map.css" />
		<link rel="stylesheet" type="text/css" href="css/muse-ui.css" />
		<link rel="stylesheet" type="text/css" href="css/muse-ui-message.all.css" />
			
		<script type="text/javascript" src="js/vue.min.js"></script>
		<script type="text/javascript" src="js/muse-ui.js"></script>
		<script type="text/javascript" src="js/muse-ui-message.js"></script>
		<script type="text/javascript" src="js/jquery.min-1.11.3.js"></script>
		<script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=dznCIamw2fFeqGOxQGHKYAxACOYw143G"></script>
		<script type="text/javascript" src="js/map.js"></script>
	
		<style type="text/css" >
			#container{
				height:100%
			}	
				
			.divMessageContainer{
				position: fixed;
				bottom: 0;
				z-index: 999;
				background-color: #FFF;
				width: 100%;
				height: 180px;
				padding: 3px 0 3px 0;
				align: center;
				border-top:1px solid #ddd;
			}
			.divMessageContainer .phone{
				text-align: center;
				height: 25px;
				font-size: 18px;
			}
			.divMessageContainer .info{
				width: 100%;
				display: block;
				text-align: center;
				padding: 15px 0;
			}
			.divMessageContainer .info div{
				display: inline-block;
				position: relative;
				top: -15px;
				margin-left: 20px;
				color: #666;
				font-size: 15px;
			}
			.divMessageContainer .button{
				text-align: center;
			}
			.divMessageContainer .button input{
				background-color: #26a2ff;
				color: #fff;
				border: none;
				width: 220px;
				height: 45px;
				font-size: 16px;
				border-radius: 15px;
			}
			
			.download{
				background-color: #26a2ff;
				display: block;
				color: #fff;
				border: none;
				width: 150px;
				height: 40px;
				font-size: 16px;
				border-radius: 15px;
				padding-top: 6px;
			}
			
			.show_true{
				display: block;
			}
			
			.show_false{
				display: none;
			}
		</style>
    </head>
    <body>    	
		<div id="container"></div> 
		
		<script type="text/javascript">
			var mid = ${mid};
			var longitude = ${longitude};
	    	var latitude = ${latitude};
			var point = new BMap.Point(longitude, latitude);
			
			var map = newMap("container", point, false);
			
		    var label = new BMap.Label("我在这里", {offset:new BMap.Size(20,-20)});
		    label.setStyle({
		    	padding:"2px",
		    	backgroundColor:"yellow", 
		    	color:"red", 
		    	fontSize:"13px",
		    	border: "1px solid #555"
		    });
		    
		    addMarker(map, point, null, label, false, false);
		</script>
		
    	<div id="app" class="divMessageContainer show_${phone != ''}">
    		<div class="phone">${phone}</div>
    		
    		<div class="info">
    			<img src="images/invite_friend_heart.jpg">
    			<div>实时共享位置<br>守护彼此安全</div>
    		</div>
    		
    		<div class="button">
    			<input type="button" value="同意好友请求" @click="confirmInvite">
    		</div>
    	
			<mu-dialog transition="slide-left" width="260"
				   :overlay="true" :esc-press-close="true" :overlay-close="true" :open.sync="openDialogEnable">
				<table width="100%" border=0>
					<tr>
						<td align="center" height="50" style="color:#000; font-size:18px; ">进入App即可成为好友</td>
					</tr>
					<tr>
						<td align="center"><img src="images/logo.png" width="50" height="50"></td>
					</tr>
					<tr>
						<td align="center" height="60"><a class="download" target="_blank" href="<%=request.getContextPath()%>/download?filename=knowroute-${version}.apk">下载-知途App</a></td>
					</tr>
				</table>
			</mu-dialog>
		</div>
	
		<script>
			var vm = new Vue({
				el: '#app',
				data: {
					openDialogEnable: false
				},
				methods: {
					confirmInvite: function(){
						jQuery.ajax({
							type: "POST",
							url: "message/confirm",
							contentType:'application/json',
							data: JSON.stringify({id:mid, result:"agree"}),
							dataType: "json",
							success: function(data, textStatus){
								if(data.code == 1){
									vm.openDialogEnable = true;
								}else{
									MuseUIToast.message("操作失败");
								}
							}
						});
					}
				}
			})
		</script>
	
    </body>
</html>
