<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<title>关于知途</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- 适应移动端页面展示 -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
	<style type="text/css">
		.download{
				background-color: #ADFF2F;
				display: block;
				text-decoration: none;
				color: #595959;
				border: none;
				width: 220px;
				height: 40px;
				font-size: 20px;
				border-radius: 20px;
				padding-top: 6px;
			}
	</style>
</head>
<body bgcolor="#FAFAD2">
	<table width="100%" border="0">
		<tr>
			<td align="center" style="padding:50px 0;">
				<div style=""><img alt="知途" src="images/logo.png"></div>
				<span style="font-size:40px; font-weight:600; color:#2F4F4F;">知途</span>
				<span style="font-size:20px; font-weight:600; color:#595959; margin-left:20px;">助寻迹&nbsp;&nbsp;保安全</span>
			</td>
		</tr>
		
		<tr>
			<td align="center" height="60">
				<a class="download" target="_blank" href="<%=request.getContextPath()%>/download?filename=knowroute-${version}.apk">下载 Android版</a>
			</td>
		</tr>
		
		<tr>
			<td align="center" style="padding:30px 0">
				<img alt="知途" src="images/qrcode.jpg">
				<div style="color:#000; font-size:15px;">扫描二维码下载</div>
			</td>
		</tr>
				
	</table>
	
</body>
</html>