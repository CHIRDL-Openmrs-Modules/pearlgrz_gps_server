<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

	<center><b style="font-size: 22px"> Pearl Grlz Survey</b> </center>
	<table style="padding: 10px" width="100%">
		<tr>
			<td style="vertical-align: middle; text-align: left;">${formInstance}</td>
			<td style="text-align: left"> ${PatientName}</td>
			<td style="text-align: left"> ${CurrentDate}</td>
			<!--  
			<td  style="text-align: left"><input  type="submit" name="logout"  value="Take a break" onclick="self.close()"></input></td>
			-->
			<td style="text-align: left"> <a href="/openmrs/logout"><b>Take a break</b></a></td>
		</tr>
	</table>
	<br></br>
