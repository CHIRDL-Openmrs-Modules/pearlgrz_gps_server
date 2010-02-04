<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="org.openmrs.web.WebConstants"%>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
	
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<openmrs:htmlInclude file="/openmrs.css" />
<openmrs:htmlInclude file="/style.css" />
<openmrs:htmlInclude file="/openmrs.js" />

<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
		</script>


<link
	href="${pageContext.request.contextPath}/moduleResources/pearlgrlz/pearlgrlz.css"
	type="text/css" rel="stylesheet" />

<title>Please choose the survey candidate patient id</title>

</head>

<body>
<div id="pageBody"/>
<div id="contentMinimal"/>

<c:if test="${msg != null}">
	<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /> </div>
</c:if> 

<c:if test="${err != null}">
	<div id="openmrs_error"><spring:message code="${err}"  text="${err}" arguments="${errArgs}" /></div>
</c:if>


<!--  This information will eventually come from a user login  -->
<form name="fillOutSurvey.form" method="post">

<p><b>Please enter the patient id of the survey participant:</b></p>
<b>Patient id: </b>&nbsp;<input type="text" size="8" name="patientId"
	tabindex="1" /><br>
<br>
<table>
	<tr>
		<td><input type="button" value="Cancel" onclick='window.close()'
			tabindex="3" /></td>
		<td><input type="submit" value="OK" tabindex="2" /></td>
	</tr>
</table>
</form>
<br />
</div>
</div>
</body>
</html>