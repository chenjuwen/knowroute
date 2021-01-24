<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<title>关于知途</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- 适应移动端页面展示 -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
	<style type="text/css">
		.download{
				background-color: #4682B4;
				display: block;
				text-decoration: none;
				color: white;
				border: none;
				width: 180px;
				height: 40px;
				font-size: 22px;
				border-radius: 20px;
				padding-top: 6px;
			}
	</style>
</head>
<body bgcolor="#FAFAD2">
	<table width="100%" border="0">
		<tr>
			<td align="center">
				<br><img alt="知途" src="images/logo.png"><br>
				<span style="font-size:45px; font-weight:600; color:#222;">知途</span>
				<span style="font-size:20px; font-weight:500; color:#222; margin-left:20px;">助寻迹&nbsp;&nbsp;保安全</span>
			</td>
		</tr>
		
		<tr><td align="center" style="padding:10px 0"><img alt="知途" src="images/qrcode.jpg"></td></tr>
		
		<tr>
			<td align="center" height="60">
				<a class="download" target="_blank" href="<%=request.getContextPath()%>/download?filename=knowroute-0.5.0.apk">立即下载</a>
			</td>
		</tr>
		
	</table>
	
</body>
</html>