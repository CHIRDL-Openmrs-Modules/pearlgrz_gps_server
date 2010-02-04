<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2><spring:message code="Pearl Grlz Survey." /></h2>

<br/ >
<table border="1">
  <tr>
   <th>Participant Id</th>
   <th>Name</th>
   <th>Identifier (MRN or SSN)</th>
  </tr>
  <c:forEach var="patient" items="${theSurveySubjectList}">
      <tr>
        <td>${patient.patientId}</td>
        <td>${patient.personName}</td>
        <td>${patient.patientIdentifier}</td>
      </tr>		
  </c:forEach>
</table>

<p/>
<h3>Checking the Suvery Participants.</h3>


<%@ include file="/WEB-INF/template/footer.jsp"%>
