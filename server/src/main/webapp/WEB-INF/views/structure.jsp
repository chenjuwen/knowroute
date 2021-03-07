<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<title>知图   > 系统架构</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- 适应移动端页面展示 -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
	<style type="text/css">
		body {
			font-size: 14px;
			line-height: 22px;
		}
		.category {
			font-size: 25px;
			font-weight: 600;
		}
		
		.introduce .label {
			padding-right: 10px;
		}
		
		.introduce .stack {
			color: blue;
			padding-bottom: 20px;
		}
	</style>
</head>
<body style="margin:0px;">
	<table width=100%" border="0" align="left">
		<tr>
			<td>
				<table width="100%" border="0" style="background-color:#4682B4;">
					<tr>
						<td>
							<img alt="知途" src="images/logo.png" width="50" height="50" style="padding-left:5px; padding-top:7px;">
							<span style="font-size:25px; font-weight:600; color:yellow; line-height:60px; vertical-align:top; padding-top:10px;">知图APP-系统架构设计</span>
						</td>
				</table>
			</td>
		</tr>
	</table>
	
	<table width="800" border="0" align="left" style="margin:25px 0 25px 10px; color:blue; font-size:18px; font-weight:600; ">
		<tr>
			<td>
				简介：知途是一款基于地图功能实现社交场景的软件，其主要提供了查看好友位置/轨迹、紧急求助、位置分类管理与导航、多人多次轨迹分享等功能。
				<span><a href="aboutme" style="color:red; font-weight:600;">（下载知图APP）</a></span>
			</td>
		</tr>
	</table>
	
	
	<!--#####  总体架构    #####-->
	<table width=100%" border="0" align="left">
		<tr><td class="category">1、总体架构</td></tr>
		<tr><td><img src="images/structure01.jpg"/></td></tr>
		<tr>
			<td class="introduce">
				<table width="100%" border="0">
					<tr>
						<td align="right" width="110" class="label">APP:</td>
						<td>提供给用户登录使用产品相关功能。</td>
					</tr>
					<tr>
						<td align="right" class="label">Nginx:</td>
						<td>负责请求分发和负载均衡器，用于将公网与应用服务器隔离开，避免应用服务器暴露到公网上，保证应用服务器的安全。</td>
					</tr>
					<tr>
						<td align="right" class="label">API Server:</td>
						<td>基于RESTful风格实现的APP应用统一API接口。</td>
					</tr>
					<tr>
						<td align="right" class="label">EMQ X Broker:</td>
						<td>基于发布订阅模式的开源MQTT消息服务器，负责发布消息和推送消息到客户端（比如app应用）。</td>
					</tr>
					<tr>
						<td align="right" class="label">Web Admin:</td>
						<td>后台管理系统，负责管理和监控服务器和应用系统。</td>
					</tr>
					<tr>
						<td align="right" class="label">Database:</td>
						<td>负责存储业务数据。</td>
					</tr>
					<tr>
						<td align="right" class="label">缓存服务器:</td>
						<td>用于缓存系统热点数据，提高响应速度，减轻数据库压力。</td>
					</tr>
					<tr>
						<td colspan="2" class="stack"><b>技术栈:&nbsp;</b>Android、Nginx、Redis5、Springboot、EMQ X Broker、MySQL</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
	
	<!--#####  APP应用架构    #####-->
	<table width=100%" border="0" align="left">
		<tr><td class="category">2、APP应用架构</td></tr>
		<tr><td colspan="2" style="padding-left:10px;">APP应用是采用基于WebView实现的混合开发框架进行开发。</td></tr>
		<tr><td><img src="images/structure02.jpg"/></td></tr>
		<tr>
			<td class="introduce">
				<table width="100%" border="0">
					<tr>
						<td align="right" width="110" class="label">UI层:</td>
						<td>用户交互界面，用于数据展示和数据录入。</td>
					</tr>
					<tr>
						<td align="right" class="label">Action层:</td>
						<td>包括内置Action和业务类Action，负责用户请求处理、业务逻辑处理、服务组件调用、数据推送到UI层。</td>
					</tr>
					<tr>
						<td align="right" class="label">Service层:</td>
						<td>包括内置服务组件和业务类服务组件，供原生Activity和Action层调用，提供通用的框架核心功能。</td>
					</tr>
					<tr>
						<td colspan="2" class="stack"><b>技术栈:&nbsp;</b>Android、VUE、EventBus、OKHttp3、百度地图、SQLite</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
	
	<!--#####  消息推送系统架构    #####-->
	<table width=100%" border="0" align="left">
		<tr><td class="category">3、消息推送系统架构</td></tr>
		<tr><td><img src="images/structure03.jpg"/></td></tr>
		<tr>
			<td class="introduce">
				<table width="100%" border="0">
					<tr>
						<td align="right" width="110" class="label">EMQ X Borker:</td>
						<td>EMQ X Borker是一种基于发布订阅模式的开源MQTT消息服务器。负责管理和控制连接到其上的客户端，客户端身份认证与授权，发布主题消息，推送消息到客户端。</td>
					</tr>
					<tr>
						<td align="right" class="label">客户端:</td>
						<td>以客户端身份连接到EMQ X服务器，发布主题消息到EMQ X服务器，从EMQ X服务器订阅主题。</td>
					</tr>
					<tr>
						<td align="right" class="label">数据库服务器:</td>
						<td>用于存储账号、ACL等客户端权限控制信息。当客户端连接到EMQ X服务器时，从数据库获取这些消息来判断客户端是否有连接权限和主题发布订阅权限。</td>
					</tr>
					
					<tr>
						<td colspan="2" class="stack" style="padding-top:10px;">
							<div><b>安全策略:</b></div>
							<div style="padding-left:30px;">
								1、启用基于TLS双向认证的安全通信机制<br>
								2、禁用匿名认证<br>
								3、启用身份认证机制<br>
								4、启用发布订阅ACL
							</div>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" class="stack"><b>技术栈:&nbsp;</b>EMQ X Broker、MySQL</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
	
	<!--#####  API Server应用架构    #####-->
	<table width=100%" border="0" align="left">
		<tr><td class="category">4、API Server应用架构</td></tr>
		<tr><td colspan="2" style="padding-left:10px;">基于RESTful风格实现的APP应用统一API接口。</td></tr>
		<tr><td><img src="images/structure04.jpg"/></td></tr>
		<tr>
			<td class="introduce">
				<table width="100%" border="0">
					<tr>
						<td align="right" width="110" class="label">访问控制层:</td>
						<td>基于注解和拦截器实现对API接口进行审计日志、身份认证、数据权限认证和访问限速等功能。</td>
					</tr>
					<tr>
						<td align="right" class="label">接口层:</td>
						<td>基于Springboot的Controller实现RESTful风格的API接口方法。</td>
					</tr>
					<tr>
						<td align="right" class="label">服务层:</td>
						<td>负责业务逻辑处理和业务数据的持久化。</td>
					</tr>
					<tr>
						<td align="right" class="label">缓存服务器:</td>
						<td>用于缓存系统热点数据，提高响应速度，减轻数据库压力。</td>
					</tr>
					<tr>
						<td align="right" class="label">统一异常处理:</td>
						<td>基于@ControllerAdvice和@ExceptionHandler注解实现统一异常处理功能。</td>
					</tr>
					<tr>
						<td align="right" class="label">数据库:</td>
						<td>用于存储业务数据和身份认证数据。</td>
					</tr>
					
					<tr>
						<td colspan="2" class="stack" style="padding-top:10px;">
							<div><b>安全策略:</b></div>
							<div style="padding-left:30px;">
								1、基于HTTPS协议的安全通信机制，保证数据的安全性<br>
								2、基于JWT的身份认证机制<br>
								3、基于拦截器的访问速度控制<br>
								4、基于拦截器的数据访问限制
							</div>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" class="stack"><b>技术栈:&nbsp;</b>Springboot、Redis5、JWT、Swagger2、MySQL</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>