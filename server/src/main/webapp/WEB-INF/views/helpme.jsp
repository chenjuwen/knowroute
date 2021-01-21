<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>知途 > 紧急求助</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<!-- 适应移动端页面展示 -->
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		<link rel="stylesheet" href="css/map.css" />
		<style type="text/css" >
			#container{
				height:100%
			}	
				
			.divMessageContainer{
				position: fixed;
				bottom: 0;
				z-index: 999;
				background-color: #2F4F4F;
				width: 100%;
				height: 100px;
				padding: 3px 0 3px 0;
				align: center;
				color: #FFF;
			}
			.divMessageContainer .phone{
				text-align: center;
				height: 22px;
				font-size: 16px;
			}
			.divMessageContainer .address{
				text-align: center;
				height: 22px;
				padding-bottom: 15px;
				font-size: 16px;
			}
			.divMessageContainer .position{
				width: 100%;
				height: 20px;
				text-align: center;
				vertical-align: middle;
				color: #ccc;
				font-size: 15px;
			}
			
			.show_true{
				display: block;
			}
			
			.show_false{
				display: none;
			}
		</style>
		
		<script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=dznCIamw2fFeqGOxQGHKYAxACOYw143G"></script>
		<script type="text/javascript" src="js/map.js"></script>
    </head>
    <body>    	
		<div id="container"></div> 
		<script type="text/javascript">
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
		
    	<div class="divMessageContainer show_${address != ''}">
    		<div class="phone">${phone}</div>
    		<div class="address">${address}</div>
    		<div class="position">经度：${longitude}</div>
    		<div class="position">纬度：${latitude}</div>
    	</div>
    </body>
</html>
