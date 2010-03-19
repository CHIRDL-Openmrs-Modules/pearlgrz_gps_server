<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Logout</title>

<script language="JavaScript">

function exit() {
  close();
}

</script>

</head>
<body>
	<%@ include file="surveyHeader.jsp" %>
	<br></br>
	
<h2 align="center">Thank you very much for your contribution.</h2>
<h2 align="center">Please click the Logout button to exit.</h2>
<br></br>
<form name="logout" method="post">
	<h2 align="center">
	<input  type="submit" name="logout"  value="Logout" onclick="exit()"></input>
	</h2>
</form>
</body>
</html>
