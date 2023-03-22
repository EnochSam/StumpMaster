<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login</title>
</head>
<body>
<h1 class="title">Login</h1>
<br>
<form method="post" action="${pageContext.servletContext.contextPath}/Login">
	<input type="text" name="username">
	<br>
	<input type="text" name="password">
	<br><br>
	<input type="submit" value="Log In">
</form>
</body>
</html>