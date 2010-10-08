<%@ include file="/WEB-INF/template/include.jsp"%>

<head>
<script language="JavaScript">
function selfSubmit(){location.href=document.pearlgrlzSelfSubmitForm.submit()};
</script>
</head>

<link
	href="${pageContext.request.contextPath}/moduleResources/pearlgrlz/pearlgrlz.css"
	type="text/css" rel="stylesheet" />
<c:choose>
	<c:when test="${finished == 'finished'}">
		<p><h2>Thank you very much for your time and effort.</h2></p>
	</c:when>
	<c:otherwise>
		<form name="input" action="fillOutSurvey.form" method="get">
			<table style="padding: 10px" width="100%">
					<tr>
						<td style="vertical-align: middle; text-align: center;">${formInstance}</td>
						<td style="vertical-align: middle; text-align: center;"><b style="font-size: 22px">Pearl Grlz Survey Form</b></td>
						<td style="text-align: left">Name:  ${PatientName}</td>
						<td style="text-align: left">Date: ${CurrentDate}</td>
					</tr>
			</table>
		
		<table>
			<tr>
				<td align="center"><b>Y</b></td>
				<td align="center"><b>N</b></td>
				<td></td>
			</tr>
			<tr>
			<c:choose>
			<c:when test="${Question1_input_type == 'number_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_1" size="3"/></td>
			</c:when>
			<c:when test="${Question1_input_type == 'dropdown_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_1" size="10"/></td>
			</c:when>
			<c:otherwise>
				<td><input type="radio" name="QuestionEntry_1" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_1" value="N" /></td>
			</c:otherwise>
			</c:choose>
				<td>${Question1}</td>
			</tr>	
			<tr>
			<c:choose>
			<c:when test="${Question2_input_type == 'number_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_2" size="3"/></td>
			</c:when>
			<c:when test="${Question2_input_type == 'dropdown_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_2" size="10"/></td>
			</c:when>
			<c:otherwise>
				<td><input type="radio" name="QuestionEntry_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_2" value="N" /></td>
			</c:otherwise>
			</c:choose>
				<td>${Question2}</td>
			</tr>	
			<tr>
			<c:choose>
			<c:when test="${Question3_input_type == 'number_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_3" size="3"/></td>
			</c:when>
			<c:when test="${Question3_input_type == 'dropdown_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_3" size="10"/></td>
			</c:when>
			<c:otherwise>
				<td><input type="radio" name="QuestionEntry_3" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_3" value="N" /></td>
			</c:otherwise>
			</c:choose>
				<td>${Question3}</td>
			</tr>	
			<tr>
			<c:choose>
			<c:when test="${Question4_input_type == 'number_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_4" size="3"/></td>
			</c:when>
			<c:when test="${Question4_input_type == 'dropdown_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_4" size="10"/></td>
			</c:when>
			<c:otherwise>
				<td><input type="radio" name="QuestionEntry_4" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_4" value="N" /></td>
			</c:otherwise>
			</c:choose>
				<td>${Question4}</td>
			</tr>	
			<tr>
			<c:choose>
			<c:when test="${Question5_input_type == 'number_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_5" size="3"/></td>
			</c:when>
			<c:when test="${Question5_input_type == 'dropdown_entry'}">
				<td colspan=2><input type="text" name="QuestionEntry_5" size="10"/></td>
			</c:when>
			<c:otherwise>
				<td><input type="radio" name="QuestionEntry_5" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_5" value="N" /></td>
			</c:otherwise>
			</c:choose>
				<td>${Question5}</td>
			</tr>		
		</table>
		
		<p>
		<table>
		<tr>
		<td>
			<input  type="submit" value="Next"/>
		</td>
		</tr>
		</table>
		</p>
		
		<input type="hidden" name="submitAnswers" value="submitAnswers" /> 
		<input type="hidden" value="${formInstance}" name="formInstance" />
		<input type="hidden" name="patientId" value="${patientId}" /> 
		<input type="hidden"  name="providerId" value="${providerId}" /> 
		<input  type="hidden" name="locationId" value="${locationId}" /> 
		<input  type="hidden" name="locationTagId" value="${locationTagId}" />
		<input type="hidden" name="encounterId" value="${encounterId}" />
		<input type="hidden" name="sessionId" value="${sessionId}" />
		</form>
</c:otherwise>
</c:choose>
