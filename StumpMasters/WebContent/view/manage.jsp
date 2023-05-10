<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Manage Account</title>
</head>
<body>

<center>
	<h1>Manage Account</h1>
	<c:if test="${! empty user}">
			<div class="username">Username: ${username}</div>
		</c:if>
	<br><br>
	<br><br>
	<form method="get" id = "delete" action="${pageContext.servletContext.contextPath}/Login">
		<input type="submit" value="Delete Account">
		<input type="hidden" name = delete value="delete">
		<input type="hidden" name = username value="${username}">
	</form>
		
	<br><br>
	<form method="get" id = "changeUsername" action="${pageContext.servletContext.contextPath}/Login">
		<input type="text" name = newUsername>
		<input type="hidden" name = username value="${username}">
		<input type="submit" value="Change Username">
	</form>
	
	<br><br>
	<form method="get" id = "Logout" action="${pageContext.servletContext.contextPath}/Login">
		<input type="submit" value="Log Out">
	</form>
</center>
    
</body>

<script>
	
	if("${! empty user}" != "true" && ("${username}".length == 0 || "${username}" == "NotLoggedIn")){
	let repost = document.getElementById("Login")
	repost.submit();
	}
	if("${loadGame}" == "true"){
		let submit =document.createElement("input")
		submit.type = "submit"
		submit.value = "Load Game"
		document.getElementById("LoadGame").appendChild(submit)
	}
</script>
</html>