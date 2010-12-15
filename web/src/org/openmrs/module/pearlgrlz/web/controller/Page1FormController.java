/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pearlgrlz.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FieldType;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.pearlgrlz.SurveySession;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * This controller backs the /web/module/surveyPage1.jsp page. This controller is tied to that
 * jsp page in the /metadata/moduleApplicationContext.xml file
 */
public class Page1FormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	public final static String SURVEY_STATE_NAME = "pearlgrlz_survey";
	
	
	/**
	 * Returns any extra data in a key-->value pair kind of way
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {
		Map<String, Object>  modelMap = new HashMap<String, Object> ();
	/*	PearlgrlzService pearlgrlzSvc = Context.getService(PearlgrlzService.class);
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		State atdState = atdService.getStateByName(SURVEY_STATE_NAME);
		PatientState patientState = null;
		Session session = null;
		String nextPage = "";
		Integer formId = null;
		Integer numberOfQuestions = null;
		UserService userService = Context.getUserService();
		String formInstanceString = request.getParameter("formInstance");
		FormInstance formInstance = null;
		if(formInstanceString!= null){
			StringTokenizer tokenizer = new StringTokenizer(formInstanceString,"_");
			Integer locationId = null;
			Integer formInstanceId = null;
			if(tokenizer.hasMoreTokens()){
				try
				{
					locationId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}

			if(tokenizer.hasMoreTokens()){
				try
				{
					formId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}
			
			if(tokenizer.hasMoreTokens()){
				try
				{
					formInstanceId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}
			formInstance = new FormInstance(locationId,formId,formInstanceId);
		}

		Patient patient = patientService.getPatient(Context.getAuthenticatedUser().getPersonId());
		//if(patient == null)	
			//pearlgrlzSvc.getPatientByUserId(Context.getAuthenticatedUser().getPersonId());

		
		
		Integer locationId = 1;
		Integer locationTagId = 1;
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		String providerId = request.getParameter("providerId");
		User provider = userService.getUser(Integer.parseInt(providerId));

		
		String submitAnswers = request.getParameter("submitAnswers");
		TeleformTranslator translator = new TeleformTranslator();
		Integer formInstanceId;
		formId = Context.getFormService().getForm("PearlGrlz").getFormId();
		
		if(submitAnswers != null && submitAnswers.length() > 0) {
			if(formInstance == null)  {
				String msg = "need to re-enter the page before submitting";
				log.error(msg);
				throw new Exception(msg);
			}
		} else {
			//numberOfQuestions = pearlgrlzSvc.getNumberQuestions(formId,locationTagId,locationId);
			formInstance = Context.getService(ATDService.class).addFormInstance(formId, locationId);
			modelMap.put("formInstance", formInstance);
		}
		formInstanceId = formInstance.getFormInstanceId();
		
		
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
		    formId, "defaultMergeDirectory", locationTagId, locationId));
		
		String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
		InputStream input = null;
		
		File file = new File(currFilename);
		
		if (file.exists()) {
			input = new FileInputStream(currFilename);
		}
		
		if (submitAnswers != null && submitAnswers.length() > 0) {
			
			patientState = atdService.addPatientState(patient, atdState, pearlgrlzSvc.getSession(patient,provider,location).getSessionId(), 
				locationTagId, locationId);
			
			//pearlgrlzSvc.saveForm(modelMap,  formId,  formInstanceId,  locationTagId,  locationId, translator,  input,  request,provider);
			
			patientState.setFormInstance(formInstance);
			patientState.setEndTime(new Date());
			atdService.updatePatientState(patientState);
			
			// If complete all the survey questions. 
			if(nextPage.equalsIgnoreCase("surveyComplete")) {
				modelMap.put("nextPage", nextPage + ".form");		
				modelMap.put("redirectto", "redirectto");					
				endSurveySession(patient, null, Boolean.FALSE);
				return modelMap;
			}
			
			modelMap.put("surveyType", nextPage);					
			modelMap.put("submitAnswers", "");						
		
			//numberOfQuestions = pearlgrlzSvc.getNumberQuestions(formId,locationTagId,locationId);
			formInstance = Context.getService(ATDService.class).addFormInstance(formId, locationId);
			modelMap.put("formInstance", formInstance);
			currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
			file = new File(currFilename);
			input = null;
		} 

		session = pearlgrlzSvc.getSession(patient,provider,location);
		if(session == null ) {//&& pearlgrlzSvc.isSurveyCompleted(patient)) {
			modelMap.put("completeMessage", "You have already completed today's Survey.  Please come back tomorrow to take a new survey.");
			modelMap.put("redirectto", "redirectto");
			return modelMap;
		}
		patientState = atdService.addPatientState(patient, atdState, session.getSessionId(), 
											locationTagId, locationId);
		
		// Render the page with Merge file
		pearlgrlzSvc.createSurveyXML(patient, null, formId, 
			numberOfQuestions, null,null,formInstance,session.getEncounterId());
		patientState.setFormInstance(formInstance);
		patientState.setEndTime(new Date());
		atdService.updatePatientState(patientState);
		
		modelMap.put("formInstance", formInstance.toString());
		modelMap.put("participant", patient.getNames());
		modelMap.put("formName", Context.getFormService().getForm(formId).getName());
		
		if (input == null && file.exists())
			input = new FileInputStream(currFilename);
		
		
		preRender(modelMap, formId, formInstance.getFormInstanceId(), 
											locationId, translator, input);
											*/
		return modelMap;
		
	}
	
	/**
     * Parse and get the parameter list.
     * 
     * @param string
     * @param delimitor
     * @return
     */
    private List<String> parseParameters(String string, String delimitor) {
    	List<String> parameters = new ArrayList<String>();
    	
    	if(string == null)
    		return null;
    	else if(delimitor == null)
    		// Arden Syntax uses ;; for the statement, we use this ,, (double comma) for delimited-string 
    		// (note Chica Rule parser added a space in between.
    		delimitor = ", ,";				
    	
    	String [] tokens = string.split(delimitor);
    	
    	for(int i=1; i < tokens.length; ++i) {
    		if( !(tokens[i] = tokens[i].trim()).equals("") ) {
    			parameters.add(tokens[i]);
    		}
    	}
    	
    	return parameters;
    }
	
	 /**
     * @see org.openmrs.module.pearlgrlz.service.PearlgrlzService#preRender(java.util.Map, java.lang.Integer, java.lang.Integer, java.lang.Integer, org.openmrs.module.atd.TeleformTranslator, java.io.InputStream)
     */
    private void preRender(Map map, Integer formId, Integer formInstanceId, Integer locationId,
                           TeleformTranslator translator, InputStream inputMergeFile)  throws Exception {
    	FormInstance formInstance = null;
    	PatientATD patientATD  = null;
    	Rule rule = null;
    	String category = "singleChoice";
    	List<String> tokens = null;
    	int nbrQuestions = 0;
    	
    	
    	ATDService atdService = Context.getService(ATDService.class);
    	if(translator == null ) 
    		translator = new TeleformTranslator();
    	
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(new FormInstance(
		        																												locationId, formId, formInstanceId));
		
		//Parse the merge file to get the field values to display
		if(fieldMap == null) {
			formInstance = xmlDatasource.parse(inputMergeFile, null, null);
			inputMergeFile.close();
			fieldMap = xmlDatasource.getParsedFile(formInstance);
		}
		
    	
    	List<FormField> fields = Context.getFormService().getForm(formId).getOrderedFormFields();
    	
		//store the values of fields
		for (FormField currField : fields) {
			FieldType fieldType = currField.getField().getFieldType();
			String fieldName = currField.getField().getName();
			if (fieldType == null || !fieldType.equals(translator.getFieldType("Export Field"))) {
				Field lookupField = fieldMap.get(fieldName);
				
				if (lookupField != null) {
					if(fieldName.contains("Question")) {
						patientATD =  atdService.getPatientATD(formInstance, currField.getField().getFieldId());
						
						if(patientATD != null) {
							if( (rule = patientATD.getRule()) != null) 
								tokens = parseParameters(rule.getPurpose(), null);
							
							if(fieldName.equals("Question1") && tokens.size() >= 1) {
								category = tokens.get(0);
								map.put("category", category.trim()); 
							} else  if(tokens.size() < 1 && !category.equalsIgnoreCase("singleChoice") || tokens.size() >=1 &&  !tokens.get(0).equals(category) )  {
									break;
							}
							addParameters(map, fieldName, tokens);
							nbrQuestions++;
						} 
					} 
					map.put(fieldName, lookupField.getValue());
				} 
			} 
		} 
		
		if(nbrQuestions <= 0) {
			map.put("redirectto", "redirectto");
			map.put("nextPage", "surveyComplete");
			endSurveySession(Context.getPatientService().getPatient(Context.getAuthenticatedUser().getUserId()), null, Boolean.FALSE);
		}
    }
    
    /**
	 * @param patient
	 * @param surveyType
	 * @param voided
	 */
	private void endSurveySession(Patient patient, String surveyType, Boolean voided) {
		
		PearlgrlzService pearlGrlzService = Context.getService(PearlgrlzService.class);
		Date now = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		
		if(surveyType == null) 
			surveyType = pearlGrlzService.calculateSurveyType();
		
		SurveySession surveySession = (SurveySession) pearlGrlzService.getLatestSurveySession(patient, surveyType);
		
		if(surveySession != null && surveySession.getEndTime() == null) {
			if(voided) 
				surveySession.setVoided(Boolean.TRUE);
			surveySession.setEndTime(new Date());
			pearlGrlzService.cupSurveySession(surveySession);
		}
		//pearlGrlzService.updateSurveyCompleted(patient, Boolean.TRUE);
	}
	
    /**
     * Auto generated method comment
     * 
     * @param parametersMap
     * @param questionFldNm
     * @param parameters
     */
    private void addParameters(Map<String, List<String>> parametersMap, String questionFldNm, List<String> parameters) {
    	String idx;
		PearlgrlzService pearlGrlzService = Context.getService(PearlgrlzService.class);

    	Patient patient = Context.getPatientService().getPatient(Context.getAuthenticatedUser().getUserId());
    	List<String> lFrequencyOptions;	// One time, Two times, ..., Not Applicable
    	List<String> lMoodOptions;		// None, Some, A lot
    	List<String> lDurationOptions;		// 1 to 3 hours, stuff
    	List<String> lLocationOptions;		// 1 to 3 hours, stuff
    	List<String> lScaleOptions;	// 1....5
    	List<String> lAmpmOptions;	// AM...PM
    	
    	lFrequencyOptions = new ArrayList<String>();
		lFrequencyOptions.add("One time");
		lFrequencyOptions.add("Two times");
		lFrequencyOptions.add("Three times");
		lFrequencyOptions.add("Four or more times");
		lFrequencyOptions.add("Not Applicable");
		
		lMoodOptions = new ArrayList<String>();
		lMoodOptions.add("None");
		lMoodOptions.add("Some");
		lMoodOptions.add("A lot");
		
		lDurationOptions = new ArrayList<String>();
		lDurationOptions.add("Less than an hour");
		lDurationOptions.add("1 to 3 hours");
		lDurationOptions.add("4 to 6 hours");
		lDurationOptions.add("More than 6 hours");
		
		lScaleOptions = new ArrayList<String>();
		lScaleOptions.add("1");
		lScaleOptions.add("2");
		lScaleOptions.add("3");
		lScaleOptions.add("4");
		lScaleOptions.add("5");
		
		lAmpmOptions = new ArrayList<String>();
		lAmpmOptions.add("AM");
		lAmpmOptions.add("PM");
		
		lLocationOptions = new ArrayList<String>();
		lLocationOptions.add("In School");
		lLocationOptions.add("At my house");
		lLocationOptions.add("At his/her house");
		lLocationOptions.add("Outside");
		lLocationOptions.add("Mall");
		lLocationOptions.add("Someone else's house");
		lLocationOptions.add("Somewhere else");
    	if(!questionFldNm.contains("Question") || (idx = questionFldNm.substring("Question".length())) == null || idx.isEmpty() 
    			|| parameters.size() <= 0 )
    		return;
    	
    	if(parameters.size() > 1) {
    		if(parameters.get(1).equalsIgnoreCase("frequencyOptions") )   {
    			parametersMap.put(questionFldNm + "List", lFrequencyOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("moodOptions") )  {
    			parametersMap.put(questionFldNm + "List", lMoodOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("durationOptions") )  {
    			parametersMap.put(questionFldNm + "List", lDurationOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("locationOptions") )  {
    			parametersMap.put(questionFldNm + "List", lLocationOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("scaleOptions") )   {
    			parametersMap.put(questionFldNm + "List", lScaleOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("ampmOptions") )   {
    			parametersMap.put(questionFldNm + "List", lAmpmOptions);
    		}
    		else if(parameters.get(1).equalsIgnoreCase("generalPartnerOptions") ) { 
    			//parametersMap.put(questionFldNm + "List",  pearlGrlzService.populatePartnerList(patient, PearlgrlzService.SURVEY_GENERAL_PARTNER_TYPE));
    		}
    		else if(parameters.get(1).equalsIgnoreCase("sexualPartnerOptions") ) { 
    			//parametersMap.put(questionFldNm + "List",  pearlGrlzService.populatePartnerList(patient, PearlgrlzService.SURVEY_SEXUAL_PARTNER_TYPE));
    		}
    	}
    }
	/**
	 * This class returns the form backing object. This can be a string, a boolean, or a normal java
	 * pojo. The type can be set in the /config/moduleApplicationContext.xml file or it can be just
	 * defined by the return type of this method
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
}
