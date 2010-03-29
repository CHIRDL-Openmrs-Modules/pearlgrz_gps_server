/********************************************************************
 Translated from - timeForForthDrink.mlm on Thu Mar 18 15:42:18 EDT 2010

 Title:  drinking alcohol related Question
 Filename:  timeForForthDrink.mlm
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Jun Wang
 Specialist:  Software Engineer
 Date: 2010-01-29T16:12:57-0500
 Validation : research
 Purpose:  Identify drinking alcohol related problems. , , doubleInput
 Explanation: 
 Keywords: 
 Citations: 
 Links: 
********************************************************************/
package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.dss.DssRule;
import org.openmrs.logic.Duration;
import java.util.StringTokenizer;

import org.openmrs.api.ConceptService;
import java.text.SimpleDateFormat;
public class timeForForthDrink implements Rule, DssRule{

	private Patient patient;
	private String firstname;
	private ArrayList<String> actions;
	private HashMap<String, String> userVarMap;

	private HashMap <String, Result> resultLookup;

	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/*** @see org.openmrs.logic.rule.Rule#getDuration()*/
	public int getDuration() {
		return 60*30;   // 30 minutes
	}

	/*** @see org.openmrs.logic.rule.Rule#getDatatype(String)*/
	public Datatype getDatatype(String token) {
		return Datatype.TEXT;
	}

	/*** @see org.openmrs.logic.rule.Rule#getParameterList()*/
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/*** @see org.openmrs.logic.rule.Rule#getDependencies()*/
	public String[] getDependencies() {
		return new String[] { };
	}

	/*** @see org.openmrs.logic.rule.Rule#getTTL()*/
	public int getTTL() {
		return 0; //60 * 30; // 30 minutes
	}

	/*** @see org.openmrs.logic.rule.Rule#getDatatype(String)*/
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAuthor()*/
	public String getAuthor(){
		return "Jun Wang";
	}

	/*** @see org.openmrs.module.dss.DssRule#getCitations()*/
	public String getCitations(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getDate()*/
	public String getDate(){
		return "2010-01-29T16:12:57-0500";
	}

	/*** @see org.openmrs.module.dss.DssRule#getExplanation()*/
	public String getExplanation(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getInstitution()*/
	public String getInstitution(){
		return "Indiana University School of Medicine";
	}

	/*** @see org.openmrs.module.dss.DssRule#getKeywords()*/
	public String getKeywords(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getLinks()*/
	public String getLinks(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPurpose()*/
	public String getPurpose(){
		return "Identify drinking alcohol related problems. , , doubleInput";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Software Engineer";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "drinking alcohol related Question";
	}

	/*** @see org.openmrs.module.dss.DssRule#getVersion()*/
	public Double getVersion(){
		return 1.0;
	}

	/*** @see org.openmrs.module.dss.DssRule#getType()*/
	public String getType(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPriority()*/
	public Integer getPriority(){
		return 134;
	}

	/*** @see org.openmrs.module.dss.DssRule#getData()*/
	public String getData(){
		return "read read read read read If read read read endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLogic()*/
	public String getLogic(){
		return "If If conclude conclude endif If If CALL CALL endif endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMin()*/
	public Integer getAgeMin(){
		return 14;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMinUnits()*/
	public String getAgeMinUnits(){
		return "years";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMax()*/
	public Integer getAgeMax(){
		return 16;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMaxUnits()*/
	public String getAgeMaxUnits(){
		return "years";
	}

private static boolean containsIgnoreCase(Result key,List<Result> lst){
for(Result element:lst){
if(key != null&&key.toString().equalsIgnoreCase(element.toString())){
	return true;
}
}	
return false;
}
	private static String toProperCase(String str){

		if(str == null || str.length()<1){
			return str;
		}

		StringBuffer resultString = new StringBuffer();
		String delimiter = " ";
		StringTokenizer tokenizer = new StringTokenizer(str,delimiter,true);
		String currToken = null;

		while(tokenizer.hasMoreTokens()){
			currToken = tokenizer.nextToken();
			if(!currToken.equals(delimiter)){
				if(currToken.length()>0){
					currToken = currToken.substring(0, 1).toUpperCase()
					+ currToken.substring(1).toLowerCase();
				}
			}
			resultString.append(currToken);
		}
		return resultString.toString();
	}

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException {

		String actionStr = "";
		resultLookup = new HashMap <String, Result>();
		Boolean ageOK = null;

		try {
			this.patient=patient;
			userVarMap = new HashMap <String, String>();
			firstname = patient.getPersonName().getGivenName();
			userVarMap.put("firstname", toProperCase(firstname));
			String lastName = patient.getFamilyName();
			userVarMap.put("lastName", lastName);
			String gender = patient.getGender();
			userVarMap.put("Gender", gender);
			if(gender.equalsIgnoreCase("M")){
				userVarMap.put("gender","his");
				userVarMap.put("hisher","his");
			}else{
				userVarMap.put("gender","her");
				userVarMap.put("hisher","her");
			}
			initAction();

			Result mode=new Result((String) parameters.get("mode"));
			resultLookup.put("mode",mode);
			Result Input1=new Result((String) parameters.get("Input1"));
			resultLookup.put("Input1",Input1);
			Result Input2=new Result((String) parameters.get("Input2"));
			resultLookup.put("Input2",Input2);
			Result Input1Value=new Result((String) parameters.get("Input1Value"));
			resultLookup.put("Input1Value",Input1Value);
			Result Input2Value=new Result((String) parameters.get("Input2Value"));
			resultLookup.put("Input2Value",Input2Value);		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){

			Result drankAlcohol=context.read(
				patient,context.getLogicDataSource("obs"),
				new LogicCriteria("Drank alcohol").within(Duration.days(-1)).last());
			resultLookup.put("drankAlcohol",drankAlcohol);
			Result frequency=context.read(
				patient,context.getLogicDataSource("obs"),
				new LogicCriteria("Number of times drinking alcohol since last survey").within(Duration.days(-1)).last());
			resultLookup.put("frequency",frequency);
			Result answered1=context.read(
				patient,context.getLogicDataSource("obs"),
				new LogicCriteria("Starting-time for the Forth drinking since last survey").within(Duration.days(-1)).last());
			resultLookup.put("answered1",answered1);
			Result answered2=context.read(
				patient,context.getLogicDataSource("obs"),
				new LogicCriteria("Ending-time for the Forth drinking since last survey").within(Duration.days(-1)).last());
			resultLookup.put("answered2",answered2);}

			if(evaluate_logic(parameters)){
				Result ruleResult = new Result();
				for(String currAction:actions){
					currAction = doAction(currAction);
					ruleResult.add(new Result(currAction));
				}
				return ruleResult;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return Result.emptyResult();
		}
		return Result.emptyResult();
	}

	private boolean evaluate_logic(Map<String, Object> parameters) throws LogicException {

		Result Gender = new Result(userVarMap.get("Gender"));
		Result Input1Value = (Result) resultLookup.get("Input1Value");
		Result Input2 = (Result) resultLookup.get("Input2");
		Result Input1 = (Result) resultLookup.get("Input1");
		Result answered1 = (Result) resultLookup.get("answered1");
		Result answered2 = (Result) resultLookup.get("answered2");
		Result frequency = (Result) resultLookup.get("frequency");
		Result Input2Value = (Result) resultLookup.get("Input2Value");
		Result drankAlcohol = (Result) resultLookup.get("drankAlcohol");
		Result mode = (Result) resultLookup.get("mode");

				String value = null;
				String variable = null;
				int varLen = 0;
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
		if((!drankAlcohol.isNull()&&drankAlcohol.toString().equalsIgnoreCase("Yes"))&&
			(!frequency.isNull()&&frequency.toString().equalsIgnoreCase("Four or more times"))&&
			!answered1.exists()||
			!answered2.exists()){
			return true;
		}
			return false;
		}
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("CONSUME"))){
		if((!Input1.isNull()&&Input1.toString().equalsIgnoreCase("true"))&&
			(!Input2.isNull()&&Input2.toString().equalsIgnoreCase("true"))){
				varLen = "Starting-time for the Forth drinking since last survey".length();
				value=userVarMap.get("Starting-time for the Forth drinking since last survey");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Starting-time for the Forth drinking since last survey".endsWith("_value"))
				{
					variable = "Starting-time for the Forth drinking since last survey".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Starting-time for the Forth drinking since last survey".endsWith("_date"))
				{
					variable = "Starting-time for the Forth drinking since last survey".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else
				{
					if (resultLookup.get("Starting-time for the Forth drinking since last survey") != null){
						value = resultLookup.get("Starting-time for the Forth drinking since last survey").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Starting-time for the Forth drinking since last survey");
				}
				varLen = "Input1Value".length();
				value=userVarMap.get("Input1Value");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("Input1Value".endsWith("_value"))
				{
					variable = "Input1Value".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Input1Value".endsWith("_date"))
				{
					variable = "Input1Value".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else
				{
					if (resultLookup.get("Input1Value") != null){
						value = resultLookup.get("Input1Value").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","Input1Value");
				}
				logicService.eval(patient, "storeObs",parameters);
								varLen = "Ending-time for the Forth drinking since last survey".length();
				value=userVarMap.get("Ending-time for the Forth drinking since last survey");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Ending-time for the Forth drinking since last survey".endsWith("_value"))
				{
					variable = "Ending-time for the Forth drinking since last survey".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Ending-time for the Forth drinking since last survey".endsWith("_date"))
				{
					variable = "Ending-time for the Forth drinking since last survey".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else
				{
					if (resultLookup.get("Ending-time for the Forth drinking since last survey") != null){
						value = resultLookup.get("Ending-time for the Forth drinking since last survey").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Ending-time for the Forth drinking since last survey");
				}
				varLen = "Input2Value".length();
				value=userVarMap.get("Input2Value");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("Input2Value".endsWith("_value"))
				{
					variable = "Input2Value".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Input2Value".endsWith("_date"))
				{
					variable = "Input2Value".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else
				{
					if (resultLookup.get("Input2Value") != null){
						value = resultLookup.get("Input2Value").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","Input2Value");
				}
				logicService.eval(patient, "storeObs",parameters);
				}}	return false;	}

	public void initAction() {
		this.actions = new ArrayList<String>();
		actions.add("When was the Forth drinking since last survey?");
	}

private String substituteString(String variable,String outStr){
//see if the variable is in the user map
String value = userVarMap.get(variable);
if (value != null)
{
}
// It must be a result value or date
else if (variable.contains("_value"))
{
	variable = variable.replace("_value","").trim();
if(resultLookup.get(variable) != null){value = resultLookup.get(variable).toString();
}}
// It must be a result date
else if (variable.contains("_date"))
{
String pattern = "MM/dd/yy";
SimpleDateFormat dateForm = new SimpleDateFormat(pattern);
variable = variable.replace("_date","").trim();
if(resultLookup.get(variable) != null){value = dateForm.format(resultLookup.get(variable).getResultDate());
}}
else
{
if(resultLookup.get(variable) != null){value = resultLookup.get(variable).toString();
}}
if (value != null)
{
	outStr += value;
}
return outStr;
}
public String doAction(String inStr)
{
int startindex = -1;
int endindex = -1;
int index = -1;
String outStr = "";
while((index = inStr.indexOf("||"))>-1)
{
if(startindex == -1){
startindex = 0;
outStr+=inStr.substring(0,index);
}else if(endindex == -1){
endindex = index-1;
String variable = inStr.substring(startindex, endindex).trim();
outStr = substituteString(variable,outStr);
startindex = -1;
endindex = -1;
}
inStr = inStr.substring(index+2);
}
outStr+=inStr;
return outStr;
}
}