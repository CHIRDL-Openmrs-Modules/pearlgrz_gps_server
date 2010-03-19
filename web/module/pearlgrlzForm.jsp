<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<%@ include file="/WEB-INF/template/include.jsp"%>

<script type="text/JavaScript">
function selfSubmit(){location.href=document.pearlgrlzSelfSubmitForm.submit()};
</script>


<h2 align="center">Pearl Grlz Survey.</h2>

<c:choose>
	<c:when test="${nonInit == 'nonInit'}">
		<p>auto-submit</p>
		<form name="pearlgrlzSelfSubmitForm" action="pearlgrlzForm.form" method="post">
			<input  type="hidden" name="patientId" value="${patientId}" /> 
			<input   type="hidden" name="providerId" value="${providerId}" /> 
			<input   type="hidden" name="locationId" value="${locationId}" /> 
			<input  type="hidden" name="locationTagId" value="${locationTagId}" /> 
			<input  type="hidden" name="formName" value="${formName}" /> 
			<input  type="hidden" name="formNumberQuestions" value="${formNumberQuestions}" /> 
			<input type="submit" value="Continue""/>
		</form>
		<script>document.pearlgrlzSelfSubmitForm.submit();</script>
	</c:when>



<c:otherwise>
<form name="pearlgrlz.form" method="post">
Please select a participant to fill out survey: 
<b>
<select  name="participantDropDown" id="patientId">
	<option value="Select one" >---Select One---</option>
	<c:forEach var="pt" items="${theSurveySubjectList}">
		<option value="${pt.patientId}">${pt.personName }</option>
	</c:forEach> 
</select>
</b>
<br></br>
Please select a Survey Provider: 
	<c:forEach var="pvdt" items="${theProviderList}">
		<input type="radio" name="providerRadioButton" value="${pvdt.userId}">${pvdt.username}</input>
	</c:forEach> <br></br>
Please select a Location: 
<select  name="locationDropDown" id="locationId">
	<option value="Select one" >---Select One---</option>
	<c:forEach var="lctn" items="${locationList}">
		<option value="${lctn.locationId}">${lctn.name}</option>
	</c:forEach> 
</select>
<br></br>
Please select a Sub-Location: 
<select  name="locationTagDropDown" id="locationTagId">
	<option value="Select one" >---Select One---</option>
	<c:forEach var="tg" items="${locationTagList}">
		<option value="${tg.locationTagId}">${tg.tag}</option>
	</c:forEach> 
</select>
<br></br>
Please select a Form: 
<select  name="formDropDown" id="formName">
	<option value="Select one" >---Select One---</option>
	<c:forEach var="fm" items="${formList}">
		<option value="${fm.name}">${fm.name }</option>
	</c:forEach> 
</select>
<br></br>
Please select a Instance ID: 
<select  name="instanceDropDown" id="instanceId">
	<option value="Select one" >---Select One---</option>
	<option value="1">1</option>
	<option value="2">2</option>
	<option value="3">3</option>
	<option value="4">4</option>
	<option value="5">5</option>
</select>
<br></br>
<input type="submit" value="Take Survey""/>
</form>

<br></br>
<br></br>
<h3>Checking the Suvery Participants.</h3>
<table border="1">
  <tr bgcolor="#FFFFFF" onMouseOver="this.bgColor='gold';" onMouseOut="this.bgColor='#FFFFFF';">
   <th>Participant Id</th>
   <th>Name</th>
   <th>Identifier (MRN or SSN)</th>
  </tr>
  <c:forEach var="patient" items="${theSurveySubjectList}">
      <tr bgcolor="#FFFFFF" onMouseOver="this.bgColor='yellow';" onMouseOut="this.bgColor='#FFFFFF';">
        <td>${patient.patientId}</td>
        <td>${patient.personName}</td>
        <td>${patient.patientIdentifier}</td>
      </tr>		
  </c:forEach>
</table>

<br></br>
<h3>Checking the Suvery Providers.</h3>
<table border="1">
  <tr bgcolor="#FFFFFF" onMouseOver="this.bgColor='gold';" onMouseOut="this.bgColor='#FFFFFF';">
   <th>Provider Id</th>
   <th>Name</th>
   <th>Identifier (MRN or SSN)</th>
  </tr>
  <c:forEach var="prvd" items="${theProviderList}">
      <tr bgcolor="#FFFFFF" onMouseOver="this.bgColor='yellow';" onMouseOut="this.bgColor='#FFFFFF';">
        <td>${prvd.userId}</td>
        <td>${prvd.username}</td>
        <td>N/A</td>
      </tr>		
  </c:forEach>
</table>
</c:otherwise>
</c:choose>

<p/>


