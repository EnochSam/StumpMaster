<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Robert A Poopie</title>
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
	<form method="get" id = "Login" action="${pageContext.servletContext.contextPath}/Login">
		<input type="submit" value="Log Out">
	</form>
</center>
    
</body>
<script>
	
	if("${! empty user}" != "true" && "${username}".length == 0){
	let repost = document.getElementById("Login")
	repost.submit();
	}
</script>
</html>