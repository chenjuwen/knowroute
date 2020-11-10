<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>map demo</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<!-- 适应移动端页面展示 -->
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		<link rel="stylesheet" href="css/map.css" />
		<style type="text/css" >
			#container{
				height:100%
			}
		</style>
		
		<script type="text/javascript" src="http://api.map.baidu.com/api?v=3.0&ak=dznCIamw2fFeqGOxQGHKYAxACOYw143G"></script>
		<script type="text/javascript" src="js/map.js"></script>
    </head>
    <body>    	
		<div id="container"></div> 
		
		<script type="text/javascript">
			var points = [
			  	new BMap.Point(111.563148, 22.617621),
			  	new BMap.Point(111.563418, 22.618247),
			  	new BMap.Point(111.564262, 22.619039),
			  	new BMap.Point(111.565313, 22.619798)
			];
		
			var point = new BMap.Point(111.563063, 22.617681);
			var map = newMap("container", point, false);
			
			var opts = {    
				    width : 250,     // 信息窗口宽度    
				    height: 100,     // 信息窗口高度    
				    title : "提示"  // 信息窗口标题   
				}    
				var infoWindow = new BMap.InfoWindow("<div style='color:red;'><input></div>", opts);  // 创建信息窗口对象    
				map.openInfoWindow(infoWindow, points[0]);      // 打开信息窗口
			
			//轨迹路线
			//blue, lime, yellow, red, fuchsia, aqua, deeppink, violet, darkorange, gold, brown, darkviolet
			var polyline = new BMap.Polyline(points, {strokeColor:"red", strokeWeight:3, strokeOpacity:0.0});
			map.addOverlay(polyline);
			
			
			//起点标注
			var startIcon = new BMap.Icon("images/start.png", new BMap.Size(24, 34), {
                anchor: new BMap.Size(12, 34) //定位点
            });
			
			var startMarker = addMarker(map, points[0], null, null, false, false);
			startMarker.setIcon(startIcon);
			
			//终点标注
			var endIcon = new BMap.Icon("images/end.png", new BMap.Size(24, 34), {
                anchor: new BMap.Size(12, 34) //定位点
            });
			
			var endMarker = addMarker(map, points[points.length - 1], null, null, false, false);
			endMarker.setIcon(endIcon);

			//行走标注
			var travelIcon = new BMap.Icon("images/person.png", new BMap.Size(18, 40), {
            	anchor: new BMap.Size(9, 40) //定位点
            });
			
			var count = 0;
			var travelMarker = null;
			setInterval(function(){
				if(travelMarker == null){
					travelMarker = addMarker(map, points[0], null, null, false, false);
					travelMarker.setIcon(travelIcon);
				}else{
					var currentPoint = points[count++ % points.length];
					travelMarker.setPosition(currentPoint);
					//map.panTo(currentPoint);
				}
			}, 1000);
		</script>
    </body>
</html>
