<%@ include file="/WEB-INF/template/include.jsp"%>

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
	<%@ include file="surveyHeader.jsp" %>
	
	
	<c:choose>

	<c:when test="${redirectto} == 'redirectto'">
		COMPLETE.
	<!-- 
		<form name="output" action="surveyPearlgrlzPage1.form" method="get">
	 -->
	 	<form name="output" action="${nextPage}.form" method="get">
			<input type="hidden" name="surveyType" value="${surveyType}" /> 
			<input type="hidden" name="submitAnswers" value="" /> 
			<input type="hidden" name="redirectto" value="" /> 
		</form>

	</c:when>

	<c:otherwise>
		<!-- 
    <form name="pearlgrlzSelfSubmitForm"  action="pearlgrlzForm.form" method="post">
     -->
     <!-- 
		<form name="input" action="surveyPearlgrlzPage1.form" method="get">
	 -->
		<form name="input" action="surveyPearlgrlzPage1.form" method="get">
			<input type="checkbox" name="QuestionEntry_1" />${Question1}<br></br>
			<input type="checkbox" name="QuestionEntry_2" />${Question2}<br></br>
			<input type="checkbox" name="QuestionEntry_3" />${Question3}<br></br>
			<input type="checkbox" name="QuestionEntry_4" />${Question4}<br></br>
			<input type="checkbox" name="QuestionEntry_5" />${Question5}<br></br>

			<%@ include file="surveyFooter.jsp"%> 
			<c:choose>
				<c:when test="${formName} != 'surveyPearlgrlzPage1'">
					<input type="button" value="Prev" onCLick="history.back()" />
				</c:when>
			</c:choose>
		
		 	<input type="submit" value="Next" />
		 	<input type="hidden" name="submitAnswers" value="submitAnswers" /> 
		 </form>
		

	</c:otherwise>

</c:choose>

</body>
</html>
