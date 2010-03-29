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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

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
		PearlgrlzService pearlgrlzSvc = Context.getService(PearlgrlzService.class);
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		State atdState = atdService.getStateByName(SURVEY_STATE_NAME);
		PatientState patientState = null;
		Session session = null;
		String nextPage = "";
		Integer formId = null;
		Integer numberOfQuestions = null;
		pearlgrlzSvc.getDefaultInfor();

		Patient patient = patientService.getPatient(Context.getAuthenticatedUser().getPersonId());
		if(patient == null)	
			pearlgrlzSvc.getPatientByUserId(Context.getAuthenticatedUser().getPersonId());

		
		
		Integer locationId = pearlgrlzSvc.getLocation().getLocationId();
		Integer locationTagId = pearlgrlzSvc.getLocationTag().getLocationTagId();

		
		String submitAnswers = request.getParameter("submitAnswers");
		TeleformTranslator translator = new TeleformTranslator();
		FormInstance formInstance = null;
		Integer formInstanceId;
		formId = Context.getFormService().getForm("PearlGrlzSurveyForm").getFormId();
		
		if(submitAnswers != null && submitAnswers.length() > 0) {
			formInstance = pearlgrlzSvc.getFormInstance(patient);
			if(formInstance == null)  {
				String msg = "need to re-enter the page before submitting";
				log.error(msg);
				throw new Exception(msg);
			}
		} else {
			numberOfQuestions = pearlgrlzSvc.getNumberQuestions(formId);
			formInstance = Context.getService(ATDService.class).addFormInstance(formId, pearlgrlzSvc.getLocation().getLocationId());
			pearlgrlzSvc.setFormInstance(patient, formInstance);
		}
		formInstanceId = formInstance.getFormInstanceId();
		
		
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
		    formId, "defaultMergeDirectory", pearlgrlzSvc.getLocationTag().getLocationTagId(), pearlgrlzSvc.getLocation()
		            .getLocationId()));
		
		String currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
		InputStream input = null;
		
		File file = new File(currFilename);
		
		if (file.exists()) {
			input = new FileInputStream(currFilename);
		}
		
		if (submitAnswers != null && submitAnswers.length() > 0) {
			
			patientState = atdService.addPatientState(patient, atdState, pearlgrlzSvc.getSession(patient).getSessionId(), 
				pearlgrlzSvc.getLocationTag().getLocationTagId(), pearlgrlzSvc.getLocation().getLocationId());
			
			pearlgrlzSvc.saveForm(modelMap,  formId,  formInstanceId,  locationTagId,  locationId, translator,  input,  request);
			
			patientState.setFormInstance(formInstance);
			patientState.setEndTime(new Date());
			atdService.updatePatientState(patientState);
			
			// If complete all the survey questions. 
			if(nextPage.equalsIgnoreCase("surveyComplete")) {
				modelMap.put("nextPage", nextPage + ".form");		// may not use
				modelMap.put("redirectto", "redirectto");					//
				System.out.println("calling endSurveySession from Controller");
				pearlgrlzSvc.endSurveySession(patient, null, Boolean.FALSE);
				return modelMap;
			}
			
			modelMap.put("surveyType", nextPage);					// can get from Service
			modelMap.put("submitAnswers", "");						// so that next time will createXML, if use one view to handle the survey.
		
			numberOfQuestions = pearlgrlzSvc.getNumberQuestions(formId);
			formInstance = Context.getService(ATDService.class).addFormInstance(formId, pearlgrlzSvc.getLocation().getLocationId());
			pearlgrlzSvc.setFormInstance(patient, formInstance);
			currFilename = defaultMergeDirectory + formInstance.toString() + ".xml";
			file = new File(currFilename);
			input = null;
		} 

		session = pearlgrlzSvc.getSession(patient);
		if(session == null && pearlgrlzSvc.isSurveyCompleted(patient)) {
			modelMap.put("completeMessage", "You have already completed today's Survey.  Please come back tomorrow to take a new survey.");
			modelMap.put("redirectto", "redirectto");
			return modelMap;
		}
		patientState = atdService.addPatientState(patient, atdState, session.getSessionId(), 
											pearlgrlzSvc.getLocationTag().getLocationTagId(), pearlgrlzSvc.getLocation().getLocationId());
		
		// Render the page with Merge file
		pearlgrlzSvc.createSurveyXML(patient, null, formId, numberOfQuestions, null);
		patientState.setFormInstance(formInstance);
		patientState.setEndTime(new Date());
		atdService.updatePatientState(patientState);
		
		modelMap.put("formInstance", formInstance.toString());
		modelMap.put("participant", patient.getNames());
		modelMap.put("formName", Context.getFormService().getForm(formId).getName());
		
		if (input == null && file.exists())
			input = new FileInputStream(currFilename);
		
		
		pearlgrlzSvc.preRender(modelMap, formId, formInstance.getFormInstanceId(), 
											pearlgrlzSvc.getLocation().getLocationId(), translator, input);
		return modelMap;
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
