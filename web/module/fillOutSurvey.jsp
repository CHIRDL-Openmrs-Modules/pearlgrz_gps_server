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
			<table style="padding: 10px" width="100%">
					<tr>
						<td style="vertical-align: middle; text-align: center;"><b style="font-size: 22px">Pearl Grlz Survey Form</b></td>
					</tr>
			</table>
		<form name="input" action="fillOutSurvey.form" method="get">
		<table>
			<c:if test="${Question1 != null}">
			<tr>
				<td>${Question1}
			<c:choose>
			<c:when test="${Question1_input_type_1 == 'number_entry'}">
				<input type="text" name="QuestionEntry_1" size="3"/>
			</c:when>
			<c:when test="${Question1_input_type_1 == 'text_entry'}">
				<input type="text" name="QuestionEntry_1" size="10"/>
			</c:when>
			<c:when test="${Question1_input_type_1 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_1" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_1.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question1_input_list_1}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			<c:otherwise>
				<input type="radio" name="QuestionEntry_1" value="Y" /><b>Y</b>
				<input type="radio" name="QuestionEntry_1" value="N" /><b>N</b>
			</c:otherwise>
			</c:choose>
			<c:choose>
			<c:when test="${Question1_input_type_2 == 'number_entry'}">
				<input type="text" name="QuestionEntry_1_2" size="3"/>
			</c:when>
			<c:when test="${Question1_input_type_2 == 'text_entry'}">
				<input type="text" name="QuestionEntry_1_2" size="10"/>
			</c:when>
			<c:when test="${Question1_input_type_2 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_1_2" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_1.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question1_input_list_2}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			</c:choose>
			</td>
			</tr>	
			</c:if>
			<c:if test="${Question2 != null}">
			<tr>
			<td>${Question2}
			<c:choose>
			<c:when test="${Question2_input_type_1 == 'number_entry'}">
				<input type="text" name="QuestionEntry_2" size="3"/>
			</c:when>
			<c:when test="${Question2_input_type_1 == 'text_entry'}">
				<input type="text" name="QuestionEntry_2" size="10"/>
			</c:when>
			<c:when test="${Question2_input_type_1 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_2" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_2.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question2_input_list_1}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			<c:otherwise>
				<input type="radio" name="QuestionEntry_2" value="Y" /><b>Y</b>
				<input type="radio" name="QuestionEntry_2" value="N" /><b>N</b>
			</c:otherwise>
			</c:choose>
			<c:choose>
			<c:when test="${Question2_input_type_2 == 'number_entry'}">
				<input type="text" name="QuestionEntry_2_2" size="3"/>
			</c:when>
			<c:when test="${Question2_input_type_2 == 'text_entry'}">
			<input type="text" name="QuestionEntry_2_2" size="10"/>
			</c:when>
			<c:when test="${Question2_input_type_2 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_2" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_2.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question2_input_list_2}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			</c:choose>
				</td>
			</tr>	
			</c:if>
			<c:if test="${Question3 != null}">
			<tr>
			<td>${Question3}
			<c:choose>
			<c:when test="${Question3_input_type_1 == 'number_entry'}">
				<input type="text" name="QuestionEntry_3" size="3"/>
			</c:when>
			<c:when test="${Question3_input_type_1 == 'text_entry'}">
				<input type="text" name="QuestionEntry_3" size="10"/>
			</c:when>
			<c:when test="${Question3_input_type_1 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_3" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_3.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question3_input_list_1}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			<c:otherwise>
				<input type="radio" name="QuestionEntry_3" value="Y" /><b>Y</b>
				<input type="radio" name="QuestionEntry_3" value="N" /><b>N</b>
			</c:otherwise>
			</c:choose>
			<c:choose>
			<c:when test="${Question3_input_type_2 == 'number_entry'}">
				<input type="text" name="QuestionEntry_3_2" size="3"/>
			</c:when>
			<c:when test="${Question3_input_type_2 == 'text_entry'}">
				<input type="text" name="QuestionEntry_3_2" size="10"/>
			</c:when>
			<c:when test="${Question3_input_type_2 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_3" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_3.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question3_input_list_2}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			</c:choose>
				</td>
			</tr>	
			</c:if>
			<c:if test="${Question4 != null}">
			<tr>
			<td>${Question4}
			<c:choose>
			<c:when test="${Question4_input_type_1 == 'number_entry'}">
				<input type="text" name="QuestionEntry_4" size="3"/>
			</c:when>
			<c:when test="${Question4_input_type_1 == 'text_entry'}">
				<input type="text" name="QuestionEntry_4" size="10"/>
			</c:when>
			<c:when test="${Question4_input_type_1 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_4" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_4.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question4_input_list_1}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			<c:otherwise>
				<input type="radio" name="QuestionEntry_4" value="Y" /><b>Y</b>
				<input type="radio" name="QuestionEntry_4" value="N" /><b>N</b>
			</c:otherwise>
			</c:choose>
			<c:choose>
			<c:when test="${Question4_input_type_2 == 'number_entry'}">
				<input type="text" name="QuestionEntry_4_2" size="3"/>
			</c:when>
			<c:when test="${Question4_input_type_2 == 'text_entry'}">
				<input type="text" name="QuestionEntry_4_2" size="10"/>
			</c:when>
			<c:when test="${Question4_input_type_2 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_4" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_4.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question4_input_list_2}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			</c:choose>
				</td>
			</tr>
			</c:if>
			<c:if test="${Question5 != null}">	
			<tr>
				<td>${Question5}
			<c:choose>
			<c:when test="${Question5_input_type_1 == 'number_entry'}">
				<input type="text" name="QuestionEntry_5" size="3"/>
			</c:when>
			<c:when test="${Question5_input_type_1 == 'text_entry'}">
				<input type="text" name="QuestionEntry_5" size="10"/>
			</c:when>
			<c:when test="${Question5_input_type_1 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_5" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_5.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question5_input_list_1}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			<c:otherwise>
				<input type="radio" name="QuestionEntry_5" value="Y" /><b>Y</b>
				<input type="radio" name="QuestionEntry_5" value="N" /><b>N</b>
			</c:otherwise>
			</c:choose>
			<c:choose>
			<c:when test="${Question5_input_type_2 == 'number_entry'}">
				<input type="text" name="QuestionEntry_5_2" size="3"/>
			</c:when>
			<c:when test="${Question5_input_type_2 == 'text_entry'}">
				<input type="text" name="QuestionEntry_5_2" size="10"/>
			</c:when>
			<c:when test="${Question5_input_type_2 == 'dropdown_entry'}">
				<table cellpadding="0" cellspacing="0">
				<tr>
				<td>
				<input type="text" name="QuestionEntry_5" value=""  size="10" >
				</td>
				</tr>
				<tr>
				<td>
				<select name="s" size="1"
onchange="document.input.QuestionEntry_5.value = document.input.s.options[document.input.s.selectedIndex].value;document.input.s.value=''">
				<option value="" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
				<c:forEach items="${Question5_input_list_2}" var="previousPartner">
					<option  value="${previousPartner}">${previousPartner}</option>
				</c:forEach>
				</select>
				</td>
				</tr>
			</table>
			</c:when>
			</c:choose>
			</td>
			</tr>	
			</c:if>
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
