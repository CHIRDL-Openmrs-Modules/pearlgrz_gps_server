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

	<c:when test="${redirectto == 'redirectto'}">
		
	<!-- 
		<form name="output" action="pearlgrlzsurvey.form" method="get">
	 -->
	 <!-- 
	 	SURVEY COMPLETE.  THANK YOU FOR YOUR CONTRIBUTION.
	 	<form name="output" action="${nextPage}.form" method="get">
			<input type="hidden" name="surveyType" value="${surveyType}" /> 
			<input type="hidden" name="submitAnswers" value="" /> 
			<input type="hidden" name="redirectto" value="" /> 
			
			<input  type="submit" name="Next"  value="Next" />
			
		</form>
	 -->		
		<h2 align="center">Thank you very much for your contribution.</h2>
		<h2 align="center">Please click the Logout button to exit.</h2>
		<br></br>
		<form name="logout" method="post">
			<h2 align="center">
			<!-- 
			<input  type="submit" name="logout"  value="Logout" onclick="exit()"></input>
			 -->
			<input  type="submit" name="logout"  value="Logout" onclick="self.close()"></input>
			</h2>
		</form>

	</c:when>
	
	<c:otherwise>
		<!-- 
    <form name="pearlgrlzSelfSubmitForm"  action="pearlgrlzForm.form" method="post">
     -->
     <!-- 
		<form name="input" action="pearlgrlzsurvey.form" method="get">
	 -->
		<form name="input" action="pearlgrlzsurvey.form" method="get">
		<!-- 
			<h4>Which of these things have you done since your last prompt....${category } </h4>
		 -->
			<h4>Which of these things have you done since your last prompt....</h4>
			<br></br>

			<c:choose>
				<c:when test="${category == 'singleChoice'}">
					<table>
						<tr>
							<td align="center"><b>Y</b></td>
							<td align="center"><b>N</b></td>
							<td></td>
						</tr>
						<tr>
							<td><input type="radio" name="QuestionEntry_1_1" value="Y" /></td>
							<td><input type="radio" name="QuestionEntry_1_1" value="N" /></td>
							<td>${Question1}</td>
						</tr>
						<c:choose>
						<c:when test="${!empty Question2}">
							<tr>
								<td><input type="radio" name="QuestionEntry_2_1" value="Y" /></td>
								<td><input type="radio" name="QuestionEntry_2_1" value="N" /></td>
								<td>${Question2}</td>
							</tr>
						</c:when></c:choose>
						<c:choose>
						<c:when test="${!empty Question3}">
							<tr>
								<td><input type="radio" name="QuestionEntry_3_1" value="Y" /></td>
								<td><input type="radio" name="QuestionEntry_3_1" value="N" /></td>
								<td>${Question3}</td>
							</tr>
						</c:when>
						</c:choose>
						<c:choose>
						<c:when test="${!empty Question4}">
							<tr>
								<td><input type="radio" name="QuestionEntry_4_1" value="Y" /></td>
								<td><input type="radio" name="QuestionEntry_4_1" value="N" /></td>
								<td>${Question4}</td>
							</tr>
						</c:when>
						</c:choose>
						<c:choose>
						<c:when test="${!empty Question5}">
							<tr>
								<td><input type="radio" name="QuestionEntry_5_1" value="Y" /></td>
								<td><input type="radio" name="QuestionEntry_5_1" value="N" /></td>
								<td>${Question5}</td>
							</tr>
						</c:when>
						</c:choose>
					</table>
				</c:when>

				<c:when test="${category == 'singleSelect'}">
					${Question1}
					<select  name="QuestionEntry_1_1" id="QuestionEntry_1_1">
						<c:forEach var="q1" items="${Question1List}">
							<option value="${q1}">${q1}</option>
						</c:forEach> 
					</select>
					
					<br></br>
					<c:choose>
					<c:when test="${!empty Question2}">
						${Question2}
						<select  name="QuestionEntry_2_1" id="QuestionEntry_2_1">
							<c:forEach var="q2" items="${Question2List}">
								<option value="${q2}">${q2}</option>
							</c:forEach> 
						</select>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question3}">
						${Question3}
						<select  name="QuestionEntry_3_1" id="QuestionEntry_3_1">
							<c:forEach var="q3" items="${Question3List}">
								<option value="${q3}">${q3}</option>
							</c:forEach> 
						</select>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question4}">
						${Question4}
						<select  name="QuestionEntry_4_1" id="QuestionEntry_4_1">
							<c:forEach var="q4" items="${Question4List}">
								<option value="${q4}">${q4}</option>
							</c:forEach> 
						</select>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question5}">
						${Question5}
						<select  name="QuestionEntry_5_1" id="QuestionEntry_5_1">
							<c:forEach var="q5" items="${Question5List}">
								<option value="${q5}">${q5}</option>
							</c:forEach> 
						</select>
					</c:when>
					</c:choose>
					<br></br>
				</c:when>

				<c:when test="${category == 'singleInput'}">
					${Question1} <input type="text" name="QuestionEntry_1_1"/>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question2}">
						${Question2} <input type="text" name="QuestionEntry_2_1"/>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question3}">
						${Question3} <input type="text" name="QuestionEntry_3_1"/>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question4}">
						${Question4} <input type="text" name="QuestionEntry_4_1"/>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question5}">
						${Question5} <input type="text" name="QuestionEntry_5_1"/>
					</c:when>
					</c:choose>
					<br></br>
				</c:when>

				<c:when test="${category == 'doubleInput'}">
					<table>
						<tr>	
							<th></th><th>From</th><th>To</th>
						</tr>
						<tr>
							<td>${Question1} </td>
							<td><input type="text" size="8" name="QuestionEntry_1_1"/></td>
							<td><input type="text" size="8" name="QuestionEntry_1_2"/></td>
						</tr>
						<c:choose>
						<c:when test="${!empty Question2}">
						<tr>
							<td>${Question2} </td>
							<td><input type="text" size="8" name="QuestionEntry_2_1"/></td>
							<td><input type="text" size="8" name="QuestionEntry_2_2"/></td>
						</tr>
						</c:when>
						</c:choose>
						<c:choose>
						<c:when test="${!empty Question3}">
						<tr>
							<td>${Question3} </td>
							<td><input type="text" size="8" name="QuestionEntry_3_1"/></td>
							<td><input type="text" size="8" name="QuestionEntry_3_2"/></td>
						</tr>
						</c:when>
						</c:choose>
						<c:choose>
						<c:when test="${!empty Question4}">
						<tr>
							<td>${Question4} </td>
							<td><input type="text" size="8" name="QuestionEntry_4_1"/></td>
							<td><input type="text" size="8" name="QuestionEntry_4_2"/></td>
						</tr>
						</c:when>
						</c:choose>
						<c:choose>
						<c:when test="${!empty Question5}">
						<tr>
							<td>${Question5} </td>
							<td><input type="text" size="8" name="QuestionEntry_5_1"/></td>
							<td><input type="text" size="8" name="QuestionEntry_5_2"/></td>
						</tr>
						</c:when>
						</c:choose>
					</table>
					<br></br>
				</c:when>

				<c:when test="${category== 'multiSelect'}">  <!-- Scrollable TextArea with multiple selection.  -->
				---Select Upto ${multiSelectRoof} of the following---
					${Question1}
						<select  name="QuestionEntry_1_1" size=2 multiple>
							<c:forEach var="q1" items="${Question1List}">
								<option name="${q1}" value="${q1}">${q1}</option>
							</c:forEach> 
						</select>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question2}">
						${Question2}
							<select  name="QuestionEntry_2_1" size=2 multiple>
								<c:forEach var="q2" items="${Question2List}">
									<option name="${q2}" value="${q2}">${q2}</option>
								</c:forEach> 
							</select>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question3}">
						${Question3}
							<select  name="QuestionEntry_3_1" size=2 multiple>
								<c:forEach var="q3" items="${Question3List}">
									<option name="${q3}" value="${q3}">${q3}</option>
								</c:forEach> 
							</select>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question4}">
						${Question4}
							<select  name="QuestionEntry_4_1" size=2 multiple>
								<c:forEach var="q4" items="${Question4List}">
									<option name="${q4}" value="${q4}">${q4}</option>
								</c:forEach> 
							</select>
					</c:when>
					</c:choose>
					<br></br>
					<c:choose>
					<c:when test="${!empty Question5}">
						${Question5}
							<select  name="QuestionEntry_5_1" size=2 multiple>
								<c:forEach var="q5" items="${Question5List}">
									<option name="${q5}" value="${q5}">${q5}</option>
								</c:forEach> 
							</select>
					</c:when>
					</c:choose>
					<br></br>
				</c:when>

				<c:otherwise>
					Failed the above all conditions, now DEFAULTS <br></br>
					<input type="checkbox" name="QuestionEntry_1" />${Question1}<br></br>
					<c:choose>
					<c:when test="${!empty Question2}">
						<input type="checkbox" name="QuestionEntry_2" />${Question2}<br></br>
					</c:when>
					</c:choose>
					<c:choose>
					<c:when test="${!empty Question3}">
						<input type="checkbox" name="QuestionEntry_3" />${Question3}<br></br>
					</c:when>
					</c:choose>
					<c:choose>
					<c:when test="${!empty Question4}">
						<input type="checkbox" name="QuestionEntry_4" />${Question4}<br></br>
					</c:when>
					</c:choose>
					<c:choose>
					<c:when test="${!empty Question5}">
						<input type="checkbox" name="QuestionEntry_5" />${Question5}<br></br>
					</c:when>
					</c:choose>
					<br></br>
				</c:otherwise>
	
			</c:choose>
		
			<br></br>
			
			<!-- 
			<c:choose>
				<c:when test="${formName != 'pearlgrlzsurvey'}">
					<input type="button" value="Prev" onCLick="history.back()" />
				</c:when>
			</c:choose>
		    -->
		    
		 	<input type="submit" value="Next" />
		 	<input type="hidden" name="submitAnswers" value="submitAnswers" /> 
			<br></br>

		 </form>
		
	</c:otherwise>

</c:choose>

</body>
</html>
