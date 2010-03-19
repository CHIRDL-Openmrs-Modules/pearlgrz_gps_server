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
	
	<c:when test="${contSurvey == 'contSurvey'}">
	<p><h2>going to call pearlgrlzForm.form</h2></p>
		<form name="pearlgrlzSelfSubmitForm"  action="pearlgrlzForm.form" method="post">
			<input type="hidden" name="patientId" value="${patientId}" /> 
			<input type="hidden"  name="providerId" value="${providerId}" /> 
			<input  type="hidden" name="locationId" value="${locationId}" /> 
			<input  type="hidden" name="locationTagId" value="${locationTagId}" /> 
			<input  type="hidden" name="formName" value="${formName}" /> 
			<input  type="hidden" name="formNumberQuestions" value="${formNumberQuestions}" /> 
			<input type="hidden" name="formInstance" value="${formInstance}"  />
			<input  type="hidden" name="nonInit" value="${nonInit}" /> 
			<input type="submit" value="Continue""/>
		</form>
		<script>document.pearlgrlzSelfSubmitForm.submit();</script>
	</c:when> 
	
<c:when test="${formNumberQuestions <= 5}">
<table style="padding: 10px" width="100%">
					<tr>
						<td style="vertical-align: middle; text-align: center;">${formInstance}</td>
						<td style="vertical-align: middle; text-align: center;"><b style="font-size: 22px">Pearl Grlz Survey Form</b></td>
						<td style="text-align: left">Name:  ${PatientName}</td>
						<td style="text-align: left">Date: ${CurrentDate}</td>
					</tr>
			</table>
			
		<form name="input" action="fillOutSurvey.form" method="get">
			<br></br>
			<table>
				<tr>	
					<th></th><th>From ~ To</th>
				</tr>
				<tr>
					<td>${Question1}</td>
					<td> <input type="text" name="QuestionEntry_1"/> </td>
				</tr>
				<tr>
					<td>${Question2}</td>
					<td> <input type="text" name="QuestionEntry_2"/> </td>
				</tr>
				<tr>
					<td>${Question3}</td>
					<td> <input type="text" name="QuestionEntry_3"/> </td>
				</tr>
				<tr>
					<td>${Question4}</td>
					<td> <input type="text" name="QuestionEntry_4"/> </td>
				</tr>
				<tr></tr>
				
				<tr>
					<td>${Question5}</td>
					<td><input type="text" name="QuestionEntry_5" /></td>
				</tr>
			</table>
			
		<p>
		<table>
		<tr>
		<td>
			<input type="button" value="Prev" onCLick="history.back()"/>
		</td> <td></td>
		<td>
			<input  type="submit" value="Next"/>
			</td>
		</tr>
		</table>
		 </p>
		 
		</form>
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
		
		<p><b>English Questions</b></p>
		<table>
			<tr>
				<td align="center"><b>Y</b></td>
				<td align="center"><b>N</b></td>
				<td></td>
				<td align="center"><b>Y</b></td>
				<td align="center"><b>N</b></td>
				<td></td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_1" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_1" value="N" /></td>
				<td>${Question1}</td>
				<td><input type="radio" name="QuestionEntry_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_2" value="N" /></td>
				<td>${Question2}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_3" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_3" value="N" /></td>
				<td>${Question3}</td>
				<td><input type="radio" name="QuestionEntry_4" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_4" value="N" /></td>
				<td>${Question4}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_5" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_5" value="N" /></td>
				<td>${Question5}</td>
				<td><input type="radio" name="QuestionEntry_6" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_6" value="N" /></td>
				<td>${Question6}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_7" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_7" value="N" /></td>
				<td>${Question7}</td>
				<td><input type="radio" name="QuestionEntry_8" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_8" value="N" /></td>
				<td>${Question8}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_9" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_9" value="N" /></td>
				<td>${Question9}</td>
				<td><input type="radio" name="QuestionEntry_10" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_10" value="N" /></td>
				<td>${Question10}</td>
			</tr>
		</table>
		
		<p>
		<table>
		<tr>
		<td>
			<input type="button" value="Prev" onCLick="history.back()"/>
		</td> <td></td>
		<td>
			<input  type="submit" value="Next"/>
			</td>
		</tr>
		</table>
		</p>
		
		<p><b>Spanish Questions</b></p>
		<table>
			<tr>
				<td align="center"><b>Y</b></td>
				<td align="center"><b>N</b></td>
				<td></td>
				<td align="center"><b>Y</b></td>
				<td align="center"><b>N</b></td>
				<td></td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_1_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_1_2" value="N" /></td>
				<td>${Question1_SP}</td>
				<td><input type="radio" name="QuestionEntry_2_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_2_2" value="N" /></td>
				<td>${Question2_SP}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_3_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_3_2" value="N" /></td>
				<td>${Question3_SP}</td>
				<td><input type="radio" name="QuestionEntry_4_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_4_2" value="N" /></td>
				<td>${Question4_SP}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_5_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_5_2" value="N" /></td>
				<td>${Question5_SP}</td>
				<td><input type="radio" name="QuestionEntry_6_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_6_2" value="N" /></td>
				<td>${Question6_SP}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_7_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_7_2" value="N" /></td>
				<td>${Question7_SP}</td>
				<td><input type="radio" name="QuestionEntry_8_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_8_2" value="N" /></td>
				<td>${Question8_SP}</td>
			</tr>
			<tr>
				<td><input type="radio" name="QuestionEntry_9_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_9_2" value="N" /></td>
				<td>${Question9_SP}</td>
				<td><input type="radio" name="QuestionEntry_10_2" value="Y" /></td>
				<td><input type="radio" name="QuestionEntry_10_2" value="N" /></td>
				<td>${Question10_SP}</td>
			</tr>
		</table>
		<input type="hidden" name="submitAnswers" value="submitAnswers" /> 
		<input type="hidden" value="${formInstance}" name="formInstance" />

		</form>
	</c:otherwise>
</c:choose>
