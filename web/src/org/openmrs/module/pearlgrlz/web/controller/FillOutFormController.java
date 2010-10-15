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


/**
 *
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.pearlgrlz.SurveyParameterHandler;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;
import org.openmrs.module.pearlgrlz.util.Util;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class FillOutFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer locationTagId = 1; //TODO needs to be configurable
		Integer locationId = 1; //TODO needs to be configurable
		Integer numQuestions = 5; //TODO needs to be a form attribute value
		String idString = request.getParameter("formInstance");
		String encounterIdString = request.getParameter("encounterId");
		String patientIdString = request.getParameter("patientId");
		String sessionIdString = request.getParameter("sessionId");
		String providerIdString = request.getParameter("providerId");
		
		Integer encounterId = null;
		if(encounterIdString != null){
			try {
	            encounterId = Integer.parseInt(encounterIdString);
            }
            catch (Exception e) {
            }
		}
		Integer patientId = null;
		if(patientIdString != null){
			try {
				patientId = Integer.parseInt(patientIdString);
            }
            catch (Exception e) {
            }
		}
		Integer sessionId = null;
		if(sessionIdString != null){
			try {
				sessionId = Integer.parseInt(sessionIdString);
            }
            catch (Exception e) {
            }
		}
		
		Integer providerId = null;
		if(providerIdString != null){
			try {
				providerId = Integer.parseInt(providerIdString);
            }
            catch (Exception e) {
            }
		}
		
		Integer chosenFormId = null;
		Integer chosenFormInstanceId = null;
		Integer chosenLocationId = null;
		Integer chosenLocationTagId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		if (idString != null) {
			StringTokenizer tokenizer = new StringTokenizer(idString, "_");
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenLocationId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenLocationTagId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenFormId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}
			
			if (tokenizer.hasMoreTokens()) {
				try {
					chosenFormInstanceId = Integer.parseInt(tokenizer.nextToken());
				}
				catch (NumberFormatException e) {}
			}		
		} 
		
		String submitAnswers = request.getParameter("submitAnswers");
		TeleformTranslator translator = new TeleformTranslator();

		//Run this if the form is scanned
		if (submitAnswers != null && submitAnswers.length() > 0) {
			//Run this to show the form
			InputStream input = null;
			
			//if a form is chosen or scanned, find the merge file that goes with
			//that form
			String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
			    chosenFormId, "defaultMergeDirectory", chosenLocationTagId, chosenLocationId));
			
			// Parse the merge file
			FormInstance formInstance = new FormInstance(chosenLocationId, chosenFormId, chosenFormInstanceId);
			
			String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
			
			File file = new File(currFilename);
			if (file.exists()) {
				input = new FileInputStream(currFilename);
			}
			scanForm(map, chosenFormId, chosenFormInstanceId, chosenLocationTagId, chosenLocationId, translator, input,
			    request,patientId,sessionId);
		} 
		
		FormInstance formInstance = null;
		
		map.put("patientId", patientId);
		map.put("providerId", providerIdString);
		map.put("encounterId", encounterId);
		map.put("sessionId", sessionId);
		
		formInstance = generatePage(patientId,providerId,locationTagId,locationId,numQuestions,map);

		//Run this to show the form
		InputStream input = null;
		
		//if a form is chosen or scanned, find the merge file that goes with
		//that form
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
		    chosenFormId, "defaultMergeDirectory", chosenLocationTagId, chosenLocationId));
		
		// Parse the merge file
		if(formInstance == null){
			formInstance = new FormInstance(chosenLocationId, chosenFormId, chosenFormInstanceId);
		}
		
		String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
		
		File file = new File(currFilename);
		if (file.exists()) {
			input = new FileInputStream(currFilename);
		}
		showForm(map, chosenFormId, chosenFormInstanceId, chosenLocationId, translator, input);

		map.put("formInstance", formInstance.getLocationId() + "_" + locationTagId + "_" + formInstance.getFormId() + "_"
	        + formInstance.getFormInstanceId());
		return map;
	}
	
	
	private FormInstance generatePage(Integer patientId,Integer providerId,Integer locationTagId,
	                                  Integer locationId, Integer numQuestions,Map<String,Object> map){
		FormInstance formInstance = null;
		try {
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		State initialState = atdService.getStateByName("create_survey");
		Session session = atdService.addSession();
		Integer sessionId = session.getSessionId();
		
		//locationId and locationTagId will eventually come from login information
		//Hard code them for now
		UserService userService = Context.getUserService();
		User provider = userService.getUser(providerId);
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		
		String formName = "PearlGrlz";
		FormService formService = Context.getFormService();
		List<Form> allForms = formService.getForms(formName, null, null, false, null, null, null);
		Form form = allForms.get(0);
		Integer formId = form.getFormId();
		
		if (patientId != null) {
			//add logic here to figure out whether a new survey should be started
			//or a previous survey should be completed
			//assume new survey for now
			Patient patient = patientService.getPatient(patientId);
			
			PatientState patientState = atdService.addPatientState(patient, initialState, sessionId, locationTagId,
				locationId);
			formInstance = atdService.addFormInstance(formId, locationId);
			
			
			//write surveyXML to "merge" directory
			String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util
			        .getFormAttributeValue(formId, "defaultMergeDirectory", locationTagId, locationId));
			String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
			
			OutputStream output = new FileOutputStream(currFilename);
			PearlgrlzService pearlgrlzService = Context.getService(PearlgrlzService.class);
			Encounter encounter = pearlgrlzService.getEncounter(patient, provider, location);
			session.setEncounterId(encounter.getEncounterId());
			atdService.updateSession(session);
			pearlgrlzService.createSurveyXML(patient,locationId, formId, 
				numQuestions, provider,locationTagId,formInstance,encounter.getEncounterId());
			output.close();  
			patientState.setEndTime(new java.util.Date());
			atdService.updatePatientState(patientState);
			initialState = atdService.getStateByName("wait_to_submit_survey");
			
			patientState = atdService.addPatientState(patient, initialState, sessionId, locationTagId, locationId);
			map.put("sessionId",sessionId);
		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return formInstance;
	}
	
	private void consumeInputXML(Integer patientId,PatientState patientState,
	                                    FormInstance formInstance,String exportFilename){

		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();

		Integer sessionId = patientState.getSessionId();
		patientState.setFormInstance(formInstance);
		ATDService atdService = Context.getService(ATDService.class);
		atdService.updatePatientState(patientState);
		consume(sessionId,formInstance,patient,
				null,locationTagId,exportFilename);
		StateManager.endState(patientState);
	}
	
	private void consume(Integer sessionId, FormInstance formInstance, Patient patient,
	                            List<FormField> fieldsToConsume, Integer locationTagId,String exportFilename) {
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		Integer encounterId = atdService.getSession(sessionId).getEncounterId();
		
		try {
			InputStream input = new FileInputStream(exportFilename);
			
			startTime = System.currentTimeMillis();
			ParameterHandler parameterHandler = new SurveyParameterHandler();
			//make sure to remove the parsed file before consume to make sure
			//the file is freshly read
			LogicService logicService = Context.getLogicService();
			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
			xmlDatasource.deleteParsedFile(formInstance); //make sure we are freshly parsing the file

			atdService.consume(input, formInstance, patient, encounterId, null, null,
			    parameterHandler, fieldsToConsume, locationTagId, sessionId);
			startTime = System.currentTimeMillis();
			input.close();
		}
		catch (Exception e) {
			log.error("Error consuming chica file: " + exportFilename);
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		
		System.out.println("chicaStateActionHandler.consume: time of saveObs: " + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();
		// remove the parsed xml from the xml datasource
		try {
			Integer purgeXMLDatasourceProperty = null;
			try {
				purgeXMLDatasourceProperty = Integer.parseInt(adminService.getGlobalProperty("atd.purgeXMLDatasource"));
			}
			catch (Exception e) {}
			LogicService logicService = Context.getLogicService();
			
			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
			if (purgeXMLDatasourceProperty != null && purgeXMLDatasourceProperty == 1) {
				xmlDatasource.deleteParsedFile(formInstance);
			}
		}
		catch (Exception e) {
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}

	/**
	 * Auto generated method comment
	 * 
	 * @param map
	 * @param formId
	 * @param formInstanceId
	 * @param locationId
	 * @param translator
	 * @param inputMergeFile
	 * @throws Exception
	 */
	private static void showForm(Map map, Integer formId, Integer formInstanceId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile) throws Exception {
		FormService formService = Context.getFormService();
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(new FormInstance(
		        locationId, formId, formInstanceId));
		
		FormInstance formInstance = new FormInstance(locationId,formId,formInstanceId);
		//Parse the merge file to get the field values to display
		formInstance = xmlDatasource.parse(inputMergeFile,
			formInstance, null);
		inputMergeFile.close();
		fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		List<org.openmrs.Field> fields = formService.getAllFields();
		
		//store the values of fields in the jsp map
		for (org.openmrs.Field currField : fields) {
			FieldType fieldType = currField.getFieldType();
			if (fieldType == null || !fieldType.equals(translator.getFieldType("Export Field"))) {
				Field lookupField = fieldMap.get(currField.getName());
				if (lookupField != null) {
					//if this is an input list parse it into a String List
					if(currField.getName().contains("_input_list")){
						List<String> inputList = new ArrayList<String>();
						if (lookupField.getValue() != null) {
							StringTokenizer tokenizer = new StringTokenizer(lookupField.getValue(), ",");
							
							while (tokenizer.hasMoreElements()) {
								String value = tokenizer.nextToken();
								inputList.add(value);
							}
							map.put(currField.getName(), inputList);
						}

					}else{
						map.put(currField.getName(), lookupField.getValue());
					}
				}
			}
		}
	}
	
	
	/**
	 * Auto generated method comment
	 * 
	 * @param map
	 * @param formId
	 * @param formInstanceId
	 * @param locationTagId
	 * @param locationId
	 * @param translator
	 * @param inputMergeFile
	 * @param request
	 */
	
	
	private void scanForm(Map map, Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                             TeleformTranslator translator, 
	                             InputStream inputMergeFile, HttpServletRequest request,
	                             Integer patientId,Integer sessionId) {

		try {
			//pull all the input fields from the database for the form
			FormService formService = Context.getFormService();
			HashSet<String> inputFields = new HashSet<String>();
			List<org.openmrs.Field> fields = formService.getAllFields();
			
			for (org.openmrs.Field currField : fields) {
				FieldType fieldType = currField.getFieldType();
				if (fieldType != null && fieldType.equals(translator.getFieldType("Export Field"))) {
					inputFields.add(currField.getName());
				}
			}
			
			Records records = (Records) XMLUtil.deserializeXML(Records.class, inputMergeFile);
			inputMergeFile.close();
			Record record = records.getRecord();
			
			//Link the values from the submitted answers to 
			//the form fields
			for (Field currField : record.getFields()) {
				String name = currField.getId(); 
				
				if (inputFields.contains(name)) {
					String inputVal = request.getParameter(name);
					currField.setValue(inputVal);
				}
			}
			String exportDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
			    formId, "defaultExportDirectory", locationTagId, locationId));
			
			FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
			//Write the xml for the export file
			String exportFilename = exportDirectory + formInstance.toString() + ".xml";
			
			OutputStream output = new FileOutputStream(exportFilename);
			XMLUtil.serializeXML(records, output);
			output.flush();
			output.close();
			ATDService atdService = Context.getService(ATDService.class);
			// Update the state for this participant session
			Patient patient = Context.getPatientService().getPatient(patientId);
			State prcsState = atdService.getStateByName("process_survey"); 
			PatientState patientState = atdService.addPatientState(patient, prcsState, sessionId, locationTagId, locationId);

			consumeInputXML(patientId,patientState,formInstance,exportFilename);
			
			patientState.setEndTime(new java.util.Date());
			atdService.updatePatientState(patientState);
			
			
			//TODO: Deal with the states here,  next Form to process, or just Finish?   Indicate done for scan
//			map.put("scanned", "scanned");
			map.put("contSurvey", "contSurvey");
			map.put("nonInit", "nonInit");
			map.put("submitAnswers", "");
					
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
