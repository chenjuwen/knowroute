<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<title>关于知途</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- 适应移动端页面展示 -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<script type="text/javascript" src="js/jquery.min-1.11.3.js"></script>
	<style type="text/css">
		.download{
			background-color: #fff;
			color: blue;
			width: 50px;
			height: 30px;
			font-size: 15px;
			border-radius: 5px;
			border:1px solid blue;
		}
		
		.box_container{  
	        border-top: 2px solid #ddd;
	        border-bottom: 2px solid #ddd;
	        width: 350px;
	        padding: 0 10px;
	  
	        display: block;  
	        overflow: auto;
	        white-space: nowrap;
	    }
	    
	    .box_container .img_screenshot{
	        width: 229px;
	        height: 389px;
	        margin: 10px 0;
	    } 
	</style>
</head>
<body style="margin:0;">
	<table id="table1" width="90%" border="0" align="left">
		<tr>
			<td>
				<table width="100%" border="0">
					<tr>
						<td rowspan="2" width="70" align="center">
							<img alt="知途" src="images/logo.png" width="50" height="50">
						</td>
						<td style="font-size:20px; font-weight:600; color:#2F4F4F;">知途</td>
						<td rowspan="2" width="70" align="center">
							<input type="button" value="下载" class="download" onclick="doDownload()" />
						</td>
					</tr>
					<tr>
						<td style="font-size:12px; color:#777;">版本号: ${version}</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="box_container">
				<img class="img_screenshot" src="images/screenshot1.png" />
				<img class="img_screenshot" src="images/screenshot2.png" />
				<img class="img_screenshot" src="images/screenshot3.png" />
				<img class="img_screenshot" src="images/screenshot4.png" />
				<img class="img_screenshot" src="images/screenshot5.png" />
			</td>
		</tr>
		<tr>
			<td style="padding:15px 10px;">
				<div style="font-size:14px;"><span style="font-weight:600;">简介：</span>知途是一款基于地图功能实现社交场景的软件，其主要提供了查看好友位置/轨迹、紧急求助、位置分类管理与导航、多人多次轨迹分享等功能。</div>
			</td>
		</tr>
		<tr>
			<td style="font-size:14px; padding:15px 10px;">
				<div style="font-weight:600;">主要功能：</div>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看好友位置/轨迹：用户之间相互成为好友后，可以随时随地查看对方的当前位置和历史轨迹。<br><br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;紧急求助：用户可以从通讯录或者好友列表中选择紧急联系人，当遇到紧急情况时可以将当前位置信息发送给紧急联系人。<br><br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;位置分类管理与导航：对位置点进行分类管理，并可以调起百度地图APP进行目的地导航。<br><br>
				</div>
			</td>
		</tr>
		<tr>
			<td align="center" style="padding:30px 0">
				<img alt="知途" src="images/qrcode.jpg">
				<div style="color:#000; font-size:15px;">扫描二维码下载</div>
			</td>
		</tr>
	</table>
	
	<Script language="javascript">
		function doDownload(){
			var url = "<%=request.getContextPath()%>/download?filename=knowroute-${version}.apk";
			window.open(url);
		}
		
		var tableWidth = document.getElementById("table1").offsetWidth;
		jQuery(".box_container").css("width", tableWidth + "px");
	</Script>
</body>
</html>