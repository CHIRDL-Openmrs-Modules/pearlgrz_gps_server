<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pearl Grlz Survey</title>
</head>

<link  href="${pageContext.request.contextPath}/moduleResources/pearlgrlz/pearlgrlz.css"  type="text/css"  rel="stylesheet"/>

<body>

	<form name="input" action="surveyPage4.form" method="get">
		${Question1}<input type="text" name="QuestionEntry_1"/><br></br>
		<table>
			<tr>
				<th></th>
				<th>From:</th>
				<th>To:</th>
			</tr>
			<tr>
				<td>${Question2}</td>
				<td><input type="text" name="QuestionEntry_2"/></td>
				<td><input type="text" name="QuestionEntry_2-2"/></td>
			</tr>
			<tr>
				<td>${Question3}</td>
				<td><input type="text" name="QuestionEntry_3"/></td>
				<td><input type="text" name="QuestionEntry_3-2"/></td>
			</tr>
			<tr>
				<td>${Question4}</td>
				<td><input type="text" name="QuestionEntry_4"/></td>
				<td><input type="text" name="QuestionEntry_4-2"/></td>
			</tr>
			<tr>
				<td>${Question5}</td>
				<td><input type="text" name="QuestionEntry_5"/></td>
				<td><input type="text" name="QuestionEntry_5-2"/></td>
			</tr>
		
		</table>
		<input type="checkbox" name="QuestionEntry_6"/>${Question6}<br></br>
		<input type="checkbox" name="QuestionEntry_7"/>${Question7}<br></br>
		<input type="button" value="Prev" onCLick="history.back()"/>
		<input  type="submit" value="Next"/>
	</form>

</body>
</html>
