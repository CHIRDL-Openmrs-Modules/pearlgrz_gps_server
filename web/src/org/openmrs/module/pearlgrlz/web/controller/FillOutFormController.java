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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class FillOutFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static Integer patientId = null;
	private static Integer providerId = null;
	private static Integer encounterId = null;
	private static Integer formId = null;
	private static Integer sessionId = null;
	private static Integer locationId = null;
	private static Integer locationTagId = null;
	
	private static boolean drankAlcoholFlg = false;
	private static boolean smokedCigarettesFlg = false;
	
	private static Integer formNbrQuestions = null;
	
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
		
		String idString = request.getParameter("formInstance");
		String encounterIdString = request.getParameter("encounterId");
		
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
					FillOutFormController.formId = chosenFormId;
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

		if(FillOutFormController.patientId == null && request.getParameter("patientId") != null ) {
			FillOutFormController.patientId = Integer.parseInt(request.getParameter("patientId"));
		}
		if(FillOutFormController.providerId == null && request.getParameter("providerId") != null ) {
			FillOutFormController.providerId = Integer.parseInt(request.getParameter("providerId"));
		}
		if(FillOutFormController.encounterId == null && request.getParameter("encounterId") != null ) {
			FillOutFormController.encounterId = Integer.parseInt(request.getParameter("encounterId"));
		}
		if(FillOutFormController.sessionId == null && request.getParameter("sessionId") != null ) {
			FillOutFormController.sessionId = Integer.parseInt(request.getParameter("sessionId"));
		}
		if(FillOutFormController.locationId == null && request.getParameter("locationId") != null ) {
			FillOutFormController.locationId = Integer.parseInt(request.getParameter("locationId"));
		}
		if(FillOutFormController.locationTagId == null && request.getParameter("locationTagId") != null ) {
			FillOutFormController.locationTagId = Integer.parseInt(request.getParameter("locationTagId"));
		}
		if(request.getParameter("formNumberQuestions") != null ) {
			FillOutFormController.formNbrQuestions = Integer.parseInt(request.getParameter("formNumberQuestions"));
		}
		
		// Get all the request parameter from previous page.
//		map.putAll(request.getParameterMap());
		map.put("formInstance", idString);

		//Run this if the form is scanned
		if (submitAnswers != null && submitAnswers.length() > 0) {
			scanForm(map, chosenFormId, chosenFormInstanceId, chosenLocationTagId, chosenLocationId, translator, input,
			    request);
		} else {
			//Run this to show the form
			showForm(map, chosenFormId, chosenFormInstanceId, chosenLocationId, translator, input);
			map.put("formNumberQuestions", request.getParameter("formNumberQuestions"));
//			if(request.getParameter("formNumberQuestions").equals("pearl_alcohol"))
//				map.put("formNumberQuestions", 5);
//				map.put("formNumberQuestions", FillOutFormController.formNbrQuestions);
			
		}
		
		return map;
	}
	
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
//	@Override
//	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object,
//	                                BindException exceptions) throws Exception {
//		Map<String, Object> map = new HashMap<String, Object>();
//		
//		return new ModelAndView(new RedirectView("pearlgrlzForm.form"), map);
//	}
	
	
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
		
		//Parse the merge file to get the field values to display
		FormInstance formInstance = xmlDatasource.parse(inputMergeFile, null, null);
		inputMergeFile.close();
		fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		List<org.openmrs.Field> fields = formService.getAllFields();
		
		//store the values of fields in the jsp map
		for (org.openmrs.Field currField : fields) {
			FieldType fieldType = currField.getFieldType();
			if (fieldType == null || !fieldType.equals(translator.getFieldType("Export Field"))) {
				Field lookupField = fieldMap.get(currField.getName());
				if (lookupField != null) {
					map.put(currField.getName(), lookupField.getValue());
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
	private static void scanForm(Map map, Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile, HttpServletRequest request) {

		try {
			//pull all the input fields from the database for the form
			FormService formService = Context.getFormService();
			HashSet<String> inputFields = new HashSet<String>();
			List<org.openmrs.Field> fields = formService.getAllFields();
			Map<String, String> mFormFieldConceptAnswerField = new HashMap<String, String>();
			
			for (org.openmrs.Field currField : fields) {
				FieldType fieldType = currField.getFieldType();
				if (fieldType != null && fieldType.equals(translator.getFieldType("Export Field"))) {
					inputFields.add(currField.getName());
					
					if(currField.getName().contains("QuestionEntry")) {
						String[] tokens = currField.getName().split("_"); // The name can be QuestionEntry_1 or QuestionEntry_1_2
						String formFieldNm = currField.getName().substring(0, currField.getName().indexOf("Entry")) + tokens[1];
						mFormFieldConceptAnswerField.put(formFieldNm, currField.getName());
					}
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
			
			// TODO: Save the obs
//			Form form = new Form(formId);									// Bad usage
			Form form = Context.getFormService().getForm(formId);
			Set<FormField> lFormFields = form.getFormFields();   
//			List<FormField> lFormFields = Context.getFormService().getFormFields(form) ;
//			List<FormField> lFormFields = form.getOrderedFormFields();
			Map<String, Concept> mConcepts = new HashMap<String, Concept>();
			
			// TODO: Currently assume all fields in this form use Concept; 
			for(FormField ff : lFormFields) {
				if(ff.getField().getConcept() != null) {
					mConcepts.put(ff.getField().getName(), ff.getField().getConcept()); 
				}
			}
			
			ConceptAnswer conceptAnswer = null;
			
			Log log = LogFactory.getLog(FillOutFormController.class);
			
			// For each field in Record, let's get the Concept and Concept Answers
			// Check the user-input-answer,  use the correct concept-answer for the obs
			for(Field currField : record.getFields()) {
				String name = currField.getId();
				
				if(mConcepts.containsKey(name)) {
					Obs  obs = new Obs();
					
					Collection<ConceptAnswer> answers = mConcepts.get(name).getAnswers();
					
//					String inputVal = request.getParameter(name);
					String inputVal = request.getParameter(mFormFieldConceptAnswerField.get(name));  
					
					// Set flag if concept is Drank alcohol etc, and the answer is Y (Yes)
					if(inputVal.equalsIgnoreCase("Y")) {
						if(mConcepts.get(name).getName().getName().equalsIgnoreCase("Drank alcohol")) 
							drankAlcoholFlg = true;
					
						if(mConcepts.get(name).getName().getName().equalsIgnoreCase("Smoked cigarettes")) 
							smokedCigarettesFlg = true;
					} 

					// Get the concept-answer based on user's input (Y or N)
					for (ConceptAnswer ans : answers) {
	                    if(inputVal != null && (ans.getAnswerConcept().getName().getName().contains(inputVal) 
	                    		||  ans.getAnswerConcept().getName().getName().contains(inputVal.toLowerCase())) ) {
	                    	conceptAnswer = ans;
	                    }
                    }
					
//					obs.setPerson(new Person(patientId));
					obs.setPerson(Context.getPersonService().getPerson(patientId));
					obs.setConcept(mConcepts.get(name));
//					obs.setEncounter(new Encounter(encounterId));
					obs.setEncounter(Context.getEncounterService().getEncounter(encounterId));
//					obs.setLocation(new Location(locationId));
					obs.setLocation(Context.getLocationService().getLocation(locationId));
//					obs.setDateCreated(new java.util.Date());				// OpenMRS should add
					obs.setObsDatetime(new java.util.Date());
					
					// The result can be a concept, text, datetime, boolean, or others
					if(conceptAnswer != null)
						obs.setValueCoded(conceptAnswer.getAnswerConcept());
//					else if(mConcepts.get(name).getDatatype().getName().equalsIgnoreCase("Text"))
					else
						obs.setValueText(inputVal); 
					
					Context.getObsService().saveObs(obs, "new");
					log.error("Saved one Observation: concept <" + mConcepts.get(name).getName().getName() + ">  the answer is <" + conceptAnswer.getAnswerConcept().getName().getName() + ">");
				}
				
				// Get Concept Name
				  log.info("current-field is <" + name + ">; has value of <" + currField.getValue() + ">");
				  
				  
						
				if(inputFields.contains(name)) {
					String inputVal = request.getParameter(name);
					
				}
			}
			
			
			
			// Update the state for this participant session
			Patient patient = Context.getPatientService().getPatient(patientId);
			ATDService atdService = Context.getService(ATDService.class);
			State prcsState = atdService.getStateByName("process_survey"); 
			PatientState patientState = atdService.addPatientState(patient, prcsState, sessionId, locationTagId, locationId);
			patientState.setEndTime(new java.util.Date());
			atdService.updatePatientState(patientState);
			
			
			//TODO: Deal with the states here,  next Form to process, or just Finish?   Indicate done for scan
//			map.put("scanned", "scanned");
			map.put("contSurvey", "contSurvey");
			map.put("nonInit", "nonInit");
			map.put("submitAnswers", "");
			
			map.put("patientId", patientId);
			map.put("providerId", providerId);
			map.put("locationId", locationId);
			map.put("locationTagId", locationTagId);
			
			if(drankAlcoholFlg) {
				map.put("formName", "pearl_alcohol");
				map.put("formNumberQuestions", "5");
				drankAlcoholFlg = false;
			} else {
				map.put("formName", "pearl_general_cont");
//				map.put("formNumberQuestions", "5");
			}
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
