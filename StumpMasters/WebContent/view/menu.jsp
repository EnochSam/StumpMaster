<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Main Menu</title>
</head>
<body>

<center>
	<h1>Stump Masters</h1>
	<c:if test="${! empty user}">
			<div class="username">Username: ${username}</div>
		</c:if>
	<br><br>
	<form method="get" action="${pageContext.servletContext.contextPath}/Game">
		<input type="submit" value="Create Game">
		<input type="hidden" name = username value="${username}">
	</form>
			<br><br>
		<form method="get" id = "LoadGame" action="${pageContext.servletContext.contextPath}/Load">
		<input type="hidden" name = username value="${username}">
		</form>
		
			<br>
	<br><br>
	<form method="get" id = "Manage" action="${pageContext.servletContext.contextPath}/Manage">
		<input type="submit" value="Manage Account">
		<input type="hidden" name = username value="${username}">
		<input type="hidden" name = user value="${user}">
	</form>
		
	<br><br>
	<form method="get" id = "Login" action="${pageContext.servletContext.contextPath}/Login">
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